package app.files;

import app.Ids;
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
        vtuberWriter.put(Sections.SHEET.getKEY(), KEYS.WIDTH.getKEY(),sheetInfo.width);
        vtuberWriter.put(Sections.SHEET.getKEY(), KEYS.HEIGHT.getKEY(),sheetInfo.height);
    }

    /**
     * Método estático que recibe la información sobre las disitintas
     * imágenes empleadas en el controlador {@link SheetController}
     * según qué capa y ajuste se indique..
     *
     * @param vtuberWriter Objeto {@link VTuberWriter} ya iniciado que
     *                     encuentra el archivo y coloca las
     *                     configuraciones según distintas claves.
     * @param sheetController El controlador del que se recibe la
     *                        información.
     */
    private static void receiveImageInfo(
            VTuberWriter vtuberWriter, SheetController sheetController,
            Ids section, int i
        ) {

        Info sheetInfo = sheetController.getInfoMap().get(section)[i];

        vtuberWriter.put(section.getID(), KEYS.XPOS.getFormatKEY(i), sheetInfo.x);
        vtuberWriter.put(section.getID(), KEYS.YPOS.getFormatKEY(i), sheetInfo.y);
        vtuberWriter.put(section.getID(), KEYS.WIDTH.getFormatKEY(i), sheetInfo.width);
        vtuberWriter.put(section.getID(), KEYS.HEIGHT.getFormatKEY(i), sheetInfo.height);
        vtuberWriter.put(section.getID(), KEYS.PATH.getFormatKEY(i), sheetInfo.path[0]);
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
            return false;
        }

        VTuberWriter vtuberWriter = new VTuberWriter();

        receiveSheetInfo(vtuberWriter, sheetController);

        Map<Ids, Info> optionsInfos = layersController.getInfos();
        Map<Ids, Info[]> layers_infos = sheetController.getInfoMap();

        for(Map.Entry<Ids, Info[]> entry: layers_infos.entrySet()) {
            Ids section = entry.getKey();

            Info optionInfo = optionsInfos.get(section);
            switch(section) {
                case BACKGROUND:
                    vtuberWriter.put(section.getID(), KEYS.IMAGE.getKEY(), optionInfo.boolParams[0]);
                    vtuberWriter.put(section.getID(), KEYS.USECOLOR.getKEY(), optionInfo.boolParams[1]);
                    if(optionInfo.boolParams[0])
                        receiveImageInfo(vtuberWriter, sheetController, section, 0);
                    if(optionInfo.boolParams[1]) {
                        vtuberWriter.put(section.getID(), KEYS.COLOR.getKEY(), optionInfo.color);
                    }
                break;

                case BODY:
                    receiveImageInfo(vtuberWriter, sheetController, section, 0);
                break;

                case EYES:
                    receiveImageInfo(vtuberWriter, sheetController, section, 0);
                    vtuberWriter.put(section.getID(), KEYS.USE.getKEY(), optionInfo.boolParams[0]);
                    if(!optionInfo.boolParams[0]) continue;
                    vtuberWriter.put(section.getID(), KEYS.TIMETO.getKEY(), optionInfo.intParams[0]);
                    vtuberWriter.put(section.getID(), KEYS.TIMEBLINK.getKEY(), optionInfo.intParams[1]);
                    receiveImageInfo(vtuberWriter, sheetController, section, 1);
                break;

                case MOUTH:
                    receiveImageInfo(vtuberWriter, sheetController, section, 0);
                    vtuberWriter.put(section.getID(), KEYS.USE.getKEY(), optionInfo.boolParams[0]);
                    if(!optionInfo.boolParams[0]) continue;
                    vtuberWriter.put(section.getID(), KEYS.CHNLS.getKEY(), optionInfo.intParams[0]);
                    vtuberWriter.put(section.getID(), KEYS.UPS.getKEY(), optionInfo.intParams[1]);
                    vtuberWriter.put(section.getID(), KEYS.SENS.getKEY(), optionInfo.intParams[2]);
                    receiveImageInfo(vtuberWriter, sheetController, section, 1);
                break;

                case TABLE:
                    vtuberWriter.put(section.getID(), KEYS.USE.getKEY(), optionInfo.boolParams[0]);
                    if(!optionInfo.boolParams[0]) continue;
                    receiveImageInfo(vtuberWriter, sheetController, section, 0);
                break;

                case KEYBOARD:
                    receiveImageInfo(vtuberWriter, sheetController, section, 0);
                    vtuberWriter.put(section.getID(), KEYS.USE.getKEY(), optionInfo.boolParams[0]);
                    if(!optionInfo.boolParams[0]) continue;
                    receiveImageInfo(vtuberWriter, sheetController, section, 1);
                break;

                case MOUSE:
                break;
            }
        }
        try {
            vtuberWriter.saveToFile(file.getPath());
        } catch (IOException e) {
            return false;
        }
        return true;
    }
}
