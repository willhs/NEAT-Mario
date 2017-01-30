package will.game.mario.experiment.evolve;

import org.encog.ml.CalculateScore;
import org.encog.ml.ea.opp.selection.TruncationSelection;
import org.encog.ml.ea.train.basic.TrainEA;
import org.encog.ml.train.strategy.end.EndIterationsStrategy;
import org.encog.neural.hyperneat.substrate.Substrate;
import org.encog.neural.neat.NEATCODEC;
import org.encog.neural.neat.NEATPopulation;
import org.encog.neural.neat.training.AbstractNEATPopulation;
import org.encog.neural.neat.training.NEATGenome;
import org.encog.neural.neat.training.opp.*;
import org.encog.neural.neat.training.opp.links.SelectFixed;
import org.encog.neural.neat.training.opp.links.SelectProportion;
import org.encog.neural.neat.training.species.OriginalNEATSpeciation;
import will.game.mario.agent.factory.AgentFactory;
import will.game.mario.agent.encog.EncogAgent;
import will.game.mario.fitness.EncogMarioFitnessFunction;
import will.game.mario.params.PhasedParameters;
import will.game.mario.rf.action.StandardHoldStrat;
import will.game.mario.rf.environment.EnvEnemyGrid;
import will.game.mario.rf.environment.GameEnvironment;
import will.neat.encog.MutatePerturbOrResetLinkWeight;
import will.neat.encog.BasicPhasedSearch;
import will.neat.encog.substrate.MultiHiddenLayerSubstrate;
import will.game.mario.params.HyperNEATParameters;
import will.game.mario.params.NEATParameters;
import will.game.mario.rf.action.ActionStratFactory;

/**
 * Created by Will on 8/10/2016.
 */
public class NEATMarioEvolver {

    protected EncogAgent.FitnessFunction ff;
    protected String simOptions;
    protected NEATParameters params;
    protected ActionStratFactory stratFactory = () -> new StandardHoldStrat();
    protected String name = "NEAT";
    protected GameEnvironment env = new EnvEnemyGrid();

    protected StringBuilder output;
    private boolean printOutput = true;

    public NEATMarioEvolver(NEATParameters params, GameEnvironment env,
                            StringBuilder output, String name) {
        this.params = params;
        this.name = name;
        this.output = output;
        this.env = env;
    }

    public NEATMarioEvolver(NEATParameters params, ActionStratFactory actionStratFactory,
                            StringBuilder output, String name) {
        this(params, actionStratFactory);
        this.name = name;
        this.output = output;
    }

    public NEATMarioEvolver(NEATParameters params, ActionStratFactory actionStratFactory,
                            StringBuilder output, String name, EncogAgent.FitnessFunction ff) {
        this(params, actionStratFactory);
        this.name = name;
        this.output = output;
        this.ff = ff;
    }

    public NEATMarioEvolver(NEATParameters params, ActionStratFactory actionStratFactory,
                            StringBuilder output, String name, EncogAgent.FitnessFunction ff, GameEnvironment env) {
        this(params, actionStratFactory, output, name, ff);
        this.env = env;
    }

    public NEATMarioEvolver(NEATParameters params, ActionStratFactory actionStratFactory) {
        this.params = params;
        this.stratFactory = actionStratFactory;
        this.printOutput = true;
    }

    public NEATMarioEvolver(NEATParameters params, String simOptions, GameEnvironment env, StringBuilder output, String name, EncogAgent.FitnessFunction ff, int seed) {
        this.params = params;
        this.simOptions = simOptions;
        this.env = env;
        this.output = output;
        this.name = name;
        this.ff = ff;
    }

    public TrainEA run() {
        // initialise neat
        TrainEA neat = setupNEAT(params, simOptions,
                setupAgent(stratFactory));

        return run(neat);
    }

    public TrainEA run(TrainEA neat) {
        while (!neat.isTrainingDone()) {
            neat.iteration();
            logIteration(neat, output);
        }

        return neat;
    }

    protected AgentFactory setupAgent(ActionStratFactory stratFactory) {
        return ff == null ? (nn) -> new EncogAgent(nn, stratFactory, env)
                : (nn) -> new EncogAgent(nn, stratFactory, env, ff);
    }

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
        if (params.PHASED_SEARCH) {
            BasicPhasedSearch phasedSearch = new BasicPhasedSearch(
                    params.PHASE_A_LENGTH, params.PHASE_B_LENGTH);

            if (params instanceof PhasedParameters) {
                phasedSearch.setPhase(((PhasedParameters)params).STARTING_PHASE);
            }

            // additive mutations
            phasedSearch.addPhaseOp(0, params.ADD_CONN_PROB, new NEATMutateAddLink());
            phasedSearch.addPhaseOp(0, params.ADD_NEURON_PROB, new NEATMutateAddNeuron());

            // subtractive mutations
            phasedSearch.addPhaseOp(1, params.REMOVE_CONN_PROB, new NEATMutateRemoveLink());
            phasedSearch.addPhaseOp(1, params.REMOVE_NEURON_PROB, new NEATMutateRemoveNeuron());

            neat.addStrategy(phasedSearch);

        } else { // blended search
            neat.addOperation(params.ADD_CONN_PROB, new NEATMutateAddLink());
            neat.addOperation(params.ADD_NEURON_PROB, new NEATMutateAddNeuron());
            neat.addOperation(params.REMOVE_CONN_PROB, new NEATMutateRemoveLink());
            neat.addOperation(params.REMOVE_NEURON_PROB, new NEATMutateRemoveNeuron());
        }
        neat.getOperators().finalizeStructure();

        neat.setThreadCount(1);

        neat.addStrategy(new EndIterationsStrategy(params.MAX_GENERATIONS));

        return neat;
    }


    protected Substrate setupSubstrate() {
        return new MultiHiddenLayerSubstrate().makeSubstrate();
    }

    private void logIteration(TrainEA neat, StringBuilder output) {
        AbstractNEATPopulation population = (AbstractNEATPopulation) neat.getPopulation();
        double bestFitness = population.getBestGenome().getScore();

        int numSpecies = population.getSpecies().size();

        double averageLinks = population.getSpecies().stream()
                .map(s -> s.getMembers())
                .flatMap(genomes -> genomes.stream())
                .mapToInt(genome -> ((NEATGenome)genome).getLinksChromosome().size())
                .average()
                .getAsDouble();

        double bestLinks = ((NEATGenome)population.getBestGenome())
                .getLinksChromosome().size();

        double bestNeurons = ((NEATGenome)population.getBestGenome())
                .getNeuronsChromosome().size();

        double averageNeurons = population.getSpecies().stream()
                .map(s -> s.getMembers())
                .flatMap(genomes -> genomes.stream())
                .mapToInt(genome -> ((NEATGenome)genome).getNeuronsChromosome().size())
                .average()
                .getAsDouble();

        StringBuilder sb = new StringBuilder();
        sb.append(String.format("%s,%d,%.0f,%.0f,%.0f,%.0f,%.0f,%d%n",
                name, neat.getIteration(), bestFitness, averageLinks, bestLinks, averageNeurons, bestNeurons, numSpecies));

        if (output != null) {
            output.append(sb.toString());
        }
        if (printOutput) {
            System.out.print(sb.toString());
        }
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getName() {
        return name;
    }

    public void setSimOptions(String simOptions) {
        this.simOptions = simOptions;
    }

    public TrainEA getNEAT() {
        return setupNEAT(params, simOptions, setupAgent(stratFactory));
    }
}
