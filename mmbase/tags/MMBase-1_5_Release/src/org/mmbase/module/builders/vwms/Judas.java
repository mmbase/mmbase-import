/*

This software is OSI Certified Open Source Software.
OSI Certified is a certification mark of the Open Source Initiative.

The license (Mozilla version 1.0) can be read at the MMBase site.
See http://www.MMBase.org/license

*/
package org.mmbase.module.builders.vwms;

import java.util.*;
import java.io.*;

import org.mmbase.module.database.*;
import org.mmbase.module.core.*;
import org.mmbase.util.*;
import org.mmbase.module.builders.*;
import org.mmbase.module.corebuilders.*;
import org.mmbase.module.builders.Properties;
import org.mmbase.util.logging.*;

/**
 * A VWM which determines which urls need to be reloaded (i.e. for caching).
 * Part of the descisionmaking is based on periodic maintenance, part is based on relating
 * how changes on nodes cause a change in a page.
 * Both methods are currently heavily stuffed with code specific to the VPRO (hardcoded urls and builders).
 *
 * @author Daniel Ockeloen
 * @author Rico Jansen
 * @author Pierre van Rooden (javadocs)
 * @version 10 Apr 2001
 */

public class Judas extends Vwm implements MMBaseObserver {
    // logger
    private static Logger log = Logging.getLoggerInstance(Judas.class.getName());
    /**
     * Thread that schedules reloads of urls.
     */
    private JudasURLpusher pusher=null;
    /**
     * Used to start initalization of the VWM during the first probeCall.
     */
    private boolean firstProbeCall = true;

    /**
     * Constructor for Judas
     */
    public Judas() {
        log.info("VWM Judas started");
    }

    /**
     * performTask: This method is called when a vwmtask activates.
     * The task that needs to be performed is determined by the tasks' name field.
     * @param taskNode The current Vwmtasks node.
     * @return <code>true</code> when the task can be found and task execution succeeds, <code>false</code> otherwise
     */
    public boolean performTask(MMObjectNode taskNode) {
        String curTime = DateSupport.date2string((int)(DateSupport.currentTimeMillis()/1000));
        log.info("performTask: Vwm Judas performing another task, it is now:"+curTime);

        // Sets tasknode state to claimed.
        claim(taskNode);
        String taskName=taskNode.getStringValue("task");
        if (taskName.equals("ReloadFiles")) {
            log.service("performTask: Performing task: "+taskName);
            try {
                boolean success = performReloadFiles(taskNode);
                if (!success)
                    log.warn("Judas:performTask: Warning: performReloadFiles returned false!");
                // Set tasknode state to finished.
                performed(taskNode);
                return true;
            } catch (Exception e) {
                log.error("Judas:performTask: Perform "+taskName+" exception"+e);
                log.error(Logging.stackTrace(e));
                return false;
            }
        } else if (taskName.equals("ReloadJukeboxes")) {
            log.service("performTask: Performing task: "+taskName);
            // 'ReloadJukeboxes' has the same implementation as 'ReloadFiles', this is correct.
            // The difference in taskname is required so this tasknode can be reused which happens in probeCall.
            try {
                boolean success = performReloadFiles(taskNode);
                if (!success)
                    log.warn("Judas:performTask: Warning: performReloadFiles returned false!");
                // Set tasknode state to finished.
                performed(taskNode);
                return true;
            } catch (Exception e) {
                log.error("Judas:performTask: Perform "+taskName+" exception"+e);
                log.error(Logging.stackTrace(e));
                return false;
            }
        } else {
            log.warn("Judas:performTask: Don't know task with name: "+taskName);
            return false;
        }
    }

    /**
     * performReloadFiles: This method retrieves gets all urls objects related with this vwmtask and
     * adds them to judas' general URL vector.
     * The syntax for the data field should look like: url=/3voor12/jukeboxes/jukeboxes.shtml?123+456
     * @param taskNode The current vwmtask node.
     * @return A boolean value which is yes when retrieving and adding succeeded, else false.
     */
    private boolean performReloadFiles(MMObjectNode taskNode) {
        String url;
        int taskNumber = taskNode.getIntValue("number");
        MMObjectNode urlsNode;
        Enumeration e = Vwms.mmb.getInsRel().getRelated(taskNumber,"urls");
        if (e.hasMoreElements()) {
            while (e.hasMoreElements()) {
                urlsNode = (MMObjectNode)e.nextElement();
                url = urlsNode.getStringValue("url");
                addURL(url);
                log.info("performReloadFiles: adding url: "+url);
            }
            return true;
        } else {
            log.debug("performReloadFiles: No urls related with task "+taskNode.getStringValue("task")+" nr:"+taskNumber);
            return false;
        }
    }

    /**
    * probeCall: This method is called every x minutes where x is the maintenancetime field of the vwm.
    * The maintanancetime for this bot HAS TO BE 1 hour=3600 sec for the 3voor12 jukeboxes reload.
    * @return Always returns 'true'.
    */
    public boolean probeCall() {
        if (pusher==null) {
            pusher=new JudasURLpusher(this);
            log.info("Judas -> Starting URL pusher");
        }

        if (firstProbeCall) {
            firstProbeCall = false;
            log.debug("Judas -> Adding observers");

            // add news for 3voor12
            Vwms.mmb.addLocalObserver("news",this);
            Vwms.mmb.addRemoteObserver("news",this);

            // add audioparts
            Vwms.mmb.addLocalObserver("audioparts",this);
            Vwms.mmb.addRemoteObserver("audioparts",this);

            // add episodes
            Vwms.mmb.addLocalObserver("episodes",this);
            Vwms.mmb.addRemoteObserver("episodes",this);

            // add groups
            Vwms.mmb.addLocalObserver("groups",this);
            Vwms.mmb.addRemoteObserver("groups",this);

            // add mmevents
            Vwms.mmb.addLocalObserver("mmevents",this);
            Vwms.mmb.addRemoteObserver("mmevents",this);

            // add items
            Vwms.mmb.addLocalObserver("items",this);
            Vwms.mmb.addRemoteObserver("items",this);

            // add people
            Vwms.mmb.addLocalObserver("people",this);
            Vwms.mmb.addRemoteObserver("people",this);

            // add guideart
            Vwms.mmb.addLocalObserver("guideart",this);
            Vwms.mmb.addRemoteObserver("guideart",this);
        }

        // davzev: 3voor12 Jukeboxes reuses a specific vwmtask.
        // The maintanancetime for this bot HAS TO BE 1 hour = > 3600 sec.
        boolean success = false;
        success = reuseReloadJukeboxesTask();
        if (!success) {
            log.debug("probeCall: Error reuseReloadJukeboxesTask returned false");
        }
        return(true);
    }

