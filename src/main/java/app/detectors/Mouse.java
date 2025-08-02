package app.detectors;

import app.Constants;
import app.LogMessage;
import app.engine.DeltaTimeManager;
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

    private double x = 0, y = 0;

    /**
     * Constructor de la clase Mouse.
     * Registra esta instancia como un oyente de eventos de movimiento
     * del ratón utilizando la clase {@link GlobalScreen}.
     */
    public Mouse() {
        GlobalScreen.addNativeMouseMotionListener(this);
        DeltaTimeManager.getInstance().addObserver(this);

        System.out.println(LogMessage.DETECT_MOUSE.get());
    }

    public void stopCapture() {
        GlobalScreen.removeNativeMouseMotionListener(this);
        DeltaTimeManager.getInstance().removeObserver(this);

        System.out.println(LogMessage.DETECT_MOUSE_CLOSE.get());
    }

    /**
     * Se llama cuando el mouse es movido.
     * Cambia un atributo de la clase para ubicar donde está el mouse
     * actualmente
     */
    @Override
    public void nativeMouseMoved(NativeMouseEvent e) {
        double mouseX = e.getX();
        double mouseY = e.getY();

        x = Math.min(Math.max(0, mouseX / Constants.FULLSCREEN_WIDTH), 1);
        y = Math.min(Math.max(0, mouseY / Constants.FULLSCREEN_HEIGHT), 1);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void update(char event, Object data) {
        switch(event) {
            case 'u':
                double[] pair = {x, y};
                notifyObservers('r', pair);
            default: break;
        }
    }
}
