package will.game.mario.agent.encog;

import ch.idsia.benchmark.mario.engine.input.MarioInput;
import org.encog.neural.neat.NEATNetwork;
import will.game.mario.agent.MarioNEATEnsembleAgent;

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
        double[] totals = new double[networkOutputs[0].length];

        for (int nn = 0; nn < networkOutputs.length; nn++) {
            for (int output = 0; output < networkOutputs[nn].length; output++) {
                totals[output] += networkOutputs[nn][output];
            }
        }

        double[] averages = new double[networkOutputs[0].length];
        for (int n = 0; n < networkOutputs.length; n++) {
            averages[n] = totals[n] / networkOutputs.length;
        }

        return mapNeuronsToAction(averages);
    }

}
