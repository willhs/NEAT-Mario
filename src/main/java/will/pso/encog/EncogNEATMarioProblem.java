package will.pso.encog;

import org.encog.engine.network.activation.ActivationBipolarSteepenedSigmoid;
import org.encog.engine.network.activation.ActivationClippedLinear;
import org.encog.ml.CalculateScore;
import org.encog.ml.ea.opp.selection.TruncationSelection;
import org.encog.ml.ea.train.basic.TrainEA;
import org.encog.ml.train.strategy.end.EndIterationsStrategy;
import org.encog.neural.neat.NEATCODEC;
import org.encog.neural.neat.NEATPopulation;
import org.encog.neural.neat.training.opp.*;
import org.encog.neural.neat.training.opp.links.SelectFixed;
import org.encog.neural.neat.training.opp.links.SelectProportion;
import org.encog.neural.neat.training.species.OriginalNEATSpeciation;
import will.neat.encog.EncogMarioFitnessFunction;
import will.neat.encog.MutatePerturbOrResetLinkWeight;
import will.neat.encog.PhasedSearch;
import will.pso.Feature;
import will.pso.WillProblem;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import static will.pso.encog.EncogHyperMarioProblem.PARAMS.SPECIES_DROPOFF;
import static will.pso.encog.EncogNEATMarioProblem.PARAMS.*;

/**
 * Created by Will on 1/09/2016.
 */
public class EncogNEATMarioProblem extends WillProblem {

    // evolution
    public static final int POP_SIZE = 100;
    public static final int MAX_GENERATIONS = 150;

    // species
    private static final int MIN_INDIVIDUAL_PER_SPECIE = 10;
    private static final double COMPAT_THRESHOLD = 8;
    private static final int INPUT_NEURONS = 169;
    private static final int OUTPUT_NEURONS = 4;

    // variable parameters
    public enum PARAMS {
        MAX_SPECIES, MAX_SPECIE_GENS, SURVIVAL_RATIO, ADD_CONN_PROB, REMOVE_CONN_PROB,
        REMOVE_NEURON_PROB, ADD_NEURON_PROB, PERTURB_PROP, PERTURB_SD, RESET_WEIGHT_PROB,
        ELITE_RATE, CROSSOVER_PROB, NN_WEIGHT_RANGE, INITIAL_CONNECTION_DENSITY,
        ACTIVATION_CYCLES, SELECTION_PROP, WEIGHT_MUT_TYPE, ACTIVATION_TYPE, PHASE_LENGTH
    }

    public EncogNEATMarioProblem() {
        // we are aiming for the HIGHEST score, not lowest
        setMinimization(false);
    }

    @Override
    public double fitness(Map<String, Double> features) {
        TrainEA neat = setupNEAT(features);

        // evolve til reached max num of iterations
        while (!neat.isTrainingDone()) {
            neat.iteration();
            System.out.print(".");
        }

        return neat.getBestGenome().getScore();
    }

    private TrainEA setupNEAT(Map<String, Double> features) {
        NEATPopulation population = new NEATPopulation(INPUT_NEURONS, OUTPUT_NEURONS, POP_SIZE);
        population.setActivationCycles((int) (double) features.get(ACTIVATION_CYCLES.name()));
        population.setInitialConnectionDensity(features.get(INITIAL_CONNECTION_DENSITY.name()));
        population.setWeightRange(features.get(NN_WEIGHT_RANGE.name()));
        population.setNEATActivationFunction(features.get(ACTIVATION_TYPE.name()) < 0.5
                ? new ActivationBipolarSteepenedSigmoid()
                : new ActivationClippedLinear()
        );
        population.reset();

        CalculateScore fitnessFunction = new EncogMarioFitnessFunction(true);

        OriginalNEATSpeciation speciation = new OriginalNEATSpeciation();
        speciation.setCompatibilityThreshold(COMPAT_THRESHOLD);
        speciation.setMaxNumberOfSpecies((int) (double) features.get(MAX_SPECIES.name()));
        speciation.setNumGensAllowedNoImprovement((int) (double) features.get(SPECIES_DROPOFF.name()));

        final TrainEA neat = new TrainEA(population, fitnessFunction);
        neat.setSpeciation(speciation);
        neat.setSelection(new TruncationSelection(neat, features.get(SELECTION_PROP.name())));
        neat.setEliteRate(features.get(ELITE_RATE.name()));
//        population.setSurvivalRate(features.get(SURVIVAL_RATIO.name()));
        neat.setCODEC(new NEATCODEC());

        double perturbProp = features.get(PERTURB_PROP.name());
        double perturbSD = features.get(PERTURB_SD.name());
        double resetWeightProb = features.get(RESET_WEIGHT_PROB.name());
        // either perturb a proportion of all weights or just one weight
        NEATMutateWeights weightMutation = new NEATMutateWeights(
                features.get(WEIGHT_MUT_TYPE.name()) < 0.5
                        ? new SelectProportion(perturbProp)
                        : new SelectFixed(1),
                new MutatePerturbOrResetLinkWeight(resetWeightProb, perturbSD)
        );

        neat.addOperation(features.get(CROSSOVER_PROB.name()), new NEATCrossover());
        neat.addOperation(features.get(PERTURB_PROP.name()), weightMutation);

        // non-phased search
/*        neat.addOperation(features.get(ADD_NEURON_PROB.name()), new NEATMutateAddNode());
        neat.addOperation(features.get(ADD_CONN_PROB.name()), new NEATMutateAddLink());
        neat.addOperation(features.get(REMOVE_CONN_PROB.name()), new NEATMutateRemoveLink());
        neat.addOperation(features.get(REMOVE_NEURON_PROB.name()), new NEATMutateRemoveNeuron());*/
        neat.getOperators().finalizeStructure();

        PhasedSearch phasedSearch = new PhasedSearch((int)(double)features.get(PHASE_LENGTH.name()));
        neat.addStrategy(phasedSearch);
        phasedSearch.addPhaseOp(0, features.get(ADD_CONN_PROB.name()), new NEATMutateAddLink());
        phasedSearch.addPhaseOp(0, features.get(ADD_NEURON_PROB.name()), new NEATMutateAddNode());
        phasedSearch.addPhaseOp(1, features.get(REMOVE_CONN_PROB.name()), new NEATMutateRemoveLink());
        phasedSearch.addPhaseOp(1, features.get(REMOVE_NEURON_PROB.name()), new NEATMutateRemoveNeuron());

        neat.setThreadCount(1);

        // end after some number of generations
        neat.addStrategy(new EndIterationsStrategy(MAX_GENERATIONS));

        return neat;
    }

