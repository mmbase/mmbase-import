package nl.eo.batchupload;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.util.Enumeration;
import java.util.Hashtable;
import java.util.Vector;

import nanoxml.XMLElement;

/**
 * Wrapper around the configuration file. This configfile (config.xml) is not
 * yet configurable.
 * @author Johannes Verelst <johannes@verelst.net>
 * @version $ID$
 */
public class Config {
    static String configfilename = "config.xml";
    static File configfile = new File(configfilename);
    static Hashtable configvalues = new Hashtable();

    /**
     * Public constructor
     */
    public Config() {
        
    }

    /**
     * Return whether or not the configfile exists
     * @return whether or not the configfile exists
     */
    public static boolean exists() {
        return configfile.exists();
    }
    
    /**
     * Open the configfile, and read all values. All values are stored internally,
     * they can be modified but a call to the {@link #store()} method is needed
     * to write the new config to disk. 
     */
    public static void open() {
        XMLElement e = new XMLElement();
        try {
            e.parseFromReader(new FileReader(configfile));
            Vector children = e.getChildren();
            for (int i=0; i < children.size(); i++) {
                XMLElement child = (XMLElement)children.get(i);
                configvalues.put(child.getName(), child.getContent());
            }
        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }
    
    /**
     * Store the current configuration values in the config file. You will need to
     * call this method after changing any configuration data through the API.
     */
    public static void store() {
        XMLElement n = new XMLElement();
        n.setName("config");
        Enumeration keys = configvalues.keys();
        while (keys.hasMoreElements()) {
            String key = (String)keys.nextElement();
            XMLElement child = new XMLElement();
            child.setName(key);
            child.setContent((String)configvalues.get(key));
            n.addChild(child);
        }
        try {
            FileWriter fw = new FileWriter(configfile);
            fw.write(n.toString());
            fw.flush();
            fw.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * Return the configuration value for a given keyname
     * @param keyname The name of the parameter to return
     * @return A string containing the configuration value
     */
    public static String getValue(String keyname) {
        return (String)configvalues.get(keyname);
    }
    
    /**
     * Set a configuration value. Note that you need to call the {@link #store()}
     * method to write the changed value to disk.
     * @param keyname The name of the parameter to change
     * @param value The new value for the parameter
     */
    public static void setValue(String keyname, String value) {
        if (configvalues.containsKey(keyname)) {
            configvalues.remove(keyname);
        }
        configvalues.put(keyname, value);
    }
}
