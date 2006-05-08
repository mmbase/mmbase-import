/*

This software is OSI Certified Open Source Software.
OSI Certified is a certification mark of the Open Source Initiative.

The license (Mozilla version 1.0) can be read at the MMBase site.
See http://www.MMBase.org/license

*/

package org.mmbase.applications.mmbob;

import java.lang.*;
import java.net.*;
import java.util.*;
import java.io.*;

import org.mmbase.module.database.*;
import org.mmbase.module.core.*;
import org.mmbase.util.*;

import org.mmbase.util.logging.Logging;
import org.mmbase.util.logging.Logger;

/**
 * @author Daniel Ockeloen
 *
 */
public class ExternalProfilesManager implements Runnable {

    // logger
    static private Logger log = Logging.getLoggerInstance(ExternalProfilesManager.class); 

    static HashMap handlers = new HashMap();
    static private ArrayList queue = new ArrayList();
    static private ArrayList checkqueue = new ArrayList();

    // thread
    Thread kicker = null;

    int sleeptime;

    /**
    */
    public ExternalProfilesManager(int sleeptime) {
        this.sleeptime=sleeptime;
	init();
    }

    /**
    * init()
    */
    public void init() {
        this.start();    
    }


    /**
     * Starts the main Thread.
     */
    public void start() {
        /* Start up the main thread */
        if (kicker == null) {
            kicker = new Thread(this,"externalprofilemanager");
            kicker.start();
        }
    }
    
    /**
     * Stops the main Thread.
     */
    public void stop() {
        /* Stop thread */
        kicker = null;
    }

    /**
     * Main loop, exception protected
     */
    public void run () {
        kicker.setPriority(Thread.MIN_PRIORITY+1);  
        while (kicker!=null) {
            try {
                doWork();
            } catch(Exception e) {
                log.error("run(): ERROR: Exception in externalprofilemanager thread!");
                log.error(Logging.stackTrace(e));
	         try {
			Thread.sleep(sleeptime);
		} catch(Exception f3) {}
            }
        }
    }

    /**
     * Main work loop
     */
    public void doWork() {
        kicker.setPriority(Thread.MIN_PRIORITY+1);  

        while (kicker!=null) {
		try {
			while (!queue.isEmpty()) {
				ProfileInfo pi = (ProfileInfo)queue.get(0);
				Iterator i=pi.getValues();
				while (i.hasNext()) {
					ProfileEntry pe = (ProfileEntry)i.next();
					ProfileEntryDef pd = pi.getProfileDef(pe.getName());
					String external = pd.getExternal();
					String externalname = pd.getExternalName();
	                                ExternalProfileInterface ci = ExternalProfilesManager.getHandler(external);
                                	if (externalname!=null && !externalname.equals("") && !pe.getSynced()) {
                                	//	String account = parent.getAccount();

						String account = pi.getAccount();
						if (externalname!=null && !externalname.equals("")) {
							boolean result = ci.setValue(account,externalname,pe.getValue());
						} else {
							boolean result = ci.setValue(account,pe.getName(),pe.getValue());
						}
						pe.setSynced(true);
						pi.setSynced(true);
						pi.save();
					}
				}
				queue.remove(pi);
			}
			while (!checkqueue.isEmpty()) {
				ProfileInfo pi = (ProfileInfo)checkqueue.get(0);
				Iterator i=pi.getValues();
				while (i.hasNext()) {
					ProfileEntry pe = (ProfileEntry)i.next();
					ProfileEntryDef pd = pi.getProfileDef(pe.getName());
					if (pd!=null) {
					String external = pd.getExternal();
					String externalname = pd.getExternalName();
	                                ExternalProfileInterface ci = ExternalProfilesManager.getHandler(external);
                                	if (externalname!=null && !externalname.equals("") && !pe.getSynced()) {

						String account = pi.getAccount();
						if (externalname!=null && !externalname.equals("")) {
							String value = ci.getValue(account,externalname);
							if (value!=null && !value.equals(pe.getValue())) {
								pe.setValue(value);
								pe.setSynced(true);
								pi.setSynced(true);
								pi.save();
							}
						} else {
							String value = ci.getValue(account,pe.getName());
							if (value!=null && !value.equals(pe.getValue())) {
								pe.setValue(value);
								pe.setSynced(true);
								pi.setSynced(true);
								pi.save();
							}
						}
					}
					}
				}
				checkqueue.remove(pi);
			}
	            	Thread.sleep(sleeptime);
		} catch (Exception f2){
			log.info("External profile sync error");
			f2.printStackTrace();
	            	try {
				Thread.sleep(sleeptime);
			} catch(Exception f3) {}
		}
        }
    }

    static public void addToSyncQueue(ProfileInfo pi) {
	if (!queue.contains(pi)) queue.add(pi);
    }

    static public void addToCheckQueue(ProfileInfo pi) {
	if (!checkqueue.contains(pi)) checkqueue.add(pi);
    }

    static public ExternalProfileInterface getHandler(String name) {
	return (ExternalProfileInterface)handlers.get(name);
    }

    public static void loadExternalHandlers(Forum f) {
       	try {
              	Class newclass = Class.forName("org.apache.commons.logging.LogFactory");    
             } catch (Exception r) {
	}
        Iterator pdi=f.getProfileDefs();
	if (pdi!=null) {
        while (pdi.hasNext()) {
           	ProfileEntryDef pd = (ProfileEntryDef)pdi.next();
               	String external = pd.getExternal();
               	String externalname = pd.getExternalName();
		if (external!=null && !external.equals("")) {
			if (!handlers.containsKey(external)) {
               	     		try {
                          		Class newclass = Class.forName(external);    
                            		ExternalProfileInterface h = (ExternalProfileInterface)newclass.newInstance(); 
					handlers.put(external,h);
                       		} catch (Exception r) {
                           		log.error("Can't create handler: "+external);
				}
			}
		}
	}
	}
    }

}
