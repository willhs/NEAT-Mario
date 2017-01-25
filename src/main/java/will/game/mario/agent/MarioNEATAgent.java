package will.game.mario.agent;

import ch.idsia.agents.AgentOptions;
import ch.idsia.benchmark.mario.engine.input.MarioInput;
import ch.idsia.benchmark.mario.engine.input.MarioKey;
import will.game.mario.rf.action.ActionStratFactory;
import will.game.mario.rf.action.ActionStrategy;
import will.game.mario.rf.action.StandardHoldStrat;
import will.game.mario.rf.environment.EnvEnemyGrid;
import will.game.mario.rf.environment.GameEnvironment;

import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;

/**
 * Created by Will on 29/06/2016.
 */
public abstract class MarioNEATAgent extends MarioAIBase2 {

    // how many frames should each key be held for
    protected Map<MarioKey, Integer> keysHeld = new HashMap<>();
    protected boolean shouldPrint = false;

    private final ActionStratFactory DEFAULT_ACTION_STRAT_FACTORY = () -> new StandardHoldStrat();
    private ActionStratFactory actionStratFactory = DEFAULT_ACTION_STRAT_FACTORY;
    protected GameEnvironment env = new EnvEnemyGrid();

    public MarioNEATAgent(){}

    public MarioNEATAgent(boolean shouldPrint) {
        this.shouldPrint = shouldPrint;
    }

    public MarioNEATAgent(ActionStratFactory stratFactory) {
        this.actionStratFactory = stratFactory;
    }

    @Override
    public MarioInput actionSelection() {
        return actionSelection(env);
    }

    protected MarioInput actionSelection(GameEnvironment env) {
        updateActionsHeld();
        double[] environment = env.asInputNeurons(this.environment, lastInput);

        return processEnvironment(environment);
    }

    protected void printNetwork(double[] environment, double[] networkOutput) {
        // print environment grid
        System.out.println("-----------------------------------------------------------");
        int gridLength = 13;
        for (int r = 0; r < gridLength; r++) {
            double[] col = Arrays.copyOfRange(environment, r * gridLength, (r + 1) * gridLength);
            System.out.println(Arrays.toString(col));
        }
        System.out.println("-----------------------------------------------------------");
        System.out.println("Network output: " + Arrays.toString(networkOutput));
    }

    protected abstract MarioInput processEnvironment(double[] networkInput);

    protected void updateActionsHeld() {
        keysHeld.forEach((key, frames) -> keysHeld.put(key, frames - 1));
    }

    @Override
    public void reset(AgentOptions options) {
        super.reset(options);
        keysHeld = new HashMap<>();
    }

    public void shouldPrint(boolean shouldPrint) {
        this.shouldPrint = shouldPrint;
    }

    protected MarioInput mapNeuronsToAction(double[] outputNeurons) {
        ActionStrategy actionStrat = actionStratFactory.create();
        MarioInput action = actionStrat.makeAction(outputNeurons, lastInput, keysHeld);

        return action;
    }

}
