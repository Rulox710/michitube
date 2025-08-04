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

import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

/**
 * Una clase con el propósito de ser implementada por los observados.
 */
public abstract class Observable {

    protected final int THREADS = 5;
    // Para ahorrar problemas usamos listas concurrentes y se acabó
    protected List<Observer> observers = new CopyOnWriteArrayList<>();

    /**
     * Agrega un observador a su lista.
     *
     * @param observer La clase que va recibir notificaciones.
     */
    public void addObserver(Observer observer) {
        this.observers.add(observer);
    }

    /**
     * Elimina a un observador de su lista.
     *
     * @param observer La clase que ya no va a recibir
     *                 notificaciones.
     */
    public void removeObserver(Observer observer) {
        this.observers.remove(observer);
    }

    /**
     * Notifica a los observadores que ha habido un cambio.
     *
     * @param event La forma de identificar el evento.
     * @param data Un dato que se envía con la notificación.
     */
    public void notifyObservers(char event, Object data) {
        //observers.forEach(observer -> observer.update(event, data));
        ExecutorService executorService = Executors.newFixedThreadPool(THREADS);
        for(Observer observer: observers) {
            executorService.execute(() -> observer.update(event, data));
        }
        executorService.shutdown();
        try {
            executorService.awaitTermination(Long.MAX_VALUE, TimeUnit.NANOSECONDS);
        } catch(InterruptedException e) {
            Thread.currentThread().interrupt();
        }
    }
}
