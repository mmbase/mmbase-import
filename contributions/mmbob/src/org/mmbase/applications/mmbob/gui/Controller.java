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
     * @param id MMBase node number of the forum
     * @param sactiveid MMBase node number of the active poster
     * @return List of postareas that matches the given params
     */
    public List getPostAreas(String id, String sactiveid, String mode) {
        List list = new ArrayList();
        try {
            int activeid = Integer.parseInt(sactiveid);

            Forum forum = ForumManager.getForum(id);
            if (forum != null) {
                Enumeration e = forum.getPostAreas();
                while (e.hasMoreElements()) {
                    PostArea area = (PostArea) e.nextElement();
                    Map map = new HashMap();
                    map.put("name", area.getName());
                    map.put("description", area.getDescription());
                    map.put("id", new Integer(area.getId()));
                    map.put("postthreadcount", new Integer(area.getPostThreadCount()));
                    map.put("postcount", new Integer(area.getPostCount()));
                    map.put("viewcount", new Integer(area.getViewCount()));
                    map.put("lastposter", area.getLastPoster());
                    map.put("lastposttime", new Integer(area.getLastPostTime()));
                    map.put("lastsubject", area.getLastSubject());
                    map.put("moderators", area.getModeratorsLine("profile.jsp"));
                    map.put("lastposternumber", new Integer(area.getLastPosterNumber()));
                    map.put("lastpostnumber", new Integer(area.getLastPostNumber()));
                    map.put("lastpostthreadnumber", new Integer(area.getLastPostThreadNumber()));
                    map.put("guestreadmodetype", area.getGuestReadModeType());
                    map.put("guestwritemodetype", area.getGuestWriteModeType());
                    map.put("threadstartlevel", area.getThreadStartLevel());
                    if (mode.equals("stats")) {
                        map.put("postthreadloadedcount", new Integer(area.getPostThreadLoadedCount()));
                        map.put("postingsloadedcount", new Integer(area.getPostingsLoadedCount()));
                        map.put("memorysize", ((float) area.getMemorySize()) / (1024 * 1024) + "MB");
                    }
                    list.add(map);

                    if (activeid != -1) {
                        Poster poster = forum.getPoster(activeid);
                        poster.signalSeen();
                        addActiveInfo(map, poster);
                        if (poster != null && forum.isAdministrator(poster.getNick())) {
                            map.put("isadministrator", "true");
                        } else {
                            map.put("isadministrator", "false");
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
     * Get the PostAreas of the given forum
     * 
     * @param id MMBase node number of the forum
     * @param sactiveid MMBase node number of the active poster
     * @return List of postareas that matches the given params
     */
    public List getTreePostAreas(String id, String sactiveid, String tree) {
        List list = new ArrayList();
        try {
            int activeid = Integer.parseInt(sactiveid);

            Forum forum = ForumManager.getForum(id);
            if (forum != null) {
                SubArea subArea = forum.getSubArea(tree);
                Iterator i = subArea.getAreas();
                while (i.hasNext()) {
                    PostArea area = (PostArea) i.next();
                    Map map = new HashMap();
                    map.put("nodetype", "area");
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
                    map.put("lastposternumber", new Integer(area.getLastPosterNumber()));
                    map.put("lastpostnumber", new Integer(area.getLastPostNumber()));
                    map.put("lastpostthreadnumber", new Integer(area.getLastPostThreadNumber()));
                    map.put("guestreadmodetype", area.getGuestReadModeType());
                    map.put("guestwritemodetype", area.getGuestWriteModeType());
                    map.put("threadstartlevel", area.getThreadStartLevel());
                    list.add(map);

                    if (activeid != -1) {
                        Poster poster = forum.getPoster(activeid);
                        poster.signalSeen();
                        addActiveInfo(map, poster);
                        if (poster != null && forum.isAdministrator(poster.getNick())) {
                            map.put("isadministrator", "true");
                        } else {
                            map.put("isadministrator", "false");
                        }
                    }
                }
                
                i = subArea.getSubAreas();
                while (i.hasNext()) {
                    subArea = (SubArea) i.next();
                    HashMap map = new HashMap();
                    map.put("nodetype", "subarea");
                    map.put("name", subArea.getName());
                    map.put("areacount", new Integer(subArea.getAreaCount()));
                    map.put("postthreadcount", new Integer(subArea.getPostThreadCount()));
                    map.put("postcount", new Integer(subArea.getPostCount()));
                    map.put("viewcount", new Integer(subArea.getViewCount()));
                    list.add(map);
                    Iterator i2 = subArea.getAreas();
                    while (i2.hasNext()) {
                        PostArea area = (PostArea) i2.next();
                        map = new HashMap();
                        map.put("nodetype", "area");
                        map.put("shortname", area.getShortName());
                        map.put("name", area.getName());
                        map.put("description", area.getDescription());
                        map.put("id", new Integer(area.getId()));
                        map.put("postthreadcount", new Integer(area.getPostThreadCount()));
                        map.put("postcount", new Integer(area.getPostCount()));
                        map.put("viewcount", new Integer(area.getViewCount()));
                        map.put("lastposter", area.getLastPoster());
                        map.put("lastposttime", new Integer(area.getLastPostTime()));
                        map.put("lastsubject", area.getLastSubject());
                        map.put("moderators", area.getModeratorsLine("profile.jsp"));
                        map.put("lastposternumber", new Integer(area.getLastPosterNumber()));
                        map.put("lastpostnumber", new Integer(area.getLastPostNumber()));
                        map.put("lastpostthreadnumber", new Integer(area.getLastPostThreadNumber()));
                        map.put("guestreadmodetype", area.getGuestReadModeType());
                        map.put("guestwritemodetype", area.getGuestWriteModeType());
                        map.put("threadstartlevel", area.getThreadStartLevel());
                        list.add(map);

                        if (activeid != -1) {
                            Poster poster = forum.getPoster(activeid);
                            poster.signalSeen();
                            addActiveInfo(map, poster);
                            if (poster != null && forum.isAdministrator(poster.getNick())) {
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
            Forum forum = (Forum) e.nextElement();
            HashMap map = new HashMap();
            map.put("name", forum.getName());
            map.put("id", new Integer(forum.getId()));
            map.put("description", forum.getDescription());
            map.put("postareacount", new Integer(forum.getPostAreaCount()));
            map.put("postthreadcount", new Integer(forum.getPostThreadCount()));
            map.put("postcount", new Integer(forum.getPostCount()));
            map.put("postersonline", new Integer(forum.getPostersOnlineCount()));
            map.put("posterstotal", new Integer(forum.getPostersTotalCount()));
            map.put("postersnew", new Integer(forum.getPostersNewCount()));
            map.put("viewcount", new Integer(forum.getViewCount()));
            map.put("lastposter", forum.getLastPoster());
            map.put("lastposttime", new Integer(forum.getLastPostTime()));
            map.put("lastsubject", forum.getLastSubject());
            map.put("lastposternumber", new Integer(forum.getLastPosterNumber()));
            map.put("lastposrnumber", new Integer(forum.getLastPostNumber()));
            if (mode.equals("stats")) {
                map.put("postthreadloadedcount", new Integer(forum.getPostThreadLoadedCount()));
                map.put("postingsloadedcount", new Integer(forum.getPostingsLoadedCount()));
                map.put("memorysize", "" + ((float) forum.getMemorySize()) / (1024 * 1024) + "MB");
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
     * @param overviewpagesize ?
     * @param baseurl Base url for links in the navigation html
     * @param cssclass Stylesheet name for the url links
     * @return List of (map) representing the postthreads within the postarea
     */
    public List getPostThreads(String forumid, String postareaid, int activeid, int pagesize, int page, int overviewpagesize,
            String baseurl, String cssclass) {
        List list = new ArrayList();

        if (cssclass == null) cssclass = "";
        // create a result list

        Forum forum = ForumManager.getForum(forumid);
        if (forum != null) {
            PostArea postArea = forum.getPostArea(postareaid);
            //TODO: here overviewpagesize is taken from params, but below it is taken from forum.getPostingsOverflowPostArea(). what da...
            Iterator e = postArea.getPostThreads(page, overviewpagesize);
            while (e.hasNext()) {
                PostThread thread = (PostThread) e.next();
                HashMap map = new HashMap();
                String subject = thread.getSubject();
                map.put("name", subject);
                if (subject.length() > 60) subject = subject.substring(0, 57) + "...";
                map.put("shortname", subject);
                map.put("id", new Integer(thread.getId()));
                map.put("mood", thread.getMood());

                Poster activePoster = forum.getPoster(activeid);
                if (activePoster != null) {
                    map.put("state", thread.getState(activePoster));
                    ThreadObserver to = forum.getThreadObserver(thread.getId());
                    if (to != null && to.wantsEmailOnChange(activePoster)) {
                        map.put("emailonchange", "true");
                    } else {
                        map.put("emailonchange", "false");
                    }
                    if (to != null && to.isBookmarked(activePoster)) {
                        map.put("bookmarked", "true");
                    } else {
                        map.put("bookmarked", "false");
                    }
                } else {
                    map.put("state", thread.getState());
                    map.put("emailonchange", "false");
                    map.put("bookmarked", "false");
                }
                map.put("type", thread.getType());
                map.put("creator", thread.getCreator());
                map.put("postcount", new Integer(thread.getPostCount()));
                map.put("pagecount", new Integer(thread.getPageCount(pagesize)));
                map.put("replycount", new Integer(thread.getPostCount() - 1));
                map.put("viewcount", new Integer(thread.getViewCount()));
                map.put("lastposter", thread.getLastPoster());
                map.put("lastposttime", new Integer(thread.getLastPostTime()));
                map.put("lastsubject", thread.getLastSubject());
                // newnode.setStringValue("threadnav",thread.getLastSubject());

                // temp until sure if we also want to be able to set this from html
                int overflowpage = forum.getPostingsOverflowPostArea();
                String navLine = thread.getNavigationLine(baseurl, pagesize, overflowpage, cssclass);
                map.put("navline", thread.getNavigationLine(baseurl, pagesize, overflowpage, cssclass));
                map.put("lastposternumber", new Integer(thread.getLastPosterNumber()));
                map.put("lastpostnumber", new Integer(thread.getLastPostNumber()));
                list.add(map);
                log.info("hallo daar");
            }
        }

        return list;
    }

    public List getBookmarkedThreads(String forumid, String postareaid, int activeid, int pagesize, int page, int overviewpagesize,
            String baseurl, String cssclass) {
        List list = new ArrayList();

        Forum forum = ForumManager.getForum(forumid);
        if (forum != null) {
            PostArea postArea = forum.getPostArea(postareaid);
            if (activeid != -1) {
                Poster poster = forum.getPoster(activeid);
                Iterator e = poster.getBookmarkedThreads(page, overviewpagesize);
                while (e.hasNext()) {
                    Integer threadId = (Integer) e.next();
                    PostThread thread = forum.getPostThread("" + threadId);
                    HashMap map = new HashMap();
                    map.put("mood", thread.getMood());
                    map.put("state", thread.getState());
                    map.put("postthreadid", new Integer(thread.getId()));
                    map.put("postareaname", thread.getParent().getName());
                    map.put("postareaid", new Integer(thread.getParent().getId()));
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
                    map.put("lastposternumber", new Integer(thread.getLastPosterNumber()));
                    map.put("lastpostnumber", new Integer(thread.getLastPostNumber()));
                    int overflowpage = forum.getPostingsOverflowPostArea();
                    map.put("navline", thread.getNavigationLine(baseurl, pagesize, overflowpage, cssclass));
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
     * @param postthreadid MMBase node number of the postthread
     * @param activeid MMBase node number of current Poster (on the page)
     * @param page Page number of the threads we want
     * @param pagesize The number of postings per page
     * @param imagecontext The context where to find the images (eg smilies)
     * @return List of (mp) representing the postings within the given postthread
     */
    public List getPostings(String forumid, String postareaid, String postthreadid, int activeid, int page, int pagesize,
            String imagecontext) {
        List list = new ArrayList();
        // long start = System.currentTimeMillis();

        Forum forum = ForumManager.getForum(forumid);
        if (forum != null) {
            PostArea postArea = forum.getPostArea(postareaid);
            if (postArea != null) {
                PostThread thread = postArea.getPostThread(postthreadid);
                if (thread != null) {
                    int pageCount = thread.getPageCount(pagesize);
                    if (page == -1 || page > pageCount) {
                        page = pageCount;
                    }
                    Iterator e = thread.getPostings(page, pagesize);
                    int pos = ((page - 1) * pagesize) + 1;

                    while (e.hasNext()) {
                        Posting posting = (Posting) e.next();
                        HashMap map = new HashMap();
                        map.put("pos", new Integer(pos++));
                        String subject = posting.getSubject();
                        map.put("subject", subject);
                        if (subject.length() > 60) {
                            subject = subject.substring(0, 57) + "...";
                        }
                        map.put("shortsubject", subject);
                        map.put("body", posting.getBodyHtml(imagecontext));
                        map.put("edittime", new Integer(posting.getEditTime()));
                        Poster poster = forum.getPosterNick(posting.getPoster());
                        if (poster != null) {
                            map.put("poster", posting.getPoster());
                            addPosterInfo(map, poster);
                        } else {
                            map.put("poster", posting.getPoster());
                            map.put("guest", "true");
                        }
                        map.put("posttime", new Integer(posting.getPostTime()));
                        map.put("id", new Integer(posting.getId()));
                        map.put("threadpos", new Integer(posting.getThreadPos()));
                        
                        // very weird way need to figure this out
                        if (posting.getThreadPos() % 2 == 0) {
                            map.put("tdvar", "threadpagelisteven");
                        } else {
                            map.put("tdvar", "threadpagelistodd");
                        }
                        
                        // should be moved out of the loop
                        if (activeid != -1) {
                            poster = forum.getPoster(activeid);
                            poster.signalSeen();
                            poster.seenThread(thread);
                            addActiveInfo(map, poster);
                            if (poster != null && poster.getNick().equals(poster.getNick())) {
                                map.put("isowner", "true");
                            } else {
                                map.put("isowner", "false");
                            }
                            if (poster != null && postArea.isModerator(poster.getNick())) {
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
        // long end = System.currentTimeMillis();
        // log.info("searchPostings "+(end-start)+"ms");

        return list;
    }

    /**
     * Get a specific posting, for use in remove post where the posting to be deleted is displayed.
     * 
     * @param forumid MMBase node number of the forum
     * @param postareaid MMBase node number of the postarea
     * @param postthreadid MMBase node number of the postthread
     * @param postingid MMBase node number of the posting
     * @param activeid MMBase node number of current Poster (on the page)
     * @param imagecontext The context where to find the images (eg smilies)
     * @return List of (map) representing the postings within the given postthread
     */
    public HashMap getPosting(String forumid, String postareaid, String postthreadid, String postingid, int activeid, String imagecontext) {
        List list = new ArrayList();
        long start = System.currentTimeMillis();

        HashMap map = new HashMap();
        Forum forum = ForumManager.getForum(forumid);
        if (forum != null) {
            PostArea postArea = forum.getPostArea(postareaid);
            if (postArea != null) {
                PostThread thread = postArea.getPostThread(postthreadid);
                if (thread != null) {
                    Posting posting = thread.getPosting(Integer.parseInt(postingid));
                    if (posting != null) {
                        String subject = posting.getSubject();
                        map.put("subject", subject);
                        if (subject.length() > 60){
                            subject = subject.substring(0, 57) + "...";
                        }
                        map.put("shortsubject", subject);
                        map.put("body", posting.getBodyHtml(imagecontext));
                        map.put("edittime", new Integer(posting.getEditTime()));
                        Poster poster = forum.getPosterNick(posting.getPoster());
                        if (poster != null) {
                            map.put("poster", posting.getPoster());
                            addPosterInfo(map, poster);
                        } else {
                            map.put("poster", posting.getPoster());
                            map.put("guest", "true");
                        }
                        map.put("posttime", new Integer(posting.getPostTime()));
                        map.put("postcount", new Integer(thread.getPostCount()));
                        map.put("id", new Integer(posting.getId()));
                        map.put("threadpos", new Integer(posting.getThreadPos()));
                        // very weird way need to figure this out
                        if (posting.getThreadPos() % 2 == 0) {
                            map.put("tdvar", "threadpagelisteven");
                        } else {
                            map.put("tdvar", "threadpagelistodd");
                        }
                        // should be moved out of the loop
                        if (activeid != -1) {
                            poster = forum.getPoster(activeid);
                            poster.signalSeen();
                            poster.seenThread(thread);
                            addActiveInfo(map, poster);
                            if (poster != null && poster.getNick().equals(poster.getNick())) {
                                map.put("isowner", "true");
                            } else {
                                map.put("isowner", "false");
                            }
                            if (poster != null && postArea.isModerator(poster.getNick())) {
                                map.put("ismoderator", "true");
                            } else {
                                map.put("ismoderator", "false");
                            }
                            if (poster != null && poster.getNick().equals(poster.getNick()) || poster != null && postArea.isModerator(poster.getNick())) {
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
        // log.info("getPosting "+(end-start)+"ms");

        return map;
    }

    public String getPostingPageNumber(String forumid, String postareaid, String postthreadid, String postingid, int pagesize) {
        Forum forum = ForumManager.getForum(forumid);
        if (forum != null) {
            PostArea a = forum.getPostArea(postareaid);
            if (a != null) {
                PostThread postThead = a.getPostThread(postthreadid);
                if (postThead != null) {
                    Posting posting = postThead.getPosting(Integer.parseInt(postingid));
                    int pagenumber = (posting.getThreadPos() / pagesize) + 1;
                    return "" + pagenumber;
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
     * @return List of (map) representing the moderators of this forum / postarea. contains id, account, firstname,
     *         lastname of the moderator
     */
    public List getModerators(String forumid, String postareaid) {
        List list = new ArrayList();

        Forum forum = ForumManager.getForum(forumid);
        if (forum != null) {
            PostArea postArea = forum.getPostArea(postareaid);
            if (postArea != null) {
                Enumeration e = postArea.getModerators();
                while (e.hasMoreElements()) {
                    Poster poster = (Poster) e.nextElement();
                    HashMap map = new HashMap();
                    map.put("id", new Integer(poster.getId()));
                    map.put("account", poster.getAccount());
                    map.put("nick", poster.getNick());
                    map.put("firstname", poster.getFirstName());
                    map.put("lastname", poster.getLastName());
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
     * @return List of (map) representing the administrators of this forum contains id, account, firstname, lastname of
     *         the administrator
     */
    public List getAdministrators(String forumid) {
        List list = new ArrayList();

        Forum forum = ForumManager.getForum(forumid);
        if (forum != null) {
            Enumeration e = forum.getAdministrators();
            while (e.hasMoreElements()) {
                Poster poster = (Poster) e.nextElement();
                HashMap map = new HashMap();
                map.put("id", new Integer(poster.getId()));
                map.put("account", poster.getAccount());
                map.put("nick", poster.getNick());
                map.put("firstname", poster.getFirstName());
                map.put("lastname", poster.getLastName());
                list.add(map);
            }
        }
        return list;
    }

    /**
     * Get the posters that are now online in this forum
     * @param forumid MMBase node number of the forum
     * @return List of (map) representing the online posters for the given forum
     */
    public List getPostersOnline(String forumid) {
        List list = new ArrayList();

        Forum forum = ForumManager.getForum(forumid);
        if (forum != null) {
            Enumeration e = forum.getPostersOnline();
            while (e.hasMoreElements()) {
                Poster poster = (Poster) e.nextElement();
                HashMap map = new HashMap();
                map.put("id", new Integer(poster.getId()));
                map.put("account", poster.getAccount());
                map.put("nick", poster.getNick());
                map.put("firstname", poster.getFirstName());
                map.put("lastname", poster.getLastName());
                map.put("location", poster.getLocation());
                map.put("level", poster.getLevel());
                map.put("levelgui", poster.getLevelGui());
                map.put("levelimage", poster.getLevelImage());
                map.put("lastseen", new Integer(poster.getLastSeen()));
                map.put("blocked", "" + poster.isBlocked());
                list.add(map);
            }
        }
        return list;
    }

    public List getPosters(String forumid, String searchkey, int page, int pagesize) {
        searchkey = searchkey.toLowerCase();
        List list = new ArrayList();

        Forum forum = ForumManager.getForum(forumid);
        if (forum != null) {
            int startpos = page * pagesize;
            int i = 1;
            int j = 1;
            Enumeration e = forum.getPosters();
            while (e.hasMoreElements()) {
                Poster poster = (Poster) e.nextElement();
                String nick = poster.getNick().toLowerCase();
                String firstname = poster.getFirstName().toLowerCase();
                String lastname = poster.getLastName().toLowerCase();
                String location = poster.getLocation().toLowerCase();
                if (searchkey.equals("*") || nick.indexOf(searchkey) != -1 || firstname.indexOf(searchkey) != -1
                        || lastname.indexOf(searchkey) != -1 || location.indexOf(searchkey) != -1) {
                    if (i > startpos) {
                        HashMap map = new HashMap();
                        map.put("number", new Integer(poster.getId()));
                        map.put("account", poster.getAccount());
                        map.put("nick", poster.getNick());
                        map.put("firstname", poster.getFirstName());
                        map.put("lastname", poster.getLastName());
                        map.put("location", poster.getLocation());
                        map.put("level", poster.getLevel());
                        map.put("levelgui", poster.getLevelGui());
                        map.put("levelimage", poster.getLevelImage());
                        map.put("blocked", "" + poster.isBlocked());
                        map.put("lastseen", new Integer(poster.getLastSeen()));
                        if (page != 0) {
                            map.put("prevpage", new Integer(page - 1));
                        } else {
                            map.put("prevpage", new Integer(-1));
                        }
                        map.put("nextpage", new Integer(-1));
                        list.add(map);
                        j++;
                        if (j > pagesize) {
                            map.put("nextpage", new Integer(page + 1));
                            break;
                        }
                    }
                    i++;
                }
            }
        }
        return list;
    }

    public List searchPostings(String forumid, String searchareaid, String searchpostthreadid, String searchkey, int posterid, int page,
            int pagesize) {
        log.info("SEARCH CALLED = " + posterid);
        long start = System.currentTimeMillis();
        searchkey = searchkey.toLowerCase();
        List list = new ArrayList();

        Forum forum = ForumManager.getForum(forumid);
        if (forum != null) {
            int startpos = page * pagesize;
            int i = 1;
            int j = 1;
            Enumeration e = null;
            if (!searchareaid.equals("-1")) {
                if (!searchpostthreadid.equals("-1")) {
                    PostArea postArea = forum.getPostArea(searchareaid);
                    if (postArea != null) {
                        PostThread postThread = postArea.getPostThread(searchpostthreadid);
                        e = postThread.searchPostings(searchkey, posterid).elements();
                    }
                } else {
                    PostArea postArea = forum.getPostArea(searchareaid);
                    if (postArea != null) e = postArea.searchPostings(searchkey, posterid).elements();
                }
            } else {
                e = forum.searchPostings(searchkey, posterid).elements();
            }
            if (e != null) {
                while (e.hasMoreElements() && j < 25) {
                    Posting posting = (Posting) e.nextElement();
                    HashMap map = new HashMap();
                    map.put("postingid", new Integer(posting.getId()));
                    PostThread postThread = posting.getParent();
                    PostArea postArea = postThread.getParent();
                    map.put("postareaid", new Integer(postArea.getId()));
                    map.put("postareaname", postArea.getName());
                    map.put("postthreadid", new Integer(postThread.getId()));
                    String subject = posting.getSubject();
                    map.put("subject", subject);
                    if (subject.length() > 60) subject = subject.substring(0, 57) + "...";
                    map.put("shortsubject", subject);
                    map.put("poster", posting.getPoster());
                    map.put("posterid", forum.getPoster(posting.getPoster()));
                    list.add(map);
                    j++;
                }
            }
        }
        long end = System.currentTimeMillis();
        log.info("searchPostings " + (end - start) + "ms");
        return list;
    }

    /**
     * List all the posters not allready a moderator (so possible moderators) for this postarea
     * 
     * @param forumid MMBase node number of the forum
     * @param postareaid MMBase node number of the new postarea
     * @return List of (map) representing all posters of the given postarea which are no moderators
     */
    public List getNonModerators(String forumid, String postareaid, String searchkey) {
        List list = new ArrayList();

        Forum forum = ForumManager.getForum(forumid);
        if (forum != null) {
            PostArea postArea = forum.getPostArea(postareaid);
            if (postArea != null) {
                Enumeration e = postArea.getNonModerators(searchkey);
                while (e.hasMoreElements()) {
                    Poster poster = (Poster) e.nextElement();
                    HashMap map = new HashMap();
                    map.put("id", new Integer(poster.getId()));
                    map.put("account", poster.getAccount());
                    map.put("nick", poster.getNick());
                    map.put("firstname", poster.getFirstName());
                    map.put("lastname", poster.getLastName());
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
    public List getNonAdministrators(String forumid, String searchkey) {
        List list = new ArrayList();

        Forum forum = ForumManager.getForum(forumid);
        if (forum != null) {
            Enumeration e = forum.getNonAdministrators(searchkey);
            while (e.hasMoreElements()) {
                Poster poster = (Poster) e.nextElement();
                HashMap map = new HashMap();
                map.put("id", new Integer(poster.getId()));
                map.put("account", poster.getAccount());
                map.put("nick", poster.getNick());
                map.put("firstname", poster.getFirstName());
                map.put("lastname", poster.getLastName());
                list.add(map);
            }
        }
        return list;
    }

    /**
     * Provide general info and statistics on a forum Remark: atm it also returns configuration settings, this will
     * change in the near future see getForumConfiguration for more info.
     * 
     * @param id MMBase node number of the forum
     * @param sactiveid Id for the current (on the page) poster for admin/onwership checks
     * @return (map) representing info for the given forum
     * 
     */
    public Map getForumInfo(String id, String sactiveid) {
        Map map = new HashMap();
        try {
            int activeid = Integer.parseInt(sactiveid);

            Forum forum = ForumManager.getForum(id);
            if (forum != null) {
                map.put("name", forum.getName());
                map.put("language", forum.getLanguage());
                map.put("accountcreationtype", forum.getAccountCreationType());
                map.put("accountremovaltype", forum.getAccountRemovalType());
                map.put("loginsystemtype", forum.getLoginSystemType());
                map.put("loginmodetype", forum.getLoginModeType());
                map.put("logoutmodetype", forum.getLogoutModeType());
                map.put("navigationmethod", forum.getNavigationMethod());
                map.put("privatemessagesenabled", forum.getPrivateMessagesEnabled());
                map.put("description", forum.getDescription());
                map.put("postareacount", new Integer(forum.getPostAreaCount()));
                map.put("postthreadcount", new Integer(forum.getPostThreadCount()));
                map.put("postcount", new Integer(forum.getPostCount()));
                map.put("postersonline", new Integer(forum.getPostersOnlineCount()));
                map.put("posterstotal", new Integer(forum.getPostersTotalCount()));
                map.put("postersnew", new Integer(forum.getPostersNewCount()));
                map.put("viewcount", new Integer(forum.getViewCount()));
                map.put("lastposter", forum.getLastPoster());
                map.put("lastposttime", new Integer(forum.getLastPostTime()));
                map.put("lastsubject", forum.getLastSubject());
                map.put("hasnick", new Boolean(forum.hasNick()));
                if (activeid != -1) {
                    Poster poster = forum.getPoster(activeid);
                    poster.signalSeen();
                    addActiveInfo(map, poster);
                    if (poster != null && forum.isAdministrator(poster.getNick())) {
                        map.put("isadministrator", "true");
                    } else {
                        map.put("isadministrator", "false");
                    }
                }
            }
        } catch (Exception e) {
            log.error(e);
        }

        return map;
    }

    public String getForumAlias(String key) {
        if (!key.equals("")) {
            Forum forum = ForumManager.getForumByAlias(key);
            if (forum != null) return "" + forum.getId();
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
            Forum forum = ForumManager.getForum(id);
            if (forum != null) {
                return forum.getHeaderPath();
            }
        } catch (Exception e) {
            log.error(e);
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
            Forum forum = ForumManager.getForum(id);
            if (forum != null) {
                return forum.getFooterPath();
            }
        } catch (Exception e) {
            log.error(e);
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
            Forum forum = ForumManager.getForum(id);
            if (forum != null) {
                return forum.getFromEmailAddress();
            }
        } catch (Exception e) {
            log.error(e);
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
        HashMap map = new HashMap();
        try {
            int activeid = Integer.parseInt(sactiveid);

            Forum forum = ForumManager.getForum(id);
            if (forum != null) {
                map.put("language", forum.getLanguage());
                map.put("accountcreationtype", forum.getAccountCreationType());
                map.put("accountremovaltype", forum.getAccountRemovalType());
                map.put("loginsystemtype", forum.getLoginSystemType());
                map.put("loginmodetype", forum.getLoginModeType());
                map.put("logoutmodetype", forum.getLogoutModeType());
                map.put("guestreadmodetype", forum.getGuestReadModeType());
                map.put("guestwritemodetype", forum.getGuestWriteModeType());
                map.put("avatarsdisabled", forum.getAvatarsDisabled());
                map.put("avatarsuploadenabled", forum.getAvatarsUploadEnabled());
                map.put("avatarsgalleryenabled", forum.getAvatarsGalleryEnabled());
                map.put("contactinfoenabled", forum.getContactInfoEnabled());
                map.put("smileysenabled", forum.getSmileysEnabled());
                map.put("privatemessagesenabled", forum.getPrivateMessagesEnabled());
                map.put("postingsperpage", new Integer(forum.getPostingsPerPage()));
                map.put("fromaddress", forum.getFromEmailAddress());
                map.put("headerpath", forum.getHeaderPath());
                map.put("footerpath", forum.getFooterPath());
                map.put("replyoneachpage", new Boolean(forum.getReplyOnEachPage()));
                map.put("navigationmethod", forum.getNavigationMethod());
                map.put("alias", forum.getAlias());

                if (activeid != -1) {
                    Poster poster = forum.getPoster(activeid);
                    poster.signalSeen();
                    addActiveInfo(map, poster);
                    if (poster != null && forum.isAdministrator(poster.getNick())) {
                        map.put("isadministrator", "true");
                    } else {
                        map.put("isadministrator", "false");
                    }
                }
            }
        } catch (Exception e) {
            log.error(e);
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
    public Map getPostAreaConfig(String id, String sactiveid, String postareaid) {
        HashMap map = new HashMap();
        try {
            int activeid = Integer.parseInt(sactiveid);

            Forum forum = ForumManager.getForum(id);
            if (forum != null) {
                PostArea postArea = forum.getPostArea(postareaid);
                if (postArea != null) {
                    map.put("guestreadmodetype", postArea.getGuestReadModeType());
                    map.put("guestwritemodetype", postArea.getGuestWriteModeType());
                    map.put("position", new Integer(postArea.getPos()));
                    map.put("threadstartlevel", postArea.getThreadStartLevel());
                }
            }
        } catch (Exception e) {
            log.error(e);
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
        map.put("postingsperpage", new Integer(ForumManager.getPostingsPerPage()));
        map.put("fromaddress", ForumManager.getFromEmailAddress());
        map.put("headerpath", ForumManager.getHeaderPath());
        map.put("footerpath", ForumManager.getFooterPath());
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
        HashMap map = new HashMap();
        Forum forum = ForumManager.getForum(id);
        if (forum != null) {
            if (posterid != null) {
                Poster poster = forum.getPoster(posterid);
                if (poster == null) {
                    try {
                        int tmpi = Integer.parseInt(posterid);
                        poster = forum.getPoster(tmpi);
                    } catch (Exception e) {
                        log.error(e);
                    }
                }
                addPosterInfo(map, poster);
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
    public Map getQuotaInfo(String id, int posterid, int barsize) {
        HashMap map = new HashMap();
        Forum forum = ForumManager.getForum(id);
        if (forum != null) {
            if (posterid != -1) {
                Poster poster = forum.getPoster(posterid);
                map.put("quotareached", new Boolean(poster.isQuotaReached()));
                int t = poster.getQuotaNumber();
                int u = poster.getQuotaUsedNumber();
                float d = 100 / (float) t;
                float b = (float) barsize / t;
                int up = (int) (d * u);
                int ub = (int) (b * u);

                // log.info("u="+u+" d="+d+" up="+up+" b="+b+" ub="+ub);

                map.put("quotausedpercentage", new Integer(up));
                map.put("quotaunusedpercentage", new Integer(100 - up));
                map.put("quotanumber", new Integer(t));
                map.put("quotausednumber", new Integer(u));
                map.put("quotaunusednumber", new Integer(t - u));
                map.put("quotausedbar", new Integer(ub));

                if (u > ForumManager.getQuotaSoftWarning()) {
                    if (u > ForumManager.getQuotaWarning()) {
                        map.put("quotawarning", "red");
                    } else {
                        map.put("quotawarning", "orange");
                    }
                } else {
                    map.put("quotawarning", "green");
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
    public Map getMailboxInfo(String id, int posterid, String mailboxid) {
        HashMap map = new HashMap();
        Forum forum = ForumManager.getForum(id);
        if (forum != null) {
            if (posterid != -1) {
                Poster poster = forum.getPoster(posterid);
                if (poster != null) {
                    Mailbox mb = poster.getMailbox(mailboxid);
                    if (mb != null) {
                        map.put("messagecount", new Integer(mb.getMessageCount()));
                        map.put("messageunreadcount", new Integer(mb.getMessageUnreadCount()));
                        map.put("messagenewcount", new Integer(mb.getMessageNewCount()));
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
    public boolean signalMailboxChange(String id, int posterid, String mailboxid) {
        HashMap map = new HashMap();
        Forum forum = ForumManager.getForum(id);
        if (forum != null) {
            if (posterid != -1) {
                Poster poster = forum.getPoster(posterid);
                if (poster != null) {
                    Mailbox mb = poster.getMailbox(mailboxid);
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
     * @param gender New gender of the poster
     * @param location ew location of the poster
     * @return Feedback regarding the success of edit action
     */
    public String editPoster(String forumid, int posterid, String firstname, String lastname, String email, String gender, String location,
            String newpassword, String newconfirmpassword) {
        if (newpassword.equals("")) {
            log.info("newpassword is empty");
            Forum forum = ForumManager.getForum(forumid);
            if (forum != null) {
                Poster poster = forum.getPoster(posterid);
                if (poster != null) {
                    poster.setFirstName(firstname);
                    poster.setLastName(lastname);
                    poster.setEmail(email);
                    poster.setGender(gender);
                    poster.setLocation(location);
                    poster.savePoster();
                } else {
                    return "false";
                }
            }
            return "true";
        } else {
            if (newpassword.equals(newconfirmpassword)) {
                log.info("newpassword equals newconfirmpassword");
                Forum forum = ForumManager.getForum(forumid);
                if (forum != null) {
                    Poster poster = forum.getPoster(posterid);
                    if (poster != null) {
                        poster.setFirstName(firstname);
                        poster.setLastName(lastname);
                        poster.setEmail(email);
                        poster.setGender(gender);
                        poster.setLocation(location);
                        poster.setPassword(newpassword);
                        poster.savePoster();
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
     * @param gender New gender of the poster
     * @param location ew location of the poster
     * @return Feedback regarding the success of edit action
     */
    public String editProfilePoster(String forumid, int posterid, int profileid, String firstname, String lastname, String email,
            String gender, String location, String newpassword, String newconfirmpassword) {
        if (newpassword.equals("")) {
            log.info("newpassword is empty");
            Forum forum = ForumManager.getForum(forumid);
            if (forum != null) {
                Poster poster = forum.getPoster(profileid);
                if (poster != null) {
                    poster.setFirstName(firstname);
                    poster.setLastName(lastname);
                    poster.setEmail(email);
                    poster.setGender(gender);
                    poster.setLocation(location);
                    poster.savePoster();
                } else {
                    return "false";
                }
            }
            return "true";
        } else {
            if (newpassword.equals(newconfirmpassword)) {
                log.info("newpassword equals newconfirmpassword");
                Forum forum = ForumManager.getForum(forumid);
                if (forum != null) {
                    Poster poster = forum.getPoster(profileid);
                    if (poster != null) {
                        poster.setFirstName(firstname);
                        poster.setLastName(lastname);
                        poster.setEmail(email);
                        poster.setGender(gender);
                        poster.setLocation(location);
                        poster.setPassword(newpassword);
                        poster.savePoster();
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
    public String createPoster(String forumid, String account, String password, String confirmpassword, String firstname, String lastname,
            String email, String gender, String location) {
        if (password.equals(confirmpassword)) {
            Forum forum = ForumManager.getForum(forumid);
            if (forum != null) {
                Poster poster = forum.getPoster(account);
                if (poster == null) {
                    if (firstname.equals("") || firstname.length() < 2) return "firstnameerror";
                    if (lastname.equals("") || lastname.length() < 1) return "lastnameerror";
                    if (email.equals("") || email.indexOf("@") == -1 || email.indexOf(".") == -1) return "emailerror";
                    poster = forum.createPoster(account, password);
                    if (poster != null) {
                        poster.setFirstName(firstname);
                        poster.setLastName(lastname);
                        poster.setEmail(email);
                        poster.setGender(gender);
                        poster.setLocation(location);
                        poster.setPassword(password);
                        poster.setPostCount(0);
                        poster.savePoster();
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

    public String createPosterNick(String forumid, String account, String password, String confirmpassword, String nick, String firstname,
            String lastname, String email, String gender, String location) {
        if (password.equals(confirmpassword)) {
            Forum forum = ForumManager.getForum(forumid);
            if (forum != null) {
                Poster poster = forum.getPoster(account);
                Poster n = forum.getPosterNick(nick);
                if (poster == null) {
                    // weird hack since entree demands the use of a nick
                    if (forum.getLoginSystemType().equals("entree")) {
                        if (n != null || nick.equals("")) return "nickinuse";
                    } else {
                        if (n != null && !nick.equals("")) return "nickinuse";
                    }
                    if (firstname.equals("") || firstname.length() < 2) return "firstnameerror";
                    if (lastname.equals("") || lastname.length() < 1) return "lastnameerror";
                    if (email.equals("") || email.indexOf("@") == -1 || email.indexOf(".") == -1) return "emailerror";
                    poster = forum.createPoster(account, password);
                    if (poster != null) {
                        poster.setFirstName(firstname);
                        poster.setLastName(lastname);
                        poster.setEmail(email);
                        poster.setGender(gender);
                        poster.setLocation(location);
                        poster.setPassword(password);
                        poster.setPostCount(0);
                        poster.savePoster();
                        if (nick != null && !nick.equals("")) setProfileValue(forumid, poster.getId(), "nick", nick);
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
    public String createPosterProxy(String forumid, String account, String password, String confirmpassword, String firstname,
            String lastname, String email, String gender, String location, String proxypassword) {
        if (password.equals(confirmpassword)) {
            Forum forum = ForumManager.getForum(forumid);
            if (forum != null) {
                Poster poster = forum.getPoster(account);
                if (poster == null) {
                    poster = forum.createPoster(account, password);
                    if (poster != null) {
                        poster.setFirstName(firstname);
                        poster.setLastName(lastname);
                        poster.setEmail(email);
                        poster.setGender(gender);
                        poster.setLocation(location);
                        poster.setPostCount(0);
                        poster.savePoster();
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
     * @param pagesize Number of postings per page
     * @param baseurl Base url for links in the navigation html
     * @param cssclass stylesheet name for the url links
     * @return (map) representing info for the given postarea
     */
    public Map getPostAreaInfo(String forumid, String postareaid, int activeid, int page, int pagesize, String baseurl, String cssclass) {
        Map map = new HashMap();

        Forum forum = ForumManager.getForum(forumid);
        if (forum != null) {
            PostArea postArea = forum.getPostArea(postareaid);
            map.put("name", postArea.getName());
            map.put("postthreadcount", new Integer(postArea.getPostThreadCount()));
            map.put("postcount", new Integer(postArea.getPostCount()));
            map.put("viewcount", new Integer(postArea.getViewCount()));
            map.put("lastposter", postArea.getLastPoster());
            map.put("lastposttime", new Integer(postArea.getLastPostTime()));
            map.put("lastsubject", postArea.getLastSubject());
            map.put("guestreadmodetype", postArea.getGuestReadModeType());
            map.put("guestwritemodetype", postArea.getGuestWriteModeType());
            map.put("threadstartlevel", postArea.getThreadStartLevel());
            map.put("privatemessagesenabled", forum.getPrivateMessagesEnabled());
            map.put("smileysenabled", forum.getSmileysEnabled());
            map.put("navline", postArea.getNavigationLine(baseurl, page, pagesize, cssclass));
            map.put("pagecount", new Integer(postArea.getPageCount(pagesize)));
            if (activeid != -1) {
                Poster activePoster = forum.getPoster(activeid);
                if (activePoster == null) {
                    throw new RuntimeException("No poster object found for id '" + activeid + "'");
                }
                activePoster.signalSeen();
                if (activePoster != null && forum.isAdministrator(activePoster.getNick())) {
                    map.put("isadministrator", "true");
                } else {
                    map.put("isadministrator", "false");
                }
                if (activePoster != null && postArea.isModerator(activePoster.getNick())) {
                    map.put("ismoderator", "true");
                } else {
                    map.put("ismoderator", "false");
                }
            }
        }
        return map;
    }

    public Map getPostThreadInfo(String forumid, String postareaid, String postthreadid, int pagesize) {
        Map map = new HashMap();

        Forum forum = ForumManager.getForum(forumid);
        if (forum != null) {
            PostArea a = forum.getPostArea(postareaid);
            if (a != null) {
                PostThread postThead = a.getPostThread(postthreadid);
                if (postThead != null) {
                    map.put("threadstate", postThead.getState());
                    map.put("threadmood", postThead.getMood());
                    map.put("threadtype", postThead.getType());
                    map.put("pagecount", new Integer(postThead.getPageCount(pagesize)));
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
        Forum forum = ForumManager.getForum(forumid);
        if (forum != null) {
            boolean result = forum.removePostArea(postareaid);
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
        Forum forum = ForumManager.getForum(forumid);
        if (forum != null) {
            Poster poster = forum.getPoster(posterid);
            if (poster != null) {
                return poster.profileUpdated();
            }
        }
        return false;
    }

    /**
     * Removes a whole thread (including postings) from a postarea
     * 
     * @param forumid MMBase node number of the forum
     * @param postareaid MMbase node number of the postarea
     * @param postthreadid MMBase node number of the postthread
     * @return feedback regarding this remove action
     */
    public boolean removePostThread(String forumid, String postareaid, String postthreadid) {
        Forum forum = ForumManager.getForum(forumid);
        if (forum != null) {
            PostArea a = forum.getPostArea(postareaid);
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
     * @param page Current page number
     * @param pagesize Number of postings per page
     * @param baseurl Base url for links in the navigation html
     * @param cssclass stylesheet name for the url links
     * @return (map) containing navline, lastpage, pagecount
     */
    public Map getPostThreadNavigation(String forumid, String postareaid, String postthreadid, int posterid, int page, int pagesize,
            String baseurl, String cssclass) {
        Map map = new HashMap();

        if (cssclass == null) cssclass = "";

        Forum forum = ForumManager.getForum(forumid);
        if (forum != null) {
            PostArea a = forum.getPostArea(postareaid);
            if (a != null) {
                PostThread postThead = a.getPostThread(postthreadid);
                if (postThead != null) {
                    int overflowpage = forum.getPostingsOverflowThreadPage();
                    map.put("navline", postThead.getNavigationLine(baseurl, page, pagesize, overflowpage, cssclass));
                    map.put("lastpage", "" + postThead.isLastPage(page, pagesize));
                    map.put("pagecount", new Integer(postThead.getPageCount(pagesize)));
                    Poster activePoster = forum.getPoster(posterid);
                    if (activePoster != null) {
                        ThreadObserver to = forum.getThreadObserver(postThead.getId());
                        if (to != null && to.wantsEmailOnChange(activePoster)) {
                            map.put("emailonchange", "true");
                        } else {
                            map.put("emailonchange", "false");
                        }
                        if (to != null && to.isBookmarked(activePoster)) {
                            map.put("bookmarked", "true");
                        } else {
                            map.put("bookmarked", "false");
                        }
                    } else {
                        map.put("emailonchange", "false");
                        map.put("bookmarked", "false");
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
     * @param postthreadid MMBase node number of the postthread
     * @param subject Subject of the reply (normally same as the postthread sibject)
     * @param posterId Posterid of the reply
     * @param body Body of the reply
     * @return Feedback regarding this post action
     */
    public Map postReply(String forumid, String postareaid, String postthreadid, String subject, String posterId, String body) {
        
        log.debug("*** post reply");
        HashMap map = new HashMap();

        if (subject.length() > 60) subject = subject.substring(0, 57) + "...";

        Forum forum = ForumManager.getForum(forumid);
        int pos = posterId.indexOf("(");
        if (pos != -1) {
            posterId = posterId.substring(0, pos - 1);
        }

        if (forum != null) {
            PostArea postArea = forum.getPostArea(postareaid);
            if (postArea != null) {
                PostThread postThead = postArea.getPostThread(postthreadid);
                if (postThead != null) {
                    // nobody may post in closed thread, unless you're a moderator
                    Poster poster = forum.getPosterNick(posterId);
                    if ((!postThead.getState().equals("closed") || !postThead.getState().equals("pinnedclosed") || postArea.isModerator(posterId))
                            && (poster == null || !poster.isBlocked())) {
                        if (body.equals("")) {
                            map.put("error", "no_body");
                        } else if (poster != null && poster.checkDuplicatePost("", body)) {
                            map.put("error", "duplicate_post");
                        } else if (checkIllegalHtml(body)) {
                            map.put("error", "illegal_html");
                        } else if (poster != null && checkSpeedPosting(postArea, poster)) {
                            map.put("error", "speed_posting");
                            map.put("speedposttime", "" + postArea.getSpeedPostTime());
                        } else {
                            body = postArea.filterContent(body);
                            subject = filterHTML(subject);
                            // temp fix for [ ] quotes.
                            body = BBCode.encode(body);
                            try {
                                postThead.postReply(subject, poster, body, false);
                                map.put("error", "none");
                                if (poster != null) {
                                    poster.setLastBody(body);
                                    poster.setLastPostTime((int) (System.currentTimeMillis() / 1000));
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
     * @param forumid MMBase node number of the forum
     * @param postareaid MMBase node number of the postarea
     * @param subject Subject of the new post
     * @param posterId Posterid to be attached to the postthread as its creator
     * @param body Body of the new post
     * @return (map) containing the postthreadid of the newly created post
     */
    public Map newPost(String forumid, String postareaid, String subject, String posterId, String body, String mood) {

        HashMap map = new HashMap();

        if (subject.length() > 60) subject = subject.substring(0, 57) + "...";

        Forum forum = ForumManager.getForum(forumid);
        if (forum != null) {
            PostArea a = forum.getPostArea(postareaid);
            Poster poster = forum.getPoster(posterId);
            if (a != null && (poster == null || !poster.isBlocked())) {
                if (subject.equals("")) {
                    map.put("error", "no_subject");
                } else if (body.equals("")) {
                    map.put("error", "no_body");
                } else if (checkIllegalHtml(subject)) {
                    map.put("error", "illegal_html");
                } else if (checkIllegalHtml(body)) {
                    map.put("error", "illegal_html");
                } else if (poster != null && poster.checkDuplicatePost(subject, body)) {
                    map.put("error", "duplicate_post");
                } else if (checkMaxPostSize(subject, body)) {
                    map.put("error", "maxpostsize");
                } else if (poster != null && checkSpeedPosting(a, poster)) {
                    map.put("error", "speed_posting");
                    map.put("speedposttime", "" + a.getSpeedPostTime());
                } else {
                    body = a.filterContent(body);
                    subject = filterHTML(subject);
                    int postthreadid = a.newPost(subject, poster, body, mood, false);
                    map.put("postthreadid", new Integer(postthreadid));
                    map.put("error", "none");
                    if (poster != null) {
                        poster.setLastSubject(subject);
                        poster.setLastBody(body);
                        poster.setLastPostTime((int) (System.currentTimeMillis() / 1000));
                    }
                }
            }
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

        HashMap map = new HashMap();
        Forum forum = ForumManager.getForum(forumid);
        if (forum != null && !forum.getPoster(poster).isBlocked()) {
            if (to.indexOf(",") == -1) {
                int privatemessageid = forum.newPrivateMessage(poster, to, subject, body);
                map.put("privatemessageid", new Integer(privatemessageid));
            } else {
                StringTokenizer tok = new StringTokenizer(to, ",\n\r");
                while (tok.hasMoreTokens()) {
                    String pto = tok.nextToken();
                    forum.newPrivateMessage(poster, pto, subject, body);
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
        Forum forum = ForumManager.getForum(forumid);
        if (forum != null) {
            int folderid = forum.newFolder(activeid, newfolder);
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
        Forum forum = ForumManager.getForum(forumid);
        if (forum != null) {
            return forum.removeFolder(activeid, foldername);
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
            Forum forum = ForumManager.getForum(forumid);
            if (forum != null) {
                PostArea postArea = forum.getPostArea(postareaid);
                if (postArea != null) {
                    Poster activePoster = forum.getPoster(activeid);
                    Poster moderatorPoster = forum.getPoster(moderatorid);
                    if (activePoster != null && forum.isAdministrator(activePoster.getNick())) {
                        postArea.addModerator(moderatorPoster);
                    }
                }
            }
        } catch (Exception e) {}
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
            Forum forum = ForumManager.getForum(forumid);
            if (forum != null) {
                Poster activePoster = forum.getPoster(activeid);
                Poster moderatorPoster = forum.getPoster(moderatorid);
                if (activePoster != null && forum.isAdministrator(activePoster.getNick())) {
                    forum.addAdministrator(moderatorPoster);
                }
            }
        } catch (Exception e) {}
        return true;
    }

    /**
     * Remove a moderator from a postarea (poster is not removed just status moderator is revoked)
     * 
     * @param forumid MMBase node number of the forum
     * @param postareaid MMBase node number of the postarea
     * @param activeid MMBase node number of current Poster (on the page)
     * @param moderatorid MMBase node number of moderator you want to remove
     * @return Feedback regarding the success of this action
     */
    public boolean removeModerator(String forumid, String postareaid, int activeid, int moderatorid) {
        Forum forum = ForumManager.getForum(forumid);
        if (forum != null) {
            PostArea postArea = forum.getPostArea(postareaid);
            if (postArea != null) {
                Poster activePoster = forum.getPoster(activeid);
                Poster moderatorPoster = forum.getPoster(moderatorid);
                if (activePoster != null && forum.isAdministrator(activePoster.getNick())) {
                    postArea.removeModerator(moderatorPoster);
                }
            }
        }
        return true;
    }

    /**
     * Remove a moderator from a postarea (poster is not removed just status moderator is revoked)
     * 
     * @param forumid MMBase node number of the forum
     * @param activeid MMBase node number of current Poster (on the page)
     * @param administratorid MMBase node number of moderator you want to remove
     * @return Feedback regarding the success of this action
     */
    public boolean removeAdministrator(String forumid, int activeid, int administratorid) {
        Forum forum = ForumManager.getForum(forumid);
        if (forum != null) {
            Poster activePoster = forum.getPoster(activeid);
            Poster moderatorPoster = forum.getPoster(administratorid);
            if (activePoster != moderatorPoster && forum.isAdministrator(activePoster.getNick())) {
                forum.removeAdministrator(moderatorPoster);
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
    public boolean editPost(String forumid, String postareaid, String postthreadid, int postingid, int activeid, String subject,
            String body, String imagecontext) {
        Forum forum = ForumManager.getForum(forumid);
        if (forum != null) {
            PostArea postArea = forum.getPostArea(postareaid);
            if (postArea != null) {
                PostThread postThread = postArea.getPostThread(postthreadid);
                if (postThread != null) {
                    Posting posting = postThread.getPosting(postingid);

                    // am i allowed to edit ?
                    Poster activePoster = forum.getPoster(activeid);
                    if (activePoster.getNick().equals(posting.getPoster()) || postArea.isModerator(activePoster.getNick())) {
                        posting.setSubject(subject);
                        posting.setBody(body, imagecontext, false);
                        posting.setEditTime((int) (System.currentTimeMillis() / 1000));
                        posting.save();

                        // if its the first posting we should also change lastsubjects
                        log.info("EDITPOS=" + posting.getThreadPos());
                        if (posting.getThreadPos() == 0) {
                            // change PostThread
                            posting.getParent().setLastSubject(posting.getSubject());
                            posting.getParent().setSubject(posting.getSubject());
                            posting.getParent().save();
                            // change PostArea
                            posting.getParent().getParent().setLastSubject(posting.getSubject());
                            posting.getParent().getParent().save();
                            // change Forum
                            posting.getParent().getParent().getParent().setLastSubject(posting.getSubject());
                            posting.getParent().getParent().getParent().save();
                        }
                        activePoster.signalSeen();
                    }
                }
            }
        }
        return true;
    }

    /**
     * update a already existing postthread
     * 
     * @param forumid MMBase node number of the forum
     * @param postareaid MMBase node number of the postarea
     * @param postthreadid MMBase node number of the postthread
     * @param activeid MMBase node number of current Poster (on the page)
     * @param mood New mood
     * @param state New state
     * @param type New type
     * @return Feedback regarding the success of this action
     */
    public boolean editPostThread(String forumid, String postareaid, String postthreadid, int activeid, String mood, String state,
            String type) {
        Forum forum = ForumManager.getForum(forumid);
        if (forum != null) {
            PostArea postArea = forum.getPostArea(postareaid);
            if (postArea != null) {
                PostThread postThread = postArea.getPostThread(postthreadid);
                if (postThread != null) {
                    // am i allowed to edit ?
                    Poster activePoster = forum.getPoster(activeid);
                    if (postArea.isModerator(activePoster.getNick())) {
                        postThread.setType(type);
                        postThread.setMood(mood);
                        postThread.setState(state);
                        postThread.save();
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
     * @param forumid MMBase node number of the forum
     * @param postareaid MMBase node number of the postarea
     * @param postthreadid MMBase node number of the postthread
     * @param activeid MMBase node number of current Poster (on the page)
     * @param newpostareaid New mood
     * @return Feedback regarding the success of this action
     */
    public boolean movePostThread(String forumid, String postareaid, String postthreadid, int activeid, String newpostareaid) {
        Forum forum = ForumManager.getForum(forumid);
        if (forum != null) {
            PostArea postArea = forum.getPostArea(postareaid);
            if (postArea != null) {
                PostThread postThead = postArea.getPostThread(postthreadid);
                if (postThead != null) {
                    // am i allowed to move ?
                    Poster activePoster = forum.getPoster(activeid);
                    if (postArea.isModerator(activePoster.getNick())) {
                        postArea.movePostThread(postthreadid, newpostareaid, activePoster);
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

        Forum forum = ForumManager.getForum(forumid);
        if (forum != null) {
            PostArea postArea = forum.getPostArea(postareaid);
            if (postArea != null) {
                PostThread postThread = postArea.getPostThread(postthreadid);
                if (postThread != null) {
                    Posting posting = postThread.getPosting(postingid);

                    // am i allowed to edit ?
                    Poster activePoster = forum.getPoster(activeid);
                    if (activePoster.getNick().equals(posting.getPoster()) || postArea.isModerator(activePoster.getNick())) {
                        posting.remove();
                        activePoster.signalSeen();
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
        Forum forum = ForumManager.getForum(forumid);
        if (forum != null) {
            Poster posterToRemove = forum.getPoster(removeposterid);
            Poster activePoster = forum.getPoster(activeid);
            if (posterToRemove != null && forum.isAdministrator(activePoster.getNick())) {
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
        Forum forum = ForumManager.getForum(forumid);
        if (forum != null) {
            Poster posterToDisable = forum.getPoster(disableposterid);
            Poster activePoster = forum.getPoster(activeid);
            if (posterToDisable != null && forum.isAdministrator(activePoster.getNick())) {
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
        Forum forum = ForumManager.getForum(forumid);
        if (forum != null) {
            Poster posterToDisable = forum.getPoster(enableposterid);
            Poster activePoster = forum.getPoster(activeid);
            if (posterToDisable != null && forum.isAdministrator(activePoster.getNick())) {
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

        Forum forum = ForumManager.getForum(forumid);
        if (forum != null) {
            Poster activePoster = forum.getPoster(activeid);
            if (activePoster != null && forum.isAdministrator(activePoster.getNick())) {
                if (!name.equals("")) {
                    int postareaid = forum.newPostArea(name, description);
                    map.put("postareaid", new Integer(postareaid));
                } else {
                    map.put("feedback", "feedback_emptyname");
                }
            } else {
                map.put("feedback", activePoster == null ? "User '" + activeid + "' not recognized" : "feedback_usernotallowed");
            }
        }
        return map;
    }

    /**
     * Add a new forum to the MMBase and create / attach a administrator to it
     * 
     * @param name Name of the new forum
     * @param language Language code of the new forum
     * @param description Description of the new forum
     * @param account default/first admin account name for this new forum
     * @param password default/first admin password name for this new forum
     * @return (map) containing the forumid of the newly created forum
     */
    public Map newForum(String name, String language, String description, String account, String password, String nick, String email) {
        Map map = new HashMap();
        name = filterHTML(name);
        description = filterHTML(description);
        int forumid = ForumManager.newForum(name, language, description, account, password, nick, email);
        map.put("forumid", new Integer(forumid));
        return map;
    }

    /**
     * Update forum information
     * 
     * @param forumid MMBase node number of the forum
     * @param name New name for this forum
     * @param language New language of this forum
     * @param description New description of this forum
     * @return Feedback regarding the success of this action
     */
    public boolean changeForum(String forumid, String name, String language, String description, int activeid) {
        Forum forum = ForumManager.getForum(forumid);
        if (forum != null) {
            Poster activePoster = forum.getPoster(activeid);
            if (activePoster != null && forum.isAdministrator(activePoster.getNick())) {
                forum.setName(name);
                forum.setLanguage(language);
                forum.setDescription(description);
                forum.saveDirect();
            } else {
                return false;
            }
        }
        return true;
    }

    public boolean changeForumPostingsPerPage(String forumid, int activeid, int maxpostcount) {
        Forum forum = ForumManager.getForum(forumid);
        if (forum != null) {
            Poster activePoster = forum.getPoster(activeid);
            if (forum.isAdministrator(activePoster.getNick())) {
                forum.setPostingsPerPage(maxpostcount);
                forum.saveConfig();
            } else {
                return false;
            }
        }
        return true;
    }

    public boolean changeForumReplyOnEachPage(String forumid, int activeid, String value) {
        Forum forum = ForumManager.getForum(forumid);
        if (forum != null) {
            Poster activePoster = forum.getPoster(activeid);
            if (forum.isAdministrator(activePoster.getNick())) {
                if (value.equals("true")) {
                    forum.setReplyOnEachPage(true);
                } else {
                    forum.setReplyOnEachPage(false);
                }
                forum.saveConfig();
            } else {
                return false;
            }
        }
        return true;
    }

    public boolean changeForumSpeedPostTime(String forumid, int activeid, int delay) {
        Forum forum = ForumManager.getForum(forumid);
        if (forum != null) {
            Poster activePoster = forum.getPoster(activeid);
            if (forum.isAdministrator(activePoster.getNick())) {
                forum.setSpeedPostTime(delay);
                forum.saveConfig();
            } else {
                return false;
            }
        }
        return true;
    }

    public int getForumSpeedPostTime(String forumid, int activeid) {
        Forum forum = ForumManager.getForum(forumid);
        if (forum != null) {
            Poster activePoster = forum.getPoster(activeid);
            if (forum.isAdministrator(activePoster.getNick())) {
                return forum.getSpeedPostTime();
            }
        }
        return -1;
    }

    public boolean changeForumPostingsOverflowPostArea(String forumid, int activeid, int count) {
        Forum forum = ForumManager.getForum(forumid);
        if (forum != null) {
            Poster activePoster = forum.getPoster(activeid);
            if (forum.isAdministrator(activePoster.getNick())) {
                forum.setPostingsOverflowPostArea(count);
                forum.saveConfig();
            } else {
                return false;
            }
        }
        return true;
    }

    public int getForumPostingsOverflowPostArea(String forumid, int activeid) {
        Forum forum = ForumManager.getForum(forumid);
        if (forum != null) {
            Poster activePoster = forum.getPoster(activeid);
            if (forum.isAdministrator(activePoster.getNick())) {
                return forum.getPostingsOverflowPostArea();
            }
        }
        return -1;
    }

    public int getForumPostingsPerPage(String forumid, int activeid) {
        Forum forum = ForumManager.getForum(forumid);
        if (forum != null) {
            Poster activePoster = forum.getPoster(activeid);
            if (forum.isAdministrator(activePoster.getNick())) {
                return forum.getPostingsPerPage();
            }
        }
        return -1;
    }

    public boolean changeForumPostingsOverflowThreadPage(String forumid, int activeid, int count) {
        Forum forum = ForumManager.getForum(forumid);
        if (forum != null) {
            Poster activePoster = forum.getPoster(activeid);
            if (forum.isAdministrator(activePoster.getNick())) {
                forum.setPostingsOverflowThreadPage(count);
                forum.saveConfig();
            } else {
                return false;
            }
        }
        return true;
    }

    public int getForumPostingsOverflowThreadPage(String forumid, int activeid) {
        Forum forum = ForumManager.getForum(forumid);
        if (forum != null) {
            Poster activePoster = forum.getPoster(activeid);
            if (forum.isAdministrator(activePoster.getNick())) {
                return forum.getPostingsOverflowThreadPage();
            }
        }
        return -1;
    }

    public boolean getForumReplyOnEachPage(String forumid, int activeid) {
        Forum forum = ForumManager.getForum(forumid);
        if (forum != null) {
            Poster activePoster = forum.getPoster(activeid);
            if (forum.isAdministrator(activePoster.getNick())) {
                return forum.getReplyOnEachPage();
            }
        }
        return false;
    }

    public boolean addWordFilter(String forumid, String name, String value, int activeid) {
        Forum forum = ForumManager.getForum(forumid);
        if (forum != null) {
            Poster activePoster = forum.getPoster(activeid);
            if (forum.isAdministrator(activePoster.getNick())) {
                forum.addWordFilter(name, value);
                forum.saveConfig();
            } else {
                return false;
            }
        }
        return true;
    }

    public boolean removeWordFilter(String forumid, String name, int activeid) {
        Forum forum = ForumManager.getForum(forumid);
        if (forum != null) {
            Poster activePoster = forum.getPoster(activeid);
            if (forum.isAdministrator(activePoster.getNick())) {
                forum.removeWordFilter(name);
                forum.saveConfig();
            } else {
                return false;
            }
        }
        return true;
    }

    public boolean changeForumConfig(String forumid, String loginsystemtype, String loginmodetype, String logoutmodetype,
            String guestreadmodetype, String guestwritemodetype, String avatarsuploadenabled, String avatarsgalleryenabled,
            String navigationmethod, String alias, int activeid) {
        Forum forum = ForumManager.getForum(forumid);
        if (forum != null) {
            Poster activePoster = forum.getPoster(activeid);
            if (forum.isAdministrator(activePoster.getNick())) {
                if (!loginsystemtype.equals("fixed")) forum.setLoginSystemType(loginsystemtype);
                if (!logoutmodetype.equals("fixed")) forum.setLogoutModeType(logoutmodetype);
                if (!loginmodetype.equals("fixed")) forum.setLoginModeType(loginmodetype);
                if (!guestreadmodetype.equals("fixed")) forum.setGuestReadModeType(guestreadmodetype);
                if (!guestwritemodetype.equals("fixed")) forum.setGuestWriteModeType(guestwritemodetype);
                if (!avatarsuploadenabled.equals("fixed")) forum.setAvatarsUploadEnabled(avatarsuploadenabled);
                if (!avatarsgalleryenabled.equals("fixed")) forum.setAvatarsGalleryEnabled(avatarsgalleryenabled);
                if (!navigationmethod.equals("fixed")) forum.setNavigationMethod(navigationmethod);
                forum.setAlias(alias);
                forum.saveConfig();
            } else {
                return false;
            }
        }
        return true;
    }

    public boolean changePostAreaConfig(String forumid, String postareaid, String guestreadmodetype, String guestwritemodetype,
            String threadstartlevel, int position, int activeid) {
        Forum forum = ForumManager.getForum(forumid);
        if (forum != null) {
            Poster activePoster = forum.getPoster(activeid);
            if (forum.isAdministrator(activePoster.getNick())) {
                PostArea a = forum.getPostArea(postareaid);
                if (a != null) {
                    a.setGuestReadModeType(guestreadmodetype);
                    a.setGuestWriteModeType(guestwritemodetype);
                    a.setThreadStartLevel(threadstartlevel);
                    a.setPos(position);
                    forum.saveConfig();
                }
            } else {
                return false;
            }
        }
        return true;
    }

    public boolean changeForumsConfig(String loginsystemtype, String loginmodetype, String logoutmodetype, String guestreadmodetype,
            String guestwritemodetype, String avatarsuploadenabled, String avatarsgalleryenabled, String contactinfoenabled,
            String smileysenabled, String privatemessagesenabled, String postingsperpage) {
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
     * @param forumid MMBase node number of the forum
     * @param postareaid MMBase node number of the postarea
     * @param name Name of the postarea
     * @param description Description of the postarea
     * @return Feedback regarding the success of this action
     */
    public boolean changePostArea(String forumid, String postareaid, String name, String description, int activeid) {
        Forum forum = ForumManager.getForum(forumid);
        if (forum != null) {
            Poster activePoster = forum.getPoster(activeid);
            if (activePoster != null && forum.isAdministrator(activePoster.getNick())) {
                PostArea a = forum.getPostArea(postareaid);
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
        } catch (Exception e) {}
        return true;
    }

    /**
     * 
     * @param node
     * @param poster
     */
    private void addPosterInfo(Map map, Poster poster) {
        map.put("posterid", new Integer(poster.getId()));
        map.put("account", poster.getAccount());
        map.put("nick", poster.getNick());
        map.put("firstname", poster.getFirstName());
        map.put("lastname", poster.getLastName());
        map.put("email", poster.getEmail());
        map.put("level", poster.getLevel());
        map.put("levelgui", poster.getLevelGui());
        map.put("levelimage", poster.getLevelImage());
        map.put("location", poster.getLocation());
        map.put("gender", poster.getGender());
        map.put("avatar", new Integer(poster.getAvatar()));
        map.put("accountpostcount", new Integer(poster.getPostCount()));
        map.put("firstlogin", new Integer(poster.getFirstLogin()));
        map.put("lastseen", new Integer(poster.getLastSeen()));
        map.put("signature", poster.getSignature());
    }

    /**
     * 
     * @param node
     * @param poster
     */
    private void addActiveInfo(Map map, Poster poster) {
        map.put("active_id", new Integer(poster.getId()));
        map.put("active_account", poster.getAccount());
        map.put("active_nick", poster.getNick());
        map.put("active_firstname", poster.getFirstName());
        map.put("active_lastname", poster.getLastName());
        map.put("active_email", poster.getEmail());
        map.put("active_level", poster.getLevel());
        map.put("active_levelgui", poster.getLevelGui());
        map.put("active_levelimage", poster.getLevelGui());
        map.put("active_location", poster.getLocation());
        map.put("active_gender", poster.getGender());
        map.put("active_firstlogin", new Integer(poster.getFirstLogin()));
        map.put("active_lastseen", new Integer(poster.getLastSeen()));
        map.put("active_avatar", new Integer(poster.getAvatar()));
        map.put("active_postcount", new Integer(poster.getPostCount()));
    }

    public boolean setRemoteAddress(String forumid, int posterid, String host) {
        Forum forum = ForumManager.getForum(forumid);
        if (forum != null) {
            Poster poster = forum.getPoster(posterid);
            if (poster != null) {
                poster.checkRemoteHost(host);
            }
        }
        return true;
    }

    /**
     * get login information for this poster
     */
    public Map forumLogin(String forumid, String account, String password) {
        // log.info("going to login with account: " + account + " and password " + password);
        Map map = new HashMap();
        Forum forum = ForumManager.getForum(forumid);
        if (forum != null) {
            Poster poster = forum.getPoster(account);
            if (poster != null) {
                org.mmbase.util.transformers.MD5 md5 = new org.mmbase.util.transformers.MD5();
                String md5passwd = md5.transform(password);
                if (!password.equals("blocked") && (poster.getPassword().equals(password) || poster.getPassword().equals(md5passwd))
                        && !poster.isBlocked()) {
                    map.put("state", "passed");
                    map.put("posterid", new Integer(poster.getId()));
                } else {
                    map.put("state", "failed");
                    if (poster.isBlocked() && (poster.getPassword().equals(password) || poster.getPassword().equals(md5passwd))) {
                        map.put("reason", "account blocked");
                    } else {
                        map.put("reason", "password not valid");
                    }
                }
            } else {
                map.put("state", "failed");
                map.put("reason", "account not valid");
            }
        }
        return map;
    }

    public Map getPosterPassword(String forumid, String account) {
        Map map = new HashMap();
        Forum forum = ForumManager.getForum(forumid);
        if (forum != null) {
            Poster poster = forum.getPoster(account);
            if (poster != null) {
                map.put("password", poster.getPassword());
                map.put("failed", "false");
            } else {
                map.put("failed", "true");
                map.put("reason", "noaccount");
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
        if (input.indexOf("<script") != -1) {
            return true;
        } else if (input.indexOf("<javascript") != -1) {
            return true;
        } else if (input.indexOf("<input") != -1) {
            return true;
        }
        return false;
    }

    private boolean checkMaxPostSize(String subject, String body) {
        if (subject.length() > 128) {
            return true;
        } else if (body.length() > (32 * 1024)) {
            return true;
        }
        return false;
    }

    private boolean checkSpeedPosting(PostArea a, Poster poster) {
        if (poster.getLastPostTime() != -1) {
            if ((System.currentTimeMillis() / 1000) - a.getSpeedPostTime() < poster.getLastPostTime()) {
                return true;
            }
        }
        return false;
    }

    public List getSignatures(String forumid, String sactiveid) {
        List list = new ArrayList();
        try {
            int activeid = Integer.parseInt(sactiveid);

            Forum forum = ForumManager.getForum(forumid);
            if (forum != null) {
                Poster poster = forum.getPoster(activeid);
                Iterator e = poster.getSignatures();
                if (e != null) {
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
            log.error(e);
        }
        return list;
    }

    public List getRemoteHosts(String forumid, String sactiveid) {
        List list = new ArrayList();
        try {
            int activeid = Integer.parseInt(sactiveid);

            Forum forum = ForumManager.getForum(forumid);
            if (forum != null) {
                Poster poster = forum.getPoster(activeid);
                Iterator e = poster.getRemoteHosts();
                if (e != null) {
                    while (e.hasNext()) {
                        RemoteHost rm = (RemoteHost) e.next();
                        Map map = new HashMap();
                        map.put("id", "" + rm.getId());
                        map.put("host", rm.getHost());
                        map.put("lastupdatetime", "" + rm.getLastUpdateTime());
                        map.put("updatecount", "" + rm.getUpdateCount());
                        list.add(map);
                    }
                }
            }
        } catch (Exception e) {
            log.error(e);
        }
        return list;
    }

    public String changeSignature(String forumid, String sactiveid, int sigid, String body, String mode, String encoding) {
        try {
            int activeid = Integer.parseInt(sactiveid);

            Forum forum = ForumManager.getForum(forumid);
            if (forum != null) {
                Poster poster = forum.getPoster(activeid);
                if (poster != null) {
                    Signature sig = poster.getSignature(sigid);
                    if (sig != null) {
                        sig.setBody(body);
                        sig.setEncoding(encoding);
                        sig.setMode(mode);
                    }
                }
            }
        } catch (Exception e) {
            log.error(e);
        }
        return "";
    }

    public String setSingleSignature(String forumid, String sactiveid, String body, String encoding) {
        try {
            int activeid = Integer.parseInt(sactiveid);

            Forum forum = ForumManager.getForum(forumid);
            if (forum != null) {
                Poster poster = forum.getPoster(activeid);
                if (poster != null) {
                    Signature sig = poster.getSingleSignature();
                    if (sig != null) {
                        sig.setBody(body);
                        sig.setEncoding(encoding);
                    } else {
                        poster.addSignature(body, "create", encoding);
                    }
                }
            }
        } catch (Exception e) {
            log.error(e);
        }
        return "";
    }

    public Map getSingleSignature(String forumid, String sactiveid) {
        Map map = new HashMap();
        try {
            int activeid = Integer.parseInt(sactiveid);

            Forum forum = ForumManager.getForum(forumid);
            if (forum != null) {
                Poster poster = forum.getPoster(activeid);
                if (poster != null) {
                    Signature sig = poster.getSingleSignature();
                    if (sig != null) {
                        map.put("body", sig.getBody());
                        map.put("encoding", sig.getEncoding());
                    }
                }
            }
        } catch (Exception e) {
            log.error(e);
        }
        return map;
    }

    public String addSignature(String forumid, String sactiveid, String body, String mode, String encoding) {
        try {
            int activeid = Integer.parseInt(sactiveid);

            Forum forum = ForumManager.getForum(forumid);
            if (forum != null) {
                Poster poster = forum.getPoster(activeid);
                if (poster != null) {
                    poster.addSignature(body, mode, encoding);
                }
            }
        } catch (Exception e) {
            log.error(e);
        }
        return "";
    }

    public boolean setBookmarkedChange(String forumid, String postthreadid, int posterid, String state) {
        Forum forum = ForumManager.getForum(forumid);
        Poster activePoster = forum.getPoster(posterid);
        if (activePoster != null && forum != null) {
            try {
                int id = Integer.parseInt(postthreadid);
                if (state.equals("true")) {
                    forum.setBookmarkedChange(id, activePoster, true);
                } else {
                    forum.setBookmarkedChange(id, activePoster, false);
                }
            } catch (Exception e) {
                log.error(e);
            }
        }
        return false;
    }

    public boolean setEmailOnChange(String forumid, String postthreadid, int posterid, String state) {
        Forum forum = ForumManager.getForum(forumid);
        Poster activePoster = forum.getPoster(posterid);
        if (activePoster != null && forum != null) {
            try {
                int id = Integer.parseInt(postthreadid);
                if (state.equals("true")) {
                    forum.setEmailOnChange(id, activePoster, true);
                } else {
                    forum.setEmailOnChange(id, activePoster, false);
                }
            } catch (Exception e) {
                log.error(e);
            }
        }
        return false;
    }

    public String filterHTML(String body) {
        StringObject obj = new StringObject(body);
        obj.replace(">", "&gt;");
        obj.replace("<", "&lt;");
        return obj.toString();
    }

    public List getProfileValues(String forumid, int posterid, int guipos) {
        List list = new ArrayList();
        Forum forum = ForumManager.getForum(forumid);
        if (forum != null && posterid != -1) {
            Poster poster = forum.getPoster(posterid);
            if (poster != null) {
                Iterator i = forum.getProfileDefs();
                if (i != null) {
                    while (i.hasNext()) {
                        Map map = new HashMap();
                        ProfileEntryDef pd = (ProfileEntryDef) i.next();
                        if (pd.getGuiPos() >= guipos) {
                            map.put("name", pd.getName());
                            map.put("guiname", pd.getGuiName());
                            map.put("guipos", new Integer(pd.getGuiPos()));
                            map.put("edit", "" + pd.getEdit());
                            map.put("type", pd.getType());
                            ProfileEntry pe = poster.getProfileValue(pd.getName());
                            if (pe != null) {
                                map.put("value", pe.getValue());
                                if (pd.getExternal() != null) {
                                    map.put("synced", "" + pe.getSynced());
                                } else {
                                    map.put("synced", "internal");
                                }
                            } else {
                                map.put("synced", "not set");
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
        Forum forum = ForumManager.getForum(forumid);
        if (forum != null) {
            Map words = forum.getFilterWords();
            Iterator i = words.keySet().iterator();
            while (i.hasNext()) {
                String key = (String) i.next();
                String value = (String) words.get(key);
                HashMap map = new HashMap();
                map.put("name", key);
                map.put("value", value);
                list.add(map);
            }
        }
        return list;
    }

    public Map setProfileValue(String forumid, int activeid, String name, String value) {
        Map map = new HashMap();

        value = filterHTML(value);

        Forum forum = ForumManager.getForum(forumid);
        if (forum != null) {
            Poster activePoster = forum.getPoster(activeid);
            if (activePoster != null) {
                String feedback = activePoster.setProfileValue(name, value);
            }
        }
        return map;
    }

    public String getBirthDateString(String name, String value) {
        // very ugly need to be changed
        String day = "1";
        String month = "1";
        String year = "1980";
        StringTokenizer tok = new StringTokenizer(value, "-\n\r");
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
        String body = "<select name=\"" + name + "_day\">";
        for (int i = 1; i < 32; i++) {
            if (day.equals("" + i)) {
                body += "<option selected>" + i;
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
        Forum forum = ForumManager.getForum(id);
        if (forum != null) {
            return forum.getGuiEdit(key);
        }
        return "true";
    }

}
