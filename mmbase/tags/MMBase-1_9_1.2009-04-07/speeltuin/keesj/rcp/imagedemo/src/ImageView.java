
import java.awt.Graphics;
import java.awt.Image;
import java.awt.MediaTracker;
import java.awt.Toolkit;
import java.net.MalformedURLException;
import java.net.URL;

import javax.swing.JComponent;

public class ImageView extends JComponent {
	private Image image;
	public ImageView(){
	    
	}
	
	
	public void setImage(String fileName) throws MalformedURLException {
		Toolkit toolkit = Toolkit.getDefaultToolkit();
		image = toolkit.getImage(new URL(fileName));
		
		MediaTracker mediaTracker = new MediaTracker(this);
		mediaTracker.addImage(image, 0);
		try
		{
			mediaTracker.waitForID(0);			
		}
		catch (InterruptedException ie)
		{
			System.err.println(ie);
			System.exit(1);
		}
		
	}

	public void paint(Graphics graphics) {
	    if (image != null){
		 graphics.drawImage(image, 0, 0,this);
	    } else {
	        graphics.drawString("empty",20,20);
	    }
	}

}
