/*

This software is OSI Certified Open Source Software.
OSI Certified is a certification mark of the Open Source Initiative.

The license (Mozilla version 1.0) can be read at the MMBase site.
See http://www.MMBase.org/license

*/

package org.mmbase.module.builders.vwms;

import java.util.*;
import java.io.*;

import org.mmbase.module.core.*;
import org.mmbase.util.*;
import org.mmbase.module.builders.*;
import org.mmbase.util.images.Imaging;
import org.mmbase.util.logging.*;

/**
 * A VWM that manages image files by scheduling them to be send to one or more mirror sites.
 * Requests for scheduling is done in the netfile builder.
 * This VWM handles those netfile requests whose service is 'images'. Available subservices are 'main' and 'mirror'.
 * Requests for file copy are checked periodically. The result is one or more requests for a 'mirror' service,
 * which then result in a file copy request, which is handled in a separate thread.
 * Before copying, images are retrieved from Icache and converted to an 'asis' file
 * (this is the file that actually gets copied).
 *
 * @author Daniel Ockeloen
 * @author Pierre van Rooden (javadocs)
 * @version $Id$
 */

public class ImageMaster extends Vwm implements MMBaseObserver,VwmServiceInterface {

    // Logger
    private static Logger log = Logging.getLoggerInstance(ImageMaster.class.getName());

    Hashtable properties;

    // field used to skip first probeCall (why???)
    boolean first=true;

    Object syncobj=new Object();  // used in commented code

    /**
     * List of files to transfer.
     * The filelist is periodically cleared by {@link ImagePusher} (which purges duplicate
     * files and handles the remaining transfers).
     */
    Vector<aFile2Copy> files=new Vector<aFile2Copy>();
    /**
     * The background thread that takes care of of the files scheduled for transfer.
     */
    ImagePusher pusher;
    /**
     * The maximum number of 'main' or 'mirror' requests to handle during each {@link #probeCall}
     */
    private int maxSweep=16;

    /**
     * Constructor for ImageMaster
     */
    public ImageMaster() {
        log.info("VWM ImageMaster started");
    }

