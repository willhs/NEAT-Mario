package will.neat.encog.ensemble;

import org.encog.ml.CalculateScore;
import org.encog.ml.ea.codec.GeneticCODEC;
import org.encog.ml.ea.opp.EvolutionaryOperator;
import org.encog.ml.ea.opp.selection.SelectionOperator;
import org.encog.ml.ea.opp.selection.TruncationSelection;
import org.encog.ml.ea.population.Population;
import org.encog.ml.ea.species.Speciation;
import org.encog.ml.ea.train.basic.TrainEA;
import org.encog.ml.train.strategy.Strategy;
import org.encog.neural.neat.NEATPopulation;
import org.encog.neural.neat.training.species.OriginalNEATSpeciation;
import will.game.mario.agent.encog.EnsembleAgent;
import will.game.mario.agent.factory.AgentFactory;
import will.game.mario.agent.factory.EnsembleAgentFactory;
import will.game.mario.agent.factory.EnsembleMasterAgentFactory;
import will.game.mario.fitness.EnsembleMarioFF;
import will.game.mario.fitness.MultiPopMasterFF;
import will.game.mario.fitness.MultiPopModuleFF;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.IntStream;
import java.util.stream.Stream;

/**
 * An EA with multiple populations that get
 * Created by hardwiwill on 2/12/16.
 */
public class MultiPopEA implements EA {

    private List<TrainEA> moduleNEATs;
    private TrainEA masterNEAT;

    public MultiPopEA(int modules, NEATPopulation startingPop, String simOptions, EnsembleAgentFactory agentFactory, boolean headless) {
        // set up internal NEATs
        this.moduleNEATs = IntStream.range(0, modules)
                .mapToObj(i -> {
                    NEATPopulation pop = cloneNEATPopulation(startingPop, startingPop.getOutputCount());
                    return new TrainEA(pop,
                            new MultiPopModuleFF(simOptions, headless, agentFactory, i));
                })
                .collect(Collectors.toList());

        NEATPopulation masterPop = cloneNEATPopulation(startingPop, modules);

        this.masterNEAT = new TrainEA(masterPop,
                new MultiPopMasterFF(simOptions, headless, agentFactory));

        // attach populations to each fitness function
        NEATPopulation[] modulePops = moduleNEATs.stream()
                .map(module -> (NEATPopulation)module.getPopulation()).toArray(NEATPopulation[]::new);

        moduleNEATs.forEach(n -> ((MultiPopModuleFF)n.getScoreFunction()).setModuleAndMasterPops(
                modulePops,
                (NEATPopulation)masterNEAT.getPopulation()
        ));

        ((MultiPopMasterFF)masterNEAT.getScoreFunction()).setModulePops(modulePops);
    }

    private NEATPopulation cloneNEATPopulation(NEATPopulation pop, int outputs) {
        NEATPopulation clone = new NEATPopulation(pop.getInputCount(), outputs, pop.getPopulationSize());
        clone.setWeightRange(pop.getWeightRange());
        clone.setActivationCycles(pop.getActivationCycles());
        clone.setInitialConnectionDensity(pop.getInitialConnectionDensity());
        clone.setNEATActivationFunction(pop.getActivationFunctions().pickFirst());
        clone.reset();
        return clone;
    }

    public void iteration() {
        allNEATs().forEach(n ->  n.iteration());
    }

    @Override
    public boolean isDone() {
        return allNEATs().stream()
                .anyMatch(n -> n.isTrainingDone());
    }

    public void addStrategy(Strategy strategy) {
        allNEATs().forEach(n -> n.addStrategy(strategy));
    }

    private List<TrainEA> allNEATs() {
        return Stream.concat(moduleNEATs.stream(), Stream.of(masterNEAT)).collect(Collectors.toList());
    }

    public void setSpeciation(OriginalNEATSpeciation orig) {
        allNEATs().forEach(n -> {
            // clone so that NEATs
            OriginalNEATSpeciation speciation = new OriginalNEATSpeciation();
            speciation.setConstExcess(orig.getConstExcess());
            speciation.setConstDisjoint(orig.getConstDisjoint());
            speciation.setConstMatched(orig.getConstMatched());
            speciation.setCompatibilityThreshold(orig.getCompatibilityThreshold());
            speciation.setMaxNumberOfSpecies(orig.getMaxNumberOfSpecies());
            speciation.setNumGensAllowedNoImprovement(orig.getNumGensAllowedNoImprovement());

            n.setSpeciation(speciation);
        });
    }

    public void setEliteRate(double eliteRate) {
        allNEATs().forEach(n -> n.setEliteRate(eliteRate));
    }

    public void setCODEC(GeneticCODEC codec) {
        allNEATs().forEach(n -> n.setCODEC(codec));
    }

    public void addOperation(double probability, EvolutionaryOperator op) {
        allNEATs().forEach(n -> n.addOperation(probability, op));
    }

    public void finaliseOps() {
        allNEATs().forEach(n -> n.getOperators().finalizeStructure());
    }

    public void setThreadCount(int threads) {
        allNEATs().forEach(n -> n.setThreadCount(threads));
    }

    public void setSelectionProp(double selectionProp) {
        allNEATs().forEach(n -> n.setSelection(new TruncationSelection(n, selectionProp)));
    }

    public TrainEA getMasterNEAT() {
        return masterNEAT;
    }

    public TrainEA[] getModuleNEATs() {
        return moduleNEATs.toArray(new TrainEA[moduleNEATs.size()]);
    }

    public int getIteration() {
        return masterNEAT.getIteration();
    }
}
