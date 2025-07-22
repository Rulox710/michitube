package app.vtuber;

import app.Ids;
import app.Sections;
import app.Sections.KEYS;
import app.detectors.Keyboard;
import app.detectors.Microphone;
import app.engine.DeltaTimeManager;
import app.engine.Observable;
import app.engine.Observer;
import app.files.TranslationM;
import app.files.VTuberReader;

import com.github.kwhat.jnativehook.GlobalScreen;
import com.github.kwhat.jnativehook.NativeHookException;

import java.awt.Color;
import java.awt.event.FocusEvent;
import java.awt.event.FocusListener;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.util.HashMap;
import java.util.LinkedList;

import javax.swing.JFrame;

/**
 * Clase que representa la ventana principal de la aplicación.
 * Esta clase controla la interfaz gráfica de usuario y la interacción
 * con el teclado cuando la ventana tiene el foco.
 */
public class VTuberWindow extends Observable implements KeyListener {

    private JFrame window;
    private MichiPanel panel;
    private boolean undecorated = false, windowFocused = false;
    private VTuberReader infoMap;
    private Microphone micDetector;
    private Keyboard keyDetector;

    private final int[] TARGET_SEQUENCE_EXIT = {
        KeyEvent.VK_CONTROL, KeyEvent.VK_SHIFT, KeyEvent.VK_C
    };
    private final int[] TARGET_SEQUENCE_DECORATE = {
        KeyEvent.VK_CONTROL, KeyEvent.VK_SHIFT, KeyEvent.VK_D
    };
    private final LinkedList<Integer> keyQueue = new LinkedList<>();

    /**
     * Configura y muestra la ventana principal.
     */
    public void setMap(VTuberReader infoMap) {
        this.infoMap = infoMap;

        window = new JFrame();
        window.setSize(
            infoMap.getInt(Sections.SHEET.getKEY(), KEYS.WIDTH.getKEY()),
            infoMap.getInt(Sections.SHEET.getKEY(), KEYS.HEIGHT.getKEY())
        );
        window.setResizable(false);
        window.setUndecorated(undecorated);

        String strColor = infoMap.get(
            Sections.BACKGROUND.getKEY(), KEYS.COLOR.getKEY()
        );
        int r = Integer.valueOf(strColor.substring(1, 3), 16);
        int g = Integer.valueOf(strColor.substring(3, 5), 16);
        int b = Integer.valueOf(strColor.substring(5, 7), 16);
        int a = Integer.valueOf(strColor.substring(7, 9), 16);
        Color backgroundColor = new Color(r, g, b, a);

        window.getContentPane().setBackground(backgroundColor);
        //window.setBackground(new Color(0, 0, 0, 0));
        window.setTitle(TranslationM.getTranslatedLabel("gui_title"));

        window.addFocusListener(new FocusListener() {
            @Override
            public void focusGained(FocusEvent e) {
                windowFocused = true;
            }

            @Override
            public void focusLost(FocusEvent e) {
                windowFocused = false;
            }
        });
        window.addKeyListener(this);

        startMichiPane();
        startInputMethods();

        window.setDefaultCloseOperation(JFrame.DO_NOTHING_ON_CLOSE);
        window.addWindowListener(new WindowAdapter() {
            @Override
            public void windowClosing(WindowEvent e) {
                window.dispose();
                if(micDetector != null) micDetector.stopCapture();
                if(keyDetector != null) keyDetector.stopCapture();
                DeltaTimeManager.getInstance().removeObserver(panel);
                notifyObservers('z', null);
            }
        });

        window.setVisible(true);
    }

