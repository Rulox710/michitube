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

import app.Sections.KEYS;
import app.fileUtils.ImageConverter;
import app.files.TranslationM;
import app.maker.FXFileChooser;
import app.maker.controllers.objects.Infos.Info;
import app.maker.controllers.objects.builders.EyesInfoBuilder;

import java.awt.image.BufferedImage;
import java.io.File;
import java.net.URI;
import java.nio.file.Path;
import java.nio.file.Paths;

import javafx.css.PseudoClass;
import javafx.embed.swing.SwingFXUtils;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.Accordion;
import javafx.scene.control.Button;
import javafx.scene.control.CheckBox;
import javafx.scene.control.Label;
import javafx.scene.control.Spinner;
import javafx.scene.control.SpinnerValueFactory;
import javafx.scene.control.TitledPane;
import javafx.scene.control.Tooltip;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.HBox;

public class EyesController extends OptionLayerController {

    @FXML private Accordion eyesOptionsRoot;
    @FXML private TitledPane titledPaneEyes, titledPaneBlink;
    @FXML private Label labelEyes, labelBlinkEvery, labelBlinkTime, labelBlink, labelBlinkEverySec, labelBlinkTimeSec;
    @FXML private Button buttonEyes, buttonBlink;
    @FXML private ImageView imagePreviewEyes, imagePreviewBlink;
    @FXML private Spinner<Integer> spinnerBlinkEvery, spinnerBlinkTime;
    @FXML private HBox hboxEyes, hboxBlink;
    @FXML private CheckBox checkboxBlink;
    @FXML private Tooltip tooltipBlink;

    /**
     * {@inheritDoc}
     */
    @Override
    public void initialize() {
        hasError = new boolean[2]; // 0: Eyes, 1: Blink
        updateLanguage();

        optionsRoot = eyesOptionsRoot;
        firstPane = titledPaneEyes;
        super.initialize();

        spinnerBlinkEvery.setValueFactory(new SpinnerValueFactory.IntegerSpinnerValueFactory(1, 60, 10));
        spinnerBlinkTime.setValueFactory(new SpinnerValueFactory.IntegerSpinnerValueFactory(1, 10, 1));

        updateEnabledState(checkboxBlink.isSelected());
        checkboxBlink.selectedProperty().addListener((obs, oldVal, newVal) -> {
            updateEnabledState(newVal);
        });
    }

    @Override
    protected void handleError(int index, boolean error, boolean notify) {
        index = index%2;
        hasError[index] = error;

        TitledPane currentPane;
        HBox currentHBox;
        switch(index) {
            case 0:
            default:
                currentPane = titledPaneEyes;
                currentHBox = hboxEyes;
            break;

            case 1:
                currentPane = titledPaneBlink;
                currentHBox = hboxBlink;
            break;
        }
        currentPane.pseudoClassStateChanged(PseudoClass.getPseudoClass("error"), hasError[index]);
        currentHBox.pseudoClassStateChanged(PseudoClass.getPseudoClass("error"), hasError[index]);

        if(notify) {
            boolean anyError = false;
            for(boolean b: hasError) if(b) {
                anyError = true;
                break;
            }
            notifyObservers('e', anyError);
        }
    }

    private void updateEnabledState(boolean enabled) {
        labelBlinkEvery.setDisable(!enabled);
        spinnerBlinkEvery.setDisable(!enabled);
        labelBlinkEverySec.setDisable(!enabled);
        labelBlinkTime.setDisable(!enabled);
        spinnerBlinkEvery.setDisable(!enabled);
        labelBlinkTimeSec.setDisable(!enabled);
        spinnerBlinkTime.setDisable(!enabled);
        buttonBlink.setDisable(!enabled);
        hboxBlink.setDisable(!enabled);
        imagePreviewBlink.setDisable(!enabled);

        if(!enabled) handleError(1, false, true);
    }

    @FXML
    private void handleButtonClick(ActionEvent event) {
        Object source = event.getSource();
        ImageView imagePreview = source == buttonEyes? imagePreviewEyes: imagePreviewBlink;
        int index = source == buttonEyes? 0: 1;

        File file = FXFileChooser.getImageChooser().showOpenDialog(null);
        if(file != null) {
            Image img = new Image(file.toURI().toString());
            imagePreview.setImage(img);
            notifyObservers((char) getTweakID(), file);

            handleError(index, false, true);
        }
    }

