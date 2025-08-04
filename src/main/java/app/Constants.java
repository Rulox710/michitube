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

package app;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.Toolkit;
import java.io.PrintStream;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

import javax.sound.sampled.AudioFormat;
import javax.sound.sampled.AudioSystem;
import javax.sound.sampled.DataLine;
import javax.sound.sampled.TargetDataLine;

/**
 * Contiene valores constantes que son relevantes para varias partes
 * del programa.
 */
public final class Constants {

    /** Nombre de la app */
    public static final String APP_NAME = "MichiTube";

    /**
     * Tipo de separación usada por el sistema operativo para navegar
     * por su sistema de archivos.
     */
    public static final String SEP = System.getProperty("file.separator");

    /** Configuraciones de la app */
    public static final String CONFIF_FILE = "config.properties";

    /** Ruta relativa del directorio de salida. */
    public static final String OUT = "." + Constants.SEP + "out" + Constants.SEP;

    /**
     * Dimensiones de la pantalla.
     */
    private static final Dimension SCREEN_SIZE =
        Toolkit.getDefaultToolkit().getScreenSize();
    /** Ancho de la pantalla. */
    public static final double FULLSCREEN_WIDTH = (double) SCREEN_SIZE.getWidth();
    /** Altura de la pantalla. */
    public static final double FULLSCREEN_HEIGHT = (double) SCREEN_SIZE.getHeight();

    private static AudioFormat format = new AudioFormat(8000.0f, 8, 1, true, false);
    private static DataLine.Info info = new DataLine.Info(TargetDataLine.class, format);
    public static final boolean IS_MICRO = AudioSystem.isLineSupported(info)? true : false;

    public static final Color BLACK_78 = new Color(78, 78, 78);
    public static final Color BLACK_50 = new Color(50, 50, 50);

    public static final void printTimeStamp(PrintStream ps) {
        String timestamp = LocalDateTime.now()
            .format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss"));
        ps.println(timestamp);
    }

}
