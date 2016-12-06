package will.neat.encog.ensemble.mutation.master;

import org.encog.ml.ea.genome.Genome;
import org.encog.neural.neat.training.SingleNEATGenome;
import will.neat.encog.ensemble.genome.EnsembleMasterGenome;
import will.neat.encog.ensemble.mutation.simple.EnsembleAddLink;

import java.util.Random;

/**
 * Created by hardwiwill on 1/12/16.
 */
public class EnsembleMAddLink extends EnsembleAddLink {

    /**
     * {@inheritDoc}
     */
    @Override
    public void performOperation(final Random rnd, final Genome[] parents,
                                 final int parentIndex, final Genome[] offspring,
                                 final int offspringIndex) {

        EnsembleMasterGenome ensemble = (EnsembleMasterGenome) this.obtainGenome(parents, parentIndex, offspring,
                offspringIndex);

        // choose one random ANN in the ensemble to mutate
        int whichAnn = rnd.nextInt(ensemble.getNumAnns() + 1);
        SingleNEATGenome targetAnn =
                whichAnn < ensemble.getNumAnns()
                        ? ensemble.getAnns()[whichAnn]
                        : ensemble.getMaster();

        peformOperation(targetAnn, rnd);
    }
}

