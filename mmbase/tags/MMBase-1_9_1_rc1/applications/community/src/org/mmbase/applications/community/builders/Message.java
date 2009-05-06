/*

This software is OSI Certified Open Source Software.
OSI Certified is a certification mark of the Open Source Initiative.

The license (Mozilla version 1.0) can be read at the MMBase site.
See http://www.MMBase.org/license

*/

package org.mmbase.applications.community.builders;

import org.mmbase.applications.community.modules.*;
import java.util.*;
import java.io.*;

import org.mmbase.module.core.*;
import org.mmbase.module.corebuilders.*;
import org.mmbase.util.*;
import org.mmbase.util.logging.*;

/**
 * This builder implements additional functionality and methods to handle
 * community objects.
 * Added functionality involve posting and removing a message,
 * managing temporary messages, and retrieving message lists for a
 * specific 'thread'.
 *
 * @author Dirk-Jan Hoekstra
 * @author Pierre van Rooden
 * @version $Id: Message.java,v 1.36 2007-06-21 15:50:22 nklasens Exp $
 */

public class Message extends MMObjectBuilder {

    // errors
    public final static int POST_OK = 0;
    public final static int POST_ERROR_UNKNOWN = -1;
    public final static int POST_ERROR_BODY_EXCEEDS_SIZE = -2;
    public final static int POST_ERROR_NO_USER = -3;
    public final static int POST_ERROR_NEED_LOGIN = -4;
    public final static int POST_ERROR_RELATION_CHANNEL = -5;
    public final static int POST_ERROR_RELATION_USER = -6;
    public final static int POST_ERROR_NO_BODY_TEXT = -7;
    public final static int POST_ERROR_NO_SUBJECT = -8;

    // logger
    private static final Logger log = Logging.getLoggerInstance(Message.class);

    /** Default tag for the <code>listhead</code> message field */
    public static final String LIST_HEAD_TAG = "<ul>";
    /** Default tag for the <code>listtail</code> message field */
    public static final String LIST_TAIL_TAG = "</ul>";

    /** Field : thread */
    public static final String F_THREAD = "thread";
    /** Field : body */
    public static final String F_BODY = "body";
    /** Field : subject */
    public static final String F_SUBJECT = "subject";
    /** Field : sequence */
    public static final String F_SEQUENCE = "sequence";
    /** Field : info */
    public static final String F_INFO = "info";
    /** Field : timestamp  */
    public static final String F_TIMESTAMP = "timestamp";
    /** Field : timestamp in seconds */
    public static final String F_TIMESTAMPSEC = "timestampsec";

    /** Virtual Field : resubject */
    public static final String F_RE_SUBJECT = "resubject";
    /** Virtual Field : replycount */
    public static final String F_REPLY_COUNT = "replycount";
    /** Virtual Field : hasreplies */
    public static final String F_HAS_REPLIES = "hasreplies";
    /** Virtual Field : parent */
    public static final String F_PARENT = "parent";

    /** Formatting Function : getinfovalue */
    public static final String F_GET_INFO_VALUE = "getinfovalue";

    /**
     * Maximum message body size in bytes.
     */
    protected int maxBodySize = 2024;
    // default expiration time for relation breaker
    private int expireTime = 1 * 60 * 1000;

    private Channel channelBuilder;

    public TemporaryNodeManager tmpNodeManager = null;
    private String messageUser = null;
    // this will be used to genererate keys for temporary nodes used by the community.
    private int tmpNumbers = 0;
    private final String tmpNumberPrefix = "cmt";

    // relation breaker for maintaining temporary messages
    private NodeBreaker chatboxMessages = null;

    // indicates whether this builder has been activated for the community application
    private boolean active = false;

    /**
     * Constructor
     */
    public Message() {
    }

    public boolean init() {
        boolean result = super.init();

        String maxBody = getInitParameter("maxbodysize");
        if ((maxBody != null) && (maxBody.length() > 0)) {
            try {
                maxBodySize = Integer.parseInt(maxBody);
            } catch (Exception e) {
                log.warn("Invalid value for property'maxbodysize' :" + maxBody);
            }
        }

        messageUser = getInitParameter("postas");
        if ((messageUser == null) || (messageUser.length() == 0)) {
            messageUser = "system";
        }
        tmpNodeManager = TransactionManager.getInstance().getTemporaryNodeManager();
        // create relation breaker for maintaining temporary relations
        chatboxMessages = new NodeBreaker(2 * expireTime, tmpNodeManager);

        // Add temporary fields
        // These are currently ment to enable the taglib posttag
        // to pass data used for the info field or to link a message
        // In future version, these virutal fields migth actually be used to set or get
        // this data directly from the message node
        checkAddTmpField("channel"); // node number of the channel object for this message
        checkAddTmpField("user");    // node number of the user object for this message
        checkAddTmpField("username"); // username of the person posting the message

        activate();

        return result;
    }

    /**
     * Activates the message builder for the community application by associating it with other community builders
     * @return true if activation worked
     */
    public boolean activate() {
        if (!active) {
            channelBuilder = (Channel) mmb.getMMObject("channel");
            active = channelBuilder != null;
        }
        return active;
    }

    // used to retrieve the parent - child role from reldef or cache
    private int getParentChildRole() {
        return mmb.getRelDef().getNumberByName("parent/child");
    }

    // used to retrieve the creator-subject role from reldef or cache
    private int getCreatorRole() {
        return mmb.getRelDef().getNumberByName("creator/subject");
    }

