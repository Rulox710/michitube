/*
 * Copyright 2025 Raúl N. Valdés
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *  http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package app.maker.controllers;

import app.Ids;
import app.Sections.KEYS;
import app.files.PropertiesM;
import app.files.TranslationM;
import app.maker.controllers.components.MouseComponentArea;
import app.maker.controllers.components.DraggableResizableImageView;
import app.maker.controllers.objects.Infos.Info;
import app.maker.controllers.objects.Objects.Delta;
import app.maker.controllers.objects.Objects.DirectionLock;
import app.maker.controllers.objects.builders.SheetInfoBuilder;

import java.io.File;
import java.net.URI;
import java.nio.file.Path;
import java.nio.file.Paths;
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
                              LAYER_TABLE = new DraggableResizableImageView[1],
                              LAYER_HAIR = new DraggableResizableImageView[1],
                              LAYER_EYES = new DraggableResizableImageView[2],
                              LAYER_MOUTH = new DraggableResizableImageView[2],
                              LAYER_KEYBOARD = new DraggableResizableImageView[3],
                              LAYER_MOUSE = new DraggableResizableImageView[0],
                              LAYER_EXTRA = new DraggableResizableImageView[1];
    private final int LAYERS_SIZE = LAYER_BACKGROUND.length + LAYER_BODY.length + LAYER_EYES.length +
                                    LAYER_MOUTH.length + LAYER_TABLE.length + LAYER_KEYBOARD.length +
                                    LAYER_MOUSE.length + LAYER_EXTRA.length + LAYER_HAIR.length;
    private final Map<Ids, DraggableResizableImageView[]> LAYERS_MAP =  new HashMap<>(Ids.values().length);

    private MouseComponentArea mouseArea;

    private Ids currentLayer = Ids.BACKGROUND, lastLayer;
    private int currentTweak = 0, lastTweak = 0;
    private Region imageHandle;

    public SheetController() {
        LAYERS_MAP.put(Ids.BACKGROUND, LAYER_BACKGROUND);
        LAYERS_MAP.put(Ids.BODY, LAYER_BODY);
        LAYERS_MAP.put(Ids.TABLE, LAYER_TABLE);
        LAYERS_MAP.put(Ids.HAIR, LAYER_HAIR);
        LAYERS_MAP.put(Ids.EYES, LAYER_EYES);
        LAYERS_MAP.put(Ids.MOUTH, LAYER_MOUTH);
        LAYERS_MAP.put(Ids.KEYBOARD, LAYER_KEYBOARD);
        LAYERS_MAP.put(Ids.MOUSE, LAYER_MOUSE);
        LAYERS_MAP.put(Ids.EXTRA, LAYER_EXTRA);
    }

    public void resetImageHandle() {
        Platform.runLater(() -> {
            paneSheet.getChildren().remove(paneSheet.getChildren().size() - 1);
            paneSheet.getChildren().add(new Pane());
        });
    }

    private void setNewImageHandle() {
        Platform.runLater(() -> {
            paneSheet.getChildren().remove(paneSheet.getChildren().size() - 1);
            try {
                imageHandle = LAYERS_MAP.get(currentLayer)[currentTweak].getHandle();
                paneSheet.getChildren().add(imageHandle);
            } catch(NullPointerException e) {
                paneSheet.getChildren().add(new Pane());
            }
        });
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

        switch(currentLayer) {
            case MOUSE:
                mouseArea.setFocus(true, currentTweak == 1, currentTweak == 2);
                resetImageHandle();
            break;
            default:
                mouseArea.setFocus(false);
                setNewImageHandle();
            break;
        }
    }

    public void setImage(File imageFile) {
        if(imageFile == null) {
            LAYERS_MAP.get(currentLayer)[currentTweak] = null;
            return;
        }
        if(!imageFile.exists()) return;

        try{
            LAYERS_MAP.get(currentLayer)[currentTweak] =
                new DraggableResizableImageView(
                    imageFile, null, paneSheet.getWidth(), paneSheet.getHeight()
                );
        } catch(Exception e) {
            LAYERS_MAP.get(currentLayer)[currentTweak] = null;
            return;
        }
        LAYERS_MAP.get(currentLayer)[currentTweak].setFocus(true);
        Pane image = LAYERS_MAP.get(currentLayer)[currentTweak];

        int index = getIndex(currentLayer, currentTweak);
        Platform.runLater(() -> {
            paneSheet.getChildren().remove(index);
            paneSheet.getChildren().add(index, image);
        });
        setNewImageHandle();
    }

    private boolean setImage(Ids layer, int tweak, String image, String rleString, int xPos, int yPos, int width, int height) {
        Path basePath = Paths.get(PropertiesM.getAppProperty("default_dir"));
        Path relativePath = Paths.get(image);
        Path fullPath = basePath.resolve(relativePath);
        URI fullUri = fullPath.toUri();
        File imageFile = new File(fullUri);

        try {
            LAYERS_MAP.get(layer)[tweak] =
                new DraggableResizableImageView(
                    imageFile, rleString, xPos, yPos, width, height
                );
        } catch(Exception e) {
            LAYERS_MAP.get(currentLayer)[currentTweak] = null;
            return false;
        }

        if(layer == currentLayer && tweak == currentTweak)
            LAYERS_MAP.get(layer)[tweak].setFocus(true);
        else LAYERS_MAP.get(layer)[tweak].setFocus(false);
        Pane imagePane = LAYERS_MAP.get(layer)[tweak];

        int index = getIndex(layer, tweak);
        Platform.runLater(() -> {
            paneSheet.getChildren().remove(index);
            paneSheet.getChildren().add(index, imagePane);
        });
        if(layer == currentLayer && tweak == currentTweak) setNewImageHandle();

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

        mouseArea = new MouseComponentArea(
            MIN_SIZE, MIN_SIZE, paneSheet.getPrefHeight(), paneSheet.getPrefWidth()
        );
        mouseArea.setFocus(false);
        children.add(mouseArea);
        children.add(new Pane());

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
        paneSheet.setPrefWidth(sheetInfo.getInt(KEYS.WIDTH));
        paneSheet.setPrefHeight(sheetInfo.getInt(KEYS.HEIGHT));
        resizeHandle.setLayoutX(sheetInfo.getInt(KEYS.WIDTH));
        resizeHandle.setLayoutY(sheetInfo.getInt(KEYS.HEIGHT));
        clipRect.setWidth(paneSheet.getPrefWidth());
        clipRect.setHeight(paneSheet.getPrefHeight());
        return true;
    }

    public Info getSheetInfo() {
        SheetInfoBuilder builder = new SheetInfoBuilder();
        builder.setWidth((int) Math.round(paneSheet.getPrefWidth()));
        builder.setHeight((int) Math.round(paneSheet.getPrefHeight()));
        return builder.getResult();
    }

    public boolean setMouseAreaInfo(Info areaInfo) {
        ObservableList<Node> children = paneSheet.getChildren();
        Platform.runLater(() -> {
            children.remove(mouseArea);
            children.remove(children.size() -1);

            double[] xProperties = {areaInfo.getInt(KEYS.XPOS_A),
                                    areaInfo.getInt(KEYS.XPOS_B),
                                    areaInfo.getInt(KEYS.XPOS_C),
                                    areaInfo.getInt(KEYS.XPOS_D)},
                     yProperties = {areaInfo.getInt(KEYS.YPOS_A),
                                    areaInfo.getInt(KEYS.YPOS_B),
                                    areaInfo.getInt(KEYS.YPOS_C),
                                    areaInfo.getInt(KEYS.YPOS_D)};
            mouseArea = new MouseComponentArea(
                areaInfo.getInt(KEYS.XPOS), areaInfo.getInt(KEYS.YPOS),
                areaInfo.getInt(KEYS.WIDTH), areaInfo.getInt(KEYS.HEIGHT),
                xProperties, yProperties
            );
            children.add(mouseArea);
            children.add(new Pane());

            switch(currentLayer) {
                case MOUSE:
                    mouseArea.setFocus(true, currentTweak == 1, currentTweak == 2);
                    resetImageHandle();
                break;
                default:
                    mouseArea.setFocus(false);
                    setNewImageHandle();
                break;
            }
        });
        return true;
    }

    public Info getMouseAreaInfo() {
        return mouseArea.getAreaInfo();
    }

    public boolean setInfoImage(Ids layer, int tweak, Info infoImage) {
        return setImage(
            layer, tweak,
            infoImage.getString(KEYS.PATH), infoImage.getString(KEYS.RLE),
            infoImage.getInt(KEYS.XPOS), infoImage.getInt(KEYS.YPOS),
            infoImage.getInt(KEYS.WIDTH), infoImage.getInt(KEYS.HEIGHT)
        );
    }

    public boolean deleteImageInfo(Ids id, int tweak) {
        LAYERS_MAP.get(id)[tweak] = null;

        int index = getIndex(id, tweak);
        Platform.runLater(() -> {
            paneSheet.getChildren().remove(index);
            paneSheet.getChildren().add(index, new Pane());
        });

        return true;
    }

    public Map<Ids, Info[]> getInfoMap() {
        Map<Ids, Info[]> INFO_MAP = new HashMap<>(Ids.values().length);
        for(Map.Entry<Ids, DraggableResizableImageView[]> entry: LAYERS_MAP.entrySet()) {
            Ids key = entry.getKey();
            DraggableResizableImageView[] views = entry.getValue();

            Info[] infos = new Info[views.length];
            for(int i = 0; i < views.length; i++) {
                DraggableResizableImageView view = views[i];
                infos[i] = view != null? view.getImageInfo(): null;
            }

            INFO_MAP.put(key, infos);
        }
        return INFO_MAP;
    }

    public boolean readyToSave() {
        return true;
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
            case (char)7:
            case (char)8:
                Ids layer = Ids.values()[(int)event];
                selectLayer(layer, (int)data);
            break;

            case 'i': setImage((File)data);
            break;

            case 'C':
                paneSheet.setBackground(new Background(new BackgroundFill(
                    (Color)data, CornerRadii.EMPTY, Insets.EMPTY
                )));
            break;

            case 'H':
                mouseArea.setHandColor((Color)data);
            break;
        }
    }
}
