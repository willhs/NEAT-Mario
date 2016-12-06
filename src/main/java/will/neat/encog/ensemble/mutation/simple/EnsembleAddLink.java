package will.neat.encog.ensemble.mutation.simple;

import org.encog.mathutil.randomize.RangeRandomizer;
import org.encog.ml.ea.genome.Genome;
import org.encog.neural.neat.NEATNeuronType;
import org.encog.neural.neat.training.NEATNeuronGene;
import org.encog.neural.neat.training.SingleNEATGenome;
import org.encog.neural.neat.training.opp.NEATMutateAddLink;
import will.neat.encog.ensemble.genome.NEATEnsembleGenome;
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
        NEATEnsembleGenome ensemble = (NEATEnsembleGenome) this.obtainGenome(parents, parentIndex, offspring,
                offspringIndex);

        // choose one random ANN in the ensemble to mutate
        int whichAnn = rnd.nextInt(ensemble.getNumAnns());
        SingleNEATGenome targetAnn = ensemble.getAnns()[whichAnn];

        peformOperation(targetAnn, rnd);
    }
}