    /**
     * Obtains the description of an error that occurred during a
     * user-induced action on a message (such as a post).
     * @param error the error number
     * @return the error description
     */
    public String getMessageError(int error) {
        switch (error) {
        case POST_ERROR_BODY_EXCEEDS_SIZE:
            return "Message body size exceeds " + maxBodySize + " bytes";
        case POST_ERROR_NO_USER:
            return "User name or object needed";
        case POST_ERROR_NEED_LOGIN:
            return "User needs to be logged on to post";
        case POST_ERROR_RELATION_CHANNEL:
            return "Could not create temporary relations between message and channel.";
        case POST_ERROR_RELATION_USER:
            return "Could not create temporary relations between message and user.";
        case POST_ERROR_NO_BODY_TEXT:
            return "No message body text specified.";
        case POST_ERROR_NO_SUBJECT:
            return "No subject specified.";
        default :
            return "Could not post message.";
        }
    }

    /**
     * Post a new message in the channel.
     *
     * @param subject The subject of the message.
     * @param body The body of the message.
     * @param channel The channel in which the message has to get posted.
     * @param thread The number of the thread (a Message or Channel) in which the Message has to get listed.
     * @param chatter The usernumber of the user that has written the message.
     * @param chatterName The name of the person that has written the message when he hasn't a usernumber.
     * @return The number of the newly created message node or a negative number if for
     *         some reason no message was created.
     *         The number can be used with getPostError() to get the errormessage.
     */
    public int post(String subject, String body, int channel, int thread, int chatter, String chatterName) {
        if (body.length() == 0) {
            log.error("post(): no body text");
            return POST_ERROR_NO_BODY_TEXT;
        }
        if (subject.length() == 0) {
            log.error("post(): no subject");
            return POST_ERROR_NO_SUBJECT;
        }
        if (body.length() > maxBodySize) {
            log.error("post(): body size exceeds " + maxBodySize + " bytes");
            return POST_ERROR_BODY_EXCEEDS_SIZE;
        }

        if (chatterName != null) {
            if (chatterName.length() == 0) {
                log.error("post(): CHATTERNAME must be larger than 0 tokens");
                chatterName = null;
            }
        }
        if ((chatterName == null) && (chatter == -1)) {
            return POST_ERROR_NO_USER;
        }

        MMObjectNode channelNode = getNode(channel);
        // test write login
        // if write-login is true, and no 'chatter' node is specified,
        // the user has not logged on - so the post should fail
        if ((channelNode.getIntValue(Channel.F_STATE) & Channel.STATE_WRITE_LOGIN) > 0) {
            if (chatter == -1) {
                return POST_ERROR_NEED_LOGIN;
            }
        }

        MMObjectNode node = getNewNode(messageUser);
        node.setValue(F_SUBJECT, subject);
        node.setValue(F_BODY, body);
        node.setValue(F_THREAD, thread);

        node.setValue(F_SEQUENCE, channelBuilder.getNewSequence(channelNode));
        /*
         * Make the relation with the MessageTread in which this Message get listed.
         * And make the relation between message and the uses who posted the message.
         * Because InsRel keeps a cache of the last 25 'most used' relations wich doesn't
         * work correct after a insert, delete all cached relations of the parent
         * messagethread so the search will be done on the database.
         */
        if (log.isDebugEnabled()) {
            log.debug("post(): make relation message with thread and chatter");
        }
        InsRel insrel = mmb.getInsRel();
        if (chatterName != null) {
            setInfoField(node, "name", chatterName);
//            node.setValue(F_INFO, "name=\"" + chatterName + "\"");
        }
        int id = insert(messageUser, node);
        if (chatter > 0) {
            MMObjectNode chattertomsg = insrel.getNewNode(messageUser);
            chattertomsg.setValue("snumber", chatter);
            chattertomsg.setValue("dnumber", id);
            chattertomsg.setValue("rnumber", getCreatorRole());
            insrel.insert(messageUser, chattertomsg);
        }
        MMObjectNode msgtothread = insrel.getNewNode(messageUser);
        msgtothread.setValue("snumber", id);
        msgtothread.setValue("dnumber", thread);
        msgtothread.setValue("rnumber", getParentChildRole());
        insrel.insert(messageUser, msgtothread);
        insrel.deleteRelationCache(thread);
        return id;
    }

    /**
     * Posts a message as a temporary message node.
     *
     * @param body The body of the message.
     * @param channel The channel in which the message has to get posted.
     * @param chatter The usernumber of the user that has written the message.
     * @return {@link #POST_OK} on success, otherwise an error number.
     *         The number can be used with getPostError() to get the errormessage.
     * @return <code>true</code. if the message was posted, <code>false</code> if the post failed
     */
    public int post(String body, int channel, int chatter) {
        return post(body, channel, chatter, null);
    }

