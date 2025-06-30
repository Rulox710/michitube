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
