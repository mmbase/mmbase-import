/*

This software is OSI Certified Open Source Software.
OSI Certified is a certification mark of the Open Source Initiative.

The license (Mozilla version 1.0) can be read at the MMBase site.
See http://www.MMBase.org/license

*/
package org.mmbase.applications.email;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import java.util.MissingResourceException;
import java.util.ResourceBundle;

import org.mmbase.module.Module;
import org.mmbase.module.core.*;

import org.mmbase.storage.search.*;
import org.mmbase.storage.search.implementation.*;

import org.mmbase.util.functions.Functions;
import org.mmbase.util.functions.Parameter;
import org.mmbase.util.functions.Parameters;
import org.mmbase.util.logging.Logger;
import org.mmbase.util.logging.Logging;

/**
 * Email builder.
 * Rewrite of the email system, that became too complex to handle.
 * The focus on the new one is different.
 * The code is now split per mail type to allow for easer debug and better control over the 'simple'
 * mail action. The delayed and repeat mail will be handled with
 * the upcomming crontab builder
 *
 * @javadoc is a bit lame
 *
 * @author Daniel Ockeloen
 * @author Michiel Meeuwissen
 */
public class EmailBuilder extends MMObjectBuilder {

    private static final Logger log = Logging.getLoggerInstance(EmailBuilder.class);

    public final static Parameter[] MAIL_PARAMETERS = {
        new Parameter("type",    String.class)
    };


    public final static Parameter[] STARTMAIL_PARAMETERS = MAIL_PARAMETERS;
    public final static Parameter[] SETTYPE_PARAMETERS   = MAIL_PARAMETERS;

    // defined values for state ( node field "mailstatus" )
    public final static int STATE_UNKNOWN   = -1; // unknown
    public final static int STATE_WAITING   = 0; // waiting
    public final static int STATE_DELIVERED = 1; // delivered
    public final static int STATE_FAILED    = 2; // failed
    public final static int STATE_SPAMGARDE = 3; // spam filter hit, not mailed
    public final static int STATE_QUEUED    = 4; // queued


    // defined values for state ( node field "mailtype" )
    public final static int TYPE_ONESHOT     = 1; // Email will be sent and removed after sending.
    // public final static int TYPE_REPEATMAIL  = 2; // Email will be sent and scheduled after sending for a next time (does not work?)
    public final static int TYPE_ONESHOTKEEP = 3; // Email will be sent and will not be removed.

    public final static String EMAILTYPE_RESOURCE = "org.mmbase.applications.email.resources.mailtype";
    public final static String EMAILSTATUS_RESOURCE = "org.mmbase.applications.email.resources.mailstatus";

    static String usersBuilder;
    static String usersEmailField;
    static String groupsBuilder;

    // reference to the sendmail module
    private static SendMailInterface sendmail;

    // reference to the expire handler
    private static EmailExpireHandler expirehandler;

    /**
     * init
     */
    public boolean init() {
        super.init ();

        // get the sendmail module
        sendmail = (SendMailInterface) Module.getModule("sendmail");

        // start the email nodes expire handler, deletes
        // oneshot email nodes after the defined expiretime
        // check every defined sleeptime
        expirehandler = new EmailExpireHandler(this, 60, 30 * 60);

        usersBuilder = getInitParameter("users-builder");
        if (usersBuilder == null) usersBuilder = "users";

        usersEmailField = getInitParameter("users-email-field");
        if (usersEmailField == null) usersEmailField = "email";

        groupsBuilder = getInitParameter("groups-builder");
        if (groupsBuilder == null) groupsBuilder = "groups";

        return true;
    }


