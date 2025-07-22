package app.vtuber;

import app.Ids;
import app.Sections;
import app.Sections.KEYS;
import app.engine.DeltaTimeManager;
import app.engine.Observer;

import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Image;
import java.awt.RenderingHints;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.HashMap;
import java.util.Map;

import javax.swing.ImageIcon;
import javax.swing.JPanel;

/**
 * Panel personalizado para dibujar los elementos de la ventana.
 */
public class MichiPanel extends JPanel implements Observer {

    private final Image[] LAYER_BACKGROUND = new Image[1],
                              LAYER_BODY = new Image[1],
                              LAYER_EYES = new Image[2],
                              LAYER_MOUTH = new Image[2],
                              LAYER_TABLE = new Image[1],
                              LAYER_KEYBOARD = new Image[2],
                              LAYER_MOUSE = new Image[2];
    private final int LAYERS_SIZE = LAYER_BACKGROUND.length + LAYER_BODY.length + LAYER_EYES.length +
                                    LAYER_MOUTH.length + LAYER_TABLE.length + LAYER_KEYBOARD.length +
                                    LAYER_MOUSE.length;
    private final Map<Ids, Image[]> LAYERS_MAP =  new HashMap<>(Ids.values().length);
    private final Map<Ids, Map<String, Integer>> PARAMS = new HashMap<>();

    private final RenderingHints HINTS;

    private boolean drawPrimaryHand = true, drawPrimaryMouth = true, drawPrimaryEyes = true;
    private float time = 0f;

    public MichiPanel() {
        setOpaque(false);
        HINTS = new RenderingHints(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_OFF);
        HINTS.put(RenderingHints.KEY_RENDERING, RenderingHints.VALUE_RENDER_QUALITY);
        HINTS.put(RenderingHints.KEY_COLOR_RENDERING, RenderingHints.VALUE_COLOR_RENDER_QUALITY);
        HINTS.put(RenderingHints.KEY_INTERPOLATION, RenderingHints.VALUE_INTERPOLATION_BILINEAR);
        HINTS.put(RenderingHints.KEY_ALPHA_INTERPOLATION, RenderingHints.VALUE_ALPHA_INTERPOLATION_QUALITY);

        LAYERS_MAP.put(Ids.BACKGROUND, LAYER_BACKGROUND);
        LAYERS_MAP.put(Ids.BODY, LAYER_BODY);
        LAYERS_MAP.put(Ids.EYES, LAYER_EYES);
        LAYERS_MAP.put(Ids.MOUTH, LAYER_MOUTH);
        LAYERS_MAP.put(Ids.TABLE, LAYER_TABLE);
        LAYERS_MAP.put(Ids.KEYBOARD, LAYER_KEYBOARD);
        LAYERS_MAP.put(Ids.MOUSE, LAYER_MOUSE);
    }

    public void setImage(Ids layer, int tweak, String imageUri) {
        try {
            ImageIcon imageIcon = new ImageIcon(new URL(imageUri));
            LAYERS_MAP.get(layer)[tweak] = imageIcon.getImage();
        } catch(MalformedURLException e) {
            System.err.println(String.format(
                "No se ha encontrado la imagen de la capa: %s, característica: %d",
                layer, tweak
            ));
            e.printStackTrace();
        }
    }

