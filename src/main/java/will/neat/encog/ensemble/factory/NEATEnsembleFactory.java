package will.neat.encog.ensemble.factory;

import org.encog.ml.ea.genome.Genome;
import org.encog.ml.ea.genome.GenomeFactory;
import org.encog.neural.neat.training.NEATLinkGene;
import org.encog.neural.neat.training.NEATNeuronGene;
import will.neat.encog.ensemble.NEATEnsemblePopulation;

import java.util.List;
import java.util.Random;

/**
 * Created by hardwiwill on 28/11/16.
 */
public interface NEATEnsembleFactory extends GenomeFactory {
    /**
     * Create a NEAT genome from a list of links and neurons.
     *
     * @param neurons
     *            A list of neuron genes.
     * @param links
     *            A list of link genes.
     * @param inputCount
     *            The input count.
     * @param outputCount
     *            The output count.
     * @return The newly factored SingleNEATGenome.
     */
    Genome factor(List<NEATNeuronGene> neurons, List<NEATLinkGene> links,
                  int inputCount, int outputCount);

    /**
     * Create a new random NEAT genome.
     *
     * @param rnd
     *            A random number generator.
     * @param pop
     *            The NEAT population.
     * @param inputCount
     *            The input count.
     * @param outputCount
     *            The output count.
     * @param connectionDensity
     *            The connection density. Specify 1.0 for fully connected.
     * @return The newly created NEAT genome.
     */
    Genome factor(Random rnd, NEATEnsemblePopulation pop, int inputCount,
                  int outputCount, double connectionDensity);
}
