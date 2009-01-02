/**
 * Component description interface.
 */
package nl.didactor.component;
import org.mmbase.module.core.MMObjectNode;
import org.mmbase.module.core.MMObjectBuilder;
import org.mmbase.module.core.MMBase;
import org.mmbase.bridge.Cloud;
import org.mmbase.security.Action;
import org.mmbase.storage.search.*;
import org.mmbase.storage.search.implementation.*;
import org.mmbase.util.logging.Logger;
import org.mmbase.util.logging.Logging;
import org.mmbase.util.ResourceLoader;
import org.mmbase.util.functions.Parameters;
import org.mmbase.util.functions.Parameter;
import org.mmbase.bridge.jsp.taglib.util.ContextContainer;

import javax.xml.parsers.*;
import org.w3c.dom.*;

import java.io.*;
import java.util.*;

/**
 * @javadoc
 * @version $Id: Component.java,v 1.32 2009-01-02 09:36:10 michiel Exp $
 */
public abstract class Component {
    private static final Logger log = Logging.getLoggerInstance(Component.class);

    private static final Map<String, Component> components = new HashMap<String, Component>();

    private final List<Component> interestedComponents = new ArrayList<Component>();
    private MMObjectNode node;

    protected final Map<String, Setting> settings = new HashMap<String, Setting>();
    private final Map<String, String>  scopes   = new HashMap<String, String>();

    /** The string indicating the path for templates of this component */
    private String templatepath = null;

    /** The string indicating in which bar (application, education, provider) the cockpit menuitem must be placed */
    private String templatebar = null;

    /** Location of the component in the bar. Default to 100, which is somewhere at the end. */
    private int barposition = 100;

