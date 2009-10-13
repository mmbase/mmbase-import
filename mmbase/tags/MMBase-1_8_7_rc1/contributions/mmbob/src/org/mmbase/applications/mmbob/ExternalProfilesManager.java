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
    /**
     * the ProfileInfo objects that go into this queue are being used to synch the 
     * external profile with. the values from these objects are copied into the external profile.
     */
    static private ArrayList queue = new ArrayList();
    /**
     * the ProfileInfo objects that go into this queue are being synced to the external
     * profile. the external values are copied into these objects.
     */
    static private ArrayList checkqueue = new ArrayList();

    // thread
    Thread kicker = null;

    int sleeptime;

    /**
     */
    public ExternalProfilesManager(int sleeptime) {
        this.sleeptime = sleeptime;
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
            kicker = new Thread(this, "externalprofilemanager");
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
    public void run() {
        kicker.setPriority(Thread.MIN_PRIORITY + 1);
        while (kicker != null) {
            try {
                doWork();
            } catch (Exception e) {
                log.error("run(): ERROR: Exception in externalprofilemanager thread!");
                log.error(Logging.stackTrace(e));
                try {
                    Thread.sleep(sleeptime);
                } catch (Exception f3) {}
            }
        }
    }

    /**
     * Main work loop
     * first it iterates over the NodeInfo instances of the 'queue' queue, and tries to synchronize the
     * external profile with the NodeInfo instance. 
     * 
     * than it iterates over all the ProfileInfo instances in the checkQueue queue and tries to synchronize the
     * values of the nodeInfo object with the relevant ExternalProfileInterface, and the profileInfo 
     * objects that are changed are saved.
     * 
     * 
     */
    public void doWork() {
        kicker.setPriority(Thread.MIN_PRIORITY + 1);

        while (kicker != null) {
            try {
                syncQueues();
                Thread.sleep(sleeptime);
            } catch (Exception f2) {
                log.error("External profile sync error");
                f2.printStackTrace();
                try {
                    Thread.sleep(sleeptime);
                } catch (Exception f3) {}
            }
        }
    }

    /**
     * this method dous the actual syncing work
     */
    void syncQueues() {
        //copy the values of these profileInfo objects into the external profile (set)
        List fields = new ArrayList();
        while (!queue.isEmpty()) {
            ProfileInfo profileInfo = (ProfileInfo) queue.get(0);
            log.debug("For user "+profileInfo.getAccount()+" the profileInfo entries are copied to the external profile ");
            
            Iterator i = profileInfo.getValues();
            while (i.hasNext()) {
                ProfileEntry profileEntry = (ProfileEntry) i.next();
                ProfileEntryDef profileEntryDef = profileInfo.getProfileDef(profileEntry.getName());
                String external = profileEntryDef.getExternal();
                String externalname = profileEntryDef.getExternalName();
                ExternalProfileInterface profileInterface = ExternalProfilesManager.getHandler(external);
                
                log.debug("name: "+profileEntry.getName()+",external: "+external+", externalname: "+externalname+", profile entry synched:"+profileEntry.getSynced());
                
                //syncing is only done for profileEntrieDefs with an external, that is out of synch and is editable
                if (external != null && !external.equals("") && !profileEntry.getSynced() && profileEntryDef.getEdit()) {
                    fields.add(profileEntry.getName());
                    String account = profileInfo.getAccount();
                    
                    if (externalname != null && !externalname.equals("")) {
                        profileInterface.setValue(account, externalname, profileEntry.getValue());
                    } else {
                        profileInterface.setValue(account, profileEntry.getName(), profileEntry.getValue());
                    }
                    profileEntry.setSynced(true);
                }
            }
            profileInfo.setSynced(true);
            log.debug("fields that were synched: "+fields.toString());
            if(fields.size() > 0){
                profileInfo.save();
            }
            queue.remove(profileInfo);
        }
        
        //copy the values from the external profiles into these ProfileInfo's (get)
        fields.clear();
        while (!checkqueue.isEmpty()) {
            ProfileInfo profileInfo = (ProfileInfo) checkqueue.get(0);
            log.debug("For user "+profileInfo.getAccount()+"  the external profile values are copied into it's profileInfo object.");
            Iterator i = profileInfo.getValues();
            while (i.hasNext()) {
                ProfileEntry profileEntry = (ProfileEntry) i.next();
                ProfileEntryDef profileEntryDef = profileInfo.getProfileDef(profileEntry.getName());
                //we only synch the profile entry defs that actually have an external (class)
                if (profileEntryDef != null && profileEntryDef.getExternal() != null) {
                    
                    String external = profileEntryDef.getExternal();
                    String externalname = profileEntryDef.getExternalName();
                    ExternalProfileInterface handler = ExternalProfilesManager.getHandler(external);
                    log.debug("name: "+profileEntry.getName()+",external: "+external+", externalname: "+externalname+", profile entry synched:"+profileEntry.getSynced() );

                    //we ALWAYS want to sync the profile to the external profile. if there is an
                    //'external' for an profileentry we MUST assume it is leading, and if it has chaned sinds
                    //the last time the user logged into the forum we want to update the profile with those changes
                    //if this is not always the case then profileentrydef must be extended with a swicht external leading/not leading.
                    //if (!profileEntry.getSynced()) {
                        //sync the field
                        String account = profileInfo.getAccount();
                        String fieldName = ((externalname != null && !externalname.equals("")) ? externalname : profileEntry.getName());
                        
                        //no external name: sync the field with the (local) name
                        String value = handler.getValue(account,fieldName);
                        if (value != null && !value.equals(profileEntry.getValue())) {
                            fields.add(profileEntry.getName());
                            profileEntry.setValue(value);
                            profileEntry.setSynced(true);
                        }
                    //}
                }
            }
            profileInfo.setSynced(true);
            if(fields.size() > 0 ){
                profileInfo.save();
            }
            log.debug("fields that were synched: "+fields.toString());
            checkqueue.remove(profileInfo);
        }
    }

    /**
     * ProfielInfo objects that go here are being used to sync the external profile with.
     * (out queueu)
     * @param profileInfo
     */
    static public void addToSyncQueue(ProfileInfo profileInfo) {
        if (!queue.contains(profileInfo)){
            queue.add(profileInfo);
            log.debug("adding profileinfo node to queue for "+profileInfo.getAccount());
        }else{
            log.debug("dont' add profileinfo node to queue for "+profileInfo.getAccount());
        }
    }

    /**
     * ProfielInfo objects that go here are being synched to the external profile.
     * (in queue)
     * @param profileInfo
     */
    static public void addToCheckQueue(ProfileInfo profileInfo) {
        if (!checkqueue.contains(profileInfo)){
            log.debug("adding profileinfo node to check queue for "+profileInfo.getAccount());
            checkqueue.add(profileInfo);
        }else{
            log.debug("dont' add profileinfo node to check queue for "+profileInfo.getAccount());
        }
    }

    static public ExternalProfileInterface getHandler(String name) {
        return (ExternalProfileInterface) handlers.get(name);
    }

    /**
     * This method iterates over every profileEntryDef in a given forum and tries to 
     * creates an ExternalProfileInterface instance for it based on it's property 'external'
     * the instance is added to the 'handlers' collection
     * @param forum
     */
    public static void loadExternalHandlers(Forum forum) {
//        try {
//            Class newclass = Class.forName("org.apache.commons.logging.LogFactory");
//        } catch (Exception r) {}
        Iterator pdi = forum.getProfileDefs();
        if (pdi != null) {
            while (pdi.hasNext()) {
                ProfileEntryDef pd = (ProfileEntryDef) pdi.next();
                String external = pd.getExternal();
                String externalname = pd.getExternalName();
                if (external != null && !external.equals("")) {
                    if (!handlers.containsKey(external)) {
                        try {
                            Class newclass = Class.forName(external);
                            ExternalProfileInterface h = (ExternalProfileInterface) newclass.newInstance();
                            handlers.put(external, h);
                        } catch (Exception r) {
                            log.error("Can't create handler: " + external);
                        }
                    }
                }
            }
        }
    }

}
