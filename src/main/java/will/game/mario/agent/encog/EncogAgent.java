package will.game.mario.agent.encog;

import org.encog.ml.data.MLData;
import org.encog.ml.data.basic.BasicMLData;
import org.encog.neural.neat.NEATNetwork;
import will.game.mario.agent.SimpleMarioNEATAgent;
import will.game.mario.rf.action.ActionStratFactory;

/**
 * Created by Will on 17/07/2016.
 */
public class EncogAgent extends SimpleMarioNEATAgent {

    protected NEATNetwork network;

    public EncogAgent(NEATNetwork network) {
        this.network = network;
    }

    public EncogAgent(NEATNetwork network, ActionStratFactory actionStratFactory) {
        super(actionStratFactory);
        this.network = network;
    }

    @Override
    protected double[] activateNetwork(double[] inputs) {
        MLData data = new BasicMLData(inputs.length);
        data.setData(inputs);

        return network.compute(data).getData();
    }
}