    public static final Parameter EDUCATION = new Parameter("education", org.mmbase.bridge.Node.class, true);
    public static final Parameter CLASS     = new Parameter("class", org.mmbase.bridge.Node.class, null);

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
        return components.get(name.toLowerCase());
    }

    public static Component[] getComponents() {
        if (log.isDebugEnabled()) {
            log.debug("Returning " + components.size() + " components");
        }
        return components.values().toArray(new Component[] {});
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
     * of Didactor. This method will be called every time your Didactor
     * installation is restarted.
     */
    public void init() {
        try {
            String xml = getName() + ".xml";
            Document doc = null;
            if (ResourceLoader.getConfigurationRoot().getResource("di_components/" + xml).openConnection().getDoInput()) {
                doc = ResourceLoader.getConfigurationRoot().getDocument("di_components/" + xml, true, Component.class);
            } else if (ResourceLoader.getConfigurationRoot().getResource("components/" + xml).openConnection().getDoInput()) {
                // legacy support, didactor used to use that dir, but it is now resereved for mmbase components.
                doc = ResourceLoader.getConfigurationRoot().getDocument("components/" + xml, true, Component.class);
            }

            if (doc != null) {
                log.service("Reading component configuration from '" + doc.getDocumentURI() + "'");
                Element componentNode = (Element) doc.getDocumentElement();
                this.templatepath = getAttribute(componentNode, "templatepath");
                this.templatebar = getAttribute(componentNode, "templatebar");
                try {
                    this.barposition = Integer.parseInt(componentNode.getAttribute("barposition"));
                } catch (Exception e) {
                    log.debug(e);
                }

                NodeList childNodes = componentNode.getChildNodes();

                for (int i = 0; i < childNodes.getLength(); i++) {
                    if (childNodes.item(i).getNodeName().equals("scope")) {
                        Element scope = (Element) childNodes.item(i);
                        String scopeName = getAttribute(scope, "name");
                        String scopeReferid = getAttribute(scope, "referid");
                        log.debug("Scope name: " + scopeName);
                        scopes.put(scopeName, scopeReferid);
                        NodeList childNodes2  = scope.getChildNodes();
                        for (int j = 0; j < childNodes2.getLength(); j++) {
                            if ("setting".equals(childNodes2.item(j).getNodeName())) {
                                Element settingNode = (Element) childNodes2.item(j);
                                String settingName = getAttribute(settingNode, "name");
                                String settingRef = getAttribute(settingNode, "ref");
                                if (settingName == null && settingRef != null) {
                                    Setting setting = settings.get(settingRef);
                                    if (setting != null) {
                                        setting.addScope(scopeName);
                                        log.debug("Added scope '" + scopeName + "' for setting '" + settingRef + "'");
                                    } else {
                                        log.warn("Referring to unknown setting " + settingRef);
                                    }
                                } else if (settingName != null) {
                                    String settingType = getAttribute(settingNode, "type");
                                    String settingDefault = getAttribute(settingNode, "default");
                                    String settingPrompt = getAttribute(settingNode, "prompt");

                                    Setting setting = new Setting(settingName, settingType, settingPrompt);

                                    if ("domain".equals(settingType)) {
                                        NodeList options = settingNode.getChildNodes();
                                        List<String> domains = new ArrayList<String>();
                                        for (int k = 0; k < options.getLength(); k++) {
                                            if ("option".equals(options.item(k).getNodeName())) {
                                                Node option = options.item(k);
                                                String optionName = getAttribute(option, "name");
                                                domains.add(optionName);
                                            }
                                        }
                                        setting.setDomain(domains.toArray(new String[] {}));
                                    }
                                    setting.canBeEmpty(settingNode.getAttribute("canbeempty").equals("true"));
                                    setting.setDefault(settingDefault);
                                    setting.addScope(scopeName);
                                    settings.put(settingName, setting);
                                    log.debug("Added setting '" + settingName + "' of type '" + settingType + "' for scope '" + scopeName + "', default = '" + settingDefault + "'");
                                }
                            }
                        }
                    }
                }

            }
        } catch (Exception e) {
            log.error(e.getMessage(), e);
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

    public Map<String, Action> getActions() {
        return Collections.emptyMap();
    }

    /**
     * Permission framework: indicate whether or not a given operation may be done, with the
     * given arguments. The return value is a list of 2 booleans; the first boolean indicates
     * whether or not the operation is allowed, the second boolean indicates whether or not
     * this result may be cached.
    */
    public boolean[] may(Cloud cloud, Action action, Parameters arguments) {
        return new boolean[]{true, true};
    }

    public String getTemplatePath() {
        return templatepath;
    }

    public String getTemplateBar() {
        return templatebar;
    }

    public int getBarPosition() {
        return barposition;
    }

    public int getNumber() {
        return node.getNumber();
    }

    /**
     *
     * @javadoc I'd say it may be somewhat necessary here. I don't for example really understand the
     * difference between this and {@link #getSetting}.
     */
    public String getValue(String variablename, Cloud cloud, Map<String, ?> context, String[] arguments) {
        return "";
    }

    /**
     * Settings: return a setting in the given context. If no value can be found for any
     * of the scopes in the context, the default value for the setting will be returned.
     * This differs from {@link getObjectSetting} in that it falls back to defaults provided by
     * 'parent' scopes.
     * @param settingName The name of the setting for which a value should be
     * returned
     * @param context A Map containing name-value pairs, that can be needed
     * to retrieve the setting value. For instance the current username or
     * education node number.
     */
    public Object getSetting(String settingName, Cloud cloud, Map<String, ?> context) {
        if (log.isDebugEnabled()) {
            log.debug("Retrieving value for setting '" + settingName + "', with in context: " + context.keySet());
        }
        Setting setting = settings.get(settingName);
        if (setting == null) {
            throw new RuntimeException("Setting '" + settingName + "' is not defined for component '" + getName() + "'");
        }
        List<String> scope = setting.getScope();
        Object retval = null;

        for (String scopeName : scope) {
            String scopeReferId = getScopesMap().get(scopeName);
            if (log.isDebugEnabled()) {
                log.debug("Trying on scope '" + scopeName + "' (" + scopeReferId + ")");
            }
            int objectid = -1;
            if ("component".equals(scopeName)) {
                objectid =  node.getNumber();
            } else if (context.get(scopeReferId) != null) {
                try {
                    objectid = Integer.parseInt(org.mmbase.util.Casting.toString(context.get(scopeReferId)));
                    if (log.isDebugEnabled()) {
                        log.debug("" + scopeReferId + " = " + objectid);
                    }
                } catch (NumberFormatException nfe) {
                    log.warn(nfe);
                }
            }

            if (objectid > 0) {
                Object cloudSetting = getObjectSetting(settingName, objectid, cloud);
                if (cloudSetting != null) {
                    retval = cloudSetting;
                    log.debug("Found value: " + retval);
                }
            }
        }

        if (retval != null) {
            return retval;
        } else {
            return setting.getDefault();
        }
    }

    /**
     * Get the setting for an object and a component from MMBase. This object can be a 'people' object,
     * 'component' object, etc. If the object is the component, and no value can be found in the database,
     * the setting's default value will be returned.
     * This method is used internally to get a setting on a specific layer. This is the reason that te
     * default value is not returned for objects other than the component itself, because it would be
     * impossible to distinguish between a 'real' value and a default value later on.
     * @param settingname The name of the setting in MMBase.
     * @param id The number of the node representing this object to get the component setting value for
     */
    public Object getObjectSetting(String settingName, int id, Cloud cloud) {

        Object defaultValue = null;
        Setting setting = settings.get(settingName);
        if (setting == null) {
            throw new RuntimeException("Setting with name '" + settingName + "' is not defined for component '" + getName() + "'");
        }
        if (cloud == null) cloud = org.mmbase.bridge.ContextProvider.getDefaultCloudContext().getCloud("mmbase", "class", null);

        org.mmbase.bridge.NodeList settingNodes;
        if (id == node.getNumber()) {
            // direct setting to this component
            settingNodes = cloud.getNode(id).getRelatedNodes("settings");
            defaultValue = setting.getDefault();
        } else {
            org.mmbase.bridge.NodeList settingrel = nl.didactor.util.GetRelation.getRelations(id, node.getNumber(), "settingrel", cloud);

            if (settingrel.size() == 0) {
                // This is not a setting of this component itself, defaultValue is left to fall back.
                return null;
            }
            if (settingrel.size() > 1) {
                log.warn("Too many relations from " + id + " to " + node.getNumber() +" (" + settingrel.size() + "). Picking first one!");
            }
            org.mmbase.bridge.Node settingRelNode = settingrel.getNode(0);

            settingNodes = settingRelNode.getRelatedNodes("settings");
        }

        if (settingNodes == null) {
            return defaultValue;
        }

        for (org.mmbase.bridge.NodeIterator i = settingNodes.nodeIterator(); i.hasNext();) {
            org.mmbase.bridge.Node sn = i.nextNode();
            if (sn.getStringValue("name").equals(settingName)) {
                return setting.cast(sn.getStringValue("value"));
            }
        }

        return defaultValue;
    }

    /**
     * Set a new value for a setting on a specific object.
     * @param settingName The name of the setting
     * @param newValue the new value for the setting
     * @param id The objectnumber of the object to set the setting for
     * @param cloud The cloud in which to set the setting
     * @throws RuntimeException In case the setting doesnt exist, or the new value falls outside of
     * the domain of the datatype of the setting.
     */
    public void setObjectSetting(String settingName, int id, Cloud cloud, String newValue) {
        Setting setting = settings.get(settingName);
        if (setting == null) {
            throw new RuntimeException("Setting with name '" + settingName + "' is not defined for component '" + getName() + "'");
        }

        // Verify that the new value is valid for this setting
        if (setting.getType() == Setting.TYPE_INTEGER) {
            try {
                int i = Integer.parseInt(newValue);
            } catch (NumberFormatException e) {
                throw new RuntimeException("Value '" + newValue + "' is invalid for setting '" + settingName + "' of type Integer");
            }
        } else if (setting.getType() == Setting.TYPE_BOOLEAN || setting.getType() == Setting.TYPE_DOMAIN) {
            newValue = "" + setting.cast(newValue);
            String[] domain = setting.getDomain();
            boolean valid = false;
            for (String d : setting.getDomain()) {
                if (d.equals(newValue)) {
                    valid = true;
                    break;
                }
            }
            if (!valid) {
                throw new RuntimeException("Value '" + newValue + "' is invalid for setting '" + settingName + "', not inside domain");
            }
        }

        org.mmbase.bridge.Node baseNode = null;
        if (id == node.getNumber()) {
            baseNode = cloud.getNode(id);
        } else {
            org.mmbase.bridge.NodeList settingrel = nl.didactor.util.GetRelation.getRelations(id, node.getNumber(), "settingrel", cloud);

            if (settingrel.size() == 0) {
                org.mmbase.bridge.RelationManager nm = cloud.getRelationManager("settingrel");
                baseNode = nm.createRelation(cloud.getNode(id), cloud.getNode(node.getNumber()));
                baseNode.commit();
            } else if (settingrel.size() > 1) {
                baseNode = settingrel.getNode(0);
                log.warn("Too many relations from " + id + " to " + node.getNumber() +". Picking first one!");
            } else {
                baseNode = settingrel.getNode(0);
            }
        }

        org.mmbase.bridge.NodeList settingNodes = baseNode.getRelatedNodes("settings");

        for (org.mmbase.bridge.NodeIterator i = settingNodes.nodeIterator(); i.hasNext();) {
            org.mmbase.bridge.Node settingNode = i.nextNode();
            if (settingNode.getStringValue("name").equals(settingName)) {
                settingNode.setValue("value", newValue);
                settingNode.commit();
                return;
            }
        }
        // not found, we need to create a new setting node.
        log.service("missing settings node. Creating one now for setting '" + settingName + "' -> " + newValue);
        org.mmbase.bridge.NodeManager nm = cloud.getNodeManager("settings");
        org.mmbase.bridge.Node node = nm.createNode();
        node.setValue("name", settingName);
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
    public Object getUserSetting(String settingname, String userid, Cloud cloud) {
        if (log.isDebugEnabled()) {
            log.debug("getUserSetting(" + settingname + ", " + userid + ", " + cloud + ")");
        }
        Setting setting = settings.get(settingname);
        if (setting == null) {
            throw new RuntimeException("Setting with name '" + settingname + "' is not defined for component '" + getName() + "'");
        }
        org.mmbase.bridge.NodeList settingrel = nl.didactor.util.GetRelation.getRelations(Integer.parseInt(userid), node.getNumber(), "settingrel", cloud);

        if (settingrel.size() == 0) {
            Object retVal = getObjectSetting(settingname, node.getNumber(), cloud);
            log.debug("Returning default value: " + retVal);
            return retVal;
        }
        if (settingrel.size() > 1) {
            log.warn("Too many relations from " + userid + " to " + node.getNumber() +". Picking first one!");
        }
        org.mmbase.bridge.Node settingRelNode = settingrel.getNode(0);
        org.mmbase.bridge.NodeList settings = settingRelNode.getRelatedNodes("settings");

        for (org.mmbase.bridge.NodeIterator i = settings.nodeIterator(); i.hasNext();) {
            org.mmbase.bridge.Node settingNode = i.nextNode();
            if (settingNode.getStringValue("name").equals(settingname)) {
                if (log.isDebugEnabled()) {
                    log.debug("Returning database value: " + settingNode.getStringValue("value"));
                }
                return setting.cast(settingNode.getStringValue("value"));
            }
        }
        if (log.isDebugEnabled()) {
            log.debug("Returning default value: " + setting.getDefault());
        }
        return setting.getDefault();
    }


    /**
     * Register a component as interested. This can be used for example in the 'getSetting' or 'may' method
     * to retrieve a setting based on some extra installed components.
     */
    public void registerInterested(Component comp) {
        interestedComponents.add(comp);
    }

    /**
     * Small helper method that returns an attribute value of a w3c DOM Node.
     */
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


    public Map<String, Setting> getSettings() {
        return settings;
    }

    /**
     * Return a list of settings that are settable on a given scope
     */
    public List<Setting> getSettings(String scope) {
        List<Setting> result = new ArrayList<Setting>();
        for (Setting s : settings.values()) {
            if (s.getScope().contains(scope)) {
                result.add(s);
            }
        }
        return result;
    }

    /**
     * @javadoc
     */
    public final Collection<String> getScopes() {
        return getScopesMap().keySet();
    }

    public Map<String, String> getScopesMap() {
        return Collections.unmodifiableMap(scopes);
    }

    /**
     * @javadoc
     */
    public class Setting {
        public static final int TYPE_INTEGER = 1;
        public static final int TYPE_BOOLEAN = 2;
        public static final int TYPE_DOMAIN = 3;
        public static final int TYPE_STRING = 4;

        private final String name;
        private final int type;
        private String[] domain;
        private Object defaultValue;
        private final List<String> scope = new ArrayList<String>();
        private final String prompt;
        private boolean canBeEmpty = true;

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
            this.prompt = prompt;
        }

        public void setDefault(String defaultValue) {
            this.defaultValue = cast(defaultValue);
        }
        /**
         * If a setting can not be empty, then the empty string will be interpreted as
         * <code>null</code>, ('not set')
         */
        public boolean canBeEmpty() {
            return canBeEmpty;
        }
        public void canBeEmpty(boolean c) {
            canBeEmpty = c;
        }

        public Object cast(String value) {
            if (! canBeEmpty() && "".equals(value)) {
                return null;
            }
            switch (this.type) {
            case TYPE_BOOLEAN:
                if ("true".equals(value) || "on".equals(value)) {
                    return Boolean.TRUE;
                } else if ("false".equals(value)) {
                    return Boolean.FALSE;
                } else {
                    log.warn("Warning: boolean value '" + value + "' is not one of {true,false}, defaulting to false");
                    return Boolean.FALSE;
                }
            case TYPE_INTEGER:
                return new Integer(value);
            case TYPE_STRING:
            case TYPE_DOMAIN:
                return value;
            }
            return null;
        }

        public void setDomain(String[] domain) {
            this.domain = domain;
        }

        public void addScope(String scopeName) {
            scope.add(scopeName);
        }

        public List<String> getScope() {
            return scope;
        }

        public Object getDefault() {
            return defaultValue;
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

        public String toString() {
            return name + "  :" + defaultValue;
        }
    }
}
