package will.game.mario.fitness;

import org.encog.ml.MLMethod;
import org.encog.ml.data.MLData;
import org.encog.neural.neat.NEATCODEC;
import org.encog.neural.neat.NEATNetwork;
import org.encog.neural.neat.NEATPopulation;
import will.game.mario.agent.encog.EnsembleAgent;
import will.game.mario.agent.factory.AgentFactory;
import will.game.mario.agent.factory.EnsembleAgentFactory;
import will.neat.encog.ensemble.NEATEnsembleMaster;

import java.util.Arrays;
import java.util.logging.Logger;

/**
 * Created by hardwiwill on 5/12/16.
 */
public class MultiPopMasterFF extends EnsembleMarioFF {
    private NEATPopulation[] modulePops;

    private static Logger logger = Logger.getLogger(MultiPopMasterFF.class
            .getSimpleName());

    public MultiPopMasterFF(String marioOptions, boolean headless, EnsembleAgentFactory agentFactory) {
        super(marioOptions, headless, agentFactory);
    }

    public void setModulePops(NEATPopulation[] modulePops) {
        this.modulePops = modulePops;
    }

    @Override
    public double calculateScore(MLMethod mlMethod) {
        NEATNetwork master = (NEATNetwork) mlMethod;

        NEATCODEC codec = new NEATCODEC();

        NEATNetwork[] modules = Arrays.stream(modulePops)
                .map(pop -> codec.decode(pop.getBestGenome()))
                .toArray(s -> new NEATNetwork[s]);


        NEATEnsembleMaster ensemble = new NEATEnsembleMaster(modules, master);

        return super.calculateScore(ensemble);
    }

}
