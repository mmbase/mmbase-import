package org.mmbase.module.builders;

import java.util.*;
import java.util.Date;
import java.sql.*;
import java.io.*;
import java.lang.reflect.Field;
import java.lang.Integer;
import java.awt.Point;

import org.mmbase.module.database.*;
import org.mmbase.module.core.*;
import org.mmbase.util.*;
import org.mmbase.module.core.MMObjectNode;
import org.mmbase.module.corebuilders.InsRel;
import org.mmbase.module.corebuilders.RelDef;
import org.mmbase.util.SortedVector;
import org.mmbase.module.core.TemporaryNodeManager;
import org.mmbase.module.corebuilders.*;
import org.mmbase.module.community.*;

/**
 * @author Dirk-Jan Hoekstra
 * @version 19 Jan 2001
 */

public class Message extends MMObjectBuilder
{ 
	private String classname = getClass().getName();
	private final boolean debug = true;
	private Channel channelBuilder;
	private int relationNumberParentChild;
	private int relationNumberCreator;
	private int relationRelated;
	private static int otypeMsg = -1;
	private static int otypeChannel = -1;
	private static int otypeCommunity = -1;
	private static int otypeMood = -1;
	private final static int maxBodySize = 2024;
	public TransactionManager transactionManager = null;
	public TemporaryNodeManager tmpNodeManager = null;
	private String tOwner = "system";
	//private String tName = "communityTransaction";
	private int tmpNumbers = 0; // this will be used to genererate keys for temporary nodes used by the community.
	private final String tmpNumberPrefix = "cmt";

	public Message()
	{
	}

	private void error(String err)
	{	debug("ERROR: " + err);
	}

	public boolean init()
	{	boolean result = super.init();
		RelDef reldef = ((RelDef)mmb.getMMObject("reldef"));
		relationNumberParentChild = reldef.getRelNrByName("parent", "child");
		relationNumberCreator = reldef.getRelNrByName("creator", "subject");
		relationRelated = reldef.getRelNrByName("related", "related");
		debug("init(): relationNumberParentChild=" + relationNumberParentChild);
		debug("init(): relationNumberCreator=" + relationNumberCreator);
		tmpNodeManager = new TemporaryNodeManager(mmb);
	/*	transactionManager = new TransactionManager(mmb, tmpNodeManager);
		try
		{	transactionManager.create(tOwner, tName);
		} catch(Exception e)
		{	error("Couldn't create a TransactionManager.");
			return false;
		}
	*/
		return result;
	}

	/**
	 * Post a new message in the channel. 
	 * When a chatterName is given, chatter is ignored and the chattername is stored in the message itself.
	 *
	 * @param subject The subject of the message.
	 * @param body The body of the message.
	 * @param channel The channel in which the message has to get posted.
	 * @param thread The number of the thread (a Message or Channel) in which the Message has to get listed.
	 * @param chatter The usernumber of the user that has written the message.
	 * @param chatterName The name of the person that has written the message when he hasn't a usernumber.
	 * @return The number of the new created message node or -1 when for some error reason no message was created.
	 */
	public int post(String subject, String body, int channel, int thread, int chatter, String chatterName)
	{ 
		if (body.length() > maxBodySize)
		{	error("post(): body size exceeds 2024Kb");
			return -1;
		}

		if (chatterName != null)
		{	chatter = -1;
			if (chatterName.length() == 0)
			{	error("post(): CHATTERNAME must be larger than 0 tokens");
				return -1;
			}
		}		
	
		MMObjectNode node = getNewNode("system");
		node.setValue("subject", subject);
		node.setValue("body", body);
		node.setValue("thread", thread);
		if (channelBuilder == null) channelBuilder = (Channel)mmb.getMMObject("channel");
		debug("post(): channel=" + channel);
		node.setValue("sequence", channelBuilder.getNewSequence(channelBuilder.getNode(channel)));
		/*
		 * Make the relation with the MessageTread in which this Message get listed.
		 * And make the relation between message and the uses who posted the message.
		 * Because InsRel keeps a cache of the last 25 'most used' relations wich doesn't
		 * work correct after a insert, delete all cached relations of the parent
		 * messagethread so the search will be done on the database.
		 */
		if (debug) debug("post(): make relation message with thread and chatter");
		InsRel insrel = mmb.getInsRel();
		int id;
		if (chatter > 0)
		{	id = insert("system", node);
			insrel.insert("system", chatter, id, relationNumberCreator);
		}
		else
		{	node.setValue("info", "name=\"" + chatterName + "\"");
			id = insert("system", node);			
		}		
		insrel.insert("system", id, thread, relationNumberParentChild);		
		insrel.deleteRelationCache(thread);		
		return id;
	}

