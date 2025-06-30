package app.vtuber;

import java.awt.*;
import java.awt.event.FocusEvent;
import java.awt.event.FocusListener;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.awt.geom.Path2D;
import java.util.LinkedList;

import javax.swing.*;

import com.github.kwhat.jnativehook.GlobalScreen;
import com.github.kwhat.jnativehook.NativeHookException;

import app.Constants;
import app.engine.DeltaTimeManager;
import app.engine.Observer;
import app.engine.readers.PropertiesM;
import app.engine.readers.TranslationM;

/**
 * Clase que representa la ventana principal de la aplicación.
 * Esta clase controla la interfaz gráfica de usuario y la interacción
 * con el teclado cuando la ventana tiene el foco.
 */
public class Window implements Observer, KeyListener {

    // Constantes de escalado para ajustar la ventana a diferentes resoluciones
    //private final double SCALING_FACTOR_X = (double) Integer.parseInt(PropertiesM.getProperty("window_width")) / 500;
    //private final double SCALING_FACTOR_Y = (double) Integer.parseInt(PropertiesM.getProperty("window_height")) / 400;
    
    /**
     * Panel personalizado para dibujar los elementos de la ventana.
     */
    private class MichiPanel extends JPanel {
        private Image[] images = new Image[Constants.ROUTES.length];
        private boolean drawHandKeyboard = true, drawMouth = false;
        private final Color PINK = new Color(0xf6dbd4);

        private final Point MOUSE = new Point(70, 285);
        private int currentMouseX = 70, currentMouseY = 285;

        private final Point START = new Point(140,180),
                            END = new Point(160,235),
                            HAND = new Point(85, 320);
        private int currentHandX = 85, currentHandY = 320;

        private final RenderingHints HINTS;

        public MichiPanel() {
            for(int i = 0; i < Constants.ROUTES.length; i++) {
                ImageIcon imageIcon = new ImageIcon(Constants.ROUTES[i]);
                images[i] = imageIcon.getImage();
            }
            setOpaque(false);
            HINTS = new RenderingHints(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_OFF);
            HINTS.put(RenderingHints.KEY_RENDERING, RenderingHints.VALUE_RENDER_SPEED);
            HINTS.put(RenderingHints.KEY_COLOR_RENDERING, RenderingHints.VALUE_COLOR_RENDER_QUALITY);
            HINTS.put(RenderingHints.KEY_INTERPOLATION, RenderingHints.VALUE_INTERPOLATION_NEAREST_NEIGHBOR);
            HINTS.put(RenderingHints.KEY_ALPHA_INTERPOLATION, RenderingHints.VALUE_ALPHA_INTERPOLATION_QUALITY);
        }

        /**
         * Sirve para indicar si la mano del lado del teclado debe ser
         * dibujada o no.
         *
         * @param drawHandKeyboard Si se debe dibujar la mano.
         */
        public void drawHandKeyboard(boolean drawHandKeyboard) {
            this.drawHandKeyboard = drawHandKeyboard;
        }

        /**
         * Sirve para indicar si la boca debe ser dibujada o no.
         *
         * @param drawHandKeyboard Si se debe dibujar la boca.
         */
        public void drawMouth(boolean drawMouth) {
            this.drawMouth = drawMouth;
        }

        /**
         * Sirve para indicar que tanto movimiento respecto al original
         * debe tener la mano del lado del ratón.
         *
         * @param x Cuanto desplazamiento en el eje x va a tener
         *          respecto a la posición original.
         * @param y Cuanto desplazamiento en el eje y va a tener
         *          respecto a la posición original.
         */
        public void moveMouse(byte x, byte y) {
            currentMouseX = MOUSE.x - x;
            currentMouseY = MOUSE.y - y;

            currentHandX = HAND.x - x;
            currentHandY = HAND.y - y;
        }

        /**
         * {@inheritDoc}
         */
        @Override
        protected void paintComponent(Graphics g) {
            Graphics2D g2d = (Graphics2D) g;
            g2d.setRenderingHints(HINTS);
            // if(SCALING_FACTOR_X != 1.0 || SCALING_FACTOR_Y != 1.0)
            //     g2d.scale(SCALING_FACTOR_X, SCALING_FACTOR_Y);

            super.paintComponent(g2d);
            for(int i = 0; i < images.length-2; i++) {
                Image image = images[i];
                switch(i) {
                    case 1: g2d.drawImage(
                                image, 0, 232,image.getWidth(null),
                                image.getHeight(null), this
                            );
                        break;
                    case 2: g2d.drawImage(
                            image, currentMouseX, currentMouseY,
                            image.getWidth(null), image.getHeight(null), this
                        );
                        break;
                    case 3:
                        Path2D path = new Path2D.Double();
                        path.moveTo(START.x, START.y);
                        path.curveTo(
                            currentHandX-30, currentHandY-15,
                            currentHandX+30, currentHandY-5, END.x, END.y
                        );
                        g2d.setColor(PINK);
                        g2d.fill(path);
                        g2d.setColor(Color.BLACK);
                        g2d.draw(path);
                        break;
                    case 4: if(this.drawHandKeyboard) g2d.drawImage(
                            image, 327, 154,
                            image.getWidth(null), image.getHeight(null), this
                        );
                        break;
                    case 5: if(this.drawMouth) g2d.drawImage(
                            image, 230, 180,
                            image.getWidth(null), image.getHeight(null), this
                        );
                        break;
                    case 7: g2d.drawImage(
                            image, 194, 226,
                            image.getWidth(null), image.getHeight(null), this
                        );
                        break;
                    default: g2d.drawImage(
                            image, 0, 0,
                            image.getWidth(null), image.getHeight(null), this
                        );
                        break;
                }
            }
            int i = (blink)? 9: 8;
            g2d.drawImage(
                images[i], 207, 164,
                images[i].getWidth(null), images[i].getHeight(null), this
            );
        }
    }

