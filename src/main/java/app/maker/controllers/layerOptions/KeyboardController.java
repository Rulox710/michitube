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
import javafx.scene.control.TitledPane;
import javafx.scene.control.Tooltip;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.HBox;

public class KeyboardController extends OptionLayerController {

    @FXML private Accordion keyboardOptionsRoot;
    @FXML private TitledPane titledPaneOff, titledPaneOn;
    @FXML private Button buttonHandOff, buttonHandOn;
    @FXML private Tooltip tooltipBHandOff, tooltipKeyboard, tooltipBHandOn;
    @FXML private Label labelHandOff, labelHandOn;
    @FXML private CheckBox checkboxKeyboard;
    @FXML private ImageView imagePreviewOff, imagePreviewOn;
    @FXML private HBox hboxHandOn;

    @Override
    public void initialize() {
        updateLanguage();

        optionsRoot = keyboardOptionsRoot;
        firstPane = titledPaneOff;
        super.initialize();

        updateEnabledState(checkboxKeyboard.isSelected());
        checkboxKeyboard.selectedProperty().addListener((obs, oldVal, newVal) -> {
            updateEnabledState(newVal);
        });
    }

    private void updateEnabledState(boolean enabled) {
        buttonHandOn.setDisable(!enabled);
        imagePreviewOn.setDisable(!enabled);
        hboxHandOn.setDisable(!enabled);
    }

    @FXML
    private void handleButtonClick(ActionEvent event) {
        Object source = event.getSource();
        ImageView imagePreview = (source == buttonHandOff)? imagePreviewOff: imagePreviewOn;
        File img = FXFileChooser.getImageChooser().showOpenDialog(null);
        if(img != null) {
            imagePreview.setImage(new Image(img.toURI().toString()));
            notifyObservers((char) getTweakID(), img);
        }
    }

    @Override
    public void updateLanguage() {
        titledPaneOff.setText(TranslationM.getTranslatedLabel("title_keyboard_off"));
        buttonHandOff.setText(TranslationM.getTranslatedLabel("button_select_image"));
        tooltipBHandOff.setText(TranslationM.getTranslatedLabel("tooltip_bhandoff"));
        labelHandOff.setText(TranslationM.getTranslatedLabel("label_handoff"));
        titledPaneOn.setText(TranslationM.getTranslatedLabel("title_keyboard_on"));
        checkboxKeyboard.setText(TranslationM.getTranslatedLabel("checkbox_keyboard_detection"));
        tooltipKeyboard.setText(TranslationM.getTranslatedLabel("tooltip_keyboard_detection"));
        buttonHandOn.setText(TranslationM.getTranslatedLabel("button_select_image"));
        tooltipBHandOn.setText(TranslationM.getTranslatedLabel("tooltip_bhandon"));
        labelHandOn.setText(TranslationM.getTranslatedLabel("label_handon"));
    }

}
