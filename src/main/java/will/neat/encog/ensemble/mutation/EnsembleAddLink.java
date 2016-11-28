package will.neat.encog.ensemble.mutation;

import org.encog.mathutil.randomize.RangeRandomizer;
import org.encog.ml.ea.genome.Genome;
import org.encog.neural.neat.NEATNeuronType;
import org.encog.neural.neat.NEATPopulation;
import org.encog.neural.neat.training.NEATNeuronGene;
import org.encog.neural.neat.training.SingleNEATGenome;
import org.encog.neural.neat.training.opp.NEATMutateAddLink;
import will.neat.encog.ensemble.NEATEnsembleGenome;
import will.neat.encog.ensemble.NEATEnsemblePopulation;

import java.util.Random;

/**
 * Created by hardwiwill on 28/11/16.
 */
public class EnsembleAddLink extends NEATMutateAddLink {

    /**
     * {@inheritDoc}
     */
    @Override
    public void performOperation(Random rnd, Genome[] parents, int parentIndex,
                                 Genome[] offspring, int offspringIndex) {
        int countTrysToAddLink = this.getOwner().getMaxTries();

        NEATEnsembleGenome ensemble = (NEATEnsembleGenome) this.obtainGenome(parents, parentIndex, offspring,
                offspringIndex);

        // choose one random ANN in the ensemble to mutate
        int whichAnn = rnd.nextInt(ensemble.getNumAnns());
        SingleNEATGenome targetAnn = ensemble.getAnns()[whichAnn];

        // the link will be between these two neurons
        long neuron1ID = -1;
        long neuron2ID = -1;

        // try to add a link
        while ((countTrysToAddLink--) > 0) {
            final NEATNeuronGene neuron1 = chooseRandomNeuron(targetAnn, true);
            final NEATNeuronGene neuron2 = chooseRandomNeuron(targetAnn, false);

            if (neuron1 == null || neuron2 == null) {
                return;
            }

            // do not duplicate
            // do not go to a bias neuron
            // do not go from an output neuron
            // do not go to an input neuron
            if (!isDuplicateLink(targetAnn, neuron1.getId(), neuron2.getId())
                    && (neuron2.getNeuronType() != NEATNeuronType.Bias)
                    && (neuron2.getNeuronType() != NEATNeuronType.Input)) {

                if ( ((NEATEnsemblePopulation)getOwner().getPopulation()).getActivationCycles() != 1
                        || neuron1.getNeuronType() != NEATNeuronType.Output) {
                    neuron1ID = neuron1.getId();
                    neuron2ID = neuron2.getId();
                    break;
                }
            }
        }

        // did we fail to find a link
        if ((neuron1ID < 0) || (neuron2ID < 0)) {
            return;
        }

        double r = ((NEATEnsemblePopulation) ensemble.getPopulation()).getWeightRange();
        createLink(targetAnn, neuron1ID, neuron2ID,
                RangeRandomizer.randomize(rnd, -r, r));
        ensemble.sortGenes();
    }
}
