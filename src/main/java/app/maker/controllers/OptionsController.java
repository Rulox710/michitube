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

package app.maker.controllers;

import app.files.PropertiesM;
import app.files.TranslationM;

import javafx.application.Platform;
import javafx.fxml.FXML;
import javafx.scene.control.Label;
import javafx.scene.control.Spinner;
import javafx.scene.control.SpinnerValueFactory;
import javafx.scene.control.TitledPane;

public class OptionsController extends AbstractController  {

    @FXML private TitledPane titledPaneFPS;
    @FXML private Label labelFPS;
    @FXML private Spinner<Integer> fpsSpinner;

    @Override
    public void initialize() {
        updateLanguage();

        fpsSpinner.setValueFactory(new SpinnerValueFactory.IntegerSpinnerValueFactory(
            1, 120, Integer.parseInt(PropertiesM.getAppProperty("frames_per_second"))
        ));
        fpsSpinner.valueProperty().addListener((obs, oldValue, newValue) -> {
            notifyObservers('f', newValue);
        });
        //startCapture();
    }

    @Override
    public void updateLanguage() {
        Platform.runLater(() -> {
        titledPaneFPS.setText(TranslationM.getTranslatedLabel("title_fps"));
            labelFPS.setText(TranslationM.getTranslatedLabel("label_fps"));
            //labelFPSSec.setText(TranslationM.getTranslatedLabel("label_seconds"));
        });
    }
}
