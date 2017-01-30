package will.game.mario.experiment;

import ch.idsia.benchmark.mario.options.FastOpts;
import ch.idsia.benchmark.mario.options.MarioOptions;
import org.encog.ml.ea.population.Population;
import org.encog.ml.ea.train.basic.TrainEA;
import org.encog.neural.neat.NEATPopulation;
import will.game.mario.agent.encog.EncogAgent;
import will.game.mario.experiment.evolve.*;
import will.game.mario.fitness.AbstractMarioFitnessFunction;
import will.game.mario.params.NEATParameters;
import will.game.mario.params.PhasedParameters;
import will.game.mario.rf.environment.EnvEnemyGrid;
import will.game.mario.rf.environment.CoinEnvEnemyGrid;
import will.game.mario.rf.environment.GameEnvironment;
import will.neat.encog.AbstractPhasedSearch;

import java.io.*;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardOpenOption;

import static will.game.mario.experiment.CECExperiments.Task.A;
import static will.game.mario.experiment.CECExperiments.Task.BOTH;
import static will.game.mario.fitness.AbstractMarioFitnessFunction.*;

/**
 * Created by Will on 8/10/2016.
 */
public class CECExperiments {

    public enum Task { A, B, BOTH }

    private final String DEFAULT_SIM_OPTIONS = AbstractMarioFitnessFunction.DEFAULT_SIM_OPTIONS;

    private final String DEFAULT_LEVEL = AbstractMarioFitnessFunction.LEVEL;

    private String[] levels = {
            DEFAULT_SIM_OPTIONS.replace(DEFAULT_LEVEL, FastOpts.LEVEL_05_GAPS)
                                .replace(DIFFICULTY, FastOpts.L_DIFFICULTY(1)),
            DEFAULT_SIM_OPTIONS.replace(DEFAULT_LEVEL, FastOpts.LEVEL_06_GOOMBA),
            DEFAULT_SIM_OPTIONS.replace(DEFAULT_LEVEL, FastOpts.LEVEL_07_SPIKY),
            DEFAULT_SIM_OPTIONS.replace(DEFAULT_LEVEL, FastOpts.LEVEL_08_FLY_SPIKY),
    };

    private static String outputDirName = "neat-output/";
    private static String outputPath = "saved/results.csv";

    private boolean writeFile = true;

    public static final String REVERSE = "reverse";
    public static final String COINS = "coins";

    private String experimentName;
    private String popPath = "saved/pop.xml";
    private String popDir = "saved/pop/";

    private static int TASK_A_GENS = 1000;
    private static int TASK_B_GENS = 500;
    private static int PHASE_LENGTH = 80;
    private static int SEED = 0;
    private static boolean SWITCH_TASK_ORDER = true;
    private static boolean COMPLEXIFICATION_FIRST = true;
    private static int BASE_EVOLVER = 3;
    private static int TRANSFER_EVOLVER = 0;
    private static Task RUN_TYPE = BOTH;

    public CECExperiments(String name) {
        this.experimentName = name;
    }

    public static void main(String[] args) throws IOException {

        String level = null;
        if (args.length == 0) {
            level = COINS;//NEAT_STANDARD_HOLD;
        } else if (args.length > 0) {
            level = args[0];
            outputPath = args[0];
        }
        if (args.length > 1) {
            outputPath = args[1];
        }
        if (args.length > 2) {
            TASK_A_GENS = Integer.parseInt(args[2]);
        }
        if (args.length > 3) {
            TASK_B_GENS = Integer.parseInt(args[3]);
        }
        if (args.length > 4) {
            PHASE_LENGTH = Integer.parseInt(args[4]);
        }
        if (args.length > 5) {
            BASE_EVOLVER = Integer.parseInt(args[5]);
        }
        if (args.length > 6) {
            TRANSFER_EVOLVER = Integer.parseInt(args[6]);
        }
        if (args.length > 7) {
            COMPLEXIFICATION_FIRST = Boolean.parseBoolean(args[7]);
        }
        if (args.length > 8) {
            RUN_TYPE = Task.valueOf(args[8]);
        }
        if (args.length > 9) {
            SEED = Integer.parseInt(args[9]);
        }

        CECExperiments ex = new CECExperiments(level);
//        ex.run();

        ex.runExperiment(level);
    }

