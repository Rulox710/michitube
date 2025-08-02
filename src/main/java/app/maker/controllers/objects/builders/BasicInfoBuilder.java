package app.maker.controllers.objects.builders;

import app.maker.controllers.objects.Infos.BasicInfo;
import app.maker.controllers.objects.Infos.Info;

/**
 * Constructor que es responsable de construir un objeto de tipo
 * {@link BasicInfo}.
 */
public class BasicInfoBuilder extends InfoBuilder {

    private boolean usage;
    private String[] strParams = {"", "", ""};
    private int counterS = 0;

    /**
     * Agrega las rutas de las imágenes de la boca.
     * Pueden agregarse hasta dos parámetros.
     *
     * -El primero indica la ruta del archivo de la imagen por
     *  defecto.
     * -El segundo indica la ruta del archivo de la imagen cuando se
     *  usa una característica.
     *
     * @param path Ruta del archivo de la imagen.
     */
    @Override
    public void setPath(String path) {
        strParams[counterS] = path;
        counterS++;
    }

    /**
     * Indica si se usa o no una característica.
     *
     * @param param Si se usa o no la característica.
     */
    @Override
    public void setUsage(boolean isUsed) {
        usage = isUsed;
    }

    /**
     * {@inheritDoc} En este caso, es un objeto tipo {@link BasicInfo}.
     */
    @Override
    public Info getResult() {
        return new BasicInfo(strParams[0], strParams[1], usage, strParams[2]);
    }
}
