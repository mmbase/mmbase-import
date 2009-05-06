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
import java.util.concurrent.*;
import java.io.*;

import org.mmbase.util.*;
import org.mmbase.bridge.*;
import org.mmbase.cache.*;
import org.mmbase.bridge.implementation.*;
import org.mmbase.storage.search.*;
import org.mmbase.storage.search.implementation.*;
import org.mmbase.module.core.*;
import org.mmbase.module.corebuilders.*;
import org.mmbase.applications.multilanguagegui.*;

import org.mmbase.util.logging.Logging;
import org.mmbase.util.logging.Logger;

/**
 * This seems to be a wrapper around a Node of type 'postareas'.
 * @javadoc
 *
 * @author Daniel Ockeloen
 * @version $Id: PostArea.java,v 1.48 2007-10-18 09:55:59 michiel Exp $:
 */
public class PostArea {

    static private Logger log = Logging.getLoggerInstance(PostArea.class);

    private int id;
    private final Forum parent;
    private Map<String, Poster> moderators = new Hashtable<String, Poster>(); // synchronized?
    private String moderatorsline;
    private List<PostThread> postThreads = null;
    private final Map<String, PostThread> nameCache = new Hashtable<String, PostThread>(); // synchronized?
    private boolean firstcachecall = true;
    private PostAreaConfig config;
    private final Map<String, String> filterwords = null; // this (proves that this) is never used!

    private int viewcount;
    private int postcount;
    private int lastposttime;
    private int lastpostnumber;
    private int lastposternumber;
    private int numberofpinned = 0;
    private String name;
    private String description;
    private String lastposter;
    private String lastpostsubject;

    /**
     * Constructor
     * @param parent forum
     * @param node postarea
     */
    public PostArea(Forum parent, Node node) {
        this.parent = parent;
	this.id = node.getNumber();
        name = node.getStringValue("name");
        description = node.getStringValue("description");

	config  = parent.getPostAreaConfig(getName());

        this.viewcount = node.getIntValue("viewcount");
        if (viewcount == -1) viewcount = 0;
        this.postcount = node.getIntValue("postcount");
        if (postcount == -1) postcount = 0;

        this.lastpostsubject = node.getStringValue("c_lastpostsubject");
        this.lastposter = node.getStringValue("c_lastposter");
        this.lastposttime = node.getIntValue("c_lastposttime");
        this.lastpostnumber=node.getIntValue("lastpostnumber");
        this.lastposternumber=node.getIntValue("lastposternumber");

        readRoles();
    }

    /**
     * set the MMBase objectnumber for the postarea
     * @param id MMBase objectnumber
     */
    public void setId(int id) {
        this.id = id;
    }

    /**
     * set the node for the postarea
     * @param node postarea
     */
    /*
      public void setNode(Node node) {
      this.node = node;
      }
    */

    /**
     * set the name for the postarea
     * @param name postareaname
     */
    public void setName(String name) {
        this.name = name;
        syncNode(ForumManager.FASTSYNC);

    }

    /**
     * set a description for the postarea
     * @param description postareadescription
     */
    public void setDescription(String description) {
        this.description = description;
    }

    /**
     * get the name of the postarea
     * @return postareaname
     */
    public String getName() {
        return name;
    }

    public String getShortName() {
	int pos = name.lastIndexOf('/');
	if (pos!=-1) return name.substring(pos+1);
        return name;
    }

    /**
     * get the description of the postarea
     * @return postareadescription
     */
    public String getDescription() {
        return description;
    }

    /**
     * get the MMBase objectnumber of the postarea
     * @return MMBase objectnumber
     */
    public int getId() {
        return id;
    }

    /**
     * get the number of postthreads of the postarea
     * @return number of postthreads
     */
    public int getPostThreadCount() {
        return postThreads == null ? 0 : postThreads.size();
    }

    /**
     * get the average number of posts per postthread
     * @return average number of posts per postthread
     */
    public int getPostThreadCountAvg() {
        int postthreadcount = getPostThreadCount();
        if (postthreadcount == 0) return 0;
        return postcount / postthreadcount;
    }

