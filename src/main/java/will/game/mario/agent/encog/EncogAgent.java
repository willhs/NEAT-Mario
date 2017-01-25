package will.game.mario.agent.encog;

import ch.idsia.tools.EvaluationInfo;
import org.encog.ml.data.MLData;
import org.encog.ml.data.basic.BasicMLData;
import org.encog.ml.data.versatile.normalizers.PassThroughNormalizer;
import org.encog.neural.neat.NEATNetwork;
import sun.security.krb5.internal.EncAPRepPart;
import will.game.mario.agent.SimpleMarioNEATAgent;
import will.game.mario.rf.action.ActionStratFactory;
import will.game.mario.rf.environment.GameEnvironment;

/**
 * Created by Will on 17/07/2016.
 */
public class EncogAgent extends SimpleMarioNEATAgent {

    protected NEATNetwork network;
    protected FitnessFunction ff;

    public EncogAgent(NEATNetwork network) {
        this.network = network;
    }

    public EncogAgent(NEATNetwork network, ActionStratFactory actionStratFactory) {
        super(actionStratFactory);
        this.network = network;
    }

    public EncogAgent(NEATNetwork network, ActionStratFactory stratFactory, GameEnvironment env) {
        this(network, stratFactory);
        this.env = env;
    }

    public EncogAgent(NEATNetwork nn, ActionStratFactory factory, GameEnvironment env, FitnessFunction ff) {
        this(nn, factory, env);
        this.ff = ff;
    }

    public EncogAgent(NEATNetwork nn, ActionStratFactory factory, FitnessFunction ff) {
        this(nn, factory);
        this.ff = ff;
    }

    @Override
    protected double[] activateNetwork(double[] inputs) {
        MLData data = new BasicMLData(inputs.length);
        data.setData(inputs);

        return network.compute(data).getData();
    }

    @Override
    public int fitness(EvaluationInfo info) {
        if (ff == null) return super.fitness(info);
        else return ff.fitness(info);
    }

    public interface FitnessFunction {
        int fitness(EvaluationInfo info);
    }
}
