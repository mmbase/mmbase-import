/* -*- tab-width: 4; -*-

This software is OSI Certified Open Source Software.
OSI Certified is a certification mark of the Open Source Initiative.

The license (Mozilla version 1.0) can be read at the MMBase site.
See http://www.MMBase.org/license

*/
/*
	-----------------------------------------------------------------------
	WARNING WARNING WARNING WARNING WARNING WARNING WARNING WARNING WARNING       	

							---------------
							PORT NEW SERVER 
							---------------
	-----------------------------------------------------------------------
	Line	: 350
	What	: if (alias != null) 
	else	: exception ( see line 350, logged exception from server beep )
	who		: marcel maatkamp, marmaa@vpro.nl
	-----------------------------------------------------------------------
							---------------
							PORT NEW SERVER 
							---------------

	WARNING WARNING WARNING WARNING WARNING WARNING WARNING WARNING WARNING       
	-----------------------------------------------------------------------

*/

package org.mmbase.module;

import java.util.*;

import org.mmbase.util.*;
import org.mmbase.module.core.*;
import org.mmbase.module.builders.*;

import org.mmbase.util.logging.Logger;
import org.mmbase.util.logging.Logging;

/**
 * The module which provides access to a filesystem residing in
 * a database
 *
 * @author Daniel Ockeloen
 */
public class Stats extends ProcessorModule implements StatisticsInterface {

    private static Logger log = Logging.getLoggerInstance(Stats.class.getName()); 
    
	//private String classname = getClass().getName();

	MMBase mmbase;
	Statistics bul;
	StatisticsProbe probe=null;
	int saveDelay=0;
	boolean processingDirty=false;
	LRUHashtable alias2number = new LRUHashtable(250);

	Vector dirty=new Vector(); // Hack vector to signal dirty

	public void init() {
		super.init();
	    mmbase=(MMBase)getModule("MMBASEROOT");
		

		bul=(Statistics)mmbase.getMMObject("statistics");
		if( bul == null )
		{
			log.error("init(): ******************** No bul created!!! *************************");
		}

		if (probe==null) probe=new StatisticsProbe(this);
	}


	public void onload() {
	}

	public void unload() {
	}

	public void shutdown() {
	}


	/**
	 */
	public Stats() {
	}

	/**
	 * Generate a list of values from a command to the processor
	 */
	 public Vector getList(scanpage sp,StringTagger tagger, String value) throws ParseException {
    	String line = Strip.DoubleQuote(value,Strip.BOTH);
		StringTokenizer tok = new StringTokenizer(line,"-\n\r");
		if (tok.hasMoreTokens()) {
			String cmd=tok.nextToken();	
			if (cmd.equals("RANGE")) return(doRange(tok,tagger));
		}
		return(null);
	}

	/**
	 */
	public Vector doRange(StringTokenizer tok,StringTagger tagger) {
		if (bul==null) {
				bul=(Statistics)mmbase.getMMObject("statistics");
		}
		Vector results=new Vector();
		String number=tagger.Value("NODE");
		int pixels=400;
		try {
			pixels=Integer.parseInt(tagger.Value("PIXELS"));
		} catch(Exception e) {}

		// get node info and shadow info
		MMObjectNode node=bul.getNode(number);
		Hashtable childs = bul.getShadows (number);
		int timeSlices=node.getIntValue("timeslices");	

		// get current slice values
		int curnr=getSliceNr(number);
		int curval=node.getIntValue("count");
		//int curtime=(int)(System.currentTimeMillis()/1000); // datefix
		int curtime=(int)(System.currentTimeMillis()/1000);

		// first find max to be able to resize
		int max=curval;
		int count;
		int total=curval;
		MMObjectNode node2;
		for (int j=0;j<timeSlices;j++) {
			node2=(MMObjectNode)childs.get(new Integer(j));
			if (node2!=null) {
				count=node2.getIntValue("count");
				if (count>max) max=count;
				total+=count;
			}
		}
		float scale=((float)pixels)/max;

		// max is down figure out how to scale it to size

		int type=0;int start=0;
		for (int j=0;j<timeSlices;j++) {
			count=0;type=0;start=0;
			node2=(MMObjectNode)childs.get(new Integer(j));
			if (node2!=null) {
				count=node2.getIntValue("count");
				start = node2.getIntValue ("start");
				type=1;
			}
			if (j==curnr) {
				type=2;
				count=curval;
				start=curtime;
			}
			results.addElement(""+count);
			results.addElement(""+(int)(count*scale));
			results.addElement(""+type);
			results.addElement(""+total);
			if (start!=0) {
				// start-=6600; // datefix
				String tmp=DateSupport.getTimeSec(start)+" op "+DateSupport.getMonthDay(start)+"/"+DateSupport.getMonth(start)+"/"+DateSupport.getYear(start);
				results.addElement(tmp);
			} else {
				results.addElement("");
			}
		}
		tagger.setValue("ITEMS","5");
		return(results);
	}

