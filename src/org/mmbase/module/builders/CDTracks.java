/*

VPRO (C)

This source file is part of mmbase and is (c) by VPRO until it is being
placed under opensource. This is a private copy ONLY to be used by the
MMBase partners.

*/
package org.mmbase.module.builders;

import java.util.*;
import java.sql.*;
import java.io.*;

import org.mmbase.module.database.*;
import org.mmbase.module.core.*;
import org.mmbase.util.*;
import org.mmbase.module.gui.html.*;

// import org.mmbase.hardware.linux.cdrom.*;

/**
 * @author Daniel Ockeloen
 * @version 12 Mar 1997
 */
public class CDTracks extends MMObjectBuilder {

	String diskid;
	int playtime;

	private String nocomma( String toconv ) { return toconv.replace(',','.'); }
	
	/**
	* insert a new object, normally not used (only subtables are used)
	*/
	/*
	public int insert(String owner,MMObjectNode node) {
		String title=node.getStringValue("title");			if( title != null ) title = nocomma( title ); 
		String subtitle=node.getStringValue("subtitle");	
		int trackNr=node.getIntValue("tracknr");
		int playtime=node.getIntValue("playtime");
		String intro=node.getStringValue("intro");
		String body=node.getStringValue("body");
		String discId=node.getStringValue("discid");
		int storage=node.getIntValue("storage");

		if (trackNr==-1 || trackNr==0) return(-1);
		if (subtitle==null) subtitle="";
		if (intro==null) intro="";
		if (body==null) body="";
		int number=getDBKey();
		if (number==-1) return(-1);
		try {
			MultiConnection con=mmb.getConnection();
			PreparedStatement stmt=con.prepareStatement("insert into "+mmb.baseName+"_"+tableName+" values(?,?,?,?,?,?,?,?,?,?,?)");
				stmt.setInt(1,number);
				stmt.setInt(2,oType);
				stmt.setString(3,owner);
				stmt.setString(4,title);
				stmt.setString(5,subtitle);
				stmt.setInt(6,trackNr);
				stmt.setInt(7,playtime);
				stmt.setString(8,intro);
				setDBText(9,stmt,body);
				stmt.setString(10,discId);
				stmt.setInt(11,storage);
				stmt.executeUpdate();
				stmt.close();
				con.close();
		} catch (SQLException e) {
			e.printStackTrace();
			System.out.println("CDTracks -> Error on : "+number+" "+owner+" fake");
			return(-1);
		}
		node.setValue("number",number);
		// RawAudios bul=(RawAudios)mmb.getMMObject("rawaudios");
		// changed for new cdplayers type
		//addRawAudio(bul,number,1,3,441000,2);   

		// add the realaudio and create a cdplayer node
		// move to insertDone
		signalNewObject(tableName,number);
		return(number);	
	}
	*/


	/*
	public int insertDone(EditState ed,MMObjectNode node) {
		String t=ed.getHtmlValue("source");
		int number=node.getIntValue("number");
		int trackNr=node.getIntValue("tracknr");
		if (trackNr==-1 || trackNr==0) return(-1);

		cdplayers bul=(cdplayers)mmb.getMMObject("cdplayers");
		Enumeration e=bul.search("WHERE name='"+t+"'");
		if (e.hasMoreElements()) {
			MMObjectNode node2=(MMObjectNode)e.nextElement();
			node2.setValue("state","record");
			node2.setValue("info","track="+trackNr+" id="+number);
			node2.commit();
		}
		return(number);
	}
	*/

	public String getGUIIndicator(String field,MMObjectNode node) {
		if (field.equals("storage")) {
			int val=node.getIntValue("storage");
			switch(val) {
				case 1: return("Stereo");
				case 2: return("Stereo geen backup");
				case 3: return("Mono");
				case 4: return("Mono geen backup");
				default: return("Onbepaald");
			}
		}
		return(null);
	}


	/**
	* get new node
	*/
	public MMObjectNode getNewNode(String owner) {
		MMObjectNode node=super.getNewNode(owner);
		// readCDInfo();
		if (diskid!=null) node.setValue("discid",diskid);
		if (playtime!=-1) node.setValue("playtime",playtime);
		return(node);
	}

