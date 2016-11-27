package will.pso;

import vuw.pso.Math;

/**
 * Created by Will on 18/05/2016.
 *
 * Based on RingTopology
 */
public class WillRingTopology extends WillTopology {

    private int _neighbors = 2;

    public WillRingTopology() {
    }

    public WillRingTopology(int n) {
        setNeighbors(n);
    }

    public void share(WillSwarm s) {

        for (int p = 0; p < s.getParticles().size(); p++) {

            WillParticle particle = s.getParticle(p);

//            System.out.println("* Setting neighbourhood bests for particle(" + p + "):");

            WillParticle bestNeighbour = null;
            double bestNeighbourFitness = s.getProblem().getWorstFitness();

            for (int j = -getNeighbors() / 2; j <= getNeighbors() / 2; ++j) {
                // n = index of neighbour particle we are comparing with best so far
                int n = Math.ModEuclidean(p + j, s.numberOfParticles());
                if (s.getProblem().isBetter(s.getParticle(n).getPBestFitness(), bestNeighbourFitness)) {
                    bestNeighbour = s.getParticle(Math.ModEuclidean(p + j, s.numberOfParticles()));
                    bestNeighbourFitness = bestNeighbour.getPBestFitness();
//                    System.out.println("particle(" + n + ") has new best fitness: " + s.getParticle(n).getPBestFitness());
                } else {
//                    System.out.println("particle(" + n + ") does not have best fitness: " + s.getParticle(n).getPBestFitness());
                }
            }
//            System.out.println("pBest fitness: \t\t" + particle.getPBestFitness());
//            System.out.println("pBest :\t\t" + particle.getPBestFeatures());
//            System.out.println("best_neighbor fitness:\t\t" + bestNeighbourFitness);
//            System.out.println("best_neighbor:\t\t" + bestNeighbour.getFeatures());
//            System.out.println();

            particle.setNBestFitness(bestNeighbourFitness);

            for (int n = 0; n < particle.getSize(); ++n) {
                particle.setNBestFeats(n, bestNeighbour.getPBestFeatures(n));
            }

//            for (int n = 0; n < p_i.getSize(); ++n) {
////                p_i.setNBestFeats(n, best_neighbor.getNBestFeat(n));
//                System.out.print(best_neighbor.getPBestFeatures(n));
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
