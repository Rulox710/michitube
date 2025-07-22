package app.files;

import app.Ids;
import app.Sections;
import app.Sections.KEYS;
import app.maker.controllers.LayersController;
import app.maker.controllers.SheetController;
import app.maker.controllers.objects.builders.BackgroundInfoBuilder;
import app.maker.controllers.objects.builders.BasicInfoBuilder;
import app.maker.controllers.objects.builders.EyesInfoBuilder;
import app.maker.controllers.objects.builders.ImageInfoBuilder;
import app.maker.controllers.objects.builders.InfoBuilder;
import app.maker.controllers.objects.builders.MouthInfoBuilder;
import app.maker.controllers.objects.builders.SheetInfoBuilder;

import java.io.File;
import java.io.IOException;

/**
 * Clase que condensa las instrucciones para cargar el archivo con las
 * configuraciones del modelo vtuber.
 */
public final class VTuberLoader {

    /**
     * Método estático que envía la información de la hoja leída del
     * archivo al controlador {@link SheetController}.
     *
     * @param vtuberReader Objeto {@link VTuberReader} ya iniciado que
     *                     encuentra el archivo y obtiene las
     *                     configuraciones de la hoja.
     * @param sheetController El controlador al que se envía la
     *                        información.
     *
     * @return Un booleano que indica si algun dato de la configuración
     *         tiene un error grave que no se puede corregir
     *         automáticamente.
     */
    private static boolean sendSheetInfo(
            VTuberReader vtuberReader, SheetController sheetController
        ) {

        InfoBuilder builder = new SheetInfoBuilder();

        builder.setWidth(vtuberReader.getInt(
            Sections.SHEET.getKEY(), KEYS.WIDTH.getKEY()
        ));
        builder.setHeight(vtuberReader.getInt(
            Sections.SHEET.getKEY(), KEYS.HEIGHT.getKEY()
        ));

        return sheetController.setSheetInfo(builder.getResult());
    }

    /**
     * Método estático que envía la información de las disitintas
     * imágenes empleadas al controlador {@link SheetController} según
     * qué capa y ajuste se indique.
     *
     * @param vtuberReader Objeto {@link VTuberReader} ya iniciado que
     *                     encuentra el archivo y obtiene las
     *                     configuraciones según distintas claves.
     * @param sheetController El controlador al que se envía la
     *                        información.
     * @param id El identificador según la clase {@link Ids} para
     *           escoger a qué capa corresponde la información de la
     *           imagen.
     * @param tweak A qué ajuste de la capa seleccionada se envía la
     *              imagen.
     *
     * @return Un booleano que indica si algun dato de la configuración
     *         tiene un error grave que no se puede corregir
     *         automáticamente.
     */
    private static boolean sendImageInfo(
            VTuberReader vtuberReader, SheetController sheetController,
            Ids id, int tweak
        ) {

        InfoBuilder imageBuilder = new ImageInfoBuilder();

        imageBuilder.setXPos(vtuberReader.getInt(
            id.getID(), KEYS.XPOS.getFormatKEY(tweak)
        ));
        imageBuilder.setYPos(vtuberReader.getInt(
            id.getID(), KEYS.YPOS.getFormatKEY(tweak)
        ));
        imageBuilder.setWidth(vtuberReader.getInt(
            id.getID(), KEYS.WIDTH.getFormatKEY(tweak)
        ));
        imageBuilder.setHeight(vtuberReader.getInt(
            id.getID(), KEYS.HEIGHT.getFormatKEY(tweak)
        ));
        imageBuilder.setPath(vtuberReader.get(
            id.getID(), KEYS.PATH.getFormatKEY(tweak)
        ));

        return sheetController.setInfoImage(
                id, tweak, imageBuilder.getResult()
            );
    }

