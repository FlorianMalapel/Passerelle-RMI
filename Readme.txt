Noel Antonin
Malapel Florian
TP3-RMI

--------
Objectif
--------
	Création d'un application répartie qui permet de transférer en RMI des données à un
ensemble d'objets organisés selon une topologie en arbre ou en graphe. Chaque noeud de l'arbre
(ou du graphe) s'exécute dans sa propre instance et propage les données à ses fils.


--------
Exécuter
--------
	
	En exécutant le JAR « Interface-RMI-launcher », une interface graphique vous permet de créer des instances de SiteImpl, de créer des liens entres eux et d’envoyer des messages.
	
------------
Architecture
------------
	
	- Package: node
		- SiteImpl.java: Classe représentant un noeud de l'arbre (ou graphe), hérite de la 
			classe UnicastRemoteObject et implémente l'interface SiteItf.java.
			Cette classe permet à la fois la création d'un noeud soit d'une structure d'arbre ou
			d'une structure d'un graphe, en effet la seule différence est que dans l'arbre, le noeud possède
			un unique noeud père, alors que dans le graphe il peut y en avoir plusieurs.
			C'est pourquoi dans cette classe on trouve une liste 'fathers' (ArrayList<SiteImpl), 
			qui ne contient qu'un seul élément si on se trouve dans une structure d'arbre, ou éventuellement
			plusieurs si on se trouve dans une structure de graphe.
			
			Elle contient également un main permettant la création d'object SiteImpl, il prenant en entrée
			2 arguments ou 1 selon les différents cas:
				- Dans le cas d'une première création de SiteImpl args[0] doit prendre la valeur "-s", il s'agit
				d'une option permettant la création d'un Server RMI, args[1] doit lui prendre la valeur de l'ID
				que l'on veut donner à l'objet SiteImpl.
				- Dans le cas ou le Server RMI a déjà été créé, le main ne prend qu'un seul argument:
				args[0] --> ID du SiteImpl à créer 
				
			
		- SiteItf.java: Interface représentant un Object SiteImpl.java et permettant de les gérer entre eux.
		
	- Package: serveur
		- Server.java: Classe basique ne contenant qu'un constructeur, celui-ci permet de créer une instance de
			la classe Server et ainsi créer le 'Registry' RMI au port indiqué dans la classe.
			
	- Package: main
		- CreateLink.java: Classe qui ne contient qu'un main, celui-ci permet de récupérer 2 objets SiteItf depuis
			le 'Registry' RMI, et de mettre en place un relation de père/fils entre eux. 
			Le main prend en entrée 2 arguments:
				args[0] --> identifiant du noeud père
				args[1] --> identifiant du noeud fils
			
		- Sender.java: Classe ne contenant qu'un main, celui-ci permet d'envoyer un message à un noeud de la structure
			passé en entrée.
			main args[0] args[1...n]
				args[1...n] --> message à envoyer
				args[0] --> ID du noeud (SiteImpl) de la structure qui sera le premier à recevoir le message puis à
				le renvoyer ensuite à ses fils et parents.
	
		- InterfaceMain.java: Classe correspondant à une interface graphique permettant de:
			- créer des objets 'SiteImpl' par l'intermédiaire du main contenue dans la classe SiteImpl.java
			- créer des liens entres les différents SiteImpl en utilisant le main contenue dans la classe CreateLink.java
			- envoyer un message à un SiteImpl en utilisant le main contenue dans la classe Sender.java
		Cette classe facilite la gestion des noeuds, de leurs liens, et de leurs messages qui leurs sont envoyés. 
			
	- Package: view
		- SiteFrame.java: Petite interface graphique représentant un objet 'SiteImpl' et affichant les informations
		qui leurs correspondent.
		

-----------------
Gestion d'erreurs
-----------------

	- Pour la gestion d'erreur nous avons majoritairement utilisé 'throws RemoteException' pour éviter les problèmes 
	de communication avec le registre RMI.
	
	- Nous avons également utilisé beaucoup de try/catch avec différentes exceptions:
		- AlreadyBoundException et NotBoundException pour objects SiteImpl avec le Registre RMI
		- InterruptedException pour les Threads
		- Exception simple
		

------------
Code samples
------------

Méthode permettant à un SiteImpl d'envoyer en parrallèle un message à tous ses fils et parents dans différents Threads:
Le mot clé 'synchronized' à été utilisé pour éviter l'accès de plusieurs processus à la même ressource en même temps.
-----------------------------------------------------------------------------------------------------------------------
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
	

Permet de déclencher l'envoie d'un message en choisissant quel noeud (SiteImpl) sera le premier receveur:
---------------------------------------------------------------------------------------------------------
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
	
	
Permet d'effectuer le lien entre deux noeuds d'ID choisit en entrée du main:
----------------------------------------------------------------------------
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
	

Permet de créer un SiteImpl et avec l'option (-s) de créer en même temps le server (Registre RMI):
--------------------------------------------------------------------------------------------------
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