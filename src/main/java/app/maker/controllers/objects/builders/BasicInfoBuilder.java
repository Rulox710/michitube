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

import app.maker.controllers.objects.Infos.BasicInfo;
import app.maker.controllers.objects.Infos.Info;

/**
 * Constructor que es responsable de construir un objeto de tipo
 * {@link BasicInfo}.
 */
public class BasicInfoBuilder extends InfoBuilder {

    private boolean usage;
    private String[] strParams = {"", "", ""};
    private int counterS = 0;

    /**
     * Agrega las rutas de las imágenes de la boca.
     * Pueden agregarse hasta dos parámetros.
     *
     * -El primero indica la ruta del archivo de la imagen por
     *  defecto.
     * -El segundo indica la ruta del archivo de la imagen cuando se
     *  usa una característica.
     *
     * @param path Ruta del archivo de la imagen.
     */
    @Override
    public void setPath(String path) {
        strParams[counterS] = path;
        counterS++;
    }

    /**
     * Indica si se usa o no una característica.
     *
     * @param param Si se usa o no la característica.
     */
    @Override
    public void setUsage(boolean isUsed) {
        usage = isUsed;
    }

    /**
     * {@inheritDoc} En este caso, es un objeto tipo {@link BasicInfo}.
     */
    @Override
    public Info getResult() {
        return new BasicInfo(strParams[0], strParams[1], usage, strParams[2]);
    }
}
