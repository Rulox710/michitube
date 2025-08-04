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

import java.io.File;

/**
 * Esta clase proporciona métodos para obtener rutas de directorios
 * específicos de la aplicación según el tipo de ruta solicitado.
 * Soporta diferentes tipos de sistemas operativos (Windows, macOS, Linux).
 */
public class AppPaths {

    /**
     * Enum que define los tipos de rutas disponibles.
     * - CONFIG: Ruta para configuraciones de la aplicación.
     * - DATA: Ruta para datos de la aplicación.
     * - CACHE: Ruta para caché de la aplicación.
     * - TEMP: Ruta para archivos temporales de la aplicación.
     */
    public enum PathType {
        CONFIG, DATA, CACHE, TEMP
    }

    private static String SEP = System.getProperty("file.separator");

    /**
     * Obtiene la ruta del directorio de la aplicación
     * según el nombre de la aplicación y el tipo de ruta especificado.
     * Crea el directorio si no existe.
     *
     * @param appName Nombre de la aplicación.
     * @param type Tipo de ruta que se desea obtener.
     *
     * @return Un objeto File que representa la ruta del directorio.
     */
    public static File getAppPath(String appName, PathType type) {
        String os = System.getProperty("os.name").toLowerCase();
        String basePath = null;

        switch(type) {
            case CONFIG:
                if(os.contains("win"))
                    basePath = System.getenv("APPDATA");
                else if(os.contains("mac"))
                    basePath = System.getProperty("user.home")+ SEP +"Library"+ SEP +"Application Support";
                else
                    basePath = System.getProperty("user.home")+ SEP +".config";
                break;

            case DATA:
                if(os.contains("win"))
                    basePath = System.getenv("LOCALAPPDATA");
                else if(os.contains("mac"))
                    basePath = System.getProperty("user.home")+ SEP +"Library"+ SEP +"Application Support";
                else
                    basePath = System.getProperty("user.home") + "/.local/share";
                break;

            case CACHE:
                if(os.contains("win"))
                    basePath = System.getenv("LOCALAPPDATA")+ SEP +"Cache";
                else if(os.contains("mac"))
                    basePath = System.getProperty("user.home")+ SEP +"Library"+ SEP +"Caches";
                else
                    basePath = System.getProperty("user.home")+ SEP +".cache";
                break;

            case TEMP:
                basePath = System.getProperty("java.io.tmpdir");
                break;
        }

        File finalPath = new File(basePath, appName);
        finalPath.mkdirs();
        return finalPath;
    }
}
