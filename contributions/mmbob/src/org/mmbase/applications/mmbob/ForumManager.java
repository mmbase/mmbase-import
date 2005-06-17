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

import org.xml.sax.InputSource;

import org.mmbase.util.*;
import org.mmbase.util.logging.*;
import org.mmbase.util.xml.*;
import org.mmbase.module.core.*;
import org.mmbase.bridge.*;
import org.mmbase.storage.*;
import org.mmbase.storage.search.*;

import org.mmbase.util.FileWatcher;


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
    private static ForumEmailSender emailsender;
    private static ForumsConfig config;

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
	if (!running) {
        readConfig();
        cloud = getCloud();
        if (!running) {
            forumnodemanager = cloud.getNodeManager("forums");
            if (forumnodemanager == null) {
                log.error("Can't load forums nodemanager from mmbase");
            }

            readForums();

            // start the mmbase syncer
            //was 10 * 1000
            syncfast = new ForumMMBaseSyncer(10 * 1000, 50, 500);
            syncslow = new ForumMMBaseSyncer(5 * 60 * 1000, 50, 2000);
            swapmanager = new ForumSwapManager(1 * 60 * 1000);
            ForumEmailSender emailsender = new ForumEmailSender();
            running = true;
        }
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
     * @return all the forums
     */
    public static Enumeration getForums() {
        return forums.elements();
    }

    /**
     * Get a forum by it's MMBase node number
     *
     * @param id MMBase node number of the forum
     * @return forum
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
     * Remove the given deleted node from the sync queues
     * ToDo: very ugly need to be beter
     * @param node
     */
    public static void nodeDeleted(org.mmbase.bridge.Node node) {
        if (syncfast != null) {
            syncfast.nodeDeleted(node);
        }
        if (syncslow != null) {
            syncslow.nodeDeleted(node);
        }
    }

    /**
     * ToDo: Write docs!
     * @param id
     * @return
     */
    protected static Map getNamePassword(String id) {
	return config.getNamePassword(id);
    }

    public static String getDefaultPassword() {
	return config.getDefaultPassword();
    }

    public static String getDefaultAccount() {
	return config.getDefaultAccount();
    }

    public static String getLanguage() {
	return "en";
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
        try {
            readConfig(file);
        } catch (FileNotFoundException ex) {
        }

    }

    private static void readConfig (File file) throws java.io.FileNotFoundException{
        if (file.exists()) {
            XMLBasicReader reader = new XMLBasicReader(new InputSource(new FileInputStream(file)), ForumManager.class);
            if (reader != null) {
// decode forums
                for (Enumeration ns = reader.getChildElements("mmbobconfig", "forums"); ns.hasMoreElements();) {
                    Element n = (Element) ns.nextElement();
		    if (n != null) {
			config =  new ForumsConfig(reader,n);
		    }
                }
            }
            configWatcher.add(file);
            configWatcher.setDelay(10 * 1000); // check every 10 secs if config changed
            configWatcher.start();
        }
    }

    private static FileWatcher configWatcher = new FileWatcher (true) {
            protected void onChange(File file) {
                try {
                    readConfig(file);
       Enumeration e = forums.elements();
        while (e.hasMoreElements()) {
            // for now all forums main nodes are loaded so
            // we just call them all for a maintain
            Forum f = (Forum) e.nextElement();
            f.resetConfig();
        }
                } catch (FileNotFoundException ex) {
                }
            }
        };


    public static void saveConfig() {
        String filename = MMBaseContext.getConfigPath() + File.separator + "mmbob" + File.separator + "mmbob.xml";
 	if (config != null) {
		config.save(filename);
	} else {
		log.info("missing config file, can't save");
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

   public static void setQuotaMax(String maxs) {
	config.setQuotaMax(maxs);
   }


   public static void setQuotaMax(int max) {
	config.setQuotaMax(max);
   }

   public static void setQuotaSoftWarning(String sws) {
	config.setQuotaSoftWarning(sws);
   }


   public static void setQuotaWarning(String ws) {
	config.setQuotaWarning(ws);
   }

   public static int getQuotaMax() {
	return config.getQuotaMax();
   }

   public static int getQuotaSoftWarning() {
	return config.getQuotaSoftWarning();
   }

   public static int getQuotaWarning() {
	return config.getQuotaWarning();
   }

   public static ForumConfig getForumConfig(String name) {
	return config.getForumConfig(name);
   }

   public static String getAccountCreationType() {
       return config.getAccountCreationType();
   }

   public static String getAccountRemovalType() {
       return config.getAccountRemovalType();
   }

   public static String getLoginModeType() {
       return config.getLoginModeType();
   }

   public static void setLoginModeType(String mode) {
       config.setLoginModeType(mode);
   }

   public static String getLogoutModeType() {
       return config.getLogoutModeType();
   }

   public static void setLogoutModeType(String mode) {
       config.setLogoutModeType(mode);
   }

   public static String getGuestReadModeType() {
       return config.getGuestReadModeType();
   }

   public static void setGuestReadModeType(String mode) {
       config.setGuestReadModeType(mode);
   }

   public static String getGuestWriteModeType() {
       return config.getGuestWriteModeType();
   }

   public static void setGuestWriteModeType(String mode) {
       config.setGuestWriteModeType(mode);
   }

    public static int getPreloadChangedThreadsTime() {
        return config.getPreloadChangedThreadsTime();
    }

    public static int getSwapoutUnusedThreadsTime() {
        return config.getSwapoutUnusedThreadsTime();
    }

    public static String getXSLTPostingsOdd() {
        return config.getXSLTPostingsOdd();
    }

    public static String getXSLTPostingsEven() {
        return config.getXSLTPostingsEven();
    }

    public static String getAvatarsUploadEnabled() {
        return config.getAvatarsUploadEnabled();
    }

   public static void setAvatarsUploadEnabled(String mode) {
       config.setAvatarsUploadEnabled(mode);
   }

    public static String getAvatarsGalleryEnabled() {
        return config.getAvatarsGalleryEnabled();
    }

   public static void setAvatarsGalleryEnabled(String mode) {
       config.setAvatarsGalleryEnabled(mode);
   }

   public static void setContactInfoEnabled(String mode) {
       config.setContactInfoEnabled(mode);
   }

   public static void setSmileysEnabled(String mode) {
       config.setSmileysEnabled(mode);
   }

   public static void setPrivateMessagesEnabled(String mode) {
       config.setPrivateMessagesEnabled(mode);
   }

   public static void setPostingsPerPage(String number) {
       config.setPostingsPerPage(number);
   }

    public static String getContactInfoEnabled() {
        return config.getContactInfoEnabled();
    }

    public static String getSmileysEnabled() {
        return config.getSmileysEnabled();
    }

    public static String getPrivateMessagesEnabled() {
        return config.getPrivateMessagesEnabled();
    }

    public static int getPostingsPerPage() {
        return config.getPostingsPerPage();
    }

    public static String getFromEmailAddress() {
        return config.getFromEmailAddress();
    }

    public static String getHeaderPath() {
        return config.getHeaderPath();
    }

    public static String getFooterPath() {
        return config.getFooterPath();
    }

    public int getPostThreadLoadedCount() {
        int count = 0;
        Enumeration i = forums.elements();
        while (i.hasMoreElements()) {
                Forum forum = (Forum)i.nextElement();
                count += forum.getPostThreadLoadedCount();
        }
        return count;
    }

   public static String filterContent(String body) {
        if (config.getFilterWords()!=null) {
		return filterContent(config.getFilterWords(),body);
        } else {
		return body;
	}
   }


   public static String filterContent(HashMap words,String body) {
        if (words!=null) {
            	StringObject obj=new StringObject(body);
        	Iterator i = words.keySet().iterator();
        	while (i.hasNext()) {
            		String key = (String)i.next();
            		String value = (String)words.get(key);
                	obj.replace(key, value);
		}
                return obj.toString();
        } else {
		return body;
	}
   }

   public static int getSpeedPostTime() {
	return config.getSpeedPostTime();
   }

   public static int getPostingsOverflowPostarea() {
	return config.getPostingsOverflowPostarea();
   }

   public static int getPostingsOverflowThreadpage() {
	return config.getPostingsOverflowThreadpage();
   }

   public static String getEmailtext(String role) {
	return config.getEmailtext(role);
   }

   public static String getExternalRootUrl() {
	return config.getExternalRootUrl();
   }

   public static boolean getReplyOnEachPage() {
	return config.getReplyOnEachPage();
   }

}