    public int getPostThreadLoadedCount() {
        if (postThreads == null) {
            return 0;
	} else {
            int count = 0;
            for (PostThread pt : postThreads) {
                if (pt.isLoaded()) count++;
            }
            return count;
	}
    }


    public int getPostingsLoadedCount() {
        if (postThreads == null) {
            return 0;
	} else {
            int count = 0;
            for (PostThread pt : postThreads) {
                if (pt.isLoaded()) count+=pt.getPostCount();
            }
            return count;
	}
    }

    public int getMemorySize() {
        if (postThreads == null) {
            return 0;
	} else {
            int size = 0;
            for (PostThread pt : postThreads) {
                size += pt.getMemorySize();
            }
            return size;
	}
    }



    /**
     * get the number of pages for this postarea
     * @param pagesize maximum number of postthreads per page
     * @return the number of pages for this postarea
     */
    public int getPageCount(int pagesize) {
        int postthreadcount = getPostThreadCount();
        int pagecount = postthreadcount / pagesize;
        if ((pagecount * pagesize) != postthreadcount) pagecount++;
        return pagecount;
    }

    /**
     * get the number of posts in this postarea
     * @return number of posts
     */
    public int getPostCount() {
        return postcount;
    }

    /**
     * get the number of views in this postarea
     * @return number of views
     */
    public int getViewCount() {
        return viewcount;
    }

    /**
     * get the accountname/nick of the last poster in the postarea
     * @return accountname/nick
     */
    public String getLastPoster() {
        return lastposter;
    }

    /**
     * get the last poster in the postarea
     * @return last poster
     */
    public int getLastPosterNumber() {
	if (parent.hasPoster(lastposternumber)) {
            return lastposternumber;
	} else {
            return -1;
	}
    }


    /**
     * get the last postthread in the postarea
     * @return last postthreadnumber
     */
    public int getLastPostThreadNumber() {
	if (postThreads == null) readPostThreads();
	if (postThreads.size() > 0) {
            PostThread pt = postThreads.get(0);
            return pt.getId();
	} else {
            return -1;
	}
    }

    /**
     * get the last post in the postarea
     * @return postnumber
     */
    public int getLastPostNumber() {
        return lastpostnumber;
    }

    /**
     * get the date/time of the last post in the postarea
     * @return date/time of the last post
     */
    public int getLastPostTime() {
        return lastposttime;
    }

    /**
     * get the subject of the last post in the postarea
     * @return subject of the last post
     */
    public String getLastSubject() {
        return lastpostsubject;
    }

    public void setLastSubject(String subject) {
        lastpostsubject = subject;
    }

    /**
     * get all the postthreads in the postarea
     * @return all the postthreads
     */
    public Enumeration<PostThread> getPostThreads() {
        if (postThreads == null) readPostThreads();
        return Collections.enumeration(postThreads);
    }

    /**
     * get an iterator of the postthreads in the postarea
     * @param page which page in the sequence
     * @param pagecount maximum number of PostThreads on the page
     * @return postthreads
     */
    public Iterator<PostThread> getPostThreads(int page, int pagecount) {
        if (postThreads == null) readPostThreads();

        // get the range we want
        int start = (page - 1) * pagecount;
        int end = page * pagecount;
        if (end > postThreads.size()) {
            end = postThreads.size();
        }
        if (log.isDebugEnabled()) {
            log.debug("START=" + start + " " + end + " " + postThreads.size());
        }
        List<PostThread> result = postThreads.subList(start, end);

        return result.iterator();
    }

    /**
     * get a postthread by it's MMbase Objectnumber
     * @param id MMbase Objectnumber
     * @return postthread
     */
    public PostThread getPostThread(String id) {
        if (postThreads == null) readPostThreads();
        return  nameCache.get(id);
    }

