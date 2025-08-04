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

package app;

/**
 * Enum que contiene mensajes de log para diferentes eventos
 * en la aplicación.
 * Estos mensajes son utilizados para registrar el flujo de la
 * aplicación y ayudar en la depuración.
 */
public enum LogMessage {
    SPACE                     ("│"),
    APP_START                 ("       ╔═──────────────────────══╗\n"
                              +"═╦══──═╬ Inicio de la aplicación ╬═──══\n"
                              +" ║     ╚═──────────────────────══╝"),

    TRANSLATIONS_START(" ╠─┬─ Cargando Traducciones         ═══"),
    TRANSLATIONS_COUNT(" ║ ├─ Líneas leídas: %d"),
    TRANSLATIONS_LANG (" ║ └─ Idimas cargados: %s"),
    PROPERTIES_START  (" ╠─┬─ Cargando Propiedades          ═══"),
    PROPERTIES_LANG_O (" ║ ├─ Idioma detectado: %s"),
    PROPERTIES_LANG_X (" ║ ├─ Idioma no detectado. Usando %s"),
    PROPERTIES_DIR_O  (" ║ ├─ Ruta detectada: %s"),
    PROPERTIES_DIR_X  (" ║ ├─ Ruta no detectada. Usando: %s"),
    PROPERTIES_FPS_O  (" ║ ├─ Actualizaciones por segundo: %s"),
    PROPERTIES_FPS_X  (" ║ ├─ Sin actualizaciones por segundo. Usando: %s"),
    PROPERTIES_WINH_O (" ║ └─ Tamaño de barra de tareas: %s"),
    PROPERTIES_WINH_X (" ║ └─ Sin tamaño de barra de tareas. Usando: %s"),
    LOCALE_SET        (" ╠─── Asignando Localidad: %s       ═══"),
    GLOBALSCREEN_START(" ╠─── Iniciando detectores globales ═══"),
    GUI_START         (" ╚─┬─ Lanzando interfaz             ═══"),
    GUI_FXML          ("   ├─ FXML Cargado: %s"),
    GUI_FXML_END      ("   └─ FXML Cargado: %s\n"),
    GUI_FXML_X        ("   └─ FXML Error al cargar: %s\n"),

    PROPERTIES_SAVE   ("=== Guardadndo configuración de la aplicación ==="),
    GLOBALSCREEN_STOP ("!!! Cerrando detectores globales !!!"),
    GUI_HIDE          ("=== Escondiendo interfaz ==="),
    GUI_SHOW          ("=== Mostrando interfaz ==="),
    GUI_END           ("!!! Cerrando interfaz !!!"),
    DETECT_MICRO      ("=== Se ha iniciado el micrófono para el VTuber ==="),
    DETECT_KEYBO      ("=== Se ha iniciado el teclado para el VTuber ==="),
    DETECT_MOUSE      ("=== Se ha iniciado el ratón para el VTuber ==="),
    DETECT_MICRO_CLOSE("!!! Se ha apagado el micrófono para el VTuber !!!"),
    DETECT_KEYBO_CLOSE("!!! Se ha apagado el teclado para el VTuber !!!"),
    DETECT_MOUSE_CLOSE("!!! Se ha apagado el ratón para el VTuber !!!"),
    MODEL_SAVE_O      ("=== Modelo VTuber guardado correctamente ==="),
    MODEL_LOAD_O      ("=== Modelo VTuber cargado correctamente ==="),
    MODEL_SAVE_X      ("!!! No se pudo guardar el modelo VTuber !!!"),
    MODEL_LOAD_X      ("!!! No se pudo cargar el modelo VTuber !!!"),

    PROGRAM_END_O     ("\n"
                      +"     ╔═────────────────────────────────══╗\n"
                      +"══──═╬ Terminando programa correctamente ╬═──══\n"
                      +"     ╚═────────────────────────────────══╝"),
    PROGRAM_END_X     ("\n"
                      +"     ╔═─────────────────────────────────────══╗\n"
                      +"══──═╬ Terminando programa por un error grave ╬═──══\n"
                      +"     ╚═─────────────────────────────────────══╝");

    private final String message;

    /**
     * Constructor para inicializar el mensaje de log.
     *
     * @param message El mensaje que se mostrará en el log.
     */
    private LogMessage(String message) {
        this.message = message;
    }

    /**
     * Obtiene el mensaje de log.
     *
     * @return El mensaje de log.
     */
    public String get() {
        return message;
    }
}
