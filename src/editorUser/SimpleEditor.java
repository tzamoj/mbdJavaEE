package editorUser;

import editorEngine.EditorEngine;
import editorEngine.EditorEngineImpl;
import javafx.application.Application;
import javafx.stage.Stage;

/**
 * Created by plouzeau on 2015-10-01.
 */
public class SimpleEditor extends Application {

    private Controller controller;
    //private EditorEngine editorEngine;

    @Override
    public void start(Stage primaryStage) throws Exception {

        //editorEngine = new EditorEngineImpl();
        controller = new Controller();
        controller.setPrimaryStage(primaryStage);
        controller.buildGUI();
        controller.run();
        //editorEngine.addObserver(controller);
    }

    public static void main(String[] args) {
        launch(args);
    }
}






