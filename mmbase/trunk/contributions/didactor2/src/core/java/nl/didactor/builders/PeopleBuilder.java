package nl.didactor.builders;
import nl.didactor.component.Component;
import org.mmbase.util.logging.Logger;
import org.mmbase.util.logging.Logging;
import org.mmbase.module.core.*;
import org.mmbase.module.corebuilders.InsRel;
import org.mmbase.storage.search.*;
import org.mmbase.storage.search.implementation.*;
import org.mmbase.bridge.*;
import java.util.Enumeration;
import java.util.List;
import java.util.Vector;

/**
 * This class provides extra functionality for the People builder. It
 * can encrypt the password of a user, and return a bridge.Node for
 * a given username/password combination
 * @author Johannes Verelst &lt;johannes.verelst@eo.nl&gt;
 */
public class PeopleBuilder extends DidactorBuilder {
    private org.mmbase.util.Encode encoder = null;
    private static Logger log=Logging.getLoggerInstance(PeopleBuilder.class.getName());

    /**
     * Return a user node (bridge) based on the given username and password.
     * @param username The username of the user
     * @param password The password of the user
     * @return an org.mmbase.bridge.Node object representing the user
     */
    public MMObjectNode getUser(String username, String password) {
        try {
            NodeSearchQuery query = new NodeSearchQuery(this);
            StepField usernameField = query.getField(getField("username"));
            query.setConstraint(new BasicFieldValueConstraint(usernameField, username));
            //StepField passwordField = query.getField(getField("password"));
            //query.setConstraint(new BasicFieldValueConstraint(passwordField, "{md5}" + encoder.encode(password)));
    
            List nodelist = getNodes(query);
            if (nodelist.size() == 0) {
                log.info("No users with the name '" + username + "'");
                return null;
                // fail silently
            } else if (nodelist.size() > 1) {
                log.error("Too many users with username '" + username + "': " + nodelist.size());
                for ( int i=0;i <nodelist.size() ;i++) {
                    MMObjectNode n = (MMObjectNode)nodelist.get(0);
                    log.error(n.getStringValue("lastname") + ""+ n.getStringValue("username"));
                }
                return null;
            } else {
                log.debug( "1 user found: " + username + " " + password);
                MMObjectNode node = (MMObjectNode)nodelist.get(0);
                String storedpassword = node.getStringValue("password");
                if (storedpassword == null || !storedpassword.equals("{md5}" + encoder.encode(password))) {
                    log.debug("Invalid password");
                    return null;
                }
                return node;
            }
        } catch (SearchQueryException e) {
            log.error(e.toString());
            return null;
        }
    }
 
    /**
     * Initialize this builder
     */
    public boolean init() {
        encoder = new org.mmbase.util.Encode("MD5");
        return super.init();
    }

    /**
     * This method is called after a setFieldValue(), to ask this builder
     * if it agrees on setting this field. Return false to abort the set.
     * @param node The node of which a value is being set
     * @param fieldname The name of the field that is being set
     * @param originalValue The original value of the field.
     * @return boolean indicating this set was allowed
     */
    public boolean setValue(MMObjectNode node, String fieldname, Object originalValue) {
        if (fieldname.equals("username")) {
            Object newValue = node.values.get(fieldname);

            /* forbid changing a username after it's been set
            if (originalValue != null && ! originalValue.equals("") && !originalValue.equals(newValue)) {
                node.values.put(fieldname, originalValue);
                return false;
            }*/

            // forbid setting a username to an existing one
            if (originalValue != null && originalValue.equals("") && !newValue.equals("")) {
                if (countUsernamesInCloud((String) newValue) != 0) {
                    log.info("setValues() cleared username "+((String) newValue)+" because it already exists");
                    node.values.put("username", "");
                    return false;
                }
            }
        }
        if (fieldname.equals("password")) {
            Object newValue = node.values.get(fieldname);
            if (((String)newValue).startsWith("{md5}")) {
                node.values.put(fieldname, (String)newValue);
            } else if (originalValue != null && !originalValue.equals(newValue)) {
                node.values.put(fieldname, "{md5}" + encoder.encode((String)newValue));
            }
        }
        
        return true;
    }

