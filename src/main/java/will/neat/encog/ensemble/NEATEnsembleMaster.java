package will.neat.encog.ensemble;

import org.encog.neural.neat.NEATNetwork;

/**
 * Created by hardwiwill on 1/12/16.
 */
public class NEATEnsembleMaster extends NEATNetworkEnsemble {

    private NEATNetwork master;

    public NEATEnsembleMaster(NEATNetwork[] anns, NEATNetwork master) {
        super(anns);
        this.master = master;
    }

    public NEATNetwork getMaster() {
        return master;
    }
}
