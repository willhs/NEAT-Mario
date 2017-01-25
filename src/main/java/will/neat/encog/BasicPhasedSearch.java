package will.neat.encog;

import org.encog.ml.ea.opp.EvolutionaryOperator;
import org.encog.ml.ea.opp.OperationList;
import org.encog.ml.ea.train.basic.TrainEA;
import org.encog.ml.train.MLTrain;
import org.encog.ml.train.strategy.Strategy;

/**
 * Created by Will on 20/09/2016.
 * does 2 phases currently
 *
 */
public class BasicPhasedSearch extends AbstractPhasedSearch {

    private int phaseALength;
    private int phaseBLength;

    public BasicPhasedSearch(int phaseLength) {
        this.phaseALength = phaseLength;
        this.phaseBLength = phaseLength;
    }

    public BasicPhasedSearch(int phaseALength, int phaseBLength) {
        this.phaseALength = phaseALength;
        this.phaseBLength = phaseBLength;
    }

    @Override
    public void preIteration() {
        // don't switch if it's the first gen
        if (train.getIteration() == 0) {
            return;
        }
        // switch if the right num of generations has gone by
        if (train.getIteration() % phaseBLength == 0) {
            switchPhase();
        }

    }
}