    /**
     * get a (html) navigationline to "walk through" the different pages of PostThreads for a postarea
     * @param baseurl url
     * @param page which page in the sequence
     * @param pagesize maximum number of postthreads to be shown on one page
     * @param cssclass cssclass to be used to display the navigationline
     * @return navigationline in html
     */
    public String getNavigationLine(String baseurl, int page, int pagesize, String cssclass) {
        int f = parent.getId();
        int a = getId();
        if (cssclass!=null && !cssclass.equals("")) {
            cssclass = " class=\"" + cssclass + "\"";
        }

        int postthreadcount = getPostThreadCount();
        // weird way must be a better way for pagecount
        int pagecount = postthreadcount / pagesize;
        if ((pagecount * pagesize) != postthreadcount) pagecount++;

        // if only one page no nav line is needed
        if (pagecount == 1) return "";


        int c = page - 1;
        if (c < 1) c = 1;
        int n = page + 1;
        if (n > pagecount) n = pagecount;

        // @todo Use StringBuilder. Use XHTML.

        String result = "<a href=\"" + baseurl + "?forumid=" + f + "&postareaid=" + a + "&page=" + c + "\"" + cssclass + ">&lt</a>";
	int i = 1;
        for (i = 1; i <= pagecount; i++) {
            result += " <a href=\"" + baseurl + "?forumid=" + f + "&postareaid=" + a + "&page=" + i + "\"" + cssclass + ">";
            if (i == page) {
                result += "[" + i + "]";
            } else {
                result += "" + i;
            }
            result += "</a>";
        }
	String lastword = "last";
	if (parent.getLanguage().equals("nl")) lastword="laatste";
        result += " <a href=\"" + baseurl + "?forumid=" + f + "&postareaid=" + a + "&page=" + (i-1) + "\"" + cssclass + "> "+lastword+"</a>";
        result += " <a href=\"" + baseurl + "?forumid=" + f + "&postareaid=" + a + "&page=" + n + "\"" + cssclass + ">&gt</a>";
        return result;
    }

    /**
     * get the moderators of the postarea
     * @return moderators
     * @deprecated returning enumerations?!
     */
    public Enumeration<Poster> getModerators() {
        return Collections.enumeration(moderators.values());
    }

    /**
     * get all posters that are no moderator of the postarea
     * @return non-moderators
     * @deprecated returning enumerations?!
     */
    public Enumeration<Poster> getNonModerators(String searchKey) {
	Vector<Poster> result =  new Vector<Poster>(); // useless synchronization
        Enumeration<Poster> e = parent.getPosters();
        while (e.hasMoreElements()) {
            Poster p = e.nextElement();
            if (!isModerator(p.getNick())) {
                String nick =  p.getNick().toLowerCase();
                String firstName = p.getFirstName().toLowerCase();
                String lastName = p.getLastName().toLowerCase();
                if (searchKey == null ||
                    "*".equals(searchKey) || nick.indexOf(searchKey) != -1 ||
                    firstName.indexOf(searchKey)!=-1 || lastName.indexOf(searchKey)!= -1) {
                    result.add(p);
                    if (result.size() >49 ) {
                        return result.elements();
                    }
                }
            }
	}
        return result.elements();
    }

    /**
     * determine if the given accountname/nick is a moderator of this postarea
     * @param nick accountname/nick to be evaluated
     * @return <code>true</code> if the account is moderator. Also <code>true</code> if the account is administrator of the parent forum.
     */
    public boolean isModerator(String nick) {
        // check if he a admin then asign him
        // the moderators role too
        if (parent.isAdministrator(nick)) return true;
        return moderators.containsKey(nick);
    }

    /**
     * remove the moderator status of the poster regarding this postarea
     * @param mp Poster with moderator status
     * @return Always <code>false</code>
     */
    public boolean removeModerator(Poster mp) {
        if (isModerator(mp.getNick())) {
            Node node = ForumManager.getCloud().getNode(id);
            RelationIterator i = node.getRelations("rolerel", "posters").relationIterator();
            while (i.hasNext()) {
                Relation rel = i.nextRelation();
                String role = rel.getStringValue("role");
                if (role.equals("moderator")) {
                    Node p = null;
                    if (rel.getSource().getNumber() == node.getNumber()) {
                        p = rel.getDestination();
                    } else {
                        p = rel.getSource();
                    }
                    if (p != null && p.getNumber() == mp.getId()) {
                        rel.delete();
                        moderators.remove(mp.getNick());
                        moderatorsline = null;
                    }
                }
            }
        }
        return false;
    }

