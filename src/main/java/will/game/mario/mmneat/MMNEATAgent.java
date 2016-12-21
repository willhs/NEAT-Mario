package will.game.mario.mmneat;

import ch.idsia.benchmark.mario.engine.input.MarioInput;
import edu.utexas.cs.nn.evolution.genotypes.Genotype;
import edu.utexas.cs.nn.networks.Network;
import will.game.mario.agent.MarioAIBase2;
import will.game.mario.agent.MarioNEATAgent;
import will.game.mario.agent.SimpleMarioNEATAgent;

/**
 * Created by hardwiwill on 21/12/16.
 */
public class MMNEATAgent extends SimpleMarioNEATAgent {
    private Network network;

    public <T extends Network> MMNEATAgent(Genotype<T> individual) {
        this.network = individual.getPhenotype();
    }

    @Override
    protected double[] activateNetwork(double[] inputs) {
        return network.process(inputs);
    }
}
