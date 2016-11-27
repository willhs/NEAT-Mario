package will.game.mario.rf.action;

import ch.idsia.benchmark.mario.engine.input.MarioInput;
import ch.idsia.benchmark.mario.engine.input.MarioKey;

import java.util.Map;

/**
 * Created by Will on 14/07/2016.
 */
public class StandardActionStrat extends AbstractActionStrat {
    @Override
    public MarioInput makeAction(double[] inputs, MarioInput currentAction, Map<MarioKey, Integer> keysHeld) {
        for (int i = 0; i < marioKeys.length; i++) {
            double val = inputs[i];
            if (val > pressThreshold) {
                action.press(marioKeys[i]);
            }
        }
        return action;
    }
}
