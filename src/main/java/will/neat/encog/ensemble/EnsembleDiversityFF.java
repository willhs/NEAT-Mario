package will.neat.encog.ensemble;

import org.encog.ml.CalculateScore;
import org.encog.ml.ea.genome.Genome;
import org.encog.neural.neat.NEATNetwork;
import org.encog.neural.neat.training.SingleNEATGenome;
import org.encog.neural.neat.training.species.OriginalNEATSpeciation;
import will.game.mario.fitness.EnsembleMarioFF;
import will.neat.encog.ensemble.codec.EnsembleCODEC;
import will.neat.encog.ensemble.genome.NEATEnsembleGenome;

/**
 * Created by hardwiwill on 6/12/16.
 */
public class EnsembleDiversityFF implements GenotypeFF {

    private OriginalNEATSpeciation speciation = new OriginalNEATSpeciation();
    private EnsembleMarioFF marioFF;

    private final double maxDiversityScore = 20;
    // so that diversity scores are vaguely the same as mario scores
    private final double diversityNormaliser = 2;
    private final double diversityWeight = 0.3;
    private final double marioScoreWeight = 0.7;

    public EnsembleDiversityFF(EnsembleMarioFF marioFF) {
        this.marioFF = marioFF;
    }

    @Override
    public double calculateScore(Genome g) {
        NEATEnsembleGenome ensembleGenome = (NEATEnsembleGenome) g;
        double diversityScore = calculateEnsembleDiversity(ensembleGenome);

        NEATNetworkEnsemble ensemble = (NEATNetworkEnsemble) new EnsembleCODEC().decode(ensembleGenome);
        double marioScore = marioFF.calculateScore(ensemble);


//        System.out.println("scaled diversity score: " + (diversityScore*diversityNormaliser*diversityWeight));
//        System.out.println("scaled mario score: " + (marioScore + marioScoreWeight));

        return (diversityScore * diversityNormaliser * diversityWeight)
                + (marioScore * marioScoreWeight);
    }

    private double calculateEnsembleDiversity(NEATEnsembleGenome ensembleGenome) {
        double totalDiff = 0;
        for (SingleNEATGenome nn: ensembleGenome.getAnns()) {
            for (SingleNEATGenome nn2: ensembleGenome.getAnns()) {
                if (nn == nn2) continue;

                totalDiff += speciation.getCompatibilityScore(nn, nn2);
            }
        }

        double averageDiff = totalDiff / (Math.pow(ensembleGenome.getNumAnns(), 2) - ensembleGenome.getNumAnns());

        return averageDiff;
    }

    public CalculateScore getPhenoTypeFF() {
        return marioFF;
    }
}
