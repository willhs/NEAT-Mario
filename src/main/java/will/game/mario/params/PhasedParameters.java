package will.game.mario.params;

import will.neat.encog.AbstractPhasedSearch;

/**
 * Created by Will on 27/01/2017.
 */
public class PhasedParameters extends NEATParameters {

    public int MIN_GENS_WITHOUT_IMPROVE = 50;
    public int MIN_SIMPLIFICATION_GENS = 50;
    public int MPC_JUMP = 80;
    public AbstractPhasedSearch.Phase STARTING_PHASE = AbstractPhasedSearch.Phase.COMPLEXIFICATION;

    public PhasedParameters() {
        PHASED_SEARCH = true;
    }
}
