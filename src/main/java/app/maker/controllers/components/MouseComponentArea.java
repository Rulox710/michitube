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

package app.maker.controllers.components;

import app.maker.controllers.objects.Infos.Info;
import app.maker.controllers.objects.Objects.Delta;
import app.maker.controllers.objects.Objects.DirectionLock;
import app.maker.controllers.objects.builders.MouseInfoBuilder;
import javafx.beans.property.DoubleProperty;
import javafx.beans.property.SimpleDoubleProperty;
import javafx.scene.Cursor;
import javafx.scene.layout.Pane;
import javafx.scene.layout.Region;
import javafx.scene.paint.Color;
import javafx.scene.shape.CubicCurveTo;
import javafx.scene.shape.LineTo;
import javafx.scene.shape.MoveTo;
import javafx.scene.shape.Path;

public class MouseComponentArea extends Pane {

    private int MIN_SIZE = 30, OFFSET = 20, HANDLE_SIZE = 10;

    private Pane area;
    private Region resizeHandle, aPointHandle, bPointHandle, cPointHandle, dPointHandle;
    private DoubleProperty[] xProperties = new DoubleProperty[4],
                             yProperties = new DoubleProperty[4];

    private Path hand;
    private MoveTo aPoint;
    private LineTo bPoint, dPoint;
    private CubicCurveTo curve;
    private Color handColor = Color.LIGHTGRAY;

    private final String FOCUS_STYLE = "-fx-background-color: white; -fx-border-color: black;",
                         DISABLE_STYLE = "-fx-background-color: transparent; -fx-border-color: transparent;";

    public MouseComponentArea(
            double width, double height,
            double maxWidth, double maxHeight
        ) {

        setLayoutX(20);
        setLayoutY(20);
        setPrefSize(
            (width < maxWidth-OFFSET)? width: maxWidth-OFFSET,
            (height < maxHeight-OFFSET)? height: maxHeight-OFFSET
        );

        area = new Pane();
        area.setLayoutX(0);
        area.setLayoutY(0);
        area.prefHeightProperty().bind(prefHeightProperty());
        area.prefWidthProperty().bind(prefWidthProperty());
        area.setCursor(Cursor.MOVE);

        xProperties[0] = new SimpleDoubleProperty(getPrefWidth()-10);
        xProperties[1] = new SimpleDoubleProperty(getPrefWidth()/2-5);
        xProperties[2] = new SimpleDoubleProperty(getPrefWidth()/2+5);
        xProperties[3] = new SimpleDoubleProperty(getPrefWidth()+10);

        yProperties[0] = new SimpleDoubleProperty(-10);
        yProperties[1] = new SimpleDoubleProperty(getPrefHeight()/2-5);
        yProperties[2] = new SimpleDoubleProperty(getPrefHeight()/2+5);
        yProperties[3] = new SimpleDoubleProperty(10);

        createHandPath(xProperties, yProperties);
        createHandles(xProperties, yProperties);

        setupResizeBehavior();
        setupHandlesBehavior();
        setupDragBehavior();

        getChildren().addAll(
            area, resizeHandle, hand,
            aPointHandle, bPointHandle, cPointHandle, dPointHandle
        );
    }

    public MouseComponentArea(
            double xPos, double yPos, double width, double height,
            double[] xProp, double[] yProp
        ) {

        setLayoutX(xPos);
        setLayoutY(yPos);
        setPrefSize(width, height);

        area = new Pane();
        area.setLayoutX(0);
        area.setLayoutY(0);
        area.prefHeightProperty().bind(prefHeightProperty());
        area.prefWidthProperty().bind(prefWidthProperty());
        area.setCursor(Cursor.MOVE);

        for(int i = 0; i < xProperties.length; i++) {
            xProperties[i] = new SimpleDoubleProperty(xProp[i]-HANDLE_SIZE/2);
            yProperties[i] = new SimpleDoubleProperty(yProp[i]-HANDLE_SIZE/2);
        }

        createHandPath(this.xProperties, this.yProperties);
        createHandles(this.xProperties, this.yProperties);

        setupResizeBehavior();
        setupHandlesBehavior();
        setupDragBehavior();

        getChildren().addAll(
            area, resizeHandle, hand,
            aPointHandle, bPointHandle, cPointHandle, dPointHandle
        );
    }

