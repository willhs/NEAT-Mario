package will.game.mario.rf.environment;

import ch.idsia.benchmark.mario.engine.input.MarioInput;
import ch.idsia.benchmark.mario.environments.IEnvironment;

import java.util.Arrays;
import java.util.stream.Stream;

/**
 * Created by Will on 15/05/2016.
 *
 * Represents environment as binary tiles - 0 if nothing, 1 if something
 */
public class BinaryEnvGridEnvironment extends AbstractGridEnvironment {

    @Override
    public double[] asInputNeurons(IEnvironment environment, MarioInput lastInput) {
        // convert 2d tiles to input neurons
        double[] environmentGrid = envGridToBinaryArray(environment.getTileField());

        // add the last action as a neurons
        double[] lastMarioActions = lastMarioActionsToArray(lastInput);

        return Stream.concat(
                        Arrays.stream(environmentGrid).boxed(),
                        Arrays.stream(lastMarioActions).boxed()
                )
                .mapToDouble(d->d)
                .toArray();
    }
}
