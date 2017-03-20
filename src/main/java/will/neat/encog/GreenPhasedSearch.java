package will.neat.encog;

import org.encog.ml.train.MLTrain;
import org.encog.neural.neat.NEATPopulation;

/**
 * Created by hardwiwill on 25/01/17.
 */
public class GreenPhasedSearch extends AbstractPhasedSearch {

    private final int MAX_GENS_WITHOUT_IMPROVEMENT;
    private final int MIN_SIMPLIFICATION_GENS;
    private double complexityCeiling = -1;
    private int complexityJump;

    // tracking performance improvement
    private double lastBestScore = 0;
    private int gensWithoutImprovement = 0;
    private double lastGenMPC;

    public GreenPhasedSearch(int complexityJump, int maxGensWithoutImprovement, int minSimplificationGens) {
        this.complexityJump = complexityJump;
        this.MAX_GENS_WITHOUT_IMPROVEMENT = maxGensWithoutImprovement;
        this.MIN_SIMPLIFICATION_GENS = minSimplificationGens;
    }

    @Override
    public void init(MLTrain train) {
        super.init(train);
    }

    @Override
    public void preIteration() {
        double mpc = ((NEATPopulation)neat.getPopulation()).getMPC();

        if (complexityCeiling == -1) {
            lastGenMPC = ((NEATPopulation)neat.getPopulation()).getMPC();
            complexityCeiling = lastGenMPC + complexityJump;
            System.out.println("Ceiling at: " + complexityCeiling);
        }

        if (phase == Phase.COMPLEXIFICATION
                && mpc > complexityCeiling
                && gensWithoutImprovement > MAX_GENS_WITHOUT_IMPROVEMENT) {
            switchPhase();
            System.out.println("Floor at: " + complexityCeiling);
        } else if (phase == Phase.SIMPLIFICATION
                && neat.getIteration() - lastTransitionGeneration > MIN_SIMPLIFICATION_GENS
                && mpc < complexityCeiling
                && mpc > lastGenMPC) {
            switchPhase();
            complexityCeiling = mpc + complexityJump;
            System.out.println("Ceiling now at: " + complexityCeiling);
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
