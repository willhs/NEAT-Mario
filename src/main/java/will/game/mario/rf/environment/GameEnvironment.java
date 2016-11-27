package will.game.mario.rf.environment;

import ch.idsia.benchmark.mario.engine.input.MarioInput;
import ch.idsia.benchmark.mario.environments.IEnvironment;

/**
 * Created by Will on 15/05/2016.
 *
 * A strategy for representing the environment
 */
public interface GameEnvironment {

    public double[] asInputNeurons(IEnvironment environment, MarioInput lastInput);
}