    /**
     * Posts a message as a temporary message node.
     *
     * @param body The body of the message.
     * @param channel The channel in which the message has to get posted.
     * @param chatter The usernumber of the user that has written the message.
     * @return {@link #POST_OK} on success, otherwise an error number.
     *         The number can be used with getPostError() to get the errormessage.
     */
    public int post(String body, int channel, int chatter, String chatterName) {
        if (body.length() == 0) {
            return POST_ERROR_NO_BODY_TEXT;
        }
        if (body.length() > maxBodySize) {
            return POST_ERROR_BODY_EXCEEDS_SIZE;
        }

        MMObjectNode channelNode = getNode(channel);
        // test write login
        // if write-login is true, and no 'chatter' node is specified,
        // the user has not logged on - so the post should fail
        if ((channelNode.getIntValue(Channel.F_STATE) & Channel.STATE_WRITE_LOGIN) > 0) {
            if (chatter == -1)
                return POST_ERROR_NEED_LOGIN;
        }

        // Build a temporary message node.
        String key = tmpNodeManager.createTmpNode("message", messageUser, getNewTemporaryKey());
        MMObjectNode message = getNewTmpNode(messageUser, key);

        // Set the fields.
        int sequence = channelBuilder.getNewSequence(channelNode);

        if (chatterName == null) {
            chatterName = "unknown";
            if (chatter != -1) {
                MMObjectNode chatternode = getNode(chatter);
                if (chatternode != null) {
                    chatterName = chatternode.getStringValue("gui()");
                } else {
                    chatterName = "chatter_" + chatter;
                }
            }
        }

        // we have a reference to the temporary node,
        // so we can change it directly
        message.setValue(F_BODY, body);
        message.setValue(F_THREAD, channel);
        message.setValue(F_SEQUENCE, sequence);
        boolean useTimeStamp = (getField(F_TIMESTAMP) != null);
        if (useTimeStamp) {
            message.setValue(F_TIMESTAMP, System.currentTimeMillis());
        } else {
            TimeStamp timeStamp = new TimeStamp();
            message.setValue("timestampl", timeStamp.lowIntegerValue());
            message.setValue("timestamph", timeStamp.highIntegerValue());
        }
        setInfoField(message, "name", chatterName);

        Writer recorder = channelBuilder.getRecorder(channel);
        if (recorder != null) {
            try {
                recorder.write(chatterName + " : " + body + "\n");
            } catch (IOException e) {
                log.error("" + e);
            }
        }

        /* Make the relation with the channel in which this Message get listed.
         * And make the relation between message and the user who posted the message.
         */
        int result = POST_OK;
        try {
            String tmp = tmpNodeManager.createTmpRelationNode("parent", messageUser, getNewTemporaryKey(), "realchannel", key);
            tmpNodeManager.setObjectField(messageUser, tmp, "snumber", new Integer(channel));
            // add the message relation to the relation breaker
            chatboxMessages.add(messageUser + "_" + tmp, (new Long(System.currentTimeMillis() + expireTime)).longValue());
        } catch (Exception e) {
            result = POST_ERROR_RELATION_CHANNEL;
        }
        if (chatter != -1) {
            try {
                String tmp = tmpNodeManager.createTmpRelationNode("creator", messageUser, getNewTemporaryKey(), "realuser", key);
                tmpNodeManager.setObjectField(messageUser, tmp, "snumber", new Integer(chatter));
                // add the message relation to the relation breaker
                chatboxMessages.add(messageUser + "_" + tmp, (new Long(System.currentTimeMillis() + expireTime)).longValue());
                MMObjectNode node = tmpNodeManager.getNode(messageUser, tmp);
                if (log.isDebugEnabled()) {
                    log.debug("just set " + tmp + " snumber to " + node.getIntValue("snumber"));
                }
            } catch (Exception e) {
                result = POST_ERROR_RELATION_USER;
            }
        }
        // add the message itself to the relation breaker
        chatboxMessages.add(messageUser + "_" + key, (new Long(System.currentTimeMillis() + expireTime)).longValue());
        return result;
    }

    /**
     * Inserts a message node to the database.
     *
     * @param owner The owner of the node.
     * @param node The node to insert.
     */
    public int insert(String owner, MMObjectNode node) {
        // determine how the timestamp is stored (as a long or as two integers)

        boolean useTimeStamp = (getField(F_TIMESTAMP) != null);
        if (useTimeStamp) {
            node.setValue(F_TIMESTAMP, new Long(System.currentTimeMillis()));
        } else {
            TimeStamp timeStamp = new TimeStamp();
            node.setValue("timestampl", timeStamp.lowIntegerValue());
            node.setValue("timestamph", timeStamp.highIntegerValue());
        }

        InsRel insrel = mmb.getInsRel();
        insrel.deleteRelationCache(node.getIntValue(F_THREAD));
        return (super.insert(owner, node));
    }

    /**
     * Changes the subject and body fields, and the name of the poster when stored in the info-field
     * of an already existing message.
     * This function is usefull for editing or moderating the context of a message.
     *
     * @param chatterName The name of the person who has written the message.
     * @param subject The subject of the message.
     * @param body The body of the message.
     * @param number The message node's number.
     * @return {@link #POST_OK} on success, otherwise an error number.
     *         The number can be used with getPostError() to get the errormessage.
     */
    public int update(String chatterName, String subject, String body, int number) {
        return update(chatterName, -1, subject, body, number);
    }

    /**
     * Changes the subject and body fields, and the name of the poster when stored in the info-field
     * of an already existing message.
     * This function is usefull for editing or moderating the context of a message.
     *
     * @param chatterName The name of the person who has written the message.
     * @param chatter the number of the chatter object
     * @param subject The subject of the message.
     * @param body The body of the message.
     * @param number The message node's number.
     * @return {@link #POST_OK} on success, otherwise an error number.
     *         The number can be used with getPostError() to get the errormessage.
     */
    public int update(String chatterName, int chatter, String subject, String body, int number) {
        if (body.length() == 0) {
            return POST_ERROR_NO_BODY_TEXT;
        }
        if (body.length() > maxBodySize) {
            return POST_ERROR_BODY_EXCEEDS_SIZE;
        }

        MMObjectNode node = getNode(number);
        Vector channels = node.getRelatedNodes("channel");
        if (channels.size() > 0) {
            MMObjectNode channelNode = (MMObjectNode) channels.get(0);
            // test write login
            // if write-login is true, and no 'chatter' node is specified,
            // the user has not logged on - so the post should fail
            if ((channelNode.getIntValue(Channel.F_STATE) & Channel.STATE_WRITE_LOGIN) > 0) {
                if (chatter == -1)
                    return POST_ERROR_NEED_LOGIN;
            }
        }
        log.info("Message:CHATTERNAME=" + chatterName);
        if (chatterName != null) {
            log.info("Message:Info pre=" + node.getStringValue("info"));
            setInfoField(node, "name", chatterName);
            log.info("Message:Info post=" + node.getStringValue("info"));
        }
        node.setValue(F_SUBJECT, subject);
        node.setValue(F_BODY, body);
        if (node.commit())
            return POST_OK;
        else
            return POST_ERROR_UNKNOWN;
    }