    /**
     * Performs general periodic maintenance.
     * This routine handles alle open images/main and images/mirror file service requests.
     * These requests are obtained from the netfiles builder.
     * For each file that should be serviced, the filechange method is called.
     * This routine handles a maximum of 10 page/main, and 50 page/mirror service
     * calls each time it is called.
     * The first time this method is call, nothing happens (?)
     * <br />
     * Very similar to {@link #probeCall}.
     *
     * @return <code>true</code> if maintenance was performed, <code>false</code> otherwise
     */
    public boolean probeCall() {
        if (first) {
            first=false;
            // create ImagePusher
            if (pusher==null) {
                pusher=new ImagePusher(this);
                log.info("ImageMaster -> Starting Image pusher");
            }
        } else {
            try {
                Netfiles bul=(Netfiles)Vwms.getMMBase().getMMObject("netfiles");
                // note: order Descending means last file is transferred first.
                // Theoretically, some files may never be handled due to use of maxsweep
                Enumeration e=bul.search("WHERE service='images' AND subservice='main' AND status="+Netfiles.STATUS_REQUEST+" ORDER BY number DESC");
                int i=0;
                while (e.hasMoreElements() && i<maxSweep) {
                    MMObjectNode node=(MMObjectNode)e.nextElement();
                    fileChange(""+node.getIntValue("number"),"c");
                    i++;
                }
                try { Thread.sleep(1500); } catch(InterruptedException x) {}
                Enumeration f=bul.search("WHERE service='images' AND subservice='mirror' AND status="+Netfiles.STATUS_REQUEST+" ORDER BY number DESC");
                i=0;
                while (f.hasMoreElements() && i<maxSweep) {
                    MMObjectNode node=(MMObjectNode)f.nextElement();
                    fileChange(""+node.getIntValue("number"),"c");
                    i++;
                }
            } catch(Exception e) {
                log.error("probeCall exception "+e);
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
        return(nodeChanged(machine,number,builder,ctype));
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
        log.debug("sees that : "+number+" has changed type="+ctype+" of type:"+builder+" by machine:"+machine);
        return true;
    }

    /**
     * Schedules a service-request on a file.
     * Only "images/main" services are handled.
     * The service-request is later handled through the {@link #probeCall} method.
     * @param service the service to be performed
     * @param subservice the subservice to be performed
     * @param filename the filename to service
     * @return <code>true</code> if maintenance was performed, <code>false</code> otherwise
     */
    public boolean fileChange(String service,String subservice,String filename) {
        filename=URLEscape.unescapeurl(filename);
        log.debug("fileChange -> "+filename);
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
        // debug("fileChange="+number+" "+ctype);
        // first get the change node so we can see what is the matter with it.
        Netfiles bul=(Netfiles)Vwms.getMMBase().getMMObject("netfiles");
        MMObjectNode filenode=bul.getNode(number);
        if (filenode!=null) {
            // obtain all the basic info on the file.
            String service=filenode.getStringValue("service");
            String subservice=filenode.getStringValue("subservice");
            int status=filenode.getIntValue("status");

            log.debug("fileChange "+number+" "+subservice+" "+status);

            // jump to correct subhandles based on the subservice
            if (subservice.equals("main")) {
                handleMain(filenode,status,ctype);
            } else if (subservice.equals("mirror")) {
                handleMirror(filenode,status,ctype);
            }
        }
        return(true);
    }

    /**
     * Return a @link{ ByteFieldContainer} containing the bytes and object number
     * for the cached image with a certain ckey, or null, if not cached.
     * @param ckey teh ckey to search for
     * @return null, or a @link{ ByteFieldContainer} object
     */
    public ByteFieldContainer getCkeyNode(ImageCaches bul, String ckey) {
        log.debug("getting ckey node with " + ckey);
        int pos = 0;
        while (Character.isDigit(ckey.charAt(pos))) pos ++;
        int nodeNumber = Integer.parseInt(ckey.substring(0, pos));
        String template   = ckey.substring(pos);
        if (template.charAt(0) == '=') template = template.substring(1);
        MMObjectNode node = bul.getCachedNode(nodeNumber, template);
        if (node == null) {
            // we dont have a cachednode yet, return null
            log.debug("cached node not found for key (" + ckey + "), returning  null");
            return null;
        }
        // find binary data
        byte data[] = node.getByteValue(Imaging.FIELD_HANDLE);
        if (data == null) {
            // if it didn't work, also cache this result, to avoid concluding that again..
            // should this trow an exception every time? I think so, otherwise  we would generate an
            // image every time it is requested, which also net very handy...
            String msg =
                "The node(#" + node.getNumber() + ") which should contain the cached result for ckey:" + ckey +
                " had as value <null>, this means that something is really wro ng.(how can we have an cache node with node value in it?)";
            log.error(msg);
            throw new RuntimeException(msg);
         }
        ByteFieldContainer result = new ByteFieldContainer(node.getNumber(), data);
        return result;
    }

    /**
     * Handles an images/mirror service request.
     * Converts images to an asis file format, then places the asis file in the files list,
     * so it will be sent to a mirror site by the ImagePusher.
     * @param filenode the filenet node that contains the service request
     * @param status the current status of the node
     * @param ctype the type of change on that node ("c" : node was changed)
     * @return <code>true</code>
     */
    public boolean handleMirror(MMObjectNode filenode,int status,String ctype) {
        if (filenode==null) {
            log.error("ERROR: handleMirror filenode null!");
            return true;
        }

        log.debug("Node "+filenode+" status "+status+" type "+ctype);
        switch(status) {
            case Netfiles.STATUS_REQUEST:  // Request
                log.debug("status=="+Netfiles.STATUS_REQUEST);
                filenode.setValue("status",Netfiles.STATUS_ON_ITS_WAY);
                filenode.commit();
                log.debug("Starting real work");
                // do stuff
                String filename=filenode.getStringValue("filename");
                if ((filename==null) || filename.equals("")) {
                    log.error("handleMirror: filename null");
                    return true;
                }
                log.debug("handleMirror"+filename);
                String dstserver=filenode.getStringValue("mmserver");

                // save the image to disk
                ImageCaches bul=(ImageCaches)Vwms.getMMBase().getMMObject("icaches");
                if (bul==null) {
                    log.error("ImageCaches builder is null");
                    return true;
                }

                String mimetype = "image/jpeg"; // When not overwritten, it will stay on 'jpeg'.

                // get the clear ckey
                // '/img.db?xxxxxxxxxx.asis'
                // zap '/img.db?' and '.asis'
                String ckey=filename.substring(8);
                int pos=ckey.lastIndexOf(".");
                if (pos!=-1) {
                    ckey=ckey.substring(0,pos);
                    // We now have a clean ckey ( aka 234242+f(gif) )
                    // Get mimetype from ckey params string.
                    StringTokenizer st = new StringTokenizer(ckey,"+\n\r");
                    Vector<Object> ckeyVec = new Vector<Object>();
                    while (st.hasMoreTokens()) {
                        ckeyVec.addElement(st.nextElement());
                    }
                    Images imagesBuilder = (Images)Vwms.getMMBase().getMMObject("images");
                    if (imagesBuilder==null) {
                        log.error("handleMirror images builder not found");
                        return true;
                    }
                    mimetype = getImageMimeType(imagesBuilder, ckeyVec);
                    // debug("handleMirror: ckey "+ckey+" has mimetype: "+mimetype);
                    ckey=path2ckey(ckey, imagesBuilder);
                }

                log.debug("handleMirror: ckey "+ckey);
                ByteFieldContainer container = getCkeyNode(bul, ckey);
                if (container == null) {
                    log.debug("handleMirror: no icaches entry yet");
                }
                byte[] filebuf = container.value;
                log.debug("request size "+filebuf.length);
                String srcpath=getProperty("test1:path"); // ??? XXX should be changed!
                // Pass mimetype. should check on succes sor failure!
                saveImageAsisFile(srcpath,filename,filebuf,mimetype);

                // recover the correct source/dest properties for this mirror
                String sshpath=getProperty("sshpath");
                String dstuser=getProperty(dstserver+":user");
                String dsthost=getProperty(dstserver+":host");
                String dstpath=getProperty(dstserver+":path");

/*
                SCPcopy scpcopy=new SCPcopy(sshpath,dstuser,dsthost,dstpath);

                synchronized(syncobj) {
                    scpcopy.copy(srcpath,filename);
                }
*/
                // should be synchronized
                files.addElement(new aFile2Copy(dstuser,dsthost,dstpath,srcpath,filename,sshpath));

                // remove the tmp image file

                filenode.setValue("status",Netfiles.STATUS_DONE);
                filenode.commit();
                break;
            case Netfiles.STATUS_ON_ITS_WAY:  // On Its Way
                break;
            case Netfiles.STATUS_DONE:  // Done
                break;
            default:
                log.error("This cannot happen, contact your system administrator");
                break;
        }
        return true;
    }

    /**
     * Handles a images/main service request.
     * Schedules requests to mirror the file using {@link #doMainRequest}<br />
     * @param filenode the filenet node that contains the service request
     * @param status the current status of the node
     * @param ctype the type of change on that node ("c" : node was changed)
     * @return <code>true</code>
     */
    public boolean handleMain(MMObjectNode filenode,int status,String ctype) {
        switch(status) {
            case Netfiles.STATUS_REQUEST:  // Request
                filenode.setValue("status",Netfiles.STATUS_ON_ITS_WAY);
                filenode.commit();
                // do stuff
                doMainRequest(filenode);
                filenode.setValue("status",Netfiles.STATUS_DONE);
                filenode.commit();
                break;
            case Netfiles.STATUS_ON_ITS_WAY:  // On Its Way
                break;
            case Netfiles.STATUS_DONE:  // Done
                break;
            default:
                log.error("This cannot happen, contact your system administrator");
                break;
        }
        return true;
    }

    /**
     * Handles a main subservice on an image.
     * The image is scheduled to be sent to all appropriate mirrorsites for this service,
     * by setting the request status in the associated mirror nodes.
     * If no mirror nodes are associated with this page, nothing happens.
     * @param filenode the netfiles node with the original (main) request
     */
    public boolean doMainRequest(MMObjectNode filenode) {
        log.debug("doMainRequest for "+filenode.getIntValue("number")+" "+filenode.getStringValue("filename"));
        // so this file has changed probably, check if the file is ready on
        // disk and set the mirrors to dirty/request.
        String filename = filenode.getStringValue("filename");
        String service = filenode.getStringValue("service");

        // find and change all the mirror node so they get resend
        Netfiles bul=(Netfiles)Vwms.getMMBase().getMMObject("netfiles");
        Enumeration e=bul.search("WHERE filename='"+filename+"' AND service='"+service+"' AND subservice='mirror'");
        if (!e.hasMoreElements()) {
            log.debug("doMainRequest: No mirror nodes found for : "+filenode.toString()+" !!");
        }
        while (e.hasMoreElements()) {
            MMObjectNode mirrornode=(MMObjectNode)e.nextElement();
            log.debug("doMainRequest sending change for "+mirrornode.getIntValue("number"));
            mirrornode.setValue("status",Netfiles.STATUS_REQUEST);
            mirrornode.commit();
        }
        return true;
    }

    /**
     * Schedules a netfile object to be send to its mirror sites.
     * The routine searches the appropriate netfile node, and sets its status to 'request'.
     * If a node does not exits, a new node is created. In the latter case, the system also creates mirrornodes
     * for each mirrorsite associated with this service. (actually, it creates one mirrornode for a vpro-server,
     * but this should be altered).
     * @param service the service to be performed
     * @param subservice the subservice to be performed
     * @param filename the filename to service
     */
    public synchronized void handleMainCheck(String service,String subservice,String filename) {
        Netfiles bul=(Netfiles)Vwms.getMMBase().getMMObject("netfiles");
        Enumeration e=bul.search("WHERE filename='"+filename+"' AND service='"+service+"' AND subservice='"+subservice+"'");
        if (e.hasMoreElements()) {
            log.debug("handleMainCheck: existing file");
            MMObjectNode mainnode=(MMObjectNode)e.nextElement();
            int currentstatus=mainnode.getIntValue("status");
            if (currentstatus>Netfiles.STATUS_ON_ITS_WAY) { // check only the ones that are done
                mainnode.setValue("status",Netfiles.STATUS_REQUEST);
                mainnode.commit();
            }
        } else {
            log.debug("handleMainCheck: new file");
            MMObjectNode mainnode=bul.getNewNode("system");
            mainnode.setValue("filename",filename);
            mainnode.setValue("mmserver","test1"); // eeks!
            mainnode.setValue("service",service);
            mainnode.setValue("subservice",subservice);
            mainnode.setValue("status",Netfiles.STATUS_DONE);
            mainnode.setValue("filesize",-1);
            bul.insert("system",mainnode);

            // create mirror nodes
            // should use same getMirrorNodes() system as PageMaster
            mainnode=bul.getNewNode("system");
            mainnode.setValue("filename",filename);
            mainnode.setValue("mmserver","omroep"); // aaaaaarghgh
            mainnode.setValue("service",service);
            mainnode.setValue("subservice","mirror");
            mainnode.setValue("status",Netfiles.STATUS_REQUEST);
            mainnode.setValue("filesize",-1);
            bul.insert("system",mainnode);
        }
    }

    /**
     * Retrieves a named property of a server.
     * Should use the same system as PageMaster (retrieve data from MSMerver).
     * @param key name of the property to retrieve
     * @return the property value
     */
    public String getProperty(String key) {
        if (properties==null) initProperties();
        return (String)properties.get(key);
    }

    /**
     * Initializes server properties.
     * @deprecated (vpro specific code)
     */
    private void initProperties() {
        properties=new Hashtable();
        properties.put("sshpath","/usr/local/bin");
        properties.put("omroep:user","vpro");
        properties.put("omroep:host","vpro.omroep.nl");
        properties.put("omroep:path","/bigdisk/htdocs/");
        properties.put("test1:path","/usr/local/log/james/scancache/PAGE");
    }

    /**
     * Stores an array byte (presumably an image) as a asis file.
     * @param path path of the asis file
     * @param filename name of the asis file
     * @param value the bytearray to store
     * @param mimetype mimetype of the byte array, i.e. image/jpeg
     * @return <code>true</code> (what if it fails??)
     */
    private boolean saveImageAsisFile(String path,String filename,byte[] value, String mimetype) {
        String header="Status: 200 OK";
        // header+="\r\nContent-type: image/jpeg";
        header+="\r\nContent-type: "+mimetype;
        header+="\r\nContent-length: "+value.length;
        header+="\r\n\r\n";

        File sfile = new File(path+filename);
        try {
            DataOutputStream scan = new DataOutputStream(new FileOutputStream(sfile));
            scan.writeBytes(header);
            scan.write(value);
            scan.flush();
            scan.close();
        } catch(Exception e) {
            log.error(e.getMessage());
            log.error(Logging.stackTrace(e));
        }
        return true;
    }

    /**
     * Converts an asis filename to a key that can be used to retrieve an Icache node
     * @param path the asis fielname
     * @param imageBuilder Reference to the Images builder, unused
     * @return an appriopriate Icache key
     */
    private String path2ckey(String path, Images imageBuilder) {
        StringTokenizer tok = new StringTokenizer(path,"+\n\r");
        String ckey=tok.nextToken();
        //ckey = ""+imageBuilder.convertAlias(ckey);
        while (tok.hasMoreTokens()) {
            String key=tok.nextToken();
            ckey+=key;
        }
        return ckey;
    }

    /**
    * Will return {@link #defaultImageType} as default type, or one of the strings in params, must contain the following "f(type)" where type will be returned
     * @param params a <code>List</code> of <code>String</code>s, which could contain the "f(type)" string
     * @return {@link #defaultImageType} by default, or the first occurence of "f(type)"
     *
     *
     */
    private String getImageMimeType(Images images, List<Object> params) {
        String format = null;
        String key;

        // WHY the itype colomn isn't used?

        for (Object object : params) {
            key = (String)object;

            // look if our string is long enough...
            if(key != null && key.length() > 2) {
                // first look if we start with an "f("... format is f(gif)
                if(key.startsWith("f(")) {
                    // one search function remaining...
                    int pos = key.lastIndexOf(')');
                    // we know for sure that our "(" is at pos 1, so we can define this hard...
                    format = key.substring(2, pos);
                    break;
                }
            }
        }
        if (format == null) format = images.getDefaultImageType();
        String mimetype = Imaging.getMimeTypeByExtension(format);
        if (log.isDebugEnabled()) {
            log.debug("getImageMimeType: getMMBase().getMimeType(" + format + ") = " + mimetype);
        }
        return mimetype;
    }

}
