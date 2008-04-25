/*

This software is OSI Certified Open Source Software.
OSI Certified is a certification mark of the Open Source Initiative.

The license (Mozilla version 1.0) can be read at the MMBase site.
See http://www.MMBase.org/license

*/

package org.mmbase.applications.community.builders;

import java.util.*;
import java.io.*;

import org.mmbase.module.core.*;
import org.mmbase.applications.community.modules.*;
import org.mmbase.util.*;
import org.mmbase.util.logging.*;

/**
 * The channel builder maintains channels for forums and chats.
 * Each channel is linked to a  {@link Community}, which defines some properties for
 * the channel (such as whether it is a forum or a chatbox).
 * Users can join a channel (up to a registered amount of users), and post
 * messages to it (stored in the {@link Message} builder).
 * <br />
 * The main tasks defined in this builder are ways to open and close a channel,
 * join users, generate sequence numbers (which defines the order for a message),
 * and remove messages belonging to a channel.
 * Optionally, a recorder (java.io.Writer) object can be opened for a
 * channel. The Message builder uses these writers for logging chats.
 * (Note that is would be possible to make a Writer that stores data in a node
 * or a builder, but this has not been implemented yet).
 * XXX: Currently, recorder info is NOT stored in the channel. A recorder has to
 * be activated manually (using $MOD-channelnr-RECORD-FILE-filname).
 *
 * @author Dirk-Jan Hoekstra
 * @author Pierre van Rooden
 * @version $Id: Channel.java,v 1.31 2007-06-21 15:50:22 nklasens Exp $
 */

public class Channel extends MMObjectBuilder {

    /** Open state : the channel is closed. */
    public static final int CLOSED = -1;
    /** Open state : the channel is waiting to be opened. */
    public static final int WANT_OPEN = 0;
    /** Open state : the channel is open for read-only. */
    public static final int READ_ONLY = 1;
    /** Open state : the channel is open. */
    public static final int OPEN = 2;

    /** Join state : the user cannot be connected to the channel. */
    public static final int FAILED = -1;
    /** Join state : the channel is full. */
    public static final int CHANNEL_FULL = 0;
    /** Join state : the user is connected to the channel. */
    public static final int CONNECTED = 1;
    /** Join state : the user is already connected to the channel. */
    public static final int ALREADY_CONNECTED = 2;
    /** Join state : the user is logged out of the channel. */
    public static final int DISCONNECTED = 3;

    /** Field : open */
    public static final String F_OPEN = "open";
    /** Field : maxusers */
    public static final String F_MAXUSERS = "maxusers";
    /** Field : state */
    public static final String F_STATE = "state";
    /** Field : session */
    public static final String F_SESSION = "session";

    /** Virtual Field : readlogin */
    public static final String F_READLOGIN = "readlogin";
    /** Virtual Field : writelogin */
    public static final String F_WRITELOGIN = "writelogin";
    /** Virtual Field or Function : hasmoods or hasmoods()  */
    public static final String F_HASMOODS = "hasmoods";

    /** state : read login bit */
    public static final int  STATE_READ_LOGIN = 1;
    /** state : write login bit */
    public static final int  STATE_WRITE_LOGIN = 2;

    // string constants for Gui representation
    // returned by isOpen().
    private static final String STR_OPEN = "open";
    private static final String STR_CLOSED = "closed";
    private static final String STR_READ_ONLY = "readonly";

    // logger
    private static Logger log = Logging.getLoggerInstance(Channel.class.getName());

    // Temporary node manager
    private TemporaryNodeManager tmpNodeManager = null;

    // Message builder
    private Message messageBuilder;
    // object type of the community builder
    private Community communityBuilder;
    // default expiration time for relation breaker
    private int expireTime = 5 * 60 * 1000;
    // NodeBreaker for maintaining temporary relations with users
    private NodeBreaker chatboxConnections = null;
    // default chat owner
    private String tOwner = "system";
    // indicates whether this builder has been activated for the community application
    private boolean active = false;

