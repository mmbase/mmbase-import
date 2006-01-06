/**
 * Component description interface.
 */
package nl.didactor.component;
import org.mmbase.module.core.MMObjectNode;
import org.mmbase.bridge.Cloud;
import org.mmbase.storage.search.*;
import org.mmbase.storage.search.implementation.*;
import org.mmbase.util.logging.Logger;
import org.mmbase.util.logging.Logging;
import org.mmbase.bridge.jsp.taglib.util.ContextContainer;

import org.apache.xpath.XPathAPI; //slow but usefull
import javax.xml.parsers.*;
import org.w3c.dom.*;

import java.io.*;
import java.util.*;

public abstract class Component {
    private static Logger log = Logging.getLoggerInstance(Component.class.getName());

    private static Hashtable components = new Hashtable();

    private Vector interestedComponents = new Vector();
    private MMObjectNode node;

    /** Save the settings for this component */
    private HashMap settings = new HashMap();

    /** A list of all possible setting scopes */
    private Vector scopes = new Vector();

    private HashMap scopesReferid = new HashMap();

    /** The string indicating the path for templates of this component */
    private String templatepath = null;

    /** The string indicating in which bar (application, education, provider) the cockpit menuitem must be placed */
    private String templatebar = null;

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
     * of Didactor. This method will be called every time your Didastor
     * installation is restarted.
     */
    public void init() {
        String configfile = org.mmbase.module.core.MMBaseContext.getConfigPath() +
                            File.separator + "components" +
                            File.separator + getName() + ".xml";
        log.debug("Reading component configuration from file '" + configfile + "'");
        if ((new File(configfile)).exists()) {
            try {
                DocumentBuilderFactory docBuilderFactory = DocumentBuilderFactory.newInstance();
                DocumentBuilder docBuilder = docBuilderFactory.newDocumentBuilder();
                Document doc = docBuilder.parse(configfile);
                Node rootNode = doc.getDocumentElement();
                Node componentNode = XPathAPI.selectSingleNode(rootNode, "/component");
                this.templatepath = getAttribute(componentNode, "templatepath");
                this.templatebar = getAttribute(componentNode, "templatebar");

                NodeList scopeNodes = XPathAPI.selectNodeList(componentNode, "scope");
                log.debug("Number of scopes: " + scopeNodes.getLength());
                for (int i=0; i<scopeNodes.getLength(); i++) {
                    Node scope = scopeNodes.item(i);
                    String scopeName = getAttribute(scope, "name");
                    String scopeReferid = getAttribute(scope, "referid");
                    log.debug("Scope name: " + scopeName);
                    scopes.add(scopeName);
                    scopesReferid.put(scopeName, scopeReferid);
                    NodeList settingNodes  = XPathAPI.selectNodeList(scope, "setting");
                    for (int j=0; j<settingNodes.getLength(); j++) {
                        Node settingNode = settingNodes.item(j);
                        String settingName = getAttribute(settingNode, "name");
                        String settingRef = getAttribute(settingNode, "ref");
                        if (settingName == null && settingRef != null) {
                            Setting setting = (Setting)settings.get(settingRef);
                            setting.addScope(scopeName);
                            log.debug("Added scope '" + scopeName + "' for setting '" + settingRef + "'");
                        } else if (settingName != null) {
                            String settingType = getAttribute(settingNode, "type");
                            String settingDefault = getAttribute(settingNode, "default");
                            String settingPrompt = getAttribute(settingNode, "prompt");

                            Setting setting = new Setting(settingName, settingType, settingPrompt);

                            if ("domain".equals(settingType)) {
                                NodeList options = XPathAPI.selectNodeList(settingNode, "option");
                                String domain[] = new String[options.getLength()];
                                for (int k=0; k<options.getLength(); k++) {
                                    Node option = options.item(k);
                                    String optionName = getAttribute(option, "name");
                                    domain[k] = optionName;
                                }
                                setting.setDomain(domain);
                            }
                            setting.setDefault(settingDefault);
                            setting.addScope(scopeName);
                            settings.put(settingName, setting);
                            log.debug("Added setting '" + settingName + "' of type '" + settingType + "' for scope '" + scopeName + "'");
                        }
                    }
                }
            } catch (Exception e) {
                log.error(e);
            }
        }
    }

    /**
     * Installs the component. This method will only be called once, 
     * during the first initial installation of the component. The component
     * can update objectstructures if it needs to.
     */
    public void install() {

    }

    /**
     * Returns an array of components this component depends on.
     * This should always contain at least one component: 'DidactorCore'
     */
    abstract public Component[] dependsOn();