    private void runExperiment(String level) {
        StringBuilder sb = new StringBuilder();

        if (level.equals(REVERSE)) {
            runReverse();
        } else if (level.equals(COINS)) {
            runCoins(sb);
        } else if (level.equals("kills")) {
            runKills(sb);
        } else if (level.equals("speed-enemies")) {
            runSpeedEnemies(sb);
        } else if (level.equals("fly")) {
            runGoomba2Winged(sb);
        } else if (level.equals("speed-kills")) {
            runSpeedKills(sb);
        } else if (level.equals("dist-coins")) {
            runSpeedCoins(sb);
        }

        if (writeFile) {
            writeToFile(sb.toString());
        }
    }

    private void runReverse() {
        StringBuilder sb = new StringBuilder();

        NEATParameters neatParams = new NEATParameters();
        neatParams.MAX_GENERATIONS = TASK_A_GENS;

        // initialise parameters
        NEATParameters neatPhasedParams = new NEATParameters();
        neatPhasedParams.MAX_GENERATIONS = TASK_A_GENS;
        neatPhasedParams.PHASED_SEARCH = true;

        int seed1 = 0;

        String options1 = levels[1].concat(FastOpts.L_RANDOM_SEED(seed1));
//        String options2 = levels[2].concat(FastOpts.L_RANDOM_SEED(seed2));

        String options2 = levels[1]
                + " " + MarioOptions.IntOption.LEVEL_MARIO_INITIAL_POSITION_X.getParam()
                + " " + (1024*16-20);

        EncogAgent.FitnessFunction ff = (info) -> info.levelLength - info.distancePassedCells;


        if (writeFile) {
            writeToFile(sb.toString());
        }
    }

    private void runCoins(StringBuilder output) {

        String baseOpts =
//                levels[1]
                DEFAULT_SIM_OPTIONS
                .replace(DEFAULT_LEVEL, FastOpts.LEVEL_08_FLY_SPIKY)
                .replace(LEVEL_LENGTH, FastOpts.L_LENGTH_512)
//                .replace(FastOpts.VIS_OFF, FastOpts.VIS_ON_2X)
//                + FastOpts.L_RANDOM_SEED(0)
                ;
        String transferOpts =
//                levels[1]
                DEFAULT_SIM_OPTIONS
                .replace(DEFAULT_LEVEL, FastOpts.COINS)
                .replace(LEVEL_LENGTH, FastOpts.L_LENGTH_512)
//                .replace(FastOpts.VIS_OFF, FastOpts.VIS_ON_2X)
//                + FastOpts.L_RANDOM_SEED(1)
                ;

        EncogAgent.FitnessFunction baseFF = (info) -> info.distancePassedCells;
        EncogAgent.FitnessFunction transferFF = (info) -> info.coinsGained;

        NEATMarioEvolver[] baseEvolvers = getCECEvolvers(output, baseFF, new EnvEnemyGrid(), baseOpts, A);
        NEATMarioEvolver[] transferEvolvers = getCECEvolvers(output, transferFF, new CoinEnvEnemyGrid(), transferOpts, A);

        runTransferLearningExperiment(baseEvolvers, transferEvolvers);
//        runTransferLearningExperiment(baseEvolver, transferEvolvers[0]);

//        Population pop = baseEvolver.run().getPopulation();
//        writePopulation(pop, popDir + "fly-spiky-neat-200.obj");
//        NEATPopulation pop1 = readNEATPopulation(popDir + "fly-spiky-neat-200.obj");
//        runOnPop(pop1, transferEvolvers[0]);
//        NEATPopulation pop2 = readNEATPopulation(popDir + "fly-spiky-neat-200.obj");
//        runOnPop(pop2, transferEvolvers[2]);
    }

