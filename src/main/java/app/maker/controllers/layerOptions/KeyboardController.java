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
import app.maker.controllers.objects.builders.BasicInfoBuilder;

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
import javafx.scene.control.TitledPane;
import javafx.scene.control.Tooltip;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.HBox;

public class KeyboardController extends OptionLayerController {

    @FXML private Accordion keyboardOptionsRoot;
    @FXML private TitledPane titledPaneOff, titledPaneOn, titledPaneKeyboard;
    @FXML private Button buttonHandOff, buttonHandOn, buttonKeyboard;
    @FXML private Tooltip tooltipBHandOff, tooltipKeyboardDetection, tooltipBHandOn, tooltipBKeyboard;
    @FXML private Label labelHandOff, labelHandOn, labelKeyboard;
    @FXML private CheckBox checkboxKeyboardDetection;
    @FXML private ImageView imagePreviewOff, imagePreviewOn, imagePreviewKeyboard;
    @FXML private HBox hboxHandOff, hboxHandOn, hboxKeyboard;

    /**
     * {@inheritDoc}
     */
    @Override
    public void initialize() {
        hasError = new boolean[3]; // 0: Hand Off, 1: Keyboard, 2: Hand On
        updateLanguage();

        optionsRoot = keyboardOptionsRoot;
        firstPane = titledPaneOff;
        super.initialize();

        updateEnabledState(checkboxKeyboardDetection, checkboxKeyboardDetection.isSelected());
        checkboxKeyboardDetection.selectedProperty().addListener((obs, oldVal, newVal) -> {
            updateEnabledState(checkboxKeyboardDetection, newVal);
        });
    }

    @Override
    protected void handleError(int index, boolean error, boolean notify) {
        index = index%3;
        hasError[index] = error;

        TitledPane currentPane;
        HBox currentHBox;
        switch(index) {
            case 0:
            default:
                currentPane = titledPaneOff;
                currentHBox = hboxHandOff;
            break;

            case 1:
                currentPane = titledPaneKeyboard;
                currentHBox = hboxKeyboard;
            break;

            case 2:
                currentPane = titledPaneOn;
                currentHBox = hboxHandOn;
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

    private void updateEnabledState(CheckBox element, boolean enabled) {
        if(element == checkboxKeyboardDetection) {
            buttonHandOn.setDisable(!enabled);
            imagePreviewOn.setDisable(!enabled);
            hboxHandOn.setDisable(!enabled);

            if(!enabled) handleError(2, false, true);
        }
    }

    @FXML
    private void handleButtonClick(ActionEvent event) {
        Object source = event.getSource();
        ImageView imagePreview;
        int index;
        if(source == buttonHandOff) {
            imagePreview = imagePreviewOff;
            index = 0;
        } else if(source == buttonHandOn) {
            imagePreview = imagePreviewOn;
            index = 2;
        } else {
            imagePreview = imagePreviewKeyboard;
            index = 1;
        }

        File file = FXFileChooser.getImageChooser().showOpenDialog(null);
        if(file != null) {
            Image img = new Image(file.toURI().toString());
            imagePreview.setImage(img);
            notifyObservers((char) getTweakID(), img);

            handleError(index, false, true);
        }
    }

    @Override
    public boolean readyToSave() {
        boolean hasBeenNotified = false;

        if(imagePreviewOff.getImage() == null) {
            handleError(0, true, !hasBeenNotified);
            hasBeenNotified = true;
        }
        if(checkboxKeyboardDetection.selectedProperty().getValue() && imagePreviewOn.getImage() == null) {
            handleError(2, true, !hasBeenNotified);
            hasBeenNotified = true;
        }
        if(imagePreviewKeyboard.getImage() == null) {
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
            imagePreviewOff.setImage(img);
            notifyObservers('l', img);
        } else {
            result = false;
            imagePreviewOff.setImage(null);
            notifyObservers('l', null);
        }

        if(!info.getString(KEYS.PATH_2).isEmpty()) {
            relativePath = Paths.get(info.getString(KEYS.PATH_2));
            fullPath = basePath.resolve(relativePath);
            fullUri = fullPath.toUri();
            img = new Image(fullUri.toString());
        } else if(!info.getString(KEYS.RLE_2).isEmpty()) {
            rle = info.getString(KEYS.RLE_2);
            rleConverter = new ImageConverter();
            rleConverter.setRLE(rle, false);
            BufferedImage bImage = rleConverter.convertRLEtoImage();
            if(bImage != null) img = SwingFXUtils.toFXImage(bImage, null);
        }
        if(img != null) {
            imagePreviewKeyboard.setImage(img);
        } else {
            result = false;
            imagePreviewKeyboard.setImage(null);
        }

        checkboxKeyboardDetection.selectedProperty().setValue(info.getBoolean(KEYS.USE));
        if(!info.getBoolean(KEYS.USE)) {
            result = false;
            imagePreviewOn.setImage(null);
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
                imagePreviewOn.setImage(img);
            } else {
                result = false;
                imagePreviewOn.setImage(null);
            }
        }

        for(int i = 0 ; hasError.length > i; i++) handleError(i, false, false);
        notifyObservers('e', false);

        return result;
    }

    public Info getInfo() {
        BasicInfoBuilder builder = new BasicInfoBuilder();
        builder.setUsage(checkboxKeyboardDetection.selectedProperty().getValue());

        return builder.getResult();
    }

    @Override
    public void updateLanguage() {
        titledPaneOff.setText(TranslationM.getTranslatedLabel("title_keyboard_off"));
        buttonHandOff.setText(TranslationM.getTranslatedLabel("button_select_image"));
        tooltipBHandOff.setText(TranslationM.getTranslatedLabel("tooltip_bhandoff"));
        labelHandOff.setText(TranslationM.getTranslatedLabel("label_handoff"));

        titledPaneKeyboard.setText(TranslationM.getTranslatedLabel("title_keyboard"));
        buttonKeyboard.setText(TranslationM.getTranslatedLabel("button_select_image"));
        tooltipBKeyboard.setText(TranslationM.getTranslatedLabel("tooltip_bkeyboard"));
        labelKeyboard.setText(TranslationM.getTranslatedLabel("label_keyboard"));

        titledPaneOn.setText(TranslationM.getTranslatedLabel("title_keyboard_on"));
        checkboxKeyboardDetection.setText(TranslationM.getTranslatedLabel("checkbox_keyboard_detection"));
        tooltipKeyboardDetection.setText(TranslationM.getTranslatedLabel("tooltip_keyboard_detection"));
        buttonHandOn.setText(TranslationM.getTranslatedLabel("button_select_image"));
        tooltipBHandOn.setText(TranslationM.getTranslatedLabel("tooltip_bhandon"));
        labelHandOn.setText(TranslationM.getTranslatedLabel("label_handon"));
    }

}
