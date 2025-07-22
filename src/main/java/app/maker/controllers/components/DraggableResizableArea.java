package app.maker.controllers.components;

import app.maker.controllers.objects.Objects.Delta;
import app.maker.controllers.objects.Objects.DirectionLock;
import javafx.scene.Cursor;
import javafx.scene.layout.Pane;
import javafx.scene.layout.Region;

public class DraggableResizableArea extends Pane {

    private Region resizeHandle;
    private int MIN_SIZE = 10, OFFSET = 20;

    public DraggableResizableArea(
            double width, double height,
            double maxWidth, double maxHeight
        ) {

        setLayoutX(MIN_SIZE);
        setLayoutY(MIN_SIZE);
        setPrefSize(
            (width < maxWidth-OFFSET)? width: maxWidth-OFFSET,
            (height < maxHeight-OFFSET)? height: maxHeight-OFFSET
        );
        setStyle("-fx-background-color: lightblue; -fx-border-color: black;");
        setCursor(Cursor.MOVE);

        this.resizeHandle = createResizeHandle();
        setupResizeBehavior();
        setupDragBehavior();

        getChildren().add(resizeHandle);
    }

    /**
     * Crea un componente que servirá para ajustar el tamaño del
     * área.
     *
     * @return El componente a añadir en la parte inferior del objeto.
     */
    private Region createResizeHandle() {
        Region handle = new Region();
        handle.setStyle("-fx-background-color: white; -fx-border-color: black");
        handle.setPrefSize(10, 10);

        handle.setLayoutX(getPrefWidth());
        handle.setLayoutY(getPrefHeight());


        handle.setCursor(Cursor.SE_RESIZE);

        return handle;
    }

    private void setupResizeBehavior() {
        final Delta startDelta = new Delta(), panelSize = new Delta(), dragDelta = new Delta();
        final DirectionLock directionLock = new DirectionLock();

        resizeHandle.setOnMousePressed(event -> {
            startDelta.x = event.getSceneX();
            startDelta.y = event.getSceneY();
            panelSize.x = getPrefWidth();
            panelSize.y = getPrefHeight();
            directionLock.reset();
            event.consume();
        });

        resizeHandle.setOnMouseDragged(event -> {
            double newWidth, newHeight;
            switch(event.getButton()) {
                case PRIMARY:
                    dragDelta.x = event.getSceneX() - startDelta.x;
                    dragDelta.y = event.getSceneY() - startDelta.y;

                    newWidth = Math.max(MIN_SIZE, panelSize.x + dragDelta.x);
                    newHeight = Math.max(MIN_SIZE, panelSize.y + dragDelta.y);

                    setPrefWidth(newWidth);
                    setPrefHeight(newHeight);
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
                        setPrefWidth(newSize);
                    resizeHandle.setLayoutX(newSize);
                    } else if (directionLock.vertical) {
                        newSize = Math.max(MIN_SIZE, panelSize.y + dragDelta.y);
                        setPrefHeight(newSize);
                        resizeHandle.setLayoutY(newSize);
                    }
                break;

                default: return;
            }
            event.consume();
        });
    }

    /**
     * Crea la lógica para mover la imagen del componente.
     */
    private void setupDragBehavior() {
        final Delta dragDelta = new Delta();
        final DirectionLock directionLock = new DirectionLock();

        setOnMousePressed(event -> {
            dragDelta.x = getLayoutX() - event.getSceneX();
            dragDelta.y = getLayoutY() - event.getSceneY();
            directionLock.reset();
        });

        setOnMouseDragged(event -> {
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

    /**
     * Cambia el estilo de componente dependiendo de si tiene el foco o
     * no según el parámetro dado.
     *
     * @param hasFocus Indica si tiene el foco o no.
     */
    public void setFocus(boolean hasFocus) {
        if(hasFocus) {
            setStyle("-fx-opacity: 0.6; -fx-background-color: lightblue; -fx-border-color: black;");
            resizeHandle.setStyle("-fx-background-color: white; -fx-border-color: black;");
        } else {
            setStyle("-fx-border-color: transparent; -fx-background-color: transparent;");
            resizeHandle.setStyle("-fx-background-color: transparent; -fx-border-color: transparent;");
        }
        setDisable(!hasFocus);
    }
}
