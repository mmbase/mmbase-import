package org.mmbase.module.community;

import java.util.*;
import java.awt.*;

import org.mmbase.module.core.*;
import org.mmbase.module.corebuilders.*;
import org.mmbase.module.core.TemporaryNodeManager;

/**
 * @author Dirk-Jan Hoekstra
 * @version 5 Jan 2001
 *
 * RelationBreaker stores relation numbers with an expiretime.
 * After the expiretime has expired the relation is removed.
 */

public class RelationBreaker extends Thread
{
	private String classname = getClass().getName();
	private Vector relations = new Vector();
	private long checkInterval = 10 * 60 * 1000;
	private MMBase mmb;
	private boolean shouldRun = false;
	private TemporaryNodeManager tmpNodeManager;

	private void debug(String msg)
	{	System.out.println (classname + "-> " + msg);
	}

	public RelationBreaker(MMBase mmb, long checkInterval, TemporaryNodeManager tmpNodeManager)
	{	this.mmb = mmb;
		this.checkInterval = checkInterval;
		this.tmpNodeManager = tmpNodeManager;
	}

	public synchronized void add(String id, long expireTime)
	{	relations.add(new RelationHolder(id, expireTime));
		debug("add");
		if (!shouldRun)
		{	shouldRun = true;
			start();
		}
	}

	public synchronized boolean update(String id, long expireTime)
	{	RelationHolder relationHolder = (RelationHolder)relations.elementAt(relations.indexOf(id));
		if (relationHolder != null)
		{	relationHolder.setExpireTime(expireTime);
			return true;
		}
		return false;
	}

	public synchronized void remove(String id)
	{	String owner = id.substring(0, id.indexOf("_"));
		String key = id.substring(id.indexOf("_") + 1);
		int i = relations.indexOf(id);
		if (i > 0) relations.remove(i);
		tmpNodeManager.deleteTmpNode(owner, key);
	}

	public synchronized void remove(RelationHolder relationHolder, int i)
	{	//relations.remove(i);
		debug(relationHolder.id);
		String owner = relationHolder.id.substring(0, relationHolder.id.indexOf("_"));
		String key = relationHolder.id.substring(relationHolder.id.indexOf("_") + 1);					
		tmpNodeManager.deleteTmpNode(owner, key);
	}

	public void run()
	{
		InsRel insrel = mmb.getInsRel();
		long currentTime;

		while (shouldRun)
		{
			try
			{	sleep(checkInterval);
			}
			catch(Exception e)
			{	debug("run(): can't sleep.");
				shouldRun = false;
				return;
			}

			currentTime = System.currentTimeMillis();

			debug("search for expired");
			int i = 0;
			while (i < relations.size())
			{	RelationHolder relationHolder = (RelationHolder)relations.elementAt(i);
				if (relationHolder.getExpireTime() < currentTime)
					remove(relationHolder, i);
				
				i++;
			}
		}		
	}
}


class RelationHolder
{	public String id;
	private long expireTime;

	public RelationHolder(String id, long expireTime)
	{	this.id = id;
		this.expireTime = expireTime;
	}

	public synchronized boolean equals(Object anObject)
	{	return (id.equals((String)anObject));
	}

	public synchronized void setExpireTime(long expireTime)
	{	this.expireTime = expireTime;
	}

	public synchronized long getExpireTime()
	{	return expireTime;
	}
}
