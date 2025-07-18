package app.maker.controllers.objects;

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

        public int x, y, width, height;
        public String color;

        public boolean[] boolParams;
        public int[] intParams;
        public String[] path;
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
            this.x = x;
            this.y = y;
            this.width = width;
            this.height = height;
            this.path = new String[1];
            this.path[0] = path;
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
            this.width = width;
            this.height = height;
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
         * @param usage Indica si el archivo y configuraciones de la
         *              segunda imagen están en uso.
         * @param pathOn Ruta del archivo de la imagen cuando se
         *               realiza el cambio temporal.
         */
        public BasicInfo(String pathOff, boolean usage, String pathOn) {
            boolParams = new boolean[1];
            boolParams[0] = usage;
            path = new String[2];
            path[0] = pathOff;
            path[1] = pathOn;
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

            boolParams = new boolean[2];
            boolParams[0] = useImage;
            boolParams[1] = useColor;
            this.path = new String[1];
            this.path[0] = path;
            this.color = color;
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

            boolParams = new boolean[1];
            boolParams[0] = usage;
            intParams = new int[2];
            intParams[0] = timeBlinking;
            intParams[1] = timeBlinking;
            path = new String[2];
            path[0] = pathOff;
            path[1] = pathOn;
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

            boolParams = new boolean[1];
            boolParams[0] = usage;
            intParams = new int[3];
            intParams[0] = channels;
            intParams[1] = updates;
            intParams[2] = sensitivity;
            path = new String[2];
            path[0] = pathOff;
            path[1] = pathOn;
        }
    }
}
