package app.files;

import app.Constants;
import app.Ids;
import app.LogMessage;
import app.Sections;
import app.Sections.KEYS;
import app.maker.controllers.LayersController;
import app.maker.controllers.SheetController;
import app.maker.controllers.objects.Infos.Info;

import java.io.File;
import java.io.IOException;
import java.util.Map;

/**
 * Clase que condensa las instrucciones para guardar el archivo con las
 * configuraciones del modelo vtuber.
 */
public class VTuberSaver {

    /**
     * Método estático que recibe la información sobre la hoja del controlador
     * {@link SheetController}.
     *
     * @param vtuberWriter Objeto {@link VTuberWriter} ya iniciado que
     *                     encuentra el archivo y coloca las
     *                     configuraciones.
     * @param sheetController El controlador del que se recibe la
     *                        información.
     */
    private static void receiveSheetInfo(
            VTuberWriter vtuberWriter, SheetController sheetController
        ) {

        Info sheetInfo = sheetController.getSheetInfo();
        vtuberWriter.put(
            Sections.SHEET, KEYS.WIDTH, sheetInfo.getInt(KEYS.WIDTH)
        );
        vtuberWriter.put(
            Sections.SHEET, KEYS.HEIGHT, sheetInfo.getInt(KEYS.HEIGHT)
        );
    }

    /**
     * Método estático que recibe la información sobre el area del
     * ratón del controlador {@link SheetController}.
     *
     * @param vtuberWriter Objeto {@link VTuberWriter} ya iniciado que
     *                     encuentra el archivo y coloca las
     *                     configuraciones.
     * @param sheetController El controlador del que se recibe la
     *                        información.
     */
    private static void receiveMouseAreaInfo(
            VTuberWriter vtuberWriter, SheetController sheetController
        ) {

        Info areaInfo = sheetController.getMouseAreaInfo();

        KEYS[] mouseKeys = {KEYS.WIDTH, KEYS.HEIGHT, KEYS.XPOS, KEYS.YPOS};
        for(KEYS key: mouseKeys)
            vtuberWriter.put(
                Sections.MOUSE, key, areaInfo.getInt(key)
            );
        vtuberWriter.put(
            Sections.MOUSE, KEYS.COLOR, areaInfo.getString(KEYS.COLOR)
        );

        KEYS[] posKeys = {
            KEYS.XPOS_A, KEYS.XPOS_B, KEYS.XPOS_C, KEYS.XPOS_D,
            KEYS.YPOS_A, KEYS.YPOS_B, KEYS.YPOS_C, KEYS.YPOS_D
        };
        for(int i = 0; i < posKeys.length/2; i++) {
            vtuberWriter.put(
                Sections.MOUSE, posKeys[i], areaInfo.getInt(posKeys[i])
            );
            vtuberWriter.put(
                Sections.MOUSE, posKeys[i+4], areaInfo.getInt(posKeys[i+4])
            );
        }
    }

    /**
     * Método estático que recibe la información sobre las disitintas
     * imágenes empleadas en el controlador {@link SheetController}
     * según qué capa y ajuste se indique.
     *
     * @param vtuberWriter Objeto {@link VTuberWriter} ya iniciado que
     *                     encuentra el archivo y coloca las
     *                     configuraciones según distintas claves.
     * @param sheetController El controlador del que se recibe la
     *                        información.
     */
    private static void receiveImageInfo(
            VTuberWriter vtuberWriter, SheetController sheetController,
            Ids id, Sections section, int i
        ) {

        Info sheetInfo = sheetController.getInfoMap().get(id)[i];

        KEYS[] keys = {
            KEYS.XPOS, KEYS.YPOS, KEYS.WIDTH, KEYS.HEIGHT, KEYS.PATH
        };
        KEYS[] elementKeys = switch(i) {
            case 0 -> new KEYS[]
                {KEYS.XPOS_0, KEYS.YPOS_0, KEYS.WIDTH_0, KEYS.HEIGHT_0, KEYS.PATH_0};
            case 1 -> new KEYS[]
                {KEYS.XPOS_1, KEYS.YPOS_1, KEYS.WIDTH_1, KEYS.HEIGHT_1, KEYS.PATH_1};
            case 2 -> new KEYS[]
                {KEYS.XPOS_2, KEYS.YPOS_2, KEYS.WIDTH_2, KEYS.HEIGHT_2, KEYS.PATH_2};
            default -> keys;
        };
        for(int k = 0; k < elementKeys.length-1; k++)
            vtuberWriter.put(section, elementKeys[k], sheetInfo.getInt(keys[k]));
        vtuberWriter.put(section, elementKeys[4], sheetInfo.getString(keys[4]));
    }

    /**
     * @todo Modificar {@link #saveVTuber} para despegar la lógica
     *       correspondiente a el controlador {@link LayersController}.
     *
     * @param vtuberWriter Objeto {@link VTuberWriter} ya iniciado que
     *                     encuentra el archivo y coloca las
     *                     configuraciones.
     * @param layersController El controlador del que se obtiene la
     *                         información.
     */
    private static void receiveLayerInfo(
            VTuberWriter vtuberWriter,  LayersController layersController
        ) {


    }