    public void setParams(Ids layer, int... params) {
        Map<String, Integer> map = new HashMap<>(4);
        switch(layer) {
            case BACKGROUND:
            case BODY:
            case TABLE:
                map.put(KEYS.XPOS.getFormatKEY(0), params[0]);
                map.put(KEYS.YPOS.getFormatKEY(0), params[1]);
                map.put(KEYS.WIDTH.getFormatKEY(0), params[2]);
                map.put(KEYS.HEIGHT.getFormatKEY(0), params[3]);
            break;

            case EYES:
                map = new HashMap<>(10);

                map.put(KEYS.XPOS.getFormatKEY(0), params[0]);
                map.put(KEYS.YPOS.getFormatKEY(0), params[1]);
                map.put(KEYS.WIDTH.getFormatKEY(0), params[2]);
                map.put(KEYS.HEIGHT.getFormatKEY(0), params[3]);

                if(params.length < 5) break;
                map.put(KEYS.XPOS.getFormatKEY(1), params[4]);
                map.put(KEYS.YPOS.getFormatKEY(1), params[5]);
                map.put(KEYS.WIDTH.getFormatKEY(1), params[6]);
                map.put(KEYS.HEIGHT.getFormatKEY(1), params[7]);

                map.put(KEYS.TIMETO.getKEY(), params[8]);
                map.put(KEYS.TIMEBLINK.getKEY(), params[9]);
            break;

            case MOUTH:
            case KEYBOARD:
                map = new HashMap<>(8);

                map.put(KEYS.XPOS.getFormatKEY(0), params[0]);
                map.put(KEYS.YPOS.getFormatKEY(0), params[1]);
                map.put(KEYS.WIDTH.getFormatKEY(0), params[2]);
                map.put(KEYS.HEIGHT.getFormatKEY(0), params[3]);

                if(params.length < 5) break;
                map.put(KEYS.XPOS.getFormatKEY(1), params[4]);
                map.put(KEYS.YPOS.getFormatKEY(1), params[5]);
                map.put(KEYS.WIDTH.getFormatKEY(1), params[6]);
                map.put(KEYS.HEIGHT.getFormatKEY(1), params[7]);
            break;

            case MOUSE:
            break;
        }
        PARAMS.put(layer, map);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    protected void paintComponent(Graphics g) {
        Graphics2D g2d = (Graphics2D) g;
        g2d.setRenderingHints(HINTS);

        super.paintComponent(g2d);

        // for(Ids layer: Ids.values()) {
        Ids ids[] = {Ids.BACKGROUND, Ids.BODY, Ids.TABLE, Ids.EYES, Ids.MOUTH, Ids.KEYBOARD};
        for(Ids layer: ids) {
            Image image = LAYERS_MAP.get(layer)[0];
            int tweak = 0;
            switch(layer) {
                case BACKGROUND:
                case BODY:
                case TABLE:

                break;

                case EYES:
                    if(!drawPrimaryEyes) {
                        image = LAYERS_MAP.get(layer)[1];
                        tweak = 1;
                    }

                break;

                case MOUTH:
                    if(!drawPrimaryMouth) {
                        image = LAYERS_MAP.get(layer)[1];
                        tweak = 1;
                    }

                break;

                case KEYBOARD:
                    if(!drawPrimaryHand) {
                        image = LAYERS_MAP.get(layer)[1];
                        tweak = 1;
                    }

                break;

                case MOUSE:
                break;
            }
            g2d.drawImage(
                image,
                PARAMS.get(layer).get(KEYS.XPOS.getFormatKEY(tweak)),
                PARAMS.get(layer).get(KEYS.YPOS.getFormatKEY(tweak)),
                PARAMS.get(layer).get(KEYS.WIDTH.getFormatKEY(tweak)),
                PARAMS.get(layer).get(KEYS.HEIGHT.getFormatKEY(tweak)),
                this
            );
        }
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void update(char event, Object data) {
        switch(event) {
            case 'm': drawPrimaryMouth = !(boolean) data;
            break;

            case 'k':
                drawPrimaryHand = !(boolean) data;
            break;

            case 'r':
            break;

            case 'u':
                if(drawPrimaryEyes && time >= PARAMS.get(Ids.EYES).get(KEYS.TIMETO.getKEY())) {
                    drawPrimaryEyes = false;
                    time = 0f;
                } else if(!drawPrimaryEyes && time >= PARAMS.get(Ids.EYES).get(KEYS.TIMEBLINK.getKEY())) {
                    drawPrimaryEyes = true;
                    time = 0f;
                }
                time += DeltaTimeManager.getInstance().getDeltaTime();

                repaint();
            break;
        }
    }
}
