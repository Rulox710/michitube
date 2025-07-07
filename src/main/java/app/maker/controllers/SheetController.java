package app.maker.controllers;

import javafx.fxml.FXML;
import javafx.scene.layout.Pane;
import javafx.scene.layout.Region;

public class SheetController extends AbstractController {

    @FXML private Pane sheetRoot, paneSheet;
    @FXML private Region resizeHandle;

    private double startX, startY;
    private double startWidth, startHeight;
    
    @Override
    public void initialize() {
        enableResizeHandle();
    }

    private void enableResizeHandle() {
        resizeHandle.setOnMousePressed(event -> {
            startX = event.getSceneX();
            startY = event.getSceneY();
            startWidth = paneSheet.getPrefWidth();
            startHeight = paneSheet.getPrefHeight();
        });

        resizeHandle.setOnMouseDragged(event -> {
            double deltaX = event.getSceneX() - startX;
            double deltaY = event.getSceneY() - startY;

            double newWidth = Math.max(100, startWidth + deltaX);
            double newHeight = Math.max(100, startHeight + deltaY);

            paneSheet.setPrefWidth(newWidth);
            paneSheet.setPrefHeight(newHeight);
            resizeHandle.setLayoutX(newWidth);
            resizeHandle.setLayoutY(newHeight);
        });
    }

    @Override
    public void updateLanguage() {
        // TODO Auto-generated method stub
        throw new UnsupportedOperationException("Unimplemented method 'updateLanguage'");
    }

}
