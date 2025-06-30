package app;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.Toolkit;

import javax.sound.sampled.AudioFormat;
import javax.sound.sampled.AudioSystem;
import javax.sound.sampled.DataLine;
import javax.sound.sampled.TargetDataLine;

/**
 * Contiene valores constantes que son relevantes para varias partes
 * del programa.
 */
public final class Constants {

    /** Nombre de la app */
    public static final String APP_NAME = "MichiTube";

    /**
     * Tipo de separación usada por el sistema operativo para navegar
     * por su sistema de archivos.
     */
    public static final String SEP = System.getProperty("file.separator");

    /** Configuraciones de la app */
    public static final String CONFIF_FILE = "config.properties";


    /** Ruta relativa del directorio de recursos. */
    public static final String RES = "/res/";
    /** Ruta relativa del directorio de recursos. */
    public static final String AST = RES + "assets/";
    /** Ruta relativa del directorio de salida. */
    public static final String OUT = "." + Constants.SEP + "out" + Constants.SEP;

    /**
     * Dimensiones de la pantalla.
     */
    private static final Dimension SCREEN_SIZE =
        Toolkit.getDefaultToolkit().getScreenSize();
    /** Ancho de la pantalla. */
    public static final int FULLSCREEN_WIDTH = (int) SCREEN_SIZE.getWidth();
    /** Altura de la pantalla. */
    public static final int FULLSCREEN_HEIGHT = (int) SCREEN_SIZE.getHeight();

    /** Ruta de los recursos visuales. */
    public static final String SKIN_ROUTE = RES + "skin.png",
                               TABLE_ROUTE = RES + "table.png",
                               MOUSE_ROUTE = RES + "mouse.png",
                               HANDMOUSE_ROUTE = RES + "handmouse.png",
                               HANDKEYBOARD_ROUTE = RES + "handkeyboard.png",
                               LAPTOP_ROUTE = RES + "laptop.png",
                               MOUTH_ROUTE = RES + "mouth.png",
                               HAIR_ROUTE = RES + "hair.png",
                               FACE_1 = RES + "face-1.png",
                               FACE_2 = RES + "face-2.png";

    /** Arreglo de rutas de los recursos. */
    public static final String[] ROUTES = {
            SKIN_ROUTE, TABLE_ROUTE, MOUSE_ROUTE, HANDMOUSE_ROUTE,
            HANDKEYBOARD_ROUTE, MOUTH_ROUTE, HAIR_ROUTE, LAPTOP_ROUTE,
            FACE_1, FACE_2
        };
    
    private static AudioFormat format = new AudioFormat(8000.0f, 8, 1, true, false);
    private static DataLine.Info info = new DataLine.Info(TargetDataLine.class, format);
    public static final boolean IS_MICRO = AudioSystem.isLineSupported(info)? true : false;
    
    public static final Color BLACK_78 = new Color(78, 78, 78);
    public static final Color BLACK_50 = new Color(50, 50, 50);
}