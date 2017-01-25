package will.neat.encog;

import org.encog.ml.ea.genome.Genome;
import org.encog.neural.neat.training.NEATBaseGene;
import org.encog.neural.neat.training.NEATLinkGene;
import org.encog.neural.neat.training.SingleNEATGenome;
import org.encog.neural.neat.training.opp.NEATMutation;

import java.util.*;

/**
 * Created by hardwiwill on 25/01/17.
 */
public class DeleteLinkSandpileMutate extends NEATMutation {

    public DeleteLinkSandpileMutate(double sandpileSlope) {
        this.sandpileSlope = sandpileSlope;
    }

    @Override
    public void performOperation(Random rnd, Genome[] parents, int parentIndex, Genome[] offspring, int offspringIndex) {
        SingleNEATGenome target = (SingleNEATGenome) this.obtainGenome(parents, parentIndex, offspring,
                offspringIndex);

        performOperation(target, rnd);
    }

    private void performOperation(SingleNEATGenome target, Random rnd) {
        NEATLinkGene targetLink = (NEATLinkGene) chooseGeneSandpile(new ArrayList<>(target.getLinksChromosome()), rnd);

        target.getLinksChromosome().remove(targetLink);

        // if this orphaned any nodes, then kill them too!
        if (!isNeuronNeeded(target, targetLink.getFromNeuronID())) {
            removeNeuron(target, targetLink.getFromNeuronID());
        }

        if (!isNeuronNeeded(target, targetLink.getToNeuronID())) {
            removeNeuron(target, targetLink.getToNeuronID());
        }
    }

}
