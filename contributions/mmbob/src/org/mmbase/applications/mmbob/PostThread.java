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
import java.io.*;

import org.mmbase.util.*;
import org.mmbase.cache.*;
import org.mmbase.bridge.*;
import org.mmbase.module.core.*;
import org.mmbase.module.corebuilders.*;
import org.mmbase.storage.search.*;
import org.mmbase.storage.search.implementation.*;

import org.mmbase.util.logging.Logging;
import org.mmbase.util.logging.Logger;
import org.mmbase.util.SizeOf;

import org.mmbase.applications.mmbob.util.transformers.PostingBody;

/**
 * @javadoc
 * @author Daniel Ockeloen
 * @version $Id: PostThread.java,v 1.49 2007-10-18 09:54:40 michiel Exp $
 */
public class PostThread {

    static private final Logger log = Logging.getLoggerInstance(PostThread.class);

    private String subject;
    private String creator;
    private String state;
    private String mood;
    private String ttype;
    private int id;
    private int viewcount;
    private int postcount;

    private int lastposttime;
    private int lastpostnumber;
    private int lastposternumber;
    private String lastposter;
    private String lastpostsubject;

    private PostArea parent;
    private Vector<Posting> postings = null; // Vector because clone is used (but that is
                                             // questionable too)

    private int threadpos = 0;
    private List writers = new Vector(); // is synchronization needed?
    private PostingBody postingBody = new PostingBody();
    private boolean loaded = false;
    private int lastused;

    public PostThread(PostArea parent, Node node, boolean prefixwanted) {
        String prefix="";
        if (prefixwanted) {
            prefix = "postthreads.";
        }
	this.parent  = parent;
	this.subject = node.getStringValue(prefix + "subject");
	this.creator = node.getStringValue(prefix + "creator");
	this.id      = node.getIntValue(prefix + "number");
	this.viewcount = node.getIntValue(prefix + "viewcount");
	if (viewcount == -1) viewcount = 0;
	this.postcount = node.getIntValue(prefix + "postcount");
	this.state    = node.getStringValue(prefix + "state");
	if (postcount == -1) postcount = 0;

	lastpostsubject  = node.getStringValue(prefix + "c_lastpostsubject");
	lastposter       = node.getStringValue(prefix + "c_lastposter");
	lastposttime     = node.getIntValue(prefix + "c_lastposttime");
	lastposternumber = node.getIntValue(prefix + "lastposternumber");
	lastpostnumber   = node.getIntValue(prefix + "lastpostnumber");
	mood  = node.getStringValue(prefix + "mood");
	ttype = node.getStringValue(prefix + "ttype");
    }

    public void setId(int id) {
	this.id = id;
    }

    public int getId() {
	return id;
    }

    public String getSubject() {
	return subject;
    }

    public void setSubject(String subject) {
	this.subject = subject;
    }

    public String getState(Poster ap) {
	boolean isnew = true;
	int lastsessionend = ap.getLastSessionEnd();

	// was the post older than my last session ?
	if (lastposttime < lastsessionend) {
            isnew = false;
	}

	// of did i read it in this session ?
	if (ap.viewedThread(id, new Integer(lastposttime))) {
            isnew = false;
	}


	String state = getState();
	if (state.equals("normal")) {
            if (isnew) state = "normalnew";
	} else if (state.equals("hot")) {
            if (isnew) state = "hotnew";
	}

	// even extra lets see if im in this thread;
	if (isWriter(ap.getNick())) {
            state += "me";
	}

	return state;
    }

    public String getState() {
	// weird
	String staten = state;
	if (staten == null || staten.equals("")) {
            staten = "normal";
	}

	// figure out if its hot
	boolean hot = false;
	if (postcount > parent.getPostThreadCountAvg()) {
            hot = true;
	}

	if (staten.equals("normal") && hot) staten = "hot";

	return staten;
    }

    public void setState(String staten) {
	String oldstate=state;
	if (oldstate.equals("pinned") && !staten.equals("pinned")) parent.decPinnedCount();
	if (oldstate.equals("pinnedclosed") && !staten.equals("pinnedclosed")) parent.decPinnedCount();
	if (!oldstate.equals("pinned") && staten.equals("pinned")) parent.incPinnedCount();
	if (!oldstate.equals("pinnedclosed") && staten.equals("pinnedclosed")) parent.incPinnedCount();
	state = staten;
    }

