package will.neat.encog.ensemble;

import org.encog.ml.ea.genome.Genome;
import org.encog.ml.ea.genome.GenomeFactory;
import org.encog.neural.neat.training.SingleNEATGenome;
import org.encog.neural.neat.training.NEATLinkGene;
import org.encog.neural.neat.training.NEATNeuronGene;

import java.util.List;
import java.util.Random;

/**
 * Created by hardwiwill on 28/11/16.
 */
public class FactorEnsembleGenome implements GenomeFactory {

    private int ensembleSize;

    public FactorEnsembleGenome(int ensembleSize) {
        this.ensembleSize = ensembleSize;
    }

    @Override
    public Genome factor() {
        return new NEATEnsembleGenome(ensembleSize);
    }

    @Override
    public Genome factor(Genome other) {
        return new NEATEnsembleGenome((NEATEnsembleGenome)other);
    }

    public Genome factor(List<NEATNeuronGene> neurons, List<NEATLinkGene> links, int inputCount, int outputCount) {
        return new NEATEnsembleGenome(new SingleNEATGenome(neurons, links, inputCount, outputCount), ensembleSize);
    }

    public Genome factor(Random rnd, NEATEnsemblePopulation pop, int inputCount, int outputCount, double connectionDensity) {
        return new NEATEnsembleGenome(new SingleNEATGenome(rnd, pop, inputCount, outputCount, connectionDensity), ensembleSize);
    }
}
