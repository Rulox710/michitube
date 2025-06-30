package app.engine.maker;

import java.io.File;

import javax.swing.JFileChooser;
import javax.swing.UIManager;
import javax.swing.filechooser.FileFilter;

import app.engine.readers.PropertiesM;
import app.engine.readers.TranslationM;
import app.fileUtils.ImageFileView;
import app.fileUtils.ImagePreview;
import app.fileUtils.Utils;

public class FileChooser {

    public static enum TYPE {
        IMG("chooser_img"),
        SVS("chooser_svs"),
        DIR("chooser_dir");

        private final String key;     

        private TYPE(String key) {
            this.key = key;
        }

        public String getKey() {
            return key;
        }
    }

    private class ImageFilter extends FileFilter {

        //Accept all directories and all gif, jpg, tiff, or png files.
        public boolean accept(File f) {
            if (f.isDirectory()) {
                return true;
            }

            String extension = Utils.getExtension(f);
            if (extension != null) {
                if (extension.equals(Utils.tiff) ||
                    extension.equals(Utils.tif) ||
                    extension.equals(Utils.gif) ||
                    extension.equals(Utils.jpeg) ||
                    extension.equals(Utils.jpg) ||
                    extension.equals(Utils.png)) {
                        return true;
                } else {
                    return false;
                }
            }
            return false;
        }

        public String getDescription() {
            return TranslationM.getTranslatedLabel("type_img");
        }
    }
  
    private class SavesFilter extends FileFilter {

        //
        public boolean accept(File f) {
            if (f.isDirectory()) {
                return true;
            }

            String extension = Utils.getExtension(f);
            if (extension != null) {
                return extension.equals(Utils.sav);
            }
            return false;
        }

        public String getDescription() {
            return TranslationM.getTranslatedLabel("type_img");
        }
    }

    private static FileChooser IMAGE_CHOOSER = createImageChooser();
    private static FileChooser SAVE_CHOOSER = createSaveChooser();
    private static FileChooser DIR_CHOOSER = createDirectoryChooser();

    private JFileChooser fileChooser;
    private File fileChoosen;
    private static String lastLanguage;
    
    private FileChooser(TYPE type) {
        if(lastLanguage == null) {
            putCustomTranslations();
            
            switch (type) {
                case IMG:
                    
                    break;
                case SVS:
                    
                    break;
                case DIR:
                    
                    break;
            }
            
        }
        
        fileChooser = new JFileChooser();

        String defaultDir = PropertiesM.getAppProperty("default_dir");
        fileChooser.setCurrentDirectory(new File(defaultDir));

        String title = "";
        switch (type) {
            case IMG:
                fileChooser.addChoosableFileFilter(new ImageFilter());
                fileChooser.setFileView(new ImageFileView());
                fileChooser.setAccessory(new ImagePreview(fileChooser));
                title = TranslationM.getTranslatedLabel(TYPE.IMG.getKey());
                break;
            case SVS:
                fileChooser.addChoosableFileFilter(new SavesFilter());
                title = TranslationM.getTranslatedLabel(TYPE.SVS.getKey());
                break;
            case DIR:
                fileChooser.setFileSelectionMode(JFileChooser.DIRECTORIES_ONLY);
                title = TranslationM.getTranslatedLabel(TYPE.DIR.getKey());
                break;
        }
        fileChooser.setDialogTitle(title);
        lastLanguage = PropertiesM.getAppProperty("language");
        
        fileChooser.setAcceptAllFileFilterUsed(false);
    }
    
    private static FileChooser createImageChooser() {
        return new FileChooser(TYPE.IMG);
    }

    private static FileChooser createSaveChooser() {
        return new FileChooser(TYPE.SVS);
    }

    private static FileChooser createDirectoryChooser() {
        return new FileChooser(TYPE.DIR);
    }

