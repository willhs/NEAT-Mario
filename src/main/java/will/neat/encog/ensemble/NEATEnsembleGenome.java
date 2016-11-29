package will.neat.encog.ensemble;

import org.encog.ml.ea.genome.BasicGenome;
import org.encog.ml.ea.genome.Genome;
import org.encog.ml.ea.population.Population;
import org.encog.neural.neat.training.NEATGenome;
import org.encog.neural.neat.training.SingleNEATGenome;
import org.encog.neural.neat.training.NEATLinkGene;
import org.encog.neural.neat.training.NEATNeuronGene;
import org.encog.util.Format;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.IntStream;
import java.util.stream.Stream;

/**
 * Created by hardwiwill on 28/11/16.
 */
public class NEATEnsembleGenome extends NEATGenome {

    /**
     * The genomes for the ANNs that this ensemble is comprised of
     */
    private List<SingleNEATGenome> anns;

    public NEATEnsembleGenome(SingleNEATGenome template, int ensembleSize) {
        this.anns = new ArrayList<>();
        for (int e = 0; e < ensembleSize; e++) {
            anns.add(new SingleNEATGenome(template));
        }

        this.inputCount = template.getInputCount();
        this.outputCount = template.getOutputCount();
    }

    public NEATEnsembleGenome(NEATEnsembleGenome other) {
        this.anns = other.anns.stream()
                .map(ann -> new SingleNEATGenome(ann))
                .collect(Collectors.toList());

        this.networkDepth = other.networkDepth;
        this.setPopulation(other.getPopulation());
        setScore(other.getScore());
        setAdjustedScore(other.getAdjustedScore());
        this.inputCount = other.inputCount;
        this.outputCount = other.outputCount;
        this.setSpecies(other.getSpecies());
    }

    /**
     * for persistance, like SingleNEATGenome()
     * @param ensembleSize
     */
    public NEATEnsembleGenome(int ensembleSize) {
        this.anns = IntStream.range(0, ensembleSize)
                .mapToObj(ann -> new SingleNEATGenome())
                .collect(Collectors.toList());
    }

    /**
     * @return The number of input neurons.
     */
    public int getInputCount() {
        return this.inputCount;
    }

    /**
     * @return The network depth.
     */
    public int getNetworkDepth() {
        return anns.stream()
                .mapToInt(ann -> ann.getNetworkDepth())
                .max().getAsInt();
    }

    /**
     * @return The number of genes in the links chromosome.
     */
    public int getNumGenes() {
        return anns.stream()
                .mapToInt(ann -> ann.getNumGenes())
                .reduce(0, (a,b) -> a + b);
    }

    /**
     * @return The output count.
     */
    public int getOutputCount() {
        return this.outputCount;
    }

    /**
     * @param networkDepth
     *            the networkDepth to set
     */
    public void setNetworkDepth(final int networkDepth) {
        anns.stream().forEach(ann -> ann.setNetworkDepth(networkDepth));
    }

    /**
     * Sort the genes.
     */
    public void sortGenes() {
        anns.stream().forEach(ann -> ann.sortGenes());
    }

    @Override
    public List<NEATLinkGene> getLinksChromosome() {
        return anns.stream()
                .flatMap(ann -> ann.getLinksChromosome().stream())
                .collect(Collectors.toList());
    }

    @Override
    public List<NEATNeuronGene> getNeuronsChromosome() {
        return anns.stream()
                .flatMap(ann -> ann.getNeuronsChromosome().stream())
                .collect(Collectors.toList());
    }

    /**
     * @return the linksChromosome
     */
    public List<NEATLinkGene> getLinksChromosome(int i) {
        return anns.get(i).getLinksChromosome();
    }

    /**
     * @return the neuronsChromosome
     */
    public List<NEATNeuronGene> getNeuronsChromosome(int i) {
        return anns.get(i).getNeuronsChromosome();
    }

    /**
     * @param inputCount
     *            the inputCount to set
     */
    public void setInputCount(int inputCount) {
        this.inputCount = inputCount;
        anns.stream().forEach(ann -> ann.setInputCount(inputCount));
    }

    /**
     * @param outputCount
     *            the outputCount to set
     */
    public void setOutputCount(int outputCount) {
        this.outputCount = outputCount;
        anns.stream().forEach(ann -> ann.setOutputCount(inputCount));
    }

    /**
     * Validate the structure of this genome.
     */
    public void validate() {
        anns.forEach(ann -> ann.validate());
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void copy(Genome source) {
        throw new UnsupportedOperationException();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public int size() {
        return anns.stream()
                .mapToInt(ann -> ann.size())
                .sum();
    }

    public SingleNEATGenome[] getAnns() {
        return anns.toArray(new SingleNEATGenome[anns.size()]);
    }

    /**
     * Find the neuron with the specified nodeID.
     *
     * @param nodeID
     *            The nodeID to look for.
     * @return The neuron, if found, otherwise null.
     */
    public NEATNeuronGene findNeuron(long nodeID) {
        for (SingleNEATGenome ann : anns) {
            NEATNeuronGene neuron = ann.findNeuron(nodeID);
            if (neuron != null)
                return neuron;
        }
        return null;
    }

    @Override
    public String toString() {
        StringBuilder result = new StringBuilder();
        result.append("[");
        result.append(this.getClass().getSimpleName());
        result.append(",score=");
        result.append(Format.formatDouble(this.getScore(), 2));
        result.append(",adjusted score=");
        result.append(Format.formatDouble(this.getAdjustedScore(), 2));
        result.append(",birth generation=");
        result.append(this.getBirthGeneration());
        result.append(",num anns in ensemble=" + anns.size());
        result.append(",neurons=");
        result.append(this.anns.stream().mapToInt(ann -> ann.getNeuronsChromosome().size()).sum());
        result.append(",links=");
        result.append(this.size());
        result.append("]");
        return result.toString();
    }

    public int getNumAnns() {
        return anns.size();
    }

    @Override
    public void setPopulation(Population p) {
        super.setPopulation(p);
        anns.forEach(ann -> ann.setPopulation(p));
    }
}
