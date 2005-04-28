/*

This software is OSI Certified Open Source Software.
OSI Certified is a certification mark of the Open Source Initiative.

The license (Mozilla version 1.0) can be read at the MMBase site.
See http://www.MMBase.org/license

*/

package org.mmbase.applications.community.modules;

import java.util.*;

import org.mmbase.util.*;
import org.mmbase.module.*;
import org.mmbase.module.core.*;
import org.mmbase.module.corebuilders.*;
import org.mmbase.applications.community.builders.*;
import org.mmbase.util.logging.*;

/**
 * This module is the general manager for the community objects (chat and forums).
 * It processes message posts, retrieves info, and generates lists of messages and users.
 * In this manner, it is more an application than a module.
 * The community 'application' consists of the Community, Channel, and Message builder.
 * <br />
 * The {@link org.mmbase.module.builders.Community} builder is a pool of channels
 * of a similar type (chatbox, forum, or guestbook).
 * <br />
 * The {@link org.mmbase.module.builders.Channel} builder defines a channel - a
 * 'location' which manages a forum discussion or chat.
 * <br />
 * The {@link org.mmbase.module.builders.Message} builder defines a single message - a
 * persistent message in case of a forum or guestbook, a temporary message in the
 * case of a chat.
 * <br />
 * Optional builders that provide additional information are a Moods builder
 * (associated with a channel, it may provide backgrounds, colors, or other
 * dressing), a Chatter builder (which hold the users of a chat or forum), or a
 * Maps builder (used for generating links).
 *
 * @author Dirk-Jan Hoekstra
 * @author Pierre van Rooden
 * @version $Id: CommunityPrc.java,v 1.15 2004-03-16 12:24:45 michiel Exp $
 */

public class CommunityPrc extends ProcessorModule {

    private static final Logger log = Logging.getLoggerInstance(CommunityPrc.class);

    private Message messageBuilder;
    private Channel channelBuilder;
    private Community communityBuilder;

    private VirtualBuilder treeBuilder;

    private MMBase mmb;

    private boolean active = false;

    /**
     * Create a Community module instance.
     */
    public CommunityPrc() {
    }

    /**
     * Initialize the community.
     * Makes sure MMBase starts first (as it needs a reference to its builders).
     */
    public void init() {
        // load MMBase and make sure it is started first
        mmb = MMBase.getMMBase();
        activate();
    }

    /**
     * Initialize the community and activate it if all builders are present.
     * @since MMBase-1.7
     */
    protected boolean activate() {
        if (!active) {

            messageBuilder = (Message) mmb.getBuilder("message");
            if (messageBuilder == null  || !messageBuilder.activate()) {
                log.info("Community module could not (yet) be activated because message builder missing or could not be activated" + (messageBuilder == null ? "." : " (" + messageBuilder + ")."));
                return false;
            }
            communityBuilder = (Community)mmb.getBuilder("community");
            if (communityBuilder == null || !communityBuilder.activate()) {
                log.info("Community builder missing or could not be activated. Communityprc can work without it though.");
            }
            channelBuilder = (Channel) mmb.getBuilder("channel");
            if (channelBuilder == null || ! channelBuilder.activate()) {
                log.info("Channel builder missing or could not be activated. Communityprc can work without it though.");
            }
            initializeTreeBuilder();
            active = true;
            log.service("Community module was activated sucessfully");
        }
        return active;
    }

    /**
     * Creates a virtual builder for adding functionality to the nodes
     * of the LIST TREE command.
     * The returned buuilder is a virtual wrapper around the messagebuilder.
     * It includes a number of additional, virtual, fields.
     * These fields are currently not configurable.
     */
    private void initializeTreeBuilder() {
        treeBuilder = new VirtualReferrerBuilder(messageBuilder);
        treeBuilder.addField(new FieldDefs("list head","string", -1,-1,"listhead",FieldDefs.TYPE_STRING));
        treeBuilder.addField(new FieldDefs("list tail","string", -1,-1,"listtail",FieldDefs.TYPE_STRING));
        treeBuilder.addField(new FieldDefs("depth","integer", -1,-1,"depth",FieldDefs.TYPE_INTEGER));
        treeBuilder.addField(new FieldDefs("nr of replies","integer", -1,-1,"replycount",FieldDefs.TYPE_INTEGER));
    };

