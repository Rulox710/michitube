package app.maker.controllers;

import app.files.PropertiesM;
import app.files.TranslationM;
import app.maker.FXFileChooser;

import java.io.File;
import java.util.Arrays;
import java.util.Locale;

import javafx.application.Platform;
import javafx.fxml.FXML;
import javafx.scene.control.Menu;
import javafx.scene.control.MenuItem;
import javafx.scene.control.RadioMenuItem;
import javafx.scene.control.ToggleGroup;

/**
 * Controlador del menú superior de la aplicación.
 *
 * Usa el <code>fxml</code> en
 * <code>/res/views/menubar_view.fxml</code>.
 *
 * @see app.maker.controllers.MainController#initialize El
 *      <code>fxml</code> y los estilos se asignan en
 *      <code>app.maker.controllers.MainController#initialize</code>
 */
public class MenubarController extends AbstractController {

    @FXML
    private Menu menuStart, menuFile, menuFileSave, menuLanguage;

    @FXML
    private MenuItem menuStartVTuber, menuFileDir, menuFileLoad, menuFileSaves, menuFileSaveh;

    /**
     * {@inheritDoc}
     */
    @Override
    public void initialize() {
        initializeMenuLanguage();
        updateLanguage();

        menuStartVTuber.setOnAction(e -> notifyObservers('R', null));

        menuFileDir.setOnAction(e -> selectFile('d'));
        menuFileLoad.setOnAction(e -> selectFile('L'));

        menuFileSaves.setOnAction(e -> selectFile('S'));
        menuFileSaveh.setDisable(true);
    }

    /**
     * Abre un diálogo para seleccionar un archivo o directorio
     * dependiendo del tipo indicado.
     *
     * @param type El tipo de archivo o directorio.
     */
    private void selectFile(char type) {
        File file = switch(type) {
            case 'd' -> FXFileChooser.getDirChooser().showDialog(null);
            case 'L' -> FXFileChooser.getSaveChooser().showOpenDialog(null);
            case 'S' -> FXFileChooser.getSaveChooser().showSaveDialog(null);
            default -> null;
        };
        if(file == null) return;
        if(type == 'd') FXFileChooser.changeInitialDirectory();
        notifyObservers(type, file);
    }

    /**
     * Inicializa el menú de selección de idioma.
     */
    private void initializeMenuLanguage() {
        ToggleGroup languageGroup = new ToggleGroup();
        String[] optionsToChoose = Arrays.stream(TranslationM.LANGUAGES.values())
            .map(Enum::toString)
            .toArray(String[]::new);

        int currentLanguage = TranslationM.LANGUAGES.valueOf(
            PropertiesM.getAppProperty("language")
        ).ordinal();

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

    /**
     * {@inheritDoc}
     */
    @Override
    public void updateLanguage() {
        Platform.runLater(() -> {
            menuStart.setText(TranslationM.getTranslatedLabel("menu_start"));
            menuStartVTuber.setText(TranslationM.getTranslatedLabel("menu_start_vtuber"));

            menuFile.setText(TranslationM.getTranslatedLabel("menu_file"));
            menuFileDir.setText(TranslationM.getTranslatedLabel("menu_file_dir"));
            menuFileLoad.setText(TranslationM.getTranslatedLabel("menu_file_load"));
            menuFileSave.setText(TranslationM.getTranslatedLabel("menu_file_save"));
            menuFileSaves.setText(TranslationM.getTranslatedLabel("menu_file_saves"));
            menuFileSaveh.setText(TranslationM.getTranslatedLabel("menu_file_saveh"));

            menuLanguage.setText(TranslationM.getTranslatedLabel("menu_language"));
        });
    }
}
