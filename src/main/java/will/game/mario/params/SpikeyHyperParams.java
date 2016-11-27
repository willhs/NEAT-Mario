package will.game.mario.params;

import org.encog.engine.network.activation.ActivationClippedLinear;

/**
 * Created by Will on 2/09/2016.
 */
public class SpikeyHyperParams extends HyperNEATParameters {

    public SpikeyHyperParams() {

        // nn
        ACTIVATION_CYCLES = 3; // 4
        NN_WEIGHT_RANGE = 0.2;
        CPPN_MIN_WEIGHT = 0.67;
        CPPN_WEIGHT_RANGE = 2.4;
        INIT_CONNECTION_DENSITY = 0.0; // 1 for fully connected!
        NN_ACTIVATION_FUNCTION = new ActivationClippedLinear();

        SELECTION_PROP = 0.43;
        ELITE_RATE = 0.0;
        CROSSOVER_PROB = 0.91;

        // speciation
        MIN_PER_SPECIE = 10;
        MAX_SPECIES = (int)(0.048*POP_SIZE);//POP_SIZE / MIN_PER_SPECIE;
        SPECIES_DROPOFF = 11;
        INIT_COMPAT_THRESHOLD = 8;

        // mutation probs
        ADD_CONN_PROB = 1;
        ADD_NEURON_PROB = 0.52;
        REMOVE_CONN_PROB = 0.52;
        PERTURB_PROB = 0.5;

        WEIGHT_MUT_TYPE = NEATParameters.WeightMutType.ONCE;
        WEIGHT_PERTURB_PROP = 0.6;
        PERTURB_SD = 0.1; // was 0
        RESET_WEIGHT_PROB = 0.32;
    }
}
