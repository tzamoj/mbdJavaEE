package editorEngine;

import javax.ws.rs.Consumes;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

import editorUser.Observer;

/* By Thomas Zamojski, Nov 6, 2015
 * 
 * The EditorEngineImpl has the role of a communication intermediary between a controller and the 
 * EditorSingleton. It receives http requests from the controller, and simply invoke the right method
 * from the singleton.
 */
@Path("/editor")
public class EditorEngineImpl {
	
	public EditorEngineImpl(){
		EngineSingleton.getInstance();
	}
	
	@GET
	@Path("/init")
	@Produces(MediaType.TEXT_PLAIN)
	public Response init(){ 
		// called at creation of a controller to connect to the server. 
		// instanciate an EngineSingleton that in particular binds itself to the rmi registry.
		EngineSingleton.getInstance();
		return Response.ok().build();
	}
	
	@GET
	@Path("/cut")
	@Produces(MediaType.APPLICATION_JSON)
	public Response cut() {
		EngineSingleton.getInstance().cut();
		return Response.ok().build();
	}

	@GET
	@Path("/copy")
	@Produces(MediaType.APPLICATION_JSON)
	public Response copy() {
		EngineSingleton.getInstance().copy();
		return Response.ok().build();
	}

	@GET
	@Path("/paste")
	@Produces(MediaType.APPLICATION_JSON)
	public Response paste() { 
		EngineSingleton.getInstance().paste();
		return Response.ok().build();
	}

	@POST
	@Path("/setSelection")
	@Produces(MediaType.APPLICATION_JSON)
	public Response setSelection(final int[] selection) {
		EngineSingleton.getInstance().setSelection(selection[0], selection[1]);
		return Response.ok().build();
	}
	
	@POST
	@Path("/insert")
	@Consumes(MediaType.TEXT_PLAIN)
	public Response insert(final String s) {
			EngineSingleton.getInstance().insert(s);
			return Response.noContent().build();
	}

	@GET
	@Path("/contents")
	@Produces(MediaType.TEXT_PLAIN)
	public Response contents() {
		return Response.ok(EngineSingleton.getInstance().contents()).build();
	}
	
	@GET
	@Path("/evaluate")
	@Produces(MediaType.TEXT_PLAIN)
	public Response evaluate(){
		return Response.ok(EngineSingleton.getInstance().evaluate()).build();
	}
	
	/* Not needed anymore, only the singleton registers to RMI registry.
	 * @Override
	public void addObserver(Observer o){
		//EngineSingleton.getInstance().addObserver(o);
	}
	
	@Override
	public void removeObserver(Observer o){
		//EngineSingleton.getInstance().removeObserver(o);
	}*/
}
