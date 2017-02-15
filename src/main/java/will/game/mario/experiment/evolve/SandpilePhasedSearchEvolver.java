package will.game.mario.experiment.evolve;

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
import will.game.mario.agent.encog.EncogAgent;
import will.game.mario.agent.factory.AgentFactory;
import will.game.mario.fitness.EncogMarioFitnessFunction;
import will.game.mario.params.HyperNEATParameters;
import will.game.mario.params.NEATParameters;
import will.game.mario.rf.environment.GameEnvironment;
import will.neat.encog.*;

/**
 * Created by Will on 26/01/2017.
 */
public class SandpilePhasedSearchEvolver extends NEATMarioEvolver {
    public SandpilePhasedSearchEvolver(NEATParameters params, String simOptions, GameEnvironment env,
                                       StringBuilder output, String name, EncogAgent.FitnessFunction ff, int seed) {
        super(params, simOptions, env, output, name, ff, seed);
    }

    @Override
    protected TrainEA setupNEAT(NEATParameters params, String marioOptions, AgentFactory agentFactory) {
        NEATPopulation population = new NEATPopulation(params.NUM_INPUTS, params.NUM_OUTPUTS, params.POP_SIZE);
        population.setActivationCycles(params.ACTIVATION_CYCLES);
        population.setInitialConnectionDensity(params.INIT_CONNECTION_DENSITY);
        population.setWeightRange(params.NN_WEIGHT_RANGE);
        population.setNEATActivationFunction(params.NN_ACTIVATION_FUNCTION);
        population.reset();

        CalculateScore fitnessFunction = new EncogMarioFitnessFunction(marioOptions, true, agentFactory, seed);

        OriginalNEATSpeciation speciation = new OriginalNEATSpeciation();
        speciation.setCompatibilityThreshold(params.INIT_COMPAT_THRESHOLD);
        speciation.setMaxNumberOfSpecies(params.MAX_SPECIES);
        speciation.setNumGensAllowedNoImprovement(params.SPECIES_DROPOFF);

        final TrainEA neat = new TrainEA(population, fitnessFunction);
        neat.setSpeciation(speciation);
        neat.setSelection(new TruncationSelection(neat, params.SELECTION_PROP));
        neat.setEliteRate(params.ELITE_RATE);
        neat.setCODEC(new NEATCODEC());

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

        // phased search (each phase has unique set of mutations)
        BasicPhasedSearch phasedSearch = new BasicPhasedSearch(
                params.PHASE_A_LENGTH, params.PHASE_B_LENGTH);

        // additive mutations
        phasedSearch.addPhaseOp(0, params.ADD_CONN_PROB, new NEATMutateAddLink());
        phasedSearch.addPhaseOp(0, params.ADD_NEURON_PROB, new NEATMutateAddNeuron());

        // subtractive mutations
        phasedSearch.addPhaseOp(1, params.REMOVE_CONN_PROB, new DeleteLinkSandpileMutate(1));
        phasedSearch.addPhaseOp(1, params.REMOVE_NEURON_PROB, new DeleteNeuronSandpileMutate(1));

        neat.addStrategy(phasedSearch);

        neat.getOperators().finalizeStructure();

        neat.setThreadCount(1);

        neat.addStrategy(new EndIterationsStrategy(params.MAX_GENERATIONS));

        return neat;
    }
}
