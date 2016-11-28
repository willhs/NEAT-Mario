package will.pso.encog;

import org.encog.engine.network.activation.ActivationBipolarSteepenedSigmoid;
import org.encog.engine.network.activation.ActivationClippedLinear;
import org.encog.ml.CalculateScore;
import org.encog.ml.ea.opp.selection.TruncationSelection;
import org.encog.ml.ea.train.basic.TrainEA;
import org.encog.ml.train.strategy.end.EndIterationsStrategy;
import org.encog.neural.hyperneat.HyperNEATCODEC;
import org.encog.neural.hyperneat.substrate.Substrate;
import org.encog.neural.neat.NEATPopulation;
import org.encog.neural.neat.training.opp.*;
import org.encog.neural.neat.training.opp.links.SelectFixed;
import org.encog.neural.neat.training.opp.links.SelectProportion;
import org.encog.neural.neat.training.species.OriginalNEATSpeciation;
import will.neat.encog.EncogMarioFitnessFunction;
import will.neat.encog.MutatePerturbOrResetLinkWeight;
import will.neat.encog.substrate.MultiHiddenLayerSubstrate;
import will.pso.Feature;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import static will.pso.encog.EncogHyperMarioProblem.PARAMS.*;

/**
 * Created by Will on 22/08/2016.
 */
public class EncogHyperMarioProblem extends EncogNEATMarioProblem {

    // evolution
    public static final int POP_SIZE = 100;
    public static final int GENERATIONS = 70;

    // species
    public static final int MIN_INDIVIDUAL_PER_SPECIE = 10;
    public static final int MAX_MAX_SPECIES = POP_SIZE/MIN_INDIVIDUAL_PER_SPECIE;
    public static final int MAX_SPECIES_DROPOFF = POP_SIZE;
    public static final double COMPAT_THRESHOLD = 8;

    // variable parameters
    public enum PARAMS {
        MAX_SPECIES, SPECIES_DROPOFF, SURVIVAL_RATIO, ADD_CONN_PROB, REMOVE_CONN_PROB,
        REMOVE_NEURON_PROB, ADD_NEURON_PROB, WEIGHT_PERTURB_PROP, PERTURB_SD, RESET_WEIGHT_PROB,
        ELITE_RATE, CROSSOVER_PROB, NN_WEIGHT_RANGE, CPPN_WEIGHT_RANGE, CPPN_MIN_WEIGHT,
        INITIAL_CONNECTION_DENSITY, ACTIVATION_CYCLES, SELECTION_PROP, WEIGHT_MUT_TYPE,
        ACTIVATION_TYPE
    }

    public EncogHyperMarioProblem() {
        // we are aiming for the HIGHEST score, not lowest
        setMinimization(false);
    }

    @Override
    public double fitness(Map<String, Double> features) {
        NEATPopulation population = makePopulation(features);
        TrainEA neat = makeNEAT(population, features);

        // evolve til reached max num of iterations
        while (!neat.isTrainingDone()) {
            neat.iteration();
            System.out.print(".");
        }

        return neat.getBestGenome().getScore();
    }

    private NEATPopulation makePopulation(Map<String, Double> features) {
        // static things
        Substrate substrate = new MultiHiddenLayerSubstrate().makeSubstrate();
        NEATPopulation population = new NEATPopulation(substrate, POP_SIZE);
        population.reset();

        // dynamic things
        population.setInitialConnectionDensity(features.get(INITIAL_CONNECTION_DENSITY.name()));
//        population.setSurvivalRate(features.get(SURVIVAL_RATIO.name()));
        population.setActivationCycles((int)(double)features.get(ACTIVATION_CYCLES.name()));
        population.setWeightRange(features.get(CPPN_WEIGHT_RANGE.name()));
        population.setHyperNEATNNWeightRange(features.get(NN_WEIGHT_RANGE.name()));
        population.setCPPNMinWeight(features.get(CPPN_MIN_WEIGHT.name()));
        population.setHyperNEATNNActivationFunction(features.get(ACTIVATION_TYPE.name()) < 0.5
                ? new ActivationBipolarSteepenedSigmoid()
                : new ActivationClippedLinear()
        );

        return population;
    }

