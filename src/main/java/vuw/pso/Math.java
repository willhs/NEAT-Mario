/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package vuw.pso;

/**
 *
 * @author xuebing
 */
public class Math {

    public static double Scale(double src_min, double src_max, double value,
            double target_min, double target_max) {
        return (target_max - target_min) / (src_max - src_min) * (value - src_min) + target_min;
    }
    

    public static double Average(Swarm s) {
        double result = 0;
        for (int i = 0; i < s.numberOfParticles(); ++i) {
            result += s.getParticle(i).getFitness();
        }
        return result / s.numberOfParticles();
    }

    
    public static int ModEuclidean(int D, int d) {
        int r = D % d;
        if (r < 0) {
            if (d > 0) {
                r = r + d;
            } else {

                r = r - d;
            }
        }
        return r;
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
