package app.maker.controllers;

import java.io.IOException;

import app.engine.Observer;
import app.maker.controllers.components.LayerButton;
import javafx.application.Platform;
import javafx.fxml.FXMLLoader;
import javafx.scene.control.Accordion;
import javafx.scene.control.ScrollPane;
import javafx.scene.layout.Pane;
import javafx.scene.layout.StackPane;

public class LayersController extends AbstractController implements Observer {

    private final int LAYERS = 7, SELECTED_LAYER = 0;

    private StackPane[] stackpaneLayers = new StackPane[LAYERS];
    private LayerButton[] buttonControllers = new LayerButton[LAYERS];
    
    private Accordion[] accordionLayers = new Accordion[LAYERS];
    private AbstractController[] accordionController = new AbstractController[LAYERS];

    private final String[] IDS = {"background", "body", "eyes", "mouth", "table", "mouse", "keyboard"},
    FXML_NAMES = {"Background"};

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

                loader = new FXMLLoader(getClass().getResource(String.format("/res/views/layerOptions/%s_view.fxml", FXML_NAMES[0])));
                accordionLayers[i] = loader.load();
                accordionController[i] = loader.getController();
                optionsContainer.setContent(accordionLayers[i]);
            }
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
            case 'T':
                for(int i = 0; i < buttonControllers.length; i++) {
                    if(buttonControllers[i] != data) {
                        buttonControllers[i].toggleSelect();
                    } else {
                        final int num = i;
                        Platform.runLater(() -> {
                            optionsContainer.setContent(accordionLayers[num]);
                        });
                        // notifyObservers('L', selectedLayer);
                        // bottomPanel.revalidate();
                        // bottomPanel.repaint();
                    }
                }
                break;
            case 'I':
                notifyObservers((char) SELECTED_LAYER, data);
                break;
        }
    }
  
}
