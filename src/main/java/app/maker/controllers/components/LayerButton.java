package app.maker.controllers.components;

import javafx.css.PseudoClass;
import javafx.fxml.FXML;
import javafx.scene.control.Label;
import javafx.scene.control.Tooltip;
import javafx.scene.image.*;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.Pane;

import java.io.File;

import app.engine.readers.TranslationM;
import app.maker.controllers.AbstractController;

public class LayerButton extends AbstractController {

    @FXML private Pane stackpaneContainer, vboxButton, imageContainer;
    @FXML private Label labelLayer;
    @FXML private ImageView imagePreview;
    @FXML private Tooltip tooltipButton;

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

    private void setImage(File fimg) {
        if(fimg != null) {
            imagePreview.setImage(new Image(fimg.toURI().toString()));
        }
    }

    private void handleClick(MouseEvent e) {
        if(e.isPrimaryButtonDown()) {
            toggleSelect(true);
            notifyObservers('c', this);
        }
    }

    public void toggleSelect(boolean clicked) {
        stackpaneContainer.pseudoClassStateChanged(PseudoClass.getPseudoClass("clicked"), clicked);
    }

    public void setTranslationId(String translationId) {
        this.translationId = translationId;
        updateLanguage();
    }

    public void updateLanguage() {
        labelLayer.setText(TranslationM.getTranslatedLabel(translationId));
        Tooltip.install(stackpaneContainer, new Tooltip(TranslationM.getTranslatedLabel(translationId)));
    }

    @Override
    public void update(char event, Object data) {
        switch(event) {
            case (char)0: setImage((File) data);
            break;
            case 'c': toggleSelect(false);
            break;
        }
    }
}
