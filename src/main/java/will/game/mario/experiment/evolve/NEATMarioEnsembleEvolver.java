package will.game.mario.experiment.evolve;

import org.encog.ml.CalculateScore;
import org.encog.ml.ea.genome.GenomeFactory;
import org.encog.ml.ea.opp.selection.TruncationSelection;
import org.encog.ml.ea.train.basic.TrainEA;
import org.encog.ml.train.strategy.end.EndIterationsStrategy;
import org.encog.neural.neat.training.opp.links.SelectFixed;
import org.encog.neural.neat.training.opp.links.SelectProportion;
import will.game.mario.agent.factory.AgentFactory;
import will.game.mario.agent.encog.EnsembleAgent;
import will.game.mario.fitness.EnsembleMarioFF;
import will.game.mario.params.HyperNEATParameters;
import will.game.mario.params.NEATEnsembleParams;
import will.game.mario.params.NEATParameters;
import will.game.mario.rf.action.ActionStratFactory;
import will.neat.encog.MutatePerturbOrResetLinkWeight;
import will.neat.encog.BasicPhasedSearch;
import will.neat.encog.ensemble.EnsembleDiversityFF;
import will.neat.encog.ensemble.codec.EnsembleCODEC;
import will.neat.encog.ensemble.factory.FactorEnsembleGenome;
import will.neat.encog.ensemble.mutation.simple.*;
import will.neat.encog.ensemble.speciation.EnsembleNEATSpeciation;
import will.neat.encog.ensemble.NEATEnsemblePopulation;

/**
 * Created by hardwiwill on 29/11/16.
 */
public class NEATMarioEnsembleEvolver extends NEATMarioEvolver {

    public NEATMarioEnsembleEvolver(NEATParameters params, ActionStratFactory actionStratFactory) {
        super(params, actionStratFactory);
    }

    public NEATMarioEnsembleEvolver(NEATParameters params, ActionStratFactory actionStratFactory, StringBuilder sb, String name) {
        super(params, actionStratFactory, sb, name);
    }

    @Override
    protected TrainEA setupNEAT(NEATParameters params, String marioOptions, AgentFactory agentFactory) {
        int ensembleSize = 4; // default value
        if (params instanceof NEATEnsembleParams) {
            ensembleSize = ((NEATEnsembleParams)params).ENSEMBLE_SIZE;
        }

        GenomeFactory genomeFactory = new FactorEnsembleGenome(ensembleSize);

        NEATEnsemblePopulation population = new NEATEnsemblePopulation(params.NUM_INPUTS, params.NUM_OUTPUTS,
                params.POP_SIZE, ensembleSize, genomeFactory);
        population.setActivationCycles(params.ACTIVATION_CYCLES);
        population.setInitialConnectionDensity(params.INIT_CONNECTION_DENSITY);
        population.setWeightRange(params.NN_WEIGHT_RANGE);
        population.setNEATActivationFunction(params.NN_ACTIVATION_FUNCTION);
        population.reset();

        CalculateScore fitnessFunction = new EnsembleMarioFF(marioOptions, true,
                ensemble -> new EnsembleAgent(ensemble));

        EnsembleDiversityFF genotypeFF = new EnsembleDiversityFF((EnsembleMarioFF) fitnessFunction);

        EnsembleNEATSpeciation speciation = new EnsembleNEATSpeciation();
        speciation.setCompatibilityThreshold(params.INIT_COMPAT_THRESHOLD);
        speciation.setMaxNumberOfSpecies(params.MAX_SPECIES);
        speciation.setNumGensAllowedNoImprovement(params.SPECIES_DROPOFF);

//        final TrainEAGenotypeFF neat = new TrainEAGenotypeFF(population, genotypeFF);
        final TrainEA neat = new TrainEA(population, fitnessFunction);
        neat.setSpeciation(speciation);
        neat.setSelection(new TruncationSelection(neat, params.SELECTION_PROP));
        neat.setEliteRate(params.ELITE_RATE);
        neat.setCODEC(new EnsembleCODEC());

        double perturbProp = params.WEIGHT_PERTURB_PROP;
        double perturbSD = params.PERTURB_SD;
        double resetWeightProb = params.RESET_WEIGHT_PROB;

        // either perturb a proportion of all weights or just one weight
        EnsemblePerturbLink weightMutation = new EnsemblePerturbLink(
                params.WEIGHT_MUT_TYPE == HyperNEATParameters.WeightMutType.PROPORTIONAL
                        ? new SelectProportion(perturbProp)
                        : new SelectFixed(1),
                new MutatePerturbOrResetLinkWeight(resetWeightProb, perturbSD)
        );

        // crossover not implemented yet
//        neat.addOperation(params.CROSSOVER_PROB, new NEATCrossover());
        neat.addOperation(params.PERTURB_PROB, weightMutation);

        // phased search (each phase has unique set of mutations)
        if (params.PHASED_SEARCH) {
            BasicPhasedSearch phasedSearch = new BasicPhasedSearch(
                    params.PHASE_A_LENGTH, params.PHASE_B_LENGTH);
            neat.addStrategy(phasedSearch);

            // additive mutations
            phasedSearch.addPhaseOp(0, params.ADD_CONN_PROB, new EnsembleAddLink());
            phasedSearch.addPhaseOp(0, params.ADD_NEURON_PROB, new EnsembleAddNeuron());

            // subtractive mutations
            phasedSearch.addPhaseOp(1, params.REMOVE_CONN_PROB, new EnsembleRemoveLink());
            phasedSearch.addPhaseOp(1, params.REMOVE_NEURON_PROB, new EnsembleRemoveNeuron());
        } else { // blended search
            neat.addOperation(params.ADD_CONN_PROB, new EnsembleAddLink());
            neat.addOperation(params.ADD_NEURON_PROB, new EnsembleAddNeuron());
            neat.addOperation(params.REMOVE_CONN_PROB, new EnsembleRemoveLink());
            neat.addOperation(params.REMOVE_NEURON_PROB, new EnsembleRemoveNeuron());
        }
        neat.getOperators().finalizeStructure();

        neat.setThreadCount(1);

        neat.addStrategy(new EndIterationsStrategy(params.MAX_GENERATIONS));

        return neat;
    }

}
