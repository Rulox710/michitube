package app;

import java.awt.Font;
import java.awt.GraphicsEnvironment;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileWriter;
import java.io.IOException;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.Map;
import java.util.Scanner;

import javax.swing.UIManager;
import javax.swing.plaf.FontUIResource;

import com.github.kwhat.jnativehook.GlobalScreen;
import com.github.kwhat.jnativehook.NativeHookException;

import app.engine.DeltaTimeManager;
import app.engine.maker.guis.MainGUI;
import app.engine.readers.PropertiesM;
import app.engine.readers.TranslationM;
import app.vtuber.Window;
import app.vtuber.detectors.Keyboard;
import app.vtuber.detectors.Microphone;
import app.vtuber.detectors.Mouse;

/**
 * Esta clase representa la entrada principal del programa.
 * Controla la inicialización de las configuraciones, la creación de
 * la ventana principal y la detección de dispositivos
 * (micrófono, teclado y ratón).
 */
public class Main {

    /**
     * El método principal que inicia el programa.
     * Carga las configuraciones, crea la ventana principal y realiza
     * la detección de dispositivos.
     *
     * @param args Los argumentos de la línea de comandos
     *             (no se utilizan en este programa).
     */
    public static void main(String[] args) {
        TranslationM.load();
        PropertiesM.loadAppProperties();
        PropertiesM.loadVtuberProperties("String vtuberName");

        // Font font = new Font("Arial", Font.PLAIN, 14);
        // for (Enumeration<Object> keys = UIManager.getDefaults().keys(); keys.hasMoreElements();) {
        //     Object key = keys.nextElement();
        //     Object value = UIManager.get(key);
        //     if (value instanceof FontUIResource)
        //         UIManager.put(key, new FontUIResource(font));
        // }
        // try {
        //     Font roboto = Font.createFont(Font.TRUETYPE_FONT, new File(Constants.RES+"Roboto-Regular.ttf")).deriveFont(16f);
        //     GraphicsEnvironment ge = GraphicsEnvironment.getLocalGraphicsEnvironment();
        //     ge.registerFont(roboto);

        //     // Aplicarla como fuente global (sin comprometer compatibilidad multilingüe)
        //     for (Enumeration<Object> keys = UIManager.getDefaults().keys(); keys.hasMoreElements();) {
        //         Object key = keys.nextElement();
        //         Object value = UIManager.get(key);
        //         if (value instanceof FontUIResource) {
        //             UIManager.put(key, new FontUIResource(roboto));
        //         }
        //     }
        // } catch (Exception e) {
        //     e.printStackTrace();
        // }
        // System.out.println(UIManager.getFont("Label.font"));

        MainGUI configGui = new MainGUI();
        // Window window = new Window();

        // Microphone micDetector = null;
        // if(Boolean.parseBoolean(Constants.PROPERTIES.getProperty("microphone_detection"))) {
        //     micDetector = new Microphone();
        //     micDetector.addObserver(window);
        // }

        // try {
        //     GlobalScreen.registerNativeHook();
        // } catch (NativeHookException e) {
        //     e.printStackTrace();
        // }

        // Keyboard keyboard = null;
        // if(Boolean.parseBoolean(Constants.PROPERTIES.getProperty("keyboard_detection"))) {
        //     keyboard = new Keyboard();
        //     keyboard.addObserver(window);
        // }

        // Mouse mouse = null;
        // if(Boolean.parseBoolean(Constants.PROPERTIES.getProperty("mouse_detection"))) {
        //     mouse = new Mouse();
        //     mouse.addObserver(window);
        // }

        // DeltaTimeManager.getInstance().addObserver(window);
        // if(micDetector != null) DeltaTimeManager.getInstance().addObserver(micDetector);
        // if(keyboard != null) DeltaTimeManager.getInstance().addObserver(keyboard);
        // if(mouse != null) DeltaTimeManager.getInstance().addObserver(mouse);
    }
}
