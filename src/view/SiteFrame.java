package view;

import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.GridBagLayout;
import java.awt.Toolkit;

import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;

/**
 * Class which represents a view of a SiteImpl object
 * @author florian Malapel & Antonin Noel
 */
public class SiteFrame extends JFrame {
	
	private String name;
	private int id;
	
	int width, height;
	private JTextArea content = null;
	private JScrollPane scrollContent = null;
	
	public SiteFrame(String _name, int _id){
		name = _name;
		id = _id;
		createFrame();
	}
	
	public void createFrame(){
		Dimension screenSize = Toolkit.getDefaultToolkit().getScreenSize();
		double widthScreen = screenSize.getWidth();
		double heightScreen = screenSize.getHeight();
		this.width =  (int)(widthScreen / 5);
		this.height =  (int)(heightScreen / 8);
		this.setSize(new Dimension(width, height));
		this.setLocation((int)(widthScreen-width), (id-1)*height);
		this.setTitle(name);
		this.setLayout(new FlowLayout());
		this.setAlwaysOnTop(true);
		this.setDefaultCloseOperation(EXIT_ON_CLOSE);
		
		
		this.setVisible(true);
	}
	
	public void updateLog(String news){
		if(content == null){
			content = new JTextArea("",5, 20);
			content.setEditable(false);
			scrollContent = new JScrollPane(content);
			content.setText(news);
			this.add(scrollContent);
		}
		else 
			content.setText(content.getText() + "\n" + news );
		this.invalidate();
		this.validate();
	}
}
