package app.engine.maker.guis;

import java.awt.Color;
import java.awt.Cursor;
import java.awt.Dimension;
import java.awt.Point;
import java.awt.event.MouseEvent;
import java.awt.event.MouseMotionAdapter;
import java.awt.image.BufferedImage;
import java.io.File;

import javax.swing.BorderFactory;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.SwingUtilities;

import app.engine.Observable;
import app.engine.Observer;
import app.engine.maker.guis.components.SheetComponent;

public class SheetGUI extends Observable implements GenericGUI, Observer {

    private int GAP = 5, RESIZE_BOX_SIZE = 10, MIN_HEIGHT = 100, MIN_WIDTH = 100;
    private JPanel parentPanel, resizablePanel, sheetPanel, resizeHandle;
    private JScrollPane sheetScrollPane;
    private SheetComponent sheetComponent;

    public SheetGUI(JPanel parentPanel) {
        this.parentPanel = parentPanel;

        resizablePanel = new JPanel(null);
        resizablePanel.setPreferredSize(
            new Dimension(parentPanel.getWidth()-GAP*3, parentPanel.getHeight()-GAP*3)
        );
        resizablePanel.setSize(resizablePanel.getPreferredSize());
        resizablePanel.setBackground(Color.BLACK);


        sheetPanel = new JPanel(null);
        sheetPanel.setPreferredSize(
            new Dimension(resizablePanel.getWidth()-GAP*4, resizablePanel.getHeight()-GAP*4)
        );
        sheetPanel.setBackground(Color.LIGHT_GRAY);
        sheetPanel.setBorder(BorderFactory.createEtchedBorder(Color.CYAN, Color.BLACK));
        sheetPanel.setSize(sheetPanel.getPreferredSize());
        sheetComponent = new SheetComponent(
            sheetPanel.getWidth(), sheetPanel.getHeight()
        );
        sheetPanel.add(sheetComponent);
        sheetScrollPane = new JScrollPane(sheetPanel);
        sheetScrollPane.setVerticalScrollBarPolicy(JScrollPane.VERTICAL_SCROLLBAR_ALWAYS);
        sheetScrollPane.setHorizontalScrollBarPolicy(JScrollPane.HORIZONTAL_SCROLLBAR_ALWAYS);


        resizeHandle = new JPanel();
        resizeHandle.setBackground(Color.GRAY);
        resizeHandle.setCursor(Cursor.getPredefinedCursor(Cursor.SE_RESIZE_CURSOR));
        resizeHandle.setBounds(
            sheetScrollPane.getWidth() - RESIZE_BOX_SIZE,
            sheetScrollPane.getHeight() - RESIZE_BOX_SIZE,
            RESIZE_BOX_SIZE, RESIZE_BOX_SIZE
        );
        resizablePanel.add(resizeHandle);
        resizeHandle.setLocation(resizablePanel.getWidth() - RESIZE_BOX_SIZE, resizablePanel.getHeight() - RESIZE_BOX_SIZE);

        resizablePanel.add(sheetScrollPane);
        

        resizeHandle.addMouseMotionListener(new MouseMotionAdapter() {
            public void mouseDragged(MouseEvent e) {
                Point dragPoint = SwingUtilities.convertPoint(
                    resizeHandle, e.getPoint(), resizablePanel
                );
                int newWidth = Math.min(
                    Math.max(MIN_WIDTH, dragPoint.x), resizablePanel.getWidth()
                );
                int newHeight = Math.min(
                    Math.max(MIN_HEIGHT, dragPoint.y), resizablePanel.getHeight()
                );
                sheetScrollPane.setBounds(0,0,newWidth, newHeight);
                sheetScrollPane.revalidate();
                sheetPanel.setPreferredSize(
                    new Dimension(newWidth-GAP*4, newHeight-GAP*4)
                );
                resizeHandle.setLocation(
                    newWidth - RESIZE_BOX_SIZE, newHeight - RESIZE_BOX_SIZE
                );
                sheetComponent.changeSize(sheetScrollPane.getWidth(), sheetScrollPane.getHeight());
            }
        });

        parentPanel.add(resizablePanel);
    }

    @Override
    public void changeSize() {
        resizablePanel.setPreferredSize(new Dimension(
            parentPanel.getWidth()-GAP*3, parentPanel.getHeight()-GAP*3
        ));
        resizablePanel.revalidate();

        sheetScrollPane.setBounds(0,0, Math.min(
            resizeHandle.getX()+GAP*2, parentPanel.getWidth()-GAP*3
        ), Math.min(
            resizeHandle.getY()+GAP*2, parentPanel.getHeight()-GAP*3)
        );
        sheetScrollPane.revalidate();
    }

    public void selectLayer(int layerID) {
        sheetComponent.selectLayer(layerID);
    }

    public void addImage(int layer, File image) {
        sheetComponent.addImage(layer, image);
    }

    @Override
    public void updateLanguage() {

    }

    @Override
    public void update(char event, Object data) {
        // TODO Auto-generated method stub
        throw new UnsupportedOperationException("Unimplemented method 'update'");
    }
}
