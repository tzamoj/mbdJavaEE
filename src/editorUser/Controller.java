package editorUser;

import java.rmi.Naming;
import java.rmi.RemoteException;
import java.rmi.server.UnicastRemoteObject;
import java.util.logging.Logger;

import editorEngine.EditorEngine;
import javafx.scene.Scene;
import javafx.scene.control.IndexRange;
import javafx.scene.control.Menu;
import javafx.scene.control.MenuBar;
import javafx.scene.control.MenuItem;
import javafx.scene.control.TextArea;
import javafx.scene.input.KeyEvent;
import javafx.scene.layout.BorderPane;
import javafx.scene.text.Text;
import javafx.stage.Stage;

/**
 * Created by plouzeau on 2015-10-01.
 */
public class Controller extends UnicastRemoteObject implements Observer {

	private Stage primaryStage;
	public void setPrimaryStage(Stage primaryStage) {
		this.primaryStage = primaryStage;
	}

	private EditorEngine remoteEngine;
	private BorderPane root;

	private TextArea inputArea;
	private Text outputArea;

	public Controller() throws RemoteException{
		if (System.getSecurityManager() == null)
			System.setSecurityManager(new SecurityManager());
		try {
			remoteEngine = (EditorEngine) Naming
					.lookup("//localhost:9999/engine");
			remoteEngine.addObserver((Observer) this);
		} catch (Exception ex) {
			ex.printStackTrace();
		};
	}

	public void buildGUI() {
		root = new BorderPane();
		buildMenus();
		buildTexts();

		primaryStage.setTitle("SimpleEditor");
		primaryStage.setScene(new Scene(root, 300, 275));
	}

	private void buildTexts() {
		inputArea = new TextArea();
		//inputArea.setText("");
		try{
			inputArea.setText(remoteEngine.contents());
		}catch (RemoteException e){
			e.printStackTrace();
		}
		inputArea.setStyle("-fx-text-fill: blue");
		inputArea.setEditable(false);
		inputArea.selectionProperty().addListener((obsValue,oldRange,newRange) -> getSelection(newRange));
		inputArea.setOnKeyTyped(keyEvent -> getTypedKey(keyEvent));
		

		outputArea = new Text();
		outputArea.setText("Results go here");
		root.setCenter(inputArea);
		root.setBottom(outputArea);

	}

	private void getTypedKey(KeyEvent keyEvent) {
		Logger.getGlobal().info(keyEvent.getCharacter());
		try {
			remoteEngine.insert(keyEvent.getCharacter());
		} catch (RemoteException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	private void getSelection(IndexRange newRange) {
		Logger.getGlobal().info(String.format("Start %d, end %d",newRange.getStart(),newRange.getLength()));
		try {
			remoteEngine.setSelection(newRange.getStart(),newRange.getLength());
		} catch (RemoteException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	private void buildMenus() {
		MenuBar menuBar = new MenuBar();
		Menu editMenu = new Menu("Edit");
		// Add a menu item to cut
		MenuItem addMenuItem = new MenuItem("Cut");

		addMenuItem.setOnAction(event -> this.cut());
		editMenu.getItems().add(addMenuItem);

		// Ditto for copy
		MenuItem removeMenuItem = new MenuItem("Copy");
		editMenu.getItems().add(removeMenuItem);
		removeMenuItem.setOnAction(event -> this.copy());


		// Ditto for paste
		MenuItem pasteMenuItem = new MenuItem("Paste");
		editMenu.getItems().add(pasteMenuItem);
		pasteMenuItem.setOnAction(event -> this.paste());


		// Ditto for evaluate
		MenuItem evaluateMenuItem = new MenuItem("Evaluate");
		editMenu.getItems().add(evaluateMenuItem);
		evaluateMenuItem.setOnAction(event -> this.evaluate());

		// Add the Edit menu
		menuBar.getMenus().add(editMenu);
		root.setTop(menuBar);
	}

	private void evaluate() {
		try {
			remoteEngine.evaluate();
		} catch (RemoteException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	private void paste() {
		try {
			remoteEngine.paste();
		} catch (RemoteException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	private void copy() {
		try {
			remoteEngine.copy();
		} catch (RemoteException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	private void cut() {
		try {
			remoteEngine.cut();
		} catch (RemoteException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	public void run() {
		primaryStage.show();

	}

	@Override
	public void textUpdate(String text) throws RemoteException {
		inputArea.setText(text);
	}

	@Override
	public void selectionUpdate(int selectionStart, int selectionLength) throws RemoteException{
		inputArea.positionCaret(selectionStart);

	}
}
