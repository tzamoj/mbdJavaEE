package editorEngine;

import java.rmi.RemoteException;
import java.rmi.registry.LocateRegistry;
import java.rmi.registry.Registry;
import java.rmi.server.UnicastRemoteObject;
import java.util.ArrayList;
import java.util.List;

import javax.script.ScriptEngine;
import javax.script.ScriptEngineManager;
import javax.script.ScriptException;

import org.renjin.sexp.SEXP;

import editorUser.Observer;

public class EngineSingleton implements EditorEngine {
	private static ScriptEngineManager renjManager;
	private static ScriptEngine renj;
	// Singleton design
	static EngineSingleton instance = null;
	public static EngineSingleton getInstance() {
		if(instance==null){
			instance = new EngineSingleton(); // create the only instance
			
			// Register to the RMI registry
			if (System.getSecurityManager() == null)
				System.setSecurityManager(new SecurityManager());
			try {
				Registry rmiRegistry = LocateRegistry.createRegistry(9999);
				EditorEngine rmiService = (EditorEngine) UnicastRemoteObject
						.exportObject(instance, 9999);
				rmiRegistry.bind("engine", instance);
			} catch (Exception ex) {
				ex.printStackTrace();
			}
			
			// Initiate renjin
			renjManager = new ScriptEngineManager();
			renj = renjManager.getEngineByName("Renjin");
			// Could throw an error if renj is null.
			
			// Initiate persistance 
		}
		return instance;
	}
	
	// On initialisation, register the engine to RMIregistry.
	public void init(){		
	}

	private StringBuffer contents = new StringBuffer("");
	private String clipboard="";
	private int selectionStart;
	private int selectionLength;
	private List<Observer> obs = new ArrayList<Observer>();
	private boolean textFlag = false;
	
	//@Override
	public void cut() {
		//System.out.println("Performing a cut from "+selectionStart+" of length "+selectionLength);
		if(selectionLength>0){
			copy();
			deleteSelection();
			textNotify();
			selectionNotify();
		}
	}

	//@Override
	public void copy() {
		if(selectionLength>0){
			clipboard = contents.substring(selectionStart, selectionStart+selectionLength);
		}
	}

	//@Override
	public void paste() { 
		insert(clipboard);
		//System.out.println("Pasting");
	}

	//@Override
	public void setSelection(Integer start, Integer length) {
		if(textFlag){
			//Ignore the call.
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

	//@Override
	public void insert(String s) {
		deleteSelection(); 
		contents.insert(selectionStart, s);
		selectionStart = selectionStart + s.length();
		textNotify();
		selectionNotify();
	}

	//@Override
	public String contents() {
		return contents.toString();
	}
	
	// should test that if selectionLength==0 then nothing is done.
	public void deleteSelection(){
		contents.delete(selectionStart, selectionStart+selectionLength);
		unSelect();
	}
	
	//@Override
	public String evaluate(){
		try{
			return renj.eval(contents.toString()).toString();
		}catch (ScriptException e){
			return "An error occured while evaluating the R code";
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
	}
	
	private void textNotify(){
		for (Observer o : obs)
			try {
				textFlag = true;
				o.textUpdate(this.contents());
				textFlag = false;
			} catch (RemoteException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
	}
	
	private void selectionNotify(){
		/*for (Observer o : obs)
			try {
				o.selectionUpdate(this.selectionStart,this.selectionLength);
			} catch (RemoteException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}*/
	}
}
