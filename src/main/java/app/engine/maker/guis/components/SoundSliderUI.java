package app.engine.maker.guis.components;

import javax.sound.sampled.AudioFormat;
import javax.sound.sampled.AudioSystem;
import javax.sound.sampled.DataLine;
import javax.sound.sampled.LineUnavailableException;
import javax.sound.sampled.TargetDataLine;
import javax.swing.JSlider;
import javax.swing.plaf.basic.BasicSliderUI;

import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Polygon;
import java.awt.Color;
import java.awt.Rectangle;

public class SoundSliderUI extends BasicSliderUI {

    private int ups = 20;
    private double amplitude = 0;
    private TargetDataLine microphone;

    public SoundSliderUI(JSlider b) {
        super(b);
        try {
            AudioFormat format = new AudioFormat(16000.0f, 8, 1, true, true);
            DataLine.Info info = new DataLine.Info(TargetDataLine.class, format);
            microphone = (TargetDataLine) AudioSystem.getLine(info);
            microphone.open(format);
            startCapture(b);
        } catch(LineUnavailableException e) {
            e.printStackTrace();
            throw new RuntimeException("No se pudo inicializar la línea de datos del micrófono");
        }
    }

    private void startCapture(JSlider slider) {
        Thread captureThread = new Thread(() -> {
            try {
                microphone.start();
                long lastExecutionTime = System.currentTimeMillis();
                long frameTime = 1000 / ups;
                while (!Thread.interrupted()) {
                    long currentTime = System.currentTimeMillis();
                    long elapsedTime = currentTime - lastExecutionTime;
                    if (elapsedTime >= frameTime) {
                        byte[] buffer = new byte[microphone.getBufferSize() / 5];
                        int bytesRead = microphone.read(buffer, 0, buffer.length);
                        if (bytesRead > 0) {
                            this.amplitude = calculateAmplitude(buffer);
                            slider.repaint();
                        }
                        lastExecutionTime = currentTime;
                    } else {
                        Thread.sleep(frameTime - elapsedTime);
                    }
                }
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        });
        captureThread.start();
    }

    /**
     * Calcula la amplitud del audio en el búfer dado.
     *
     * @param buffer El búfer de bytes de audio.
     * @return La amplitud del audio.
     */
    private static double calculateAmplitude(byte[] buffer) {
        double maxAmplitude = Math.pow(2, 8);
        double sum = 0;
        for (byte sample : buffer) {
            double normalized = (sample & 0xFF) / maxAmplitude;
            sum += normalized * normalized;
        }
        double mean = sum / buffer.length;
        return Math.min(Math.sqrt(mean), 1.0);
    }

    @Override
    public void paintTrack(Graphics g) {
        Graphics2D g2d = (Graphics2D) g.create();
        int width = trackRect.width;
        int height = trackRect.height;
        int x = trackRect.x;
        int y = trackRect.y;

        // Dibuja un triángulo de menos a más
        Polygon triangle = new Polygon();
        triangle.addPoint(x, y + height);
        triangle.addPoint(x + width, y + height);
        triangle.addPoint(x + width, y);

        Polygon triangle2 = new Polygon();
        triangle2.addPoint(x, y + height);
        triangle2.addPoint(x + (int)(width * amplitude), y + height);
        triangle2.addPoint(x + (int)(width * amplitude), y + height - (int)(height * amplitude));

        g2d.setColor(Color.LIGHT_GRAY);
        g2d.fillPolygon(triangle);
        g2d.setColor(Color.BLUE);
        g2d.fillPolygon(triangle2);
        g2d.dispose();
    }

    @Override
    public void paintThumb(Graphics g) {
        // Opcional: dibujar un "cursor" personalizado
        Graphics2D g2d = (Graphics2D) g.create();
        Rectangle knobBounds = thumbRect;
        g2d.setColor(Color.RED);
        g2d.fillOval(knobBounds.x, knobBounds.y, knobBounds.width, knobBounds.height);
        g2d.dispose();
    }
}
