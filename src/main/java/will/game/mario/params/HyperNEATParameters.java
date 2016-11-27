package will.game.mario.params;

import org.encog.engine.network.activation.ActivationClippedLinear;

/**
 * Created by Will on 26/08/2016.
 * Members are public and not final only for convenience. They shouldn't be changed though..
 */
public class HyperNEATParameters extends NEATParameters {

    // defaults
    public double CPPN_WEIGHT_RANGE = 1.0; //
    public double CPPN_MIN_WEIGHT = 0.5;


    public HyperNEATParameters() {

        POP_SIZE = 100;

        // nn
        ACTIVATION_CYCLES = 4; // for CPPN
        NN_WEIGHT_RANGE = 5.0;
        INIT_CONNECTION_DENSITY = 0.3; // 1 for fully connected!
        NN_ACTIVATION_FUNCTION = new ActivationClippedLinear();//new ActivationBiPolar();

        SELECTION_PROP = 0.4;
        ELITE_RATE = 0.1;
        CROSSOVER_PROB = 0;

        // speciation
        MIN_PER_SPECIE = 10;
        MAX_SPECIES = POP_SIZE / MIN_PER_SPECIE;
        SPECIES_DROPOFF = 50;
        INIT_COMPAT_THRESHOLD = 8;

        // mutation probs
        ADD_CONN_PROB = 0.5;
        ADD_NEURON_PROB = 0.9;
        REMOVE_CONN_PROB = 0.1;
        REMOVE_NEURON_PROB = 0.0;
        PERTURB_PROB = 0.5;

        WEIGHT_MUT_TYPE = WeightMutType.PROPORTIONAL;
        WEIGHT_PERTURB_PROP = 0.1;
        PERTURB_SD = 0.92; // perturb standard deviation
        RESET_WEIGHT_PROB = 0.2;
    }
}
