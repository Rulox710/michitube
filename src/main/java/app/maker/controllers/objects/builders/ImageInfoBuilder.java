package app.maker.controllers.objects.builders;

import app.maker.controllers.objects.Infos.Info;
import app.maker.controllers.objects.Infos.ImageInfo;

/**
 * Constructor que es responsable de construir un objeto de tipo
 * {@link ImageInfo}.
 */
public class ImageInfoBuilder extends InfoBuilder {

    private int x, y, width, height;
    private String path;

    /**
     * {@inheritDoc} No permite valores menores a <code>1</code> y
     * éste es pasado, se cambia a <code>0</code>.
     */
    @Override
    public void setXPos(int xPos) {
        x = (xPos < 1)? 0: xPos;
    }

    /**
     * {@inheritDoc} No permite valores menores a <code>1</code> y
     * éste es pasado, se cambia a <code>0</code>.
     */
    @Override
    public void setYPos(int yPos) {
        y = (yPos < 1)? 0: yPos;
    }

    /**
     * {@inheritDoc} No permite valores menores a <code>1</code> y
     * éste es pasado, se cambia a <code>290</code>.
     */
    @Override
    public void setWidth(int width) {
        this.width = (width < 1)? 290: width;
    }

    /**
     * {@inheritDoc} No permite valores menores a <code>1</code> y
     * éste es pasado, se cambia a <code>290</code>.
     */
    @Override
    public void setHeight(int height) {
        this.height = (height < 1)? 290: height;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void setPath(String path) {
        this.path = path;
    }

    /**
     * {@inheritDoc} En este caso, es un objeto tipo {@link ImageInfo}.
     */
    public Info getResult() {
        return new ImageInfo(x, y, width, height, path);
    }
}