    public void createHandPath(DoubleProperty[] xProperties, DoubleProperty[] yProperties) {
        aPoint = new MoveTo();
        aPoint.xProperty().bind(xProperties[0]);
        aPoint.yProperty().bind(yProperties[0]);

        bPoint = new LineTo();
        bPoint.xProperty().bind(xProperties[1]);
        bPoint.yProperty().bind(yProperties[1]);

        dPoint = new LineTo();
        dPoint.xProperty().bind(xProperties[3]);
        dPoint.yProperty().bind(yProperties[3]);

        curve = new CubicCurveTo();
        curve.controlX1Property().bind(xProperties[1].subtract(10));
        curve.controlY1Property().bind(yProperties[1].add(10));
        curve.controlX2Property().bind(xProperties[2].subtract(10));
        curve.controlY2Property().bind(yProperties[2].add(10));
        curve.xProperty().bind(xProperties[2]);
        curve.yProperty().bind(yProperties[2]);

        hand = new Path();
        hand.getElements().addAll(
            aPoint, bPoint, curve, dPoint
            // new QuadCurveTo(
            //     getPrefWidth()/2-10, getPrefHeight()/2+10 ,
            //     getPrefWidth()/2+5, getPrefHeight()/2+5
            // ),
        );
        hand.setStroke(Color.BLACK);
        hand.setFill(handColor);
    }

    /**
     * Crea componentes que servirán para ajustar el tamaño del
     * área y mover puntos de la mano.
     */
    private void createHandles(DoubleProperty[] xProperties, DoubleProperty[] yProperties) {
        resizeHandle = new Region();
        resizeHandle.setPrefSize(HANDLE_SIZE, HANDLE_SIZE);

        resizeHandle.layoutXProperty().bind(area.prefWidthProperty().subtract(HANDLE_SIZE/2));
        resizeHandle.layoutYProperty().bind(area.prefHeightProperty().subtract(HANDLE_SIZE/2));
        resizeHandle.setCursor(Cursor.SE_RESIZE);

        aPointHandle = new Region();
        aPointHandle.setPrefSize(HANDLE_SIZE, HANDLE_SIZE);
        aPointHandle.layoutXProperty().bind(xProperties[0].subtract(HANDLE_SIZE/2));
        aPointHandle.layoutYProperty().bind(yProperties[0].subtract(HANDLE_SIZE/2));
        aPointHandle.setCursor(Cursor.MOVE);

        bPointHandle = new Region();
        bPointHandle.setPrefSize(HANDLE_SIZE, HANDLE_SIZE);
        bPointHandle.layoutXProperty().bind(xProperties[1].subtract(HANDLE_SIZE/2));
        bPointHandle.layoutYProperty().bind(yProperties[1].subtract(HANDLE_SIZE/2));
        bPointHandle.setCursor(Cursor.MOVE);

        cPointHandle = new Region();
        cPointHandle.setPrefSize(HANDLE_SIZE, HANDLE_SIZE);
        cPointHandle.layoutXProperty().bind(xProperties[2].subtract(HANDLE_SIZE/2));
        cPointHandle.layoutYProperty().bind(yProperties[2].subtract(HANDLE_SIZE/2));
        cPointHandle.setCursor(Cursor.MOVE);

        dPointHandle = new Region();
        dPointHandle.setPrefSize(HANDLE_SIZE, HANDLE_SIZE);
        dPointHandle.layoutXProperty().bind(xProperties[3].subtract(HANDLE_SIZE/2));
        dPointHandle.layoutYProperty().bind(yProperties[3].subtract(HANDLE_SIZE/2));
        dPointHandle.setCursor(Cursor.MOVE);
    }