	/**
	 * Posts a message as a temporary message node.
	 *
	 * @param body The body of the message.
	 * @param channel The channel in which the message has to get posted.
	 * @param chatter The usernumber of the user that has written the message.
	 */
	public void post(String body, int channel, int chatter)
	{	if (body.length() > maxBodySize)
		{	error("post(): body size exceeds " + maxBodySize + "Kb");
			return;
		}

		//debug ("post: chatter = " + chatter);

		/* Build a temporary message node.
		 */
		String key = tmpNodeManager.createTmpNode("message", tOwner, getNewTemporaryKey());
		MMObjectNode message = tmpNodeManager.getNode(tOwner, key);
		
		/* Set the fields.
		 */
		if (channelBuilder == null) channelBuilder = (Channel)mmb.getMMObject("channel");
		int sequence = channelBuilder.getNewSequence(channelBuilder.getNode(channel));
		TimeStamp timeStamp = new TimeStamp();

		tmpNodeManager.setObjectField(tOwner, key, "body",       (Object)body);
		tmpNodeManager.setObjectField(tOwner, key, "thread",     (Object)new Integer(channel));
		tmpNodeManager.setObjectField(tOwner, key, "sequence",   (Object)new Integer(sequence));		
		tmpNodeManager.setObjectField(tOwner, key, "timestampl", (Object)new Integer(timeStamp.lowIntegerValue()));
		tmpNodeManager.setObjectField(tOwner, key, "timestamph", (Object)new Integer(timeStamp.highIntegerValue()));

		//message.commit();

		/* Make the relation with the channel in which this Message get listed.
		 * And make the relation between message and the uses who posted the message.
		 */
		try
		{	String tmp = tmpNodeManager.createTmpRelationNode("parent", tOwner, getNewTemporaryKey(), "realchannel", key);
			tmpNodeManager.setObjectField(tOwner, tmp, "snumber", new Integer(channel));			
		}
		catch(Exception e)
		{	error("post(): Could create temporary relations between message and channel.\n" + e);
		}
		try
		{	String tmp = tmpNodeManager.createTmpRelationNode("creator", tOwner, getNewTemporaryKey(), "realuser", key);
			tmpNodeManager.setObjectField(tOwner, tmp, "snumber", (Object)new Integer(chatter));
			MMObjectNode node = tmpNodeManager.getNode(tOwner, tmp);
			debug ("just set " + tmp + " snumber to " + node.getIntValue("snumber"));			
		}
		catch(Exception e)
		{	error("post(): Could create temporary relations between between message and user.\n" + e);
		}



	}

	/**
	 * Inserts a message node to the database.
	 *
	 * @param owner The owner of the node.
	 * @param node The node to insert.
	 */
	public int insert(String owner, MMObjectNode node)
	{	/* Get the current time splitted into two Integers.
		 */
		TimeStamp timeStamp = new TimeStamp();
		node.setValue("timestampl", timeStamp.lowIntegerValue());
		node.setValue("timestamph", timeStamp.highIntegerValue());
		InsRel insrel = mmb.getInsRel();
		insrel.deleteRelationCache(node.getIntValue("thread"));
		return(super.insert(owner, node));
	}

	/**
	 * Changes the subject and body fields, and the name of the poster when stored in the info-field
	 * of an already existing message.
	 *
	 * @param chatterName The name of the person who have written the message.
	 * @param subject The subject of the message.
	 * @param body The body of the message.
	 * @param number The message node's number.
	 */
	public boolean update(String chatterName, String subject, String body, int number)
	{ /* PRE:  The Subject and Body of the Message and optionally the chatterName.
	   *       The number of the Message that has to be updated.
	   *       This function is usefull for editing or moderating the context of a message.
	   * POST: Make the changes on the node.
	   */	
		if (body.length() > maxBodySize)
		{	error("update(): body size exceeds 2024Kb");
			return false;
		}
	
		MMObjectNode node = getNode(number);
		if (chatterName != null)
		{	String info = (String)node.getValue("info");
			StringTagger tagger = new StringTagger(info);
			tagger.setValue("name", chatterName);
			node.setValue("info", tagger.toString());
		}
		node.setValue("subject", subject);
		node.setValue("body", body);
		return node.commit();
	}

