/*

This software is OSI Certified Open Source Software.
OSI Certified is a certification mark of the Open Source Initiative.

The license (Mozilla version 1.0) can be read at the MMBase site.
See http://www.MMBase.org/license

*/
package org.mmbase.applications.email;

import java.util.*;
import java.util.concurrent.*;

import org.mmbase.bridge.Node;

import org.mmbase.module.*;
import org.mmbase.module.core.*;
import org.mmbase.util.ThreadPools;

import org.mmbase.storage.search.*;
import org.mmbase.storage.search.implementation.*;

import org.mmbase.util.functions.*;
import org.mmbase.util.logging.Logger;
import org.mmbase.util.logging.Logging;

/**
 * Email builder. Nodes of this type are representations of email messages. Functions are available
 * to e.g. send these messages (using {@link SendMail}).
 *
 * @author Daniel Ockeloen
 * @author Michiel Meeuwissen
 * @version $Id: EmailBuilder.java,v 1.34 2008-11-27 13:53:35 michiel Exp $
 */
public class EmailBuilder extends MMObjectBuilder {

    private static final Logger log = Logging.getLoggerInstance(EmailBuilder.class);

    public final static Parameter[] MAIL_PARAMETERS = {
        new Parameter("type",    String.class)
    };


    public final static Parameter[] STARTMAIL_PARAMETERS = MAIL_PARAMETERS;
    public final static Parameter[] SETTYPE_PARAMETERS   = MAIL_PARAMETERS;

    // defined values for state (field "mailstatus" )
    public final static int STATE_UNKNOWN   = -1; // unknown
    public final static int STATE_WAITING   = 0; // waiting
    public final static int STATE_DELIVERED = 1; // delivered
    public final static int STATE_FAILED    = 2; // failed
    public final static int STATE_SPAMGARDE = 3; // spam filter hit, not mailed
    public final static int STATE_QUEUED    = 4; // queued


    // defined values for type (field "mailtype" )
    public final static int TYPE_STATIC      = 0; // A normal email object, which just represents an email object, and nothing more
    public final static int TYPE_ONESHOT     = 1; // Email will be sent and removed after sending.
    public final static int TYPE_RECEIVED    = 2; // Email which is received.
    public final static int TYPE_ONESHOTKEEP = 3; // Email will be sent and will not be removed.

    // public final static int TYPE_REPEATMAIL  = 4; // Email will be sent and scheduled after sending for a next time (does not work?)



    static String usersBuilder;
    static String usersEmailField;
    static String groupsBuilder;

    // reference to the expire handler
    private ScheduledFuture  expireHandler;

    protected int expireTime = 60 * 30 ;
    protected int sleepTime = 60;

    /**
     * init
     */
    @Override public boolean init() {
        super.init ();

        String property = getInitParameter("expireTime");
        if (property != null) {
            try {
                expireTime = Integer.parseInt(property);
            } catch(NumberFormatException nfe) {
                log.warn("property: expireTime contained an invalid integer value:'" + property +"'(" + nfe + ")");
            }
        }

        property = getInitParameter("sleepTime");
        if (property != null) {
            try {
                sleepTime = Integer.parseInt(property);
            } catch(NumberFormatException nfe) {
                log.warn("property: sleepTime contained an invalid integer value:'" + property +"'(" + nfe + ")");
            }
        }

        if (sleepTime > 0 && expireTime >0) {
            // start the email nodes expire handler, deletes
            // oneshot email nodes after the defined expiretime
            // check every defined sleeptime
            log.service("Expirehandler started with sleep time " + sleepTime + "sec, expire time " + expireTime + "sec.");
            expireHandler =
                ThreadPools.scheduler.scheduleAtFixedRate(new EmailExpireHandler(this, expireTime),
                                                          sleepTime,
                                                          sleepTime, TimeUnit.SECONDS);
            ThreadPools.identify(expireHandler, "Sent email deleter");



        } else {
            log.service("Expirehandler not started");
        }

        usersBuilder = getInitParameter("users-builder");
        if (usersBuilder == null) usersBuilder = "users";

        usersEmailField = getInitParameter("users-email-field");
        if (usersEmailField == null) usersEmailField = "email";

        groupsBuilder = getInitParameter("groups-builder");
        if (groupsBuilder == null) groupsBuilder = "groups";

        return true;
    }

    @Override public void shutdown() {
        if (expireHandler != null) { expireHandler.cancel(true); }
    }

