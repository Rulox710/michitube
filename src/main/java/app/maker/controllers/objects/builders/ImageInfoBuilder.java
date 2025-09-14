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
import app.maker.controllers.objects.Infos.ImageInfo;

/**
 * Constructor que es responsable de construir un objeto de tipo
 * {@link ImageInfo}.
 */
public class ImageInfoBuilder extends InfoBuilder {

    private int x, y, width, height;
    private String path, rle;

    /**
     * {@inheritDoc}
     */
    @Override
    public void setXPos(int xPos) {
        x = xPos;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void setYPos(int yPos) {
        y = yPos;
    }

    /**
     * {@inheritDoc} No permite valores menores a <code>1</code> y
     * éste es pasado, se cambia a <code>290</code>.
     */
    @Override
    public void setWidth(int width) {
        this.width = (width < 1)? 290: width;
    }

    /**
     * {@inheritDoc} No permite valores menores a <code>1</code> y
     * éste es pasado, se cambia a <code>290</code>.
     */
    @Override
    public void setHeight(int height) {
        this.height = (height < 1)? 290: height;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void setPath(String path) {
        this.path = path;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void setRLE(String rle) {
        this.rle = rle;
    }

    /**
     * {@inheritDoc} En este caso, es un objeto tipo {@link ImageInfo}.
     * En caso de que no tenga ruta a una imagen y tampoco una imagen
     * en formato RLE devuelve <code>null</code>.
     */
    public Info getResult() {
        if(path == null && rle == null) return null;
        return new ImageInfo(x, y, width, height, path, rle);
    }
}