    private void startMichiPane() {
        panel = new MichiPanel();
        int[] params = new int[4];
        for(Ids layer: Ids.values()) {
            switch(layer) {
                case BACKGROUND:
                    if(!infoMap.getBoolean(layer.getID(), KEYS.IMAGE.getKEY())) break;
                    params[0] = infoMap.getInt(layer.getID(), KEYS.XPOS.getFormatKEY(0));
                    params[1] = infoMap.getInt(layer.getID(), KEYS.YPOS.getFormatKEY(0));
                    params[2] = infoMap.getInt(layer.getID(), KEYS.WIDTH.getFormatKEY(0));
                    params[3] = infoMap.getInt(layer.getID(), KEYS.HEIGHT.getFormatKEY(0));
                    panel.setImage(layer, 0, infoMap.get(layer.getID(), KEYS.PATH.getFormatKEY(0)));
                break;

                case TABLE:
                    if(!infoMap.getBoolean(layer.getID(), KEYS.USE.getKEY())) break;
                    params[0] = infoMap.getInt(layer.getID(), KEYS.XPOS.getFormatKEY(0));
                    params[1] = infoMap.getInt(layer.getID(), KEYS.YPOS.getFormatKEY(0));
                    params[2] = infoMap.getInt(layer.getID(), KEYS.WIDTH.getFormatKEY(0));
                    params[3] = infoMap.getInt(layer.getID(), KEYS.HEIGHT.getFormatKEY(0));
                    panel.setImage(layer, 0, infoMap.get(layer.getID(), KEYS.PATH.getFormatKEY(0)));
                break;

                case BODY:
                    params[0] = infoMap.getInt(layer.getID(), KEYS.XPOS.getFormatKEY(0));
                    params[1] = infoMap.getInt(layer.getID(), KEYS.YPOS.getFormatKEY(0));
                    params[2] = infoMap.getInt(layer.getID(), KEYS.WIDTH.getFormatKEY(0));
                    params[3] = infoMap.getInt(layer.getID(), KEYS.HEIGHT.getFormatKEY(0));
                    panel.setImage(layer, 0, infoMap.get(layer.getID(), KEYS.PATH.getFormatKEY(0)));
                break;

                case EYES:
                    params = new int[10];

                    params[0] = infoMap.getInt(layer.getID(), KEYS.XPOS.getFormatKEY(0));
                    params[1] = infoMap.getInt(layer.getID(), KEYS.YPOS.getFormatKEY(0));
                    params[2] = infoMap.getInt(layer.getID(), KEYS.WIDTH.getFormatKEY(0));
                    params[3] = infoMap.getInt(layer.getID(), KEYS.HEIGHT.getFormatKEY(0));
                    panel.setImage(layer, 0, infoMap.get(layer.getID(), KEYS.PATH.getFormatKEY(0)));

                    if(!infoMap.getBoolean(layer.getID(), KEYS.USE.getKEY())) break;
                    params[4] = infoMap.getInt(layer.getID(), KEYS.XPOS.getFormatKEY(1));
                    params[5] = infoMap.getInt(layer.getID(), KEYS.YPOS.getFormatKEY(1));
                    params[6] = infoMap.getInt(layer.getID(), KEYS.WIDTH.getFormatKEY(1));
                    params[7] = infoMap.getInt(layer.getID(), KEYS.HEIGHT.getFormatKEY(1));
                    panel.setImage(layer, 1, infoMap.get(layer.getID(), KEYS.PATH.getFormatKEY(1)));

                    params[8] = infoMap.getInt(layer.getID(), KEYS.TIMETO.getKEY());
                    params[9] = infoMap.getInt(layer.getID(), KEYS.TIMEBLINK.getKEY());
                break;

                case MOUTH:
                case KEYBOARD:
                    params = new int[8];

                    params[0] = infoMap.getInt(layer.getID(), KEYS.XPOS.getFormatKEY(0));
                    params[1] = infoMap.getInt(layer.getID(), KEYS.YPOS.getFormatKEY(0));
                    params[2] = infoMap.getInt(layer.getID(), KEYS.WIDTH.getFormatKEY(0));
                    params[3] = infoMap.getInt(layer.getID(), KEYS.HEIGHT.getFormatKEY(0));
                    panel.setImage(layer, 0, infoMap.get(layer.getID(), KEYS.PATH.getFormatKEY(0)));

                    if(!infoMap.getBoolean(layer.getID(), KEYS.USE.getKEY())) break;
                    params[4] = infoMap.getInt(layer.getID(), KEYS.XPOS.getFormatKEY(1));
                    params[5] = infoMap.getInt(layer.getID(), KEYS.YPOS.getFormatKEY(1));
                    params[6] = infoMap.getInt(layer.getID(), KEYS.WIDTH.getFormatKEY(1));
                    params[7] = infoMap.getInt(layer.getID(), KEYS.HEIGHT.getFormatKEY(1));
                    panel.setImage(layer, 1, infoMap.get(layer.getID(), KEYS.PATH.getFormatKEY(1)));
                break;

                case MOUSE:
                break;
            }
            panel.setParams(layer, params);
        }

        window.add(panel);
        DeltaTimeManager.getInstance().addObserver(panel);
    }

