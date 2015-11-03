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
		EngineSingleton.getInstance();
	}
	
	@GET
	@Path("/init")
	@Produces(MediaType.TEXT_PLAIN)
	public Response init(){
		// Do nothing, but the constructor will be called and singleton registered in the RMIregistry.
		return Response.ok().build();
	}
	
	//@Override
	@GET
	@Path("/cut")
	@Produces(MediaType.APPLICATION_JSON)
	public Response cut() {
		EngineSingleton.getInstance().cut();
		return Response.ok().build();
	}

	//@Override
	@GET
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
	public Response setSelection(final int[] selection) {
		//System.out.println("Selection parameters "+selection[0]+" "+selection[1]);
		try{ 
			return Response.ok().build();
		}finally{
			EngineSingleton.getInstance().setSelection(selection[0], selection[1]);
		}
		
	}
	
	//@Override
	@POST
	@Path("/insert")
	@Consumes(MediaType.TEXT_PLAIN)
	//@Produces(MediaType.APPLICATION_JSON)
	public Response insert(final String s) {
		try{
			return Response.noContent().build();
		}
		finally{
			EngineSingleton.getInstance().insert(s);
		}
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
	@Produces(MediaType.TEXT_PLAIN)
	public Response evaluate(){
		return Response.ok(EngineSingleton.getInstance().evaluate()).build();
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
