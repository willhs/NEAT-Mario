package will.neat.encog;

import org.encog.mathutil.randomize.RangeRandomizer;
import org.encog.ml.ea.genome.Genome;
import org.encog.ml.ea.score.parallel.ParallelScore;
import org.encog.neural.neat.NEATLink;
import org.encog.neural.neat.NEATNeuronType;
import org.encog.neural.neat.training.NEATGenome;
import org.encog.neural.neat.training.NEATLinkGene;
import org.encog.neural.neat.training.NEATNeuronGene;
import org.encog.neural.neat.training.SingleNEATGenome;
import org.encog.neural.neat.training.opp.NEATMutation;

import java.util.List;
import java.util.Random;
import java.util.stream.Collectors;

/**
 * Created by Will on 26/01/2017.
 */
public class DeleteNodeGreenMutate extends NEATMutation {
    @Override
    public void performOperation(Random rnd, Genome[] parents, int parentIndex, Genome[] offspring, int offspringIndex) {
        final SingleNEATGenome targetGenome = (SingleNEATGenome)obtainGenome(parents, parentIndex, offspring,
                offspringIndex);

        performOperation(targetGenome, rnd);
    }

    protected void performOperation(SingleNEATGenome targetGenome, Random rnd) {
        final List<NEATNeuronGene> hiddenNeurons = targetGenome.getNeuronsChromosome().stream()
                .filter(neuron -> neuron.getNeuronType() == NEATNeuronType.Hidden)
                // the node has only one incoming or outgoing connection
                .filter(n -> {
                    long incoming = targetGenome.getLinksChromosome().stream()
                            .filter(l -> l.getToNeuronID() == n.getId())
                            .count();

                    long outgoing = targetGenome.getLinksChromosome().stream()
                            .filter(l -> l.getFromNeuronID() == n.getId())
                            .count();

                    return incoming <= 1 || outgoing <= 1;
                })
                .collect(Collectors.toList());

        // if no hidden neurons, nothing to do
        if (hiddenNeurons.isEmpty()) {
            return;
        }
        // determine the target and remove
        final int index = rnd.nextInt(hiddenNeurons.size());

        final NEATNeuronGene targetNeuron = hiddenNeurons.get(index);
        final long targetID = targetNeuron.getId();
        removeNeuron(targetGenome, targetID);

        // reconnect links which have been disconnected
        NEATLinkGene[] incoming = targetGenome.getLinksChromosome().stream()
                .filter(l -> l.getToNeuronID() == targetNeuron.getId())
                .toArray(NEATLinkGene[]::new);

        NEATLinkGene[] outgoing = targetGenome.getLinksChromosome().stream()
                .filter(l -> l.getFromNeuronID() == targetNeuron.getId())
                .toArray(NEATLinkGene[]::new);

        // check whether the neuron is already disconnected
        if (incoming.length == 0 || outgoing.length == 0) {
            removeNeuron(targetGenome, targetNeuron.getId());
            for (int i = 0; i < Math.max(incoming.length, outgoing.length); i++) {
                if (incoming.length > i) {
                    removeLink(targetGenome, incoming[i]);
                }
                if (outgoing.length > i){
                    removeLink(targetGenome, outgoing[i]);
                }
            }
        }
        // otherwise reconnect links after deleting the neuron
        else {
            NEATLinkGene toRemove;
            if (incoming.length == 1) {
                reconnectLinks(targetGenome, incoming[0], outgoing, targetID);
                toRemove = incoming[0];
            } else {
                reconnectLinks(targetGenome, outgoing[0], incoming, targetID);
                toRemove = outgoing[0];
            }

            // remove remaining link that is now goes nowhere
            int size = targetGenome.getLinksChromosome().size();
            removeLink(targetGenome, toRemove);

            NEATLinkGene[] badLinks = targetGenome.getLinksChromosome().stream()
                    .filter(l -> l.getToNeuronID() == targetID || l.getFromNeuronID() == targetID )
                    .toArray(NEATLinkGene[]::new);

            System.out.print("");
        }

        NEATLinkGene[] badLinks = targetGenome.getLinksChromosome().stream()
                .filter(l -> l.getToNeuronID() == targetID || l.getFromNeuronID() == targetID )
                .toArray(NEATLinkGene[]::new);




        for (NEATLinkGene badLink : badLinks) {
            removeLink(targetGenome, badLink);
        }
    }

    /**
     * Reconnects links unless they point to and from the same neuron, in which case those are deleted
     * @param genome
     * @param single
     * @param several
     * @param neuronID
     */
    private void reconnectLinks(SingleNEATGenome genome, NEATLinkGene single, NEATLinkGene[] several, long neuronID) {
        if (single.getFromNeuronID() == neuronID) {
            for (NEATLinkGene l : several) {
                if (l.getFromNeuronID() != single.getToNeuronID()) {
                    removeLink(genome, l);
                } else {
                    l.setToNeuronID(single.getToNeuronID());
                }
            }
        } else {
            for (NEATLinkGene l : several) {
                if (l.getToNeuronID() == single.getFromNeuronID()) {
                    removeLink(genome, l);
                } else {
                    l.setFromNeuronID(single.getFromNeuronID());
                }
            }
        }
    }
}
