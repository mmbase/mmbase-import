package org.mmbase.module.builders;

import java.lang.*;
import java.net.*;
import java.util.*;
import java.io.*;
import java.lang.reflect.Field;
import java.awt.Point;

import org.mmbase.util.StringTagger;
import org.mmbase.util.scanpage;
import org.mmbase.module.ProcessorModule;
import org.mmbase.module.ParseException;
import org.mmbase.module.core.*;
import org.mmbase.module.corebuilders.RelDef;
import org.mmbase.module.corebuilders.InsRel;
import org.mmbase.module.gui.html.*;
import org.mmbase.module.ParseException;
import org.mmbase.module.core.TemporaryNodeManager;
import org.mmbase.module.builders.Message;
import org.mmbase.module.community.*;
import org.mmbase.module.corebuilders.TypeDef;
import org.mmbase.util.logging.*;

/**
 * @author Dirk-Jan Hoekstra
 * @version 31 Jan 2001
 */

public class Channel extends MMObjectBuilder {
    private static Logger log = Logging.getLoggerInstance(Channel.class.getName());
	private Message messageBuilder;
	private int otypeMsg = -1;
	private int otypeChn;
	private int otypeCommunity = -1;
	private int relationNumberParentChild;
	private int relationRelated;
	private int expireTime = 1 * 60 * 1000;
	private RelationBreaker chatboxConnections = null; 
	private String tOwner = "system";
	public TemporaryNodeManager tmpNodeManager = null;

	/* state aliases.
	 * Becareful with changing their values, because they are used in the database.
	 */
	public static final int channelClosed = -1;
	public static final int channelWantOpen = 0;
	public static final int channelReadOnly = 1;
	public static final int channelOpen = 2;

	/* This Hashtable contains all open channels with their highest sequence number of which the highseq is keeped track of in memory.
	 */
	private Hashtable openChannels = new Hashtable();
	private Hashtable recordingSessions = new Hashtable();

	/**
	 * Initializes the builder.
	 * Opens all channels whose open field is set to open or wantopen.
	 */
	public boolean init() {
		boolean result = super.init();
		otypeChn = mmb.TypeDef.getIntValue("channel");
		otypeCommunity = mmb.TypeDef.getIntValue("community");
		RelDef reldef = ((RelDef)mmb.getMMObject("reldef"));
		relationNumberParentChild = reldef.getRelNrByName("parent", "child");
		relationRelated = reldef.getRelNrByName("related", "related");
		tmpNodeManager = new TemporaryNodeManager(mmb);
		chatboxConnections = new RelationBreaker(mmb, 2 * expireTime, tmpNodeManager);

		/* Lookup all channels in the cloud of who the field open is set to (want)open and try to open all these channels.
		 */
		log.debug("init(): try to open all channels who are set open in the database");
		Enumeration openChannels = search("WHERE otype='" + mmb.TypeDef.getIntValue("channel") + "' AND (open='" + channelOpen + "' OR open='" + channelWantOpen + "')");
		while (openChannels.hasMoreElements()) open((MMObjectNode)openChannels.nextElement());
		return result;		
	}

	/**
	 * Opens the channel.
 	 *
	 * @param channel The channel to open.
	 * @result true if opening the channel was succesfull.
	 */
	public boolean open(MMObjectNode channel) {
		/* Try to open the channel, when the channel is part of a chatbox put the channel with his highest sequence in the openChannels HashTable.
		 */
		channel.setValue("open", channelOpen);
		MMObjectNode community = communityParent(channel);
		int highestSequence = channel.getIntValue("highseq");
		if (community.getStringValue("kind").equals("chatbox")) channel.setValue("highseq", -1); // this way we can check if the channel get closed properly
		if (channel.commit()) {
			if (community.getStringValue("kind").equals("chatbox"))
				if (openChannels.get(channel.getValue("number")) == null) {
					openChannels.put(channel.getValue("number"), new Integer(highestSequence));
 				}
			log.debug("open(): channel " + channel.getValue("number") + " (" + channel.getValue("name") + ") opened");			
			return true;
		}
		log.error("open(): Can't open channel " + channel.getValue("number"));
		return false;
	}