    /**
     * {@inheritDoc}
     *
     * Override the function call to receive the functions called from
     * the outside world (mostly from the taglibs)
     */
    protected Object executeFunction(MMObjectNode node, String function, List args) {
        if (log.isDebugEnabled()) {
            log.debug("function: " + function);
        }

        if (function.equals("info")) {
        	List empty = new ArrayList();
        	java.util.Map info = (java.util.Map) super.executeFunction(node, function, empty);
        	info.put("gui", "(mailtype or mailstatus) Gui representation of this object.");
        	if (args == null || args.size() == 0) {
        		return info;
        	} else {
        		return info.get(args.get(0));
        	}

        } else if (args != null && args.size() > 0) {

        	String field = (String) args.get(0);
			log.debug("field: '" + field + "'");	// mailtype, mailstatus

			if (function.equals("gui") && field != null) {
				if (field.equals("mailstatus")) {
					String val = node.getStringValue("mailstatus");
					log.debug("val: " + val); // 0, 1, 2, 3
					ResourceBundle bundle;
					Parameters pars = Functions.buildParameters(GUI_PARAMETERS, args);
					Locale locale = (Locale) pars.get(Parameter.LOCALE);
					if (locale == null) {
						String lang = (String) pars.get(Parameter.LANGUAGE);
						if (lang != null) {
							locale = new Locale(lang, "");
						}
					}
					if (locale == null) {
						locale = mmb.getLocale();
					}
					bundle = ResourceBundle.getBundle(EMAILTYPE_RESOURCE, locale, getClass().getClassLoader() );

					try {
						return bundle.getString(val);
					} catch (MissingResourceException e) {
						return val;
					}
				} else if (field.equals("mailtype")){	// mailtype
					String val = node.getStringValue("mailtype");
					return getMailtypeResource(val, args);
				} else {
					return super.executeFunction(node, function, args);
				}
		    } else if (function.equals("setType") || function.equals("settype") ) {
		        setType(node, args);
		        return null;

		    } else if (function.equals("mail")) {  // function mail(type) called
	            log.debug("We're in mail - args: " + args);
	            setType(node, args);

		        // get the mailtype so we can call the correct handler/method
		        int mailType = node.getIntValue("mailtype");
		        switch(mailType) {
		        case TYPE_ONESHOT :
		            // deleting the node happens in EmailExpireHandler
		        case TYPE_ONESHOTKEEP :
		            EmailHandler.sendMailNode(node);
		            break;
		        // case TYPE_REPEATMAIL :
		        default:
		            log.warn("Trying to mail a node with unsupported type " + mailType);
		        }

		        String val = node.getStringValue("mailtype");
		        //return getMailtypeResource(val, args);
		        return null;

		    } else if (function.equals("startmail")) {         // function startmail(type) called (starts a background thread)
		    	log.debug("We are in startmail - args: " + args);
		        // check if we have arguments ifso call setType()
		    	setType(node, args);

		        // get the mailtype so we can call the correct handler/method
		        int mailType = node.getIntValue("mailtype");
		        log.debug("mailtype: " + mailType);
		        switch(mailType) {
		        case TYPE_ONESHOT :
		            // deleting the node happens in EmailExpireHandler
		        case TYPE_ONESHOTKEEP :
		            new EmailBackgroundHandler(node);
		            break;
		        // case TYPE_REPEATMAIL :
		        default:
		            log.warn("Trying to start a mail of a node with unsupported type " + mailType);
		        }
				String val = node.getStringValue("mailtype");
				// return getMailtypeResource(val, args);
				return null;
		    }
        }
        if (log.isDebugEnabled()) {
        	log.debug("Function '" + function + "' is not found in email app.");
        }
        return super.executeFunction(node, function, args);

    }


    /**
     * Return the sendmail module
     */
    static SendMailInterface getSendMail() {
        return sendmail;
    }

    /**
     * Set the mailtype based on the first argument in the list.
     *
     * @param node	Email node on which to set the type
     * @param args	List with arguments
     */
    private static void setType(MMObjectNode node, List args) {
        String type = (String) args.get(0);
        if ("oneshot".equals(type)) {
            node.setValue("mailtype", TYPE_ONESHOT);
            log.debug("Setting mailtype to: " + TYPE_ONESHOT);
        } else if ("oneshotkeep".equals(type)) {
            node.setValue("mailtype", TYPE_ONESHOTKEEP);
            log.debug("Setting mailtype to " + TYPE_ONESHOTKEEP);
        } else {
            node.setValue("mailtype", TYPE_ONESHOT);
            log.debug("Setting mailtype to: " + TYPE_ONESHOT);
        }
    }

    /**
     * Mailtype maps to an int in a resource, this method finds it,
     * if possible and available the localized version of it.
     *
     * @param val	The int value that maps to a mailtype (1 = oneshot etc.)
     * @param args	List with arguments containing the wanted mailtype
     * @return A String from the resource file being a mailtype
     */
    private String getMailtypeResource(String val, List args) {
    	if (args != null && args.size() > 0) {
    		ResourceBundle bundle;
    		Parameters pars = Functions.buildParameters(GUI_PARAMETERS, args);
    		Locale locale = (Locale) pars.get(Parameter.LOCALE);
    		if (locale == null) {
    			String lang = (String) pars.get(Parameter.LANGUAGE);
    			if (lang != null) {
    				locale = new Locale(lang, "");
    			}
    		}
    		if (locale == null) {
    			locale = mmb.getLocale();
    		}
    		bundle = ResourceBundle.getBundle(EMAILTYPE_RESOURCE, locale, getClass().getClassLoader());

    		try {
    			return bundle.getString(val);
    		} catch (MissingResourceException e) {
    			return val;
    		}
    	}
    	return val;
    }


    /**
     * Returns all the one-shot delivered mail nodes older than a specified time.
     * This is used by {@link EmailExpireHandler} to remove expired emails.
     * @param expireAge The minimum age of the desired nodes in seconds
     * @return a unmodifiable List of MMObjectNodes
     */
    List getDeliveredMailOlderThan(long expireAge) {
        // calc search time based on expire time
        long age = (System.currentTimeMillis() / 1000) - expireAge;
        // query database for the nodes

        NodeSearchQuery query = new NodeSearchQuery(this);
        BasicCompositeConstraint cons = new BasicCompositeConstraint(CompositeConstraint.LOGICAL_AND);

        cons.addChild(new BasicFieldValueConstraint(query.getField(getField("mailstatus")), new Integer(STATE_DELIVERED)));
        cons.addChild(new BasicFieldValueConstraint(query.getField(getField("mailtype")),   new Integer(TYPE_ONESHOT)));
        cons.addChild(new BasicFieldValueConstraint(query.getField(getField("mailedtime")), new Long(age)).setOperator(FieldCompareConstraint.LESS));
        query.setConstraint(cons);
        try {
            return getNodes(query);
        } catch (SearchQueryException sqe) {
            log.error(sqe.getMessage());
            return new ArrayList();
        }

    }
}
