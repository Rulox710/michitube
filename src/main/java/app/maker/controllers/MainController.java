package app.maker.controllers;

import app.engine.Observer;
import app.files.PropertiesM;
import app.files.TranslationM;
import app.files.VTuberLoader;
import app.files.VTuberSaver;

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

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/res/views/menubar_view.fxml"));
            menuBarInclude = loader.load();
            menubarController = loader.getController();
            rootVBox.getChildren().add(0, menuBarInclude);

            loader = new FXMLLoader(getClass().getResource("/res/views/options_view.fxml"));
            vboxOptionsInclude = loader.load();
            optionsController = loader.getController();
            scrollpaneOptions.setContent(vboxOptionsInclude);

            layersController = new LayersController(scrollpaneLayers, scrollpaneLayerOptions);
            layersController.initialize();

            loader = new FXMLLoader(getClass().getResource("/res/views/sheet_view.fxml"));
            anchorpaneSheet = loader.load();
            sheetController = loader.getController();
            scrollpaneSheet.setContent(anchorpaneSheet);
        } catch (IOException e) {
            e.printStackTrace();
        }
        menubarController.addObserver(this);
        optionsController.addObserver(this);
        layersController.addObserver(sheetController);
        Platform.runLater(() -> {
            Stage stage = (Stage) rootVBox.getScene().getWindow();
            stage.setOnCloseRequest(event -> {
                saveConfig();
                System.exit(0);
            });
        });
    }

    public void saveConfig() {
        PropertiesM.saveVtuberProperties();
        PropertiesM.saveAppProperties();
    }

    public boolean readyToSave() {
        return layersController.readyToSave();
    }

    @Override
    public void update(char event, Object data) {
        switch(event) {
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
            VTuberSaver.saveVTuber((File)data, layersController, sheetController);
        break;
        case 'm':
            PropertiesM.setVtuberProperty("mouse_detection", String.valueOf(data));
        break;
        case 'k':
            PropertiesM.setVtuberProperty("keyboard_detection", String.valueOf(data));
        break;
        case 'n':
            PropertiesM.setVtuberProperty("microphone_detection", String.valueOf(data));
        break;
        case 'u':
            PropertiesM.setVtuberProperty("microphone_ups", String.valueOf(data));
        break;
        case 'c':
            PropertiesM.setVtuberProperty("microphone_channels", String.valueOf(data));
        break;
        case 's':
            PropertiesM.setVtuberProperty("microphone_threshold", String.valueOf(data));
        break;
        case 'f':
            PropertiesM.setVtuberProperty("frames_per_second", String.valueOf(data));
        break;
        }
    }
}
