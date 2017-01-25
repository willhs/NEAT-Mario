package will.neat.encog;

import org.encog.ml.ea.genome.Genome;
import org.encog.neural.neat.NEATNeuronType;
import org.encog.neural.neat.training.NEATBaseGene;
import org.encog.neural.neat.training.NEATNeuronGene;
import org.encog.neural.neat.training.SingleNEATGenome;
import org.encog.neural.neat.training.opp.NEATMutation;

import java.util.*;
import java.util.stream.Collectors;

/**
 * Created by hardwiwill on 25/01/17.
 */
public class DeleteNeuronSandpileMutate extends NEATMutation {

    private double sandpileSlope;

    public DeleteNeuronSandpileMutate(double sandpileSlope) {
        this.sandpileSlope = sandpileSlope;
    }

    @Override
    public void performOperation(Random rnd, Genome[] parents, int parentIndex, Genome[] offspring, int offspringIndex) {
        SingleNEATGenome target = (SingleNEATGenome) this.obtainGenome(parents, parentIndex, offspring,
                offspringIndex);

        performOperation(target, rnd);
    }

    private void performOperation(SingleNEATGenome target, Random rnd) {
        final List<NEATNeuronGene> hiddenNeurons = target.getNeuronsChromosome().stream()
                .filter(neuron -> neuron.getNeuronType() == NEATNeuronType.Hidden)
                .collect(Collectors.toList());

        // if no hidden neurons, nothing to do
        if (hiddenNeurons.isEmpty()) {
            return;
        }

        NEATNeuronGene targetNeuron = (NEATNeuronGene) chooseGeneSandpile(new ArrayList<>(hiddenNeurons), rnd);
        target.getNeuronsChromosome().remove(targetNeuron);

        // remove all links to the neuron
        target.getLinksChromosome().removeIf(link ->
                link.getFromNeuronID() == targetNeuron.getId()
                        ||  link.getToNeuronID() == targetNeuron.getId()
        );
    }

    private NEATBaseGene chooseGeneSandpile(List<NEATBaseGene> genes, Random rnd) {
        // assign each link gene a border in probabilty space
        Map<Double, NEATBaseGene> positionsMap = new HashMap<>();
        double sum = 0;
        for (int i = 0; i < genes.size(); i++) {
            NEATBaseGene l = genes.get(i);
            double weight = Math.pow(i+1, -sandpileSlope);
            sum += weight;
            positionsMap.put(sum, l);
        }

        // choose a gene based on the probability distribution
        double random = rnd.nextDouble() * sum;
        List<Double> positions = new ArrayList<>(positionsMap.keySet());
        Collections.sort(positions);

        double position = positions.stream()
                .filter(p -> p >= random).findFirst().get();
        return positionsMap.get(position);
    }
}
