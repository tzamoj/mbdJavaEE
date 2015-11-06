package editorEngine;

import java.rmi.registry.LocateRegistry;
import java.rmi.registry.Registry;
import java.util.logging.Logger;
import org.jboss.resteasy.plugins.server.undertow.UndertowJaxrsServer;
import io.undertow.Undertow;

/**
 * RESTfull microservice, based on JAX-RS and JBoss Undertow
 *
 */
public class RestServer {

    private static final Logger logger = Logger.getLogger(RestServer.class.getName());

    public static void main( String[] args ) {
        UndertowJaxrsServer ut = new UndertowJaxrsServer();
        EnsaiApplication ta = new EnsaiApplication();
        ut.deploy(ta);
        ut.start(
                Undertow.builder()
                        .addHttpListener(8080, "localhost")
        );

        logger.info("JAX-RS based micro-service running!");
        
        // Launch the RMI registry
        if (System.getSecurityManager() == null)
			System.setSecurityManager(new SecurityManager());
        try{
        	Registry rmiRegistry = LocateRegistry.createRegistry(9999);
        }catch(Exception e){
        	e.printStackTrace();
        }
        
   
        
        // Could launch Singleton, Renjin and persistance, but for efficiency better do it if there is at least one client.  
        //EngineSingleton.getInstance();
        //logger.info("Singleton is up");
    }
}

