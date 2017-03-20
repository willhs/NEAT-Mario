package will.game.mario.fitness;

import org.encog.ml.CalculateScore;
import org.encog.ml.MLMethod;
import org.encog.neural.neat.NEATNetwork;
import will.game.mario.agent.MarioNEATAgent;
import will.game.mario.agent.factory.AgentFactory;
import will.game.mario.agent.encog.EncogAgent;

import java.util.Arrays;
import java.util.logging.Logger;

/**
 * Created by Will on 17/07/2016.
 */
public class EncogMarioFitnessFunction extends AbstractMarioFitnessFunction<NEATNetwork> implements CalculateScore {

    private static Logger logger = Logger.getLogger(EncogMarioFitnessFunction.class
            .getSimpleName());

    private double total = 0;
    private int runs = 0;

    private final AgentFactory DEFAULT_AGENT_SUPPLIER = network -> new EncogAgent(network);
    private AgentFactory agentFactory = DEFAULT_AGENT_SUPPLIER;

    public EncogMarioFitnessFunction(){}

    public EncogMarioFitnessFunction(String marioOptions, boolean headless, AgentFactory agentFactory) {
        super(marioOptions, headless);
        this.agentFactory = agentFactory;
    }

    public EncogMarioFitnessFunction(String marioOptions, boolean headless, AgentFactory agentFactory, int seed, int trials) {
        super(marioOptions, headless, seed, trials);
        this.agentFactory = agentFactory;
    }

    public EncogMarioFitnessFunction(boolean headless) {
        super(headless);
    }


    @Override
    public double calculateScore(MLMethod mlMethod) {
        NEATNetwork nn = (NEATNetwork) mlMethod;
//        System.out.println(nn == null);

        MarioNEATAgent agent = agentFactory.create(nn);

        double score = evaluate(agent, nn, logger);
        runs++;
        total += score;
        return score;
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

    @Override
    protected void logRun(Logger logger, double fitness, NEATNetwork nn) {
        super.logRun(logger, fitness, nn);

        int numConnections = nn.getLinks().length;

        String weights = Arrays.toString(
                Arrays.stream(nn.getLinks())
                        .mapToDouble(l -> l.getWeight())
                        .toArray()
        );

        logger.info("connections: " + numConnections);
//        logger.info("connection weights: " + weights);
//        logger.info("connections: " + Arrays.toString(nn.getLinks()));
    }

    @Override
    public boolean shouldMinimize() {
        return false;
    }

    @Override
    public boolean requireSingleThreaded() {
        return true;
    }

}
