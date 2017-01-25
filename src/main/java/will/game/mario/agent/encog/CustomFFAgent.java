package will.game.mario.agent.encog;

import ch.idsia.tools.EvaluationInfo;
import org.encog.neural.neat.NEATNetwork;

/**
 * Created by Will on 26/01/2017.
 */
public class CustomFFAgent extends EncogAgent {
    public CustomFFAgent(NEATNetwork network) {
        super(network);
    }

    @Override
    public int fitness(EvaluationInfo info) {
        return info.levelLength - info.distancePassedCells;
    }
}