    /**
     * This method is called just before when a new object is added to Didactor. If the component
     * needs to insert objects for this object, it can do so. 
     */
    public boolean preInsert(MMObjectNode node) {
        return true;
    }

    /**
     * This method is called just after when a new object is added to Didactor. If the component
     * needs to insert objects for this object, it can do so. 
     */
    public boolean postInsert(MMObjectNode node) {
        return true;
    }

    /**
     * This method is called just before an object is being committed.
     */
    public boolean preCommit(MMObjectNode node) {
        return true;
    }

    /**
     * This method is called right after an object is being committed.
     */
    public boolean postCommit(MMObjectNode node) {
        return true;
    }

    /**
     * This method is called just before an object is being deleted.
     */
    public boolean preDelete(MMObjectNode node) {
        return true;
    }

    /**
     * Permission framework: indicate whether or not a given operation may be done, with the
     * given arguments. The return value is a list of 2 booleans; the first boolean indicates
     * whether or not the operation is allowed, the second boolean indicates whether or not
     * this result may be cached.
    */
    public boolean[] may (String operation, Cloud cloud, Map context, String[] arguments) {
        return new boolean[]{true, true};
    }

    public String getTemplatePath() {
        return templatepath;
    }

    public String getTemplateBar() {
        return templatebar;
    }

    public String getValue(String variablename, Cloud cloud, Map context, String[] arguments) {
        return "";
    }

    /**
     * Settings: return a setting in the given context.
     * @param setting The name of the setting for which a value should be 
     * returned
     * @param context A 'Map' containing name-value pairs, that can be needed
     * to retrieve the setting value. For instance the current username or
     * education node number.
     */
    public Object getSetting(String settingName, Cloud cloud, Map context) {
        log.debug("Retrieving value for setting '" + settingName + "', with in context: " + context.keySet());
        Setting setting = (Setting)settings.get(settingName);
        if (setting == null) {
            throw new RuntimeException("Setting '" + settingName + "' is not defined for component '" + getName() + "'");
        }
        Vector scope = setting.getScope();
        String retval = setting.getDefault();

        for (int i=0; i<scope.size(); i++) {
            String scopeName = (String)scope.get(i);
            String scopeReferId = (String)scopesReferid.get(scopeName);
            log.debug("Trying on scope '" + scopeName + "' (" + scopeReferId + ")");
            int objectid = -1;
            if ("component".equals(scopeName)) {
                objectid =  node.getNumber();
            } else if (getMapValue(context, scopeReferId) != null) {
                objectid = Integer.parseInt(getMapValue(context, scopeReferId).toString());
                log.debug("" + scopeReferId + " = " + objectid);
            }

            if (objectid > 0) {
                String cloudSetting = getObjectSetting(settingName, objectid, cloud);
                if (cloudSetting != null) {
                    retval = cloudSetting;
                    log.debug("Found value: " + retval);
                }
            }
        }

        if (setting.getType() == Setting.TYPE_INTEGER) {
            return new Integer(retval);
        } else if (setting.getType() == Setting.TYPE_BOOLEAN) {
            return new Boolean(retval);
        } else if (setting.getType() == Setting.TYPE_DOMAIN) {
            return retval;
        } else {
            return retval;
        }
    }

    /**
     * Get the setting for an object and a component from MMBase. This object can be a 'people' object,
     * 'component' object, etc.
     * @param settingname The name of the setting in MMBase.
     * @param objectid The number of the node representing this object to get the component setting value for
     */
    public String getObjectSetting(String settingname, int id, Cloud cloud) {
        org.mmbase.bridge.NodeList settingNodes = null;
        if (id == node.getNumber()) {
            // direct setting to this component
            settingNodes = cloud.getNode(id).getRelatedNodes("settings");
        } else {
            org.mmbase.bridge.NodeList settingrel = nl.didactor.util.GetRelation.getRelations(id, node.getNumber(), "settingrel", cloud);

            if (settingrel.size() == 0) {
                return null;
            }
            if (settingrel.size() > 1) {
                log.warn("Too many relations from " + id + " to " + node.getNumber() +". Picking first one!");
            }
            org.mmbase.bridge.Node settingRelNode = settingrel.getNode(0);
            settingNodes = settingRelNode.getRelatedNodes("settings");
        }

        if (settingNodes == null) {
            return null;
        }

        for (int i=0; i<settingNodes.size(); i++) {
            if (settingNodes.getNode(i).getStringValue("name").equals(settingname)) {
                return settingNodes.getNode(i).getStringValue("value");
            }
        }

        return null;
    }

