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

import java.io.File;

import app.Sections.KEYS;
import app.files.TranslationM;
import app.maker.FXFileChooser;
import app.maker.controllers.objects.Infos.Info;
import app.maker.controllers.objects.builders.BackgroundInfoBuilder;

import javafx.application.Platform;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.Accordion;
import javafx.scene.control.CheckBox;
import javafx.scene.control.ColorPicker;
import javafx.scene.control.Label;
import javafx.scene.control.TitledPane;
import javafx.scene.control.Tooltip;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.Pane;
import javafx.scene.paint.Color;

public class MouseController extends OptionLayerController {

    @FXML private Accordion mouseOptionsRoot;
    @FXML private TitledPane titledPaneArea, titledPaneHand, titledPaneMouse;

    @FXML private ImageView imagePreviewMouse;
    @FXML private CheckBox checkboxMovement;
    @FXML private Tooltip tooltipMovement;
    @FXML private ColorPicker colorPicker;
    @FXML private Pane vboxHand;
    @FXML private Label labelHand, labelArea;

    /**
     * {@inheritDoc}
     */
    @Override
    public void initialize() {
        updateLanguage();

        titledPaneMouse.setDisable(true);

        optionsRoot = mouseOptionsRoot;
        firstPane = titledPaneArea;
        super.initialize();

        checkboxMovement.selectedProperty().addListener((obs, oldVal, newVal) -> {
            //System.out.println(checkboxMovement.selectedProperty().getValue());
        });
    }

    @Override
    protected void handleError(int index, boolean error, boolean notify) {}

    @FXML
    private void handleButtonClick(ActionEvent event) {
        File img = FXFileChooser.getImageChooser().showOpenDialog(null);
        if(img != null) {
            imagePreviewMouse.setImage(new Image(img.toURI().toString()));
            notifyObservers((char) getTweakID(), img);
        }
    }

    @FXML
    private void handleColorSelection(ActionEvent event) {
        notifyObservers('H', colorPicker.getValue());
    }

    @Override
    public boolean readyToSave() {
        return true;
    }

    @Override
    public Info getInfo() {
        BackgroundInfoBuilder builder = new BackgroundInfoBuilder();

        builder.setUsage(checkboxMovement.selectedProperty().getValue());

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
    public boolean setInfo(Info info) {
        boolean result = true;

        checkboxMovement.selectedProperty().setValue(info.getBoolean(KEYS.IMAGE));

        String hex = info.getString(KEYS.COLOR);
        if(hex.equals("")) result = false;
        try {
            double r = (double)(Integer.valueOf(hex.substring(1, 3), 16)) / 255;
            double g = (double)(Integer.valueOf(hex.substring(3, 5), 16)) / 255;
            double b = (double)(Integer.valueOf(hex.substring(5, 7), 16)) / 255;
            double a = (double)(Integer.valueOf(hex.substring(7, 9), 16)) / 255;
            Platform.runLater(() -> {
                colorPicker.setValue(new Color(r, g, b, a));
                notifyObservers('H', colorPicker.getValue());
            });
        } catch(StringIndexOutOfBoundsException | NumberFormatException e) {
            result = false;
        }

        return result;
    }

    @Override
    public void updateLanguage() {
        titledPaneMouse.setText(TranslationM.getTranslatedLabel("title_mouse"));
        checkboxMovement.setText(TranslationM.getTranslatedLabel("checkbox_movement"));
        tooltipMovement.setText(TranslationM.getTranslatedLabel("tooltip_movement"));

        titledPaneHand.setText(TranslationM.getTranslatedLabel("title_hand"));
        vboxHand.getChildren().remove(colorPicker);
        ObservableList<Color> customColors = colorPicker.getCustomColors();
        colorPicker = new ColorPicker(colorPicker.getValue());
        colorPicker.getCustomColors().addAll(customColors);
        colorPicker.setMinHeight(Double.NEGATIVE_INFINITY);
        colorPicker.setMinWidth(Double.NEGATIVE_INFINITY);
        vboxHand.getChildren().add(0, colorPicker);
        Tooltip.install(colorPicker, new Tooltip(TranslationM.getTranslatedLabel("tooltip_background_bcolor")));
        colorPicker.setOnAction(this::handleColorSelection);
        labelHand.setText(TranslationM.getTranslatedLabel("label_hand"));

        titledPaneArea.setText(TranslationM.getTranslatedLabel("title_area"));
        labelArea.setText(TranslationM.getTranslatedLabel("label_area"));
    }
}
