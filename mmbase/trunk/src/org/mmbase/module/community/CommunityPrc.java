/*

This software is OSI Certified Open Source Software.
OSI Certified is a certification mark of the Open Source Initiative.

The license (Mozilla version 1.0) can be read at the MMBase site.
See http://www.MMBase.org/license

*/

package org.mmbase.module.community;

import java.lang.*;
import java.net.*;
import java.util.*;
import java.io.*;

import org.mmbase.util.*;
import org.mmbase.module.*;
import org.mmbase.module.core.*;
import org.mmbase.module.corebuilders.*;
import org.mmbase.module.builders.Message;
import org.mmbase.module.builders.Channel;
import org.mmbase.module.builders.Community;

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
 * @version 31 Jan 2001
 */

public class CommunityPrc extends ProcessorModule {

    // logger
    private static Logger log = Logging.getLoggerInstance(CommunityPrc.class.getName());

    private Message messageBuilder;
    private Channel channelBuilder;
    private Community communityBuilder;
    private MMBase mmb;

    /**
     * Create a Community module instance.
     */
    public CommunityPrc() {
    }

    /**
     * Initailize the communit.
     * Makes sure MMBase starts first (as it needs a refernce to its builders).
     */
    public void init() {
        log.info("Start MMBase before community");
        // load MMBase and make sure it is started first
        mmb = (MMBase)getModule("MMBASEROOT", true);
        messageBuilder = (Message)mmb.getMMObject("message");
        channelBuilder = (Channel)mmb.getMMObject("channel");
        communityBuilder = (Community)mmb.getMMObject("community");
    }

    /**
     * Handle a $MOD command.
     * The actual commands are directed to the Message, Channel, and
     * Community builders.
     * @param sp The scanpage (containing http and user info) that calls the function
     * @param cmds the command to execute
     * @return the result value as a <code>String</code>
     */
    public String replace(scanpage sp, String cmds) {
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
        return "";
    }

    /**
     * Execute the commands provided in the form values.
     * @param sp The scanpage (containing http and user info) that calls the function
     * @param cmds the commands to process
     * @param vars variables that were set to be used during processing.
     * @return the result value as a <code>String</code>
     */
    public boolean process(scanpage sp, Hashtable cmds, Hashtable vars) {
        boolean result = false;

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
        return result;
    }

    /**
     * Translates the commands for posting a message provided in the form values
     * to a post() call in the message builder.
     * @param cmds the commands to process
     * @param vars variables that were set to be used during processing.
     * @return <code>true</code> if the post was sucecsful
     */
    private boolean doPostProcess(scanpage sp, Hashtable cmds, Hashtable vars) {
        /* Get the MessageThread, Subject and Body from the formvalues.
         */
        try {
            int messagethreadnr = Integer.parseInt((String)cmds.get("MESSAGE-POST"));
            String subject = (String)vars.get("MESSAGE-SUBJECT");
            String body = (String)vars.get("MESSAGE-BODY");
            String tmp = (String)vars.get("MESSAGE-CHANNEL");
            int channel = Integer.parseInt(tmp);

            // Get user and chatterName
            tmp = (String)vars.get("MESSAGE-CHATTER");
            int user;
            if (tmp != null) user = Integer.parseInt(tmp); else user = -1;
            String chatterName = (String)vars.get("MESSAGE-CHATTERNAME");

            // Let the messagebuilder post the message.
            if (subject != null)
                return messageBuilder.post(subject, body, channel, messagethreadnr, user, chatterName)>0;
            else
                return messageBuilder.post(body, messagethreadnr, user, chatterName);
        } catch (NumberFormatException e) {  // catches erros when parsing integers
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
        try {
            // Get the Subject, Body, number from the formvalues.
            int number = Integer.parseInt((String)cmds.get("MESSAGE-UPDATE"));
            String subject = (String)vars.get("MESSAGE-SUBJECT");
            String body = (String)vars.get("MESSAGE-BODY");
            // Get user and chatterName
            String tmp = (String)vars.get("MESSAGE-CHATTER");
            int user;
            if (tmp != null) user = Integer.parseInt(tmp); else user = -1;
            String chatterName = (String)vars.get("MESSAGE-CHATTERNAME");
            return messageBuilder.update(chatterName, user, subject, body, number);
        } catch (NumberFormatException e) {  // catches erros when parsing integers
            return false;
        }
    }

    /**
     * Generates a list of values from a command to the processor.
     * Recognized commands are TREE, WHO, and TEMPORARYRELATIONS.
     * If the command is not one of these, it returns null;
     * @pram sp the page context
     * @param tagger contains the attributes for teh list
     * @param value the list command to execute.
     * @return a <code>Vector</code> that contains the list values
     */
    public Vector getList(scanpage sp, StringTagger tagger, String value) throws ParseException {
        if (value.equals("TREE")) return messageBuilder.getListMessages(tagger);
        if (value.equals("WHO")) return channelBuilder.getListUsers(tagger);
        if (value.equals("TEMPORARYRELATIONS")) return getListTemporaryRelations(sp, tagger);
        return null;
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
    public Vector getListTemporaryRelations(scanpage sp, StringTagger tagger) {
        Vector result = new Vector();
        String number = tagger.Value("NODE");
        MMObjectNode node;
        if (number == null) {
            log.warn("getListTemporaryRelations(): Can't find node: " + number);
            return result;
        }
        if (number.indexOf("_") < 0)
            node = messageBuilder.getNode(number);
        else
            node = (MMObjectNode)messageBuilder.TemporaryNodes.get(number);
        Enumeration relatedNodes = messageBuilder.getTemporaryRelated(node, tagger.Value("TYPE")).elements();
        MMObjectNode relatedNode;
        Object value;
        Vector fields = tagger.Values("FIELDS");
        while (relatedNodes.hasMoreElements()) {
            relatedNode = (MMObjectNode)relatedNodes.nextElement();
            for (int i = 0; i < fields.size(); i++) {
                value = relatedNode.getValue((String)fields.elementAt(i));
                if (value != null) result.add("" + value); else result.add("");
            }
        }
        return result;
    }
}