    /**
     * This Hashtable contains all open channels with their highest sequence
     * number for which the highseq is keeped track of in memory.
     */
    private Hashtable<Integer, Integer> openChannels = new Hashtable<Integer, Integer>();

    // Holds the recorders associated with their channels.
    private Hashtable<Integer, Writer> recorders = new Hashtable<Integer, Writer>();
    // The base file path for log files.
    private String baseRecordPath = null;
    // The default filename for log files.
    private String defaultrecordfile = "chat.log";
    // The default suer type used in searches
    private String defaultUserType = null;

    /**
     * Constructor
     */
    public Channel() {
    }

    /**
     * Initializes the Channel builder.
     * <br />
     * Creates references to other required builders and relation roles.
     * <br />
     * Also opens all channels whose <code>open</code> field is set to {@link #OPEN} or
     * {@link #WANT_OPEN}. This step can be skipped, to speed up startup of the server,
     * by specifying the property <code>open-channels-on-startup</code> in the
     * Channel builder configuration, and setting it to <code>false</code>.
     */
    public boolean init() {
        boolean result = super.init();
        // for recorders: find out basic logpath
        baseRecordPath=getInitParameter("baserecordpath");
        if ((baseRecordPath!=null) && (baseRecordPath.length()!=0)) {
            if (baseRecordPath.charAt(baseRecordPath.length()-1)!=File.separatorChar) {
                baseRecordPath+=File.separator;
            }
        }
        defaultUserType=getInitParameter("defaultusertype");
        if ((defaultUserType==null) || (defaultUserType.length()==0)){
            defaultUserType="chatter";
        }

        activate();
        return result;
    }

    /**
     * Activates the channel builder for the community application by associating it with other
     * community builders and opening all communities.
     * @return true if activation worked
     */
    public boolean activate() {
        if (!active) {
            // get message builder
            messageBuilder   = (Message) mmb.getMMObject("message");
            // get community object type
            communityBuilder = (Community) mmb.getMMObject("community");
            if (messageBuilder != null && communityBuilder != null) {
                // obtain temporary node manager
                tmpNodeManager = TransactionManager.getInstance().getTemporaryNodeManager();
                // create relation breaker for maintaining temporary relations
                chatboxConnections = new NodeBreaker(2 * expireTime, tmpNodeManager);
                active = true;
                try {
                    // open-channels-on-startup property:
                    String strOpenChannelsOnStartup = getInitParameter("open-channels-on-startup");
                    boolean openChannelsOnStartup = !"false".equals(strOpenChannelsOnStartup);
                    // Open all channels, unless the configuration specifies not to
                    if (openChannelsOnStartup) {
                        openChannels();
                    }
                } catch (Exception e) {
                    // opening all channels may throw an error if the community is not properly installed
                    active = false;
                }
            }
        }
        return active;
    }

    /**
     *  Lookup all channels in the cloud of who the field open is set to
     *  (want)open and try to open all these channels.
     */

    private void openChannels() {
        if (communityBuilder != null) {
            communityBuilder.openAllCommunities();
        }
/*
        log.debug("Try to open all channels who are set open in the database");
        String where="WHERE "+mmb.getDatabase().getAllowedField(F_OPEN)+"=" + OPEN +
                     " OR "+mmb.getDatabase().getAllowedField(F_OPEN)+"=" + WANT_OPEN;
        Enumeration openChannels = search(where);
        while (openChannels.hasMoreElements()) {
            open((MMObjectNode)openChannels.nextElement());
        }
*/
    }

    /**
     * Opens the channel.
     *
     * @param channel The channel to open.
     * @return <code>true</code> if opening the channel was successfull.
     */
    public boolean open(MMObjectNode channel) {
        MMObjectNode community = communityParent(channel);
        if (community==null) {
            log.error("open(): Can't open channel " + channel.getNumber()+" : no relation with a community");
            return false;
        }
        return open(channel,community);
    }

