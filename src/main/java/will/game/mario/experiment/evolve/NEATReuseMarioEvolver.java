package will.game.mario.experiment.evolve;

import org.encog.ml.ea.genome.Genome;
import org.encog.ml.ea.population.Population;
import org.encog.ml.ea.train.basic.TrainEA;
import org.encog.neural.neat.NEATPopulation;
import org.encog.neural.neat.training.NEATGenome;
import will.game.mario.rf.action.ActionStratFactory;

import java.util.List;
import java.util.stream.IntStream;

/**
 * Created by Will on 25/01/2017.
 */
public class NEATReuseMarioEvolver {

    private final NEATMarioEvolver[] evolvers;

    public NEATReuseMarioEvolver(NEATMarioEvolver[] evolvers) {
        this.evolvers = evolvers;
    }

    public TrainEA run() {
        TrainEA first = evolvers[0].run();
        List<NEATGenome> individuals = ((NEATPopulation)first.getPopulation()).getIndividuals();

        for (int i = 1; i < evolvers.length; i++) {
            // wipe the scores from the last task
            individuals.stream()
                    .forEach(m -> m.setScore(0));

            TrainEA neat = evolvers[i].getNEAT();
            NEATPopulation pop = (NEATPopulation) neat.getPopulation();
            pop.reset(individuals);
            evolvers[i].run(neat);
            individuals = ((NEATPopulation) neat.getPopulation()).getIndividuals();

        }

        return first;
    }
}
