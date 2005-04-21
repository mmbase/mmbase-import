/*

VPRO (C)

This source file is part of mmbase and is (c) by VPRO until it is being
placed under opensource. This is a private copy ONLY to be used by the
MMBase partners.

*/
package org.mmbase.module;

import java.lang.*;
import java.net.*;
import java.util.*;
import java.io.*;
import java.awt.*;

import org.mmbase.util.*;

/**
 * The module which provides access to a filesystem residing in
 * a database
 *
 * @author Rico Jansen
 * @author Daniel Ockeloen
 */
public abstract class objectstore extends Module {


	/**
	 * Put an object into the objectstore at the default (current) time
	 */
	public abstract long putObject(String user,String name,filebuffer obj);

	/**
	 * Put an object into the objectstore at the specified time
	 */
	public abstract long putObjectAtTime(String user,String name,filebuffer obj,Date when);

	/**
	 * Get an object from the objectstore, the last one stored 
	 */
	public abstract filebuffer getObject(String user,String name);

	/**
	 * Get an object from the objectstore at the specified time 
	 */
	public abstract filebuffer getObjectAtTime(String user,String name,Date when);

	/**
	 * Get the Directory entries from a Directory (warning this generates a full tree)
	 */
	public abstract Vector getDirectory(String user,String dir);

	/**
	 * Get the Directory entries from a Directory simple, only files in this directory are returned 
	 */
	public abstract Vector getDirectorySimple(String user,String dir);

	/**
	 * Get a list of entries which reside within the specified timespan of the file
	 */
	public abstract Vector getDates(String user,String name,Date begin,Date end);

	/**
	 * Get a list of entries which reside within the specified timespan of the file
	 */
	public abstract Vector getDates(String user,String dir,String file,Date begin,Date end);

	/**
	 * Get the revision/timestamp Directory of a user
	 */
	public abstract Vector getRevDirectory(String user,String dir);

	/**
	 * Get the lastmodified of a file 
	 */
	public abstract Date getLastModified(String user,String name);

	/**
	 * Get all the file information from a file (currently only returns last modified)
	 */
	public abstract Date getFileInfo(String user,String name);

	/**
	 * Copy a file from a user to another user 
	 */
	public abstract boolean CopyFile(String srcuser,String srcpath,String dstuser,String dstpath);

	/**
	 * Copy a file from a user to another user , specifying at which timestamp
	 */
	public abstract boolean CopyFile(String srcuser,String srcpath,Date srcdate,String dstuser,String dstpath);

	// New New


	public long putProperties(String user, String name, Hashtable obj) {
		String body="",key;

		for (Enumeration e=obj.keys();e.hasMoreElements();) {
			key=(String)e.nextElement();
			body+=key+"="+(String)obj.get(key)+"\n";
		}
		filebuffer buffer = new filebuffer(body.length()-1);
		body.getBytes(0,body.length()-1,buffer.data,0);

		buffer.mimesuper="vpro";
		buffer.mimesub="properties";
		buffer.mimetype="vpro/properties";
		return(putObject(user,name,buffer));
	}

	public Hashtable getProperties(String user, String name) {
		String body="",key,val,line;
		Hashtable obj;
		int pos;
		
		filebuffer buffer=getObject(user,name);
		if (buffer!=null) {
			obj=new Hashtable();
			body=new String(buffer.data,0);
			StringTokenizer tok = new StringTokenizer(body,"\r\n");
			while (tok.hasMoreTokens()) {
				line=tok.nextToken();
				pos=line.indexOf('=');
				if (pos!=-1) {
					key=line.substring(0,pos);
					val=line.substring(pos+1);
					obj.put(key,val);
				}
			}
			System.out.println(obj);
			return(obj);
		} else {
			return(null);
		}
	}

}

