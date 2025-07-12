package app.maker.controllers;

import java.io.File;
import java.io.IOException;

import app.engine.Observer;
import app.maker.controllers.components.LayerButton;
import app.maker.controllers.layerOptions.OptionLayerController;
import javafx.application.Platform;
import javafx.fxml.FXMLLoader;
import javafx.scene.control.Accordion;
import javafx.scene.control.ScrollPane;
import javafx.scene.layout.Pane;
import javafx.scene.layout.StackPane;
import javafx.scene.paint.Color;

public class LayersController extends AbstractController implements Observer {

    private final int LAYERS = 7;
    private int selectedLayer = 0;

    private StackPane[] stackpaneLayers = new StackPane[LAYERS];
    private LayerButton[] buttonControllers = new LayerButton[LAYERS];

    private Accordion[] accordionLayers = new Accordion[LAYERS];
    private OptionLayerController[] accordionController = new OptionLayerController[LAYERS];

    private final String[] IDS = {"background", "body", "eyes", "mouth", "table", "keyboard", "mouse"};

    private Pane parentContainer;
    private ScrollPane optionsContainer;

    public LayersController(Pane parentContainer, ScrollPane optionsContainer) {
        this.parentContainer = parentContainer;
        this.optionsContainer = optionsContainer;
    }

    @Override
    public void initialize() {
        try {
            for(int i = 0; i < LAYERS; i++) {
                FXMLLoader loader = new FXMLLoader(getClass().getResource("/res/views/components/layer_button.fxml"));
                stackpaneLayers[i] = loader.load();
                stackpaneLayers[i].getStylesheets().add(getClass().getResource("/res/styles/layer_button.css").toExternalForm());
                buttonControllers[i] = loader.getController();
                buttonControllers[i].setTranslationId(String.format("button_%s", IDS[i]));
                parentContainer.getChildren().add(stackpaneLayers[i]);
                buttonControllers[i].addObserver(this);

                loader = new FXMLLoader(getClass().getResource(String.format("/res/views/layerOptions/%s_view.fxml", IDS[i%6])));
                accordionLayers[i] = loader.load();
                accordionLayers[i].getStylesheets().add(getClass().getResource("/res/styles/image_preview.css").toExternalForm());
                if(i == 3)
                    accordionLayers[i].getStylesheets().add(getClass().getResource("/res/styles/mic_style.css").toExternalForm());
                accordionController[i] = loader.getController();
                accordionController[i].addObserver(this);
                accordionController[i].addObserver(buttonControllers[i]);
            }
            for(int i = 0; i < buttonControllers.length; i++) {
                for(int j = 0; j < buttonControllers.length; j++) {
                    if(j != i) buttonControllers[i].addObserver(buttonControllers[j]);
                }
            }
            optionsContainer.setContent(accordionLayers[0]);
            buttonControllers[0].toggleSelect(true);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void updateLanguage() {
        Platform.runLater(() -> {
            for(int i = 0; i < LAYERS; i++) {
                buttonControllers[i].updateLanguage();
                accordionController[i].updateLanguage();
            }
        });
    }

    @Override
    public void update(char event, Object data) {
        switch(event) {
            case 'c':
                for(int i = 0; i < buttonControllers.length; i++) {
                    if(buttonControllers[i] == data) {
                        selectedLayer = i;
                        Platform.runLater(() -> {
                            optionsContainer.setContent(accordionLayers[selectedLayer]);
                        });
                        int tweakID = accordionController[selectedLayer].getTweakID();
                        notifyObservers((char)selectedLayer, tweakID);
                        break;
                    }
                }
            break;
            case 'C': notifyObservers('C', data);
            break;
            case 'T':
                int tweakID = accordionController[selectedLayer].getTweakID();
                notifyObservers((char)selectedLayer, tweakID);
            break;
            case (char)0:
            case (char)1: notifyObservers('i', data);
            break;
        }
    }
}