    public void setMood(String mood) {
	this.mood = mood;
    }

    public void setType(String ttype) {
	this.ttype = ttype;
    }

    public String getMood() {
	if (mood==null || mood.equals("")) {
            return "normal";
	}
	return mood;
    }

    public String getType() {
	if (ttype == null || ttype.equals("")) {
            return "normal";
	}
	return ttype;
    }

    public String getCreator() {
	return creator;
    }

    public int getPostCount() {
	return postcount;
    }

    public int getViewCount() {
	return viewcount;
    }

    public String getLastPoster() {
	return lastposter;
    }

    public int getLastPosterNumber() {
        if (parent.getParent().hasPoster(lastposternumber)) {
            return lastposternumber;
        } else {
            return -1;
        }
    }

    public int getLastPostNumber() {
	return lastpostnumber;
    }

    public int getLastPostTime() {
	return lastposttime;
    }


    public String getLastSubject() {
	return lastpostsubject;
    }

    public void setLastSubject(String subject) {
	lastpostsubject = subject;
    }

    public Iterator<Posting> getPostings(int page,int pagecount) {
	if (postings == null) readPostings();

	lastused = (int)(System.currentTimeMillis() / 1000);

	// get the range we want
	int start = (page - 1) * pagecount;
        if (start < 0) start = 0;

	int end = page * pagecount;
	if (end > postcount) {
            end = postings.size();
	}
	List result;
	if (start < end) {
            result = postings.subList(start, end);
	} else {
            result = postings;
	}

	viewcount++;
	syncNode(ForumManager.SLOWSYNC);
	parent.signalViewsChanged(this);

	return result.iterator();
    }


    public Posting getPostingPos(int pos) {
	if (postings==null) readPostings();
	if (postings.size()>pos) {
            return postings.get(pos);
	}
	return null;
    }

    public boolean save() {
        Node node = ForumManager.getCloud().getNode(id);
	node.setValue("subject",subject);
	node.setValue("creator",creator);
	node.setIntValue("viewcount",viewcount);
	node.setIntValue("postcount",postcount);
	node.setValue("c_lastpostsubject",lastpostsubject);
	node.setValue("c_lastposter",lastposter);
	node.setIntValue("c_lastposttime",lastposttime);
	node.setIntValue("lastposternumber",lastposternumber);
	node.setIntValue("lastpostnumber",lastpostnumber);
	node.setValue("mood",mood);
	node.setValue("state",state);
	node.setValue("ttype",ttype);
        node.commit();
	parent.resort(this);
        return true;
    }


    public int postReply(String nsubject,Poster poster,String nbody,boolean parsed) {
	if (postings == null) readPostings();

        NodeManager nm=  ForumManager.getCloud().getNodeManager("postings");
        if (nm != null) {
            Node pnode = nm.createNode();
            if (subject != null && !subject.equals("")) {
                pnode.setStringValue("subject", subject);
            } else {
                pnode.setStringValue("subject", nsubject);
            }
            if (poster!=null) {
                pnode.setStringValue("c_poster", poster.getNick());
                pnode.setIntValue("posternumber", poster.getId());
            } else {
                pnode.setStringValue("c_poster", "gast"); // TODO 'gast' is dutch.
                pnode.setValue("posternumber", null);
            }

            // This must be it, please do not change the line below. If somebody's having problems
            // with it please contact me <gvenk@xs4all.nl> to discuss the problems!
            if (parsed) {
                pnode.setStringValue("body", nbody);
            } else {
                pnode.setStringValue("body", "<posting>" + postingBody.transform(nbody) + "</posting>");
            }

            pnode.setStringValue("c_body", "");
            pnode.setIntValue("createtime", (int)(System.currentTimeMillis()/1000));
            // can be arranged by Datatypes.

            pnode.setIntValue("edittime", -1);
            // -1 is a strange time

            pnode.commit();
            Node node = ForumManager.getCloud().getNode(id);
            RelationManager rm = ForumManager.getCloud().getRelationManager("postthreads", "postings", "related");
            if (rm != null) {
                Node rel = rm.createRelation(node, pnode);
                rel.commit();
                Posting posting = new Posting(pnode, this, false);
                posting.setThreadPos(threadpos++);
                postings.add(posting);

                // update stats and signal parent of change
                if (poster != null) poster.addPostCount();
                addWriter(posting);

                // update the counters
                postcount++;

                lastposttime     = pnode.getIntValue("createtime");
                lastposter       = pnode.getStringValue("c_poster");
                lastposternumber = pnode.getIntValue("posternumber");
                lastpostnumber   = pnode.getIntValue("number");
                lastpostsubject  = pnode.getStringValue("subject");

                syncNode(ForumManager.FASTSYNC);
                parent.signalNewReply(this);
                // signal the threadobservers
                ThreadObserver to = parent.getParent().getThreadObserver(id);
                if (to != null) to.signalContentAdded(this);

                return pnode.getNumber();
            } else {
                log.error("Forum can't load relation nodemanager postthreads/postings/related");
                return -1;
            }
        } else {
            log.error("Forum can't load postings nodemanager");
            return -1;
        }
    }

