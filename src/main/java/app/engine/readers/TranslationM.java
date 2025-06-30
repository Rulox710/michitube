package app.engine.readers;

import java.io.InputStream;
import java.util.HashMap;
import java.util.Map;
import java.util.Scanner;

import app.Constants;


public final class TranslationM {

    public static enum LANGUAGES {
        EN("English", 0),
        ES("Español", 1),;

        private final String name; 
        private final int index;      

        private LANGUAGES(String s, int index) {
            this.name = s;
            this.index = index;
        }

        public String toString() {
            return this.name;
        }

        public int getIndex() {
            return this.index;
        }
    }

    /** Ubicación de las cadenas traducidas. */
    private static final String TRANSLATIONS_ROUTE = Constants.RES + "translations.csv";

    /**
     * Contiene cadenas en varios idiomas.
     */
    private static final Map<String, Map<String, String>> translationsMap =
        new HashMap<>();

    public static void load() {
        InputStream in = TranslationM.class.getResourceAsStream(TRANSLATIONS_ROUTE);
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

    /** Método para obtener traducciones de cadenas */
    public static final String getTranslatedLabel(String label) {
        return translationsMap.get(label).get(
            PropertiesM.getAppProperty("language")
        );
    }
}
