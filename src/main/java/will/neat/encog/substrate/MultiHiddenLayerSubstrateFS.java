package will.neat.encog.substrate;

import org.encog.neural.hyperneat.substrate.Substrate;
import org.encog.neural.hyperneat.substrate.SubstrateNode;

import java.awt.*;
import java.util.ArrayList;
import java.util.List;
import java.util.function.BiConsumer;
import java.util.function.Supplier;

/**
 * Created by Will on 9/10/2016.
 */
public class MultiHiddenLayerSubstrateFS extends MultiHiddenLayerSubstrate {

    private Point[] inputs;

    public MultiHiddenLayerSubstrateFS(Point[] inputs) {
        this.inputs = inputs;
    }

    @Override
    protected List<SubstrateNode> inputs(Substrate substrate, int width, int height, double z) {
        return layerFS(substrate, () -> substrate.createInputNode(), new ArrayList<>(), width, height, z);
    }

    // helper functions
    protected List<SubstrateNode> layerFS(Substrate substrate, Supplier<SubstrateNode> node, List<SubstrateNode> prevLayer, int width, int height, double z) {

        double xTickInput = HYPERCUBE_LENGTH / width;
        double yTickInput = HYPERCUBE_LENGTH / height;
        double xStartInput = -1 + xTickInput/2;
        double yStartInput = -1 + yTickInput/2;

        return makePartialNeuronGrid((n1, n2) -> substrate.createLink(n1, n2),
                node, prevLayer, xStartInput,
                yStartInput, xTickInput, yTickInput, z, inputs
        );
    }

    private List<SubstrateNode> makePartialNeuronGrid(BiConsumer<SubstrateNode, SubstrateNode> link,
                                                      Supplier<SubstrateNode> neuron, List<SubstrateNode> prevLayer,
                                                      double xStart, double yStart, double xTick,
                                                      double yTick, double z, Point[] points) {
        List<SubstrateNode> nodes = new ArrayList<>();
        // make hidden nodes
        for (int i = 0; i < points.length; i++) {
            Point p = points[i];
            SubstrateNode hidden = neuron.get();
            hidden.getLocation()[0] = xStart + (p.x * xTick);
            hidden.getLocation()[1] = yStart + (p.y * yTick);
            hidden.getLocation()[2] = z;
            nodes.add(hidden);

            // create connections to all input nodes
            prevLayer.forEach(prev -> link.accept(prev, hidden));
        }

        return nodes;
    }
}