    /**
     * Retrieves a list of messages related to a thread.
     * The tagger parameter specified what fields to treturn, the order of
     * the messages, and any filters and options to use for the list.
     * <br />
     * Attributes that can be set for th list are:
     * <ul>
     * <li>NODE : the number of the message or channel that is the parent of
     *      the messages requested.</li>
     * <li>FIELDS : a Vector with fieldnames to return. This may change as some
     *      fields are mandatory in certain situations.</li>
     * <li>ITEMS : this is filled by this method with the actual number of
     *      fields that are returned</li>
     * <li>FROMCOUNT : starts messsages after the specified number of messages
     *      in the list that results form this query.</li>
     * <li>MAXCOUNT : Maximum number of messages to return</li>
     * <li>STARTAFTERNODE : starts messages after the message identified
     *      with the specified node number. Does not work for chats.</li>
     * <li>STARTAFTERSEQUENCE: starts messages after the message identified
     *      with the specified sequence number.</li>
     * <li>MAXDEPTH</li> the maximum depth at which to search for (replies to)
     *      messages</li>
     * <li>SORTFIELDS or DBSORT : the fields to sort on </li>
     * <li>SORTDIRS or DBDIR : the direction to sort the fields on (UP or DOWN)</li>
     * <li>OPENTAG : a tag to use instead of <code>&lt;ul&gt;<code> for the
     *      <code>listhead</code> field. Should be tagname without the tag delimiters.</li>
     * <li>CLOSETAG : a value to use instead of <code>&lt;/ul&gt;<code> for the
     *      <code>listtail</code> field. Should be tagname without the tag delimiters.</li>
     * </ul>
     *
     * @param params the attributes of the LIST tag.
     * @return A <code>Vector</code> containing the requested fields.
     */
    public Vector<String> getListMessages(StringTagger params) {
        /* Get the thread/node from who the related messages have to be given.
         */
        String id = params.Value("NODE");
        MMObjectNode node = getNode(id);
        if (node == null) {
            log.debug("getListMessages(): no or incorrect node specified");
            return new Vector<String>();
        }

        /* Get the fieldnames out of the FIELDS attribute.
         */
        Vector<String> fields = params.Values("FIELDS");

        /*
         * When fields contains listhead it's assumed a <ul> HTML listing has to get generated.
         * A <ul> list can only be generated when listhead, listtail and depth are both used.
         * Since depth is optional it's added automatically when absent.
         * OPENTAG and CLOSETAG can contain alternate tags for the <ul> tag(s).
         */
        String openTag = LIST_HEAD_TAG;
        String closeTag = LIST_TAIL_TAG;
        int listheadItemNr = fields.indexOf("listhead");
        int listtailItemNr = -1;
        int depthItemNr = -1;
        if (listheadItemNr >= 0) {
            listtailItemNr = fields.indexOf("listtail");
            depthItemNr = fields.indexOf("depth");
            // add depth to fiel;ds
            if (depthItemNr < 0) {
                fields.add("depth");
                depthItemNr = fields.indexOf("depth");
            }
            openTag = params.Value("OPENTAG");
            closeTag = params.Value("CLOSETAG");
            if ((openTag == null) || (closeTag == null)) {
                openTag = LIST_HEAD_TAG;
                closeTag = LIST_TAIL_TAG;
            } else {
                openTag = "<" + openTag.replace('\'', '"').replace('#', '=') + ">";
                closeTag = "</" + closeTag + ">";
            }
        }

        // Put in params the number of fields that will get returned.
        params.setValue("ITEMS", "" + fields.size());

        // Get fromCount and maxCount.
        String tmp = params.Value("FROMCOUNT");
        int fromCount;
        if (tmp != null) fromCount = Integer.decode(tmp).intValue(); else fromCount = 0;
        int maxCount;
        tmp = params.Value("MAXCOUNT");
        // MAXCOUNT was maxCount (now really!) line below is to allow support for 'old'
        // communities, but should be dropped!
        if (tmp == null) tmp = params.Value("maxCount");

        if (tmp != null) maxCount = Integer.decode(tmp).intValue(); else maxCount = Integer.MAX_VALUE;
        int maxDepth;
        tmp = params.Value("MAXDEPTH");
        if (tmp != null) maxDepth = Integer.decode(tmp).intValue(); else maxDepth = Integer.MAX_VALUE;

        // Get startAfterNode / startAfterSequence
        String nodeselectfield = "number";
        int startAfterNode = -1;

        tmp = params.Value("STARTAFTERNODE");
        try {
            if (tmp != null) {
                startAfterNode = Integer.decode(tmp).intValue();
            } else {
                tmp = params.Value("STARTAFTERSEQUENCE");
                if (tmp != null) {
                    startAfterNode = Integer.decode(tmp).intValue();
                    nodeselectfield = F_SEQUENCE;
                }
            }
        } catch (NumberFormatException e) {
            log.error("" + e);
        }

        /* Create a NodeComparator. If no SORTFIELDS or DBSORT are specified in
         * the list sequence is used as a default.
         * Sortdirections can be specified in SORTDIRS or DBDIR.
         */
        Vector<String> sortFields = params.Values("SORTFIELDS");
        if (sortFields == null) {
            sortFields = params.Values("DBSORT");
            if (sortFields == null) {
                sortFields = new Vector<String>(1);
                sortFields.add(F_SEQUENCE);
            }
        }
        Vector<String> sortDirs = params.Values("SORTDIRS");
        if (sortDirs == null) sortDirs = params.Values("DBDIR");
        NodeComparator compareMessages;
        if (sortDirs == null) {
            compareMessages = new NodeComparator(sortFields);
        } else {
            compareMessages = new NodeComparator(sortFields, sortDirs);
        }

        Vector<String> result = null;

        if (maxCount == Integer.MAX_VALUE) {
            // no max, grab everything
            result = getListMessages(node, fields, compareMessages, maxCount, 0,
                    maxDepth, startAfterNode, nodeselectfield);
        } else {
            // limit list to given max of items
            result = getListMessages(node, fields, compareMessages, fromCount + maxCount, 0,
                    maxDepth, startAfterNode, nodeselectfield);
            int realCount = (fromCount + maxCount) * fields.size();
            int realFromCount = (fromCount) * fields.size();
            if (result.size() > realCount) {
                result = new Vector<String>(result.subList(realFromCount, realCount));
            } else if (realFromCount > 0) {
                if (realFromCount >= result.size()) {
                    result = new Vector<String>();
                } else {
                    result = new Vector<String>(result.subList(realFromCount, result.size()));
                }
            }
        }

        if ((listheadItemNr >= 0) && (listtailItemNr >= 0)) {
            addListTags(result, listheadItemNr, listtailItemNr, depthItemNr, fields.size(), openTag, closeTag);
        } else if ((listheadItemNr >= 0) || (listtailItemNr >= 0)) {
            log.error("getListMessages(): Listhead and listtail only work when used together.");
        }
        return result;
    }

