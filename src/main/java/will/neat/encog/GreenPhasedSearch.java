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
    private double lastMPC;

    public GreenPhasedSearch(int complexityCeilingGap, int maxGensWithoutImprovement, int minSimplificationGens) {
        this.complexityCeilingGap = complexityCeilingGap;
        this.MAX_GENS_WITHOUT_IMPROVEMENT = maxGensWithoutImprovement;
        this.MIN_SIMPLIFICATION_GENS = minSimplificationGens;
    }

    @Override
    public void init(MLTrain train) {
        super.init(train);
        lastMPC = getMPC();
        complexityCeiling = lastMPC + complexityCeilingGap;
    }

    @Override
    public void preIteration() {
        double mpc = getMPC();

        if (phase == Phase.COMPLEXIFICATION) {
            if (mpc > complexityCeiling
                && gensWithoutImprovement > MAX_GENS_WITHOUT_IMPROVEMENT) {
                switchPhase();
            }
        } else if (mpc < complexityCeiling
                && train.getIteration() - lastTransitionGeneration > MIN_SIMPLIFICATION_GENS
                && mpc > lastMPC) {
            switchPhase();
            complexityCeiling = mpc + complexityCeilingGap;
        }

        lastMPC = mpc;
    }

    @Override
    public void postIteration() {
        double newScore = train.getBestGenome().getScore();

        if (newScore > lastBestScore) {
            lastBestScore = newScore;
            gensWithoutImprovement = 0;
        } else {
            gensWithoutImprovement ++;
        }
    }


    public double getMPC() {
        List<Genome> genomes = train.getPopulation().getSpecies().stream()
                .flatMap(s -> s.getMembers().stream())
                .collect(Collectors.toList());

        long numLinks = genomes.stream()
                .flatMap(g -> ((NEATGenome)g).getLinksChromosome().stream())
                .count();

        long numNodes = genomes.stream()
                .flatMap(g -> ((NEATGenome)g).getNeuronsChromosome().stream())
                .count();

        return (numLinks + numNodes) / genomes.size();
    }
}
