/*

This software is OSI Certified Open Source Software.
OSI Certified is a certification mark of the Open Source Initiative.

The license (Mozilla version 1.0) can be read at the MMBase site.
See http://www.MMBase.org/license

*/
package org.mmbase.module;
 

import java.lang.*;
import java.net.*;
import java.util.*;
import java.io.*;

import javax.servlet.*;
import javax.servlet.http.*;

import org.mmbase.module.core.*;
import org.mmbase.module.builders.*;
import org.mmbase.util.*;

/**
 * Simple file cache system that can be used by any servlet
 * the configuration properties of this file can be found in /config/database/modules/cache.properties
 */
public class scancache extends Module implements scancacheInterface {

	private String 	classname 	= getClass().getName();
	private boolean	debug		= false;
	private void debug( String msg ) { System.out.println( classname +":"+ msg ); }
	
	Hashtable pools = new Hashtable();
	Hashtable timepool = new Hashtable();
	private String cachepath="";
	MMBase mmbase;	
	// org.mmbas StatisticsInterface stats;

	public void onload() {
	}


	public void shutdown() {
	}


	/**
	 * Simple file cache system that can be used by any servlet
	 */
	public scancache() {
	}

	public String get(String poolName, String key) {
		if (debug) debug("poolName="+poolName+" key="+key);
		if (poolName.equals("HENK")) {
			String tmp=get(poolName,key,">");
			return(tmp);
		} else if (poolName.equals("PAGE")) {
			return(getPage(poolName,key,""));
		}
		debug("get("+poolName+","+key+"): ERROR: poolname("+poolName+") is an unknown cachetype!");
		return(null);
	}


	public String getNew(String poolName, String key,String line) {
		line=line.substring(0,line.indexOf('>'));
		StringTagger tagger=new StringTagger(line);
		String counter=tagger.Value("COUNTER");
		/* org.mmbase
		if( debug ) debug("scancache -> new poolName="+poolName+" key="+key+" line="+line+" tagger="+counter+" stats="+stats);
		if (counter!=null && stats!=null) {
			stats.setCount(counter,1);
		}
		*/
		if (poolName.equals("HENK")) {
			String tmp=get(poolName,key,">");
			return(tmp);
		} else if (poolName.equals("PAGE")) {
			return(getPage(poolName,key,line));
		}
		debug("getNew("+poolName+","+key+"): ERROR: poolname("+poolName+") is an unknown cachetype!");
		return(null);
	}

	private static int defaultExpireTime = 21600; // in sec 6 uur
	
	public String get(String poolName, String key, String line) {
		// get the interval time
		// ---------------------
		String tmp=line.substring(0,line.indexOf('>')).trim();
		int interval;
		if (tmp.equals("")) interval = defaultExpireTime;
		else if (tmp.toUpperCase().equals("NOEXPIRE")) interval = Integer.MAX_VALUE;
		else try {
				 interval = Integer.parseInt(tmp);
			 }
			 catch (NumberFormatException n) {
				 debug("CACHE "+poolName+": Number format exception for expiration time");
				 interval = defaultExpireTime;
			 }
		int now=(int)(System.currentTimeMillis()/1000);

		LRUHashtable pool=(LRUHashtable)pools.get(poolName);
		if (pool!=null) {
			String value=(String)pool.get(key);

			// oke i got may i return it or is it expired ?
			// --------------------------------------------
			try {
				// now get the time from memory
				// ----------------------------
				Integer tmp2=(Integer)timepool.get(poolName+key);
				int then=tmp2.intValue();
				if( debug ) debug("scancache -> file="+then+" now="+now+" interval="+interval);
				if (((now-then)-interval)<0) {
					if (value!=null) return(value);
				} else {
					debug("get("+poolName+","+key+","+line+"): Wow its expired");
					timepool.remove(poolName+key);
					return(null);
				}
			} catch(Exception e) {}
		}
		fileInfo fileinfo=loadFile(poolName,key);
		if (fileinfo!=null && fileinfo.value!=null) {
			if (((now-fileinfo.time)-interval)>0) {
				if( debug ) debug("get("+poolName+","+key+","+line+"): Diskfile invalid for file("+fileinfo.time+"), now("+now+") and interval("+interval+")");
				return(null);
			}
			if (pool==null) {
				// create a new pool
				pool=new LRUHashtable(100);
				pools.put(poolName,pool);
			}
			pool.put(key,fileinfo.value);
			timepool.put(poolName+key,new Integer(fileinfo.time));
			return(fileinfo.value);
		}
		return(null);
	}

