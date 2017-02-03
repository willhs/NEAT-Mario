package will.neat.encog;

import cz.cuni.amis.pogamut.base.utils.math.A;
import org.encog.mathutil.randomize.RangeRandomizer;
import org.encog.ml.ea.genome.Genome;
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

        // check whether the neuron is disconnected
        if (incoming.length == 0 || outgoing.length == 0) {
            removeNeuron(targetGenome, targetNeuron.getId());
            for (int i = 0; i < Math.max(incoming.length, outgoing.length); i++) {
                removeLink(targetGenome, incoming[i]);
                removeLink(targetGenome, outgoing[i]);
            }
        }
        // otherwise reconnect links after deleting the neuron
        else {
            long toRemove;
            if (incoming.length == 1) {
                reconnectLinks(incoming[0], outgoing, targetID);
                toRemove = incoming[0].getInnovationId();
            } else {
                reconnectLinks(outgoing[0], incoming, targetID);
                toRemove = outgoing[0].getInnovationId();
            }

            removeNeuron(targetGenome, toRemove);
        }
    }

    private void reconnectLinks(NEATLinkGene single, NEATLinkGene[] several, long neuronID) {
        if (single.getFromNeuronID() == neuronID) {
            for (NEATLinkGene l : several) {
                l.setToNeuronID(single.getToNeuronID());
            }
        } else {
            for (NEATLinkGene l : several) {
                l.setFromNeuronID(single.getFromNeuronID());
            }
        }
    }
}
