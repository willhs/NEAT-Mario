package will.game.mario.params;

import org.encog.engine.network.activation.ActivationClippedLinear;
import org.encog.engine.network.activation.ActivationFunction;

/**
 * Created by Will on 30/08/2016.
 */
public class NEATParameters {

    //
    public int POP_SIZE = 200;
    public int MAX_GENERATIONS = 1000;

    // nn
    public int ACTIVATION_CYCLES = 7; // was 5
    public double NN_WEIGHT_RANGE = 1.0;
    public double INIT_CONNECTION_DENSITY = 0.0; // 1 for fully connected!
    public ActivationFunction NN_ACTIVATION_FUNCTION = new ActivationClippedLinear();
    public int NUM_INPUTS = 169;
    public int NUM_OUTPUTS = 4;

    // selection
    public double SELECTION_PROP = 0.4;
    public double ELITE_RATE = 0.1;
    public double CROSSOVER_PROB = 0;

    // speciation
    public int MIN_PER_SPECIE = 10;
    public int MAX_SPECIES = POP_SIZE / MIN_PER_SPECIE;
    public int SPECIES_DROPOFF = 50;
    public double INIT_COMPAT_THRESHOLD = 8;

    // mutation probs
    public enum WeightMutType { PROPORTIONAL, ONCE }
    public double ADD_CONN_PROB = 0.8;
    public double ADD_NEURON_PROB = 0.7;
    public double PERTURB_PROB = 0.5;
    public double REMOVE_CONN_PROB = 0.1;
    public double REMOVE_NEURON_PROB = 0.1;

    public WeightMutType WEIGHT_MUT_TYPE = WeightMutType.PROPORTIONAL;
    public double WEIGHT_PERTURB_PROP = 0.1;
    public double PERTURB_SD = 0.92; // perturb standard deviation
    public double RESET_WEIGHT_PROB = 0.2;

    // phased search
    public boolean PHASED_SEARCH = false;
    public int PHASE_A_LENGTH = 70;
    public int PHASE_B_LENGTH = 70;
}
