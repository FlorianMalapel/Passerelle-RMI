package main;

import java.rmi.NotBoundException;
import java.rmi.RemoteException;
import java.rmi.registry.LocateRegistry;
import java.rmi.registry.Registry;

import node.SiteItf;


/**
 * Class with a main which create a link between the 2 nodes ID in entry.
 * @author florian Malapel & Antonin Noel
 */
public class CreateLink {
	
	/**
	 * @param args
	 * 		args[0] --> the node father's ID
	 * 		args[1] --> the node child's ID
	 */
	public static void main(String[] args){
		// PORT for the registry
		final int PORT = 4242;
		// Alert message if the program it is not well called
		String alert = "You're wrong mate,\nyou "
				+ "should use the command this way:\nCommand [father's ID] [child's ID]\n";
		
		// If the program is not well called
		if( args.length != 2 ){
			System.err.println(alert);
			return;
		}
		
		// Get father & child 's ID
		int idChild = Integer.parseInt(args[1]);
		int idFather = Integer.parseInt(args[0]);
		
		// Get the registry, the father & child to create the link between them
		Registry registry;
		try {
			registry = LocateRegistry.getRegistry(PORT);
			SiteItf father = (SiteItf) registry.lookup("Node#" + idFather);
			SiteItf child = (SiteItf) registry.lookup("Node#" + idChild);
			father.addChild(child);
			child.addAfather(father);
			System.out.println("Link between " + father.getName() + " and " + child.getName() + ": done.");
		} catch (RemoteException | NotBoundException e) {
			System.out.println("Creation of the link: fail.");
			e.printStackTrace();
		}
	}
}