	public Vector getListMessages(StringTagger tagger, String method)
	{ /* PRE:  Tagger has to contain the attributes of the LIST tag.
	   * POST: A vector containing the requested fields.
	   */

		Hashtable optionalAttributes = new Hashtable();

		/* Get the thread/node from who the related messages have to be given.
		 */
		String id = tagger.Value("NODE");
		MMObjectNode node = getNode(id);
		if (node == null)
		{	debug("getListMessages(): no or no correct node specified");
			return new Vector();
		}
		
		/* Get the fieldnames out of the FIELDS attribute.
		 */
		Vector fields = tagger.Values("FIELDS");

		/* Put in tagger the number of fields that will get returned.
		 */
		tagger.setValue("ITEMS","" + fields.size());

		/*
		 * When fields contains listhead it's assumed a <UL> HTML listing has to get generated.
		 * A <UL> list can only be generated when listhead, listtail and depth are both used.
		 * Since depth is optional it's added automatically when absence.
		 */
		String openTag = "<UL>";
		String closeTag = "</UL>";
		int listheadItemNr = fields.indexOf("listhead");
		int listtailItemNr = -1;
		int depthItemNr = -1;
		if (listheadItemNr > 0)
		{	listtailItemNr = fields.indexOf("listtail");
			depthItemNr = fields.indexOf("depth");
			if (depthItemNr < 0) fields.add(new String("depth"));
			openTag = tagger.Value("OPENTAG");
			closeTag = tagger.Value("CLOSETAG");
			if ((openTag == null) || (closeTag == null))
			{	openTag = "<UL>";
				closeTag = "</UL>";
			}
			else
			{	openTag = "<" + openTag.replace('\'','"').replace('#','=') + ">";
				closeTag = "</" + closeTag + ">";
			}
		}
		
		/* Get fromCount and maxCount.
		 */
		String tmp = tagger.Value("FROMCOUNT");
		int fromCount;
		if (tmp != null) fromCount = Integer.decode(tmp).intValue(); else fromCount = 0;
		int maxCount;
		tmp = tagger.Value("maxCount");
		if (tmp != null) maxCount = Integer.decode(tmp).intValue(); else maxCount = Integer.MAX_VALUE;
		int maxDepth;
		tmp = tagger.Value("MAXDEPTH");
		if (tmp != null) maxDepth = Integer.decode(tmp).intValue(); else maxDepth = Integer.MAX_VALUE;

		/* Get startAfterNode.
		 */
		tmp = tagger.Value("STARTAFTERNODE");
		int startAfterNode;
		if (tmp != null) startAfterNode = Integer.decode(tmp).intValue(); else startAfterNode = -1;
		
		/* Create a CompareMessage. If no SORTFIELDS or DBSORT are specified in the list sequence is used as a default.
		 * Sortdirections can be specified in SORTDIRS or DBDIR.
		 */
		Vector sortFields = tagger.Values("SORTFIELDS");
		if (sortFields == null)
		{	sortFields = tagger.Values("DBSORT");
			if (sortFields == null)
			{	sortFields = new Vector(1);
				sortFields.add(new String("sequence"));
			}
		}
		Vector sortDirs = tagger.Values("SORTDIRS");
		if (sortDirs == null) sortDirs = tagger.Values("DBDIR");
		CompareMessages compareMessages;
		if (sortDirs == null) compareMessages = new CompareMessages(sortFields); else compareMessages = new CompareMessages(sortFields, sortDirs);

		Vector result = getListMessages(node, fields, compareMessages, fromCount + maxCount, 0, maxDepth, startAfterNode);
		if (result.size() > maxCount) result = new Vector(result.subList(0, maxCount));

		if ((listheadItemNr >= 0) && (listtailItemNr >= 0))		
			addListTags(result, listheadItemNr, listtailItemNr, depthItemNr, fields.size(), openTag, closeTag);
		else
			if ((listheadItemNr >= 0) || (listtailItemNr >= 0)) error("getListMessages(): Listhead and listtail only work when used together."); 
		return result;
	}	

