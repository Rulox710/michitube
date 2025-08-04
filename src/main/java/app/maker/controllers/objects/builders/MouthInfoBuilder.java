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

import app.maker.controllers.objects.Infos.MouthInfo;
import app.maker.controllers.objects.Infos.Info;

/**
 * Constructor que es responsable de construir un objeto de tipo
 * {@link MouthInfo}.
 */
public class MouthInfoBuilder extends InfoBuilder {

    private boolean usage;
    private int [] intParams = new int[3];
    private String[] strParams = {"", ""};
    private int counterI = 0, counterS = 0;

    /**
     * Agrega las rutas de las imágenes de la boca.
     * Pueden agregarse hasta dos parámetros.
     *
     * -El primero indica la ruta del archivo de la imagen por
     *  defecto.
     * -El segundo indica la ruta del archivo de la imagen cuando se
     *  detecta sonido.
     *
     * @param path Ruta del archivo de la imagen.
     */
    public void setPath(String path) {
        strParams[counterS] = path;
        counterS++;
    }

    /**
     * Indica si se usa o no el micrófono para detectar sonido.
     *
     * @param param Si se detecta o no el micrófono.
     */
    @Override
    public void setUsage(boolean isUsed) {
        usage = isUsed;
    }

    /**
     * Agrega números enteros al objeto.
     * Pueden agregarse hasta tres parámetros.
     *
     * - El primero indica cuantos canales tiene el micrófono y solo
     *   puede ser <code>1<code> o <code>2<code>. En caso de ser menor
     *   a <code>1</code> se cambia a <code>1</code> y mayor a
     *   <code>2</code> cambia a <code>2</code>.
     * - El segundo indica las actualizaciones por segundo y puede
     *   estar entre <code>1<code> y <code>120<code>. En caso de ser
     *   menor a <code>1</code> se cambia a <code>1</code> y mayor a
     *   <code>120</code> cambia a <code>120</code>.
     * - El tercero indica la sensibilidad del micrófono y puede estar
     *   entre <code>1</code> y <code>100</code>. En caso de ser menor
     *   a <code>1</code> se cambia a <code>1</code> y mayor a
     *   <code>100</code> cambia a <code>100</code>.
     *
     * @param param Un valor del parámetro entero.
     */
    @Override
    public void setIntParam(int param) {
        if(param < 1) param = 1;

        switch(counterI) {
            case 0: if(param > 2) param = 2;
            break;

            case 1: if(param > 120) param = 120;
            break;

            default: if(param > 100) param = 100;
            break;
        }

        intParams[counterI] = param;
        counterI++;
    }

    /**
     * {@inheritDoc} En este caso, es un objeto tipo {@link MouthInfo}.
     */
    @Override
    public Info getResult() {
        return new MouthInfo(strParams[0], usage, intParams[0], intParams[1], intParams[2], strParams[1]);
    }
}
