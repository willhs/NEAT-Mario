package will.game.mario.params;

import org.encog.engine.network.activation.ActivationSteepenedSigmoid;

/**
 * Created by Will on 8/10/2016.
 */
public class HyperNEATParametersPSO extends HyperNEATParameters{

    public HyperNEATParametersPSO() {
        POP_SIZE = 100;

        // nn
        ACTIVATION_CYCLES = 4; // for CPPN
        NN_WEIGHT_RANGE = 3.5; // or 0.04
        INIT_CONNECTION_DENSITY = 0.95; // 1 for fully connected!
        NN_ACTIVATION_FUNCTION = new ActivationSteepenedSigmoid();//new ActivationBiPolar();

        SELECTION_PROP = 0.4; // varies wildly
        ELITE_RATE = 0.22;
        CROSSOVER_PROB = 0.2; // or 0.8

        // speciation
        MIN_PER_SPECIE = 9;
        MAX_SPECIES = POP_SIZE / MIN_PER_SPECIE;
        SPECIES_DROPOFF = 50;
        INIT_COMPAT_THRESHOLD = 8;

        // mutation probs
        ADD_CONN_PROB = 0.8; // varies from 0.26 - 0.84
        ADD_NEURON_PROB = 0.9; // same as above, also diff between conns and neurons vary
        REMOVE_CONN_PROB = 0.5; //
        REMOVE_NEURON_PROB = 0.3; // no data
        PERTURB_PROB = 0.5; //

        WEIGHT_MUT_TYPE = NEATParameters.WeightMutType.PROPORTIONAL;
        WEIGHT_PERTURB_PROP = 0.3; // varies pretty wildly again
        PERTURB_SD = 0.5; // perturb standard deviation
        RESET_WEIGHT_PROB = 0.2; // varies wildy
        PHASED_SEARCH = false;
    }
}