    {
        addFunction(new NodeFunction/*<Void>*/("mail", MAIL_PARAMETERS, ReturnType.VOID) {
                protected Boolean getFunctionValue(Node node, Parameters parameters) {
                    log.debug("We're in mail - args: " + parameters);
                    setType(node, parameters);

                    // get the mailtype so we can call the correct handler/method
                    int mailType = node.getIntValue(getTypeField());
                    boolean success = false;
                    switch(mailType) {
                    case TYPE_ONESHOT :
                        // deleting the node happens in EmailExpireHandler
                    case TYPE_ONESHOTKEEP :
                        try {
                            EmailHandler.sendMailNode(node);
                        } catch (javax.mail.MessagingException me) {
                            log.error(me.getMessage(), me);
                        }
                        break;
                        // case TYPE_REPEATMAIL :
                    default:
                        log.warn("Trying to mail a node with unsupported type " + mailType);
                    }

                    return null;
                }
            }
            );
        addFunction(new NodeFunction/*<Void>*/("startmail", MAIL_PARAMETERS, ReturnType.VOID) {
                protected Void getFunctionValue(final Node node, Parameters parameters) {
                    log.debug("We're in startmail - args: " + parameters);
                    setType(node, parameters);

                    // get the mailtype so we can call the correct handler/method
                    int mailType = node.getIntValue(getTypeField());
                    switch(mailType) {
                    case TYPE_ONESHOT :
                        // deleting the node happens in EmailExpireHandler
                    case TYPE_ONESHOTKEEP :
                        org.mmbase.util.ThreadPools.jobsExecutor.execute(new Runnable() {
                                public void run() {
                                    try {
                                        EmailHandler.sendMailNode(node);
                                    } catch (javax.mail.MessagingException me) {
                                        log.error(me.getMessage(), me);
                                    }
                                }
                            });
                        break;
                        // case TYPE_REPEATMAIL :
                    default:
                        log.warn("Trying to mail a node with unsupported type " + mailType);
                    }

                    return null;
                }
            }
            );


        // This is a silly function.
        // We could override setStringValue on 'type' itself. Perhaps that even already works.
        addFunction(new NodeFunction/*<Void>*/("settype", MAIL_PARAMETERS, ReturnType.VOID) {
                protected Void getFunctionValue(final Node node, Parameters parameters) {
                    log.debug("We're in startmail - args: " + parameters);
                    setType(node, parameters);
                    return null;
                }
            }
            );

    }

    /**
     * Return the sendmail module
     */
    static SendMail getSendMail() {
        return (SendMail) Module.getModule("sendmail");
    }

    static String getTypeField() {
        SendMail sm = getSendMail();
        if (sm != null) return sm.getTypeField();
        return "mailtype";
    }


    /**
     * Set the mailtype based on the first argument in the list.
     *
     * @param node	Email node on which to set the type
     * @param args	List with arguments
     */
    private static void setType(Node node, Parameters parameters) {
        String type = (String) parameters.get("type");
        String typeField = getTypeField();
        if ("oneshot".equals(type)) {
            node.setValue(typeField, TYPE_ONESHOT);
            log.debug("Setting mailtype to: " + TYPE_ONESHOT);
        } else if ("oneshotkeep".equals(type)) {
            node.setValue(typeField, TYPE_ONESHOTKEEP);
            log.debug("Setting mailtype to " + TYPE_ONESHOTKEEP);
        } else {
            node.setValue(typeField, TYPE_ONESHOT);
            log.debug("Setting mailtype to: " + TYPE_ONESHOT);
        }
    }



    /**
     * Returns all the one-shot delivered mail nodes older than a specified time.
     * This is used by {@link EmailExpireHandler} to remove expired emails.
     * @param expireAge The minimum age of the desired nodes in seconds
     * @return a unmodifiable List of MMObjectNodes
     */
    List<MMObjectNode> getDeliveredMailOlderThan(long expireAge) {
        // calc search time based on expire time
        long age = System.currentTimeMillis() - expireAge * 1000;
        // query database for the nodes

        NodeSearchQuery query = new NodeSearchQuery(this);
        BasicCompositeConstraint cons = new BasicCompositeConstraint(CompositeConstraint.LOGICAL_AND);

        try {
            cons.addChild(new BasicFieldValueConstraint(query.getField(getField("mailstatus")), STATE_DELIVERED));
            cons.addChild(new BasicFieldValueConstraint(query.getField(getField(getTypeField())),   TYPE_ONESHOT));
            cons.addChild(new BasicFieldValueConstraint(query.getField(getField("mailedtime")), new java.util.Date(age)).setOperator(FieldCompareConstraint.LESS));
        } catch (IllegalArgumentException e) {
            log.warn(e.getMessage());
            return new ArrayList<MMObjectNode>();
        }
        query.setConstraint(cons);
        try {
            // mailedtime constraints makes it useless to do a cached query.
            return storageConnector.getNodes(query, false);
        } catch (SearchQueryException sqe) {
            log.error(sqe.getMessage());
            return new ArrayList<MMObjectNode>();
        }

    }
}
