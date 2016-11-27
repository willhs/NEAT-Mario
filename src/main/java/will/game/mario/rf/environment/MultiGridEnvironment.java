package will.game.mario.rf.environment;

import ch.idsia.benchmark.mario.engine.generalization.Tile;
import ch.idsia.benchmark.mario.engine.input.MarioInput;
import ch.idsia.benchmark.mario.engine.input.MarioKey;
import ch.idsia.benchmark.mario.environments.IEnvironment;

import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

/**
 * Created by Will on 8/06/2016.
 */
public class MultiGridEnvironment implements GridEnvironment {

    @Override
    public double[] asInputNeurons(IEnvironment environment, MarioInput lastInput) {
        // a grid for each different type of tile
        List<List<Double>> grids = Arrays.stream(Tile.values())
                // only use most common tiles to reduce total nodes
                .filter(tile -> tile == Tile.BRICK
                            || tile == Tile.BREAKABLE_BRICK
                            || tile == Tile.QUESTION_BRICK
                            || tile == Tile.COIN_ANIM
                            || tile == Tile.NOTHING
    //                        || tile == Tile.SOMETHING
                )
                .map(tileType -> {
                    return Arrays.stream(environment.getTileField())
                            .flatMap(tileRow -> {
                                return Arrays.stream(tileRow)
                                        .map(tile -> tile == tileType ? 1 : 0 );
                            })
                            .mapToDouble(t->t)
                            .boxed()
                            .collect(Collectors.toList());
                }).collect(Collectors.toList());

        List<Double> inputNeurons = grids.stream().flatMap(grid -> {
            return grid.stream();
        }).collect(Collectors.toList());

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
