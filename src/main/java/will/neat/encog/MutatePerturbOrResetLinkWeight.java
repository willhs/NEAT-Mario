package will.neat.encog;

import org.encog.ml.ea.train.EvolutionaryAlgorithm;
import org.encog.neural.neat.training.NEATLinkGene;
import org.encog.neural.neat.training.opp.links.MutateLinkWeight;
import org.encog.neural.neat.training.opp.links.MutatePerturbLinkWeight;
import org.encog.neural.neat.training.opp.links.MutateResetLinkWeight;

import java.util.Random;

/**
 * Mutate weight links by either resetting or perturbing
 * The operation (resetting or perturbing) will be delegated to
 * either MutatePerturbLinkWeight or MutateResetLinkWeight
 */
public class MutatePerturbOrResetLinkWeight implements MutateLinkWeight {

    /**
     * The trainer being used.
     */
    private EvolutionaryAlgorithm trainer;

    private double resetProb;
    private double sigma;

    public MutatePerturbOrResetLinkWeight(double resetProb, double theSigma) {
        this.resetProb = resetProb;
        this.sigma = theSigma;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public EvolutionaryAlgorithm getTrainer() {
        return this.trainer;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void init(final EvolutionaryAlgorithm theTrainer) {
        this.trainer = theTrainer;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void mutateWeight(final Random rnd, final NEATLinkGene linkGene,
                             final double weightRange) {
        if (rnd.nextDouble() > resetProb) {
            new MutatePerturbLinkWeight(sigma).mutateWeight(rnd, linkGene, weightRange);
        } else {
            new MutateResetLinkWeight().mutateWeight(rnd, linkGene, weightRange);
        }
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public String toString() {
        final StringBuilder result = new StringBuilder();
        result.append("[");
        result.append(this.getClass().getSimpleName());
        result.append("]");
        return result.toString();
    }
}

