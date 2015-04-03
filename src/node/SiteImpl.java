package node;

import java.rmi.AlreadyBoundException;
import java.rmi.NotBoundException;
import java.rmi.RemoteException;
import java.rmi.registry.LocateRegistry;
import java.rmi.registry.Registry;
import java.rmi.server.UnicastRemoteObject;
import java.util.ArrayList;

import serveur.Server;
import view.SiteFrame;

/**
 * Class which describe an UnicastRemoteObject,
 * There is a main for the SiteImpl object creation.
 * @author florian Malapel & Antonin Noel
 */
public class SiteImpl extends UnicastRemoteObject implements SiteItf{

	private final int PORT = 4242;
	private Registry registry;
	private int id;
	private ArrayList<SiteItf> children;
	private ArrayList<SiteItf> fathers;
	private String name;
	private boolean getInformation;
	private ArrayList<Thread> threads;
	public SiteFrame frame = null;
	
	// === Constructors === // 
	public SiteImpl() throws RemoteException {
		super();
		children = new ArrayList<SiteItf>();
		fathers = new ArrayList<SiteItf>();
		threads = new ArrayList<Thread>();
		name = new String("Node unknown");
		id = -1;
		getInformation = false;
		// Get the registry
		registry = LocateRegistry.getRegistry(PORT);
	}
	
	/**
	 * Create a SiteImpl
	 * @param _id the id of the SiteImpl
	 * @throws RemoteException
	 */
	public SiteImpl(int _id) throws RemoteException {
		super();
		id = _id;
		children = new ArrayList<SiteItf>();
		fathers = new ArrayList<SiteItf>();
		threads = new ArrayList<Thread>();
		name = new String("Node#" + id);
		getInformation = false;
		frame = new SiteFrame(name,id);

		// Get the registry
		registry = LocateRegistry.getRegistry(PORT);
		// TODO maybe not rebind ??
		try {
			registry.bind(this.name, this);
			frame.updateLog(getName() + ": registred.");
			System.out.println(getName() + ": registred.");
		} catch (AlreadyBoundException e) {
			System.out.println("Error bind " + name + " : " + e.getLocalizedMessage() );
		}
	}

	
	// === Methods === //
	
	public synchronized void sendMessage(final int sourceID, final byte[] msg) throws RemoteException{
		// If it already got the information (message), return.
		if(getInformation){
			frame.updateLog("Hey, I just already receive the message, bye.");
			System.out.println("Hey, I just already receive the message, bye.");
			return;
		}
		// If not, now it get it.
		getInformation = true;
		// Display the message that the node just receive
		displayTraceMsg(msg); 
		// Send the message to the father in the case that we begin in a child
		for(final SiteItf father: fathers){
			if(!father.isGetInformation()){
				Thread thread = new Thread(){
					public void run(){
						try {
							father.sendMessage((int) this.getId(), msg);
						}catch( Exception e ){
							System.out.println("Error while " + getName() + " was sending the message... ");
							e.printStackTrace();
						}
					}
				};
				thread.start();
				this.threads.add(thread);
			}
		}
		// Send the message to all of the children
		for(final SiteItf child: children){
			if( sourceID != child.getId() && !child.isGetInformation()){
				Thread thread = new Thread(){
					public void run(){
						try {
							child.sendMessage((int) this.getId(), msg);
						}catch( Exception e ){
							System.out.println("Error while " + getName() + " was sending the message...");
							e.printStackTrace();
						}
					}
				};
				thread.start();
				this.threads.add(thread);
			}
		}
		// Now we wait the end of all thread in this.threads
		for(Thread t: threads){
			try {
				t.join();
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
		}
	}
	
	
	public String toString(){
		try {
			return name + " - Father: " + displayList(fathers) + " - Children: " + displayList(children);
		} catch (RemoteException e) {
			e.printStackTrace();
		}
		return "Error";
	}
	

	public void displayTraceMsg(byte[] msg) throws RemoteException{
		frame.updateLog(getName() + " receive: " + new String(msg));
		System.out.println(getName() + " receive: " + new String(msg));
	}
		
	
	// --- Getters & Setters --- //
	public boolean isGetInformation() throws RemoteException{
		return getInformation;
	}

	public void setGetInformation(boolean getInformation) throws RemoteException{
		this.getInformation = getInformation;
	}
	
	public Registry getRegistry() throws RemoteException{
		return registry;
	}

	public void setRegistry(Registry registry) throws RemoteException{
		this.registry = registry;
	}

	public int getId() throws RemoteException{
		return id;
	}

	public void setId(int id) throws RemoteException{
		this.id = id;
	}

	public ArrayList<SiteItf> getChildren() throws RemoteException{
		return children;
	}

	public void setChildren(ArrayList<SiteItf> children) throws RemoteException{
		this.children = children;
	}

	public ArrayList<SiteItf> getFathers() throws RemoteException{
		return fathers;
	}
	
	public void setFathers( ArrayList<SiteItf> _fathers ) throws RemoteException{
		fathers = _fathers;
	}
	
	public SiteItf getAfather(int i) throws RemoteException{
		return fathers.get(i);
	}
	
	public void deleteAfather(int i) throws RemoteException{
		fathers.remove(i);
	}

	public String getName() throws RemoteException {
		return name;
	}

	public void addAfather(SiteItf father) throws RemoteException {
		this.fathers.add(father);
	}
	
	public void setName(String name) throws RemoteException {
		this.name = name;
	}
	
	
	public void addChild(SiteItf child) throws RemoteException{
		if(!children.contains(child)){
			children.add(child);
			frame.updateLog(this.getName() + " is now the father of " + child.getName());
			System.out.println(this.getName() + " is now the father of " + child.getName());
		}
	}
	
	public void removeChild(SiteItf child) throws RemoteException{
		children.remove(child);
	}


	public String displayList( ArrayList<SiteItf> a ) throws RemoteException{
		String tmp = new String();
		for(SiteItf s: a){
			tmp += s.getName() + " ";
		}
		return tmp;
	}
	
	
	public void clearMessageForAll() throws RemoteException{
		String[] list = registry.list();
		ArrayList<SiteItf> listSite = new ArrayList<SiteItf>();
		for(String name: list){
			try {
				listSite.add((SiteItf)registry.lookup(name));
			} catch (NotBoundException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
		for(SiteItf site: listSite){
			site.setGetInformation(false);
		}
	}
	
	
	// === MAIN === //
	/**
	 * @param args
	 * 		args[0] --> option: -s to create the Server (Registry) before the SiteImpl object
	 * 		args[1] --> id of the node you want to create
	 * @throws Exception
	 */
	public static void main( String[] args ) throws Exception {
		int id;
		String alert = "You're wrong mate,\nyou "
				+ "should use the command this way: Command option id\n"
				+ "\tid --> the id of the node you want to create\n"
				+ "\toption:\n\t\t-s --> to launch a Server if it's not already done ";
		// If the program is not well called
		if( args.length < 1 && args.length >= 0 ){
			System.err.println(alert);
			return;
		}
		// If we need to create the Server (Registry)
		if( args[0].equals("-s") ){
			Server server = new Server();
			id = Integer.parseInt(args[1]);
		} else 
			id = Integer.parseInt(args[0]);
		new SiteImpl(id);
	}
} 