    /**
     * add the postarea-node to the given syncQueue
     * @param queue syncQueue that must be used
     */
    private void syncNode(int queue) {
        Node node = ForumManager.getCloud().getNode(id);
        node.setIntValue("postcount", postcount);
        node.setIntValue("viewcount", viewcount);
        node.setIntValue("lastposternumber",lastposternumber);
        node.setIntValue("lastpostnumber", lastpostnumber);
        node.setIntValue("c_lastposttime", lastposttime);
        node.setStringValue("c_lastposter", lastposter);
        node.setStringValue("c_lastpostsubject", lastpostsubject);

        ForumManager.syncNode(node,queue);
    }

    /**
     * Fill the postings vector with all Postings within the PostThread
     */
    public void readPostings() {
	if (postings!=null) return;
        postings = new Vector<Posting>(); //synchronized? . Also: Vector is cloneable. needed somewhere.
        long start=System.currentTimeMillis();
        //NodeIterator i=node.getRelatedNodes("postings").nodeIterator();


        NodeManager postthreadsmanager = ForumManager.getCloud().getNodeManager("postthreads");
        NodeManager postingsmanager = ForumManager.getCloud().getNodeManager("postings");
        Query query = ForumManager.getCloud().createQuery();
        Step step1 = query.addStep(postthreadsmanager);
        RelationStep step2 = query.addRelationStep(postingsmanager);
        StepField f1 = query.addField(step1, postthreadsmanager.getField("number"));
        StepField f3 = query.addField(step2.getNext(), postingsmanager.getField("number"));
        query.addField(step2.getNext(), postingsmanager.getField("c_body"));
        query.addField(step2.getNext(), postingsmanager.getField("body"));
        query.addField(step2.getNext(), postingsmanager.getField("c_poster"));
        query.addField(step2.getNext(), postingsmanager.getField("subject"));
        query.addField(step2.getNext(), postingsmanager.getField("createtime"));
        query.addField(step2.getNext(), postingsmanager.getField("edittime"));
        query.setConstraint(query.createConstraint(f1, new Integer(getId())));
        query.addSortOrder(f3, SortOrder.ORDER_ASCENDING);

        NodeIterator i = ForumManager.getCloud().getList(query).nodeIterator();
        long end = System.currentTimeMillis();
        int newcount = 0;
        //log.info("getting list="+(end-start));
        while (i.hasNext()) {
            Node node=i.nextNode();
            //start=System.currentTimeMillis();
            Posting posting = new Posting(node,this,true);
            newcount++;
            //end=System.currentTimeMillis();
            posting.setThreadPos(threadpos++);
            addWriter(posting);
            postings.add(posting);
        }

        // check the count number
        if (postcount != newcount) {
            log.info("resync of posttreadcount : " + postcount + " " + newcount);
            postcount = newcount;
            save();
        }

        // very raw way to zap the cache
        // but WHY?!
        log.info("Clearing _All_ mmbase caches!!");
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
	loaded = true;
	lastused = (int)(System.currentTimeMillis() / 1000);
    }