    private void setupResizeBehavior() {
        final Delta startDelta = new Delta(), panelSize = new Delta(), dragDelta = new Delta();
        final DirectionLock directionLock = new DirectionLock();

        resizeHandle.setOnMousePressed(event -> {
            startDelta.x = event.getSceneX();
            startDelta.y = event.getSceneY();
            panelSize.x = area.getPrefWidth();
            panelSize.y = area.getPrefHeight();
            directionLock.reset();
            event.consume();
        });

        resizeHandle.setOnMouseDragged(event -> {
            double newWidth, newHeight;
            switch(event.getButton()) {
                case PRIMARY:
                    dragDelta.x = event.getSceneX() - startDelta.x;
                    dragDelta.y = event.getSceneY() - startDelta.y;

                    newWidth = Math.max(Math.max(MIN_SIZE, cPointHandle.getLayoutX()+cPointHandle.getPrefWidth()*2), Math.min(dPointHandle.getLayoutX()-dPointHandle.getPrefWidth()/2, panelSize.x + dragDelta.x));
                    newHeight = Math.max(Math.max(MIN_SIZE, cPointHandle.getLayoutY()+cPointHandle.getPrefHeight()*2), panelSize.y + dragDelta.y);

                    setPrefWidth(newWidth);
                    setPrefHeight(newHeight);
                break;

                case SECONDARY:
                    dragDelta.x = event.getSceneX() - startDelta.x;
                    dragDelta.y = event.getSceneY() - startDelta.y;
                    if(!directionLock.locked) {
                        if(dragDelta.x > 0.77) directionLock.horizontal = true;
                        if(dragDelta.y > 0.77) directionLock.vertical = true;
                        directionLock.locked = true;
                    }

                    double newSize;
                    if(directionLock.horizontal) {
                        newSize = Math.max(MIN_SIZE, panelSize.x + dragDelta.x);
                        setPrefWidth(newSize);
                    } else if (directionLock.vertical) {
                        newSize = Math.max(MIN_SIZE, panelSize.y + dragDelta.y);
                        setPrefHeight(newSize);
                    }
                break;

                default: return;
            }
            event.consume();
        });
    }

    private void setupHandlesBehavior() {
        final Region[] handles = {aPointHandle, bPointHandle, cPointHandle, dPointHandle};
        final Delta dragDelta = new Delta();
        final DirectionLock directionLock = new DirectionLock();

        for(int i = 0; i < handles.length; i++) {
            final int index = i;
            handles[index].setOnMousePressed(event -> {
                dragDelta.x = xProperties[index].get() - event.getSceneX();
                dragDelta.y = yProperties[index].get() - event.getSceneY();
                directionLock.reset();
            });

            handles[index].setOnMouseDragged(event -> {
                double maxX = 0, maxY = 0, minX = 0, minY = 0;
                switch(index) {
                    case 0:
                        maxX = bPointHandle.getLayoutX()+bPointHandle.getPrefWidth()*1.5;
                        maxY = Double.NEGATIVE_INFINITY;
                        minX = dPointHandle.getLayoutX()-dPointHandle.getPrefWidth()/2;
                        minY = area.getLayoutY();
                    break;

                    case 1:
                        maxX = area.getLayoutX();
                        maxY = area.getLayoutY();
                        minX = cPointHandle.getLayoutX()-cPointHandle.getPrefWidth()/2;
                        minY = cPointHandle.getLayoutY()-cPointHandle.getPrefHeight()/2;
                    break;

                    case 2:
                        maxX = bPointHandle.getLayoutX()+bPointHandle.getPrefWidth()*1.5;
                        maxY = bPointHandle.getLayoutY()+bPointHandle.getPrefHeight()*1.5;
                        minX = area.getPrefWidth();
                        minY = area.getPrefHeight();
                    break;

                    case 3:
                        maxX = area.getPrefWidth() + aPointHandle.getPrefHeight()*1.5;
                        maxY = aPointHandle.getLayoutY()+aPointHandle.getPrefHeight()*1.5;
                        minX = Double.POSITIVE_INFINITY;
                        minY = cPointHandle.getLayoutY()-cPointHandle.getPrefHeight()/2;
                    break;
                }

                switch(event.getButton()) {
                    case PRIMARY:
                        xProperties[index].set(Math.max(maxX, Math.min(minX, event.getSceneX() + dragDelta.x)));
                        yProperties[index].set(Math.max(maxY, Math.min(minY, event.getSceneY() + dragDelta.y)));
                    break;

                    default:
                    break;
                }
            });
        }
    }

