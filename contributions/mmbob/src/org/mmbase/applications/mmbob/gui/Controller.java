/*

This software is OSI Certified Open Source Software.
OSI Certified is a certification mark of the Open Source Initiative.

The license (Mozilla version 1.0) can be read at the MMBase site.
See http://www.MMBase.org/license

*/
package org.mmbase.applications.mmbob.gui;

import java.io.*;
import java.net.URL;
import java.text.DateFormat;
import java.util.*;
import java.util.jar.*;

import org.mmbase.bridge.*;
import org.mmbase.module.core.*;
import org.mmbase.bridge.implementation.*;
import org.mmbase.util.logging.*;
import org.mmbase.util.*;

import org.mmbase.applications.mmbob.*;
import org.mmbase.applications.mmbob.util.transformers.*;


/**
 * @author Daniel Ockeloen
 * @version $Id: guiController.java
 */
public class Controller {

    private static Logger log = Logging.getLoggerInstance(Controller.class.getName());
    private static Cloud cloud;
    NodeManager manager;
    CloudContext context;

    /**
     * Constructor
     */
    public Controller() {
        cloud = LocalContext.getCloudContext().getCloud("mmbase");

        // hack needs to be solved
        manager = cloud.getNodeManager("typedef");
        if (manager == null) log.error("Can't access builder typedef");
        context = LocalContext.getCloudContext();

        // start the ForumManager
        ForumManager.init();
    }

