package will.game.mario.experiment.evolve;

import will.game.mario.agent.encog.EncogAgent;
import will.game.mario.agent.factory.AgentFactory;
import will.game.mario.params.NEATParameters;
import will.game.mario.rf.action.ActionStratFactory;

/**
 * Created by Will on 26/01/2017.
 */
public class CustomFFEvolver extends NEATMarioEvolver {

    private EncogAgent.FitnessFunction ff;

    public CustomFFEvolver(NEATParameters params, ActionStratFactory actionStratFactory,
                           StringBuilder output, String name, EncogAgent.FitnessFunction ff) {
        super(params, actionStratFactory, output, name);
        this.ff = ff;
    }

    @Override
    protected AgentFactory setupAgent(ActionStratFactory stratFactory) {
        return (nn) -> new EncogAgent(nn, stratFactory, ff);
    }
}
