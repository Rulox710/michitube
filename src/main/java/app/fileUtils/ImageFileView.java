package app.fileUtils;

import java.io.File;
import javax.swing.*;
import javax.swing.filechooser.*;

import app.Constants;
import app.engine.readers.TranslationM;

public class ImageFileView extends FileView {

    ImageIcon jpgIcon = Utils.createImageIcon(Constants.AST + "icon-jpg.png");
    ImageIcon gifIcon = Utils.createImageIcon(Constants.AST + "icon-gif.png");
    ImageIcon tiffIcon = Utils.createImageIcon(Constants.AST + "icon-tif.png");
    ImageIcon pngIcon = Utils.createImageIcon(Constants.AST + "icon-png.png");

    /**
     * Da una descripción de un archivo de imagen.
     * 
     * @param file El archivo de imagen.
     * @return Una descripción del tipo de imagen
     *         (JPEG, GIF, TIFF, PNG).
     */
    public String getTypeDescription(File file) {
        String extension = Utils.getExtension(file);
        String type = null;

        if (extension != null) {
            if (extension.equals(Utils.jpeg) || extension.equals(Utils.jpg))
                type = TranslationM.getTranslatedLabel("type_jpg");
            else if (extension.equals(Utils.gif))
                type = TranslationM.getTranslatedLabel("type_gif");
            else if (extension.equals(Utils.tiff) || extension.equals(Utils.tif))
                type = TranslationM.getTranslatedLabel("type_tif");
            else if (extension.equals(Utils.png))
                type = TranslationM.getTranslatedLabel("type_png");
        }
        return type;
    }

    /**
     * Obtiene el icono de un archivo de imagen.
     * 
     * @param file El archivo de imagen.
     * @return El icono correspondiente al tipo de imagen.
     */
    public Icon getIcon(File file) {
        String extension = Utils.getExtension(file);
        Icon icon = null;

        if (extension != null) {
            if (extension.equals(Utils.jpeg) || extension.equals(Utils.jpg))
                icon = jpgIcon;
            else if (extension.equals(Utils.gif)) icon = gifIcon;
            else if (extension.equals(Utils.tiff) || extension.equals(Utils.tif))
                icon = tiffIcon;
            else if (extension.equals(Utils.png)) icon = pngIcon;
        }
        return icon;
    }
}
