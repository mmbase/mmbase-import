/*

This software is OSI Certified Open Source Software.
OSI Certified is a certification mark of the Open Source Initiative.

The license (Mozilla version 1.0) can be read at the MMBase site.
See http://www.MMBase.org/license

*/
package org.mmbase.module.builders;

import java.util.*;
import java.sql.*;

import org.mmbase.module.database.*;
import org.mmbase.module.builders.vwms.*;
import org.mmbase.module.core.*;
import org.mmbase.util.*;

/**
 * @author Daniel Ockeloen
 * @version 12 Mar 1997
 */
public class NetFileSrv extends MMObjectBuilder {

	Hashtable service2bot=new Hashtable();

	public String getGUIIndicator(MMObjectNode node) {
        String str=node.getStringValue("service");
        str+="/"+node.getStringValue("subservice");
        if (str.length()>15) {
            return(str.substring(0,12)+"...");
        } else {
            return(str);
        }
	}

	public boolean fileChange(String number,String ctype) {
		try {
			debug(number+" "+ctype);
			MMObjectNode node=getNode(number);
			String service=node.getStringValue("service");
			String subservice=node.getStringValue("subservice");
			String servicestr=service+"/"+subservice;
			Object bot=service2bot.get(servicestr);
			if (bot!=null) {
				if (bot instanceof VwmServiceInterface){
					 ((VwmServiceInterface)bot).fileChange(number,ctype);
				} else {
					debug("Problem bot NOT a VwmServiceInterface !!!");
				}
			} else {
				// figure out the bot for this service/subservice
				bot=getAttachedBot(service,subservice);	
				if (bot!=null) {
					service2bot.put(servicestr,bot);
					if (bot instanceof VwmServiceInterface) {
						((VwmServiceInterface)bot).fileChange(number,ctype);
					} else {
						debug("Problem bot NOT a VwmServiceInterface !!!");
					}
				}
			}
		} catch (Exception e) {
			debug("Exception "+e);
			e.printStackTrace();
		}
		return(true);
	}


	public boolean fileChange(String service,String subservice,String filename) {
		String servicestr=service+"/"+subservice;
		Object bot=service2bot.get(servicestr);
		if (bot!=null) {
			if (bot instanceof VwmServiceInterface) ((VwmServiceInterface)bot).fileChange(service,subservice,filename);
		} else {
			// figure out the bot for this service/subservice
			bot=getAttachedBot(service,subservice);	
			if (bot!=null) {
				service2bot.put(servicestr,bot);
				if (bot instanceof VwmServiceInterface) ((VwmServiceInterface)bot).fileChange(service,subservice,filename);
			}
		}
		return(true);
	}

	public Object getAttachedBot(String service,String subservice) {
		Enumeration e=search("WHERE service='"+service+"' AND subservice='"+subservice+"'");
		while (e.hasMoreElements()) {
			MMObjectNode node=(MMObjectNode)e.nextElement();
			int number=node.getIntValue("number");
			Enumeration f=mmb.getInsRel().getRelated(""+number,"vwms");
			while (f.hasMoreElements()) {
				MMObjectNode vwmnode=(MMObjectNode)f.nextElement();
				Vwms vwms=(Vwms)mmb.getMMObject("vwms");	
				if (vwms!=null) {
					String name=vwmnode.getStringValue("name");
					VwmServiceInterface vwm=(VwmServiceInterface)vwms.getVwm(name);
					if (vwm!=null) {
						return(vwm);
					}
				}
			
			}	
		}
		return(new Object());
	}

}