    /**
     * Retrieves a list of messages related to a thread.
     * @param thread the number of the message or channel that is the parent of
     *      the messages requested
     * @param fields a Vector with fieldnames to return. This may change as some
     *      fields are mandatory in certain situations
     * @param ci A Comparator to use for sorting the messages
     * @param maxCount : Maximum number of messages to return</li>
     * @param depth the current depth at which emssages are searched
     * @param maxDepth the maximum depth at which to search for (replies to)
     *      messages
     * @param startAfterNode starts messages after the message identified
     *      with the specified value
     * @param nodeselectfield field to which to compare the value of
     *      startAfterNode (i.e. 'number' or 'sequence').
     * @return A <code>Vector</code> containing the requested fields.
     */
    public Vector<String> getListMessages(MMObjectNode thread, Vector<String> fields, Comparator<MMObjectNode> ci,
                                  int maxCount, int depth, int maxDepth, int startAfterNode,
                                  String nodeselectfield) {
        Vector<String> result = new Vector<String>(), childMsgs;
        Vector<MMObjectNode> relatedMessages = getRelatedMessages(thread, ci);

        int msgPointer = relatedMessages.size() - 1;
        int added = 0, count = 0;
        String item, cmd;
        MMObjectNode relmsg;
        int fieldsCount = fields.size();
        int replycount;
        boolean startAfterNodePassed = (startAfterNode == -1);

        while ((msgPointer >= 0) && (added < maxCount)) {
            relmsg = relatedMessages.elementAt(msgPointer);

            startAfterNodePassed = startAfterNodePassed ||
                    (startAfterNode == relmsg.getIntValue(nodeselectfield));

            if (depth < maxDepth) {
                childMsgs = getListMessages(relmsg, fields, ci, maxCount - added, depth + 1,
                        maxDepth, startAfterNode, nodeselectfield);
                replycount = childMsgs.size() / fieldsCount;
                added += replycount;

                /* When recursion stops because we've got a big enough result,
                 * the number of returned childMsg is less than the actual ammount of childmessages
                 * and replycount isn't calculated correctly, therefor getNrMsgAndHighSeq is called.
                 */
                if (added == maxCount) replycount = getNrMsgAndHighSeq(relmsg).messageCount;
            } else {
                childMsgs = new Vector<String>();
                replycount = getNrMsgAndHighSeq(relmsg).messageCount;
            }

            if (startAfterNodePassed) {

                added++; // Another message is going to be added.

                /* Add the fields of the relmsg to the result and then of his children.
                */
                for (int i = 0; i < fieldsCount; i++) {
                    cmd = fields.elementAt(i).trim();
                    if (cmd.equals("depth")) {
                        item = "" + depth;
                    } else if (cmd.equals("replycount")) {
                        item = "" + replycount;
                    } else if ((cmd.equals("listhead")) ||
                            (cmd.equals("listtail"))) {
                        item = "";
                    } else {
                        item = "" + relmsg.getValue(cmd);
                    }
                    result.add(item);
                }
                result.addAll(childMsgs);
            }
            msgPointer--;
            count++;
        }
        return result;
    }

    /**
     * Retrieves a list of messages related to a thread.
     * @param node the message or channel node that is the parent of
     *      the messages requested
     * @param ci A Comparator to use for sorting the messages
     * @return A <code>Vector</code> containing the requested message nodes.
     */
    public Vector<MMObjectNode> getRelatedMessages(MMObjectNode node, Comparator<MMObjectNode> ci) {
        // we might decide to use a SortedSet instead
        Vector<MMObjectNode> result = new Vector<MMObjectNode>();
        Enumeration<MMObjectNode> relatedMessages;

        MMObjectNode channel = isPostedInChannel(node);
        MMObjectNode community = channelBuilder.communityParent(channel);
        String kind;
        if (community != null) {
            kind = (String) community.getValue("kind");
        } else {
            kind = "forum";
        }

        if (kind.equalsIgnoreCase("chatbox")) {
            relatedMessages = getTemporaryRelated(node, "message").elements();
        } else {
            relatedMessages = getReplies(node);
        }

        if (relatedMessages == null) return result;

        MMObjectNode relmsg = null;
        int parent = node.getNumber();
        while (relatedMessages.hasMoreElements()) {
            // XXX: Tempory hack: test if the related message is indeed
            // a child by his thread field.
            relmsg = relatedMessages.nextElement();
            if (relmsg.getIntValue(F_THREAD) == parent)
                result.add(relmsg);
        }
        Collections.sort(result, ci);
        return result;
    }

