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

package app.fileUtils;

import java.awt.image.BufferedImage;
import java.io.File;
import java.util.ArrayList;
import java.util.List;

import javax.imageio.ImageIO;

import app.Constants;
import app.LogMessage;

/**
 * Clase para convertir imágenes a formato RLE y viceversa.
 */
public class ImageConverter {

    private static char countChar = ':', nextChar = ';', sizeChar = 'x';

    private BufferedImage img = null;
    private int width, height;
    private List<Run> rle = new ArrayList<>();

    /**
     * Clase anidada que representa una secuencia de píxeles del mismo
     * color
     */
    protected static class Run {
        public int count, argb;

        /**
         * Constructor de la clase.
         *
         * @param count La cantidad de ocurrencias seguidas del pixel.
         * @param argb El valor ARGB del pixel.
         */
        public Run(int count, int argb) {
            this.count = count;
            this.argb = argb;
        }

        /**
         * Genera la representación en cadena de la secuencia.
         *
         * @param lengthFirst Indica si lo primero a mostrarse en la
         *                    secuencia es la cantidad de ocurrencias
         *                    del pixel o el valor ARGB del éste.
         *
         * @return Una cadena con la representación.
         */
        public String getRunString(boolean lengthFirst) {
            int[] list = (lengthFirst)?
                new int[]{count, argb}: new int[]{argb, count};
            return String.format("%d%c%d", list[0], countChar, list[1]);
        }

        /**
         * Genera la representación en cadena de la secuencia.
         * Primero se muestra el valor ARGB del pixel, un caracter
         * separador ({@link ImageConverter#countChar}) y luego la cantidad de
         * ocurrencias.
         */
        @Override
        public String toString() {
            return getRunString(false);
        }
    }

    /**
     * Establece el archivo con una imagen. Luego, se obtienen sus
     * medidas y se convierte a formato RLE.
     * Si por alguna razón no es posible leer la imagen, se
     * limpia la lista de secuencias RLE.
     *
     * @param file El archivo que contiene la imagen.
     */
    public void setFile(File file) {
        try {
            img = ImageIO.read(file);
            convertImagetoRLE();
        } catch(Exception e) {
            rle.clear();
            Constants.printTimeStamp(System.err);
            System.err.println(LogMessage.IMAGE_SAVE_X.get());
            e.printStackTrace();
        }
    }

    /**
     * Establece la cadena en formato RLE que representa una imagen.
     * Si por alguna razón no es posible interpretar la cadena, se
     * limpia la lista de secuencas RLE.
     *
     * @param rleString La cadena en formato RLE que representa una
     *                  imagen.
     * @param lengthFirst Indica si la cadena tiene primero la cantidad
     *                    de ocurrencias del pixel o el valor ARGB.
     */
    public void setRLE(String rleString, boolean lengthFirst) {
        rle.clear();

        String[] runs = rleString.split(nextChar + "");
        for(String run: runs) {
            String[] parts = run.split(countChar + "");
            if(parts.length != 2) {
                parts = run.split(sizeChar + "");
                try {
                    width = Integer.parseInt(parts[0]);
                    height = Integer.parseInt(parts[1]);
                } catch(Exception e) {
                    rle.clear();
                    Constants.printTimeStamp(System.err);
                    System.err.println(LogMessage.IMAGE_SAVE_X.get());
                    e.printStackTrace();
                    break;
                }
                continue;
            }
            try {
                int count = Integer.parseInt(parts[lengthFirst? 0: 1]);
                int argb = Integer.parseInt(parts[lengthFirst? 1: 0]);
                rle.add(new Run(count, argb));
            } catch(Exception e) {
                rle.clear();
                Constants.printTimeStamp(System.err);
                System.err.println(LogMessage.IMAGE_SAVE_X.get());
                e.printStackTrace();
                break;
            }
        }
    }

    /**
     * Crea la representación RLE de la imagen asignada.
     */
    private void convertImagetoRLE() {
        width = img.getWidth();
        height = img.getHeight();
        rle.clear();
        int count = 1, prevRGB = img.getRGB(0, 0);
        for(int y = 0; y < height; y++)
            for(int x = 0; x < width; x++) {
                if(x == 0 && y == 0) continue;
                int currRGB = img.getRGB(x, y);
                if(prevRGB == currRGB) count++;
                else {
                    rle.add(new Run(count, prevRGB));
                    count = 1;
                    prevRGB = currRGB;
                }
            }
        rle.add(new Run(count, prevRGB));
    }

    /**
     * Genera una imagen a partir de la representación en RLE asignada
     * con {@link ImageConverter#setRLE} o generada con
     * {@link ImageConverter#setFile}.
     *
     * @return La imagen generada o <code>null</code> si no hay una.
     */
    public BufferedImage convertRLEtoImage() {
        if(rle.isEmpty()) return null;

        BufferedImage newImg = new BufferedImage(
            width, height, BufferedImage.TYPE_INT_ARGB
        );
        int X = 0, Y = 0;
        for(Run run: rle) {
            int count = run.count, rgb = run.argb;
            for(int i = 0; i < count; i++) {
                newImg.setRGB(X, Y, rgb);
                if(++X == width) {
                    X = 0;
                    Y++;
                }
            }
        }
        return newImg;
    }

    /**
     * Genera la representación en cadena de la imagen en rle.
     *
     * @param lengthFirst Indica si lo primero a mostrarse en las
     *                    secuencias es la cantidad de ocurrencias
     *                    del pixel o el valor ARGB del éste.
     *
     * @return Una cadena con la representación.
     */
    public String getRLEString(boolean lengthFirst) {
        StringBuilder sb = new StringBuilder();
        sb.append(width).append(sizeChar).append(height).append(nextChar);
        for(int i = 0; i < rle.size(); i++) {
            sb.append(rle.get(i).getRunString(lengthFirst));
            if(i < rle.size() - 1) sb.append(nextChar);
        }
        return sb.toString();
    }

    /**
     * Genera la representación en cadena de la imagen en rle.
     * Primero se muestran las medidas (ancho x alto) separadas por un
     * caracter especial({@link ImageConverter#sizeChar}).
     * Luego se muestra cada secuencia, donde primero se escribe el
     * valor ARGB del pixel seguido de sus ocurrencias.
     * Cada uno de estos elementos se separa con un caracter especial
     * ({@link ImageConverter#nextChar}).
     */
    @Override
    public String toString() {
        return getRLEString(false);
    }
}