	/**
 	 * Closes the channel.
	 *
	 * @param channel The channel to close.
	 */
	public boolean close(MMObjectNode channel) {
		if (channel.getIntValue("open") != channelClosed) {
			channel.setValue("open", channelClosed);
			Integer highseq = (Integer)openChannels.get(channel.getValue("number"));
			if (highseq != null) channel.setValue("highseq", highseq);
			if (channel.commit()) {
				log.debug("close(): channel " + channel.getIntValue("number") + "(" + channel.getValue("name") + ") closed.");
				openChannels.remove(channel.getValue("number"));
				return true;
			}
			log.error("close(): Can't close channel " + channel.getValue("number"));		
			return false;
		}
		return true;
	}

	/**
	 * Returns if a channel is open, closed or readonly.
	 *
	 * @param The channel of which the open state is asked.
	 */
	public String isOpen(MMObjectNode channel)
	{	switch (channel.getIntValue("open"))
		{	case channelOpen: return "open";
			case channelReadOnly: return "readonly";
			case channelClosed: return "closed";
			default:
				log.error("isOpen: the folowing channel hasn't a valid open value: " + channel + " Returning the channel is closed.");
 				return "closed";
		}
	}

	/**
	 * @param The channel that has to give out a new sequence number.
	 * @return A new sequence number for a message that's want to get postend in this channel.
	 */
	public int getNewSequence(MMObjectNode channel)
	{ 	int newHighseq;
		Integer highseqObj = (Integer)openChannels.get(channel.getValue("number"));		

		if (highseqObj != null)
		{	/* The highest sequence is kept track of in the openChannels table.
			 */
			newHighseq = ((Integer)highseqObj).intValue() + 1;			
			openChannels.put(channel.getValue("number"), new Integer(newHighseq));
			return newHighseq;
		}
		/* The highest sequence is kept track of in the node's highseq field.
		 */
		newHighseq = channel.getIntValue("highseq") + 1;
		channel.setValue("highseq", newHighseq);
		if (channel.commit()) return newHighseq;
		log.error("getnewSequence(): couldn't return a new sequence number, because couldn't commit channel node.");
		return -1;		
	}


	/**
	 * Removes all messages posted in the channel.
	 *
	 * @param The channel whose messages have to be removed.
	 */
	public void removeAllMessages(MMObjectNode channel)
	{ /* This function will call the removeNode of the message builder with the recursive parameter set to true
	   * for all messages in the channel.
	   */
		if (messageBuilder == null) messageBuilder = (Message)mmb.getMMObject("message");
		if (otypeMsg < 0) otypeMsg = mmb.TypeDef.getIntValue("message");
		for (Enumeration messages = mmb.getInsRel().getRelated(channel.getIntValue("number"), otypeMsg); messages.hasMoreElements();)
			messageBuilder.removeNode((MMObjectNode)messages.nextElement(), true);
	}

	/**
	 * The method isn't implemented yet.
	 * This will start the recording session of the given session. All messages posted in the channel
	 * to who the session is related get also related to the recording session channel.
	 */
	public void startRecordSession(MMObjectNode session) {
		log.error("startRecordSession(): startRecordSession is not implemented yet!");
	}

