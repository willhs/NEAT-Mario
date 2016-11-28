package will.neat.encog.ensemble.mutation;

import org.encog.mathutil.randomize.RangeRandomizer;
import org.encog.ml.ea.genome.Genome;
import org.encog.ml.ea.opp.EvolutionaryOperator;
import org.encog.ml.ea.train.EvolutionaryAlgorithm;
import org.encog.neural.neat.NEATNeuronType;
import org.encog.neural.neat.NEATPopulation;
import org.encog.neural.neat.training.SingleNEATGenome;
import org.encog.neural.neat.training.NEATInnovation;
import org.encog.neural.neat.training.NEATLinkGene;
import org.encog.neural.neat.training.NEATNeuronGene;
import will.neat.encog.ensemble.NEATEnsembleGenome;
import will.neat.encog.ensemble.NEATEnsemblePopulation;

/**
 * Created by hardwiwill on 28/11/16.
 */
public abstract class EnsembleNEATMutation implements EvolutionaryOperator {
    /**
     * The trainer that owns this class.
     */
    private EvolutionaryAlgorithm owner;

    /**
     * Choose a random neuron.
     *
     * @param target
     *            The target genome. Should the input and bias neurons be
     *            included.
     * @param choosingFrom
     *            True if we are chosing from all neurons, false if we exclude
     *            the input and bias.
     * @return The random neuron.
     */
    public NEATNeuronGene chooseRandomNeuron(final SingleNEATGenome target,
                                             final boolean choosingFrom) {
        int start;

        if (choosingFrom) {
            start = 0;
        } else {
            start = target.getInputCount() + 1;
        }

        // if this network will not "cycle" then output neurons cannot be source
        // neurons
        if (!choosingFrom) {
            final int ac = ((NEATEnsemblePopulation) target.getPopulation())
                    .getActivationCycles();
            if (ac == 1) {
                start += target.getOutputCount();
            }
        }

        final int end = target.getNeuronsChromosome().size() - 1;

        // no neurons to pick!
        if (start > end) {
            return null;
        }

        final int neuronPos = RangeRandomizer.randomInt(start, end);
        final NEATNeuronGene neuronGene = target.getNeuronsChromosome().get(
                neuronPos);
        return neuronGene;

    }

    /**
     * Create a link between two neuron id's. Create or find any necessary
     * innovation records.
     *
     * @param target
     *            The target genome.
     * @param neuron1ID
     *            The id of the source neuron.
     * @param neuron2ID
     *            The id of the target neuron.
     * @param weight
     *            The weight of this new link.
     */
    public void createLink(final SingleNEATGenome target, final long neuron1ID,
                           final long neuron2ID, final double weight) {

        // first, does this link exist? (and if so, hopefully disabled,
        // otherwise we have a problem)
        for (final NEATLinkGene linkGene : target.getLinksChromosome()) {
            if ((linkGene.getFromNeuronID() == neuron1ID)
                    && (linkGene.getToNeuronID() == neuron2ID)) {
                // bring the link back, at the new weight
                linkGene.setEnabled(true);
                linkGene.setWeight(weight);
                return;
            }
        }

        // check to see if this innovation has already been tried
        final NEATInnovation innovation = ((NEATPopulation) target
                .getPopulation()).getInnovations().findInnovation(neuron1ID,
                neuron2ID);

        // now create this link
        final NEATLinkGene linkGene = new NEATLinkGene(neuron1ID, neuron2ID,
                true, innovation.getInnovationID(), weight);
        target.getLinksChromosome().add(linkGene);
    }

    /**
     * Get the specified neuron's index.
     *
     * @param neuronID
     *            The neuron id to check for.
     * @return The index.
     */
    public int getElementPos(final SingleNEATGenome target, final long neuronID) {

        for (int i = 0; i < target.getNeuronsChromosome().size(); i++) {
            final NEATNeuronGene neuronGene = target.getNeuronsChromosome()
                    .get(i);
            if (neuronGene.getId() == neuronID) {
                return i;
            }
        }

        return -1;
    }

