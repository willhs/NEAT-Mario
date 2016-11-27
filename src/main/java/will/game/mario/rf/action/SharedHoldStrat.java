package will.game.mario.rf.action;

import ch.idsia.benchmark.mario.engine.input.MarioInput;
import ch.idsia.benchmark.mario.engine.input.MarioKey;
import will.util.Algorithms;

import java.util.Map;

/**
 * Created by Will on 28/08/2016.
 */
public class SharedHoldStrat extends AbstractHoldActionStrat {

    protected double pressThreshold = 0.5;

    @Override
    public MarioInput makeAction(double[] inputs, MarioInput currentAction, Map<MarioKey, Integer> keysHeld) {
        if (inputs.length != 5) {
            throw new IllegalArgumentException("this strategy takes 5 input nodes, was given " + inputs.length);
        }

        double time = inputs[4];
        // scale from between -1 and 1 because that is the range of the output nodes
        int pressFrames = (int) Algorithms.scaleToRange(
                time, -1, 1, MIN_HOLD_FOR, MAX_HOLD_FOR
        );

        for (int i = 0; i < marioKeys.length; i++) {
            double input = inputs[i];
            MarioKey key = marioKeys[i];

            // if key is already held
            if (keysHeld.containsKey(key) && keysHeld.get(key) >= 0) {
                continue;
            }

            if (input > pressThreshold) {
                keysHeld.put(key, pressFrames);
            }
        }

        // held keys should be pressed for this frame, otherwise released
        keysHeld.forEach((key, frames) -> {
            if (frames > 0) {
                action.press(key);
            } else {
                action.release(key);
            }
        });
        return action;
    }
}
