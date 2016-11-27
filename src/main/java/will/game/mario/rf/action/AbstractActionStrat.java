package will.game.mario.rf.action;

import ch.idsia.benchmark.mario.engine.input.MarioInput;
import ch.idsia.benchmark.mario.engine.input.MarioKey;

/**
 * Created by Will on 14/07/2016.
 */
public abstract class AbstractActionStrat implements ActionStrategy{

    protected MarioKey[] marioKeys = new MarioKey[]{
            MarioKey.LEFT,
            MarioKey.RIGHT,
            MarioKey.JUMP,
            MarioKey.SPEED
    };

    protected double pressThreshold = 0.00;

    protected MarioInput action = new MarioInput();
}
