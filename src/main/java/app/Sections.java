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
 * Enumeración con los posibles identificadores que tienen las claves
 * principales a usar para leer y escribir archivos con información del
 * modelo vtuber.
 *
 * @see app.files.VTuberReader
 * @see app.files.VTuberWriter
 */
public enum Sections {
    SHEET("sheet"),
    BACKGROUND("background"),
    BODY("body"),
    TABLE("table"),
    HAIR("hair"),
    EYES("eyes"),
    MOUTH("mouth"),
    KEYBOARD("keyboard"),
    MOUSE("mouse"),
    EXTRA("extra");

    private final String KEY;

    /**
     * Constructor para crear una clave principal.
     *
     * @param key La cadena con la que se identifica esta clave.
     */
    private Sections(String key) {
        KEY = key;
    }

    /**
     * Devuelve la cadena con la que se identifica esta clave
     * principal.
     *
     * @return La cadena con la que se identifica esta clave
     *         principal.
     */
    public String getKEY() {
        return KEY;
    }

    public static Sections fromString(String key) {
        for(Sections k: Sections.values())
            if(k.KEY.equals(key)) return k;
        return null;
    }

    public Ids getEquivalent() {
        for(Ids id: Ids.values())
            if(id.getID().equals(KEY)) return id;
        return null;
    }

    /**
     * Enumeración con los posibles identificadores que tienen las
     * claves secundarias a usar para leer y escribir archivos con
     * información del modelo vtuber.
     *
     * @see app.files.VTuberReader
     * @see app.files.VTuberWriter
     */
    public static enum KEYS {
        XPOS("X"),
        XPOS_A("X_A"),
        XPOS_B("X_B"),
        XPOS_C("X_C"),
        XPOS_D("X_D"),
        XPOS_0("X_0"),
        XPOS_1("X_1"),
        XPOS_2("X_2"),
        YPOS("Y"),
        YPOS_A("Y_A"),
        YPOS_B("Y_B"),
        YPOS_C("Y_C"),
        YPOS_D("Y_D"),
        YPOS_0("Y_0"),
        YPOS_1("Y_1"),
        YPOS_2("Y_2"),

        WIDTH("W"),
        WIDTH_0("W_0"),
        WIDTH_1("W_1"),
        WIDTH_2("W_2"),
        HEIGHT("H"),
        HEIGHT_0("H_0"),
        HEIGHT_1("H_1"),
        HEIGHT_2("H_2"),

        PATH("P"),
        PATH_0("P_0"),
        PATH_1("P_1"),
        PATH_2("P_2"),

        RLE("RLE"),
        RLE_0("RLE_0"),
        RLE_1("RLE_1"),
        RLE_2("RLE_2"),

        COLOR("C"),
        USE("use"),
        IMAGE("image"),
        USECOLOR("color"),
        TIMETO("timeto"),
        TIMEBLINK("timeblink"),
        CHNLS("channels"),
        UPS("updates"),
        SENS("sensitivity");

        private final String KEY;

        /**
         * Constructor para crear una clave secundaria.
         *
         * @param key La cadena con la que se identifica esta clave.
         */
        private KEYS(String key) {
            KEY = key;
        }

        /**
         * Devuelve la cadena con la que se identifica esta clave
         * secundaria.
         *
         * @return La cadena con la que se identifica esta clave
         *         secundaria.
         */
        public String getKEY() {
            return KEY;
        }

        /**
         * Devuelve la cadena con la que se identifica esta clave
         * secundaria en un formato específico.
         *
         * @param digit El número que se añadirá al final de la clave.
         * @return La cadena con la que se identifica esta clave
         *         secundaria en el formato "KEY_digit".
         */
        @Deprecated
        public String getFormatKEY(int digit) {
            return String.format("%s_%d", KEY, digit);
        }

        public static KEYS fromString(String key) {
            for(KEYS k: KEYS.values())
                if(k.KEY.equals(key)) return k;
            return null;
        }
    }
}
