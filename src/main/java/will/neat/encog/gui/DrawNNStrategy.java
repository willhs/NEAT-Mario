package will.neat.encog.gui;

import javafx.scene.canvas.Canvas;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.input.MouseEvent;
import javafx.scene.paint.Color;
import javafx.scene.paint.Paint;
import org.encog.ml.ea.genome.Genome;
import org.encog.ml.ea.train.basic.TrainEA;
import org.encog.ml.train.MLTrain;
import org.encog.ml.train.strategy.Strategy;
import org.encog.neural.hyperneat.HyperNEATCODEC;
import org.encog.neural.hyperneat.substrate.Substrate;
import org.encog.neural.hyperneat.substrate.SubstrateNode;
import org.encog.neural.neat.NEATNetwork;
import org.encog.neural.neat.NEATPopulation;
import will.util.Algorithms;

import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;

/**
 * Created by Will on 25/08/2016.
 */
public class DrawNNStrategy implements Strategy {

    private static final double MIN_LINK_WIDTH = 0.4;
    private static final double MAX_LINK_WIDTH = 2;
    private final Paint INPUT_NEURON_COLOUR = Color.AQUA;
    private final Paint HIDDEN_NEURON_COLOUR = Color.GREENYELLOW;
    private final Paint OUTPUT_NEURON_COLOUR = Color.RED;
    private final Paint BIAS_NEURON_COLOUR = Color.YELLOW;

    private final double WIDTH, HEIGHT;

//    private final Paint CONNECTION_LOW

    private TrainEA train;
    private Canvas canvas;

    private double  rotateX = Math.PI/4,
                    rotateY = Math.PI/4,
                    rotateZ = 0;

    private static final int NEURON_SIZE = 10;
    private Genome genome;

    public DrawNNStrategy(Canvas canvas) {
        this.canvas = canvas;
        this.WIDTH = canvas.getWidth() * 0.3;
        this.HEIGHT = canvas.getHeight() * 0.3;
    }

    @Override
    public void init(MLTrain train) {
        this.train = (TrainEA) train;
    }

    @Override
    public void preIteration() {
        if (train.getIteration() > 0) {
            NEATPopulation pop = (NEATPopulation) train.getPopulation();
            Genome champ = pop.getBestGenome();
            genome = champ;
            NEATNetwork nn = (NEATNetwork) new HyperNEATCODEC().decode(champ);
            // initiate draw when JavaFX thread is ready (otherwise weird stuff happens)
//            Platform.runLater(() -> draw());
        }
    }

