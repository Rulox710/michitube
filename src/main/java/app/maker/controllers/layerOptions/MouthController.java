package app.maker.controllers.layerOptions;

import app.files.TranslationM;
import app.maker.FXFileChooser;
import app.maker.controllers.objects.Infos.Info;
import app.maker.controllers.objects.builders.MouthInfoBuilder;

import java.io.File;
import java.net.URI;

import javax.sound.sampled.AudioFormat;
import javax.sound.sampled.AudioSystem;
import javax.sound.sampled.DataLine;
import javax.sound.sampled.LineUnavailableException;
import javax.sound.sampled.TargetDataLine;

import javafx.application.Platform;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.Accordion;
import javafx.scene.control.Button;
import javafx.scene.control.CheckBox;
import javafx.scene.control.Label;
import javafx.scene.control.Slider;
import javafx.scene.control.Spinner;
import javafx.scene.control.SpinnerValueFactory;
import javafx.scene.control.TitledPane;
import javafx.scene.control.Tooltip;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Region;
import javafx.util.StringConverter;

public class MouthController extends OptionLayerController {

    @FXML private Accordion mouthOptionsRoot;
    @FXML private TitledPane titledPaneMouth, titledPaneTalk;
    @FXML private Label labelMouth, labelTalk,labelMicrophoneChannel,
                        labelMicrophoneUpdate, labelMicrophoneSensitivity,
                        labelTalkSec;
    @FXML private Button buttonMouth, buttonTalk;
    @FXML private ImageView imagePreviewMouth, imagePreviewTalk;
    @FXML private CheckBox checkboxMicrophone;
    @FXML private Slider sliderMicrophoneChannel, sliderMicrophoneSensitivity;
    @FXML private Region micLevelOverlay;
    @FXML private HBox hboxTalk;
    @FXML private Spinner<Integer> spinnerMicrophoneUpdate;
    @FXML private Tooltip tooltipMicrophone;

    private TargetDataLine microphone;
    private Thread captureThread;
    private volatile boolean running = false;

    public MouthController() {
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

        optionsRoot = mouthOptionsRoot;
        firstPane = titledPaneMouth;
        super.initialize();

        spinnerMicrophoneUpdate.setValueFactory(new SpinnerValueFactory.IntegerSpinnerValueFactory(1, 120, 60));

        updateEnabledState(checkboxMicrophone.isSelected());
        checkboxMicrophone.selectedProperty().addListener((obs, oldVal, newVal) -> {
            updateEnabledState(newVal);
        });
    }

    private void updateEnabledState(boolean enabled) {
        buttonTalk.setDisable(!enabled);
        imagePreviewTalk.setDisable(!enabled);
        hboxTalk.setDisable(!enabled);
        labelMicrophoneChannel.setDisable(!enabled);
        labelMicrophoneUpdate.setDisable(!enabled);
        spinnerMicrophoneUpdate.setDisable(!enabled);
        labelTalkSec.setDisable(!enabled);
        labelMicrophoneSensitivity.setDisable(!enabled);
        sliderMicrophoneChannel.setDisable(!enabled);
        sliderMicrophoneSensitivity.setDisable(!enabled);
        if(enabled) startCapture();
        else {
            stopCapture();
            micLevelOverlay.setMaxWidth(0);
        }
    }

    @FXML
    private void handleButtonClick(ActionEvent event) {
        Object source = event.getSource();
        ImageView imagePreview = (source == buttonMouth)? imagePreviewMouth: imagePreviewTalk;
        File img = FXFileChooser.getImageChooser().showOpenDialog(null);
        if(img != null) {
            imagePreview.setImage(new Image(img.toURI().toString()));
            notifyObservers((char) getTweakID(), img);
        }
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
        if (running) return; // ya está corriendo
        running = true;

        captureThread = new Thread(() -> {
            try {
                microphone.start();
                long lastExecutionTime = System.currentTimeMillis();
                while (!Thread.interrupted()) {
                    long frameTime = 1000 / spinnerMicrophoneUpdate.getValue();
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
                Thread.currentThread().interrupt();
            }
        });
        captureThread.start();
    }

    private void stopCapture() {
        running = false;
        if (captureThread != null && captureThread.isAlive()) {
            captureThread.interrupt();
        }
    }

    @Override
    public boolean readyToSave() {
        if(imagePreviewMouth.getImage() == null) return false;
        if(checkboxMicrophone.selectedProperty().getValue() && imagePreviewTalk.getImage() == null)
            return false;
        return true;
    }

    @Override
    public boolean setInfo(Info info) {
        boolean result = true;

        if(info.path[0].length() != 0) {
            imagePreviewMouth.setImage(new Image(info.path[0]));
            notifyObservers('l', new File(URI.create(info.path[0])));
        } else result = false;

        sliderMicrophoneChannel.setValue(info.intParams[0]);
        spinnerMicrophoneUpdate.getValueFactory().setValue(info.intParams[1]);
        sliderMicrophoneSensitivity.setValue(info.intParams[2]);
        checkboxMicrophone.selectedProperty().setValue(info.boolParams[0]);
        if(info.boolParams[0] && info.path[1].length() != 0)
            imagePreviewTalk.setImage(new Image(info.path[1]));
        else result = false;

        return result;
    }

    public Info getInfo() {
        MouthInfoBuilder builder = new MouthInfoBuilder();
        builder.setUsage(checkboxMicrophone.selectedProperty().getValue());
        builder.setIntParam((int) sliderMicrophoneChannel.getValue());
        builder.setIntParam(spinnerMicrophoneUpdate.getValue());
        builder.setIntParam((int) sliderMicrophoneSensitivity.getValue());

        return builder.getResult();
    }

    @Override
    public void updateLanguage() {
        titledPaneMouth.setText(TranslationM.getTranslatedLabel("title_mouth"));
        buttonMouth.setText(TranslationM.getTranslatedLabel("button_select_image"));
        labelMouth.setText(TranslationM.getTranslatedLabel("label_mouth"));

        titledPaneTalk.setText(TranslationM.getTranslatedLabel("title_talk"));
        checkboxMicrophone.setText(TranslationM.getTranslatedLabel("checkbox_microphone_detection"));
        tooltipMicrophone.setText(TranslationM.getTranslatedLabel("tooltip_microphone_detection"));
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
        labelTalkSec.setText(TranslationM.getTranslatedLabel("label_seconds"));
        labelMicrophoneSensitivity.setText(TranslationM.getTranslatedLabel("label_microphone_sensitivity"));
        buttonTalk.setText(TranslationM.getTranslatedLabel("button_select_image"));
        labelTalk.setText(TranslationM.getTranslatedLabel("label_talk"));
    }
}
