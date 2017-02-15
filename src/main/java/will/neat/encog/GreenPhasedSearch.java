package will.neat.encog;

import org.encog.ml.ea.genome.Genome;
import org.encog.ml.train.MLTrain;
import org.encog.neural.neat.NEATPopulation;
import org.encog.neural.neat.training.NEATGenome;

import java.util.List;
import java.util.stream.Collectors;

/**
 * Created by hardwiwill on 25/01/17.
 */
public class GreenPhasedSearch extends AbstractPhasedSearch {

    private final int MAX_GENS_WITHOUT_IMPROVEMENT;
    private final int MIN_SIMPLIFICATION_GENS;
    private double complexityCeiling;
    private int complexityCeilingGap;

    // tracking performance improvement
    private double lastBestScore = 0;
    private int gensWithoutImprovement = 0;
    private double lastGenMPC;

    public GreenPhasedSearch(int complexityCeilingGap, int maxGensWithoutImprovement, int minSimplificationGens) {
        this.complexityCeilingGap = complexityCeilingGap;
        this.MAX_GENS_WITHOUT_IMPROVEMENT = maxGensWithoutImprovement;
        this.MIN_SIMPLIFICATION_GENS = minSimplificationGens;
    }

    @Override
    public void init(MLTrain train) {
        super.init(train);
        lastGenMPC = ((NEATPopulation)neat.getPopulation()).getMPC();
        complexityCeiling = lastGenMPC + complexityCeilingGap;
    }

    @Override
    public void preIteration() {
        double mpc = ((NEATPopulation)neat.getPopulation()).getMPC();

        if (phase == Phase.COMPLEXIFICATION
                && mpc > complexityCeiling
                && gensWithoutImprovement > MAX_GENS_WITHOUT_IMPROVEMENT) {
            switchPhase();
        } else if (phase == Phase.SIMPLIFICATION
                && neat.getIteration() - lastTransitionGeneration > MIN_SIMPLIFICATION_GENS
                && mpc < complexityCeiling
                && mpc > lastGenMPC) {
            switchPhase();
            complexityCeiling = mpc + complexityCeilingGap;
        }

        lastGenMPC = mpc;
    }

    @Override
    public void postIteration() {
        double newScore = neat.getBestGenome().getScore();

        if (newScore > lastBestScore) {
            lastBestScore = newScore;
            gensWithoutImprovement = 0;
        } else {
            gensWithoutImprovement ++;
        }
    }


}