    /**
     * add moderator status to given poster for this postarea
     * @param mp Poster
     * @return <code>true</code> if the action succeeded
     */
    public boolean addModerator(Poster mp) {
        if (isModerator(mp.getNick())) return true;
        RelationManager rm = ForumManager.getCloud().getRelationManager("postareas", "posters", "rolerel");
        if (rm != null) {
            Node node = ForumManager.getCloud().getNode(id);
            Node rel = rm.createRelation(node, mp.getNode());
            rel.setStringValue("role", "moderator");
            rel.commit();
            moderators.put(mp.getNick(), mp);
            moderatorsline = null;
        } else {
            log.error("Forum can't load relation nodemanager postareas/posters/rolerel");
            return false;
        }
        return true;
    }

    /**
     * get the moderators line in html
     * @param baseurl
     * @return moderatorsline in html
     */
    public String getModeratorsLine(String baseurl) {
        if (moderatorsline != null) return moderatorsline;
        moderatorsline = "";
        Enumeration<Poster> e = Collections.enumeration(moderators.values()); // WTF!
        while (e.hasMoreElements()) {
            Poster p = e.nextElement();
            if (!moderatorsline.equals("")) moderatorsline += ",";
            if (baseurl.equals("")) {
                moderatorsline += p.getNick();
            } else {
                moderatorsline += "<a href=\"" + baseurl + "?forumid=" + parent.getId() + "&postareaid=" + getId() + "&posterid=" + p.getId() + "\">" + p.getNick() + "</a>";
            }
        }
        return moderatorsline;
    }

    /**
     * Fills the postthreads-Vector
     */
    private synchronized void readPostThreads() {
        if (postThreads == null) {
            List<PostThread> list = new ArrayList<PostThread>();

            long start = System.currentTimeMillis();
            //log.info("reading threads");
            NodeManager postareasmanager = ForumManager.getCloud().getNodeManager("postareas");
            NodeManager postthreadsmanager = ForumManager.getCloud().getNodeManager("postthreads");
            Query query = ForumManager.getCloud().createQuery();
            Step step1 = query.addStep(postareasmanager);
            RelationStep step2 = query.addRelationStep(postthreadsmanager);
            StepField f1 = query.addField(step1, postareasmanager.getField("number"));
            StepField f3 = query.addField(step2.getNext(), postthreadsmanager.getField("c_lastposttime"));
            query.addField(step2.getNext(), postthreadsmanager.getField("number"));
            query.addField(step2.getNext(), postthreadsmanager.getField("subject"));
            query.addField(step2.getNext(), postthreadsmanager.getField("creator"));
            query.addField(step2.getNext(), postthreadsmanager.getField("viewcount"));
            query.addField(step2.getNext(), postthreadsmanager.getField("postcount"));
            query.addField(step2.getNext(), postthreadsmanager.getField("c_lastpostsubject"));
            query.addField(step2.getNext(), postthreadsmanager.getField("c_lastposter"));
            query.addField(step2.getNext(), postthreadsmanager.getField("lastposternumber"));
            query.addField(step2.getNext(), postthreadsmanager.getField("lastpostnumber"));
            query.addField(step2.getNext(), postthreadsmanager.getField("mood"));
            query.addField(step2.getNext(), postthreadsmanager.getField("state"));
            query.addField(step2.getNext(), postthreadsmanager.getField("ttype"));

            query.addSortOrder(f3, SortOrder.ORDER_DESCENDING);

            query.setConstraint(query.createConstraint(f1, Integer.valueOf(id)));
            NodeIterator i2 = ForumManager.getCloud().getList(query).nodeIterator();
            int newcount = 0;
            int newthreadcount = 0;
            while (i2.hasNext()) {
                Node n2 = i2.nextNode();
                PostThread postthread = new PostThread(this, n2,true);
                newcount += postthread.getPostCount();
                newthreadcount++;
                if (postthread.getState().equals("pinned") || postthread.getState().equals("pinnedclosed")) {
                    list.add(numberofpinned, postthread);
                    numberofpinned++;
                } else {
                    list.add(postthread);
                }
                nameCache.put("" + n2.getIntValue("postthreads.number"), postthread);
            }
            postThreads = new CopyOnWriteArrayList<PostThread>(list);


            long end = System.currentTimeMillis();
            //log.info("end reading threads time="+(end-start));

            // check the count number
            if (postcount != newcount) {
                log.info("resync of postareacount : "+postcount+" "+newcount);
                postcount = newcount;
                save();
            }

            int postthreadcount = getPostThreadCount();

            // check the threadcount number
            if (postthreadcount != newthreadcount) {
                log.info("resync of postareathreadcount : "+postthreadcount+" "+newthreadcount);
                postthreadcount = newthreadcount;
                save();
            }


            // very raw way to zap the cache

            // MM: OH NO, THIS IS VERY, VERY STUPID. Remove this ASAP.
            log.info("Clearing _All_ MMBase caches!");
            Cache cache = RelatedNodesCache.getCache();
            cache.clear();
            cache = NodeCache.getCache();
            cache.clear();
            cache = NodeCache.getCache();
            cache.clear();
            cache = MultilevelCache.getCache();
            cache.clear();
            cache = NodeListCache.getCache();
            cache.clear();
        }
    }

