package will.neat.encog.ensemble.factory;

import org.encog.ml.ea.genome.Genome;
import org.encog.neural.neat.training.AbstractNEATPopulation;
import org.encog.neural.neat.training.SingleNEATGenome;
import will.neat.encog.ensemble.genome.EnsembleMasterGenome;
import will.neat.encog.ensemble.NEATEnsemblePopulation;

import java.util.Random;

/**
 * Created by hardwiwill on 1/12/16.
 */
public class FactorEnsembleMaster extends FactorEnsembleGenome {

    public FactorEnsembleMaster(int ensembleSize) {
        super(ensembleSize);
    }

    @Override
    public Genome factor() {
        return new EnsembleMasterGenome(getEnsembleSize());
    }

    @Override
    public Genome factor(Genome other) {
        return new EnsembleMasterGenome((EnsembleMasterGenome)other);
    }

    @Override
    public Genome factor(Random rnd, AbstractNEATPopulation pop, int inputCount, int outputCount, double connectionDensity) {
        return new EnsembleMasterGenome(new SingleNEATGenome(rnd, pop, inputCount, outputCount, connectionDensity), getEnsembleSize(), pop);
    }
}
