package app.maker.controllers.layerOptions;

import app.files.TranslationM;
import app.maker.FXFileChooser;
import app.maker.controllers.objects.Infos.Info;
import app.maker.controllers.objects.builders.BackgroundInfoBuilder;

import java.io.File;
import java.net.URI;

import javafx.application.Platform;
import javafx.collections.ObservableList;
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
    public boolean readyToSave() {
        if(checkboxImage.selectedProperty().getValue() && imagePreview == null)
            return false;
        return true;
    }

    @Override
    public boolean setInfo(Info info) {
        boolean result = true;

        if(info.boolParams[0] && info.path[0].length() != 0){
            imagePreview.setImage(new Image(info.path[0]));
            notifyObservers('l', new File(URI.create(info.path[0])));
        } else result = false;

        if(info.boolParams[1]) {
            String hex = info.color;
            if(hex.equals("")) result = false;

            try {
                double r = (double)(Integer.valueOf(hex.substring(1, 3), 16)) / 255;
                double g = (double)(Integer.valueOf(hex.substring(3, 5), 16)) / 255;
                double b = (double)(Integer.valueOf(hex.substring(5, 7), 16)) / 255;
                double a = (double)(Integer.valueOf(hex.substring(7, 9), 16)) / 255;
                Platform.runLater(() -> {
                    colorPicker.setValue(new Color(r, g, b, a));
                    checkboxColor.selectedProperty().setValue(info.boolParams[1]);
                });
            } catch(StringIndexOutOfBoundsException | NumberFormatException e) {
                result = false;
                checkboxColor.selectedProperty().setValue(info.boolParams[1]);
            }

        }

        return result;
    }

    @Override
    public Info getInfo() {
        BackgroundInfoBuilder builder = new BackgroundInfoBuilder();
        builder.setUsage(checkboxImage.selectedProperty().getValue());
        builder.setUsage(checkboxColor.selectedProperty().getValue());

        String hexWithAlpha = String.format("#%02x%02x%02x%02x",
            (int) Math.round(colorPicker.getValue().getRed() * 255),
            (int) Math.round(colorPicker.getValue().getGreen() * 255),
            (int) Math.round(colorPicker.getValue().getBlue() * 255),
            (int) Math.round(colorPicker.getValue().getOpacity() * 255)
        );
        builder.setColor(hexWithAlpha);

        return builder.getResult();
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
        colorPicker.setOnAction(this::handleColorSelection);

        //tooltipColor.setText(TranslationM.getTranslatedLabel("tooltip_background_color"));
        labelColor.setText(TranslationM.getTranslatedLabel("label_background_color"));
    }
}
