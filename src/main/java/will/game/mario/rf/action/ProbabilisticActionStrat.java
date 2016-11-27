package will.game.mario.rf.action;

import ch.idsia.benchmark.mario.engine.input.MarioInput;
import ch.idsia.benchmark.mario.engine.input.MarioKey;
import will.util.Algorithms;

import java.util.Map;

/**
 * Created by Will on 14/07/2016.
 */
public class ProbabilisticActionStrat extends AbstractActionStrat {

    private double minChanceToPress = 0.8;

    @Override
    public MarioInput makeAction(double[] inputs, MarioInput currentAction, Map<MarioKey, Integer> keysHeld) {
        for (int i = 0; i < marioKeys.length; i++ ){
            double outputVal = inputs[i];
            if (outputVal > pressThreshold) {
                double chanceToPress = Algorithms.scaleToRange(
                        outputVal, pressThreshold, 1, minChanceToPress, 1
                );

                if (Math.random() < chanceToPress) {
                    action.press(marioKeys[i]);
                } else {
                }
            }
        }
        return action;
     }
}
