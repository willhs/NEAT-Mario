package will.game.mario.rf.environment;

import ch.idsia.benchmark.mario.engine.generalization.Entity;
import ch.idsia.benchmark.mario.engine.generalization.EntityType;
import ch.idsia.benchmark.mario.engine.generalization.Tile;
import ch.idsia.benchmark.mario.engine.input.MarioInput;
import ch.idsia.benchmark.mario.engine.input.MarioKey;

import java.util.Arrays;
import java.util.List;
import java.util.stream.DoubleStream;

/**
 * Created by Will on 16/07/2016.
 */
abstract class AbstractGridEnvironment implements GridEnvironment {
    double[] envGridToBinaryArray(Tile[][] grid) {
        // convert 2d tiles to input neurons
        return Arrays.stream(grid)
                .flatMap(tileRow -> {
                    return Arrays.stream(tileRow)
                            .map(tile -> {
                                // 0 if nothing in tile or 1 if something
                                return tile == Tile.NOTHING ? 0 : 1;
                            });
                })
                .mapToDouble(i->i)
                .toArray();
    }

    double[] entityGridToBinaryArray(List<Entity>[][] entityGrid) {
        // add entities
        return Arrays.stream(entityGrid)
                .flatMap(entityRow -> {
                    return Arrays.stream(entityRow)
                            .map(entities -> {
                                // 0 if tile has no entities, 1 if it does
                                return entities.isEmpty() ||
                                        entities.stream().allMatch( entity -> entity.type == EntityType.NOTHING)
                                        ? 0 : 1;
                            });
                })
                .mapToDouble(i->i)
                .toArray();
    }

    double[] lastMarioActionsToArray(MarioInput lastActions) {
        // one input for each different key
        return MarioKey.getKeys().stream()
                .map(key -> lastActions.getPressed().contains(key) ? 1 : 0 )
                .mapToDouble(d->d)
                .toArray();
    }

    double[] entityGridToBinaryArray(List<Entity>[][] entityGrid, EntityType type) {
        return Arrays.stream(entityGrid)
                .flatMap(entityRow -> {
                    return Arrays.stream(entityRow)
                            .map(entities -> {
                                // number of entities of type 'type'
                                return entities.stream().filter(e -> e.type == type).count();
                            });
                })
                .mapToDouble(i->i)
                .toArray();
    }

    // utility method
    double[] concatArrays(double[] a, double[] b) {
        return DoubleStream.concat(Arrays.stream(a), Arrays.stream(b))
                .toArray();
    }
}
