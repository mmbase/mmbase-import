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
 * @version $Id: ForumManager.java,v 1.36 2007-02-21 11:22:14 michiel Exp $
 */
public class ForumManager {
    private static final Logger log = Logging.getLoggerInstance(ForumManager.class);

    private static Map<Integer, Forum> forums = new Hashtable<Integer, Forum>(); // ConcurrentHashMap?
    private static Map<String, Integer> forumNameCache = new Hashtable<String, Integer>(); // ConcurrentHashMap
    private static ForumMMBaseSyncer syncfast,syncslow;
    private static ForumSwapManager swapmanager;
    private static ExternalProfilesManager externalprofilesmanager;
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
            if (! running) {
                forumnodemanager = cloud.getNodeManager("forums");
                if (forumnodemanager == null) {
                    log.error("Can't load forums nodemanager from mmbase");
                }

                externalprofilesmanager =  new ExternalProfilesManager(1 * 60 * 1000);

                readForums();

                // start the mmbase syncer
                //was 10 * 1000
                DocumentReader reader = config.getReader();
                Element element = config.getElement();

                for (Element syncerElement : ForumsConfig.list(reader.getChildElements(element, "syncer"))) {
                    String type = syncerElement.getAttribute("type");
                    if ("slow".equals(type)) {
                        syncslow = new ForumMMBaseSyncer(Integer.parseInt(syncerElement.getAttribute("sleepTime")), 50,
                                                         Integer.parseInt(syncerElement.getAttribute("delayTime")));
                    } else if ("fast".equals(type)) {
                        syncfast = new ForumMMBaseSyncer(Integer.parseInt(syncerElement.getAttribute("sleepTime")), 50,
                                                         Integer.parseInt(syncerElement.getAttribute("delayTime")));
                    } else {
                        log.warn("Unknown value for type attribute " + type);
                    }
                }
                if (syncfast == null) syncfast = new ForumMMBaseSyncer(10 * 1000, 50, 500);
                if (syncslow == null) syncslow = new ForumMMBaseSyncer(5 * 60 * 1000, 50, 2000);
                log.service("Syncer for 'fast' objects: " + syncfast);
                log.service("Syncer for 'slow' objects: " + syncslow);

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
    public static Enumeration<Forum> getForums() {
        return Collections.enumeration(forums.values());
    }

    /**
     * Get a forum by it's MMBase node number
     *
     * @param id MMBase node number of the forum
     * @return forum
     */
    public static Forum getForum(int id) {
        return forums.get(Integer.valueOf(id));
    }


    public static Forum getForumByAlias(String key) {
        for (Forum f : forums.values()) {
            if (f.getAlias()!=null && key.indexOf(f.getAlias())!=-1) {
                return f;
            }
        }
       return null;
    }


    public static Forum getForumCloneMaster() {
        for (Forum f : forums.values()) {
            if (f.getCloneMaster()) {
                return f;
            }
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
        Forum f = forums.get(Integer.valueOf(id));
        f.remove();
        forumNameCache.remove(Integer.valueOf(id));
        forums.remove(Integer.valueOf(id));
        return true;
    }

    /**
     * Get a forum by it's MMBase node number
     *
     * @param id  MMBase node number of the forum
     */
    public static Forum getForum(String id) {
        try {
            int idi = Integer.parseInt(id);
            return getForum(idi);
        } catch (Exception e) {
            // maybe its a alias ?
            Integer nid = forumNameCache.get(id);
            if (nid != null) {
                return getForum(nid.intValue());
            } else {
                org.mmbase.bridge.Node node = cloud.getNode(id);
                if (node != null) {
                    forumNameCache.put(id, Integer.valueOf(node.getNumber()));
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
            forums.put(Integer.valueOf(f.getId()), f);
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
    public static int newForum(String name, String language, String description, String account, String password, String nick, String email) {
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

        forums.put(Integer.valueOf(f.getId()), f);

        Poster p = f.createPoster(account, password);

	p.setEmail(email);
	p.savePoster();



	// check if we have a clone master
	Forum cf = getForumCloneMaster();
	if (cf != null) {
            // ok we have a clone master copy the wanted settings
            f.setPostingsPerPage(cf.getPostingsPerPage());
            f.setPostingsOverflowPostArea(cf.getPostingsOverflowPostArea());
            f.setPostingsOverflowThreadPage(cf.getPostingsOverflowThreadPage());
            f.setLanguage(cf.getLanguage());
            f.setLoginSystemType(cf.getLoginSystemType());
            f.setLoginModeType(cf.getLoginModeType());
            f.setLogoutModeType(cf.getLogoutModeType());
            f.setGuestReadModeType(cf.getGuestReadModeType());
            f.setGuestWriteModeType(cf.getGuestWriteModeType());
            f.setNavigationMethod(cf.getNavigationMethod());
            f.setSpeedPostTime(cf.getSpeedPostTime());
            f.setReplyOnEachPage(cf.getReplyOnEachPage());
            f.save(); // some basic settings, weird
            
            // check if we need to copy ProfileDefs
            Iterator i = cf.getProfileDefs();
            if (i!=null) {
                while (i.hasNext()) {
                    ProfileEntryDef pd = (ProfileEntryDef) i.next();
                    f.addProfileDef(pd);
                    if (pd.getName().equals("nick")) {
                        // kinda trick, we need a way to make forums.jsp optional for this *sigh*
                        if (nick!=null && !nick.equals("")) p.setProfileValue("nick",nick);
                    }
                }
            }
            f.saveConfig();
	}
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
             log.debug("deleting node " + node.getNumber() + " from fast sync");
             syncfast.nodeDeleted(node);
             if (log.isDebugEnabled()) {
                 log.debug("contents of queu: " + syncfast.printCurrentContent());
             }
         }
         if (syncslow != null) {
            log.debug("deleting node "+node.getNumber()+" from slow sync");
            syncslow.nodeDeleted(node);
            if (log.isDebugEnabled()) {
                 log.debug("contents of que: " + syncslow.printCurrentContent());
            }
         }
     }



    /**
     * ToDo: Write docs!
     * @param id
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
     */
    public static Cloud getCloud() {
        if (cloud == null) cloud = ContextProvider.getDefaultCloudContext().getCloud("mmbase", "class", getNamePassword("default"));
        return cloud;
    }


    /**
     * ToDo: Write docs!
     * Called on init.
     * reads configfile
     */
    public static void readConfig() {
	try {
            InputSource is = ResourceLoader.getConfigurationRoot().getInputSource("mmbob/mmbob.xml");
            DocumentReader reader = new DocumentReader(is, ForumManager.class);
            // decode forums
            for (Element n : ForumsConfig.list(reader.getChildElements("mmbobconfig", "forums"))) {
                // so it seems that you can configure more than one forum, but only the last one is
                // actually used. (@id is ignored).
                if (n != null) {
                    config =  new ForumsConfig(reader, n);
                }
            }
	} catch (Exception e) {
            log.error(e.getMessage(), e);
	}
        if (config == null) {
            log.error("No correct mmbob.xml configuration found");
            config = new ForumsConfig();
        }
    }


    public static void saveConfig() {
	log.info("SAVE CONFIG !");
 	if (config != null) {
            config.save();
	} else {
            log.info("missing config file, can't save");
	}
    }

    /**
     * ToDo: Write docs!
     */
    public static void maintainMemoryCaches() {
        for (Forum f : forums.values()) {
            // for now all forums main nodes are loaded so
            // we just call them all for a maintain
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

   public static String getLoginSystemType() {
       return config.getLoginSystemType();
   }

   public static void setLoginModeType(String mode) {
       config.setLoginModeType(mode);
   }

   public static void setLoginSystemType(String system) {
       config.setLoginSystemType(system);
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

   public static String getThreadStartLevel() {
       return config.getThreadStartLevel();
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

    public static String getNavigationMethod() {
        return config.getNavigationMethod();
    }

    public int getPostThreadLoadedCount() {
        int count = 0;
        for (Forum forum : forums.values()) {
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

   public static HashMap getFilterWords() {
        if (config.getFilterWords()!=null) {
		return config.getFilterWords();
	} else {
		return null;
	}
   }


   public static void addWordFilter(String name,String value) {
        if (config.getFilterWords()!=null) {
		config.addWordFilter(name,value);
	}
   }


   public static void removeWordFilter(String name) {
        if (config.getFilterWords()!=null) {
		config.removeWordFilter(name);
	}
   }


   public static String longWordWrap(String body) {
	StringTokenizer tok = new StringTokenizer(body," \n\r\t",true);
	String newbody = "";
	while (tok.hasMoreTokens()) {
		String tmp = tok.nextToken();
		int len = tmp.length();
		boolean first = true;
		if (len>45) {
			while (len>45) {
				if (first) {
					newbody = newbody + tmp.substring(0,44);
					first = false;
				} else {
					newbody = newbody + "- "+tmp.substring(0,44);
				}
				log.info("newbody="+newbody);
				tmp = tmp.substring(45);
				log.info("tmp="+tmp);
				len=tmp.length();
			}
			newbody = newbody + "- "+tmp;
		} else {
			newbody = newbody+tmp;
		}
	}
	return newbody;
   }

   public static String filterContent(Map<String, String> words, String body) {
       body = longWordWrap(body);
       StringObject obj = new StringObject(body);
       if (words != null) {
           // should iterator over _entrySet_ !
           Iterator<String> i = words.keySet().iterator();
           while (i.hasNext()) {
               String key = i.next();
               String value = words.get(key);
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

   public static int getPostingsOverflowPostArea() {
	return config.getPostingsOverflowPostArea();
   }

   public static int getPostingsOverflowThreadPage() {
	return config.getPostingsOverflowThreadPage();
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
