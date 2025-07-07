package app.engine.maker.guis;

import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.Arrays;
import java.util.Hashtable;

import javax.swing.BorderFactory;
import javax.swing.BoxLayout;
import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JComboBox;
import javax.swing.JComponent;
import javax.swing.JFormattedTextField;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JSlider;
import javax.swing.JSpinner;
import javax.swing.SpinnerNumberModel;
import javax.swing.text.NumberFormatter;

import app.engine.Observable;
import app.engine.maker.guis.components.SoundSliderUI;
import app.engine.readers.PropertiesM;
import app.engine.readers.TranslationM;

public class OptionsGUI extends Observable implements GenericGUI {
    
    private JPanel panel, handinputPanel, microphonePanel, fpsPanel;
    private JScrollPane scrollPane;
    private JCheckBox mouseCheckbox, keyboardCheckbox, microphoneCheckbox;
    private JSpinner microphoneUpdateSpinner;
    private JSlider microphoneChannelSlider, microphoneSensitivitySlider;
    private JLabel microphoneChannelLabel, microphoneUpdateLabel, microphoneSensitivityLabel, fpsLabel;
    private Hashtable<Integer, JLabel> defaultsMap = new Hashtable<Integer, JLabel>(2);
  
    private JPanel parentPanel;

    public OptionsGUI(JPanel parentPanel) {

        this.parentPanel = parentPanel;
        panel = new JPanel(null);
        panel.setPreferredSize(
            new Dimension(parentPanel.getWidth()-24, 490)
        );
        panel.setSize(new Dimension(parentPanel.getWidth()-24, 500));
        int GAP = 20;

        handinputPanel = getPanelHandinput();
        handinputPanel.setBounds(2, 59, panel.getWidth()-GAP, 80);
        panel.add(handinputPanel);

        microphonePanel = getPanelMicrophone();
        microphonePanel.setBounds(2, 143, panel.getWidth()-GAP, 280);
        panel.add(microphonePanel);

        fpsPanel = getPanelFPS();
        fpsPanel.setBounds(2, 427, panel.getWidth()-GAP, 55);
        panel.add(fpsPanel);

        scrollPane = new JScrollPane(panel);
        scrollPane.setVerticalScrollBarPolicy(JScrollPane.VERTICAL_SCROLLBAR_AS_NEEDED);
        scrollPane.setHorizontalScrollBarPolicy(JScrollPane.HORIZONTAL_SCROLLBAR_NEVER);
        scrollPane.getVerticalScrollBar().getWidth();

        parentPanel.add(scrollPane);
    }

    @Override
    public void changeSize() {
        scrollPane.setPreferredSize(new Dimension(parentPanel.getWidth()-24, parentPanel.getHeight()-44));
        //panel.setSize(new Dimension(parentPanel.getWidth()-24, parentPanel.getHeight()-44));
        panel.revalidate();
        panel.repaint();
    }

    private JPanel getPanelHandinput() {
        JPanel panel = new JPanel(new FlowLayout(FlowLayout.LEFT));
        panel.setBorder(BorderFactory.createTitledBorder(
            TranslationM.getTranslatedLabel("title_handinput"))
        );

        mouseCheckbox = new JCheckBox(TranslationM.getTranslatedLabel("label_mouse"));
        mouseCheckbox.setSelected(
            Boolean.parseBoolean(PropertiesM.getVtuberProperty("mouse_detection"))
        );
        mouseCheckbox.addItemListener(e -> {
            notifyObservers('m', mouseCheckbox.isSelected());
        });

        keyboardCheckbox = new JCheckBox(TranslationM.getTranslatedLabel("label_keyboard"));
        keyboardCheckbox.setSelected(
            Boolean.parseBoolean(PropertiesM.getVtuberProperty("keyboard_detection"))
        );
        keyboardCheckbox.addItemListener(e -> {
            notifyObservers('k', keyboardCheckbox.isSelected());
        });

        panel.add(mouseCheckbox);
        panel.add(keyboardCheckbox);

        return panel;
    }