    /**
     * Crea la lógica para mover la imagen del componente.
     */
    private void setupDragBehavior() {
        final Delta dragDelta = new Delta();
        final DirectionLock directionLock = new DirectionLock();

        area.setOnMousePressed(event -> {
            dragDelta.x = getLayoutX() - event.getSceneX();
            dragDelta.y = getLayoutY() - event.getSceneY();
            directionLock.reset();
        });

        area.setOnMouseDragged(event -> {
            double deltaX = Math.abs(
                event.getSceneX() + dragDelta.x - getLayoutX()
            );
            double deltaY = Math.abs(
                event.getSceneY() + dragDelta.y - getLayoutY()
            );

            switch(event.getButton()) {
                case PRIMARY:
                    setLayoutX(event.getSceneX() + dragDelta.x);
                    setLayoutY(event.getSceneY() + dragDelta.y);
                break;

                case SECONDARY:
                    if (!directionLock.locked) {
                        if(deltaX > 0.77) directionLock.horizontal = true;
                        if(deltaY > 0.77) directionLock.vertical = true;
                        directionLock.locked = true;
                    }

                    if (directionLock.horizontal)
                        setLayoutX(event.getSceneX() + dragDelta.x);
                    else if (directionLock.vertical)
                        setLayoutY(event.getSceneY() + dragDelta.y);
                break;

                default:
                break;
            }
        });
    }

    public void setHandColor(Color color) {
        handColor = color;
        hand.setFill(handColor);
    }

    public Info getAreaInfo() {
        MouseInfoBuilder builder = new MouseInfoBuilder();

        builder.setXPos((int) Math.round(getLayoutX()));
        builder.setYPos((int) Math.round(getLayoutY()));
        builder.setWidth((int) Math.round(area.getPrefWidth()));
        builder.setHeight((int) Math.round(area.getPrefHeight()));

        String hexWithAlpha = String.format("#%02x%02x%02x%02x",
            (int) Math.round(handColor.getRed() * 255),
            (int) Math.round(handColor.getGreen() * 255),
            (int) Math.round(handColor.getBlue() * 255),
            (int) Math.round(handColor.getOpacity() * 255)
        );
        builder.setColor(hexWithAlpha);

        for(int i = 0; i < xProperties.length; i++) {
            builder.setIntParam((int) Math.round(xProperties[i].get() + HANDLE_SIZE/2));
            builder.setIntParam((int) Math.round(yProperties[i].get() + HANDLE_SIZE/2));
        }

        return builder.getResult();
    }

    /**
     * Cambia el estilo de componente dependiendo de si tiene el foco o
     * no según el parámetro dado.
     *
     * @param hasFocus Indica si tiene el foco o no.
     */
    public void setFocus(boolean hasFocus, boolean... componentFocus) {
        setDisable(!hasFocus);
        if(!hasFocus) {
            area.setStyle("-fx-border-color: transparent; -fx-background-color: transparent;");
            hand.setStroke(Color.TRANSPARENT);
            hand.setFill(handColor.darker());
            setFocusArea(false);
            setFocusHand(false);
        } else {
            area.setStyle("-fx-opacity: 0.6; -fx-background-color: lightblue; -fx-border-color: black;");
            hand.setStroke(Color.BLACK);
            hand.setFill(handColor);
            setFocusHand(componentFocus[0]);
            setFocusArea(componentFocus[1]);
        }
    }

    /**
     * Cambia el estilo de componente dependiendo de si tiene el foco o
     * no según el parámetro dado.
     *
     * @param hasFocus Indica si tiene el foco o no.
     */
    private void setFocusArea(boolean hasFocus) {
        if(hasFocus) {
            resizeHandle.setStyle(FOCUS_STYLE);
        } else {
            resizeHandle.setStyle(DISABLE_STYLE);
        }
        area.setDisable(!hasFocus);
        resizeHandle.setDisable(!hasFocus);
    }

    /**
     * Cambia el estilo de componente dependiendo de si tiene el foco o
     * no según el parámetro dado.
     *
     * @param hasFocus Indica si tiene el foco o no.
     */
    private void setFocusHand(boolean hasFocus) {
        Region[] handles = {aPointHandle, bPointHandle, cPointHandle, dPointHandle};

        for(Region handle: handles) {
            if(hasFocus) handle.setStyle(FOCUS_STYLE);
            else handle.setStyle(DISABLE_STYLE);

            handle.setDisable(!hasFocus);
        }
    }
}
