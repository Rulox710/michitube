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

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.io.PrintStream;
import java.util.Locale;

import com.github.kwhat.jnativehook.GlobalScreen;
import com.github.kwhat.jnativehook.NativeHookException;

import app.files.PropertiesM;
import app.files.TranslationM;

/**
 * Clase principal que inicia la aplicación MichiTube.
 * Configura el entorno, carga las propiedades y recursos,
 * y lanza la interfaz gráfica.
 */
public class AppLauncher {

    /**
     * El método principal que inicia el programa.
     * Carga las configuraciones, crea la ventana principal y realiza
     * la detección de dispositivos.
     *
     * @param args Los argumentos de la línea de comandos.
     *             Estos son banderas opcionales que pueden habilitar
     *             el registro y la depuración.
     *             * --log para habilitar el registro,
     *             * --debug para habilitar la depuración y
     *             * --verbose para habilitar ambos.
     */
    public static void main(String[] args) {
        boolean enableLogging = false, enableDebug = false;

        for(String arg: args) {
            switch(arg.toLowerCase()) {
                case "--log":
                    enableLogging = true;
                break;

                case "--debug":
                    enableDebug = true;
                break;

                case "--verbose":
                    enableLogging = true;
                    enableDebug = true;
                break;
            }
        }

        if(enableLogging || enableDebug) {
            try {
                File directory = new File(Constants.OUT);
                if (!directory.exists()) directory.mkdir();

                if(enableLogging) {
                    File logFile = new File(directory, "michi_out.log");
                    PrintStream logStream = new PrintStream(new FileOutputStream(logFile, false));
                    System.setOut(logStream);
                }

                if(enableDebug) {
                    File errFile = new File(directory, "michi_err.log");
                    PrintStream errStream = new PrintStream(new FileOutputStream(errFile, true));
                    System.setErr(errStream);
                }
            } catch (IOException e) {
                System.err.println(LogMessage.PROGRAM_END_X);

                e.printStackTrace();
                return;
            }
        } else {
            System.setOut(new PrintStream(OutputStream.nullOutputStream()));
            System.setErr(new PrintStream(OutputStream.nullOutputStream()));
        }

        System.out.println(LogMessage.APP_START.get());

        TranslationM.load();
        PropertiesM.loadAppProperties();

        String language = PropertiesM.getAppProperty("language");
        Locale locale = Locale.ENGLISH;
        switch(language) {
            case "EN":
                locale = Locale.ENGLISH;
                break;
            case "ES":
                locale = new Locale(language);
                break;
            default:
                locale = Locale.ENGLISH;
                break;
        }
        Locale.setDefault(locale);
        System.out.println(String.format(LogMessage.LOCALE_SET.get(), locale.toLanguageTag()));

        try {
            GlobalScreen.registerNativeHook();
            System.out.println(LogMessage.GLOBALSCREEN_START.get());
        } catch(NativeHookException e) {
            System.out.println(LogMessage.PROGRAM_END_X);
            System.err.println(LogMessage.PROGRAM_END_X);

            Constants.printTimeStamp(System.err);
            e.printStackTrace();
            return;
        }

        Main.launchApp(args);
    }
}
