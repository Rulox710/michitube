package app.maker.controllers;

import java.io.IOException;
import java.net.URL;
import java.util.ResourceBundle;

import app.engine.Observer;
import app.engine.readers.PropertiesM;
import app.maker.controllers.components.LayerButton;
import javafx.application.Platform;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.control.Control;
import javafx.scene.control.ScrollPane;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.Pane;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;

public class MainController implements Initializable, Observer {

    @FXML private VBox rootVBox;
    @FXML private ScrollPane scrollpaneOptions, scrollpaneLayerOptions, scrollpaneSheet;
    @FXML private VBox scrollpaneLayers;

    private Control menuBarInclude;
    private Pane vboxOptionsInclude, anchorpaneSheet;

    private AbstractController menubarController, optionsController, layersController, sheeController;

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/res/views/menubar_view.fxml"));
            menuBarInclude = loader.load();
            menubarController = loader.getController();
            rootVBox.getChildren().add(0, menuBarInclude);

            loader = new FXMLLoader(getClass().getResource("/res/views/options_view.fxml"));
            vboxOptionsInclude = loader.load();
            vboxOptionsInclude.getStylesheets().add(getClass().getResource("/res/styles/mic_style.css").toExternalForm());
            optionsController = loader.getController();
            scrollpaneOptions.setContent(vboxOptionsInclude);

            layersController = new LayersController(scrollpaneLayers, scrollpaneLayerOptions);
            layersController.initialize();

            loader = new FXMLLoader(getClass().getResource("/res/views/sheet_view.fxml"));
            anchorpaneSheet = loader.load();
            sheeController = loader.getController();
            scrollpaneSheet.setContent(anchorpaneSheet);

        } catch (IOException e) {
            e.printStackTrace();
        }
        menubarController.addObserver(this);
        optionsController.addObserver(this);
        layersController.addObserver(this);
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

    @Override
    public void update(char event, Object data) {
        switch (event) {
        case 'l':
            PropertiesM.setAppProperty("language", (String) data);
            menubarController.updateLanguage();
            optionsController.updateLanguage();
            layersController.updateLanguage();
            //layersGUI.updateLanguage();
            break;
        case 'd':
            PropertiesM.setAppProperty("default_dir", (String) data);
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
        case 'L':
            // sheetGUI.selectLayer((int)data);
            break;
        case (char)0:
        case (char)1:
        case (char)2:
        case (char)3:
        case (char)4:
        case (char)5:
        case (char)6:
            // sheetGUI.addImage((int)event, (File)data);
            break;
        }
    }



}
