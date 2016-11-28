package will.game.mario.agent;

import ch.idsia.benchmark.mario.engine.input.MarioInput;
import will.game.mario.rf.action.ActionStratFactory;
import will.game.mario.rf.action.ActionStrategy;
import will.game.mario.rf.action.StandardHoldStrat;

/**
 * Created by hardwiwill on 25/11/16.
 */
public abstract class SimpleMarioNEATAgent extends MarioNEATAgent {

    public SimpleMarioNEATAgent() {}

    public SimpleMarioNEATAgent(ActionStratFactory factory) {
        super(factory);
    }

    @Override
    protected MarioInput processEnvironment(double[] environment) {
        // put tiles through the neural network to receive game inputs
        // 1 or 0 for each of the game inputs: [left,right,down,jump,speed/attack,up(useless)]
        double[] networkOutput = activateNetwork(environment);

        if (shouldPrint) {
            printNetwork(environment, networkOutput);
        }

        MarioInput action = mapNeuronsToAction(networkOutput);

        lastInput = action;

        return action;
    }


    protected abstract double[] activateNetwork(double[] inputs);
}
