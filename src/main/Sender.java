package main;

import java.rmi.NotBoundException;
import java.rmi.RemoteException;
import java.rmi.registry.LocateRegistry;
import java.rmi.registry.Registry;

import node.SiteItf;

/**
 * Class with a main which send a message to a node informed in entry
 * @author florian Malapel & Antonin Noel
 */
public class Sender {

	/**
	 * @param args
	 * 		args[0] --> the node's id of the receiver
	 * 		args[1] and > args[1] --> the message to send
	 * @throws NotBoundException
	 */
	public static void main(String[] args) throws NotBoundException {
		//TODO: allow to send multiple messages.
		// PORT for the registry
		final int PORT = 4242;
		// Alert message if the program it is not well called
		String alert = "You're wrong mate,\nyou "
				+ "should use the command this way: Command id message\n"
				+ "\tid: the node's id which is the receiver\n"
				+ "\tmessage: the message to send";
		
		// If the program is not well called
		if( args.length < 2 && args.length >= 0 ){
			System.err.println(alert);
			return;
		}
		// Get id of the node
		int id = Integer.parseInt(args[0]);
		String message = new String();
		// Get the message
		for(int i=1; i<args.length; i++){
			message += args[i] + " ";
		}
		
		// Get the registry & send the message 
		Registry registry;
		try {
			registry = LocateRegistry.getRegistry(PORT);
			SiteItf sender = (SiteItf) registry.lookup("Node#" + id);
			sender.sendMessage(id, message.getBytes());
			sender.clearMessageForAll();
		}catch(RemoteException e){
			System.out.println("I'm looking for but I can't find the Sender...\n"
					+ "Error: " + e.getLocalizedMessage());
			e.printStackTrace();
		}
		
	}

}
