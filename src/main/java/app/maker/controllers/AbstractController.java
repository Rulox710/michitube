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

package app.maker.controllers;

import app.engine.Observable;
import app.engine.Observer;
import javafx.fxml.FXML;

/**
 * Controlador abstracto que los demás controladores también
 * extenderán.
 */
public abstract class AbstractController extends Observable implements Observer {

    /**
     * Inicializa los elementos de <code>fxml</code> para añadirles la
     * lógica correspondiente.
     */
    @FXML
    public abstract void initialize();

    /**
     * Al invocarse este método cambia las cadenas usadas según el
     * idioma actual. Generalmente necesita usar
     * {@link javafx.application.Platform#runLater} para que no haya
     * problemas al momento de actualizar las cadenas.
     */
    public abstract void updateLanguage();

    /**
     * {@inheritDoc}
     * Este método se implementa vacío ya que no será necesario en
     * todos los controladores.
     */
    @Override
    public void update(char event, Object data) {}
}
