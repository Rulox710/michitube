package app;

/**
 * Enumeración con los posibles valoes para las distintas capas
 * que se pueden usar en el modelo vtuber.
 */
public enum Ids {
    BACKGROUND("background"),
    BODY("body"),
    EYES("eyes"),
    MOUTH("mouth"),
    TABLE("table"),
    KEYBOARD("keyboard"),
    MOUSE("mouse");

    private final String ID;

    /**
     * Constructor para crear un identificador de capa.
     *
     * @param id La cadena con la que se identifica esta capa.
     */
    private Ids(String id) {
        ID = id;
    }

    /**
     * Devuelve la cadena con la que se identifica esta capa.
     *
     * @return La cadena con la que se identifica esta capa.
     */
    public String getID() {
        return ID;
    }

    public Sections getEquivalent() {
        for(Sections section: Sections.values())
            if(section.getKEY().equals(ID)) return section;
        return null;
    }
}