    /**
     * Opens the channel.
     *
     * @param channel The channel to open.
     * @param community The community with this channel's settings
     * @return <code>true</code> if opening the channel was successfull.
     */
    public boolean open(MMObjectNode channel, MMObjectNode community) {
        // Try to open the channel, when the channel is part of a chatbox put
        // the channel with his highest sequence in the openChannels HashTable.
        Integer channelnr=new Integer(channel.getNumber());
        if (log.isDebugEnabled())
            log.debug("open(): Opening channel "+channelnr+" ("+channel.getValue("name")+")");

        channel.setValue(F_OPEN, OPEN);
        int highestSequence = channel.getIntValue("highseq");
        boolean isChatBox = community.getStringValue("kind").equalsIgnoreCase(Community.STR_CHATBOX);
        if (isChatBox) {
            // this way we can check if the channel gets closed properly
            channel.setValue("highseq", -1);
        }
        if (channel.commit()) {
            if (isChatBox) {
                if (openChannels.get(channelnr) == null) {
                    openChannels.put(channelnr,new Integer(highestSequence));
                 }
            }
            log.debug("open(): channel "+channelnr+" opened");
            return true;
        }
        log.error("open(): Can't open channel "+channelnr);
        return false;
    }

    /**
     * Closes the channel.
     *
     * @param channel The channel to close.
     * @return <code>true</code> if closing the channel was successfull.
     */
    public boolean close(MMObjectNode channel) {
        Integer channelnr=new Integer(channel.getNumber());
        if (channel.getIntValue(F_OPEN) != CLOSED) {
            channel.setValue(F_OPEN, CLOSED);
            Integer highseq = openChannels.get(channelnr);
            if (highseq != null) channel.setValue("highseq", highseq);
            if (channel.commit()) {
                log.debug("close(): channel "+channelnr+"("+channel.getValue("name")+") closed.");
                openChannels.remove(channelnr);
                return true;
            }
            log.error("close(): Can't close channel "+channelnr);
            return false;
        }
        return true;
    }

    /**
     * Makes a channel read only.
     * @param channel The channel to affect.
     * @return <code>true</code> if changing the channel open status was successfull.
     */
    public boolean readonly(MMObjectNode channel) {
        Integer channelnr=new Integer(channel.getNumber());
        if (channel.getIntValue(F_OPEN) != READ_ONLY) {
            channel.setValue(F_OPEN, READ_ONLY);
            if (channel.commit()) {
                log.debug("close(): channel "+channelnr+"("+channel.getValue("name")+") made read only.");
                return true;
            }
            log.error("close(): Can't make channel "+channelnr+" read only");
            return false;
        }
        return true;
    }

    /**
     * Returns if a channel is open, closed or readonly.
     *
     * @param channel The channel of which the open state is asked.
     * @return the state of the channel, described as a <code>String</code>
     */
    public String isOpen(MMObjectNode channel) {
        switch (channel.getIntValue(F_OPEN)) {
            case OPEN: return STR_OPEN;
            case READ_ONLY: return STR_READ_ONLY;
            case CLOSED: return STR_CLOSED;
            default:
                log.warn("isOpen: the following channel has an invalid open value: " +
                          channel + " - assuming the channel is closed.");
                return STR_CLOSED;
        }
    }

    /**
     * @param channel The channel that has to give out a new sequence number.
     * @return A new sequence number for a message that's want to get postend in this channel.
     */
    public int getNewSequence(MMObjectNode channel) {
        int newHighseq;
        Integer channelnr=new Integer(channel.getNumber());
        Integer highseqObj = openChannels.get(channelnr);
        if (highseqObj != null) {
            // The highest sequence is kept track of in the openChannels table.
            newHighseq = highseqObj.intValue() + 1;
            openChannels.put(channelnr, new Integer(newHighseq));
            return newHighseq;
        }
        // The highest sequence is kept track of in the node's highseq field.
        newHighseq = channel.getIntValue("highseq") + 1;
        channel.setValue("highseq", newHighseq);
        if (channel.commit()) {
            return newHighseq;
        }
        log.error("getnewSequence(): couldn't return a new sequence number, because couldn't commit channel node.");
        return -1;
    }


