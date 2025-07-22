package app;

import java.util.Locale;

import com.github.kwhat.jnativehook.GlobalScreen;
import com.github.kwhat.jnativehook.NativeHookException;

import app.files.PropertiesM;
import app.files.TranslationM;

public class AppLauncher {

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

        String language = PropertiesM.getAppProperty("language");
        Locale locale = Locale.ENGLISH;
        switch(language) {
            case "EN":
                locale = Locale.ENGLISH;
                break;
            case "ES":
                locale = new Locale(language);
                break;
            default:
                locale = Locale.ENGLISH;
                break;
        }
        Locale.setDefault(locale);

        try {
            GlobalScreen.registerNativeHook();
        } catch(NativeHookException e) {
            e.printStackTrace();
        }

        Main.launchApp(args);
    }
}
