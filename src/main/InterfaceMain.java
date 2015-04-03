package main;

import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.GridLayout;
import java.awt.Toolkit;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.rmi.RemoteException;

import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JTextField;

import node.SiteImpl;

/**
 * Create a JFrame which allow to the utilisateur to:
 *  - create a registry RMI
 * 	- create instance of node (SiteImpl) in a Tree form or Graph form
 *  - create link between the nodes
 *  - send message to a specific node which after send it to all nodes
 * @author florian Malapel & Antonin Noel
 */
public class InterfaceMain {
	
	static String[] entry;
	static JFrame frame = new JFrame();
	static JPanel mainPanel = new JPanel();
	static JPanel add = new JPanel(new BorderLayout());
	static JLabel addSite = new JLabel("Create a SiteImpl (args: [-s for create server] id) ");
	static JTextField fieldAddSiteID = new JTextField();
	static JCheckBox checkOPT = new JCheckBox("-s");
	static JButton buttonAddSite = new JButton("Create");
	static JPanel addLink = new JPanel(new BorderLayout());
	static JLabel addLinkLabel = new JLabel("Add link between (args: father child): ");
	static JTextField addFather = new JTextField();
	static JTextField addChild = new JTextField();
	static JButton buttonAddLink = new JButton("Create");
	static JPanel addMessage = new JPanel(new BorderLayout());
	static JLabel messageLabel = new JLabel("Receiver id + message:");
	static JTextField sendTo = new JTextField();
	static JTextField message = new JTextField();
	static JButton send = new JButton(" Send ");
	
	
	static ActionListener listener = new ActionListener() {
		@Override
		public void actionPerformed(ActionEvent e) {
			if( e.getSource() == buttonAddSite ){
				createSite();
			}
			else if(e.getSource() == buttonAddLink){
				createLink();
			}
			else if(e.getSource() == send){
				sendMessage();
			}
		}
	};
	
	static public void createLink(){
		if(addFather.getText() != "" && addChild.getText() != "" ){
			entry = new String[2];
			entry[0] = addFather.getText();
			entry[1] = addChild.getText();
			CreateLink.main(entry);
		}
		addFather.setText("");
		addChild.setText("");
	}
	
	static public void createSite(){
		try {
			if(fieldAddSiteID.getText() != null){
				if(checkOPT.isSelected()){
					entry = new String[2];
					entry[0] = new String("-s");
					entry[1] = fieldAddSiteID.getText();
				} else {
					entry = new String[1];
					entry[0] = fieldAddSiteID.getText();
				}
				checkOPT.setSelected(false);
				fieldAddSiteID.setText(null);
			}
			SiteImpl.main(entry);
		} catch (RemoteException re) {
			re.printStackTrace();
		} catch (Exception ex) {
			ex.printStackTrace();
		}
	}
	
	static public void sendMessage(){
		try {
			if(message.getText() != "" && sendTo.getText() != ""){
				entry = new String[2];
				entry[0] = sendTo.getText();
				entry[1] = message.getText();
				Sender.main(entry);
			}
		} catch (Exception ex) {
			ex.printStackTrace();
		}
	}

	public static void main(String[] args) {
		Dimension screenSize = Toolkit.getDefaultToolkit().getScreenSize();
		double widthScreen = screenSize.getWidth();
		double heightScreen = screenSize.getHeight();
		
		frame.setTitle("Handler");
		frame.setSize((int)(widthScreen*0.40), (int)(heightScreen*0.2));
		frame.setLocationRelativeTo(null);
		frame.setResizable(false);
		frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		
		
		// Create Panel
		GridLayout grid = new GridLayout(3, 3);
		mainPanel.setLayout(grid);
		
		// Interface to add SiteImpl
		checkOPT.setSelected(true);
		add.add(addSite, BorderLayout.WEST);
		JPanel field = new JPanel(new FlowLayout());
		fieldAddSiteID.setColumns(2);
		field.add(checkOPT);
		field.add(fieldAddSiteID);
		add.add(field);
		add.add(buttonAddSite, BorderLayout.EAST);
		mainPanel.add(add);
		
		// Interface to add a link
		addLink.add(addLinkLabel, BorderLayout.WEST);
		JPanel fieldLink = new JPanel(new FlowLayout());
		addFather.setColumns(2);
		addChild.setColumns(2);
		fieldLink.add(addFather);
		fieldLink.add(addChild);
		addLink.add(fieldLink);
		addLink.add(buttonAddLink, BorderLayout.EAST);
		mainPanel.add(addLink);
		
		// Interface to send a message
		JPanel fieldMessage = new JPanel(new FlowLayout());
		sendTo.setColumns(2);
		message.setColumns(20);
		fieldMessage.add(sendTo);
		fieldMessage.add(message);
		addMessage.add(messageLabel, BorderLayout.WEST);
		addMessage.add(fieldMessage, BorderLayout.CENTER);
		addMessage.add(send, BorderLayout.EAST);
		mainPanel.add(addMessage);
		
		frame.setContentPane(mainPanel);
		frame.setVisible(true);
		
		buttonAddSite.addActionListener(listener);
		buttonAddLink.addActionListener(listener);
		send.addActionListener(listener);
	}
}