    /**
     * Removes all messages posted in the channel.
     * XXX: does not work for chats.
     *
     * @param channel The channel whose messages have to be removed.
     */
    public void removeAllMessages(MMObjectNode channel) {
        /* This function will call the removeNode of the message builder with
         * the recursive parameter set to true for all messages in the channel.
         */
        if (messageBuilder != null) {
            for (Enumeration messages = mmb.getInsRel().getRelated(channel.getNumber(), messageBuilder.getNumber());
                 messages.hasMoreElements();) {
                messageBuilder.removeNode((MMObjectNode)messages.nextElement(), true);
            }
        }
    }

    /**
     * Returns the recorder for this channel.
     * The recorder is currently only used in chats.
     * @param channel the channel to record
     * @return a <code>Writer</code> with which to record data, or <code>null</code> if
     *         the recorder doesn't exist.
     */
    public Writer getRecorder(int channel) {
        return recorders.get(new Integer(channel));
    }

    /**
     * Start the recording session of a channel.
     * @param channel the channel to record
     * @param recorder a Writer to record to.
     * @return the Writer that will record the messages
     */
    public Writer startRecorder(int channel, Writer recorder) {
        Writer writer=getRecorder(channel);
        if (writer!=null) {
            return writer;
        }
        recorders.put(new Integer(channel), recorder);
        Date now= new Date();
        try {
            recorder.write("Start recorder for channel "+channel+" at "+now+"\n");
            recorder.flush();
        } catch (IOException e) {
            log.error(""+e);
        }
        return recorder;
    }

    /**
     * Start the recording session of a channel, using a file to stream the output to.
     * @param channel the channel to record
     * @param filepath the file to record to.
     * @return the Writer that will record the messages
     */
    public Writer startRecorder(int channel, String filepath) {
        try {
            return startRecorder(channel,new FileWriter(filepath,true));
        } catch(IOException e) {
            log.error(""+e);
            return null;
        }
    }

    /**
     * Stop the recording session of a channel.
     * @param channel the channel being recorded
     */
    public void stopRecorder(int channel) {
        Writer recorder=recorders.get(new Integer(channel));
        if (recorder!=null) {
            try {
                recorder.flush();
                recorder.close();
            } catch(IOException e) {
                log.error(""+e);
            }
            recorders.remove(new Integer(channel));
        }
    }

    /**
     * Returns a key for the combination of nodes (a channel and a user).
     * The key need not be unique.
     * @param channel the channel object
     * @param suer the user object
     * @return a key for sue with teh temporary node manager
     */
    private String getTemporaryNodeKey(MMObjectNode channel, MMObjectNode user) {
        return "C"+channel.getNumber()+"U"+user.getNumber();
    }

