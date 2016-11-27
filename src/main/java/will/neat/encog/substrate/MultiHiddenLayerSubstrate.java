package will.neat.encog.substrate;

import org.encog.neural.hyperneat.substrate.Substrate;
import org.encog.neural.hyperneat.substrate.SubstrateNode;

import java.util.List;

/**
 * Created by Will on 9/09/2016.
 */
public class MultiHiddenLayerSubstrate extends SimpleMarioSubstrate {
    @Override
    protected List<SubstrateNode> hidden(Substrate substrate, List<SubstrateNode> input) {
        List<SubstrateNode> first = layer(substrate, () -> substrate.createHiddenNode(), input, 5, 5, -0.5);
        List<SubstrateNode> second = layer(substrate, () -> substrate.createHiddenNode(), first, 5, 5, 0);
        List<SubstrateNode> last = layer(substrate, () -> substrate.createHiddenNode(), second, 5, 5, 0.5);
        return last;
    }
}