    /**
     * Método estático que envía la información de las disitintas
     * configuraciones de los parámetros según qué capa al controlador
     * {@link LayersController}. Este es útil para los archivos de
     * guardado tipo "enlace simbólico"
     *
     * @param vtuberReader Objeto {@link VTuberReader} ya iniciado que
     *                     encuentra el archivo y obtiene las
     *                     configuraciones según distintas claves.
     * @param layersController El controlador al que se envía la
     *                         información.
     * @param id El identificador según la clase {@link Ids} para
     *           escoger a qué capa corresponde la información de las
     *           configuraciones.
     * @param fileFound Parámetros que indican si anteriormente ha sido
     *                  posible encontrar el archivo correspondiente a
     *                  las imágenes usadas.
     *
     * @return Un booleano que indica si algún dato de la configuración
     *         tiene un error grave que no se puede corregir
     *         automáticamente.
     */
    private static boolean sendLayerInfo(
            VTuberReader vtuberReader, LayersController layersController,
            Ids id, boolean... fileFound
        ) {

        InfoBuilder layerBuilder = new BasicInfoBuilder();
        switch(id) {
            case BACKGROUND:
                layerBuilder = new BackgroundInfoBuilder();

                layerBuilder.setUsage(vtuberReader.getBoolean(
                    id.getID(), KEYS.IMAGE.getKEY()
                ));
                layerBuilder.setUsage(vtuberReader.getBoolean(
                    id.getID(), KEYS.USECOLOR.getKEY()
                ));
                if(fileFound[0])
                    layerBuilder.setPath(vtuberReader.get(
                        id.getID(), KEYS.PATH.getFormatKEY(0)
                    ));
                if(vtuberReader.getBoolean(id.getID(), KEYS.USECOLOR.getKEY()))
                    layerBuilder.setColor(vtuberReader.get(
                        id.getID(), KEYS.COLOR.getKEY()
                    ));
            break;

            case BODY:
                if(fileFound[0])
                    layerBuilder.setPath(vtuberReader.get(
                        id.getID(), KEYS.PATH.getFormatKEY(0)
                    ));
            break;

            case EYES:
                layerBuilder = new EyesInfoBuilder();

                layerBuilder.setUsage(vtuberReader.getBoolean(
                    id.getID(), KEYS.USE.getKEY()
                ));
                layerBuilder.setIntParam(vtuberReader.getInt(
                    id.getID(), KEYS.TIMETO.getKEY()
                ));
                layerBuilder.setIntParam(vtuberReader.getInt(
                    id.getID(), KEYS.TIMEBLINK.getKEY()
                ));
                for(int i = 0; i < fileFound.length; i++)
                    if(fileFound[i])
                        layerBuilder.setPath(vtuberReader.get(
                            id.getID(), KEYS.PATH.getFormatKEY(i)
                        ));
            break;

            case MOUTH:
                layerBuilder = new MouthInfoBuilder();

                layerBuilder.setUsage(vtuberReader.getBoolean(
                    id.getID(), KEYS.USE.getKEY()
                ));
                layerBuilder.setIntParam(vtuberReader.getInt(
                    id.getID(), KEYS.CHNLS.getKEY()
                ));
                layerBuilder.setIntParam(vtuberReader.getInt(
                    id.getID(), KEYS.UPS.getKEY()
                ));
                layerBuilder.setIntParam(vtuberReader.getInt(
                    id.getID(), KEYS.SENS.getKEY()
                ));
                for(int i = 0; i < fileFound.length; i++)
                    if(fileFound[i])
                        layerBuilder.setPath(vtuberReader.get(
                            id.getID(), KEYS.PATH.getFormatKEY(i)
                        ));
            break;

            case TABLE:
                layerBuilder.setUsage(vtuberReader.getBoolean(
                    id.getID(), KEYS.USE.getKEY()
                ));
                if(fileFound[0])
                    layerBuilder.setPath(vtuberReader.get(
                        id.getID(), KEYS.PATH.getFormatKEY(0)
                    ));
            break;

            case KEYBOARD:
                layerBuilder.setUsage(vtuberReader.getBoolean(
                    id.getID(), KEYS.USE.getKEY()
                ));
                for(int i = 0; i < fileFound.length; i++)
                    if(fileFound[i])
                        layerBuilder.setPath(vtuberReader.get(
                            id.getID(), KEYS.PATH.getFormatKEY(i)
                        ));
            break;

            case MOUSE:
            break;
        }

        return layersController.setInfo(id, layerBuilder.getResult());
    }

