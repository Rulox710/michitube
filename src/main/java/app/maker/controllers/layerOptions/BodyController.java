package app.maker.controllers.layerOptions;

import app.Sections.KEYS;
import app.files.TranslationM;
import app.maker.FXFileChooser;
import app.maker.controllers.objects.Infos.Info;

import java.io.File;
import java.net.URI;
import java.nio.file.Path;
import java.nio.file.Paths;

import javafx.css.PseudoClass;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.Accordion;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TitledPane;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.HBox;

public class BodyController extends OptionLayerController {

    @FXML private Accordion bodyOptionsRoot;
    @FXML private TitledPane titledPaneImage;
    @FXML private Label labelImage;
    @FXML private Button buttonImage;
    @FXML private ImageView imagePreview;
    @FXML private HBox hboxImage;

    /**
     * {@inheritDoc}
     */
    @Override
    public void initialize() {
        hasError = new boolean[1]; // 0: Image
        updateLanguage();

        optionsRoot = bodyOptionsRoot;
        firstPane = titledPaneImage;
        super.initialize();
    }

    @Override
    protected void handleError(int index, boolean error, boolean notify) {
        index = index%1;
        hasError[index] = error;

        TitledPane currentPane;
        HBox currentHBox;
        switch(index) {
            case 0:
            default:
                currentPane = titledPaneImage;
                currentHBox = hboxImage;
            break;
        }
        currentPane.pseudoClassStateChanged(PseudoClass.getPseudoClass("error"), hasError[index]);
        currentHBox.pseudoClassStateChanged(PseudoClass.getPseudoClass("error"), hasError[index]);

        if(notify) notifyObservers('e', hasError[0]);
    }

    @FXML
    private void handleButtonClick(ActionEvent event) {
        File img = FXFileChooser.getImageChooser().showOpenDialog(null);
        if(img != null) {
            imagePreview.setImage(new Image(img.toURI().toString()));
            notifyObservers((char) getTweakID(), img);

            handleError(0, false, true);
        }
    }

    @Override
    public boolean readyToSave() {
        if(imagePreview.getImage() == null)
            handleError(0, true, true);
        return !hasError[0];
    }

    @Override
    public boolean setInfo(Info info) {
        boolean result = true;
        Path relativePath, fullPath;
        URI fullUri;

        if(info.getString(KEYS.PATH_0).length() != 0) {
            relativePath = Paths.get(info.getString(KEYS.PATH_0));
            fullPath = basePath.resolve(relativePath);
            fullUri = fullPath.toUri();
            imagePreview.setImage(new Image(fullUri.toString()));
            notifyObservers('l', new File(fullUri));
        } else {
            result = false;
            imagePreview.setImage(null);
            notifyObservers('l', null);
        }

        handleError(0, false, true);

        return result;
    }

    @Override
    public void updateLanguage() {
        titledPaneImage.setText(TranslationM.getTranslatedLabel("title_body_image"));
        buttonImage.setText(TranslationM.getTranslatedLabel("button_select_image"));
        labelImage.setText(TranslationM.getTranslatedLabel("label_body_image"));
    }
}
