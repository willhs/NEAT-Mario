package will.neat.encog.ensemble.genome;

import org.encog.ml.ea.population.Population;
import org.encog.neural.neat.training.AbstractNEATPopulation;
import org.encog.neural.neat.training.NEATLinkGene;
import org.encog.neural.neat.training.NEATNeuronGene;
import org.encog.neural.neat.training.SingleNEATGenome;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Random;
import java.util.stream.Stream;

/**
 * Created by hardwiwill on 1/12/16.
 */
public class EnsembleMasterGenome extends NEATEnsembleGenome {

    private SingleNEATGenome master;

    public EnsembleMasterGenome(SingleNEATGenome template, int ensembleSize, AbstractNEATPopulation pop) {
        super(template, ensembleSize);
        master = new SingleNEATGenome(new Random(), pop, inputCount, ensembleSize, pop.getInitialConnectionDensity());
    }

    public EnsembleMasterGenome(int ensembleSize) {
        super(ensembleSize);
    }

    public EnsembleMasterGenome(EnsembleMasterGenome other) {
        super(other);
        this.master = new SingleNEATGenome(other.master);
    }

    public SingleNEATGenome getMaster() {
        return master;
    }

    @Override
    public int getNumGenes() {
        return super.getNumGenes() + master.getNumGenes();
    }

    @Override
    public void sortGenes() {
        super.sortGenes();
        master.sortGenes();
    }

    @Override
    public List<NEATLinkGene> getLinksChromosome() {
        List<NEATLinkGene> links = new ArrayList(super.getLinksChromosome());
        links.addAll(master.getLinksChromosome());
        return links;
    }

    @Override
    public List<NEATNeuronGene> getNeuronsChromosome() {
        List<NEATNeuronGene> neurons = new ArrayList(super.getLinksChromosome());
        neurons.addAll(master.getNeuronsChromosome());
        return neurons;
    }

    public List<NEATLinkGene> getMasterLinksChromosome() {
        return master.getLinksChromosome();
    }

    public List<NEATNeuronGene> getMasterNeuronsChromosome() {
        return master.getNeuronsChromosome();
    }

    @Override
    public void validate() {
        super.validate();
        master.validate();
    }

    @Override
    public int size() {
        return super.size() + master.size();
    }

    @Override
    public NEATNeuronGene findNeuron(long neuronID) {
        NEATNeuronGene found = super.findNeuron(neuronID);
        if (found == null) {
            return master.findNeuron(neuronID);
        } else return null;
    }

    @Override
    public void setPopulation(Population p) {
        super.setPopulation(p);
        if (master != null) {
            master.setPopulation(p);
        }
    }

    @Override
    public SingleNEATGenome[] getComponents() {
        return Stream.concat(
                Arrays.stream(super.getComponents()),
                Stream.of(master)
        ).toArray(SingleNEATGenome[]::new);
    }
}
