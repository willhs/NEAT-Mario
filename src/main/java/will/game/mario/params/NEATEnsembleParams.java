package will.game.mario.params;

/**
 * Created by hardwiwill on 29/11/16.
 */
public class NEATEnsembleParams extends NEATParameters {
    public int ENSEMBLE_SIZE = 3;

    public NEATEnsembleParams() {
        INIT_COMPAT_THRESHOLD = 8;

        PHASED_SEARCH = false;
    }
}
