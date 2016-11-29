package will.game.mario.experiment;

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
import will.game.mario.fitness.EncogMarioFitnessFunction;
import will.neat.encog.MutatePerturbOrResetLinkWeight;
import will.neat.encog.substrate.MultiHiddenLayerSubstrate;
import will.game.mario.params.HyperNEATParameters;
import will.game.mario.params.HyperNEATParametersPSO;

/**
 * Created by Will on 2/09/2016.
 */
public class HyperNEATOldEvolver {

    private TrainEA neat;
    private HyperNEATParameters params = new HyperNEATParametersPSO();

    public HyperNEATOldEvolver() {
        HyperNEATParameters hyperParams = params;
        Substrate substrate = new MultiHiddenLayerSubstrate().makeSubstrate();

        NEATPopulation population = new NEATPopulation(substrate, hyperParams.POP_SIZE);
        population.setActivationCycles(hyperParams.ACTIVATION_CYCLES);
        population.setInitialConnectionDensity(hyperParams.INIT_CONNECTION_DENSITY);
        population.setWeightRange(hyperParams.CPPN_WEIGHT_RANGE);
        population.setCPPNMinWeight(hyperParams.CPPN_MIN_WEIGHT);
        population.setHyperNEATNNWeightRange(hyperParams.NN_WEIGHT_RANGE);
        population.setHyperNEATNNActivationFunction(hyperParams.NN_ACTIVATION_FUNCTION);

        population.reset();

        CalculateScore fitnessFunction = new EncogMarioFitnessFunction(
//                AbstractMarioFitnessFunction.DEFAULT_SIM_OPTIONS.replace(AbstractMarioFitnessFunction.LEVEL, FastOpts.LEVEL_05_GAPS).replace(AbstractMarioFitnessFunction.DIFFICULTY, FastOpts.L_DIFFICULTY(1)), network -> new EncogAgent(network)
         );

        OriginalNEATSpeciation speciation = new OriginalNEATSpeciation();
        speciation.setCompatibilityThreshold(hyperParams.INIT_COMPAT_THRESHOLD);
        speciation.setMaxNumberOfSpecies(hyperParams.MAX_SPECIES);
        speciation.setNumGensAllowedNoImprovement(hyperParams.SPECIES_DROPOFF);

        neat = new TrainEA(population, fitnessFunction);
        neat.setSpeciation(speciation);
        neat.setSelection(new TruncationSelection(neat, hyperParams.SELECTION_PROP));
        neat.setEliteRate(hyperParams.ELITE_RATE);
        neat.setCODEC(new HyperNEATCODEC());

        double perturbProp = hyperParams.WEIGHT_PERTURB_PROP;
        double perturbSD = hyperParams.PERTURB_SD;
        double resetWeightProb = hyperParams.RESET_WEIGHT_PROB;
        // either perturb a proportion of all weights or just one weight
        NEATMutateWeights weightMutation = new NEATMutateWeights(
                hyperParams.WEIGHT_MUT_TYPE == HyperNEATParameters.WeightMutType.PROPORTIONAL
                        ? new SelectProportion(perturbProp)
                        : new SelectFixed(1),
                new MutatePerturbOrResetLinkWeight(resetWeightProb, perturbSD)
        );

        neat.addOperation(hyperParams.CROSSOVER_PROB, new NEATCrossover());
        neat.addOperation(hyperParams.PERTURB_PROB, weightMutation);
        neat.addOperation(hyperParams.ADD_NEURON_PROB, new NEATMutateAddNeuron());
        neat.addOperation(hyperParams.ADD_CONN_PROB, new NEATMutateAddLink());
        neat.addOperation(hyperParams.REMOVE_CONN_PROB, new NEATMutateRemoveLink());
        neat.addOperation(hyperParams.REMOVE_NEURON_PROB, new NEATMutateRemoveNeuron());
        neat.getOperators().finalizeStructure();

        neat.setThreadCount(1);

        neat.addStrategy(new EndIterationsStrategy(params.MAX_GENERATIONS));
/*        Substrate substrate = new MultiHiddenLayerSubstrate().makeSubstrate();

        NEATPopulation population = new NEATPopulation(substrate, params.POP_SIZE);
        population.setActivationCycles(params.ACTIVATION_CYCLES);
        population.setInitialConnectionDensity(params.INIT_CONNECTION_DENSITY);
        population.setWeightRange(params.CPPN_WEIGHT_RANGE);
        population.setCPPNMinWeight(params.CPPN_MIN_WEIGHT);
        population.setHyperNEATNNWeightRange(params.NN_WEIGHT_RANGE);
        population.setHyperNEATNNActivationFunction(params.NN_ACTIVATION_FUNCTION);

        population.reset();

        CalculateScore fitnessFunction = new EncogMarioFitnessFunction();

        OriginalNEATSpeciation speciation = new OriginalNEATSpeciation();
        speciation.setCompatibilityThreshold(params.INIT_COMPAT_THRESHOLD);
        speciation.setMaxNumberOfSpecies(params.MAX_SPECIES);
        speciation.setNumGensAllowedNoImprovement(params.SPECIES_DROPOFF);

        neat = new TrainEA(population, fitnessFunction);
        neat.setSpeciation(speciation);
        neat.setSelection(new TruncationSelection(neat, params.SELECTION_PROP));
        neat.setEliteRate(params.ELITE_RATE);
        neat.setCODEC(new HyperNEATCODEC());

        double perturbProp = params.WEIGHT_PERTURB_PROP;
        double perturbSD = params.PERTURB_SD;
        double resetWeightProb = params.RESET_WEIGHT_PROB;
        // either perturb a proportion of all weights or just one weight
        NEATMutateWeights weightMutation = new NEATMutateWeights(
                params.WEIGHT_MUT_TYPE == HyperNEATParameters.WeightMutType.PROPORTIONAL
                        ? new SelectProportion(perturbProp)
                        : new SelectFixed(1),
                new MutatePerturbOrResetLinkWeight(resetWeightProb, perturbSD)
        );

        neat.addOperation(params.CROSSOVER_PROB, new NEATCrossover());
        neat.addOperation(params.PERTURB_PROB, weightMutation);
        neat.addOperation(params.ADD_NEURON_PROB, new NEATMutateAddNeuron());
        neat.addOperation(params.ADD_CONN_PROB, new NEATMutateAddLink());
        neat.addOperation(params.REMOVE_CONN_PROB, new NEATMutateRemoveLink());
        neat.addOperation(params.REMOVE_NEURON_PROB, new NEATMutateRemoveNeuron());
        neat.getOperators().finalizeStructure();
        neat.setThreadCount(1);*/
    }

    public TrainEA getNEAT() {
        return neat;
    }

    public HyperNEATParameters getParams() {
        return params;
    }
}
