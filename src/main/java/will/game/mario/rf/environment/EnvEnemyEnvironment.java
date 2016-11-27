package will.game.mario.rf.environment;

import ch.idsia.benchmark.mario.engine.generalization.EntityType;
import ch.idsia.benchmark.mario.engine.input.MarioInput;
import ch.idsia.benchmark.mario.environments.IEnvironment;

/**
 * Created by Will on 16/07/2016.
 */
public class EnvEnemyEnvironment extends AbstractGridEnvironment {

    EntityType[] types;

    public EnvEnemyEnvironment(EntityType... types) {
        this.types = types;
    }

    @Override
    public double[] asInputNeurons(IEnvironment environment, MarioInput lastInput) {
        double[] envGrid = envGridToBinaryArray(environment.getTileField());
        double[] lastActions = lastMarioActionsToArray(lastInput);

        // todo: use Stream.reduce
        double[] enemyGrids = new double[0];

        for (EntityType type : types) {
            double[] enemyGrid = entityGridToBinaryArray(environment.getEntityField(), type);
            enemyGrids = concatArrays(enemyGrids, enemyGrid);
        }

        return concatArrays(
                envGrid,
                concatArrays(enemyGrids, lastActions)
        );
    }
}
