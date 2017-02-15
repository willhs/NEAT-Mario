package will.neat.encog;

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
        if (neat.getIteration() == 0) {
            return;
        }
        // switch if the right num of generations has gone by
        if (neat.getIteration() % phaseALength == 0) {
            switchPhase();
        }

    }
}