    /**
     * Handle a $MOD command.
     * The actual commands are directed to the Message, Channel, and
     * Community builders.
     * @param sp The scanpage (containing http and user info) that calls the function
     * @param cmds the command to execute
     * @return the result value as a <code>String</code>
     */
    public String replace(scanpage sp, String cmds) {
        if (activate()) {
            StringTokenizer tok = new StringTokenizer(cmds,"-\n\r");
            if (tok.hasMoreTokens()) {
                String cmd = tok.nextToken();
                if (cmd.equals("MESSAGE")) {
                    return messageBuilder.replace(sp, tok);
                }
                if (cmd.equals("CHANNEL")) {
                    return channelBuilder.replace(sp, tok);
                }
                if (cmd.equals("COMMUNITY")) {
                    return communityBuilder.replace(sp, tok);
                }
            }
        }
        return "";
    }

    /**
     * Execute the commands provided in the form values.
     * This method returns values which allow the page to
     * check whether the operation was succesful (MESSAGE-NUMBER or MESSAGE-ERROR).
     * If the page has a session object, the data is stored in the session
     * object. This means that SCAN can retrieve the data using the $SESSION
     * command. If session is not present, the data is stored (added) in the
     * <code>vars<vars> parameter. This allows a system that uses the MMCI
     * (such as jsp) to retrieve the value.
     * <br />
     * XXX: This is a bit of a sloppy way to pass results, and the actual
     * mechanics may get changed (formalized) in the future.
     *
     * @param sp The scanpage (containing http and user info) that calls the function
     * @param cmds the commands to process
     * @param vars variables that were set to be used during processing.
     * @return the result value as a <code>String</code>
     */
    public boolean process(scanpage sp, Hashtable cmds, Hashtable vars) {
        boolean result = false;
        if (activate()) {
            String token;
            for (Enumeration h = cmds.keys(); h.hasMoreElements();) {
                String key = (String)h.nextElement();
                StringTokenizer tok = new StringTokenizer(key , "-\n\r");

                token = tok.nextToken();
                if (token.equals("MESSAGE")) {
                    if (tok.hasMoreElements()) {
                        token = tok.nextToken();
                        /* $MOD-MESSAGE-POST- */
                        if (token.equals("POST")) {
                            doPostProcess(sp, cmds, vars);
                        } else if (token.equals("UPDATE")) {
                            doUpdateProcess(sp, cmds,vars);
                        }
                    }
                }
                result = true;
            }
        }
        return result;
    }

    /**
     * Stores an output parameter as a value.
     * The limitations of SCAN require the value to be set in a {@link sessionInfo}
     * variable maintained by the scan servlet.
     * However, the MMCI does not 'know' sessionInfo, so in those instances
     * variables are stored in the vars parameter (which is passed back to the
     * MMCI).
     * If neither object is supported, no data is returned.
     */
    private void setReturnValue(scanpage sp, Hashtable vars, String name, String value) {
        if (sp.session!=null) {
            // for SCAN, the data need be stored in a SESSION var
            if (value==null) {
                sp.session.removeValue(name);
            } else {
                sp.session.setValue(name,value);
            }
        } else if (vars!=null) {
            // otherwise return it in the vars hashtable
            if (value==null) {
                vars.remove(name);
            } else {
                vars.put(name,value);
            }
        }
    }