    /**
     * Set default values for a node
     * @param node The node
     */ 
    public void setDefaults(MMObjectNode node) {
        node.setValue("password", "");
    }

    /**
     * Return the value for a field of the node. This method
     * is overridden from MMObjectBuilder, and will return a value
     * for the virtual 'isonline' field.
     * @param node Node to get a value for
     * @param field Name of the field.
     * @return an object containing the value.
     */
    public Object getValue(MMObjectNode node, String field) {
        if ("isonline".equals(field)) {
            int now = (int)(System.currentTimeMillis() / 1000);    
            int oldtime = node.getIntValue("lastactivity");
            if (now - oldtime > 60 * 5) {
                return new Integer("0");
            } else {
                int islogged = node.getIntValue("islogged");
                if (islogged == 0) {
                    return new Integer("0");
                }
                return new Integer("1");
            }
        }

        if (getField(field) == null) {
            // Is it a component setting?
            if (field.indexOf("-") > 0) {
                String componentName = field.substring(0, field.indexOf("-"));
                String settingName = field.substring(field.indexOf("-") + 1, field.length());
                Enumeration e = node.getRelations("settingrel");
                MMBase mmbase = MMBase.getMMBase();

                // Check all relations, to see if they go to the right component
                while (e.hasMoreElements()) {
                    MMObjectNode srel = (MMObjectNode)e.nextElement();
                    int snumber = srel.getIntValue("snumber");
                    int dnumber = srel.getIntValue("dnumber");
                    MMObjectNode sourceNode = mmbase.getBuilder("object").getNode(snumber);
                    MMObjectNode destNode = mmbase.getBuilder("object").getNode(dnumber);
                    MMObjectNode relNode = null;
                    if (snumber == node.getNumber() && "components".equalsIgnoreCase(destNode.getBuilder().getTableName())) {
                        String cName = destNode.getStringValue("name");
                        if (componentName.equalsIgnoreCase(cName)) {
                            relNode = srel;
                        }
                    } else if (dnumber == node.getNumber() && "components".equalsIgnoreCase(destNode.getBuilder().getTableName())) {
                        String cName = destNode.getStringValue("name");
                        if (componentName.equalsIgnoreCase(cName)) {
                            relNode = srel;
                        }
                    }

                    // We found the settingrel from the user to the right component, now we
                    // look for all related settings
                    if (relNode != null) {
                        Enumeration e2 = relNode.getRelatedNodes("settings").elements();
                        while (e2.hasMoreElements()) {
                            MMObjectNode set = (MMObjectNode)e2.nextElement();
                            String name = set.getStringValue("name");
                            String value = set.getStringValue("value");

                            // Setting found: return the value
                            if (settingName.equalsIgnoreCase(name)) {
                                return value;
                            }
                        }
                    }
                }
            } else {
                // it is a virtual field, to be retrieved from the related 'fields' object
                Vector fieldNodes = node.getRelatedNodes("fields");
                for (int i=0; i<fieldNodes.size(); i++) {
                    MMObjectNode fieldNode = (MMObjectNode)fieldNodes.get(i);
                    if (field.equals(fieldNode.getStringValue("name"))) {
                        return fieldNode.getStringValue("value");
                    }
                }
            }
        }
        return super.getValue(node, field);
    }

    public int insert(String owner, MMObjectNode node) {
        // forbid setting a username to an existing one

        String newValue = (String) node.values.get("username");
        if (newValue != null && !newValue.equals("")) {
            if (countUsernamesInCloud(newValue) != 0) {
                log.info("insert() cleared username "+newValue+" because it already exists");
                node.values.put("username", "");
                node.values.put("password","");
            }
        }
        int number = super.insert(owner, node);
        log.info("insert people node");
        return number;
    }

    
    private int countUsernamesInCloud(String username) {
        try {
            NodeSearchQuery nsq = new NodeSearchQuery(this);
            nsq.setConstraint(new BasicFieldValueConstraint(nsq.getField(getField("username")),username));
            return count(nsq);
        }
        catch (SearchQueryException e) {
            log.error(e.toString());
            return -1;
        }
    }

}


