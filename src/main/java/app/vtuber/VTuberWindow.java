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

package app.vtuber;

import app.Ids;
import app.Sections;
import app.Sections.KEYS;
import app.detectors.Keyboard;
import app.detectors.Microphone;
import app.detectors.Mouse;
import app.engine.DeltaTimeManager;
import app.engine.Observable;
import app.files.TranslationM;
import app.files.VTuberReader;

import com.github.kwhat.jnativehook.GlobalScreen;
import com.github.kwhat.jnativehook.NativeHookException;

import java.awt.Color;
import java.awt.Image;
import java.awt.Toolkit;
import java.awt.event.FocusEvent;
import java.awt.event.FocusListener;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
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
    private Mouse mouseDetector;

    private final int[] TARGET_SEQUENCE_EXIT = {
        KeyEvent.VK_CONTROL, KeyEvent.VK_SHIFT, KeyEvent.VK_C
    };
    private final int[] TARGET_SEQUENCE_DECORATE = {
        KeyEvent.VK_CONTROL, KeyEvent.VK_SHIFT, KeyEvent.VK_D
    };
    private final LinkedList<Integer> keyQueue = new LinkedList<>();

    private int[] getColor(String hexString) {
        int r = Integer.valueOf(hexString.substring(1, 3), 16);
        int g = Integer.valueOf(hexString.substring(3, 5), 16);
        int b = Integer.valueOf(hexString.substring(5, 7), 16);
        int a = Integer.valueOf(hexString.substring(7, 9), 16);

        return new int[] {r, g, b, a};
    }

    /**
     * Configura y muestra la ventana principal.
     */
    public void setMap(VTuberReader infoMap) {
        this.infoMap = infoMap;

        window = new JFrame();
        Image icono = Toolkit.getDefaultToolkit().getImage(
            getClass().getResource("/assets/icon.png")
        );
        window.setIconImage(icono);

        window.setSize(
            infoMap.getInt(Sections.SHEET, KEYS.WIDTH),
            infoMap.getInt(Sections.SHEET, KEYS.HEIGHT)
        );
        window.setResizable(false);
        window.setUndecorated(undecorated);

        if(infoMap.getBoolean(Sections.BACKGROUND, KEYS.USECOLOR)) {
            int[] color = getColor(infoMap.get(Sections.BACKGROUND, KEYS.COLOR));
            Color backgroundColor = new Color(color[0], color[1], color[2], color[3]);
            window.getContentPane().setBackground(backgroundColor);
        } else {
            window.setUndecorated(undecorated = !undecorated);
            window.setBackground(new Color(0, 0, 0, 0));
            window.getContentPane().setBackground(Color.GREEN);
        }

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
                if(mouseDetector != null) mouseDetector.stopCapture();
                micDetector = null;
                keyDetector = null;
                mouseDetector = null;
                DeltaTimeManager.getInstance().removeObserver(panel);
                notifyObservers('z', null);
            }
        });

        window.setVisible(true);
    }

    private void startMichiPane() {
        panel = new MichiPanel();
        int[] params = new int[4];

        for(Ids id: Ids.values()) {
            Sections section = id.getEquivalent();
            KEYS[] infoKeys = new KEYS[]
                {KEYS.XPOS_0, KEYS.YPOS_0, KEYS.WIDTH_0, KEYS.HEIGHT_0, KEYS.PATH_0};

            switch(id) {
                case BACKGROUND:
                    if(!infoMap.getBoolean(section, KEYS.IMAGE)) break;
                    for(int i = 0; i < 4; i++)
                        params[i] = infoMap.getInt(section, infoKeys[i]);
                    panel.setImage(id, 0, infoMap.get(section, infoKeys[4]));
                break;

                case TABLE:
                case EXTRA:
                case HAIR:
                    if(!infoMap.getBoolean(section, KEYS.USE)) break;
                    for(int i = 0; i < 4; i++)
                        params[i] = infoMap.getInt(section, infoKeys[i]);
                    panel.setImage(id, 0, infoMap.get(section, infoKeys[4]));
                break;

                case BODY:
                    for(int i = 0; i < 4; i++)
                        params[i] = infoMap.getInt(section, infoKeys[i]);
                    panel.setImage(id, 0, infoMap.get(section, infoKeys[4]));
                break;

                case EYES:
                    params = new int[10];

                    for(int i = 0; i < 4; i++)
                        params[i] = infoMap.getInt(section, infoKeys[i]);
                    panel.setImage(id, 0, infoMap.get(section, infoKeys[4]));

                    infoKeys = new KEYS[]
                        {KEYS.XPOS_1, KEYS.YPOS_1, KEYS.WIDTH_1, KEYS.HEIGHT_1, KEYS.PATH_1};

                    if(!infoMap.getBoolean(section, KEYS.USE)) break;
                    for(int i = 0; i < 4; i++)
                        params[i+(infoKeys.length-1)] = infoMap.getInt(section, infoKeys[i]);
                    panel.setImage(id, 1, infoMap.get(section, infoKeys[4]));

                    params[8] = infoMap.getInt(section, KEYS.TIMETO);
                    params[9] = infoMap.getInt(section, KEYS.TIMEBLINK);
                break;

                case MOUTH:
                    params = new int[8];

                    for(int i = 0; i < 4; i++)
                        params[i] = infoMap.getInt(section, infoKeys[i]);
                    panel.setImage(id, 0, infoMap.get(section, infoKeys[4]));

                    infoKeys = new KEYS[]
                        {KEYS.XPOS_1, KEYS.YPOS_1, KEYS.WIDTH_1, KEYS.HEIGHT_1, KEYS.PATH_1};

                    if(!infoMap.getBoolean(section, KEYS.USE)) break;
                    for(int i = 0; i < 4; i++)
                        params[i+(infoKeys.length-1)] = infoMap.getInt(section, infoKeys[i]);
                    panel.setImage(id, 1, infoMap.get(section, infoKeys[4]));
                break;

                case KEYBOARD:
                    params = new int[12];

                    for(int i = 0; i < 4; i++)
                        params[i] = infoMap.getInt(section, infoKeys[i]);
                    panel.setImage(id, 0, infoMap.get(section, infoKeys[4]));

                    infoKeys = new KEYS[]
                        {KEYS.XPOS_2, KEYS.YPOS_2, KEYS.WIDTH_2, KEYS.HEIGHT_2, KEYS.PATH_2};

                    for(int i = 0; i < 4; i++)
                        params[i+infoKeys.length-1] = infoMap.getInt(section, infoKeys[i]);
                    panel.setImage(id, 2, infoMap.get(section, infoKeys[4]));

                    infoKeys = new KEYS[]
                        {KEYS.XPOS_1, KEYS.YPOS_1, KEYS.WIDTH_1, KEYS.HEIGHT_1, KEYS.PATH_1};

                    if(!infoMap.getBoolean(section, KEYS.USE)) break;
                    for(int i = 0; i < 4; i++)
                        params[i+(infoKeys.length-1)*2] = infoMap.getInt(section, infoKeys[i]);
                    panel.setImage(id, 1, infoMap.get(section, infoKeys[4]));
                break;

                case MOUSE:
                    params = new int[12];

                    infoKeys = new KEYS[] {
                        KEYS.XPOS, KEYS.YPOS, KEYS.WIDTH, KEYS.HEIGHT,
                        KEYS.XPOS_A, KEYS.YPOS_A, KEYS.XPOS_B, KEYS.YPOS_B,
                        KEYS.XPOS_C, KEYS.YPOS_C, KEYS.XPOS_D, KEYS.YPOS_D
                    };
                    for(int i = 0; i < infoKeys.length; i++)
                        params[i] = infoMap.getInt(section, infoKeys[i]);

                    int[] color = getColor(infoMap.get(section, KEYS.COLOR));
                    Color handColor = new Color(
                        color[0], color[1], color[2], color[3]
                    );
                    panel.setHandColor(handColor);
                break;
            }
            panel.setParams(id, params);
        }

        window.add(panel);
        DeltaTimeManager.getInstance().addObserver(panel);
    }

    private void startInputMethods() {
        if(infoMap.getBoolean(Sections.MOUTH, KEYS.USE)) {
            micDetector = new Microphone(
                infoMap.getInt(Sections.MOUTH, KEYS.SENS),
                infoMap.getInt(Sections.MOUTH, KEYS.CHNLS),
                infoMap.getInt(Sections.MOUTH, KEYS.UPS)
            );
            micDetector.addObserver(panel);
        }

        if(infoMap.getBoolean(Sections.KEYBOARD, KEYS.USE)) {
            keyDetector = new Keyboard();
            keyDetector.addObserver(panel);
        }

        if(infoMap.getBoolean(Sections.MOUSE, KEYS.USE)) {
            mouseDetector = new Mouse();
            mouseDetector.addObserver(panel);
        }
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
                if(mouseDetector != null) mouseDetector.stopCapture();
                micDetector = null;
                keyDetector = null;
                mouseDetector = null;
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
