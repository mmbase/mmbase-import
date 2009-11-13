/*

This software is OSI Certified Open Source Software.
OSI Certified is a certification mark of the Open Source Initiative.

The license (Mozilla version 1.0) can be read at the MMBase site.
See http://www.MMBase.org/license

*/
package org.mmbase.module.builders.vwms;

import java.util.*;

import org.mmbase.module.core.*;
import org.mmbase.util.*;
import org.mmbase.util.Queue;
import org.mmbase.util.logging.*;
import org.mmbase.module.builders.*;
import org.mmbase.module.gui.html.*;

/**
 * A VWM that manages the files by scheduling them to be send to one or more mirror sites.
 * Requests for scheduling is done in the netfile builder.
 * This VWM handles those netfile requests whose service is 'pages'. Available subservices are 'main' and 'mirror'.
 * Requests for file copy are checked periodically.
 * This results in one or more requests for a 'mirror' service,
 * which then result in a file copy request, which is handled in a separate thread.
 * This VWM also has methods for recalculating pages and handling page changes (which in turn result in
 * a request for file copy.)
 * Entry point for these requests are the FileChange methods from the {@link VwmServiceInterface}.
 *
 * @author Daniel Ockeloen
 * @author Pierre van Rooden (javadocs)
 * @version $Id$
 */

public class PageMaster extends Vwm implements MMBaseObserver,VwmServiceInterface {

    private static final Logger log = Logging.getLoggerInstance(PageMaster.class);

    // field used to skip first probeCall (why???)
    boolean first=true;

    Object syncobj=new Object();  // used in commented code

    /**
    * Queue containing the file-copy tasks that need to be performed by {@link #filecopier}
    */
    Queue files2copy=new Queue(128);
    /**
     * Thread that handles the actual file transfers.
     */
    FileCopier filecopier=new FileCopier(files2copy);
    /**
     * Cache for mirror servers
     */
    Vector mirrornodes;

    //Hashtable properties; (unused)

    /**
     * Constructor for the PageMaster VWM.
     */
    public PageMaster() {
        log.debug("ready for action");
    }

    /**
     * Performs general periodic maintenance.
     * This routine handles alle open pages/main and pages/mirror file service requests.
     * These requests are obtained from the netfiles builder.
     * For each file that should be serviced, the filechange method is called.
     * This routine handles a maximum of 10 page/main, and 50 page/mirror service
     * calls each time it is called.
     * The first time this method is call, nothing happens (?)
     *
     * @return <code>true</code> if maintenance was performed, <code>false</code> otherwise
     */
    public boolean probeCall() {
        if (first) {
            // skip first time this method is called
            first=false;
        } else {
            // handle up to 10 pages/main fileservice requests
            try {
                Netfiles bul=(Netfiles)Vwms.getMMBase().getMMObject("netfiles");
                //Enumeration e=bul.search("WHERE service='pages' AND subservice='main' AND status="+Netfiles.STATUS_REQUEST+" ORDER BY number DESC");
                Enumeration e=bul.search("service=='pages'+subservice=='main'+status="+Netfiles.STATUS_REQUEST);
                int i=0;
                while (e.hasMoreElements() && i<10) {
                    MMObjectNode node=(MMObjectNode)e.nextElement();
                    fileChange(""+node.getIntValue("number"),"c");
                    i++;
                }
            } catch(Exception e) {
                log.error(Logging.stackTrace(e));
            }
            // handle up to 50 pages/mirror fileservice requests
            try {
                Netfiles bul=(Netfiles)Vwms.getMMBase().getMMObject("netfiles");
                Enumeration e=bul.search("service=='pages'+subservice=='mirror'+status="+Netfiles.STATUS_REQUEST);
                //Enumeration e=bul.search("WHERE service='pages' AND subservice='mirror' AND status="+Netfiles.STATUS_REQUEST+" ORDER BY number DESC");
                int i=0;
                while (e.hasMoreElements() && i<50) {
                    MMObjectNode node=(MMObjectNode)e.nextElement();
                    fileChange(""+node.getIntValue("number"),"c");
                    i++;
                }
            } catch(Exception e) {
                log.error(Logging.stackTrace(e));
            }
        }
        return true;
    }

