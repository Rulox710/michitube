package app.engine.maker.guis;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.File;
import java.util.Arrays;

import javax.swing.BorderFactory;
import javax.swing.ButtonGroup;
import javax.swing.JButton;
import javax.swing.JCheckBoxMenuItem;
import javax.swing.JComboBox;
import javax.swing.JFrame;
import javax.swing.JMenu;
import javax.swing.JMenuBar;
import javax.swing.JMenuItem;
import javax.swing.JPanel;
import javax.swing.JRadioButtonMenuItem;
import javax.swing.JSeparator;

import app.engine.Observable;
import app.engine.maker.FileChooser;
import app.engine.readers.ImageReader;
import app.engine.readers.PropertiesM;
import app.engine.readers.TranslationM;

public class MenuGUI extends Observable implements GenericGUI {

    private JButton executeButton;
    private JMenu menuFile, smFileSave, menuLanguage;
    private JMenuItem smFileDir, smFileLoad, smFileHard, smFileSoft;

    public MenuGUI(JFrame parent) {
        JMenuBar mb = new JMenuBar();

        executeButton = new JButton(TranslationM.getTranslatedLabel("button_start_vtuber"));
        executeButton.setBorderPainted(false);
        executeButton.setContentAreaFilled(false);


        menuFile = getMenuFiles();
        menuLanguage = getMenuLanguage();


        mb.add(executeButton);
        mb.add(menuFile);
        mb.add(menuLanguage);

        parent.setJMenuBar(mb);
    }

    @Override
    public void changeSize() {}

    private JMenu getMenuFiles() {
        JMenu menu = new JMenu(TranslationM.getTranslatedLabel("menu_file"));

        smFileDir = new JMenuItem(TranslationM.getTranslatedLabel("menu_file_dir"));
        smFileDir.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                FileChooser fcSelect = FileChooser.getDirChooser();
                if (fcSelect.chooseFile()){
                    File file = fcSelect.getFileChoosen();
                    notifyObservers('d', file.getPath());
                }
            }
        });
        smFileLoad = new JMenuItem(TranslationM.getTranslatedLabel("menu_file_load"));
        smFileLoad.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                System.out.print("smFileLoad");
            }
        });
        smFileSave = new JMenu(TranslationM.getTranslatedLabel("menu_file_save"));
        smFileHard = new JMenuItem(TranslationM.getTranslatedLabel("menu_file_saveh"));
        smFileSoft = new JMenuItem(TranslationM.getTranslatedLabel("menu_file_saves"));
        
        menu.add(smFileDir);
        menu.add(new JSeparator());
        menu.add(smFileLoad);
        menu.add(new JSeparator());
        smFileSave.add(smFileHard);
        smFileSave.add(smFileSoft);
        menu.add(smFileSave);


        return menu;
    }

    private JMenu getMenuLanguage() {
        JMenu menu = new JMenu(TranslationM.getTranslatedLabel("menu_language"));
        
        String[] optionsToChoose = Arrays.stream(TranslationM.LANGUAGES.values())
                    .map(TranslationM.LANGUAGES::toString)
                    .toArray(String[]::new);
        
        ButtonGroup languajeGroups = new ButtonGroup();
        int currentLanguage = TranslationM.LANGUAGES.valueOf(PropertiesM.getAppProperty("language")).getIndex();
        for(int i = 0; i < optionsToChoose.length; i++) {
            String language = optionsToChoose[i];
            JRadioButtonMenuItem languagMenuItem = new JRadioButtonMenuItem(language);
            if(i == currentLanguage) languagMenuItem.setSelected(true);
            languajeGroups.add(languagMenuItem);
            int index = i;
            languagMenuItem.addActionListener(e ->
                notifyObservers('l', TranslationM.LANGUAGES.values()[index].name())
            );
            menu.add(languagMenuItem);
        }

        return menu;
    }

    @Override
    public void updateLanguage() {
        executeButton.setText(TranslationM.getTranslatedLabel("button_start_vtuber"));

        menuFile.setText(TranslationM.getTranslatedLabel("menu_file"));
        smFileDir.setText(TranslationM.getTranslatedLabel("menu_file_dir"));
        smFileLoad.setText(TranslationM.getTranslatedLabel("menu_file_load"));
        smFileSave.setText(TranslationM.getTranslatedLabel("menu_file_save"));
        smFileHard.setText(TranslationM.getTranslatedLabel("menu_file_saveh"));
        smFileSoft.setText(TranslationM.getTranslatedLabel("menu_file_saves"));

        menuLanguage.setText(TranslationM.getTranslatedLabel("menu_language"));
    }

}
