package will.game.mario.fitness;

import ch.idsia.benchmark.mario.MarioSimulator;
import ch.idsia.benchmark.mario.options.FastOpts;
import ch.idsia.benchmark.mario.options.MarioOptions;
import will.game.mario.agent.MarioNEATAgent;

import java.util.Random;
import java.util.logging.Level;
import java.util.logging.LogManager;
import java.util.logging.Logger;

/**
 * Created by hardwiwill on 21/07/16.
 * Generic 'N' represents the kind of neural network that is being evaluated
 */
public abstract class AbstractMarioFitnessFunction<N> {

    public static final String LEVEL = FastOpts.LEVEL_06_GOOMBA;
    public static final String TIME_LIMIT = " " + MarioOptions.IntOption.SIMULATION_TIME_LIMIT.getParam() + " 120";
    public static final String DIFFICULTY = FastOpts.L_DIFFICULTY(0);
    public static final String MARIO_TYPE = FastOpts.S_MARIO_SMALL;
    public static final String LEVEL_LENGTH = FastOpts.L_LENGTH_512;

    public static final String RECEPTIVE_FIELD_WIDTH = " " + MarioOptions.IntOption.AI_RECEPTIVE_FIELD_WIDTH.getParam() + " 13";
    public static final String RECEPTIVE_FIELD_HEIGHT = " " + MarioOptions.IntOption.AI_RECEPTIVE_FIELD_HEIGHT.getParam() + " 13";
    public static final String RECEPTIVE_FIELD_MARIO_ROW = " " + MarioOptions.IntOption.AI_MARIO_EGO_ROW.getParam() + " 6";
    public static final String RECEPTIVE_FIELD_MARIO_COL = " " + MarioOptions.IntOption.AI_MARIO_EGO_COLUMN.getParam() + " 6";

    public static final String DEFAULT_SIM_OPTIONS = ""
            + FastOpts.VIS_OFF
            + LEVEL
            + DIFFICULTY
            + MARIO_TYPE
            + TIME_LIMIT
            + LEVEL_LENGTH

            + RECEPTIVE_FIELD_WIDTH
            + RECEPTIVE_FIELD_HEIGHT
            + RECEPTIVE_FIELD_MARIO_ROW
            + RECEPTIVE_FIELD_MARIO_COL
            ;

    private String simOptions = DEFAULT_SIM_OPTIONS;

    protected final int TRIALS = 1;

    // public static for lazy reasons
    public static boolean headless = false;

    protected double bestFitness = 0;

    private int seed = 0;

    public AbstractMarioFitnessFunction(String simOptions, boolean headless) {
        this.simOptions = simOptions;
        this.headless = headless;
        if (headless) {
            disableLogging();
        }
    }

    public AbstractMarioFitnessFunction(String simOptions, boolean headless, int seed) {
        this.simOptions = simOptions;
        this.seed = seed;
        this.headless = headless;
        if (headless) {
            disableLogging();
        }
    }

    public AbstractMarioFitnessFunction(boolean headless) {
        this.headless = headless;
        if (headless) {
            disableLogging();
        }
    }

    private void disableLogging() {
        Logger.getGlobal().setLevel(Level.OFF);
        LogManager.getLogManager().reset();
    }

    public AbstractMarioFitnessFunction() { }

    protected double evaluate(MarioNEATAgent agent, N nn, Logger logger) {
        double fitnessSum = 0;

        Random random = new Random(seed);

        for (int t = 0; t < TRIALS; t++) {
            int seed = random.nextInt();
            String simOptions = getSimOptions(seed);

            float trialFitness = playMario(agent, simOptions);

            // notify best fitness
            if (trialFitness > bestFitness) {
                logger.info("Fitness function saw new best fitness! = " + trialFitness);
            }

            // show the run visually
            if (shouldPlayBack(trialFitness)) {
                logRun(logger, trialFitness, nn);
                String vizSimOptions = simOptions.replace(FastOpts.VIS_OFF, FastOpts.VIS_ON_2X);
                agent.shouldPrint(true);
                float score = playMario(agent, vizSimOptions);
                logger.info("playback got score: " + score);
                agent.shouldPrint(false);
            }

            updateBestFitness(trialFitness);

            fitnessSum += trialFitness;
        }

        double averageFitness = fitnessSum / TRIALS;
        double fitnessVal = averageFitness;

        return fitnessVal;
    }

    public float playMario(MarioNEATAgent agent, String simOptions) {
        MarioSimulator simulator = new MarioSimulator(simOptions);
        simulator.run(agent);

        return agent.getFitness();
    }

    protected boolean shouldPlayBack(double fitness) {
        return !headless;
    }

    private void updateBestFitness(double fitness) {
        if (fitness > bestFitness) {
            bestFitness = fitness;
        }
    }

    protected String getSimOptions(int seed) {
        return simOptions
                + " " + MarioOptions.IntOption.LEVEL_RANDOM_SEED.getParam() + " " + seed;
    }

    protected void logRun(Logger logger, double fitness, N n) {}

}
