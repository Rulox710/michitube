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

import app.Constants;
import app.LogMessage;
import app.Main;
import app.engine.DeltaTimeManager;
import app.engine.Observer;
import app.files.PropertiesM;
import app.files.TranslationM;
import app.files.VTuberLoader;
import app.files.VTuberReader;
import app.files.VTuberSaver;
import app.vtuber.VTuberWindow;

import java.io.File;
import java.io.IOException;
import java.net.URL;
import java.util.ResourceBundle;

import javafx.application.Platform;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.control.Accordion;
import javafx.scene.control.Alert;
import javafx.scene.control.Alert.AlertType;
import javafx.scene.control.Control;
import javafx.scene.control.ScrollPane;
import javafx.scene.layout.Pane;
import javafx.stage.Stage;
import javafx.stage.WindowEvent;

/**
 * Controlador principal, aunque no extiende {@link AbstractController}.
 * Maneja casi todos los demás controladores, los vincula con los
 * correspontientes observadores, avisa de cambios a los controladores
 * que no están vinculados y tiene el esqueleto de los
 * <code>fxml</code>.
 *
 * Usa el <code>fxml</code> en
 * <code>/res/views/main_view.fxml</code>.
 *
 * @see app.Main#start El <code>fxml</code> se asigna en
 *      <code>app.Main#start</code>.
 */
public class MainController implements Initializable, Observer {

    @FXML private Pane rootVBox, scrollpaneLayers;
    @FXML private ScrollPane scrollpaneOptions, scrollpaneLayerOptions, scrollpaneSheet;

    private Control menuBarInclude;
    private Pane anchorpaneSheet;
    private Accordion vboxOptionsInclude;

    private AbstractController menubarController, optionsController;
    private LayersController layersController;
    private SheetController sheetController;

    private VTuberWindow vtWin = new VTuberWindow();

    /**
     * Inicializa el controlador principal.
     * Carga los componentes de la interfaz y vincula los controladores
     * necesarios.
     *
     * @param location La ubicación del recurso FXML.
     * @param resources Los recursos asociados con el FXML.
     */
    @Override
    public void initialize(URL location, ResourceBundle resources) {
        int count = 0;
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/views/menubar_view.fxml"));
            count++;
            menuBarInclude = loader.load();
            System.out.println(String.format(LogMessage.GUI_FXML.get(), "Barra de menu"));
            menubarController = loader.getController();
            rootVBox.getChildren().add(0, menuBarInclude);

            loader = new FXMLLoader(getClass().getResource("/views/options_view.fxml"));
            vboxOptionsInclude = loader.load();
            System.out.println(String.format(LogMessage.GUI_FXML.get(), "Panel de opciones"));
            count++;
            optionsController = loader.getController();
            scrollpaneOptions.setContent(vboxOptionsInclude);

            layersController = new LayersController(scrollpaneLayers, scrollpaneLayerOptions);
            layersController.initialize();

            loader = new FXMLLoader(getClass().getResource("/views/sheet_view.fxml"));
            anchorpaneSheet = loader.load();
            System.out.println(String.format(LogMessage.GUI_FXML_END.get(), "Vista de la hoja"));
            count++;
            sheetController = loader.getController();
            scrollpaneSheet.setContent(anchorpaneSheet);
        } catch (IOException e) {
            String error = switch(count) {
                case 0 -> "Barra de menu";
                case 1 -> "Panel de opciones";
                case 2 -> "Vista de la hoja";
                default -> "Desconocido";
            };
            System.out.println(String.format(LogMessage.GUI_FXML_X.get(), error));

            Constants.printTimeStamp(System.err);
            e.printStackTrace();
            Platform.runLater(() -> {
                Main.stopApp(true);
            });
        }
        menubarController.addObserver(this);
        optionsController.addObserver(this);
        layersController.addObserver(sheetController);
        vtWin.addObserver(this);
        vtWin.addObserver(layersController);
    }

    public void saveConfig() {
        PropertiesM.saveAppProperties();
    }

    public boolean readyToSave() {
        return layersController.readyToSave() && sheetController.readyToSave();
    }

    /**
     * /**
     * {@inheritDoc}
     *
     * @param event Las posibles baderas de este método son las
     *              siguientes:
     * - <code>l</code>:
     * - <code>d</code>:
     * - <code>L</code>:
     * - <code>S</code>:
     * - <code>m</code>:
     * - <code>k</code>:
     * - <code>n</code>:
     * - <code>u</code>:
     * - <code>c</code>:
     * - <code>s</code>:
     * - <code>f</code>:
     */
    @Override
    public void update(char event, Object data) {
        switch(event) {
        case 'R':
            if(!readyToSave()) {
                Platform.runLater(() -> {
                    Alert alert = new Alert(AlertType.ERROR);
                    alert.setTitle(TranslationM.getTranslatedLabel("title_error_startup"));
                    alert.setHeaderText(TranslationM.getTranslatedLabel("header_error_startup"));
                    alert.setContentText(TranslationM.getTranslatedLabel("content_error_startup"));
                    alert.showAndWait();
                });
                break;
            }
            layersController.close();
            Main.hidePrimaryStage();

            VTuberReader reader = new VTuberReader();
            reader.loadFromMap(VTuberSaver.getVTuberWriter(
                layersController, sheetController, false
            ).getClone());

            vtWin.setMap(reader);
        break;

        case 'f':
            PropertiesM.setAppProperty("frames_per_second", String.valueOf((int) data));
            DeltaTimeManager.getInstance().changeMaxUPS((int) data);
        break;

        case 'l':
            PropertiesM.setAppProperty("language", (String) data);
            menubarController.updateLanguage();
            optionsController.updateLanguage();
            layersController.updateLanguage();
            sheetController.updateLanguage();
        break;

        case 'd':
            PropertiesM.setAppProperty("default_dir", ((File)data).getPath());
        break;

        case 'L':
            if(!VTuberLoader.loadVTuber((File)data, layersController, sheetController))
                Platform.runLater(() -> {
                    Alert alert = new Alert(AlertType.ERROR);
                    alert.setTitle(TranslationM.getTranslatedLabel("title_error_loading"));
                    alert.setHeaderText(TranslationM.getTranslatedLabel("header_error_loading"));
                    alert.setContentText(TranslationM.getTranslatedLabel("content_error_loading"));
                    alert.showAndWait();
                });
        break;

        case 'S':
        case 'H':
            if(!readyToSave()) {
                Platform.runLater(() -> {
                    Alert alert = new Alert(AlertType.ERROR);
                    alert.setTitle(TranslationM.getTranslatedLabel("title_error_saving"));
                    alert.setHeaderText(TranslationM.getTranslatedLabel("header_error_saving"));
                    alert.setContentText(TranslationM.getTranslatedLabel("content_error_saving"));
                    alert.showAndWait();
                });
                break;
            }
            VTuberSaver.saveVTuber((File)data, layersController, sheetController, event == 'S');
        break;

        case 'z': Main.showPrimaryStage();
        break;

        case 'Z':
            Stage stage = (Stage) rootVBox.getScene().getWindow();
            WindowEvent closeEvent = new WindowEvent(stage, WindowEvent.WINDOW_CLOSE_REQUEST);
            Platform.runLater(() -> stage.fireEvent(closeEvent));
        break;
        }
    }
}