    private static void putCustomTranslations() {
        UIManager.put("FileChooser.cancelButtonText", TranslationM.getTranslatedLabel("button_cancel"));
        UIManager.put("FileChooser.saveButtonText", TranslationM.getTranslatedLabel("button_save"));
        UIManager.put("FileChooser.openButtonText", TranslationM.getTranslatedLabel("button_open"));
        UIManager.put("FileChooser.updateButtonText", TranslationM.getTranslatedLabel("button_update"));
        UIManager.put("FileChooser.helpButtonText", TranslationM.getTranslatedLabel("button_help"));
        UIManager.put("FileChooser.openButtonToolTipText", TranslationM.getTranslatedLabel("tooltip_open"));
        UIManager.put("FileChooser.saveButtonToolTipText", TranslationM.getTranslatedLabel("tooltip_save"));
        UIManager.put("FileChooser.cancelButtonToolTipText", TranslationM.getTranslatedLabel("tooltip_cancel"));
        UIManager.put("FileChooser.updateButtonToolTipText", TranslationM.getTranslatedLabel("tooltip_update"));
        UIManager.put("FileChooser.helpButtonToolTipText", TranslationM.getTranslatedLabel("tooltip_help"));

        UIManager.put("FileChooser.fileNameLabelText", TranslationM.getTranslatedLabel("label_filename"));
        UIManager.put("FileChooser.filesOfTypeLabelText", TranslationM.getTranslatedLabel("label_filetype"));
        UIManager.put("FileChooser.lookInLabelText", TranslationM.getTranslatedLabel("label_lookin"));
        
        UIManager.put("FileChooser.upFolderToolTipText", TranslationM.getTranslatedLabel("tooltip_upfolder"));
        UIManager.put("FileChooser.newFolderToolTipText", TranslationM.getTranslatedLabel("tooltip_newfolder"));
        UIManager.put("FileChooser.homeFolderToolTipText", TranslationM.getTranslatedLabel("tooltip_home"));
        UIManager.put("FileChooser.listViewButtonToolTipText", TranslationM.getTranslatedLabel("tooltip_listview"));
        UIManager.put("FileChooser.detailsViewButtonToolTipText", TranslationM.getTranslatedLabel("tooltip_detailsview"));

        UIManager.put("FileChooser.renameErrorTitleText", TranslationM.getTranslatedLabel("error_rename"));
        UIManager.put("FileChooser.renameErrorText", TranslationM.getTranslatedLabel("error_filerename"));
        UIManager.put("FileChooser.newFolderErrorText", TranslationM.getTranslatedLabel("error_newdir"));
    }

    private static void applyCustomTranslations() {
        putCustomTranslations();
        
        lastLanguage = PropertiesM.getAppProperty("language");
        IMAGE_CHOOSER = createImageChooser();
        SAVE_CHOOSER = createSaveChooser();
        DIR_CHOOSER = createDirectoryChooser();
    }

    public static FileChooser getImageChooser() {
        if(!lastLanguage.equals(PropertiesM.getAppProperty("language"))) {
            applyCustomTranslations();
            IMAGE_CHOOSER.fileChooser.setDialogTitle(TranslationM.getTranslatedLabel(TYPE.IMG.getKey()));
        }
        String defaultDir = PropertiesM.getAppProperty("default_dir");
        IMAGE_CHOOSER.fileChooser.setCurrentDirectory(new File(defaultDir));
        return IMAGE_CHOOSER;
    }

    public static FileChooser getSavesChooser() {
        if(!lastLanguage.equals(PropertiesM.getAppProperty("language"))) {
            applyCustomTranslations();
            SAVE_CHOOSER.fileChooser.setDialogTitle(TranslationM.getTranslatedLabel(TYPE.SVS.getKey()));
        }
        String defaultDir = PropertiesM.getAppProperty("default_dir");
        SAVE_CHOOSER.fileChooser.setCurrentDirectory(new File(defaultDir));
        return SAVE_CHOOSER;
    }

    public static FileChooser getDirChooser() {
        if(!lastLanguage.equals(PropertiesM.getAppProperty("language"))) {
            applyCustomTranslations();
            DIR_CHOOSER.fileChooser.setDialogTitle(TranslationM.getTranslatedLabel(TYPE.DIR.getKey()));
        }
        String defaultDir = PropertiesM.getAppProperty("default_dir");
        DIR_CHOOSER.fileChooser.setCurrentDirectory(new File(defaultDir));
        return DIR_CHOOSER;
    }

    public boolean chooseFile() {
        fileChoosen = null;
        if (fileChooser.showOpenDialog(null) == JFileChooser.APPROVE_OPTION) {
            fileChoosen = new File(fileChooser.getSelectedFile().getAbsolutePath());
            return true;
        }
        return false;
    }

    public File getFileChoosen() {
        return fileChoosen;
    }
}
