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

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.Collections;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Set;

import app.Sections;
import app.Sections.KEYS;

/**
 * Clase que lee un archivo de texto y convierte su contenido en datos
 * acerca del modelo vtuber si los tiene con un formato dado por
 * {@link VTuberWriter#saveToFile}.
 *
 * Este archivo para ser leido correctamente tiene que estar en el
 * siguiete formato:
 *
 * - Clave principal:
 * <code>[<em>Nombre de la clave</em>]</code>
 *
 * - Clave secundaria:
 * <code><em>Nombre de la clave</em>=<em>Valor de la clave</em></code>
 *
 * - Ejemplo completo:
 * <code>
 * [sheet]<br>
 * W=502<br>
 * H=301<br>
 * <br>
 * [keyboard]<br>
 * X_0=308<br>
 * Y_0=111<br>
 * W_0=127<br>
 * H_0=111<br>
 * P_0=file:/C:/Users/user/Documents/michitube/res/keyboard.png<br>
 * use=false<br>
 * </code>...
 */
public class VTuberReader {

    public final Map<Sections, Map<KEYS, String>> sections = new LinkedHashMap<>();

    /**
     * Carga toda la información dentro de un archivo que se pueda del
     * modelo vtuber y la guarda en un mapa.
     *
     * @param path Ruta del archivo de donde se leerá la información.
     *
     * @throws IOException
     */
    public void loadFromFile(String path) throws IOException {
        try (BufferedReader reader = new BufferedReader(new FileReader(path))) {
            String line;
            String currentSection = null;
            Sections currentEnum = null;

            while ((line = reader.readLine()) != null) {
                line = line.strip();
                if(line.isEmpty() || line.startsWith("#")) continue;

                if(line.startsWith("[") && line.endsWith("]")) {
                    currentSection = line.substring(1, line.length() - 1);
                    currentEnum = Sections.fromString(currentSection);
                    sections.putIfAbsent(currentEnum, new LinkedHashMap<>());
                } else if(currentSection != null && line.contains("=")) {
                    String[] parts = line.split("=", 2);
                    sections.get(currentEnum).put(
                        KEYS.fromString(parts[0].strip()), parts[1].strip()
                    );
                }
            }
        }
    }

    public void loadFromMap(Map<Sections, Map<KEYS, String>> infoMap) {
        //Map.Entry<String, Map<KEYS, String>>
        for(var entry: infoMap.entrySet())
            sections.put(entry.getKey(), new HashMap<>(entry.getValue()));
    }

    /**
     * Obtiene la información correspondiente a las claves si ésta es
     * una cadena. En caso de no encontrarla o que no sea una cadena
     * da una cadena vacía.
     *
     * @param section Clave principal.
     * @param key Clave secundaria.
     *
     * @return La cadena guardada en el mapa o cadena vacía.
     */
    public String get(Sections section, KEYS key) {
        return sections.getOrDefault(section, Collections.emptyMap())
                .getOrDefault(key, "");
    }

    /**
     * Obtiene la información correspondiente a las claves si ésta es
     * un número entero. En caso de no encontrarla o que no sea un
     * número entero da el número <code>-1</code>.
     *
     * @param section Clave principal.
     * @param key Clave secundaria.
     *
     * @return El entero guardado en el mapa o <code>-1</code>.
     */
    public int getInt(Sections section, KEYS key) {
        String str = get(section, key);
        if(str.length() == 0) return -1;
        return Integer.parseInt(str);
    }

    /**
     * Obtiene la información correspondiente a las claves si ésta es
     * un booleano. En caso de no encontrarla o que no sea un
     * booleano da el valor <code>false</code>.
     *
     * @param section Clave principal.
     * @param key Clave secundaria.
     *
     * @return El booleano guardado en el mapa o <code>false</code>.
     */
    public boolean getBoolean(Sections section, KEYS key) {
        String str = get(section, key);
        if(str.length() == 0) return false;
        return Boolean.parseBoolean(str);
    }

    /**
     * Obtiene el conjunto de las claves principales en el mapa.
     *
     * @return El conjunto de todas las claves encontradas en el
     *         archivo.
     */
    public Set<Sections> getSections() {
        return sections.keySet();
    }

    /**
     * Obtiene el mapa contenido según la clave principal.
     *
     * @param section La clave principal.
     *
     * @return El mapa que se ha guardado según la clave principal.
     */
    public Map<KEYS, String> getSection(String section) {
        return sections.getOrDefault(section, Collections.emptyMap());
    }
}