	public Vector getListMessages(MMObjectNode thread, Vector fields, CompareInterface ci, int maxCount, int depth, int maxDepth, int startAfterNode)
	{ /* PRE:  The tread who's childmessages you want to list and the (virtual) fields. fromCount skips all messages until a count of
	   *       fromCount messages have passed. maxCount specifies the maxium number of messages that will be returend.
	   *       Depth is used for the recursion depth of this function, and should be 0 for all first calls.
	   * POST: A vector containing the requested fields.
	   */
		Vector result = new Vector(), childMsgs;
		Vector relatedMessages = getRelatedMessages(thread, ci);

		int msgPointer = relatedMessages.size() - 1;
		int added = 0, count = 0;
		String item, cmd;
		MMObjectNode relmsg;
		int fieldsCount = fields.size();
		int replycount;
		boolean startAfterNodePassed = (startAfterNode == -1);
		
		while ((msgPointer >= 0) && (added < maxCount))
		{	relmsg = (MMObjectNode)relatedMessages.elementAt(msgPointer);
			if (!startAfterNodePassed) startAfterNodePassed = (startAfterNode == relmsg.getIntValue("number"));
			
			added++; // Another message is going to be added.
			if (depth < maxDepth)
			{	childMsgs = getListMessages(relmsg, fields, ci, maxCount - added, depth + 1, maxDepth, startAfterNode);
				replycount = childMsgs.size() / fieldsCount;
				added += replycount;

				/* When recursion stops becease we've got a big enough result,
				 * the number of returned childMsg is less than the actual ammount of childmessages
				 * and replycount isn't calculated correctly, therefor getNrMsgAndHighSeq is called.
				 */
				if (added == maxCount) replycount = getNrMsgAndHighSeq(relmsg).x;
			}
			else
			{	childMsgs = new Vector();
				replycount = getNrMsgAndHighSeq(relmsg).x;
			}					
			
			if (startAfterNodePassed)
			{	
				/* Add the fields of the relmsg to the result and then of his children.
				*/
				for (int i = 0; i < fieldsCount; i++)
				{ 	cmd = ((String)fields.elementAt(i)).trim();
					if (cmd.equals("depth"))
					 item = "" + depth;
					else
					 if (cmd.equals("replycount"))
					  item = "" + replycount;
					 else
					  if ((cmd.equals("listhead")) || (cmd.equals("listtail")))
					   item = "";					
					  else
					   item = "" + relmsg.getValue(cmd);
		  			result.add(item);
				}
				result.addAll(childMsgs);
			}
			
			msgPointer--;
			count++;			
		}
		return result;
	}

	public Vector getRelatedMessages(MMObjectNode node, CompareInterface ci)
	{ /* PRE:  A message or channel node.
	   * POST: A vector containing the related message sorted by sequence number.
	   */
		if (otypeMsg < 0) otypeMsg = mmb.TypeDef.getIntValue("message");

		SortedVector result = new SortedVector(ci);
		Enumeration relatedMessages;

		MMObjectNode channel = isPostedInChannel(node);
		if (otypeCommunity < 0) otypeCommunity = mmb.TypeDef.getIntValue("community");
		Enumeration relatedCommunity = mmb.getInsRel().getRelated(channel.getIntValue("number"), otypeCommunity);
		MMObjectNode community = (MMObjectNode)relatedCommunity.nextElement();
		String kind = (String)community.getValue("kind");

		if (kind.equals("chatbox")) 
			relatedMessages = getTemporaryRelated(node, "message").elements();
		else
			relatedMessages = mmb.getInsRel().getRelated(node.getIntValue("number"), otypeMsg);

		if (relatedMessages == null) return result;

		MMObjectNode relmsg = null;
		int parent = node.getIntValue("number");
		while (relatedMessages.hasMoreElements())
		{	/* Tempory hack: test if the related message is indeed a child by his thread field.
			 */
			relmsg = (MMObjectNode)relatedMessages.nextElement();
			if (relmsg.getIntValue("thread") == parent)
			result.addSorted(relmsg);
		}
		return result;
	}

