package app.maker.controllers;

import java.io.File;
import java.util.Arrays;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.Map;

import app.engine.readers.TranslationM;
import app.maker.controllers.components.DraggableResizableImageView;
import app.maker.controllers.objects.Objects.Delta;
import app.maker.controllers.objects.Objects.DirectionLock;
import app.maker.controllers.objects.Objects.ImageInfo;
import app.maker.controllers.objects.Objects.SheetInfo;
import javafx.application.Platform;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.geometry.Insets;
import javafx.scene.Node;
import javafx.scene.control.Tooltip;
import javafx.scene.layout.Background;
import javafx.scene.layout.BackgroundFill;
import javafx.scene.layout.CornerRadii;
import javafx.scene.layout.Pane;
import javafx.scene.layout.Region;
import javafx.scene.shape.Rectangle;
import javafx.scene.paint.Color;

public class SheetController extends AbstractController {

    @FXML protected Pane sheetRoot, paneSheet;
    @FXML private Region resizeHandle;
    @FXML private Rectangle clipRect;

    protected int MIN_SIZE = 100;

    private double startX, startY;
    private double startWidth, startHeight;

    private final DraggableResizableImageView[] LAYER_BACKGROUND = new DraggableResizableImageView[1],
                              LAYER_BODY = new DraggableResizableImageView[1],
                              LAYER_EYES = new DraggableResizableImageView[2],
                              LAYER_MOUTH = new DraggableResizableImageView[2],
                              LAYER_TABLE = new DraggableResizableImageView[1],
                              LAYER_KEYBOARD = new DraggableResizableImageView[2],
                              LAYER_MOUSE = new DraggableResizableImageView[2];
    private final int[] layerSizes = { LAYER_BACKGROUND.length,
                         LAYER_BODY.length,
                         LAYER_EYES.length,
                         LAYER_MOUTH.length,
                         LAYER_TABLE.length,
                         LAYER_KEYBOARD.length,
                         LAYER_MOUSE.length };
    private final String[] IDS = {"background", "body", "eyes", "mouth", "table", "keyboard", "mouse"};
    private final Map<String, DraggableResizableImageView[]> LAYERS_MAP =  new HashMap<>(IDS.length);

    private int currentLayer = 0, lastLayer;
    private int currentTweak = 0, lastTweak;

    public SheetController() {
        LAYERS_MAP.put(IDS[0], LAYER_BACKGROUND);
        LAYERS_MAP.put(IDS[1], LAYER_BODY);
        LAYERS_MAP.put(IDS[2], LAYER_EYES);
        LAYERS_MAP.put(IDS[3], LAYER_MOUTH);
        LAYERS_MAP.put(IDS[4], LAYER_TABLE);
        LAYERS_MAP.put(IDS[5], LAYER_KEYBOARD);
        LAYERS_MAP.put(IDS[6], LAYER_MOUSE);
    }

    public void selectLayer(int layerID, int tweakID) {
        lastLayer = currentLayer;
        lastTweak = currentTweak;
        currentLayer = layerID;
        currentTweak = tweakID;

        try {
            LAYERS_MAP.get(IDS[lastLayer])[lastTweak].setFocus(false);
        } catch(Exception e) {}
        try {
            LAYERS_MAP.get(IDS[currentLayer])[currentTweak].setFocus(true);
        } catch(Exception e) {}
    }

    public void setImage(File imageFile) {
        if (imageFile == null || !imageFile.exists()) return;

        LAYERS_MAP.get(IDS[currentLayer])[currentTweak] = new DraggableResizableImageView(imageFile, paneSheet.getWidth(), paneSheet.getHeight());
        LAYERS_MAP.get(IDS[currentLayer])[currentTweak].setFocus(true);
        Pane image = LAYERS_MAP.get(IDS[currentLayer])[currentTweak];
        int a = currentLayer;
        int b = currentTweak;
        Platform.runLater(() -> {
            paneSheet.getChildren().remove(getIndex(a, b));
            paneSheet.getChildren().add(getIndex(a, b), image);
        });
    }

    private int getIndex(int layer, int tweak) {
        int index = 0;
        for(int i = 0; i < layer; i++) index += layerSizes[i];
        return index + tweak;
    }

    @Override
    public void initialize() {
        ObservableList<Node> children = paneSheet.getChildren();
        while(children.size() < Arrays.stream(layerSizes).sum()) {
            children.add(new Pane());
        }
        enableResizeHandle();

        updateLanguage();
    }

