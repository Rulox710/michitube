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

package app.files;

import app.Constants;
import app.LogMessage;
import app.fileUtils.AppPaths;
import app.files.TranslationM.LANGUAGES;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileWriter;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Properties;

/**
 * Clase que tiene lo necesario para encontrar las configuraciones
 * de la aplicación, usarlas y actualizarlas
 */
public final class PropertiesM {

    /** Ruta del archivo de configuración. */
    public static final String APP_CONFIG_FILE = "config.properties";
    /** Propiedades del programa. */
    private static final Properties APP_CONFIG_PROPERTIES = new Properties();
    /** Comentario al guradar las configuraciones */
    private static final String APP_CONFIG_DESC = "App configurations";

    public static String getAppProperty(String key) {
        return APP_CONFIG_PROPERTIES.getProperty(key);
    }

    /**
     * Carga las configuraciones del archivo de propiedades.
     * Si el archivo no existe, se crea y se establecen valores
     * predeterminados. También arregla el archivo en caso de existir y
     * tener errores.
     */
    public static void loadAppProperties() {
        System.out.println(LogMessage.PROPERTIES_START.get());

        File configDir = AppPaths.getAppPath(Constants.APP_NAME, AppPaths.PathType.CONFIG);
        File configFile = new File(configDir, APP_CONFIG_FILE);

        try(FileInputStream fileInputStream = new FileInputStream(configFile)) {
            APP_CONFIG_PROPERTIES.load(fileInputStream);
            configFile.createNewFile();
        } catch (IOException e) {
            Constants.printTimeStamp(System.err);
            e.printStackTrace();
        }

        String property = APP_CONFIG_PROPERTIES.getProperty("language");
        if(!isValidLanguage(property)) {
            System.out.println(String.format(
                LogMessage.PROPERTIES_LANG_X.get(), LANGUAGES.ES.name()
            ));
            APP_CONFIG_PROPERTIES.setProperty("language", LANGUAGES.ES.name());
        } else
            System.out.println(String.format(
                LogMessage.PROPERTIES_LANG_O.get(), property
            ));

        property = APP_CONFIG_PROPERTIES.getProperty("default_dir");
        if(!isValidPath(property)) {
            System.out.println(String.format(
                LogMessage.PROPERTIES_DIR_X.get(), System.getProperty("user.dir")
            ));
            APP_CONFIG_PROPERTIES.setProperty("default_dir", System.getProperty("user.dir"));
            //System.getProperty("user.home")
        } else
            System.out.println(String.format(
                LogMessage.PROPERTIES_DIR_O.get(), property
            ));

        property = APP_CONFIG_PROPERTIES.getProperty("frames_per_second");
        if(!isValidInt(property)) {
            System.out.println(String.format(
                LogMessage.PROPERTIES_FPS_X.get(), "60"
            ));
            APP_CONFIG_PROPERTIES.setProperty("frames_per_second", "60");
        } else
            System.out.println(String.format(
                LogMessage.PROPERTIES_FPS_O.get(), property
            ));

        property = APP_CONFIG_PROPERTIES.getProperty("windows_taskbar_height");
        if(!isValidInt(property)) {
            System.out.println(String.format(
                LogMessage.PROPERTIES_WINH_X.get(), "50"
            ));
            APP_CONFIG_PROPERTIES.setProperty("windows_taskbar_height", "50");
        } else
            System.out.println(String.format(
                LogMessage.PROPERTIES_WINH_O.get(), property
            ));

        //save(configFile, APP_CONFIG_PROPERTIES, APP_CONFIG_DESC);
    }

    public static void saveAppProperties() {
        File configDir = AppPaths.getAppPath(Constants.APP_NAME, AppPaths.PathType.CONFIG);
        File configFile = new File(configDir, APP_CONFIG_FILE);
        save(configFile, APP_CONFIG_PROPERTIES, APP_CONFIG_DESC);
    }

    private static void save(File file, Properties properties, String comment) {
        try {
            FileWriter writer = new FileWriter(file);
            properties.store(writer, comment);
            writer.close();
            System.out.println(LogMessage.PROPERTIES_SAVE.get());
        } catch (IOException e) {
            Constants.printTimeStamp(System.err);
            e.printStackTrace();
        }
    }

    public static boolean setAppProperty(String key, String value) {
        boolean isDone = setProperty(APP_CONFIG_PROPERTIES, key, value);
        if(isDone) {
            saveAppProperties();
            return true;
        }
        return false;
    }

    private static boolean setProperty(Properties properties, String key, String value) {
        if (key == null || value == null) {
            return false;
        }

        switch (key) {
            case "microphone_detection":
            case "keyboard_detection":
            case "mouse_detection":
                if (!isValidBoolean(value)) {
                    return false;
                }
                break;
            case "microphone_threshold":
                if (!isValidByte(value)) {
                    return false;
                }
                break;
            case "microphone_channels":
            case "microphone_ups":
            case "window_width":
            case "window_height":
            case "windows_taskbar_height":
            case "frames_per_second":
                if (!isValidInt(value)) {
                    return false;
                }
                break;
            case "language":
                if (!isValidLanguage(value)) {
                    return false;
                }
                break;
            case "default_dir":
                if (!isValidPath(value)) {
                    return false;
                }
                break;
            default:
                return false;
        }

        properties.setProperty(key, value);
        return true;
    }

    /**
     * Verifica si una cadena representa un valor booleano válido.
     *
     * @param value La cadena que se va a verificar.
     *
     * @return {@code true} si la cadena es "true" o "false",
     *         {@code false} de lo contrario.
     */
    private static boolean isValidBoolean(String value) {
        return value != null &&
            (value.equalsIgnoreCase("true") || value.equalsIgnoreCase("false"));
    }

    /**
     * Verifica si una cadena representa un valor entero válido.
     *
     * @param value La cadena que se va a verificar.
     * @return {@code true} si la cadena es un entero válido,
     *         {@code false} de lo contrario.
     */
    private static boolean isValidInt(String value) {
        try {
            return Integer.parseInt(value) > 0;
        } catch (NumberFormatException e) {
            return false;
        }
    }

    /**
     * Verifica si una cadena representa un valor byte válido.
     *
     * @param value La cadena que se va a verificar.
     * @return {@code true} si la cadena es un byte válido,
     *         {@code false} de lo contrario.
     */
    private static boolean isValidByte(String value) {
        try {
            return Byte.parseByte(value) > 0;
        } catch (NumberFormatException e) {
            return false;
        }
    }

    /**
     * Verifica si una cadena representa un valor para el idioma válido.
     *
     * @param value La cadena que se va a verificar.
     * @return {@code true} si la cadena es un valor para idioma válido,
     *         {@code false} de lo contrario.
     */
    private static boolean isValidLanguage(String value) {
        try {
            TranslationM.LANGUAGES.valueOf(value);
            return true;
        } catch (Exception e) {
            return false;
        }
    }

    private static boolean isValidPath(String value) {
        try {
            Path path = Paths.get(value);
            if (Files.exists(path) && Files.isDirectory(path)) {
                return true;
            }
            return false;
        } catch (Exception e) {
            return false;
        }
    }
}
