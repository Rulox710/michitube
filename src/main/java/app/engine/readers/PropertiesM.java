package app.engine.readers;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileWriter;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Properties;

import app.Constants;
import app.fileUtils.AppPaths;

public final class PropertiesM {

    /** Ruta del archivo de configuración. */
    public static final String APP_CONFIG_FILE = "config.properties";
    /** Propiedades del programa. */
    private static final Properties APP_CONFIG_PROPERTIES = new Properties();
    /** Comentario al guradar las configuraciones */
    private static final String APP_CONFIG_DESC = "App configurations";

    private static final Properties VTUBER_PROPERTIES = new Properties();
    private static final String VTUBER_DESC = "VTuber model configurations";

    public static String getAppProperty(String key) {
        return APP_CONFIG_PROPERTIES.getProperty(key);
    }

    public static String getVtuberProperty(String key) {
        return VTUBER_PROPERTIES.getProperty(key);
    }

    /**
     * Carga las configuraciones del archivo de propiedades.
     * Si el archivo no existe, se crea y se establecen valores
     * predeterminados. También arregla el archivo en caso de existir y
     * tener errores.
     */
    public static void loadAppProperties() {
        File configDir = AppPaths.getAppPath(Constants.APP_NAME, AppPaths.PathType.CONFIG);
        File configFile = new File(configDir, APP_CONFIG_FILE);

        try(FileInputStream fileInputStream = new FileInputStream(configFile)) {
            APP_CONFIG_PROPERTIES.load(fileInputStream);
        } catch (IOException e) {
            try {
                configFile.createNewFile();
            } catch (IOException e1) {
                e1.printStackTrace();
            }
        }

        String property = APP_CONFIG_PROPERTIES.getProperty("language");
        if(!isValidLanguage(property))
            APP_CONFIG_PROPERTIES.setProperty("language", "ES");
        property = APP_CONFIG_PROPERTIES.getProperty("default_dir");
        if(!isValidPath(property))
            APP_CONFIG_PROPERTIES.setProperty("default_dir", System.getProperty("user.home"));

        save(configFile, APP_CONFIG_PROPERTIES, APP_CONFIG_DESC);
    }

    
    public static void loadVtuberProperties(String vtuberName) {
        File configDir = AppPaths.getAppPath(Constants.APP_NAME, AppPaths.PathType.CONFIG);
        File configFile = new File(configDir, "VTUBER.sav");

        try(FileInputStream fileInputStream = new FileInputStream(configFile)) {
            VTUBER_PROPERTIES.load(fileInputStream);
        } catch (IOException e) {
            try {
                configFile.createNewFile();
            } catch (IOException e1) {
                e1.printStackTrace();
            }
        }

        String property = VTUBER_PROPERTIES.getProperty("microphone_detection");
        if(!isValidBoolean(property))
            VTUBER_PROPERTIES.setProperty("microphone_detection", "true");
        property = VTUBER_PROPERTIES.getProperty("microphone_threshold");
        if(!isValidByte(property))
            VTUBER_PROPERTIES.setProperty("microphone_threshold", "5");
        property = VTUBER_PROPERTIES.getProperty("microphone_channels");
        if(!isValidInt(property))
            VTUBER_PROPERTIES.setProperty("microphone_channels", "1");
        property = VTUBER_PROPERTIES.getProperty("keyboard_detection");
        if(!isValidBoolean(property))
            VTUBER_PROPERTIES.setProperty("keyboard_detection", "true");
        property = VTUBER_PROPERTIES.getProperty("microphone_ups");
        if(!isValidInt(property))
            VTUBER_PROPERTIES.setProperty("microphone_ups", "10");
        property = VTUBER_PROPERTIES.getProperty("mouse_detection");
        if(!isValidBoolean(property))
            VTUBER_PROPERTIES.setProperty("mouse_detection","true");
        property = VTUBER_PROPERTIES.getProperty("window_width");
        if(!isValidInt(property))
            VTUBER_PROPERTIES.setProperty("window_width", "500");
        property = VTUBER_PROPERTIES.getProperty("window_height");
        if(!isValidInt(property))
            VTUBER_PROPERTIES.setProperty("window_height", "400");
        property = VTUBER_PROPERTIES.getProperty("window_location");
        if(property == null) 
            VTUBER_PROPERTIES.setProperty("window_location", "se");
        property = VTUBER_PROPERTIES.getProperty("windows_taskbar_height");
        if(!isValidInt(property))
            VTUBER_PROPERTIES.setProperty("windows_taskbar_height", "50");
        property = VTUBER_PROPERTIES.getProperty("frames_per_second");
        if(!isValidInt(property))
            VTUBER_PROPERTIES.setProperty("frames_per_second", "60");

        save(configFile, VTUBER_PROPERTIES, VTUBER_DESC);
    }

    public static void saveAppProperties() {
        File configDir = AppPaths.getAppPath(Constants.APP_NAME, AppPaths.PathType.CONFIG);
        File configFile = new File(configDir, APP_CONFIG_FILE);
        save(configFile, APP_CONFIG_PROPERTIES, APP_CONFIG_DESC);
    }

    public static void saveVtuberProperties() {
        File configDir = AppPaths.getAppPath(Constants.APP_NAME, AppPaths.PathType.CONFIG);
        File configFile = new File(configDir, "VTUBER.sav");
        save(configFile, VTUBER_PROPERTIES, VTUBER_DESC);
    }

    private static void save(File file, Properties properties, String comment) {
        try {
            FileWriter writer = new FileWriter(file);
            properties.store(writer, comment);
            writer.close();
        } catch (IOException e) {
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

    public static boolean setVtuberProperty(String key, String value) {
        boolean isDone = setProperty(VTUBER_PROPERTIES, key, value);
        if(isDone) {
            saveVtuberProperties();
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
