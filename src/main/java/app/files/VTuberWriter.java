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

import java.io.BufferedWriter;
import java.io.FileWriter;
import java.io.IOException;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.Map;

import app.Sections;
import app.Sections.KEYS;

/**
 * Clase que escribe un archivo de texto acerca del modelo vtuber en un formato
 * que luego será legible por {@link VTuberReader#loadFromFile}.
 *
 * Este archivo para se crea con el siguiete formato:
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
public class VTuberWriter {

    private final Map<Sections, Map<KEYS, String>> sections = new LinkedHashMap<>();

    /**
     * Agrega una clave principal si esta falta en el mapa que luego se
     * usa para guardar en el archivo.
     *
     * @param sectionName La sección con la que se identifica ésta clave.
     */
    private void addSection(Sections sectionName) {
        sections.putIfAbsent(sectionName, new LinkedHashMap<>());
    }

    /**
     * Agrega un valor al mapa según la clave principal y secundaria.
     *
     * @param sectionName La sección con la que se identifica la clave
     *                    principal.
     * @param key La llave con la que se identifica la clave
     *            secundaria.
     * @param value El valor que se va a guardar.
     */
    public void put(Sections sectionName, KEYS key, Object value) {
        addSection(sectionName);
        String v = (value != null)? value.toString(): "null";
        sections.get(sectionName).put(key, v);
    }

    /**
     * Convierte el mapa con todo su contenido en el formato indicado
     * para el archivo.
     *
     * @param path Ruta del archivo de donde se guardará la información.
     *
     * @throws IOException
     */
    public void saveToFile(String path) throws IOException {
        try(BufferedWriter writer = new BufferedWriter(new FileWriter(path))) {
            //Entry<Sections, Map<String, String>>
            for(var entry: sections.entrySet()) {
                writer.write("[" + entry.getKey().getKEY() + "]");
                writer.newLine();
                for(var kv : entry.getValue().entrySet()) {
                    writer.write(kv.getKey().getKEY() + "=" + kv.getValue());
                    writer.newLine();
                }
                writer.newLine();
            }
        }
    }

    public Map<Sections, Map<KEYS, String>> getClone() {
        Map<Sections, Map<KEYS, String>> clone = new HashMap<>();
        for(var entry : sections.entrySet())
            clone.put(entry.getKey(), new HashMap<>(entry.getValue()));
        return clone;
    }
}
