package app.engine.maker.guis;

import de.lars.colorpicker.ColorPicker;
import de.lars.colorpicker.listener.ColorListener;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;

import javax.swing.BorderFactory;
import javax.swing.JButton;
import javax.swing.JPanel;
import javax.swing.JScrollPane;

import app.Constants;
import app.engine.Observable;
import app.engine.Observer;
import app.engine.maker.guis.components.ColorPickerMod;
import app.engine.maker.guis.components.LayerButton;
import app.engine.readers.TranslationM;

public class LayersGUI extends Observable implements GenericGUI, Observer {

    private int GAP = 28, selectedLayer = 0;
    private JPanel parentPanel, topPanel, bottomPanel;
    private JScrollPane topScrollPane, bottomScrollPane;
    private JPanel backgroundOptions;
    private final LayerButton[] LAYER_BUTTONS = new LayerButton[2];

    public LayersGUI(JPanel parentPanel) {
        this.parentPanel = parentPanel;

        topPanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
        topPanel.setPreferredSize(
            new Dimension((int)(parentPanel.getHeight()*2.5)+5*2, parentPanel.getHeight()/2)
        );
        topPanel.setBackground(Constants.BLACK_78);
        topPanel.setBorder(BorderFactory.createEtchedBorder(Color.CYAN, Color.BLACK));
        topPanel.setSize(topPanel.getPreferredSize());
        topScrollPane = new JScrollPane(topPanel);
        topScrollPane.setVerticalScrollBarPolicy(JScrollPane.VERTICAL_SCROLLBAR_NEVER);
        topScrollPane.setHorizontalScrollBarPolicy(JScrollPane.HORIZONTAL_SCROLLBAR_AS_NEEDED);

        

        bottomPanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
        bottomPanel.setPreferredSize(
            new Dimension((int)(parentPanel.getHeight()*2.5)+5*2, parentPanel.getHeight()/2)
        );
        bottomPanel.setBackground(Constants.BLACK_50);
        bottomPanel.setBorder(BorderFactory.createEtchedBorder(Color.CYAN, Color.BLACK));
        bottomPanel.setSize(bottomPanel.getPreferredSize());
        bottomScrollPane = new JScrollPane(bottomPanel);
        bottomScrollPane.setVerticalScrollBarPolicy(JScrollPane.VERTICAL_SCROLLBAR_NEVER);
        bottomScrollPane.setHorizontalScrollBarPolicy(JScrollPane.HORIZONTAL_SCROLLBAR_AS_NEEDED);
        

        backgroundOptions();
        bodyOptions();

        parentPanel.add(topScrollPane);
        parentPanel.add(bottomScrollPane);
    }

    @Override
    public void changeSize() {
        topScrollPane.setPreferredSize(new Dimension(parentPanel.getWidth(), parentPanel.getHeight()/2));
        //topPanel.setSize(new Dimension(parentPanel.getWidth(), parentPanel.getHeight()/2));
        topPanel.revalidate();
        topPanel.repaint();

        bottomScrollPane.setPreferredSize(new Dimension(parentPanel.getWidth(), parentPanel.getHeight()/2));
        //bottomPanel.setSize(new Dimension(parentPanel.getWidth(), parentPanel.getHeight()/2));
        bottomPanel.revalidate();
        bottomPanel.repaint();
    }

    // private void labels() {
    //     public void drawText(Graphics g, String text) {
    //     int x = (int)(getWidth() * 0.2), y = 14, maxWidth = (int)(getWidth() * 0.8);
    //     Graphics2D g2d = (Graphics2D) g;
    //     Font originalFont = g2d.getFont();
    //     Font newFont = originalFont.deriveFont(14f);
    //     g2d.setFont(newFont);
    //     g2d.setColor(Color.WHITE);
    //     FontMetrics metrics = g2d.getFontMetrics();
    //     String ellipsis = "...";
    //     int ellipsisWidth = metrics.stringWidth(ellipsis);
    //     if (metrics.stringWidth(text) <= maxWidth) {
    //         g2d.drawString(text, x, y);
    //     } else {
    //         String truncatedText = text;
    //         while (metrics.stringWidth(truncatedText) + ellipsisWidth > maxWidth && truncatedText.length() > 0) {
    //             truncatedText = truncatedText.substring(0, truncatedText.length() - 1);
    //         }
    //         truncatedText += ellipsis;
    //         g2d.drawString(truncatedText, x, y);
    //     }
    //     g2d.setFont(originalFont);
    // }

    private void backgroundOptions() {
        LayerButton button = new LayerButton(parentPanel.getHeight()/2-GAP, "label_back");
        topPanel.add(button);
        button.addObserver(this);
        LAYER_BUTTONS[0] = button;

        backgroundOptions = new JPanel();
        backgroundOptions.setOpaque(false);
        JButton imageSelectButton = new JButton(TranslationM.getTranslatedLabel("button_select_image"));
        backgroundOptions.add(imageSelectButton);
        imageSelectButton.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                button.chooseImage();
            }
        });

        JButton colorSelectButton = new JButton(TranslationM.getTranslatedLabel("button_select_color"));
        backgroundOptions.add(colorSelectButton);
        colorSelectButton.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                Color selected = ColorPickerMod.showDialog(TranslationM.getTranslatedLabel("button_select_color"), TranslationM.getTranslatedLabel("button_ok"), TranslationM.getTranslatedLabel("button_cancel"), new Dimension(500, 350), null);
                bottomPanel.setBackground(selected);
            }
        });
    }

    private void bodyOptions() {
        LayerButton button = new LayerButton(parentPanel.getHeight()/2-GAP, "label_body");
        topPanel.add(button);
        button.addObserver(this);
        LAYER_BUTTONS[1] = button;
    }


    @Override
    public void updateLanguage() {
        
    }

    @Override
    public void update(char event, Object data) {
        switch(event) {
            case 'T':
                for(int i = 0; i < LAYER_BUTTONS.length; i++) {
                    if(LAYER_BUTTONS[i] != data) {
                        LAYER_BUTTONS[i].toggleSelect(false);
                    } else {
                        bottomPanel.removeAll();
                        switch(i) {
                            case 0: // Background:
                                bottomPanel.add(backgroundOptions);
                                selectedLayer = 0;
                                break;
                            case 1: // Body:
                                selectedLayer = 1;
                                break;
                        }
                        notifyObservers('L', selectedLayer);
                        bottomPanel.revalidate();
                        bottomPanel.repaint();
                    }
                }
                break;
            case 'I':
                notifyObservers((char) selectedLayer, data);
                break;
        }
    }
}