	/**
	 * Connects a user to channel by making a temporary relation between them.
	 * If the user is already connected to another channel of the same community the old connection is closed.
	 *
	 * @return When there's no room in the channel "full".
	 * When the connection is made "connected" else "failed".
	 * When the user is already connected to the channel "already connected".
	 */
	public synchronized String join(MMObjectNode user, MMObjectNode channel)
	{ 
		/* Test if the user is not already connected to this channel
		 */
		if (messageBuilder == null) messageBuilder = (Message)mmb.getMMObject("message");
		Enumeration relatedChannels = messageBuilder.getTemporaryRelated(user, "channel").elements();
		while (relatedChannels.hasMoreElements())
			if (((MMObjectNode)relatedChannels.nextElement()).getIntValue("number") == channel.getIntValue("number")) return "already connected";		

		/* Test if there is room in the channel. When the channel is full return "full" and abort.
		 */
		int maxusers = channel.getIntValue("maxusers");
		if (maxusers != -1)
		{	int usersCount = messageBuilder.getTemporaryRelated(channel, "users").size();
			if (usersCount >= maxusers) return "full";
		}
				
		/* Make the relation between the user and the joined channel.
		 * Also give the relation ID to the RelationBreaker so the relation is automatically broken after a specified period of inactivety.
		 */
		try
		{	String tmp = tmpNodeManager.createTmpRelationNode("related", tOwner, messageBuilder.getNewTemporaryKey(), "realchannel", "realusers");
			MMObjectNode node = tmpNodeManager.getNode(tOwner, tmp);
			tmpNodeManager.setObjectField(tOwner, tmp, "snumber", channel.getValue("number"));
			tmpNodeManager.setObjectField(tOwner, tmp, "dnumber", user.getValue("number"));
			chatboxConnections.add(tOwner + "_" + tmp, (new Long(System.currentTimeMillis() + expireTime)).longValue());			
		}
		catch(Exception e) {
			log.error("join(): Could create temporary relations between between channel and user.\n" + e);
			return "failed";
		}

		/* If there is another channel to which the user is connected in the same community logout of the eldest connectiond,
		 * so the user is only in one channel of a community at the same time.
		 */
		if (otypeCommunity == -1) otypeCommunity = mmb.TypeDef.getIntValue("community");
		MMObjectNode relatedChannel;
		MMObjectNode channelInSameCommunity = null;
		MMObjectNode community = communityParent(channel);
		MMObjectNode relatedCommunity;

		relatedChannels = messageBuilder.getTemporaryRelated(user, "channel").elements();
		while (relatedChannels.hasMoreElements())
		{	relatedChannel = (MMObjectNode)relatedChannels.nextElement();
			relatedCommunity = communityParent(relatedChannel);
			if (relatedCommunity.getIntValue("number") == community.getIntValue("number"))
				if (relatedChannel.getIntValue("number") != channel.getIntValue("number"))
					logout(user, channelInSameCommunity);
		}		
		return "connected";
	}

	/**
	 * Removes the temporary relatios between the user and the channel, making the user logged out.
	 *
	 * @param The user to disconnect.
	 * @param The channel to disconnect from.
	 */
	public synchronized String logout(MMObjectNode user, MMObjectNode channel)
	{	
		MMObjectNode tmpInsRel;
		TypeDef typeDef = (TypeDef)mmb.getMMObject("typedef");
		int otypeInsrel = typeDef.getIntValue("insrel");

		if (messageBuilder == null) messageBuilder = (Message)mmb.getMMObject("message");
		Enumeration tmpInsRels = TemporaryNodes.keys();
		while (tmpInsRels.hasMoreElements())
		{	tmpInsRel = (MMObjectNode)TemporaryNodes.get(tmpInsRels.nextElement());
			if (tmpInsRel != null)
				if (tmpInsRel.getIntValue("otype") == otypeInsrel)
					if ((tmpInsRel.getValue("snumber") != null) && (tmpInsRel.getValue("dnumber") != null))
						if ((tmpInsRel.getIntValue("snumber") == channel.getIntValue("number")) && (tmpInsRel.getIntValue("dnumber") == user.getIntValue("number")))
						{	String _number = tmpInsRel.getStringValue("_number");
							String owner = _number.substring(0, _number.indexOf("_"));
							String key = _number.substring(_number.indexOf("_") + 1);
							tmpNodeManager.deleteTmpNode(owner, key);
							chatboxConnections.remove(_number);
							log.debug("logout(): owner = " + owner + " key = " + key);
						}
		}		
		return "logged out";
	}

	/**
	 * Tells relationBreaker not to remove automatically the relation between the user and the channel
	 * for another period of time.
	 */
	public synchronized void userStillActive(MMObjectNode user, MMObjectNode channel)
	{	//chatboxConnections.update(channel.getIntValue("number"), System.currentTimeMillis() + expireTime);		
	}
	
	/**
	 * Returns of which community the channel is part of.
	 *
	 * @param The channel node of who the parent community is asked.
	 * @return The parent community of the channel or when the channel in't part of a community null.
	 */
	public MMObjectNode communityParent(MMObjectNode channel)
	{	if (otypeCommunity == -1) otypeCommunity = mmb.TypeDef.getIntValue("community");
		Enumeration relatedCommunity = mmb.getInsRel().getRelated(channel.getIntValue("number"), otypeCommunity);
		if (relatedCommunity.hasMoreElements()) return (MMObjectNode)relatedCommunity.nextElement(); else return null;
	}	

