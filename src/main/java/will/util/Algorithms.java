package will.util;

/**
 * Created by Will on 10/06/2016.
 */
public class Algorithms {
    public static double scaleToRange(final double valueIn, final double baseMin, final double baseMax, final double limitMin, final double limitMax) {
        return ((limitMax - limitMin) * (valueIn - baseMin) / (baseMax - baseMin)) + limitMin;
    }

    public static double clamp(double val, double min, double max) {
        return val < min ? min : val > max ? max : val;
    }
}
