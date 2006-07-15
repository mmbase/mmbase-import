/*

This software is OSI Certified Open Source Software.
OSI Certified is a certification mark of the Open Source Initiative.

The license (Mozilla version 1.0) can be read at the MMBase site.
See http://www.MMBase.org/license

*/
package org.mmbase.module.builders;

import java.io.DataInputStream;

import org.mmbase.module.core.*;
import org.mmbase.util.*;
import org.mmbase.util.logging.*;

/**
 * @author Daniel Ockeloen
 * @version $Id: CDs.java,v 1.8 2003-03-10 11:50:16 pierre Exp $
 */
public class CDs extends MMObjectBuilder {
    private static Logger log = Logging.getLoggerInstance(CDs.class.getName());

	String diskid;
	int nrtracks;
	int playtime;

	public String getGUIIndicator(String field,MMObjectNode node) {
		if (field.equals("playtime")) {
			String str;
			int val=(node.getIntValue("playtime")/1000);
			int h=val/3600;val=val-(h*3600);
			int m=val/60;val=val-(m*60);
			if (m<10) { str="0"+h; } else { str=""+h; }
			if (m<10) { str+=":0"+m; } else { str+=":"+m; }
			if (val<10) { str+=":0"+val; } else { str+=":"+val; }
			return(str);			
		}
		return(null);
	}

	/**
	* get new node
	*/
	public MMObjectNode getNewNode(String owner) {
		MMObjectNode node=super.getNewNode(owner);
		// readCDInfo();
		if (diskid!=null) node.setValue("discId",diskid);
		if (nrtracks!=-1) node.setValue("nrTracks",nrtracks);
		if (playtime!=-1) node.setValue("playtime",playtime);
		return(node);
	}

	/**
 	 * executes the given command
	 * @return standard output
	 */
    private String readCDInfo() {
		Process p=null;
        String s="",tmp="";
		DataInputStream dip= null;
		diskid=null;
		nrtracks=-1;
		playtime=-1;
		
		try {
        	String command="/export/home/wwwtech/mp3/bin/galette/galette -i";
			log.service("CD -> Command ="+command);
			p = (Runtime.getRuntime()).exec(command,null);
			log.debug("CD -> Process = "+p);
			p.waitFor();
		} catch (Exception e) {
			s+=e.toString();
			return s;
		}
		dip = new DataInputStream(p.getErrorStream());
		log.debug("CD -> Dip = "+dip);
        try {
            while ((tmp = dip.readLine()) != null) {
				log.debug("CD -> line = "+tmp);
				if (tmp.indexOf("CDROM:")==0) {
					int pos=tmp.indexOf("disk id = ");
					if (pos!=-1) {
						diskid=tmp.substring(pos+10);
						log.debug("CD -> Disk ID="+diskid);
					} else if (tmp.indexOf("TOTAL")!=-1) {
						String tmp2=tmp.substring(39,48);
						try {
							int min=Integer.parseInt(tmp2.substring(0,2));
							int sec=Integer.parseInt(tmp2.substring(3,5));
							int msec=Integer.parseInt(tmp2.substring(6,8));
							log.debug("CD -> m="+min+" s="+sec+" ms="+msec);
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

	public String getDefaultUrl(int src) {
		return("/winkel/cd.shtml?"+src);
	}


}