    /**
     * reuseReloadJukeboxesTask: This method reuses a vwmtask named 'ReloadJukeboxes'.
     * Reuse is done by setting the task state from DONE to REQUEST and committing it again.
     * This is a task with a specific set of Jukebox urls related to it. The task is only written
     * out again when the current date is equal to the date information set in the tasknodes' data field.
     * @return A boolean which is true is task was found and false otherwise.
     */
    private boolean reuseReloadJukeboxesTask() {
        String methodName = "reuseReloadJukeboxesTask";
        String TASK = "ReloadJukeboxes";
        String MACHINE = "twohigh";
        int STATE_REQUEST = 1;
        int STATE_DONE = 3;

        // A task should only be written out when current date info is equal to what's stored in 'data' field.
        int curtime = (int)(DateSupport.currentTimeMillis()/1000);
        TimeZone tz = TimeZone.getTimeZone("ECT");
        GregorianCalendar calendar = new GregorianCalendar(tz);
        int curDayOfWeek = calendar.get(Calendar.DAY_OF_WEEK);
        int curHourOfDay = calendar.get(Calendar.HOUR_OF_DAY);

        // Find the first 'ReloadJukeboxes' task performed by this bot on that machine that has state 'done'.
        Vwmtasks vwmtask = (Vwmtasks)Vwms.mmb.getMMObject("vwmtasks");
        Enumeration e= vwmtask.searchWithWhere("task='"+TASK+"' AND vwm='"+getName()+"' AND wantedcpu='"+MACHINE+"' AND status='"+STATE_DONE+"'");
        if (e.hasMoreElements()) {
            log.debug("probeCall: Reusing 'ReloadJukeboxes' task.");
            MMObjectNode taskNode = (MMObjectNode) e.nextElement();

            // Get date information from nodes' data field.
            String dataField = taskNode.getStringValue("data");
            Hashtable props = parseProperties(dataField);
            int dayOfWeek = Integer.parseInt((String)props.get("DAY_OF_WEEK"));
            int hourOfDay = Integer.parseInt((String)props.get("HOUR_OF_DAY"));

            if ((curDayOfWeek == dayOfWeek) && (curHourOfDay == hourOfDay)) {
                log.debug(methodName+": dayOfWeek:"+dayOfWeek+" hourOfDay:"+hourOfDay+" -> writting out task again.");
                // The task will actually start ten minutes later.
                int starttime = (int) ((DateSupport.currentTimeMillis()/1000) + 10*60);
                taskNode.setValue("wantedtime",starttime);
                taskNode.setValue("expiretime",starttime + 50*60);
                taskNode.setValue("status",STATE_REQUEST);
                taskNode.commit();
                return true;
            } else {
                log.debug(methodName+": It isn't time yet.");
                return true;
            }
        } else {
            log.error("Judas: Error can't find task "+TASK);
            return false;
        }
    }


    public boolean nodeRemoteChanged(String machine,String number,String builder,String ctype) {
        return(nodeChanged(machine,number,builder,ctype));
    }

    public boolean nodeLocalChanged(String machine,String number,String builder,String ctype) {
        return(nodeChanged(machine,number,builder,ctype));
    }

    public boolean nodeChanged(String machine,String number,String builder, String ctype) {
        log.debug("Judas -> sees that a "+builder+" "+number+" has changed type="+ctype+" by machine:"+machine);

        // check if its a valid track in the cache
        int inumber=-1;
        try {
            inumber=Integer.parseInt(number);
        } catch(Exception e) {}


        // checks for 3voor12
        if (builder.equals("news")) {
            get3voor12_zapcentral(inumber,ctype);
            get3voor12_journalism(inumber,ctype);
        } else if (builder.equals("episodes")) {
            get3voor12_shows(inumber,ctype);
        } else if (builder.equals("audioparts")) {
            get3voor12_audioparts_shows(inumber,ctype);
            get3voor12_audioparts_month(inumber,ctype);
            get3voor12_audioparts_categorie(inumber,ctype);
            get3voor12_audioparts_tracks(inumber,ctype);
            get3voor12_zapcentral(inumber,ctype);
        } else if (builder.equals("groups")) {
            get3voor12_groups(inumber,ctype);
            get3voor12_zapcentral(inumber,ctype);
        } else if (builder.equals("mmevents")) {
            get3voor12_zapcentral(inumber,ctype);
        }

        // checks for programs/episodes
        if (builder.equals("episodes")) {
            getEpisodes(inumber);
        } else if (builder.equals("items")) {
            getItems(inumber);
        } else if (builder.equals("people")) {
            getPeople(inumber);
        }

        return(true);
    }

