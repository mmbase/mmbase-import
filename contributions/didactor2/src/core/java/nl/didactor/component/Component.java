/**
 * Component description interface.
 */
package nl.didactor.component;
import org.mmbase.module.core.MMObjectNode;
import org.mmbase.bridge.*;
import org.mmbase.storage.search.*;
import org.mmbase.storage.search.implementation.*;
import org.mmbase.util.logging.Logger;
import org.mmbase.util.logging.Logging;

import java.util.*;

public abstract class Component {
    private static Logger log = Logging.getLoggerInstance(Component.class.getName());

    private static Hashtable components = new Hashtable();

    private Vector interestedComponents = new Vector();
    private MMObjectNode node;

    /**
     * Register a component in the registry.
     */
    public static void register(String name, Component component) {
        components.put(name, component);
    }

    /**
     * Retrieve a component from the registry.
     */
    public static Component getComponent(String name) {
        return (Component)components.get(name);
    }

    public static Component[] getComponents() {
        Component[] comps = new Component[components.size()];
        int cnt = 0;
        for (Enumeration e = components.elements(); e.hasMoreElements(); ) {
            comps[cnt] = (Component)e.nextElement();
            cnt++;
        }
        log.info("Returning " + comps.length + " components");
        return comps;
    }

    public void setNode(MMObjectNode node) {
        this.node = node;
    }

    /**
     * Returns the version of the component
     */
    abstract public String getVersion();

    /**
     * Returns the name of the component
     */
    abstract public String getName();

    /**
     * Initializes the component. This is called during startup 
     * of Didactor.
     */
    public void init() {

    }

    /**
     * Returns an array of components this component depends on.
     * This should always contain at least one component: 'DidactorCore'
     */
    abstract public Component[] dependsOn();

    /**
     * This method is called when a new object is added to Didactor. If the component
     * needs to insert objects for this object, it can do so. 
     */
    public boolean notifyCreate(MMObjectNode node) {
        return true;
    }

    /**
     * This method is called just before an object is being removed from Didactor.
     * A component should implement this method to clean up all objects created in
     * the #link{notifyCreate} method.
     */
    public boolean notifyDelete(MMObjectNode node) {
        return true;
    }

    /**
     * Permission framework: indicate whether or not a given operation may be done, with the
     * given arguments. The return value is a list of 2 booleans; the first boolean indicates
     * whether or not the operation is allowed, the second boolean indicates whether or not
     * this result may be cached.
     */
    abstract public boolean[] may (String operation, Cloud cloud, Map context, String[] arguments);

    /**
     * Settings: return a setting in the given context.
     * @param setting The name of the setting for which a value should be 
     * returned
     * @param context A 'Map' containing name-value pairs, that can be needed
     * to retrieve the setting value. For instance the current username or
     * education node number.
     */
    abstract public String getSetting(String setting, Cloud cloud, Map context, String[] arguments);

    /**
     * Get the setting for a user and a component from MMBase.
     * @param settingname The name of the setting in MMBase.
     * @param userid The number of the node representing this user
     */
    public String getUserSetting(String settingname, String userid, Cloud cloud, String[] arguments) {
        NodeList settingrel = nl.didactor.util.GetRelation.getRelations(Integer.parseInt(userid), node.getNumber(), "settingrel", cloud);

        if (settingrel.size() == 0) {
            return "";
        }
        if (settingrel.size() > 1) {
            log.warn("Too many relations from " + userid + " to " + node.getNumber() +". Picking first one!");
        }
        Node settingRelNode = settingrel.getNode(0);
        NodeList settings = settingRelNode.getRelatedNodes("settings");
        for (int i=0; i<settings.size(); i++) {
            if (settings.getNode(i).getStringValue("name").equals(settingname)) {
                return settings.getNode(i).getStringValue("value");
            }
        }
        return "";
    }


    /**
     * Register a component as interested. This can be used for example in the 'getSetting' or 'may' method
     * to retrieve a setting based on some extra installed components.
     */
    public void registerInterested(Component comp) {
        interestedComponents.add(comp);
    }
}
