package app.maker.controllers.layerOptions;

import java.io.File;

import app.engine.readers.TranslationM;
import app.maker.FXFileChooser;
import app.maker.controllers.AbstractController;
import javafx.application.Platform;
import javafx.collections.ObservableList;
import javafx.css.PseudoClass;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.Accordion;
import javafx.scene.control.Button;
import javafx.scene.control.ColorPicker;
import javafx.scene.control.Label;
import javafx.scene.control.TitledPane;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.Pane;
import javafx.scene.paint.Color;

public class BackgroundController extends AbstractController {

    @FXML private Accordion backgroundOptionsRoot;
    @FXML private TitledPane tiledPaneImage, titledPaneColor;
    @FXML private Pane vboxColor;
    @FXML private ColorPicker colorPicker;
    @FXML private Label labelImage, labelColor;
    @FXML private Button buttonImage;
    @FXML private ImageView imagePreview;

    private TitledPane lastExpanded;

    @Override
    public void initialize() {
        updateLanguage();

        if(backgroundOptionsRoot.getExpandedPane() == null && !backgroundOptionsRoot.getPanes().isEmpty()) {
            lastExpanded = tiledPaneImage;
            backgroundOptionsRoot.setExpandedPane(tiledPaneImage);
        }

        backgroundOptionsRoot.expandedPaneProperty().addListener((obs, oldPane, newPane) -> {
            if (newPane == null) {
                Platform.runLater(() -> backgroundOptionsRoot.setExpandedPane(lastExpanded));
            } else {
                lastExpanded = newPane;
            }
        });
    }

    @FXML
    private void handleButtonClick(ActionEvent event) {
        File img = FXFileChooser.getImageChooser().showOpenDialog(null);
        if(img != null) {
            imagePreview.setImage(new Image(img.toURI().toString()));
            notifyObservers((char) 0, img.getPath());
        }
    }

    @Override
    public void updateLanguage() {
        tiledPaneImage.setText(TranslationM.getTranslatedLabel("title_background_image"));
        labelImage.setText(TranslationM.getTranslatedLabel("label_background_image"));
        buttonImage.setText(TranslationM.getTranslatedLabel("button_background_image"));

        titledPaneColor.setText(TranslationM.getTranslatedLabel("title_background_color"));
        vboxColor.getChildren().remove(colorPicker);
        ObservableList<Color> customColors = colorPicker.getCustomColors();
        colorPicker = new ColorPicker(colorPicker.getValue());
        colorPicker.getCustomColors().addAll(customColors);
        colorPicker.setMinHeight(Double.NEGATIVE_INFINITY);
        colorPicker.setMinWidth(Double.NEGATIVE_INFINITY);
        vboxColor.getChildren().add(0, colorPicker);
        labelColor.setText(TranslationM.getTranslatedLabel("label_background_color"));
    }
}