    public void getEpisodes(int number) {
        Vector tables=new Vector();
        tables.addElement("programs");
        tables.addElement("episodes");
        Vector fields=new Vector();
        fields.addElement("programs.number");
        fields.addElement("episodes.number");
        Vector ordervec=new Vector();
        Vector dirvec=new Vector();


        MultiRelations multirelations=(MultiRelations)Vwms.mmb.getMMObject("multirelations");

        Vector vec=multirelations.searchMultiLevelVector(number,fields,"YES",tables,"",ordervec,dirvec);
        for (Enumeration e=vec.elements();e.hasMoreElements();) {
            MMObjectNode node=(MMObjectNode)e.nextElement();
            log.info("Judas -> found nodes "+node);
            int prog=node.getIntValue("programs.number");
            int episode=node.getIntValue("episodes.number");

            // extra check for 5jaar site !!
            if (prog==1615807) {
                log.info("5 jaar check="+prog);
                String url="/data/projecten/5JAAR2HOOG/1.1/aflevering/aflevering-txt.shtml?"+episode;
                addURL(url);
            } else if (false && prog==2215511) {
                // Kaft toer
                log.info("Judas -> Kaft toer check");
                String url="/data/kaft/kafttoer/1.0/episode/one_episode.shtml?"+prog+"+"+episode;
                addURL(url);

                url="/data/kaft/kafttoer/1.0/episode/list_items.shtml?"+prog+"+"+episode;
                addURL(url);

                url="/data/kaft/kafttoer/1.0/episode/intro.shtml?"+prog;
                addURL(url);

                url="/data/kaft/kafttoer/1.0/episode/episode.shtml?"+prog+"+"+episode;
                addURL(url);

                url="/data/kaft/kafttoer/1.0/episode/parts/list_episodes.shtml?"+prog+"+"+episode;
                addURL(url);

                url="/data/kaft/kafttoer/1.0/program/description.shtml?"+prog;
                addURL(url);
            } else {
                String url="/data/"+prog+"/aflevering.shtml?"+episode;
                addURL(url,PriorityURL.MIN_PRIORITY);
                url="/data/"+prog+"/aflevering-txt.shtml?"+episode;
                addURL(url,PriorityURL.MIN_PRIORITY);
                url="/data/"+prog+"/aflevering_txt.shtml?"+episode;
                addURL(url,PriorityURL.MIN_PRIORITY);

                url="/data/"+prog+"/archief.shtml?"+prog;
                addURL(url,PriorityURL.MIN_PRIORITY);
                url="/data/"+prog+"/archief-txt.shtml?"+prog;
                addURL(url,PriorityURL.MIN_PRIORITY);
                url="/data/"+prog+"/archief_txt.shtml?"+prog;
                addURL(url,PriorityURL.MIN_PRIORITY);

                url="/data/"+prog+"/gasten.shtml?"+prog;
                addURL(url,PriorityURL.MIN_PRIORITY);
                url="/data/"+prog+"/gasten-txt.shtml?"+prog;
                addURL(url,PriorityURL.MIN_PRIORITY);
                url="/data/"+prog+"/gasten_txt.shtml?"+prog;
                addURL(url,PriorityURL.MIN_PRIORITY);
                url="/data/"+prog+"/program.shtml?"+prog;
                addURL(url,PriorityURL.MIN_PRIORITY);
                url="/data/"+prog+"/program-txt.shtml?"+prog;
                addURL(url,PriorityURL.MIN_PRIORITY);
                url="/data/"+prog+"/program_txt.shtml?"+prog;
                addURL(url,PriorityURL.MIN_PRIORITY);
            }
        }
    }



    public void getItems(int number) {
        Vector tables=new Vector();
        tables.addElement("programs");
        tables.addElement("episodes");
        tables.addElement("items");
        Vector fields=new Vector();
        fields.addElement("programs.number");
        fields.addElement("episodes.number");
        fields.addElement("items.number");
        Vector ordervec=new Vector();
        Vector dirvec=new Vector();


        MultiRelations multirelations=(MultiRelations)Vwms.mmb.getMMObject("multirelations");

        Vector vec=multirelations.searchMultiLevelVector(number,fields,"YES",tables,"",ordervec,dirvec);
        for (Enumeration e=vec.elements();e.hasMoreElements();) {
            MMObjectNode node=(MMObjectNode)e.nextElement();
            log.info("Judas -> found nodes "+node);
            int prog=node.getIntValue("programs.number");
            int episode=node.getIntValue("episodes.number");
            int item=node.getIntValue("items.number");

            // extra check for 5jaar site !!
            if (prog==1615807) {
                log.info("5 jaar check="+prog);

                InsRel bul=(InsRel)Vwms.mmb.getMMObject("insrel");
                Enumeration g=bul.getRelated(item,5);
                if (g.hasMoreElements()) {
                    MMObjectNode pnode=(MMObjectNode)g.nextElement();

                    String url="/data/projecten/5JAAR2HOOG/1.1/persoon/persoon-item.shtml?"+pnode.getIntValue("number");
                    addURL(url);
                }
                String url="/data/projecten/5JAAR2HOOG/1.1/aflevering/aflevering-item.shtml?"+episode;
                addURL(url);
            } else if (false && prog==2215511) {
                // Kaft toer
                log.info("Judas -> Kaft toer check");
                String url="/data/kaft/kafttoer/1.0/episode/one_episode.shtml?"+prog+"+"+episode;
                addURL(url);

                url="/data/kaft/kafttoer/1.0/episode/list_items.shtml?"+prog+"+"+episode;
                addURL(url);

                url="/data/kaft/kafttoer/1.0/program/description.shtml?"+prog;
                addURL(url);

                InsRel bul=(InsRel)Vwms.mmb.getMMObject("insrel");
                Enumeration g=bul.getRelated(""+item,"people");
                MMObjectNode pnode;
                if (g.hasMoreElements()) {
                    pnode=(MMObjectNode)g.nextElement();
                    url="/data/kaft/kafttoer/1.0/item/item.shtml?"+prog+"+"+episode+"+"+pnode.getIntValue("number")+"+"+item;
                    addURL(url);
                }
            } else {
                String url="/data/"+prog+"/item.shtml?"+item;
                addURL(url);
                url="/data/"+prog+"/item-txt.shtml?"+item;
                addURL(url);
                url="/data/"+prog+"/item_txt.shtml?"+item;
                addURL(url);
                url="/data/"+prog+"/aflevering.shtml?"+episode;
                addURL(url);
                url="/data/"+prog+"/aflevering-txt.shtml?"+episode;
                addURL(url);
            }

        }
    }


