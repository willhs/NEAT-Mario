package will.neat.encog.ensemble;

import org.encog.ml.ea.genome.Genome;
import org.encog.ml.ea.species.ThresholdSpeciation;
import org.encog.neural.neat.training.SingleNEATGenome;

/**
 * Created by hardwiwill on 29/11/16.
 */
public class EnsembleNEATSpeciation extends ThresholdSpeciation {

    /**
     * The serial id
     */
    private static final long serialVersionUID = 1L;

    /**
     * The adjustment factor for disjoint genes.
     */
    private double constDisjoint = 1;

    /**
     * The adjustment factor for excess genes.
     */
    private double constExcess = 1;

    /**
     * The adjustment factor for matched genes.
     */
    private double constMatched = 0.4;



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

            scoreSum += getCompatibilityScore0(g1, g2);
        }

        double average = scoreSum / genome1.getAnns().length;
        return average;
    }

    private double getCompatibilityScore0(SingleNEATGenome genome1, SingleNEATGenome genome2) {
        double numDisjoint = 0;
        double numExcess = 0;
        double numMatched = 0;
        double weightDifference = 0;

        final int genome1Size = genome1.getLinksChromosome().size();
        final int genome2Size = genome2.getLinksChromosome().size();
        final int n = 1;// Math.max(genome1Size, genome2Size);

        int g1 = 0;
        int g2 = 0;

        while ((g1 < genome1Size) || (g2 < genome2Size)) {

            if (g1 == genome1Size) {
                g2++;
                numExcess++;
                continue;
            }

            if (g2 == genome2Size) {
                g1++;
                numExcess++;
                continue;
            }

            // get innovation numbers for each gene at this point
            final long id1 = genome1.getLinksChromosome().get(g1)
                    .getInnovationId();
            final long id2 = genome2.getLinksChromosome().get(g2)
                    .getInnovationId();

            // innovation numbers are identical so increase the matched score
            if (id1 == id2) {

                // get the weight difference between these two genes
                weightDifference += Math.abs(genome1.getLinksChromosome()
                        .get(g1).getWeight()
                        - genome2.getLinksChromosome().get(g2).getWeight());
                g1++;
                g2++;
                numMatched++;
            }

            // innovation numbers are different so increment the disjoint score
            if (id1 < id2) {
                numDisjoint++;
                g1++;
            }

            if (id1 > id2) {
                ++numDisjoint;
                ++g2;
            }

        }

        final double score = ((this.constExcess * numExcess) / n)
                + ((this.constDisjoint * numDisjoint) / n)
                + (this.constMatched * (weightDifference / numMatched));

        return score;
    }

    /**
     * @return the constDisjoint
     */
    public double getConstDisjoint() {
        return this.constDisjoint;
    }

    /**
     * @return the constExcess
     */
    public double getConstExcess() {
        return this.constExcess;
    }

    /**
     * @return the constMatched
     */
    public double getConstMatched() {
        return this.constMatched;
    }



    /**
     * @param constDisjoint
     *            the constDisjoint to set
     */
    public void setConstDisjoint(final double constDisjoint) {
        this.constDisjoint = constDisjoint;
    }

    /**
     * @param constExcess
     *            the constExcess to set
     */
    public void setConstExcess(final double constExcess) {
        this.constExcess = constExcess;
    }

    /**
     * @param constMatched
     *            the constMatched to set
     */
    public void setConstMatched(final double constMatched) {
        this.constMatched = constMatched;
    }
}
