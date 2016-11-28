package will.game.mario.agent;

import ch.idsia.benchmark.mario.engine.input.MarioInput;
import will.game.mario.agent.MarioNEATAgent;

/**
 * Created by hardwiwill on 25/11/16.
 */
public abstract class MarioNEATEnsembleAgent extends MarioNEATAgent {

    @Override
    protected MarioInput processEnvironment(double[] environment) {
        double[][] networkOutput = activateNetworks(environment);

        MarioInput action = interpretNetworkOutputs(networkOutput);

        lastInput = action;

        return action;
    }

    protected abstract double[][] activateNetworks(double[] networkInput);

    protected abstract MarioInput interpretNetworkOutputs(double[][] networkOutputs);
}
