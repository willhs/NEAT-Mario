package will.game.mario.mmneat;

import ch.idsia.benchmark.mario.MarioSimulator;
import ch.idsia.benchmark.mario.options.FastOpts;
import ch.idsia.benchmark.mario.options.MarioOptions;
import edu.utexas.cs.nn.MMNEAT.MMNEAT;
import edu.utexas.cs.nn.evolution.genotypes.Genotype;
import edu.utexas.cs.nn.evolution.genotypes.TWEANNGenotype;
import edu.utexas.cs.nn.networks.Network;
import edu.utexas.cs.nn.networks.NetworkTask;
import edu.utexas.cs.nn.networks.hyperneat.HyperNEATTask;
import edu.utexas.cs.nn.networks.hyperneat.Substrate;
import edu.utexas.cs.nn.parameters.Parameters;
import edu.utexas.cs.nn.tasks.NoisyLonerTask;
import edu.utexas.cs.nn.util.datastructures.Pair;
import will.game.mario.agent.MarioNEATAgent;

import java.util.List;

/**
 * Created by hardwiwill on 21/12/16.
 */
public class WillMarioTask <T extends Network> extends NoisyLonerTask<T> implements NetworkTask, HyperNEATTask {

    public static final String MARIO_TYPE = FastOpts.S_MARIO_SMALL;
    public static final String LEVEL_LENGTH = FastOpts.L_LENGTH_1024;

    public static final String RECEPTIVE_FIELD_WIDTH = " " + MarioOptions.IntOption.AI_RECEPTIVE_FIELD_WIDTH.getParam() + " 13";
    public static final String RECEPTIVE_FIELD_HEIGHT = " " + MarioOptions.IntOption.AI_RECEPTIVE_FIELD_HEIGHT.getParam() + " 13";
    public static final String RECEPTIVE_FIELD_MARIO_ROW = " " + MarioOptions.IntOption.AI_MARIO_EGO_ROW.getParam() + " 6";
    public static final String RECEPTIVE_FIELD_MARIO_COL = " " + MarioOptions.IntOption.AI_MARIO_EGO_COLUMN.getParam() + " 6";

    private int trials;
    private String simOptions;

    public WillMarioTask() {
//        this.trials = Parameters.parameters.integerParameter("trials");
        this.trials = 1;
        this.simOptions = " "
                + FastOpts.VIS_OFF + " "
                + MARIO_TYPE
                + LEVEL_LENGTH
                + RECEPTIVE_FIELD_WIDTH
                + RECEPTIVE_FIELD_HEIGHT
                + RECEPTIVE_FIELD_MARIO_ROW
                + RECEPTIVE_FIELD_MARIO_COL
                + FastOpts.L_DIFFICULTY(Parameters.parameters.integerParameter("difficulty"));

        MMNEAT.registerFitnessFunction("Progress");

    }

    @Override
    public String[] sensorLabels() {
        return new String[0];
    }

    @Override
    public String[] outputLabels() {
        return new String[]{"Left", "Right", "Down", "Jump", "Speed"};
        //Note: These may not be correct, as there are only 5/6 -Gab
    }

    @Override
    public List<Substrate> getSubstrateInformation() {
        return null;
    }

    @Override
    public List<Pair<String, String>> getSubstrateConnectivity() {
        return null;
    }

    @Override
    public Pair<double[], double[]> oneEval(Genotype<T> individual, int num) {
        double fitnessSum = 0;
        MMNEATAgent agent = new MMNEATAgent(individual);

        for (int t = 0; t < trials; t++) {
            // do trial with new random seed
            int seed = 1;
//            int seed = new Random().nextInt();

            float trialFitness = playMario(agent, simOptions);

            fitnessSum += trialFitness;
        }

        double averageFitness = fitnessSum / trials;
        double fitnessVal = averageFitness;

        return new Pair<>(new double[]{ fitnessVal }, new double[0] );
    }

    public float playMario(MMNEATAgent agent, String simOptions) {
        MarioSimulator simulator = new MarioSimulator(simOptions);
        simulator.run(agent);

        return agent.getFitness();
    }

    @Override
    public int numObjectives() {
        if(Parameters.parameters.booleanParameter("moMario")){
            return 2;
        } else {
            return 1;
        }
    }

    @Override
    public double getTimeStamp() {
        return 0; // doesn't apply?
    }
}
