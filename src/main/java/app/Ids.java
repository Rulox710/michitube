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
 * Enumeración con los posibles valoes para las distintas capas
 * que se pueden usar en el modelo vtuber.
 */
public enum Ids {
    BACKGROUND("background"),
    BODY("body"),
    TABLE("table"),
    EYES("eyes"),
    MOUTH("mouth"),
    KEYBOARD("keyboard"),
    MOUSE("mouse"),
    HAIR("hair"),
    EXTRA("extra");

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
