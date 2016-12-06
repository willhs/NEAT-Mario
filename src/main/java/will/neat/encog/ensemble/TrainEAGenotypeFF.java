package will.neat.encog.ensemble;

import org.encog.ml.CalculateScore;
import org.encog.ml.ea.genome.Genome;
import org.encog.ml.ea.population.Population;
import org.encog.ml.ea.train.basic.TrainEA;
import org.encog.ml.prg.expvalue.DivisionByZeroError;

/**
 * Uses a fitness function that takes a genotype instead a of phenotype
 * Created by hardwiwill on 6/12/16.
 */
public class TrainEAGenotypeFF extends TrainEA {
    private GenotypeFF genotypeFF;

    public TrainEAGenotypeFF(Population thePopulation, EnsembleDiversityFF genotypeFF) {
        super(thePopulation, genotypeFF.getPhenoTypeFF());
        this.genotypeFF = genotypeFF;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void calculateScore(final Genome g) {

        // try rewrite
        getPopulation().getRules().rewrite(g);

        // deal with invalid decode
        double score = genotypeFF.calculateScore(g);

        // now set the scores
        g.setScore(score);
        g.setAdjustedScore(score);
    }
}
