package nl.didactor.builders;
import nl.didactor.component.Component;
import nl.didactor.events.Event;

import org.mmbase.util.logging.Logger;
import org.mmbase.util.logging.Logging;
import org.mmbase.module.core.*;
import org.mmbase.module.corebuilders.InsRel;
import org.mmbase.module.corebuilders.FieldDefs;
import org.mmbase.storage.search.*;
import org.mmbase.storage.search.implementation.*;
import org.mmbase.bridge.*;
import java.util.*;
import nl.didactor.events.*;

/**
 * This class provides extra functionality for the People builder. It
 * can encrypt the password of a user, and return a bridge.Node for
 * a given username/password combination
 * @author Johannes Verelst &lt;johannes.verelst@eo.nl&gt;
 */
public class PeopleBuilder extends DidactorBuilder {
    private final org.mmbase.util.Encode MD5 = new org.mmbase.util.Encode("MD5");

    private static final Logger log = Logging.getLoggerInstance(PeopleBuilder.class);

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
            SearchQueryHandler handler = MMBase.getMMBase().getSearchQueryHandler();
            if (log.isDebugEnabled()) {
                log.debug("Using query " + query + " --> " + handler.createSqlString(query));
            }

            //StepField passwordField = query.getField(getField("password"));
            //query.setConstraint(new BasicFieldValueConstraint(passwordField, "{md5}" + encoder.encode(password)));

            List nodelist = getNodes(query);
            if (nodelist.size() == 0) {
                log.debug("No users with the name '" + username + "'");
                return null;
                // fail silently
            } else if (nodelist.size() > 1) {
                log.error("Too many users with username '" + username + "': " + nodelist.size());
                MMObjectNode n = (MMObjectNode)nodelist.get(0);
                log.error(n.getStringValue("lastname") + ""+ n.getStringValue("username"));
                return null;
            } else {
                log.debug("1 user found: " + username + " " + password);
                MMObjectNode node = (MMObjectNode)nodelist.get(0);
                String storedPassword = node.getStringValue("password");
                String md5 = "{md5}" + MD5.encode(password);
                if (storedPassword == null || ! storedPassword.equals(md5)) {
                    log.debug("Invalid password " + storedPassword + " != " + md5);
                    return null;
                }
                return node;
            }
        } catch (SearchQueryException e) {
            log.error(e.toString(), e);
            return null;
        }
    }

    public MMObjectNode getUser(final String username) {
        try {
            NodeSearchQuery query = new NodeSearchQuery(this);
            StepField usernameField = query.getField(getField("username"));
            query.setConstraint(new BasicFieldValueConstraint(usernameField, username));

            List nodelist = getNodes(query);
            if (nodelist.size() == 0) {
               log.service ("No users with the name");
                return null;
                // fail silently
            } else if (nodelist.size() > 1) {
               for ( int i=0;i <nodelist.size() ;i++) {
                  MMObjectNode n = (MMObjectNode)nodelist.get(0);
                  log.service("Found multiple users with username '" + username + "': " + n.getStringValue("firstname") + "" + n.getStringValue("lastname"));
               }

               log.error("Too many users with username '" + username + "': " + nodelist.size());
               return null;
            } else {
                log.debug("1 user found with username '" + username + "'");
                MMObjectNode node = (MMObjectNode)nodelist.get(0);
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
    @Override public boolean setValue(MMObjectNode node, String fieldname, Object originalValue) {
        if (fieldname.equals("username")) {
            Object newValue = node.getValues().get(fieldname);

            /* forbid changing a username after it's been set
            if (originalValue != null && ! originalValue.equals("") && !originalValue.equals(newValue)) {
                node.storeValue(fieldname, originalValue);
                return false;
            }*/

            // forbid setting a username to an existing one
            if (originalValue != null && originalValue.equals("") && !newValue.equals("")) {
                if (countUsernamesInCloud((String) newValue) != 0) {
                    log.warn("setValues() cleared username " + ((String) newValue) + " because it already exists");
                    node.storeValue("username", "");
                    return false;
                }
            }
        }

        return true;
    }

    /**
     * Return the value for a field of the node. This method
     * is overridden from MMObjectBuilder, and will return a value
     * for the virtual 'isonline' field.
     * @param node Node to get a value for
     * @param field Name of the field.
     * @return an object containing the value.
     */
    @Override public Object getValue(MMObjectNode node, String field) {
        FieldDefs fd = getField(field);
        if (fd != null) {
            return super.getValue(node, field);
        }

        if ("isonline".equals(field)) {
            int now = (int)(System.currentTimeMillis() / 1000);
            int oldtime = node.getIntValue("lastactivity");
            if (now - oldtime > 60 * 5) {
                return Boolean.FALSE;
            } else {
                return node.getIntValue("islogged") == 0 ? Boolean.FALSE : Boolean.TRUE;
            }
        }

        // Oh no!

        // No fielddefs, so it is definately a virtual field. Is it a component setting?
        if (field.indexOf("-") > 0) {
            if (log.isDebugEnabled()) {
                log.debug("Trying to get '" + field + "'");
            }
            String componentName = field.substring(0, field.indexOf("-"));
            String settingName = field.substring(field.indexOf("-") + 1, field.length());
            if (log.isDebugEnabled()) {
                log.debug("Component [" + componentName + "], setting [" + settingName + "]");
            }
            Component c = Component.getComponent(componentName);
            Object value = c.getUserSetting(settingName, "" + node.getNumber(), LocalContext.getCloudContext().getCloud("mmbase"));
            if (log.isDebugEnabled()) {
                log.debug("Value: [" + value + "] (" + value.getClass() + ")");
            }
            return value;
        }

        // And maybe it was a computed field on lower level?
        return super.getValue(node, field);
    }

    @Override public int insert(String owner, MMObjectNode node) {
        // forbid setting a username to an existing one

        String newValue = (String) node.getValues().get("username");
        if (newValue != null && !newValue.equals("")) {
            if (countUsernamesInCloud(newValue) != 0) {
                log.info("insert() cleared username " + newValue + " because it already exists");
                node.storeValue("username", "");
                node.storeValue("password","");
            }
        }
        int number = super.insert(owner, node);
        Event event = new Event((String) node.getValues().get("username"), null, null, null, null,
                                "peopleaccountcreated", "" + number, "accountcreated");
        org.mmbase.core.event.EventManager.getInstance().propagateEvent(event);
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


