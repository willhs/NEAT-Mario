package will.neat.encog.gui;

import javafx.application.Platform;
import javafx.collections.FXCollections;
import javafx.scene.control.ComboBox;
import org.encog.ml.ea.genome.Genome;
import org.encog.ml.ea.train.basic.TrainEA;
import org.encog.ml.train.MLTrain;
import org.encog.ml.train.strategy.Strategy;

import java.util.List;
import java.util.stream.Collectors;

/**
 * Created by Will on 2/09/2016.
 */
public class UpdateComboBoxChamps implements Strategy {

    private TrainEA neat;
    private ComboBox<Genome> comboBox;

    public UpdateComboBoxChamps(ComboBox<Genome> comboBox) {
        this.comboBox = comboBox;
    }

    @Override
    public void init(MLTrain train) {
        this.neat = (TrainEA) train;
    }

    @Override
    public void preIteration() {
        if (neat.getIteration() > 0) {
            Platform.runLater(() -> updatePane());
        }
    }

    private void updatePane() {
        // to use later
        int selectedIndex = comboBox.getSelectionModel().getSelectedIndex();

        List<Genome> genomeList = neat.getPopulation().getSpecies().stream()
                        .map(s -> s.getLeader())
                        .collect(Collectors.toList());
        comboBox.setItems(FXCollections.observableArrayList(genomeList));

        comboBox.getSelectionModel().select(selectedIndex);
    }

    @Override
    public void postIteration() {

    }
}
