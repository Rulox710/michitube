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

package app.maker.controllers.objects.builders;

import app.maker.controllers.objects.Infos.Info;

/**
 * Clase abstracta que define el patrón de construcción para
 * las diferentes informaciones de los objetos.
 *
 * Cada constructor debe implementar los métodos para establecer
 * los parámetros específicos y un método para obtener el
 * resultado final.
 */
public abstract class InfoBuilder {

    /**
     * Establece la posición X del objeto.
     *
     * @param xPos La posición X.
     */
    public void setXPos(int xPos) {
        throw new UnsupportedOperationException("Unimplemented method 'setXPos'");
    }

    /**
     * Establece la posición Y del objeto.
     *
     * @param yPos La posición Y.
     */
    public void setYPos(int yPos) {
        throw new UnsupportedOperationException("Unimplemented method 'setYPos'");
    }

    /**
     * Establece el ancho del objeto.
     *
     * @param width El ancho del objeto.
     */
    public void setWidth(int width) {
        throw new UnsupportedOperationException("Unimplemented method 'setWidth'");
    }

    /**
     * Establece la altura del objeto.
     *
     * @param height La altura del objeto.
     */
    public void setHeight(int height) {
        throw new UnsupportedOperationException("Unimplemented method 'setHeight'");
    }

    /**
     * Establece el color del objeto en formato hexadecimal.
     *
     * @param hexString El color en formato hexadecimal.
     */
    public void setColor(String hexString) {
        throw new UnsupportedOperationException("Unimplemented method 'setColor'");
    }

    /**
     * Establece la ruta del archivo asociado al objeto.
     *
     * @param path La ruta del archivo.
     */
    public void setPath(String path) {
        throw new UnsupportedOperationException("Unimplemented method 'setPath'");
    }

    /**
     * Establece un parámetro booleano para el objeto.
     *
     * @param param Un valor del parámetro booleano.
     */
    public void setUsage(boolean isUsed) {
        throw new UnsupportedOperationException("Unimplemented method 'setUsage'");
    }

    /**
     * Establece un parámetro entero para el objeto.
     *
     * @param param Un valor del parámetro entero.
     */
    public void setIntParam(int param) {
        throw new UnsupportedOperationException("Unimplemented method 'setIntParam'");
    }

    /**
     * Establece una imagen en formato RLE para el objeto.
     *
     * @param rle Cadena RLE de la imagen.
     */
    public void setRLE(String rle) {
        throw new UnsupportedOperationException("Unimplemented method 'setRLE'");
    }

    /**
     * Obtiene el resultado final, contruyendo un objeto que extiende a
     * {@link Info}.
     *
     * @return La información construida.
     */
    public abstract Info getResult();
}