	/**
	* Get temporary MMObjectNodes related to a specified MMObjectNode
	* @param sourceNode this is the source MMObjectNode 
	* @param wtype Specifies the type of the nodes you want to have e.g. wtype="pools"
	*/
	public Vector getTemporaryRelated(MMObjectNode node, String wtype)
	{	
		Vector result = new Vector();
		MMObjectNode relatedNode;
		MMObjectNode tmpInsRel;
		boolean found;
		String _dnumber;
		String _snumber;
		TypeDef typeDef = (TypeDef)mmb.getMMObject("typedef");
		int otypeInsrel = typeDef.getIntValue("insrel");
		int otypeWanted = typeDef.getIntValue(wtype);

		/* Get the node's number or _number.
		 */
		int number;
		String _number = (String)node.getValue("_number");
		if (_number == null) number = node.getIntValue("number"); else number = -1;
		
		/* Get all temporary nodes and filter out all insrels.
		 */
		Enumeration tmpInsRels = TemporaryNodes.keys();
		while (tmpInsRels.hasMoreElements())
		{	tmpInsRel = (MMObjectNode)TemporaryNodes.get(tmpInsRels.nextElement());
			if (tmpInsRel != null)
				if (tmpInsRel.getIntValue("otype") == otypeInsrel)
				{
					found = false;
					/* Test if the (_)snumbers are equal.
					 */
					if (_number != null)			
						found = _number.equals(tmpInsRel.getStringValue("_snumber"));
					else
						if (tmpInsRel.getValue("snumber") != null)
							found = (number == tmpInsRel.getIntValue("snumber"));
						else found = false;

					if (found) // snumbers are equal
					{	if (tmpInsRel.getValue("dnumber") != null)
						{	relatedNode = getNode(tmpInsRel.getIntValue("dnumber"));
							if (relatedNode != null)
								if (relatedNode.getIntValue("otype") == otypeWanted) result.add(relatedNode);
						}
						else
						{	_dnumber = (String)tmpInsRel.getValue("_dnumber");
							if (_dnumber != null)
							{	relatedNode = (MMObjectNode)TemporaryNodes.get(_dnumber);
								if (relatedNode != null)
									if (relatedNode.getIntValue("otype") == otypeWanted) result.add(relatedNode);
							}
						}
					}
					else
					{	if (_number != null)
							found = _number.equals(tmpInsRel.getStringValue("_dnumber"));
						else
							if (tmpInsRel.getValue("dnumber") != null)
								found = (number == tmpInsRel.getIntValue("dnumber"));
							else
								found = false;	

						if (found) // (_)dumbers are equal.
						{	if (tmpInsRel.getValue("snumber") != null)
							{	relatedNode = getNode(tmpInsRel.getIntValue("snumber"));						
								if (relatedNode != null)
									if (relatedNode.getIntValue("otype") == otypeWanted) result.add(relatedNode);
							}
							else
							{	_snumber = (String)tmpInsRel.getValue("_snumber");
								if (_snumber != null)
								{	relatedNode = (MMObjectNode)TemporaryNodes.get(_snumber);
									if (relatedNode != null)
										if (relatedNode.getIntValue("otype") == otypeWanted) result.add(relatedNode);
								}
							}
						}
					}
			}
		}
		return result;
	}

	private void addListTags(Vector items, int listheadItemNr, int listtailItemNr, int depthItemNr, int fieldsCount, String openTag, String closeTag)
	{ /* PRE:  A vector containing list ITEMS, the field number of listheadtag, listtailtag, and depth (it's the
	   *       number in for example $ITMEM4 minus one) and the number of fields specified.
	   * POST: There was no easy way for letting getListMessages(...) fill in the fields listhead and listtail,
	   *       so it's done here instead.
	   */
		int depth;
		int previousDepth = 0;
		int itemNr = 0;
		int itemsCount = items.size();
		String prefix, postfix;
		while (itemNr < itemsCount)
		{	depth = (Integer.decode((String)items.elementAt(itemNr + depthItemNr))).intValue();
			if (itemNr == 0)
			{	prefix = openTag; // begin of the list
				while (previousDepth < depth)
				{	prefix += openTag; // begin of sublist, just in case a page starts with a sublist
					previousDepth++;
				}
			}
			else
				if (depth > previousDepth) prefix = openTag; else prefix = ""; // begin of a sublist
			postfix = "";
			if (depth < previousDepth)
				while (previousDepth > depth)
				{	prefix += closeTag; // end of one ore more sublists
					previousDepth--;
				}
			if (itemNr == itemsCount - fieldsCount)
			{	depth = 0;
				while (previousDepth > depth)
				{	postfix += closeTag; // end of one ore more sublists
					previousDepth--;					
				}
				postfix += closeTag + closeTag;
			}
			items.set(itemNr + listheadItemNr, prefix);
			items.set(itemNr + listtailItemNr, postfix);
			itemNr += fieldsCount;
			previousDepth = depth;
		}
	}

