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
 * @version $Id$
 */
public class Controller {

    private static final Logger log = Logging.getLoggerInstance(Controller.class);
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

            Forum f = ForumManager.getForum(id);
            if (f != null) {
                Enumeration e = f.getPostAreas();
                while (e.hasMoreElements()) {
                    PostArea area = (PostArea) e.nextElement();
                    Map map = new HashMap();
                    map.put("name", area.getName());
                    map.put("description", area.getDescription());
                    map.put("id", new Integer(area.getId()));
                    map.put("postthreadcount",new Integer(area.getPostThreadCount()));
                    map.put("postcount",new Integer(area.getPostCount()));
                    map.put("viewcount",new Integer(area.getViewCount()));
                    map.put("lastposter", area.getLastPoster());
                    map.put("lastposttime",new Integer(area.getLastPostTime()));
                    map.put("lastsubject", area.getLastSubject());
                    map.put("moderators", area.getModeratorsLine("profile.jsp"));
                    map.put("lastposternumber",new Integer(area.getLastPosterNumber()));
                    map.put("lastpostnumber",new Integer(area.getLastPostNumber()));
                    map.put("lastpostthreadnumber",new Integer(area.getLastPostThreadNumber()));
                    map.put("guestreadmodetype", area.getGuestReadModeType());
                    map.put("guestwritemodetype", area.getGuestWriteModeType());
                    map.put("threadstartlevel", area.getThreadStartLevel());
                    if (mode.equals("stats")) {
                        map.put("postthreadloadedcount",new Integer(area.getPostThreadLoadedCount()));
                        map.put("postingsloadedcount",new Integer(area.getPostingsLoadedCount()));
                        map.put("memorysize", ((float)area.getMemorySize())/(1024*1024)+"MB");
                    }
                    list.add(map);

                    if (activeid != -1) {
                        Poster ap = f.getPoster(activeid);
                        if (ap != null) {
                            ap.signalSeen();
                            addActiveInfo(map, ap);
                        }
                        if (ap != null && f.isAdministrator(ap.getNick())) {
                            map.put("isadministrator", "true"); // why not using Boolean.TRUE or so?
                        } else {
                            map.put("isadministrator", "false");
                        }
                    }
                }
            }
        } catch (Exception e) {
            log.error(e.getMessage(), e);
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

            Forum f = ForumManager.getForum(id);
            if (f != null) {
                SubArea sa = f.getSubArea(tree);
                Iterator i = sa.getAreas();
                while (i.hasNext()) {
                    PostArea area = (PostArea) i.next();
                    Map  map =  new HashMap();
                    map.put("nodetype","area");
                    map.put("name", area.getName());
                    map.put("shortname", area.getShortName());
                    map.put("description", area.getDescription());
                    map.put("id", new Integer(area.getId()));
                    map.put("postthreadcount", new Integer(area.getPostThreadCount()));
                    map.put("postcount", new Integer(area.getPostCount()));
                    map.put("viewcount", new Integer(area.getViewCount()));
                    map.put("lastposter", area.getLastPoster());
                    map.put("lastposttime", new Integer(area.getLastPostTime()));
                    map.put("lastsubject", area.getLastSubject());
                    map.put("moderators", area.getModeratorsLine("profile.jsp"));
                    map.put("lastposternumber",new Integer(area.getLastPosterNumber()));
                    map.put("lastpostnumber",new Integer(area.getLastPostNumber()));
                    map.put("lastpostthreadnumber",new Integer(area.getLastPostThreadNumber()));
                    map.put("guestreadmodetype", area.getGuestReadModeType());
                    map.put("guestwritemodetype", area.getGuestWriteModeType());
                    map.put("threadstartlevel", area.getThreadStartLevel());
                    list.add(map);

                    if (activeid != -1) {
                        Poster ap = f.getPoster(activeid);
                        ap.signalSeen();
                        addActiveInfo(map, ap);
                        if (ap != null && f.isAdministrator(ap.getNick())) {
                            map.put("isadministrator", "true");
                        } else {
                            map.put("isadministrator", "false");
                        }
                    }
                }
                i = sa.getSubAreas();
                while (i.hasNext()) {
                    sa = (SubArea) i.next();
                    HashMap map =  new HashMap();
                    map.put("nodetype","subarea");
                    map.put("name", sa.getName());
                    map.put("areacount", new Integer(sa.getAreaCount()));
                    map.put("postthreadcount", new Integer(sa.getPostThreadCount()));
                    map.put("postcount", new Integer(sa.getPostCount()));
                    map.put("viewcount", new Integer(sa.getViewCount()));
                    list.add(map);
                    Iterator i2 = sa.getAreas();
                    while (i2.hasNext()) {
                        PostArea area = (PostArea) i2.next();
                        map =  new HashMap();
                        map.put("nodetype","area");
                        map.put("shortname", area.getShortName());
                        map.put("name", area.getName());
                        map.put("description", area.getDescription());
                        map.put("id",new Integer(area.getId()));
                        map.put("postthreadcount",new Integer(area.getPostThreadCount()));
                        map.put("postcount",new Integer(area.getPostCount()));
                        map.put("viewcount",new Integer(area.getViewCount()));
                        map.put("lastposter",area.getLastPoster());
                        map.put("lastposttime",new Integer(area.getLastPostTime()));
                        map.put("lastsubject", area.getLastSubject());
                        map.put("moderators", area.getModeratorsLine("profile.jsp"));
                        map.put("lastposternumber",new Integer(area.getLastPosterNumber()));
                        map.put("lastpostnumber",new Integer(area.getLastPostNumber()));
                        map.put("lastpostthreadnumber",new Integer(area.getLastPostThreadNumber()));
                        map.put("guestreadmodetype", area.getGuestReadModeType());
                        map.put("guestwritemodetype", area.getGuestWriteModeType());
                        map.put("threadstartlevel", area.getThreadStartLevel());
                        list.add(map);

                        if (activeid != -1) {
                            Poster ap = f.getPoster(activeid);
                            ap.signalSeen();
                            addActiveInfo(map, ap);
                            if (ap != null && f.isAdministrator(ap.getNick())) {
                                map.put("isadministrator", "true");
                            } else {
                                map.put("isadministrator", "false");
                            }
                        }
                    }
                }
            }
        } catch (Exception e) {
            log.error(e);
        }
        return list;
    }

    /**
     * Get list of all forums
     *
     * @return List of (mapl) objects representing the available forums
     *
     */
    public static List getForums(String mode) {
        List list = new ArrayList();

        Enumeration e = ForumManager.getForums();
        while (e.hasMoreElements()) {
            Forum f = (Forum) e.nextElement();
            HashMap map = new HashMap();
            map.put("name", f.getName());
            map.put("id", new Integer(f.getId()));
            map.put("description", f.getDescription());
            map.put("postareacount", new Integer(f.getPostAreaCount()));
            map.put("postthreadcount", new Integer(f.getPostThreadCount()));
            map.put("postcount", new Integer(f.getPostCount()));
            map.put("postersonline", new Integer(f.getPostersOnlineCount()));
            map.put("posterstotal", new Integer(f.getPostersTotalCount()));
            map.put("postersnew", new Integer(f.getPostersNewCount()));
            map.put("viewcount", new Integer(f.getViewCount()));
            map.put("lastposter", f.getLastPoster());
            map.put("lastposttime", new Integer(f.getLastPostTime()));
            map.put("lastsubject", f.getLastSubject());
            map.put("lastposternumber",new Integer(f.getLastPosterNumber()));
            map.put("lastposrnumber",new Integer(f.getLastPostNumber()));
            if (mode.equals("stats")) {
                map.put("postthreadloadedcount", new Integer(f.getPostThreadLoadedCount()));
                map.put("postingsloadedcount", new Integer(f.getPostingsLoadedCount()));
                map.put("memorysize",""+((float)f.getMemorySize())/(1024*1024)+"MB");
            }
            list.add(map);
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
     * @return List of (map) representing the postthreads within the postarea
     */
    public List getPostThreads(String forumid, String postareaid, int activeid, int pagesize, int page, int overviewpagesize, String baseurl, String cssclass) {
        List list = new ArrayList();


        if (cssclass == null) cssclass = "";
        // create a result list

        Forum f = ForumManager.getForum(forumid);
        if (f != null) {
            PostArea a = f.getPostArea(postareaid);
            Iterator e = a.getPostThreads(page, overviewpagesize);
            while (e.hasNext()) {
                PostThread thread = (PostThread) e.next();
                HashMap map =  new HashMap();
                String subject = thread.getSubject();
                map.put("name", subject);
                if (subject.length()>60) subject = subject.substring(0,57)+"...";
                map.put("shortname", subject);
                map.put("id",new Integer(thread.getId()));
                map.put("mood", thread.getMood());

                Poster ap = f.getPoster(activeid);
                if (ap != null) {
                    map.put("state", thread.getState(ap));
                    ThreadObserver to = f.getThreadObserver(thread.getId());
                    if (to!=null && to.wantsEmailOnChange(ap)) {
                        map.put("emailonchange","true");  
                    } else {
                        map.put("emailonchange","false");  
                    }
                    if (to!=null && to.isBookmarked(ap)) {
                        map.put("bookmarked","true");  
                    } else {
                        map.put("bookmarked","false");  
                    }
                } else {
                    map.put("state", thread.getState());
                    map.put("emailonchange","false");  
                    map.put("bookmarked","false");  
                }
                map.put("type", thread.getType());
                map.put("creator", thread.getCreator());
                map.put("postcount", new Integer(thread.getPostCount()));
                map.put("pagecount", new Integer(thread.getPageCount(pagesize)));
                map.put("replycount", new Integer(thread.getPostCount() - 1));
                map.put("viewcount", new Integer(thread.getViewCount()));
                map.put("lastposter", thread.getLastPoster());
                map.put("lastposttime",new Integer(thread.getLastPostTime()));
                map.put("lastsubject", thread.getLastSubject());
                //newnode.setStringValue("threadnav",thread.getLastSubject());

                // temp until sure if we also want to be able to set this from html
                int overflowpage = f.getPostingsOverflowPostArea();
                map.put("navline", thread.getNavigationLine(baseurl, pagesize,overflowpage, cssclass));
                map.put("lastposternumber",new Integer(thread.getLastPosterNumber()));
                map.put("lastpostnumber",new Integer(thread.getLastPostNumber()));
                list.add(map);
            }
        }

        return list;
    }


    public List getBookmarkedThreads(String forumid, String postareaid, int activeid, int pagesize, int page, int overviewpagesize, String baseurl, String cssclass) {
        List list = new ArrayList();


        Forum f = ForumManager.getForum(forumid);
        if (f != null) {
            PostArea a = f.getPostArea(postareaid);
            if (activeid != -1) {
                Poster ap = f.getPoster(activeid);
                Iterator e = ap.getBookmarkedThreads(page, overviewpagesize);
                while (e.hasNext()) {
                    Integer tid = (Integer) e.next();
                    PostThread thread = f.getPostThread(""+tid);
                    HashMap map =  new HashMap();
                    map.put("mood", thread.getMood());
                    map.put("state", thread.getState());
                    map.put("postthreadid",new Integer(thread.getId()));
                    map.put("postareaname", thread.getParent().getName());
                    map.put("postareaid",new Integer(thread.getParent().getId()));
                    map.put("forumid", new Integer(thread.getParent().getParent().getId()));
                    map.put("type", thread.getType());
                    map.put("creator", thread.getCreator());
                    map.put("postcount", new Integer(thread.getPostCount()));
                    map.put("pagecount", new Integer(thread.getPageCount(pagesize)));
                    map.put("replycount", new Integer(thread.getPostCount() - 1));
                    map.put("viewcount", new Integer(thread.getViewCount()));
                    map.put("lastposter", thread.getLastPoster());
                    map.put("lastposttime", new Integer(thread.getLastPostTime()));
                    map.put("lastsubject", thread.getLastSubject());
                    map.put("lastposternumber",new Integer(thread.getLastPosterNumber()));
                    map.put("lastpostnumber",new Integer(thread.getLastPostNumber()));
                    int overflowpage = f.getPostingsOverflowPostArea();
                    map.put("navline", thread.getNavigationLine(baseurl,  pagesize,overflowpage, cssclass));
                    list.add(map);
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
     * @return List of (mp) representing the postings within the given postthread
     */
    public List getPostings(String forumid, String postareaid, String postthreadid, int activeid, int page, int pagesize, String imagecontext) {
        List list = new ArrayList();
        //long start = System.currentTimeMillis();

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
                        HashMap map =  new HashMap();
                        map.put("pos", new Integer(pos++));
                        String subject = p.getSubject();
                        map.put("subject", subject);
                        if (subject.length()>60) subject = subject.substring(0,57)+"...";
                        map.put("shortsubject", subject);
                        map.put("body", p.getBodyHtml(imagecontext));
                        map.put("edittime", new Integer(p.getEditTime()));
                        Poster po = f.getPosterNick(p.getPoster());
                        if (po != null) {
                            map.put("poster", p.getPoster());
                            addPosterInfo(map, po);
                        } else {
                            map.put("poster", p.getPoster());
                            map.put("guest", "true");
                        }
                        map.put("posttime",new Integer(p.getPostTime()));
                        map.put("id",new Integer(p.getId()));
                        map.put("threadpos",new Integer(p.getThreadPos()));
                        // very weird way need to figure this out
                        if (p.getThreadPos() % 2 == 0) {
                            map.put("tdvar", "threadpagelisteven");
                        } else {
                            map.put("tdvar", "threadpagelistodd");
                        }
                        // should be moved out of the loop
                        if (activeid != -1) {
                            Poster ap = f.getPoster(activeid);
                            if (ap != null) {
                                ap.signalSeen();
                                ap.seenThread(t);
                                addActiveInfo(map, ap);
                            }
                            if (ap != null && po != null && po.getNick().equals(ap.getNick())) {
                                map.put("isowner", "true");
                            } else {
                                map.put("isowner", "false");
                            }
                            if (ap != null && a.isModerator(ap.getNick())) {
                                map.put("ismoderator", "true");
                            } else {
                                map.put("ismoderator", "false");
                            }
                        }
                        list.add(map);
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
     * @return List of (map) representing the postings within the given postthread
     */
    public Map getPosting(String forumid, String postareaid, String postthreadid, String postingid, int activeid, String imagecontext) {
        List list = new ArrayList();
        long start = System.currentTimeMillis();

        Map map = new HashMap();
        Forum f = ForumManager.getForum(forumid);
        if (f != null) {
            PostArea a = f.getPostArea(postareaid);
            if (a != null) {
                PostThread t = a.getPostThread(postthreadid);
                if (t != null) {
                    Posting p = t.getPosting(Integer.parseInt(postingid));
                    if (p!=null) {
                        String subject = p.getSubject();
                        map.put("subject", subject);
                        if (subject.length()>60) subject = subject.substring(0,57)+"...";
                        map.put("shortsubject", subject);
                        map.put("body", p.getBodyHtml(imagecontext));
                        map.put("edittime", new Integer(p.getEditTime()));
                        Poster po = f.getPosterNick(p.getPoster());
                        if (po != null) {
                            map.put("poster", p.getPoster());
                            addPosterInfo(map, po);
                        } else {
                            map.put("poster", p.getPoster());
                            map.put("guest", "true");
                        }
                        map.put("posttime",new Integer(p.getPostTime()));
                        map.put("postcount", new Integer(t.getPostCount()));
                        map.put("id", new Integer(p.getId()));
                        map.put("threadpos",new Integer(p.getThreadPos()));
                        //very weird way need to figure this out
                        if (p.getThreadPos()%2==0) {
                            map.put("tdvar", "threadpagelisteven");
                        } else {
                            map.put("tdvar", "threadpagelistodd");
                        }
                        // should be moved out of the loop
                        if (activeid != -1) {
                            Poster ap = f.getPoster(activeid);
                            ap.signalSeen();
                            ap.seenThread(t);
                            addActiveInfo(map, ap);
                            if (po != null && po.getNick().equals(ap.getNick())) {
                                map.put("isowner", "true");
                            } else {
                                map.put("isowner", "false");
                            }
                            if (ap != null && a.isModerator(ap.getNick())) {
                                map.put("ismoderator", "true");
                            } else {
                                map.put("ismoderator", "false");
                            }
                            if (po != null && po.getNick().equals(ap.getNick()) || ap != null && a.isModerator(ap.getNick())) {
                                map.put("maychange", "true");
                            } else {
                                map.put("maychange", "false");
                            }
                        }
                    }
                }
            }
        }
        long end = System.currentTimeMillis();
        //log.info("getPosting "+(end-start)+"ms");

        return map;
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
     * @return List of (map) representing the moderators of this forum / postarea.
     *         contains id, account, firstname, lastname of the moderator
     */
    public List getModerators(String forumid, String postareaid) {
        List list = new ArrayList();

        Forum f = ForumManager.getForum(forumid);
        if (f != null) {
            PostArea a = f.getPostArea(postareaid);
            if (a != null) {
                Enumeration e = a.getModerators();
                while (e.hasMoreElements()) {
                    Poster p = (Poster) e.nextElement();
                    HashMap map =  new HashMap();
                    map.put("id",new Integer(p.getId()));
                    map.put("account", p.getAccount());
                    map.put("nick", p.getNick());
                    map.put("firstname", p.getFirstName());
                    map.put("lastname", p.getLastName());
                    list.add(map);
                }
            }
        }
        return list;
    }


    /**
     * Get the administrators of this forum
     *
     * @param forumid MMBase node number of the forum
     * @return List of (map) representing the administrators of this forum 
     *         contains id, account, firstname, lastname of the administrator
     */
    public List getAdministrators(String forumid) {
        List list = new ArrayList();

        Forum f = ForumManager.getForum(forumid);
        if (f != null) {
            Enumeration e = f.getAdministrators();
            while (e.hasMoreElements()) {
                Poster p = (Poster) e.nextElement();
                HashMap map =  new HashMap();
                map.put("id",new Integer(p.getId()));
                map.put("account", p.getAccount());
                map.put("nick", p.getNick());
                map.put("firstname", p.getFirstName());
                map.put("lastname", p.getLastName());
                list.add(map);
            }
        }
        return list;
    }


    /**
     * Get the posters that are now online in this forum
     * @param forumid MMBase node number of the forum
     * @return  List of (map) representing the online posters for the given forum
     */
    public List getPostersOnline(String forumid) {
        List list = new ArrayList();

        Forum f = ForumManager.getForum(forumid);
        if (f != null) {
            Enumeration e = f.getPostersOnline();
            while (e.hasMoreElements()) {
                Poster p = (Poster) e.nextElement();
                HashMap map =  new HashMap();
                map.put("id", new Integer(p.getId()));
                map.put("account", p.getAccount());
                map.put("nick", p.getNick());
                map.put("firstname", p.getFirstName());
                map.put("lastname", p.getLastName());
                map.put("location", p.getLocation());
                map.put("level", p.getLevel());
                map.put("levelgui", p.getLevelGui());
                map.put("levelimage", p.getLevelImage());
                map.put("lastseen", new Integer(p.getLastSeen()));
                map.put("blocked", ""+p.isBlocked());
                list.add(map);
            }
        }
        return list;
    }


    public List getPosters(String forumid,String searchkey,int page,int pagesize) {
        searchkey=searchkey.toLowerCase();
        List list = new ArrayList();

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
                        HashMap map = new HashMap();
                        map.put("number", new Integer(p.getId()));
                        map.put("account", p.getAccount());
                        map.put("nick", p.getNick());
                        map.put("firstname",p.getFirstName());
                        map.put("lastname", p.getLastName());
                        map.put("location", p.getLocation());
                        map.put("level", p.getLevel());
                        map.put("levelgui", p.getLevelGui());
                        map.put("levelimage", p.getLevelImage());
                        map.put("blocked", ""+p.isBlocked());
                        map.put("lastseen", new Integer(p.getLastSeen()));
                        if (page!=0) {
                            map.put("prevpage",new Integer(page-1));
                        } else {
                            map.put("prevpage",new Integer(-1));
                        }
                        map.put("nextpage",new Integer(-1));
                        list.add(map);
                        j++;    
                        if (j>pagesize) {
                            map.put("nextpage",new Integer(page+1));
                            break;
                        }
                    }
                    i++;
                }
            }
        }
        return list;
    }


    public List searchPostings(String forumid,String searchareaid,String searchpostthreadid,String searchkey,int posterid,int page,int pagesize) {
        log.info("SEARCH CALLED = "+posterid);
        long start = System.currentTimeMillis();
        searchkey = searchkey.toLowerCase();
        List list = new ArrayList();

        Forum f = ForumManager.getForum(forumid);
        if (f != null) {
            int startpos = page*pagesize;
            int i = 1;
            int j = 1;
            List<Posting> l = null;
            if (!searchareaid.equals("-1")) {
                if (!searchpostthreadid.equals("-1")) {
                    PostArea a = f.getPostArea(searchareaid);
                    if (a != null) {
                        PostThread t = a.getPostThread(searchpostthreadid);
                        l = t.searchPostings(searchkey,posterid);
                    }
                } else {
                    PostArea a = f.getPostArea(searchareaid);
                    if (a!=null) l = a.searchPostings(searchkey,posterid);
                }
            } else {
                l = f.searchPostings(searchkey,posterid);
            }
            if (l != null) {
                Iterator<Posting> iterator = l.iterator();
                while (iterator.hasNext() && j<25) {
                    Posting p = iterator.next();
                    Map map =  new HashMap();
                    map.put("postingid", new Integer(p.getId()));
                    PostThread pt = p.getParent();
                    PostArea pa = pt.getParent();
                    map.put("postareaid",new Integer(pa.getId()));
                    map.put("postareaname", pa.getName());
                    map.put("postthreadid",new Integer(pt.getId()));
                    String subject = p.getSubject();
                    map.put("subject", subject);
                    if (subject.length()>60) subject = subject.substring(0,57)+"...";
                    map.put("shortsubject", subject);
                    map.put("poster", p.getPoster());
                    map.put("posterid", f.getPoster(p.getPoster()));
                    list.add(map);
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
     * @return List of (map) representing all posters of the given postarea which are no moderators
     */
    public List getNonModerators(String forumid, String postareaid,String searchkey) {
        List list = new ArrayList();

        Forum f = ForumManager.getForum(forumid);
        if (f != null) {
            PostArea a = f.getPostArea(postareaid);
            if (a != null) {
                Enumeration e = a.getNonModerators(searchkey);
                while (e.hasMoreElements()) {
                    Poster p = (Poster) e.nextElement();
                    HashMap map =  new HashMap();
                    map.put("id", new Integer(p.getId()));
                    map.put("account", p.getAccount());
                    map.put("nick", p.getNick());
                    map.put("firstname", p.getFirstName());
                    map.put("lastname", p.getLastName());
                    list.add(map);
                }
            }
        }
        return list;
    }


    /**
     * List all the posters not allready a administrator for this forum
     *
     * @param forumid MMBase node number of the forum
     * @return List of (map) representing all posters of the given postarea which are no moderators
     */
    public List getNonAdministrators(String forumid,String searchkey) {
        List list = new ArrayList();

        Forum f = ForumManager.getForum(forumid);
        if (f != null) {
            Enumeration e = f.getNonAdministrators(searchkey);
            while (e.hasMoreElements()) {
                Poster p = (Poster) e.nextElement();
                HashMap map = new HashMap();
                map.put("id",new Integer(p.getId()));
                map.put("account", p.getAccount());
                map.put("nick", p.getNick());
                map.put("firstname", p.getFirstName());
                map.put("lastname", p.getLastName());
                list.add(map);
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
     * @return (map) representing info for the given forum
     *
     */
    public Map getForumInfo(String id, String sactiveid) {
        Map map =  new HashMap();
        try {
            int activeid = Integer.parseInt(sactiveid);

            Forum f = ForumManager.getForum(id);
            if (f != null) {
                map.put("name", f.getName());
                map.put("language", f.getLanguage());
                map.put("accountcreationtype", f.getAccountCreationType());
                map.put("accountremovaltype", f.getAccountRemovalType());
                map.put("loginsystemtype", f.getLoginSystemType());
                map.put("loginmodetype", f.getLoginModeType());
                map.put("logoutmodetype", f.getLogoutModeType());
                map.put("navigationmethod", f.getNavigationMethod());
                map.put("privatemessagesenabled", f.getPrivateMessagesEnabled());
                map.put("description", f.getDescription());
                map.put("postareacount", new Integer(f.getPostAreaCount()));
                map.put("postthreadcount",new Integer(f.getPostThreadCount()));
                map.put("postcount",new Integer(f.getPostCount()));
                map.put("postersonline",new Integer(f.getPostersOnlineCount()));
                map.put("posterstotal",new Integer(f.getPostersTotalCount()));
                map.put("postersnew",new Integer(f.getPostersNewCount()));
                map.put("viewcount",new Integer(f.getViewCount()));
                map.put("lastposter", f.getLastPoster());
                map.put("lastposttime", new Integer(f.getLastPostTime()));
                map.put("lastsubject", f.getLastSubject());
                map.put("hasnick", new Boolean(f.hasNick()));
                if (activeid != -1) {
                    Poster ap = f.getPoster(activeid);
                    if (ap != null) {
                        ap.signalSeen();
                        addActiveInfo(map, ap);
                    }
                    if (ap != null && f.isAdministrator(ap.getNick())) {
                        map.put("isadministrator", "true");
                    } else {
                        map.put("isadministrator", "false");
                    }
                }
            }
        } catch (Exception e) {
            log.error(e.getMessage(), e);
        }

        return map;
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
            log.error(e.getMessage(), e);
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
            log.error(e.getMessage(), e);
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
            log.error(e.getMessage(), e);
        }
        return "";
    }


    /**
     * Provide configuration info on a forum
     *
     * @param id MMBase node number of the forum
     * @param sactiveid Id for the current (on the page) poster for admin/onwership checks
     * @return (map) representing the configuration of the given forum
     *
     */
    public Map getForumConfig(String id, String sactiveid) {
        HashMap map =  new HashMap();
        try {
            int activeid = Integer.parseInt(sactiveid);

            Forum f = ForumManager.getForum(id);
            if (f != null) {
                map.put("language", f.getLanguage());
                map.put("accountcreationtype", f.getAccountCreationType());
                map.put("accountremovaltype", f.getAccountRemovalType());
                map.put("loginsystemtype", f.getLoginSystemType());
                map.put("loginmodetype", f.getLoginModeType());
                map.put("logoutmodetype", f.getLogoutModeType());
                map.put("guestreadmodetype", f.getGuestReadModeType());
                map.put("guestwritemodetype", f.getGuestWriteModeType());
                map.put("avatarsdisabled", f.getAvatarsDisabled());
                map.put("avatarsuploadenabled", f.getAvatarsUploadEnabled());
                map.put("avatarsgalleryenabled", f.getAvatarsGalleryEnabled());
                map.put("contactinfoenabled", f.getContactInfoEnabled());
                map.put("smileysenabled", f.getSmileysEnabled());
                map.put("privatemessagesenabled", f.getPrivateMessagesEnabled());
                map.put("postingsperpage", new Integer(f.getPostingsPerPage()));
                map.put("fromaddress",f.getFromEmailAddress());
                map.put("headerpath",f.getHeaderPath());
                map.put("footerpath",f.getFooterPath());
                map.put("replyoneachpage",new Boolean(f.getReplyOnEachPage()));
                map.put("navigationmethod",f.getNavigationMethod());
                map.put("alias",f.getAlias());

                if (activeid != -1) {
                    Poster ap = f.getPoster(activeid);
                    ap.signalSeen();
                    addActiveInfo(map, ap);
                    if (ap != null && f.isAdministrator(ap.getNick())) {
                        map.put("isadministrator", "true");
                    } else {
                        map.put("isadministrator", "false");
                    }
                }
            }
        } catch (Exception e) {
            log.error(e.getMessage(), e);
        }
        return map;
    }


    /**
     * Provide configuration info on a forum
     *
     * @param id MMBase node number of the forum
     * @param sactiveid Id for the current (on the page) poster for admin/onwership checks
     * @return (map) representing the configuration of the given forum
     *
     */
    public Map getPostAreaConfig(String id, String sactiveid,String postareaid) {
        HashMap map = new HashMap();
        try {
            int activeid = Integer.parseInt(sactiveid);

            Forum f = ForumManager.getForum(id);
            if (f != null) {
                PostArea a = f.getPostArea(postareaid);
                if (a!=null) {
                    map.put("guestreadmodetype", a.getGuestReadModeType());
                    map.put("guestwritemodetype", a.getGuestWriteModeType());
                    map.put("position",new Integer(a.getPos()));
                    map.put("threadstartlevel", a.getThreadStartLevel());
                }
            }
        } catch (Exception e) {
            log.error(e.getMessage(), e);
        }

        return map;
    }

    public Map getForumsConfig() {
        HashMap map = new HashMap();
        map.put("language", ForumManager.getLanguage());
        map.put("accountcreationtype", ForumManager.getAccountCreationType());
        map.put("accountremovaltype", ForumManager.getAccountRemovalType());
        map.put("loginmodetype", ForumManager.getLoginModeType());
        map.put("loginsystemtype", ForumManager.getLoginSystemType());
        map.put("logoutmodetype", ForumManager.getLogoutModeType());
        map.put("guestreadmodetype", ForumManager.getGuestReadModeType());
        map.put("guestwritemodetype", ForumManager.getGuestWriteModeType());
        map.put("avatarsuploadenabled", ForumManager.getAvatarsUploadEnabled());
        map.put("avatarsgalleryenabled", ForumManager.getAvatarsGalleryEnabled());
        map.put("contactinfoenabled", ForumManager.getContactInfoEnabled());
        map.put("smileysenabled", ForumManager.getSmileysEnabled());
        map.put("privatemessagesenabled", ForumManager.getPrivateMessagesEnabled());
        map.put("postingsperpage",new Integer(ForumManager.getPostingsPerPage()));
        map.put("fromaddress",ForumManager.getFromEmailAddress());
        map.put("headerpath",ForumManager.getHeaderPath());
        map.put("footerpath",ForumManager.getFooterPath());
        return map;
    }





    /**
     * Provide info on a poster forum
     *
     * @param id MMBase node number of the forum
     * @param posterid Id for poster we want (string/account field)
     * @return (map) representing info for the given poster
     */
    public Map getPosterInfo(String id, String posterid) {
        HashMap map =  new HashMap();
        Forum f = ForumManager.getForum(id);
        if (f != null) {
            if (posterid != null) {
                Poster po = f.getPoster(posterid);
                if (po==null) {
                    try {
                        int tmpi = Integer.parseInt(posterid);
                        po = f.getPoster(tmpi);
                    } catch(Exception e) {
                        log.error(e.getMessage(), e);
                    }
                }
                addPosterInfo(map, po);
            }
        }
        return map;
    }


    /**
     * Provide quota info on a poster
     *
     * @param id MMBase node number of the forum
     * @param posterid Id for poster we want (string/account field)
     * @return (map) representing posters quota info
     */
    public Map getQuotaInfo(String id, int posterid,int barsize) {
        HashMap map = new HashMap();
        Forum f = ForumManager.getForum(id);
        if (f != null) {
            if (posterid != -1) {
                Poster po = f.getPoster(posterid);
                map.put("quotareached",new Boolean(po.isQuotaReached()));
                int t=po.getQuotaNumber();
                int u=po.getQuotaUsedNumber();
                float d=100/(float)t;
                float b=(float)barsize/t;
                int up=(int)(d*u);
                int ub=(int)(b*u);

                // log.info("u="+u+" d="+d+" up="+up+" b="+b+" ub="+ub);

                map.put("quotausedpercentage",new Integer(up));
                map.put("quotaunusedpercentage",new Integer(100-up));
                map.put("quotanumber",new Integer(t));
                map.put("quotausednumber",new Integer(u));
                map.put("quotaunusednumber",new Integer(t-u));
                map.put("quotausedbar",new Integer(ub));

                if (u>ForumManager.getQuotaSoftWarning()) {
                    if (u>ForumManager.getQuotaWarning()) {
                        map.put("quotawarning","red");
                    } else {
                        map.put("quotawarning","orange");
                    }
                } else {
                    map.put("quotawarning","green");
                }
            }
        }
        return map;
    }


    /**
     * Provide info a mailbox
     *
     * @param id MMBase node number of the forum
     * @param posterid Id for poster we want (string/account field)
     * @param mailboxid Id for mailbox we want
     * @return (map) representing info for the given poster
     */
    public Map getMailboxInfo(String id, int posterid,String mailboxid) {
        HashMap map = new HashMap();
        Forum f = ForumManager.getForum(id);
        if (f != null) {
            if (posterid != -1) {
                Poster po = f.getPoster(posterid);
                if (po != null ) {
                    Mailbox mb=po.getMailbox(mailboxid);
                    if (mb != null) {
                        map.put("messagecount",new Integer(mb.getMessageCount()));
                        map.put("messageunreadcount",new Integer(mb.getMessageUnreadCount()));
                        map.put("messagenewcount",new Integer(mb.getMessageNewCount()));
                    }
                }
            }
        }
        return map;
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
        HashMap map  = new HashMap();
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
            log.info("newpassword is empty");
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
                log.info("newpassword equals newconfirmpassword");
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
                return "profilechanged";
            } else {
                log.info("newpassword and confirmpassword are not equal");
                return "newpasswordnotequal";
            }
        }
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
    public String editProfilePoster(String forumid, int posterid, int profileid, String firstname, String lastname, String email, String gender, String location, String newpassword, String newconfirmpassword) {
        if (newpassword.equals("")) {
            log.info("newpassword is empty");
            Forum f = ForumManager.getForum(forumid);
            if (f != null) {
                Poster p = f.getPoster(profileid);
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
                log.info("newpassword equals newconfirmpassword");
                Forum f = ForumManager.getForum(forumid);
                if (f != null) {
                    Poster p = f.getPoster(profileid);
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
                return "profilechanged";
            } else {
                log.info("newpassword and confirmpassword are not equal");
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
                    if (firstname.equals("") || firstname.length() < 2) return "firstnameerror";
                    if (lastname.equals("") || lastname.length() < 1)   return "lastnameerror";
                    if (email.equals("") || email.indexOf("@") ==-1 || email.indexOf(".") == -1) return "emailerror";
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
            return "passwordnotequal";
        }
    }


    public String createPosterNick(String forumid, String account, String password, String confirmpassword,String nick, String firstname, String lastname, String email, String gender, String location) {
        if (password.equals(confirmpassword)) {
            Forum f = ForumManager.getForum(forumid);
            if (f != null) {
                Poster p = f.getPoster(account);
                Poster n = f.getPosterNick(nick);
                if (p == null) {
                    // weird hack since entree demands the use of a nick
                    if (f.getLoginSystemType().equals("entree")) {
                        if (n!=null || nick.equals("")) return "nickinuse";
                    } else {
                        if (n!=null && !nick.equals("")) return "nickinuse";
                    }
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
                        if (nick!=null && !nick.equals("")) setProfileValue(forumid, p.getId(),"nick",nick);
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
     * @return (map) representing info for the given postarea
     */
    public Map getPostAreaInfo(String forumid, String postareaid, int activeid, int page, int pagesize, String baseurl, String cssclass) {
        Map map = new HashMap();

        Forum f = ForumManager.getForum(forumid);
        if (f != null) {
            PostArea a = f.getPostArea(postareaid);
            map.put("name", a.getName());
            map.put("postthreadcount", Integer.valueOf(a.getPostThreadCount()));
            map.put("postcount", Integer.valueOf(a.getPostCount()));
            map.put("viewcount", Integer.valueOf(a.getViewCount()));
            map.put("lastposter", a.getLastPoster());
            map.put("lastposttime", Integer.valueOf(a.getLastPostTime()));
            map.put("lastsubject", a.getLastSubject());
            map.put("guestreadmodetype", a.getGuestReadModeType());
            map.put("guestwritemodetype", a.getGuestWriteModeType());
            map.put("threadstartlevel", a.getThreadStartLevel());
            map.put("privatemessagesenabled", f.getPrivateMessagesEnabled());
            map.put("smileysenabled", f.getSmileysEnabled());
            map.put("navline", a.getNavigationLine(baseurl, page, pagesize, cssclass));
            map.put("pagecount", Integer.valueOf(a.getPageCount(pagesize)));
            if (activeid != -1) {
                Poster ap = f.getPoster(activeid);
                if (ap != null) {
                    ap.signalSeen();
                } else {
                    log.warn("No poster object found for id '" + activeid + "'");
                    //throw new RuntimeException("No poster object found for id '" + activeid + "'");
                }

                if (ap != null && f.isAdministrator(ap.getNick())) {
                    map.put("isadministrator", "true");
                } else {
                    map.put("isadministrator", "false");
                }
                if (ap != null && a.isModerator(ap.getNick())) {
                    map.put("ismoderator", "true");
                } else {
                    map.put("ismoderator", "false");
                }
            }
        }
        return map;
    }


    public Map getPostThreadInfo(String forumid, String postareaid, String postthreadid,int pagesize) {
        Map map = new HashMap();

        Forum f = ForumManager.getForum(forumid);
        if (f != null) {
            PostArea a = f.getPostArea(postareaid);
            if (a != null) {
                PostThread t = a.getPostThread(postthreadid);
                if (t != null) {
                    map.put("threadstate", t.getState());
                    map.put("threadmood", t.getMood());
                    map.put("threadtype", t.getType());
                    map.put("pagecount",new Integer(t.getPageCount(pagesize)));
                }
            }
        }
        return map;
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
     * @return (map) containing navline, lastpage, pagecount
     */
    public Map getPostThreadNavigation(String forumid, String postareaid, String postthreadid, int posterid, int page, int pagesize, String baseurl, String cssclass) {
        Map map =  new HashMap();

        if (cssclass == null) cssclass = "";

        Forum f = ForumManager.getForum(forumid);
        if (f != null) {
            PostArea a = f.getPostArea(postareaid);
            if (a != null) {
                PostThread t = a.getPostThread(postthreadid);
                if (t != null) {
                    int overflowpage = f.getPostingsOverflowThreadPage();
                    map.put("navline", t.getNavigationLine(baseurl, page, pagesize,overflowpage, cssclass));
                    map.put("lastpage", "" + t.isLastPage(page, pagesize));
                    map.put("pagecount",new Integer(t.getPageCount(pagesize)));
                    Poster ap = f.getPoster(posterid);
                    if (ap != null) {
                        ThreadObserver to = f.getThreadObserver(t.getId());
                        if (to!=null && to.wantsEmailOnChange(ap)) {
                            map.put("emailonchange","true");  
                        } else {
                            map.put("emailonchange","false");  
                        }
                        if (to!=null && to.isBookmarked(ap)) {
                            map.put("bookmarked","true");  
                        } else {
                            map.put("bookmarked","false");  
                        }
                    } else {
                        map.put("emailonchange","false");  
                        map.put("bookmarked","false");  
                    }
                }
            }
        }
        return map;
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
    public Map postReply(String forumid, String postareaid, String postthreadid, String subject, String poster, String body) {
        HashMap map = new HashMap();

        if (subject.length() > 60) subject = subject.substring(0, 57) + "..."; 

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
                    if ((!t.getState().equals("closed")|| !t.getState().equals("pinnedclosed") || a.isModerator(poster)) && (p==null || !p.isBlocked())) {
                        if (body.equals("")) {
                            map.put("error", "no_body");
                        } else if (p!=null && p.checkDuplicatePost("",body)) {
                            map.put("error", "duplicate_post");
                        } else if (checkIllegalHtml(body)) {
                            map.put("error", "illegal_html");
                        } else if (p!=null && checkSpeedPosting(a,p)) {
                            map.put("error", "speed_posting");
                            map.put("speedposttime", ""+a.getSpeedPostTime());
                        } else {
                            body = a.filterContent(body);
                            subject = filterHTML(subject);
                            // temp fix for [ ] quotes.
                            body = BBCode.encode(body);
                            try {
                                t.postReply(subject, p, body,false);
                                map.put("error", "none");
                                if (p!=null) {
                                    p.setLastBody(body);
                                    p.setLastPostTime((int)(System.currentTimeMillis()/1000));
                                }
                            } catch (Exception e) {
                                log.info("Error while posting a reply");
                                map.put("error", "illegal_html");
                            }
                        }
                    }
                } else {
                    log.warn("No thread with id '" + postthreadid + "'");
                }
            } else {
                log.warn("No post area with id '" + postareaid + "'");
            }
        } else {
            log.warn("No forum with id '" + forumid + "'");
        }
        return map;
    }

    /**
     * add a new post (postthread+1 posting) in a postarea, use postReply for all following postings in the postthread
     *
     * @param forumid  MMBase node number of the forum
     * @param postareaid MMBase node number of the postarea
     * @param subject Subject of the new post
     * @param poster Posterid to be attached to the postthread as its creator
     * @param body Body of the new post
     * @return  (map) containing the postthreadid of the newly created post
     */
    public Map<String, Object> newPost(String forumid, String postareaid, String subject, String poster, String body, String mood) {

        Map<String, Object> map = new HashMap<String, Object>();

        if (subject.length() > 60) subject = subject.substring(0, 57) + "...";

        Forum f = ForumManager.getForum(forumid);
        if (f != null) {
            PostArea a = f.getPostArea(postareaid);
            if (log.isDebugEnabled()) {
                log.debug("Posting to " + f + " area " + a);
            }
            Poster p = f.getPoster(poster);
            if (a != null && (p==null || !p.isBlocked())) {
                if (subject.equals("")) {
                    map.put("error", "no_subject");
                } else if (body.equals("")) {
                    map.put("error", "no_body");
                } else if (checkIllegalHtml(subject)) {
                    map.put("error", "illegal_html");
                } else if (checkIllegalHtml(body)) {
                    map.put("error", "illegal_html");
                } else if (p != null && p.checkDuplicatePost(subject, body)) {
                    map.put("error", "duplicate_post");
                } else if (checkMaxPostSize(subject,body)) {
                    map.put("error", "maxpostsize");
                } else if (p!=null && checkSpeedPosting(a, p)) {
                    map.put("error", "speed_posting");
                    map.put("speedposttime", "" + a.getSpeedPostTime());
                } else {
                    body = a.filterContent(body);
                    subject = filterHTML(subject);
                    int postthreadid = a.newPost(subject, p, body,mood,false);
                    map.put("postthreadid", Integer.valueOf(postthreadid));
                    map.put("error", "none");
                    if (p!=null) {
                        p.setLastSubject(subject);
                        p.setLastBody(body);
                        p.setLastPostTime((int)(System.currentTimeMillis()/1000));
                    }
                }
            } else {
                log.debug("Not posting because area=" + a + " poster=" + p + " " + (p == null ? "" : ("(blocked: " + p.isBlocked() + ")")));
            }
        } else {
            log.debug("Coudl not find forum " + forumid);
        }
        return map;
    }

    /**
     * send a private message to a other poster
     *
     * @param forumid MMBase node number of the forum
     * @param subject Subject of the new message
     * @param poster Poster who is sending the message
     * @param to Poster to which to send the message
     * @param body Body of the new post
     * @return (map) containing privatemessageid of the newly created private message
     */
    public Map newPrivateMessage(String forumid, String subject, String poster, String to, String body) {

        HashMap map =  new HashMap();
        Forum f = ForumManager.getForum(forumid);
        if (f != null && !f.getPoster(poster).isBlocked()) {
            if (to.indexOf(",")==-1) {
                int privatemessageid = f.newPrivateMessage(poster, to, subject, body);
                map.put("privatemessageid",new Integer(privatemessageid));
            } else {
                StringTokenizer tok=new StringTokenizer(to,",\n\r");
                while (tok.hasMoreTokens()) {
                    String pto = tok.nextToken();
                    f.newPrivateMessage(poster, pto, subject, body);
                }
            }
        }
        return map;
    }


    /**
     *
     * @param forumid
     * @param activeid
     * @param newfolder
     */
    public Node newFolder(String forumid, int activeid, String newfolder) {
        Map map = new HashMap();
        Forum f = ForumManager.getForum(forumid);
        if (f != null) {
            int folderid = f.newFolder(activeid, newfolder);
            map.put("folderid", new Integer(folderid));
        }
        return new org.mmbase.bridge.util.MapNode(map);
    }


    /**
     *
     * @param forumid
     * @param activeid
     * @param foldername
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
     * @param sactiveid MMBase node number of current Poster (on the page)
     * @param sadministratorid MMBase node number of moderator you want to add
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
                if (mp != null) {
                    if (ap != null && f.isAdministrator(ap.getNick())) {
                        log.service("Added administrator " + moderatorid + " to forum " + f);
                        f.addAdministrator(mp);
                    } else {
                        log.warn("Cannot make " + sadministratorid + " administrator by " + activeid + " because she is no adminstrator herself: " + ap);
                        return false;
                    }
                } else {
                    log.warn("Could not find poster with id " + moderatorid);
                    return false;
                }
            } else {
                log.warn("No forum with id " + forumid + " found");
                return false;
            }
        } catch (Exception e) {
            log.error(e.getMessage(), e);
            return false;
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
     * @param activeid MMBase node number of current Poster (on the page)
     * @param administratorid MMBase node number of moderator you want to remove
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
                        p.setBody(body,imagecontext,false);
                        p.setEditTime((int) (System.currentTimeMillis() / 1000));
                        p.save();

                        // if its the first posting we should also change lastsubjects
                        log.info("EDITPOS="+p.getThreadPos());
                        if (p.getThreadPos()==0) {
                            // change PostThread
                            p.getParent().setLastSubject(p.getSubject());
                            p.getParent().setSubject(p.getSubject());
                            p.getParent().save();
                            // change PostArea
                            p.getParent().getParent().setLastSubject(p.getSubject());
                            p.getParent().getParent().save();
                            // change Forum
                            p.getParent().getParent().getParent().setLastSubject(p.getSubject());
                            p.getParent().getParent().getParent().save();
                        }
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
     * move a existing postthread
     *
     * @param forumid  MMBase node number of the forum
     * @param postareaid MMBase node number of the postarea
     * @param postthreadid MMBase node number of the postthread
     * @param activeid MMBase node number of current Poster (on the page)
     * @param newpostareaid  New mood
     * @return Feedback regarding the success of this action
     */
    public boolean movePostThread(String forumid, String postareaid, String postthreadid, int activeid, String newpostareaid) {
        Forum f = ForumManager.getForum(forumid);
        if (f != null) {
            PostArea a = f.getPostArea(postareaid);
            if (a != null) {
                PostThread t = a.getPostThread(postthreadid);
                if (t != null) {
                    // am i allowed to move ?
                    Poster ap = f.getPoster(activeid);
                    if (a.isModerator(ap.getNick())) {
                        a.movePostThread(postthreadid,newpostareaid,ap);
                    } else {
                        log.info("postthread move tried but not allowed by poster");
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
     * @return (map) containing the postareaid of the newly created postarea
     */
    public Map newPostArea(String forumid, String name, String description, int activeid) {
        Map map = new HashMap();

        name = filterHTML(name);
        description = filterHTML(description);

        Forum f = ForumManager.getForum(forumid);
        if (f != null ) {
            Poster ap = f.getPoster(activeid);
            if (ap != null && f.isAdministrator(ap.getNick())) {
                if (!name.equals("")) {
                    int postareaid = f.newPostArea(name, description);
                    map.put("postareaid", new Integer(postareaid));
                } else {
                    map.put("feedback","feedback_emptyname");
                }
            } else {
                map.put("feedback", ap == null ? "User '" + activeid + "' not recognized" : "feedback_usernotallowed");
            }
        }
        return map;
    }


    /**
     * Add a new forum to the MMBase and create / attach a administrator to it
     *
     * @param name  Name of the new forum
     * @param language Language code of the new forum
     * @param description  Description of the new forum
     * @param account default/first admin account name for this new forum
     * @param password default/first admin password name for this new forum
     * @return (map) containing the forumid of the newly created forum
     */
    public Map<String, Object> newForum(String name, String language, String description, String account, String password, String nick, String email) {
        Map<String, Object> map = new HashMap();
        name = filterHTML(name);
        description = filterHTML(description);
        int forumid = ForumManager.newForum(name, language, description, account, password, nick, email);
        map.put("forumid", Integer.valueOf(forumid));
        Forum forum = ForumManager.getForum(forumid);
        map.put("adminid", Integer.valueOf(forum.getAdministrators().nextElement().getId()));
        return map;
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
            if (ap != null && f.isAdministrator(ap.getNick())) {
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


    public boolean changeForumPostingsPerPage(String forumid,int activeid,int maxpostcount) {
        Forum f = ForumManager.getForum(forumid);
        if (f != null) {
            Poster ap = f.getPoster(activeid);
            if (f.isAdministrator(ap.getNick())) {
                f.setPostingsPerPage(maxpostcount);
                f.saveConfig();
            } else {
                return false;
            }
        }
        return true;
    }


    public boolean changeForumReplyOnEachPage(String forumid,int activeid,String value) {
        Forum f = ForumManager.getForum(forumid);
        if (f != null) {
            Poster ap = f.getPoster(activeid);
            if (f.isAdministrator(ap.getNick())) {
                if (value.equals("true")) {
                    f.setReplyOnEachPage(true);
                } else {
                    f.setReplyOnEachPage(false);
                }
                f.saveConfig();
            } else {
                return false;
            }
        }
        return true;
    }


    public boolean changeForumSpeedPostTime(String forumid,int activeid,int delay) {
        Forum f = ForumManager.getForum(forumid);
        if (f != null) {
            Poster ap = f.getPoster(activeid);
            if (f.isAdministrator(ap.getNick())) {
                f.setSpeedPostTime(delay);
                f.saveConfig();
            } else {
                return false;
            }
        }
        return true;
    }


    public int getForumSpeedPostTime(String forumid,int activeid) {
        Forum f = ForumManager.getForum(forumid);
        if (f != null) {
            Poster ap = f.getPoster(activeid);
            if (f.isAdministrator(ap.getNick())) {
                return f.getSpeedPostTime();
            }
        }
        return -1;
    }


    public boolean changeForumPostingsOverflowPostArea(String forumid,int activeid,int count) {
        Forum f = ForumManager.getForum(forumid);
        if (f != null) {
            Poster ap = f.getPoster(activeid);
            if (f.isAdministrator(ap.getNick())) {
                f.setPostingsOverflowPostArea(count);
                f.saveConfig();
            } else {
                return false;
            }
        }
        return true;
    }


    public int getForumPostingsOverflowPostArea(String forumid,int activeid) {
        Forum f = ForumManager.getForum(forumid);
        if (f != null) {
            Poster ap = f.getPoster(activeid);
            if (f.isAdministrator(ap.getNick())) {
                return f.getPostingsOverflowPostArea();
            }
        }
        return -1;
    }


    public int getForumPostingsPerPage(String forumid,int activeid) {
        Forum f = ForumManager.getForum(forumid);
        if (f != null) {
            Poster ap = f.getPoster(activeid);
            if (f.isAdministrator(ap.getNick())) {
                return f.getPostingsPerPage();
            }
        }
        return -1;
    }


    public boolean changeForumPostingsOverflowThreadPage(String forumid,int activeid,int count) {
        Forum f = ForumManager.getForum(forumid);
        if (f != null) {
            Poster ap = f.getPoster(activeid);
            if (f.isAdministrator(ap.getNick())) {
                f.setPostingsOverflowThreadPage(count);
                f.saveConfig();
            } else {
                return false;
            }
        }
        return true;
    }


    public int getForumPostingsOverflowThreadPage(String forumid,int activeid) {
        Forum f = ForumManager.getForum(forumid);
        if (f != null) {
            Poster ap = f.getPoster(activeid);
            if (f.isAdministrator(ap.getNick())) {
                return f.getPostingsOverflowThreadPage();
            }
        }
        return -1;
    }


    public boolean getForumReplyOnEachPage(String forumid,int activeid) {
        Forum f = ForumManager.getForum(forumid);
        if (f != null) {
            Poster ap = f.getPoster(activeid);
            if (f.isAdministrator(ap.getNick())) {
                return f.getReplyOnEachPage();
            }
        }
        return false;
    }


    public boolean addWordFilter(String forumid, String name, String value,int activeid) {
        Forum f = ForumManager.getForum(forumid);
        if (f != null) {
            Poster ap = f.getPoster(activeid);
            if (f.isAdministrator(ap.getNick())) {
                f.addWordFilter(name,value);
                f.saveConfig();
            } else {
                return false;
            }
        }
        return true;
    }


    public boolean removeWordFilter(String forumid, String name,int activeid) {
        Forum f = ForumManager.getForum(forumid);
        if (f != null) {
            Poster ap = f.getPoster(activeid);
            if (f.isAdministrator(ap.getNick())) {
                f.removeWordFilter(name);
                f.saveConfig();
            } else {
                return false;
            }
        }
        return true;
    }


    public boolean changeForumConfig(String forumid,String loginsystemtype,String loginmodetype, String logoutmodetype, String guestreadmodetype,String guestwritemodetype,String avatarsuploadenabled,String avatarsgalleryenabled,String navigationmethod,String alias,int activeid) {
        Forum f = ForumManager.getForum(forumid);
        if (f != null) {
            Poster ap = f.getPoster(activeid);
            if (f.isAdministrator(ap.getNick())) {
                if (!loginsystemtype.equals("fixed")) f.setLoginSystemType(loginsystemtype);
                if (!logoutmodetype.equals("fixed")) f.setLogoutModeType(logoutmodetype);
                if (!loginmodetype.equals("fixed")) f.setLoginModeType(loginmodetype);
                if (!guestreadmodetype.equals("fixed")) f.setGuestReadModeType(guestreadmodetype);
                if (!guestwritemodetype.equals("fixed")) f.setGuestWriteModeType(guestwritemodetype);
                if (!avatarsuploadenabled.equals("fixed")) f.setAvatarsUploadEnabled(avatarsuploadenabled);
                if (!avatarsgalleryenabled.equals("fixed")) f.setAvatarsGalleryEnabled(avatarsgalleryenabled);
                if (!navigationmethod.equals("fixed")) f.setNavigationMethod(navigationmethod);
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


    public boolean changeForumsConfig(String loginsystemtype,String loginmodetype, String logoutmodetype, String guestreadmodetype,String guestwritemodetype,String avatarsuploadenabled,String avatarsgalleryenabled,String contactinfoenabled,String smileysenabled,String privatemessagesenabled,String postingsperpage) {
        ForumManager.setLogoutModeType(logoutmodetype);
        ForumManager.setLoginModeType(loginmodetype);
        ForumManager.setLoginSystemType(loginsystemtype);
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
            if (ap != null && f.isAdministrator(ap.getNick())) {
                PostArea a = f.getPostArea(postareaid);
                if (a != null) {
                    a.setName(name);
                    a.setDescription(description);
                    a.save();
                } else {
                    log.warn("Cannot find post area " + postareaid);
                }
            } else {
                log.warn("Cannot change postarea because " + activeid + " (" + ap + ") is no administrator");
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
            log.error(e.getMessage(), e);
        }
        return true;
    }


    /**
     *
     * @param node
     * @param p
     */
    private void addPosterInfo(Map map, Poster p) {
        map.put("posterid", new Integer(p.getId()));
        map.put("account", p.getAccount());
        map.put("nick", p.getNick());
        map.put("firstname", p.getFirstName());
        map.put("lastname", p.getLastName());
        map.put("email", p.getEmail());
        map.put("level", p.getLevel());
        map.put("levelgui", p.getLevelGui());
        map.put("levelimage", p.getLevelImage());
        map.put("location", p.getLocation());
        map.put("gender", p.getGender());
        map.put("avatar", new Integer(p.getAvatar()));
        map.put("accountpostcount", new Integer(p.getPostCount()));
        map.put("firstlogin", new Integer(p.getFirstLogin()));
        map.put("lastseen", new Integer(p.getLastSeen()));
        map.put("signature", p.getSignature());
    }


    /**
     *
     * @param node
     * @param p
     */
    private void addActiveInfo(Map map, Poster p) {
        map.put("active_id", new Integer(p.getId()));
        map.put("active_account", p.getAccount());
        map.put("active_nick", p.getNick());
        map.put("active_firstname", p.getFirstName());
        map.put("active_lastname", p.getLastName());
        map.put("active_email", p.getEmail());
        map.put("active_level", p.getLevel());
        map.put("active_levelgui", p.getLevelGui());
        map.put("active_levelimage", p.getLevelGui());
        map.put("active_location", p.getLocation());
        map.put("active_gender", p.getGender());
        map.put("active_firstlogin", new Integer(p.getFirstLogin()));
        map.put("active_lastseen", new Integer(p.getLastSeen()));
        map.put("active_avatar", new Integer(p.getAvatar()));
        map.put("active_postcount", new Integer(p.getPostCount()));
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
    public Map forumLogin(String forumid,String account,String password) {
        //log.info("going to login with account: " + account + " and password " + password);
        Map map = new HashMap();
        Forum f = ForumManager.getForum(forumid);
        if (f != null) {
            Poster po = f.getPoster(account);
            if (po != null) {
                org.mmbase.util.transformers.MD5 md5 = new org.mmbase.util.transformers.MD5();
                String md5passwd = md5.transform(password);
                if (!password.equals("blocked") && (po.getPassword().equals(password) || po.getPassword().equals(md5passwd)) && !po.isBlocked()) {
                    map.put("state","passed");
                    map.put("posterid", new Integer(po.getId()));
                } else {
                    map.put("state","failed");
                    if (po.isBlocked() && (po.getPassword().equals(password) || po.getPassword().equals(md5passwd))) {
                        map.put("reason","account blocked");
                    } else {
                        map.put("reason","password not valid");
                    }
                }
            } else {
                map.put("state","failed");
                map.put("reason","account not valid");
            }
        }
        return map;
    }


    public Map getPosterPassword(String forumid,String account) {
        Map map = new HashMap();
        Forum f=ForumManager.getForum(forumid);
        if (f!=null) {
            Poster po=f.getPoster(account);
            if (po!=null) {
                map.put("password",po.getPassword());
                map.put("failed","false");
            } else {
                map.put("failed","true");
                map.put("reason","noaccount");
            }
        }
        return map;
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


    private boolean checkMaxPostSize(String subject,String body) {
        if (subject.length()>128) {
            return true;
        } else if (body.length()>(32*1024)) {
            return true;
        }
        return false;
    }

    private boolean checkSpeedPosting(PostArea a, Poster p) {
        if (p.getLastPostTime()!=-1) {
            if ((System.currentTimeMillis()/1000) - a.getSpeedPostTime() < p.getLastPostTime()) {
                return true;
            }
        }
        return false;
    }

    public List getSignatures(String forumid,String sactiveid) {
        List list = new ArrayList();
        try {
            int activeid = Integer.parseInt(sactiveid);

            Forum f = ForumManager.getForum(forumid);
            if (f != null) {
                Poster poster = f.getPoster(activeid);
                Iterator e = poster.getSignatures();
                if (e!=null)  {
                    while (e.hasNext()) {
                        Signature sig = (Signature) e.next();
                        Map map = new HashMap();
                        map.put("id", new Integer(sig.getId()));
                        map.put("body", sig.getBody());
                        map.put("mode", sig.getMode());
                        map.put("encodings", sig.getMode());
                        list.add(map);
                    }
                }
            }
        } catch (Exception e) {
            log.error(e.getMessage(), e);
        }
        return list;
    }

    public List getRemoteHosts(String forumid,String sactiveid) {
        List list = new ArrayList();
        try {
            int activeid = Integer.parseInt(sactiveid);

            Forum f = ForumManager.getForum(forumid);
            if (f != null) {
                Poster poster = f.getPoster(activeid);
                Iterator e = poster.getRemoteHosts();
                if (e!=null)  {
                    while (e.hasNext()) {
                        RemoteHost rm = (RemoteHost) e.next();
                        Map map = new HashMap();
                        map.put("id", ""+rm.getId());
                        map.put("host",rm.getHost());
                        map.put("lastupdatetime", ""+rm.getLastUpdateTime());
                        map.put("updatecount", ""+rm.getUpdateCount());
                        list.add(map);
                    }
                }
            }
        } catch (Exception e) {
            log.error(e.getMessage(), e);
        }
        return list;
    }


    public String changeSignature(String forumid, String sactiveid, int sigid, String body, String mode, String encoding) {
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
            log.error(e.getMessage(), e);
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
            log.error(e.getMessage(), e);
        }
        return "";
    }


    public Map getSingleSignature(String forumid,String sactiveid) {
        Map map = new HashMap();
        try {
            int activeid = Integer.parseInt(sactiveid);

            Forum f = ForumManager.getForum(forumid);
            if (f != null) {
                Poster poster = f.getPoster(activeid);
                if (poster != null) {
                    Signature sig = poster.getSingleSignature();
                    if (sig!=null) {
                        map.put("body",sig.getBody());
                        map.put("encoding",sig.getEncoding());
                    }
                }
            }
        } catch (Exception e) {
            log.error(e.getMessage(), e);
        }
        return map;
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
            log.error(e.getMessage(), e);
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
            } catch (Exception e) {
                log.error(e.getMessage(), e);
            }
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
            } catch (Exception e) {
                log.error(e.getMessage(), e);
            }
        }
        return false;
    }

    // much much too simple
    public String filterHTML(String body) {
        StringObject obj = new StringObject(body);
        obj.replace(">", "&gt;");
        obj.replace("<", "&lt;");
        return obj.toString();
    }

    public List getProfileValues(String forumid, int posterid,int guipos) {
        List list = new ArrayList();
        Forum f = ForumManager.getForum(forumid);
        if (f!=null && posterid!=-1) {
            Poster po = f.getPoster(posterid);
            if (po!=null) {
                Iterator i = f.getProfileDefs();
                if (i!=null) {
                    while (i.hasNext()) {
                        Map map = new HashMap();
                        ProfileEntryDef pd = (ProfileEntryDef) i.next();
                        if (pd.getGuiPos()>=guipos) {
                            map.put("name", pd.getName());
                            map.put("guiname", pd.getGuiName());
                            map.put("guipos", new Integer(pd.getGuiPos()));
                            map.put("edit", "" + pd.getEdit());
                            map.put("type", pd.getType());
                            ProfileEntry pe = po.getProfileValue(pd.getName());
                            if (pe!=null) {
                                map.put("value",pe.getValue());
                                if (pd.getExternal()!=null) {
                                    map.put("synced",""+pe.getSynced());
                                } else {
                                    map.put("synced","internal");
                                }
                            } else {
                                map.put("synced","not set");
                            }
                            list.add(map);
                        }
                    }
                }
            }
        }
        return list;
    }


    public List getFilterWords(String forumid) {
        List list = new ArrayList();
        Forum f = ForumManager.getForum(forumid);
        if (f!=null) {
            Map words = f.getFilterWords();
            Iterator i = words.keySet().iterator();
            while (i.hasNext()) {
                String key =  (String)i.next();
                String value = (String)words.get(key);
                HashMap map = new HashMap();
                map.put("name",key);
                map.put("value",value);
                list.add(map);
            }
        }
        return list;
    }



    public Map setProfileValue(String forumid, int activeid,String name,String value) {
        Map map = new HashMap();

        value = filterHTML(value);

        Forum f = ForumManager.getForum(forumid);
        if (f != null ) {
            Poster ap = f.getPoster(activeid);
            if (ap!=null) {
                String feedback = ap.setProfileValue(name,value);
            }
        }
        return map;
    }

    public String getBirthDateString(String name,String value) {
        // very ugly need to be changed
        String day = "1";
        String month = "1";
        String year = "1980";
        StringTokenizer tok = new StringTokenizer(value,"-\n\r");
        if (tok.hasMoreTokens()) {
            day = tok.nextToken();
            if (tok.hasMoreTokens()) {
                month = tok.nextToken();
                if (tok.hasMoreTokens()) {
                    year = tok.nextToken();
                }
            }
        }

        // TODO use StringBuilder.
        String body ="<select name=\"" + name + "_day\">";
        for (int i = 1; i < 32; i++) {
            if (day.equals("" + i)) {
                body += "<option selected>"+i;
            } else {
                body += "<option>" + i;
            }
        }
        body += "</select>";
        body += "<select name=\"" + name + "_month\">";
        for (int i = 1; i < 13; i++) {
            if (month.equals("" + i)) {
                body += "<option selected>" + i;
            } else {
                body += "<option>" + i;
            }
        }
        body += "</select>";
        body += "<select name=\"" + name + "_year\">";
        for (int i = 1920; i < 2004; i++) {
            if (year.equals("" + i)) {
                body += "<option selected>" + i;
            } else {
                body += "<option>" + i;
            }
        }
        body += "</select>";
        return body;
    }


    public String getGuiEdit(String id, String key) {
        Forum f = ForumManager.getForum(id);
        if (f != null) {
            return f.getGuiEdit(key);
        }
        return "true";
    }


}
