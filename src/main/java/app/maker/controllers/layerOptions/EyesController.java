package app.maker.controllers.layerOptions;

import java.io.File;

import app.engine.readers.TranslationM;
import app.maker.FXFileChooser;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.Accordion;
import javafx.scene.control.Button;
import javafx.scene.control.CheckBox;
import javafx.scene.control.Label;
import javafx.scene.control.Spinner;
import javafx.scene.control.SpinnerValueFactory;
import javafx.scene.control.TitledPane;
import javafx.scene.control.Tooltip;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.HBox;

public class EyesController extends OptionLayerController {

    @FXML private Accordion eyesOptionsRoot;
    @FXML private TitledPane titledPaneEyes, titledPaneBlink;
    @FXML private Label labelEyes, labelBlinkEvery, labelBlinkTime, labelBlink, labelBlinkEverySec, labelBlinkTimeSec;
    @FXML private Button buttonEyes, buttonBlink;
    @FXML private ImageView imagePreviewEyes, imagePreviewBlink;
    @FXML private Spinner<Integer> spinnerBlinkEvery, spinnerBlinkTime;
    @FXML private HBox hboxBlink;
    @FXML private CheckBox checkboxBlink;
    @FXML private Tooltip tooltipBlink;

    @Override
    public void initialize() {
        updateLanguage();

        optionsRoot = eyesOptionsRoot;
        firstPane = titledPaneEyes;
        super.initialize();

        spinnerBlinkEvery.setValueFactory(new SpinnerValueFactory.IntegerSpinnerValueFactory(1, 60, 10));
        spinnerBlinkTime.setValueFactory(new SpinnerValueFactory.IntegerSpinnerValueFactory(1, 10, 1));

        updateEnabledState(checkboxBlink.isSelected());
        checkboxBlink.selectedProperty().addListener((obs, oldVal, newVal) -> {
            updateEnabledState(newVal);
        });
    }

    private void updateEnabledState(boolean enabled) {
        labelBlinkEvery.setDisable(!enabled);
        spinnerBlinkEvery.setDisable(!enabled);
        labelBlinkEverySec.setDisable(!enabled);
        labelBlinkTime.setDisable(!enabled);
        spinnerBlinkEvery.setDisable(!enabled);
        labelBlinkTimeSec.setDisable(!enabled);
        spinnerBlinkTime.setDisable(!enabled);
        buttonBlink.setDisable(!enabled);
        hboxBlink.setDisable(!enabled);
        imagePreviewBlink.setDisable(!enabled);
    }

    @FXML
    private void handleButtonClick(ActionEvent event) {
        Object source = event.getSource();
        ImageView imagePreview = (source == buttonEyes)? imagePreviewEyes: imagePreviewBlink;
        File img = FXFileChooser.getImageChooser().showOpenDialog(null);
        if(img != null) {
            imagePreview.setImage(new Image(img.toURI().toString()));
            notifyObservers((char) getTweakID(), img);
        }
    }

    @Override
    public void updateLanguage() {
        titledPaneEyes.setText(TranslationM.getTranslatedLabel("title_eyes"));
        buttonEyes.setText(TranslationM.getTranslatedLabel("button_select_image"));
        labelEyes.setText(TranslationM.getTranslatedLabel("label_eyes"));

        titledPaneBlink.setText(TranslationM.getTranslatedLabel("title_blink"));
        checkboxBlink.setText(TranslationM.getTranslatedLabel("checkbox_blink"));
        tooltipBlink.setText(TranslationM.getTranslatedLabel("tooltip_blink"));
        labelBlinkEvery.setText(TranslationM.getTranslatedLabel("label_blink_every"));
        labelBlinkEverySec.setText(TranslationM.getTranslatedLabel("label_seconds"));
        labelBlinkTime.setText(TranslationM.getTranslatedLabel("label_blink_time"));
        labelBlinkTimeSec.setText(TranslationM.getTranslatedLabel("label_seconds"));
        buttonBlink.setText(TranslationM.getTranslatedLabel("button_select_image"));
        labelBlink.setText(TranslationM.getTranslatedLabel("label_blink"));
    }
}
