package app.files;

import app.Constants;

import java.io.InputStream;
import java.util.HashMap;
import java.util.Map;
import java.util.Scanner;

/**
 * Clase para encontrar las cadenas usadas en el texto de la aplicación
 * con traducciones a varios idiomas.
 */
public final class TranslationM {

    /**
     * Enumeración con las claves de los idiomas soportados.
     */
    public static enum LANGUAGES {
        EN("English"),
        ES("Español"),;

        private final String NAME;

        /**
         *
         * @param s Cadena con el nombre del idioma como debería ser
         *          mostrado en la interfaz sin importar el idioma
         *          seleccionado actualmente.
         */
        private LANGUAGES(String s) {
            this.NAME = s;
        }

        /**
         * Obtiene el nombre del idioma.
         *
         * @return La cadena que se pasó en el constructor.
         */
        public String toString() {
            return this.NAME;
        }
    }

    /** Ubicación de las cadenas traducidas. */
    private static final String TRANSLATIONS_ROUTE = Constants.RES
        + "translations.csv";

    /** Contiene cadenas en varios idiomas. */
    private static final Map<String, Map<String, String>> translationsMap =
        new HashMap<>();

    /**
     * Método estático para cargar el archivo con las cadenas con las
     * distintas traducciones.
     */
    public static void load() {
        InputStream in =
            TranslationM.class.getResourceAsStream(TRANSLATIONS_ROUTE);
        Scanner sc = new Scanner(in);
        String[] languages = sc.nextLine().split(",");
        while(sc.hasNextLine()) {
            String[] fields = sc.nextLine().split(",");
            Map<String, String> languageValues = new HashMap<>();
            for(int i = 1; i < fields.length; i++)
                languageValues.put(languages[i], fields[i]);
            translationsMap.put(fields[0], languageValues);
        }
        sc.close();
    }

    /**
     * Método estático para obtener traducciones de cadenas.
     *
     * @param label La clave para encontrar las traducciones.
     *
     * @return La cadena correspondiente a la clave en el idioma
     *         actualmente seleccionado.
     */
    public static final String getTranslatedLabel(String label) {
        return translationsMap.get(label).get(
            PropertiesM.getAppProperty("language")
        );
    }
}
