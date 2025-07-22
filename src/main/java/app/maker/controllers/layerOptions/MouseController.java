package app.maker.controllers.layerOptions;

import java.io.File;

import app.files.TranslationM;
import app.maker.FXFileChooser;
import app.maker.controllers.objects.Infos.Info;
import app.maker.controllers.objects.builders.BasicInfoBuilder;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.Accordion;
import javafx.scene.control.CheckBox;
import javafx.scene.control.TitledPane;
import javafx.scene.control.Tooltip;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;

public class MouseController extends OptionLayerController {

    @FXML private Accordion mouseOptionsRoot;
    @FXML private TitledPane titledPaneArea, titledPaneHand, titledPaneMouse;

    @FXML private ImageView imagePreviewMouse;
    @FXML private CheckBox checkboxMovement;
    @FXML private Tooltip tooltipMovement;

    @Override
    public void initialize() {
        updateLanguage();

        optionsRoot = mouseOptionsRoot;
        firstPane = titledPaneMouse;
        super.initialize();
    }

    @FXML
    private void handleButtonClick(ActionEvent event) {
        File img = FXFileChooser.getImageChooser().showOpenDialog(null);
        if(img != null) {
            imagePreviewMouse.setImage(new Image(img.toURI().toString()));
            notifyObservers((char) getTweakID(), img);
        }
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
        titledPaneArea.setText(TranslationM.getTranslatedLabel("title_area"));
        checkboxMovement.setText(TranslationM.getTranslatedLabel("checkbox_movement"));
        tooltipMovement.setText(TranslationM.getTranslatedLabel("tooltip_movement"));

        titledPaneMouse.setText(TranslationM.getTranslatedLabel("title_mouse"));
    }
}
