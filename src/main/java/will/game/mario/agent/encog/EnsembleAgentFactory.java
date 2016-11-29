package will.game.mario.agent.encog;

import will.neat.encog.ensemble.NEATNetworkEnsemble;

/**
 * Created by hardwiwill on 29/11/16.
 */
public interface EnsembleAgentFactory {
    EnsembleAgent create(NEATNetworkEnsemble nn);
}
