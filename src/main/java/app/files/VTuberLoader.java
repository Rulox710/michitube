package app.files;

import app.Constants;
import app.Ids;
import app.LogMessage;
import app.Sections;
import app.Sections.KEYS;
import app.maker.controllers.LayersController;
import app.maker.controllers.SheetController;
import app.maker.controllers.objects.builders.BackgroundInfoBuilder;
import app.maker.controllers.objects.builders.BasicInfoBuilder;
import app.maker.controllers.objects.builders.EyesInfoBuilder;
import app.maker.controllers.objects.builders.ImageInfoBuilder;
import app.maker.controllers.objects.builders.InfoBuilder;
import app.maker.controllers.objects.builders.MouseInfoBuilder;
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

        builder.setWidth(vtuberReader.getInt(Sections.SHEET, KEYS.WIDTH));
        builder.setHeight(vtuberReader.getInt(Sections.SHEET, KEYS.HEIGHT));

        return sheetController.setSheetInfo(builder.getResult());
    }

    /**
     * Método estático que envía la información del area para el ratón
     * leída del archivo al controlador {@link SheetController}.
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
    private static boolean sendMouseAreaInfo(
            VTuberReader vtuberReader, SheetController sheetController
        ) {

        InfoBuilder builder = new MouseInfoBuilder();

        builder.setWidth(vtuberReader.getInt(Sections.MOUSE, KEYS.WIDTH));
        builder.setHeight(vtuberReader.getInt(Sections.MOUSE, KEYS.HEIGHT));
        builder.setXPos(vtuberReader.getInt(Sections.MOUSE, KEYS.XPOS));
        builder.setYPos(vtuberReader.getInt(Sections.MOUSE, KEYS.YPOS));

        final KEYS[] POS_KEYS = {
            KEYS.XPOS_A, KEYS.XPOS_B, KEYS.XPOS_C, KEYS.XPOS_D,
            KEYS.YPOS_A, KEYS.YPOS_B, KEYS.YPOS_C, KEYS.YPOS_D
        };
        for(int i = 0; i < POS_KEYS.length/2; i++) {
            builder.setIntParam(vtuberReader.getInt(
                Sections.MOUSE, POS_KEYS[i]
            ));
            builder.setIntParam(vtuberReader.getInt(
                Sections.MOUSE, POS_KEYS[i+4]
            ));
        }

        return sheetController.setMouseAreaInfo(builder.getResult());
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
            Ids id, Sections section, int tweak
        ) {

        InfoBuilder imageBuilder = new ImageInfoBuilder();

        final KEYS[] INFO_KEYS = switch(tweak) {
            case 0 -> new KEYS[]
                {KEYS.XPOS_0, KEYS.YPOS_0, KEYS.WIDTH_0, KEYS.HEIGHT_0, KEYS.PATH_0};
            case 1 -> new KEYS[]
                {KEYS.XPOS_1, KEYS.YPOS_1, KEYS.WIDTH_1, KEYS.HEIGHT_1, KEYS.PATH_1};
            case 2 -> new KEYS[]
                {KEYS.XPOS_2, KEYS.YPOS_2, KEYS.WIDTH_2, KEYS.HEIGHT_2, KEYS.PATH_2};
            default -> new KEYS[]
                {KEYS.XPOS, KEYS.YPOS, KEYS.WIDTH, KEYS.HEIGHT, KEYS.PATH};
        };

        imageBuilder.setXPos(vtuberReader.getInt(section, INFO_KEYS[0]));
        imageBuilder.setYPos(vtuberReader.getInt(section, INFO_KEYS[1]));
        imageBuilder.setWidth(vtuberReader.getInt(section, INFO_KEYS[2]));
        imageBuilder.setHeight(vtuberReader.getInt(section, INFO_KEYS[3]));
        imageBuilder.setPath(vtuberReader.get(section, INFO_KEYS[4]));

        boolean fileFound = sheetController.setInfoImage(
            id, tweak, imageBuilder.getResult()
        );
        if(!fileFound) sheetController.deleteImageInfo(id, tweak);
        return fileFound;
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
            Ids id, Sections section, boolean... fileFound
        ) {

        InfoBuilder layerBuilder = new BasicInfoBuilder();

        KEYS[] paths_key = new KEYS[]{KEYS.PATH_0, KEYS.PATH_1};
        switch(id) {
            case BACKGROUND:
                layerBuilder = new BackgroundInfoBuilder();

                layerBuilder.setUsage(vtuberReader.getBoolean(
                    section, KEYS.IMAGE
                ));
                layerBuilder.setUsage(vtuberReader.getBoolean(
                    section, KEYS.USECOLOR
                ));
                if(fileFound[0])
                    layerBuilder.setPath(vtuberReader.get(
                        section, paths_key[0]
                    ));
                if(vtuberReader.getBoolean(section, KEYS.USECOLOR))
                    layerBuilder.setColor(vtuberReader.get(
                        section, KEYS.COLOR
                    ));
            break;

            case BODY:
                if(fileFound[0])
                    layerBuilder.setPath(vtuberReader.get(
                        section, paths_key[0]
                    ));
            break;

            case EYES:
                layerBuilder = new EyesInfoBuilder();

                layerBuilder.setUsage(vtuberReader.getBoolean(
                    section, KEYS.USE
                ));
                layerBuilder.setIntParam(vtuberReader.getInt(
                    section, KEYS.TIMETO
                ));
                layerBuilder.setIntParam(vtuberReader.getInt(
                    section, KEYS.TIMEBLINK
                ));

                for(int i = 0; i < fileFound.length; i++)
                    if(fileFound[i])
                        layerBuilder.setPath(vtuberReader.get(
                            section, paths_key[i]
                        ));
            break;

            case MOUTH:
                layerBuilder = new MouthInfoBuilder();

                layerBuilder.setUsage(vtuberReader.getBoolean(
                    section, KEYS.USE
                ));
                layerBuilder.setIntParam(vtuberReader.getInt(
                    section, KEYS.CHNLS
                ));
                layerBuilder.setIntParam(vtuberReader.getInt(
                    section, KEYS.UPS
                ));
                layerBuilder.setIntParam(vtuberReader.getInt(
                    section, KEYS.SENS
                ));
                for(int i = 0; i < fileFound.length; i++)
                    if(fileFound[i])
                        layerBuilder.setPath(vtuberReader.get(
                            section, paths_key[i]
                        ));
            break;

            case TABLE:
                layerBuilder.setUsage(vtuberReader.getBoolean(
                    section, KEYS.USE
                ));
                if(fileFound[0])
                    layerBuilder.setPath(vtuberReader.get(
                        section, paths_key[0]
                    ));
            break;

            case KEYBOARD:
                layerBuilder.setUsage(vtuberReader.getBoolean(
                    section, KEYS.USE
                ));
                for(int i = 0; i < fileFound.length; i++)
                    if(fileFound[i])
                        layerBuilder.setPath(vtuberReader.get(
                            section, paths_key[i]
                        ));
            break;

            case MOUSE:
                layerBuilder = new BackgroundInfoBuilder();

                layerBuilder.setUsage(vtuberReader.getBoolean(
                    section, KEYS.USE
                ));
                layerBuilder.setColor(vtuberReader.get(
                    section, KEYS.COLOR
                ));
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
            System.out.println(LogMessage.MODEL_LOAD_X.get());
            Constants.printTimeStamp(System.err);
            System.err.println(LogMessage.MODEL_LOAD_X.get());
            return false;
        }

        sendSheetInfo(vTuberReader, sheetController);

        for(Ids id: Ids.values()) {
            boolean[] fileFound;
            Sections section = id.getEquivalent();

            switch(id) {
                case BACKGROUND:
                    fileFound = new boolean[1];
                    if(vTuberReader.getBoolean(section, KEYS.IMAGE))
                        fileFound[0] = sendImageInfo(
                            vTuberReader, sheetController,
                            id, section, 0
                        );
                    else sheetController.deleteImageInfo(id, 0);
                    sendLayerInfo(
                        vTuberReader, layersController, id, section, fileFound[0]
                    );
                break;

                case BODY:
                    fileFound = new boolean[1];
                    fileFound[0] = sendImageInfo(
                        vTuberReader, sheetController, id, section, 0
                    );
                    sendLayerInfo(
                        vTuberReader, layersController, id, section, fileFound[0]
                    );
                break;

                case EYES:
                    fileFound = new boolean[2];
                    fileFound[0] = sendImageInfo(
                        vTuberReader, sheetController, id, section, 0
                    );
                    if(vTuberReader.getBoolean(section, KEYS.USE))
                        fileFound[1] = sendImageInfo(
                            vTuberReader, sheetController, id, section, 1
                        );
                    else sheetController.deleteImageInfo(id, 1);
                    sendLayerInfo(
                        vTuberReader, layersController, id, section,
                        fileFound[0], fileFound[1]
                    );
                break;

                case MOUTH:
                    fileFound = new boolean[2];
                    fileFound[0] = sendImageInfo(
                        vTuberReader, sheetController, id, section, 0
                    );
                    if(vTuberReader.getBoolean(section, KEYS.USE))
                        fileFound[1] = sendImageInfo(
                            vTuberReader, sheetController, id, section, 1
                        );
                    else sheetController.deleteImageInfo(id, 1);
                    sendLayerInfo(
                        vTuberReader, layersController, id, section,
                        fileFound[0], fileFound[1]
                    );
                break;

                case TABLE:
                    fileFound = new boolean[1];
                    if(vTuberReader.getBoolean(section, KEYS.USE))
                        fileFound[0] = sendImageInfo(
                            vTuberReader, sheetController, id, section, 0
                        );
                    else sheetController.deleteImageInfo(id, 0);
                    sendLayerInfo(
                        vTuberReader, layersController, id, section, fileFound[0]
                    );
                break;

                case KEYBOARD:
                    fileFound = new boolean[3];
                    fileFound[0] = sendImageInfo(
                        vTuberReader, sheetController, id, section, 0
                    );
                    fileFound[1] = sendImageInfo(
                        vTuberReader, sheetController, id, section, 1
                    );
                    if(vTuberReader.getBoolean(section, KEYS.USE))
                        fileFound[2] = sendImageInfo(
                            vTuberReader, sheetController, id, section, 1
                        );
                    else sheetController.deleteImageInfo(id, 2);

                    sendLayerInfo(
                        vTuberReader, layersController, id, section,
                        fileFound[0], fileFound[1], fileFound[2]
                    );

                break;

                case MOUSE:
                    fileFound = new boolean[1];
                    sendMouseAreaInfo(vTuberReader, sheetController);
                    sendLayerInfo(
                        vTuberReader, layersController, id, section
                    );
                break;
            }
        }

        System.out.println(LogMessage.MODEL_LOAD_O.get());
        return true;
    }
}
