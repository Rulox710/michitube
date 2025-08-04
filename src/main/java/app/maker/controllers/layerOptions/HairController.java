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
import app.files.TranslationM;
import app.maker.FXFileChooser;
import app.maker.controllers.objects.Infos.Info;
import app.maker.controllers.objects.builders.BasicInfoBuilder;

import java.io.File;
import java.net.URI;
import java.nio.file.Path;
import java.nio.file.Paths;

import javafx.css.PseudoClass;
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

public class HairController extends OptionLayerController {

    @FXML private Accordion hairOptionsRoot;
    @FXML private TitledPane titledPaneImage;
    @FXML private Label labelImage;
    @FXML private Button buttonImage;
    @FXML private ImageView imagePreview;
    @FXML private HBox hboxImage;
    @FXML private CheckBox checkboxImage;
    @FXML private Tooltip tooltipCImage;

    /**
     * {@inheritDoc}
     */
    @Override
    public void initialize() {
        hasError = new boolean[1]; // 0: Image
        updateLanguage();

        optionsRoot = hairOptionsRoot;
        firstPane = titledPaneImage;
        super.initialize();

        updateEnabledState(checkboxImage, checkboxImage.isSelected());
        checkboxImage.selectedProperty().addListener((obs, oldVal, newVal) -> {
            updateEnabledState(checkboxImage, newVal);
        });
    }

    @Override
    protected void handleError(int index, boolean error, boolean notify) {
        index = index%1;
        hasError[index] = error;

        TitledPane currentPane;
        HBox currentHBox;
        switch(index) {
            case 0:
            default:
                currentPane = titledPaneImage;
                currentHBox = hboxImage;
            break;
        }
        currentPane.pseudoClassStateChanged(PseudoClass.getPseudoClass("error"), hasError[index]);
        currentHBox.pseudoClassStateChanged(PseudoClass.getPseudoClass("error"), hasError[index]);

        if(notify) notifyObservers('e', hasError[0]);
    }

    private void updateEnabledState(CheckBox checkbox, boolean enabled) {
        if(checkbox == checkboxImage) {
            buttonImage.setDisable(!enabled);
            hboxImage.setDisable(!enabled);
            imagePreview.setDisable(!enabled);
            if(!enabled) handleError(0, false, true);
        }
    }

    @FXML
    private void handleButtonClick(ActionEvent event) {
        File img = FXFileChooser.getImageChooser().showOpenDialog(null);
        if(img != null) {
            imagePreview.setImage(new Image(img.toURI().toString()));
            notifyObservers((char) getTweakID(), img);

            handleError(0, false, true);
        }
    }

    @Override
    public boolean readyToSave() {
        if(checkboxImage.selectedProperty().getValue() && imagePreview.getImage() == null)
            handleError(0, true, true);
        return !hasError[0];

    }

    @Override
    public boolean setInfo(Info info) {
        boolean result = true;
        Path relativePath, fullPath;
        URI fullUri;

        checkboxImage.selectedProperty().setValue(info.getBoolean(KEYS.USE));
        if(info.getBoolean(KEYS.USE) && info.getString(KEYS.PATH_0).length() != 0) {
            relativePath = Paths.get(info.getString(KEYS.PATH_0));
            fullPath = basePath.resolve(relativePath);
            fullUri = fullPath.toUri();
            imagePreview.setImage(new Image(fullUri.toString()));
            notifyObservers('l', new File(fullUri));
        } else {
            result = false;
            imagePreview.setImage(null);
            notifyObservers('l', null);
        }

        handleError(0, false, true);

        return result;
    }

    @Override
    public Info getInfo() {
        BasicInfoBuilder builder = new BasicInfoBuilder();
        builder.setUsage(checkboxImage.selectedProperty().getValue());

        return builder.getResult();
    }

    @Override
    public void updateLanguage() {
        titledPaneImage.setText(TranslationM.getTranslatedLabel("title_hair_image"));
        buttonImage.setText(TranslationM.getTranslatedLabel("button_select_image"));
        labelImage.setText(TranslationM.getTranslatedLabel("label_hair_image"));
        checkboxImage.setText(TranslationM.getTranslatedLabel("checkbox_hair_image"));
        tooltipCImage.setText(TranslationM.getTranslatedLabel("tooltip_hair_cimage"));
    }
}
