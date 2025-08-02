package app.maker.controllers.objects.builders;

import app.maker.controllers.objects.Infos.Info;
import app.maker.controllers.objects.Infos.MouseInfo;

public class MouseInfoBuilder extends InfoBuilder {

    private int x, y, width, height;
    private int[] intParams = new int[8];
    private String path = "", color;

    private int counterI = 0;

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
    public void setColor(String hexString) {
        color = hexString;
    }

    public void setPath(String path) {
        this.path = path;
    }

    @Override
    public void setIntParam(int param) {
        intParams[counterI] = param;
        counterI++;
    }

    @Override
    public Info getResult() {
        return new MouseInfo(
            path, color, x, y, width, height,
            intParams[0], intParams[1], intParams[2], intParams[3],
            intParams[4], intParams[5], intParams[6], intParams[7]
        );
    }


}
