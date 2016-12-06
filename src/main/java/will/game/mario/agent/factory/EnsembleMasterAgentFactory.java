package will.game.mario.agent.factory;

import will.neat.encog.ensemble.NEATEnsembleMaster;

/**
 * Created by hardwiwill on 1/12/16.
 */
public interface EnsembleMasterAgentFactory {
    EnsembleMasterAgent create(NEATEnsembleMaster ensemble);
}
