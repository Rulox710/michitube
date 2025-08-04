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

package app.maker;

import app.files.PropertiesM;
import app.files.TranslationM;

import java.io.File;

import javafx.stage.DirectoryChooser;
import javafx.stage.FileChooser;

/**
 * Clase para manejar los diálogos de selección de archivos y
 * directorios en la aplicación. Permite seleccionar imágenes,
 * archivos de guardado y directorios.
 */
public class FXFileChooser {

    /**
     * Tipos de archivos que se pueden seleccionar.
     */
    public enum TYPE {
        IMG, SVS, DIR
    }

    private static FileChooser imageChooser = getImageChooser();
    private static FileChooser saveChooser = getSaveChooser();
    private static DirectoryChooser dirChooser = getDirChooser();

    /**
     * Aplica las traducciones a los diálogos de selección de archivos.
     * Se debe llamar después de inicializar la aplicación y antes de
     * mostrar cualquier diálogo.
     */
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

    /**
     * Cambia el directorio inicial de los diálogos de selección de
     * archivos.
     */
    public static void changeInitialDirectory() {
        imageChooser.setInitialDirectory(new File(PropertiesM.getAppProperty("default_dir")));
        saveChooser.setInitialDirectory(new File(PropertiesM.getAppProperty("default_dir")));
        dirChooser.setInitialDirectory(new File(PropertiesM.getAppProperty("default_dir")));
    }

    /**
     * Devuelve el diálogo de selección de imágenes.
     *
     * @return El diálogo de selección de imágenes.
     */
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

    /**
     * Devuelve el diálogo de selección de archivos de guardado.
     *
     * @return El diálogo de selección de archivos de guardado.
     */
    public static FileChooser getSaveChooser() {
        if(saveChooser == null) {
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

    /**
     * Devuelve el diálogo de selección de directorios.
     *
     * @return El diálogo de selección de directorios.
     */
    public static DirectoryChooser getDirChooser() {
        if(dirChooser == null) {
            dirChooser = new DirectoryChooser();
            dirChooser.setTitle(TranslationM.getTranslatedLabel("chooser_dir"));
            dirChooser.setInitialDirectory(new File(PropertiesM.getAppProperty("default_dir")));
        }
        return dirChooser;
    }
}
