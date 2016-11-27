package will.game.mario.agent.encog;

import org.encog.neural.neat.NEATNetwork;
import will.game.mario.agent.MarioNEATAgent;

/**
 * Created by Will on 8/10/2016.
 */
public interface AgentFactory {
    MarioNEATAgent create(NEATNetwork nn);
}