    /**
     * Add a node to a result vector, provided the type matches and the
     * current count is higher than the offset of the resultlist.
     */
    private int addRelatedNode(MMObjectNode node, int otypeWanted, int count, int offset, Vector<MMObjectNode> result) {
        if ((node != null) &&
                (node.getIntValue("otype") == otypeWanted)) {
            count += 1;
            if (count > offset) {
                result.add(node);
            }
        }
        return count;
    }

    /**
     * Add a node identified by number to a result vector,
     * provided the type matches and the current count is higher than the
     * offset of the resultlist.
     */
    private int addRelated(int number, int otypeWanted, int count, int offset, Vector<MMObjectNode> result) {
        MMObjectNode node = getNode(number);
        return addRelatedNode(node, otypeWanted, count, offset, result);
    }

    /**
     * Add a temporary node identified by key to a result vector,
     * provided the type matches and the current count is higher than the
     * offset of the resultlist.
     */
    private int addRelated(Object key, int otypeWanted, int count, int offset, Vector<MMObjectNode> result) {
        if (key != null) {
            MMObjectNode node = temporaryNodes.get("" + key);
            count = addRelatedNode(node, otypeWanted, count, offset, result);
        }
        return count;
    }

    /**
     * Get temporary MMObjectNodes related to a specified MMObjectNode
     * @param node this is the source MMObjectNode
     * @param wtype Specifies the type of the nodes you want to have e.g. wtype="pools"
     */
    public Vector<MMObjectNode> getTemporaryRelated(MMObjectNode node, String wtype) {
        return getTemporaryRelated(node, wtype, 0, Integer.MAX_VALUE);
    }

    /**
     * Get temporary MMObjectNodes related to a specified MMObjectNode
     * @param node this is the source MMObjectNode
     * @param wtype Specifies the type of the nodes you want to have e.g. wtype="pools"
     */
    public Vector<MMObjectNode> getTemporaryRelated(MMObjectNode node, String wtype,
                                      int offset, int max) {
        Vector<MMObjectNode> result = new Vector<MMObjectNode>();
        if (max <= 0) return result;
        MMObjectNode tmpInsRel;
        boolean found;
        int otypewanted = mmb.getTypeDef().getIntValue(wtype);
        int count = 0;

        // Get the node's number or _number.
        int number = -1;
        String _number = (String) node.getValue("_number");
        if (_number == null) number = node.getNumber();

        // Get all temporary nodes and filter out all insrels.
        Iterator tmpInsRels = temporaryNodes.keySet().iterator();
        while ((count < (offset + max)) && tmpInsRels.hasNext()) {
            tmpInsRel = temporaryNodes.get(tmpInsRels.next());
            if (tmpInsRel != null) {
                if (tmpInsRel.getBuilder() instanceof InsRel) {
                    found = false;
                    // Test if the (_)snumbers are equal.
                    if (_number != null) {
                        found = _number.equals(tmpInsRel.getStringValue("_snumber"));
                    } else {
                        found = (number == tmpInsRel.getIntValue("snumber"));
                    }
                    if (found) { // snumbers are equal
                        int dnumber = tmpInsRel.getIntValue("dnumber");
                        if (dnumber > -1) {
                            count = addRelated(dnumber, otypewanted, count, offset, result);
                        } else {
                            count = addRelated(tmpInsRel.getValue("_dnumber"),
                                    otypewanted, count, offset, result);
                        }
                    } else {
                        if (_number != null) {
                            found = _number.equals(tmpInsRel.getStringValue("_dnumber"));
                        } else {
                            found = (number == tmpInsRel.getIntValue("dnumber"));
                        }
                        if (found) { // (_)dumbers are equal.
                            int snumber = tmpInsRel.getIntValue("snumber");
                            if (snumber > -1) {
                                count = addRelated(snumber, otypewanted, count, offset, result);
                            } else {
                                count = addRelated(tmpInsRel.getValue("_snumber"),
                                        otypewanted, count, offset, result);
                            }
                        }
                    }
                }
            }
        }
        return result;
    }

    /**
     * Adds the values of <code>listhead</code> and <code>listtail</code> to a
     * list of fields.
     * There was no easy way for letting getListMessages() fill in the virtual
     * fields <code>listhead</code> and <code>listtail</code>, so it's done
     * here instead.
     * @param fields contains a list of message field values, ordered by message and fieldnumber.
     * @param listheadItemNr the field number of the <code>listhead</code> field
     * @param listtailItemNr the field number of the <code>listtail</code> field
     * @param depthItemNr the field number of the <code>depth</code> field
     * @param fieldscount teh numbe rof fields per message stored in fields
     * @param opentag the base value to enter in <code>listhead</code> when a
     *      message is the first in a thread
     * @param closetag the base value to enter in <code>listhead</code> when
     *      a message follows a thread or to <code>listtail</code> when a
     *      message is the last in the list.
     */
    private void addListTags(Vector<String> items, int listheadItemNr, int listtailItemNr,
                             int depthItemNr, int fieldsCount, String openTag, String closeTag) {
        int depth;
        int previousDepth = 0;
        int itemNr = 0;
        int itemsCount = items.size();
        String prefix, postfix;
        while (itemNr < itemsCount) {
            depth = (Integer.decode(items.elementAt(itemNr + depthItemNr))).intValue();
            if (itemNr == 0) {
                prefix = openTag; // begin of the list
                while (previousDepth < depth) {
                    prefix += openTag; // begin of sublist, just in case a page starts with a sublist
                    previousDepth++;
                }
            } else if (depth > previousDepth) {
                prefix = openTag; // begin of a sublist
            } else {
                prefix = "";
            }
            postfix = "";
            if (depth < previousDepth) {
                while (previousDepth > depth) {
                    prefix += closeTag; // end of one ore more sublists
                    previousDepth--;
                }
            }
            if (itemNr == itemsCount - fieldsCount) {
                while (depth > 0) {
                    postfix += closeTag; // end of one ore more sublists
                    depth--;
                }
                postfix += closeTag;
            }
            items.set(itemNr + listheadItemNr, prefix);
            items.set(itemNr + listtailItemNr, postfix);
            itemNr += fieldsCount;
            previousDepth = depth;
        }
    }

