/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package vuw.pso;

/**
 *
 * @author xuebing
 */
public class RingTopology extends Topology {

    private int _neighbors = 2;

    public RingTopology() {
    }

    public RingTopology(int n) {
        setNeighbors(n);
    }

    public void share(Swarm s) {

        for (int i = 0; i < s.numberOfParticles(); ++i) {

            Particle p_i = s.getParticle(i);

//            System.out.println("getNeighbors()" + getNeighbors());
            System.out.println("");
//            if (i == 7) {
            System.out.print("i  " + i + "  NNN  ");
//            }



            Particle best_neighbor = null;
            double best_fitness = s.getProblem().getWorstFitness();

            for (int j = -getNeighbors() / 2; j <= getNeighbors() / 2; ++j) {

                System.out.print("  " + Math.ModEuclidean(i + j, s.numberOfParticles()));

                if (s.getProblem().isBetter(s.getParticle(Math.ModEuclidean(i + j, s.numberOfParticles())).getPersonalFitness(), best_fitness)) {
                    best_neighbor = s.getParticle(Math.ModEuclidean(i + j, s.numberOfParticles()));
                    best_fitness = best_neighbor.getPersonalFitness();
                }
            }
            System.out.println("");
            System.out.println("best_neighbor " + best_neighbor.getPosition());
            System.out.println("best_fitness  " + best_fitness);

            p_i.setNeighborhoodFitness(best_fitness);

            for (int n = 0; n < p_i.getSize(); ++n) {
                p_i.setNeighborhoodPosition(n, best_neighbor.getPersonalPosition(n));
            }

//            for (int n = 0; n < p_i.getSize(); ++n) {
////                p_i.setNBestFeats(n, best_neighbor.getNBestFeat(n));
//                System.out.print(best_neighbor.getPersonalPosition(n));
//                System.out.println("");
//
//            }
        }
    }

    public int getNeighbors() {
        return _neighbors;
    }

    public void setNeighbors(int neighbors) {
        this._neighbors = neighbors;
    }
}
//   int n = 10;
//        int nei = 4;
//        
//        for (int j = 0; j < 10; ++j) {
//            System.out.print(j + ":");
//            
//            for (int p =-nei/2; p <= nei/2 ; ++p) {
//
//                System.out.print(Math.ModEuclidean(p + j, n) + " ; ");
//            }
//            System.out.println();
//        }
//        System.out.print((1/2)*nei);
//        System.exit(32);  
//0:8 ; 9 ; 0 ; 1 ; 2 ; 
//1:9 ; 0 ; 1 ; 2 ; 3 ; 
//2:0 ; 1 ; 2 ; 3 ; 4 ; 
//3:1 ; 2 ; 3 ; 4 ; 5 ; 
//4:2 ; 3 ; 4 ; 5 ; 6 ; 
//5:3 ; 4 ; 5 ; 6 ; 7 ; 
//6:4 ; 5 ; 6 ; 7 ; 8 ; 
//7:5 ; 6 ; 7 ; 8 ; 9 ; 
//8:6 ; 7 ; 8 ; 9 ; 0 ; 
//9:7 ; 8 ; 9 ; 0 ; 1 ; 

