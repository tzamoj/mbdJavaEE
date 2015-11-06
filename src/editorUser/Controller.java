package editorUser;

import editorEngine.EditorEngine;
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

import javafx.scene.Scene;
import javafx.scene.control.IndexRange;
import javafx.scene.control.Menu;
import javafx.scene.control.MenuBar;
import javafx.scene.control.MenuItem;
import javafx.scene.control.TextArea;
import javafx.scene.input.KeyEvent;
import javafx.scene.layout.BorderPane;
import javafx.stage.Stage;

/**By Thomas Zamojski, Nov 6, 2015
 * 
 * The controller's purpose is to manage the flow on the client side. It adds/removes itself as an observer
 * via RMI and all other requests are HTTP requests. 
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
	private WebTarget weburl;    // the target rest server.

	// GUI variables.
	private BorderPane root;
	private TextArea inputArea;
	private TextArea outputArea;

	// my variables.
	private Response rp; // responses from the rest server.
	private boolean rpFlag=true; // True if connection is free.
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
		if(rpFlag){
			rpFlag=false;
			rp = weburl.path("/init").request().get();
			rp.close();
			rpFlag=true;
		}
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
		// On initialisation, request the content of the buffer on server.
		inputArea = new TextArea();
		inputArea.setText(weburl.path("/contents").request().get(String.class));
		inputArea.setStyle("-fx-text-fill: green"); // input text is green
		inputArea.setEditable(false);
		
		// Listeners on mouse and keyboard
		inputArea.selectionProperty().addListener((obsValue,oldRange,newRange) -> getSelection(newRange));
		inputArea.setOnKeyTyped(keyEvent -> getTypedKey(keyEvent));
		
		// output area is for the renjin evaluation string buffer.
		outputArea = new TextArea();
		outputArea.setStyle("-fx-text-fill: red"); // output text is red
		outputArea.setText("");
		root.setCenter(inputArea);
		root.setBottom(outputArea);
	}

	private void getTypedKey(KeyEvent keyEvent) {
		//Logger.getGlobal().info(keyEvent.toString());
		if(rpFlag){ // Assume that it is true, as it should be
			rpFlag=false;
			rp = weburl.path("/insert").request()
					.post(Entity.entity(keyEvent.getCharacter(), MediaType.TEXT_PLAIN));
			rp.close();
			rpFlag=true;
		}
	}

	private void getSelection(IndexRange newRange) {
	        //Logger.getGlobal().info(String.format("Start %d, end %d",newRange.getStart(),newRange.getLength()));	    
	    	sl[0]= newRange.getStart();
	    	sl[1]= newRange.getLength();
	    	if(rpFlag){
			rpFlag=false;
			rp = weburl.path("/setSelection").request()
				.post(Entity.entity(sl, MediaType.APPLICATION_JSON));
			rp.close();
			rpFlag=true;
	    	}
	}

	private void buildMenus() {
		
		MenuBar menuBar = new MenuBar();
		Menu fileMenu = new Menu("File");
		Menu editMenu = new Menu("Edit");
		
		// Add evaluate and exit items to File menu
		MenuItem evaluateMenuItem = new MenuItem("Evaluate");
		fileMenu.getItems().add(evaluateMenuItem);
		evaluateMenuItem.setOnAction(event -> this.evaluate());
		
		MenuItem exitMenuItem = new MenuItem("Exit");
		fileMenu.getItems().add(exitMenuItem);
		exitMenuItem.setOnAction(event -> this.exit());
		
		
		// Add cut, copy and paste to the Edit menu
		MenuItem cutMenuItem = new MenuItem("Cut");
		editMenu.getItems().add(cutMenuItem);
		cutMenuItem.setOnAction(event -> this.cut());

		MenuItem copyMenuItem = new MenuItem("Copy");
		editMenu.getItems().add(copyMenuItem);
		copyMenuItem.setOnAction(event -> this.copy());

		MenuItem pasteMenuItem = new MenuItem("Paste");
		editMenu.getItems().add(pasteMenuItem);
		pasteMenuItem.setOnAction(event -> this.paste());

		// Add File and Edit menus to the menuBar
		menuBar.getMenus().add(fileMenu);
		menuBar.getMenus().add(editMenu);
		root.setTop(menuBar);
	}
	
	private void exit(){
		// On exit, remove myself as observer first.
		try{
			remoteEngine.removeObserver((Observer) this);
		}catch(RemoteException e){
			System.err.println("Unable to remove from the RMI registry!");
		}
		System.exit(0);
	}

	private void evaluate() {
		if(rpFlag){
			rpFlag=false;
			outputArea.setText(weburl.path("/evaluate").request().get(String.class));
			rpFlag=true;
		}
	}

	private void paste() {
		if(rpFlag){
			rpFlag=false;
			rp = weburl.path("/paste").request()
					.get();
			rp.close();
			rpFlag=true;
		}
	}

	private void copy() {
		if(rpFlag){
			rpFlag=false;
			rp = weburl.path("/copy").request()
					.get();
			rp.close();
			rpFlag=true;
		}
	}

	private void cut() {
		if(rpFlag){
			rpFlag=false;
			rp = weburl.path("/cut").request()
					.get();
			rp.close();
			rpFlag=true;
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
