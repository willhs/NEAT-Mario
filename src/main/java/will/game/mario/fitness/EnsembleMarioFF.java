package will.game.mario.fitness;

import org.encog.ml.CalculateScore;
import org.encog.ml.MLMethod;
import will.game.mario.agent.MarioNEATAgent;
import will.game.mario.agent.encog.EnsembleAgent;
import will.game.mario.agent.factory.EnsembleAgentFactory;
import will.neat.encog.ensemble.NEATNetworkEnsemble;

import java.util.logging.Logger;

/**
 * Created by hardwiwill on 29/11/16.
 */
public class EnsembleMarioFF extends AbstractMarioFitnessFunction<NEATNetworkEnsemble> implements CalculateScore{

    private static Logger logger = Logger.getLogger(EnsembleMarioFF.class
            .getSimpleName());

    private final EnsembleAgentFactory DEFAULT_AGENT_SUPPLIER = network -> new EnsembleAgent(network);
    private EnsembleAgentFactory agentFactory = DEFAULT_AGENT_SUPPLIER;

    private double total = 0;
    private int runs = 0;

    public EnsembleMarioFF(String marioOptions, boolean headless, EnsembleAgentFactory agentFactory) {
        super(marioOptions, headless);
        this.agentFactory = agentFactory;
    }

    public EnsembleMarioFF(boolean headless) {
        super(headless);
    }


    @Override
    public double calculateScore(MLMethod mlMethod) {
        NEATNetworkEnsemble nn = (NEATNetworkEnsemble) mlMethod;
//        System.out.println(nn == null);

        MarioNEATAgent agent = agentFactory.create(nn);

        double score = evaluate(agent, nn, logger);
        runs++;
        total += score;
        return score;
    }

    @Override
    public boolean shouldMinimize() {
        return false;
    }

    @Override
    public boolean requireSingleThreaded() {
        return false;
    }

    @Override
    protected boolean shouldPlayBack(double fitness) {
        boolean should = super.shouldPlayBack(fitness)
                && fitness > (total/runs) * 2
//                && Math.random() < 0.02;
                ;
        if (should) logger.info("playing run with score: " + fitness);
        return should;
    }

    protected EnsembleAgentFactory getAgentFactory() {
        return agentFactory;
    }
}
