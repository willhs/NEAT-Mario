package will.neat.encog.ensemble.mutation.master;

import org.encog.ml.ea.genome.Genome;
import org.encog.neural.neat.training.SingleNEATGenome;
import org.encog.neural.neat.training.opp.links.MutateLinkWeight;
import org.encog.neural.neat.training.opp.links.SelectLinks;
import will.neat.encog.ensemble.genome.EnsembleMasterGenome;
import will.neat.encog.ensemble.genome.NEATEnsembleGenome;
import will.neat.encog.ensemble.mutation.simple.EnsemblePerturbLink;

import java.util.Random;

/**
 * Created by hardwiwill on 1/12/16.
 */
public class EnsembleMPerturbLink extends EnsemblePerturbLink {
    /**
     * Construct a weight mutation operator.
     *
     * @param theLinkSelection  The method used to choose the links for mutation.
     * @param theWeightMutation The method used to actually mutate the weights.
     */
    public EnsembleMPerturbLink(SelectLinks theLinkSelection, MutateLinkWeight theWeightMutation) {
        super(theLinkSelection, theWeightMutation);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void performOperation(final Random rnd, final Genome[] parents,
                                 final int parentIndex, final Genome[] offspring,
                                 final int offspringIndex) {
        final EnsembleMasterGenome ensemble = (EnsembleMasterGenome) obtainGenome(parents, parentIndex, offspring,
                offspringIndex);

        // choose one random ANN in the ensemble to mutate
        int whichAnn = rnd.nextInt(ensemble.getNumAnns());
        SingleNEATGenome targetAnn =
                whichAnn < ensemble.getNumAnns()
                        ? ensemble.getAnns()[whichAnn]
                        : ensemble.getMaster();

        performOperation(targetAnn, rnd);
    }
}