    public boolean isLastPage(int page,int pagesize) {

	int pagecount=postcount/pagesize;
	if ((pagecount*pagesize)!=postcount) pagecount++;
	if (page==pagecount) {
            return true;
	}
	return false;
    }

    public int getPageCount(int pagesize) {
	int pagecount=postcount/pagesize;
	if ((pagecount*pagesize)!=postcount) pagecount++;
	return pagecount;
    }

    /**
     * I hate how this is done but don't see a way to get this fast enough
     * any other way.
     */
    public String getNavigationLine(String baseurl, int page, int pagesize, int overflowpage, String cssclass) {
	int f = parent.getParent().getId();
	int a = parent.getId();
	int p = getId();

	if (!cssclass.equals("")) {
            cssclass = " class=\"" + cssclass + "\"";
	}

	// weird way must be a better way for pagecount
	int pagecount = postcount / pagesize;
	if ((pagecount * pagesize) != postcount) pagecount++;


	int c = page - 1;
	if (c < 1) c = 1;
	int n = page + 1;
	if (n > pagecount) n = pagecount;
	String result = "<a href=\"" + baseurl + "?forumid=" + f + "&postareaid=" + a + "&postthreadid=" + p + "&page=" + c + "\"" + cssclass + ">&lt</a>";
        // TODO & is not valid XML, hence not XHTML

	int i = 1;
	for (i = 1; i <= pagecount; i++) {
            // TODO should use StringBuilder?
            result +=" <a href=\"" + baseurl + "?forumid=" + f + "&postareaid=" + a + "&postthreadid=" + p + "&page=" + i + "\"" + cssclass + ">";
            if (i < overflowpage || i == pagecount) {
	  	if (i==page) {
                    result += "[" + i + "]";
	  	} else {
                    result += "" + i;
	  	}
            } else {
		if (i == overflowpage) {
                    result += "...";
		}
            }
            result += "</a>";
        }
        String lastword = "last";
        if (parent.getParent().getLanguage().equals("nl")) lastword = "laatste";
	result +=
            " <a href=\"" + baseurl + "?forumid=" + f + "&postareaid=" + a + "&postthreadid=" + p +
            "&page=" + (i - 1) + "#reply\"" + cssclass + "> " + lastword + "</a>";
	result += " <a href=\"" + baseurl + "?forumid=" + f + "&postareaid=" + a + "&postthreadid=" + p + "&page=" + n + "\"" + cssclass + ">&gt</a>";
	return result;
    }


    public String getNavigationLine(String baseurl, int pagesize,int overflowpage,String cssclass) {
	int f = parent.getParent().getId();
	int a = parent.getId();
	int p = getId();

	if (cssclass != null && ! cssclass.equals("")) {
            cssclass = " class=\"" + cssclass + "\"";
	}

	// weird way must be a better way for pagecount
	int pagecount=postcount/pagesize;
	if ((pagecount*pagesize)!=postcount) pagecount++;

	// if only one page no nav line is needed
	if (pagecount==1) return "";

	String result = "(";
	int i=1;
	for (i=1;i<=pagecount;i++) {
            if (i!=1) result+=" ";
            if (i<overflowpage || i==pagecount) {
	  	result+="<a href=\""+baseurl+"?forumid="+f+"&postareaid="+a+"&postthreadid="+p+"&page="+i+"\""+cssclass+">"+i+"</a>";
            } else {
		if (i==overflowpage) {
                    result+="...";
		}
            }
        }
	if (i>1) {
            String lastword = "last";
            if (parent.getParent().getLanguage().equals("nl")) lastword="laatste";
            result+="<a href=\""+baseurl+"?forumid="+f+"&postareaid="+a+"&postthreadid="+p+"&page="+(i-1)+"#reply\""+cssclass+"> "+lastword+"</a>";
	}
	result += ")";
	return result;
    }

    public Posting getPosting(int postingid) {
	if (postings == null) readPostings();

        for (Posting p : postings) {
            if(p.getId() == postingid) {
                return p;
            }
	}
	return null;
    }

