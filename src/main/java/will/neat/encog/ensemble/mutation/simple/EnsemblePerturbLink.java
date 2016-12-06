package will.neat.encog.ensemble.mutation.simple;

import org.encog.ml.ea.genome.Genome;
import org.encog.neural.neat.training.AbstractNEATPopulation;
import org.encog.neural.neat.training.SingleNEATGenome;
import org.encog.neural.neat.training.NEATLinkGene;
import org.encog.neural.neat.training.opp.NEATMutateWeights;
import org.encog.neural.neat.training.opp.links.MutateLinkWeight;
import org.encog.neural.neat.training.opp.links.SelectLinks;
import will.neat.encog.ensemble.genome.NEATEnsembleGenome;

import java.util.List;
import java.util.Random;

/**
 * Created by hardwiwill on 28/11/16.
 */
public class EnsemblePerturbLink extends NEATMutateWeights {

    /**
     * Construct a weight mutation operator.
     *
     * @param theLinkSelection  The method used to choose the links for mutation.
     * @param theWeightMutation The method used to actually mutate the weights.
     */
    public EnsemblePerturbLink(SelectLinks theLinkSelection, MutateLinkWeight theWeightMutation) {
        super(theLinkSelection, theWeightMutation);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void performOperation(final Random rnd, final Genome[] parents,
                                 final int parentIndex, final Genome[] offspring,
                                 final int offspringIndex) {
        final NEATEnsembleGenome ensemble = (NEATEnsembleGenome) obtainGenome(parents, parentIndex, offspring,
                offspringIndex);

        // choose one random ANN in the ensemble to mutate
        int whichAnn = rnd.nextInt(ensemble.getNumAnns());
        SingleNEATGenome targetAnn = ensemble.getAnns()[whichAnn];

        performOperation(targetAnn, rnd);
    }
}
