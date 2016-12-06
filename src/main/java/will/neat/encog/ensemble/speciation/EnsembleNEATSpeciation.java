package will.neat.encog.ensemble.speciation;

import org.encog.ml.ea.genome.Genome;
import org.encog.neural.neat.training.SingleNEATGenome;
import org.encog.neural.neat.training.species.OriginalNEATSpeciation;
import will.neat.encog.ensemble.genome.NEATEnsembleGenome;

/**
 * Created by hardwiwill on 29/11/16.
 */
public class EnsembleNEATSpeciation extends OriginalNEATSpeciation {

    /**
     * {@inheritDoc}
     */
    @Override
    public double getCompatibilityScore(final Genome gen1,
                                        final Genome gen2) {

        final NEATEnsembleGenome genome1 = (NEATEnsembleGenome)gen1;
        final NEATEnsembleGenome genome2 = (NEATEnsembleGenome)gen2;

        // find compatibility between each pair of singular ANNs and return the average
        double scoreSum = 0;
        for (int i = 0; i < genome1.getAnns().length; i++) {
            SingleNEATGenome g1 = genome1.getAnns()[i];
            SingleNEATGenome g2 = genome2.getAnns()[i];

            scoreSum += super.getCompatibilityScore(g1, g2);
        }

        double average = scoreSum / genome1.getAnns().length;
        return average;
    }

    protected double getCompatibilityScore(final SingleNEATGenome g1, final SingleNEATGenome g2) {
        return super.getCompatibilityScore(g1, g2);
    }

}
