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
import will.game.mario.fitness.AbstractMarioFitnessFunction;
import will.game.mario.fitness.EncogMarioFitnessFunction;
import will.game.mario.params.HyperNEATParameters;
import will.game.mario.params.NEATParameters;
import will.game.mario.rf.environment.EnvEnemyGrid;
import will.game.mario.rf.environment.GameEnvironment;
import will.neat.encog.BasicPhasedSearch;
import will.neat.encog.GreenPhasedSearch;
import will.neat.encog.MutatePerturbOrResetLinkWeight;

/**
 * Created by Will on 26/01/2017.
 */
public class GreenPhasedSearchEvolver extends NEATMarioEvolver {
    public GreenPhasedSearchEvolver(NEATParameters params, GameEnvironment env, StringBuilder output, String name, EncogAgent.FitnessFunction ff) {
        super(params, env, output, name);
        this.ff = ff;
    }

    @Override
    protected TrainEA setupNEAT(NEATParameters params, String marioOptions, AgentFactory agentFactory) {
        NEATPopulation population = new NEATPopulation(params.NUM_INPUTS, params.NUM_OUTPUTS, params.POP_SIZE);
        population.setActivationCycles(params.ACTIVATION_CYCLES);
        population.setInitialConnectionDensity(params.INIT_CONNECTION_DENSITY);
        population.setWeightRange(params.NN_WEIGHT_RANGE);
        population.setNEATActivationFunction(params.NN_ACTIVATION_FUNCTION);
        population.reset();

        CalculateScore fitnessFunction = new EncogMarioFitnessFunction(marioOptions, true, agentFactory);

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
        GreenPhasedSearch phasedSearch = new GreenPhasedSearch(
                params.PHASE_A_LENGTH, params.PHASE_B_LENGTH, 70);
        neat.addStrategy(phasedSearch);

        // additive mutations
        phasedSearch.addPhaseOp(0, params.ADD_CONN_PROB, new NEATMutateAddLink());
        phasedSearch.addPhaseOp(0, params.ADD_NEURON_PROB, new NEATMutateAddNeuron());

        // subtractive mutations
        phasedSearch.addPhaseOp(1, params.REMOVE_CONN_PROB, new NEATMutateRemoveLink());
        phasedSearch.addPhaseOp(1, params.REMOVE_NEURON_PROB, new NEATMutateRemoveNeuron());
        phasedSearch.finalizeOps();

        neat.getOperators().finalizeStructure();

        neat.setThreadCount(1);

        neat.addStrategy(new EndIterationsStrategy(params.MAX_GENERATIONS));

        return neat;
    }

    public static void main(String[] args) {

        NEATParameters neatPhasedParams = new NEATParameters();
        neatPhasedParams.MAX_GENERATIONS = 500;
        neatPhasedParams.PHASED_SEARCH = true;
        neatPhasedParams.PHASE_A_LENGTH=5;
        neatPhasedParams.PHASE_B_LENGTH=5;

        StringBuilder sb = new StringBuilder();
        GreenPhasedSearchEvolver g = new GreenPhasedSearchEvolver(neatPhasedParams, new EnvEnemyGrid(), sb, "test", null);
        g.setSimOptions(AbstractMarioFitnessFunction.DEFAULT_SIM_OPTIONS);
        g.run();
    }
}