    /**
     * Connects a user to a channel by making a temporary relation between them.
     * If the user is already connected to another channel of the same
     * community the old connection is closed.
     *
     * @param user the node representing the user that tries to join the channel.
     * @param channel the channel to join
     * @return <code>CHANNEL_FULL</code> if there's no room in the channel,
     * <code>CONNECTED</code> if the connection is made, and <code>FAILED</code>
     * if an error occurred. If the user is already connected to the channel
     * <code>ALREADY_CONNECTED</code> is returned.
     */
    public synchronized int join(MMObjectNode user, MMObjectNode channel) {
        // Test if the user is not already connected to this channel
        if (messageBuilder == null) {
            log.error("No message builder : join failed");
            return FAILED;
        }
        String key=getTemporaryNodeKey(channel,user);

        if (getTmpNode(key)!=null) {
                return ALREADY_CONNECTED;
        }

        // Test if there is room in the channel. When the channel is full return "full" and abort.
        int maxusers = channel.getIntValue("maxusers");
        if (maxusers != -1) {
            int usersCount = messageBuilder.getTemporaryRelated(channel, "users").size();
            if (usersCount >= maxusers)
                return CHANNEL_FULL;
        }

        /* Make the relation between the user and the joined channel.
         * Also give the relation ID to the NodeBreaker so the relation is
         * automatically broken after a specified period of inactivety.
         */
        try {
            String tmp = tmpNodeManager.createTmpRelationNode("related", tOwner,key, "realchannel", "realusers");
            MMObjectNode node = tmpNodeManager.getNode(tOwner, tmp);
            tmpNodeManager.setObjectField(tOwner, tmp, "snumber", channel.getValue("number"));
            tmpNodeManager.setObjectField(tOwner, tmp, "dnumber", user.getValue("number"));
            // XXX: the way owner/key are combined to a key and/or
            // stored in a node could be application-specific.
            // Should be fixed by adding some methods to the
            // TemporaryNodeManagerInterface
            chatboxConnections.add(tOwner+"_"+key, (new Long(System.currentTimeMillis() + expireTime)).longValue());
        }catch(Exception e) {
            log.error("join(): Could not create temporary relations between between channel and user.\n" + e);
            return FAILED;
        }

        // XXX:code below should be optional ?

        /* If there is another channel to which the user is connected
         * in the same community logout of the eldest connection,
         * so the user is only in one channel of a community at the same time.
         */
        MMObjectNode relatedChannel;
        MMObjectNode community = communityParent(channel);
        MMObjectNode relatedCommunity;

        Enumeration<MMObjectNode> relatedChannels = messageBuilder.getTemporaryRelated(user, "channel").elements();
        while (relatedChannels.hasMoreElements()) {
            relatedChannel = relatedChannels.nextElement();
            relatedCommunity = communityParent(relatedChannel);
            if (relatedCommunity.getNumber() == community.getNumber())
                if (relatedChannel.getNumber() != channel.getNumber())
                    logout(user, relatedChannel);
        }
        return CONNECTED;
    }

    /**
     * Disconnects a user from a channel.
     * Removes the temporary relations between the user and the channel.
     * @param user The user to disconnect.
     * @param channel The channel to disconnect from.
     * @return <code>DISCONNECTED</code> if the user was successfully disconnected,
     *    <code>FAILED</code> if an error occurred.
     * @deprecated use {@link #leave} instead
     */
    public synchronized int logout(MMObjectNode user, MMObjectNode channel) {
        return leave(user,channel);
    }

    /**
     * Disconnects a user from a channel.
     * Removes the temporary relations between the user and the channel.
     * @param user The user to disconnect.
     * @param channel The channel to disconnect from.
     * @return <code>DISCONNECTED</code> if the user was successfully disconnected,
     *    <code>FAILED</code> if an error occurred.
     */
    public synchronized int leave(MMObjectNode user, MMObjectNode channel) {
        chatboxConnections.remove(tOwner+"_"+getTemporaryNodeKey(channel,user));
        return DISCONNECTED;
    }

    /**
     * Registers a user as still being active.
     * Tells the associated NodeBreaker to extend the relation between the
     * user and the channel for another period of time.
     * @param user the node representing the user
     * @param channel the channel to which the user is connected
     */
    public synchronized void userStillActive(MMObjectNode user, MMObjectNode channel) {
        chatboxConnections.update(tOwner+"_"+getTemporaryNodeKey(channel,user),
                                  System.currentTimeMillis() + expireTime);
    }

    /**
     * Retrieve a list of users connected to a channel.
     * The params parameter contains the parameters for the list.
     * <ul>
     * <li>CHANNEL should containt the number or alias of the channel node</li>
     * <li>TYPE may conmtain the user's object type. The default is the
     *          value specified in the builder properties</li>
     * <li>FIELDS should contain the names of the fields whose values to return</li>
     * <li>SORTFIELDS may contain the names of the fields to sort on</li>
     * <li>SORTDIRS may contain the sort-order for the fields in SORTFIELDS</li>
     * </ul>
     * @param params contains the parameters for this list command.
     * @return a vector with the (string) values of the requested fields, per user.
     */
    public Vector<String> getListUsers(StringTagger params) {
        Vector<MMObjectNode> relatedUsers = getNodeListUsers(params);
        // Get the fieldnames out of the FIELDS attribute.
        Vector<String> fields = params.Values("FIELDS");
        // Put in params the number of fields that will get returned.
        params.setValue("ITEMS","" + fields.size());

        Vector<String> result=new Vector<String>();
        MMObjectNode relatedUser;
        String field;
        for (Iterator<MMObjectNode> i=relatedUsers.iterator(); i.hasNext();) {
            relatedUser = i.next();
            for (Iterator<String> j=fields.iterator(); j.hasNext(); ) {
                field=j.next();
                result.add(relatedUser.getStringValue(field));
            }
        }
        return result;
    }

