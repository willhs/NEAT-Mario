package will.neat.encog.substrate;

import org.encog.neural.hyperneat.substrate.Substrate;
import org.encog.neural.hyperneat.substrate.SubstrateNode;

import java.util.ArrayList;
import java.util.List;
import java.util.function.BiConsumer;
import java.util.function.Supplier;

/**
 * Created by Will on 21/08/2016.
 */
public class SimpleMarioSubstrate implements SubstrateFactory {

    public static final double HYPERCUBE_LENGTH = 2;

    @Override
    public Substrate makeSubstrate() {
        Substrate substrate = new Substrate(3);

        List<SubstrateNode> inputs = inputs(substrate, 13, 13, -1);
        List<SubstrateNode> hidden1 = hidden(substrate, inputs);
        outputLayer(substrate, hidden1, 4);
        substrate.setActivationCycles(2);
        return substrate;
    }

    protected List<SubstrateNode> inputs(Substrate substrate, int width, int height, double z) {
        return layer(substrate, () -> substrate.createInputNode(), new ArrayList<>(), width, height, z);
    }

    protected List<SubstrateNode> hidden(Substrate substrate, List<SubstrateNode> prevNodes) {
        List<SubstrateNode> first = layer(substrate, () -> substrate.createHiddenNode(), prevNodes, 8, 8, 0);
        return first;
    }

    protected void outputLayer(Substrate substrate, List<SubstrateNode> prevNodes, int outputNodes) {
        int middleX = 0;
        int middleY = 0;
        double variance = 1; // how far the node should vary from the centre

        // coordinates for controls in order: left, right, jump, speed
        double[] xs = {
                middleX - variance,
                middleX + variance,
                middleX,
                middleX,
                middleX
        };
        double[] ys = {
                middleY,
                middleY,
                middleY - variance,
                middleY,
                middleY + variance
        };

        for (int i = 0; i < outputNodes; i++) {
            SubstrateNode output = substrate.createOutputNode();
            output.getLocation()[0] = xs[i];
            output.getLocation()[1] = ys[i];
            output.getLocation()[2] = 1;

            // create connections to all input nodes
            prevNodes.forEach(prev -> substrate.createLink(prev, output));
        }
    }

    // helper functions
    protected List<SubstrateNode> layer(Substrate substrate, Supplier<SubstrateNode> node, List<SubstrateNode> prevLayer, int width, int height, double z) {

        double xTickInput = HYPERCUBE_LENGTH / width;
        double yTickInput = HYPERCUBE_LENGTH / height;
        double xStartInput = -1 + xTickInput/2;
        double yStartInput = -1 + yTickInput/2;

        return makeNeuronGrid((n1, n2) -> substrate.createLink(n1, n2),
                node, prevLayer, width, height, xStartInput,
                yStartInput, xTickInput, yTickInput, z
        );
    }

    private List<SubstrateNode> makeNeuronGrid(BiConsumer<SubstrateNode, SubstrateNode> link,
                                               Supplier<SubstrateNode> neuron, List<SubstrateNode> prevLayer,
                                               double width, double height, double xStart,
                                               double yStart, double xTick, double yTick, double z) {
        List<SubstrateNode> nodes = new ArrayList<>();
        // make hidden nodes
        for (int r = 0; r < width; r++ ) {
            for (int c = 0; c < height; c++) {
                SubstrateNode hidden = neuron.get();
                hidden.getLocation()[0] = xStart + (c * xTick);
                hidden.getLocation()[1] = yStart + (r * yTick);
                hidden.getLocation()[2] = z;
                nodes.add(hidden);

                // create connections to all input nodes
                prevLayer.forEach(prev -> link.accept(prev, hidden));
            }
        }
        return nodes;
    }

}
