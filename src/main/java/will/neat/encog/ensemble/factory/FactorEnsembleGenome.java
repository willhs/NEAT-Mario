package will.neat.encog.ensemble.factory;

import org.encog.ml.ea.genome.Genome;
import org.encog.ml.ea.genome.GenomeFactory;
import org.encog.neural.neat.training.AbstractNEATPopulation;
import org.encog.neural.neat.training.SingleNEATGenome;
import will.neat.encog.ensemble.genome.NEATEnsembleGenome;

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

    public Genome factor(Random rnd, AbstractNEATPopulation pop, int inputCount, int outputCount, double connectionDensity) {
        return new NEATEnsembleGenome(new SingleNEATGenome(rnd, pop, inputCount, outputCount, connectionDensity), ensembleSize);
    }

    public int getEnsembleSize() {
        return ensembleSize;
    }
}
