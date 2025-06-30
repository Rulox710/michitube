package app.engine.maker.guis.components;

import java.awt.Color;
import java.awt.Cursor;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.RenderingHints;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.awt.event.MouseMotionListener;
import java.awt.image.BufferedImage;
import java.io.File;

import javax.swing.JComponent;

import app.engine.readers.ImageReader;

public class SheetComponent extends JComponent
        implements MouseListener, MouseMotionListener, ActionListener {

    protected int minSize = 20;

    protected class ImageInfo {
        private boolean initialized = false;
        private File fimg;
        private BufferedImage image;
        private double width, height;
        public int x = 0, y = 0;

        public ImageInfo() {}

        public ImageInfo(File fimg) {
            initialized = true;
            this.fimg = fimg;
            image = ImageReader.loadImage(fimg);
            width = (image.getWidth() < SheetComponent.this.getWidth())? image.getWidth(): SheetComponent.this.getWidth();
            height = (image.getHeight() < SheetComponent.this.getHeight())? image.getHeight(): SheetComponent.this.getHeight();
        }

        public boolean isInitialized() {
            return initialized;
        }

        public void changeSize(double width, double height) {
            this.width = Math.max(width, SheetComponent.this.minSize);
            this.height = Math.max(height, SheetComponent.this.minSize);

            //Image scaledImage = image.getScaledInstance(width, height, Image.SCALE_SMOOTH);
            BufferedImage scaledImage = new BufferedImage((int)this.width, (int)this.height, image.getType());
            Graphics2D g2d = scaledImage.createGraphics();
            g2d.setRenderingHint(RenderingHints.KEY_INTERPOLATION, RenderingHints.VALUE_INTERPOLATION_BILINEAR);
            g2d.drawImage(ImageReader.loadImage(fimg), 0, 0, (int)this.width, (int)this.height, null);
            g2d.dispose();
            image = scaledImage;
        }

        public void changeImage(File fimg) {
            this.fimg = fimg;
            this.image = ImageReader.loadImage(fimg);
            width = (image.getWidth() < SheetComponent.this.getWidth())? image.getWidth(): SheetComponent.this.getWidth();
            height = (image.getHeight() < SheetComponent.this.getHeight())? image.getHeight(): SheetComponent.this.getHeight();
        }

        public void deleteImage() {
            fimg = null;
            image = null;
            initialized = false;
        }

        public BufferedImage getImage() {
            return image;
        }

        public int getWidth() {
            return (int)width;
        }

        public double getDoubleWidth() {
            return width;
        }

        public double getDoubleHeight() {
            return height;
        }

        public int getHeight() {
            return (int)height;
        }
    }

    private final ImageInfo[] LAYERS;
    private int currentLayer = 0, resizeHandleSize = 10;
    private Color borderColor = Color.BLACK;

    private int clickPressedX, clickPressedY;
    private boolean movingX = false, movingY = false;
    private int buttonPressed = MouseEvent.NOBUTTON;
    
    public SheetComponent(int width, int height) {
        this.LAYERS = new ImageInfo[2];

        setOpaque(false);
        setBounds(0, 0, width, height);

        addMouseListener(this);
        addMouseMotionListener(this);  
    }

    public void changeSize(int width, int height) {
        setBounds(0,0, width, height);
        repaint();
    }

    public void selectLayer(int layerID) {
        if(layerID < 0 || layerID >= LAYERS.length) return;
        currentLayer = layerID;
        repaint();
    }

    public void addImage(int layer, File image) {
        this.LAYERS[layer] = new ImageInfo(image);
        repaint();
    }

    @Override
    public void paintComponent(Graphics g) {
        Graphics2D g2d = (Graphics2D) g;
        super.paintComponent(g2d);

        for(int i = 0; i < LAYERS.length; i++) {
            ImageInfo fimg = LAYERS[i];
            if(fimg == null || !fimg.isInitialized()) continue;
            drawImage(g2d, fimg, currentLayer == i);
        }
    }

    private void drawImage(Graphics2D g2d, ImageInfo imageInfo, boolean focus) {
        g2d.drawImage(imageInfo.getImage(), imageInfo.x, imageInfo.y, imageInfo.getWidth(), imageInfo.getHeight(), null);

        if(!focus) return;
        g2d.setColor(borderColor);
        g2d.drawRect(imageInfo.x-1, imageInfo.y-1, imageInfo.getWidth()+1, imageInfo.getHeight()+1);
        g2d.setColor(Color.WHITE);
        g2d.fillRect(imageInfo.x+imageInfo.getWidth()-7, imageInfo.y+imageInfo.getHeight()-7, resizeHandleSize, resizeHandleSize);
        g2d.setColor(Color.BLACK);
        g2d.drawRect(imageInfo.x+imageInfo.getWidth()-7, imageInfo.y+imageInfo.getHeight()-7, resizeHandleSize, resizeHandleSize);
    }

    @Override
    public void actionPerformed(ActionEvent e) {
        // TODO Auto-generated method stub
        //throw new UnsupportedOperationException("Unimplemented method 'actionPerformed'");
    }

    @Override
    public void mouseDragged(MouseEvent e) {
        ImageInfo imageInfo = LAYERS[currentLayer];
        if(imageInfo == null || !imageInfo.isInitialized()) return;

        switch(buttonPressed) {
        case MouseEvent.BUTTON1:
            switch(getCursor().getType()) {
            case Cursor.MOVE_CURSOR:
                int deltaX = e.getX()-clickPressedX;
                int deltaY = e.getY()-clickPressedY;
                imageInfo.x = imageInfo.x+deltaX;
                imageInfo.y = imageInfo.y+deltaY;
                break;
            case Cursor.SE_RESIZE_CURSOR:
                int deltaWidth = e.getX()-clickPressedX;
                int deltaHeight = e.getY()-clickPressedY;
                imageInfo.changeSize(imageInfo.getDoubleWidth()+deltaWidth, imageInfo.getDoubleHeight()+deltaHeight);
                break;
            }
            break;
        case MouseEvent.BUTTON3:
            switch(getCursor().getType()) {
            case Cursor.MOVE_CURSOR:
                int deltaX = e.getX()-clickPressedX;
                int deltaY = e.getY()-clickPressedY;
                if(Math.abs(deltaX) > 1 && !movingY) movingX = true;
                if(Math.abs(deltaY) > 1 && !movingX) movingY = true;
                deltaY = (!movingX && movingY)? deltaY: 0;
                deltaX = (!movingY && movingX)? deltaX: 0;
                imageInfo.x = imageInfo.x+deltaX;
                imageInfo.y = imageInfo.y+deltaY;
                
                break;
            case Cursor.SE_RESIZE_CURSOR:
                int deltaWidth = e.getX()-clickPressedX;
                double aspectRatio = imageInfo.getDoubleWidth() / imageInfo.getDoubleHeight();

                double newWidth = imageInfo.getDoubleWidth()+deltaWidth;
                double newHeight = newWidth / aspectRatio;
                if(newHeight >= 20 && newWidth >= 20)
                    imageInfo.changeSize(newWidth, newHeight);
                break;
            }
        }
        clickPressedX = e.getX();
        clickPressedY = e.getY();
        repaint();
    }

    @Override
    public void mouseMoved(MouseEvent e) {
        ImageInfo imageInfo = LAYERS[currentLayer];
        if(imageInfo == null || !imageInfo.isInitialized()) return;

        if(imageInfo.x < e.getX() && imageInfo.x + imageInfo.getWidth() > e.getX()
                && imageInfo.y < e.getY() && imageInfo.y + imageInfo.getHeight() > e.getY())
            setCursor(Cursor.getPredefinedCursor(Cursor.MOVE_CURSOR));
        else if(imageInfo.x+imageInfo.getWidth()-7 < e.getX()
                && imageInfo.x+imageInfo.getWidth()-7 + resizeHandleSize > e.getX()
                && imageInfo.y+imageInfo.getHeight()-7 < e.getY()
                && imageInfo.y+imageInfo.getHeight()-7 + resizeHandleSize > e.getY())
            setCursor(Cursor.getPredefinedCursor(Cursor.SE_RESIZE_CURSOR));
        else setCursor(Cursor.getDefaultCursor());
    }

    @Override
    public void mouseClicked(MouseEvent e) {
    }

    @Override
    public void mousePressed(MouseEvent e) {
        switch(e.getButton()) {
            case MouseEvent.BUTTON1:
            case MouseEvent.BUTTON3:
                clickPressedX = e.getX();
                clickPressedY = e.getY();
        }
        buttonPressed = e.getButton();
    }

    @Override
    public void mouseReleased(MouseEvent e) {
        clickPressedX = -1;
        clickPressedY = -1;
        movingX = false;
        movingY = false;
        buttonPressed = MouseEvent.NOBUTTON;
    }

    @Override
    public void mouseEntered(MouseEvent e) {
        borderColor = Color.WHITE;
        repaint();
    }

    @Override
    public void mouseExited(MouseEvent e) {
        borderColor = Color.BLACK;
        repaint();
    }
  
}