	public String getPage(String poolName, String key,String line) {
		fileInfo fileinfo=loadFile(poolName,key);
		if (fileinfo!=null && fileinfo.value!=null) {
			return(fileinfo.value);
		} else {
			/*
			if (mmbase!=null) {
				pagemakers bul=(pagemakers)mmbase.getMMObject("pagemakers");	
				if (bul!=null) {
					MMObjectNode node=bul.getNode(1452549);
					if (node!=null) {
						String state=node.getStringValue("state");
						debug("scancache -> PAGE STATE="+state);
						if (state!=null && state.equals("waiting")) {
							node.setValue("info","URL=\""+key+"\" "+line);
							node.setValue("state","newpage");
							node.commit();
						}
					}
				} else {
					debug("scancache-> can't get pagemakers");
				}
			}	
			return(null);
			*/
			return(null);
		}
	}

	/**
	* try to put a cacheline in cache, returns old one if available
	* in all other cases returns null.
	*/
	public String newput(String poolName,HttpServletResponse res, String key,String value, String mimeType) {
		LRUHashtable pool=(LRUHashtable)pools.get(poolName);
		if (pool==null) {

			// create a new pool
			// -----------------
			pool=new LRUHashtable();
			pools.put(poolName,pool);
		}
		// got pool now insert the new item and save to disk
		// -------------------------------------------------
		if (poolName.equals("HENK")) {
			saveFile(poolName,key,value);
			return((String)pool.put(key,value));
		} else if (poolName.equals("PAGE")) {
			saveFile(poolName,key,value);

			// new file for asis support
			// -------------------------
			int pos=key.indexOf('?');
			String filename=key;
			if (pos!=-1) {
				filename=filename.replace('?',':');
				filename+=".asis";
			} else {
				filename+=":.asis";
			}
			// obtain and add headers
			// ----------------------
			// org.mmbase String body="Status:"+(((worker)res).getWriteHeaders()).substring(8);

			String body=getWriteHeaders(value, mimeType);
			body+=value;
			saveFile(poolName,filename,body);
			signalNetFileSrv(filename);
			return((String)pool.put(key,value));
		}
		debug("newPut("+poolName+",HttpServletRequest,"+key+","+value+"): ERROR: poolname("+poolName+") is not a valid cache name!");
		return(null);
	}

	// temp hack for asis
	public String newput2(String poolName,String key,String value,int cachetype, String mimeType) {
		LRUHashtable pool=(LRUHashtable)pools.get(poolName);
		if (pool==null) {

			// create a new pool
			// -----------------
			pool=new LRUHashtable();
			pools.put(poolName,pool);
		}
		if( debug ) debug("newput2("+poolName+","+key+","+value+","+cachetype+"): NEWPUT");

		// got pool now insert the new item and save to disk
		// -------------------------------------------------

		if (poolName.equals("HENK")) {
			saveFile(poolName,key,value);
			return((String)pool.put(key,value));
		} else if (poolName.equals("PAGE")) {
			saveFile(poolName,key,value);

			// new file for asis support
			// -------------------------
			int pos=key.indexOf('?');
			String filename=key;
			if (pos!=-1) {
				filename=filename.replace('?',':');
				filename+=".asis";
			} else {
				filename+=":.asis";
			}
			// obtain and add headers
			// ----------------------
			
			String body=getWriteHeaders(value, mimeType);
			body+=value;

			if( debug ) debug("newput2("+poolName+","+key+","+value+","+cachetype+"): NEWPUT=SAVE");
			saveFile(poolName,filename,body);
			if (cachetype!=0) signalNetFileSrv(filename);
			return((String)pool.put(key,value));
		}
		debug("newput2("+poolName+","+key+","+value+","+cachetype+"): ERROR: poolName("+poolName+") is not a valid cachetype!");
		return(null);
	}

	/**
	* try to put a cacheline in cache, returns old one if available
	* in all other cases returns null.
	*/
	public String put(String poolName, String key,String value) {
		LRUHashtable pool=(LRUHashtable)pools.get(poolName);
		if (pool==null) {
			// create a new pool
			pool=new LRUHashtable();
			pools.put(poolName,pool);
		}
		// got pool now insert the new item and save to disk
		saveFile(poolName,key,value);
		return((String)pool.put(key,value));
	}
	

