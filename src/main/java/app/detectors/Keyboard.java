package app.detectors;

import app.LogMessage;
import app.engine.DeltaTimeManager;
import app.engine.Observable;
import app.engine.Observer;

import com.github.kwhat.jnativehook.GlobalScreen;
import com.github.kwhat.jnativehook.keyboard.NativeKeyEvent;
import com.github.kwhat.jnativehook.keyboard.NativeKeyListener;

import java.util.HashSet;
import java.util.Set;

/**
 * Detecta y gestiona la entrada del teclado.
 * Esta clase implementa {@link NativeKeyListener} para detectar las
 * acciones del teclado.
 * También actua como {@link Observer} para recibir notificaciones
 * sobre actualizaciones de otros componentes del sistema.
 */
public class Keyboard extends Observable
        implements Observer, NativeKeyListener {

    private Set<String> keysPressedSet = new HashSet<>();

    /**
     * Constructor de la clase Keyboard.
     * Registra esta clase como listener para eventos de teclado
     * utilizando la clase {@link GlobalScreen}.
     */
    public Keyboard() {
        GlobalScreen.addNativeKeyListener(this);
        DeltaTimeManager.getInstance().addObserver(this);

        System.out.println(LogMessage.DETECT_KEYBO.get());
    }

    public void stopCapture() {
        GlobalScreen.removeNativeKeyListener(this);
        DeltaTimeManager.getInstance().removeObserver(this);

        System.out.println(LogMessage.DETECT_KEYBO_CLOSE.get());
    }

    /**
     * Asigna el valor de una tecla identificada por su nombre en
     * cadena generado por JNativeHook.
     *
     * @param key La cadena que identifica a la tecla.
     * @param isPressed Si esta tecla está presionada.
     */
    private void setKey(String key, boolean isPressed) {
        if(isPressed && !keysPressedSet.contains(key)) keysPressedSet.add(key);
        else if(!isPressed && keysPressedSet.contains(key))
            keysPressedSet.remove(key);
    }

    /**
     * Se llama cuando una tecla ha sido presionada.
     * Guarda el código de la tecla para mantener un registro de cuáles
     * están presionadas.
     */
    @Override
    public void nativeKeyPressed(NativeKeyEvent e) {
        setKey(NativeKeyEvent.getKeyText(e.getKeyCode()), true);
    }

    /**
     * Se llama cuando una tecla ha sido soltada.
     * Retira el código de la tecla que se mantiene en el registro de
     * cuáles están presionadas.
     */
    @Override
    public void nativeKeyReleased(NativeKeyEvent e) {
        setKey(NativeKeyEvent.getKeyText(e.getKeyCode()), false);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void update(char event, Object data) {
        switch(event) {
            case 'u': notifyObservers('k', keysPressedSet.size() > 0);
            default: break;
        }
    }
}
