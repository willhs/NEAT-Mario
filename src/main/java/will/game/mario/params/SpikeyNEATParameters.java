package will.game.mario.params;

import org.encog.engine.network.activation.ActivationBipolarSteepenedSigmoid;

/**
 * Created by Will on 1/09/2016.
 */
public class SpikeyNEATParameters extends NEATParameters {
    public SpikeyNEATParameters() {
        // nn
        ACTIVATION_CYCLES = 3;
        NN_WEIGHT_RANGE = 4.97;
        INIT_CONNECTION_DENSITY = 0.00;
        NN_ACTIVATION_FUNCTION = new ActivationBipolarSteepenedSigmoid();

        SELECTION_PROP = 0.27;
        ELITE_RATE = 0.12;
        CROSSOVER_PROB = 0.29;

        // speciation
        MIN_PER_SPECIE = 10;
        MAX_SPECIES = (int)(POP_SIZE * 0.0959);
        SPECIES_DROPOFF = 44; // questionable
        INIT_COMPAT_THRESHOLD = 10; // 6

        // mutation probs
        ADD_CONN_PROB = 0.67;
        ADD_NEURON_PROB = 0.3;
        PERTURB_PROB = 1.0;
        REMOVE_CONN_PROB = 0.63;

        WEIGHT_MUT_TYPE = WeightMutType.ONCE;
        WEIGHT_PERTURB_PROP = 0.17;
        PERTURB_SD = 0.33; // perturb standard deviation
        RESET_WEIGHT_PROB = 0.11;
    }
}
