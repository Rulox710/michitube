package app.maker.controllers.layerOptions;

import java.io.File;

import app.engine.readers.TranslationM;
import app.maker.FXFileChooser;
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
            notifyObservers((char) 0, img);
        }
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
