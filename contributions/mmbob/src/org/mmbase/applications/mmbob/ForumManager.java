/*
 
This software is OSI Certified Open Source Software.
OSI Certified is a certification mark of the Open Source Initiative.
 
The license (Mozilla version 1.0) can be read at the MMBase site.
See http://www.MMBase.org/license
 
 */

package org.mmbase.applications.mmbob;

import org.w3c.dom.*;

import java.io.*;
import java.util.*;

import org.mmbase.util.*;
import org.mmbase.util.logging.*;
import org.mmbase.util.xml.*;
import org.mmbase.module.core.*;
import org.mmbase.bridge.*;
import org.mmbase.storage.*;
import org.mmbase.storage.search.*;


/**
 * forumManager
 * ToDo: Write docs!
 *
 * @author Daniel Ockeloen (MMBased)
 */
public class ForumManager {
    private static Logger log = Logging.getLoggerInstance(ForumManager.class);
  
    private static Hashtable forums=new Hashtable();
    private static Hashtable forumnamecache=new Hashtable();
    private static ForumMMBaseSyncer syncfast,syncslow;
    private static ForumSwapManager swapmanager;
    private static String defaultaccount, defaultpassword;

    public static final int FASTSYNC = 1;
    public static final int SLOWSYNC = 2;

    /**
     * DTD resource filename of the mmbob config file DTD version 1.0
     */
    public static final String DTD_MMBOBCONFIG_1_0 = "mmbobconfig_1_0.dtd";

    /**
     * Public ID of the mmbobconfig DTD version 1.0
     */
    public static final String PUBLIC_ID_MMBOBCONFIG_1_0 = "-//MMBase//DTD mmbob config 1.0//EN";

    /**
     * Register the Public Ids for DTDs used by DatabaseReader
     * This method is called by XMLEntityResolver.
     */
    public static void registerPublicIDs() {
        XMLEntityResolver.registerPublicID(PUBLIC_ID_MMBOBCONFIG_1_0, DTD_MMBOBCONFIG_1_0, ForumManager.class);
    }

    // is the install manager active (for dependencies reasons)
    private static boolean running = false;

    private static NodeManager forumnodemanager;

    private static Cloud cloud;

    /**
     *  Initialization
     */
    public static synchronized void init() {
        readConfig();
        cloud = getCloud();
        if (!running) {
            forumnodemanager = cloud.getNodeManager("forums");
            if (forumnodemanager == null) {
                log.error("Can't load forums nodemanager from mmbase");
            }

            readForums();

            // start the mmbase syncer
            syncfast = new ForumMMBaseSyncer(10 * 1000, 50, 500);
            syncslow = new ForumMMBaseSyncer(5 * 60 * 1000, 50, 2000);
            swapmanager = new ForumSwapManager(1 * 60 * 1000);
            running = true;
        }
    }

    /**
     * Determine if the forumManager passed it's initilization
     *
     * @return <code>true</code> if the forumManager passed it's initilization, <code>false</code> if it isn't
     */
    public static boolean isRunning() {
        return running;
    }

    /**
     * Get the number of forums in the cloud
     *
     * @return number of forums
     */
    public static int getForumsCount() {
        return forums.size();
    }

    /**
     * Get an enumeration of all the forums in the cloud
     *
     * @return
     */
    public static Enumeration getForums() {
        return forums.elements();
    }

    /**
     * Get a forum by it's MMBase node number
     *
     * @param id MMBase node number of the forum
     * @return
     */
    public static Forum getForum(int id) {
        Forum f = (Forum) forums.get(new Integer(id));
        if (f != null) {
            return f;
        }
        return null;
    }

    /**
     * Remove a forum by it's MMBase node number
     *
     * @param id MMBase node number of the forum
     * @return <code>true</code> if the remove action was successful
     */
    public static boolean removeForum(int id) {
        Forum f = (Forum) forums.get(new Integer(id));
        f.remove();
        forumnamecache.remove(new Integer(id));
        forums.remove(new Integer(id));
        return true;
    }

    /**
     * Get a forum by it's MMBase node number
     *
     * @param id  MMBase node number of the forum
     * @return
     */
    public static Forum getForum(String id) {
        try {
            int idi = Integer.parseInt(id);
            return getForum(idi);
        } catch (Exception e) {
            // maybe its a alias ?
            Integer nid = (Integer) forumnamecache.get(id);
            if (nid != null) {
                return getForum(nid.intValue());
            } else {
                org.mmbase.bridge.Node node = cloud.getNode(id);
                if (node != null) {
                    forumnamecache.put(id, new Integer(node.getNumber()));
                    return getForum(node.getNumber());
                }
            }
        }
        return null;
    }

    /**
     * Called on init.
     * Fill the forums hastable with all forums that are found in the cloud.
     */
    private static void readForums() {
        NodeQuery query = forumnodemanager.createQuery();
        org.mmbase.bridge.NodeList result = forumnodemanager.getList(query);
        NodeIterator i = result.nodeIterator();
        while (i.hasNext()) {
            org.mmbase.bridge.Node n = (org.mmbase.bridge.Node) i.nextNode();
            Forum f = new Forum(n);
            f.setId(n.getNumber());
            f.setName(n.getStringValue("name"));
            forums.put(new Integer(f.getId()), f);
        }
    }

