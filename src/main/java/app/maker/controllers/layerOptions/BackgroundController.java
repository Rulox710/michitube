package app.maker.controllers.layerOptions;

import java.io.File;

import app.engine.readers.TranslationM;
import app.maker.FXFileChooser;
import javafx.application.Platform;
import javafx.collections.ObservableList;
import javafx.css.PseudoClass;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.Accordion;
import javafx.scene.control.Button;
import javafx.scene.control.CheckBox;
import javafx.scene.control.ColorPicker;
import javafx.scene.control.Label;
import javafx.scene.control.TitledPane;
import javafx.scene.control.Tooltip;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Pane;
import javafx.scene.paint.Color;

public class BackgroundController extends OptionLayerController {

    @FXML private Accordion backgroundOptionsRoot;
    @FXML private TitledPane titledPaneImage, titledPaneColor;
    @FXML private Pane vboxColor;
    @FXML private ColorPicker colorPicker;
    @FXML private Label labelImage, labelColor;
    @FXML private Button buttonImage;
    @FXML private HBox hboxImage;
    @FXML private ImageView imagePreview;
    @FXML private Tooltip tooltipBImage/*, tooltipColor*/, tooltipCImage, tooltipCColor;
    @FXML private CheckBox checkboxImage, checkboxColor;

    @Override
    public void initialize() {
        updateLanguage();

        optionsRoot = backgroundOptionsRoot;
        firstPane = titledPaneImage;
        super.initialize();

        updateEnabledState(checkboxImage, checkboxImage.isSelected());
        checkboxImage.selectedProperty().addListener((obs, oldVal, newVal) -> {
            updateEnabledState(checkboxImage, newVal);
        });
        updateEnabledState(checkboxColor, checkboxColor.isSelected());
        checkboxColor.selectedProperty().addListener((obs, oldVal, newVal) -> {
            updateEnabledState(checkboxColor, newVal);
        });

        colorPicker.setOnAction(this::handleColorSelection);
    }

    private void updateEnabledState(CheckBox checkbox, boolean enabled) {
        if(checkbox == checkboxImage) {
            buttonImage.setDisable(!enabled);
            hboxImage.setDisable(!enabled);
            imagePreview.setDisable(!enabled);
        } else {
            colorPicker.setDisable(!enabled);
            Color color = (enabled)? colorPicker.getValue(): Color.LIGHTGRAY;
            notifyObservers('C', color);
        }
    }

    @FXML
    private void handleButtonClick(ActionEvent event) {
        File img = FXFileChooser.getImageChooser().showOpenDialog(null);
        if(img != null) {
            imagePreview.setImage(new Image(img.toURI().toString()));
            notifyObservers((char) 0, img);
        }
    }

    private void handleColorSelection(ActionEvent event) {
        notifyObservers('C', colorPicker.getValue());
    }

    @Override
    public void updateLanguage() {
        titledPaneImage.setText(TranslationM.getTranslatedLabel("title_background_image"));
        checkboxImage.setText(TranslationM.getTranslatedLabel("checkbox_background_image"));
        tooltipCImage.setText(TranslationM.getTranslatedLabel("tooltip_background_cimage"));
        labelImage.setText(TranslationM.getTranslatedLabel("label_background_image"));
        buttonImage.setText(TranslationM.getTranslatedLabel("button_select_image"));
        tooltipBImage.setText(TranslationM.getTranslatedLabel("tooltip_background_bimage"));

        titledPaneColor.setText(TranslationM.getTranslatedLabel("title_background_color"));
        checkboxColor.setText(TranslationM.getTranslatedLabel("checkbox_background_color"));
        tooltipCColor.setText(TranslationM.getTranslatedLabel("tooltip_background_ccolor"));
        vboxColor.getChildren().remove(colorPicker);
        ObservableList<Color> customColors = colorPicker.getCustomColors();
        colorPicker = new ColorPicker(colorPicker.getValue());
        colorPicker.getCustomColors().addAll(customColors);
        colorPicker.setMinHeight(Double.NEGATIVE_INFINITY);
        colorPicker.setMinWidth(Double.NEGATIVE_INFINITY);
        vboxColor.getChildren().add(1, colorPicker);
        Tooltip.install(colorPicker, new Tooltip(TranslationM.getTranslatedLabel("tooltip_background_bcolor")));
        //tooltipColor.setText(TranslationM.getTranslatedLabel("tooltip_background_color"));
        labelColor.setText(TranslationM.getTranslatedLabel("label_background_color"));
    }
}