    private void runKills(StringBuilder output) {

        String baseOpts =
//                levels[1]
                DEFAULT_SIM_OPTIONS
                        .replace(DEFAULT_LEVEL, FastOpts.LEVEL_06_GOOMBA)
                        .replace(MARIO_TYPE, FastOpts.S_MARIO_FIRE)
                        .replace(LEVEL_LENGTH, FastOpts.L_LENGTH_256)
                        .replace(DIFFICULTY, FastOpts.L_DIFFICULTY(2))
//                .replace(FastOpts.VIS_OFF, FastOpts.VIS_ON_2X)
//                + FastOpts.L_RANDOM_SEED(0)
//                + FastOpts.S_MARIO_INVULNERABLE
                ;

        String transferOpts =
//                levels[1]
                DEFAULT_SIM_OPTIONS
                        .replace(DEFAULT_LEVEL, FastOpts.LEVEL_06_GOOMBA)
//                        .replace(MARIO_TYPE, FastOpts.S_MARIO_FIRE)
                        .replace(LEVEL_LENGTH, FastOpts.L_LENGTH_256)
                        .replace(DIFFICULTY, FastOpts.L_DIFFICULTY(2))
//                .replace(FastOpts.VIS_OFF, FastOpts.VIS_ON_2X)
//                + FastOpts.L_RANDOM_SEED(1)
//                + FastOpts.S_MARIO_INVULNERABLE
                ;

        EncogAgent.FitnessFunction baseFF = (info) -> info.killsTotal;
        EncogAgent.FitnessFunction transferFF = (info) -> info.killsByStomp;

        NEATMarioEvolver[] baseEvolvers = getCECEvolvers(output, baseFF, new EnvEnemyGrid(), baseOpts, A);
        NEATMarioEvolver[] transferEvolvers = getCECEvolvers(output, transferFF, new EnvEnemyGrid(), transferOpts, A);

        runTransferLearningExperiment(baseEvolvers, transferEvolvers);
//        runTransferLearningExperiment(baseEvolver, transferEvolvers[0]);


//        Population pop = baseEvolver.run().getPopulation();
//        writePopulation(popDir + "kills-firemario-phased.obj");// score = 77 kills

//        NEATPopulation pop1 = readNEATPopulation(popDir + "kills-firemario-200.obj");
//        runOnPop(pop1, transferEvolvers[0]);
//        NEATPopulation pop2 = readNEATPopulation(popDir + "kills-firemario-200.obj");
//        runOnPop(pop2, transferEvolvers[2]);
    }

    private void runSpeedEnemies(StringBuilder output) {
        String baseOpts =
//                levels[1]
                DEFAULT_SIM_OPTIONS
                        .replace(DEFAULT_LEVEL, FastOpts.HILLY_BLANK)
                        .replace(LEVEL_LENGTH, FastOpts.L_LENGTH_512)
//                .replace(FastOpts.VIS_OFF, FastOpts.VIS_ON_2X)
//                + FastOpts.L_RANDOM_SEED(0)
//                + FastOpts.S_MARIO_INVULNERABLE
                ;

        String transferOpts =
//                levels[1]
                DEFAULT_SIM_OPTIONS
                        .replace(DEFAULT_LEVEL, FastOpts.LEVEL_06_GOOMBA)
//                        .replace(MARIO_TYPE, FastOpts.S_MARIO_FIRE)
                        .replace(LEVEL_LENGTH, FastOpts.L_LENGTH_512)
                        .replace(DIFFICULTY, FastOpts.L_DIFFICULTY(0))
//                .replace(FastOpts.VIS_OFF, FastOpts.VIS_ON_2X)
//                + FastOpts.L_RANDOM_SEED(1)
//                + FastOpts.S_MARIO_INVULNERABLE
                ;

        EncogAgent.FitnessFunction baseFF = (info) -> info.distancePassedCells - info.timeSpent;
        EncogAgent.FitnessFunction transferFF = (info) -> info.distancePassedCells - info.timeSpent;

        NEATMarioEvolver[] baseEvolvers = getCECEvolvers(output, baseFF, new EnvEnemyGrid(), baseOpts, A);
        NEATMarioEvolver[] transferEvolvers = getCECEvolvers(output, transferFF, new EnvEnemyGrid(), transferOpts, A);

        runTransferLearningExperiment(baseEvolvers, transferEvolvers);
//        runTransferLearningExperiment(baseEvolver, transferEvolvers[0]);

//        Population pop = baseEvolver.run().getPopulation();
//        writePopulation(pop, popDir + "run-hilly-neat-200.obj");

//        NEATPopulation pop1 = readNEATPopulation(popDir + "run-hilly-neat-200.obj");
//        runOnPop(pop1, transferEvolvers[0]);
//        NEATPopulation pop2 = readNEATPopulation(popDir + "run-hilly-neat-200.obj");
//        runOnPop(pop2, transferEvolvers[2]);
    }