    /**
     * Remove a node if it's not a parent message to another message.
     * If it is, the node is not removed.
     * @param node the node to remove
     */
    public void removeNode(MMObjectNode node) {
        removeNode(node, false);
    }

    /**
     * Remove a node and, optionally, it's child nodes.
     * @param node the node to remove
     * @param recursive if <code>true</code>, the node will be removed along with
     *      its childnodes if it has any. Otherwise, it will only be removed if the ndoe
     *      hs no children.
     *
     */
    public void removeNode(MMObjectNode node, boolean recursive) {
        log.service("removeNode(): node=" + node.getNumber());

        // Make sure we got a Message node, else abort the remove operation.
        if (!(node.getBuilder() instanceof Message)) {
            log.error(node.getNumber() + " is of type " + node.getName() + " instead of Message");
            return;
        }

        // If recursive is true delete the childmessages
        // else abort on any childmessages.
        MMObjectNode relmsg;
        for (Enumeration<MMObjectNode> messages = getReplies(node); messages.hasMoreElements();) {
            relmsg = messages.nextElement();
            // Before doing all this hot stuff, there should be
            // a check preventing recorded messages get deleted!
            // (not an issue right now, as session is not implemented)
            if (relmsg.getIntValue(F_THREAD) == node.getNumber()) {
                // Here we have a childmessage.
                if (recursive) {
                    removeNode(relmsg, true);
                } else {
                    log.error("Can't delete Message " + node.getNumber() + " because it has child Messages.");
                    return;
                }
            }
        }
        // Remove all relations to other nodes.
        InsRel insrel = mmb.getInsRel();
        insrel.removeRelations(node);
        if (node.hasRelations()) {
            log.error("Failed to remove node relations");
        }
        // Now it's possible to remove the node itself.
        super.removeNode(node);
    }

    public ThreadStats getNrMsgAndHighSeq(MMObjectNode node) {
        /* PRE:  A message or channel node.
         * POST: A ThreadStats object which contains the number of messages
         *       and the highest sequence in this thread.
         */
        int sequence = -1;
        int highestSequence = -1;
        int messageCount = 0;
        MMObjectNode msg;
        for (Enumeration<MMObjectNode> messages = getReplies(node); messages.hasMoreElements();) {
            msg = messages.nextElement();
            if (msg.getIntValue(F_THREAD) == node.getNumber()) {
                sequence = msg.getIntValue(F_SEQUENCE);
                if (sequence > highestSequence) highestSequence = sequence;
                /* Get the nummer of messages and the highes sequence in this childmessage.
                 */
                ThreadStats cs = getNrMsgAndHighSeq(msg);
                messageCount += cs.messageCount + 1;
                if (cs.highestSequence > highestSequence) highestSequence = cs.highestSequence;
            }
        }
        return new ThreadStats(node.getNumber(), messageCount, highestSequence);
    }

    /**
     * Returns whether a node has replies or not.
     * @param node the node to find the relations of
     * @return <code>true</code> if the node has related messages.
     */
    public boolean hasReplies(MMObjectNode node) {
        return getNrMsgAndHighSeq(node).messageCount > 0;
    }

    /**
     * Get all relations for a node with other messages.
     * These are not necessarily all a reply to this message (it may be a
     * parent message).
     * XXX: should be fixed with a directional modifier, but then we first need
     * a few additonal calls in InsRel or MMObjectNode.
     * @param node the node to find the relations of
     * @return an <code>Enumeration</code> containign the related messages.
     */
    public Enumeration<MMObjectNode> getReplies(MMObjectNode node) {
        return node.getRelatedNodes("message").elements();
    }

    /**
     * Provides additional functionality when obtaining field values.
     * This method is called whenever a Node of the builder's type fails at
     * evaluating a getValue() request (generally when a fieldname is supplied
     * that doesn't exist).
     * <br />
     * The following virtual fields are calculated:
     * <br />
     * timestamp - returns the timestamp of the message
     * <br />
     * resubject - returns the subject with a "RE:" prefix (if needed)
     * <br />
     * replycount - returns the number of replies
     * <br />
     * hasreplies - returns "true" if the message has replies, "false" otherwise
     * <br />
     * parent - returns the number of the parent node (same as "thread")
     *
     * @param node the node whose fields are queried
     * @param field the fieldname that is requested
     * @return the result of the call, <code>null</code> if no valid functions or virtual fields could be determined.
     */
    public Object getValue(MMObjectNode node, String field) {
        // if we get here, timestamp is NOT an existing field,
        // so retrieve the values using the two integer fields
        if (field.equals(F_TIMESTAMP)) {
            TimeStamp ts = new TimeStamp(); // default, return current time
            if (super.getValue(node, F_TIMESTAMP) == null) { // no value to be found.
                if (getField(F_TIMESTAMP) == null) { // field does not exist
                    ts = getTimeStamp(node);
                }
            }
            return "" + ts.getTime();
        }
        if (field.equals(F_TIMESTAMPSEC)) {
            long ts = node.getLongValue(F_TIMESTAMP) / 1000;
            return new Long(ts);
        }
        if (field.equals(F_RE_SUBJECT)) {
            String subject = node.getStringValue(F_SUBJECT);
            if (subject.startsWith("RE: ")) return subject; else return ("RE: " + subject);
        }
        if (field.equals(F_REPLY_COUNT)) {
            return new Integer(getNrMsgAndHighSeq(node).messageCount);
        }
        if (field.equals(F_HAS_REPLIES)) {
            return Boolean.valueOf(hasReplies(node));
        }
        if (field.equals(F_PARENT)) {
            return node.getValue(F_THREAD);
        }
        // XXX: substring is supported for now, but should be dropped as it already
        // exists in MMObjectBuilder.
        // the main difference is that MMObjectBuilder separates values with
        // comma or semicolon, while this version seperates using 'x'.
        if (field.startsWith("substring")) {
            /* Something like substring(realfield,size) is expected. The realField is asked from the node and
             * truncated to the size. Truncation is done before whole words.
             */
            int commaPos = field.lastIndexOf('x');
            String realField = field.substring(10, commaPos); // 10 is the beginning of the first character after '('.
            String value = (String) node.getValue(realField);
            int size = Integer.decode(field.substring(commaPos + 1, field.length() - 1)).intValue();
            if (size < value.length())
                return value.substring(0, size);
            else
                return value;
        }                         // ??? Must still cut for new word!

        Object o = super.getValue(node, field);
        if (o instanceof String) {
            if ((field.indexOf("html(") < 0) && (field.indexOf("html_") < 0))
                o = getHTML((String) o);
        }
        return o;
    }