    /**
     * Translates the commands for posting a message provided in the form values
     * to a post() call in the message builder.
     * @param cmds the commands to process
     * @param vars variables that were set to be used during processing.
     * @return <code>true</code> if the post was successful
     */
    private boolean doPostProcess(scanpage sp, Hashtable cmds, Hashtable vars) {
        // Get the MessageThread, Subject and Body from the formvalues.
        String tmp = (String)cmds.get("MESSAGE-POST");
        setReturnValue(sp,vars,"MESSAGE-ERROR",null);
        try {
            int messagethreadnr = Integer.parseInt(tmp);
            String subject = (String)vars.get("MESSAGE-SUBJECT");
            String body = (String)vars.get("MESSAGE-BODY");
            tmp = (String)vars.get("MESSAGE-CHANNEL");
            int channel = Integer.parseInt(tmp);

            // Get user and chatterName
            tmp = (String)vars.get("MESSAGE-CHATTER");
            int user;
            if (tmp != null) user = Integer.parseInt(tmp); else user = -1;
            String chatterName = (String)vars.get("MESSAGE-CHATTERNAME");

            // Let the messagebuilder post the message.
            int result=Message.POST_ERROR_UNKNOWN;
            if (subject != null) {
                result=messageBuilder.post(subject, body, channel, messagethreadnr, user, chatterName);
                setReturnValue(sp,vars,"MESSAGE-NUMBER",""+result);
            } else {
                result=messageBuilder.post(body, messagethreadnr, user, chatterName);
                setReturnValue(sp,vars,"MESSAGE-NUMBER","-1");
            }
            if (result<Message.POST_OK) {
                String err=messageBuilder.getMessageError(result);
                log.error(result+":"+err);
                setReturnValue(sp,vars,"MESSAGE-ERROR",err);
            }
            return result>=Message.POST_OK;
        } catch (NumberFormatException e) {  // catches erros when parsing integers
            setReturnValue(sp,vars,"MESSAGE-ERROR",
                           "Invalid parameter value ( '"+tmp+"' is not a number)");
            return false;
        }
    }

    /**
     * Translates the commands for updating a message provided in the form values
     * to a update() call in the message builder.
     * @param cmds the commands to process
     * @param vars variables that were set to be used during processing.
     * @return <code>true</code> if the update was sucecsful
     */
    private boolean doUpdateProcess(scanpage sp, Hashtable cmds, Hashtable vars) {
        String tmp = (String)cmds.get("MESSAGE-UPDATE");
        try {
            // Get the Subject, Body, number from the formvalues.
            int number = Integer.parseInt(tmp);
            String subject = (String)vars.get("MESSAGE-SUBJECT");
            String body = (String)vars.get("MESSAGE-BODY");
            // Get user and chatterName
            tmp = (String)vars.get("MESSAGE-CHATTER");
            int user;
            if (tmp != null) user = Integer.parseInt(tmp); else user = -1;
            String chatterName = (String)vars.get("MESSAGE-CHATTERNAME");
            log.info("MESSAGE-CHATTERNAME="+chatterName);
            int result=messageBuilder.update(chatterName, user, subject, body, number);
            if (result<Message.POST_OK) {
                String err=messageBuilder.getMessageError(result);
                log.error(result+":"+err);
                setReturnValue(sp,vars,"MESSAGE-ERROR",err);
            } else {
                setReturnValue(sp,vars,"MESSAGE-NUMBER",""+number);
            }
            return result==Message.POST_OK;
        } catch (NumberFormatException e) {  // catches erros when parsing integers
            setReturnValue(sp,vars,"MESSAGE-ERROR",
                           "Invalid parameter value ( '"+tmp+"' is not a number)");
            return false;
        }
    }

    /**
     * Returns a virtual builder used to create node lists from the results
     * returned by getList().
     * The default method does not associate the builder with a cloud (mmbase module),
     * so processormodules that need this association need to override this method.
     * Note that different lists may return different builders.
     * @param command the LIST command for which to retrieve the builder
     * @param params contains the attributes for the list
     */
    public MMObjectBuilder getListBuilder(String command, Map params) {
        activate();
        if (command.equals("TREE")) return treeBuilder;
        if (command.equals("WHO") || command.equals("TEMPORARYRELATIONS")) {
            String type=(String)params.get("TYPE");
            if (type!=null) {
                return mmb.getMMObject(type);
            }
        }
        return new VirtualBuilder(mmb);
    }

    /**
     * Generates a list of values from a command to the processor.
     * Recognized commands are TREE, WHO, and TEMPORARYRELATIONS.
     * @param context the context of the page or calling application (currently, this should be a scanpage object)
     * @param command the list command to execute.
     * @param params contains the attributes for the list
     * @return a <code>Vector</code> that contains the list values as MMObjectNodes
     */
    public Vector getNodeList(Object context, String command, Map params) throws ParseException {
        activate();
        if (command.equals("WHO")) return channelBuilder.getNodeListUsers(params);
        if (command.equals("TEMPORARYRELATIONS")) return getNodeListTemporaryRelations(params);
        return super.getNodeList(context,command, params);
    }