    private void runGoomba2Winged(StringBuilder output) {
        String baseOpts =
//                levels[1]
                DEFAULT_SIM_OPTIONS
                        .replace(DEFAULT_LEVEL, FastOpts.LEVEL_06_GOOMBA)
                        .replace(LEVEL_LENGTH, FastOpts.L_LENGTH_512)
//                .replace(FastOpts.VIS_OFF, FastOpts.VIS_ON_2X)
//                + FastOpts.L_RANDOM_SEED(0)
//                + FastOpts.S_MARIO_INVULNERABLE
                ;

        String transferOpts =
//                levels[1]
                DEFAULT_SIM_OPTIONS
                        .replace(DEFAULT_LEVEL, FastOpts.GOOMBAS_WINGED)
//                        .replace(MARIO_TYPE, FastOpts.S_MARIO_FIRE)
                        .replace(LEVEL_LENGTH, FastOpts.L_LENGTH_512)
                        .replace(DIFFICULTY, FastOpts.L_DIFFICULTY(0))
//                .replace(FastOpts.VIS_OFF, FastOpts.VIS_ON_2X)
//                + FastOpts.L_RANDOM_SEED(1)
//                + FastOpts.S_MARIO_INVULNERABLE
                ;

        EncogAgent.FitnessFunction baseFF = (info) -> info.distancePassedCells - info.timeSpent;
        EncogAgent.FitnessFunction transferFF = (info) -> info.distancePassedCells - info.timeSpent;

        NEATMarioEvolver[] baseEvolvers = getCECEvolvers(output, baseFF, new EnvEnemyGrid(), baseOpts, A);
        NEATMarioEvolver[] transferEvolvers = getCECEvolvers(output, transferFF, new EnvEnemyGrid(), transferOpts, A);

        runTransferLearningExperiment(baseEvolvers, transferEvolvers);
//        runTransferLearningExperiment(baseEvolvers[2], transferEvolvers[0]);

//        Population neatPop = baseEvolvers[0].run().getPopulation();
//        writePopulation(neatPop, popDir + "goombas-neat-1000.obj");
//        Population phasedPop = baseEvolvers[2].run().getPopulation();
//        writePopulation(phasedPop, popDir + "goombas-phased-1000.obj");

//        NEATPopulation pop1 = readNEATPopulation(popDir + "goombas-phased-1000.obj");
//        runOnPop(pop1, transferEvolvers[2]);
//        NEATPopulation pop2 = readNEATPopulation(popDir + "run-hilly-neat-200.obj");
//        runOnPop(pop2, transferEvolvers[2]);
    }

