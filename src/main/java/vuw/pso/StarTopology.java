/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package vuw.pso;

/**
 *
 * @author xuebing
 */
public class StarTopology extends Topology {

    public void share(Swarm s) {

        Particle best_particle = null;
        double best_fitness = s.getProblem().getWorstFitness();

        for (int i = 0; i < s.numberOfParticles(); ++i) {
            if (s.getProblem().isBetter(s.getParticle(i).getPersonalFitness(), best_fitness)) {
                best_particle = s.getParticle(i);
                best_fitness = best_particle.getPersonalFitness();
            }
        }

        for (int i = 0; i < s.numberOfParticles(); ++i) {
            Particle p_i = s.getParticle(i);
            for (int j = 0; j < p_i.getSize(); ++j) {
                p_i.setNeighborhoodPosition(j, best_particle.getPersonalPosition(j));
                p_i.setNeighborhoodFitness(best_fitness);
            }

        }

    }
}
