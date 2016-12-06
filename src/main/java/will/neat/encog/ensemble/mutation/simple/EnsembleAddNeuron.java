package will.neat.encog.ensemble.mutation.simple;

import org.encog.engine.network.activation.ActivationFunction;
import org.encog.mathutil.randomize.RangeRandomizer;
import org.encog.ml.ea.genome.Genome;
import org.encog.neural.neat.NEATNeuronType;
import org.encog.neural.neat.training.*;
import org.encog.neural.neat.training.opp.NEATMutateAddNeuron;
import will.neat.encog.ensemble.genome.NEATEnsembleGenome;
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

        final int whichAnn = rnd.nextInt(ensemble.getNumAnns());
        final SingleNEATGenome targetAnn = ensemble.getAnns()[whichAnn];

        performOperation(targetAnn);
    }
}