	/**
	 * Gives the value of a field or virtual field from the channel node.
	 * The folowing virtual fields are calculated:
	 * readlogin - returns "true" or "false" depending of an user first has to loging before he may read messages.
	 * writelogin - returns "true" or "false" depending of an user first has to loging before he may post a message.
	 * hasmoods - "true" when there are moods related to the channel else "false'.
	 */
	public Object getValue(MMObjectNode node, String field)
	{ /* PRE:  The name of a field in the database or the name of a virtual field.
	   * POST: The value of the field.
           */	
		if (field.equals("readlogin"))
		{	int state = node.getIntValue("state");
			if ((state & 1) == 1) return "true"; else return "false";
		}
		if (field.equals("writelogin"))
		{	int state = node.getIntValue("state");
			if ((state & 2) == 2) return "true"; else return "false";
		}
		if (field.equals("hasmoods"))
		{	Enumeration channels = mmb.getInsRel().getRelated(node.getIntValue("number"), mmb.TypeDef.getIntValue("mood"));
			if (channels.hasMoreElements()) return "yes"; else return "no";
		}
		return(super.getValue(node, field));
	}

	/**
	 * Handles the $MOD-MMBASE-BUILDER-channel- commands.
	 * The commands to user are:
	 * OPEN - opens the channel
	 * CLOSE - closes the channel
	 * ISOPEN - returns if a channel is "open", "closed" or "readonly";
	 * DELALLMESSAGES - deletes all posted messages in the channel
	 * NEWSEQ - returns a new sequence number for in the channel
	 * JOIN-{a user number} - connects the user to the channel.
	 * QUIT-{a user number} - disconnects a user from the channel.
	 * STILLACTIVE-{a user number} - resets the time-out before the user is automatically disconnected from the channel.
	 */
	public String replace(scanpage sp, StringTokenizer tok)
	{
		/* The first thing we expect is a channel number.
		 */
		if (!tok.hasMoreElements()) {
			log.error("replace(): channel number expected after $MOD-BUILDER-channel-.");
			return "";
		}
		MMObjectNode channel = getNode(tok.nextToken());
		
		if (tok.hasMoreElements()) {
			String cmd = tok.nextToken();

			if (cmd.equals("OPEN")) open(channel);
			if (cmd.equals("CLOSE")) close(channel);
			if (cmd.equals("ISOPEN")) return isOpen(channel);
			if (cmd.equals("DELALLMESSAGES")) removeAllMessages(channel);
			if (cmd.equals("NEWSEQ")) return "" + getNewSequence(channel);
			if (cmd.equals("JOIN")) join(getNode(tok.nextToken()), channel);
			if (cmd.equals("QUIT")) logout(getNode(tok.nextToken()), channel);
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
		if (otypeCommunity == -1) otypeCommunity = mmb.TypeDef.getIntValue("community");
		Enumeration e = mmb.getInsRel().getRelated(src, otypeCommunity);
		if (!e.hasMoreElements()) {
			log.warn("GetDefaultURL Could not find related community for channel node " + src);
			return(null);
		}
		
		MMObjectNode commNode = (MMObjectNode)e.nextElement();
		String URL = commNode.parent.getDefaultUrl(commNode.getIntValue("number"));
		if (URL!=null) URL += "+" + src;
		return URL;
	}

	/**
	 * Represents the value of a channel node's field as more user friendly representation in the Editor.
	 */
	public String getGUIIndicator(String field, MMObjectNode node) {
		if (field.equals("session")) {
			int value = node.getIntValue(field);
			if (value == 1) return "yes"; else return "no";
		}
		if (field.equals("state")) { // alias login 
			int value = node.getIntValue(field);
			switch(value) {
				case 0: return "no login";
				case 2: return "login before post";
				case 3: return "login before read";
			}
		}
		if (field.equals("open")) {
			int value = node.getIntValue(field);
			switch(value) {
				case channelOpen: return "open";
				case channelReadOnly: return "readonly";
				case channelClosed: return "closed";				
			}
		}
		return null;
	}
}
