package editorEngine;

import java.rmi.registry.LocateRegistry;
import java.rmi.registry.Registry;
import java.rmi.server.UnicastRemoteObject;

public class rmiServerInit {
	private static EditorEngineImpl engine = new EditorEngineImpl();
	
	public static void main(String[] args){
	     if (System.getSecurityManager() == null)
	            System.setSecurityManager(new SecurityManager());
	        try {
	            Registry rmiRegistry = LocateRegistry.createRegistry(9999);
	            EditorEngine rmiService = (EditorEngine) UnicastRemoteObject
	                    .exportObject(engine, 9999);
	            rmiRegistry.bind("engine", rmiService);
	        } catch (Exception ex) {
	            ex.printStackTrace();
	        }
	}
}
