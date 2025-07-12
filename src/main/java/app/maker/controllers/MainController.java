package app.maker.controllers;

import java.io.File;
import java.io.IOException;
import java.net.URL;
import java.util.Map;
import java.util.ResourceBundle;

import app.engine.Observer;
import app.engine.readers.PropertiesM;
import app.engine.readers.TranslationM;
import app.maker.controllers.components.DraggableResizableImageView;
import app.maker.controllers.objects.Objects.ImageInfo;
import app.maker.controllers.objects.Objects.SheetInfo;
import app.saver.VTuberWriter;
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
import javafx.scene.layout.VBox;
import javafx.stage.Stage;

public class MainController implements Initializable, Observer {

    @FXML private VBox rootVBox;
    @FXML private ScrollPane scrollpaneOptions, scrollpaneLayerOptions, scrollpaneSheet;
    @FXML private VBox scrollpaneLayers;

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
        return false;
    }

    public void saveVTuber(File file) {
        if(!file.getName().toLowerCase().endsWith(".sav"))
            file = new File(file.getParentFile(), file.getName() + ".sav");
        try {
            file.createNewFile();
        } catch (IOException e) {}

        VTuberWriter vtuberWriter = new VTuberWriter();

        String sectionName = "sheet";
        SheetInfo sheetInfo = sheetController.getSheetInfo();
        vtuberWriter.put(sectionName, "W",sheetInfo.width);
        vtuberWriter.put(sectionName, "H",sheetInfo.height);
        vtuberWriter.put(sectionName, "C",sheetInfo.color);

        Map<String, ImageInfo[]> layers_infos = sheetController.getInfoMap();
        for(Map.Entry<String, ImageInfo[]> entry: layers_infos.entrySet()) {
            sectionName = entry.getKey();
            ImageInfo[] infos = entry.getValue();
            for (int i = 0; i < infos.length; i++) {
                ImageInfo view = infos[i];
                if(view == null) continue;
                vtuberWriter.put(sectionName + "_" + i, "X", view.x);
                vtuberWriter.put(sectionName + "_" + i, "Y", view.y);
                vtuberWriter.put(sectionName + "_" + i, "W", view.width);
                vtuberWriter.put(sectionName + "_" + i, "H", view.height);
                vtuberWriter.put(sectionName + "_" + i, "P", view.path);
            }
        }
        try {
            vtuberWriter.saveToFile(file.getPath());
        } catch (IOException e) {
            e.printStackTrace();
        }
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
            saveVTuber((File)data);
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