	/**
 	 * executes the given command
	 * @return standard output
	 */
	/* mmbase
    private String readCDInfo() {
		Process p=null;
        String s="",tmp="";
		DataInputStream dip= null;
		diskid=null;
		int nrtracks=-1;
		playtime=-1;
		
		try {
        	String command="/export/home/wwwtech/mp3/bin/galette/galette -i";
			System.out.println("CDtracks -> Command ="+command);
			p = (Runtime.getRuntime()).exec(command,null);
			System.out.println("CDtracks -> Process = "+p);
			p.waitFor();
		} catch (Exception e) {
			s+=e.toString();
			return s;
		}
		dip = new DataInputStream(p.getErrorStream());
		System.out.println("CDtracks -> Dip = "+dip);
        try {
            while ((tmp = dip.readLine()) != null) {
				System.out.println("CDtracks -> line = "+tmp);
				if (tmp.indexOf("CDROM:")==0) {
					int pos=tmp.indexOf("disk id = ");
					if (pos!=-1) {
						diskid=tmp.substring(pos+10);
					} else if (tmp.indexOf("TOTAL")!=-1) {
						String tmp2=tmp.substring(39,48);
						try {
							int min=Integer.parseInt(tmp2.substring(0,2));
							int sec=Integer.parseInt(tmp2.substring(3,5));
							int msec=Integer.parseInt(tmp2.substring(6,8));
							System.out.println("CDtracks -> m="+min+" s="+sec+" ms="+msec);
							playtime=(min*60*1000)+(sec*1000)+msec;
						} catch (Exception e) {};
					} else {
						try {
							String tmp2=tmp.substring(9,15);
							tmp2=Strip.Whitespace(tmp2,Strip.BOTH);
							nrtracks=Integer.parseInt(tmp2);
						} catch (Exception e) {};
					}
				}
               	s+=tmp+"\n"; 
		}
        } catch (Exception e) {
			//s+=e.toString();
			return s;
	}
	return s;
	}
	*/

	public void wavAvailable(int id) {
		MMObjectNode node=getNode(id);
		int st=node.getIntValue("storage"); 
		if (st!=0) {
			RawAudios bul=(RawAudios)mmb.getMMObject("rawaudios");
			if (bul!=null) {
				if (st==1 || st==2) { // muziek
						addRawAudio(bul,id,1,2,16000,1);   
						addRawAudio(bul,id,1,2,32000,1);   
						addRawAudio(bul,id,1,2,40000,1);   
						addRawAudio(bul,id,1,2,40000,2);   
						addRawAudio(bul,id,1,2,80000,2);   
				}
			}
		}
	}


	public void pcmAvailable(String id) {
		MMObjectNode node=getNode(id);
		int st=node.getIntValue("storage"); 
		if (st!=0) {
			RawAudios bul=(RawAudios)mmb.getMMObject("rawaudios");
			if (bul!=null) {
				if (st==1) {
					try {
						int idi=Integer.parseInt(id);
						addRawAudio(bul,idi,1,5,192000,2);   
					} catch (Exception e) {
						System.out.println("CDtracks -> Wrong id in ParseInt");
					}
				}
			}
		}
	}


	public void addRawAudio(RawAudios bul,int id, int status, int format, int speed, int channels) {
		MMObjectNode node=bul.getNewNode("system");		
		node.setValue("id",id);
		node.setValue("status",status);
		node.setValue("format",format);
		node.setValue("speed",speed);
		node.setValue("channels",channels);
		bul.insert("system",node);
	}

	public String getDefaultUrl(int src) {
		return("/winkel/cd.shtml");
	}

	public void doWork(int number, String filename) {
	/* mmbase
		LiteOn242 cd=new LiteOn242();
		System.out.println("CDtracks -> Do Scan from cd : "+number+" to "+filename);
		cd.getTrack(number,filename);
		System.out.println("Scan done");
	*/
	}

	public void getTrack(int number,MMObjectNode caller) {
		MMObjectNode node=getNode(number);
		new CDTracksProbe(this,node.getIntValue("tracknr"),"/data/audio/wav/"+number+".wav",caller);
	}

	public Object getValue(MMObjectNode node,String field) {
		if (field.indexOf("short_")==0) {
			String val=node.getStringValue(field.substring(6));
			val=getShort(val,34);
			return(val);
		} else if (field.equals("shorted(title)")) {
			node.prefix="cdtracks.";
			String val=node.getStringValue("title");
			val=getShort(val,32);
			node.prefix="";
			return(val);
		}  else if (field.indexOf("html_")==0) {
			String val=node.getStringValue(field.substring(5));
			val=getHTML(val);
			return(val);
		} 
		return(null);
	}


	/**
	* setDefaults for a node
	*/
	public void setDefaults(MMObjectNode node) {
		node.setValue("storage",2);
		node.setValue("body","");
	}
}
