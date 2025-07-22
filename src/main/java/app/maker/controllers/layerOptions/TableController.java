package app.maker.controllers.layerOptions;

import app.files.TranslationM;
import app.maker.FXFileChooser;
import app.maker.controllers.objects.Infos.Info;
import app.maker.controllers.objects.builders.BasicInfoBuilder;

import java.io.File;
import java.net.URI;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.Accordion;
import javafx.scene.control.Button;
import javafx.scene.control.CheckBox;
import javafx.scene.control.Label;
import javafx.scene.control.TitledPane;
import javafx.scene.control.Tooltip;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.HBox;

public class TableController extends OptionLayerController {

    @FXML private Accordion tableOptionsRoot;
    @FXML private TitledPane titledPaneImage;
    @FXML private Label labelImage;
    @FXML private Button buttonImage;
    @FXML private ImageView imagePreview;
    @FXML private CheckBox checkboxTable;
    @FXML private Tooltip tooltipTable, tooltipBTable;
    @FXML private HBox hboxImage;

    @Override
    public void initialize() {
        updateLanguage();

        optionsRoot = tableOptionsRoot;
        firstPane = titledPaneImage;
        super.initialize();

        updateEnabledState(checkboxTable.isSelected());
        checkboxTable.selectedProperty().addListener((obs, oldVal, newVal) -> {
            updateEnabledState(newVal);
        });
    }

    private void updateEnabledState(boolean enabled) {
        buttonImage.setDisable(!enabled);
        hboxImage.setDisable(!enabled);
        imagePreview.setDisable(!enabled);
    }

    @FXML
    private void handleButtonClick(ActionEvent event) {
        File img = FXFileChooser.getImageChooser().showOpenDialog(null);
        if(img != null) {
            imagePreview.setImage(new Image(img.toURI().toString()));
            notifyObservers((char) getTweakID(), img);
            System.out.print(getTweakID());
        }
    }

    @Override
    public boolean readyToSave() {
        if(checkboxTable.selectedProperty().getValue() && imagePreview.getImage() == null)
            return false;
        return true;
    }

    @Override
    public boolean setInfo(Info info) {
        boolean result = true;

        checkboxTable.selectedProperty().setValue(info.boolParams[0]);
        if(info.boolParams[0] && info.path[0].length() != 0) {
            imagePreview.setImage(new Image(info.path[0]));
            notifyObservers('l', new File(URI.create(info.path[0])));
        } else result = false;

        return result;
    }

    public Info getInfo() {
        BasicInfoBuilder builder = new BasicInfoBuilder();
        builder.setUsage(checkboxTable.selectedProperty().getValue());

        return builder.getResult();
    }

    @Override
    public void updateLanguage() {
        titledPaneImage.setText(TranslationM.getTranslatedLabel("title_table_image"));
        checkboxTable.setText(TranslationM.getTranslatedLabel("checkbox_table"));
        tooltipTable.setText(TranslationM.getTranslatedLabel("tooltip_ctable"));
        buttonImage.setText(TranslationM.getTranslatedLabel("button_select_image"));
        tooltipBTable.setText(TranslationM.getTranslatedLabel("tooltip_btable"));
        labelImage.setText(TranslationM.getTranslatedLabel("label_table_image"));
    }
}
