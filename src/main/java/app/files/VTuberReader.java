package app.files;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.Collections;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Set;

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

    private final Map<String, Map<String, String>> sections = new LinkedHashMap<>();

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

            while ((line = reader.readLine()) != null) {
                line = line.strip();
                if (line.isEmpty() || line.startsWith("#")) continue;

                if (line.startsWith("[") && line.endsWith("]")) {
                    currentSection = line.substring(1, line.length() - 1);
                    sections.putIfAbsent(currentSection, new LinkedHashMap<>());
                } else if (currentSection != null && line.contains("=")) {
                    String[] parts = line.split("=", 2);
                    sections.get(currentSection).put(parts[0].strip(), parts[1].strip());
                }
            }
        }
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
    public String get(String section, String key) {
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
    public int getInt(String section, String key) {
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
    public boolean getBoolean(String section, String key) {
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
    public Set<String> getSections() {
        return sections.keySet();
    }

    /**
     * Obtiene el mapa contenido según la clave principal.
     *
     * @param section La clave principal.
     *
     * @return El mapa que se ha guardado según la clave principal.
     */
    public Map<String, String> getSection(String section) {
        return sections.getOrDefault(section, Collections.emptyMap());
    }
}
