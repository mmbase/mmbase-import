/*

This software is OSI Certified Open Source Software.
OSI Certified is a certification mark of the Open Source Initiative.

The license (Mozilla version 1.0) can be read at the MMBase site.
See http://www.MMBase.org/license

*/

package org.mmbase.applications.email;

import java.lang.*;
import java.net.*;
import java.util.*;
import java.io.*;
import javax.naming.*;
import javax.mail.*;
import javax.mail.internet.*;
import javax.activation.*;

import org.mmbase.module.database.*;
import org.mmbase.module.core.*;
import org.mmbase.util.*;

import org.mmbase.util.logging.Logging;
import org.mmbase.util.logging.Logger;

/**
 * @author Daniel Ockeloen
 * 
 */
public class MimeMessageGenerator {

    // logger
    private static Logger log = Logging.getLoggerInstance(MimeMessageGenerator.class.getName());



    public static MimeMultipart getMimeMultipart(String text) {
        Hashtable nodes=new Hashtable();
        Vector rootnodes=new Vector();


	Enumeration tags=MimeBodyTagger.getMimeBodyParts(text);
	while (tags.hasMoreElements()) {
		try {
		MimeBodyTag tag=(MimeBodyTag)tags.nextElement();

		// get all the needed fields
		String type=tag.getType();
		String id=tag.getId();
		String related=tag.getRelated();
		String alt=tag.getAlt();

		// add it to the id cache
		nodes.put(id,tag);

		// is it a root node ?
		if (alt==null && related==null) {
			rootnodes.addElement(tag);
		} else if (alt!=null) {
			MimeBodyTag oldpart=(MimeBodyTag)nodes.get(alt);
			if (oldpart!=null) {
				oldpart.addAlt(tag);
			}
		} else if (related!=null) {
			MimeBodyTag oldpart=(MimeBodyTag)nodes.get(related);
			if (oldpart!=null) {
				oldpart.addRelated(tag);
			}
		}	

	   } catch(Exception e) {
		log.error("Mime mail error");
	   }
	}

	if (rootnodes.size()==1) {
		MimeBodyTag t=(MimeBodyTag)rootnodes.elementAt(0);
		MimeMultipart mmp=t.getMimeMultipart();
		if (mmp!=null) {
			return(mmp);
		}
	} else if (rootnodes.size()>1) {
		try {
		MimeMultipart root = new MimeMultipart();
		root.setSubType("mixed");
		Enumeration l=rootnodes.elements();
		while (l.hasMoreElements()) {
			MimeBodyTag t=(MimeBodyTag)l.nextElement();
			MimeMultipart mmp=t.getMimeMultipart();
			if (mmp!=null) {
				log.info("setting parent info : "+t.getId());
				MimeBodyPart wrapper=new MimeBodyPart();
				wrapper.setContent(mmp);
				root.addBodyPart(wrapper);
			} else {
				log.info("adding info : "+t.getId());
				root.addBodyPart(t.getMimeBodyPart());
			}
		}
		return root;
		} catch(Exception e) {
			log.error("Root generation error");
			e.printStackTrace();
		}
	} else {
		log.error("Don't have a root node");
	}
	return(null);
    }

}
