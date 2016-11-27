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
public class PhasedSearch implements Strategy {

    private TrainEA train;
    private double phaseALength;
    private double phaseBLength;
    private int phase = 1;
    private OperationList[] phaseOps = new OperationList[2];
    private int phases = 2;

    private PhasedSearch() {
        phaseOps[0] = new OperationList();
        phaseOps[1] = new OperationList();
    }

    public PhasedSearch(int phaseLength) {
        this();
        this.phaseALength = phaseLength;
        this.phaseBLength = phaseLength;
    }

    public PhasedSearch(int phaseALength, int phaseBLength) {
        this();
        this.phaseALength = phaseALength;
        this.phaseBLength = phaseBLength;
    }

    public void addPhaseOp(int phase, double prob, EvolutionaryOperator op) {
        phaseOps[phase].add(prob, op);
        op.init(train);
    }
    @Override
    public void init(MLTrain train) {
        this.train = (TrainEA) train;
    }

    @Override
    public void preIteration() {
        // switch if the right num of generations has gone by
        if (phase == 0 && train.getIteration() % phaseALength == 0
                || phase == 1 && train.getIteration() % phaseBLength == 0) {
            switchPhase();
        }
    }

    @Override
    public void postIteration() { }

    private void switchPhase() {
        int lastPhase = phase;
        phase = (phase+1) % phases;
        int newPhase = phase;

        implementPhase(lastPhase, newPhase);

//        System.out.println("Phase changed to : " + phase);
//        System.out.println("operators: " + train.getOperators().getList());
    }

    private void implementPhase(int lastPhase, int newPhase) {
        // remove all ops in last phase
        train.getOperators().getList().removeIf(objectHolder ->
                phaseOps[lastPhase].getList().stream().anyMatch(phaseOp ->
                        phaseOp.getObj().getClass() == objectHolder.getObj().getClass()
                )
        );

        // add ops of the current phase
        train.getOperators().getList().addAll(phaseOps[newPhase].getList());

        // finalize (make probabilities add to 1)
        train.getOperators().finalizeStructure();
    }
}