    private void runSpeedKills(StringBuilder output) {

        String baseOpts =
//                levels[1]
                DEFAULT_SIM_OPTIONS
                        .replace(DEFAULT_LEVEL, FastOpts.LEVEL_06_GOOMBA)
                        .replace(LEVEL_LENGTH, FastOpts.L_LENGTH_512)
                        .replace(DIFFICULTY, FastOpts.L_DIFFICULTY(1))
//                .replace(FastOpts.VIS_OFF, FastOpts.VIS_ON_2X)
//                + FastOpts.L_RANDOM_SEED(0)
//                + FastOpts.S_MARIO_INVULNERABLE
                ;

        String transferOpts =
//                levels[1]
                DEFAULT_SIM_OPTIONS
                        .replace(DEFAULT_LEVEL, FastOpts.LEVEL_06_GOOMBA)
                        .replace(LEVEL_LENGTH, FastOpts.L_LENGTH_512)
                        .replace(DIFFICULTY, FastOpts.L_DIFFICULTY(1))
//                .replace(FastOpts.VIS_OFF, FastOpts.VIS_ON_2X)
//                + FastOpts.L_RANDOM_SEED(1)
//                + FastOpts.S_MARIO_INVULNERABLE
                ;

        EncogAgent.FitnessFunction baseFF = (info) -> info.distancePassedCells - info.timeSpent;
        EncogAgent.FitnessFunction transferFF = (info) -> (info.distancePassedCells - info.timeSpent) + (info.killsTotal * 5);

        NEATMarioEvolver[] baseEvolvers = getCECEvolvers(output, baseFF, new EnvEnemyGrid(), baseOpts, A);
        NEATMarioEvolver[] transferEvolvers = getCECEvolvers(output, transferFF, new EnvEnemyGrid(), transferOpts, A);

        runTransferLearningExperiment(baseEvolvers, transferEvolvers);

//        Population pop = baseEvolver.run().getPopulation();
//        writePopulation(pop, popDir + "run-hilly-neat-200.obj");

//        NEATPopulation pop1 = readNEATPopulation(popDir + "run-hilly-neat-200.obj");
//        runOnPop(pop1, transferEvolvers[0]);
//        NEATPopulation pop2 = readNEATPopulation(popDir + "run-hilly-neat-200.obj");
//        runOnPop(pop2, transferEvolvers[2]);
    }

    private void runSpeedCoins(StringBuilder output) {
        String baseOpts =
//                levels[1]
                DEFAULT_SIM_OPTIONS
                        .replace(DEFAULT_LEVEL, FastOpts.FLAT_GOOMBAS)
                        .replace(LEVEL_LENGTH, FastOpts.L_LENGTH_512)
                        .replace(DIFFICULTY, FastOpts.L_DIFFICULTY(0))
//                .replace(FastOpts.VIS_OFF, FastOpts.VIS_ON_2X)
//                + FastOpts.L_RANDOM_SEED(0)
//                + FastOpts.S_MARIO_INVULNERABLE
                ;

        String transferOpts =
//                levels[1]
                DEFAULT_SIM_OPTIONS
                        .replace(DEFAULT_LEVEL, FastOpts.FLAT_GOOMBAS)
                        .replace(LEVEL_LENGTH, FastOpts.L_LENGTH_512)
                        .replace(DIFFICULTY, FastOpts.L_DIFFICULTY(0))
//                .replace(FastOpts.VIS_OFF, FastOpts.VIS_ON_2X)
                + FastOpts.L_COINS_ON
//                + FastOpts.S_MARIO_INVULNERABLE
                ;

        EncogAgent.FitnessFunction baseFF = (info) -> info.distancePassedCells - info.timeSpent;
        EncogAgent.FitnessFunction transferFF = (info) -> (info.distancePassedCells - info.timeSpent) + (info.coinsGained * 5);

        NEATMarioEvolver[] baseEvolvers = getCECEvolvers(output, baseFF, new EnvEnemyGrid(), baseOpts, A);
        NEATMarioEvolver[] transferEvolvers = getCECEvolvers(output, transferFF, new CoinEnvEnemyGrid(), transferOpts, A);

        runTransferLearningExperiment(baseEvolvers, transferEvolvers);
//        runTransferLearningExperiment(baseEvolver, transferEvolvers[0]);

//        Population pop = baseEvolver.run().getPopulation();
//        writePopulation(pop, popDir + "run-hilly-neat-200.obj");

//        NEATPopulation pop1 = readNEATPopulation(popDir + "run-hilly-neat-200.obj");
//        runOnPop(pop1, transferEvolvers[0]);
//        NEATPopulation pop2 = readNEATPopulation(popDir + "run-hilly-neat-200.obj");
//        runOnPop(pop2, transferEvolvers[2]);
    }

