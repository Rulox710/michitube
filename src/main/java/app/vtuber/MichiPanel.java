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

import app.Constants;
import app.Ids;
import app.Sections.KEYS;
import app.engine.DeltaTimeManager;
import app.engine.Observer;
import app.fileUtils.ImageConverter;
import app.files.PropertiesM;

import java.awt.Color;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Image;
import java.awt.Point;
import java.awt.RenderingHints;
import java.awt.geom.Path2D;
import java.awt.image.BufferedImage;
import java.net.MalformedURLException;
import java.net.URI;
import java.nio.file.Path;
import java.nio.file.Paths;
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
                            LAYER_KEYBOARD = new Image[3],
                            LAYER_MOUSE = new Image[0],
                            LAYER_EXTRA = new Image[1],
                            LAYER_HAIR = new Image[1];
    private final int LAYERS_SIZE = LAYER_BACKGROUND.length + LAYER_BODY.length + LAYER_EYES.length +
                                    LAYER_MOUTH.length + LAYER_TABLE.length + LAYER_KEYBOARD.length +
                                    LAYER_MOUSE.length + LAYER_EXTRA.length + LAYER_HAIR.length;
    private final Map<Ids, Image[]> LAYERS_MAP =  new HashMap<>(Ids.values().length);
    private final Map<Ids, Map<KEYS, Integer>> PARAMS = new HashMap<>();

    private final ImageConverter CONVERTER = new ImageConverter();

    private final RenderingHints HINTS;

    private boolean drawPrimaryHand = true, drawPrimaryMouth = true, drawPrimaryEyes = true;
    private float time = 0f;
    private Path basePath;
    private Color handColor = Color.LIGHT_GRAY;

    private final Point SPACE_AREA = new Point();
    private Point handPoint1 = new Point(), handPoint2 = new Point();

    public MichiPanel() {
        basePath = Paths.get(PropertiesM.getAppProperty("default_dir"));

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
        LAYERS_MAP.put(Ids.HAIR, LAYER_HAIR);
        LAYERS_MAP.put(Ids.KEYBOARD, LAYER_KEYBOARD);
        LAYERS_MAP.put(Ids.MOUSE, LAYER_MOUSE);
        LAYERS_MAP.put(Ids.EXTRA, LAYER_EXTRA);
    }

    public void setHandColor(Color color) {
        handColor = color;
    }

    public void setImage(Ids layer, int tweak, String rle) {
        // try {
        //     Path relativePath = Paths.get(imagePath);
        //     Path fullPath = basePath.resolve(relativePath);
        //     URI fullUri = fullPath.toUri();
        //     ImageIcon imageIcon = new ImageIcon(fullUri.toURL());
        //     LAYERS_MAP.get(layer)[tweak] = imageIcon.getImage();

        //     System.out.println(String.format(
        //         "ID: %s Tweak: %d uri: %s",
        //         layer.getID(), tweak, fullPath.toUri().toString()
        //     ));
        // } catch(MalformedURLException e) {
        CONVERTER.setRLE(rle, false);
        BufferedImage bImage = CONVERTER.convertRLEtoImage();
        if(bImage != null) {
            ImageIcon imageIcon = new ImageIcon(bImage);
            LAYERS_MAP.get(layer)[tweak] = imageIcon.getImage();
        } else {
            Constants.printTimeStamp(System.err);
            System.err.println(String.format(
                "No se ha encontrado la imagen de la capa: %s, característica: %d",
                layer, tweak
            ));
            //e.printStackTrace();
        }
    }

    public void setParams(Ids layer, int... params) {
        Map<KEYS, Integer> map = new HashMap<>(4);
        KEYS[] infoKeys = new KEYS[]
            {KEYS.XPOS_0, KEYS.YPOS_0, KEYS.WIDTH_0, KEYS.HEIGHT_0};

        switch(layer) {
            case BACKGROUND:
            case BODY:
            case TABLE:
            case EXTRA:
            case HAIR:
                for(int i = 0; i < infoKeys.length; i++)
                    map.put(infoKeys[i], params[i]);
            break;

            case EYES:
                map = new HashMap<>(10);

                for(int i = 0; i < infoKeys.length; i++)
                    map.put(infoKeys[i], params[i]);

                if(params.length < 5) break;
                infoKeys = new KEYS[]
                    {KEYS.XPOS_1, KEYS.YPOS_1, KEYS.WIDTH_1, KEYS.HEIGHT_1};
                for(int i = 0; i < infoKeys.length; i++)
                    map.put(infoKeys[i], params[i+infoKeys.length]);

                map.put(KEYS.TIMETO, params[8]);
                map.put(KEYS.TIMEBLINK, params[9]);
            break;

            case MOUTH:
                map = new HashMap<>(8);

                for(int i = 0; i < infoKeys.length; i++)
                    map.put(infoKeys[i], params[i]);

                infoKeys = new KEYS[]
                    {KEYS.XPOS_1, KEYS.YPOS_1, KEYS.WIDTH_1, KEYS.HEIGHT_1};
                for(int i = 0; i < infoKeys.length; i++)
                    map.put(infoKeys[i], params[i+infoKeys.length]);
            break;

            case KEYBOARD:
                map = new HashMap<>(12);

                for(int i = 0; i < infoKeys.length; i++)
                    map.put(infoKeys[i], params[i]);

                infoKeys = new KEYS[]
                    {KEYS.XPOS_2, KEYS.YPOS_2, KEYS.WIDTH_2, KEYS.HEIGHT_2};
                for(int i = 0; i < infoKeys.length; i++)
                    map.put(infoKeys[i], params[i+infoKeys.length]);

                infoKeys = new KEYS[]
                    {KEYS.XPOS_1, KEYS.YPOS_1, KEYS.WIDTH_1, KEYS.HEIGHT_1};
                for(int i = 0; i < infoKeys.length; i++)
                    map.put(infoKeys[i], params[i+infoKeys.length*2]);
            break;

            case MOUSE:
                map = new HashMap<>(12);

                infoKeys = new KEYS[] {
                    KEYS.XPOS, KEYS.YPOS, KEYS.WIDTH, KEYS.HEIGHT,
                    KEYS.XPOS_A, KEYS.YPOS_A, KEYS.XPOS_B, KEYS.YPOS_B,
                    KEYS.XPOS_C, KEYS.YPOS_C, KEYS.XPOS_D, KEYS.YPOS_D
                };
                for(int i = 0; i < infoKeys.length; i++)
                    map.put(infoKeys[i], params[i]);

                SPACE_AREA.setLocation(
                    map.get(KEYS.WIDTH),
                    map.get(KEYS.HEIGHT)
                );

                handPoint1.setLocation(
                    map.get(KEYS.XPOS) + map.get(KEYS.XPOS_B),
                    map.get(KEYS.YPOS) + map.get(KEYS.YPOS_B)
                );
                handPoint2.setLocation(
                    map.get(KEYS.XPOS) + map.get(KEYS.XPOS_C),
                    map.get(KEYS.YPOS) + map.get(KEYS.YPOS_C)
                );

            break;
        }
        PARAMS.put(layer, map);
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
    public void moveMouse(double x, double y) {
        double xPosition = PARAMS.get(Ids.MOUSE).get(KEYS.XPOS) + SPACE_AREA.getX() * (1-x);
        double yPosition = PARAMS.get(Ids.MOUSE).get(KEYS.YPOS) + SPACE_AREA.getY() * (1-y);

        double offsetX = (PARAMS.get(Ids.MOUSE).get(KEYS.XPOS_C) - PARAMS.get(Ids.MOUSE).get(KEYS.XPOS_B)) / 2;
        double offsetY = (PARAMS.get(Ids.MOUSE).get(KEYS.YPOS_C) - PARAMS.get(Ids.MOUSE).get(KEYS.YPOS_B)) / 2;

        handPoint1.setLocation(
            xPosition - offsetX, yPosition - offsetY
        );
        handPoint2.setLocation(
            xPosition + offsetX, yPosition + offsetY
        );
    }

    /**
     * {@inheritDoc}
     */
    @Override
    protected void paintComponent(Graphics g) {
        Graphics2D g2d = (Graphics2D) g;
        g2d.setRenderingHints(HINTS);

        super.paintComponent(g2d);

         for(Ids layer: Ids.values()) {
        // Ids ids[] = new Ids[]
        //     {Ids.BACKGROUND, Ids.BODY, Ids.TABLE, Ids.KEYBOARD, Ids.HAIR, Ids.EYES, Ids.MOUTH, Ids.EXTRA};
        // for(Ids layer: ids) {

            if(layer == Ids.MOUSE) {
                g2d.setColor(Color.blue);
                g2d.drawRect(
                    PARAMS.get(Ids.MOUSE).get(KEYS.XPOS),
                    PARAMS.get(Ids.MOUSE).get(KEYS.YPOS),
                    PARAMS.get(Ids.MOUSE).get(KEYS.WIDTH),
                    PARAMS.get(Ids.MOUSE).get(KEYS.HEIGHT)
                );

                Path2D path = new Path2D.Double();
                int xOffset = PARAMS.get(Ids.MOUSE).get(KEYS.XPOS) - 5;
                int yOffset = PARAMS.get(Ids.MOUSE).get(KEYS.YPOS) - 5;

                path.moveTo(
                    xOffset + PARAMS.get(Ids.MOUSE).get(KEYS.XPOS_A),
                    yOffset + PARAMS.get(Ids.MOUSE).get(KEYS.YPOS_A)
                );
                path.lineTo(handPoint1.x, handPoint1.y);
                path.curveTo(
                    handPoint1.x-10, handPoint1.y+10,
                    handPoint2.x-10, handPoint2.y+10,
                    handPoint2.x, handPoint2.y
                );
                path.lineTo(
                    xOffset + PARAMS.get(Ids.MOUSE).get(KEYS.XPOS_D),
                    yOffset + PARAMS.get(Ids.MOUSE).get(KEYS.YPOS_D)
                );

                g2d.setColor(handColor);
                g2d.fill(path);
                g2d.setColor(Color.BLACK);
                g2d.draw(path);

                continue;
            }

            Image image = LAYERS_MAP.get(layer)[0];
            int tweak = 0;

            switch(layer) {
                case BACKGROUND:
                case BODY:
                case TABLE:
                case EXTRA:
                case HAIR:

                break;

                case EYES:
                    if(!drawPrimaryEyes)
                        image = LAYERS_MAP.get(layer)[tweak = 1];

                break;

                case MOUTH:
                    if(!drawPrimaryMouth)
                        image = LAYERS_MAP.get(layer)[tweak = 1];

                break;

                case KEYBOARD:
                    g2d.drawImage(
                        LAYERS_MAP.get(layer)[2],
                        PARAMS.get(layer).get(KEYS.XPOS_2),
                        PARAMS.get(layer).get(KEYS.YPOS_2),
                        PARAMS.get(layer).get(KEYS.WIDTH_2),
                        PARAMS.get(layer).get(KEYS.HEIGHT_2),
                        this
                    );

                    if(!drawPrimaryHand)
                        image = LAYERS_MAP.get(layer)[tweak = 1];

                break;

                default:
                break;
            }
            KEYS[] infoKeys = switch(tweak) {
                case 0 -> new KEYS[]
                    {KEYS.XPOS_0, KEYS.YPOS_0, KEYS.WIDTH_0, KEYS.HEIGHT_0};
                case 1 -> new KEYS[]
                    {KEYS.XPOS_1, KEYS.YPOS_1, KEYS.WIDTH_1, KEYS.HEIGHT_1};
                case 2 -> new KEYS[]
                    {KEYS.XPOS_2, KEYS.YPOS_2, KEYS.WIDTH_2, KEYS.HEIGHT_2};
                default -> new KEYS[]
                    {KEYS.XPOS, KEYS.YPOS, KEYS.WIDTH, KEYS.HEIGHT};
            };
            g2d.drawImage(
                image,
                PARAMS.get(layer).get(infoKeys[0]),
                PARAMS.get(layer).get(infoKeys[1]),
                PARAMS.get(layer).get(infoKeys[2]),
                PARAMS.get(layer).get(infoKeys[3]),
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
                double[] pair = (double[])data;
                moveMouse(pair[0], pair[1]);
            break;

            case 'u':
                int toBlink = PARAMS.get(Ids.EYES).get(KEYS.TIMETO);
                if(drawPrimaryEyes && toBlink != 0 && time >= toBlink) {
                    drawPrimaryEyes = false;
                    time = 0f;
                } else if(!drawPrimaryEyes && time >= PARAMS.get(Ids.EYES).get(KEYS.TIMEBLINK)) {
                    drawPrimaryEyes = true;
                    time = 0f;
                }
                time += DeltaTimeManager.getInstance().getDeltaTime();

                repaint();
            break;
        }
    }
}
