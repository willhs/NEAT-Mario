/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package vuw.pso;

/**
 *
 * @author xuebing
 */
public class BasicVelocityClamp extends VelocityClamp {
    
     
    public BasicVelocityClamp(){
        
    }
    
     public void clamp(Particle p, double MaxVelocity) {
        for (int i = 0 ; i < p.getSize(); ++i){
            //This should be the absolute value... TODO
            if (p.getVelocity(i) > MaxVelocity){
              p.setVelocity(i, MaxVelocity);
            }
        }
    }
}
