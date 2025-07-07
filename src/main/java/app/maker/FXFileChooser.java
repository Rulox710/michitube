package app.maker;

import java.io.File;

import app.engine.readers.PropertiesM;
import app.engine.readers.TranslationM;
import javafx.stage.DirectoryChooser;
import javafx.stage.FileChooser;

public class FXFileChooser {

    public enum TYPE {
        IMG, SVS, DIR
    }

    private static FileChooser imageChooser = getImageChooser();
    private static FileChooser saveChooser = getSaveChooser();
    private static DirectoryChooser dirChooser = getDirChooser();

    public static void applyTranslations() {
        imageChooser.setTitle(TranslationM.getTranslatedLabel("chooser_img"));
        imageChooser.getExtensionFilters().remove(0);
        imageChooser.getExtensionFilters().add(
            new FileChooser.ExtensionFilter(
                TranslationM.getTranslatedLabel("type_img"),
                "*.jpg", "*.jpeg", "*.png", "*.tiff", "*.tif"
            )
        );

        saveChooser.setTitle(TranslationM.getTranslatedLabel("chooser_svs"));
        saveChooser.getExtensionFilters().remove(0);
        saveChooser.getExtensionFilters().add(
            new FileChooser.ExtensionFilter(
                TranslationM.getTranslatedLabel("type_svs"), "*.sav"
            )
        );

        dirChooser.setTitle(TranslationM.getTranslatedLabel("chooser_dir"));
    }

    public static void changeInitialDirectory() {
        imageChooser.setInitialDirectory(new File(PropertiesM.getAppProperty("default_dir")));
        saveChooser.setInitialDirectory(new File(PropertiesM.getAppProperty("default_dir")));
        dirChooser.setInitialDirectory(new File(PropertiesM.getAppProperty("default_dir")));
    }

    public static FileChooser getImageChooser() {
        if(imageChooser == null) {
            imageChooser = new FileChooser();
            imageChooser.setTitle(TranslationM.getTranslatedLabel("chooser_img"));
            imageChooser.setInitialDirectory(new File(PropertiesM.getAppProperty("default_dir")));
            imageChooser.getExtensionFilters().add(
                new FileChooser.ExtensionFilter(
                    TranslationM.getTranslatedLabel("type_img"),
                    "*.jpg", "*.jpeg", "*.png", "*.tiff", "*.tif"
                )
            );
        }
        return imageChooser;
    }

    public static FileChooser getSaveChooser() {
        if (saveChooser == null) {
            saveChooser = new FileChooser();
            saveChooser.setTitle(TranslationM.getTranslatedLabel("chooser_svs"));
            saveChooser.setInitialDirectory(new File(PropertiesM.getAppProperty("default_dir")));
            saveChooser.getExtensionFilters().add(
                new FileChooser.ExtensionFilter(
                    TranslationM.getTranslatedLabel("type_svs"), "*.sav"
                )
            );
        }
        return saveChooser;
    }

    public static DirectoryChooser getDirChooser() {
        if (dirChooser == null) {
            dirChooser = new DirectoryChooser();
            dirChooser.setTitle(TranslationM.getTranslatedLabel("chooser_dir"));
            dirChooser.setInitialDirectory(new File(PropertiesM.getAppProperty("default_dir")));
        }
        return dirChooser;
    }
}
