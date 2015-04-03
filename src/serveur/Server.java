package serveur;

import java.rmi.registry.LocateRegistry;
import java.rmi.registry.Registry;

/**
 * Class which initialize a resgistry RMI
 * @author florian Malapel & Antonin Noel
 */
public class Server {
	
	// PORT for the Registry
	private final int PORT = 4242;
	
	/** Create & active the server **/
	public Server() throws Exception {
		try {
			Registry registry = LocateRegistry.createRegistry(PORT);
			System.out.println("Server: ok");
		} catch(Exception e) {
			System.out.println("Problem with the server: " + e.getLocalizedMessage());
		}
		
	}
	
}
