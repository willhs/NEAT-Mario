package will.game.mario.experiment.runners;

import ch.idsia.benchmark.mario.engine.generalization.Tile;
import ch.idsia.benchmark.mario.options.FastOpts;
import org.encog.ml.ea.train.basic.TrainEA;
import org.encog.neural.neat.NEATLink;
import org.encog.neural.neat.NEATNetwork;
import will.game.mario.experiment.evolve.*;
import will.game.mario.fitness.AbstractMarioFitnessFunction;
import will.game.mario.params.HyperNEATParameters;
import will.game.mario.params.HyperNEATParametersPSO;
import will.game.mario.params.NEATEnsembleParams;
import will.game.mario.params.NEATParameters;
import will.game.mario.rf.action.SharedHoldStrat;
import will.game.mario.rf.action.StandardActionStrat;
import will.game.mario.rf.action.StandardHoldStrat;

import java.awt.*;
import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardOpenOption;
import java.util.Arrays;

import static will.game.mario.fitness.AbstractMarioFitnessFunction.DIFFICULTY;

/**
 * Created by Will on 8/10/2016.
 */
public class HonoursExperiments {

    private NEATParameters neatParams = new NEATParameters();
    private HyperNEATParameters hyperParams = new HyperNEATParametersPSO();

    private final String DEFAULT_SIM_OPTIONS = AbstractMarioFitnessFunction.DEFAULT_SIM_OPTIONS;
    private final String DEFAULT_LEVEL = AbstractMarioFitnessFunction.LEVEL;

    private String[] levels = {
            DEFAULT_SIM_OPTIONS.replace(DEFAULT_LEVEL, FastOpts.LEVEL_05_GAPS)
                                .replace(DIFFICULTY, FastOpts.L_DIFFICULTY(1)),
            DEFAULT_SIM_OPTIONS.replace(DEFAULT_LEVEL, FastOpts.LEVEL_06_GOOMBA),
            DEFAULT_SIM_OPTIONS.replace(DEFAULT_LEVEL, FastOpts.LEVEL_07_SPIKY),
            DEFAULT_SIM_OPTIONS.replace(DEFAULT_LEVEL, FastOpts.LEVEL_08_FLY_SPIKY)
    };
    private static String outputDirName = "neat-output/";
    private static String outputFilename = "results.csv";

    private boolean writeFile = true;

    public static final String NEAT_EACH_FRAME = "neat-each-frame";
    public static final String NEAT_STANDARD_HOLD = "neat-standard-hold";
    public static final String NEAT_SHARED_HOLD = "neat-shared-hold";
    public static final String NEAT_PHASED = "neat-phased";
    public static final String HYPERNEAT = "hyperneat";
    public static final String HYPERNEAT_PHASED = "hyperneat-phased";

    private String experimentName;

    public static void main(String[] args) throws IOException {

        String arg = null;
        if (args.length < 1) {
            arg = NEAT_PHASED;//NEAT_STANDARD_HOLD;
        } else arg = args[0];

        if (args.length == 2) {
            outputFilename = args[1];
        }

        HonoursExperiments ex = new HonoursExperiments(arg);
        ex.runSingleExperiment();
    }

    public HonoursExperiments(String name) {
        this.experimentName = name;
    }

    private void runSingleExperiment() {
        // initialise parameters
        NEATParameters neatPhasedParams = new NEATParameters();
        neatPhasedParams.PHASED_SEARCH = true;

        HyperNEATParameters hyperPhasedParams = new HyperNEATParametersPSO();
        hyperPhasedParams.PHASED_SEARCH = true;

        NEATParameters neatShareActionParams = new NEATParameters();
        neatShareActionParams.NUM_OUTPUTS = 5;

        NEATEnsembleParams ensembleParams = new NEATEnsembleParams();

        StringBuilder sb = new StringBuilder();

        NEATMarioEvolver evolver = null;

        if (experimentName.equals(NEAT_EACH_FRAME)) {
            evolver = new NEATMarioEvolver(neatParams,
                    () -> new StandardActionStrat(), sb, experimentName);
        } else if (experimentName.equals(NEAT_STANDARD_HOLD)) {
            evolver = new NEATMarioEvolver(neatParams,
                    () -> new StandardHoldStrat(), sb, experimentName);
        } else if (experimentName.equals(NEAT_SHARED_HOLD)) {
            evolver = new NEATMarioEvolver(neatShareActionParams,
                    () -> new SharedHoldStrat(), sb, experimentName);
        } else if (experimentName.equals(NEAT_PHASED)) {
            evolver = new NEATMarioEvolver(neatPhasedParams,
                    () -> new StandardHoldStrat(), sb, experimentName);
        } else if (experimentName.equals(HYPERNEAT)) {
            evolver = new HyperNEATMarioEvolver(hyperParams,
                    () -> new StandardHoldStrat(), sb, experimentName);
        } else if (experimentName.equals(HYPERNEAT_PHASED)) {
            evolver = new HyperNEATMarioEvolver(hyperPhasedParams,
                    () -> new StandardHoldStrat(), sb, experimentName);
        }

/*        String level = levels[1];

        evolver.setSimOptions(level);
        evolver.run();*/

        testOnLevels(evolver);

        if (writeFile) {
            writeToFile(sb.toString());
        }
    }

    private void testOnLevels(NEATMarioEvolver evolver) {
        String basicName = evolver.getName();
        TrainEA[] results = new TrainEA[levels.length];
        for (int l = 0; l < levels.length; l++) {
            String level = levels[l];

            evolver.setSimOptions(level);
            evolver.setName(basicName + "-" + l);
            results[l] = evolver.run();
        }
    }

    private void writeToFile(String s) {
        // make sure output dir exists
        File outputDir = new File(outputDirName);
        if (!outputDir.exists()) {
            outputDir.getAbsoluteFile().mkdirs();
        }

        int numFiles = outputDir.listFiles().length;

//            String outputFilename = outputDirName + experimentName + "-" + numFiles + ".csv";
        Path output = Paths.get(outputFilename);
        // write string output to file
        try {
            Files.write(output,
                    "algorithm,generation,fitness,ave-links,best-links,ave-nodes,best-nodes,species\n".getBytes(),
                    StandardOpenOption.CREATE, StandardOpenOption.APPEND);

            Files.write(output, s.getBytes(),
                    StandardOpenOption.CREATE, StandardOpenOption.APPEND);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
