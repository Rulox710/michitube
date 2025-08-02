package app.maker.controllers;

import app.engine.DeltaTimeManager;
import app.files.PropertiesM;
import app.files.TranslationM;

import javafx.application.Platform;
import javafx.fxml.FXML;
import javafx.scene.control.Label;
import javafx.scene.control.Spinner;
import javafx.scene.control.SpinnerValueFactory;
import javafx.scene.control.TitledPane;

public class OptionsController extends AbstractController  {

    @FXML private TitledPane titledPaneFPS;
    @FXML private Label labelFPS;
    @FXML private Spinner<Integer> fpsSpinner;

    @Override
    public void initialize() {
        updateLanguage();

        fpsSpinner.setValueFactory(new SpinnerValueFactory.IntegerSpinnerValueFactory(
            1, 120, Integer.parseInt(PropertiesM.getAppProperty("frames_per_second"))
        ));
        fpsSpinner.valueProperty().addListener((obs, oldValue, newValue) -> {
            notifyObservers('f', newValue);
        });
        //startCapture();
    }

    @Override
    public void updateLanguage() {
        Platform.runLater(() -> {
        titledPaneFPS.setText(TranslationM.getTranslatedLabel("title_fps"));
            labelFPS.setText(TranslationM.getTranslatedLabel("label_fps"));
            //labelFPSSec.setText(TranslationM.getTranslatedLabel("label_seconds"));
        });
    }
}
