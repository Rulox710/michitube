package app.maker.controllers.objects.builders;

import app.maker.controllers.objects.Infos.Info;
import app.maker.controllers.objects.Infos.SheetInfo;

/**
 * Constructor que es responsable de construir un objeto de tipo
 * {@link SheetInfo}.
 */
public class SheetInfoBuilder extends InfoBuilder {

    private int width, height;

    /**
     * {@inheritDoc}
     */
    @Override
    public void setWidth(int width) {
        this.width = (width < 1)? 300: width;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void setHeight(int height) {
        this.height = (height < 1)? 300: height;
    }

    /**
     * {@inheritDoc} En este caso, es un objeto tipo {@link SheetInfo}.
     */
    public Info getResult() {
        return new SheetInfo(width, height);
    }
}
