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
    private double complexityCeiling;
    private int complexityCeilingGap;

    // tracking performance improvement
    private double lastBestScore = 0;
    private int gensWithoutImprovement = 0;

    public GreenPhasedSearch(int complexityCeilingGap, int maxGensWithoutImprovement) {
        this.complexityCeilingGap = complexityCeilingGap;
        this.MAX_GENS_WITHOUT_IMPROVEMENT = maxGensWithoutImprovement;
    }

    @Override
    public void init(MLTrain train) {
        super.init(train);
        double mpc = getMPC();
        complexityCeiling = mpc + complexityCeilingGap;
    }

    @Override
    public void preIteration() {
        double mpc = getMPC();

        if (phase == Phase.COMPLEXIFICATION
                && mpc > complexityCeiling
                && gensWithoutImprovement > MAX_GENS_WITHOUT_IMPROVEMENT) {
            switchPhase();
        }
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
