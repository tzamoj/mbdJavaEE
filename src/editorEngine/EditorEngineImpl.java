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
	@Path("/cut")
	@Produces(MediaType.APPLICATION_JSON)
	public Response cut() {
		EngineSingleton.getInstance().cut();
		return Response.ok().build();
	}

	//@Override
	@POST
	@Path("/copy")
	@Produces(MediaType.APPLICATION_JSON)
	public Response copy() {
		EngineSingleton.getInstance().copy();
		return Response.ok().build();
	}

	//@Override
	@GET
	@Path("/paste")
	@Produces(MediaType.APPLICATION_JSON)
	public Response paste() { 
		EngineSingleton.getInstance().paste();
		return Response.ok().build();
	}

	//@Override
	@POST
	@Path("/setSelection")
	@Produces(MediaType.APPLICATION_JSON)
	public Response setSelection(Integer start, Integer length) {
		EngineSingleton.getInstance().setSelection(start, length);
		return Response.ok().build();
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
	@Produces(MediaType.TEXT_PLAIN)
	public Response contents() {
		return Response.ok(EngineSingleton.getInstance().contents()).build();
	}
	
	//@Override
	@GET
	@Path("/evaluate")
	@Produces(MediaType.APPLICATION_JSON)
	public Response evaluate(){
		EngineSingleton.getInstance().evaluate();
		return Response.ok().build();
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
