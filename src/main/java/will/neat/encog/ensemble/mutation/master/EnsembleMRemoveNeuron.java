package will.neat.encog.ensemble.mutation.master;

import org.encog.ml.ea.genome.Genome;
import org.encog.neural.neat.training.SingleNEATGenome;
import will.neat.encog.ensemble.genome.EnsembleMasterGenome;
import will.neat.encog.ensemble.genome.NEATEnsembleGenome;
import will.neat.encog.ensemble.mutation.simple.EnsembleRemoveNeuron;

import java.util.Random;

/**
 * Created by hardwiwill on 2/12/16.
 */
public class EnsembleMRemoveNeuron extends EnsembleRemoveNeuron {

    /**
     * {@inheritDoc}
     */
    @Override
    public void performOperation(final Random rnd, final Genome[] parents,
                                 final int parentIndex, final Genome[] offspring,
                                 final int offspringIndex) {

        final EnsembleMasterGenome ensemble = (EnsembleMasterGenome)obtainGenome(parents, parentIndex, offspring,
                offspringIndex);

        final int whichAnn = rnd.nextInt(ensemble.getNumAnns());
        final SingleNEATGenome targetAnn = ensemble.getAnns()[whichAnn];

        performOperation(targetAnn);
    }
}
