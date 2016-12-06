package will.game.mario.experiment;

import org.encog.mathutil.randomize.factory.BasicRandomFactory;
import org.encog.ml.CalculateScore;
import org.encog.ml.ea.genome.GenomeFactory;
import org.encog.ml.ea.opp.selection.TruncationSelection;
import org.encog.ml.ea.train.basic.TrainEA;
import org.encog.ml.train.strategy.end.EndIterationsStrategy;
import org.encog.neural.neat.training.opp.links.SelectFixed;
import org.encog.neural.neat.training.opp.links.SelectProportion;
import will.game.mario.agent.factory.AgentFactory;
import will.game.mario.agent.factory.EnsembleMasterAgent;
import will.game.mario.fitness.EnsembleMarioFF;
import will.game.mario.params.HyperNEATParameters;
import will.game.mario.params.NEATEnsembleParams;
import will.game.mario.params.NEATParameters;
import will.game.mario.rf.action.ActionStratFactory;
import will.neat.encog.MutatePerturbOrResetLinkWeight;
import will.neat.encog.PhasedSearch;
import will.neat.encog.ensemble.NEATEnsembleMaster;
import will.neat.encog.ensemble.codec.EnsembleCODEC;
import will.neat.encog.ensemble.codec.EnsembleMasterCODEC;
import will.neat.encog.ensemble.factory.FactorEnsembleMaster;
import will.neat.encog.ensemble.mutation.master.*;
import will.neat.encog.ensemble.mutation.simple.*;
import will.neat.encog.ensemble.speciation.EnsembleMasterSpeciation;
import will.neat.encog.ensemble.speciation.EnsembleNEATSpeciation;
import will.neat.encog.ensemble.NEATEnsemblePopulation;

/**
 * Created by hardwiwill on 1/12/16.
 */
public class EnsembleMasterMarioEvolver extends NEATMarioEnsembleEvolver {
    public EnsembleMasterMarioEvolver(NEATParameters params, ActionStratFactory actionStratFactory) {
        super(params, actionStratFactory);
    }

    public EnsembleMasterMarioEvolver(NEATEnsembleParams ensembleParams, ActionStratFactory actionStratFactory, StringBuilder sb, String s) {
        super(ensembleParams, actionStratFactory, sb, s);
    }

    @Override
    protected TrainEA setupNEAT(NEATParameters params, String marioOptions, AgentFactory agentFactory) {
        int ensembleSize = 4; // default value
        if (params instanceof NEATEnsembleParams) {
            ensembleSize = ((NEATEnsembleParams)params).ENSEMBLE_SIZE;
        }

        GenomeFactory genomeFactory = new FactorEnsembleMaster(ensembleSize);

        NEATEnsemblePopulation population = new NEATEnsemblePopulation(params.NUM_INPUTS, params.NUM_OUTPUTS,
                params.POP_SIZE, ensembleSize, genomeFactory);
        population.setActivationCycles(params.ACTIVATION_CYCLES);
        population.setInitialConnectionDensity(params.INIT_CONNECTION_DENSITY);
        population.setWeightRange(params.NN_WEIGHT_RANGE);
        population.setNEATActivationFunction(params.NN_ACTIVATION_FUNCTION);
//        population.setRandomNumberFactory(new BasicRandomFactory(0));
        population.reset();

        CalculateScore fitnessFunction = new EnsembleMarioFF(marioOptions, true,
                ensemble -> new EnsembleMasterAgent((NEATEnsembleMaster) ensemble));

        EnsembleNEATSpeciation speciation = new EnsembleMasterSpeciation();
        speciation.setCompatibilityThreshold(params.INIT_COMPAT_THRESHOLD);
        speciation.setMaxNumberOfSpecies(params.MAX_SPECIES);
        speciation.setNumGensAllowedNoImprovement(params.SPECIES_DROPOFF);

        final TrainEA neat = new TrainEA(population, fitnessFunction);
        neat.setSpeciation(speciation);
        neat.setSelection(new TruncationSelection(neat, params.SELECTION_PROP));
        neat.setEliteRate(params.ELITE_RATE);
        neat.setCODEC(new EnsembleMasterCODEC());

        double perturbProp = params.WEIGHT_PERTURB_PROP;
        double perturbSD = params.PERTURB_SD;
        double resetWeightProb = params.RESET_WEIGHT_PROB;

        // either perturb a proportion of all weights or just one weight
        EnsemblePerturbLink weightMutation = new EnsembleMPerturbLink(
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
            PhasedSearch phasedSearch = new PhasedSearch(
                    params.PHASE_A_LENGTH, params.PHASE_B_LENGTH);
            neat.addStrategy(phasedSearch);

            // additive mutations
            phasedSearch.addPhaseOp(0, params.ADD_CONN_PROB, new EnsembleMAddLink());
            phasedSearch.addPhaseOp(0, params.ADD_NEURON_PROB, new EnsembleMAddNeuron());

            // subtractive mutations
            phasedSearch.addPhaseOp(1, params.REMOVE_CONN_PROB, new EnsembleMRemoveLink());
            phasedSearch.addPhaseOp(1, params.REMOVE_NEURON_PROB, new EnsembleMRemoveNeuron());
        } else { // blended search
            neat.addOperation(params.ADD_CONN_PROB, new EnsembleMAddLink());
            neat.addOperation(params.ADD_NEURON_PROB, new EnsembleMAddNeuron());
            neat.addOperation(params.REMOVE_CONN_PROB, new EnsembleMRemoveLink());
            neat.addOperation(params.REMOVE_NEURON_PROB, new EnsembleMRemoveNeuron());
        }
        neat.getOperators().finalizeStructure();

        neat.setThreadCount(1);

        neat.addStrategy(new EndIterationsStrategy(params.MAX_GENERATIONS));

        return neat;
    }
}
