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

package app.engine;

/**
 * Una interfaz con el propósito de ser implementada por los
 * observadores.
 */
public interface Observer {

    /**
     * Método que realiza acciones al recibir una notificación.
     *
     * @param event Banderas que identifican al evento.
     * @param data Un dato que es pasado al realizarse la
     *             notificación.
     */
    public abstract void update(char event, Object data);
}