    /**
     * Executes a function on the field of a node, and returns the result.
     * This method is called by the builder's {@link #getValue} method.
     * Functions implemented are hasreplies() and getinfovalue().
     * @param node the node whose fields are queries
     * @param field the fieldname that is requested
     * @return the result of the 'function', or null if no valid functions could be determined.
     */
    protected Object executeFunction(MMObjectNode node, String function, String field) {
        if (function.equals(F_GET_INFO_VALUE)) {
            return getInfoField(node, field);
        } else {
            return super.executeFunction(node, function, field);
        }
    }

    /**
     * Handles the $MOD-MMBASE-BUILDER-message- commands.
     */
    public String replace(PageInfo sp, StringTokenizer tok) {
        /* The first thing we expect is a message number.
         */
        if (!tok.hasMoreElements()) {
            log.error("replace(): message number expected after $MOD-BUILDER-message-.");
            return "";
        }
        String tmp = tok.nextToken();
        MMObjectNode message = getNode(tmp);
        //tmp = tmp.substring(tmp.indexOf("_") + 1);
        if (message == null) message = temporaryNodes.get(tmp.trim());
        if (message == null) {
            log.error("Message with id '" + tmp + "' cannot be found.");
            return "";
        }

        if (tok.hasMoreElements()) {
            String cmd = tok.nextToken();
            if (cmd.equals("DEL")) removeNode(message, true);
            if (cmd.equals("GETINFOFIELD")) return getInfoField(message, tok.nextToken());
            if (cmd.equals("SETINFOFIELD")) {
                setInfoField(message, tok.nextToken(), tok.nextToken());
                message.commit();
            }
        }
        return "";
    }

    /**
     * Set a value in the multi-purpose <code>info</code> field.
     * The info field of the message node contains a StringTagger.
     * This function sets the field in the tagger to value.
     * @param message the node in which to store the data
     * @param field the name of the value as it is to be stored in <code>info</code>
     * @param value the value to store
     */
    private void setInfoField(MMObjectNode message, String field, String value) {
        value = remove(value, "\n=,\"");
        String info = message.getStringValue(F_INFO);
        StringTagger tagger = new StringTagger(info, '\n', '=', ',', '\"');
        tagger.setValue(field, value);
        String key = "";
        String content = "";
        for (Iterator i = tagger.keySet().iterator(); i.hasNext();) {
            key = (String) i.next();
            content += key + "=" + tagger.Value(key) + "\n";
        }
        message.setValue(F_INFO, content);
    }

    private String remove(String s, String r) {
        if (s == null) return null;
        String result = "";
        for (int i = 0; i < s.length(); i++) {
            char c = s.charAt(i);
            if (r.indexOf(c) < 0)
                result = result + c;
        }
        return result;
    }

    /**
     * Obtain a value from the multi-purpose <code>info</code> field.
     * The info field of the message node contains a StringTagger.
     * This function gets the value out of the field in the tagger.
     * @param message the node from which to obtain the data
     * @param field the name of the value as it is known in <code>info</code>
     * @return the retrieved value as a <code>String</code>
     */
    private String getInfoField(MMObjectNode message, String field) {
        String info = message.getStringValue(F_INFO);
        StringTagger tagger = new StringTagger(info, '\n', '=', ',', '\"');
        return getHTML(tagger.Value(field));
    }

    /**
     * Get the nodes timestampl and timestamph and return them as a TimeStamp.
     */
    public TimeStamp getTimeStamp(MMObjectNode node) {
        return new TimeStamp((Integer) node.getValue("timestampl"), (Integer) node.getValue("timestamph"));
    }

    /**
     * Returns the channel in which the given message node is posted.
     * @param node the node to get the channel of
     * @return the channel of the message as an <code>MMObjectNode</code>
     */
    public MMObjectNode isPostedInChannel(MMObjectNode node) {
        while (node.getBuilder() instanceof Message) {
            node = getNode(node.getIntValue(F_THREAD));
        }
        return node;
    }

    /**
     * Generates a new temporary key for a temporary message.
     */
    synchronized public String getNewTemporaryKey() {
        return (tmpNumberPrefix + tmpNumbers++);
    }
}

/**
 * Class for holding statistics on a thread.
 */
class ThreadStats {
    /** The thread (channel or message number) to which these stats belong */
    public int thread = -1;
    /** The number of messages in this thread */
    public int messageCount = 0;
    /** The highest sequence number in this thread */
    public int highestSequence = 0;

    /**
     * Create a statistics object for a thread
     * @param thread the thread (channel or message number) to which these stats belong
     * @param messageCount  The number of messages in this thread
     * @param highestsequence The highest sequence number in this thread
     */
    public ThreadStats(int thread, int messageCount, int highestSequence) {
        this.thread = thread;
        this.messageCount = messageCount;
        this.highestSequence = highestSequence;
    }
}
