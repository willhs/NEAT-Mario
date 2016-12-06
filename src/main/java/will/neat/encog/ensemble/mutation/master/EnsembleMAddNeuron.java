package will.neat.encog.ensemble.mutation.master;

import org.encog.ml.ea.genome.Genome;
import org.encog.neural.neat.training.SingleNEATGenome;
import will.neat.encog.ensemble.genome.EnsembleMasterGenome;
import will.neat.encog.ensemble.genome.NEATEnsembleGenome;
import will.neat.encog.ensemble.mutation.simple.EnsembleAddNeuron;

import java.util.Random;

/**
 * Created by hardwiwill on 1/12/16.
 */
public class EnsembleMAddNeuron extends EnsembleAddNeuron {

    /**
     * {@inheritDoc}
     */
    @Override
    public void performOperation(final Random rnd, final Genome[] parents,
                                 final int parentIndex, final Genome[] offspring,
                                 final int offspringIndex) {
        final EnsembleMasterGenome ensemble = (EnsembleMasterGenome) obtainGenome(parents, parentIndex, offspring,
                offspringIndex);

        int whichAnn = rnd.nextInt(ensemble.getNumAnns() + 1);
        SingleNEATGenome targetAnn =
                whichAnn < ensemble.getNumAnns()
                        ? ensemble.getAnns()[whichAnn]
                        : ensemble.getMaster();

        performOperation(targetAnn);
    }
}