    public void getPeople(int number) {
        Vector tables=new Vector();
        tables.addElement("programs");
        tables.addElement("episodes");
        tables.addElement("items");
        tables.addElement("people");
        Vector fields=new Vector();
        fields.addElement("programs.number");
        fields.addElement("episodes.number");
        fields.addElement("items.number");
        fields.addElement("people.number");
        Vector ordervec=new Vector();
        Vector dirvec=new Vector();


        MultiRelations multirelations=(MultiRelations)Vwms.mmb.getMMObject("multirelations");

        Vector vec=multirelations.searchMultiLevelVector(number,fields,"YES",tables,"",ordervec,dirvec);
        for (Enumeration e=vec.elements();e.hasMoreElements();) {
            MMObjectNode node=(MMObjectNode)e.nextElement();
            log.info("Judas -> found nodes "+node);
            int prog=node.getIntValue("programs.number");
            int episode=node.getIntValue("episodes.number");
            int item=node.getIntValue("items.number");
            int people=node.getIntValue("people.number");

            if (false && prog==2215511) {
                log.info("Judas -> Kaft toer check");
                // Kaft toer
                String url="/data/kaft/kafttoer/1.0/item/item.shtml?"+prog+"+"+episode+"+"+people+"+"+item;
                addURL(url);

                url="/data/kaft/kafttoer/1.0/people/list_people.shtml?"+prog+"+"+people;
                addURL(url);

                url="/data/kaft/kafttoer/1.0/people/one_people.shtml?"+prog+"+"+people;
                addURL(url);

                url="/data/kaft/kafttoer/1.0/people/intro.shtml?"+prog+"+"+people;
                addURL(url);

                url="/data/kaft/kafttoer/1.0/people/list_items.shtml?"+prog+"+"+people;
                addURL(url);
            }
        }
    }


    /* ---------------- */
    /* New 3voor12 site */
    /* ---------------- */


    /*
        /3voor12/shows/episodes.shtml?portal+map+program+episode
        /3voor12/shows/programs.shtml?portal+map+program
    */
    private void get3voor12_shows(int number,String ctype) {
        MMObjectNode node;
        Vector tables=new Vector();
        Vector fields=new Vector();
        Vector ordervec=new Vector();
        Vector dirvec=new Vector();

        boolean doAdd=false;

        int daymark10;
        int daymark35;

        int portal,map,program,episode;

        if (ctype.equals("d")) return;

        MultiRelations multirelations=(MultiRelations)Vwms.mmb.getMMObject("multirelations");
        DayMarkers daymarks=(DayMarkers)Vwms.mmb.getMMObject("daymarks");
        daymark10=daymarks.getDayCountAge(10);
        daymark35=daymarks.getDayCountAge(35);
        int cur=(int)(DateSupport.currentTimeMillis()/1000);


        /* 2534202 (portal) episodes */
        tables=new Vector();
        tables.addElement("portals");
        tables.addElement("maps");
        tables.addElement("programs");
        tables.addElement("episodes");
        fields=new Vector();
        fields.addElement("portals.number");
        fields.addElement("maps.number");
        fields.addElement("programs.number");
        fields.addElement("episodes.number");
        Vector vec=multirelations.searchMultiLevelVector(number,fields,"YES",tables,"where portals.number=2534202",ordervec,dirvec);
        log.debug("shows: portals,maps,programs,episodes result "+vec);
        for (Enumeration e=vec.elements();e.hasMoreElements();) {
            node=(MMObjectNode)e.nextElement();
            portal=node.getIntValue("portals.number");
            map=node.getIntValue("maps.number");
            program=node.getIntValue("programs.number");
            episode=node.getIntValue("episodes.number");
            addURL("/3voor12/shows/episodes.shtml?"+portal+"+"+map+"+"+program+"+"+episode);
            addURL("/3voor12/shows/programs.shtml?"+portal+"+"+map+"+"+program);
        }

    }

    /*
        /3voor12/shows/episodes.shtml?portal+map+program+episode
        /3voor12/shows/programs.shtml?portal+map+program
        /3voor12/shows/shows.shtml?portal
    */
    private void get3voor12_audioparts_shows(int number,String ctype) {
        MMObjectNode node;
        Vector tables=new Vector();
        Vector fields=new Vector();
        Vector ordervec=new Vector();
        Vector dirvec=new Vector();

        int daymark10;
        int daymark75;

        int portal,map,program,episode;

        if (ctype.equals("d")) return;

        MultiRelations multirelations=(MultiRelations)Vwms.mmb.getMMObject("multirelations");
        DayMarkers daymarks=(DayMarkers)Vwms.mmb.getMMObject("daymarks");
        daymark10=daymarks.getDayCountAge(10);
        daymark75=daymarks.getDayCountAge(75);
        int cur=(int)(DateSupport.currentTimeMillis()/1000);


        /* 2534202 (portal) episodes */
        tables=new Vector();
        tables.addElement("portals");
        tables.addElement("maps");
        tables.addElement("programs");
        tables.addElement("episodes");
        tables.addElement("audioparts");
        fields=new Vector();
        fields.addElement("portals.number");
        fields.addElement("maps.number");
        fields.addElement("programs.number");
        fields.addElement("episodes.number");
        Vector vec=multirelations.searchMultiLevelVector(number,fields,"YES",tables,"where portals.number=2534202 AND audioparts.number>"+daymark75+"",ordervec,dirvec);
        log.debug("audioparts: portals,maps,programs,episodes result "+vec);
        for (Enumeration e=vec.elements();e.hasMoreElements();) {
            node=(MMObjectNode)e.nextElement();
            portal=node.getIntValue("portals.number");
            map=node.getIntValue("maps.number");
            program=node.getIntValue("programs.number");
            episode=node.getIntValue("episodes.number");
            addURL("/3voor12/shows/episodes.shtml?"+portal+"+"+map+"+"+program+"+"+episode,PriorityURL.MIN_PRIORITY);
            addURL("/3voor12/shows/programs.shtml?"+portal+"+"+map+"+"+program,PriorityURL.MIN_PRIORITY);
        }
        if (vec.size()>0) {
            addURL("/3voor12/shows/shows.shtml?2534202",PriorityURL.MIN_PRIORITY);
        }

    }