    @Override
    public boolean readyToSave() {
        boolean hasBeenNotified = false;

        if(imagePreviewEyes.getImage() == null) {
            handleError(0, true, !hasBeenNotified);
            hasBeenNotified = true;
        }
        if(checkboxBlink.selectedProperty().getValue() && imagePreviewBlink.getImage() == null) {
            handleError(1, true, !hasBeenNotified);
            hasBeenNotified = true;
        }

        return !hasBeenNotified;
    }

    @Override
    public boolean setInfo(Info info) {
        boolean result = true;
        Path relativePath, fullPath;
        URI fullUri;
        String rle;
        ImageConverter rleConverter;
        Image img = null;

        if(!info.getString(KEYS.PATH_0).isEmpty()) {
            relativePath = Paths.get(info.getString(KEYS.PATH_0));
            fullPath = basePath.resolve(relativePath);
            fullUri = fullPath.toUri();
            img = new Image(fullUri.toString());
        } else if(!info.getString(KEYS.RLE_0).isEmpty()) {
            rle = info.getString(KEYS.RLE_0);
            rleConverter = new ImageConverter();
            rleConverter.setRLE(rle, false);
            BufferedImage bImage = rleConverter.convertRLEtoImage();
            if(bImage != null) img = SwingFXUtils.toFXImage(bImage, null);
        }
        if(img != null) {
            imagePreviewEyes.setImage(img);
            notifyObservers('l', img);
        } else {
            result = false;
            imagePreviewEyes.setImage(null);
            notifyObservers('l', null);
        }

        spinnerBlinkEvery.getValueFactory().setValue(info.getInt(KEYS.TIMETO));
        spinnerBlinkTime.getValueFactory().setValue(info.getInt(KEYS.TIMEBLINK));
        checkboxBlink.selectedProperty().setValue(info.getBoolean(KEYS.USE));
        if(!info.getBoolean(KEYS.USE)) {
            result = false;
            imagePreviewBlink.setImage(null);
        } else {
            img = null;
            if(!info.getString(KEYS.PATH_1).isEmpty()) {
                relativePath = Paths.get(info.getString(KEYS.PATH_1));
                fullPath = basePath.resolve(relativePath);
                fullUri = fullPath.toUri();
                img = new Image(fullUri.toString());
            } else if(!info.getString(KEYS.RLE_1).isEmpty()) {
                rle = info.getString(KEYS.RLE_1);
                rleConverter = new ImageConverter();
                rleConverter.setRLE(rle, false);
                BufferedImage bImage = rleConverter.convertRLEtoImage();
                if(bImage != null) img = SwingFXUtils.toFXImage(bImage, null);
            }
            if(img != null) {
                imagePreviewBlink.setImage(img);
                notifyObservers('l', img);
            } else {
                result = false;
                imagePreviewBlink.setImage(null);
            }
        }

        for(int i = 0 ; hasError.length > i; i++) handleError(i, false, false);
        notifyObservers('e', false);

        return result;
    }

    @Override
    public Info getInfo() {
        EyesInfoBuilder builder = new EyesInfoBuilder();
        builder.setUsage(checkboxBlink.selectedProperty().getValue());
        builder.setIntParam(spinnerBlinkEvery.getValue());
        builder.setIntParam(spinnerBlinkTime.getValue());

        return builder.getResult();
    }

    @Override
    public void updateLanguage() {
        titledPaneEyes.setText(TranslationM.getTranslatedLabel("title_eyes"));
        buttonEyes.setText(TranslationM.getTranslatedLabel("button_select_image"));
        labelEyes.setText(TranslationM.getTranslatedLabel("label_eyes"));

        titledPaneBlink.setText(TranslationM.getTranslatedLabel("title_blink"));
        checkboxBlink.setText(TranslationM.getTranslatedLabel("checkbox_blink"));
        tooltipBlink.setText(TranslationM.getTranslatedLabel("tooltip_blink"));
        labelBlinkEvery.setText(TranslationM.getTranslatedLabel("label_blink_every"));
        labelBlinkEverySec.setText(TranslationM.getTranslatedLabel("label_seconds"));
        labelBlinkTime.setText(TranslationM.getTranslatedLabel("label_blink_time"));
        labelBlinkTimeSec.setText(TranslationM.getTranslatedLabel("label_seconds"));
        buttonBlink.setText(TranslationM.getTranslatedLabel("button_select_image"));
        labelBlink.setText(TranslationM.getTranslatedLabel("label_blink"));
    }
}
