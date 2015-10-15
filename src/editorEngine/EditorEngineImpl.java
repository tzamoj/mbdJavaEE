package editorEngine;

import java.rmi.RemoteException;
import java.util.ArrayList;
import java.util.List;

import editorUser.Observer;

public class EditorEngineImpl implements EditorEngine {
	
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
		insert(clipboard);
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
		return contents.toString();
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
