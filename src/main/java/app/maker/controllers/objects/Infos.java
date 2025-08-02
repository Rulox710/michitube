package app.maker.controllers.objects;

import java.util.HashMap;
import java.util.Map;

import app.Sections.KEYS;

/**
 * Clase que contiene disitintos objetos que contienen información
 * sobre disitintos elemeentos de la aplicación.
 */
public final class Infos {

    /**
     * Clase abstracta que define la estructura de un objeto con
     * información relevante y debe ser extendido por las clases
     * que representan elementos específicos de la aplicación.
     */
    public static abstract class Info {

        protected Map<KEYS, Integer> intParams;
        protected Map<KEYS, String> strParams;
        protected Map<KEYS, Boolean> boolParams;

        public int getInt(KEYS key) {
            return intParams.getOrDefault(key, -1);
        }

        public String getString(KEYS key) {
            return strParams.getOrDefault(key, "");
        }

        public boolean getBoolean(KEYS key) {
            return boolParams.getOrDefault(key, false);
        }
    }

    /**
     * Clase que representa la información de una imagen. Es usada en
     * {@link app.maker.controllers.components.DraggableResizableImageView}
     *
     * @see app.maker.controllers.objects.builders.ImageInfoBuilder
     */
    public static class ImageInfo extends Info {

        /**
         * Constructor de la clase que inicializa los parámetros
         * necesarios para la imagen.
         *
         * @param x Posición X de la imagen.
         * @param y Posición Y de la imagen.
         * @param width Ancho de la imagen.
         * @param height Alto de la imagen.
         * @param path Ruta del archivo de la imagen.
         */
        public ImageInfo(int x, int y, int width, int height, String path) {
            intParams = new HashMap<>(4);
            intParams.put(KEYS.XPOS, x);
            intParams.put(KEYS.YPOS, y);
            intParams.put(KEYS.WIDTH, width);
            intParams.put(KEYS.HEIGHT, height);

            strParams = new HashMap<>(1);
            strParams.put(KEYS.PATH, path);
        }
    }

    /**
     * Clase que representa la información de una imagen. Es usada en
     * {@link app.maker.controllers.SheetController}
     *
     * @see app.maker.controllers.objects.builders.SheetInfoBuilder
     */
    public static class SheetInfo extends Info {

        /**
         * Constructor de la clase que inicializa los parámetros
         * necesarios para la hoja.
         *
         * @param width Ancho de la hoja.
         * @param height Alto de la hoja.
         */
        public SheetInfo(int width, int height) {
            intParams = new HashMap<>(2);
            intParams.put(KEYS.WIDTH, width);
            intParams.put(KEYS.HEIGHT, height);
        }
    }

    /**
     * Clase que representa la información de un elemento básico
     * de la aplicación, como un botón o un campo de texto. Es usada
     * en varios de los componentes que extienden a
     * {@link app.maker.controllers.layerOptions.OptionLayerController}
     *
     * @see app.maker.controllers.objects.builders.BasicInfoBuilder
     */
    public static class BasicInfo extends Info {

        /**
         * Constructor de la clase que inicializa los parámetros
         * necesarios para el elemento básico.
         *
         * @param pathOff Ruta del archivo de la imagen por defecto.
         * @param pathOn Ruta del archivo de una imagen adicional.
         * @param usage Indica si el archivo y configuraciones de la
         *              segunda imagen están en uso.
         * @param pathOn Ruta del archivo de la imagen cuando se
         *               realiza el cambio temporal.
         */
        public BasicInfo(
                String pathOff, String pathExtra,
                boolean usage, String pathOn
            ) {

            boolParams = new HashMap<>(1);
            boolParams.put(KEYS.USE, usage);

            strParams = new HashMap<>(2);
            strParams.put(KEYS.PATH_0, pathOff);
            strParams.put(KEYS.PATH_1, pathExtra);
            strParams.put(KEYS.PATH_2, pathOn);
        }
    }

    /**
     * Clase que representa la información de un fondo. Es usada en
     * {@link app.maker.controllers.layerOptions.BackgroundLayerController}
     *
     * @see app.maker.controllers.objects.builders.BackgroundInfoBuilder
     */
    public static class BackgroundInfo extends Info {

