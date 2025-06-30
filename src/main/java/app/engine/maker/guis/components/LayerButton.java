package app.engine.maker.guis.components;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.FontMetrics;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Image;
import java.awt.Point;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.awt.event.MouseMotionListener;
import java.awt.image.BufferedImage;
import java.io.File;

import javax.swing.JButton;
import javax.swing.JComponent;
import javax.swing.JLabel;
import javax.swing.border.LineBorder;

import app.Constants;
import app.engine.Observable;
import app.engine.Observer;
import app.engine.maker.FileChooser;
import app.engine.readers.ImageReader;
import app.engine.readers.TranslationM;

public class LayerButton extends JComponent
        implements MouseListener, MouseMotionListener, ActionListener {

    private JButton selectImage;
    private String translation_id;
    private JLabel label_name;
    private Color borderColor = Color.BLACK;
    private BufferedImage image = null;
    private final Observable OBSERVABLE = new Observable() {};

    public LayerButton(int size, String translation_id) {
        this.translation_id = translation_id;

        label_name = new JLabel("NA");
        
        selectImage = new JButton(TranslationM.getTranslatedLabel("label_img"));

        setName(TranslationM.getTranslatedLabel(translation_id));
        setBorder(new LineBorder(Color.WHITE,1));
        setBackground(Constants.BLACK_78);
        setOpaque(true);
        //setBounds(0, 0, size, size);
        setPreferredSize(new Dimension(size+30, size));
        setSize(getPreferredSize());
        
        //UIManager.put("ToolTip.background", java.awt.Color.YELLOW);
        //UIManager.put("ToolTip.foreground", java.awt.Color.RED);
        //UIManager.put("ToolTip.font", new java.awt.Font("Arial", java.awt.Font.BOLD, 14));
        setToolTipText(TranslationM.getTranslatedLabel(translation_id));

        addMouseListener(this);
        addMouseMotionListener(this);

        selectImage.addActionListener(this);
        add(label_name);
        add(selectImage);
    }

    public void toggleSelect(boolean hasFocus) {
        if (hasFocus) {
            setBackground(Constants.BLACK_50);
            OBSERVABLE.notifyObservers('T', this);
        } else {
            setBackground(Constants.BLACK_78);
        }
    }

    public void addObserver(Observer o) {
        OBSERVABLE.addObserver(o);
    }

    public void updateLanguage() {
        setName(TranslationM.getTranslatedLabel(translation_id));
        setToolTipText(TranslationM.getTranslatedLabel(translation_id));
        repaint();
    }

    @Override
    public Point getToolTipLocation(MouseEvent event) {
        int mouseX = event.getX();
        String tooltipText = getToolTipText();
        FontMetrics metrics = getFontMetrics(getFont());
        int tooltipWidth = metrics.stringWidth(tooltipText);
        int adjustedX = mouseX;
        int margin = (int)(getWidth() * 0.1);
        int adjustedY = (int)(getHeight() * 0.1);
        if(adjustedX + tooltipWidth + margin >= getWidth()) {
            adjustedX = getWidth() - tooltipWidth - margin;
        } else if(adjustedX <= margin) {
            adjustedX = margin;
        }
        return new Point(adjustedX, adjustedY);
    }

    @Override
    public String getToolTipText(MouseEvent event) {
        return getName();
    }

    @Override
    public void paintComponent(Graphics g) {
        Graphics2D g2d = (Graphics2D) g;

        super.paintComponent(g2d);
        g2d.setColor(getBackground());
        g2d.fillRect(0, 0, getWidth(), getHeight());
        g2d.setColor(Color.BLACK);
        
        drawText(g2d, getName());
        drawImagePreview(g2d);
    }

    private void drawText(Graphics2D g2d, String text) {
        int x = (int)(getWidth() * 0.2), y = 14, maxWidth = (int)(getWidth() * 0.8);
        Font originalFont = g2d.getFont();
        Font newFont = originalFont.deriveFont(14f);
        g2d.setFont(newFont);
        g2d.setColor(Color.WHITE);
        FontMetrics metrics = g2d.getFontMetrics();
        String ellipsis = "...";
        int ellipsisWidth = metrics.stringWidth(ellipsis);
        if (metrics.stringWidth(text) <= maxWidth) {
            g2d.drawString(text, x, y);
        } else {
            String truncatedText = text;
            while (metrics.stringWidth(truncatedText) + ellipsisWidth > maxWidth && truncatedText.length() > 0) {
                truncatedText = truncatedText.substring(0, truncatedText.length() - 1);
            }
            truncatedText += ellipsis;
            g2d.drawString(truncatedText, x, y);
        }
        g2d.setFont(originalFont);

        // List<String> lines = new ArrayList<>();
        // String[] words = text.split(" ");
        // StringBuilder currentLine = new StringBuilder();
        // for(String word : words) {
        //     String testLine = currentLine + word + " ";
        //     if(metrics.stringWidth(testLine) > maxWidth) {
        //         lines.add(currentLine.toString());
        //         currentLine = new StringBuilder(word + " ");
        //     } else {
        //         currentLine.append(word).append(" ");
        //     }
        // }
        // if (currentLine.length() > 0) {
        //     lines.add(currentLine.toString());
        // }
        // int lineHeight = metrics.getHeight();
        // for(int i = 0; i < lines.size(); i++) {
        //     g2d.drawString(lines.get(i), x, y + (i * lineHeight));
        // }
    }

    private void drawImagePreview(Graphics2D g2d) {
        int x = (int)(getWidth() * 0.1), y = 20, width = (int)(getWidth() * 0.8), height = getHeight() - 25;
        if (image == null) {
            g2d.setColor(Color.LIGHT_GRAY);
            g2d.fillRect(x, y, width, height);
        } else {
            Image scaledImage = image.getScaledInstance(width, height, Image.SCALE_SMOOTH);
            g2d.drawImage(scaledImage, x, y, width, height, null);
        }

        // Dibujar borde
        g2d.setColor(borderColor);
        g2d.drawRect(x - 1, y - 1, width + 1, height + 1);
    }

    public void chooseImage() {
        FileChooser fcSelect = FileChooser.getImageChooser();
        if (fcSelect.chooseFile()){
            File file = fcSelect.getFileChoosen();
            image = ImageReader.loadImage(file);
            OBSERVABLE.notifyObservers('I', file);
            repaint();
        }
    }

    @Override
    public void mouseDragged(MouseEvent e) {
        // // TODO Auto-generated method stub
        // throw new UnsupportedOperationException("Unimplemented method 'mouseDragged'");
    }

    @Override
    public void mouseMoved(MouseEvent e) { }

    @Override
    public void mouseClicked(MouseEvent e) {
        // // TODO Auto-generated method stub
        // throw new UnsupportedOperationException("Unimplemented method 'mouseClicked'");
    }

    @Override
    public void mousePressed(MouseEvent e) {
        if (e.getButton() == MouseEvent.BUTTON1) {
            toggleSelect(true);
        } else if (e.getButton() == MouseEvent.BUTTON2) {
            System.out.println("Click con el botón del medio");
        } else if (e.getButton() == MouseEvent.BUTTON3) {
            toggleSelect(true);
            chooseImage();
        }
    }

    @Override
    public void mouseReleased(MouseEvent e) {
        // // TODO Auto-generated method stub
        // throw new UnsupportedOperationException("Unimplemented method 'mouseReleased'");
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

    @Override
    public void actionPerformed(ActionEvent e) {
        // TODO Auto-generated method stub
        // throw new UnsupportedOperationException("Unimplemented method 'actionPerformed'");
    }
}
