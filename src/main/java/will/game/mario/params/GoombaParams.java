package will.game.mario.params;

import org.encog.engine.network.activation.ActivationClippedLinear;

/**
 * Created by Will on 26/08/2016.
 */
public class GoombaParams extends HyperNEATParameters {

    public GoombaParams() {
        // nn
        ACTIVATION_CYCLES = 3;
        CPPN_WEIGHT_RANGE = 3.23; //
        NN_WEIGHT_RANGE = 3.08;
        CPPN_MIN_WEIGHT = 0.14;
        INIT_CONNECTION_DENSITY = 0.03; // 1 for fully connected!
        NN_ACTIVATION_FUNCTION = new ActivationClippedLinear();

        // evolution
        POP_SIZE = 200;

        SELECTION_PROP = 0.44;
        ELITE_RATE = 0.08;
        CROSSOVER_PROB = 0.17;

        // speciation
        MIN_PER_SPECIE = 10;
        MAX_SPECIES = (int)(POP_SIZE * 0.0732);
        SPECIES_DROPOFF = 10; // questionable
        INIT_COMPAT_THRESHOLD = 10; // 6

        // mutation probs
        ADD_CONN_PROB = 0.64;
        ADD_NEURON_PROB = 0.27;
        PERTURB_PROB = 1.0;
        REMOVE_CONN_PROB = 0.93;

        WEIGHT_MUT_TYPE = WeightMutType.PROPORTIONAL;
        WEIGHT_PERTURB_PROP = 0.21;
        PERTURB_SD = 0.54; // perturb standard deviation
        RESET_WEIGHT_PROB = 0.32;
    }
}
