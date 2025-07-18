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
    EYES("eyes"),
    MOUTH("mouth"),
    TABLE("table"),
    KEYBOARD("keyboard"),
    MOUSE("mouse");

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
        YPOS("Y"),
        WIDTH("W"),
        HEIGHT("H"),
        COLOR("C"),
        USE("use"),
        PATH("P"),
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
        public String getFormatKEY(int digit) {
            return String.format("%s_%d", KEY, digit);
        }
    }
}
