package will.game.mario.experiment.evolve;

import org.encog.ml.train.strategy.end.EndIterationsStrategy;
import org.encog.neural.neat.NEATCODEC;
import org.encog.neural.neat.NEATPopulation;
import org.encog.neural.neat.training.NEATGenome;
import org.encog.neural.neat.training.opp.*;
import org.encog.neural.neat.training.opp.links.SelectFixed;
import org.encog.neural.neat.training.opp.links.SelectProportion;
import org.encog.neural.neat.training.species.OriginalNEATSpeciation;
import will.game.mario.agent.encog.EnsembleAgent;
import will.game.mario.agent.factory.EnsembleAgentFactory;
import will.game.mario.agent.factory.EnsembleMasterAgent;
import will.game.mario.params.NEATEnsembleParams;
import will.game.mario.params.NEATParameters;
import will.game.mario.rf.action.ActionStratFactory;
import will.neat.encog.MutatePerturbOrResetLinkWeight;
import will.neat.encog.PhasedSearch;
import will.neat.encog.ensemble.EA;
import will.neat.encog.ensemble.MultiPopEA;
import will.neat.encog.ensemble.NEATEnsembleMaster;

import java.util.Arrays;

/**
 * Created by hardwiwill on 5/12/16.
 */
public class MultiPopNEATMarioEvolver {

    private StringBuilder output;
    private String simOptions;
    private NEATParameters params;
    private ActionStratFactory stratFactory;
    private String name;
    private boolean printOutput = true;

    public MultiPopNEATMarioEvolver(NEATParameters params, String simOptions, ActionStratFactory actionStratFactory,
                            String name) {
        this.params = params;
        this.simOptions = simOptions;
        this.stratFactory = actionStratFactory;
        this.name = name;
    }

    public MultiPopNEATMarioEvolver(NEATParameters params, ActionStratFactory actionStratFactory,
                                    StringBuilder output, String name) {
        this.params = params;
        this.stratFactory = actionStratFactory;
        this.name = name;
        this.output = output;
    }

    public MultiPopNEATMarioEvolver(NEATParameters params, ActionStratFactory actionStratFactory) {
        this.params = params;
        this.stratFactory = actionStratFactory;
    }

    public EA run() {
        // initialise neat
        MultiPopEA neat = setupNEAT(params, simOptions,
                setupAgent(stratFactory));

        while (!neat.isDone()) {
            neat.iteration();
            logIteration(neat);
        }

        return neat;
    }

    protected EnsembleAgentFactory setupAgent(ActionStratFactory stratFactory) {
        return (nn) -> new EnsembleAgent(nn, stratFactory);
    }


    protected MultiPopEA setupNEAT(NEATParameters params, String marioOptions, EnsembleAgentFactory agentFactory) {
        int ensembleSize = 4; // default value
        if (params instanceof NEATEnsembleParams) {
            ensembleSize = ((NEATEnsembleParams)params).ENSEMBLE_SIZE;
        }

        NEATPopulation population = new NEATPopulation(params.NUM_INPUTS, params.NUM_OUTPUTS,
                params.POP_SIZE);
        population.setActivationCycles(params.ACTIVATION_CYCLES);
        population.setInitialConnectionDensity(params.INIT_CONNECTION_DENSITY);
        population.setWeightRange(params.NN_WEIGHT_RANGE);
        population.setNEATActivationFunction(params.NN_ACTIVATION_FUNCTION);
//        population.setRandomNumberFactory(new BasicRandomFactory(0));

        OriginalNEATSpeciation speciation = new OriginalNEATSpeciation();
        speciation.setCompatibilityThreshold(params.INIT_COMPAT_THRESHOLD);
        speciation.setMaxNumberOfSpecies(params.MAX_SPECIES);
        speciation.setNumGensAllowedNoImprovement(params.SPECIES_DROPOFF);

        final MultiPopEA neat = new MultiPopEA(ensembleSize, population,
                this.simOptions, ensemble -> new EnsembleMasterAgent((NEATEnsembleMaster)ensemble), true);
        neat.setSpeciation(speciation);
        neat.setSelectionProp(params.SELECTION_PROP);
        neat.setEliteRate(params.ELITE_RATE);
        neat.setCODEC(new NEATCODEC());

        double perturbProp = params.WEIGHT_PERTURB_PROP;
        double perturbSD = params.PERTURB_SD;
        double resetWeightProb = params.RESET_WEIGHT_PROB;

        // either perturb a proportion of all weights or just one weight
        NEATMutateWeights weightMutation = new NEATMutateWeights(
                params.WEIGHT_MUT_TYPE == NEATParameters.WeightMutType.PROPORTIONAL
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
            phasedSearch.addPhaseOp(0, params.ADD_CONN_PROB, new NEATMutateAddLink());
            phasedSearch.addPhaseOp(0, params.ADD_NEURON_PROB, new NEATMutateAddNeuron());

            // subtractive mutations
            phasedSearch.addPhaseOp(1, params.REMOVE_CONN_PROB, new NEATMutateRemoveLink());
            phasedSearch.addPhaseOp(1, params.REMOVE_NEURON_PROB, new NEATMutateRemoveNeuron());
        } else { // blended search
            neat.addOperation(params.ADD_CONN_PROB, new NEATMutateAddLink());
            neat.addOperation(params.ADD_NEURON_PROB, new NEATMutateAddNeuron());
            neat.addOperation(params.REMOVE_CONN_PROB, new NEATMutateRemoveLink());
            neat.addOperation(params.REMOVE_NEURON_PROB, new NEATMutateRemoveNeuron());
        }
        neat.finaliseOps();

        neat.setThreadCount(1);

        neat.addStrategy(new EndIterationsStrategy(params.MAX_GENERATIONS));

        return neat;
    }