    /**
     * increase the numberofpinned threads in the postarea
     */
    public void incPinnedCount() {
        numberofpinned++;
    }

    /**
     * decrease the numberofpinned threads in the postarea
     */
    public void decPinnedCount() {
        numberofpinned--;
    }


    /**
     * create a new postthread in the postarea
     * @param subject subject of the new postthread
     * @param poster accountname/nick of the poster that posts the new postthread
     * @param body body of the new postthread
     * @return MMbase objectnumber of the newly created postthread or -1 if the postthread-nodemanager could not be found
     */
    public int newPost(String subject, Poster poster, String body, String mood, boolean parsed) {
        if (postThreads == null) readPostThreads();
        NodeManager nm = ForumManager.getCloud().getNodeManager("postthreads");
        if (nm != null) {
            Node ptnode = nm.createNode();
            ptnode.setStringValue("subject", subject);
	    if (poster!=null) {
            	ptnode.setStringValue("creator", poster.getNick());
	    } else {
            	ptnode.setStringValue("creator", "gast");
	    }
            ptnode.setStringValue("state", "normal");
            ptnode.setStringValue("mood", mood);
            ptnode.setStringValue("ttype", "post");
            ptnode.setIntValue("createtime", (int) (System.currentTimeMillis() / 1000));
            ptnode.commit();
            RelationManager rm = ForumManager.getCloud().getRelationManager("postareas", "postthreads", "areathreadrel");
            if (rm != null) {
	        Node node = ForumManager.getCloud().getNode(id);
                Node rel = rm.createRelation(node, ptnode);
                rel.commit();
                PostThread postthread = new PostThread(this, ptnode, false);
		postthread.setLoaded(true);

                // now add the first 'reply' (wrong name since its not a reply)
                postthread.postReply(subject, poster, body,parsed);
                if (postthread.getState().equals("pinned") || postthread.getState().equals("pinnedclosed")) {
                    postThreads.add(0, postthread);
                } else {
                    postThreads.add(numberofpinned, postthread);
                }
                nameCache.put("" + ptnode.getNumber(), postthread);

                syncNode(ForumManager.FASTSYNC);
                parent.signalNewPost(this);
                return ptnode.getNumber();
            } else {
                log.error("Forum can't load relation nodemanager postareas/postthreads/areathreadrel");
            }
        } else {
            log.error("Forum can't load postthreads nodemanager");
        }
        return -1;
    }