    public void draw() {
        try {
            if (train.getIteration() < 1
                    || genome == null) { return; }

            GraphicsContext g = canvas.getGraphicsContext2D();
            g.clearRect(0,0,canvas.getWidth(), canvas.getHeight());

            NEATPopulation pop = (NEATPopulation) train.getPopulation();
            Substrate substrate = pop.getSubstrate();
            NEATNetwork nn = (NEATNetwork) new HyperNEATCODEC().decode(genome);

            List<SubstrateNode> allNodes = Stream.concat(
                    Stream.concat(
                            substrate.getInputNodes().stream(),
                            substrate.getHiddenNodes().stream()
                    ),
                    substrate.getOutputNodes().stream()
            ).collect(Collectors.toList());

            // draw links
            Arrays.stream(nn.getLinks()).forEach(link -> {
                if (link.getFromNeuron() == 0) return;
                SubstrateNode from = allNodes.stream()
                        .filter(n -> n.getId() == link.getFromNeuron())
                        .findFirst()
                        .get();

                SubstrateNode to = allNodes.stream()
                        .filter(n -> n.getId() == link.getToNeuron())
                        .findFirst()
                        .get();

                double NNWeightRange = pop.getHyperNEATNNWeightRange();
                // weight normalised
                double weight = Algorithms.clamp(link.getWeight() / NNWeightRange, -1, 1);
                double weightAbs = Math.abs(weight);
                // scale a little up so that small connections can be seen
                double weightAbsAlpha = Algorithms.scaleToRange(weightAbs, 0, 1, 0.1, 1);
                double lineWidth = MIN_LINK_WIDTH + (weightAbs * MAX_LINK_WIDTH-MIN_LINK_WIDTH);

                double positiveAmount = Algorithms.clamp(weight, 0, 1);
                double negativeAmount = Math.abs(Algorithms.clamp(weight, -1, 0));
                double scaledAmount = Algorithms.scaleToRange(weight, -1, 1, 0, 1);
                g.setStroke(Color.color(scaledAmount, scaledAmount, scaledAmount, weightAbsAlpha));

                double[] transformedFrom = transform(from.getLocation());
                double[] transformedTo = transform(to.getLocation());
                g.setLineWidth(1);
                g.strokeLine(transformedFrom[0] + (NEURON_SIZE/2), transformedFrom[1] + (NEURON_SIZE/2),
                        transformedTo[0] + (NEURON_SIZE/2), transformedTo[1] + (NEURON_SIZE/2));
            });

            // draw nodes
            substrate.getInputNodes().forEach(n -> {
                double[] loc = n.getLocation();
                double[] locTrans = transform(loc);
                g.setFill(INPUT_NEURON_COLOUR);
                g.fillOval(locTrans[0], locTrans[1], NEURON_SIZE, NEURON_SIZE);
            });

            substrate.getHiddenNodes().forEach(n -> {
                double[] loc = n.getLocation();
                double[] locTrans = transform(loc);
                g.setFill(HIDDEN_NEURON_COLOUR);
                g.fillOval(locTrans[0], locTrans[1], NEURON_SIZE, NEURON_SIZE);
            });

            substrate.getOutputNodes().forEach(n -> {
                double[] loc = n.getLocation();
                double[] locTrans = transform(loc);
                g.setFill(OUTPUT_NEURON_COLOUR);
                g.fillOval(locTrans[0], locTrans[1], NEURON_SIZE, NEURON_SIZE);
//                g.fillText(Arrays.toString(n.getLocation()), locTrans[0], locTrans[1]);
            });

            // bias node
            double[] originTransformed = transform(new double[]{0,0,0});
            g.setFill(BIAS_NEURON_COLOUR);
            g.fillOval(originTransformed[0], originTransformed[1], NEURON_SIZE, NEURON_SIZE);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private double[] transform(double[] point) {
        Vector3D vec = new Vector3D(point[0], point[1], point[2]);

        Transform translate = Transform.identity()
                .compose(Transform.newXRotation(rotateX))
                .compose(Transform.newYRotation(rotateY))
                .compose(Transform.newZRotation(rotateZ));

        Transform other = Transform.identity()
                .compose(Transform.newTranslation(canvas.getWidth()/2 , canvas.getHeight()/2, 0))
//                .compose(Transform.newTranslation(canvas.getWidth()*0.2, 0, 0))
                .compose(Transform.newScale(WIDTH, HEIGHT, WIDTH))
                ;

        Vector3D translated = translate.multiply(vec);
        Vector3D result = other.multiply(translated);

        return new double[]{ result.x, result.y, result.z };
    }

    @Override
    public void postIteration() { }

    public void setGenome(Genome genome) {
        this.genome = genome;
    }

    // rotation mouse drag handling stuff
    private double prevX;
    private double prevY;
    private boolean released = true;
    private static final double DRAG_SCALE = 0.015;

    public void rotateWithDrag(MouseEvent event) {
        if (event.getEventType().equals(MouseEvent.MOUSE_RELEASED)) {
            released = true;
        } else {
            if (!released) {
                double rotateAmountY = (event.getX() - prevX) * DRAG_SCALE;
                double rotateAmountX = (event.getY() - prevY) * DRAG_SCALE;
                rotateY -= rotateAmountY;
                rotateX += rotateAmountX;
                draw();
            }
            prevX = event.getX();
            prevY = event.getY();

            released = false;
        }
    }
}

