/*

This software is OSI Certified Open Source Software.
OSI Certified is a certification mark of the Open Source Initiative.

The license (Mozilla version 1.0) can be read at the MMBase site.
See http://www.MMBase.org/license

*/
package org.mmbase.util;

import java.lang.*;
import java.net.*;
import java.util.*;
import java.io.*;

/**
 * @author Daniel Ockeloen
 */
public class CVSReader {
	String filename;
	Hashtable name2pos;
	Vector rows=new Vector();

	public CVSReader(String filename) {
		readCVS(filename);
	}
	
	public void readCVS(String filename) {
		String body=loadFile(filename);

		StringTokenizer tok=new StringTokenizer(body,"\n\r");
		if (tok.hasMoreTokens()) name2pos=decodeHeader(tok.nextToken());
		rows=decodeBody(tok);	
	}

	Vector decodeBody(StringTokenizer mtok) {
		Vector results=new Vector();

		while (mtok.hasMoreTokens()) {
			int u=0;
			String line=mtok.nextToken();
			Vector results2=new Vector();
			StringTokenizer tok=new StringTokenizer(line,",\"\n\r",true);
			while (tok.hasMoreTokens()) {
				String bar=tok.nextToken();
				//System.out.println("BAR="+bar);
				if (bar.equals("\"")) {
					String part=tok.nextToken();
					String part2="";
					while (!part.equals("\"")) {
						part2+=part;	
						part=tok.nextToken();
					}
					//System.out.println("P="+part2+" "+(u++));
					results2.addElement(part2);
				} else {
					if (bar.equals(",")) {
					//	String part=tok.nextToken();
					//	System.out.println("P2="+part);
					//	results.addElement(part);
					} else {
						//System.out.println("P="+bar+" "+(u++));
						results2.addElement(bar);
					}
				}
			
			}
			results.addElement(results2);
		}
		return(results);	
	}

	Hashtable decodeHeader(String line) {
		int i=0;
		Hashtable results=new Hashtable();
		StringTokenizer tok=new StringTokenizer(line,",\n\r");
		while (tok.hasMoreTokens()) {
			String part=tok.nextToken();

    			part = Strip.DoubleQuote(part,Strip.BOTH); 
			results.put(part,new Integer(i++));
		}
		return(results);
	}

	public String loadFile(String filename) {
		try {
			File sfile = new File(filename);
			FileInputStream scan =new FileInputStream(sfile);
			int filesize = (int)sfile.length();
			byte[] buffer=new byte[filesize];
			int len=scan.read(buffer,0,filesize);
			if (len!=-1) {
				String value=new String(buffer,0);
				return(value);
			}
			scan.close();
		} catch(Exception e) {
			e.printStackTrace();
		}
		return(null);
	}

	public String getElement(int row,int col) {
		Vector rw=(Vector)rows.elementAt(row);
		String value=(String)rw.elementAt(col);
		return(value);
	}


	public String getElement(int row,String colname) {
		Integer ii=(Integer)name2pos.get(colname);
		if (ii!=null) {
			int i=ii.intValue();
			Vector rw=(Vector)rows.elementAt(row);
			String value=(String)rw.elementAt(i);
			return(value);
		}
		return(null);
	}

	public int size() {
		return(rows.size());
	}
}
