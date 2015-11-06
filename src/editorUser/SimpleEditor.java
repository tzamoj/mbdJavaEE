package editorUser;

import editorEngine.EditorEngine;
import editorEngine.EditorEngineImpl;
import javafx.application.Application;
import javafx.stage.Stage;

/**
 * By Thomas Zamojski, Nov 6 2015.
 * Simple Editor launches the controller and GUI.
 */
public class SimpleEditor extends Application {

    private Controller controller;

    @Override
    public void start(Stage primaryStage) throws Exception {
        controller = new Controller();
        controller.setPrimaryStage(primaryStage);
        controller.buildGUI();
        controller.run();
    }

    public static void main(String[] args) {
        launch(args);
    }
}






