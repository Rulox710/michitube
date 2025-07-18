package app.detectors;

import app.engine.Observable;
import app.engine.Observer;
import app.files.PropertiesM;

import javax.sound.sampled.AudioFormat;
import javax.sound.sampled.AudioSystem;
import javax.sound.sampled.DataLine;
import javax.sound.sampled.LineUnavailableException;
import javax.sound.sampled.TargetDataLine;

/**
 * Detecta y gestiona la entrada de audio del micrófono.
 * Esta clase implementa {@link Observer} para recibir notificaciones
 * sobre actualizaciones de otros componentes del sistema.
 */
public class Microphone extends Observable implements Observer {

    private final byte THRESHOLD;
    private final AudioFormat FORMAT;
    private final TargetDataLine MICROPHONE;
    private final int UPS;

    private boolean detected = false;

    /**
     * Constructor de la clase Microphone.
     * Inicializa los parámetros del micrófono, como el umbral de
     * detección y el formato de audio.
     * También inicia la captura de audio del micrófono.
     */
    public Microphone() {
        THRESHOLD = Byte.parseByte(
            PropertiesM.getVtuberProperty("microphone_threshold")
        );
        int channels = Integer.parseInt(
            PropertiesM.getVtuberProperty("microphone_channels")
        );

        FORMAT = new AudioFormat(16000.0f, 8, channels, true, true);
        try {
            DataLine.Info info = new DataLine.Info(TargetDataLine.class, FORMAT);
            MICROPHONE = (TargetDataLine) AudioSystem.getLine(info);
            MICROPHONE.open(FORMAT);
        } catch(LineUnavailableException e) {
            e.printStackTrace();
            throw new RuntimeException("No se pudo inicializar la línea de datos del micrófono");
        }

        UPS = Integer.parseInt(
            PropertiesM.getVtuberProperty("microphone_ups")
        );
        startCapture();
    }

    /**
     * Inicia la captura de audio del micrófono en un hilo separado.
     */
    private void startCapture() {
        Thread captureThread = new Thread(() -> {
            try {
                MICROPHONE.start();
                long lastExecutionTime = System.currentTimeMillis();
                long frameTime = 1000 / UPS;
                while (!Thread.interrupted()) {
                    long currentTime = System.currentTimeMillis();
                    long elapsedTime = currentTime - lastExecutionTime;
                    if (elapsedTime >= frameTime) {
                        byte[] buffer = new byte[MICROPHONE.getBufferSize() / 5];
                        int bytesRead = MICROPHONE.read(buffer, 0, buffer.length);
                        if (bytesRead > 0) {
                            double amplitude = calculateAmplitude(buffer);
                            detected = (amplitude > THRESHOLD);
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
    private static byte calculateAmplitude(byte[] buffer) {
        double maxAmplitude = Math.pow(2, 8);
        double sum = 0;
        for (byte b : buffer) {
            double amplitude = (b & 0xFF) / maxAmplitude;
            sum += amplitude * amplitude;
        }
        double rms = Math.sqrt(sum / buffer.length);
        return (byte) (rms * 100);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void update(char event, Object data) {
        switch(event) {
            case 'u':
                notifyObservers('m', detected);
            default: break;
        }
    }
}