    private JFrame window;
    private MichiPanel panel;
    private boolean undecorated = true, windowFocused = false, blink = false;
    private float time = 0f;

    // private final int repaintX = (int) (15 * SCALING_FACTOR_X);
    // private final int repaintY = (int) (154 * SCALING_FACTOR_Y);
    // private final int repaintW = (int)(452 * SCALING_FACTOR_X);
    // private final int repaintH = (int) (325 * SCALING_FACTOR_Y);

    private final int[] TARGET_SEQUENCE_EXIT = {
        KeyEvent.VK_CONTROL, KeyEvent.VK_SHIFT, KeyEvent.VK_C
    };
    private final int[] TARGET_SEQUENCE_DECORATE = {
        KeyEvent.VK_CONTROL, KeyEvent.VK_SHIFT, KeyEvent.VK_D
    };
    private final LinkedList<Integer> keyQueue = new LinkedList<>();

    /**
     * Constructor de la clase Window.
     * Configura y muestra la ventana principal.
     */
    public Window() {
        window = new JFrame();
        // window.setSize(
        //     Integer.parseInt(PropertiesM.getProperty("window_width")),
        //     Integer.parseInt(PropertiesM.getProperty("window_height"))
        // );
        window.setResizable(false);
        window.setUndecorated(undecorated);
        window.getContentPane().setBackground(Color.GREEN);
        window.setBackground(new Color(0, 0, 0, 0));
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

        window.addWindowListener(new WindowAdapter() {
            @Override
            public void windowClosing(WindowEvent e) {
                try {
                    GlobalScreen.unregisterNativeHook();
                } catch (NativeHookException ex) {
                    ex.printStackTrace();
                }
                window.dispose();
                System.exit(0);
            }
        });

        panel = new MichiPanel();
        window.add(panel);

        // int taskbarHeight = Integer.parseInt(
        //     PropertiesM.getProperty("windows_taskbar_height")
        // );
        // String location = PropertiesM.getProperty("window_location");
        // switch (location.toLowerCase()) {
        //     case "nw":
        //         window.setLocation(0, 0);
        //         break;
        //     case "ne":
        //         window.setLocation(
        //             Constants.FULLSCREEN_WIDTH - window.getWidth(), 0
        //         );
        //         break;
        //     case "sw":
        //         window.setLocation(
        //             0,
        //             Constants.FULLSCREEN_HEIGHT - window.getHeight() - taskbarHeight
        //         );
        //         break;
        //     case "se":
        //         window.setLocation(
        //             Constants.FULLSCREEN_WIDTH - window.getWidth(),
        //             Constants.FULLSCREEN_HEIGHT - window.getHeight() - taskbarHeight
        //         );
        //         break;
        //     default:
        //         window.setLocation(
        //             Constants.FULLSCREEN_WIDTH - window.getWidth(),
        //             Constants.FULLSCREEN_HEIGHT - window.getHeight() - taskbarHeight
        //         );
        //         break;
        // }

        window.setVisible(true);
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
                } catch (NativeHookException ex) {
                    ex.printStackTrace();
                }
                window.dispose();
                System.exit(0);
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

    /**
     * {@inheritDoc}
     */
    @Override
    public void update(char event, Object data) {
        switch(event) {
            case 'm': panel.drawMouth((boolean) data);
                break;
            case 'k': panel.drawHandKeyboard(!(boolean) data);
                break;
            case 'r':
                byte[] pair = (byte[])data;
                panel.moveMouse(pair[0], pair[1]);
                break;
            case 'u':
                if(time > 4) blink = true;
                time += DeltaTimeManager.getInstance().getDeltaTime();
                //panel.repaint(repaintX, repaintY, repaintW, repaintH);
                if(time > 4.3) {
                    blink =false;
                    time = 0;
                }
            default: return;
        }
    }
}
