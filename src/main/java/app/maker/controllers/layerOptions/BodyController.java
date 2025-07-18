package app.maker.controllers.layerOptions;

import app.files.TranslationM;
import app.maker.FXFileChooser;
import app.maker.controllers.objects.Infos.Info;

import java.io.File;
import java.net.URI;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.Accordion;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TitledPane;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;

public class BodyController extends OptionLayerController {

    @FXML private Accordion bodyOptionsRoot;
    @FXML private TitledPane titledPaneImage;
    @FXML private Label labelImage;
    @FXML private Button buttonImage;
    @FXML private ImageView imagePreview;

    @Override
    public void initialize() {
        updateLanguage();

        optionsRoot = bodyOptionsRoot;
        firstPane = titledPaneImage;
        super.initialize();
    }

    @FXML
    private void handleButtonClick(ActionEvent event) {
        File img = FXFileChooser.getImageChooser().showOpenDialog(null);
        if(img != null) {
            imagePreview.setImage(new Image(img.toURI().toString()));
            notifyObservers((char) 0, img);
        }
    }

    @Override
    public boolean readyToSave() {
        return (imagePreview.getImage() != null);
    }

    @Override
    public boolean setInfo(Info info) {
        boolean result = true;

        if(info.path[0].length() != 0) {
            imagePreview.setImage(new Image(info.path[0]));
            notifyObservers('l', new File(URI.create(info.path[0])));
        } else result = false;

        return result;
    }

    @Override
    public void updateLanguage() {
        titledPaneImage.setText(TranslationM.getTranslatedLabel("title_body_image"));
        buttonImage.setText(TranslationM.getTranslatedLabel("button_select_image"));
        labelImage.setText(TranslationM.getTranslatedLabel("label_body_image"));
    }
}
