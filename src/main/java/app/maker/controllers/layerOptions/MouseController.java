package app.maker.controllers.layerOptions;

import app.files.TranslationM;
import app.maker.controllers.objects.Infos.Info;
import app.maker.controllers.objects.builders.BasicInfoBuilder;

import javafx.fxml.FXML;
import javafx.scene.control.Accordion;
import javafx.scene.control.TitledPane;

public class MouseController extends OptionLayerController {

    @FXML private Accordion mouseOptionsRoot;
    @FXML private TitledPane titledPaneMouse;

    @Override
    public void initialize() {
        updateLanguage();

        optionsRoot = mouseOptionsRoot;
        firstPane = titledPaneMouse;
        super.initialize();
    }

    @Override
    public boolean readyToSave() {
        return true;
    }

    @Override
    public boolean setInfo(Info info) {
        return true;
    }

    @Override
    public Info getInfo() {
        BasicInfoBuilder builder = new BasicInfoBuilder();

        return builder.getResult();
    }

    @Override
    public void updateLanguage() {
        titledPaneMouse.setText(TranslationM.getTranslatedLabel("title_mouse"));
    }
}
