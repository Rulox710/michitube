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
import app.maker.controllers.objects.Infos.SheetInfo;

/**
 * Constructor que es responsable de construir un objeto de tipo
 * {@link SheetInfo}.
 */
public class SheetInfoBuilder extends InfoBuilder {

    private int width, height;

    /**
     * {@inheritDoc}
     */
    @Override
    public void setWidth(int width) {
        this.width = (width < 1)? 300: width;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void setHeight(int height) {
        this.height = (height < 1)? 300: height;
    }

    /**
     * {@inheritDoc} En este caso, es un objeto tipo {@link SheetInfo}.
     */
    public Info getResult() {
        return new SheetInfo(width, height);
    }
}
