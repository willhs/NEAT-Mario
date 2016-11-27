package will.game.mario.rf.environment;

import ch.idsia.benchmark.mario.engine.generalization.Tile;
import ch.idsia.benchmark.mario.engine.input.MarioInput;
import ch.idsia.benchmark.mario.engine.input.MarioKey;
import ch.idsia.benchmark.mario.environments.IEnvironment;

import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

/**
 * Created by Will on 15/05/2016.
 *
 * Represents environment as tiles - MIN_VAL if nothing, a unique integer for each other tile
 */
public class ValueGridEnvironment implements GridEnvironment {

    @Override
    public double[] asInputNeurons(IEnvironment environment, MarioInput lastInput) {
        double VAL_MULTIPLIER = 25;
        // convert 2d tiles to input neurons
        List<Double> inputNeurons = Arrays.stream(environment.getTileField())
                .flatMap(tileRow -> {
                    return Arrays.stream(tileRow)
                            .map(tile -> {
                                // represent unique tiles
                                return tile == Tile.NOTHING ? -1000 : tile.ordinal() * VAL_MULTIPLIER;
                            });
                })
                .mapToDouble(i->i)
                .boxed()
                .collect(Collectors.toList());

/*        // add entities
        inputNeurons.addAll(Arrays.stream(environment.getEntityField())
                .flatMap(entityRow -> {
                    return Arrays.stream(entityRow)
                            .map(entities -> {
                                // -1 if tile has no entities, otherwise unique number for each type
                                return entities.isEmpty() ||
                                        entities.stream().allMatch(entity -> entity.type == EntityType.NOTHING)
                                        ? -1 : entities.get(0).type.ordinal();
                            });
                })
                .mapToDouble(i->i)
                .boxed()
                .collect(Collectors.toList())
        );*/
        // add the last action as a neurons
        // one input for each different key
        MarioKey.getKeys().forEach(key -> {
            int pressed  = lastInput.getPressed().contains(key) ? 1 : 0;
            inputNeurons.add((double)pressed);
        });

        return inputNeurons.stream()
                .mapToDouble(d -> d)
                .toArray();
    }
}