    /**
     * signal the postarea that there is a new reply
     * @param child postthread
     */
    public void signalNewReply(PostThread child) {
        postcount++;
        lastposttime = child.getLastPostTime();
        lastposter = child.getLastPoster();
        lastpostsubject = child.getLastSubject();
        lastpostnumber=child.getLastPostNumber();
        lastposternumber=child.getLastPosterNumber();
        syncNode(ForumManager.FASTSYNC);

        resort(child);

        parent.signalNewReply(this);
    }

    /**
     * signal the postarea that a reply was removed so it cat update the postcount etc ..
     * @param child postthread
     */
    public void signalRemovedReply(PostThread child) {
        // todo: Make this configurable.
        //       uncomment this if you want to decrease the stats if a thread was removed
        //postcount--;

        if (lastposttime == child.getLastPostTime() && lastposter.equals(child.getLastPoster())) {
            lastpostsubject = "removed";
        }

        lastposttime = child.getLastPostTime();
        lastposter = child.getLastPoster();

        syncNode(ForumManager.FASTSYNC);

        // signal the parent Forum that it's postcount needs to be updated
        parent.signalRemovedReply(this);
    }

    /**
     * re-add the given PostThread to the postthreads-Vector
     * @param child postthread
     */
    protected void resort(PostThread child) {
        log.debug("Readding " + child);
        // move to the top of the queue
        if (postThreads.remove(child)) {
            if (child.getState().equals("pinned") || child.getState().equals("pinnedclosed")) {
                postThreads.add(0, child);
            } else {
                postThreads.add(numberofpinned, child);
            }
        }
    }

    public boolean movePostThread(String postthreadid,String newpostareaid,Poster poster) {
	// get the target area
	PostArea ta = parent.getPostArea(newpostareaid);
	if (ta!=null) {
            PostThread t = getPostThread(postthreadid);
            if (t != null) {
                int pos=0;
                PostThread npt = null;
                Posting p = t.getPostingPos(0);
                while (p != null) {
                    if (pos == 0) {
                        String body = p.getDirectBody(); // stringbuilder?
                        if (body.indexOf("<posting>") == 0) {
                            body = "<posting> " + MultiLanguageGui.getConversion("mmbob.movedfrom", parent.getLanguage()) +
                                " : " + getName() + " " + MultiLanguageGui.getConversion("mmbob.by", parent.getLanguage())+
                                " " + poster.getNick() + "<br />----<br /><br />" + body.substring(9);
                        }
                        log.info("P1=" + p.getPoster()); // debug?
                        log.info("P2=" + parent.getPosterNick(p.getPoster()));
                        Poster nposter = parent.getPosterNick(p.getPoster());
                        int np = ta.newPost(p.getSubject(),nposter,body,p.getParent().getMood(),true);
                        log.info("NPOSRER=" + nposter);
                        nposter.decPostCount(); // compensate counter
                        npt = ta.getPostThread("" + np);
                        if (npt != null) {
                            int nr=npt.getLastPostNumber();
                            if (nr!=-1) {
                                Posting npr=npt.getPosting(nr);
                                npr.setPostTime(p.getPostTime());
                                npr.save();
                                body =
                                    MultiLanguageGui.getConversion("mmbob.movedto", parent.getLanguage()) +
                                    " : " + ta.getName() + " " +
                                    MultiLanguageGui.getConversion("mmbob.by", parent.getLanguage()) +
                                    " " + poster.getNick() + "\n\r\n\r";
                                body += "[url]/mmbob/thread.jsp?forumid=" + parent.getId() + "&postareaid=" + ta.getId() +
                                    "&postthreadid=" + npt.getId() + "[/url]";
                                p.setBody(body, "", false);
                                p.save();
                                t.setState("closed");
                                t.save();
                            }
                        }
                    } else {
                        String body = p.getDirectBody();
                        if (npt != null) {
                            Poster nposter = parent.getPoster(p.getPoster());
                            int nr = npt.postReply(p.getSubject(), nposter, body, true);
                            nposter.decPostCount(); // compensate counter
                            if (nr != -1) {
                                Posting npr = npt.getPosting(nr);
                                npr.setPostTime(p.getPostTime());
                                npr.save();
                                p.remove();
                            }
                        }
                    }
                    pos++;
                    p = t.getPostingPos(1);
                }
            }
	}
	return true;
    }

