package will.game.mario.agent.factory;

import org.encog.ml.data.basic.BasicMLData;
import will.game.mario.agent.MarioNEATEnsembleAgent;
import will.game.mario.agent.encog.EnsembleAgent;
import will.neat.encog.ensemble.NEATEnsembleMaster;
import will.neat.encog.ensemble.NEATNetworkEnsemble;

import java.util.Comparator;
import java.util.stream.IntStream;

/**
 * Created by hardwiwill on 1/12/16.
 */
public class EnsembleMasterAgent extends EnsembleAgent {

    public EnsembleMasterAgent(NEATEnsembleMaster ensemble) {
        super(ensemble);
    }

    public double[][] activateNetworks(double[] networkInput) {
        NEATEnsembleMaster ensemble = (NEATEnsembleMaster) getEnsemble();

        double[] output = chooseBestModule(networkInput, ensemble);

        return new double[][] { output };
    }

    // computes the result of the most favourable module
    private double[] chooseBestModule(double[] networkInput, NEATEnsembleMaster ensemble) {
        double[] masterOutput = ensemble.getMaster()
                .compute(new BasicMLData(networkInput))
                .getData();

        int moduleIndex = IntStream.range(0, masterOutput.length)
                .boxed().max((a,b) -> Double.compare(masterOutput[a],masterOutput[b]))
                .get();

//        System.out.print(moduleIndex + ",");
        if (moduleIndex == 3) {
            System.out.println("?");
        }

        double[] moduleOutput = ensemble.getAnns()[moduleIndex]
                .compute(new BasicMLData(networkInput))
                .getData();

        return moduleOutput;
    }
}
