/*
 * Copyright 2025 Raúl N. Valdés
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *  http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package app.maker.controllers.layerOptions;

import app.Constants;
import app.Sections.KEYS;
import app.fileUtils.ImageConverter;
import app.files.TranslationM;
import app.maker.FXFileChooser;
import app.maker.controllers.objects.Infos.Info;
import app.maker.controllers.objects.builders.MouthInfoBuilder;

import java.awt.image.BufferedImage;
import java.io.File;
import java.net.URI;
import java.nio.file.Path;
import java.nio.file.Paths;

import javax.sound.sampled.AudioFormat;
import javax.sound.sampled.AudioSystem;
import javax.sound.sampled.DataLine;
import javax.sound.sampled.LineUnavailableException;
import javax.sound.sampled.TargetDataLine;

import javafx.application.Platform;
import javafx.css.PseudoClass;
import javafx.embed.swing.SwingFXUtils;
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
    @FXML private HBox hboxMouth, hboxTalk;
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
            Constants.printTimeStamp(System.err);
            e.printStackTrace();
            throw new RuntimeException("No se pudo inicializar la línea de datos del micrófono");
        }
    }

    public void open() {
        try {
            AudioFormat format = new AudioFormat(8000.0f, 8, 1, true, false);
            DataLine.Info info = new DataLine.Info(TargetDataLine.class, format);
            microphone = (TargetDataLine) AudioSystem.getLine(info);
            microphone.open(format);
        } catch(LineUnavailableException e) {
            e.printStackTrace();
            throw new RuntimeException("No se pudo inicializar la línea de datos del micrófono");
        }
        if(checkboxMicrophone.selectedProperty().getValue()) startCapture();
    }

    public void close() {
        stopCapture();
        microphone.stop();
        microphone.close();
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

    /**
     * Actualiza la amplitud del micrófono en la interfaz de manera
     * viusual en la interfaz del usuario.
     *
     * @param amplitude El valor a representar acutalmente en la
     *                  interfaz. El valor debe estar entre
     *                  <code>0</code> y <code>1</code>.
     */
    private void updateAmplitude(double amplitude) {
        Platform.runLater(() -> {
            double width = sliderMicrophoneSensitivity.getWidth() * amplitude;
            micLevelOverlay.setMaxWidth(width);
        });
    }

    private void startCapture() {
        if(running) return;
        running = true;

        captureThread = new Thread(() -> {
            try {
                microphone.start();
                long lastExecutionTime = System.currentTimeMillis();
                while(!Thread.interrupted()) {
                    long frameTime = 1000 / spinnerMicrophoneUpdate.getValue();
                    long currentTime = System.currentTimeMillis();
                    long elapsedTime = currentTime - lastExecutionTime;
                    if(elapsedTime >= frameTime) {
                        byte[] buffer = new byte[microphone.getBufferSize() / 10];
                        int bytesRead = microphone.read(buffer, 0, buffer.length);
                        if(bytesRead > 0) {
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
        if(captureThread != null && captureThread.isAlive()) {
            captureThread.interrupt();
        }
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void initialize() {
        hasError = new boolean[2]; // 0: Mouth, 1: Talk
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

    @Override
    protected void handleError(int index, boolean error, boolean notify) {
        index = index%2;
        hasError[index] = error;

        TitledPane currentPane;
        HBox currentHBox;
        switch(index) {
            case 0:
            default:
                currentPane = titledPaneMouth;
                currentHBox = hboxMouth;
            break;

            case 1:
                currentPane = titledPaneTalk;
                currentHBox = hboxTalk;
            break;
        }
        currentPane.pseudoClassStateChanged(PseudoClass.getPseudoClass("error"), hasError[index]);
        currentHBox.pseudoClassStateChanged(PseudoClass.getPseudoClass("error"), hasError[index]);

        if(notify) {
            boolean anyError = false;
            for(boolean b: hasError) if(b) {
                anyError = true;
                break;
            }
            notifyObservers('e', anyError);
        }
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

            handleError(1, false, true);
        }
    }

    @FXML
    private void handleButtonClick(ActionEvent event) {
        Object source = event.getSource();
        ImageView imagePreview = source == buttonMouth? imagePreviewMouth: imagePreviewTalk;
        int index = source == buttonMouth? 0: 1;

        File file = FXFileChooser.getImageChooser().showOpenDialog(null);
        if(file != null) {
            Image img = new Image(file.toURI().toString());
            imagePreview.setImage(img);
            notifyObservers((char) getTweakID(), img);

            handleError(index, false, true);
        }
    }

    @Override
    public boolean readyToSave() {
        boolean hasBeenNotified = false;

        if(imagePreviewMouth.getImage() == null) {
            handleError(0, true, !hasBeenNotified);
            hasBeenNotified = true;
        }
        if(checkboxMicrophone.selectedProperty().getValue() && imagePreviewTalk.getImage() == null) {
            handleError(1, true, !hasBeenNotified);
            hasBeenNotified = true;
        }

        return !hasBeenNotified;
    }

    @Override
    public boolean setInfo(Info info) {
        boolean result = true;
        Path relativePath, fullPath;
        URI fullUri;
        String rle;
        ImageConverter rleConverter;
        Image img = null;

        if(!info.getString(KEYS.PATH_0).isEmpty()) {
            relativePath = Paths.get(info.getString(KEYS.PATH_0));
            fullPath = basePath.resolve(relativePath);
            fullUri = fullPath.toUri();
            img = new Image(fullUri.toString());
        } else if(!info.getString(KEYS.RLE_0).isEmpty()) {
            rle = info.getString(KEYS.RLE_0);
            rleConverter = new ImageConverter();
            rleConverter.setRLE(rle, false);
            BufferedImage bImage = rleConverter.convertRLEtoImage();
            if(bImage != null) img = SwingFXUtils.toFXImage(bImage, null);
        }
        if(img != null) {
            imagePreviewMouth.setImage(img);
            notifyObservers('l', img);
        } else {
            result = false;
            imagePreviewMouth.setImage(null);
            notifyObservers('l', null);
        }

        sliderMicrophoneChannel.setValue(info.getInt(KEYS.CHNLS));
        spinnerMicrophoneUpdate.getValueFactory().setValue(info.getInt(KEYS.UPS));
        sliderMicrophoneSensitivity.setValue(info.getInt(KEYS.SENS));
        checkboxMicrophone.selectedProperty().setValue(info.getBoolean(KEYS.USE));

        if(!info.getBoolean(KEYS.USE)) {
            result = false;
            imagePreviewTalk.setImage(null);
            stopCapture();
        } else {
            img = null;
            if(!info.getString(KEYS.PATH_1).isEmpty()) {
                relativePath = Paths.get(info.getString(KEYS.PATH_1));
                fullPath = basePath.resolve(relativePath);
                fullUri = fullPath.toUri();
                img = new Image(fullUri.toString());
            } else if(!info.getString(KEYS.RLE_1).isEmpty()) {
                rle = info.getString(KEYS.RLE_1);
                rleConverter = new ImageConverter();
                rleConverter.setRLE(rle, false);
                BufferedImage bImage = rleConverter.convertRLEtoImage();
                if(bImage != null) img = SwingFXUtils.toFXImage(bImage, null);
            }
            if(img != null) {
                imagePreviewTalk.setImage(img);
                startCapture();
            } else {
                result = false;
                imagePreviewTalk.setImage(null);
                stopCapture();
            }
        }

        for(int i = 0 ; hasError.length > i; i++) handleError(i, false, false);
        notifyObservers('e', false);

        return result;
    }

    @Override
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
