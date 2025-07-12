package app.maker.controllers.components;

import java.io.File;

import app.maker.controllers.objects.Objects.Delta;
import app.maker.controllers.objects.Objects.DirectionLock;
import app.maker.controllers.objects.Objects.ImageInfo;
import javafx.scene.image.Image;
import javafx.scene.Cursor;
import javafx.scene.image.ImageView;
import javafx.scene.layout.Pane;
import javafx.scene.layout.Region;

public class DraggableResizableImageView extends Pane {

    private final ImageView imageView;
    private final Region resizeHandle;
    private final int MIN_SIZE = 5, OFFSET = 20;
    private final String PATH;

    public DraggableResizableImageView(File fimg, double maxWidth, double maxHeight) {
        PATH = fimg.toURI().toString();
        Image image = new Image(PATH);
        double width = (image.getWidth() < maxWidth-OFFSET)? image.getWidth(): maxWidth-OFFSET;
        double height = (image.getHeight() < maxHeight-OFFSET)? image.getHeight(): maxHeight-OFFSET;
        imageView = new ImageView(image);
        imageView.setCursor(Cursor.MOVE);
        imageView.setFitWidth(width);
        imageView.setFitHeight(height);

        this.resizeHandle = createResizeHandle();
        setupResizeBehavior();
        setupDragBehavior();

        getChildren().addAll(imageView, resizeHandle);
    }

    private Region createResizeHandle() {
        Region handle = new Region();
        handle.setStyle("-fx-background-color: white; -fx-border-color: black");
        handle.setPrefSize(10, 10);
        handle.setCursor(Cursor.SE_RESIZE);

        handle.layoutXProperty().bind(imageView.fitWidthProperty());
        handle.layoutYProperty().bind(imageView.fitHeightProperty());

        return handle;
    }

    private void setupResizeBehavior() {
        final Delta startDelta = new Delta(), startImage = new Delta(), dragDelta = new Delta();

        resizeHandle.setOnMousePressed(event -> {
            startDelta.x = event.getSceneX();
            startDelta.y = event.getSceneY();
            startImage.x = imageView.fitWidthProperty().get();
            startImage.y = imageView.fitHeightProperty().get();
        });

        resizeHandle.setOnMouseDragged(event -> {
            double newWidth, newHeight;
            switch(event.getButton()) {
                case PRIMARY:
                    dragDelta.x = event.getSceneX() - startDelta.x;
                    dragDelta.y = event.getSceneY() - startDelta.y;

                    newWidth = Math.max(MIN_SIZE, startImage.x + dragDelta.x);
                    newHeight = Math.max(MIN_SIZE, startImage.y + dragDelta.y);

                    imageView.setFitWidth(newWidth);
                    imageView.setFitHeight(newHeight);
                break;
                case SECONDARY:
                    dragDelta.x = event.getSceneX() - startDelta.x;

                    double aspectRatio = imageView.getFitWidth() / imageView.getFitHeight();
                    newWidth = Math.max(MIN_SIZE, startImage.x + dragDelta.x);
                    newHeight = newWidth / aspectRatio;

                    if(newHeight >= MIN_SIZE && newWidth >= MIN_SIZE) {
                        imageView.setFitWidth(newWidth);
                        imageView.setFitHeight(newHeight);
                    }
                break;
            }

        });
    }

    private void setupDragBehavior() {
        final Delta dragDelta = new Delta();
        final DirectionLock directionLock = new DirectionLock();

        imageView.setOnMousePressed(event -> {
            dragDelta.x = getLayoutX() - event.getSceneX();
            dragDelta.y = getLayoutY() - event.getSceneY();
            directionLock.reset();
        });

        imageView.setOnMouseDragged(event -> {
            double deltaX = Math.abs(event.getSceneX() + dragDelta.x - getLayoutX());
            double deltaY = Math.abs(event.getSceneY() + dragDelta.y - getLayoutY());

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
            }
        });
    }

    public void setFocus(boolean hasFocus) {
        if (hasFocus) {
            setStyle("-fx-border-color: white;");
            resizeHandle.setStyle("-fx-background-color: white; -fx-border-color: black");
            imageView.setStyle("-fx-opacity: 1.0;");
        } else {
            setStyle("-fx-border-color: transparent;");
            resizeHandle.setStyle("-fx-background-color: transparent; -fx-border-color: transparent");
            imageView.setStyle("-fx-opacity: 0.5;");
        }
        setDisable(!hasFocus);
    }

    public ImageInfo getImageInfo() {
        final ImageInfo IMAGE_INFO = new ImageInfo();
        IMAGE_INFO.x = (int) Math.round(getLayoutX());
        IMAGE_INFO.y = (int) Math.round(getLayoutY());
        IMAGE_INFO.width = (int) Math.round(imageView.getFitWidth());
        IMAGE_INFO.height = (int) Math.round(imageView.getFitHeight());
        IMAGE_INFO.path = PATH;
        return IMAGE_INFO;
    }
}
