package will.neat.encog;

import org.encog.ml.ea.opp.EvolutionaryOperator;
import org.encog.ml.ea.opp.OperationList;
import org.encog.ml.ea.train.basic.TrainEA;
import org.encog.ml.train.MLTrain;
import org.encog.ml.train.strategy.Strategy;

/**
 * Created by hardwiwill on 25/01/17.
 */
public abstract class AbstractPhasedSearch implements Strategy {

    protected TrainEA train;
    protected enum Phase { COMPLEXIFICATION, SIMPLIFICATION }
    protected Phase phase = Phase.COMPLEXIFICATION;

    // last generation switch
    protected int lastTransitionGeneration = 0;

    // operations that are phase specific (additive/substractive mutations)
    protected OperationList[] phaseOps = new OperationList[2];

    protected AbstractPhasedSearch() {
        phaseOps[0] = new OperationList();
        phaseOps[1] = new OperationList();
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
    public void postIteration() { }

    protected void switchPhase() {
        Phase last = phase;
        if (phase == Phase.COMPLEXIFICATION) {
            phase = Phase.SIMPLIFICATION;
        } else phase = Phase.COMPLEXIFICATION;

        // remove operations associated with the last phase
        train.getOperators().getList().removeIf(objectHolder ->
                phaseOps[last.ordinal()].getList().stream().anyMatch(phaseOp ->
                        phaseOp.getObj().getClass() == objectHolder.getObj().getClass()
                )
        );

        // add ops of the current phase
        train.getOperators().getList().addAll(phaseOps[phase.ordinal()].getList());

        // finalize (make probabilities add to 1)
        train.getOperators().finalizeStructure();

        lastTransitionGeneration = train.getIteration();

//        System.out.println("Phase changed to : " + phase);
//        System.out.println("operators: " + train.getOperators().getList());
    }
}
