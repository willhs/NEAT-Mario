package will.neat.encog.ensemble.speciation;

import org.encog.ml.ea.genome.Genome;
import org.encog.neural.neat.training.SingleNEATGenome;
import org.encog.neural.neat.training.species.OriginalNEATSpeciation;
import will.neat.encog.ensemble.genome.EnsembleMasterGenome;

/**
 * Created by hardwiwill on 1/12/16.
 */
public class EnsembleMasterSpeciation extends EnsembleNEATSpeciation {

    /**
     * {@inheritDoc}
     */
    @Override
    public double getCompatibilityScore(final Genome gen1,
                                        final Genome gen2) {

        final EnsembleMasterGenome genome1 = (EnsembleMasterGenome)gen1;
        final EnsembleMasterGenome genome2 = (EnsembleMasterGenome)gen2;

        final OriginalNEATSpeciation neatSpeciation = new OriginalNEATSpeciation();

        // find compatibility between each pair of singular ANNs and return the average
        double scoreSum = 0;
        for (int i = 0; i < genome1.getAnns().length; i++) {
            SingleNEATGenome g1 = genome1.getAnns()[i];
            SingleNEATGenome g2 = genome2.getAnns()[i];

            scoreSum += super.getCompatibilityScore(g1, g2);
        }

        SingleNEATGenome m1 = genome1.getMaster();
        SingleNEATGenome m2 = genome2.getMaster();

        scoreSum += super.getCompatibilityScore(m1, m2);

        double average = scoreSum / (genome1.getAnns().length + 1);
        return average;
    }
}
