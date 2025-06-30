package app.fileUtils;

import java.io.File;
import java.io.InputStream;

import javax.swing.ImageIcon;

import app.engine.readers.TranslationM;

public class Utils {
    public final static String jpeg = "jpeg";
    public final static String jpg = "jpg";
    public final static String gif = "gif";
    public final static String tiff = "tiff";
    public final static String tif = "tif";
    public final static String png = "png";
    public final static String sav = "sav";

    /*
    * Obtener extenciones del archivo.
    */
    public static String getExtension(File f) {
        String ext = null;
        String s = f.getName();
        int i = s.lastIndexOf('.');

        if (i > 0 && i < s.length() - 1) ext = s.substring(i+1).toLowerCase();
        return ext;
    }

    /** Regresa un ImageIcon, o null si la ruta es inválida. 
     *
     * @param path Ruta del archivo de imagen.
     * @return ImageIcon
     */
    protected static ImageIcon createImageIcon(String path) {
        // java.net.URL imgURL = Utils.class.getResource(path);
        // if (imgURL != null) return new ImageIcon(imgURL);
        InputStream in = Utils.class.getResourceAsStream(path);
        try {
            return new ImageIcon(in.readAllBytes());
        } catch (Exception e) {
            return null;
        }
    }
}