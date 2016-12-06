package will.neat.encog.ensemble.mutation.master;

import org.encog.ml.ea.genome.Genome;
import org.encog.neural.neat.training.SingleNEATGenome;
import will.neat.encog.ensemble.genome.EnsembleMasterGenome;
import will.neat.encog.ensemble.genome.NEATEnsembleGenome;
import will.neat.encog.ensemble.mutation.simple.EnsembleRemoveLink;

import java.util.Random;

/**
 * Created by hardwiwill on 1/12/16.
 */
public class EnsembleMRemoveLink extends EnsembleRemoveLink {
    /**
     * {@inheritDoc}
     */
    @Override
    public void performOperation(final Random rnd, final Genome[] parents,
                                 final int parentIndex, final Genome[] offspring,
                                 final int offspringIndex) {

        final EnsembleMasterGenome ensemble = (EnsembleMasterGenome) obtainGenome(parents, parentIndex, offspring,
                offspringIndex);

        final int whichAnn = rnd.nextInt(ensemble.getNumAnns());
        SingleNEATGenome targetAnn =
                whichAnn < ensemble.getNumAnns()
                        ? ensemble.getAnns()[whichAnn]
                        : ensemble.getMaster();

        performOperation(targetAnn);
    }
}