    /*
        /3voor12/tracks/track.shtml?portal,map,program,group,track
        /3voor12/tracks/track.shtml?portal,map,program,group
        /3voor12/tracks/tracks.shtml?portal,map,program
        /3voor12/dj_sets/dj_set.shtml?portal,map,program,group,track
        /3voor12/dj_sets/dj_sets.shtml?portal,map,program
        /3voor12/concerts/concert.shtml?portal,map,program,group,track
        /3voor12/concerts/concerts.shtml?portal,map,program

    */
    private void get3voor12_audioparts_tracks(int number,String ctype) {
        MMObjectNode node;
        Vector tables=new Vector();
        Vector fields=new Vector();
        Vector ordervec=new Vector();
        Vector dirvec=new Vector();

        int daymark10;
        int daymark75;

        int portal,map,program,audiopart,group,iclass;

        if (ctype.equals("d")) return;

        MultiRelations multirelations=(MultiRelations)Vwms.mmb.getMMObject("multirelations");
        DayMarkers daymarks=(DayMarkers)Vwms.mmb.getMMObject("daymarks");
        daymark10=daymarks.getDayCountAge(10);
        daymark75=daymarks.getDayCountAge(75);
        int cur=(int)(DateSupport.currentTimeMillis()/1000);


        /* 2534202 (portal) */
        tables=new Vector();
        tables.addElement("portals");
        tables.addElement("maps");
        tables.addElement("programs");
        tables.addElement("groups");
        tables.addElement("audioparts");
        fields=new Vector();
        fields.addElement("portals.number");
        fields.addElement("maps.number");
        fields.addElement("programs.number");
        fields.addElement("groups.number");
        fields.addElement("audioparts.number");
        fields.addElement("audioparts.class");
        Vector vec=multirelations.searchMultiLevelVector(number,fields,"YES",tables,"where portals.number=2534202 AND audioparts.number>"+daymark75+" AND audioparts.class in (1,2,3,4)",ordervec,dirvec);
        log.debug("audioparts_tracks: portals,maps,programs,audioparts result "+vec);
        for (Enumeration e=vec.elements();e.hasMoreElements();) {
            node=(MMObjectNode)e.nextElement();
            portal=node.getIntValue("portals.number");
            map=node.getIntValue("maps.number");
            program=node.getIntValue("programs.number");
            group=node.getIntValue("groups.number");
            audiopart=node.getIntValue("audioparts.number");
            iclass=node.getIntValue("audioparts.class");
            switch(iclass) {
                case 1: // Track
                    addURL("/3voor12/tracks/track.shtml?"+portal+"+"+map+"+"+program+"+"+group);
                    addURL("/3voor12/tracks/track.shtml?"+portal+"+"+map+"+"+program+"+"+group+"+"+audiopart);
                    addURL("/3voor12/tracks/tracks.shtml?"+portal+"+"+map+"+"+program);
                    break;
                case 2: // StudioSession
                    // No pages ?
                    break;
                case 3: // Live Recording

                    addURL("/3voor12/concerts/concert.shtml?"+portal+"+"+map+"+"+program+"+"+group+"+"+audiopart);
                    addURL("/3voor12/concerts/concerts.shtml?"+portal+"+"+map+"+"+program);
                    break;
                case 4: // DJ Set
                    addURL("/3voor12/dj_sets/dj_set.shtml?"+portal+"+"+map+"+"+program+"+"+group+"+"+audiopart);
                    addURL("/3voor12/dj_sets/dj_sets.shtml?"+portal+"+"+map+"+"+program);

                    break;
                default:
                    log.error("Invalid query result!, got a classtype that wasn't queried");
                    break;
            }
        }
    }



    /*
        /3voor12/tracks/tracks_month.shtml?portal,map,program,'months'
        /3voor12/concerts/concerts_month.shtml?portal,map,program,'months'

    */
    private void get3voor12_audioparts_month(int number,String ctype) {
        MMObjectNode node;
        Vector tables=new Vector();
        Vector fields=new Vector();
        Vector ordervec=new Vector();
        Vector dirvec=new Vector();

        int daymark10;
        int daymark75;

        int portal,map,program,audiopart,group,themonth,theclass;

        if (ctype.equals("d")) return;

        MultiRelations multirelations=(MultiRelations)Vwms.mmb.getMMObject("multirelations");
        DayMarkers daymarks=(DayMarkers)Vwms.mmb.getMMObject("daymarks");
        daymark10=daymarks.getDayCountAge(10);
        daymark75=daymarks.getDayCountAge(75);
        int cur=(int)(DateSupport.currentTimeMillis()/1000);


        /* 2534202 (portal) */
        tables=new Vector();
        tables.addElement("portals");
        tables.addElement("maps");
        tables.addElement("programs");
        tables.addElement("groups");
        tables.addElement("audioparts");
        fields=new Vector();
        fields.addElement("portals.number");
        fields.addElement("maps.number");
        fields.addElement("programs.number");
        fields.addElement("groups.number");
        fields.addElement("audioparts.number");
        fields.addElement("audioparts.class");
        Vector vec=multirelations.searchMultiLevelVector(number,fields,"YES",tables,"where portals.number=2534202 AND audioparts.class in (1,2,3,4)",ordervec,dirvec);
        log.debug("audioparts_month: portals,maps,programs,audioparts result "+vec);
        for (Enumeration e=vec.elements();e.hasMoreElements();) {
            node=(MMObjectNode)e.nextElement();
            portal=node.getIntValue("portals.number");
            map=node.getIntValue("maps.number");
            program=node.getIntValue("programs.number");
            group=node.getIntValue("groups.number");
            theclass=node.getIntValue("audioparts.class");
            audiopart=node.getIntValue("audioparts.number");
            themonth=getMonthNumber(audiopart);
            switch(theclass) {
                case 1: // Track
                    addURL("/3voor12/tracks/tracks_month.shtml?"+portal+"+"+map+"+"+program+"+"+themonth,PriorityURL.MIN_PRIORITY);
                    break;
                case 2: // StudioSession
                    // No pages ?
                    break;
                case 3: // Live Recording
                    addURL("/3voor12/concerts/concerts_month.shtml?"+portal+"+"+map+"+"+program+"+"+themonth,PriorityURL.MIN_PRIORITY);

                    break;
                case 4: // DJ Set
                    break;
                default:
                    log.error("Invalid query result!, got a classtype that wasn't queried");
                    break;
            }
        }
    }



