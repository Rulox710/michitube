package app.maker.controllers;

import app.Ids;
import app.files.TranslationM;
import app.maker.controllers.components.DraggableResizableArea;
import app.maker.controllers.components.DraggableResizableImageView;
import app.maker.controllers.objects.Infos.Info;
import app.maker.controllers.objects.Objects.Delta;
import app.maker.controllers.objects.Objects.DirectionLock;
import app.maker.controllers.objects.builders.SheetInfoBuilder;

import java.io.File;
import java.net.URI;
import java.util.HashMap;
import java.util.Map;

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

    protected final int MIN_SIZE = 100;

    private final DraggableResizableImageView[] LAYER_BACKGROUND = new DraggableResizableImageView[1],
                              LAYER_BODY = new DraggableResizableImageView[1],
                              LAYER_EYES = new DraggableResizableImageView[2],
                              LAYER_MOUTH = new DraggableResizableImageView[2],
                              LAYER_TABLE = new DraggableResizableImageView[1],
                              LAYER_KEYBOARD = new DraggableResizableImageView[2],
                              LAYER_MOUSE = new DraggableResizableImageView[1];
    private final int LAYERS_SIZE = LAYER_BACKGROUND.length + LAYER_BODY.length + LAYER_EYES.length +
                                    LAYER_MOUTH.length + LAYER_TABLE.length + LAYER_KEYBOARD.length +
                                    LAYER_MOUSE.length;
    private final Map<Ids, DraggableResizableImageView[]> LAYERS_MAP =  new HashMap<>(Ids.values().length);

    private DraggableResizableArea mouseArea;

    private Ids currentLayer = Ids.BACKGROUND, lastLayer;
    private int currentTweak = 0, lastTweak;

    public SheetController() {
        LAYERS_MAP.put(Ids.BACKGROUND, LAYER_BACKGROUND);
        LAYERS_MAP.put(Ids.BODY, LAYER_BODY);
        LAYERS_MAP.put(Ids.EYES, LAYER_EYES);
        LAYERS_MAP.put(Ids.MOUTH, LAYER_MOUTH);
        LAYERS_MAP.put(Ids.TABLE, LAYER_TABLE);
        LAYERS_MAP.put(Ids.KEYBOARD, LAYER_KEYBOARD);
        LAYERS_MAP.put(Ids.MOUSE, LAYER_MOUSE);
    }

    public void selectLayer(Ids layerID, int tweakID) {
        lastLayer = currentLayer;
        lastTweak = currentTweak;
        currentLayer = layerID;
        currentTweak = tweakID;

        try {
            LAYERS_MAP.get(lastLayer)[lastTweak].setFocus(false);
        } catch(Exception e) {}
        try {
            LAYERS_MAP.get(currentLayer)[currentTweak].setFocus(true);
        } catch(Exception e) {}

        if(lastLayer == Ids.MOUSE && currentLayer != Ids.MOUSE)
            mouseArea.setFocus(false);
        if(currentLayer == Ids.MOUSE)
            mouseArea.setFocus(true);
    }

    public void setImage(File imageFile) {
        if (imageFile == null || !imageFile.exists()) return;

        LAYERS_MAP.get(currentLayer)[currentTweak] = new DraggableResizableImageView(imageFile, paneSheet.getWidth(), paneSheet.getHeight());
        LAYERS_MAP.get(currentLayer)[currentTweak].setFocus(true);
        Pane image = LAYERS_MAP.get(currentLayer)[currentTweak];
        Ids a = currentLayer;
        int b = currentTweak;
        Platform.runLater(() -> {
            paneSheet.getChildren().remove(getIndex(a, b));
            paneSheet.getChildren().add(getIndex(a, b), image);
        });
    }

    private boolean setImage(Ids layer, int tweak, String imageUri, int xPos, int yPos, int width, int height) {
        File imageFile;
        try {
            imageFile = new File(URI.create(imageUri));
            if(imageFile == null || !imageFile.exists()) return false;
        } catch(IllegalArgumentException e) {
            return false;
        }

        LAYERS_MAP.get(layer)[tweak] = new DraggableResizableImageView(imageFile, xPos, yPos, width, height);
        if(layer == currentLayer && tweak == currentTweak)
           LAYERS_MAP.get(layer)[tweak].setFocus(true);
        else LAYERS_MAP.get(layer)[tweak].setFocus(false);
        Pane image = LAYERS_MAP.get(layer)[tweak];
        Ids a = layer;
        int b = tweak;
        Platform.runLater(() -> {
            paneSheet.getChildren().remove(getIndex(a, b));
            paneSheet.getChildren().add(getIndex(a, b), image);
        });

        return true;
    }

    private int getIndex(Ids layer, int tweak) {
        int sum = -1;
        for(Ids id: Ids.values()) {
            if(id == layer) break;
            sum += LAYERS_MAP.get(id).length;
        }
        return sum + LAYERS_MAP.get(layer).length - tweak;
    }

    @Override
    public void initialize() {
        ObservableList<Node> children = paneSheet.getChildren();
        while(children.size() < LAYERS_SIZE) {
            children.add(new Pane());
        }

        paneSheet.setBackground(new Background(new BackgroundFill(
            new Color(0.8274509906768799, 0.8274509906768799, 0.8274509906768799, 1),
            CornerRadii.EMPTY,
            Insets.EMPTY
        )));

        enableResizeHandle();

        mouseArea = new DraggableResizableArea(
            MIN_SIZE, MIN_SIZE, paneSheet.getPrefHeight(), paneSheet.getPrefWidth()
        );
        mouseArea.setFocus(false);
        children.add(mouseArea);

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

                default: return;
            }
            clipRect.setWidth(paneSheet.getPrefWidth());
            clipRect.setHeight(paneSheet.getPrefHeight());
        });
    }

    public boolean setSheetInfo(Info sheetInfo) {
        paneSheet.setPrefWidth(sheetInfo.width);
        paneSheet.setPrefHeight(sheetInfo.height);
        resizeHandle.setLayoutX(sheetInfo.width);
        resizeHandle.setLayoutY(sheetInfo.height);
        clipRect.setWidth(paneSheet.getPrefWidth());
        clipRect.setHeight(paneSheet.getPrefHeight());

        // if(hex.equals("")) {
        //     return false;
        // }
        // double r = (double)(Integer.valueOf(hex.substring(1, 3), 16)) / 255;
        // double g = (double)(Integer.valueOf(hex.substring(3, 5), 16)) / 255;
        // double b = (double)(Integer.valueOf(hex.substring(5, 7), 16)) / 255;
        // double a = (double)(Integer.valueOf(hex.substring(7, 9), 16)) / 255;
        // Color color = new Color(r, g, b, a);
        return true;
    }

    public Info getSheetInfo() {
        SheetInfoBuilder builder = new SheetInfoBuilder();
        builder.setWidth((int) Math.round(paneSheet.getPrefWidth()));
        builder.setHeight((int) Math.round(paneSheet.getPrefHeight()));
        return builder.getResult();
    }

    public boolean setInfoImage(Ids layer, int tweak, Info infoImage) {
        return setImage(layer, tweak, infoImage.path[0], infoImage.x, infoImage.y, infoImage.width, infoImage.height);
    }

    public Map<Ids, Info[]> getInfoMap() {
        final Map<Ids, Info[]> INFO_MAP = new HashMap<>(Ids.values().length);
        for(Map.Entry<Ids, DraggableResizableImageView[]> entry: LAYERS_MAP.entrySet()) {
            Ids key = entry.getKey();
            DraggableResizableImageView[] views = entry.getValue();

            Info[] infos = new Info[views.length];
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
                Ids layer = Ids.values()[(int)event];
                selectLayer(layer, (int)data);
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
