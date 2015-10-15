package editorEngine;

import javax.ws.rs.Consumes;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

import editorUser.Observer;

@Path("/editor")
public class EditorEngineImpl implements EditorEngine {
	
	public EditorEngineImpl(){
		EngineSingleton.getInstance().init();
	}
	
	//@Override
	@POST
	public void cut() {
		EngineSingleton.getInstance().cut();
	}

	//@Override
	@POST
	public void copy() {
		EngineSingleton.getInstance().copy();
	}

	//@Override
	@GET
	@Path("/paste")
	public void paste() { 
		EngineSingleton.getInstance().paste();
	}

	//@Override
	@POST
	public void setSelection(Integer start, Integer length) {
		EngineSingleton.getInstance().setSelection(start, length);
	}
	
	//@Override
	@POST
	@Path("/insert")
	@Consumes(MediaType.APPLICATION_JSON)
	@Produces(MediaType.APPLICATION_JSON)
	public Response insert(final String s) {
		EngineSingleton.getInstance().insert(s);
		return Response.ok().build();
	}

	//@Override
	@GET
	@Path("/contents")
	@Produces(MediaType.APPLICATION_JSON)
	public Response contents() {
		return Response.ok(EngineSingleton.getInstance().contents()).build();
	}
	
	//@Override
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