    /**
     * Called when a remote node is changed.
     * @param machine Name of the machine that changed the node.
     * @param number Number of the changed node as a <code>String</code>
     * @param builder type of the changed node
     * @param ctype command type, 'c'=changed, 'd'=deleted', 'r'=relations changed, 'n'=new
     * @return <code>true</code>
     */
    public boolean nodeRemoteChanged(String machine,String number,String builder,String ctype) {
        return nodeChanged(machine,number,builder,ctype);
    }

    /**
     * Called when a local node is changed.
     * @param machine Name of the machine that changed the node.
     * @param number Number of the changed node as a <code>String</code>
     * @param builder type of the changed node
     * @param ctype command type, 'c'=changed, 'd'=deleted', 'r'=relations changed, 'n'=new
     * @return <code>true</code>
     */
    public boolean nodeLocalChanged(String machine,String number,String builder,String ctype) {
        return nodeChanged(machine,number,builder,ctype);
    }

    /**
     * Called when a local or remote node is changed.
     * Does not take any action.
     * @param machine Name of the machine that changed the node.
     * @param number Number of the changed node as a <code>String</code>
     * @param builder type of the changed node
     * @param ctype command type, 'c'=changed, 'd'=deleted', 'r'=relations changed, 'n'=new
     * @return <code>true</code>
     */
    public boolean nodeChanged(String machine,String number,String builder, String ctype) {
        // log.debug("sees that : "+number+" has changed type="+ctype);
        return true;
    }

    /**
     * Schedules a service-request on a file.
     * Only "pages/main" services are handled.
     * The service-request is later handled through the {@link #probeCall} method.
     * @param service the service to be performed
     * @param subservice the subservice to be performed
     * @param filename the filename to service
     * @return <code>true</code> if maintenance was performed, <code>false</code> otherwise
     */
    public boolean fileChange(String service,String subservice,String filename) {
        log.debug("frontend change -> "+filename);
        log.service("s="+service+" sub="+subservice+"file="+filename);
        // jump to correct subhandles based on the subservice
        if (subservice.equals("main")) {
            handleMainCheck(service,subservice,filename);
        }
        return true;
    }

    /**
     * Handles a service-request on a file, registered in the netfiles builder.
     * Depending on the subservice requested, this routine calls {@link #handleMirror}
     * or {@link #handleMain}.
     * @param number Number of the node in the netfiles buidler than contain service request information.
     * @param ctype the type of change on that node ("c" : node was changed)
     * @return <code>true</code>
     */
    public boolean fileChange(String number, String ctype) {
        // log.debug("fileChange="+number+" "+ctype);
        // first get the change node so we can see what is the matter with it.
        Netfiles bul=(Netfiles)Vwms.getMMBase().getMMObject("netfiles");
        MMObjectNode filenode=bul.getNode(number);
        if (filenode!=null) {
            // obtain all the basic info on the file.
            String service=filenode.getStringValue("service");
            String subservice=filenode.getStringValue("subservice");
            int status=filenode.getIntValue("status");

            // jump to correct subhandles based on the subservice
            if (subservice.equals("main")) {
                return handleMain(filenode,status,ctype);
            } else if (subservice.equals("mirror")) {
                return handleMirror(filenode,status,ctype);
            }
        }
        return true;
    }

