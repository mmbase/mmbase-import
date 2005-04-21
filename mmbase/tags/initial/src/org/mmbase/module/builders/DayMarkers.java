/*

VPRO (C)

This source file is part of mmbase and is (c) by VPRO until it is being
placed under opensource. This is a private copy ONLY to be used by the
MMBase partners.

*/
package org.mmbase.module.builders;

import java.util.*;
import java.sql.*;
import org.mmbase.module.core.*;
import org.mmbase.module.database.*;
import org.mmbase.util.*;

/**
 * @author Daniel Ockeloen
 * @version 12 Mar 1997
 */
public class DayMarkers extends MMObjectBuilder {

	public int day=0;
	Hashtable daycache=new Hashtable();
	boolean setmarker=false;

	public DayMarkers() {
		day=daycount();
	}

	public int daycount() {
		int time=(int)(DateSupport.currentTimeMillis()/1000);
		return(time/(3600*24));
	}

	

	public boolean commit(MMObjectNode node) {
		boolean res=super.commit(node);
		return(res);
	}


	public void createMarker() {
		if (setmarker) return;
		int max=-1;
		int mday=-1;
		//System.out.println("Daymarker -> DAY="+day);
		try {
			MultiConnection con=mmb.getConnection();
			Statement stmt=con.createStatement();
			ResultSet rs=stmt.executeQuery("select number from "+mmb.baseName+"_"+tableName+" where daycount="+day);
			if (rs.next()) {
				mday=rs.getInt(1);
			}
			stmt.close();
			con.close();
		} catch (Exception e) {
			e.printStackTrace();
		}

		if (mday<0) {
			System.out.println("DayMarker inserting new marker "+day);
			try {
				MultiConnection con=mmb.getConnection();
				Statement stmt=con.createStatement();
				ResultSet rs=stmt.executeQuery("select max(number) from "+mmb.baseName+"_object");
				if (rs.next()) {
					max=rs.getInt(1);
				}
				stmt.close();
				con.close();
				MMObjectNode node=getNewNode("system");
				node.setValue("daycount",day);
				node.setValue("mark",max);
				insert("system",node);
			} catch(Exception e) {
				e.printStackTrace();
			}
		} else {
			//System.out.println("DayMarker marker already exists "+day);
		}
		setmarker=true;
	}

	public int getAge(MMObjectNode node) {
		/*
		return(node.getAge());
		*/
		// still has to be implemented with a cache !!!

		return(-1);
	}

	public int getDayCountAge(int daysold) {
		int wday=day-daysold;
		Integer result=(Integer)daycache.get(new Integer(wday));
		if (result!=null) {
			return(result.intValue());
		}
		if (mmb==null) return(-1);
		try {
			MultiConnection con=mmb.getConnection();
			if (con==null) return(-1);
			Statement stmt=con.createStatement();
			ResultSet rs=stmt.executeQuery("select mark from "+mmb.baseName+"_daymarks where daycount="+(wday));
			if (rs.next()) {
				int tmp=rs.getInt(1);
				daycache.put(new Integer(wday),new Integer(tmp));
				stmt.close();
				con.close();
				return(tmp);
			}
			stmt.close();
			con.close();
		} catch(Exception e) {
			daycache.put(new Integer(wday),new Integer(1));
			return(1);
		}
		daycache.put(new Integer(wday),new Integer(1));
		return(1);
	}
}
