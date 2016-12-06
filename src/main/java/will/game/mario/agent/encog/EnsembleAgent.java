package will.game.mario.agent.encog;

import ch.idsia.benchmark.mario.engine.input.MarioInput;
import org.encog.ml.data.basic.BasicMLData;
import org.encog.neural.neat.NEATNetwork;
import will.game.mario.agent.MarioNEATEnsembleAgent;
import will.game.mario.rf.action.ActionStratFactory;
import will.neat.encog.ensemble.NEATNetworkEnsemble;

import java.util.Arrays;

/**
 * Created by hardwiwill on 25/11/16.
 */
public class EnsembleAgent extends MarioNEATEnsembleAgent {

    private NEATNetworkEnsemble ensemble;

    public EnsembleAgent(NEATNetworkEnsemble ensemble) {
        this.ensemble = ensemble;
    }

    public EnsembleAgent(NEATNetworkEnsemble ensemble, ActionStratFactory stratFactory) {
        super(stratFactory);
        this.ensemble = ensemble;
    }


    @Override
    protected double[][] activateNetworks(double[] networkInput) {
        double[][] result = new double[ensemble.getAnns().length][];
        for (int i = 0; i < ensemble.getAnns().length; i++) {
            result[i] = ensemble.getAnns()[i].compute(new BasicMLData(networkInput)).getData();
        }
        return result;
    }

    @Override
    protected MarioInput interpretNetworkOutputs(double[][] networkOutputs) {
        return average(networkOutputs);
    }

    /**
     * Finds an average of network outputs and uses them for StandardHoldActionStrat
     * @param networkOutputs
     * @return
     */
    protected MarioInput average(double[][] networkOutputs) {
        double[] totals = new double[networkOutputs[0].length];

        for (int nn = 0; nn < networkOutputs.length; nn++) {
            for (int output = 0; output < networkOutputs[nn].length; output++) {
                totals[output] += networkOutputs[nn][output];
            }
        }

        double[] averages = new double[networkOutputs[0].length];
        for (int n = 0; n < averages.length; n++) {
            averages[n] = totals[n] / networkOutputs.length;
        }

        return mapNeuronsToAction(averages);
    }

    protected NEATNetworkEnsemble getEnsemble() {
        return ensemble;
    }
}
