Michi Vtuber con Java.
======================

## Acerda de
Esta aplicación sirve para crear una animación de un "gato" que tiene movimientos de acuerdo a las actividades en tiempo real con el ratón, teclado y micrófono de la computadora.
<div style="text-align:center;">
  <img src="./res/example.gif" alt="Gif de cómo se ve el programa" width="300" height="200">
</div>
Inicialmente se creó con el propósito de ser usado por un streamer de twitch como un regalo, no esperando una compensación a cambio, pero únicamente podía usarse con el modelo por defecto (ahora el que se usa como ejemplo). Después se modificó para mostrar una interfaz simple que permite que se asignen disitintas propiedades y recursos para crear un modelo más acorde a las necesidades del usuario.

## Uso
Nótese que al ser un programa de Java, requiere que tengan Java Runtime Environment (JRE). Se necesita de java 17 como mínimo si se va a usar el archivo jar. De momento los usuarios de windows puede usar un ejecutable `.exe` que contiene todas las librerías necesarias.

### Compilación
Para compilar el programa use cualquiera de los scripts proporcionados, ya sea `program.bat` para el SO Windows o `program.sh` para SO afines a Unix.

### Antes de la ejecución
Si se desea, se pueden pasar las siguientes banderas:
 * `--log`: Desde donde se ejecuta el programa, crea una carpeta llamada `\out` con un archivo llamado `michi_out.log` donde se registran todos los pasos de la aplicación durante la ejecución.
 * `--debug`: Desde donde se ejecuta el programa, crea una carpeta llamada `\out` con un archivo llamado `michi_err.log` donde se registran todos los errores en tiempo de ejecución de la aplicación.
 * `--verbose`: Realiza las dos cosas anteriores.

En relación a los archivos de guardado, de momento solo pueden guardar los recursos como enlaces, así que si el recurso original se mueve, borra o es innaccesible, entonces no funcionará correctamente. A futuro se planea agregar la posibilidad de comprimir las imágenes y guardarlas dentro del archivo de guardado.
Estos archivos tienen los siguientes parámetros:
 * `P`: La ruta relativa (a la carpeta por defecto) del recurso. (cadena)
 * `X`: La coordenada X del recurso. (entero)
 * `Y`: La coordenada Y del recurso. (entero)
 * `W`: El ancho del recurso. (entero)
 * `H`: El alto del recurso. (entero)
 * `use`: Si cierta característica se utiliza. (booleano)
 * `image` : Si se utiliza la imagen. (booleano)
 * `color`: Si se utiliza un color. (booleano)
 * `C`: El color en hexadecimal que se usa. (cadena)
 * `channels`: Canales que dispone el micrófono. (1 o 2)
 * `updates`: Actualizacions por segundo del micrófono. (entero)
 * `sensitivity`: La sensibilidad del micrófono. (entero)
 * `timeto`: Tiempo que le toma al modelo en parpadear. (entero)
 * `timeblink`: Tiempo que le toma al modelo en volver a abrir los ojos. (entero)
 En caso de que este archivo sea modificado incorrectamente desde un editor de texto el programa ignorará todos los parámetros malformados.

Además, las características del programa persistentes serán guardadas en la carpeta del usuario actual:
 * Windows: Se usa la variable de entorno APPDATA + MichiTube, que normalmente apunta a una ruta como: `C:\Users\TuUsuario\AppData\Roaming\MichiTube\`
 * macOS: Se guardan en: `~/Library/Application Support/MichiTube/`
 * Linux (y otros Unix-like): Se utiliza: `~/.config/MichiTube/`
El archivo creado es `config.properties` y contiene los siguientes parámetros:
 * `default_dir`: El directorio por defecto para buscar recursos desde la aplicación. (una cadena que representa una ruta)
 * `language`: El idioma de la aplicación. (una cadena de dos letras que la representa)
 * `frames_per_second`: La cantidad de actualizaciones por segundo que realiza el modelo en ejecución. (un entero)
 * `windows_taskbar_height`: Un margen que se le deja a la ventana para no solaparse con la barra de tareas de Windows. (un entero)
 en caso de que este archivo no exista se crea uno con parámetros por dejecto y en caso de mal formación se modifican los parámetros para que tenga valores correctos.

### En tiempo de ejeción
Mientras la ventana de edición esté abierta, se podrá crear el modelo VTuber con disitintas imagenes y ajustando los distintis parámetros según las necesidades. Cuando el modelo esté listo se podrá guardar en un archivo `.sav`, en caso de errores se indicará visualmente en la interfaz. También se puede comenzar la ejecución del modelo desde la pestaña ▶ Iniciar.

Mientras la ventana del modelo esté activa puede registrar el teclado, ratón y micrófono del usuario si lo indicó así lo inidicó en la ventana de edición, y si además esta ventana tiene el foco principal, tiene 2 atajos del teclado:
 * Terminar el programa: Si se pulsa la secuencia de botones `CTRL-SHIFT-C` en ese orden, ya sea manteniendolos presionados o cada uno por separado, el programa termina.
 * Activar decoracion: Si se pulsa la secuencia de botones `CTRL-SHIFT-D` en ese orden, ya sea manteniendolos presionados o cada uno por separado, se activan los bordes de la ventana, permitiendo el movimiento de esta a la ubicación deseada, además de colocar un fondo color verde RGB(0, 255, 0).
Cuando los bordes están activos, el botón de cerrado no termina la aplicación, sino que vuelve a abrir la ventana de edición.

## Programa
<div style="display: flex; align-items: center;">
  <a href="https://dev.java">
    <img src="https://dev.java/assets/images/java-affinity-logo-icode-lg.png" alt="Java Affinity Logo" width="70px" />
  </a>
  <p style="margin-left: 10px;">Programado con Java 17</p>
</div>

## Licencia
Este proyecto está licenciado bajo la **Licencia Apache 2.0**.

Ver el archivo [LICENSE](LICENSE) para más detalles.

Para más información, visita: http://www.apache.org/licenses/LICENSE-2.0

## Licencias de terceros
Este proyecto utiliza:

- **JNativeHook** (https://github.com/kwhat/jnativehook)
  Licencia: GNU Lesser General Public License v3
  [COPYING](legal/jnativehook-2.2.2/COPYING.md)
  [COPYING.LESSER](legal/jnativehook-2.2.2/COPYING.LESSER.md)

- **JavaFX** (https://openjfx.io/)
  Licencia GPL v2 + OpenJDK Assembly Exception
  JavaFX.Base [LICENCE](legal/javafx-17.0.15/javafx.base/LICENSE)
  JavaFX.Controls [LICENCE](legal/javafx-17.0.15/javafx.controls/LICENSE)
  JavaFX.FXML [LICENCE](legal/javafx-17.0.15/javafx.fxml/LICENSE)
  JavaFX.Graphics [LICENCE](legal/javafx-17.0.15/javafx.graphics/LICENSE)
