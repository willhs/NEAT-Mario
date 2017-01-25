package will.game.mario.experiment;

import ch.idsia.benchmark.mario.engine.generalization.Enemy;
import ch.idsia.benchmark.mario.engine.generalization.Tile;
import ch.idsia.benchmark.mario.options.FastOpts;
import ch.idsia.benchmark.mario.options.MarioOptions;
import org.encog.ml.ea.train.basic.TrainEA;
import org.encog.neural.neat.NEATLink;
import org.encog.neural.neat.NEATNetwork;
import will.game.mario.agent.encog.EncogAgent;
import will.game.mario.experiment.evolve.*;
import will.game.mario.fitness.AbstractMarioFitnessFunction;
import will.game.mario.params.HyperNEATParameters;
import will.game.mario.params.HyperNEATParametersPSO;
import will.game.mario.params.NEATEnsembleParams;
import will.game.mario.params.NEATParameters;
import will.game.mario.rf.action.SharedHoldStrat;
import will.game.mario.rf.action.StandardActionStrat;
import will.game.mario.rf.action.StandardHoldStrat;
import will.game.mario.rf.environment.EnvEnemyGrid;
import will.game.mario.rf.environment.CoinEnvEnemyGrid;
import will.neat.encog.ensemble.EA;

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
public class MarioAIExperiment {

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

    public static final String REVERSE = "reverse";
    public static final String COINS = "coins";

    private static final String ENSEMBLE_SHARED = "ensemble-shared";

    private String experimentName;

    public static void main(String[] args) throws IOException {

        String arg = null;
        if (args.length < 1) {
            arg = NEAT_PHASED;//NEAT_STANDARD_HOLD;
        } else arg = args[0];

        if (args.length == 2) {
            outputFilename = args[1];
        }

        MarioAIExperiment ex = new MarioAIExperiment(arg);
//        ex.run();

        if (arg.equals(REVERSE)) {
            ex.runReverse();
        } else if (arg.equals(COINS)) {
            ex.runCoins();
        }
    }

    public MarioAIExperiment(String name) {
        this.experimentName = name;
    }

