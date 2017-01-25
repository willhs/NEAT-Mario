package will.game.mario.experiment.evolve;

import org.encog.ml.ea.population.Population;
import org.encog.ml.ea.train.basic.TrainEA;
import will.game.mario.rf.action.ActionStratFactory;

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
        Population population = first.getPopulation();

        for (int i = 1; i < evolvers.length; i++) {
            TrainEA neat = evolvers[i].getNEAT();
            neat.setPopulation(population);
            evolvers[i].run(neat);
            population = neat.getPopulation();
            population.getSpecies().stream()
                    .flatMap(s -> s.getMembers().stream())
                    .forEach(m -> m.setScore(0));
        }

         return first;
    }
}
