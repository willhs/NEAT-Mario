package will.game.mario.experiment;

import java.io.File;
import java.io.FileNotFoundException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.*;
import java.util.function.Consumer;
import java.util.stream.Collectors;
import java.util.stream.IntStream;
import java.util.stream.Stream;
import java.util.stream.StreamSupport;

/**
 * Created by Will on 16/10/2016.
 */
public class ExperimentPostProcessing {

    private static final int LEVELS = 5;

    private static String ROOT_RESULTS_DIR = "/vol/grid-solar/sgeusers/hardwiwill/results/mario/31st";
    private static String EXPERIMENT_NAME = "";
    private static String EXPERIMENT_DIR = EXPERIMENT_NAME + "/train/";

    private static String ROOT_OUTPUT_DIR = ROOT_RESULTS_DIR + "averaged/train/";
    private static String OUTPUT_FILENAME = EXPERIMENT_NAME + ".csv";

    public static void main(String[] args) throws Exception {
/*        Files.newDirectoryStream(Paths.get(ROOT_RESULTS_DIR))
                .forEach(expPath -> {
                    try {
                        createAverageFiles(expPath);
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                });*/
        Path path = Paths.get("/vol/grid-solar/sgeusers/hardwiwill/results/mario/31st/speed-kills/speed-killsNN");
//        createAverageFiles(path);
        createAverageFile(path, Paths.get(path.toString() + "/averaged/averaged.csv"));
    }