    /**
     * Generates a list of values from a command to the processor.
     * Recognized commands are TREE, WHO, and TEMPORARYRELATIONS.
     * If the command is not one of these, it returns null;
     * @param sp the page context
     * @param params contains the attributes for the list
     * @param command the list command to execute.
     * @return a <code>Vector</code> that contains the list values
     */
    public Vector getList(scanpage sp, StringTagger params, String command) throws ParseException {
        if (activate()) {
            if (command.equals("TREE")) return messageBuilder.getListMessages(params);
            if (command.equals("WHO")) return channelBuilder.getListUsers(params);
            if (command.equals("TEMPORARYRELATIONS")) return getListTemporaryRelations(params);
            throw new ParseException("Unknown command '" + command + "'");
        } else {
            throw new RuntimeException("CommunityPrc module could not be activated");
            // return null; // returning null gives NPE in ProcessorModule, how nice it that?
        }
    }

    /**
     * This function returns a vector, like LIST RELATED does, with the values
     * of the specified fields of the related nodes to a specified node.
     * Only the field values of nodes that are related via a temporary
     * <code>insrel</code> object are returned.
     * Both real and temporary nodes are returned.
     * <ul>
     * <li>&lt;LIST TEMPORARYRELATED NODE=&quot;...&quot; TYPE=&quot;...&quot; FIELDS=&quot;...&quot;&gt;</li>
     * <li>NODE - The node to get the related nodes from.</li>
     * <li>TYPE - The wanted type of nodes to return.</li>
     * <li>FIELDS - The values of the fields to return.</li>
     * </ul>
     */
    public Vector getListTemporaryRelations(StringTagger params) {
        Enumeration relatedNodes = getNodeListTemporaryRelations(params).elements();
        MMObjectNode relatedNode;
        Object value;
        Vector result=new Vector();
        Vector fields = params.Values("FIELDS");
        while (relatedNodes.hasMoreElements()) {
            relatedNode = (MMObjectNode)relatedNodes.nextElement();
            for (int i = 0; i < fields.size(); i++) {
                value = relatedNode.getValue((String)fields.elementAt(i));
                if (value != null) result.add("" + value); else result.add("");
            }
        }
        return result;
    }

    /**
     * This function returns a vector, like LIST RELATED does, with the values
     * of the specified fields of the related nodes to a specified node.
     * Only the field values of nodes that are related via a temporary
     * <code>insrel</code> object are returned.
     * Both real and temporary nodes are returned.
     * <ul>
     * <li>&lt;LIST TEMPORARYRELATED NODE=&quot;...&quot; TYPE=&quot;...&quot; FIELDS=&quot;...&quot;&gt;</li>
     * <li>NODE - The node to get the related nodes from.</li>
     * <li>TYPE - The wanted type of nodes to return.</li>
     * <li>FIELDS - The values of the fields to return.</li>
     * </ul>
     */
    public Vector getNodeListTemporaryRelations(Map params) {
        activate();
        String number = (String)params.get("NODE");
        MMObjectNode node;
        if (number == null) {
            log.warn("getListTemporaryRelations(): Can't find node: " + number);
            return new Vector();
        }
        int offset=0;
        String tmp = (String)params.get("FROMCOUNT");
        if (tmp!=null) offset=Integer.parseInt(tmp);
        int max=Integer.MAX_VALUE;
        tmp = (String)params.get("MAXCOUNT");
        if (tmp!=null) max=Integer.parseInt(tmp);

        // reaaaaaallly ugly!
        if (number.indexOf("_") < 0)
            node = messageBuilder.getNode(number);
        else
            node = (MMObjectNode)messageBuilder.TemporaryNodes.get(number);
        Vector relatedNodes = messageBuilder.getTemporaryRelated(node, (String)params.get("TYPE"),offset,max);
        return relatedNodes;
    }
}