    private void startInputMethods() {
        if(infoMap.getBoolean(Sections.MOUTH.getKEY(), KEYS.USE.getKEY())) {
            micDetector = new Microphone(
                infoMap.getInt(Sections.MOUTH.getKEY(), KEYS.SENS.getKEY()),
                infoMap.getInt(Sections.MOUTH.getKEY(), KEYS.CHNLS.getKEY()),
                infoMap.getInt(Sections.MOUTH.getKEY(), KEYS.UPS.getKEY())
            );
            micDetector.addObserver(panel);
        }

        if(infoMap.getBoolean(Sections.KEYBOARD.getKEY(), KEYS.USE.getKEY())) {
            keyDetector = new Keyboard();
            keyDetector.addObserver(panel);
        }

        // Mouse mouse = null;
        // if(Boolean.parseBoolean(Constants.PROPERTIES.getProperty("mouse_detection"))) {
        //     mouse = new Mouse();
        //     mouse.addObserver(this);
        // }
    }

    /**
     * Revisa si una lista de teclas guardada actualmente coincide con
     * la secuencia de teclas para activar la decoración.
     *
     * @return Si la secuencia de teclas actualmente registrada
     *         coincide con la secuencia para activar la decoración de
     *         la ventana.
     */
    private boolean sequenceDecorateMatches() {
        if(keyQueue.size() != TARGET_SEQUENCE_DECORATE.length) return false;
        for(int i = 0; i < TARGET_SEQUENCE_DECORATE.length; i++) {
            if(keyQueue.get(i) != TARGET_SEQUENCE_DECORATE[i]) return false;
        }
        keyQueue.clear();
        return true;
    }

    /**
     * Revisa si una lista de teclas guardada actualmente coincide con
     * la secuencia de teclas para terminar la aplicación.
     *
     * @return Si la secuencia de teclas actualmente registrada
     *         coincide con la secuencia para terminar la aplicación.
     */
    private boolean sequenceExitMatches() {
        if(keyQueue.size() != TARGET_SEQUENCE_EXIT.length) return false;
        for(int i = 0; i < TARGET_SEQUENCE_EXIT.length; i++) {
            if(keyQueue.get(i) != TARGET_SEQUENCE_EXIT[i]) return false;
        }
        keyQueue.clear();
        return true;
    }

    /**
     * Método llamado cuando una tecla es presionada.
     * Sirva para detectar si una secuancia de teclas específica fue
     * pulsada.
     *
     * @param e El evento a procesar.
     */
    @Override
    public void keyPressed(KeyEvent e) {
        if(windowFocused) {
            int keyCode = e.getKeyCode();
            keyQueue.offer(keyCode);
            if(keyQueue.size() > 3) keyQueue.poll();
            if(sequenceExitMatches()) {
                try {
                    GlobalScreen.unregisterNativeHook();
                } catch(NativeHookException ex) {
                    ex.printStackTrace();
                }
                window.dispose();
                if(micDetector != null) micDetector.stopCapture();
                if(keyDetector != null) keyDetector.stopCapture();
                DeltaTimeManager.getInstance().removeObserver(panel);
                notifyObservers('Z', null);
            }
            if(sequenceDecorateMatches()) {
                window.dispose();
                if(undecorated) {
                    window.setBackground(null);
                    window.setUndecorated(undecorated = !undecorated);
                } else {
                    window.setUndecorated(undecorated = !undecorated);
                    window.setBackground(new Color(0, 0, 0, 0));
                }
                window.setVisible(true);
            }
        }
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void keyReleased(KeyEvent e) {}

    /**
     * {@inheritDoc}
     */
    @Override
    public void keyTyped(KeyEvent e) {}
}