    private JPanel getPanelMicrophone() {
        JPanel panel = new JPanel();
        panel.setLayout(new BoxLayout(panel, BoxLayout.Y_AXIS));
        panel.setBorder(BorderFactory.createTitledBorder(
            TranslationM.getTranslatedLabel("title_microphone"))
        );

        // Para la detección del micrófono
        JPanel microphonePanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
        microphoneCheckbox = new JCheckBox(TranslationM.getTranslatedLabel("label_microphone_detection"));
        microphoneCheckbox.setSelected(
            Boolean.parseBoolean(PropertiesM.getVtuberProperty("microphone_detection"))
        );
        microphoneCheckbox.addItemListener(e -> {
            notifyObservers('n', microphoneCheckbox.isSelected());
        });
        microphonePanel.add(microphoneCheckbox);

        // Para los canales del micrófono
        JPanel panelChannel = new JPanel(new FlowLayout(FlowLayout.LEFT));
        microphoneChannelLabel = new JLabel(
            TranslationM.getTranslatedLabel("label_microphone_channels")
        );
        microphoneChannelSlider = new JSlider(
            JSlider.HORIZONTAL, 1, 2, Integer.parseInt(
                PropertiesM.getVtuberProperty("microphone_channels"))
        );
        microphoneChannelSlider.setPreferredSize(new Dimension(100, 50));
        defaultsMap.put(1, new JLabel(TranslationM.getTranslatedLabel("label_microphone_channel_1")));
        defaultsMap.put(2, new JLabel(TranslationM.getTranslatedLabel("label_microphone_channel_2")));
        microphoneChannelSlider.setLabelTable(defaultsMap);
        microphoneChannelSlider.setPaintLabels(true);
        microphoneChannelSlider.setPaintTicks(false);
        panelChannel.add(microphoneChannelLabel);
        panelChannel.add(microphoneChannelSlider);
        microphoneChannelSlider.addChangeListener(e -> {
            notifyObservers('c', microphoneChannelSlider.getValue());
        });

        // Para las actializaciones por segundo del micrófono
        JPanel panelUpdate = new JPanel(new FlowLayout(FlowLayout.LEFT));
        microphoneUpdateLabel = new JLabel(
            TranslationM.getTranslatedLabel("label_microphone_ups")
        );
        SpinnerNumberModel model = new SpinnerNumberModel(
            Integer.parseInt(PropertiesM.getVtuberProperty("microphone_ups")),
            1, 120, 1
        );
        microphoneUpdateSpinner = new JSpinner(model);
        JComponent editor = microphoneUpdateSpinner.getEditor();
        if (editor instanceof JSpinner.NumberEditor) {
            JFormattedTextField textField = ((JSpinner.NumberEditor) editor).getTextField();
            ((NumberFormatter) textField.getFormatter()).setAllowsInvalid(false);
            textField.setColumns(3);
            textField.addCaretListener(e -> {
                notifyObservers('u', textField.getText());
            });
        }
        panelUpdate.add(microphoneUpdateLabel);
        panelUpdate.add(microphoneUpdateSpinner);

        // Para ajustar la sensibilidad del micrófono
        JPanel microphoneSensitivityPanel = new JPanel();
        microphoneSensitivityPanel.setLayout(new BoxLayout(microphoneSensitivityPanel, BoxLayout.Y_AXIS));
        microphoneSensitivityLabel = new JLabel(
            TranslationM.getTranslatedLabel("label_microphone_sensitivity")
        );
        JPanel microphoneSensitivityPanel2 = new JPanel(new FlowLayout(FlowLayout.CENTER));
        microphoneSensitivitySlider = new JSlider(
            JSlider.HORIZONTAL, 0, 100, Integer.parseInt(
                PropertiesM.getVtuberProperty("microphone_threshold"))
        );
        microphoneSensitivitySlider.setUI(new SoundSliderUI(microphoneSensitivitySlider));
        microphoneSensitivitySlider.setPreferredSize(new Dimension(200, 50));
        microphoneSensitivityPanel2.add(microphoneSensitivitySlider);
        Hashtable<Integer, JLabel> percentMap = new Hashtable<>(3);
        percentMap.put(0, new JLabel("0%"));
        percentMap.put(50, new JLabel("50%"));
        percentMap.put(100, new JLabel("100%"));
        microphoneSensitivitySlider.setLabelTable(percentMap);
        microphoneSensitivitySlider.setPaintTicks(false);
        microphoneSensitivitySlider.setPaintLabels(true);
        microphoneSensitivitySlider.addChangeListener(e -> {
            notifyObservers('s', microphoneSensitivitySlider.getValue());
        });
        microphoneSensitivityPanel.add(microphoneSensitivityLabel);
        microphoneSensitivityPanel.add(microphoneSensitivityPanel2);

        panel.add(microphonePanel);
        panel.add(panelChannel);
        panel.add(panelUpdate);
        panel.add(microphoneSensitivityPanel);

        return panel;
    }

