package editorUser;

import java.rmi.Naming;
import java.rmi.RemoteException;
import java.rmi.server.UnicastRemoteObject;
import java.util.logging.Logger;

import javax.ws.rs.client.Client;
import javax.ws.rs.client.ClientBuilder;
import javax.ws.rs.client.Entity;
import javax.ws.rs.client.WebTarget;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

import org.renjin.sexp.SEXP;

import editorEngine.EditorEngine;
import javafx.scene.Scene;
import javafx.scene.control.IndexRange;
import javafx.scene.control.Menu;
import javafx.scene.control.MenuBar;
import javafx.scene.control.MenuItem;
import javafx.scene.control.TextArea;
import javafx.scene.input.KeyEvent;
import javafx.scene.layout.BorderPane;
import javafx.stage.Stage;

/**
 * Created by plouzeau on 2015-10-01.
 */
public class Controller extends UnicastRemoteObject implements Observer {

	private Stage primaryStage;
	public void setPrimaryStage(Stage primaryStage) {
		this.primaryStage = primaryStage;
	}

	// Server side variables.
	private EditorEngine remoteEngine;
	private Client restClient;
	private final String URL="http://localhost:8080/editor";

	// GUI variables.
	private BorderPane root;
	private TextArea inputArea;
	private TextArea outputArea;

	// my variables.
	private WebTarget weburl;    // the target rest server.
	private Response rp,rpt,rps; // responses from the rest server.
	private Integer[] sl= new Integer[2];  // used to get selection parameters in one object.
	private boolean flag=true;   // true will mean that controller is NOT making a setText call.

	/* On initialisation, controller configures itself as a rest client on target URL,
	 * and sends an init msg to the server, who has time to configure itself if not up yet,
	 * and register itself in the RMI registry. Then the controller looks in the RMI registry
	 * to register itself as an Observer of the server.
	 */
	public Controller() throws RemoteException{
		restClient = ClientBuilder.newClient();
		weburl = restClient.target(URL);
		rp = weburl.path("/init").request().get();
		rp.close();
		if (System.getSecurityManager() == null)
			System.setSecurityManager(new SecurityManager());
		try {
			remoteEngine = (EditorEngine) Naming
					.lookup("//localhost:9999/engine");
			remoteEngine.addObserver((Observer) this);
		} catch (Exception ex) {
			System.err.println("The communication with the rmiRegistry or the engine seems to be broken: verify the registry and engine are up and registered.");
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

	private void buildTexts(){
		inputArea = new TextArea();
		/*try{
			inputArea.setText(remoteEngine.contents());
		}catch (RemoteException e){
			e.printStackTrace();
		}*/
		//rp = weburl.path("/contents").request().get();
		/*if(resp.getStatus()!=200){
			throw new RuntimeException();
		}*/
		//inputArea.setText((String) rp.getEntity());
		//rp.close();
		inputArea.setText(weburl.path("/contents").request().get(String.class));
		inputArea.setStyle("-fx-text-fill: green");
		inputArea.setEditable(false);
		
		//inputArea.selectionProperty().addListener((obsValue,oldRange,newRange) -> getSelection(newRange));
		inputArea.setOnKeyTyped(keyEvent -> getTypedKey(keyEvent));
		

		outputArea = new TextArea();
		outputArea.setStyle("-fx-text-fill: red");
		outputArea.setText("");
		root.setCenter(inputArea);
		root.setBottom(outputArea);
	}

	private void getTypedKey(KeyEvent keyEvent) {
		//Logger.getGlobal().info(keyEvent.toString());

		rpt = weburl.path("/insert").request()
		    .post(Entity.entity(keyEvent.getCharacter(), MediaType.TEXT_PLAIN));
		rpt.close();
		/*if(resp.getStatus()!=200){
			throw new RuntimeException();
		}*/
	}

	private void getSelection(IndexRange newRange) {
	    	Logger.getGlobal().info(String.format("Start %d, end %d",newRange.getStart(),newRange.getLength()));	    
	    	sl[0]= newRange.getStart();
	    	sl[1]= newRange.getLength();
	    	rps = weburl.path("/setSelection").request()
	    			.post(Entity.entity(sl, MediaType.APPLICATION_JSON));
	    	rps.close();
	}

	private void buildMenus() {
		
		MenuBar menuBar = new MenuBar();
		
		Menu fileMenu = new Menu("File");
		MenuItem exitMenuItem = new MenuItem("Exit");
		exitMenuItem.setOnAction(event -> this.exit());
		
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
	
	private void exit(){
		try{
			remoteEngine.removeObserver((Observer) this);
		}catch(RemoteException e){
			System.err.println("Unable to remove from the RMI registry!");
		}
	}

	private void evaluate() {
		/*rp = weburl.path("/evaluate").request()
			    .get();
		//outputArea.setText(((SEXP)rp.getEntity()).toString());
		outputArea.setText((String)rp.getEntity());
		rp.close();*/
		outputArea.setText(weburl.path("/evaluate").request().get(String.class));
	}

	private void paste() {
		getSelection(inputArea.getSelection());
		rp = weburl.path("/paste").request()
			    .get();
		rp.close();
	}

	private void copy() {
		getSelection(inputArea.getSelection());
		rp = weburl.path("/copy").request()
			    .get();
		rp.close();
	}

	private void cut() {
		getSelection(inputArea.getSelection());
		rp = weburl.path("/cut").request()
			    .get();
		rp.close();
	}

	public void run() {
		primaryStage.show();

	}

	@Override
	public void textUpdate(String text) throws RemoteException {
		flag=false;
		inputArea.setText(text);
		flag=true;
	}

	@Override
	public void selectionUpdate(int selectionStart, int selectionLength) throws RemoteException{
		inputArea.positionCaret(selectionStart);

	}
}
