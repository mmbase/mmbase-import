
// Generated by ThinG on Jan 17, 2005 8:54:19 PM.

import java.io.InputStreamReader;
import java.net.MalformedURLException;
import java.net.URL;

import simplexml.XMLElement;
import thinlet.Thinlet;
import thinlet.FrameLauncher;

/**
 * A thinlet with the contents of "<code>untitled.xml</code>".
 */
public class Images extends Thinlet
{
    
    /**
     * Create a new MMBase instance.
     */
    public Images() throws java.io.IOException
    {
        add(parse("images.xml"));
        load();
    }
    
    
    
    public void load(){
        try {
            URL url = new URL("http://elgris.xs4all.nl/imgbrowser/tekst.jsp");
            XMLElement xmle = new XMLElement();
            xmle.parseFromReader(new InputStreamReader(url.openStream()));
            
            Object tree = find("images");
            for (int x =0 ; x < xmle.countChildren(); x ++){
                XMLElement child = xmle.getChildAt(x);
                Object node = create("node");
                setString(node, "text", child.getProperty("title"));
                putProperty(node,"url", child.getProperty("url"));
                //setIcon(node, "icon", getIcon("/icon/library.gif"));
                add(tree,node, 0);
            }
            
            
        } catch (Exception e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
    }
    
	public void showImage() {
	    Object tree = find("tree");
	    Object selected = getSelectedItem(tree);
	    Object url = getProperty(selected,"url");
	    if (url != null){
	        ImageView view = (ImageView)getComponent(find("imageview"),"bean");
	        try {
                view.setImage("http://elgris.xs4all.nl" + url);
                repaint();
                //System.err.println("select" + url);
            } catch (MalformedURLException e) {
                // TODO Auto-generated catch block
                e.printStackTrace();
            }

	    }
	}
	


    /**
     * Show a test frame with the Thinlet contents.
     *
     * @param args  ignored.
     */
    public static void main(String[] args) throws java.io.IOException
    {
        new FrameLauncher("MMBase", new Images(), 400, 400);
    }
    
}
