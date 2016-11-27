package will.game.mario.agent;

import ch.idsia.benchmark.mario.engine.input.MarioInput;
import org.encog.neural.neat.NEATNetwork;

/**
 * Created by hardwiwill on 25/11/16.
 */
public class EnsembleAgent extends MarioNEATEnsembleAgent {

    private NEATNetwork[] networks;

    public EnsembleAgent(NEATNetwork[] networks) {
        this.networks = networks;
    }

    @Override
    protected double[][] activateNetworks(double[] networkInput) {
        return null;
    }

    @Override
    protected MarioInput interpretNetworkOutputs(double[][] networkOutputs) {
        return null;
    }

}
