/*

This software is OSI Certified Open Source Software.
OSI Certified is a certification mark of the Open Source Initiative.

The license (Mozilla version 1.0) can be read at the MMBase site.
See http://www.MMBase.org/license

*/

package org.mmbase.applications.mmbob.generate;

import java.lang.*;
import java.net.*;
import java.util.*;
import java.io.*;

import org.mmbase.util.*;
import org.mmbase.module.core.*;
import org.mmbase.module.corebuilders.*;
import org.mmbase.applications.mmbob.*;

import org.mmbase.util.logging.Logging;
import org.mmbase.util.logging.Logger;

/**
 * @author Daniel Ockeloen
 * 
 */
public class PostersHandler extends Handler {

    private int generatecount=0; 
    private int delaytime=0; 
    private String inforum;
 
    // logger
    static private Logger log = Logging.getLoggerInstance(PostersHandler.class); 
    public PostersHandler(int generatecount, int delaytime, String inforum) {
	this.generatecount=generatecount;
	this.delaytime=delaytime;
	this.inforum=inforum;
	init();
    }

    public void run() {
	try {
	log.info("Run called on Posters Generate Thread");
	if (generatecount>0) {
		for (int i=0;i<generatecount;i++) {
			if (!inforum.equals("all")) {
				log.info("generate poster : "+i);
				createPoster(inforum,i);
			}
		}
	}
	} catch(Exception e) {
		e.printStackTrace();
	}
	Generate.setState(0);
    }


    public boolean createPoster(String forumid,int i) {
		String firstname=generateFirstName();
		String lastname=generateSurName();
		String account=firstname+lastname.substring(0,1)+i;
		log.info("account="+account);
		String password=account;
		String email=firstname+lastname+"@"+generateProvider();
		String gender="male";
		String location=generatePlace();

		Forum f=ForumManager.getForum(forumid);
		if (f!=null) {
			Poster p=f.getPoster(account);
			if (p==null) {
				p=f.createPoster(account,password);	
				if (p!=null) {
					p.setFirstName(firstname);
					p.setLastName(lastname);
					p.setEmail(email);
					p.setGender(gender);
					p.setLocation(location);
					p.setPostCount(0);
					p.savePoster();
				} else {
					return false;
				}
			} else {
				return false;
			}
		}
		return true;
	}


}