    /**
     * @return the owner
     */
    public EvolutionaryAlgorithm getOwner() {
        return this.owner;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void init(final EvolutionaryAlgorithm theOwner) {
        this.owner = theOwner;
    }

    /**
     * Determine if this is a duplicate link.
     *
     * @param fromNeuronID
     *            The from neuron id.
     * @param toNeuronID
     *            The to neuron id.
     * @return True if this is a duplicate link.
     */
    public boolean isDuplicateLink(final SingleNEATGenome target,
                                   final long fromNeuronID, final long toNeuronID) {
        for (final NEATLinkGene linkGene : target.getLinksChromosome()) {
            if ((linkGene.isEnabled())
                    && (linkGene.getFromNeuronID() == fromNeuronID)
                    && (linkGene.getToNeuronID() == toNeuronID)) {
                return true;
            }
        }

        return false;
    }

    /**
     * Determines if a neuron is still needed. If all links to/from a neuron
     * have been removed, then the neuron is no longer needed.
     *
     * @param target
     *            The target genome.
     * @param neuronID
     *            The neuron id to check for.
     * @return Returns true, if the neuron is still needed.
     */
    public boolean isNeuronNeeded(final SingleNEATGenome target, final long neuronID) {

        // do not remove bias or input neurons or output
        if (isStartingNeuron(target, neuronID)) {
            return true;
        }

        // Now check to see if the neuron is used in any links
        for (final NEATLinkGene gene : target.getLinksChromosome()) {
            final NEATLinkGene linkGene = gene;
            if (linkGene.getFromNeuronID() == neuronID) {
                return true;
            }
            if (linkGene.getToNeuronID() == neuronID) {
                return true;
            }
        }

        return false;
    }

    public boolean isStartingNeuron(final SingleNEATGenome target, final long neuronID) {
        for (final NEATNeuronGene gene : target.getNeuronsChromosome()) {
            if (gene.getId() == neuronID) {
                final NEATNeuronGene neuron = gene;
                if ((neuron.getNeuronType() == NEATNeuronType.Input)
                        || (neuron.getNeuronType() == NEATNeuronType.Bias)
                        || (neuron.getNeuronType() == NEATNeuronType.Output)) {
                    return true;
                }
            }
        }
        return false;
    }

    /**
     * Obtain the SingleNEATGenome that we will mutate. NEAT mutates the genomes in
     * place. So the parent and child genome must be the same literal object.
     * Throw an exception, if this is not the case.
     *
     * @param parents
     *            The parents.
     * @param parentIndex
     *            The parent index.
     * @param offspring
     *            The offspring.
     * @param offspringIndex
     *            The offspring index.
     * @return The genome that we will mutate.
     */
    public NEATEnsembleGenome obtainGenome(final Genome[] parents,
                                           final int parentIndex, final Genome[] offspring,
                                           final int offspringIndex) {
        offspring[offspringIndex] = this.getOwner().getPopulation()
                .getGenomeFactory().factor(parents[0]);
        return (NEATEnsembleGenome) offspring[offspringIndex];
    }

    /**
     * @return Returns 1, as NEAT mutations only produce one child.
     */
    @Override
    public int offspringProduced() {
        return 1;
    }

    /**
     * @return Returns 1, as mutations typically are asexual and only require a
     *         single parent.
     */
    @Override
    public int parentsNeeded() {
        return 1;
    }

    /**
     * Remove the specified neuron.
     *
     * @param target
     *            The target genome.
     * @param neuronID
     *            The neuron to remove.
     */
    public void removeNeuron(final SingleNEATGenome target, final long neuronID) {
        for (final NEATNeuronGene gene : target.getNeuronsChromosome()) {
            if (gene.getId() == neuronID) {
                target.getNeuronsChromosome().remove(gene);
                return;
            }
        }
    }
}