    /**
     * Retrieve a list of users connected to a channel.
     * The params parameter contains the parameters for the list.
     * <ul>
     * <li>CHANNEL should containt the number or alias of the channel node</li>
     * <li>TYPE may conmtain the user's object type. The default is the
     *          value specified in the builder properties</li>
     * <li>FIELDS should contain the names of the fields whose values to return</li>
     * <li>SORTFIELDS may contain the names of the fields to sort on</li>
     * <li>SORTDIRS may contain the sort-order for the fields in SORTFIELDS</li>
     * </ul>
     * @param params contains the parameters for this list command.
     * @return a vector with the user nodes.
     */
    public Vector<MMObjectNode> getNodeListUsers(Map params) {
        Vector<MMObjectNode> result = new Vector<MMObjectNode>();
        String id = (String)params.get("CHANNEL");
        MMObjectNode node = getNode(id);
        if ((node == null) || !(node.getBuilder() instanceof Channel)) {
            log.debug("getListUsers(): no or incorrect channel specified");
            return result;
        }
        String usertype = (String)params.get("TYPE");
        if (usertype==null) usertype= defaultUserType;

        int offset=0;
        String tmp = (String)params.get("FROMCOUNT");
        if (tmp!=null) offset=Integer.parseInt(tmp);
        int max=Integer.MAX_VALUE;
        tmp = (String)params.get("MAXCOUNT");
        if (tmp!=null) max=Integer.parseInt(tmp);

        /* Create a Comparator, provided SORTFIELDS is specified.
         * Sortdirections can be specified in SORTDIRS.
         *
         * Note: this is not very nice, but we prefer to use
         * the more generic  Map over StringTagger.
         * However, the get() method of StringTagger always returns a string.
         * We need to fix StringTagger so that get() always returns the
         * _original_ value.
         */
        Vector<String> sortFields;
        Vector<String> sortDirs;
        if (params instanceof StringTagger) {
            sortFields = ((StringTagger)params).Values("SORTFIELDS");
            sortDirs = ((StringTagger)params).Values("SORTDIRS");
        } else {
            sortFields = (Vector<String>)params.get("SORTFIELDS");
            sortDirs = (Vector<String>)params.get("SORTDIRS");
        }

        NodeComparator compareUsers=null;
        if (sortFields!=null) {
            if (sortDirs == null) {
                compareUsers = new NodeComparator(sortFields);
            } else {
                compareUsers = new NodeComparator(sortFields, sortDirs);
            }
        }
        return getListUsers(node, usertype, compareUsers,offset, max);
    }

    // javadoc overriden
    public void setDefaults(MMObjectNode node) {
        node.setValue(F_OPEN, OPEN);
        node.setValue(F_STATE, 0);
    }



    /**
     * Retrieve a sorted list of users connected to a channel.
     * @param channel the channel
     * @param usertype the type of the userobjects to retrieve
     * @param compareUsers a Comparator used for sorting. <code>null</code>
     *          means the result is not no sorted
     * @return a vector with the nodes of the users.
     */
    public Vector<MMObjectNode> getListUsers(MMObjectNode channel, String usertype,
                               Comparator<MMObjectNode> compareUsers, int offset, int max) {
        Vector<MMObjectNode> relatedUsers = messageBuilder.getTemporaryRelated(channel,usertype,offset,max);
        if (compareUsers!=null) {
            Collections.sort(relatedUsers,compareUsers);
        }
        return relatedUsers;
    }