    private JPanel getPanelFPS() {
        JPanel panel = new JPanel(new FlowLayout(FlowLayout.LEFT));
        panel.setBorder(BorderFactory.createTitledBorder(
            TranslationM.getTranslatedLabel("title_fps"))
        );

        fpsLabel = new JLabel(TranslationM.getTranslatedLabel("label_fps"));
        JSpinner spinner = new JSpinner(
            new SpinnerNumberModel(
                Integer.parseInt(PropertiesM.getVtuberProperty("frames_per_second")),
                1, 120, 1
            )
        );
        JComponent editor = spinner.getEditor();
        if (editor instanceof JSpinner.NumberEditor) {
            JFormattedTextField textField = ((JSpinner.NumberEditor) editor).getTextField();
            ((NumberFormatter) textField.getFormatter()).setAllowsInvalid(false);
            textField.setColumns(3);
            textField.addCaretListener(e -> {
                notifyObservers('f', textField.getText());
            });
        }

        panel.add(fpsLabel);
        panel.add(spinner);

        return panel;
    }

    @Override
    public void updateLanguage() {
        // languagePanel.setBorder(BorderFactory.createTitledBorder(
        //         TranslationM.getTranslatedLabel("title_language"))
        //     );
        // languageOkButton.setText(TranslationM.getTranslatedLabel("button_ok"));

        handinputPanel.setBorder(BorderFactory.createTitledBorder(
            TranslationM.getTranslatedLabel("title_handinput"))
        );
        mouseCheckbox.setText(TranslationM.getTranslatedLabel("label_mouse"));
        keyboardCheckbox.setText(TranslationM.getTranslatedLabel("label_keyboard"));

        microphonePanel.setBorder(BorderFactory.createTitledBorder(
                TranslationM.getTranslatedLabel("title_microphone"))
            );
        microphoneCheckbox.setText(TranslationM.getTranslatedLabel("label_microphone_detection"));
        microphoneChannelLabel.setText(
            TranslationM.getTranslatedLabel("label_microphone_channels")
        );
        defaultsMap.put(1, new JLabel(TranslationM.getTranslatedLabel("label_microphone_channel_1")));
        defaultsMap.put(2, new JLabel(TranslationM.getTranslatedLabel("label_microphone_channel_2")));
        microphoneChannelSlider.setLabelTable(defaultsMap);
        microphoneUpdateLabel.setText(
            TranslationM.getTranslatedLabel("label_microphone_ups")
        );
        microphoneSensitivityLabel.setText(
            TranslationM.getTranslatedLabel("label_microphone_sensitivity")
        );
        fpsPanel.setBorder(BorderFactory.createTitledBorder(
            TranslationM.getTranslatedLabel("title_fps"))
        );
        fpsLabel.setText(TranslationM.getTranslatedLabel("label_fps"));

        panel.revalidate();
        panel.repaint();
    }
}