        /**
         * Constructor de la clase que inicializa los parámetros
         * necesarios para el fondo.
         *
         * @param useImage Indica si se usa una imagen de fondo.
         * @param path Ruta del archivo de la imagen de fondo.
         * @param useColor Indica si se usa un color de fondo.
         * @param color Color del fondo en formato hexadecimal.
         */
        public BackgroundInfo(
                boolean useImage, String path,
                boolean useColor, String color
            ) {

            boolParams = new HashMap<>(2);
            boolParams.put(KEYS.IMAGE, useImage);
            boolParams.put(KEYS.USECOLOR, useColor);

            strParams = new HashMap<>(2);
            strParams.put(KEYS.PATH, path);
            strParams.put(KEYS.COLOR, color);
        }
    }

    /**
     * Clase que representa la información de los ojos. Es usada en
     * {@link app.maker.controllers.layerOptions.EyesLayerController}
     *
     * @see app.maker.controllers.objects.builders.EyesInfoBuilder
     */
    public static class EyesInfo extends Info {

        /**
         * Constructor de la clase que inicializa los parámetros
         * necesarios para los ojos.
         *
         * @param pathOff Ruta del archivo de la imagen por defecto.
         * @param usage Indica si el archivo y configuraciones de la
         *              segunda imagen están en uso.
         * @param timeToBlink Tiempo que tarda en parpadear.
         * @param timeBlinking Tiempo que dura el parpadeo.
         * @param pathOn Ruta del archivo de la imagen cuando se
         *               realiza el parpadeo.
         */
        public EyesInfo(
                String pathOff, boolean usage,
                int timeToBlink, int timeBlinking, String pathOn
            ) {

            boolParams = new HashMap<>(1);
            boolParams.put(KEYS.USE, usage);

            intParams = new HashMap<>(2);
            intParams.put(KEYS.TIMETO, timeToBlink);
            intParams.put(KEYS.TIMEBLINK, timeBlinking);

            strParams = new HashMap<>(2);
            strParams.put(KEYS.PATH_0, pathOff);
            strParams.put(KEYS.PATH_1, pathOn);
        }
    }

    /**
     * Clase que representa la información de la boca. Es usada en
     * {@link app.maker.controllers.layerOptions.MouthLayerController}
     *
     * @see app.maker.controllers.objects.builders.MouthInfoBuilder
     */
    public static class MouthInfo extends Info {

        /**
         * Constructor de la clase que inicializa los parámetros
         * necesarios para la boca.
         *
         * @param pathOff Ruta del archivo de la imagen por defecto.
         * @param usage Indica si el archivo y configuraciones de la
         *              segunda imagen están en uso.
         * @param channels Número de canales del micrófono.
         * @param updates Número de actualizaciones por segundo del
         *                micrófono.
         * @param sensitivity Sensibilidad del micrófono.
         * @param pathOn Ruta del archivo de la imagen cuando se
         *               detecta sonido con el micrófono.
         */
        public MouthInfo(
                String pathOff, boolean usage,
                int channels, int updates, int sensitivity,
                String pathOn
            ) {

            boolParams = new HashMap<>(1);
            boolParams.put(KEYS.USE, usage);

            intParams = new HashMap<>(3);
            intParams.put(KEYS.CHNLS, channels);
            intParams.put(KEYS.UPS, updates);
            intParams.put(KEYS.SENS, sensitivity);

            strParams = new HashMap<>(2);
            strParams.put(KEYS.PATH_0, pathOff);
            strParams.put(KEYS.PATH_1, pathOn);
        }
    }

    public static class MouseInfo extends Info {

        public MouseInfo(
                String pathMouse, String color,
                int xPos, int yPos, int width, int height,
                int pointAX, int pointAY, int pointBX, int pointBY,
                int pointCX, int pointCY, int pointDX, int pointDY
            ) {

            intParams = new HashMap<>(12);
            intParams.put(KEYS.XPOS, xPos);
            intParams.put(KEYS.YPOS, yPos);
            intParams.put(KEYS.WIDTH, width);
            intParams.put(KEYS.HEIGHT, height);

            intParams.put(KEYS.XPOS_A, pointAX);
            intParams.put(KEYS.YPOS_A, pointAY);
            intParams.put(KEYS.XPOS_B, pointBX);
            intParams.put(KEYS.YPOS_B, pointBY);
            intParams.put(KEYS.XPOS_C, pointCX);
            intParams.put(KEYS.YPOS_C, pointCY);
            intParams.put(KEYS.XPOS_D, pointDX);
            intParams.put(KEYS.YPOS_D, pointDY);

            strParams = new HashMap<>(2);
            strParams.put(KEYS.PATH, pathMouse);
            strParams.put(KEYS.COLOR, color);
        }
    }
}
