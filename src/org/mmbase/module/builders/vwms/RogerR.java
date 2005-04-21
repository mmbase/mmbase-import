/*

VPRO (C)

This source file is part of mmbase and is (c) by VPRO until it is being
placed under opensource. This is a private copy ONLY to be used by the
MMBase partners.

*/
package org.mmbase.module.builders.vwms;

import java.util.*;
import java.io.*;
import java.sql.*;

import org.mmbase.module.database.*;
import org.mmbase.module.core.*;
import org.mmbase.util.*;
import org.mmbase.module.builders.*;

/**
 * @author Daniel Ockeloen
 */

public class RogerR extends Vwm {

	public RogerR() {
		System.out.println("Yo Im Roger Rabbit");
	}

	public boolean probeCall() {
		//doCopy();
//		doCreateNetfiles();
		return(true);
	}

	/**
	*/
	public void doCopy() {
		try {
			MultiConnection con=Vwms.mmb.getConnection();
			Statement stmt=con.createStatement();
			ResultSet rs=stmt.executeQuery("select b.number, a.title, b.url from "+Vwms.mmb.baseName+"_cdtracks a, "+Vwms.mmb.baseName+"_rawaudios b where (a.number=b.id)AND(b.url not like '%F=%')AND(format=2) order by b.number desc");
			//ResultSet rs=stmt.executeQuery("select f.number,e.title,f.url from vpro4_pools a, vpro4_insrel b, vpro4_episodes c, vpro4_insrel d, vpro4_audioparts e, vpro4_rawaudios f where a.number=1672822 and a.number=b.dnumber and c.number=b.snumber and c.number=d.snumber and e.number=d.dnumber and e.number=f.id and f.status=3 and f.format=2 and f.url not like '%F=%'");
			// ResultSet rs=stmt.executeQuery("select f.number,e.title,f.url from vpro4_episodes c, vpro4_insrel d, vpro4_audioparts e, vpro4_rawaudios f where e.number>1836055 and c.number=d.snumber and e.number=d.dnumber and e.number=f.id and f.status=3 and f.format=2 and f.url not like '%F=%'");
			int max=0;
			Vector nodes=new Vector();
		
			RawAudios bul=(RawAudios)Vwms.mmb.getMMObject("rawaudios");		
			while (rs.next() && max<25) {
				int number=rs.getInt(1);
				String title=rs.getString(2);
				String url=rs.getString(3);
				max++;
				MMObjectNode node=bul.getNode(number);
				if (node!=null) {
					//node.setValue("title",title);
					nodes.addElement(node);		
				}
				// following steps will check and perform changes on the
				// rawaudios
			}
			stmt.close();
			con.close();
			System.out.println("RogerR-> Found size n="+nodes.size());

			Enumeration t=nodes.elements();
			boolean error=false;
			while (t.hasMoreElements() && !error) {
				MMObjectNode node=(MMObjectNode)t.nextElement();
				int id=node.getIntValue("id");
				String url=node.getStringValue("url");
				int speed=node.getIntValue("speed");
				int channels=node.getIntValue("channels");
				System.out.println("RogerR-> Copy id="+id+" url="+url);
				// is the url allready in the new format ? if not convert the node
				// the new F= format
				if (url.indexOf("F=")==-1) {
					url="F=/"+id+"/"+(speed/1000)+"_"+channels+".ra H1=station.vpro.nl";
					System.out.println("RogerR -> NEWURL="+url);
				}
				if (url.indexOf("streams.omroep.nl")==-1) {
					// use ssh/scp to copy the file to remore
					if (doScpCopy(id,""+(speed/1000)+"_"+channels+".ra","vprosmc@streams.omroep.nl:rafiles")) {
						url+=" H2=streams.omroep.nl/vpro";
						System.out.println("RogerR -> NEWURL="+url);
						node.setValue("url",url);
						node.commit();
					} else {
						System.out.println("RogerR -> NEWURL=PROBLEM WITH SCP COPY");
					}
				}
			}
		} catch (SQLException e) {
			e.printStackTrace();
		}
	}


	/**
	*/
	public void doCreateNetfiles() {
		try {
			MultiConnection con=Vwms.mmb.getConnection();
			Statement stmt=con.createStatement();
			ResultSet rs=stmt.executeQuery("select id,status,format from "+Vwms.mmb.baseName+"_rawaudios where number>1800000 AND status=3 AND format=2 order by b.number desc");
			int max=0;
			Vector nodes=new Vector();
		
			RawAudios bul=(RawAudios)Vwms.mmb.getMMObject("rawaudios");		
			while (rs.next() && max<1000) {
				int number=rs.getInt(1);
				max++;
				MMObjectNode node=bul.getNode(number);
				if (node!=null) {
					nodes.addElement(node);		
					System.out.println("Netfile="+number);
				}
				// following steps will check and perform changes on the
			}
			stmt.close();
			con.close();
		} catch (SQLException e) {
			e.printStackTrace();
		}
	}

	boolean doScpCopy(int id,String file,String host) {
		System.out.println("RogerR -> S="+execute("/bin/bash /data/audio/ra/RogerR.sh "+id+" "+file+" "+host));
		return(true);
	}


	/**
 	 * executes the given command
	 * @return standard output
	 */
	private String execute (String command) {
		Process p=null;
        String s="",tmp="";
		DataInputStream dip= null;

		try {
			System.out.println("RogerR -> Command ="+command);
			p = (Runtime.getRuntime()).exec(command,null);
			System.out.println("RogerR -> Process = "+p);
		} catch (Exception e) {
			s+=e.toString();
			return s;
		}
		dip = new DataInputStream(p.getErrorStream());
		System.out.println("RogerR -> Dip = "+dip);
        try {
            while ((tmp = dip.readLine()) != null) {
               	s+=tmp+"\n"; 
		}
        } catch (Exception e) {
			s+=e.toString();
			return s;
	}
	return s;
	}
}
