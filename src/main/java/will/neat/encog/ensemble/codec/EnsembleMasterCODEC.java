package will.neat.encog.ensemble.codec;

import org.encog.ml.MLMethod;
import org.encog.ml.ea.genome.Genome;
import org.encog.neural.neat.NEATCODEC;
import org.encog.neural.neat.NEATNetwork;
import org.encog.neural.neat.training.SingleNEATGenome;
import will.neat.encog.ensemble.NEATEnsembleMaster;
import will.neat.encog.ensemble.genome.EnsembleMasterGenome;

import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

/**
 * Created by hardwiwill on 1/12/16.
 */
public class EnsembleMasterCODEC extends EnsembleCODEC {

    @Override
    public MLMethod decode(Genome genome) {

        EnsembleMasterGenome ensembleGenome = (EnsembleMasterGenome) genome;

        NEATCODEC neatCODEC = new NEATCODEC();

        List<NEATNetwork> modules = Arrays.stream(ensembleGenome.getAnns())
                .map(ann -> ((NEATNetwork)neatCODEC.decode(ann)))
                .collect(Collectors.toList());

        SingleNEATGenome master = ensembleGenome.getMaster();
        NEATNetwork masterNetwork = (NEATNetwork) neatCODEC.decode(master);

        return new NEATEnsembleMaster(modules.toArray(new NEATNetwork[modules.size()]), masterNetwork);
    }
}