    /*
        /3voor12/tracks/tracks_genre_year.shtml?portal,map,program,month,categorie
        /3voor12/tracks/genre_search_result.shtml?portal,map,program,categorie
        /3voor12/concerts/concerts_genre_year.shtml?portal,map,program,month,categorie
        /3voor12/concerts/genre_search_result.shtml?portal,map,program,categorie

    */
    private void get3voor12_audioparts_categorie(int number,String ctype) {
        MMObjectNode node;
        Vector tables=new Vector();
        Vector fields=new Vector();
        Vector ordervec=new Vector();
        Vector dirvec=new Vector();

        int daymark10;
        int daymark75;

        int portal,map,program,audiopart,group,themonth,theclass,categorie,theyearmonth;

        if (ctype.equals("d")) return;

        MultiRelations multirelations=(MultiRelations)Vwms.mmb.getMMObject("multirelations");
        DayMarkers daymarks=(DayMarkers)Vwms.mmb.getMMObject("daymarks");
        daymark10=daymarks.getDayCountAge(10);
        daymark75=daymarks.getDayCountAge(75);
        int cur=(int)(DateSupport.currentTimeMillis()/1000);


        /* 2534202 (portal) */
        tables=new Vector();
        tables.addElement("portals");
        tables.addElement("maps");
        tables.addElement("programs");
        tables.addElement("groups1");
        tables.addElement("audioparts");
        tables.addElement("groups2");
        tables.addElement("categories");
        fields=new Vector();
        fields.addElement("portals.number");
        fields.addElement("maps.number");
        fields.addElement("programs.number");
        fields.addElement("groups1.number");
        fields.addElement("audioparts.number");
        fields.addElement("audioparts.class");
        fields.addElement("categories.number");
        Vector vec=multirelations.searchMultiLevelVector(number,fields,"YES",tables,"where portals.number=2534202 AND groups1.number=groups2.number AND audioparts.class in (1,2,3,4)",ordervec,dirvec);
        log.debug("audioparts_categorie: portals,maps,programs,audioparts result "+vec);
        for (Enumeration e=vec.elements();e.hasMoreElements();) {
            node=(MMObjectNode)e.nextElement();
            portal=node.getIntValue("portals.number");
            map=node.getIntValue("maps.number");
            program=node.getIntValue("programs.number");
            group=node.getIntValue("groups.number");
            theclass=node.getIntValue("audioparts.class");
            categorie=node.getIntValue("categories.number");
            audiopart=node.getIntValue("audioparts.number");
            themonth=getMonthNumber(audiopart);
            theyearmonth=themonth-(themonth%12);
            switch(theclass) {
                case 1: // Track
                    addURL("/3voor12/tracks/genre_search_result.shtml?"+portal+"+"+map+"+"+program+"+"+categorie,PriorityURL.MIN_PRIORITY);
                    addURL("/3voor12/tracks/tracks_genre_year.shtml?"+portal+"+"+map+"+"+program+"+"+theyearmonth+"+"+categorie,PriorityURL.MIN_PRIORITY);
                    break;
                case 2: // StudioSession
                    // No pages ?
                    break;
                case 3: // Live Recording
                    addURL("/3voor12/concerts/genre_search_result.shtml?"+portal+"+"+map+"+"+program+"+"+categorie,PriorityURL.MIN_PRIORITY);
                    addURL("/3voor12/concerts/concerts_genre_year.shtml?"+portal+"+"+map+"+"+program+"+"+theyearmonth+"+"+categorie,PriorityURL.MIN_PRIORITY);

                    break;
                case 4: // DJ Set
                    break;
                default:
                    log.error("Invalid query result!, got a classtype that wasn't queried");
                    break;
            }
        }
    }

