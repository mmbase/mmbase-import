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
import org.mmbase.cache.*;
import org.mmbase.module.core.*;
import org.mmbase.module.corebuilders.*;
import org.mmbase.applications.mmbob.*;

import org.mmbase.util.logging.Logging;
import org.mmbase.util.logging.Logger;

/**
 * @author Daniel Ockeloen
 * 
 */
public class ReplysHandler extends Handler {

    private int generatecount=0; 
    private int delaytime=0; 
    private String inforum;
    private String inpostarea;
 
    // logger
    static private Logger log = Logging.getLoggerInstance(ReplysHandler.class); 

    public ReplysHandler(int generatecount, int delaytime, String inforum,String inpostarea) {
	this.generatecount=generatecount;
	this.delaytime=delaytime;
	this.inforum=inforum;
	this.inpostarea=inpostarea;
	init();
    }

    public void run() {
	try {
	log.info("Run called on Replys Generate Thread");
	if (generatecount>0) {
        	Forum f=ForumManager.getForum(inforum);
		Enumeration posters=f.getPosters();
		Poster poster;
        	PostArea a=f.getPostArea(inpostarea);
		if (a!=null) {
			Enumeration threads=a.getPostThreads();
			PostThread postthread;
		
			int j = 0;	
			for (int i=0;i<generatecount;i++) {
				log.info("generate reply : "+i);
	
				// get next poster
				if (posters.hasMoreElements()) {
					poster=(Poster)posters.nextElement();
				} else {
					posters=f.getPosters();
					poster=(Poster)posters.nextElement();
				}

				// get next Thread
				if (threads.hasMoreElements()) {
					postthread=(PostThread)threads.nextElement();
				} else {
					threads=a.getPostThreads();
					postthread=(PostThread)threads.nextElement();
				}

				createReply(postthread,poster,i);

				j++;
				if (j>99) {
        				Cache cache = RelatedNodesCache.getCache();
				        cache.clear();
				        cache = NodeCache.getCache();
				        cache.clear();
				        cache = NodeCache.getCache();
				        cache.clear();
				        cache = MultilevelCache.getCache();
				        cache.clear();
				        cache = NodeListCache.getCache();
				        cache.clear();
					j=0;
				}
			}
		}
	}
	} catch(Exception e) {
		e.printStackTrace();
	}
	Generate.setState(0);
    }


    public boolean createReply(PostThread postthread,Poster poster,int i) {
	String subject=generateLine();
	String body=generateLines();
        postthread.postReply(subject,poster,body,false);
	return true;
    }

}
