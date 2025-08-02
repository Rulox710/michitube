package app.maker.controllers.components;

import app.files.PropertiesM;
import app.maker.controllers.objects.Infos.Info;
import app.maker.controllers.objects.Objects.Delta;
import app.maker.controllers.objects.Objects.DirectionLock;
import app.maker.controllers.objects.builders.ImageInfoBuilder;

import java.io.File;
import java.net.URI;
import java.nio.file.Path;
import java.nio.file.Paths;

import javafx.scene.image.Image;
import javafx.scene.Cursor;
import javafx.scene.effect.ColorAdjust;
import javafx.scene.image.ImageView;
import javafx.scene.layout.Pane;
import javafx.scene.layout.Region;

/**
 * Compoente que tiene una imagen y permite moverla en los ejes y
 * cambiar su tamaño.
 */
public class DraggableResizableImageView extends Pane {

    private final ImageView imageView;
    private final Region resizeHandle;
    private final int MIN_SIZE = 10, OFFSET = 20, HANDLE_SIZE = 10;
    private final String PATH;

    /**
     * Constructor de la clase. Una vez creado, no hay forma de
     * modificar case ningún valor que no sea el tamano de la imagen y
     * su posición.
     *
     * @param fimg El archivo con la imagen a colocar.
     * @param maxWidth Máximo ancho de la imagen inicialmente.
     * @param maxHeight Máximo alto de la imagen inicialmente.
     */
    public DraggableResizableImageView(
            File fimg, double maxWidth, double maxHeight
        ) {

        PATH = fimg.toURI().toString();
        Image image = new Image(PATH);
        double width = (image.getWidth() < maxWidth-OFFSET)?
                image.getWidth(): maxWidth-OFFSET;
        double height = (image.getHeight() < maxHeight-OFFSET)?
                image.getHeight(): maxHeight-OFFSET;
        setPrefSize(width, height);

        imageView = new ImageView(image);
        imageView.setCursor(Cursor.MOVE);
        imageView.fitHeightProperty().bind(prefHeightProperty());
        imageView.fitWidthProperty().bind(prefWidthProperty());

        this.resizeHandle = createResizeHandle();
        setupResizeBehavior();
        setupDragBehavior();

        getChildren().addAll(imageView, resizeHandle);
    }

    /**
     * Constructor de la clase. Una vez creado, no hay forma de
     * modificar case ningún valor que no sea el tamano de la imagen y
     * su posición.
     *
     * @param fimg El archivo con la imagen a colocar.
     * @param xPos Posición en el eje x inicialmente.
     * @param yPos Posición en el eje y inicialmente.
     * @param width Ancho de la imagen inicialmente.
     * @param height Alto de la imagen inicialmente.
     */
    public DraggableResizableImageView(
            File fimg, int xPos, int yPos, int width, int height
        ) {

        PATH = fimg.toURI().toString();
        setLayoutX(xPos);
        setLayoutY(yPos);
        setPrefSize(width, height);

        Image image = new Image(PATH);
        imageView = new ImageView(image);
        imageView.setCursor(Cursor.MOVE);
        imageView.fitHeightProperty().bind(prefHeightProperty());
        imageView.fitWidthProperty().bind(prefWidthProperty());

        this.resizeHandle = createResizeHandle();
        setupResizeBehavior();
        setupDragBehavior();

        getChildren().addAll(imageView, resizeHandle);
    }

    /**
     * Crea un componente que servirá para ajustar el tamaño de la
     * imagen.
     *
     * @return El componente a añadir en la parte inferior del objeto.
     */
    private Region createResizeHandle() {
        Region handle = new Region();
        handle.setStyle("-fx-background-color: white; -fx-border-color: black");
        handle.setPrefSize(HANDLE_SIZE, HANDLE_SIZE);
        handle.setCursor(Cursor.SE_RESIZE);

        handle.layoutXProperty().bind(imageView.fitWidthProperty().subtract(HANDLE_SIZE/2));
        handle.layoutYProperty().bind(imageView.fitHeightProperty().subtract(HANDLE_SIZE/2));

        return handle;
    }

    /**
     * Crea la lógica para ajustar los tamaños de la imagen con el
     * componente <code>resizeHandle</code>.
     */
    private void setupResizeBehavior() {
        final Delta startDelta = new Delta(),
                    startImage = new Delta(),
                    dragDelta = new Delta();

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

                    setPrefWidth(newWidth);
                    setPrefHeight(newHeight);
                break;

                case SECONDARY:
                    dragDelta.x = event.getSceneX() - startDelta.x;

                    double aspectRatio = imageView.getFitWidth()
                            / imageView.getFitHeight();
                    newWidth = Math.max(MIN_SIZE, startImage.x + dragDelta.x);
                    newHeight = newWidth / aspectRatio;

                    if(newHeight >= MIN_SIZE && newWidth >= MIN_SIZE) {
                        setPrefWidth(newWidth);
                        setPrefHeight(newHeight);
                    }
                break;

                default:
                break;
            }

        });
    }

    /**
     * Crea la lógica para mover la imagen del componente.
     */
    private void setupDragBehavior() {
        final Delta dragDelta = new Delta();
        final DirectionLock directionLock = new DirectionLock();

        imageView.setOnMousePressed(event -> {
            dragDelta.x = getLayoutX() - event.getSceneX();
            dragDelta.y = getLayoutY() - event.getSceneY();
            directionLock.reset();
        });

        imageView.setOnMouseDragged(event -> {
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
            setStyle("-fx-border-color: white;");
            resizeHandle.setStyle("-fx-background-color: white; -fx-border-color: black");
            imageView.setEffect(null);
        } else {
            setStyle("-fx-border-color: transparent;");
            resizeHandle.setStyle("-fx-background-color: transparent; -fx-border-color: transparent");
            ColorAdjust adjust = new ColorAdjust();
            adjust.setBrightness(-0.4);      // más oscuro
            adjust.setSaturation(-0.5);      // menos saturado
            imageView.setEffect(adjust);
        }
        setDisable(!hasFocus);
    }

    /**
     * Crea un objeto en el que se pueden guardar distintas primitivas
     * y cadenas según las necesidades específicas. En este caso el
     * objeto es del tipo {@link ImageInfoBuilder}.
     *
     * @return El objeto que trasporta toda la información relevante
     *         del componente.
     */
    public Info getImageInfo() {
        Path basePath = Paths.get(PropertiesM.getAppProperty("default_dir"));
        URI fileUri = URI.create(PATH);
        Path fullPath = Paths.get(fileUri);
        Path relativePath = basePath.relativize(fullPath);

        ImageInfoBuilder builder = new ImageInfoBuilder();

        builder.setXPos((int) Math.round(getLayoutX()));
        builder.setYPos((int) Math.round(getLayoutY()));
        builder.setWidth((int) Math.round(imageView.getFitWidth()));
        builder.setHeight((int) Math.round(imageView.getFitHeight()));
        builder.setPath(relativePath.toString());
        return builder.getResult();
    }
}
