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
import org.mmbase.module.community.*;

/**
 * @author Dirk-Jan Hoekstra
 * @version 31 Jan 2001
 */

public class Community extends MMObjectBuilder
{ 
	private String classname = getClass().getName();
	private final boolean debug = true;
	private Channel channelBuilder;
	private int relationNumberParentChild;
	private int relationNumberCreator;
	private static int otypeMsg = -1;
	private static int otypeChannel = -1;

	public Community()
	{
	}

	/**
	 * Prints a error message.
	 *
	 * @param msg The error message.
	 */
	private String error(String msg)
	{	return classname + " error-> " + msg;
	}

	public boolean init()
	{	boolean result = super.init();
		RelDef reldef = ((RelDef)mmb.getMMObject("reldef"));
		relationNumberParentChild = reldef.getRelNrByName("parent", "child");
		relationNumberCreator = reldef.getRelNrByName("creator", "subject");		
		return result;
	}

	/**
	 * Opens all the channels of the community.
	 *
	 * @param community The community node of which to open all the channels.
	 */
	public void openAllChannels(MMObjectNode community)
	{	if (channelBuilder == null) channelBuilder = (Channel)mmb.getMMObject("channel");
		Enumeration relatedChannels = mmb.getInsRel().getRelated(community.getIntValue("number"), otypeMsg);
		while (relatedChannels.hasMoreElements()) channelBuilder.open((MMObjectNode)relatedChannels.nextElement());
	}

	/**
	 * Closes all the channels of the community.
	 *
	 * @param community The community of which to close all the channels.
	 */
	public void closeAllChannels(MMObjectNode community)
	{	if (channelBuilder == null) channelBuilder = (Channel)mmb.getMMObject("channel");
		Enumeration relatedChannels = mmb.getInsRel().getRelated(community.getIntValue("number"), otypeMsg);
		while (relatedChannels.hasMoreElements()) channelBuilder.close((MMObjectNode)relatedChannels.nextElement());
	}

	/**
	 * Handles the $MOD-MMBASE-BUILDER-community- commands.
	 */
	public String replace(scanpage sp, StringTokenizer tok)
	{
		/* The first thing we expect is a community number.
		 */
		if (!tok.hasMoreElements())
		{	error("replace(): community number expected after $MOD-BUILDER-community-.");
			return "";
		}		
		MMObjectNode community = getNode(tok.nextToken());
		
		if (tok.hasMoreElements())
		{	String cmd = tok.nextToken();
			if (cmd.equals("OPEN")) openAllChannels(community);
			if (cmd.equals("CLOSE")) closeAllChannels(community);
		}
		return "";
	}
	
	/**
	 * Ask URL from related Map and append the community number to the URL
	 *
	 * @param src The number of the community MMObjectNode.
	 */
	public String getDefaultUrl(int src)
	{
		int otypeMap = mmb.TypeDef.getIntValue("maps");
		Enumeration e= mmb.getInsRel().getRelated(src, otypeMap);
		if (!e.hasMoreElements())
		{	debug("GetDefaultURL Could not find related map for community node " + src);
			return(null);
		}
		
		MMObjectNode mapNode = (MMObjectNode)e.nextElement();
		String URL = mapNode.parent.getDefaultUrl(mapNode.getIntValue("number"));
		if (URL!=null) URL += "+" + src;
		return URL;
	}
}
