package org.mmbase.module.community;

import java.lang.*;
import java.net.*;
import java.util.*;
import java.io.*;
import java.lang.reflect.Field;

import org.mmbase.util.StringTagger;
import org.mmbase.util.scanpage;
import org.mmbase.module.ProcessorModule;
import org.mmbase.module.ParseException;
import org.mmbase.module.core.*;
import org.mmbase.module.core.TemporaryNodeManager;
import org.mmbase.module.ParseException;
import org.mmbase.module.corebuilders.*;
import nl.vpro.mmbase.module.builders.Message;
import nl.vpro.mmbase.module.builders.Channel;
import nl.vpro.mmbase.module.builders.Community;

/**
 * @author Dirk-Jan Hoekstra
 * @version 31 Jan 2001
 */

public class CommunityPrc extends ProcessorModule {

	public final String classname = getClass().getName();
	private Message messageBuilder;
	private Channel channelBuilder;
	private Community communityBuilder;
	private MMBase mmb;
	private final boolean debug = true;

	private void debug(String msg)
	{	System.out.println (classname + "-> " + msg);
	}

	private String error(String msg)
	{	return classname + " error-> " + msg;
	}

	public CommunityPrc()
	{
	}

	public void init()
	{ mmb = (MMBase)getModule("MMBASEROOT");
	}

	/**
	* Handle a $MOD command.
	*/
	public String replace(scanpage sp, String cmds)
	{
		StringTokenizer tok = new StringTokenizer(cmds,"-\n\r");
		if (tok.hasMoreTokens())
		{	String cmd = tok.nextToken();
			if (cmd.equals("MESSAGE"))
			{	if (messageBuilder == null) messageBuilder = (Message)mmb.getMMObject("message");
				return messageBuilder.replace(sp, tok); 
			}
			if (cmd.equals("CHANNEL"))
			{	if (channelBuilder == null) channelBuilder = (Channel)mmb.getMMObject("channel");
				return channelBuilder.replace(sp, tok);
			}
			if (cmd.equals("COMMUNITY"))
			{	if (communityBuilder == null) communityBuilder = (Community)mmb.getMMObject("community");
				return communityBuilder.replace(sp, tok);
			}
		}
		return "";
	}
	
	/**
	 * Execute the commands provided in the form values.
	 */
	public boolean process(scanpage sp, Hashtable cmds, Hashtable vars)
	{
		boolean result = false;

		String token;
		for (Enumeration h = cmds.keys(); h.hasMoreElements();)
		{	String key = (String)h.nextElement();
			StringTokenizer tok = new StringTokenizer(key , "-\n\r");
			
			token = tok.nextToken();
			if (token.equals("MESSAGE"))
				if (tok.hasMoreElements())
				{	token = tok.nextToken();
					/* $MOD-MESSAGE-POST- */
					if (token.equals("POST")) doPostProcess(sp, cmds, vars);				  	
					if (token.equals("UPDATE"))
					{	/* Get the Subject, Body, number from the formvalues.
						 */
						int number = (new Integer((String)cmds.get("MESSAGE-UPDATE"))).intValue();
					 	String subject = (String)vars.get("MESSAGE-SUBJECT");
					  	String body = (String)vars.get("MESSAGE-BODY");
						String chatterName = (String)vars.get("MESSAGE-CHATTERNAME");
						/* Let the messagebuilder update the message.
						 */
					  	if (messageBuilder == null) messageBuilder = (Message)mmb.getMMObject("message");
					  	messageBuilder.update(chatterName, subject, body, number);
					}
				}			   
			result = true;
		}
		return result;
	}

	/**
	 * Translates the the commands for posting a message provided in the form values to a post() call in the message builder.
	 */
	private void doPostProcess(scanpage sp, Hashtable cmds, Hashtable vars)
	{	
		/* Get the MessageThread, Subject and Body from the formvalues.
		 */
		int messagethreadnr = (new Integer((String)cmds.get("MESSAGE-POST"))).intValue();
		String subject = (String)vars.get("MESSAGE-SUBJECT");
		String body = (String)vars.get("MESSAGE-BODY");
		String tmp = (String)vars.get("MESSAGE-CHANNEL");
		int channel;
		if (tmp != null) channel = (new Integer(tmp)).intValue(); else channel = -1;

		/* Get user and chatterName
		 */
		tmp = (String)vars.get("MESSAGE-CHATTER");
		int user;
		if (tmp != null) user = (new Integer(tmp)).intValue(); else user = -1;						
		String chatterName = (String)vars.get("MESSAGE-CHATTERNAME");
						
		/* Let the messagebuilder post the message.
		 */
		if (messageBuilder == null) messageBuilder = (Message)mmb.getMMObject("message");
		if (subject != null)
			messageBuilder.post(subject, body, channel, messagethreadnr, user, chatterName);
		else
			messageBuilder.post(body, messagethreadnr, user);
	}

	/**
	 * Generate a list of values from a command to the processor.
	 */
	public Vector getList(scanpage sp, StringTagger tagger, String value) throws ParseException
	{ /* PRE:  Value is the first keyword after "<LIST " in the SHTML page.
	   *       Tagger contains the attributes, like " NODE="23334" FIELDS="subject,body,chatter,timestamp,depth">".
	   * POST: A vector that contains the values for the $ITEM1, $ITEM2, etc.
	   *       When value is not regonized the getList of ProcessorModule is called.
	   */
		if (messageBuilder == null) messageBuilder = (Message)mmb.getMMObject("message");
		if (value.equals("TREE")) return messageBuilder.getListMessages(tagger, value);
		if (value.equals("TEMPORARYRELATIONS")) return getListTemporaryRelations(sp, tagger, value);
		return mmb.getList(sp, tagger, value);		
	}

	/**
	 * This function returns a vector, like LIST RELATED does, with the values of the specified fields of the related nodes to a specified node.
	 * Only the field values of nodes that are related via a temporary insrel are returned. Both real and temporary nodes are returned.
	 * 
	 * &lt;LIST TEMPORARYRELATED NODE=&quot;...&quot; TYPE=&quot;...&quot; FIELDS=&quot;...&quot;&gt;
	 * NODE - The node to get the related nodes from.
	 * TYPE - The wanted type of nodes to return.
	 * FIELDS - The values of the fields to return.
	 */
	public Vector getListTemporaryRelations(scanpage sp, StringTagger tagger, String value2)
	{	Vector result = new Vector();
		String number = tagger.Value("NODE");
		MMObjectNode node;
		if (number.indexOf("_") < 0)
			node = messageBuilder.getNode(number);
		else
			node = (MMObjectNode)mmb.getMMObject("message").TemporaryNodes.get(number);		
		if (number == null)
		{	debug("getListTemporaryRelations(): Can't find node: " + number);
			return result;
		}
		Enumeration relatedNodes = (((Message)mmb.getMMObject("message")).getTemporaryRelated(node, tagger.Value("TYPE"))).elements();
		MMObjectNode relatedNode;
		Object value;
		Vector fields = tagger.Values("FIELDS");
		while (relatedNodes.hasMoreElements())
		{	relatedNode = (MMObjectNode)relatedNodes.nextElement();
			for (int i = 0; i < fields.size(); i++)
			{	value = relatedNode.getValue((String)fields.elementAt(i));
				if (value != null) result.add("" + value); else result.add("");
			}
		}
		return result;
	}
}