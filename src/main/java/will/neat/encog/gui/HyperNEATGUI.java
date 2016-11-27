package will.neat.encog.gui;

import ch.idsia.benchmark.mario.options.FastOpts;
import javafx.application.Application;
import javafx.application.Platform;
import javafx.concurrent.Task;
import javafx.geometry.Insets;
import javafx.scene.Scene;
import javafx.scene.canvas.Canvas;
import javafx.scene.control.Button;
import javafx.scene.control.CheckBox;
import javafx.scene.control.ComboBox;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.*;
import javafx.scene.paint.Color;
import javafx.scene.text.Font;
import javafx.scene.text.Text;
import javafx.stage.Stage;
import org.encog.engine.network.activation.ActivationBipolarSteepenedSigmoid;
import org.encog.ml.ea.genome.Genome;
import org.encog.ml.ea.train.basic.TrainEA;
import org.encog.neural.hyperneat.HyperNEATCODEC;
import org.encog.neural.hyperneat.HyperNEATGenome;
import org.encog.neural.neat.NEATNetwork;
import org.encog.neural.neat.NEATPopulation;
import will.game.mario.agent.encog.EncogAgent;
import will.neat.AbstractMarioFitnessFunction;
import will.neat.encog.EncogMarioFitnessFunction;
import will.game.mario.experiment.HyperNEATOldEvolver;
import will.game.mario.params.HyperNEATParameters;

import java.util.logging.Logger;

/**
 * Created by Will on 17/07/2016.
 */
public class HyperNEATGUI extends Application {

    private static final double PADDING = 10;
    private static Logger logger = Logger.getLogger(HyperNEATGUI.class
            .getSimpleName());
    private Genome selectedGenome;

    public HyperNEATGUI() {
    }

    private final double SCENE_WIDTH = 1000;
    private final double SCENE_HEIGHT = 600;
    private final double CANVAS_HEIGHT = 600;
    private final double CANVAS_WIDTH = 600;

    private boolean playbackMode = false;

    @Override
    public void start(Stage primaryStage) {
        primaryStage.setTitle("Mario AI NEAT experiment");
        primaryStage.setOnCloseRequest(e -> { Platform.exit(); System.exit(0); });

        BorderPane root = new BorderPane();
        Scene scene = new Scene(root, SCENE_WIDTH, SCENE_HEIGHT);
        primaryStage.setScene(scene);

        // define neat
        HyperNEATOldEvolver evolver = new HyperNEATOldEvolver();
        TrainEA neat = evolver.getNEAT();

        // testing
/*        HyperNEATParameters params = new HyperNEATParametersPSO();
        HyperNEATMarioEvolverFS evolver = new HyperNEATMarioEvolverFS(params,
                () -> new StandardActionStrat());

        Point[] inputs = new Point[] {
                new Point(7, 6), new Point(8,6), new Point(9,6), new Point(6, 7),
                new Point(12,12), new Point(0, 8), new Point(0, 5)
        };

        evolver.setInputs(inputs);
        TrainEA neat = evolver.setupNEAT(params,
                AbstractMarioFitnessFunction.DEFAULT_SIM_OPTIONS,
                (nn) -> new EncogAgentFS(nn, () -> new StandardActionStrat(), inputs));*/
        // ---

        Canvas canvas = new Canvas(CANVAS_WIDTH, CANVAS_HEIGHT);
        root.setCenter(canvas);
        root.setBackground(new Background(new BackgroundFill(Color.GRAY, null, null)));

        DrawNNStrategy draw = new DrawNNStrategy(canvas);
        neat.addStrategy(draw);

        canvas.addEventHandler(MouseEvent.MOUSE_DRAGGED, draw::rotateWithDrag);
        canvas.addEventHandler(MouseEvent.MOUSE_RELEASED, draw::rotateWithDrag);

        // left info pane
        VBox left = new VBox();
        left.setPadding(new Insets(PADDING));
        populateLeftPane(left, evolver.getParams());
//        populateLeftPane(left, params);
        left.setPrefWidth(SCENE_WIDTH/4);
        root.setLeft(left);

        Task<Void> evolve = new Task<Void>() {
            @Override
            protected Void call() throws Exception {
                // evolve til done
                while (!neat.isTrainingDone()) {
                    // don't progress if we have initiated replaying
                    if (playbackMode) {
                        Thread.sleep(100);
                        continue;
                    }
                    neat.iteration();
                    logIteration(neat);
                }

                logger.info("Evolving done");
                logger.info("Winning fitness: " + neat.getPopulation().getBestGenome().getScore());

                return null;
            }
        };
        Thread thread = new Thread(evolve);
        thread.setDaemon(true);
        thread.start();

        // top pane
        // checkbox for headless mode
        HBox top = new HBox();
        top.setPadding(new Insets(PADDING));
        top.setSpacing(PADDING);

        // play genomes as it is evolving
        CheckBox liveToggle = new CheckBox("Play live");
        liveToggle.setSelected(false);
        liveToggle.selectedProperty().addListener((obs, old, newVal) ->
                AbstractMarioFitnessFunction.headless = !newVal
        );

        // start playbackMode mode
        CheckBox playbackToggle = new CheckBox("Playback mode");
        playbackToggle.setSelected(false);
        playbackToggle.selectedProperty().addListener((obs, old, newVal) ->
                playbackMode = newVal
        );

        ComboBox<Genome> specieChamps = new ComboBox<>();
        specieChamps.setOnAction(a -> {
            selectedGenome = specieChamps.getValue();
            draw.setGenome(selectedGenome);
            draw.draw();
        });
        neat.addStrategy(new UpdateComboBoxChamps(specieChamps));

        Button playButton = new Button("Play");
        playButton.setOnAction(e -> {
            if (playbackMode) {
                Genome genome = selectedGenome == null ? neat.getBestGenome() : selectedGenome;
                startPlayback(genome, draw, (EncogMarioFitnessFunction) neat.getScoreFunction());
            }
        });

        top.getChildren().add(liveToggle);
        top.getChildren().add(playbackToggle);
        top.getChildren().add(specieChamps);
        top.getChildren().add(playButton);
        root.setTop(top);

        primaryStage.show();
    }

