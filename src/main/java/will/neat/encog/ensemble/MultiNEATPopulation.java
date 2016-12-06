package will.neat.encog.ensemble;

import org.encog.ml.ea.genome.Genome;
import org.encog.ml.ea.genome.GenomeFactory;
import org.encog.ml.ea.population.BasicPopulation;
import org.encog.ml.ea.population.Population;
import org.encog.ml.ea.rules.RuleHolder;
import org.encog.ml.ea.species.Species;
import org.encog.neural.neat.NEATPopulation;

import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

/**
 * Created by hardwiwill on 2/12/16.
 */
public class MultiNEATPopulation extends BasicPopulation {

    private static final String MASTER_NAME = "MASTER";

    private List<NEATPopulation> modulePopulations;
    private NEATPopulation masterPopulation;

    public MultiNEATPopulation(int numModules, int popSize, int inputs, int outputs) {
        modulePopulations = IntStream.range(0, numModules)
                .mapToObj(i -> {
                    NEATPopulation pop = new NEATPopulation(inputs, outputs, popSize);
                    pop.setName(pop + "");
                    return pop;
                })
                .collect(Collectors.toList());

        masterPopulation = new NEATPopulation(inputs, modulePopulations.size(), popSize);
        masterPopulation.setName(MASTER_NAME);
    }

    public NEATPopulation getModulePopulation(int moduleNum) {
        return modulePopulations.get(moduleNum);
    }

    public NEATPopulation getMasterPopulation() {
        return masterPopulation;
    }


}