    /**
     * get the parent forum of this postarea
     * @return Forum
     */
    public Forum getParent() {
        return parent;
    }

    /**
     * signal the postarea that the number of views has been changed
     * @param child
     */
    public void signalViewsChanged(PostThread child) {
        viewcount++;
        syncNode(ForumManager.SLOWSYNC);
        parent.signalViewsChanged(this);
    }

    /**
     * save the postarea (add it to the SyncQueue)
     */
    public boolean save() {
	// this can still give a problem since its not direct !
	// so forced direct
        Node node = ForumManager.getCloud().getNode(id);
        node.setStringValue("name", name);
        node.setStringValue("description", description);
	node.commit();

	// need to work in this, daniel
        syncNode(ForumManager.FASTSYNC);
        return true;
    }

    /**
     * add the postarea-node to the given syncQueue
     *
     * @param queue syncQueue that must be used
     */
    private void syncNode(int queue) {
        Node node = ForumManager.getCloud().getNode(id);
        node.setStringValue("name", name);
        node.setStringValue("description", description);
        node.setIntValue("postthreadcount", getPostThreadCount());
        node.setIntValue("postcount", postcount);
        node.setIntValue("viewcount", viewcount);
        node.setIntValue("c_lastposttime", lastposttime);
        node.setStringValue("c_lastposter", lastposter);
        node.setStringValue("c_lastpostsubject", lastpostsubject);
        node.setIntValue("lastposternumber",lastposternumber);
        node.setIntValue("lastpostnumber",lastpostnumber);
        ForumManager.syncNode(node, queue);
    }

    /**
     * Called by constructor
     * fills the HashTable "moderators" with all known moderators for this postarea
     */
    private void readRoles() {
        Node node = ForumManager.getCloud().getNode(id);
        RelationIterator i = node.getRelations("rolerel", "posters").relationIterator();
        while (i.hasNext()) {
            Relation rel = i.nextRelation();
            Node p = null;
            if (rel.getSource().getNumber() == node.getNumber()) {
                p = rel.getDestination();
            } else {
                p = rel.getSource();
            }
            String role = rel.getStringValue("role");
            if (role.equals("moderator")) {
                Poster po = parent.getPoster(p.getNumber());
                moderators.put(po.getNick(), po);
            }
        }
    }

    /**
     * remove the postarea
     * remove the postarea.
     * This method will first (try to) remove all postthreads.
      * @return  <code>true</code> if it succeeds, <code>false</code> if it doesn't
      */
     public boolean remove() {
         //first remove all the postTheads
         if (postThreads == null) readPostThreads();
         if (getPostThreadCount() != 0) {
             Iterator<PostThread> i = postThreads.iterator();
             while (i.hasNext()) {
                 PostThread postThread = i.next();
                 log.debug("try to remove postthread: "+postThread.getId());
                 if (!postThread.remove()) {
                     log.error("Can't remove PostThread : " + postThread.getId());
                     return false;
                 }
                 //i.remove(); // This causes ConcurrentModificationException, which I don't quite understand.
                 // This used to be:
                 //postThreads.remove("" + postThread.getId());
                 // but that can't be correct, no Strings in that list.
                 // I suppose this is meant:
                 nameCache.remove("" + postThread.getId());
             }
         }
         Node node = ForumManager.getCloud().getNode(id);
         log.debug("deleting PostArea with id " + node.getNumber());
         ForumManager.nodeDeleted(node);
         node.delete(true);
         if (log.isDebugEnabled()) {
             log.debug("postThreads " + postThreads);
         }
         return true;
     }





    /**
     * remove a postthread by it's MMbase objectnumber
     * @param postthreadid MMbase objectnumber of the postthread
     * @return <code>true</code> if it succeeds, <code>false</code> if it doesn't
     */
    public boolean removePostThread(String postthreadid) {
        PostThread t = getPostThread(postthreadid);
        if (t != null) {
            if (!t.remove()) {
                log.error("Can't remove PostThread : " + t.getId());
                return false;
            } else {
                log.info("removed PostThread : " + t);
            	postThreads.remove(t);
	    }
        }
        return true;
    }