    /**
     * Handles a pages/mirror service request.
     * Places a page in the file2copy queue, so it will be sent to a mirror
     * site by the FileCopier.
     * @param filenode the filenet node that contains the service request
     * @param status the current status of the node
     * @param ctype the type of change on that node ("c" : node was changed)
     * @return <code>true</code>
     */
    public boolean handleMirror(MMObjectNode filenode,int status,String ctype) {
        switch(status) {
            case Netfiles.STATUS_REQUEST:  // Request
                // register the node as being On Its Way
                filenode.setValue("status",Netfiles.STATUS_ON_ITS_WAY);
                filenode.commit();
                String filename=filenode.getStringValue("filename");
                String dstserver=filenode.getStringValue("mmserver");
                // recover the correct source/dest properties for this mirror
                //
                // why does it say "demoserver" ??
                //
                String sshpath=getProperty("demoserver","sshpath");
                log.debug("sshpath="+sshpath);
                String srcpath=getProperty("demoserver","path");
                log.debug("srcpath="+srcpath);
                String dstuser=getProperty(dstserver,"user");
                log.debug("dstuser="+dstuser);
                String dsthost=getProperty(dstserver,"host");
                log.debug("dsthost="+dsthost);
                String dstpath=getProperty(dstserver,"path");
                log.debug("dstpath="+dstpath);

/* this code can be dropped as it is handled in FileCopier

        SCPcopy scpcopy=new SCPcopy(sshpath,dstuser,dsthost,dstpath);

        synchronized(syncobj) {
            scpcopy.copy(srcpath,filename);
        }
*/
                // create a new file2copy object and add it to the queue,
                // so the FileCopier thread will handle it.
                files2copy.append(new aFile2Copy(dstuser,dsthost,dstpath,srcpath,filename,sshpath));

                // register the node as being Done
                filenode.setValue("status",Netfiles.STATUS_DONE);
                filenode.commit();
                break;
            case Netfiles.STATUS_ON_ITS_WAY:  // On its way
                break;
            case Netfiles.STATUS_DONE:  // Done
                break;
        }
        return true;
    }

    /**
     * Handles a pages/main service request.
     * The events handled are:<br />
     * - requests for handling: schedules requests to mirror this page using {@link #doMainRequest}<br />
     * - changed: page is scheduled to be recalculated<br />
     * - recaculate" page is recaclcutated and scheduled to be handled<br />
     *
     * @param filenode the netfiles node that contains the service request
     * @param status the current status of the node
     * @param ctype the type of change on that node ("c" : node was changed)
     * @return <code>true</code>
     */
    public boolean handleMain(MMObjectNode filenode,int status,String ctype) {
        switch(status) {
            case Netfiles.STATUS_REQUEST:  // Request
                // register the node as being On Its Way
                filenode.setValue("status",Netfiles.STATUS_ON_ITS_WAY);
                filenode.commit();
                // do stuff
                doMainRequest(filenode);
                // register the node as being Done
                filenode.setValue("status",Netfiles.STATUS_DONE);
                filenode.commit();
                break;
            case Netfiles.STATUS_ON_ITS_WAY:  // On Its Way
                break;
            case Netfiles.STATUS_DONE:  // Done
                break;
            case Netfiles.STATUS_CHANGED:  // Dirty (?)
                filenode.setValue("status",Netfiles.STATUS_CALC_PAGE);
                filenode.commit();
                break;
            case Netfiles.STATUS_CALC_PAGE:  // Recalculate Page
                String filename=filenode.getStringValue("filename");
                calcPage(filename);
                filenode.setValue("status",Netfiles.STATUS_REQUEST);
                filenode.commit();
                break;
        }
        return true;
    }

    /**
     * Handles a main subservice on a page.
     * The page is scheduled to be sent to all appropriate mirrorsites for this service,
     * by setting the request status in the associated mirror nodes.
     * If no mirror nodes are associated with this page, nothing happens.
     * @param filenode the netfiles node with the original (main) request
     */
    public boolean doMainRequest(MMObjectNode filenode) {
        // so this file has changed probably, check if the file is ready on
        // disk and set the mirrors to request.
        String filename = filenode.getStringValue("filename");

        // find and change all the mirror nodes so they get resend
        Netfiles bul=(Netfiles)Vwms.getMMBase().getMMObject("netfiles");
        Enumeration e=bul.search("WHERE filename='"+filename+"' AND service='pages' AND subservice='mirror'");
        while (e.hasMoreElements()) {
            MMObjectNode mirrornode=(MMObjectNode)e.nextElement();
            mirrornode.setValue("status",Netfiles.STATUS_REQUEST);
            mirrornode.commit();
        }
        return true;
    }

