package editorEngine;

import java.rmi.RemoteException;
import java.util.ArrayList;
import java.util.List;

import javax.ws.rs.Consumes;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;
import editorUser.Observer;

@Path("/editor")
public class EditorEngineImpl implements EditorEngine {
	
	public EditorEngineImpl(){
		EngineSingleton.getInstance().init();
	}
	
	@Override
	@POST
	public void cut() {
		EngineSingleton.getInstance().cut();
	}

	@Override
	@POST
	public void copy() {
		EngineSingleton.getInstance().copy();
	}

	@Override
	@GET
	@Path("/paste")
	public void paste() { 
		EngineSingleton.getInstance().paste();
	}

	@Override
	@POST
	public void setSelection(Integer start, Integer length) {
		EngineSingleton.getInstance().setSelection(start, length);
	}
	
	@Override
	@POST
	public void insert(String s) {
		EngineSingleton.getInstance().insert(s);
	}

	@Override
	@GET
	@Path("/contents")
	@Produces(MediaType.TEXT_HTML)
	public String contents() {
		return EngineSingleton.getInstance().contents();
	}
	
	@Override
	@GET
	public void evaluate(){
		EngineSingleton.getInstance().evaluate();
	}
	
	@Override
	public void addObserver(Observer o){
		EngineSingleton.getInstance().addObserver(o);
	}
	
	@Override
	public void removeObserver(Observer o){
		EngineSingleton.getInstance().removeObserver(o);
	}
}
