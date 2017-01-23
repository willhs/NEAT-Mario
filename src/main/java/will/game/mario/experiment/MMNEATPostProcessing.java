package will.game.mario.experiment;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.nio.charset.Charset;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Scanner;
import java.util.stream.Collectors;

/**
 * Created by hardwiwill on 9/01/17.
 */
public class MMNEATPostProcessing {

    private static String ROOT_RESULTS_DIR = "grid_results/mm/";
    private static String EXPERIMENT_NAME = "-phased";

    private static String ROOT_OUTPUT_DIR = ROOT_RESULTS_DIR + "mm-processed/";

    public static void main(String[] args) {

/*        File[] trialDirs = Arrays.stream(new File(ROOT_RESULTS_DIR + EXPERIMENT_NAME).listFiles())
                .filter(file -> file.isDirectory())
                .toArray(s -> new File[s]);*/

        File[] trialDirs = new File[] { new File(ROOT_RESULTS_DIR + EXPERIMENT_NAME) };

        for (int i = 0; i < trialDirs.length; i++) {
            File dir = trialDirs[i];
            // obtain the best scores and their corresponding generation from each parent generation file
            List<String> scores = Arrays.stream(dir.listFiles())
//                    .filter(f -> f.getName().contains("parents_gen"))
                    .map(f -> {
                        // get generation number from filename
                        String filename = f.getName();
                        int gen = Integer.parseInt(filename.substring(filename.indexOf("gen")+"gen".length(), filename.indexOf(".")));

                        List<String> lines = null;
                        try {
                            lines = Files.readAllLines(f.toPath(), Charset.defaultCharset());
                        } catch (IOException e) {
                            e.printStackTrace();
                        }

                        int bestScore = lines.stream()
                                .mapToInt(l -> {
                                    String[] tokens = l.split("\t");
                                    return (int)Double.parseDouble(tokens[2]);
                                })
                                .max().getAsInt();

                        return gen + "," + bestScore;
                    })
                    .collect(Collectors.toList());

            Collections.sort(scores, (a,b) -> {
                return Integer.compare(Integer.parseInt(a.split(",")[0]), Integer.parseInt(b.split(",")[0]));
            });

            // add header to the list
            scores.add(i, "Gen,Fitness");

            scores.stream().forEach(System.out::println);

            // write the results to file
            Path path = Paths.get(ROOT_OUTPUT_DIR + EXPERIMENT_NAME + File.separator + i + ".csv");
            try {
                Files.createDirectories(path.getParent());
                Files.write(path, scores);
            } catch (IOException e) {
                e.printStackTrace();
            }

        }
    }

}
