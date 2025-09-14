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

package app.maker.controllers.components;

import app.files.TranslationM;
import app.maker.controllers.AbstractController;

import javafx.css.PseudoClass;
import javafx.fxml.FXML;
import javafx.scene.control.Label;
import javafx.scene.control.Tooltip;
import javafx.scene.image.*;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.Pane;

/**
 * Clase con la lógica de controlador para atender las acciones del
 * usuario en los botones que muestran una vista previa del contenido
 * de las capas.
 *
 * Usa el <code>fxml</code> en
 * <code>/res/views/components/layer_button.fxml</code>
 * y los estilos en <code>/res/styles/layer_button.css</code>.
 *
 * @see app.maker.controllers.LayersController#initialize El
 *      <code>fxml</code> y los estilos se asignan en
 *      <code>app.maker.controllers.LayersController#initialize</code>
 */
public class LayerButton extends AbstractController {

    @FXML private Pane stackpaneContainer, vboxButton, imageContainer;
    @FXML private Label labelLayer;
    @FXML private ImageView imagePreview;
    @FXML private Tooltip tooltipButton;

    private String translationId = "label_layers";

    /**
     * {@inheritDoc}
     */
    @FXML
    public void initialize() {
        stackpaneContainer.setOnMousePressed(this::handleClick);
        stackpaneContainer.setOnMouseEntered(e -> {
            imageContainer.pseudoClassStateChanged(PseudoClass.getPseudoClass("hover"), true);
        });
        stackpaneContainer.setOnMouseExited(e -> {
            imageContainer.pseudoClassStateChanged(PseudoClass.getPseudoClass("hover"), false);
        });
    }

    /**
     * Asigna una imagen para ser mostrada en el componente.
     *
     * @param fimg El archivo con la imagen.
     */
    private void setImage(Image fimg) {
        if(fimg != null) {
            imagePreview.setImage(fimg);
        } else {
            imagePreview.setImage(null);
        }
    }

    /**
     * Método con la lógica cuando se hace clic sobre el componente.
     *
     * @param e El evento relativo al clic con el mouse.
     */
    private void handleClick(MouseEvent e) {
        if(e.isPrimaryButtonDown()) {
            toggleSelect(true);
            notifyObservers('c', this);
        }
    }

    /**
     * Cambia el estilo del componente dependiendo de si tiene el foco o
     * no según el parámetro dado.
     *
     * @param hasFocus Indica si tiene el foco o no.
     */
    public void toggleSelect(boolean hasFocus) {
        stackpaneContainer.pseudoClassStateChanged(PseudoClass.getPseudoClass("clicked"), hasFocus);
        imageContainer.pseudoClassStateChanged(PseudoClass.getPseudoClass("clicked"), hasFocus);
    }

    public void toggleError(boolean hasError) {
        stackpaneContainer.pseudoClassStateChanged(PseudoClass.getPseudoClass("error"), hasError);
        imageContainer.pseudoClassStateChanged(PseudoClass.getPseudoClass("error"), hasError);
    }

    /**
     * Asigna que clave se usará para este componente al momento de
     * cambiar el contenido de sus cadenas según el idioma actual.
     *
     * @param translationId La clave a usar.
     */
    public void setTranslationId(String translationId) {
        this.translationId = translationId;
        updateLanguage();
    }

    /**
     * {@inheritDoc}
     */
    public void updateLanguage() {
        labelLayer.setText(TranslationM.getTranslatedLabel(translationId));
        Tooltip.install(stackpaneContainer, new Tooltip(TranslationM.getTranslatedLabel(translationId)));
    }

    /**
     * {@inheritDoc}
     *
     * @param event Las posibles baderas de este método son las
     *              siguientes:
     *              - <code>(char)0</code>: usa el método
     *                  {@link #setImage}<code>(data)</code>
     *              - <code>c</code>: usa el método
     *                  {@link #toggleSelect}<code>(false)</code>
     *              - <code>l</code>: usa el método
     *                  {@link #setImage}<code>(data)</code>
     *              - <code>e</code>: usa el método
     *                  {@link #toggleError}<code>(data)</code>
     */
    @Override
    public void update(char event, Object data) {
        switch(event) {
            case (char)0: setImage((Image) data);
            break;

            case 'c': toggleSelect(false);
            break;

            case 'l': setImage((Image) data);
            break;

            case 'e': toggleError((boolean) data);
        }
    }
}
