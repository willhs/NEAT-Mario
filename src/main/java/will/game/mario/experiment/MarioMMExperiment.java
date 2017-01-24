package will.game.mario.experiment;

import edu.utexas.cs.nn.MMNEAT.MMNEAT;
import edu.utexas.cs.nn.evolution.genotypes.Genotype;
import edu.utexas.cs.nn.experiment.evolution.LimitedSinglePopulationGenerationalEAExperiment;
import edu.utexas.cs.nn.parameters.Parameters;
import edu.utexas.cs.nn.tasks.NoisyLonerTask;
import edu.utexas.cs.nn.util.datastructures.Pair;
import will.game.mario.mmneat.WillMarioTask;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.PrintStream;

/**
 * Created by Will on 24/01/2017.
 */
public class MarioMMExperiment<T> extends LimitedSinglePopulationGenerationalEAExperiment<T> {

    private int testTrials = Parameters.parameters.integerParameter("testTrials");

    @Override
    public void run() {
        super.run();

        // obtain champion of the population
        Genotype<T> champ = population.stream().reduce((a, b) -> {
            Pair<double[], double[]> aScores = ((WillMarioTask) MMNEAT.task).oneEval(a, 0);
            Pair<double[], double[]> bScores = ((WillMarioTask) MMNEAT.task).oneEval(b, 0);
            if (aScores.t1[0] >= bScores.t1[0]) {
                return a;
            } else return b;
        }).get();

        // test the champion
        WillMarioTask task = (WillMarioTask) MMNEAT.ea.getTask();
        double average = task.test(champ, testTrials);

        logTestScore(average);
    }


    private void logTestScore(double score) {
        String prefix = Parameters.parameters.stringParameter("log")
                + Parameters.parameters.integerParameter("runNumber");

        PrintStream test = null;
        try {
            test = new PrintStream(new FileOutputStream(new File(saveDirectory + "/" + prefix + "_test.txt")));
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }

        test.println(score);
        test.close();
    }
}