    public void setObjectSetting(String settingname, int id, Cloud cloud, String newValue) {
        org.mmbase.bridge.Node baseNode = null;
        if (id == node.getNumber()) {
            baseNode = cloud.getNode(id);
        } else {
            org.mmbase.bridge.NodeList settingrel = nl.didactor.util.GetRelation.getRelations(id, node.getNumber(), "settingrel", cloud);

            if (settingrel.size() == 0) {
                throw new RuntimeException("Cannot set a value for setting '" + settingname + "' if there is no settingrelation between component '" + node.getNumber() + "' and object '" + id + "'");
            }
            if (settingrel.size() > 1) {
                log.warn("Too many relations from " + id + " to " + node.getNumber() +". Picking first one!");
            }
            baseNode = settingrel.getNode(0);
        }

        org.mmbase.bridge.NodeList settingNodes = baseNode.getRelatedNodes("settings");

        for (int i=0; i<settingNodes.size(); i++) {
            org.mmbase.bridge.Node settingNode = settingNodes.getNode(i);
            if (settingNode.getStringValue("name").equals(settingname)) {
                settingNode.setValue("value", newValue);
                settingNode.commit();
                return;
            }
        }
        // not found, we need to create a new setting node.
        org.mmbase.bridge.NodeManager nm = cloud.getNodeManager("settings");
        org.mmbase.bridge.Node node = nm.createNode();
        node.setValue("name", settingname);
        node.setValue("value", newValue);
        node.commit();
        org.mmbase.bridge.RelationManager rm = cloud.getRelationManager("related");
        org.mmbase.bridge.Relation rel = rm.createRelation(baseNode, node);
        rel.commit();
    }

    /**
     * Get the setting for a user and a component from MMBase.
     * @param settingname The name of the setting in MMBase.
     * @param userid The number of the node representing this user
     */
    public String getUserSetting(String settingname, String userid, Cloud cloud, String[] arguments) {
        org.mmbase.bridge.NodeList settingrel = nl.didactor.util.GetRelation.getRelations(Integer.parseInt(userid), node.getNumber(), "settingrel", cloud);

        if (settingrel.size() == 0) {
            return "";
        }
        if (settingrel.size() > 1) {
            log.warn("Too many relations from " + userid + " to " + node.getNumber() +". Picking first one!");
        }
        org.mmbase.bridge.Node settingRelNode = settingrel.getNode(0);
        org.mmbase.bridge.NodeList settings = settingRelNode.getRelatedNodes("settings");
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

    private static String getAttribute(Node n, String attr) {
        if (n == null) {
            throw new RuntimeException("Node is null!");
        } else if (n.getAttributes() == null) {
            return null;
        } else if (n.getAttributes().getNamedItem(attr) == null) {
            return null;
        }
        return n.getAttributes().getNamedItem(attr).getNodeValue();
    }

    /**
     * Workaround for bug in MMBase 1.7, this can be removed
     * as soon as MMBase 1.8 is released.
     */
    private static Object getMapValue(Map v, String key) {
        if (v instanceof ContextContainer) {
            ContextContainer cv = (ContextContainer)v;
            try {
                return cv.get(key, true);
            } catch (Exception e) {
                return null;
            }
        } else {
            return v.get(key);
        }
    }

    public HashMap getSettings() {
        return settings;
    }

    public Vector getScopes() {
        return scopes;
    }

    public class Setting {
        public static final int TYPE_INTEGER = 1;
        public static final int TYPE_BOOLEAN = 2;
        public static final int TYPE_DOMAIN = 3;
        public static final int TYPE_STRING = 4;

        private String name;
        private int type;
        private String[] domain;
        private String defaultvalue;
        private Vector scope;
        private String prompt;

        public Setting(String name, String type, String prompt) {
            this.name = name;
            if ("integer".equals(type)) {
                this.type = TYPE_INTEGER;
            } else if ("boolean".equals(type)) {
                this.type = TYPE_BOOLEAN;
                this.domain = new String[]{"true", "false"};
            } else if ("domain".equals(type)) {
                this.type = TYPE_DOMAIN;
            } else {
                this.type = TYPE_STRING;
            }
            this.scope = new Vector();
            this.prompt = prompt;
        }

        public void setDefault(String defaultvalue) {
            this.defaultvalue = defaultvalue;
        }

        public void setDomain(String[] domain) {
            this.domain = domain;
        }

        public void addScope(String scopeName) {
            scope.add(scopeName);
        }

        public Vector getScope() {
            return scope;
        }

        public String getDefault() {
            return defaultvalue;
        }

        public int getType() {
            return type;
        }

        public String getName() {
            return name;
        }

        public String getPrompt() {
            return prompt;
        }

        public String[] getDomain() {
            return domain;
        }
    }
}
