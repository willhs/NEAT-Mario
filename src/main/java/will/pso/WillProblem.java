/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package will.pso;

import java.util.List;
import java.util.Map;

/**
 *
 * @author xuebing
 */
public abstract class WillProblem {

    private boolean _minimization = false;
    private double _max_domain, _min_domain;
    private double _max_velocity;

    public WillProblem() {
    }

    /**
     * most important thing here
     * calculate fitness using position
     * @param position
     * @return calucated fitness
     */
    public abstract double fitness(Map<String, Double> position);

    public abstract List<Feature> getFeatures();

    public boolean isMinimization() {
        return _minimization;
    }

    public void setMinimization(boolean minimization) {
        this._minimization = minimization;
    }

    public double getMaxDomain() {
        return _max_domain;
    }

    public void setMaxDomain(double max_domain) {
        this._max_domain = max_domain;
    }

    public double getMinDomain() {
        return _min_domain;
    }

    public void setMinDomain(double min_domain) {
        this._min_domain = min_domain;
    }

    /**
     * @return the _max_velocity
     */
    public double getMaxVelocity() {
        return _max_velocity;
    }

    /**
     * @param max_velocity the _max_velocity to set
     */
    public void setMaxVelocity(double max_velocity) {
        this._max_velocity = max_velocity;
    }


    public boolean isBetter(double fitness_a, double fitness_b) {
        return isMinimization() ? fitness_a < fitness_b : fitness_a > fitness_b;
//        if (isMinimization()) {
//            return fitness_a < fitness_b;
//        } else //if is Maximization
//        {
//            return fitness_a > fitness_b;
//        }
    }


    public double getWorstFitness(){
        return isMinimization() ? Double.POSITIVE_INFINITY : Double.NEGATIVE_INFINITY;
    }

}