	/**
	 * Execute the commands provided in the form values
	 */
	public boolean process(scanpage sp, Hashtable cmds,Hashtable vars) {
		log.info("CMDS="+cmds);
		log.info("VARS="+vars);
		return(false);
	}

	/**
	*	Handle a $MOD command
	*/
	public String replace(scanpage sp, String cmds) {
		bul=(Statistics)mmbase.getMMObject("statistics");
		StringTokenizer tok = new StringTokenizer(cmds,"-\n\r");
		if (tok.hasMoreTokens()) {
			String cmd=tok.nextToken();	
			if (cmd.equals("GETCOUNT")) {
				if (tok.hasMoreTokens()) {
					String nodeStr=tok.nextToken();
					if (nodeStr!=null) { 
						return(getCount(nodeStr));
					} else {
						return("No Node field");
					}
				}
			}
			if (cmd.equals("ADDCOUNT")) {
				if (tok.hasMoreTokens()) {
					String nodeStr=tok.nextToken();
					if (nodeStr!=null) { 
						//log.debug ("Setting counter...");

						String tmp = setCount (nodeStr, 1);

						//log.debug ("Counter set.");

						return tmp;
					} else {
						return("No Node field");
					}
				}
			}
		}
		return("No command defined");
	}
	
	public String getCount(String number) {
		if (bul==null) {
			bul=(Statistics)mmbase.getMMObject("statistics");
		}
		MMObjectNode node=bul.getNode(number);
		if (node!=null) {
			int count=node.getIntValue("count");
			return(""+count);
		} else {
			return("No Stats node");
		}
	}


	public int getSliceNr(String number) {
		if (bul==null) {
				bul=(Statistics)mmbase.getMMObject("statistics");
		}
		MMObjectNode node=bul.getNode(number);
		if (node!=null) {
			int nrOfSlices = node.getIntValue ("timeslices");
			int interval = node.getIntValue ("timeinterval");
			int curSliceBegin = node.getIntValue ("timeslice");
			int curSliceNr = (curSliceBegin / interval) % nrOfSlices;
			return(curSliceNr);
		}
		return(-1);
	}



/**
 * Given the number of a statistics node and a slice-index number, returns the 
 * corresponding shadow-statistics node.
 */
private MMObjectNode getSlice (String number,int slice) {
	// Retreive a hashtable with the shadow-nodes belonging to the statistics node
	Hashtable h = ((Statistics)mmbase.getMMObject ("statistics")).getShadows (number);
	if (h!=null) {
		// Hashtable exists, so get the slice from the index corresponding with the slice-number
		MMObjectNode node = (MMObjectNode)h.get (new Integer (slice));
		int tmp = -1;
		// If node exists, check its slice-number of the node found with the requested slice-number
		if (node != null) tmp = node.getIntValue ("slicenumber");

		//if (tmp == -1) log.debug ("Slice " + slice + " is not available");
		//else log.debug ("Slice " + slice + " Tmp " + tmp + " (hashed)");

		return node;
	} else {
		//log.debug ("getSlice didn't get a Hashtable from getShadows");
		return(null);
	}
}

/**
 * Given an existing ShadowStatistics, reset its values
 */
private void resetShadow (MMObjectNode n, int start, int stop) {
	n.setValue ("start", start);
	n.setValue ("stop", stop);
	n.setValue ("count", 0);
}


	/**
	 *
	 */
	public synchronized String setAliasCount(String alias, int incr) {
		String number=getAliasNumber(alias);
		if (number==null) {
			NewStat(alias,"Autogenerated",0,0,0,"",incr);
			return("");
		} else {
			return(setCount(number,incr));
		}
	}



