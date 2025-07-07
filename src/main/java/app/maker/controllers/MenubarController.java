package app.maker.controllers;

import java.io.File;
import java.util.Arrays;
import java.util.Locale;

import app.engine.readers.PropertiesM;
import app.engine.readers.TranslationM;
import app.maker.FXFileChooser;
import javafx.application.Platform;
import javafx.fxml.FXML;
import javafx.scene.control.Menu;
import javafx.scene.control.MenuItem;
import javafx.scene.control.RadioMenuItem;
import javafx.scene.control.ToggleGroup;

public class MenubarController extends AbstractController {

    @FXML
    private Menu menuStart, menuFile, menuFileSave, menuLanguage;
    
    @FXML
    private MenuItem menuStartVTuber, menuFileDir, menuFileLoad, menuFileSaves, menuFileSaveh;

    @Override
    public void initialize() {
        initializeMenuLanguage();
        updateLanguage();

        menuFileDir.setOnAction(e -> {
            File dir = FXFileChooser.getDirChooser().showDialog(null);
            if (dir != null) {
                notifyObservers('d', dir.getPath());
                FXFileChooser.changeInitialDirectory();
            }
        });

        menuFileLoad.setOnAction(e -> {
            File img = FXFileChooser.getImageChooser().showOpenDialog(null);
            if (img != null) {
                notifyObservers((char) 0, img.getPath());
            }
        });
    }

    private void initializeMenuLanguage() {
        ToggleGroup languageGroup = new ToggleGroup();
        String[] optionsToChoose = Arrays.stream(TranslationM.LANGUAGES.values())
            .map(Enum::toString)
            .toArray(String[]::new);

        int currentLanguage = TranslationM.LANGUAGES.valueOf(
            PropertiesM.getAppProperty("language")
        ).getIndex();

        menuLanguage.getItems().clear();

        for (int i = 0; i < optionsToChoose.length; i++) {
            String language = optionsToChoose[i];
            RadioMenuItem item = new RadioMenuItem(language);
            item.setToggleGroup(languageGroup);
            if (i == currentLanguage) item.setSelected(true);

            int index = i;
            item.setOnAction(e -> {
                String name = TranslationM.LANGUAGES.values()[index].name();
                notifyObservers('l', name);
                Locale locale = Locale.ENGLISH;
                switch(name) {
                    case "EN":
                        locale = Locale.ENGLISH;
                        break;
                    case "ES":
                        locale = new Locale(name);
                        break;
                    default:
                        locale = Locale.ENGLISH;
                        break;
                }
                Locale.setDefault(locale);
                FXFileChooser.applyTranslations();
            });

            menuLanguage.getItems().add(item);
        }
    }
    
    @Override
    public void updateLanguage() {
        Platform.runLater(() -> {
            menuStart.setText(TranslationM.getTranslatedLabel("menu_start"));
            menuStartVTuber.setText(TranslationM.getTranslatedLabel("menu_start_vtuber"));

            menuFile.setText(TranslationM.getTranslatedLabel("menu_file"));
            menuFileDir.setText(TranslationM.getTranslatedLabel("menu_file_dir"));
            menuFileLoad.setText(TranslationM.getTranslatedLabel("menu_file_load"));
            menuFileSave.setText(TranslationM.getTranslatedLabel("menu_file_save"));
            menuFileSaves.setText(TranslationM.getTranslatedLabel("menu_file_saveh"));
            menuFileSaveh.setText(TranslationM.getTranslatedLabel("menu_file_saves"));

            menuLanguage.setText(TranslationM.getTranslatedLabel("menu_language"));
        });
    }
}