    /*
        /3voor12/shows/shows.shtml?portal
        /3voor12/zapcentral.shtml?
    */
    private void get3voor12_zapcentral(int number,String ctype) {
        MMObjectNode node;
        Vector tables=new Vector();
        tables.addElement("maps");
        tables.addElement("programs");
        tables.addElement("news");
        tables.addElement("mmevents");
        Vector fields=new Vector();
        fields.addElement("maps.number");
        Vector ordervec=new Vector();
        Vector dirvec=new Vector();

        boolean doAdd=false;
        int daymark10;
        int daymark35;

        if (ctype.equals("d")) return;

        MultiRelations multirelations=(MultiRelations)Vwms.mmb.getMMObject("multirelations");
        DayMarkers daymarks=(DayMarkers)Vwms.mmb.getMMObject("daymarks");
        daymark10=daymarks.getDayCountAge(10);
        daymark35=daymarks.getDayCountAge(35);
        int cur=(int)(DateSupport.currentTimeMillis()/1000);


        if (!doAdd) {
            /* 2534202 (portal)  epsisodes */
            tables=new Vector();
            tables.addElement("portals");
            tables.addElement("maps");
            tables.addElement("programs");
            tables.addElement("episodes");
            tables.addElement("bcastrel");
            tables.addElement("mmevents");
            fields=new Vector();
            fields.addElement("portals.number");
            log.debug("event check datesupport");
            Vector vec=multirelations.searchMultiLevelVector(number,fields,"YES",tables,"where portals.number=2534202 AND episodes.number>"+daymark35+" AND mmevents.number>"+daymark35+" AND (mmevents.stop>"+(cur-10*60)+" AND mmevents.stop<"+(cur+10*60)+") OR (mmevents.start>"+(cur-10*60)+" AND mmevents.start<"+(cur+10*60)+")",ordervec,dirvec);
            log.debug("result "+vec);
            if (vec.size()>0) {
                doAdd=true;
                addURL("/3voor12/shows/shows.shtml?2534202",PriorityURL.MAX_PRIORITY);
            }
        }

        if (!doAdd) {
            /* maps,programs,news,mmevents 2584688 (map) 3voor12 magazine*/
            tables=new Vector();
            tables.addElement("maps");
            tables.addElement("programs");
            tables.addElement("news");
            tables.addElement("mmevents");
            fields=new Vector();
            fields.addElement("maps.number");
            log.debug("map news check");
            Vector vec=multirelations.searchMultiLevelVector(number,fields,"YES",tables,"where maps.number=2584688 AND news.number>"+daymark10+"",ordervec,dirvec);
            log.debug("result "+vec);
            if (vec.size()>0) {
                doAdd=true;
            }
        }

        if (!doAdd) {
            /* programs,groups,audioparts 2584508 (program) */
            tables=new Vector();
            tables.addElement("programs");
            tables.addElement("groups");
            tables.addElement("audioparts");
            fields=new Vector();
            fields.addElement("programs.number");
            log.debug("group track check");
            Vector vec=multirelations.searchMultiLevelVector(number,fields,"YES",tables,"where programs.number=2635958 AND audioparts.number>"+daymark10+"",ordervec,dirvec);
            log.debug("result "+vec);
            if (vec.size()>0) {
                doAdd=true;
            }
        }

        if (!doAdd) {
            /* maps,programs,news,mmevents 2635958 Speciale aankondigingen */
            tables=new Vector();
            tables.addElement("maps");
            tables.addElement("programs");
            tables.addElement("news");
            tables.addElement("mmevents");
            fields=new Vector();
            fields.addElement("maps.number");
            log.debug("map news check specials");
            Vector vec=multirelations.searchMultiLevelVector(number,fields,"YES",tables,"where maps.number=2635958 AND news.number>"+daymark10+"",ordervec,dirvec);
            log.debug("result "+vec);
            if (vec.size()>0) {
                doAdd=true;
            }
        }

        if (doAdd) {
            addURL("/3voor12/zapcentral.shtml?",PriorityURL.MAX_PRIORITY);
//            addURL("/3voor12/navigatie/dollarmod.flashvar?");
        }
    }


    /*
        /3voor12/artists/artist.shtml?portal,map,program,artist
        /3voor12/artists/artists.shtml?portal,map,program
        /3voor12/artists/artists_genre_year.shtml?portal,map,program,'months',categorie
        /3voor12/artists/artists_month.shtml?portal,map,program,'months'
        /3voor12/artists/genre_search_result.shtml?portal,map,program,categorie
    */
    private void get3voor12_groups(int number,String ctype) {
        MMObjectNode node;
        Vector tables=new Vector();
        Vector fields=new Vector();
        Vector ordervec=new Vector();
        Vector dirvec=new Vector();

        int portal,map,program,group,categorie,themonth,theyearmonth;
        int daymark,curdaymark;

        if (ctype.equals("d")) return;

        DayMarkers daymarks=(DayMarkers)Vwms.mmb.getMMObject("daymarks");
        MultiRelations multirelations=(MultiRelations)Vwms.mmb.getMMObject("multirelations");

        /* 2534202 (portal) */
        tables=new Vector();
        tables.addElement("portals");
        tables.addElement("maps");
        tables.addElement("programs");
        tables.addElement("groups");
        tables.addElement("categories");
        fields=new Vector();
        fields.addElement("portals.number");
        fields.addElement("maps.number");
        fields.addElement("programs.number");
        fields.addElement("groups.number");
        fields.addElement("categories.number");
        Vector vec=multirelations.searchMultiLevelVector(number,fields,"YES",tables,"where portals.number=2534202",ordervec,dirvec);
        log.debug("audioparts_groups: portals,maps,programs,groups result "+vec);
        for (Enumeration e=vec.elements();e.hasMoreElements();) {
            node=(MMObjectNode)e.nextElement();
            portal=node.getIntValue("portals.number");
            map=node.getIntValue("maps.number");
            program=node.getIntValue("programs.number");
            group=node.getIntValue("groups.number");
            categorie=node.getIntValue("categories.number");
            themonth=getMonthNumber(group);
            theyearmonth=themonth-(themonth%12);
            addURL("/3voor12/artists/artist.shtml?"+portal+"+"+map+"+"+program+"+"+group);
            addURL("/3voor12/artists/artists_genre_year.shtml?"+portal+"+"+map+"+"+program+"+"+theyearmonth+"+"+categorie,PriorityURL.MIN_PRIORITY);
            addURL("/3voor12/artists/artists_month.shtml?"+portal+"+"+map+"+"+program+"+"+themonth,PriorityURL.MIN_PRIORITY);
            addURL("/3voor12/artists/genre_search_result.shtml?"+portal+"+"+map+"+"+program+"+"+categorie,PriorityURL.MIN_PRIORITY);
            daymark=daymarks.getDayCountByObject(group);
            curdaymark=daymarks.getDayCount();
            if ((curdaymark-daymark)<60) {
                addURL("/3voor12/artists/artists.shtml?"+portal+"+"+map+"+"+program);
            }
        }
    }