    /**
     * Returns to which community a channel belongs.
     *
     * @param channel The channel node
     * @return The community of the channel, or <code>null</code> if the
     *  channel is not associated with a community.
     */
    public MMObjectNode communityParent(MMObjectNode channel) {
        // During call to Channel.init(), communityBuilder.getNumber() can still be 0
        // So need to check it before using it
        // When openChannels is removed from init this can be removed
        if (communityBuilder != null) {
            int oType = communityBuilder.getNumber();
            if (oType == 0) oType = mmb.getTypeDef().getIntValue("community");
            Enumeration relatedCommunity = mmb.getInsRel().getRelated(channel.getNumber(), oType);
            if (relatedCommunity.hasMoreElements()) {
                return (MMObjectNode)relatedCommunity.nextElement();
            }
        }
        return null;
    }

    /**
     * Provides additional functionality when obtaining field values.
     * This method is called whenever a Node of the builder's type fails at
     * evaluating a getValue() request (generally when a fieldname is supplied
     * that doesn't exist).
     * <br />
     * The following virtual fields are calculated:
     * <br />
     * readlogin - returns true when a user has to log on before he can read messages,
     *      false otherwise.
     * <br />
     * writelogin - returns true when a user has to log on before he can post a message,
     *      false otherwise.
     * <br />
     * hasmoods - returns "yes" when there are moods related to the channel,
     *      "no" otherwise.
     * <br />
     * It is also possible to specify hasmoods as a function.
     * hasmoods() performs the same test, but returns a Boolean object (which
     * evaluates to "true" and "false" when used referenced as a string)
     * instead of "yes"/"no". This method is preferred
     * for use with the MMCI.
     *
     * @param node the node whose fields are queried
     * @param field the fieldname that is requested
     * @return the result of the call, <code>null</code> if no valid functions or virtual fields could be determined.
     */
    public Object getValue(MMObjectNode node, String field) {
        // PRE:  The name of a field in the database or the name of a virtual field.
        // POST: The value of the field.
        if (field.equals(Message.F_REPLY_COUNT)) {
            return new Integer(messageBuilder.getNrMsgAndHighSeq(node).messageCount);
        }

        if (field.equals(F_READLOGIN)) {
            return Boolean.valueOf((node.getIntValue(F_STATE) & STATE_READ_LOGIN)>0);
        }
        if (field.equals(F_WRITELOGIN)) {
            return Boolean.valueOf((node.getIntValue(F_STATE) & STATE_WRITE_LOGIN)>0);
        }
        if (field.equals(F_HASMOODS)) {
            if (node.getRelationCount("mood")>0) return "yes"; else return "no";
        }
        return super.getValue(node, field);
    }

    /**
     * Executes a function on the field of a node, and returns the result.
     * This method is called by the builder's {@link #getValue} method.
     * Functions implemented are readlogin(), writelogin() and hasmoods().
     * @param node the node whose fields are queries
     * @param field the fieldname that is requested
     * @return the result of the 'function', or null if no valid functions could be determined.
     */
    protected Object executeFunction(MMObjectNode node,String function,String field) {
        if (function.equals(F_HASMOODS)) {
            return Boolean.valueOf(node.getRelationCount("mood")>0);
        } else {
            return super.executeFunction(node,function,field);
        }
    }

