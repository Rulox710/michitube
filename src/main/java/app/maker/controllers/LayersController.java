package app.maker.controllers;

import app.Ids;
import app.maker.controllers.components.LayerButton;
import app.maker.controllers.layerOptions.MouthController;
import app.maker.controllers.layerOptions.OptionLayerController;
import app.maker.controllers.objects.Infos.Info;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

import javafx.application.Platform;
import javafx.fxml.FXMLLoader;
import javafx.scene.control.Accordion;
import javafx.scene.control.ScrollPane;
import javafx.scene.layout.Pane;
import javafx.scene.layout.StackPane;

public class LayersController extends AbstractController {

    private int selectedLayer = 0;

    private final StackPane[] stackpaneLayers = new StackPane[Ids.values().length];
    private final LayerButton[] buttonControllers = new LayerButton[Ids.values().length];

    private final Accordion[] accordionLayers = new Accordion[Ids.values().length];
    private final OptionLayerController[] accordionController = new OptionLayerController[Ids.values().length];

    private final Pane parentContainer;
    private final ScrollPane optionsContainer;

    public void open() {
        MouthController mc = (MouthController) accordionController[Ids.MOUTH.ordinal()];
        mc.open();
    }

    public void close() {
        MouthController mc = (MouthController) accordionController[Ids.MOUTH.ordinal()];
        mc.close();
    }

    public LayersController(Pane parentContainer, ScrollPane optionsContainer) {
        this.parentContainer = parentContainer;
        this.optionsContainer = optionsContainer;
    }

    public boolean readyToSave() {
        for(int i = 0; i < accordionController.length; i++) {
            if(!accordionController[i].readyToSave()) return false;
        }
        return true;
    }

    public boolean setInfo(Ids id, Info info) {
        return accordionController[id.ordinal()].setInfo(info);
    }

    public Map<Ids, Info> getInfos() {
        final Map<Ids, Info> INFO_MAP = new HashMap<>(Ids.values().length-1);
        for(int i = 0; i < accordionController.length; i++) {
            Ids key = Ids.values()[i];
            Info infos = accordionController[i].getInfo();
            if(infos == null) continue;
            INFO_MAP.put(key, infos);

        }
        return INFO_MAP;
    }

    @Override
    public void initialize() {
        try {
            for(int i = 0; i < Ids.values().length; i++) {
                FXMLLoader loader = new FXMLLoader(getClass().getResource("/res/views/components/layer_button.fxml"));
                stackpaneLayers[i] = loader.load();
                stackpaneLayers[i].getStylesheets().add(getClass().getResource("/res/styles/layer_button.css").toExternalForm());
                buttonControllers[i] = loader.getController();
                buttonControllers[i].setTranslationId(String.format("button_%s", Ids.values()[i].getID()));
                parentContainer.getChildren().add(stackpaneLayers[i]);
                buttonControllers[i].addObserver(this);

                loader = new FXMLLoader(getClass().getResource(String.format("/res/views/layerOptions/%s_view.fxml", Ids.values()[i].getID())));
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
            for(int i = 0; i < Ids.values().length; i++) {
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

            case 'z': open();
            break;
        }
    }
}
