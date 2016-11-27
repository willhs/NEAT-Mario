/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package vuw.pso;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

/**
 *
 * @author xuebing
 */
public class Particle {

    private List<Double> _position = new ArrayList<Double>();
    private List<Double> _velocity = new ArrayList<Double>();
    private double _fitness;
    private List<Double> _personal_position = new ArrayList<Double>();
    private double _personal_fitness;
    private List<Double> _neighborhood_position = new ArrayList<Double>();
    private double _neighborhood_fitness;
    private double _inertia;
    private double _c1,  _c2;
    private Random _r1 = new Random(),  _r2 = new Random();
    private double _max_position,  _min_position;

    public Particle() {
    }

    public void setSize(int size) {
        _position.clear();
        _velocity.clear();
        _personal_position.clear();
        _neighborhood_position.clear();
        for (int i = 0; i < size; ++i) {
            _position.add(0.0);
            _velocity.add(0.0);
            _personal_position.add(0.0);
            _neighborhood_position.add(0.0);
        }
    }

    public int getSize() {
        return _position.size();
    }

    public void setPosition(int index, double value) {
        this._position.set(index, value);
    }

    public double getPosition(int index) {
        return _position.get(index);
    }

    public List<Double> getPosition() {
        return _position;
    }

    public void setVelocity(int index, double value) {
        _velocity.set(index, value);
    }

    public double getVelocity(int index) {
        return _velocity.get(index);
    }

    public double getFitness() {
        return _fitness;
    }

    public void setFitness(double fitness) {
        this._fitness = fitness;
    }

    public void setPersonalPosition(int index, double value) {
        _personal_position.set(index, value);
    }

    public double getPersonalPosition(int index) {
        return _personal_position.get(index);
    }

    public double getPersonalFitness() {
        return _personal_fitness;
    }

    public void setPersonalFitness(double fitness_best_personal) {
        _personal_fitness = fitness_best_personal;
    }

    public void setNeighborhoodPosition(int index, double value) {
        this._neighborhood_position.set(index, value);
    }

    public double getNeighborhoodPosition(int index) {
        return _neighborhood_position.get(index);
    }

    public double getNeighborhoodFitness() {
        return _neighborhood_fitness;
    }

    public void setNeighborhoodFitness(double fitness_best_neighbor) {
        this._neighborhood_fitness = fitness_best_neighbor;
    }

    public double getInertia() {
        return _inertia;
    }

    public void setInertia(double inertia) {
        this._inertia = inertia;
    }

    public double getC1() {
        return _c1;
    }

    public void setC1(double c1) {
        this._c1 = c1;
    }

    public double getC2() {
        return _c2;
    }

    public void setC2(double c2) {
        this._c2 = c2;
    }

    public Random getR1() {
        return _r1;
    }

    public Random getR2() {
        return _r2;
    }

    public double getMaxPosition() {
        return _max_position;
    }

    public void setMaxPosition(double max_position) {
        this._max_position = max_position;
    }

    public double getMinPosition() {
        return _min_position;
    }

    public void setMinPosition(double min_position) {
        this._min_position = min_position;
    }

    public void updateVelocity() {
        for (int i = 0; i < getSize(); ++i) {
            double v_i = getInertia() * getVelocity(i);
            v_i += getC1() * getR1().nextDouble() * (getPersonalPosition(i) - getPosition(i));
            v_i += getC2() * getR2().nextDouble() * (getNeighborhoodPosition(i) - getPosition(i));
            
            setVelocity(i, v_i);
        }
    }

    public void updatePosition() {
        for (int i = 0; i < getSize(); ++i) {
            double p_i = getPosition(i) + getVelocity(i);
            if (p_i > getMaxPosition()){
                p_i = getMaxPosition();
            }
            if (p_i < getMinPosition()){
                p_i = getMinPosition();
            }
            
            setPosition(i, p_i);
        }
    }
}