    /*
        /3voor12/journalism/nieuws.shtml?portal,map,program,news
        /3voor12/journalism/journalism.shtml?portal,map,program
        /3voor12/journalism/journalism_genre_year.shtml?portal,map,program,'months',newstype
        /3voor12/journalism/journalism_month.shtml?portal,map,program,'months'
        /3voor12/journalism/type_search_result.shtml?portal,map,program,newstype
    */
    private void get3voor12_journalism(int number,String ctype) {
        MMObjectNode node;
        Vector tables=new Vector();
        Vector fields=new Vector();
        Vector ordervec=new Vector();
        Vector dirvec=new Vector();

        int portal,map,program,news,newstype,themonth,theyearmonth;
        int daymark,curdaymark;

        if (ctype.equals("d")) return;

        DayMarkers daymarks=(DayMarkers)Vwms.mmb.getMMObject("daymarks");
        MultiRelations multirelations=(MultiRelations)Vwms.mmb.getMMObject("multirelations");

        /* 2534202 (portal) */
        tables=new Vector();
        tables.addElement("portals");
        tables.addElement("maps");
        tables.addElement("programs");
        tables.addElement("news");
        fields=new Vector();
        fields.addElement("portals.number");
        fields.addElement("maps.number");
        fields.addElement("programs.number");
        fields.addElement("news.number");
        fields.addElement("news.type");
        Vector vec=multirelations.searchMultiLevelVector(number,fields,"YES",tables,"where portals.number=2534202",ordervec,dirvec);
        log.debug("journalism: portals,maps,programs,news result "+vec);
        for (Enumeration e=vec.elements();e.hasMoreElements();) {
            node=(MMObjectNode)e.nextElement();
            portal=node.getIntValue("portals.number");
            map=node.getIntValue("maps.number");
            program=node.getIntValue("programs.number");
            news=node.getIntValue("news.number");
            newstype=node.getIntValue("news.type");
            themonth=getMonthNumber(news);
            theyearmonth=themonth-(themonth%12);
            addURL("/3voor12/journalism/nieuws.shtml?"+portal+"+"+map+"+"+program+"+"+news);
            addURL("/3voor12/journalism/journalism_genre_year.shtml?"+portal+"+"+map+"+"+program+"+"+theyearmonth+"+"+newstype,PriorityURL.MIN_PRIORITY);
            addURL("/3voor12/journalism/journalism_month.shtml?"+portal+"+"+map+"+"+program+"+"+themonth,PriorityURL.MIN_PRIORITY);
            addURL("/3voor12/journalism/genre_search_result.shtml?"+portal+"+"+map+"+"+program+"+"+newstype,PriorityURL.MIN_PRIORITY);
            daymark=daymarks.getDayCountByObject(news);
            curdaymark=daymarks.getDayCount();
            if ((curdaymark-daymark)<60) {
                addURL("/3voor12/journalism/journalism.shtml?"+portal+"+"+map+"+"+program+"+0"); // Why the zero ? ask the frigging designer
                addURL("/3voor12/journalism/journalism.shtml?"+portal+"+"+map+"+"+program);
                addURL("/3voor12/journalism/type_search_result.shtml?"+portal+"+"+map+"+"+program+"+"+newstype);
            }
        }
    }

    /* ----------- */
    /* 3voor12 end */
    /* ----------- */


    /**
     * Adds a url to be scheduled for reload.
     * The url has to be a relative url eg. /3voor12/bla/index.shtml?123+456 .
     * If no parameters are given, the url STILL has to have a '?' character eg. /3voor12/test.shtml?
     * The urls are forwarded to a thread that schedules the reloads based on priority.
     * Uses default priority for scheduling reload.
     * @param url the url to reload
     */
    public void addURL(String url) {
        pusher.addURL(url,PriorityURL.DEF_PRIORITY);
    }

    /**
     * Adds a url to be scheduled for reload.
     * The url has to be a relative url eg. /3voor12/bla/index.shtml?123+456 .
     * If no parameters are given, the url STILL has to have a '?' character eg. /3voor12/test.shtml?
     * The urls are forwarded to a thread that schedules the reloads based on priority.
     * @param url the url to reload
     * @param priority priority at which the url needs to be reloaded
     */
    public void addURL(String url,int priority) {
        pusher.addURL(url,priority);
    }

    /**
     * Forces a reload and subsequent caching of a page.
     * The routine makes a change to a netfiles object, which causes the NetFileSrv builder to invoke
     * the {@link PageMaster} VWM that handles the filechanges, in this case transfer of the file to
     * a remote proxy (a mirror server).
     * @param url Url of the page to reload
     * @return <code>true</code>
     */
    public boolean pushReload(String url) {
        log.info("Judas -> pushing ="+url);
        url=url.replace('?',':')+".asis";
        Netfiles bul=(Netfiles)Vwms.mmb.getMMObject("netfiles");
        Enumeration e=bul.search("WHERE filename='"+url+"' AND service='pages' AND subservice='main'");
        if (e.hasMoreElements()) {
            MMObjectNode node=(MMObjectNode)e.nextElement();
            node.setValue("status",Netfiles.STATUS_CHANGED);
            node.commit();
        }
        return true;
    }

    /**
     * Retrieves the month in which a node was created.
     * Calculated using the age of the node.
     * @param number number of the node
     * @return the number of the month in which the node was created.
     */
    private int getMonthNumber(int number) {
        DayMarkers daymarks=(DayMarkers)Vwms.mmb.getMMObject("daymarks");
        int daymark;
        int month=0;
        // get Daymarker for number
        daymark=daymarks.getDayCountByObject(number);
        // convert daymarker to monthnumber
        month=daymarks.getMonthsByDayCount(daymark);
        return month;
    }
}