    private static List<Feature> makeFeatures() {
        List<Feature> features = new ArrayList<>();

/*        // variable parameters
        public enum PARAMS {
            MAX_SPECIES, SPECIES_DROPOFF, SURVIVAL_RATIO, ADD_CONN_PROB, REMOVE_CONN_PROB,
            ADD_NEURON_PROB, PERTURB_PROP, PERTURB_SD, RESET_WEIGHT_PROB,
            ELITE_RATE, CROSSOVER_PROB, CPPN_WEIGHT_RANGE, CPPN_MIN_WEIGHT,
            INITIAL_CONNECTION_DENSITY, ACTIVATION_CYCLES, SELECTION_PROP, WEIGHT_MUT_TYPE
        }*/

        //  TODO:
        // disjoint, excess and matched components of speciation
        // proportional mutation for adding and removing nodes and connections
        // substrate: num layers, size layer (or implement ES-HyperNEAT)

        double maxSpecies = POP_SIZE / MIN_INDIVIDUAL_PER_SPECIE;

        double maxSpeciesDropoff = MAX_GENERATIONS / 2;

        // muts
        features.add(new Feature(WEIGHT_MUT_TYPE.name(), 0, 1));
        features.add(new Feature(PERTURB_SD.name(), 0, 1)); // scales with max weight
        features.add(new Feature(PERTURB_PROP.name(), 0, 1));
        features.add(new Feature(RESET_WEIGHT_PROB.name(), 0, 1));
        features.add(new Feature(PHASE_LENGTH.name(), 1, 50));

        features.add(new Feature(ADD_CONN_PROB.name(), 0, 1));
        features.add(new Feature(ADD_NEURON_PROB.name(), 0, 1));

        features.add(new Feature(REMOVE_NEURON_PROB.name(), 0, 1)); // not implemented yet
        features.add(new Feature(REMOVE_CONN_PROB.name(), 0, 1));

        // species
        features.add(new Feature(MAX_SPECIES.name(), 1, maxSpecies));
        features.add(new Feature(SPECIES_DROPOFF.name(), 1, maxSpeciesDropoff));
//        features.add(new Feature(SURVIVAL_RATIO.name(), 0, 0.5)); // useless
        features.add(new Feature(ELITE_RATE.name(), 0, 1));
        features.add(new Feature(SELECTION_PROP.name(), 0, 1));
        features.add(new Feature(CROSSOVER_PROB.name(), 0, 1));

        // nn
        features.add(new Feature(ACTIVATION_CYCLES.name(), 1, 5));
        features.add(new Feature(NN_WEIGHT_RANGE.name(), 0, 5));
        features.add(new Feature(INITIAL_CONNECTION_DENSITY.name(), 0, 1)); // not as useful since CPPN starts with only a few nodes?
        features.add(new Feature(ACTIVATION_TYPE.name(), 0, 1));

        return features;
    }

    public List<Feature> getFeatures() {
        return makeFeatures();
    }
}