    public static VTuberWriter getVTuberWriter(
            LayersController layersController, SheetController sheetController
        ) {

        VTuberWriter vtuberWriter = new VTuberWriter();

        receiveSheetInfo(vtuberWriter, sheetController);

        Map<Ids, Info> optionsInfos = layersController.getInfos();
        Map<Ids, Info[]> layers_infos = sheetController.getInfoMap();

        for(Map.Entry<Ids, Info[]> entry: layers_infos.entrySet()) {
            Ids id = entry.getKey();
            Sections section = id.getEquivalent();

            Info optionInfo = optionsInfos.get(id);
            switch(id) {
                case BACKGROUND:
                    vtuberWriter.put(section, KEYS.IMAGE, optionInfo.getBoolean(KEYS.IMAGE));
                    vtuberWriter.put(section, KEYS.USECOLOR, optionInfo.getBoolean(KEYS.USECOLOR));
                    if(optionInfo.getBoolean(KEYS.IMAGE))
                        receiveImageInfo(vtuberWriter, sheetController, id, section, 0);
                    if(optionInfo.getBoolean(KEYS.USECOLOR))
                        vtuberWriter.put(section, KEYS.COLOR, optionInfo.getString(KEYS.COLOR));
                break;

                case BODY:
                    receiveImageInfo(vtuberWriter, sheetController, id, section, 0);
                break;

                case EYES:
                    receiveImageInfo(vtuberWriter, sheetController, id, section, 0);
                    vtuberWriter.put(section, KEYS.USE, optionInfo.getBoolean(KEYS.USE));
                    if(!optionInfo.getBoolean(KEYS.USE)) continue;
                    vtuberWriter.put(section, KEYS.TIMETO, optionInfo.getInt(KEYS.TIMETO));
                    vtuberWriter.put(section, KEYS.TIMEBLINK, optionInfo.getInt(KEYS.TIMEBLINK));
                    receiveImageInfo(vtuberWriter, sheetController, id, section, 1);
                break;

                case MOUTH:
                    receiveImageInfo(vtuberWriter, sheetController, id, section, 0);
                    vtuberWriter.put(section, KEYS.USE, optionInfo.getBoolean(KEYS.USE));
                    if(!optionInfo.getBoolean(KEYS.USE)) continue;
                    vtuberWriter.put(section, KEYS.CHNLS, optionInfo.getInt(KEYS.CHNLS));
                    vtuberWriter.put(section, KEYS.UPS, optionInfo.getInt(KEYS.UPS));
                    vtuberWriter.put(section, KEYS.SENS, optionInfo.getInt(KEYS.SENS));
                    receiveImageInfo(vtuberWriter, sheetController, id, section, 1);
                break;

                case TABLE:
                    vtuberWriter.put(section, KEYS.USE, optionInfo.getBoolean(KEYS.USE));
                    if(!optionInfo.getBoolean(KEYS.USE)) continue;
                    receiveImageInfo(vtuberWriter, sheetController, id, section, 0);
                break;

                case KEYBOARD:
                    receiveImageInfo(vtuberWriter, sheetController, id, section, 0);
                    receiveImageInfo(vtuberWriter, sheetController, id, section, 1);
                    vtuberWriter.put(section, KEYS.USE, optionInfo.getBoolean(KEYS.USE));
                    if(!optionInfo.getBoolean(KEYS.USE)) continue;
                    receiveImageInfo(vtuberWriter, sheetController, id, section, 2);
                break;

                case MOUSE:
                    vtuberWriter.put(section, KEYS.USE, optionInfo.getBoolean(KEYS.IMAGE));
                    receiveMouseAreaInfo(vtuberWriter, sheetController);
                break;
            }
        }
        return vtuberWriter;
    }

    /**
     * Método estático que debe ser llamado para guardar las
     * configuraciones del modelo vtuber.
     *
     * @param file Archivo en donde se guardarán las configuracions del
     *             vtuber.
     * @param layersController El controlador de las cofiguraciones de
     *                         los parámetros de donde se obtienen los
     *                         distintos parámetros.
     * @param sheetController El controlador del que se obtiene la
     *                        información de la hoja y los gráficos
     *                        relativos a las imágenes.
     *
     * @return Un booleano que indica si algún dato de la configuración
     *         tiene un error grave que no se puede corregir
     *         automáticamente.
     */
    public static boolean saveVTuber(
            File file, LayersController layersController, SheetController sheetController
        ) {

        if(!file.getName().toLowerCase().endsWith(".sav"))
            file = new File(file.getParentFile(), file.getName() + ".sav");
        try {
            file.createNewFile();
        } catch (IOException e) {
            Constants.printTimeStamp(System.err);
            System.out.println(LogMessage.MODEL_SAVE_X.get());
            System.err.println(LogMessage.MODEL_SAVE_X.get());
            return false;
        }

        VTuberWriter vtuberWriter = getVTuberWriter(
            layersController, sheetController
        );

        try {
            vtuberWriter.saveToFile(file.getPath());
        } catch (IOException e) {
            Constants.printTimeStamp(System.err);
            System.err.println(LogMessage.MODEL_SAVE_X.get());
            return false;
        }

        System.out.println(LogMessage.MODEL_SAVE_O.get());
        return true;
    }
}
