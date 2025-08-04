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

import app.LogMessage;

import java.io.InputStream;
import java.util.Arrays;
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
    private static final String TRANSLATIONS_ROUTE = "/translations.csv";

    /** Contiene cadenas en varios idiomas. */
    private static final Map<String, Map<String, String>> translationsMap =
        new HashMap<>();

    /**
     * Método estático para cargar el archivo con las cadenas con las
     * distintas traducciones.
     */
    public static void load() {
        System.out.println(LogMessage.TRANSLATIONS_START.get());
        int count = 0;
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
            count++;
        }
        sc.close();

        System.out.println(String.format(LogMessage.TRANSLATIONS_COUNT.get(), count));
        System.out.println(String.format(
            LogMessage.TRANSLATIONS_LANG.get(),
            String.join(", ", Arrays.copyOfRange(languages, 1, languages.length))
        ));
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