    /**
     * Schedules a netfile object to be send to its mirror sites.
     * The routine searches the appropriate netfile node, and sets its status to 'request'.
     * If a node does not exits, a new node is created. In the latter case, the system also creates mirrornodes
     * for each mirrorsite associated with this service.
     * @param service the service to be performed
     * @param subservice the subservice to be performed
     * @param filename the filename to service
     */
    public void handleMainCheck(String service,String subservice,String filename) {
        log.debug("Reached handleMainCheck");
        Netfiles bul=(Netfiles)Vwms.getMMBase().getMMObject("netfiles");
        Enumeration e=bul.search("WHERE filename='"+filename+"' AND service='"+service+"' AND subservice='"+subservice+"'");
        if (e.hasMoreElements()) {
            MMObjectNode mainnode=(MMObjectNode)e.nextElement();
            mainnode.setValue("status",Netfiles.STATUS_REQUEST);
            mainnode.commit();
        } else {
            MMObjectNode mainnode=bul.getNewNode("system");
            mainnode.setValue("filename",filename);
            mainnode.setValue("mmserver",Vwms.getMMBase().getMachineName());
            mainnode.setValue("service",service);
            mainnode.setValue("subservice",subservice);
            mainnode.setValue("status",Netfiles.STATUS_REQUEST);
            mainnode.setValue("filesize",-1);
            bul.insert("system",mainnode);

            Enumeration f=getMirrorNodes(service).elements();
            while (f.hasMoreElements()) {
                MMObjectNode n2=(MMObjectNode)f.nextElement();
                // hack hack also have to create mirror nodes !
                mainnode=bul.getNewNode("system");
                mainnode.setValue("filename",filename);
                mainnode.setValue("mmserver",n2.getStringValue("name"));
                mainnode.setValue("service",service);
                mainnode.setValue("subservice","mirror");
                mainnode.setValue("status",Netfiles.STATUS_DONE);
                mainnode.setValue("filesize",-1);
                bul.insert("system",mainnode);
            }
        }
    }

    /**
     * Retrieves a named property of a server.
     * @param machine name of the server
     * @param key name of the property to retrieve
     * @return the property value
     */
    public String getProperty(String machine,String key) {
        MMServers mmservers=(MMServers)Vwms.getMMBase().getMMObject("mmservers");
        return mmservers.getMMServerProperty(machine,key);
    }


    /**
     * Recalculate a page.
     * Invokes the SCAN parser (which will re-cache the page through the scancache module)
     * Only works for SCAN.
     * @param url of the page to cache
     */
    public void calcPage(String url) {
        scanparser m=(scanparser)Vwms.getMMBase().getModule("SCANPARSER");
        url=url.substring(0,url.length()-5);
        url=url.replace(':','?');
        log.debug("getPage="+url);
        if (m!=null) {
            scanpage sp=new scanpage();
            m.calcPage(url,sp,0);
        }
    }

    /**
     * Retrieves a list of Mirror Servers.
     * This is done by obtaining a fileserver node and retrieving associated mmserver nodes.
     * This method should be renamed and moved to the netfilesrv builder.
     * @param service preseumably the service to query for. Unused.
     * @return a <code>Vector</code> containing mmserver nodes that act as mirror server for this service
     */
    public Vector getMirrorNodes(String service) {
        if (mirrornodes!=null) return mirrornodes;
        NetFileSrv bul=(NetFileSrv)Vwms.getMMBase().getMMObject("netfilesrv");
        if (bul!=null) {
            Enumeration e=bul.search("service=='pages'+subservice=='mirror'");
            if (e.hasMoreElements()) {
                MMObjectNode n1=(MMObjectNode)e.nextElement();
                mirrornodes=n1.getRelatedNodes("mmservers");
                if (mirrornodes!=null) return mirrornodes;
            }
        }
        mirrornodes=new Vector();
        return mirrornodes;
    }
}
