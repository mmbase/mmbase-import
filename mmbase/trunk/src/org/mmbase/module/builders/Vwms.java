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
import org.mmbase.module.core.*;
import org.mmbase.module.builders.vwms.*;
import org.mmbase.util.*;

/**
 * @author Arjan Houtman
 * @author Rico Jansen
 */
public class Vwms extends MMObjectBuilder implements MMBaseObserver {
	Hashtable vwm_cache = new Hashtable ();
	private String emailFromDomain;
	private String emailReturnPath;
	private String emailTo;

	public boolean init () {
		super.init ();

		// get email config and check it
   		emailFromDomain = getInitParameter("fromdomain");
		if (emailFromDomain == null || emailFromDomain.equals("")) debug (" missing init param from");
		if(emailFromDomain.equals("@yourcompany.nl")) 
			debug (" fromdomain init parameter is still default, please change!!!!");
		emailReturnPath = getInitParameter("returnpath");
		if (emailReturnPath == null || emailReturnPath.equals("")) debug (" missing init param returnpath");
		if(emailReturnPath.equals("youremail@yourcompany.nl")) 
			debug (" returnpath init parameter is still default, please change!!!!");
		emailTo = getInitParameter("to");
		if (emailTo == null || emailTo.equals("")) debug ("missing init param subject");
 		if(emailTo.equals("youremail@yourcompany.nl")) 
			debug (" to init parameter is still default, please change!!!!");
		return (true);
	}

	public String getGUIIndicator (String field, MMObjectNode node) {
		if (field.equals ("status")) {
			int val = node.getIntValue ("status");
			if (val==1) { 
				return("inactive");
			} else if (val==2) {
				return("active");
			} else if (val==3) {
				return("refresh");
			} else {
				return ("unknown");
			}
		}
		return (null);
	}

	public void startVwmsByField() {
		Class newclass;
		System.out.println("Vwms on machine "+getMachineName());
		Enumeration e=search("WHERE (wantedcpu='"+getMachineName()+"' OR wantedcpu='*') AND status=2");
		for (;e.hasMoreElements();) {
			MMObjectNode node=(MMObjectNode)e.nextElement();
			System.out.println("Vwms -> VWM="+node);
			String name = node.getStringValue("name");
			String classname=node.getStringValue("classname");
			try {
				System.out.println("Vwms -> Trying to create bot : "+name+" classname "+classname);
				newclass=Class.forName(classname);
				System.out.println("Vwms -> Loaded load class : "+newclass);
				VwmInterface vwm = (VwmInterface)newclass.newInstance();
				vwm.init(node,this);
				vwm_cache.put(name,vwm);
			} catch (Exception f) {
				System.out.println("Vwms -> Can't load class : "+name);
			}
		}
	}


	public void startVwms() {
		Class newclass;
		// try to find my own node
		System.out.println("Vwms on machine "+getMachineName());
		MMObjectBuilder bul=mmb.getMMObject("mmservers");
		Enumeration e=bul.search("WHERE name='"+getMachineName()+"'");
		if (e.hasMoreElements()) {
			MMObjectNode node=(MMObjectNode)e.nextElement();
			Enumeration f=mmb.getInsRel().getRelated(node.getIntValue("number"),"vwms");
			for (;f.hasMoreElements();) {
				MMObjectNode vwmnode=(MMObjectNode)f.nextElement();
				System.out.println("Vwms -> VWM="+vwmnode);
				String name = vwmnode.getStringValue("name");
				String classname=vwmnode.getStringValue("classname");
				try {
					System.out.println("Vwms -> Trying to create bot : "+name+" classname "+classname);
					newclass=Class.forName(classname);
					System.out.println("Vwms -> Loaded load class : "+newclass);
					VwmInterface vwm = (VwmInterface)newclass.newInstance();
					vwm.init(vwmnode,this);
					vwm_cache.put(name,vwm);
				} catch (Exception g) {
					System.out.println("Vwms -> Can't load class : "+name);
				}
			}
		}
	}

	public boolean putTask(String vwmname, MMObjectNode node) {
		Vwm vwm=(Vwm)vwm_cache.get(vwmname);
		if (vwm!=null) {
			vwm.putTask(node);
			return(true);
		} else {
			System.out.println("Vwms : Could not find VWM : "+vwmname);
			return(false);
		}
	}

	public boolean sendMail(String who,String subject, String msg) {
		//using default to mailadres
		return(sendMail(who,emailTo,subject,msg));
	}



	public boolean sendMail(String who,String to,String subject, String msg) {
		String from="vwm_"+who+emailFromDomain;
		Mail mail=new Mail(to,from);
		mail.setSubject("Mail van VWM : "+who+ " : "+subject);
		mail.setDate();
		mail.setReplyTo(emailReturnPath); // should be from

		if (msg!=null && msg.length()>0) {
			mail.setText(msg);
		} else {
			mail.setText(subject);
		}
		if (mmb.getSendMail().sendMail(mail)==false) {
			System.out.println("vwms -> mail failed");
			return(false);
		} else {
			System.out.println("vwms -> mail send");
			return(true);
		}
	}

	public VwmInterface getVwm(String vwmname) {
		VwmInterface vwm=(VwmInterface)vwm_cache.get(vwmname);
		if (vwm!=null) {
			return(vwm);
		} else {
			return(null);
		}
	}

    public boolean nodeRemoteChanged(String number,String builder,String ctype) {
        super.nodeRemoteChanged(number,builder,ctype);
		if (ctype.equals("c")) {
			MMObjectNode node=getNode(number);
			if (node!=null) {
				String name=node.getStringValue("name");
				if (name!=null) {
					VwmInterface vwm=getVwm(name);
					if (vwm!=null) {
						vwm.nodeRemoteChanged(number,builder,ctype);
					}
				}
			}
		}
		return(true);
	}

    public boolean nodeLocalChanged(String number,String builder,String ctype) {
        super.nodeLocalChanged(number,builder,ctype);
		if (ctype.equals("c")) {
			MMObjectNode node=getNode(number);
			if (node!=null) {
				String name=node.getStringValue("name");
				if (name!=null) {
					VwmInterface vwm=getVwm(name);
					if (vwm!=null) {
						vwm.nodeLocalChanged(number,builder,ctype);
					}
				}
			}
		}
		return(true);
	}

}