	public void init() {
		String statmode=getInitParameter("statmode");	
		cachepath=getInitParameter("CacheRoot");	
		if (cachepath==null) {
			debug("SCANCACHE -> No CacheRoot set in SCANCACHE.properties");
		}
		mmbase=(MMBase)getModule("MMBASEROOT");
		/* org.mmbase
		if (statmode!=null && statmode.equals("yes")) {
			stats=(StatisticsInterface)getModule("STATS");
		} else {
			stats=null;
		}
		*/
	}

	public void unload() {
	}

	public Hashtable state() {
		/*
		state.put("Hits",""+hits);
		state.put("Misses",""+miss);
		*/
		return(state);
	}
	
	/** 
	* maintainance call, will be called by the admin to perform managment
	* tasks. This can be used instead of its own thread.
	*/
	public void maintainance() {

	}

	/*
	void readParams() {
		String tmp=getInitParameter("MaxLines");
		if (tmp!=null) MaxLines=Integer.parseInt(tmp);
		tmp=getInitParameter("MaxSize");
		if (tmp!=null) MaxSize=Integer.parseInt(tmp)*1024;
		tmp=getInitParameter("Active");
		if (tmp!=null && (tmp.equals("yes") || tmp.equals("Yes"))) {
			active=true;
		} else {
			active=false;
		}
	}
	*/ 

	public String getModuleInfo() {
		return("this module provides cache function scan requests");	
	}

	public boolean saveFile(String pool,String filename,String value) {
		if( debug ) debug("saveFile("+pool+","+filename+",length("+value.length()+" bytes): saving!");
		File sfile = new File(cachepath+pool+filename);
		try {
			DataOutputStream scan = new DataOutputStream(new FileOutputStream(sfile));
			scan.writeBytes(value);
			scan.flush();
			scan.close();
		} catch(Exception e) {
			// e.printStackTrace();
			String dname=cachepath+pool+filename;
			int pos=dname.lastIndexOf('/');
			String dirname=dname.substring(0,pos);
			File file = new File(dirname);
			try {
				if (file.mkdirs()) {
					DataOutputStream scan = new DataOutputStream(new FileOutputStream(sfile));
					scan.writeBytes(value);
					scan.flush();
					scan.close();
				} else {
					// debug("scandisk cache -> making "+dirname+" failed ");
				}
			} catch (Exception f) {
					//debug("scandisk cache -> Saving file "+filename+" failed "+f);
			}
			return(false);
		}
		return(true);
	}

	public fileInfo loadFile(String pool,String filename) {
		fileInfo fileinfo=new fileInfo();
		try {
			File sfile = new File(cachepath+pool+filename);
			FileInputStream scan =new FileInputStream(sfile);
			int filesize = (int)sfile.length();
			byte[] buffer=new byte[filesize];
			int len=scan.read(buffer,0,filesize);
			if (len!=-1) {
				String value=new String(buffer,0);
				fileinfo.value=value;
				fileinfo.time=(int)(sfile.lastModified()/1000);
				return(fileinfo);
			}
			scan.close();
		} catch(Exception e) {
			// its a cache so ehmmm no warning e.printStackTrace();
			return(null);
		}
		return(null);
	}

	public void signalNetFileSrv(String filename) {
		if( debug ) debug("signalNetFileSrv("+filename+"): SIGNAL");
		if (mmbase!=null) {
			NetFileSrv bul=(NetFileSrv)mmbase.getMMObject("netfilesrv");	
			if (bul!=null) {
				((NetFileSrv)bul).fileChange("pages","main",filename);
			}
		} else {
			debug("signalNetFileSrv("+filename+"): ERROR: can't use NetFileSrv builder");
		}
	}

	String getWriteHeaders(String value, String mimeType) {
		if ((mimeType==null) || mimeType.equals("") || mimeType.equals("text/html"))
			mimeType = "text/html; charset=iso-8859-1";
		String body="Status: 200 OK\n";
		body+="Server: OrionCache\n";
		body+="Content-type: "+mimeType+"\n";
		body+="Content-length: "+value.length()+"\n";
		body+="Expires: Fri, 15 Oct 1999 10:44:47 GMT\n";
		body+="Date: Fri, 15 Oct 1999 12:44:47 GMT\n";
		body+="Cache-Control: no-cache\n";
		body+="Pragma: no-cache\n";
		body+="Last-Modified: Fri, 15 Oct 1999 12:44:47 GMT\n\n";
		return(body);
	}
	
	/**
	 * Removes an entry from the cache.
	 * @param pool name of cache, "HENK" or "PAGE"
	 * @param key url of page 
	 * @return nothing
	 */
	public void removeCacheEntry(String pool, String key) {
			timepool.remove( pool+key );
	}
}
