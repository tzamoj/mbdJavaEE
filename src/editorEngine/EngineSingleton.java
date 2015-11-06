package editorEngine;

import editorUser.Observer;
import java.rmi.NotBoundException;
import java.rmi.RemoteException;
import java.rmi.registry.LocateRegistry;
import java.rmi.registry.Registry;
import java.rmi.server.UnicastRemoteObject;
import java.util.ArrayList;
import java.util.List;

import javax.script.ScriptEngine;
import javax.script.ScriptEngineManager;
import javax.script.ScriptException;

import org.renjin.eval.EvalException;
import org.renjin.parser.ParseException;

/*By Thomas Zamojski, Nov 6, 2015
 * 
 * The EngineSingleton is a singleton whose purpose is to store and manipulate the data required for
 * the editor. It is fired up when the first controller is initiated, and is removed when there is no more 
 * controllers (observers). It registers itself in the RMI registry to notify observers when its state has
 * changed. Also, it brings up Renjin to evaluate R code.
 */
public class EngineSingleton implements EditorEngine {
	
	private StringBuffer contents = new StringBuffer("");
	private String clipboard="";
	private int selectionStart;
	private int selectionLength;
	private List<Observer> obs = new ArrayList<Observer>();
	private boolean textFlag = false; // Is making a textNotify. not sure it is needed anymore.

	//Renjin variables.	
	private static ScriptEngineManager renjManager;
	private static ScriptEngine renj;
	private static StringBuilder sb;
	
	// Singleton design
	static EngineSingleton instance = null;
	public static EngineSingleton getInstance() {
		if(instance==null){
			instance = new EngineSingleton(); // create the only instance
			
			// Make the observer part of instance available remotely through RMI.			
			try {
				Registry rmiRegistry = LocateRegistry.getRegistry(9999);
				EditorEngine rmiService = (EditorEngine) UnicastRemoteObject
						.exportObject(instance, 9999);
				rmiRegistry.bind("engine", instance);
			} catch (Exception ex) {
				ex.printStackTrace();
			}
			
			// Initiate renjin
			renjManager = new ScriptEngineManager();
			renj = renjManager.getEngineByName("Renjin");
			sb = new StringBuilder();
			
		}
		return instance;
	}
	
	
	public void cut() {
		if(selectionLength>0){
			copy();
			deleteSelection();
			textNotify();
			selectionNotify();
		}
	}

	public void copy() {
		if(selectionLength>0){
			clipboard = contents.substring(selectionStart, selectionStart+selectionLength);
		}
	}

	public void paste() { 
		insert(clipboard);
	}

	public void setSelection(Integer start, Integer length) {
		if(textFlag){
			// Ignore the call. This is to avoid setText to call infinite selection notification.
			// In this version, not needed anymore.
		}
		else{
			selectionStart = start.intValue();
			selectionLength = length.intValue();
			selectionNotify();
		}
	}
	
	public void unSelect(){
		selectionLength = 0;
	}

	public void insert(String s) {
		deleteSelection(); 
		contents.insert(selectionStart, s);
		selectionStart = selectionStart + s.length();
		textNotify();
		selectionNotify();
	}

	public String contents() {
		return contents.toString();
	}
	
	public void deleteSelection(){
		contents.delete(selectionStart, selectionStart+selectionLength);
		unSelect();
	}
	
	public String evaluate(){
		String command=""; 
		// If there is text selected, evaluate only the selection, else evaluate the whole buffer.
		if(selectionLength>0){
			command = contents.substring(selectionStart, selectionStart+selectionLength);
		}
		else{
			command = contents.toString();
		}
		try{
			sb.append(renj.eval(command).toString()).append("\n");
			return sb.toString();
		}catch (ScriptException e){
			return sb.append("Script exception\n").toString();
		}catch (ParseException e){
			return sb.append("An error occured while parsing the R code\n").toString();
		}catch (EvalException e){
			return sb.append("An error occured while evaluating the R code\n").toString();
		}
		
	}
	
	@Override
	public void addObserver(Observer o){
		if(!obs.contains(o)){
			obs.add(o);
		}
	}
	
	@Override
	public void removeObserver(Observer o){
		if(obs.contains(o)){
			obs.remove(o);
		}
		// If no more observer, remove myself from RMI registry and kill myself.
		if(obs.isEmpty()){
			try{
				Registry rmiRegistry = LocateRegistry.getRegistry(9999);
				rmiRegistry.unbind("engine");
				boolean a = UnicastRemoteObject.unexportObject((EditorEngine) instance, true);
			}catch(RemoteException e){
				e.printStackTrace();
			}catch(NotBoundException e){
				System.err.println("Singleton not bound to rmiRegistry");
				instance=null;
			}
			instance=null;
		}
	}
	
	// textNotify sends the contents of the buffer to all observers.
	private void textNotify(){
		for (Observer o : obs)
			try {
				textFlag = true; //entering a text notification
				o.textUpdate(this.contents());
				textFlag = false; //exiting a text notification
			} catch (RemoteException e) {
				e.printStackTrace();
			}
	}
	
	// In this version, selection notifications are disabled. Otherwise, it sends a huge number of notifications and
	// rest requests. Another solution would be to get less active synchronisation between the clients and server when
	// selecting text.
	private void selectionNotify(){
		/* We remove selectionNotify since this feature is over reactive at the moment.
		for (Observer o : obs)
			try {
				o.selectionUpdate(this.selectionStart,this.selectionLength);
			} catch (RemoteException e) {
				e.printStackTrace();
			}
			*/
	}
}
