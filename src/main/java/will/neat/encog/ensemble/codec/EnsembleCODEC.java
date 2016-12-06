package will.neat.encog.ensemble.codec;

import org.encog.ml.MLMethod;
import org.encog.ml.ea.codec.GeneticCODEC;
import org.encog.ml.ea.genome.Genome;
import org.encog.ml.genetic.GeneticError;
import org.encog.neural.neat.NEATCODEC;
import org.encog.neural.neat.NEATNetwork;
import will.neat.encog.ensemble.NEATNetworkEnsemble;
import will.neat.encog.ensemble.genome.NEATEnsembleGenome;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 * Created by hardwiwill on 28/11/16.
 */
public class EnsembleCODEC implements GeneticCODEC {


    @Override
    public MLMethod decode(Genome genome) {
        NEATEnsembleGenome ensembleGenome = (NEATEnsembleGenome) genome;

        NEATCODEC neatCODEC = new NEATCODEC();

        List<NEATNetwork> anns = new ArrayList<>();

        Arrays.stream(ensembleGenome.getAnns())
                .forEach(ann -> anns.add((NEATNetwork)neatCODEC.decode(ann)));

        return new NEATNetworkEnsemble(anns.toArray(new NEATNetwork[anns.size()]));
    }

    @Override
    public Genome encode(MLMethod phenotype) {
        throw new GeneticError(
                "Encoding of a NEAT network is not supported.");
    }
}