    /**
     * Get the PostAreas of the given forum
     *
     * @param id  MMBase node number of the forum
     * @param sactiveid MMBase node number of the active poster
     * @return List of postareas that matches the given params
     */
    public List getPostAreas(String id, String sactiveid,String mode) {
        List list = new ArrayList();
        try {
            int activeid = Integer.parseInt(sactiveid);
            VirtualBuilder builder = new VirtualBuilder(MMBase.getMMBase());

            Forum f = ForumManager.getForum(id);
            if (f != null) {
                Enumeration e = f.getPostAreas();
                while (e.hasMoreElements()) {
                    PostArea area = (PostArea) e.nextElement();
                    MMObjectNode virtual = builder.getNewNode("admin");
                    virtual.setValue("name", area.getName());
                    virtual.setValue("description", area.getDescription());
                    virtual.setValue("id", area.getId());
                    virtual.setValue("postthreadcount", area.getPostThreadCount());
                    virtual.setValue("postcount", area.getPostCount());
                    virtual.setValue("viewcount", area.getViewCount());
                    virtual.setValue("lastposter", area.getLastPoster());
                    virtual.setValue("lastposttime", area.getLastPostTime());
                    virtual.setValue("lastsubject", area.getLastSubject());
                    virtual.setValue("moderators", area.getModeratorsLine("profile.jsp"));
                    virtual.setValue("lastposternumber",area.getLastPosterNumber());
                    virtual.setValue("lastpostnumber",area.getLastPostNumber());
                    virtual.setValue("lastpostthreadnumber",area.getLastPostThreadNumber());
	            virtual.setValue("guestreadmodetype", area.getGuestReadModeType());
       	     	    virtual.setValue("guestwritemodetype", area.getGuestWriteModeType());
            	    virtual.setValue("threadstartlevel", area.getThreadStartLevel());
		   if (mode.equals("stats")) {
            	   	 virtual.setValue("postthreadloadedcount", area.getPostThreadLoadedCount());
            	   	 virtual.setValue("postingsloadedcount", area.getPostingsLoadedCount());
                   	 virtual.setValue("memorysize", ((float)area.getMemorySize())/(1024*1024)+"MB");
		   }
                    list.add(virtual);

                    if (activeid != -1) {
                        Poster ap = f.getPoster(activeid);
                        ap.signalSeen();
                        addActiveInfo(virtual, ap);
                        if (ap != null && f.isAdministrator(ap.getNick())) {
                            virtual.setValue("isadministrator", "true");
                        } else {
                            virtual.setValue("isadministrator", "false");
                        }
                    }
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return list;
    }


    /**
     * Get the PostAreas of the given forum
     *
     * @param id  MMBase node number of the forum
     * @param sactiveid MMBase node number of the active poster
     * @return List of postareas that matches the given params
     */
    public List getTreePostAreas(String id, String sactiveid,String tree) {
        List list = new ArrayList();
        try {
            int activeid = Integer.parseInt(sactiveid);
            VirtualBuilder builder = new VirtualBuilder(MMBase.getMMBase());

            Forum f = ForumManager.getForum(id);
            if (f != null) {
                SubArea sa = f.getSubArea(tree);
                Iterator i = sa.getAreas();
                while (i.hasNext()) {
                    PostArea area = (PostArea) i.next();
                    MMObjectNode virtual = builder.getNewNode("admin");
                    virtual.setValue("nodetype","area");
                    virtual.setValue("name", area.getName());
                    virtual.setValue("shortname", area.getShortName());
                    virtual.setValue("description", area.getDescription());
                    virtual.setValue("id", area.getId());
                    virtual.setValue("postthreadcount", area.getPostThreadCount());
                    virtual.setValue("postcount", area.getPostCount());
                    virtual.setValue("viewcount", area.getViewCount());
                    virtual.setValue("lastposter", area.getLastPoster());
                    virtual.setValue("lastposttime", area.getLastPostTime());
                    virtual.setValue("lastsubject", area.getLastSubject());
                    virtual.setValue("moderators", area.getModeratorsLine("profile.jsp"));
                    virtual.setValue("lastposternumber",area.getLastPosterNumber());
                    virtual.setValue("lastpostnumber",area.getLastPostNumber());
                    virtual.setValue("lastpostthreadnumber",area.getLastPostThreadNumber());
	            virtual.setValue("guestreadmodetype", area.getGuestReadModeType());
       	     	    virtual.setValue("guestwritemodetype", area.getGuestWriteModeType());
            	    virtual.setValue("threadstartlevel", area.getThreadStartLevel());
                    list.add(virtual);

                    if (activeid != -1) {
                        Poster ap = f.getPoster(activeid);
                        ap.signalSeen();
                        addActiveInfo(virtual, ap);
                        if (ap != null && f.isAdministrator(ap.getNick())) {
                            virtual.setValue("isadministrator", "true");
                        } else {
                            virtual.setValue("isadministrator", "false");
                        }
                    }
                }
                i = sa.getSubAreas();
                while (i.hasNext()) {
                   sa = (SubArea) i.next();
                   MMObjectNode virtual = builder.getNewNode("admin");
                   virtual.setValue("nodetype","subarea");
                   virtual.setValue("name", sa.getName());
                   virtual.setValue("areacount", sa.getAreaCount());
                   virtual.setValue("postthreadcount", sa.getPostThreadCount());
                   virtual.setValue("postcount", sa.getPostCount());
                   virtual.setValue("viewcount", sa.getViewCount());
                   list.add(virtual);
               	   Iterator i2 = sa.getAreas();
               	   while (i2.hasNext()) {
                    	PostArea area = (PostArea) i2.next();
                    	virtual = builder.getNewNode("admin");
                    	virtual.setValue("nodetype","area");
                        virtual.setValue("shortname", area.getShortName());
                   	virtual.setValue("name", area.getName());
                    	virtual.setValue("description", area.getDescription());
                    	virtual.setValue("id", area.getId());
                    	virtual.setValue("postthreadcount", area.getPostThreadCount());
                    	virtual.setValue("postcount", area.getPostCount());
                    	virtual.setValue("viewcount", area.getViewCount());
                    	virtual.setValue("lastposter", area.getLastPoster());
                    	virtual.setValue("lastposttime", area.getLastPostTime());
                    	virtual.setValue("lastsubject", area.getLastSubject());
                    	virtual.setValue("moderators", area.getModeratorsLine("profile.jsp"));
                    	virtual.setValue("lastposternumber",area.getLastPosterNumber());
                    	virtual.setValue("lastpostnumber",area.getLastPostNumber());
                    	virtual.setValue("lastpostthreadnumber",area.getLastPostThreadNumber());
	            	virtual.setValue("guestreadmodetype", area.getGuestReadModeType());
       	     	    	virtual.setValue("guestwritemodetype", area.getGuestWriteModeType());
            	    	virtual.setValue("threadstartlevel", area.getThreadStartLevel());
                    	list.add(virtual);

                    	if (activeid != -1) {
                        	Poster ap = f.getPoster(activeid);
                        	ap.signalSeen();
                        	addActiveInfo(virtual, ap);
                        	if (ap != null && f.isAdministrator(ap.getNick())) {
                            		virtual.setValue("isadministrator", "true");
                        	} else {
                            		virtual.setValue("isadministrator", "false");
                        	}
                    	}
		   }
		}
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return list;
    }

    /**
     * Get list of all forums
     *
     * @return List of (virtual) MMObjectNode-objects representing the available forums
     *
     */
    public static List getForums(String mode) {
        List list = new ArrayList();
        VirtualBuilder builder = new VirtualBuilder(MMBase.getMMBase());

        Enumeration e = ForumManager.getForums();
        while (e.hasMoreElements()) {
            Forum f = (Forum) e.nextElement();
            MMObjectNode virtual = builder.getNewNode("admin");
            virtual.setValue("name", f.getName());
            virtual.setValue("id", f.getId());
            virtual.setValue("description", f.getDescription());
            virtual.setValue("postareacount", f.getPostAreaCount());
            virtual.setValue("postthreadcount", f.getPostThreadCount());
            virtual.setValue("postcount", f.getPostCount());
            virtual.setValue("postersonline", f.getPostersOnlineCount());
            virtual.setValue("posterstotal", f.getPostersTotalCount());
            virtual.setValue("postersnew", f.getPostersNewCount());
            virtual.setValue("viewcount", f.getViewCount());
            virtual.setValue("lastposter", f.getLastPoster());
            virtual.setValue("lastposttime", f.getLastPostTime());
            virtual.setValue("lastsubject", f.getLastSubject());
            virtual.setValue("lastposternumber",f.getLastPosterNumber());
            virtual.setValue("lastposrnumber",f.getLastPostNumber());
            if (mode.equals("stats")) {
            	virtual.setValue("postthreadloadedcount", f.getPostThreadLoadedCount());
            	virtual.setValue("postingsloadedcount", f.getPostingsLoadedCount());
            	virtual.setValue("memorysize", ((float)f.getMemorySize())/(1024*1024)+"MB");
            }
            list.add(virtual);
        }
        return list;
    }

    /**
     * List all the postthreads within a postarea
     *
     * @param forumid MMBase node number of the forum
     * @param postareaid MMBase node number of the postarea
     * @param activeid active posterid
     * @param pagesize Number of pages per thread
     * @param page Page number of the threads we want
     * @param overviewpagesize The number of threads per page
     * @param baseurl Base url for links in the navigation html
     * @param cssclass Stylesheet name for the url links
     * @return List of (virtual) MMObjectNodes representing the postthreads within the postarea
     */
    public List getPostThreads(String forumid, String postareaid, int activeid, int pagesize, int page, int overviewpagesize, String baseurl, String cssclass) {
        List list = new ArrayList();

        VirtualBuilder builder = new VirtualBuilder(MMBase.getMMBase());

        if (cssclass == null) cssclass = "";
        // create a result list

        Forum f = ForumManager.getForum(forumid);
        if (f != null) {
            PostArea a = f.getPostArea(postareaid);
            Iterator e = a.getPostThreads(page, overviewpagesize);
            while (e.hasNext()) {
                PostThread thread = (PostThread) e.next();
                MMObjectNode virtual = builder.getNewNode("admin");
                String subject = thread.getSubject();
                virtual.setValue("name", subject);
                if (subject.length()>60) subject = subject.substring(0,57)+"...";
                virtual.setValue("shortname", subject);
                virtual.setValue("id", thread.getId());
                virtual.setValue("mood", thread.getMood());

                Poster ap = f.getPoster(activeid);
                if (ap != null) {
                    virtual.setValue("state", thread.getState(ap));
		    ThreadObserver to = f.getThreadObserver(thread.getId());
		    if (to!=null && to.wantsEmailOnChange(ap)) {
			virtual.setValue("emailonchange","true");  
		    } else {
			virtual.setValue("emailonchange","false");  
		    }
		    if (to!=null && to.isBookmarked(ap)) {
			virtual.setValue("bookmarked","true");  
		    } else {
			virtual.setValue("bookmarked","false");  
		    }
                } else {
                    virtual.setValue("state", thread.getState());
		    virtual.setValue("emailonchange","false");  
		    virtual.setValue("bookmarked","false");  
                }
                virtual.setValue("type", thread.getType());
                virtual.setValue("creator", thread.getCreator());
                virtual.setValue("postcount", thread.getPostCount());
                virtual.setValue("pagecount", thread.getPageCount(pagesize));
                virtual.setValue("replycount", thread.getPostCount() - 1);
                virtual.setValue("viewcount", thread.getViewCount());
                virtual.setValue("lastposter", thread.getLastPoster());
                virtual.setValue("lastposttime", thread.getLastPostTime());
                virtual.setValue("lastsubject", thread.getLastSubject());
                //newnode.setStringValue("threadnav",thread.getLastSubject());

		// temp until sure if we also want to be able to set this from html
		int overflowpage = ForumManager.getPostingsOverflowPostarea();
                virtual.setValue("navline", thread.getNavigationLine(baseurl, pagesize,overflowpage, cssclass));
                virtual.setValue("lastposternumber",thread.getLastPosterNumber());
                virtual.setValue("lastpostnumber",thread.getLastPostNumber());
                list.add(virtual);
            }
        }

        return list;
    }


    public List getBookmarkedThreads(String forumid, String postareaid, int activeid, int pagesize, int page, int overviewpagesize, String baseurl, String cssclass) {
        List list = new ArrayList();

        VirtualBuilder builder = new VirtualBuilder(MMBase.getMMBase());

        Forum f = ForumManager.getForum(forumid);
        if (f != null) {
            PostArea a = f.getPostArea(postareaid);
            if (activeid != -1) {
                Poster ap = f.getPoster(activeid);
            	Iterator e = ap.getBookmarkedThreads(page, overviewpagesize);
            	while (e.hasNext()) {
                	Integer tid = (Integer) e.next();
                	PostThread thread = f.getPostThread(""+tid);
                	MMObjectNode virtual = builder.getNewNode("admin");
                	virtual.setValue("mood", thread.getMood());
                        virtual.setValue("state", thread.getState());
                	virtual.setValue("postthreadid", thread.getId());
                	virtual.setValue("postareaname", thread.getParent().getName());
                	virtual.setValue("postareaid", thread.getParent().getId());
                	virtual.setValue("forumid", thread.getParent().getParent().getId());
                	virtual.setValue("type", thread.getType());
                	virtual.setValue("creator", thread.getCreator());
              	  	virtual.setValue("postcount", thread.getPostCount());
                	virtual.setValue("pagecount", thread.getPageCount(pagesize));
                	virtual.setValue("replycount", thread.getPostCount() - 1);
                	virtual.setValue("viewcount", thread.getViewCount());
                	virtual.setValue("lastposter", thread.getLastPoster());
                	virtual.setValue("lastposttime", thread.getLastPostTime());
                	virtual.setValue("lastsubject", thread.getLastSubject());
                    	virtual.setValue("lastposternumber",thread.getLastPosterNumber());
                    	virtual.setValue("lastpostnumber",thread.getLastPostNumber());
			int overflowpage = ForumManager.getPostingsOverflowPostarea();
                        virtual.setValue("navline", thread.getNavigationLine(baseurl,  pagesize,overflowpage, cssclass));
                	list.add(virtual);
		}
	    }
        }
        return list;
    }


    /**
     * List the postings within a postthread
     *
     * @param forumid MMBase node number of the forum
     * @param postareaid MMBase node number of the postarea
     * @param postthreadid  MMBase node number of the postthread
     * @param activeid MMBase node number of current Poster (on the page)
     * @param page Page number of the threads we want
     * @param pagesize The number of postings per page
     * @param imagecontext The context where to find the images (eg smilies)
     * @return List of (virtual) MMObjectNodes representing the postings within the given postthread
     */
    public List getPostings(String forumid, String postareaid, String postthreadid, int activeid, int page, int pagesize, String imagecontext) {
        List list = new ArrayList();
       //long start = System.currentTimeMillis();

        VirtualBuilder builder = new VirtualBuilder(MMBase.getMMBase());

        Forum f = ForumManager.getForum(forumid);
        if (f != null) {
            PostArea a = f.getPostArea(postareaid);
            if (a != null) {
                PostThread t = a.getPostThread(postthreadid);
                if (t != null) {
		    if (page==-1) page=t.getPageCount(pagesize);
                    Iterator e = t.getPostings(page, pagesize);
                    int pos = ((page - 1) * pagesize) + 1;

                    while (e.hasNext()) {
                        Posting p = (Posting) e.next();
                        MMObjectNode virtual = builder.getNewNode("admin");
                        virtual.setValue("pos", pos++);
                        String subject = p.getSubject();
                        virtual.setValue("subject", subject);
                        if (subject.length()>60) subject = subject.substring(0,57)+"...";
                        virtual.setValue("shortsubject", subject);
                        virtual.setValue("body", p.getBodyHtml(imagecontext));
                        virtual.setValue("edittime", p.getEditTime());
                        Poster po = f.getPosterNick(p.getPoster());
                        if (po != null) {
                            virtual.setValue("poster", p.getPoster());
                            addPosterInfo(virtual, po);
                        } else {
                            virtual.setValue("poster", p.getPoster());
                            virtual.setValue("guest", "true");
                        }
                        virtual.setValue("posttime", p.getPostTime());
                        virtual.setValue("id", p.getId());
                        virtual.setValue("threadpos", p.getThreadPos());
			// very weird way need to figure this out
			if (p.getThreadPos()%2==0) {
                        	virtual.setValue("tdvar", "threadpagelisteven");
			} else {
                        	virtual.setValue("tdvar", "threadpagelistodd");
			}
                        // should be moved out of the loop
                        if (activeid != -1) {
                            Poster ap = f.getPoster(activeid);
                            ap.signalSeen();
                            ap.seenThread(t);
                            addActiveInfo(virtual, ap);
                            if (po != null && po.getNick().equals(ap.getNick())) {
                                virtual.setValue("isowner", "true");
                            } else {
                                virtual.setValue("isowner", "false");
                            }
                            if (ap != null && a.isModerator(ap.getNick())) {
                                virtual.setValue("ismoderator", "true");
                            } else {
                                virtual.setValue("ismoderator", "false");
                            }
                        }
                        list.add(virtual);
                    }
                }
            }
        }
        //long end = System.currentTimeMillis();
        //log.info("searchPostings "+(end-start)+"ms");

        return list;
    }

    /**
     * Get a specific posting, for use in remove post where the posting
     * to be deleted is displayed.
     *
     * @param forumid MMBase node number of the forum
     * @param postareaid MMBase node number of the postarea
     * @param postthreadid  MMBase node number of the postthread
     * @param postingid MMBase node number of the posting
     * @param activeid MMBase node number of current Poster (on the page)
     * @param imagecontext The context where to find the images (eg smilies)
     * @return List of (virtual) MMObjectNodes representing the postings within the given postthread
     */
    public MMObjectNode getPosting(String forumid, String postareaid, String postthreadid, String postingid, int activeid, String imagecontext) {
        List list = new ArrayList();
        long start = System.currentTimeMillis();

        VirtualBuilder builder = new VirtualBuilder(MMBase.getMMBase());
        MMObjectNode virtual = builder.getNewNode("admin");
        Forum f = ForumManager.getForum(forumid);
        if (f != null) {
            PostArea a = f.getPostArea(postareaid);
            if (a != null) {
                PostThread t = a.getPostThread(postthreadid);
                if (t != null) {
                    Posting p = t.getPosting(Integer.parseInt(postingid));
                    String subject = p.getSubject();
                    virtual.setValue("subject", subject);
                    if (subject.length()>60) subject = subject.substring(0,57)+"...";
                    virtual.setValue("shortsubject", subject);
                    virtual.setValue("body", p.getBodyHtml(imagecontext));
                    virtual.setValue("edittime", p.getEditTime());
                    Poster po = f.getPosterNick(p.getPoster());
                    if (po != null) {
                        virtual.setValue("poster", p.getPoster());
                        addPosterInfo(virtual, po);
                    } else {
                        virtual.setValue("poster", p.getPoster());
                        virtual.setValue("guest", "true");
                    }
                    virtual.setValue("posttime", p.getPostTime());
                    virtual.setValue("postcount", t.getPostCount());
                    virtual.setValue("id", p.getId());
                    virtual.setValue("threadpos", p.getThreadPos());
		    //very weird way need to figure this out
                    if (p.getThreadPos()%2==0) {
                        virtual.setValue("tdvar", "threadpagelisteven");
                    } else {
                        virtual.setValue("tdvar", "threadpagelistodd");
                    }
                    // should be moved out of the loop
                    if (activeid != -1) {
                        Poster ap = f.getPoster(activeid);
                        ap.signalSeen();
                        ap.seenThread(t);
                        addActiveInfo(virtual, ap);
                        if (po != null && po.getNick().equals(ap.getNick())) {
                            virtual.setValue("isowner", "true");
                        } else {
                            virtual.setValue("isowner", "false");
                        }
                        if (ap != null && a.isModerator(ap.getNick())) {
                            virtual.setValue("ismoderator", "true");
                        } else {
                            virtual.setValue("ismoderator", "false");
                        }
			log.info("PO="+po+" ap="+ap);
                        if (po != null && po.getNick().equals(ap.getNick()) || ap != null && a.isModerator(ap.getNick())) {
                            virtual.setValue("maychange", "true");
			} else {
                            virtual.setValue("maychange", "false");
			}
                    }
                }
            }
        }
        long end = System.currentTimeMillis();
        //log.info("getPosting "+(end-start)+"ms");

        return virtual;
    }



    public String getPostingPageNumber(String forumid, String postareaid, String postthreadid, String postingid,int pagesize) {
        Forum f = ForumManager.getForum(forumid);
        if (f != null) {
            PostArea a = f.getPostArea(postareaid);
            if (a != null) {
                PostThread t = a.getPostThread(postthreadid);
                if (t != null) {
                    Posting p = t.getPosting(Integer.parseInt(postingid));
		    int pagenumber = (p.getThreadPos()/pagesize)+1;
		    return ""+pagenumber;
                }
            }
        }
        return "-1";
    }










    /**
     * Get the moderators of this postarea / forum
     *
     * @param forumid MMBase node number of the forum
     * @param postareaid MMBase node number of the postarea
     * @return List of (virtual) MMObjectNodes representing the moderators of this forum / postarea.
     *         contains id, account, firstname, lastname of the moderator
     */
    public List getModerators(String forumid, String postareaid) {
        List list = new ArrayList();
        VirtualBuilder builder = new VirtualBuilder(MMBase.getMMBase());

        Forum f = ForumManager.getForum(forumid);
        if (f != null) {
            PostArea a = f.getPostArea(postareaid);
            if (a != null) {
                Enumeration e = a.getModerators();
                while (e.hasMoreElements()) {
                    Poster p = (Poster) e.nextElement();
                    MMObjectNode virtual = builder.getNewNode("admin");
                    virtual.setValue("id", p.getId());
                    virtual.setValue("account", p.getAccount());
                    virtual.setValue("nick", p.getNick());
                    virtual.setValue("firstname", p.getFirstName());
                    virtual.setValue("lastname", p.getLastName());
                    list.add(virtual);
                }
            }
        }
        return list;
    }


    /**
     * Get the administrators of this forum
     *
     * @param forumid MMBase node number of the forum
     * @return List of (virtual) MMObjectNodes representing the administrators of this forum 
     *         contains id, account, firstname, lastname of the administrator
     */
    public List getAdministrators(String forumid) {
        List list = new ArrayList();
        VirtualBuilder builder = new VirtualBuilder(MMBase.getMMBase());

        Forum f = ForumManager.getForum(forumid);
        if (f != null) {
            Enumeration e = f.getAdministrators();
            while (e.hasMoreElements()) {
               Poster p = (Poster) e.nextElement();
               MMObjectNode virtual = builder.getNewNode("admin");
               virtual.setValue("id", p.getId());
               virtual.setValue("account", p.getAccount());
               virtual.setValue("nick", p.getNick());
               virtual.setValue("firstname", p.getFirstName());
               virtual.setValue("lastname", p.getLastName());
               list.add(virtual);
            }
        }
        return list;
    }


    /**
     * Get the posters that are now online in this forum
     * @param forumid MMBase node number of the forum
     * @return  List of (virtual) MMObjectNodes representing the online posters for the given forum
     */
    public List getPostersOnline(String forumid) {
        List list = new ArrayList();
        VirtualBuilder builder = new VirtualBuilder(MMBase.getMMBase());

        Forum f = ForumManager.getForum(forumid);
        if (f != null) {
            Enumeration e = f.getPostersOnline();
            while (e.hasMoreElements()) {
                Poster p = (Poster) e.nextElement();
                MMObjectNode virtual = builder.getNewNode("admin");
                virtual.setValue("id", p.getId());
                virtual.setValue("account", p.getAccount());
                virtual.setValue("nick", p.getNick());
                virtual.setValue("firstname", p.getFirstName());
                virtual.setValue("lastname", p.getLastName());
                virtual.setValue("location", p.getLocation());
                virtual.setValue("lastseen", p.getLastSeen());
                list.add(virtual);
            }
        }
        return list;
    }


    public List getPosters(String forumid,String searchkey,int page,int pagesize) {
	searchkey=searchkey.toLowerCase();
        List list = new ArrayList();
        VirtualBuilder builder = new VirtualBuilder(MMBase.getMMBase());

        Forum f = ForumManager.getForum(forumid);
        if (f != null) {
		int startpos = page*pagesize;
		int i = 1;
		int j = 1;
            	Enumeration e = f.getPosters();
            	while (e.hasMoreElements()) {
                	Poster p = (Poster) e.nextElement();
               		String nick =  p.getNick().toLowerCase();
               		String firstname = p.getFirstName().toLowerCase();
               		String lastname = p.getLastName().toLowerCase();
               		String location = p.getLocation().toLowerCase();
			if (searchkey.equals("*") || nick.indexOf(searchkey)!=-1 || firstname.indexOf(searchkey)!=-1 || lastname.indexOf(searchkey)!=-1 || location.indexOf(searchkey)!=-1) {
				if (i>startpos) {
                			MMObjectNode virtual = builder.getNewNode("admin");
               	 			virtual.setValue("number", p.getId());
                			virtual.setValue("account", p.getAccount());
                        		virtual.setValue("nick", p.getNick());
                			virtual.setValue("firstname",p.getFirstName());
                			virtual.setValue("lastname", p.getLastName());
                			virtual.setValue("location", p.getLocation());
                			virtual.setValue("lastseen", p.getLastSeen());
					if (page!=0) {
						virtual.setValue("prevpage",page-1);
					} else {
						virtual.setValue("prevpage",-1);
					}
					virtual.setValue("nextpage",-1);
					list.add(virtual);
					j++;	
					if (j>pagesize) {
						virtual.setValue("nextpage",page+1);
						break;
					}
				}
				i++;
			}
		}
            }
        return list;
    }


    public List searchPostings(String forumid,String searchareaid,String searchpostthreadid,String searchkey,int page,int pagesize) {
        long start = System.currentTimeMillis();
	searchkey = searchkey.toLowerCase();
        List list = new ArrayList();
        VirtualBuilder builder = new VirtualBuilder(MMBase.getMMBase());

        Forum f = ForumManager.getForum(forumid);
        if (f != null) {
		int startpos = page*pagesize;
		int i = 1;
		int j = 1;
            	Enumeration e = null;
		if (!searchareaid.equals("-1")) {
			if (!searchpostthreadid.equals("-1")) {
	                	PostArea a = f.getPostArea(searchareaid);
				if (a!=null) {
	                		PostThread t = a.getPostThread(searchpostthreadid);
					e = t.searchPostings(searchkey).elements();
				}
			} else {
	                	PostArea a = f.getPostArea(searchareaid);
				if (a!=null) e = a.searchPostings(searchkey).elements();
			}
		} else {
            		e = f.searchPostings(searchkey).elements();
		}
		if (e!=null) {
            	while (e.hasMoreElements() && j<25) {
                	Posting p = (Posting) e.nextElement();
                	MMObjectNode virtual = builder.getNewNode("admin");
               	 	virtual.setValue("postingid", p.getId());
			PostThread pt = p.getParent();
			PostArea pa = pt.getParent();
               	 	virtual.setValue("postareaid", pa.getId());
               	 	virtual.setValue("postareaname", pa.getName());
               	 	virtual.setValue("postthreadid", pt.getId());
                        String subject = p.getSubject();
                        virtual.setValue("subject", subject);
                        if (subject.length()>60) subject = subject.substring(0,57)+"...";
                        virtual.setValue("shortsubject", subject);
                	virtual.setValue("poster", p.getPoster());
                	virtual.setValue("posterid", f.getPoster(p.getPoster()));
			list.add(virtual);
			j++;
		}
		}
            }
        long end = System.currentTimeMillis();
        log.info("searchPostings "+(end-start)+"ms");
        return list;
    }


    /**
     * List all the posters not allready a moderator (so possible moderators) for this postarea
     *
     * @param forumid MMBase node number of the forum
     * @param postareaid MMBase node number of the new postarea
     * @return List of (virtual) MMObjectNodes representing all posters of the given postarea which are no moderators
     */
    public List getNonModerators(String forumid, String postareaid,String searchkey) {
        List list = new ArrayList();
        VirtualBuilder builder = new VirtualBuilder(MMBase.getMMBase());

        Forum f = ForumManager.getForum(forumid);
        if (f != null) {
            PostArea a = f.getPostArea(postareaid);
            if (a != null) {
                Enumeration e = a.getNonModerators(searchkey);
                while (e.hasMoreElements()) {
                    Poster p = (Poster) e.nextElement();
                    MMObjectNode virtual = builder.getNewNode("admin");
                    virtual.setValue("id", p.getId());
                    virtual.setValue("account", p.getAccount());
                    virtual.setValue("nick", p.getNick());
                    virtual.setValue("firstname", p.getFirstName());
                    virtual.setValue("lastname", p.getLastName());
                    list.add(virtual);
                }
            }
        }
        return list;
    }


    /**
     * List all the posters not allready a administrator for this forum
     *
     * @param forumid MMBase node number of the forum
     * @param postareaid MMBase node number of the new postarea
     * @return List of (virtual) MMObjectNodes representing all posters of the given postarea which are no moderators
     */
    public List getNonAdministrators(String forumid,String searchkey) {
        List list = new ArrayList();
        VirtualBuilder builder = new VirtualBuilder(MMBase.getMMBase());

        Forum f = ForumManager.getForum(forumid);
        if (f != null) {
           Enumeration e = f.getNonAdministrators(searchkey);
           while (e.hasMoreElements()) {
               Poster p = (Poster) e.nextElement();
               MMObjectNode virtual = builder.getNewNode("admin");
               virtual.setValue("id", p.getId());
               virtual.setValue("account", p.getAccount());
               virtual.setValue("nick", p.getNick());
               virtual.setValue("firstname", p.getFirstName());
               virtual.setValue("lastname", p.getLastName());
               list.add(virtual);
            }
        }
        return list;
    }


    /**
     * Provide general info and statistics on a forum
     * Remark: atm it also returns configuration settings, this will change in the near future
     *         see getForumConfiguration for more info.
     *
     * @param id MMBase node number of the forum
     * @param sactiveid Id for the current (on the page) poster for admin/onwership checks
     * @return (virtual) MMObjectNode representing info for the given forum
     *
     */
    public MMObjectNode getForumInfo(String id, String sactiveid) {
        VirtualBuilder builder = new VirtualBuilder(MMBase.getMMBase());
        MMObjectNode virtual = builder.getNewNode("admin");
        try {
            int activeid = Integer.parseInt(sactiveid);

            Forum f = ForumManager.getForum(id);
            if (f != null) {
                virtual.setValue("name", f.getName());
                virtual.setValue("language", f.getLanguage());
                virtual.setValue("accountcreationtype", f.getAccountCreationType());
                virtual.setValue("accountremovaltype", f.getAccountRemovalType());
                virtual.setValue("loginmodetype", f.getLoginModeType());
                virtual.setValue("logoutmodetype", f.getLogoutModeType());
                virtual.setValue("navigationmethod", f.getNavigationMethod());
                virtual.setValue("privatemessagesenabled", f.getPrivateMessagesEnabled());
                virtual.setValue("description", f.getDescription());
                virtual.setValue("postareacount", f.getPostAreaCount());
                virtual.setValue("postthreadcount", f.getPostThreadCount());
                virtual.setValue("postcount", f.getPostCount());
                virtual.setValue("postersonline", f.getPostersOnlineCount());
                virtual.setValue("posterstotal", f.getPostersTotalCount());
                virtual.setValue("postersnew", f.getPostersNewCount());
                virtual.setValue("viewcount", f.getViewCount());
                virtual.setValue("lastposter", f.getLastPoster());
                virtual.setValue("lastposttime", f.getLastPostTime());
                virtual.setValue("lastsubject", f.getLastSubject());
                virtual.setValue("hasnick", f.hasNick());
                if (activeid != -1) {
                    Poster ap = f.getPoster(activeid);
                    ap.signalSeen();
                    addActiveInfo(virtual, ap);
                    if (ap != null && f.isAdministrator(ap.getNick())) {
                        virtual.setValue("isadministrator", "true");
                    } else {
                        virtual.setValue("isadministrator", "false");
                    }
                }
            }
        } catch (Exception e) {
		e.printStackTrace();
        }

        return virtual;
    }

    public String getForumAlias(String key) {
	if (!key.equals("")) {
		Forum f = ForumManager.getForumByAlias(key);
		if (f!=null) return ""+f.getId();
	}
	return "unknown";
    }

   /**
     * Provide the headerpath for the given forum
     *
     * @param id MMBase node number of the forum
     * @return String representing the headerpath of the given forum
     *
     */
    public String getForumHeaderPath(String id) {
        try {
            Forum f = ForumManager.getForum(id);
            if (f != null) {
                return f.getHeaderPath();
            }
        } catch (Exception e) {
		    e.printStackTrace();
        }
        return "";
    }

    /**
     * Provide the footerpath for the given forum
     *
     * @param id MMBase node number of the forum
     * @return String representing the footerpath of the given forum
     *
     */
    public String getForumFooterPath(String id) {
        try {
            Forum f = ForumManager.getForum(id);
            if (f != null) {
                return f.getFooterPath();
            }
        } catch (Exception e) {
		    e.printStackTrace();
        }
        return "";
    }

    /**
     * Provide the fromaddress for the given forum
     *
     * @param id MMBase node number of the forum
     * @return String representing the from-emailaddress of the given forum
     *
     */
    public String getForumFromEmailAddress(String id) {
        try {
            Forum f = ForumManager.getForum(id);
            if (f != null) {
                return f.getFromEmailAddress();
            }
        } catch (Exception e) {
		    e.printStackTrace();
        }
        return "";
    }


    /**
     * Provide configuration info on a forum
     *
     * @param id MMBase node number of the forum
     * @param sactiveid Id for the current (on the page) poster for admin/onwership checks
     * @return (virtual) MMObjectNode representing the configuration of the given forum
     *
     */
    public MMObjectNode getForumConfig(String id, String sactiveid) {
        VirtualBuilder builder = new VirtualBuilder(MMBase.getMMBase());
        MMObjectNode virtual = builder.getNewNode("admin");
        try {
            int activeid = Integer.parseInt(sactiveid);

            Forum f = ForumManager.getForum(id);
            if (f != null) {
                virtual.setValue("language", f.getLanguage());
                virtual.setValue("accountcreationtype", f.getAccountCreationType());
                virtual.setValue("accountremovaltype", f.getAccountRemovalType());
                virtual.setValue("loginmodetype", f.getLoginModeType());
                virtual.setValue("logoutmodetype", f.getLogoutModeType());
                virtual.setValue("guestreadmodetype", f.getGuestReadModeType());
                virtual.setValue("guestwritemodetype", f.getGuestWriteModeType());
                virtual.setValue("avatarsdisabled", f.getAvatarsDisabled());
                virtual.setValue("avatarsuploadenabled", f.getAvatarsUploadEnabled());
                virtual.setValue("avatarsgalleryenabled", f.getAvatarsGalleryEnabled());
                virtual.setValue("contactinfoenabled", f.getContactInfoEnabled());
                virtual.setValue("smileysenabled", f.getSmileysEnabled());
                virtual.setValue("privatemessagesenabled", f.getPrivateMessagesEnabled());
                virtual.setValue("postingsperpage", f.getPostingsPerPage());
                virtual.setValue("fromaddress",f.getFromEmailAddress());
                virtual.setValue("headerpath",f.getHeaderPath());
                virtual.setValue("footerpath",f.getFooterPath());
        	virtual.setValue("replyoneachpage",f.getReplyOnEachPage());
        	virtual.setValue("navigationmethod",f.getNavigationMethod());
        	virtual.setValue("alias",f.getAlias());

                if (activeid != -1) {
                    Poster ap = f.getPoster(activeid);
                    ap.signalSeen();
                    addActiveInfo(virtual, ap);
                    if (ap != null && f.isAdministrator(ap.getNick())) {
                        virtual.setValue("isadministrator", "true");
                    } else {
                        virtual.setValue("isadministrator", "false");
                    }
                }
            }
        } catch (Exception e) {
		e.printStackTrace();
        }

        return virtual;
    }


    /**
     * Provide configuration info on a forum
     *
     * @param id MMBase node number of the forum
     * @param sactiveid Id for the current (on the page) poster for admin/onwership checks
     * @return (virtual) MMObjectNode representing the configuration of the given forum
     *
     */
    public MMObjectNode getPostAreaConfig(String id, String sactiveid,String postareaid) {
        VirtualBuilder builder = new VirtualBuilder(MMBase.getMMBase());
        MMObjectNode virtual = builder.getNewNode("admin");
        try {
            int activeid = Integer.parseInt(sactiveid);

            Forum f = ForumManager.getForum(id);
            if (f != null) {
	            PostArea a = f.getPostArea(postareaid);
		    if (a!=null) {
                	virtual.setValue("guestreadmodetype", a.getGuestReadModeType());
                	virtual.setValue("guestwritemodetype", a.getGuestWriteModeType());
                	virtual.setValue("position", a.getPos());
                	virtual.setValue("threadstartlevel", a.getThreadStartLevel());
		   }
            }
        } catch (Exception e) {
		e.printStackTrace();
        }

        return virtual;
    }

    public MMObjectNode getForumsConfig() {
        VirtualBuilder builder = new VirtualBuilder(MMBase.getMMBase());
        MMObjectNode virtual = builder.getNewNode("admin");
        virtual.setValue("language", ForumManager.getLanguage());
        virtual.setValue("accountcreationtype", ForumManager.getAccountCreationType());
        virtual.setValue("accountremovaltype", ForumManager.getAccountRemovalType());
        virtual.setValue("loginmodetype", ForumManager.getLoginModeType());
        virtual.setValue("logoutmodetype", ForumManager.getLogoutModeType());
        virtual.setValue("guestreadmodetype", ForumManager.getGuestReadModeType());
        virtual.setValue("guestwritemodetype", ForumManager.getGuestWriteModeType());
        virtual.setValue("avatarsuploadenabled", ForumManager.getAvatarsUploadEnabled());
        virtual.setValue("avatarsgalleryenabled", ForumManager.getAvatarsGalleryEnabled());
        virtual.setValue("contactinfoenabled", ForumManager.getContactInfoEnabled());
        virtual.setValue("smileysenabled", ForumManager.getSmileysEnabled());
        virtual.setValue("privatemessagesenabled", ForumManager.getPrivateMessagesEnabled());
        virtual.setValue("postingsperpage", ForumManager.getPostingsPerPage());
        virtual.setValue("fromaddress",ForumManager.getFromEmailAddress());
        virtual.setValue("headerpath",ForumManager.getHeaderPath());
        virtual.setValue("footerpath",ForumManager.getFooterPath());
        return virtual;
    }





    /**
     * Provide info on a poster forum
     *
     * @param id MMBase node number of the forum
     * @param posterid Id for poster we want (string/account field)
     * @return (virtual) MMObjectNode representing info for the given poster
     */
    public MMObjectNode getPosterInfo(String id, String posterid) {
        VirtualBuilder builder = new VirtualBuilder(MMBase.getMMBase());
        MMObjectNode virtual = builder.getNewNode("admin");
        Forum f = ForumManager.getForum(id);
        if (f != null) {
            if (posterid != null) {
                Poster po = f.getPoster(posterid);
                addPosterInfo(virtual, po);
            }
        }
        return virtual;
    }


    /**
     * Provide quota info on a poster
     *
     * @param id MMBase node number of the forum
     * @param posterid Id for poster we want (string/account field)
     * @return (virtual) MMObjectNode representing posters quota info
     */
    public MMObjectNode getQuotaInfo(String id, int posterid,int barsize) {
        VirtualBuilder builder = new VirtualBuilder(MMBase.getMMBase());
        MMObjectNode virtual = builder.getNewNode("admin");
        Forum f = ForumManager.getForum(id);
        if (f != null) {
            if (posterid != -1) {
                Poster po = f.getPoster(posterid);
		virtual.setValue("quotareached",po.isQuotaReached());
		int t=po.getQuotaNumber();
		int u=po.getQuotaUsedNumber();
		float d=100/(float)t;
		float b=(float)barsize/t;
		int up=(int)(d*u);
		int ub=(int)(b*u);

		// log.info("u="+u+" d="+d+" up="+up+" b="+b+" ub="+ub);

		virtual.setValue("quotausedpercentage",up);
		virtual.setValue("quotaunusedpercentage",100-up);
		virtual.setValue("quotanumber",t);
		virtual.setValue("quotausednumber",u);
		virtual.setValue("quotaunusednumber",t-u);
		virtual.setValue("quotausedbar",ub);

		if (u>ForumManager.getQuotaSoftWarning()) {
			if (u>ForumManager.getQuotaWarning()) {
				virtual.setValue("quotawarning","red");
			} else {
				virtual.setValue("quotawarning","orange");
			}
		} else {
			virtual.setValue("quotawarning","green");
		}
            }
        }
        return virtual;
    }


    /**
     * Provide info a mailbox
     *
     * @param id MMBase node number of the forum
     * @param posterid Id for poster we want (string/account field)
     * @param mailboxid Id for mailbox we want
     * @return (virtual) MMObjectNode representing info for the given poster
     */
    public MMObjectNode getMailboxInfo(String id, int posterid,String mailboxid) {
        VirtualBuilder builder = new VirtualBuilder(MMBase.getMMBase());
        MMObjectNode virtual = builder.getNewNode("admin");
        Forum f = ForumManager.getForum(id);
        if (f != null) {
            if (posterid != -1) {
                Poster po = f.getPoster(posterid);
		if (po != null ) {
			Mailbox mb=po.getMailbox(mailboxid);
			if (mb != null) {
				virtual.setValue("messagecount",mb.getMessageCount());
				virtual.setValue("messageunreadcount",mb.getMessageUnreadCount());
				virtual.setValue("messagenewcount",mb.getMessageNewCount());
			}
		}
            }
        }
        return virtual;
    }


    /**
     * signal mailbox change
     *
     * @param id MMBase node number of the forum
     * @param posterid Id for poster we want (string/account field)
     * @param mailboxid Id for mailbox we want
     * @return signal given 
     */
    public boolean signalMailboxChange(String id, int posterid,String mailboxid) {
        VirtualBuilder builder = new VirtualBuilder(MMBase.getMMBase());
        MMObjectNode virtual = builder.getNewNode("admin");
        Forum f = ForumManager.getForum(id);
        if (f != null) {
            if (posterid != -1) {
                Poster po = f.getPoster(posterid);
		if (po != null ) {
			Mailbox mb=po.getMailbox(mailboxid);
			if (mb != null) {
				mb.signalMailboxChange();
			}
		}
            }
        }
        return true;
    }

    /**
     * Change values of a Poster
     *
     * @param forumid MMBase node number of the forum
     * @param posterid MMBase node number of the poster
     * @param firstname New Firstname of the poster
     * @param lastname New lastname of the poster
     * @param email New email address of the poster
     * @param gender  New gender of the poster
     * @param location ew location of the poster
     * @return  Feedback regarding the success of edit action
     */
    public String editPoster(String forumid, int posterid, String firstname, String lastname, String email, String gender, String location, String newpassword, String newconfirmpassword) {
        if (newpassword.equals("")) {
            log.debug("newpassword is empty");
            Forum f = ForumManager.getForum(forumid);
            if (f != null) {
                Poster p = f.getPoster(posterid);
                if (p != null) {
                    p.setFirstName(firstname);
                    p.setLastName(lastname);
                    p.setEmail(email);
                    p.setGender(gender);
                    p.setLocation(location);
                    p.savePoster();
                } else {
                    return "false";
                }
            }
            return "true";
        } else {
            if (newpassword.equals(newconfirmpassword)) {
            log.debug("newpassword equals newconfirmpassword");
                Forum f = ForumManager.getForum(forumid);
                if (f != null) {
                    Poster p = f.getPoster(posterid);
                    if (p != null) {
                        p.setFirstName(firstname);
                        p.setLastName(lastname);
                        p.setEmail(email);
                        p.setGender(gender);
                        p.setLocation(location);
                        p.setPassword(newpassword);
                        p.savePoster();
                    } else {
                        return "false";
                    }
                }
                return "passwordchanged";
            } else {
                log.debug("newpassword and confirmpassword are not equal");
                return "newpasswordnotequal";
            }
        }
    }
       

    /**
     * create a new poster, creates a account and puts in the users admin system of the forum
     *
     * @param forumid MMBase node number of the forum
     * @param account account name of the new poster
     * @param password Password for the new poster
     * @param firstname Firstname of the new poster
     * @param lastname Lastname of the new poster
     * @param email Email address of the new poster
     * @param gender Gender of the new poster
     * @param location Location of the new poster
     * @return Feedback from the create command (accountused for example)
     */
    public String createPoster(String forumid, String account, String password, String confirmpassword, String firstname, String lastname, String email, String gender, String location) {
        if (password.equals(confirmpassword)) {
            Forum f = ForumManager.getForum(forumid);
            if (f != null) {
                Poster p = f.getPoster(account);
                if (p == null) {
		    if (firstname.equals("") || firstname.length()<2) return "firstnameerror";
		    if (lastname.equals("") || lastname.length()<1) return "lastnameerror";
		    if (email.equals("") || email.indexOf("@")==-1 || email.indexOf(".")==-1) return "emailerror";
                    p = f.createPoster(account, password);
                    if (p != null) {
                       	p.setFirstName(firstname); 
                       	p.setLastName(lastname);
                        p.setEmail(email);
                        p.setGender(gender);
                        p.setLocation(location);
                        p.setPassword(password);
                        p.setPostCount(0);
                        p.savePoster();
                    } else {
                        return "createerror";
                    }
                } else {
                    return "inuse";
                }
            }
            return "ok";
        } else {
            return ("passwordnotequal");
        }
    }


    public String createPosterNick(String forumid, String account, String password, String confirmpassword,String nick, String firstname, String lastname, String email, String gender, String location) {
        if (password.equals(confirmpassword)) {
            Forum f = ForumManager.getForum(forumid);
            if (f != null) {
                Poster p = f.getPoster(account);
                Poster n = f.getPosterNick(nick);
                if (p == null) {
		    if (n!=null) return "nickinuse";
		    if (firstname.equals("") || firstname.length()<2) return "firstnameerror";
		    if (lastname.equals("") || lastname.length()<1) return "lastnameerror";
		    if (email.equals("") || email.indexOf("@")==-1 || email.indexOf(".")==-1) return "emailerror";
                    p = f.createPoster(account, password);
                    if (p != null) {
                       	p.setFirstName(firstname); 
                       	p.setLastName(lastname);
                        p.setEmail(email);
                        p.setGender(gender);
                        p.setLocation(location);
                        p.setPassword(password);
                        p.setPostCount(0);
                        p.savePoster();
    			setProfileValue(forumid, p.getId(),"nick",nick);
                    } else {
                        return "createerror";
                    }
                } else {
                    return "inuse";
                }
            }
            return "ok";
        } else {
            return ("passwordnotequal");
        }
    }


    /**
     * create a new poster proxy, creates a account and puts in the users admin system of the forum
     *
     * @param forumid MMBase node number of the forum
     * @param account account name of the new poster
     * @param password Password for the new poster
     * @param firstname Firstname of the new poster
     * @param lastname Lastname of the new poster
     * @param email Email address of the new poster
     * @param gender Gender of the new poster
     * @param location Location of the new poster
     * @return Feedback from the create command (accountused for example)
     */
    public String createPosterProxy(String forumid, String account, String password, String confirmpassword,String firstname, String lastname, String email, String gender, String location,String proxypassword) {
        if (password.equals(confirmpassword)) {
            Forum f = ForumManager.getForum(forumid);
            if (f != null) {
                Poster p = f.getPoster(account);
                if (p == null) {
                    p = f.createPoster(account, password);
                    if (p != null) {
                        p.setFirstName(firstname);
                        p.setLastName(lastname);
                        p.setEmail(email);
                        p.setGender(gender);
                        p.setLocation(location);
                        p.setPostCount(0);
                        p.savePoster();
                    } else {
                        return "createerror";
                    }
                } else {
                    return "inuse";
                }
            }
            return "ok";
        } else {
            return ("passwordnotequal");
        }
    }

    /**
     * Provide general info on this postarea within the given forum
     *
     * @param forumid MMBase node number of the forum
     * @param postareaid MMBase node number of the postarea
     * @param activeid MMBase node number of current Poster (on the page)
     * @param page Current page number
     * @param pagesize   Number of postings per page
     * @param baseurl Base url for links in the navigation html
     * @param cssclass stylesheet name for the url links
     * @return (virtual) MMObjectNode representing info for the given postarea
     */
    public MMObjectNode getPostAreaInfo(String forumid, String postareaid, int activeid, int page, int pagesize, String baseurl, String cssclass) {
        VirtualBuilder builder = new VirtualBuilder(MMBase.getMMBase());
        MMObjectNode virtual = builder.getNewNode("admin");

        Forum f = ForumManager.getForum(forumid);
        if (f != null) {
            PostArea a = f.getPostArea(postareaid);
            virtual.setValue("name", a.getName());
            virtual.setValue("postthreadcount", a.getPostThreadCount());
            virtual.setValue("postcount", a.getPostCount());
            virtual.setValue("viewcount", a.getViewCount());
            virtual.setValue("lastposter", a.getLastPoster());
            virtual.setValue("lastposttime", a.getLastPostTime());
            virtual.setValue("lastsubject", a.getLastSubject());
            virtual.setValue("guestreadmodetype", a.getGuestReadModeType());
            virtual.setValue("guestwritemodetype", a.getGuestWriteModeType());
            virtual.setValue("threadstartlevel", a.getThreadStartLevel());
            virtual.setValue("privatemessagesenabled", f.getPrivateMessagesEnabled());
            virtual.setValue("smileysenabled", f.getSmileysEnabled());
            virtual.setValue("navline", a.getNavigationLine(baseurl, page, pagesize, cssclass));
            virtual.setValue("pagecount", a.getPageCount(pagesize));
            if (activeid != -1) {
                Poster ap = f.getPoster(activeid);
                ap.signalSeen();
                // addActiveInfo(virtual,ap);
                if (ap != null && f.isAdministrator(ap.getNick())) {
                    virtual.setValue("isadministrator", "true");
                } else {
                    virtual.setValue("isadministrator", "false");
                }
                if (ap != null && a.isModerator(ap.getNick())) {
                    virtual.setValue("ismoderator", "true");
                } else {
                    virtual.setValue("ismoderator", "false");
                }
            }
        }
        return virtual;
    }


    public MMObjectNode getPostThreadInfo(String forumid, String postareaid, String postthreadid,int pagesize) {
        VirtualBuilder builder = new VirtualBuilder(MMBase.getMMBase());
        MMObjectNode virtual = builder.getNewNode("admin");

        Forum f = ForumManager.getForum(forumid);
        if (f != null) {
            PostArea a = f.getPostArea(postareaid);
	    if (a != null) {
           	 PostThread t = a.getPostThread(postthreadid);
                 if (t != null) {
            		virtual.setValue("threadstate", t.getState());
            		virtual.setValue("threadmood", t.getState());
            		virtual.setValue("threadtype", t.getType());
            		virtual.setValue("pagecount", t.getPageCount(pagesize));
		 }
	   }
        }
        return virtual;
    }

    /**
     * Remove a postarea (including postthreads and postings) from a forum
     *
     * @param forumid MMBase node number of the forum
     * @param postareaid MMBase node number of the postarea
     * @return Feedback regarding this remove action
     */
    public boolean removePostArea(String forumid, String postareaid) {
        Forum f = ForumManager.getForum(forumid);
        if (f != null) {
            boolean result = f.removePostArea(postareaid);
            return result;
        }
        return false;
    }

    /**
     * Profile of a poster changed signal
     *
     * @param forumid MMBase node number of the forum
     * @param posterid MMBase node number of the poster that has changed
     * @return feedback regarding this action
     */
    public boolean profileUpdated(String forumid, int posterid) {
        Forum f = ForumManager.getForum(forumid);
        if (f != null) {
            Poster p = f.getPoster(posterid);
            if (p != null) {
                return p.profileUpdated();
            }
        }
        return false;
    }

    /**
     * Removes a whole thread (including postings) from a postarea
     *
     * @param forumid MMBase node number of the forum
     * @param postareaid  MMbase node number of the postarea
     * @param postthreadid MMBase node number of the postthread
     * @return feedback regarding this remove action
     */
    public boolean removePostThread(String forumid, String postareaid, String postthreadid) {
        Forum f = ForumManager.getForum(forumid);
        if (f != null) {
            PostArea a = f.getPostArea(postareaid);
            if (a != null) {
                boolean result = a.removePostThread(postthreadid);
                return result;
            }
        }
        return false;
    }

    /**
     * Generate a navigation line (html) for a postthread
     *
     * @param forumid MMBase node number of the forum
     * @param postareaid MMBase node number of the postarea
     * @param postthreadid MMBase node number of the postthread
     * @param page  Current page number
     * @param pagesize  Number of postings per page
     * @param baseurl Base url for links in the navigation html
     * @param cssclass  stylesheet name for the url links
     * @return (virtual) MMObjectNode containing navline, lastpage, pagecount
     */
    public MMObjectNode getPostThreadNavigation(String forumid, String postareaid, String postthreadid, int posterid, int page, int pagesize, String baseurl, String cssclass) {
        VirtualBuilder builder = new VirtualBuilder(MMBase.getMMBase());
        MMObjectNode virtual = builder.getNewNode("admin");

        if (cssclass == null) cssclass = "";

        Forum f = ForumManager.getForum(forumid);
        if (f != null) {
            PostArea a = f.getPostArea(postareaid);
            if (a != null) {
                PostThread t = a.getPostThread(postthreadid);
                if (t != null) {
		    int overflowpage = ForumManager.getPostingsOverflowThreadpage();
                    virtual.setValue("navline", t.getNavigationLine(baseurl, page, pagesize,overflowpage, cssclass));
                    virtual.setValue("lastpage", "" + t.isLastPage(page, pagesize));
                    virtual.setValue("pagecount", t.getPageCount(pagesize));
                    Poster ap = f.getPoster(posterid);
               	    if (ap != null) {
		    	ThreadObserver to = f.getThreadObserver(t.getId());
		    	if (to!=null && to.wantsEmailOnChange(ap)) {
				virtual.setValue("emailonchange","true");  
		    	} else {
				virtual.setValue("emailonchange","false");  
		    	}
		        if (to!=null && to.isBookmarked(ap)) {
				virtual.setValue("bookmarked","true");  
		        } else {
				virtual.setValue("bookmarked","false");  
		        }
               	    } else {
		    	virtual.setValue("emailonchange","false");  
			virtual.setValue("bookmarked","false");  
                    }
                }
            }
        }
        return virtual;
    }

    /**
     * Post a reply on the given postthread
     *
     * @param forumid MMBase node number of the forum
     * @param postareaid MMBase node number of the forum
     * @param postthreadid  MMBase node number of the postthread
     * @param subject Subject of the reply (normally same as the postthread sibject)
     * @param poster Posterid of the reply
     * @param body Body of the reply
     * @return  Feedback regarding this post action
     */
    public MMObjectNode postReply(String forumid, String postareaid, String postthreadid, String subject, String poster, String body) {
        VirtualBuilder builder = new VirtualBuilder(MMBase.getMMBase());
        MMObjectNode virtual = builder.getNewNode("admin");

        if (subject.length()>60) subject = subject.substring(0,57)+"..."; 

        Forum f = ForumManager.getForum(forumid);
	int pos = poster.indexOf("(");
	if (pos!=-1) {
		poster=poster.substring(0,pos-1);
	}

        if (f != null) {
            PostArea a = f.getPostArea(postareaid);
            if (a != null) {
                PostThread t = a.getPostThread(postthreadid);
                if (t != null) {
                    // nobody may post in closed thread, unless you're a moderator
		    Poster p=f.getPosterNick(poster);
                    if ((!t.getState().equals("closed") || a.isModerator(poster)) && (p==null || !p.isBlocked())) {
			if (body.equals("")) {
                		virtual.setValue("error", "no_body");
			} else if (p.checkDuplicatePost("",body)) {
                		virtual.setValue("error", "duplicate_post");
			} else if (checkIllegalHtml(body)) {
                		virtual.setValue("error", "illegal_html");
			} else if (checkSpeedPosting(a,p)) {
                		virtual.setValue("error", "speed_posting");
                		virtual.setValue("speedposttime", ""+a.getSpeedPostTime());
			} else {
				body = a.filterContent(body);
				subject = filterHTML(subject);
				//body = BBCode.decode(body);
				try {
                       	        	t.postReply(subject, p, body);
                			virtual.setValue("error", "none");
					p.setLastBody(body);
					p.setLastPostTime((int)(System.currentTimeMillis()/1000));
				} catch (Exception e) {
					log.info("Error while posting a reply");
                			virtual.setValue("error", "illegal_html");
				}
			}
                    }
                }
            }
        }
        return virtual;
    }

    /**
     * add a new post (postthread+1 posting) in a postarea, use postReply for all following postings in the postthread
     *
     * @param forumid  MMBase node number of the forum
     * @param postareaid MMBase node number of the postarea
     * @param subject Subject of the new post
     * @param poster Posterid to be attached to the postthread as its creator
     * @param body Body of the new post
     * @return  (virtual) MMObjectNode containing the postthreadid of the newly created post
     */
    public MMObjectNode newPost(String forumid, String postareaid, String subject, String poster, String body,String mood) {
        VirtualBuilder builder = new VirtualBuilder(MMBase.getMMBase());

        MMObjectNode virtual = builder.getNewNode("admin");

        if (subject.length()>60) subject = subject.substring(0,57)+"...";

        Forum f = ForumManager.getForum(forumid);
        if (f != null) {
            PostArea a = f.getPostArea(postareaid);
	    Poster p=f.getPoster(poster);
            if (a != null && (p==null || !p.isBlocked())) {
		if (subject.equals("")) {
                	virtual.setValue("error", "no_subject");
		} else if (body.equals("")) {
                	virtual.setValue("error", "no_body");
		} else if (checkIllegalHtml(subject)) {
                	virtual.setValue("error", "illegal_html");
		} else if (checkIllegalHtml(body)) {
                	virtual.setValue("error", "illegal_html");
		} else if (p.checkDuplicatePost(subject,body)) {
                	virtual.setValue("error", "duplicate_post");
		} else if (checkSpeedPosting(a,p)) {
                	virtual.setValue("error", "speed_posting");
                	virtual.setValue("speedposttime", ""+a.getSpeedPostTime());
		} else {
			body = a.filterContent(body);
			subject = filterHTML(subject);
                	int postthreadid = a.newPost(subject, p, body,mood);
                	virtual.setValue("postthreadid", postthreadid);
                	virtual.setValue("error", "none");
			p.setLastSubject(subject);
			p.setLastBody(body);
			p.setLastPostTime((int)(System.currentTimeMillis()/1000));
		}
            }
        }
        return virtual;
    }

    /**
     * send a private message to a other poster
     *
     * @param forumid MMBase node number of the forum
     * @param subject Subject of the new message
     * @param poster Poster who is sending the message
     * @param to Poster to which to send the message
     * @param body Body of the new post
     * @return (virtual) MMObjectNode containing privatemessageid of the newly created private message
     */
    public MMObjectNode newPrivateMessage(String forumid, String subject, String poster, String to, String body) {
        VirtualBuilder builder = new VirtualBuilder(MMBase.getMMBase());

        MMObjectNode virtual = builder.getNewNode("admin");
        Forum f = ForumManager.getForum(forumid);
        if (f != null && !f.getPoster(poster).isBlocked()) {
	    if (to.indexOf(",")==-1) {
            	int privatemessageid = f.newPrivateMessage(poster, to, subject, body);
            	virtual.setValue("privatemessageid", privatemessageid);
	    } else {
		StringTokenizer tok=new StringTokenizer(to,",\n\r");
		while (tok.hasMoreTokens()) {
			String pto = tok.nextToken();
            		f.newPrivateMessage(poster, pto, subject, body);
		}
	    }
        }
        return virtual;
    }


    /**
     *
     * @param forumid
     * @param activeid
     * @param newfolder
     * @return
     */
    public MMObjectNode newFolder(String forumid, int activeid, String newfolder) {
        VirtualBuilder builder = new VirtualBuilder(MMBase.getMMBase());

        MMObjectNode virtual = builder.getNewNode("admin");
        Forum f = ForumManager.getForum(forumid);
        if (f != null) {
            int folderid = f.newFolder(activeid, newfolder);
            virtual.setValue("folderid", folderid);
        }
        return virtual;
    }


    /**
     *
     * @param forumid
     * @param activeid
     * @param foldername
     * @return
     */
    public boolean removeFolder(String forumid, int activeid, String foldername) {
        Forum f = ForumManager.getForum(forumid);
        if (f != null) {
            return f.removeFolder(activeid, foldername);
        }
        return false;
    }


    /**
     * Add a moderator to a postarea within a forum
     *
     * @param forumid MMBase node number of the forum
     * @param postareaid MMBase node number of the postarea
     * @param sactiveid MMBase node number of current Poster (on the page)
     * @param smoderatorid MMBase node number of moderator you want to add
     * @return Feedback regarding the success of this action
     */
    public boolean newModerator(String forumid, String postareaid, String sactiveid, String smoderatorid) {
        try {
            int activeid = Integer.parseInt(sactiveid);
            int moderatorid = Integer.parseInt(smoderatorid);
            Forum f = ForumManager.getForum(forumid);
            if (f != null) {
                PostArea a = f.getPostArea(postareaid);
                if (a != null) {
                    Poster ap = f.getPoster(activeid);
                    Poster mp = f.getPoster(moderatorid);
                    if (ap != null && f.isAdministrator(ap.getNick())) {
                        a.addModerator(mp);
                    }
                }
            }
        } catch (Exception e) {
        }
        return true;
    }


    /**
     * Add a moderator to a postarea within a forum
     *
     * @param forumid MMBase node number of the forum
     * @param postareaid MMBase node number of the postarea
     * @param sactiveid MMBase node number of current Poster (on the page)
     * @param smoderatorid MMBase node number of moderator you want to add
     * @return Feedback regarding the success of this action
     */
    public boolean newAdministrator(String forumid, String sactiveid, String sadministratorid) {
        try {
            int activeid = Integer.parseInt(sactiveid);
            int moderatorid = Integer.parseInt(sadministratorid);
            Forum f = ForumManager.getForum(forumid);
            if (f != null) {
                    Poster ap = f.getPoster(activeid);
                    Poster mp = f.getPoster(moderatorid);
                    if (ap != null && f.isAdministrator(ap.getNick())) {
                        f.addAdministrator(mp);
                    }
            }
        } catch (Exception e) {
        }
        return true;
    }

    /**
     * Remove a moderator from a postarea (poster is not removed just status moderator is revoked)
     *
     * @param forumid  MMBase node number of the forum
     * @param postareaid MMBase node number of the postarea
     * @param activeid MMBase node number of current Poster (on the page)
     * @param moderatorid MMBase node number of moderator you want to remove
     * @return Feedback regarding the success of this action
     */
    public boolean removeModerator(String forumid, String postareaid, int activeid, int moderatorid) {
        Forum f = ForumManager.getForum(forumid);
        if (f != null) {
            PostArea a = f.getPostArea(postareaid);
            if (a != null) {
                Poster ap = f.getPoster(activeid);
                Poster mp = f.getPoster(moderatorid);
                if (ap != null && f.isAdministrator(ap.getNick())) {
                    a.removeModerator(mp);
                }
            }
        }
        return true;
    }


    /**
     * Remove a moderator from a postarea (poster is not removed just status moderator is revoked)
     *
     * @param forumid  MMBase node number of the forum
     * @param postareaid MMBase node number of the postarea
     * @param activeid MMBase node number of current Poster (on the page)
     * @param moderatorid MMBase node number of moderator you want to remove
     * @return Feedback regarding the success of this action
     */
    public boolean removeAdministrator(String forumid, int activeid, int administratorid) {
        Forum f = ForumManager.getForum(forumid);
        if (f != null) {
            Poster ap = f.getPoster(activeid);
            Poster mp = f.getPoster(administratorid);
            if (ap!=mp && f.isAdministrator(ap.getNick())) {
                f.removeAdministrator(mp);
            }
        }
        return true;
    }


    /**
     * update a allready existing posting, it will also update the last edit time
     *
     * @param forumid MMBase node number of the forum
     * @param postareaid MMBase node number of the postarea
     * @param postthreadid MMBase node number of the postthread
     * @param postingid MMBase node number of the postting we want to edit
     * @param activeid MMBase node number of current Poster (on the page)
     * @param subject New subject of the post
     * @param body new body of the post
     * @return Feedback regarding the success of this action
     */
    public boolean editPost(String forumid, String postareaid, String postthreadid, int postingid, int activeid, String subject, String body, String imagecontext) {
        Forum f = ForumManager.getForum(forumid);
        if (f != null) {
            PostArea a = f.getPostArea(postareaid);
            if (a != null) {
                PostThread t = a.getPostThread(postthreadid);
                if (t != null) {
                    Posting p = t.getPosting(postingid);

                    // am i allowed to edit ?
                    Poster ap = f.getPoster(activeid);
                    if (ap.getNick().equals(p.getPoster()) || a.isModerator(ap.getNick())) {
                        p.setSubject(subject);
                        p.setBody(body,imagecontext);
                        p.setEditTime((int) (System.currentTimeMillis() / 1000));
                        p.save();
                        ap.signalSeen();
                    }
                }
            }
        }
        return true;
    }


    /**
     * update a allready existing postthread
     *
     * @param forumid  MMBase node number of the forum
     * @param postareaid MMBase node number of the postarea
     * @param postthreadid MMBase node number of the postthread
     * @param activeid MMBase node number of current Poster (on the page)
     * @param mood  New mood
     * @param state New state
     * @param type  New type
     * @return Feedback regarding the success of this action
     */
    public boolean editPostThread(String forumid, String postareaid, String postthreadid, int activeid, String mood, String state, String type) {
        Forum f = ForumManager.getForum(forumid);
        if (f != null) {
            PostArea a = f.getPostArea(postareaid);
            if (a != null) {
                PostThread t = a.getPostThread(postthreadid);
                if (t != null) {
                    // am i allowed to edit ?
                    Poster ap = f.getPoster(activeid);
                    if (a.isModerator(ap.getNick())) {
                        t.setType(type);
                        t.setMood(mood);
                        t.setState(state);
                        t.save();
                    } else {
                        log.info("postthread edit tried but not allowed by poster");
                    }
                }
            }
        }
        return true;
    }

    /**
     * Remove a Post from a PostArea
     *
     * @param forumid MMBase node number of the forum
     * @param postareaid MMBase node number of the postarea
     * @param postthreadid MMBase node number of postthread
     * @param postingid MMBase node number of the posting
     * @param activeid MMBase node number of the poster
     * @return Feedback regarding the success of this action
     */
    public boolean removePost(String forumid, String postareaid, String postthreadid, int postingid, int activeid) {

        Forum f = ForumManager.getForum(forumid);
        if (f != null) {
            PostArea a = f.getPostArea(postareaid);
            if (a != null) {
                PostThread t = a.getPostThread(postthreadid);
                if (t != null) {
                    Posting p = t.getPosting(postingid);

                    // am i allowed to edit ?
                    Poster ap = f.getPoster(activeid);
                   if (ap.getNick().equals(p.getPoster()) || a.isModerator(ap.getNick())) {
                        p.remove();
                        ap.signalSeen();
                    } else {
			log.info("DELETED KILLED");
		    } 
                }
            }
        }
        return true;
    }

   /**
     * Remove a Poster from a forum
     *
     * @param forumid MMBase node number of the forum
     * @param removeposterid MMBase node number of the poster to be removed
     * @param activeid MMBase node number of the poster
     * @return Feedback regarding the success of this action
     */
    public boolean removePoster(String forumid, int removeposterid, int activeid) {
        Forum f = ForumManager.getForum(forumid);
        if (f != null) {
            Poster posterToRemove = f.getPoster(removeposterid);
            Poster activePoster = f.getPoster(activeid);
            if (posterToRemove != null && f.isAdministrator(activePoster.getNick())) {
                posterToRemove.remove();
            }
        }
        return true;
    }

    /**
      * Disable a Poster from a forum
      *
      * @param forumid MMBase node number of the forum
      * @param disableposterid MMBase node number of the poster to be disabled
      * @param activeid MMBase node number of the poster
      * @return Feedback regarding the success of this action
      */
     public boolean disablePoster(String forumid, int disableposterid, int activeid) {
         Forum f = ForumManager.getForum(forumid);
         if (f != null) {
             Poster posterToDisable = f.getPoster(disableposterid);
             Poster activePoster = f.getPoster(activeid);
             if (posterToDisable != null && f.isAdministrator(activePoster.getNick())) {
                 posterToDisable.disable();
             }
         }
         return true;
     }

    /**
      * Enable a Poster from a forum
      *
      * @param forumid MMBase node number of the forum
      * @param enableposterid MMBase node number of the poster to be disabled
      * @param activeid MMBase node number of the poster
      * @return Feedback regarding the success of this action
      */
     public boolean enablePoster(String forumid, int enableposterid, int activeid) {
         Forum f = ForumManager.getForum(forumid);
         if (f != null) {
             Poster posterToDisable = f.getPoster(enableposterid);
             Poster activePoster = f.getPoster(activeid);
             if (posterToDisable != null && f.isAdministrator(activePoster.getNick())) {
                 posterToDisable.enable();
             }
         }
         return true;
     }


    /**
     * Add a new postarea to the given forum
     *
     * @param forumid MMBase node number of the forum
     * @param name Name of the new post area
     * @param description Description of the new post area
     * @return (virtual) MMObjectNode containing the postareaid of the newly created postarea
     */
    public MMObjectNode newPostArea(String forumid, String name, String description,int activeid) {
        VirtualBuilder builder = new VirtualBuilder(MMBase.getMMBase());
        MMObjectNode virtual = builder.getNewNode("admin");

	name = filterHTML(name);
	description = filterHTML(description);

        Forum f = ForumManager.getForum(forumid);
        if (f != null ) {
        	Poster ap = f.getPoster(activeid);
	    	if (f.isAdministrator(ap.getNick())) {
	   		 if (!name.equals("")) {
            			int postareaid = f.newPostArea(name, description);
            			virtual.setValue("postareaid", postareaid);
            		} else {
				virtual.setValue("feedback","feedback_emptyname");
	    		}
		} else {
				virtual.setValue("feedback","feedback_usernotallowed");
		}
        }
        return virtual;
    }


    /**
     * Add a new forum to the MMBase and create / attach a administrator to it
     *
     * @param name  Name of the new forum
     * @param language Language code of the new forum
     * @param description  Description of the new forum
     * @param account default/first admin account name for this new forum
     * @param password default/first admin password name for this new forum
     * @return (virtual) MMObjectNode containing the forumid of the newly created forum
     */
    public MMObjectNode newForum(String name, String language, String description, String account, String password,String email) {
        VirtualBuilder builder = new VirtualBuilder(MMBase.getMMBase());
        MMObjectNode virtual = builder.getNewNode("admin");
	name = filterHTML(name);
	description = filterHTML(description);
        int forumid = ForumManager.newForum(name, language, description, account, password,email);
        virtual.setValue("forumid", forumid);
        return virtual;
    }


    /**
     * Update forum information
     *
     * @param forumid MMBase node number of the forum
     * @param name  New name for this forum
     * @param language  New language of this forum
     * @param description New description of this forum
     * @return Feedback regarding the success of this action
     */
    public boolean changeForum(String forumid, String name, String language, String description,int activeid) {
       	Forum f = ForumManager.getForum(forumid);
        if (f != null) {
        	Poster ap = f.getPoster(activeid);
	    	if (f.isAdministrator(ap.getNick())) {
	        	f.setName(name);
       	     		f.setLanguage(language);
                	f.setDescription(description);
                	f.saveDirect();
	   	} else {
			return false;
	    	}
        }
        return true;
    }


    public boolean changeForumConfig(String forumid, String loginmodetype, String logoutmodetype, String guestreadmodetype,String guestwritemodetype,String avatarsuploadenabled,String avatarsgalleryenabled,String navigationmethod,String alias,int activeid) {
        Forum f = ForumManager.getForum(forumid);
        if (f != null) {
        	Poster ap = f.getPoster(activeid);
	    	if (f.isAdministrator(ap.getNick())) {
			f.setLogoutModeType(logoutmodetype);
			f.setLoginModeType(loginmodetype);
			f.setGuestReadModeType(guestreadmodetype);
			f.setGuestWriteModeType(guestwritemodetype);
			f.setAvatarsUploadEnabled(avatarsuploadenabled);
			f.setAvatarsGalleryEnabled(avatarsgalleryenabled);
			f.setNavigationMethod(navigationmethod);
			f.setAlias(alias);
			f.saveConfig();
		} else {
			return false;
		}
        }
        return true;
    }


    public boolean changePostAreaConfig(String forumid, String postareaid,String guestreadmodetype,String guestwritemodetype,String threadstartlevel,int position,int activeid) {
        Forum f = ForumManager.getForum(forumid);
        if (f != null) {
        	Poster ap = f.getPoster(activeid);
	    	if (f.isAdministrator(ap.getNick())) {
	                PostArea a = f.getPostArea(postareaid);
       		         if (a!=null) {
				a.setGuestReadModeType(guestreadmodetype);
				a.setGuestWriteModeType(guestwritemodetype);
				a.setThreadStartLevel(threadstartlevel);
				a.setPos(position);
				f.saveConfig();
			}
		} else {
			return false;
		}
        }
        return true;
    }


    public boolean changeForumsConfig(String loginmodetype, String logoutmodetype, String guestreadmodetype,String guestwritemodetype,String avatarsuploadenabled,String avatarsgalleryenabled,String contactinfoenabled,String smileysenabled,String privatemessagesenabled,String postingsperpage) {
	ForumManager.setLogoutModeType(logoutmodetype);
	ForumManager.setLoginModeType(loginmodetype);
	ForumManager.setGuestReadModeType(guestreadmodetype);
	ForumManager.setGuestWriteModeType(guestwritemodetype);
	ForumManager.setAvatarsUploadEnabled(avatarsuploadenabled);
	ForumManager.setAvatarsGalleryEnabled(avatarsgalleryenabled);
	ForumManager.setContactInfoEnabled(contactinfoenabled);
	ForumManager.setSmileysEnabled(smileysenabled);
	ForumManager.setPrivateMessagesEnabled(privatemessagesenabled);
	ForumManager.setPostingsPerPage(postingsperpage);
	ForumManager.saveConfig();
        return true;
    }


    /**
     * Update settings of the given PostArea
     *
     * @param forumid  MMBase node number of the forum
     * @param postareaid MMBase node number of the postarea
     * @param name Name of the postarea
     * @param description Description of the postarea
     * @return Feedback regarding the success of this action
     */
    public boolean changePostArea(String forumid, String postareaid, String name, String description,int activeid) {
        Forum f = ForumManager.getForum(forumid);
        if (f != null) {
            Poster ap = f.getPoster(activeid);
	    if (f.isAdministrator(ap.getNick())) {
            	PostArea a = f.getPostArea(postareaid);
            	if (a != null) {
               	 	a.setName(name);
                	a.setDescription(description);
               		a.save();
            	}
	    }
        }
        return true;
    }


    /**
     * Remove a forum from this MMBase (including postareas, postareas, postthreads, postings and posters).
     *
     * @param sforumid MMBase node number of the forum you want to remove
     * @return Feedback regarding the success of this action
     */
    public boolean removeForum(String sforumid) {
        try {
            int forumid = Integer.parseInt(sforumid);
            ForumManager.removeForum(forumid);
        } catch (Exception e) {
        }
        return true;
    }


    /**
     *
     * @param node
     * @param p
     */
    private void addPosterInfo(MMObjectNode node, Poster p) {
        node.setValue("posterid", p.getId());
        node.setValue("account", p.getAccount());
        node.setValue("nick", p.getNick());
        node.setValue("firstname", p.getFirstName());
        node.setValue("lastname", p.getLastName());
        node.setValue("email", p.getEmail());
        node.setValue("level", p.getLevel());
        node.setValue("location", p.getLocation());
        node.setValue("gender", p.getGender());
        node.setValue("avatar", p.getAvatar());
        node.setValue("accountpostcount", p.getPostCount());
        node.setValue("firstlogin", p.getFirstLogin());
        node.setValue("lastseen", p.getLastSeen());
        node.setValue("signature", p.getSignature());
    }


    /**
     *
     * @param node
     * @param p
     */
    private void addActiveInfo(MMObjectNode node, Poster p) {
        node.setValue("active_id", p.getId());
        node.setValue("active_account", p.getAccount());
        node.setValue("active_nick", p.getNick());
        node.setValue("active_firstname", p.getFirstName());
        node.setValue("active_lastname", p.getLastName());
        node.setValue("active_email", p.getEmail());
        node.setValue("active_level", p.getLevel());
        node.setValue("active_location", p.getLocation());
        node.setValue("active_gender", p.getGender());
        node.setValue("active_firstlogin", p.getFirstLogin());
        node.setValue("active_lastseen", p.getLastSeen());
        node.setValue("active_avatar", p.getAvatar());
        node.setValue("active_postcount", p.getPostCount());
    }

    public boolean setRemoteAddress(String forumid,int posterid,String host) {
        Forum f = ForumManager.getForum(forumid);
        if (f != null) {
		Poster p=f.getPoster(posterid);
		if (p!=null) {
			p.checkRemoteHost(host);
		}
	}
	return true;
    } 

    /**
    * get login information for this poster
    */
    public MMObjectNode forumLogin(String forumid,String account,String password) {
        //log.info("going to login with account: " + account + " and password " + password);
                VirtualBuilder builder = new VirtualBuilder(MMBase.getMMBase());
                MMObjectNode virtual = builder.getNewNode("admin");
		Forum f=ForumManager.getForum(forumid);
		if (f!=null) {
			Poster po=f.getPoster(account);
			if (po!=null) {
				org.mmbase.util.transformers.MD5 md5 = new org.mmbase.util.transformers.MD5();
				String md5passwd = md5.transform(password);
				if (!password.equals("blocked") && (po.getPassword().equals(password) || po.getPassword().equals(md5passwd)) && !po.isBlocked()) {
					virtual.setValue("state","passed");
					virtual.setValue("posterid",po.getId());
				} else {
					virtual.setValue("state","failed");
                    if (po.isBlocked() && (po.getPassword().equals(password) || po.getPassword().equals(md5passwd))) {
                        virtual.setValue("reason","account blocked");
                    } else {
                        virtual.setValue("reason","password not valid");
                    }

				}	
			} else {
				virtual.setValue("state","failed");
				virtual.setValue("reason","account not valid");
			}
		}
		return virtual;
	}

	public String getDefaultPassword() {
		return ForumManager.getDefaultPassword();
	}

	public String getDefaultAccount() {
		return ForumManager.getDefaultAccount();
	}

        private boolean checkIllegalHtml(String input) {
		input = input.toLowerCase();
		if (input.indexOf("<script")!=-1) {
			return true;
		} else if (input.indexOf("<javascript")!=-1) {
			return true;
		} else if (input.indexOf("<input")!=-1) {
			return true;
		}
		return false;
	}

        private boolean checkSpeedPosting(PostArea a,Poster p) {
		if (p.getLastPostTime()!=-1) {
			if ((System.currentTimeMillis()/1000)-a.getSpeedPostTime()<p.getLastPostTime()) {
				return true;
			}
		}
		return false;
        }

        public List getSignatures(String forumid,String sactiveid) {
       		List list = new ArrayList();
  		VirtualBuilder builder = new VirtualBuilder(MMBase.getMMBase());
        	try {
            		int activeid = Integer.parseInt(sactiveid);

            		Forum f = ForumManager.getForum(forumid);
            		if (f != null) {
                       	    Poster poster = f.getPoster(activeid);
		            Iterator e = poster.getSignatures();
			    if (e!=null)  {
            		    	while (e.hasNext()) {
                           	 	Signature sig = (Signature) e.next();
                    			MMObjectNode virtual = builder.getNewNode("admin");
                    			virtual.setValue("id", sig.getId());
                    			virtual.setValue("body", sig.getBody());
                    			virtual.setValue("mode", sig.getMode());
                    			virtual.setValue("encodings", sig.getMode());
					list.add(virtual);
				    }
			    }
			}
        	} catch (Exception e) {
            	e.printStackTrace();
        	}
		return list;
	}

        public List getRemoteHosts(String forumid,String sactiveid) {
       		List list = new ArrayList();
  		VirtualBuilder builder = new VirtualBuilder(MMBase.getMMBase());
        	try {
            		int activeid = Integer.parseInt(sactiveid);

            		Forum f = ForumManager.getForum(forumid);
            		if (f != null) {
                       	    Poster poster = f.getPoster(activeid);
		            Iterator e = poster.getRemoteHosts();
			    if (e!=null)  {
            		    	while (e.hasNext()) {
                           	 	RemoteHost rm = (RemoteHost) e.next();
                    			MMObjectNode virtual = builder.getNewNode("admin");
                    			virtual.setValue("id", ""+rm.getId());
                    			virtual.setValue("host",rm.getHost());
                    			virtual.setValue("lastupdatetime", ""+rm.getLastUpdateTime());
                    			virtual.setValue("updatecount", ""+rm.getUpdateCount());
					list.add(virtual);
				    }
			    }
			}
        	} catch (Exception e) {
            	e.printStackTrace();
        	}
		return list;
	}


        public String changeSignature(String forumid,String sactiveid,int sigid,String body,String mode,String encoding) {
        	try {
            		int activeid = Integer.parseInt(sactiveid);

            		Forum f = ForumManager.getForum(forumid);
            		if (f != null) {
                       	    Poster poster = f.getPoster(activeid);
			    if (poster != null) {
				Signature sig = poster.getSignature(sigid);
				if (sig!=null) {
					sig.setBody(body);
					sig.setEncoding(encoding);
					sig.setMode(mode);
				}
			    }
			}
        	} catch (Exception e) {
	            	e.printStackTrace();
        	}
		return "";
	}


        public String setSingleSignature(String forumid,String sactiveid,String body,String encoding) {
        	try {
            		int activeid = Integer.parseInt(sactiveid);

            		Forum f = ForumManager.getForum(forumid);
            		if (f != null) {
                       	    Poster poster = f.getPoster(activeid);
			    if (poster != null) {
				Signature sig = poster.getSingleSignature();
				if (sig!=null) {
					sig.setBody(body);
					sig.setEncoding(encoding);
				} else {
					poster.addSignature(body,"create",encoding);
				}
			    }
			}
        	} catch (Exception e) {
	            	e.printStackTrace();
        	}
		return "";
	}


        public MMObjectNode getSingleSignature(String forumid,String sactiveid) {
        	VirtualBuilder builder = new VirtualBuilder(MMBase.getMMBase());
       		MMObjectNode virtual = builder.getNewNode("admin");
        	try {
            		int activeid = Integer.parseInt(sactiveid);

            		Forum f = ForumManager.getForum(forumid);
            		if (f != null) {
                       	    Poster poster = f.getPoster(activeid);
			    if (poster != null) {
				Signature sig = poster.getSingleSignature();
				if (sig!=null) {
					virtual.setValue("body",sig.getBody());
					virtual.setValue("encoding",sig.getEncoding());
				}
			    }
			}
        	} catch (Exception e) {
	            	e.printStackTrace();
        	}
		return virtual;
	}


        public String addSignature(String forumid,String sactiveid,String body,String mode,String encoding) {
        	try {
            		int activeid = Integer.parseInt(sactiveid);

            		Forum f = ForumManager.getForum(forumid);
            		if (f != null) {
                       	    Poster poster = f.getPoster(activeid);
			    if (poster != null) {
				poster.addSignature(body,mode,encoding);
			    }
			}
        	} catch (Exception e) {
	            	e.printStackTrace();
        	}
		return "";
	}

    public boolean setBookmarkedChange(String forumid, String postthreadid,int posterid,String state) {
        Forum f = ForumManager.getForum(forumid);
        Poster ap = f.getPoster(posterid);
        if (ap !=null && f != null) {	
		try {
			int id=Integer.parseInt(postthreadid);
			if (state.equals("true")) {
				f.setBookmarkedChange(id,ap,true);
			} else {
				f.setBookmarkedChange(id,ap,false);
			}
		} catch (Exception e) {}
        }
        return false;
    }


    public boolean setEmailOnChange(String forumid, String postthreadid,int posterid,String state) {
        Forum f = ForumManager.getForum(forumid);
        Poster ap = f.getPoster(posterid);
        if (ap !=null && f != null) {	
		try {
			int id=Integer.parseInt(postthreadid);
			if (state.equals("true")) {
				f.setEmailOnChange(id,ap,true);
			} else {
				f.setEmailOnChange(id,ap,false);
			}
		} catch (Exception e) {}
        }
        return false;
    }

    public String filterHTML(String body) {
        StringObject obj=new StringObject(body);
        obj.replace(">", "&gt;");
        obj.replace("<", "&lt;");
	return obj.toString();
    }

    public List getProfileValues(String forumid, int posterid) {
        List list = new ArrayList();
        VirtualBuilder builder = new VirtualBuilder(MMBase.getMMBase());
        Forum f = ForumManager.getForum(forumid);
	if (f!=null && posterid!=-1) {
                Poster po = f.getPoster(posterid);
		if (po!=null) {
                	Iterator i = f.getProfileDefs();
			if (i!=null) {
                	   while (i.hasNext()) {
        			MMObjectNode virtual = builder.getNewNode("admin");
                    		ProfileEntryDef pd = (ProfileEntryDef) i.next();
				if (pd.getGuiPos()!=-1) {
					virtual.setValue("name",pd.getName());
					virtual.setValue("guiname",pd.getGuiName());
					virtual.setValue("guipos",pd.getGuiPos());
					virtual.setValue("edit",""+pd.getEdit());
					virtual.setValue("type",pd.getType());
					ProfileEntry pe = po.getProfileValue(pd.getName());
					if (pe!=null) {
						virtual.setValue("value",pe.getValue());
					}
					list.add(virtual);
				}
			   }
			}
		}
        }
	return list;
    }


    public MMObjectNode setProfileValue(String forumid, int activeid,String name,String value) {
        VirtualBuilder builder = new VirtualBuilder(MMBase.getMMBase());
        MMObjectNode virtual = builder.getNewNode("admin");

	value = filterHTML(value);

        Forum f = ForumManager.getForum(forumid);
        if (f != null ) {
        	Poster ap = f.getPoster(activeid);
		if (ap!=null) {
			String feedback = ap.setProfileValue(name,value);
		}
	}
	return virtual;
    }

    public String getBirthDateString(String name,String value) {
	String day = "1";
	String month = "1";
	String year = "1980";
	StringTokenizer tok=new StringTokenizer(value,"-\n\r");
	if (tok.hasMoreTokens()) {
		day = tok.nextToken();
		if (tok.hasMoreTokens()) {
			month = tok.nextToken();
			if (tok.hasMoreTokens()) {
				year = tok.nextToken();
			}
		}
	}
	String body ="<select name=\""+name+"_day\">";
	for (int i=1;i<32;i++) {
		if (day.equals(""+i)) {
			body+="<option selected>"+i;
		} else {
			body+="<option>"+i;
		}
	}
	body+="</select>";
	body +="<select name=\""+name+"_month\">";
	for (int i=1;i<13;i++) {
		if (month.equals(""+i)) {
			body+="<option selected>"+i;
		} else {
			body+="<option>"+i;
		}
	}
	body+="</select>";
	body+="<select name=\""+name+"_year\">";
	for (int i=1920;i<2004;i++) {
		if (year.equals(""+i)) {
			body+="<option selected>"+i;
		} else {
			body+="<option>"+i;
		}
	}
	body+="</select>";
	return body;
    }

}
