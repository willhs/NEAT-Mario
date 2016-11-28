package will.neat.encog.ensemble;

import org.encog.ml.MLMethod;
import org.encog.neural.neat.NEATNetwork;

/**
 * Created by hardwiwill on 28/11/16.
 */
public class NEATNetworkEnsemble implements MLMethod {
    private NEATNetwork[] anns;

    public NEATNetworkEnsemble(NEATNetwork[] anns) {
        this.anns = anns;
    }

    public NEATNetwork[] getAnns() { return anns; }
}
