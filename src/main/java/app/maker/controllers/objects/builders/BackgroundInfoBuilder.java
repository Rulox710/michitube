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

import app.maker.controllers.objects.Infos.BackgroundInfo;
import app.maker.controllers.objects.Infos.Info;

/**
 * Constructor que es responsable de construir un objeto de tipo
 * {@link BackgroundInfo}.
 */
public class BackgroundInfoBuilder extends InfoBuilder {

    private String color, path = "";
    private boolean[] usage = new boolean[2];
    private int counter = 0;

    /**
     * {@inheritDoc}
     */
    @Override
    public void setColor(String hexString) {
        color = hexString;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void setPath(String path) {
        this.path = path;
    }

    /**
     * Agrega parametros booleanos al objeto.
     * Pueden agregarse hasta dos parámetros booleanos.
     *
     * - El primero indica si se usa una imagen de fondo
     * - El segundo si se usa un color.
     *
     * @param param Un valor del parámetro booleano.
     */
    @Override
    public void setUsage(boolean param) {
        usage[counter] = param;
        counter++;
    }


    /**
     * {@inheritDoc} En este caso, es un objeto tipo
     * {@link BackgroundInfo}.
     */
    @Override
    public Info getResult() {
        return new BackgroundInfo(usage[0], path, usage[1], color);
    }

}
