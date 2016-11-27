package will.game.mario.rf.action;

import ch.idsia.benchmark.mario.engine.input.MarioInput;
import ch.idsia.benchmark.mario.engine.input.MarioKey;
import will.util.Algorithms;

import java.util.HashMap;
import java.util.Map;

/**
 * Created by Will on 14/07/2016.
 */
public class StandardHoldStrat extends AbstractHoldActionStrat {

    private Map<MarioKey, Integer> toHold = new HashMap<>();


    private boolean enableCancellation = false;
    protected double cancelThreshold = -0.5;

    public StandardHoldStrat() {}

    public StandardHoldStrat(boolean enableCancel) {
        this.enableCancellation = enableCancel;
    }

    @Override
    public MarioInput makeAction(double[] inputs, MarioInput currentAction, Map<MarioKey, Integer> keysHeld) {
        if (inputs.length != 4) {
            throw new IllegalArgumentException("this strategy takes 4 input nodes, was given " + inputs.length);
        }

        for (int i = 0; i < marioKeys.length; i++) {
            double input = inputs[i];
            MarioKey key = marioKeys[i];

            // if key is already held
            if (keysHeld.containsKey(key) && keysHeld.get(key) >= 0) {
                if (enableCancellation && input < cancelThreshold) {
                    keysHeld.put(key, 0);
                } else continue;
            }

            if (input > pressThreshold) {
                int holdFor = (int) Algorithms.scaleToRange(
                        input, pressThreshold, 1, MIN_HOLD_FOR, MAX_HOLD_FOR
                );
                toHold.put(key, holdFor);
            }
        }

        // update keysHeld
        toHold.keySet().forEach(key -> {
            keysHeld.put(key, toHold.get(key));
        });

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