    /**
     * Handles the $MOD-MMBASE-BUILDER-channel- commands.
     * The commands to use are: <br />
     * <ul>
     * <li>channelnumber-OPEN - opens the channel </li>
     * <li>channelnumber-CLOSE - closes the channel </li>
     * <li>channelnumber-ISOPEN - returns if a channel is "open", "closed" or "readonly"; </li>
     * <li>channelnumber-DELALLMESSAGES - deletes all posted messages in the channel </li>
     * <li>channelnumber-NEWSEQ - returns a new sequence number for in the channel </li>
     * <li>channelnumber-JOIN-usernumber - connects the user to the channel. </li>
     * <li>channelnumber-LEAVE-usernumber - disconnects a user from the channel. </li>
     * <li>channelnumber-STILLACTIVE-usernumber - resets the time-out before the user is
     *      automatically disconnected from the channel. </li>
     * </ul>
     * @param sp  the current PageInfo context
     * @param tok the tokenized command
     * @return the result of the command as a String
     */
    public String replace(PageInfo sp, StringTokenizer tok) {
        // The first thing we expect is a channel number.
        if (!tok.hasMoreElements()) {
            log.error("replace(): channel number expected after $MOD-BUILDER-channel-.");
            return "";
        }

        MMObjectNode channel = getNode(tok.nextToken());

        if (tok.hasMoreElements()) {
            String cmd = tok.nextToken();
            // i.e.:
            // channel-RECORD-FILE-channels.txt
            // channel-RECORD-STOP
            if (cmd.equals("RECORD")) {
                if (tok.hasMoreElements()) {
                    cmd = tok.nextToken();
                    if (cmd.equals("STOP")) {
                        stopRecorder(channel.getNumber());
                    } else if (cmd.equals("FILE")) {
                        if (tok.hasMoreElements()) {
                            cmd=tok.nextToken();
                        } else {
                            cmd=defaultrecordfile;
                        }
                        if (baseRecordPath==null) {
                            log.error("Base recording filepath not specified - cannot record this channel.");
                            return "";
                        }
                        String filename = baseRecordPath+cmd;
                        startRecorder(channel.getNumber(),filename);
                   }
                    return "";
                }
            }

            if (cmd.equals("OPEN")) open(channel);
            if (cmd.equals("READONLY")) readonly(channel);
            if (cmd.equals("CLOSE")) close(channel);
            if (cmd.equals("ISOPEN")) return isOpen(channel);
            if (cmd.equals("DELALLMESSAGES")) removeAllMessages(channel);
            if (cmd.equals("NEWSEQ")) return "" + getNewSequence(channel);
            if (cmd.equals("JOIN"))
                join(getNode(tok.nextToken()), channel);
            if (cmd.equals("LEAVE") || cmd.equals("QUIT"))
                leave(getNode(tok.nextToken()), channel);
            if (cmd.equals("STILLACTIVE")) {
                userStillActive(getNode(tok.nextToken()), channel);
                log.debug("replace(): user still active") ;
            }
        }
        return "";
    }

    /**
     * Ask URL from related community and append the channel number to the URL.
     */
    public String getDefaultUrl(int src) {
        if (communityBuilder != null) {
            Enumeration e = mmb.getInsRel().getRelated(src, communityBuilder.getNumber());
            if (!e.hasMoreElements()) {
                log.warn("GetDefaultURL Could not find related community for channel node " + src);
                return null;
            }
            MMObjectNode commNode = (MMObjectNode)e.nextElement();
            String url = commNode.getBuilder().getDefaultUrl(commNode.getNumber());
            if (url != null) url += "+" + src;
            return url;
        } else {
            return null;
        }
    }

    /**
     * What should a GUI display for this node/field combo.
     * Fields converted are ""session" (returns yes/no) and
     * ""state", which returns a description of the login condition.
     * @param node The node to display
     * @param field the name field of the field to display
     * @return the display of the node's field as a <code>String</code>, null if not specified
     */
    public String getGUIIndicator(String field, MMObjectNode node) {
        // for recording sessions.
        // If true, guess a 'session' object would be linked
        // to a channel. Messages related to teh chanenl should then also be
        // related to the session object.
        // This doesn't work right now (sounds like overkill anyway)

        if (field.equals(F_SESSION)) {
            int value = node.getIntValue(field);
            if (value == 1) return "yes"; else return "no";
        }
        if (field.equals(F_STATE)) { // alias login
            int value = node.getIntValue(field);
            if ((value & STATE_READ_LOGIN) >0)
                return "login before read";
            else if ((value & STATE_WRITE_LOGIN) > 0)
                return "login before post";
            else return "no login";
        }
        if (field.equals(F_OPEN)) {
            return isOpen(node);
        }
        if (field.equals(F_MAXUSERS)) {
            if (node.getIntValue(field)==-1) return "unlimited";
        }
        return null;
    }

}