	public void removeNode(MMObjectNode node)
	{ /* POST: Remove this node if it's not a parent message to another message.
	   */
		removeNode(node, false);
	}

	public void removeNode(MMObjectNode node, boolean recursive)
	{ /* PRE:  The node that has to be deleted and if childmessages should be deleted or not.
	   * POST: If the Message is not a parent for any other Message or recursive is true all relations with other 
	   *       nodes will be deleted and the node itself will be removed.
	   */

		debug("removeNode(): node="+node.getValue("number"));

		if (otypeMsg < 0) otypeMsg = mmb.TypeDef.getIntValue("message");
		
		/* Make sure we got a Message node, else abort the remove operation.
		 */
		if (node.getIntValue("otype") != otypeMsg)
		{	debug("removeNode(" + node.getValue("number") + ") aborted because it isn't a Message!" + " It's a " + node.getName());
						
			try {
				throw new Exception("test");
			} catch (Exception e) { 
				debug("Stacktrace "+e);
				e.printStackTrace();
			}
		
			return;
		}
		
		/* If recursive is true delete the childmessages else abort on any childmessages.
		 */
		MMObjectNode relmsg;
		for (Enumeration messages = mmb.getInsRel().getRelated(node.getIntValue("number"), otypeMsg); messages.hasMoreElements();)
		{	relmsg = (MMObjectNode)messages.nextElement();
			if (relmsg.getIntValue("thread") == node.getIntValue("number")) // Before doing all this hot stuff, there should be here a check preventing recorded messages get deleted!
			{	/* Here we have a childmessage.
				 */ 
				if (recursive)
					removeNode(relmsg, true);
				else
				{	error("Can't delete Message " + node.getValue("number") + " because it has child Messages.");
					return;
				}
			}		 	
		}				

		/* Remove all relations to other nodes.
		 */
		InsRel insrel = mmb.getInsRel();
		insrel.removeRelations(node);
		insrel.deleteRelationCache(node.getIntValue("number"));
		insrel.deleteRelationCache(node.getIntValue("thread"));
		Enumeration messages = mmb.getInsRel().getRelated(node.getIntValue("number"), otypeChannel);
		if (messages.hasMoreElements()) debug("he you should have removed them!"); 

		/* Now it's possible to remove the node itself.
		 */
		super.removeNode(node);
	}

	public Point getNrMsgAndHighSeq(MMObjectNode node)
	{ /* PRE:  A message or channel node.
	   * POST: A point in which the x contains the number of messages in this thread
	   *       and the y the highest sequence in this thread.
	   */
		if (otypeMsg < 0) otypeMsg = mmb.TypeDef.getIntValue("message");
		int sequence = -1;
		int highestSequence = -1;
		int messageCount = 0;
		MMObjectNode msg;
		Enumeration messages = mmb.getInsRel().getRelated(node.getIntValue("number"), otypeMsg);
		while(messages.hasMoreElements())
		{	msg = (MMObjectNode)messages.nextElement();
			if (msg.getIntValue("thread") == node.getIntValue("number"))
			{	sequence = msg.getIntValue("sequence");
				if (sequence > highestSequence) highestSequence = sequence;
				/* Get the nummer of messages and the highes sequence in this childmessage.
				 */
				Point cs = getNrMsgAndHighSeq(msg);
				messageCount += cs.x + 1;
				if (cs.y > highestSequence) highestSequence = cs.y;
			}
		}
		return new Point(messageCount, highestSequence);
	}

	public boolean hasReplies(MMObjectNode node)
	{ /* PRE:  A message or channel node.
	   * POST: A point in which the x contains the number of messages in this thread
	   *       and the y the highest sequence in this thread.
	   */
		if (otypeMsg < 0) otypeMsg = mmb.TypeDef.getIntValue("message");
		Enumeration messages = mmb.getInsRel().getRelated(node.getIntValue("number"), otypeMsg);
		return (messages.hasMoreElements());
	}