    public void setSimOptions(String simOptions) {
        this.simOptions = simOptions;
    }

    private void logIteration(MultiPopEA neat) {

        NEATPopulation masterPop = (NEATPopulation) neat.getMasterNEAT().getPopulation();
        NEATPopulation[] modulePops = Arrays.stream(neat.getModuleNEATs())
                .map(n -> (NEATPopulation) n.getPopulation())
                .toArray(NEATPopulation[]::new);

        StringBuilder sb = new StringBuilder();

        int masterBestFitness = (int)masterPop.getBestGenome().getScore();
        double masterAveLinks = getAverageLinks(masterPop);
        double masterAveNeurons = getAverageNeurons(masterPop);
        double masterBestLinks = ((NEATGenome)masterPop.getBestGenome())
                .getLinksChromosome().size();
        double masterBestNeurons = ((NEATGenome)masterPop.getBestGenome())
                .getNeuronsChromosome().size();

        sb.append(String.format("%s,%d, { %d,%.0f,%.0f,%.0f,%.0f,%d }, ",
                name, neat.getIteration(), masterBestFitness, masterAveLinks, masterBestLinks,
                masterAveNeurons, masterBestNeurons, masterPop.getSpecies().size()));

        for (NEATPopulation modulePop: modulePops) {
            int moduleBestFitness = (int)modulePop.getBestGenome().getScore();
            double moduleAveLinks = getAverageLinks(modulePop);
            double moduleAveNeurons = getAverageNeurons(modulePop);
            double moduleBestLinks = ((NEATGenome)modulePop.getBestGenome())
                    .getLinksChromosome().size();
            double moduleBestNeurons = ((NEATGenome)modulePop.getBestGenome())
                    .getNeuronsChromosome().size();

            sb.append(String.format("[ %d,%.0f,%.0f,%.0f,%.0f,%d ], ",
                    moduleBestFitness, moduleAveLinks, moduleBestLinks, moduleAveNeurons,
                    moduleBestNeurons, modulePop.getSpecies().size()));
        }

        sb.append("\n");

        if (printOutput) {
            System.out.print(sb.toString());
        }

        if (this.output != null) {
            this.output.append(sb.toString());
        }
    }

    private double getAverageLinks(NEATPopulation pop) {
        return pop.getSpecies().stream()
                .map(s -> s.getMembers())
                .flatMap(genomes -> genomes.stream())
                .mapToInt(genome -> ((NEATGenome)genome).getLinksChromosome().size())
                .average()
                .getAsDouble();
    }

    private double getAverageNeurons(NEATPopulation pop) {
        return pop.getSpecies().stream()
                .map(s -> s.getMembers())
                .flatMap(genomes -> genomes.stream())
                .mapToInt(genome -> ((NEATGenome)genome).getNeuronsChromosome().size())
                .average()
                .getAsDouble();
    }


    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }
}