    /**
     * Método estático que debe ser llamado para cargar las
     * configuraciones del modelo vtuber.
     *
     * @param file El archivo donde están las configuraciones del
     *             vtubuer.
     * @param layersController El controlador de las cofiguraciones de
     *                         los parámetros al que se envía la
     *                         información.
     * @param sheetController El controlador al que se envía la
     *                        información de la hoja y los gráficos
     *                        relativos a las imágenes.
     *
     * @return Un booleano que indica si algún dato de la configuración
     *         tiene un error grave que no se puede corregir
     *         automáticamente.
     */
    public static boolean loadVTuber(
            File file,
            LayersController layersController, SheetController sheetController
        ) {

        VTuberReader vTuberReader = new VTuberReader();
        try {
            vTuberReader.loadFromFile(file.getPath());
        } catch (IOException e) {
            return false;
        }

        sendSheetInfo(vTuberReader, sheetController);

        for(Ids sectionName: Ids.values()) {
            boolean[] arr;
            switch(sectionName) {
                case BACKGROUND:
                    arr = new boolean[1];
                    if(vTuberReader.getBoolean(
                        sectionName.getID(), KEYS.IMAGE.getKEY()
                    ))
                        arr[0] = sendImageInfo(
                            vTuberReader, sheetController, sectionName, 0
                        );
                    sendLayerInfo(
                        vTuberReader, layersController, sectionName, arr[0]
                    );
                break;

                case BODY:
                    arr = new boolean[1];
                    arr[0] = sendImageInfo(
                        vTuberReader, sheetController, sectionName, 0
                    );
                    sendLayerInfo(
                        vTuberReader, layersController, sectionName, arr[0]
                    );
                break;

                case EYES:
                    arr = new boolean[2];
                    arr[0] = sendImageInfo(
                        vTuberReader, sheetController, sectionName, 0
                    );
                    if(vTuberReader.getBoolean(
                        sectionName.getID(), KEYS.USE.getKEY()
                    ))
                        arr[1] = sendImageInfo(
                            vTuberReader, sheetController, sectionName, 1
                        );
                    sendLayerInfo(
                        vTuberReader, layersController, sectionName,
                        arr[0], arr[1]
                    );
                break;

                case MOUTH:
                    arr = new boolean[2];
                    arr[0] = sendImageInfo(
                        vTuberReader, sheetController, sectionName, 0
                    );
                    if(vTuberReader.getBoolean(
                        sectionName.getID(), KEYS.USE.getKEY()
                    ))
                        arr[1] = sendImageInfo(
                            vTuberReader, sheetController, sectionName, 1
                        );
                    sendLayerInfo(
                        vTuberReader, layersController, sectionName,
                        arr[0], arr[1]
                    );
                break;

                case TABLE:
                    arr = new boolean[1];
                    if(vTuberReader.getBoolean(
                        sectionName.getID(), KEYS.USE.getKEY()
                    ))
                        arr[0] = sendImageInfo(
                            vTuberReader, sheetController, sectionName, 0
                        );
                    sendLayerInfo(
                        vTuberReader, layersController, sectionName, arr[0]
                    );
                break;

                case KEYBOARD:
                    arr = new boolean[2];
                    arr[0] = sendImageInfo(
                        vTuberReader, sheetController, sectionName, 0
                    );
                    if(vTuberReader.getBoolean(
                        sectionName.getID(), KEYS.USE.getKEY()
                    ))
                        arr[1] = sendImageInfo(
                            vTuberReader, sheetController, sectionName, 1
                        );
                    sendLayerInfo(
                        vTuberReader, layersController, sectionName,
                        arr[0], arr[1]
                    );
                break;

                case MOUSE:
                break;
            }
        }
        return true;
    }
}
