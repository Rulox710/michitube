package app;

import com.github.kwhat.jnativehook.GlobalScreen;
import com.github.kwhat.jnativehook.NativeHookException;

import app.files.PropertiesM;
import app.files.TranslationM;

import javafx.application.Application;
import javafx.application.Platform;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.image.Image;
import javafx.scene.layout.Pane;
import javafx.stage.Stage;

/**
 * Clase principal que inicia la interfaz de la aplicación con javaFX.
 */
public class Main extends Application {

    private static Stage primaryStage;

    /**
     * Método principal que inicia la aplicación.
     * @param args Argumentos de la línea de comandos.
     */
    @Override
    public void start(Stage primaryStage) throws Exception {
        openMainWindow(primaryStage);
    }

    /**
     * Método que se encarga de iniciar la aplicación.
     * @param args Argumentos de la línea de comandos.
     */
    public static void openMainWindow(Stage stage) {
        try {
            FXMLLoader loader = new FXMLLoader(Main.class.getResource("/views/main_view.fxml"));
            System.out.println(String.format(LogMessage.GUI_FXML.get(), "Vista principal"));

            Pane root = loader.load();
            Scene scene = new Scene(root);

            int taskbarHeight = Integer.parseInt(
                PropertiesM.getAppProperty("windows_taskbar_height")
            );

            Image icon = new Image(Main.class.getResourceAsStream("/assets/icon.png"));
            stage.getIcons().add(icon);
            stage.setTitle(TranslationM.getTranslatedLabel("gui_title"));
            stage.setScene(scene);
            stage.setMinWidth(700);
            stage.setMinHeight(680);
            stage.setWidth(Constants.FULLSCREEN_WIDTH);
            stage.setHeight(Constants.FULLSCREEN_HEIGHT - taskbarHeight);
            stage.show();

            Main.primaryStage = stage;
            Main.primaryStage.setOnCloseRequest(event -> {
                stopApp(false);
            });
        } catch(Exception ex) {
            System.out.println(String.format(LogMessage.GUI_FXML_X.get(), "Vista principal"));

            System.out.println(LogMessage.PROGRAM_END_X);
            System.err.println(LogMessage.PROGRAM_END_X);

            Constants.printTimeStamp(System.err);
            ex.printStackTrace();
        }
    }

    /**
     * Esconde la ventana principal de la aplicación.
     */
    public static void hidePrimaryStage() {
        Platform.runLater(() -> {
            Main.primaryStage.hide();
            System.out.println(LogMessage.GUI_HIDE.get());
        });
    }

    /**
     * Muestra la ventana principal de la aplicación.
     */
    public static void showPrimaryStage() {
        Platform.runLater(() -> {
            System.out.println(LogMessage.GUI_SHOW.get());
            Main.primaryStage.show();
        });
    }

    /**
     * Cierra la ventana principal de la aplicación y finaliza la
     * ejecución de javaFX.
     */
    public static void closePrimaryStage() {
        System.out.println(LogMessage.GUI_END.get());
        Platform.runLater(() -> {
            Main.primaryStage.close();
            Platform.exit();
        });
    }

    public static void stopApp(boolean error) {
        PropertiesM.saveAppProperties();
        Main.closePrimaryStage();
        try {
            GlobalScreen.unregisterNativeHook();
            System.out.println(LogMessage.GLOBALSCREEN_STOP.get());
        } catch(NativeHookException ex) {
            Constants.printTimeStamp(System.err);
            ex.printStackTrace();
        }
        if(error) {
            System.out.println(LogMessage.PROGRAM_END_X.get());
            System.err.println(LogMessage.PROGRAM_END_X.get());
        } else {
            System.out.println(LogMessage.PROGRAM_END_O.get());
        }
        System.exit(0);
    }


    public static void startPrimaryStage() {
        Platform.runLater(() -> {
            Main.openMainWindow(new Stage());
        });
    }

    /**
     * Lanza la aplicación javaFX.
     * @param args Argumentos de la línea de comandos.
     */
    public static void launchApp(String[] args) {
        System.out.println(LogMessage.GUI_START.get());
        Platform.setImplicitExit(false);
        launch(args);
    }
}
