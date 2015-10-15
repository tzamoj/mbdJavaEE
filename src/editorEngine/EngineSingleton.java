package editorEngine;

import java.rmi.RemoteException;
import java.rmi.registry.LocateRegistry;
import java.rmi.registry.Registry;
import java.rmi.server.UnicastRemoteObject;
import java.util.ArrayList;
import java.util.List;

import editorUser.Observer;

public class EngineSingleton implements EditorEngine {
	// Singleton design
	static EngineSingleton instance = new EngineSingleton();
	public static EngineSingleton getInstance() {
		return instance;
	}
	
	// On initialisation, register the engine to RMIregistry.
	public void init(){
		if (System.getSecurityManager() == null)
            System.setSecurityManager(new SecurityManager());
        try {
            Registry rmiRegistry = LocateRegistry.createRegistry(9999);
            EditorEngine rmiService = (EditorEngine) UnicastRemoteObject
                    .exportObject(this, 9999);
            rmiRegistry.bind("engine", rmiService);
        } catch (Exception ex) {
            ex.printStackTrace();
        }
	}

	private StringBuffer contents = new StringBuffer("");
	private String clipboard="";
	private int selectionStart;
	private int selectionLength;
	private List<Observer> obs = new ArrayList<Observer>();
	private boolean textFlag = false;
	
	@Override
	public void cut() {
		if(selectionLength>0){
			copy();
			deleteSelection();
			textNotify();
			selectionNotify();
		}
	}

	@Override
	public void copy() {
		if(selectionLength>0){
			clipboard = contents.substring(selectionStart, selectionStart+selectionLength);
		}
	}

	@Override
	public void paste() { 
		//insert(clipboard);
		System.out.println("Pasting");
	}

	@Override
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

	@Override
	public void insert(String s) {
		deleteSelection(); 
		contents.insert(selectionStart, s);
		selectionStart = selectionStart + s.length();
		textNotify();
		selectionNotify();
	}

	@Override
	public String contents() {
		//return contents.toString();
		return "<HTML><BODY>I am doing fine</BODY></HTML>";
	}
	
	// should test that if selectionLength==0 then nothing is done.
	public void deleteSelection(){
		contents.delete(selectionStart, selectionStart+selectionLength);
		unSelect();
	}
	
	@Override
	public void evaluate(){
		// Connect to Renjin
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
		for (Observer o : obs)
			try {
				o.selectionUpdate(this.selectionStart,this.selectionLength);
			} catch (RemoteException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
	}
}
