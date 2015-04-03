package node;

import java.rmi.Remote;
import java.rmi.RemoteException;
import java.rmi.registry.Registry;
import java.util.ArrayList;
/**
 * Interface which describe a Remote object
 * @author florian Malapel & Antonin Noel
 */
public interface SiteItf extends Remote {
	// TODO: make it better
	
	/**
	 * Get a message in input and send this message to all its fathers and children 
	 * @param sourceID --> ID of the node which send the message
	 * @param msg --> The message to send
	 */
	public void sendMessage(int sourceID, final byte[] msg) throws RemoteException;
	
	/**
	 * Print the message in input
	 * @param msg 
	 * @throws RemoteException
	 */
	public void displayTraceMsg(byte[] msg) throws RemoteException;
	
	/**
	 * Print the ArrayList in input
	 */
	public String displayList(ArrayList<SiteItf> list) throws RemoteException;
	
	/**
	 * Put the boolean 'getInformation' of all the SiteImpl to false
	 * to allow to send other message.
	 */
	public void clearMessageForAll() throws RemoteException;
	
	//* Getters & Setters *//
	public Registry getRegistry() throws RemoteException;
	public void setRegistry(Registry registry) throws RemoteException;
	public int getId() throws RemoteException;
	public void setId(int id) throws RemoteException;
	public ArrayList<SiteItf> getChildren() throws RemoteException;
	public void setChildren(ArrayList<SiteItf> children) throws RemoteException;
	public String getName() throws RemoteException;
	public void setName(String name) throws RemoteException;
	public void addChild(SiteItf child) throws RemoteException;
	public ArrayList<SiteItf> getFathers() throws RemoteException;
	public void setFathers( ArrayList<SiteItf> _fathers ) throws RemoteException;
	public SiteItf getAfather(int i) throws RemoteException;
	public void addAfather(SiteItf father) throws RemoteException;
	public void deleteAfather(int i) throws RemoteException;
	public void removeChild(SiteItf child) throws RemoteException;
	public boolean isGetInformation() throws RemoteException;
	public void setGetInformation(boolean getInformation) throws RemoteException;
	
	
}
