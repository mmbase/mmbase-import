/*

This software is OSI Certified Open Source Software.
OSI Certified is a certification mark of the Open Source Initiative.

The license (Mozilla version 1.0) can be read at the MMBase site.
See http://www.MMBase.org/license

*/
package org.mmbase.module.builders;

import java.util.*;
import java.sql.*;

import org.mmbase.module.core.*;

import org.mmbase.util.logging.*;

/**
 * @author Rico Jansen
 * @version $Id: MediaInputs.java,v 1.6 2003-03-10 11:50:20 pierre Exp $
 */
public class MediaInputs extends MMObjectBuilder {

    private static Logger log = Logging.getLoggerInstance(MediaInputs.class.getName());
	public static final int TV=1;
	public static final int Radio=2;
	public static final int Web=3;

	/**
	* insert a new object, normally not used (only subtables are used)
	*/
	/*
	public int insert(String owner,MMObjectNode node) {
		int medium=node.getIntValue("medium");
		int channel=node.getIntValue("channel");

		int number=getDBKey();
		if (number==-1) return(-1);
		try {
			MultiConnection con=mmb.getConnection();
			PreparedStatement stmt=con.prepareStatement("insert into "+mmb.baseName+"_"+tableName+" values(?,?,?,?,?)");
				stmt.setInt(1,number);
				stmt.setInt(2,oType);
				stmt.setString(3,owner);
				stmt.setInt(4,medium);
				stmt.setInt(5,channel);
				stmt.executeUpdate();
				stmt.close();
				con.close();
		} catch (SQLException e) {
			e.printStackTrace();
			log.error("Error on : "+number+" "+owner+" fake");
			return(-1);
		}
		signalNewObject(tableName,number);
		return(number);	
	}
	*/

	public String getGUIIndicator(MMObjectNode node) {
            return(getGUIIndicator("medium",node)+" "+node.getIntValue("channel"));
    }

	public String getGUIIndicator(String field,MMObjectNode node) {
		String rtn=null;
		if (field.equals("medium")) {
			int val=node.getIntValue("medium");
			switch(val) {
				case TV: rtn="Televisie";
					break;
				case Radio: rtn="Radio";
					break;
				case Web: rtn="Web";
					break;
				default: 	rtn="Onbekend";
					break;
			}
		}
		return(rtn);
	}

	public void maintainTable() {
		// We need to keep up to date with all medium/channel combinations
		// So we multilevel select : programs,insrel,episodes,bcastrel
		// With programs.medium and bcastrel.channel
		// Then we check if the combinations exist, if not insert

		MultiRelations multirel=(MultiRelations)mmb.getMMObject("multirelations");
		Vector tables,fields,ordervec,dirvec,mediums,channels;
		String selectstring,tablestring,relstring,mo,mi,query;
		Connection conn;

		tables=new Vector();
		tables.addElement("programs");
		tables.addElement("insrel");
		tables.addElement("episodes");
		tables.addElement("bcastrel");
		tables.addElement("mmevents");
		fields=new Vector();
		fields.addElement("programs.medium");
		fields.addElement("bcastrel.channel");
		ordervec=new Vector();
		ordervec.addElement("programs.medium");
		dirvec=new Vector();
		dirvec.addElement("UP");

		selectstring=multirel.getSelectString(tables,fields);
		tablestring=multirel.getTableString(tables);
		relstring=multirel.getRelationString(tables);
		query="SELECT DISTINCT "+selectstring+" FROM "+tablestring+" WHERE "+relstring;
		log.debug("MediaInputs : query "+query);
		conn=mmb.getConnection();
		mediums=new Vector();
		channels=new Vector();
		try {
			MMObjectNode node;

			Statement stmt=conn.createStatement();
			ResultSet rs=stmt.executeQuery(query);
			while(rs.next()) {
				mediums.addElement(new Integer(rs.getInt(1)));
				channels.addElement(new Integer(rs.getInt(2)));
			}
			stmt.close();
			conn.close();
		} catch (Exception e) {
			log.error("MediaInputs -> Can't update table");
			e.printStackTrace();
		}
		Integer medium,channel;
		for (int i=0;i<channels.size();i++) {
			medium=(Integer)mediums.elementAt(i);
			channel=(Integer)channels.elementAt(i);
			log.debug("MediaInputs : checking "+medium+" , "+channel);
			checkCombo(medium.intValue(),channel.intValue());
		}
	}

	private void checkCombo(int medium,int channel) {
		Statement stmt;
		ResultSet rs;
		Connection conn;
		MMObjectNode node;
		boolean exi;

		conn=mmb.getConnection();
		try {
			stmt=conn.createStatement();
			rs=stmt.executeQuery("select number from "+mmb.baseName+"_"+tableName+" where medium="+medium+" AND channel="+channel);	
			if (rs.next()) {
				exi=true;
			} else { 
				exi=false;
			}
			stmt.close();
			conn.close();
			if (exi) {
				log.debug("MediaInputs -> combo exists ("+medium+","+channel+")");
			} else {
				log.debug("MediaInputs -> creating combo ("+medium+","+channel+")");
				node=getNewNode("system");
				node.setValue("medium",medium);
				node.setValue("channel",channel);
				insert("system",node);
			}
		} catch(Exception e) {
			log.error("MediaInputs -> Can't check combo");
			e.printStackTrace();
		}

	}
}