    public void testing() {
        String level = levels[1];

        StringBuilder sb = new StringBuilder();

        NEATParameters neatPhasedParams = new NEATEnsembleParams();
        neatPhasedParams.PHASED_SEARCH = true;

        HyperNEATParameters hyperPhasedParams = new HyperNEATParametersPSO();
        hyperPhasedParams.PHASED_SEARCH = true;

        NEATParameters neatShareActionParams = new NEATParameters();
        neatShareActionParams.NUM_OUTPUTS = 5;

        NEATMarioEvolver evolver = null;

        evolver = new NEATMarioEnsembleEvolver(neatParams,
                () -> new StandardHoldStrat());

//        evolver = new NEATMarioEnsembleEvolver(neatParams,
//                () -> new StandardHoldStrat());

//        evolver = new HyperNEATMarioEvolver(hyperPhasedParams,
//                () -> new StandardHoldStrat(), sb, "hyper");

        evolver.setSimOptions(level);
        evolver.run();

//        testOnLevels(evolver);
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
        } else if (experimentName.equals(ENSEMBLE_SHARED)) {
            evolver = new NEATMarioEnsembleEvolver(ensembleParams,
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

    private void runMultiExperiment() throws IOException {
        // init string output
        StringBuilder sb = new StringBuilder();

        NEATEnsembleParams ensembleParams = new NEATEnsembleParams();

        NEATMarioEvolver neatStandard = new NEATMarioEvolver(neatParams,
                () -> new StandardHoldStrat(), sb, "neat");
        NEATMarioEnsembleEvolver ensembleShared = new NEATMarioEnsembleEvolver(ensembleParams,
                () -> new StandardHoldStrat(), sb, "ensemble-shared");
        EnsembleMasterMarioEvolver ensembleMaster = new EnsembleMasterMarioEvolver(ensembleParams,
                () -> new SharedHoldStrat(), sb, "ensemble-master-shared");
        MultiPopNEATMarioEvolver ensembleMulti = new MultiPopNEATMarioEvolver(ensembleParams,
                () -> new StandardHoldStrat(), sb, "emsemble-master-multi");

        // run all experiments without dependencies
        testOnLevels(neatStandard);
        testOnLevels(ensembleShared);
        testOnLevels(ensembleMaster);
        testOnLevels(ensembleMulti);

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

    // uuuhh copy for multipop stuff
    private void testOnLevels(MultiPopNEATMarioEvolver evolver) throws IOException {
        String basicName = evolver.getName();
        EA[] results = new EA[levels.length];
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

    private void runReverse() {
        StringBuilder sb = new StringBuilder();

        NEATParameters neatParams = new NEATParameters();
        neatParams.MAX_GENERATIONS = 500;

        // initialise parameters
        NEATParameters neatPhasedParams = new NEATParameters();
        neatPhasedParams.MAX_GENERATIONS = 500;
        neatPhasedParams.PHASED_SEARCH = true;

        int seed1 = 0;

        String options1 = levels[1].concat(FastOpts.L_RANDOM_SEED(seed1));
//        String options2 = levels[2].concat(FastOpts.L_RANDOM_SEED(seed2));

        String options2 = levels[1]
                + " " + MarioOptions.IntOption.LEVEL_MARIO_INITIAL_POSITION_X.getParam()
                + " " + (1024*16-20);

        EncogAgent.FitnessFunction ff = (info) -> info.levelLength - info.distancePassedCells;

        NEATMarioEvolver neat1 = new NEATMarioEvolver(neatParams,
                () -> new StandardHoldStrat(), sb, "neat");
        neat1.setSimOptions(options1);

        NEATMarioEvolver neat2 = new NEATMarioEvolver(neatPhasedParams,
                () -> new StandardHoldStrat(), sb, "phased-static", ff);
        neat2.setSimOptions(options2);

        NEATMarioEvolver neat3 = new GreenPhasedSearchEvolver(neatPhasedParams,
                 new EnvEnemyGrid(), sb, "phased-green", ff);
        neat3.setSimOptions(options2);

        NEATMarioEvolver neat4 = new SandpilePhasedSearchEvolver(neatPhasedParams,
                new EnvEnemyGrid(), sb, "phased-green-sandpile", ff);
        neat4.setSimOptions(options2);


        NEATReuseMarioEvolver evolver = new NEATReuseMarioEvolver(new NEATMarioEvolver[] { neat1, neat2 });
        evolver.run();

        NEATReuseMarioEvolver evolver2 = new NEATReuseMarioEvolver(new NEATMarioEvolver[] { neat1, neat3 });
        evolver2.run();

        NEATReuseMarioEvolver evolver3 = new NEATReuseMarioEvolver(new NEATMarioEvolver[] { neat1, neat4 });
        evolver3.run();

        writeToFile(sb.toString());
    }

    private void runCoins() {
        NEATParameters neatParams = new NEATParameters();
        neatParams.MAX_GENERATIONS = 500;

        // initialise parameters
        NEATParameters neatPhasedParams = new NEATParameters();
        neatPhasedParams.MAX_GENERATIONS = 500;
        neatPhasedParams.PHASED_SEARCH = true;

        StringBuilder sb = new StringBuilder();
        int seed1 = 0;

        String options1 = levels[3].concat(FastOpts.L_RANDOM_SEED(seed1));
//        String options2 = levels[2].concat(FastOpts.L_RANDOM_SEED(seed2));

        String options2 = options1.replace(FastOpts.L_ENEMY(Enemy.SPIKY_WINGED), "") + FastOpts.L_COINS_ON;

        EncogAgent.FitnessFunction ff = (info) -> info.coinsGained;

        NEATMarioEvolver neat1 = new NEATMarioEvolver(neatParams,
                () -> new StandardHoldStrat(), sb, "neat");
        neat1.setSimOptions(options1);

        NEATMarioEvolver neat2 = new NEATMarioEvolver(neatPhasedParams,
                () -> new StandardHoldStrat(), sb, "phased-static", ff, new CoinEnvEnemyGrid());
        neat2.setSimOptions(options2);

        NEATMarioEvolver neat3 = new GreenPhasedSearchEvolver(neatPhasedParams,
                new CoinEnvEnemyGrid(), sb, "phased-green", ff);
        neat3.setSimOptions(options2);

        NEATMarioEvolver neat4 = new SandpilePhasedSearchEvolver(neatPhasedParams,
                new CoinEnvEnemyGrid(), sb, "phased-green-sandpile", ff);
        neat4.setSimOptions(options2);

        NEATReuseMarioEvolver evolver = new NEATReuseMarioEvolver(new NEATMarioEvolver[] { neat1, neat2 });
        evolver.run();

        NEATReuseMarioEvolver evolver2 = new NEATReuseMarioEvolver(new NEATMarioEvolver[] { neat1, neat3 });
        evolver2.run();

        NEATReuseMarioEvolver evolver3 = new NEATReuseMarioEvolver(new NEATMarioEvolver[] { neat1, neat4 });
        evolver3.run();

        writeToFile(sb.toString());
    }


    // ----- for testing ------
    private Point[] extractFeatures(NEATNetwork network) {
        NEATLink[] links = network.getLinks();
        return Arrays.stream(links)
                .filter(l -> l.getFromNeuron() < network.getInputCount())
                .map(l -> {
                    int neuronNum = l.getFromNeuron();
                    int x = neuronNum % 13;
                    int y = neuronNum / 13;

                    return new Point(x, y);
                })
                .toArray(Point[]::new);
    }

    private static double[] envGridToBinaryArray(Tile[][] tiles) {
        return Arrays.stream(tiles)
                .flatMap(tileRow -> {
                    return Arrays.stream(tileRow)
                            .map(tile -> {
                                // represent unique tiles
                                return tile == Tile.NOTHING ? 0 : 1;
                            });
                })
                .mapToDouble(i->i)
                .toArray();
    }

}
