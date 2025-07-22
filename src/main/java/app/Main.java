package app;

import app.files.PropertiesM;
import app.files.TranslationM;

import javafx.application.Application;
import javafx.application.Platform;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.layout.Pane;
import javafx.stage.Stage;

/**
 * Esta clase representa la entrada principal del programa.
 * Controla la inicialización de las configuraciones, la creación de
 * la ventana principal y la detección de dispositivos
 * (micrófono, teclado y ratón).
 */
public class Main extends Application {

    private static Stage primaryStage;

    @Override
    public void start(Stage primaryStage) throws Exception {
        openMainWindow(primaryStage);
    }

    public static void openMainWindow(Stage stage) {
        try {
            FXMLLoader loader = new FXMLLoader(Main.class.getResource("/res/views/main_view.fxml"));
            Pane root = loader.load();
            Scene scene = new Scene(root);

            int taskbarHeight = Integer.parseInt(
                PropertiesM.getVtuberProperty("windows_taskbar_height")
            );

            stage.setTitle(TranslationM.getTranslatedLabel("gui_title"));
            stage.setScene(scene);
            stage.setMinWidth(700);
            stage.setMinHeight(680);
            stage.setWidth(Constants.FULLSCREEN_WIDTH);
            stage.setHeight(Constants.FULLSCREEN_HEIGHT - taskbarHeight);
            stage.show();

            Main.primaryStage = stage;
        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }

    public static void hidePrimaryStage() {
        Platform.runLater(() -> {
            Main.primaryStage.hide();
        });
    }

    public static void showPrimaryStage() {
        Platform.runLater(() -> {
            Main.primaryStage.show();
        });
    }

    public static void closePrimaryStage() {
        Platform.runLater(() -> {
            Main.primaryStage.close();
            Platform.exit();
        });
    }

    public static void startPrimaryStage() {
        Platform.runLater(() -> {
            Main.openMainWindow(new Stage());
        });
    }

    public static void launchApp(String[] args) {
        Platform.setImplicitExit(false);
        launch(args);
    }
}
