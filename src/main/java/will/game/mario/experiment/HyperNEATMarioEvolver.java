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
import will.game.mario.agent.factory.AgentFactory;
import will.game.mario.fitness.EncogMarioFitnessFunction;
import will.neat.encog.MutatePerturbOrResetLinkWeight;
import will.neat.encog.PhasedSearch;
import will.game.mario.params.HyperNEATParameters;
import will.game.mario.params.NEATParameters;
import will.game.mario.rf.action.ActionStratFactory;

/**
 * Created by Will on 9/10/2016.
 */
public class HyperNEATMarioEvolver extends NEATMarioEvolver {

    public HyperNEATMarioEvolver(HyperNEATParameters params, ActionStratFactory actionStratFactory,
                                 StringBuilder output, String name) {
        super(params, actionStratFactory, output, name);
    }

    public HyperNEATMarioEvolver(HyperNEATParameters params, ActionStratFactory actionStratFactory) {
        super(params, actionStratFactory);
    }

    @Override
    public TrainEA setupNEAT(NEATParameters params, String marioOptions, AgentFactory factory) {
        HyperNEATParameters hyperParams = (HyperNEATParameters) params;
        Substrate substrate = setupSubstrate();

        NEATPopulation population = new NEATPopulation(substrate, hyperParams.POP_SIZE);
        population.setActivationCycles(hyperParams.ACTIVATION_CYCLES);
        population.setInitialConnectionDensity(hyperParams.INIT_CONNECTION_DENSITY);
        population.setWeightRange(hyperParams.CPPN_WEIGHT_RANGE);
        population.setCPPNMinWeight(hyperParams.CPPN_MIN_WEIGHT);
        population.setHyperNEATNNWeightRange(hyperParams.NN_WEIGHT_RANGE);
        population.setHyperNEATNNActivationFunction(hyperParams.NN_ACTIVATION_FUNCTION);

        population.reset();

        CalculateScore fitnessFunction = new EncogMarioFitnessFunction(marioOptions, true, factory);

        OriginalNEATSpeciation speciation = new OriginalNEATSpeciation();
        speciation.setCompatibilityThreshold(hyperParams.INIT_COMPAT_THRESHOLD);
        speciation.setMaxNumberOfSpecies(hyperParams.MAX_SPECIES);
        speciation.setNumGensAllowedNoImprovement(hyperParams.SPECIES_DROPOFF);

        TrainEA neat = new TrainEA(population, fitnessFunction);
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

        if (hyperParams.PHASED_SEARCH) {
            PhasedSearch phasedSearch = new PhasedSearch(
                    params.PHASE_A_LENGTH, params.PHASE_B_LENGTH);
            neat.addStrategy(phasedSearch);

            // additive mutations
            phasedSearch.addPhaseOp(0, params.ADD_CONN_PROB, new NEATMutateAddLink());
            phasedSearch.addPhaseOp(0, params.ADD_NEURON_PROB, new NEATMutateAddNeuron());

            // subtractive mutations
            phasedSearch.addPhaseOp(1, params.REMOVE_CONN_PROB, new NEATMutateRemoveLink());
            phasedSearch.addPhaseOp(1, params.REMOVE_NEURON_PROB, new NEATMutateRemoveNeuron());
        } else {
            neat.addOperation(hyperParams.ADD_NEURON_PROB, new NEATMutateAddNeuron());
            neat.addOperation(hyperParams.ADD_CONN_PROB, new NEATMutateAddLink());
            neat.addOperation(hyperParams.REMOVE_CONN_PROB, new NEATMutateRemoveLink());
            neat.addOperation(hyperParams.REMOVE_NEURON_PROB, new NEATMutateRemoveNeuron());
        }
        neat.getOperators().finalizeStructure();


        neat.setThreadCount(1);

        neat.addStrategy(new EndIterationsStrategy(params.MAX_GENERATIONS));

        return neat;
    }
}