	public String getAliasNumber(String alias) {
	// try to get the alias from the alias2number table
	if (alias!=null) {
		String number=(String)alias2number.get(alias);
		if (number!=null) {
			// oke alias fount
			return(number);
		} else {
			if (bul==null) {
					bul=(Statistics)mmbase.getMMObject("statistics");
			}
			// get the number from the database by a search
	
			String alias2=Encode.encode("ESCAPE_SINGLE_QUOTES", alias);
	
			// hack hack (marcel) 
			// gives:
			// worker -> Service Error : java.lang.NullPoiNterException
			//	java.lang.NullPointerException
			//        at vpro.james.modules.Stats.getAliasNumber(Stats.java:333)
			//        at vpro.james.modules.Stats.setAliasCount(Stats.java:311)
			//        at vpro.james.modules.Stats.countSimpleEvent(Stats.java:562)
			//        at vpro.james.servlets.replacer.servscan.service(servscan.java:176)
			//        at vpro.james.coreserver.HttpServlet.service(HttpServlet.java:65)
			//        at vpro.james.coreserver.worker.run(worker.java:564)
			//        at java.lang.Thread.run(Thread.java)

			if( alias2 != null && !alias2.equals(""))
			{
				if( bul != null )
				{
		    		Enumeration w=bul.search("WHERE name='"+alias2+"'");

					if(w != null) 
					{
						while (w.hasMoreElements()) 
						{
							MMObjectNode node=(MMObjectNode)w.nextElement();
							number=""+node.getIntValue("number");
							alias2number.put(alias2,number);
							return(number);
						}
					}
					else
						log.error("getAliasNumber("+alias+"), alias2("+alias2+"), w("+w+"): Enumeration empty!");
				}
				else
					log.error("getAliasNumber("+alias+"), alias2("+alias2+"), bul("+bul+"): Bul empty!");
			}
			else
			{
				log.error("getAliasNumber("+alias+"), alias2("+alias2+"): Null or empty from client!");
			}
		}
	} else {
		log.warn("Stats -> alias==null");
	}
	return(null);
}

/**
 *
 */
	public synchronized String setCount (String number, int incr) 
	{
		//debug ("Number=" + number + ", increase=" + incr);

		try 
		{
			if (bul==null) {
				bul=(Statistics)mmbase.getMMObject("statistics");
			}
		
			if( bul != null )
			{
	
				MMObjectNode stats = bul.getNode (number);
	
				if (stats != null) 
				{
					/* ----------------------------------------------------------
					 * First check if we really have to mess with the shadow-statistics.
					 * 
					 * Parameters for all statistics
					 *  hitTime    = the absolute time of the hit, in seconds.
					 *  statsBegin = the absolute starttime of the statistics, in seconds.
					 *  nrOfSlices = number of time-slices
					 *  interval   = size of a time-interval
					 * 
					 * Parameters for 'current' statistics
					 *  curSliceBegin = the relative starttime of the current statistics (relative w.r.t. statsBegin)
					 *  hits          = number of hits in last interval
					 */
					//int hitTime    = (int)(new java.util.Date().getTime() / 1000); // datefix
					int hitTime=(int)(System.currentTimeMillis()/1000);
			
					int statsStart = stats.getIntValue ("start");
					int nrOfSlices = stats.getIntValue ("timeslices");
					int interval   = stats.getIntValue ("timeinterval");
			
					int curSliceBegin = stats.getIntValue ("timeslice");
					int hits          = stats.getIntValue ("count");
			
					//log.debug ("Current statistics begin is " + (statsStart + curSliceBegin));
					//log.debug ("Hit came on time " + hitTime);
			
					if ((nrOfSlices < 1) || ((hitTime >= statsStart + curSliceBegin) && (hitTime < (statsStart + curSliceBegin + interval)))) { // This statistics-node has no shadow-statistics OR hit falls in current slice
						hits += incr;
						stats.setValue ("count", hits);
						if (!dirty.contains(stats)) dirty.addElement(stats);
						//log.debug ("Increased node " + number + " to " + hits);
						return ("");
					}
					int curSliceNr = (curSliceBegin / interval) % nrOfSlices;
					
					/*-------------------------------------------------------------
					 * Time of the current slice has expired, so if it contains hits we should copy it
					 * to a shadow.
					 * 
					 * curSliceNr = slice number of the current statistics
					 */
					if (hits > 0) {
						MMObjectNode nowSlice = getSlice (number,curSliceNr);
			
						if (nowSlice == null) { // Node doesn't exist, so we have to make it
							//log.debug ("Slice " + curSliceNr + " doesn't exist yet... making it");
							StatisticsShadow ssbuild = ((StatisticsShadow)mmbase.getMMObject ("sshadow"));
							nowSlice = ssbuild.getNewNode ("logger");
							nowSlice.setValue ("parent", java.lang.Integer.parseInt (number));
							nowSlice.setValue ("slicenumber", curSliceNr);
							nowSlice.setValue ("data", stats.getStringValue ("data"));
							nowSlice.setValue ("start", curSliceBegin);
							nowSlice.setValue ("stop", curSliceBegin + interval - 1);
							nowSlice.setValue ("count", hits);
			
							int id = ssbuild.insert ("logger", nowSlice); 
							((Statistics)mmbase.getMMObject ("statistics")).delShadows(number);
							} else {
							//log.debug ("Changing values of existing slice " + curSliceNr);
							nowSlice.setValue ("start", curSliceBegin);
							nowSlice.setValue ("stop", curSliceBegin + interval - 1);
							nowSlice.setValue ("count", stats.getIntValue ("count"));
							if (!dirty.contains(nowSlice)) dirty.addElement(nowSlice);
						}
					} 
					else 
					{
						//log.debug ("Current stats have no hits, so no need to store it");
					}
			
					/*--------------------------------------------------------------
					 * Update the "current" statistics.
					 *
					 *  hitSliceBegin = relative begin-time of the time-slice in which the hit occured
					 */
					int hitSliceBegin = ((hitTime - statsStart) / interval) * interval;
					stats.setValue ("count", incr); // We still have to count the hit that caused all this mess
					stats.setValue ("timeslice", hitSliceBegin);
			
					if (!dirty.contains(stats)) dirty.addElement(stats);	
			
					/*-------------------------------------------------------------------
					 * Update old existing slices in the database. We do not create slices of which the
					 * count is 0 anyway. When searching the database we have to consider this.
					 *
					 *  fillSliceBegin = relative begin-times of the time-slices "before" the slice with the hit
					 *  fillSliceNr    = number of the time-slices before the slice with the hit
					 *  max            = maximum number of time-slices that may be reset
					 */
					int fillSliceBegin = hitSliceBegin - interval;
					int fillSliceNr = (fillSliceBegin / interval) % nrOfSlices;
					int max = nrOfSlices;
			
					for ( ; fillSliceBegin > curSliceBegin && max > 0
					      ; fillSliceBegin -= interval, fillSliceNr--, max--
					    ) {
						if (fillSliceNr < 0) fillSliceNr = nrOfSlices - 1;
			
						MMObjectNode s = getSlice (number,fillSliceNr);
			
						if (s != null) { // Node exists in database, so we have to reset it to new values
							//log.debug ("Resetting values of slice " + fillSliceNr + " (new begin = " + fillSliceBegin + ")");
							resetShadow (s, fillSliceBegin, fillSliceBegin + interval - 1);
							if (!dirty.contains (s)) dirty.addElement (s);
						} 
						else 
						{
							log.warn("Skipping slice " + fillSliceNr + ", begin should have been " + fillSliceBegin);
						}
					}
				}
				return ("");		
			}
			else
			{
				log.debug(""); //??
			}
			/* else */
			//debug ("No Stats node");
	
			return (null);
	
		} 
		catch(Exception re) 
		{
			log.error(Logging.stackTrace(re));
			return("error");
		}
	}