    /**
     * remove the whole PostThread
     * @return <code>true</code> if the removal was successful
     */
    public boolean remove() {
        if (postings == null) readPostings();

        // need to clone the vector, because the postings change while we're removing the thread
        // MM: really?
        // EB: as in Form.remove(): there all posters are iterated over and removed through childRemove()
        // so a concurrentHashMap could be used here as well.
        Vector v = (Vector) postings.clone();
        Enumeration e = v.elements();


        // remove the postings
        //when all postings have been removed this thread will be removed as well.
        //see childRemove();

        while (e.hasMoreElements()) {
            Posting posting = (Posting) e.nextElement();
            log.debug("try to remove posting: " + posting.getId());
            if (!posting.remove()) {
                log.error("Can't remove Posting : " + posting.getId());
                return false;
            }
        }

        //        Node node = null;
//        try {
//            // this is broken why doesn't ForumManager.nodeDelete, delete the
//            // node ??? Daniel.
//            // it should already been deleted by the above loop this is done in
//            // case of a 0 postings
//            if (ForumManager.getCloud().hasNode(id)) {
//                node = ForumManager.getCloud().getNode(id);
//
//            }
//        } catch (Exception e2) {
//            // e2.printStackTrace();
//            // return false;
//        }
//
//        if (node != null) {
//            log.debug("PostThread node " + id + " exists (as it should, and will be deleted");
//            node.delete(true);
//            ForumManager.nodeDeleted(node);
//        } else {
//            log.debug("PostThread node " + id + " dous not exist!");
//        }

        return true;
    }

    /**
     * add the accountname/nick of the Poster of the given Posting in the Posthread to the writers vector
     * @param p Posting
     */
    public void addWriter(Posting p) {
        if (!writers.contains(p.getPoster())) writers.add(p.getPoster());
    }

    /**
     * determine if the given accountname/nick is a writer in this PostThread
     * @param asker accountname/nick to be evaluated
     * @return <code>true</code> if the accountname/nick is a writer in this thread. <code>false</code> if he isn't.
     */
    public boolean isWriter(String asker) {
        if (writers.contains(asker)) return true;
        return false;
    }

    /**
     * signal that a child (posting) has been
     * removed inside this postthread.
     *
     * @param p posting that has been removed
     */
    public void childRemoved(Posting p) {
        if (postings == null) {
            readPostings();
        }
        postings.remove(p);
        postcount--;

        // if it was the last post that was removed, replace the lastpostsubject
        // with a remove-message.
        if (postings.size() > 0 && lastposttime == p.getPostTime() && lastposter.equals(p.getPoster())) {
            Posting op = postings.lastElement();
            if (op != null) {
                lastpostsubject = op.getSubject();
                lastposter = op.getPoster();
                lastposttime = op.getPostTime();
            }
        }

        if (postings.size() == 0) {
            log.debug("Postthread: removing whole thread");
            Node node = ForumManager.getCloud().getNode(id);
            ForumManager.nodeDeleted(node);
            node.delete(true);
            parent.childRemoved(this);
        } else {
            log.debug("Postthread: removing just a reply from the thread");
            syncNode(ForumManager.FASTSYNC);
        }

        // Also signal the parent PostArea to decrease it's postcount etc ..
        parent.signalRemovedReply(this);
    }

    public PostArea getParent() {
	return parent;
    }

    public int getLastUsed() {
	return lastused;
    }

    public boolean isLoaded() {
	return loaded;
    }

    public void setLoaded(boolean loaded) {
	this.loaded = loaded;
    }

    public void swapOut() {
	postings = null;
	loaded = false;
    }


    public int getMemorySize() {
        if (postings == null) {
            return 0;
	} else {
            int size = 0;
            for (Posting p : postings) {
                size += p.getMemorySize();
            }
            return size;
	}
    }


    public List<Posting> searchPostings(String searchkey, int posterid) {
	List<Posting> results = new Vector<Posting>(); // synchronized?
	return searchPostings(results, searchkey, posterid);
    }

    public List<Posting> searchPostings(List results, String searchkey, int posterid) {
	if (postings != null) {
            for (Posting posting : postings) {
	    	if (posting.inBody(searchkey) || posting.inSubject(searchkey)) {
                    results.add(posting);
	    	}
            }
	}
	return results;
    }
}

