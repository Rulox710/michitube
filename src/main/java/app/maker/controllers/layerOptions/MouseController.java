package app.maker.controllers.layerOptions;

import app.engine.readers.TranslationM;
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
    public void updateLanguage() {
        titledPaneMouse.setText(TranslationM.getTranslatedLabel("title_mouse"));
    }
}