    private void writePopulation(Population pop, String filename) {
        try {
            FileOutputStream fout = new FileOutputStream(filename);
            ObjectOutputStream oos = new ObjectOutputStream(fout);
            oos.writeObject(pop);
            oos.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private NEATPopulation readNEATPopulation(String filename) {
        try {
            FileInputStream streamIn = new FileInputStream(filename);
            ObjectInputStream ois = new ObjectInputStream(streamIn);
            NEATPopulation pop = (NEATPopulation) ois.readObject();

            // fix up the deserialized pop
            pop.getSpecies().stream()
                    .forEach(s -> s.getMembers().stream()
                        .forEach(m -> {
                            m.setPopulation(pop);
                            m.setSpecies(s);
                        })
                    );
            return pop;

        } catch (IOException | ClassNotFoundException e) {
            e.printStackTrace();
        }
        return null;
    }

    private NEATMarioEvolver[] getCECEvolvers(StringBuilder sb, EncogAgent.FitnessFunction ff,
                                              GameEnvironment env, String simOptions, Task task) {
        int gens = task == A ? TASK_A_GENS : TASK_B_GENS;

        NEATParameters neatParams = new NEATParameters();
        neatParams.MAX_GENERATIONS = gens;
        neatParams.REMOVE_CONN_PROB = 0;
        neatParams.REMOVE_NEURON_PROB = 0;

        NEATParameters blendedParams = new NEATParameters();
        blendedParams.MAX_GENERATIONS = gens;

        PhasedParameters phasedParams = new PhasedParameters();
        phasedParams.MAX_GENERATIONS = gens;
        phasedParams.PHASED_SEARCH = true;
        phasedParams.PHASE_A_LENGTH = PHASE_LENGTH;
        phasedParams.PHASE_B_LENGTH = PHASE_LENGTH;
        phasedParams.STARTING_PHASE = COMPLEXIFICATION_FIRST
                ? AbstractPhasedSearch.Phase.COMPLEXIFICATION
                : AbstractPhasedSearch.Phase.SIMPLIFICATION;


        NEATMarioEvolver neat = new NEATMarioEvolver(neatParams,
                simOptions, env, sb, "neat", ff, SEED);

        NEATMarioEvolver blended = new NEATMarioEvolver(blendedParams,
                simOptions, env, sb, "blended", ff, SEED);

        NEATMarioEvolver phasedStatic = new NEATMarioEvolver(phasedParams,
                simOptions, env, sb, "phased-static", ff, SEED);

        NEATMarioEvolver phasedGreen = new GreenPhasedSearchEvolver(phasedParams,
                simOptions, env, sb, "phased-green", ff, SEED);

/*        NEATMarioEvolver phasedSan = new SandpilePhasedSearchEvolver(phasedParams,
                env, sb, "phased-green-sandpile", ff);
        neat4.setSimOptions(simOptions);*/

        return new NEATMarioEvolver[] {neat, blended, phasedStatic, phasedGreen};
    }

    private void runTransferLearningExperiment(NEATMarioEvolver start, NEATMarioEvolver next) {
        NEATMarioEvolver[] evolvers = null;
        if (SWITCH_TASK_ORDER) {
            evolvers = new NEATMarioEvolver[] {start, next};
        } else {
            evolvers = new NEATMarioEvolver[] {next, start};
        }
        NEATReuseMarioEvolver transferEvolver = new NEATReuseMarioEvolver(evolvers);
        transferEvolver.run();
    }

    private void runTransferLearningExperiment(NEATMarioEvolver[] bases, NEATMarioEvolver[] transferEvolvers) {
        NEATMarioEvolver baseEvolver = bases[BASE_EVOLVER];
        NEATMarioEvolver transferEvolver = transferEvolvers[TRANSFER_EVOLVER];
        if (RUN_TYPE == Task.BOTH) {
            runTransferLearningExperiment(baseEvolver, transferEvolver);
        } else if (RUN_TYPE == Task.A) {
            baseEvolver.run();
        } else if (RUN_TYPE == Task.B) {
            transferEvolver.run();
        }
    }

    private void runOnPop(NEATPopulation pop, NEATMarioEvolver evolver) {
        TrainEA neat = evolver.getNEAT();
        neat.setPopulation(pop);
        evolver.run(neat);
    }

    private void writeToFile(String s) {
        // make sure output dir exists
        File outputDir = new File(outputDirName);
        if (!outputDir.exists()) {
            outputDir.getAbsoluteFile().mkdirs();
        }

        int numFiles = outputDir.listFiles().length;

//            String outputPath = outputDirName + experimentName + "-" + numFiles + ".csv";
        Path output = Paths.get(outputPath);
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
