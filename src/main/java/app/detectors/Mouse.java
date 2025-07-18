package app.detectors;

import app.Constants;
import app.engine.Observable;
import app.engine.Observer;

import com.github.kwhat.jnativehook.GlobalScreen;
import com.github.kwhat.jnativehook.mouse.NativeMouseEvent;
import com.github.kwhat.jnativehook.mouse.NativeMouseMotionListener;

/**
 * Detecta y gestiona los movimientos del ratón en la pantalla.
 * Esta clase implementa {@link NativeMouseMotionListener} para
 * detectar los movimientos del ratón y actualizar su posición.
 * También actúa como un {@link Observer} para recibir notificaciones
 * sobre actualizaciones de otros componentes del sistema.
 */
public class Mouse extends Observable
        implements Observer, NativeMouseMotionListener {

    private byte x = 0, y = 0;
    private final byte MAX_X = 50, MAX_Y = 40;

    /**
     * Constructor de la clase Mouse.
     * Registra esta instancia como un oyente de eventos de movimiento
     * del ratón utilizando la clase {@link GlobalScreen}.
     */
    public Mouse() {
        GlobalScreen.addNativeMouseMotionListener(this);
    }

    /**
     * Limita un valor entero al rango válido para las coordenadas del
     * ratón.
     *
     * @param value El valor entero a limitar.
     * @return El valor limitado al rango de 0 a 100.
     */
    private byte limitToValidRange(int value) {
        return (byte) Math.min(Math.max(value, 0), 100);
    }

    /**
     * Se llama cuando el mouse es movido.
     * Cambia un atributo de la clase para ubicar donde está el mouse
     * actualmente
     */
    @Override
    public void nativeMouseMoved(NativeMouseEvent e) {
        int mouseX = e.getX();
        int mouseY = e.getY();
        x = limitToValidRange(
            (int) ((double) mouseX / Constants.FULLSCREEN_WIDTH * MAX_X)
        );
        y = limitToValidRange(
            (int) ((double) mouseY / Constants.FULLSCREEN_HEIGHT * MAX_Y)
        );
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void update(char event, Object data) {
        switch(event) {
            case 'u':
                byte[] pair = {x, y};
                notifyObservers('r', pair);
            default: break;
        }
    }
}
