/*
 * Copyright 2025 Raúl N. Valdés
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *  http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package app.maker.controllers.layerOptions;

import java.nio.file.Path;
import java.nio.file.Paths;

import app.files.PropertiesM;
import app.maker.controllers.AbstractController;
import app.maker.controllers.objects.Infos.Info;

import javafx.application.Platform;
import javafx.scene.control.Accordion;
import javafx.scene.control.TitledPane;

public abstract class OptionLayerController extends AbstractController {

    protected Accordion optionsRoot;
    protected TitledPane firstPane, lastExpanded;
    protected Path basePath;

    protected boolean[] hasError;

    @Override
    public void initialize() {
        basePath = Paths.get(PropertiesM.getAppProperty("default_dir"));

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

    protected abstract void handleError(int index, boolean error, boolean notify);

    public abstract boolean readyToSave();

    public boolean setInfo(Info info) {
        return true;
    }

    public Info getInfo() {
        return null;
    }
}
