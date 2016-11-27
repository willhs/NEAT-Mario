/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package vuw.pso;

import java.util.List;

/**
 *
 * @author xuebing
 */
public class QuadricProblem extends Problem{

    public QuadricProblem(){
        setMinimization(true);
        setMinDomain(-100);
        setMaxDomain(100);
    }
    
    public double fitness(List<Double> position){
        double result = 0;
        for (int i = 0 ; i < position.size(); ++i){
            double sum = 0;
            for (int j = 0 ; j <= i ; ++j){
                sum += position.get(j);
            }
            result += sum * sum;
        }
        return result;
    }
    
}