    private void enableResizeHandle() {
        final Delta startDelta = new Delta(), panelSize = new Delta(), dragDelta = new Delta();
        final DirectionLock directionLock = new DirectionLock();

        resizeHandle.setOnMousePressed(event -> {
            startDelta.x = event.getSceneX();
            startDelta.y = event.getSceneY();
            panelSize.x = paneSheet.getPrefWidth();
            panelSize.y = paneSheet.getPrefHeight();
            directionLock.reset();
        });

        resizeHandle.setOnMouseDragged(event -> {
            double newWidth, newHeight;
            switch(event.getButton()) {
                case PRIMARY:
                    dragDelta.x = event.getSceneX() - startDelta.x;
                    dragDelta.y = event.getSceneY() - startDelta.y;

                    newWidth = Math.max(MIN_SIZE, panelSize.x + dragDelta.x);
                    newHeight = Math.max(MIN_SIZE, panelSize.y + dragDelta.y);

                    paneSheet.setPrefWidth(newWidth);
                    paneSheet.setPrefHeight(newHeight);
                    resizeHandle.setLayoutX(newWidth);
                    resizeHandle.setLayoutY(newHeight);
                break;
                case SECONDARY:
                    dragDelta.x = event.getSceneX() - startDelta.x;
                    dragDelta.y = event.getSceneY() - startDelta.y;
                    if (!directionLock.locked) {
                        if(dragDelta.x > 0.77) directionLock.horizontal = true;
                        if(dragDelta.y > 0.77) directionLock.vertical = true;
                        directionLock.locked = true;
                    }

                    double newSize;
                    if (directionLock.horizontal) {
                        newSize = Math.max(MIN_SIZE, panelSize.x + dragDelta.x);
                        paneSheet.setPrefWidth(newSize);
                    resizeHandle.setLayoutX(newSize);
                    } else if (directionLock.vertical) {
                        newSize = Math.max(MIN_SIZE, panelSize.y + dragDelta.y);
                        paneSheet.setPrefHeight(newSize);
                        resizeHandle.setLayoutY(newSize);
                    }

                break;
            }
            clipRect.setWidth(paneSheet.getPrefWidth());
            clipRect.setHeight(paneSheet.getPrefHeight());
        });
    }

    public SheetInfo getSheetInfo() {
        final SheetInfo INFO = new SheetInfo();
        INFO.width = (int) Math.round(paneSheet.getPrefWidth());
        INFO.height = (int) Math.round(paneSheet.getPrefHeight());
        Background background = paneSheet.getBackground();
        Color fxColor = (Color) background.getFills().get(0).getFill();
        String hexWithAlpha = String.format("#%02x%02x%02x%02x",
            (int) Math.round(fxColor.getRed() * 255),
            (int) Math.round(fxColor.getGreen() * 255),
            (int) Math.round(fxColor.getBlue() * 255),
            (int) Math.round(fxColor.getOpacity() * 255)
        );
        INFO.color = hexWithAlpha;
        return INFO;
    }

    public Map<String, ImageInfo[]> getInfoMap() {
        final Map<String, ImageInfo[]> INFO_MAP = new HashMap<>(IDS.length);
        for(Map.Entry<String, DraggableResizableImageView[]> entry: LAYERS_MAP.entrySet()) {
            String key = entry.getKey();
            DraggableResizableImageView[] views = entry.getValue();

            ImageInfo[] infos = new ImageInfo[views.length];
            for (int i = 0; i < views.length; i++) {
                DraggableResizableImageView view = views[i];
                infos[i] = (view != null)? view.getImageInfo(): null;
            }

            INFO_MAP.put(key, infos);
        }
        return INFO_MAP;
    }

    @Override
    public void updateLanguage() {
        Tooltip.install(resizeHandle, new Tooltip(TranslationM.getTranslatedLabel("tooltip_resize")));
    }

    @Override
    public void update(char event, Object data) {
        switch(event) {
            case (char)0:
            case (char)1:
            case (char)2:
            case (char)3:
            case (char)4:
            case (char)5:
            case (char)6:
                selectLayer((int)event, (int)data);
                break;
            case 'i': setImage((File)data);
            break;
            case 'C':
                paneSheet.setBackground(new Background(new BackgroundFill(
                    (Color)data,
                    CornerRadii.EMPTY,
                    Insets.EMPTY
                )));
            break;
        }
    }
}