    /**
     * remove a postthread from the postthreads and signal the
     * parent forum that Thread was removed
     *
     * @param t postthread
     */
    public void childRemoved(PostThread t) {
        // todo: Make this configurable.
        //       uncomment this if you want to decrease the stats if a thread was removed
        //postthreadcount--;

        postThreads.remove(t);
        syncNode(ForumManager.FASTSYNC);
        parent.signalRemovedPost(this);
    }

    /**
     * called to maintain the memorycaches
     */
    public void maintainMemoryCaches() {
	int ptime = ForumManager.getPreloadChangedThreadsTime();
        //if (ptime!=0 && postthreads != null && firstcachecall) {
        if (ptime != 0 && firstcachecall) {
       	    if (postThreads == null) {
                readPostThreads();
            }
            firstcachecall = false;
            int time = (int) (System.currentTimeMillis() / 1000) - ptime;
            for (PostThread t : postThreads) {
                int time2 = t.getLastPostTime();
                if (time2 == -1 || time2 > time) {
                    t.readPostings();
                }
            }
        }

        if (postThreads != null) {
	    int etime = ForumManager.getSwapoutUnusedThreadsTime();
	    if (etime != 0) {
                int time = (int) (System.currentTimeMillis() / 1000) - etime;
                int time4 = (int) (System.currentTimeMillis() / 1000) - ptime;

                for (PostThread t : postThreads) {
                    if (t.isLoaded()) {
                	int time2 = t.getLastUsed();
                	int time3 = t.getLastPostTime();
	                if (time2 < time && time4 > time3) {
                            t.swapOut();
                	}
                    }
                }
	    }
        }
    }


    public String getGuestReadModeType() {
	if (config != null) {
            String tmp = config.getGuestReadModeType();
            if (tmp != null) {
                return tmp;
            }
	}
        return parent.getGuestReadModeType();
    }


    public String getGuestWriteModeType() {
	if (config != null) {
            String tmp = config.getGuestWriteModeType();
            if (tmp != null) {
                return tmp;
            }
	}
        return parent.getGuestWriteModeType();
    }


    public String getThreadStartLevel() {
	if (config != null) {
            String tmp = config.getThreadStartLevel();
            if (tmp != null) {
                return tmp;
            }
	}
        return parent.getThreadStartLevel();
    }

    public String filterContent(String body) {
	if (filterwords != null) {
            return parent.filterContent(filterwords, body);
	} else {
            return parent.filterContent(body);
	}
    }

    public int getSpeedPostTime() {
	return parent.getSpeedPostTime();
    }


    public List<Posting> searchPostings(String searchkey, int posterid) {
	List<Posting> results = new Vector<Posting>(); // synchronized?
	return searchPostings(results,searchkey,posterid);
    }

    public List<Posting> searchPostings(List results, String searchkey, int posterid) {
	// check if this area is searchable for this user (is he logged in)
	if (posterid == -1 && getGuestReadModeType().equals("closed")) return results;
	if (postThreads != null) {
            for (PostThread thread : postThreads) {
                results = thread.searchPostings(results, searchkey, posterid);
            }
	}
	return results;
    }


    public void setGuestReadModeType(String guestreadmodetype) {
	checkConfig();
	config.setGuestReadModeType(guestreadmodetype);
    }

    public void setGuestWriteModeType(String guestwritemodetype) {
	checkConfig();
	config.setGuestWriteModeType(guestwritemodetype);
    }

    public void setThreadStartLevel(String threadstartlevel) {
	checkConfig();
	config.setThreadStartLevel(threadstartlevel);
    }

    public void setPos(int pos) {
	checkConfig();
	config.setPos(pos);
    }

    public int getPos() {
	if (config == null) return 0;
	return config.getPos();
    }


    private boolean checkConfig() {
        if (config == null) {
            log.info("BLA="+getName()+" "+parent.getConfig());
            config = parent.getConfig().addPostAreaConfig(getName());
        }
        return true;
    }

}
