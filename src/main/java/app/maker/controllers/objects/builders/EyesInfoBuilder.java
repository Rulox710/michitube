package app.maker.controllers.objects.builders;

import app.maker.controllers.objects.Infos.EyesInfo;
import app.maker.controllers.objects.Infos.Info;

/**
 * Constructor que es responsable de construir un objeto de tipo
 * {@link EyesInfo}.
 */
public class EyesInfoBuilder extends InfoBuilder {

    private boolean usage;
    private int [] intParams = new int[2];
    private String[] strParams = {"", ""};
    private int counterI = 0, counterS = 0;

    /**
     * Agrega las rutas de las imágenes de los ojos.
     * Pueden agregarse hasta dos parámetros.
     *
     * -El primero indica la ruta del archivo de la imagen por
     *  defecto.
     * -El segundo indica la ruta del archivo de la imagen cuando se
     *  realiza el parpadeo.
     *
     * @param path Ruta del archivo de la imagen.
     */
    @Override
    public void setPath(String path) {
        strParams[counterS] = path;
        counterS++;
    }

    /**
     * Indica si se usa o no el parpadeo.
     *
     * @param param Si se usa o no el parpadeo.
     */
    @Override
    public void setUsage(boolean isUsed) {
        usage = isUsed;
    }

    /**
     * Agrega números enteros al objeto.
     * Pueden agregarse hasta dos parámetros.
     *
     * - El primero indica cuanto tiepo en segundos toma para que el
     *   modelo parpadee y puede ser de <code>1<code> a <code>60<code>.
     *   En caso de ser menor a <code>1</code> se cambia a
     *   <code>1</code> y mayor a <code>60</code> cambia a
     *   <code>60</code>.
     * - El segundo indica el tiempo en segundos que toma en dejar de
     *   parpadear y puede estar entre <code>1<code> y <code>10<code>.
     *   En caso de ser menor a <code>1</code> se cambia a
     *   <code>1</code> y mayor a <code>60</code> cambia a
     *   <code>60</code>.
     *
     * @param param Un valor del parámetro entero.
     */
    @Override
    public void setIntParam(int param) {
        if(param < 1) param = 1;
        if(counterI == 0)
            if(param > 60) param = 60;
        else
            if(param > 10) param = 10;

        intParams[counterI] = param;
        counterI++;
    }

    /**
     * {@inheritDoc} En este caso, es un objeto tipo {@link EyesInfo}.
     */
    @Override
    public Info getResult() {
        return new EyesInfo(strParams[0], usage, intParams[0], intParams[1], strParams[1]);
    }

}