    private void startPlayback(Genome genome, DrawNNStrategy draw, EncogMarioFitnessFunction ff) {
        Playback playback = new Playback(genome, draw, ff);
        Thread thread = new Thread(playback);
        thread.setDaemon(true);
        thread.start();
    }

    private void populateLeftPane(VBox left, HyperNEATParameters params) {
        left.getChildren().add(new Text("-- Network --")); // spacing
        addTextField(left, "Population size", params.POP_SIZE);
        addTextField(left, "NN weight range", params.NN_WEIGHT_RANGE);
        addTextField(left, "CPPN weight range", params.CPPN_WEIGHT_RANGE);
        addTextField(left, "CPPN min weight", params.CPPN_MIN_WEIGHT);
        addTextField(left, "Initial conn density", params.CPPN_MIN_WEIGHT);
        addTextField(left, "Activation function",
                params.NN_ACTIVATION_FUNCTION instanceof ActivationBipolarSteepenedSigmoid
                ? "Steepened sigmoid"
                : "Clipped Linear"
        );
        addTextField(left, "Activation cycles", params.ACTIVATION_CYCLES);
        left.getChildren().add(new Text(""));
        left.getChildren().add(new Text("-- Evolution --")); // spacing
        addTextField(left, "Selection prop", params.SELECTION_PROP);
        addTextField(left, "Elite rate", params.ELITE_RATE);
        addTextField(left, "Crossover prob", params.CROSSOVER_PROB);

        left.getChildren().add(new Text(""));
        left.getChildren().add(new Text("-- Mutations --")); // spacing
        addTextField(left, "Add conn prob", params.ADD_CONN_PROB);
        addTextField(left, "Add neuron prob", params.ADD_NEURON_PROB);
        addTextField(left, "Perturb weight prob", params.PERTURB_PROB);
        addTextField(left, "Perturb SD", params.PERTURB_SD);
        addTextField(left, "Perturb type", params.WEIGHT_MUT_TYPE.name());
        addTextField(left, "Perturb prop*", params.WEIGHT_PERTURB_PROP);
        addTextField(left, "Perturb reset prob", params.RESET_WEIGHT_PROB);

        left.getChildren().add(new Text("")); // spacing
        left.getChildren().add(new Text("-- Speciation -- ")); // spacing
        addTextField(left, "Max species", params.MAX_SPECIES);
        addTextField(left, "No improve gens", params.SPECIES_DROPOFF);
        addTextField(left, "Init compat thresh", params.INIT_COMPAT_THRESHOLD);

        left.getChildren().add(new Text("")); // spacing
        left.getChildren().add(new Text("-- Level -- ")); // spacing
        Text simOptions = new Text(AbstractMarioFitnessFunction.DEFAULT_SIM_OPTIONS);
        simOptions.setWrappingWidth(SCENE_WIDTH/4);
        left.getChildren().add(simOptions); // spacing
    }

    private void addTextField(Pane pane, String name, Object val) {
        String label = String.format("%-25s %-25s", name, val);
        Text text = new Text(label);
        text.setFont(Font.font("Consolas", 12));
        pane.getChildren().add(text);
    }

    private void logIteration(TrainEA neat) {
        NEATPopulation population = (NEATPopulation) neat.getPopulation();
        double bestFitness = population.getBestGenome().getScore();
//            double bestFitnessGen = population.determineBestSpecies().getLeader().getScore();

        int numSpecies = population.getSpecies().size();

        double averageCPPNLinks = population.getSpecies().stream()
                .map(s -> s.getMembers())
                .flatMap(genomes -> genomes.stream())
                .mapToInt(genome -> ((HyperNEATGenome)genome).getLinksChromosome().size())
                .average()
                .getAsDouble();

        double averageCPPNNodes = population.getSpecies().stream()
                .map(s -> s.getMembers())
                .flatMap(genomes -> genomes.stream())
                .mapToInt(genome -> ((HyperNEATGenome)genome).getNeuronsChromosome().size())
                .average()
                .getAsDouble();


        logger.info("Generation:\t" + neat.getIteration());
        logger.info("Best fitness:\t" + bestFitness);
        logger.info("Num species:\t" + numSpecies);
        logger.info("Ave CPPN conns:\t" + averageCPPNLinks);
        logger.info("Ave CPPN nodes:\t" + averageCPPNNodes);
    }

    public static void main(String[] args) {
        Application.launch(args);
    }

    private static class Playback extends Task<Void> {

        private Genome genome;
        private DrawNNStrategy drawer;
        private EncogMarioFitnessFunction ff;

        public Playback(Genome genome, DrawNNStrategy drawer, EncogMarioFitnessFunction ff) {
            this.genome = genome;
            this.drawer = drawer;
            this.ff = ff;
        }

        @Override
        protected Void call() throws Exception {
            NEATNetwork nn = (NEATNetwork) new HyperNEATCODEC().decode(genome);
            drawer.setGenome(genome);
            Platform.runLater(() -> drawer.draw());

            String simOptions = AbstractMarioFitnessFunction.DEFAULT_SIM_OPTIONS
                    .replace(FastOpts.VIS_OFF, FastOpts.VIS_ON_2X);
            float score = ff.playMario(new EncogAgent(nn), simOptions);
            System.out.println("score: " + score);
            return null;
        }
    }

}

