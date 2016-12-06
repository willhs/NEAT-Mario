package will.neat.encog.ensemble;

import org.encog.ml.CalculateScore;
import org.encog.ml.ea.genome.Genome;

/**
 * Created by hardwiwill on 6/12/16.
 */
public interface GenotypeFF {
    double calculateScore(Genome g);
}
