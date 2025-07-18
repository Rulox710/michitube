package app.maker.controllers.layerOptions;

import app.maker.controllers.AbstractController;
import app.maker.controllers.objects.Infos.Info;

import javafx.application.Platform;
import javafx.scene.control.Accordion;
import javafx.scene.control.TitledPane;

public abstract class OptionLayerController extends AbstractController {

    protected Accordion optionsRoot;
    protected TitledPane firstPane, lastExpanded;

    @Override
    public void initialize() {
        if(optionsRoot.getExpandedPane() == null && !optionsRoot.getPanes().isEmpty()) {
            lastExpanded = firstPane;
            optionsRoot.setExpandedPane(firstPane);
        }

        optionsRoot.expandedPaneProperty().addListener((obs, oldPane, newPane) -> {
            if (newPane == null) {
                Platform.runLater(() -> optionsRoot.setExpandedPane(lastExpanded));
            } else {
                lastExpanded = newPane;
            }
            notifyObservers('T', optionsRoot.getPanes().indexOf(lastExpanded));
        });
    }

    public final int getTweakID() {
        return optionsRoot.getPanes().indexOf(lastExpanded);
    }

    public abstract boolean readyToSave();

    public boolean setInfo(Info info) {
        return true;
    }

    public Info getInfo() {
        return null;
    }
}