    /**
     * Create a new forum
     *
     * @param name Name of the new forum
     * @param language Language of the new forum
     * @param description Description of the new forum
     * @param account account of the creator of the new forum
     * @param password password of the creator of the new forum
     * @return The MMBase node number of the newly created forum node
     */
    public static int newForum(String name, String language, String description, String account, String password) {
        org.mmbase.bridge.Node node = forumnodemanager.createNode();
        node.setStringValue("name", name);
        node.setStringValue("language", language);
        node.setStringValue("description", description);
        node.setIntValue("createtime", (int) (System.currentTimeMillis()));
        node.setIntValue("viewcount", 0);
        node.setIntValue("postcount", 0);
        node.setIntValue("postthreadcount", 0);
        node.setIntValue("postercount", 0);

        node.commit();

        Forum f = new Forum(node);
        f.setId(node.getNumber());
        f.setName(node.getStringValue("name"));

        forums.put(new Integer(f.getId()), f);

        Poster p = f.createPoster(account, password);

        f.addAdministrator(p);
        return node.getNumber();
    }

    /**
     * ToDo: Write docs!
     * @param node
     * @param queue
     */
    public static void syncNode(org.mmbase.bridge.Node node, int queue) {
        if (queue == FASTSYNC && syncfast != null) {
            syncfast.syncNode(node);
        } else if (queue == SLOWSYNC && syncslow != null) {
            syncslow.syncNode(node);
        }
    }

    /**
     * ToDo: Write docs!
     * very ugly need to be beter
     * @param node
     */
    public static void nodeDeleted(org.mmbase.bridge.Node node) {
        if (syncfast != null) {
            syncfast.nodeDeleted(node);
        } else if (syncslow != null) {
            syncslow.nodeDeleted(node);
        }
    }

    /**
     * ToDo: Write docs!
     * @param id
     * @return
     */
    protected static Map getNamePassword(String id) {
        Map user = new HashMap();
        if (id.equals("default")) {
            user.put("username", defaultaccount);
            user.put("password", defaultpassword);
        }
        return user;
    }

    /**
     * ToDo: Write docs!
     * @return
     */
    public static Cloud getCloud() {
        if (cloud == null) cloud = ContextProvider.getDefaultCloudContext().getCloud("mmbase", "name/password", getNamePassword("default"));
        return cloud;
    }


    /**
     * ToDo: Write docs!
     * Called on init.
     * reads configfile
     */
    public static void readConfig() {
        String filename = MMBaseContext.getConfigPath() + File.separator + "mmbob" + File.separator + "mmbob.xml";

        File file = new File(filename);
        if (file.exists()) {
            XMLBasicReader reader = new XMLBasicReader(filename, ForumManager.class);
            if (reader != null) {
// decode forums
                for (Enumeration ns = reader.getChildElements("mmbobconfig", "forum"); ns.hasMoreElements();) {
                    Element n = (Element) ns.nextElement();
                    NamedNodeMap nm = n.getAttributes();
                    if (nm != null) {
                        String id = "default";
                        String account = "admin";
                        String password = "admin2k";
// decode name
                        org.w3c.dom.Node n3 = nm.getNamedItem("id");
                        if (n3 != null) {
                            id = n3.getNodeValue();
                        }
// decode account
                        n3 = nm.getNamedItem("account");
                        if (n3 != null) {
                            account = n3.getNodeValue();
                        }
// decode password
                        n3 = nm.getNamedItem("password");
                        if (n3 != null) {
                            password = n3.getNodeValue();
                        }
                        //log.info("ID="+id+" account="+account+" password="+password);
                        if (id.equals("default")) {
                            defaultaccount = account;
                            defaultpassword = password;
                        }

                        for (Enumeration ns2 = reader.getChildElements(n, "generatedata"); ns2.hasMoreElements();) {
                            Element n2 = (Element) ns2.nextElement();
                            nm = n2.getAttributes();
                            if (nm != null) {
                                String role = null;
                                String dfile = null;
                                String tokenizer = null;
                                n3 = nm.getNamedItem("role");
                                if (n3 != null) {
                                    role = n3.getNodeValue();
                                }
                                n3 = nm.getNamedItem("file");
                                if (n3 != null) {
                                    dfile = n3.getNodeValue();
                                }
                                n3 = nm.getNamedItem("tokenizer");
                                if (n3 != null) {
                                    tokenizer = n3.getNodeValue();
                                }
                                org.mmbase.applications.mmbob.generate.Handler.setGenerateFile(role, dfile, tokenizer);
                            }
                        }

                    }
                }
            }
        }
    }

    /**
     * ToDo: Write docs!
     */
    public static void maintainMemoryCaches() {
        Enumeration e = forums.elements();
        while (e.hasMoreElements()) {
            // for now all forums main nodes are loaded so
            // we just call them all for a maintain
            Forum f = (Forum) e.nextElement();
            f.maintainMemoryCaches();
        }
    }
}