    private static void createAverageFiles(Path rootExperimentPath) throws Exception {
        Files.newDirectoryStream(rootExperimentPath)
                .forEach(p -> {
                    try {
                        createAverageFile(p, Paths.get(p.toString() + "/averaged/averaged.csv"));
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                });
    }

    private static void createAverageFile(Path experimentDir, Path outputPath) throws Exception {
        List<Path> experiments = StreamSupport.stream(Files.newDirectoryStream(experimentDir).spliterator(), false)
                .filter(p -> !Files.isDirectory(p))
                .collect(Collectors.toList());

        Tuple[] averagedResults = averageResults(experiments.stream());
        Arrays.stream(averagedResults).forEach(System.out::println);

        // csv the results
        List<String> resultsCSV = new ArrayList<>(Arrays.asList(
                "Fitness"
//                        + ",Average Connections,Best Connections,Average Neurons,Best Neurons"
         ));

        resultsCSV.addAll(Arrays.stream(averagedResults)
                .map(t -> {
                    // tuple in csv format
                    return t.fitness + ""
//                            + "," + t.aveConns + "," + t.bestConns + "," +
//                            + t.aveNeurons + "," + t.bestNeurons
                    ;
                }).collect(Collectors.toList()));

        // write the results to file
        Files.createDirectories(outputPath.getParent());

        Files.write(outputPath, resultsCSV);
    }

    private static Tuple[] averageResults(Stream<Path> paths) throws Exception {
        Tuple[] allTuples = paths.flatMap(path -> {
                    Scanner scan = null;
                    try {
                        scan = new Scanner(path.toFile());
                    } catch (FileNotFoundException e) {
                        e.printStackTrace();
                    }

//                    scan.nextLine(); // skip header (for MarioEvolver files)

                    List<Tuple> tuples = new ArrayList<>();
                    int gen = 0;
                    while (scan.hasNext()) {
                        String[] parts = scan.nextLine().split(",");
                        try {
                            if (parts.length == 8) {
                                tuples.add(new Tuple(
                                        gen,
                                        Double.parseDouble(parts[2]),
                                        Double.parseDouble(parts[3]),
                                        Double.parseDouble(parts[4]),
                                        Double.parseDouble(parts[5]),
                                        Double.parseDouble(parts[6]),
                                        Integer.parseInt(parts[7])
                                ));
                            } else {
                                throw new RuntimeException("Tuple has weird number of elements");
                            }
                            gen++;
                        } catch (Exception e) {
                            System.err.println("skipping malformed tuple");
                            e.printStackTrace();
                        }
                    }

                    return tuples.stream();
                }).toArray(s -> new Tuple[s]);

        System.out.println(allTuples.length + " tuples");

        Tuple[] averaged = Arrays.stream(allTuples)
                .collect(TupleAverager::new, TupleAverager::accept, TupleAverager::combine)
                .average();

        return averaged;
    }

    private static class Tuple {
        public int gen;
        public double fitness;
        public double aveConns;
        public double bestConns;
        public double aveNeurons;
        public double bestNeurons;
        public int species;

        public Tuple(int gen, double fitness, double aveConns, double bestConns, double aveNeurons, double bestNeurons, int species) {
            this.gen = gen;
            this.fitness = fitness;
            this.aveConns = aveConns;
            this.bestConns = bestConns;
            this.aveNeurons = aveNeurons;
            this.bestNeurons = bestNeurons;
            this.species = species;
        }

        public Tuple(int gen, double fitness) {
            this.gen = gen;
            this.fitness = fitness;
        }

        public String toString() {
            return "gen: " + gen + ", fitness: " + fitness + ", aveCon: " + aveConns + ", aveNeur: " + aveNeurons;
        }

    }

    private static class TupleAverager implements Consumer<Tuple>
    {
        private final int GENS = 2000;
        private int[] count = new int[GENS];

        private List<Double> fitnessTotal = new ArrayList<>(Collections.nCopies(GENS, 0.0));
        private List<Double> aveConnsTotal = new ArrayList<>(Collections.nCopies(GENS, 0.0));
        private List<Double> bestConnsTotal = new ArrayList<>(Collections.nCopies(GENS, 0.0));
        private List<Double> aveNeuronsTotal = new ArrayList<>(Collections.nCopies(GENS, 0.0));
        private List<Double> bestNeuronsTotal = new ArrayList<>(Collections.nCopies(GENS, 0.0));
        private List<Integer> speciesTotal = new ArrayList<>(Collections.nCopies(GENS, 0));

        public Tuple[] average() {
            return IntStream.range(0, GENS)
                    .mapToObj(i ->
                        new Tuple(
                                i,
                                // prevent / by 0
                                fitnessTotal.get(i) != 0 ? fitnessTotal.get(i)/ count[i] : 0,
                                aveConnsTotal.get(i) != 0 ? aveConnsTotal.get(i) / count[i] : 0,
                                bestConnsTotal.get(i) != 0 ? bestConnsTotal.get(i) / count[i] : 0,
                                aveNeuronsTotal.get(i) != 0 ? aveNeuronsTotal.get(i) / count[i] : 0,
                                bestNeuronsTotal.get(i) != 0 ? bestNeuronsTotal.get(i) / count[i] : 0,
                                speciesTotal.get(i) != 0 ? speciesTotal.get(i) / count[i] : 0
                        )
                    ).toArray(s -> new Tuple[s]);
        }

        public void combine(TupleAverager other) {

            IntStream.range(0, GENS).forEach(i -> {
                count[i] += other.count[i];
                fitnessTotal.set(i, fitnessTotal.get(i) + other.fitnessTotal.get(i));
                aveConnsTotal.set(i, aveConnsTotal.get(i) + other.aveConnsTotal.get(i));
                bestConnsTotal.set(i, bestConnsTotal.get(i) + other.bestConnsTotal.get(i));
                aveNeuronsTotal.set(i, aveConnsTotal.get(i) + other.aveNeuronsTotal.get(i));
                bestNeuronsTotal.set(i, bestNeuronsTotal.get(i) + other.bestNeuronsTotal.get(i));
                speciesTotal.set(i, speciesTotal.get(i) + other.speciesTotal.get(i));
            });
        }

        @Override
        public void accept(Tuple tuple) {
//            int gen = tuple.gen - 1;
            int gen = tuple.gen;
            count[gen]++;
            fitnessTotal.set(gen, fitnessTotal.get(gen) + tuple.fitness);
            aveConnsTotal.set(gen, aveConnsTotal.get(gen) + tuple.aveConns);
            bestConnsTotal.set(gen, bestConnsTotal.get(gen) + tuple.bestConns);
            aveNeuronsTotal.set(gen, aveNeuronsTotal.get(gen) + tuple.aveNeurons);
            bestNeuronsTotal.set(gen, bestNeuronsTotal.get(gen) + tuple.bestNeurons);
            speciesTotal.set(gen, speciesTotal.get(gen) + tuple.species);
        }
    }
}
