package will.game.mario.fitness;

import org.encog.ml.MLMethod;
import org.encog.ml.ea.genome.Genome;
import org.encog.neural.neat.NEATCODEC;
import org.encog.neural.neat.NEATNetwork;
import org.encog.neural.neat.NEATPopulation;
import will.game.mario.agent.factory.EnsembleAgentFactory;
import will.neat.encog.ensemble.NEATEnsembleMaster;

import java.util.Arrays;
import java.util.Optional;

/**
 * Created by hardwiwill on 5/12/16.
 */
public class MultiPopModuleFF extends EnsembleMarioFF {

    private NEATPopulation[] modulePops;
    private NEATPopulation masterPop;

    private int module;

    public MultiPopModuleFF(String marioOptions, boolean headless, EnsembleAgentFactory agentFactory, int module) {
        super(marioOptions, headless, agentFactory);
        this.module = module;
    }

    public void setModuleAndMasterPops(NEATPopulation[] modulePops, NEATPopulation masterPop) {
        this.modulePops = modulePops;
        this.masterPop = masterPop;
    }

    @Override
    public double calculateScore(MLMethod mlMethod) {
        NEATNetwork thisModule = (NEATNetwork) mlMethod;

        NEATCODEC codec = new NEATCODEC();

        // use best individuals from each module population
        NEATNetwork[] modules = Arrays.stream(modulePops)
                .map(pop -> getBestIndividual(pop, codec))
                .toArray(s -> new NEATNetwork[s]);

        // replace best genome with this genome (for the module that this ff is for)
        modules[module] = thisModule;

        NEATNetwork master = getBestIndividual(masterPop, codec);

        NEATEnsembleMaster ensemble = new NEATEnsembleMaster(modules, master);

        return super.calculateScore(ensemble);
    }

    private NEATNetwork getBestIndividual(NEATPopulation pop, NEATCODEC codec) {
        Genome best = Optional.ofNullable(pop.getBestGenome())
                .orElse(pop.getSpecies().get(0).getLeader());
        return (NEATNetwork) codec.decode(best);
    }
}
