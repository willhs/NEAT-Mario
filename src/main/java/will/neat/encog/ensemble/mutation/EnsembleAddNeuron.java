package will.neat.encog.ensemble.mutation;

import org.encog.engine.network.activation.ActivationFunction;
import org.encog.mathutil.randomize.RangeRandomizer;
import org.encog.ml.ea.genome.Genome;
import org.encog.neural.neat.NEATNeuronType;
import org.encog.neural.neat.NEATPopulation;
import org.encog.neural.neat.training.*;
import org.encog.neural.neat.training.opp.NEATMutateAddNeuron;
import will.neat.encog.ensemble.NEATEnsembleGenome;
import will.neat.encog.ensemble.NEATEnsemblePopulation;

import java.util.Random;

/**
 * Created by hardwiwill on 28/11/16.
 */
public class EnsembleAddNeuron extends NEATMutateAddNeuron {

    /**
     * {@inheritDoc}
     */
    @Override
    public void performOperation(final Random rnd, final Genome[] parents,
                                 final int parentIndex, final Genome[] offspring,
                                 final int offspringIndex) {
        final NEATEnsembleGenome ensemble = (NEATEnsembleGenome) obtainGenome(parents, parentIndex, offspring,
                offspringIndex);
        int countTrysToFindOldLink = getOwner().getMaxTries();

        final int whichAnn = rnd.nextInt(ensemble.getNumAnns());
        final SingleNEATGenome targetAnn = ensemble.getAnns()[whichAnn];

        // cannot perform operation if there are no links
        if (targetAnn.getLinksChromosome().isEmpty()) {
            return;
        }

        final AbstractNEATPopulation pop = ((AbstractNEATPopulation) ensemble.getPopulation());

        // the link to split
        NEATLinkGene splitLink = null;

        final int sizeBias = ((NEATEnsembleGenome)parents[0]).getInputCount()
                + ((NEATEnsembleGenome)parents[0]).getOutputCount() + 10;

        // if there are not at least
        int upperLimit;
        if (targetAnn.getLinksChromosome().size() < sizeBias) {
            upperLimit = targetAnn.getNumGenes() - 1
                    - (int) Math.sqrt(targetAnn.getNumGenes());
        } else {
            upperLimit = targetAnn.getNumGenes() - 1;
        }

        while ((countTrysToFindOldLink--) > 0) {
            // choose a link, use the square root to prefer the older links
            final int i = RangeRandomizer.randomInt(0, upperLimit);
            final NEATLinkGene link = targetAnn.getLinksChromosome().get(i);

            // get the from neuron
            final long fromNeuron = link.getFromNeuronID();

            if ((link.isEnabled())
                    && (ensemble.getNeuronsChromosome()
                    .get(getElementPos(targetAnn, fromNeuron))
                    .getNeuronType() != NEATNeuronType.Bias)) {
                splitLink = link;
                break;
            }
        }

        if (splitLink == null) {
            return;
        }

        splitLink.setEnabled(false);

        final long from = splitLink.getFromNeuronID();
        final long to = splitLink.getToNeuronID();

        final NEATInnovation innovation = ((NEATEnsemblePopulation)getOwner().getPopulation()).getInnovations()
                .findInnovationSplit(from, to);

        // add the splitting neuron
        final ActivationFunction af = ((NEATEnsemblePopulation)getOwner().getPopulation())
                .getActivationFunctions().pick(new Random());

        targetAnn.getNeuronsChromosome().add(
                new NEATNeuronGene(NEATNeuronType.Hidden, af, innovation
                        .getNeuronID(), innovation.getInnovationID()));

        // add the other two sides of the link
        createLink(targetAnn, from, innovation.getNeuronID(),
                splitLink.getWeight());
        createLink(targetAnn, innovation.getNeuronID(), to, pop.getWeightRange());

        targetAnn.sortGenes();
    }
}
