package app.engine.readers;

import java.awt.Color;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;

import javax.imageio.ImageIO;

public final class ImageReader {

    /**
     * Método para iniciar una imagen dado un File
     * @param fimg El archivo de la imagen
     */
    public static BufferedImage loadImage(File fimg) {
        try {
            BufferedImage loadedImage = ImageIO.read(fimg);
            return loadedImage;
        } catch (IOException  e) {
            String workingDir = System.getProperty("user.dir");
            System.out.println("Current working directory : " + workingDir);
            e.printStackTrace();
        }
        return null;
    }

    /**
     * Método para crear un archivo en formato png en una ruta dada por un 
     * objeto File
     * @param fimg El File con la dirección y nombre de la nueva imagen
     * @param image El BufferedImage de la imagen a guardar 
     */
    public static void saveImage(File fimg, BufferedImage image) {
        try {
            // Save as PNG
            File file = new File(fimg + ".png");
            ImageIO.write(image, "png", file);
        } catch(IOException e) {
            String workingDir = System.getProperty("user.dir");
            System.out.println("Current working directory : " + workingDir);
            e.printStackTrace();
        }
    }

    /**
     * Método para generar una matriz de Color a partir de un BufferedImage
     * @param image Un BufferdImage que se va a poner en formato de una matriz
     * @return La imagen en formato de Color[][]
     */
    public static Color[][] generarMatrizColor(BufferedImage image) {
        int[] srgb = image.getRGB(0,0,image.getWidth(),image.getHeight(),null,0,image.getHeight());
        Color[][] pixeles = new Color[image.getWidth()][image.getHeight()];
        for(int i = 0, j = -1, k = -1; i < srgb.length; i++) {
            if(i % image.getWidth() == 0) { 
                k++; 
                j=0;
            }
            pixeles[j][k] = new Color(srgb[i]);
            j++;
        }
        return pixeles;
    }

    /**
     * Método para generar un BufferedImage a partir de una matriz de Color
     * @param pixeles Una matrz de Color que se va a poner en formato de un 
     * BufferedImage
     * @return La imagen en formato de BufferedImage
     */
    public static BufferedImage generarBufferedImage(Color[][] pixeles) {
        BufferedImage nImage = new BufferedImage(pixeles.length, 
        pixeles[0].length, BufferedImage.TYPE_INT_RGB);
        // Set each pixel of the BufferedImage to the color from the Color[][].
        for (int i = 0; i < pixeles.length; i++) {
            for (int j = 0; j < pixeles[0].length; j++) {
                nImage.setRGB(i, j, pixeles[i][j].getRGB());
            }
        }
        return nImage;
    }
}