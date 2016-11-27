package will.pso;

/**
 * Created by Will on 18/05/2016.
 */
public class Feature implements Cloneable{

    private double initialVal;

    private String name;
    private double val;
    private double max;
    private double min;
    private double vel; // velocity

    public Feature(double initialVal) {
        this.initialVal = initialVal;
    }

    public Feature(String name, double min, double max) {
        this.name = name;
        this.min = min;
        this.max = max;
    }

    public Feature(String name, double initialVal, double min, double max) {
        this.name = name;
        this.initialVal = initialVal;
        this.max = max;
        this.min = min;
    }

    public Feature(String name, double val, double vel, double min, double max, double initialVal) {
        this.name = name;
        this.val = val;
        this.vel = vel;
        this.min = min;
        this.max = max;
        this.initialVal = initialVal;
    };

    public void generateInitialVals() {
        // calculate starting value and velocity (random)
        this.val = vuw.pso.Math.Scale(0, 1, Math.random(), min, max);
        // use same starting vel calculations as the vuw.vuw.pso code
        this.vel = vuw.pso.Math.Scale(0, 1, Math.random(),
                1.0 / 5.0 * -(max-min),
                1.0 / 5.0 * (max-min)
        );
    }


    public double getInitialVal() {
        return initialVal;
    }

    public double getVal() {
        return val;
    }

    public void setValue(double val) {
        this.val = val;
    }

    public double getMax() {
        return max;
    }

    public double getMin() {
        return min;
    }

    public String getName() {
        return name;
    }

    public double getVel() {
        return vel;
    }

    @Override
    public boolean equals(Object o) {
        if (o instanceof Feature) {
            Feature f = (Feature) o;
            return f.getVal() == this.val
                    && f.getName().equals(this.name)
                    && f.getVel() == f.getVel()
                    && f.getInitialVal() == f.getInitialVal()
                    && f.getMin() == f.getMin()
                    && f.getMax() == f.getMax();
        }
        return false;
    }

    @Override
    public int hashCode() {
        return (int)(name.hashCode() * val);
    }

    public void setVel(double vel) {
        this.vel = vel;
    }

    public void printDiffs() {
        System.out.printf(
//                "%s: %f, %4.2f%% difference%n",
//                name, val, (((Math.abs(initialVal - val))/(Math.abs(max - min)))*100)
                "%s: val: %4.2f, init: %4.2f, vel: %4.2f%n",
                name, val, initialVal, vel
        );
    }

    public String toString(){
        return String.format(
        "%s: %4.2f (%4.2fpi)",
                 name, val, vel
        );
    }

    public Feature clone() {
        return new Feature(name, initialVal, min, max);
    }
}