	public Object getValue(MMObjectNode node, String field)
	{ /* PRE:  The name of a field in the database or the name of a virtual field implemented in the Message object.
	   * POST: The value of the field.
           */	
		if (field.equals("timestamp"))
		{	TimeStamp ts = getTimeStamp(node);
			return "" + ts.getTime();
		}
		if (field.equals("resubject"))
		{	String subject = node.getStringValue("subject");
			if (subject.startsWith("RE: ")) return subject; else return ("RE: " + subject);
		}
		if (field.equals("replycount"))
		{	return new Integer((getNrMsgAndHighSeq(node)).x);
		}
		if (field.equals("hasreplies"))
		{	if (hasReplies(node)) return "true"; else return "false";
		}
		if (field.equals("parent"))
		{	return node.getValue("thread");
		}
		if (field.startsWith("getinfovalue"))
		{	String key = field.substring(13, field.length() - 1); // 13 is the beginning of the first character after '('.
			StringTagger tagger = new StringTagger(node.getStringValue("info"));
			return tagger.Value(key);
		}
		if (field.startsWith("substring"))
		{	/* Something like substring(realfield,size) is expected. The realField is asked from the node and
			 * truncated to the size. Truncation is done before whole words.
			 */
			int commaPos = field.lastIndexOf('x');
			String realField = field.substring(10, commaPos); // 10 is the beginning of the first character after '('.
			String value = (String)node.getValue(realField);
			int size = Integer.decode(field.substring(commaPos + 1, field.length() - 1)).intValue();
			if (size < value.length())
				return value.substring(0, size);
			else
				return value;
		} 						// ??? Must still cut for new word!
		return(super.getValue(node, field));
	}

	/**
	 * Handles the $MOD-MMBASE-BUILDER-message- commands.
	 */
	public String replace(scanpage sp, StringTokenizer tok)
	{
		/* The first thing we expect is a message number.
		 */
		if (!tok.hasMoreElements())
		{	error("replace(): message number expected after $MOD-BUILDER-message-.");
			return "";
		}
		String tmp = tok.nextToken();
		MMObjectNode message = getNode(tmp);
		//tmp = tmp.substring(tmp.indexOf("_") + 1);
		if (message == null) message = (MMObjectNode)TemporaryNodes.get(tmp.trim());	
		if (message == null) error ("didn't got a message node, what is this " + tmp + "?");
		
		if (tok.hasMoreElements())
		{	String cmd = tok.nextToken();
			if (cmd.equals("DEL")) removeNode(message, true);
			if (cmd.equals("GETINFOFIELD")) return getInfoField(message, tok.nextToken());
			if (cmd.equals("SETINFOFIELD"))
			{	setInfoField(message, tok.nextToken(), tok.nextToken());
				message.commit();
			}
		}
		return "";
	}

	/**
	 * The info field of the message node contains a StringTagger. This function sets the field in the tagger to value.
	 */
	private void setInfoField(MMObjectNode message, String field, String value)
	{	String info = message.getStringValue("info");
		StringTagger tagger = new StringTagger(info);
		tagger.setValue(field, value);
		message.setValue("info", tagger.toString());
	}

	/**
	 * The info field of the message node contains a StringTagger. This function gets the value out of the field in the tagger.
	 */
	private String getInfoField(MMObjectNode message, String field)
	{	String info = message.getStringValue("info");
		StringTagger tagger = new StringTagger(info);
		return tagger.Value(field);
	}

	/**
	 * Get the nodes timestampl and timestamph and return them as a TimeStamp.
	 */
	public TimeStamp getTimeStamp(MMObjectNode node)
	{	return new TimeStamp((Integer)node.getValue("timestampl"), (Integer)node.getValue("timestamph"));
	}
	
	/**
	 * Returns the channel in which the given message node is posted.
	 */ 
	public MMObjectNode isPostedInChannel(MMObjectNode node)
	{ 	if (otypeMsg < 0) otypeMsg = mmb.TypeDef.getIntValue("message");
		while (node.getIntValue("otype") == otypeMsg) node = getNode(node.getIntValue("thread"));
		return node;
	}

	/**
	 * Generate temporary keys for the temporary nodes used by the community.
	 */
	synchronized public String getNewTemporaryKey()
	{	return (tmpNumberPrefix + tmpNumbers++);
	}
}

