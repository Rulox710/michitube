package app.files;

import java.io.BufferedWriter;
import java.io.FileWriter;
import java.io.IOException;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.Map;

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

    private final Map<String, Map<String, String>> sections = new LinkedHashMap<>();

    /**
     * Agrega una clave principal si esta falta en el mapa que luego se
     * usa para guardar en el archivo.
     *
     * @param sectionName La cadena con la que se identifica ésta clave.
     */
    private void addSection(String sectionName) {
        sections.putIfAbsent(sectionName, new LinkedHashMap<>());
    }

    /**
     * Agrega un valor al mapa según la clave principal y secundaria.
     *
     * @param sectionName La cadena con la que se identifica la clave
     *                    principal.
     * @param key La cadena con la que se identifica la clave
     *            secundaria.
     * @param value El valor que se va a guardar.
     */
    public void put(String sectionName, String key, Object value) {
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
        try (BufferedWriter writer = new BufferedWriter(new FileWriter(path))) {
            for (var entry : sections.entrySet()) {
                writer.write("[" + entry.getKey() + "]");
                writer.newLine();
                for (var kv : entry.getValue().entrySet()) {
                    writer.write(kv.getKey() + "=" + kv.getValue());
                    writer.newLine();
                }
                writer.newLine();
            }
        }
    }

    public Map<String, Map<String, String>> getClone() {
        Map<String, Map<String, String>> clone = new HashMap<>();
        for (Map.Entry<String, Map<String, String>> entry : sections.entrySet())
            clone.put(entry.getKey(), new HashMap<>(entry.getValue()));
        return clone;
    }
}
