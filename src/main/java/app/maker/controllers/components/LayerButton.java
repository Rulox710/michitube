package app.maker.controllers.components;

import javafx.css.PseudoClass;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.image.*;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.Pane;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;

import java.io.File;
import java.io.IOException;

import app.engine.readers.TranslationM;
import app.maker.FXFileChooser;
import app.maker.controllers.AbstractController;

public class LayerButton extends AbstractController {

    @FXML private Pane stackpaneContainer, vboxButton, imageContainer;
    @FXML private Label labelLayer;
    @FXML private ImageView imagePreview;

    private String translationId = "label_layers";

    @FXML
    public void initialize() {
        stackpaneContainer.setOnMousePressed(this::handleClick);
        stackpaneContainer.setOnMouseEntered(e -> {
            imageContainer.pseudoClassStateChanged(PseudoClass.getPseudoClass("hover"), true);
        });
        stackpaneContainer.setOnMouseExited(e -> {
            imageContainer.pseudoClassStateChanged(PseudoClass.getPseudoClass("hover"), false);
        });
    }

    private void chooseImage() {
        File file = FXFileChooser.getImageChooser().showOpenDialog(null); // implementa tu file chooser
        if (file != null) {
            Image image = new Image(file.toURI().toString());
            imagePreview.setImage(image);
            notifyObservers('I', file);
        }
    }

    private void handleClick(MouseEvent e) {
        if(e.isPrimaryButtonDown()) {
            stackpaneContainer.pseudoClassStateChanged(PseudoClass.getPseudoClass("clicked"), true);
            notifyObservers('T', this);
        }
    }

    public void toggleSelect() {
        stackpaneContainer.pseudoClassStateChanged(PseudoClass.getPseudoClass("clicked"), false);
    }

    public void setTranslationId(String translationId) {
        this.translationId = translationId;
        updateLanguage();
    }

    public void updateLanguage() {
        labelLayer.setText(TranslationM.getTranslatedLabel(translationId));
    }
}
