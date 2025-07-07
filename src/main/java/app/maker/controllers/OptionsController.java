package app.maker.controllers;

import javax.sound.sampled.AudioFormat;
import javax.sound.sampled.AudioSystem;
import javax.sound.sampled.DataLine;
import javax.sound.sampled.LineUnavailableException;
import javax.sound.sampled.TargetDataLine;

import app.engine.readers.TranslationM;
import javafx.application.Platform;
import javafx.fxml.FXML;
import javafx.scene.control.CheckBox;
import javafx.scene.control.Label;
import javafx.scene.control.Slider;
import javafx.scene.control.TitledPane;
import javafx.scene.layout.Region;
import javafx.scene.layout.StackPane;
import javafx.util.StringConverter;

public class OptionsController extends AbstractController  {

    @FXML
    private TitledPane titledPaneHandInput, titledPaneMicrophone, titledPaneFPS;
    @FXML
    private CheckBox checkboxMouse, checkboxKeyboard, checkboxMicrophone;
    @FXML
    private Label labelMicrophoneChannel, labelMicrophoneUpdate, labelMicrophoneSensitivity, labelFPS;
    @FXML
    private Slider sliderMicrophoneChannel, sliderMicrophoneSensitivity;
    @FXML
    private StackPane micSensitivityStack;
    @FXML
    private Region micLevelOverlay;

    private int ups = 20;
    private TargetDataLine microphone;

    public OptionsController() {
        try {
            AudioFormat format = new AudioFormat(8000.0f, 8, 1, true, false);
            DataLine.Info info = new DataLine.Info(TargetDataLine.class, format);
            microphone = (TargetDataLine) AudioSystem.getLine(info);
            microphone.open(format);
        } catch(LineUnavailableException e) {
            e.printStackTrace();
            throw new RuntimeException("No se pudo inicializar la línea de datos del micrófono");
        }
    }
 
    @Override
    public void initialize() {
        updateLanguage();
        startCapture();
    }

    private void updateAmplitude(double amplitude) {
        Platform.runLater(() -> {
            double width = sliderMicrophoneSensitivity.getWidth() * amplitude;
            micLevelOverlay.setMaxWidth(width);
        });
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

        // double maxAmplitude = Math.pow(2, 7);
        // double sum = 0;
        // for (byte sample: buffer) {
        //     double amplitude = (double) sample / maxAmplitude;
        //     sum += amplitude * amplitude;
        // }
        // double rms = Math.sqrt(sum / buffer.length);
        // return Math.min(rms, 1.0);
    }

    private void startCapture() {
        Thread captureThread = new Thread(() -> {
            try {
                microphone.start();
                long lastExecutionTime = System.currentTimeMillis();
                long frameTime = 1000 / ups;
                while (!Thread.interrupted()) {
                    long currentTime = System.currentTimeMillis();
                    long elapsedTime = currentTime - lastExecutionTime;
                    if (elapsedTime >= frameTime) {
                        byte[] buffer = new byte[microphone.getBufferSize() / 10];
                        int bytesRead = microphone.read(buffer, 0, buffer.length);
                        if (bytesRead > 0) {
                            updateAmplitude(calculateAmplitude(buffer));
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

    @Override
    public void updateLanguage() {
        Platform.runLater(() -> {
            titledPaneHandInput.setText(TranslationM.getTranslatedLabel("title_handinput"));
            checkboxMouse.setText(TranslationM.getTranslatedLabel("label_mouse"));
            checkboxKeyboard.setText(TranslationM.getTranslatedLabel("label_keyboard"));

            titledPaneMicrophone.setText(TranslationM.getTranslatedLabel("title_microphone"));
            checkboxMicrophone.setText(TranslationM.getTranslatedLabel("label_microphone_detection"));
            labelMicrophoneChannel.setText(TranslationM.getTranslatedLabel("label_microphone_channels"));
            sliderMicrophoneChannel.setLabelFormatter(new StringConverter<Double>() {
                @Override
                public String toString(Double value) {
                    return switch (value.intValue()) {
                        case 1 -> TranslationM.getTranslatedLabel("label_microphone_channel_1");
                        case 2 -> TranslationM.getTranslatedLabel("label_microphone_channel_2");
                        default -> "";
                    };
                }
                @Override
                public Double fromString(String string) {
                    if(string.equals(TranslationM.getTranslatedLabel("label_microphone_channel_1"))) return 1.0;
                    if(string.equals(TranslationM.getTranslatedLabel("label_microphone_channel_2"))) return 2.0;
                    return 0.0;
                }
            });
            labelMicrophoneUpdate.setText(TranslationM.getTranslatedLabel("label_microphone_ups"));
            labelMicrophoneSensitivity.setText(TranslationM.getTranslatedLabel("label_microphone_sensitivity"));

            titledPaneFPS.setText(TranslationM.getTranslatedLabel("title_fps"));
            labelFPS.setText(TranslationM.getTranslatedLabel("label_fps"));
        });
    }
}
