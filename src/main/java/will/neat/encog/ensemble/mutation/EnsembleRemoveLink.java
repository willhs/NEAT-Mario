package will.neat.encog.ensemble.mutation;

import org.encog.mathutil.randomize.RangeRandomizer;
import org.encog.ml.ea.genome.Genome;
import org.encog.neural.neat.training.NEATLinkGene;
import org.encog.neural.neat.training.SingleNEATGenome;
import org.encog.neural.neat.training.opp.NEATMutateRemoveLink;
import will.neat.encog.ensemble.NEATEnsembleGenome;

import java.util.Random;

/**
 * Created by hardwiwill on 28/11/16.
 */
public class EnsembleRemoveLink extends NEATMutateRemoveLink {

    /**
     * {@inheritDoc}
     */
    @Override
    public void performOperation(final Random rnd, final Genome[] parents,
                                 final int parentIndex, final Genome[] offspring,
                                 final int offspringIndex) {

        final NEATEnsembleGenome ensemble = (NEATEnsembleGenome) obtainGenome(parents, parentIndex, offspring,
                offspringIndex);

        final int whichAnn = rnd.nextInt(ensemble.getNumAnns());
        final SingleNEATGenome targetAnn = ensemble.getAnns()[whichAnn];

        if (targetAnn.getLinksChromosome().size() < NEATMutateRemoveLink.MIN_LINK) {
            // don't remove from small genomes
            return;
        }

        // determine the target and remove
        final int index = RangeRandomizer.randomInt(0, targetAnn
                .getLinksChromosome().size() - 1);
        final NEATLinkGene targetGene = targetAnn.getLinksChromosome().get(index);
        targetAnn.getLinksChromosome().remove(index);

        // if this orphaned any nodes, then kill them too!
        if (!isNeuronNeeded(targetAnn, targetGene.getFromNeuronID())) {
            removeNeuron(targetAnn, targetGene.getFromNeuronID());
        }

        if (!isNeuronNeeded(targetAnn, targetGene.getToNeuronID())) {
            removeNeuron(targetAnn, targetGene.getToNeuronID());
        }
    }
}