    private TrainEA makeNEAT(NEATPopulation pop, Map<String, Double> features) {
        // static things
        CalculateScore fitnessFunction = new EncogMarioFitnessFunction(true);
        final TrainEA neat = new TrainEA(pop, fitnessFunction);

        OriginalNEATSpeciation speciation = new OriginalNEATSpeciation();
        speciation.setCompatibilityThreshold(COMPAT_THRESHOLD);
        neat.setSpeciation(speciation);
        neat.setCODEC(new HyperNEATCODEC());

        // dynamic things
        // genetic
        final int MAX_SPECIES_NUM = (int)(features.get(MAX_SPECIES.name()) * POP_SIZE);
        final int SPECIES_DROPOFF = (int)(features.get(PARAMS.SPECIES_DROPOFF.name()) * POP_SIZE);
        speciation.setMaxNumberOfSpecies(MAX_SPECIES_NUM);
        speciation.setNumGensAllowedNoImprovement(SPECIES_DROPOFF);
        neat.setSelection(new TruncationSelection(neat, features.get(SELECTION_PROP.name())));
        neat.setEliteRate(features.get(ELITE_RATE.name()));
        neat.addOperation(features.get(CROSSOVER_PROB.name()), new NEATCrossover());

        // mutations
        neat.addOperation(features.get(ADD_NEURON_PROB.name()), new NEATMutateAddNeuron());
        neat.addOperation(features.get(ADD_CONN_PROB.name()), new NEATMutateAddLink());
//        neat.addOperation(features.get(REMOVE_NEURON_PROB.name(), new NEATRemoveNode())); // not implemented yet
        neat.addOperation(features.get(REMOVE_CONN_PROB.name()), new NEATMutateRemoveLink());
        double perturbProp = features.get(WEIGHT_PERTURB_PROP.name());
        double weightRange = features.get(NN_WEIGHT_RANGE.name());
        double perturbSD = features.get(PERTURB_SD.name()) * weightRange;
        double resetWeightProb = features.get(RESET_WEIGHT_PROB.name());
        // either perturb a proportion of all weights or just one weight
        NEATMutateWeights weightMutation = new NEATMutateWeights(
                features.get(WEIGHT_MUT_TYPE.name()) < 0.5
                        ? new SelectProportion(perturbProp)
                        : new SelectFixed(1),
                new MutatePerturbOrResetLinkWeight(resetWeightProb, perturbSD)
        );
        neat.addOperation(1, weightMutation);
        neat.getOperators().finalizeStructure();

        neat.setThreadCount(1);

        // end after some number of generations
        neat.addStrategy(new EndIterationsStrategy(GENERATIONS));

        return neat;
    }

    private static List<Feature> makeFeatures() {
        List<Feature> features = new ArrayList<>();

        //  TODO:
        // disjoint, excess and matched components of speciation
        // proportional mutation for adding and removing nodes and connections
        // substrate: num layers, size layer (or implement ES-HyperNEAT)

        double maxSpecies = POP_SIZE / MIN_INDIVIDUAL_PER_SPECIE;

        double maxSpeciesDropoff = GENERATIONS /2;

        // muts
        features.add(new Feature(ADD_CONN_PROB.name(), 0, 1));
        features.add(new Feature(ADD_NEURON_PROB.name(), 0, 1));
//        features.add(new Feature(REMOVE_NEURON_PROB.name(), 0, 1)); // not implemented yet
        features.add(new Feature(REMOVE_CONN_PROB.name(), 0, 1));
        features.add(new Feature(WEIGHT_MUT_TYPE.name(), 0, 1));
        features.add(new Feature(PERTURB_SD.name(), 0, 1)); // scales with max weight
        features.add(new Feature(WEIGHT_PERTURB_PROP.name(), 0, 1));
        features.add(new Feature(RESET_WEIGHT_PROB.name(), 0, 1));

        // species
        // these vals are proportional to pop size
        double MAX_SPECIES_PROP = MAX_MAX_SPECIES / (double)POP_SIZE;
        double MAX_SPECIES_DROPOFF_PROP = MAX_SPECIES_DROPOFF / (double)POP_SIZE;
        features.add(new Feature(MAX_SPECIES.name(), 0, MAX_SPECIES_PROP));
        features.add(new Feature(SPECIES_DROPOFF.name(), 0, MAX_SPECIES_DROPOFF_PROP));
//        features.add(new Feature(SURVIVAL_RATIO.name(), 0, 0.5)); // useless
        features.add(new Feature(ELITE_RATE.name(), 0, 1));
        features.add(new Feature(SELECTION_PROP.name(), 0, 1));
        features.add(new Feature(CROSSOVER_PROB.name(), 0, 1));

        // nn
        features.add(new Feature(ACTIVATION_CYCLES.name(), 1, 5));
        features.add(new Feature(NN_WEIGHT_RANGE.name(), 0, 5));
        features.add(new Feature(CPPN_WEIGHT_RANGE.name(), 0, 5));
        features.add(new Feature(CPPN_MIN_WEIGHT.name(), 0, 1)); // scales with max weight
        features.add(new Feature(INITIAL_CONNECTION_DENSITY.name(), 0, 1)); // not as useful since CPPN starts with only a few nodes?
        features.add(new Feature(ACTIVATION_TYPE.name(), 0, 1));

        return features;
    }

    public List<Feature> getFeatures() {
        return makeFeatures();
    }

}
