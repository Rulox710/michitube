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

package app.maker.controllers.objects;

/**
 * Clase que contiene clases auxiliares para manejar doatos primitivos.
 */
public final class Objects {

    public double x, y;

    /**
     * Clase que puede llevar dos números enteros, util para
     * representar coordenadas o dimensiones.
     */
    public static class Delta {
        public double x, y;

        public Delta() {}

        public Delta(double x, double y) {
            this.x = x;
            this.y = y;
        }
    }

    /**
     * Clase que indica si un objeto se puede mover en una dirección
     * horizontal o verticalmente, y si está bloqueado.
     */
    public static class DirectionLock {
        public boolean horizontal = false;
        public boolean vertical = false;
        public boolean locked = false;

        /**
         * Vuelve los valores de dirección a su estado inicial y
         * desbloquea el objeto.
         */
        public void reset() {
            horizontal = false;
            vertical = false;
            locked = false;
        }
    }
}