	public String getModuleInfo() {
		return("Support routines for statistics, Daniel Ockeloen");
	}

	public void checkDirty() {
		// Check against check when processing
		if (!processingDirty) {
			Vector proc=null;
			processingDirty=true;
			// Clone the dirty vector
			synchronized(dirty) {
				if (dirty.size()>0) {
					proc=(Vector)dirty.clone();
					dirty.removeAllElements();
				}
			}
			// Process the (copied) dirty elements
			if (proc!=null) {
				MMObjectNode node;
				log.info("Updating "+proc.size()+" nodes");
				while (proc.size()>0) {
					node=(MMObjectNode)proc.elementAt(0);
					node.commit();
					proc.removeElementAt(0);		
					try {Thread.sleep(5000);} catch (InterruptedException e){} // 5 Sec is dat niet wat veel ???
					try {Thread.sleep(250);} catch (InterruptedException e){}
				}
			}
			processingDirty=false;
		} else {
			log.info("CheckDirty while Processing");
		}
	}


	/**
	* insert a new object, normally not used (only subtables are used)
	*/
	public void NewStat(String name,String description,int timeslices,int timeinterval, int timeslice, String data, int inc) {
		if( bul != null )
		{
			MMObjectNode node=bul.getNewNode("system");
			node.setValue("name",name);		
			node.setValue("description",description);		
			node.setValue("start",0);		
			node.setValue("count",inc);		
			node.setValue("timeslices",timeslices);		
			node.setValue("timeinterval",timeinterval);		
			node.setValue("timeslice",timeslice);		
			node.setValue("timesync",0);		
			node.setValue("data",data);		
			bul.insert("system",node);
		}
		else
		{
			log.error("NewStat(" + name + "," + description + ","+ timeslices + "," + timeinterval + "," + timeslice + "," + data + ","+inc+"): Bul not defined!");
		}
	}

	/** 
	* maintainance call, will be called by the admin to perform managment
	* tasks. This can be used instead of its own thread.
	*/
	public void maintainance() {
		if (probe==null) probe=new StatisticsProbe(this);
	}

	/**
	* count simple events is a method that can only count how many times something
	* happend, but unlike other statistics nodes its very simple to use because
	* it autogenerates its nodes based an the name itself and caches this name for
	* speed.
	*/
	public boolean countSimpleEvent(String eventname) {
		setAliasCount(eventname,1);
		return(true);
	}
}
