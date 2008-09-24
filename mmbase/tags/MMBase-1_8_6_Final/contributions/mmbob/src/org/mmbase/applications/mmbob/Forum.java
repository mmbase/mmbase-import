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
import java.util.concurrent.ConcurrentHashMap;
import java.io.*;

import org.mmbase.util.*;
import org.mmbase.module.core.*;
import org.mmbase.module.corebuilders.*;

import org.mmbase.bridge.*;
import org.mmbase.cache.*;
import org.mmbase.storage.search.*;
import org.mmbase.storage.search.implementation.*;

import org.mmbase.util.logging.Logging;
import org.mmbase.util.logging.Logger;

/**
 * @javadoc
 * @author Daniel Ockeloen
 * @version $Id: Forum.java,v 1.66 2007-10-18 09:54:26 michiel Exp $
 */
public class Forum {

    static private final Logger log = Logging.getLoggerInstance(Forum.class);

    private String name;
    private String description;
    private String administratorsline;
    private int id, totalusers, totalusersnew;
    private Node node;

    private int viewcount;
    private int postcount;
    private int postthreadcount = -1;
    private int lastposternumber;
    private int lastpostnumber;

    private int lastposttime;
    private String lastposter;
    private String lastpostsubject;

    private Map<String, Poster> administrators = new Hashtable(); // synchronized?

    private Map<String, PostArea> postareas = new Hashtable<String, PostArea>(); // synchronized?
    private Map<String, String> filterwords;
    private SubArea subareas = new SubArea();

    private Map<Integer, Poster> posters = new ConcurrentHashMap<Integer, Poster>();
    private Map<String, Poster> posternames = new Hashtable<String, Poster>(); // synchronized?
    // private Hashtable posternicknames = new Hashtable();
    private Vector onlineposters = new Vector();
    private Vector newposters = new Vector();
    private HashMap threadobservers = new HashMap();

    private ForumConfig config;

    /**
     * Constructor
     *
     * @param node forum node
     */
    public Forum(Node node) {
        this.node = node;
        this.name = node.getStringValue("name");
        this.description = node.getStringValue("description");
        this.id = node.getNumber();

        this.viewcount = node.getIntValue("viewcount");
        if (viewcount == -1) { // wtf
            viewcount = 0;
        }
        this.postcount = node.getIntValue("postcount");
        if (postcount == -1) { // wtf
            postcount = 0;
        }
        this.postthreadcount = node.getIntValue("postthreadcount");
        if (postthreadcount == -1) { // wtf
            postthreadcount = 0;
        }

        this.lastpostsubject = node.getStringValue("c_lastpostsubject");
        this.lastposter = node.getStringValue("c_lastposter");
        this.lastposttime = node.getIntValue("c_lastposttime");
        this.lastposternumber = node.getIntValue("lastposternumber");
        this.lastpostnumber = node.getIntValue("lastpostnumber");

        // get out config node
        config = ForumManager.getForumConfig(name);
        ExternalProfilesManager.loadExternalHandlers(this);
        // read postareas
        ThreadPools.jobsExecutor.execute(new Runnable() {
                public void run() {
                    preCachePosters();
                    readSignatures();
                    readProfiles();
                    readRoles();
                    readAreas();
                    if (getNavigationMethod().equals("tree")) {
                        syncTreeAreas();
                    }
                    readThreadObservers();
                    readFieldaliases();
                }
            }
            );
    }

    public void resetConfig() {
        config = ForumManager.getForumConfig(name);
    }

    /**
     * Set the MMBase objectnumber of the forum
     *
     * @param id MMase objectnumber
     */
    public void setId(int id) {
        this.id = id;
    }

    /**
     * Set forum to node
     *
     * @param node forumnode
     */
    public void setNode(Node node) {
        this.node = node;
    }

    /**
     * Set name of the forum
     *
     * @param name
     */
    public void setName(String name) {
        this.name = name;
        node.setValue("name", name);
    }

    /**
     * Set the language of the forum
     *
     * @param language
     */
    public void setLanguage(String language) {
        node.setValue("language", language);
    }

    /**
     * Get the language of the forum
     *
     * @return the language
     */
    public String getLanguage() {
        return node.getStringValue("language");
    }

    /**
     * Set the description of the forum
     *
     * @param description
     */
    public void setDescription(String description) {
        this.description = description;
        node.setValue("description", description);
    }

    /**
     * get the name of the forum
     *
     * @return name of the forum
     */
    public String getName() {
        return name;
    }

    /**
     * get the description of the forum
     *
     * @return description of the forum
     */
    public String getDescription() {
        return description;
    }

    /**
     * Get the MMBase objectnumber of the forum
     *
     * @return MMBase objectnumber of the forum
     */
    public int getId() {
        return id;
    }

    /**
     * get the number of posts on the forum
     *
     * @return number of posts on the furum
     */
    public int getPostCount() {
        return postcount;
    }

    /**
     * get the number of views on the forum
     *
     * @return number of views
     */
    public int getViewCount() {
        return viewcount;
    }

    /**
     * get the accountname / nick of the last poster on the forum
     *
     * @return accountname / nick of the last poster on the forum
     */
    public String getLastPoster() {
        return lastposter;
    }

    /**
     * get last poster on the forum
     *
     * @return poster id
     */
    public int getLastPosterNumber() {
        return lastposternumber;
    }

    /**
     * get the date/time (Epoch) of the last post on the forum
     *
     * @return date/time (Epoch) of the last post on the forum
     */
    public int getLastPostTime() {
        return lastposttime;
    }

    /**
     * get the subject of the last post on the forum
     *
     * @return subject of the last post on the forum
     */
    public String getLastSubject() {
        return lastpostsubject;
    }

    public void setLastSubject(String subject) {
        lastpostsubject = subject;
    }

    /**
     * "Save" the forum (add it to the syncQueue)
     *
     * @return <code>true</code>
     */
    public boolean save() {
        syncNode(ForumManager.FASTSYNC);
        return true;
    }

    public boolean saveConfig() {
        ForumManager.saveConfig();
        if (getNavigationMethod().equals("tree"))
            syncTreeAreas();
        return true;
    }

    /**
     * "Save direct" the forum
     *
     * @return <code>true</code>
     */
    public boolean saveDirect() {
        node.commit();
        return true;
    }

    /**
     * add the forum-node to the given syncQueue
     *
     * @param queue syncQueue that must be used
     */
    private void syncNode(int queue) {
        node.setIntValue("postcount", postcount);
        node.setIntValue("postthreadcount", postthreadcount);
        node.setIntValue("viewcount", viewcount);
        node.setIntValue("c_lastposttime", lastposttime);
        node.setStringValue("c_lastposter", lastposter);
        node.setStringValue("c_lastpostsubject", lastpostsubject);
        node.setIntValue("lastposternumber", lastposternumber);
        node.setIntValue("lastpostnumber", lastpostnumber);
        ForumManager.syncNode(node, queue);
    }

    /**
     * get the administrators of the forum
     *
     * @return administrators
     */
    public Enumeration<Poster> getAdministrators() {
        return Collections.enumeration(administrators.values());
    }

    public Enumeration<Poster> getNonAdministrators(String searchkey) {
        Vector<Poster> result = new Vector<Poster>(); // wtf
        Enumeration<Poster> e = getPosters();
        while (e.hasMoreElements()) {
            Poster p = e.nextElement();
            if (!isAdministrator(p.getNick())) {
                String account = p.getNick().toLowerCase();
                String firstname = p.getFirstName().toLowerCase();
                String lastname = p.getLastName().toLowerCase();
                if (searchkey.equals("*") || account.indexOf(searchkey) != -1 || firstname.indexOf(searchkey) != -1
                        || lastname.indexOf(searchkey) != -1) {
                    result.add(p);
                    if (result.size() > 49) {
                        return result.elements();
                    }
                }
            }
        }
        return result.elements();
    }

    /**
     * get the posters of the forum
     *
     * @return posters
     */
    public Enumeration<Poster> getPosters() {
        return Collections.enumeration(posters.values());
    }

    public List searchPostings(String searchkey, int posterid) {
        List results = new Vector(); // synchronized?
        for (PostArea area : postareas.values()) {
            results = area.searchPostings(results, searchkey, posterid);
        }
        return results;
    }

    public PostThread getPostThread(String postthreadid) {
        for (PostArea area : postareas.values()) {
            PostThread pt = area.getPostThread(postthreadid);
            if (pt != null) {
                return pt;
            }
        }
        return null;
    }

    /**
     * get the posters that are online
     *
     * @return online posters
     */
    public Enumeration getPostersOnline() {
        return onlineposters.elements();
    }

    /**
     * determine if the given account is an administrator
     *
     * @param nick
     * @return <code>true</code> if the account is an administrator
     */
    public boolean isAdministrator(String nick) {
        return administrators.containsKey(nick);
    }

    /**
     * get the online administrators (comma-seperated)
     *
     * @param baseurl
     * @return comma-seperated list of administrators-accounts for this forum. If the passed baseurl isn't empty it will
     *         make html-links for you
     */
    public String getAdministratorsLine(String baseurl) {
        if (administratorsline != null)
            return administratorsline;
        administratorsline = "";
        for (Poster p : administrators.values()) {
            if (!administratorsline.equals(""))
                administratorsline += ",";
            if (baseurl.equals("")) {
                administratorsline += p.getNick();
            } else {
                administratorsline += "<a href=\"" + baseurl + "?forumid=" + getId() + "&posterid=" + p.getId() + "\">" + p.getNick()
                        + "</a>";
            }
        }
        return administratorsline;
    }

    /**
     * get a postarea of this forum by it's MMbase objectnumber
     *
     * @param id MMbase objectnumber of the postarea
     */
    public PostArea getPostArea(String id) {
        Object o = postareas.get(id);
        if (o != null) {
            return (PostArea) o;
        }
        return null;
    }

    public SubArea getSubArea(String name) {
        if (name.equals("root")) {
            return subareas;
        } else {
            Iterator i = subareas.getSubAreas();
            while (i.hasNext()) {
                SubArea sa = (SubArea) i.next();
                if (sa.getName().equals(name)) {
                    return sa;
                }
            }
            return subareas;
        }
    }

    /**
     * remove a postarea of this forum by it's MMbase objectnumber
     *
     * @param id MMbase objectnumber of the postarea
     * @return Feedback. <code>true</code> if the action was successful, <code>false</code> if it wasn't
     */
    public boolean removePostArea(String id) {
        PostArea a = postareas.get(id);
        if (a != null) {
            if (a.remove()) {
                postareas.remove(id);
                if (getNavigationMethod().equals("tree"))
                    syncTreeAreas();
                return true;
            }
        } else {
            log.error("trying to delete a unknown postarea");
        }
        return false;
    }

    /**
     * remove a folder(mailbox) for a poster
     *
     * @param posterid
     * @param foldername
     * @return <code>true</code> if the action was successful
     */
    public boolean removeFolder(int posterid, String foldername) {
        Poster poster = getPoster(posterid);
        if (poster != null) {
            return poster.removeMailbox(foldername);
        }
        return false;
    }

    /**
     * remove a poster from the onlineposters-Vector
     *
     * @param p posternode
     */
    public void removeOnlinePoster(Poster p) {
        onlineposters.remove(p);
    }

    /**
     * get the number of postareas for this forum
     *
     * @return number of postareas for this forum
     */
    public int getPostAreaCount() {
        return postareas.size();
    }

    /**
     * get all the postareas of this forum
     *
     * @todo This is unordered.
     * @return postareas
     */
    public Enumeration<PostArea> getPostAreas() {
        return Collections.enumeration(postareas.values());
    }

    /**
     * add a poster to the onlineposters-Vector
     *
     * @param p new online poster
     */
    public void newPosterOnline(Poster p) {
        if (!onlineposters.contains(p)) {
            onlineposters.add(p);
        }
    }

    /**
     * add a poster to the newposters-Vector
     *
     * @param p new poster
     */
    public void newPoster(Poster p) {
        if (!newposters.contains(p)) {
            newposters.add(p);
        }
    }

    /**
     * create a new postarea for this forum
     *
     * @param name name of the new postarea
     * @param description description of the new postarea
     * @return MMBase objectnumber for the newly created postarea
     */
    public int newPostArea(String name, String description) {
        NodeManager nm = ForumManager.getCloud().getNodeManager("postareas");
        if (nm != null) {
            Node anode = nm.createNode();
            anode.setStringValue("name", name);
            anode.setStringValue("description", description);
            anode.commit();

            RelationManager rm = ForumManager.getCloud().getRelationManager("forums", "postareas", "forarearel");
            if (rm != null) {
                Node rel = rm.createRelation(node, anode);
                rel.commit();
                PostArea area = new PostArea(this, anode);
                postareas.put("" + anode.getNumber(), area);
                if (getNavigationMethod().equals("tree"))
                    syncTreeAreas();
                return anode.getNumber();
            } else {
                log.error("Forum can't load relation nodemanager forums/postareas/forarearel");
            }
        } else {
            log.error("Forum can't load postareas nodemanager");
        }
        return -1;
    }

    /**
     * Called on construction Fill the postareas-Hashtable
     */
    private void readAreas() {
        long start = System.currentTimeMillis();
        if (node != null) {
            NodeIterator i = node.getRelatedNodes("postareas").nodeIterator();
            int newcount = 0;
            int newthreadcount = 0;
            while (i.hasNext()) {
                Node node2 = i.nextNode();
                PostArea area = new PostArea(this, node2);
                newcount += area.getPostCount();
                newthreadcount += area.getPostThreadCount();
                postareas.put("" + node2.getNumber(), area);
            }

            // check the count number
            if (postcount != newcount) {
                log.info("resync of postforumcount : " + postcount + " " + newcount);
                postcount = newcount;
                save();
            }

            // check the threadcount number
            if (postthreadcount != newthreadcount) {
                log.info("resync of postforumtreadcount : " + postthreadcount + " " + newthreadcount);
                postthreadcount = newthreadcount;
                save();
            }
        }
        long end = System.currentTimeMillis();
    }

    public void syncTreeAreas() {
        subareas = new SubArea();
        for (PostArea a : postareas.values()) {
            subareas.insert(a, a.getName());
        }
    }

    /**
     * get the total number of postthreads in this forum
     *
     * @return number of postthreads
     */
    public int getPostThreadCount() {
        // if (postthreadcount==-1) recalcPostThreadCount();
        return postthreadcount;
    }

    /**
     * Recalculates the private variabele "postcount" which contains the total number of posts in the forum <p/> ***
     * This method is not used ***
     */
    private void recalcPostCount() {
        int count = 0;
        for (PostArea a : postareas.values()) {
            count += a.getPostCount();
        }
        postcount = count;
    }

    /**
     * Recalculates the private variabele "postthreadcount" which contains the total number of postthreads in the forum
     * <p/> *** This method is not used ***
     */
    private void recalcPostThreadCount() {
        int count = 0;
        for (PostArea a : postareas.values()) {
            count += a.getPostThreadCount();
        }
        postthreadcount = count;
    }

    /**
     * Well, what does this do, then?
     */
    public void leafsChanged() {
    // recalcPostCount();
    // recalcPostThreadCount();
    }

    /**
     * signal the forum that there is a new reply updates the postcount, lastposttime, lastposter, lastpostsubject of
     * ths forum, and places it in the syncQueue
     *
     * @param child PostArea
     */
    public void signalNewReply(PostArea child) {
        postcount++;
        lastposttime = child.getLastPostTime();
        lastposter = child.getLastPoster();
        lastpostsubject = child.getLastSubject();
        lastposternumber = child.getLastPosterNumber();
        lastpostnumber = child.getLastPostNumber();
        syncNode(ForumManager.FASTSYNC);
    }

    /**
     * signal the forum that a reply was removed updates the postcount, lastposttime, lastposter, lastpostsubject of ths
     * forum, and places it in the syncQueue
     *
     * @param child PostArea
     */
    public void signalRemovedReply(PostArea child) {
        // todo: Make this configurable.
        // uncomment this if you want to decrease the stats if a message was removed
        // postcount--;

        if (lastposttime == child.getLastPostTime() && lastposter.equals(child.getLastPoster())) {
            lastpostsubject = "removed";
        }

        lastposttime = child.getLastPostTime();
        lastposter = child.getLastPoster();

        syncNode(ForumManager.FASTSYNC);
    }

    /**
     * signal the forum that there is a new postthread updates the postthreadcount , and places it in the syncQueue
     *
     * @param child PostArea
     */
    public void signalNewPost(PostArea child) {
        postthreadcount++;
        syncNode(ForumManager.FASTSYNC);
    }

    /**
     * signal the forum that there was a postthread removed updates the postthreadcount , and places it in the syncQueue
     *
     * @param child PostArea
     */
    public void signalRemovedPost(PostArea child) {
        // todo: Make this configurable.
        // uncomment this if you want to decrease the stats if a thread was removed
        // postthreadcount--;
        syncNode(ForumManager.FASTSYNC);
    }

    /**
     * signal the forum that there's a new view
     *
     * @param child PostArea
     */
    public void signalViewsChanged(PostArea child) {
        viewcount++;
        syncNode(ForumManager.SLOWSYNC);
    }

    /**
     * get a poster of this forum by it's accountname/nick (???)
     *
     * @param posterid accountname/nick
     * @return Poster <code>null</code> if the account was not found
     */
    public Poster getPoster(String posterid) {
        return posternames.get(posterid);
    }

    public Poster getPosterNick(String nick) {
        for (Poster p : posters.values()) {
            ProfileEntry pe = p.getProfileValue("nick");
            if (pe != null && pe.getValue().equals(nick)) {
                return p;
            }
        }
        // not found then try normal account
        return getPoster(nick);
    }

    /**
     * get a poster of this forum by it's MMBase Objectnumber
     *
     * @param posterid MMBase Objectnumber of the poster
     * @return Poster <code>null</code> if the poster was not found
     */
    public Poster getPoster(int posterid) {
        Poster p = posters.get(Integer.valueOf(posterid));
        if (p != null) {
            return p;
        } else {
            // MM: I'm not entirely sure that it is acceptable that any poster node can be poster of
            // any forum.
            // But something like this is needed in Didactor.
            if (node != null && node.getCloud().hasNode(posterid)) {
                Node posterNode = node.getCloud().getNode(posterid);
                if (posterNode.getNodeManager().getName().equals("posters")) {
                    p = new Poster(posterNode, this, false);
                    posters.put(Integer.valueOf(posterid), p);
                    posternames.put(p.getNick(), p);
                    return p;
                } else {
                    log.warn("Node " + posterNode + " is not a node of type 'posters' (but a " + posterNode.getNodeManager().getName());
                }
            }
        }
        return null;
    }

    public boolean hasPoster(int posterid) {
        if (posters.containsKey(Integer.valueOf(posterid))) {
            return true;
        }
        return false;
    }

    /**
     * get the total number of posters in the forum
     *
     * @return number of posters in the forum
     */
    public int getPostersTotalCount() {
        return totalusers;
    }

    /**
     * get the number of online posters for the forum
     *
     * @return number of online posters
     */
    public int getPostersOnlineCount() {
        return onlineposters.size();
    }

    /**
     * get the number of new posters for the forum
     *
     * @return number of new posters
     */
    public int getPostersNewCount() {
        return newposters.size();
    }

    /**
     * Called on construction Fill the posters, posternames, totalusers and onlineposters etc ... <p/> this is all wrong
     * should be replaced way to much mem to read them all.
     */
    private void preCachePosters() {
        if (node != null) {
            totalusers = 0;
            totalusersnew = 0;
            int onlinetime = ((int) (System.currentTimeMillis() / 1000)) - (getPosterExpireTime());
            int newtime = ((int) (System.currentTimeMillis() / 1000)) - (24 * 60 * 60 * 7);

            NodeManager forumsmanager = ForumManager.getCloud().getNodeManager("forums");
            NodeManager postersmanager = ForumManager.getCloud().getNodeManager("posters");
            Query query = ForumManager.getCloud().createQuery();
            Step step1 = query.addStep(forumsmanager);
            RelationStep step2 = query.addRelationStep(postersmanager);
            StepField f1 = query.addField(step1, forumsmanager.getField("number"));
            query.addField(step2.getNext(), postersmanager.getField("number"));
            // query.addField(step2.getNext(), postersmanager.getField("state"));
            query.addField(step2.getNext(), postersmanager.getField("account"));
            query.addField(step2.getNext(), postersmanager.getField("password"));
            query.addField(step2.getNext(), postersmanager.getField("firstname"));
            query.addField(step2.getNext(), postersmanager.getField("lastname"));
            query.addField(step2.getNext(), postersmanager.getField("email"));
            query.addField(step2.getNext(), postersmanager.getField("postcount"));
            query.addField(step2.getNext(), postersmanager.getField("level"));
            query.addField(step2.getNext(), postersmanager.getField("location"));
            query.addField(step2.getNext(), postersmanager.getField("gender"));
            query.addField(step2.getNext(), postersmanager.getField("firstlogin"));
            query.addField(step2.getNext(), postersmanager.getField("lastseen"));

            query.setConstraint(query.createConstraint(f1, Integer.valueOf(node.getNumber())));

            NodeIterator i = ForumManager.getCloud().getList(query).nodeIterator();
            while (i.hasNext()) {
                Node node = i.nextNode();
                // long start = System.currentTimeMillis();
                Poster p = new Poster(node, this, true);
                // long end = System.currentTimeMillis();
                posters.put(Integer.valueOf(p.getId()), p);
                posternames.put(p.getNick(), p);
                totalusers++;
                if (p.getLastSeen() > onlinetime) {
                    onlineposters.add(p);
                }
                if (p.getFirstLogin() == -1 || p.getFirstLogin() > newtime) {
                    newPoster(p);
                }
            }
        }

        // very raw way to zap the cache
        log.service("Clearing _All_ MMBase caches! ");
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

    private void readSignatures() {
        if (node != null) {
            NodeManager forumsmanager = ForumManager.getCloud().getNodeManager("forums");
            NodeManager postersmanager = ForumManager.getCloud().getNodeManager("posters");
            NodeManager signaturesmanager = ForumManager.getCloud().getNodeManager("signatures");
            Query query = ForumManager.getCloud().createQuery();
            Step step1 = query.addStep(forumsmanager);
            RelationStep step2 = query.addRelationStep(postersmanager);
            RelationStep step3 = query.addRelationStep(signaturesmanager);

            StepField f1 = query.addField(step1, forumsmanager.getField("number"));
            query.addField(step2.getNext(), postersmanager.getField("account"));
            query.addField(step3.getNext(), signaturesmanager.getField("number"));
            query.addField(step3.getNext(), signaturesmanager.getField("body"));
            query.addField(step3.getNext(), signaturesmanager.getField("mode"));
            query.addField(step3.getNext(), signaturesmanager.getField("encoding"));

            query.setConstraint(query.createConstraint(f1, Integer.valueOf(node.getNumber())));

            NodeIterator i = ForumManager.getCloud().getList(query).nodeIterator();
            while (i.hasNext()) {
                Node node = i.nextNode();
                Poster poster = getPoster(node.getStringValue("posters.account"));
                if (poster != null) {
                    Signature sig = new Signature(poster, node.getIntValue("signatures.number"), node.getStringValue("signatures.body"),
                            node.getStringValue("signatures.mode"), node.getStringValue("signatures.encoding"));
                    poster.addSignature(sig);
                } else {
                    log.error("Got a signature of a missing poster !" + node.getStringValue("posters.account"));
                }
            }
        }
    }

    private void readProfiles() {
        if (node != null) {
            NodeManager forumsmanager = ForumManager.getCloud().getNodeManager("forums");
            NodeManager postersmanager = ForumManager.getCloud().getNodeManager("posters");
            NodeManager profilesmanager = ForumManager.getCloud().getNodeManager("profileinfo");
            Query query = ForumManager.getCloud().createQuery();
            Step step1 = query.addStep(forumsmanager);
            RelationStep step2 = query.addRelationStep(postersmanager);
            RelationStep step3 = query.addRelationStep(profilesmanager);

            StepField f1 = query.addField(step1, forumsmanager.getField("number"));
            query.addField(step2.getNext(), postersmanager.getField("account"));
            query.addField(step3.getNext(), profilesmanager.getField("number"));
            query.addField(step3.getNext(), profilesmanager.getField("xml"));
            query.addField(step3.getNext(), profilesmanager.getField("external"));
            query.addField(step3.getNext(), profilesmanager.getField("synced"));

            query.setConstraint(query.createConstraint(f1, Integer.valueOf(node.getNumber())));

            NodeIterator i = ForumManager.getCloud().getList(query).nodeIterator();
            while (i.hasNext()) {
                Node node = i.nextNode();
                Poster poster = getPoster(node.getStringValue("posters.account"));
                if (poster != null) {
                    if (poster.getProfileInfo() == null) {
                        ProfileInfo pi = new ProfileInfo(poster, node.getIntValue("profileinfo.number"), node
                                .getStringValue("profileinfo.xml"), node.getStringValue("profileinfo.external"), node
                                .getIntValue("profileinfo.synced"));
                        poster.addProfileInfo(pi);
                    }
                } else {
                    log.error("Got a profileinfo of a missing poster !" + node.getStringValue("posters.account"));
                }
            }
        }
    }

    private void readThreadObservers() {
        if (node != null) {
            NodeManager forumsmanager = ForumManager.getCloud().getNodeManager("forums");
            NodeManager postareasmanager = ForumManager.getCloud().getNodeManager("postareas");
            NodeManager postthreadsmanager = ForumManager.getCloud().getNodeManager("postthreads");
            NodeManager threadobserversmanager = ForumManager.getCloud().getNodeManager("threadobservers");
            Query query = ForumManager.getCloud().createQuery();
            Step step1 = query.addStep(forumsmanager);
            RelationStep step2 = query.addRelationStep(postareasmanager);
            RelationStep step3 = query.addRelationStep(postthreadsmanager);
            RelationStep step4 = query.addRelationStep(threadobserversmanager);

            StepField f1 = query.addField(step1, forumsmanager.getField("number"));
            query.addField(step3.getNext(), postthreadsmanager.getField("number"));
            query.addField(step4.getNext(), threadobserversmanager.getField("number"));
            query.addField(step4.getNext(), threadobserversmanager.getField("emailonchange"));
            query.addField(step4.getNext(), threadobserversmanager.getField("bookmarked"));
            query.addField(step4.getNext(), threadobserversmanager.getField("ignorelist"));

            query.setConstraint(query.createConstraint(f1, node.getNumber()));

            NodeIterator i = ForumManager.getCloud().getList(query).nodeIterator();
            while (i.hasNext()) {
                Node node = i.nextNode();
                ThreadObserver to = new ThreadObserver(this, node.getIntValue("threadobservers.number"), node
                        .getIntValue("postthreads.number"), node.getStringValue("threadobservers.emailonchange"), node
                        .getStringValue("threadobservers.bookmarked"), node.getStringValue("threadobservers.ignorelist"));
                int postthreadid = node.getIntValue("postthreads.number");
                to.setThreadId(postthreadid);
                threadobservers.put(postthreadid, to);
            }
        }
    }

    /**
     * create a new poster for the forum
     *
     * @param account accountname to register
     * @param password password to register
     * @return newly created Poster-object <code>null</code> if creation failed
     */
    public Poster createPoster(String account, String password) {
        NodeManager nm = ForumManager.getCloud().getNodeManager("posters");
        if (nm != null) {
            log.debug("Creating poster");
            Node pnode = nm.createNode();
            pnode.setStringValue("account", account);

            org.mmbase.util.transformers.MD5 md5 = new org.mmbase.util.transformers.MD5();
            String md5passwd = md5.transform(password);

            pnode.setStringValue("password", md5passwd);
            log.debug("set password: " + password + " as md5 it looks like this: " + md5passwd);
            pnode.setStringValue("firstname", "");
            pnode.setStringValue("lastname", "");
            pnode.setIntValue("postcount", 0);
            pnode.setIntValue("firstlogin", ((int) (System.currentTimeMillis() / 1000)));
            pnode.setIntValue("lastseen", ((int) (System.currentTimeMillis() / 1000)));
            pnode.commit();
            log.service("Created poster object " + pnode + " now relating it to " + node);
            RelationManager rm = ForumManager.getCloud().getRelationManager("forums", "posters", "forposrel");
            if (rm != null) {

                Node rel = rm.createRelation(node, pnode);
                rel.commit();

                Poster p = new Poster(pnode, this, false);
                posters.put(p.getId(), p);
                onlineposters.add(p);
                posternames.put(p.getNick(), p);

                totalusers++;
                totalusersnew++;
                log.info("Created new poster " + p);
                return p;
            } else {
                log.error("Forum can't load relation nodemanager forums/posters/forposrel");
                return null;
            }
        } else {
            log.error("Forum can't load posters nodemanager");
            return null;
        }
    }

    /**
     * add administrator to forum
     *
     * @param ap Poster
     * @return always <code>false</code> (ToDo ??)
     */
    public boolean addAdministrator(Poster ap) {
        if (!isAdministrator(ap.getNick())) {
            RelationManager rm = ForumManager.getCloud().getRelationManager("forums", "posters", "rolerel");
            if (rm != null) {
                Node rel = rm.createRelation(node, ap.getNode());
                rel.setStringValue("role", "administrator");
                rel.commit();
                administrators.put(ap.getNick(), ap);
            } else {
                log.error("Forum can't load relation nodemanager forums/posters/rolerel");
            }
        }
        return false;
    }


    public boolean removeAdministrator(Poster mp) {
        if (isAdministrator(mp.getNick())) {
            Node node = ForumManager.getCloud().getNode(id);
            RelationIterator i = node.getRelations("rolerel", "posters").relationIterator();
            while (i.hasNext()) {
                Relation rel = i.nextRelation();
                String role = rel.getStringValue("role");
                if (role.substring(0, 12).equals("administrato")) {
                    Node p = null;
                    if (rel.getSource().getNumber() == node.getNumber()) {
                        p = rel.getDestination();
                    } else {
                        p = rel.getSource();
                    }
                    if (p != null && p.getNumber() == mp.getId()) {
                        rel.delete();
                        administrators.remove(mp.getNick());
                        administratorsline = null;
                    }
                }
            }
        }
        return false;
    }

    /**
     * remove a poster from the forum
     *
     * @param poster poster
     */
    public void childRemoved(Poster poster) {
        log.debug("removing poster nr " + poster.getId());
        posters.remove(poster.getId());
        posternames.remove("" + poster.getAccount());
        onlineposters.remove(poster);
        newposters.remove(poster);
        syncNode(ForumManager.SLOWSYNC);
    }

    /**
     * remove the forum
     *
     * @return <code>true</code> if it succeeds, <code>false</code> if it doesn't
     */
    public boolean remove() {
        log.debug("posters to remove: " + posters.size());
        Iterator<Poster> i = posters.values().iterator();
        while (i.hasNext()) {
            Poster poster = i.next();
            log.debug("try to remove poster :" + poster.getId());
            if (!poster.remove()) {
                log.error("Can't remove Poster : " + poster.getId());
                // jikes!, what if first ones succeeded?
                return false;
            }
        }

        // now delete all the postArea's
        Iterator<PostArea> j = postareas.values().iterator();
        while (j.hasNext()) {
            PostArea postArea = j.next();
            log.debug("try to remove postarea " + postArea.getId());
            if (!postArea.remove()) {
                log.error("Can't remove Area : " + postArea.getId());
                return false;
            }
            log.debug("removing postarea nr " + postArea.getId());
            j.remove();
        }

        //the abouve operations have most certainly put this forum's node
        //i a sync que. Let's remove it first before we delete it.
        ForumManager.nodeDeleted(node);
        node.delete(true);
        return true;
    }

    /**
     * Called on construction gather the administrators
     */
    private void readRoles() {
        if (node != null) {
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
                // check limited to 12 chars to counter mmbase 12
                // chars in role bug in some installs
                try {
                    if (role.substring(0, 12).equals("administrato")) {
                        Poster po = getPoster(p.getNumber());
                        administrators.put(po.getNick(), po);
                    }
                } catch (Exception e) {
                    log.info("Role error : " + role + " forum=" + id + " poster=" + p);
                }
            }
        }
    }

    /**
     * get the expiretime for the posters in seconds
     *
     * @return expiretime in seconds
     */
    public int getPosterExpireTime() {
        return 5 * 60;
    }

    /**
     * create a new private message
     *
     * @param poster  accountname/nick of the sending poster
     * @param to      accountname/nick of the recepient poster
     * @param subject subject of the private message
     * @param body    body of the private message
     * @return always -1 (ToDo ?)
     */
    public int newPrivateMessage(String poster, String to, String subject, String body) {
        Poster toposter = getPoster(to);
        Poster fromposter = getPoster(poster);
        if (toposter != null) {
            Mailbox mailbox = toposter.getMailbox("Inbox");
            if (mailbox == null) {
                mailbox = toposter.addMailbox("Inbox", "inbox for user " + toposter.getNick(), 1, 25, -1, 1, 1);
            }
            NodeManager nm = ForumManager.getCloud().getNodeManager("forumprivatemessage");
            if (nm != null) {
                Node mnode = nm.createNode();
                mnode.setStringValue("subject", subject);
                mnode.setStringValue("body", body);
                mnode.setStringValue("poster", fromposter.getNick());
                mnode.setIntValue("createtime", (int) (System.currentTimeMillis() / 1000));
                mnode.setIntValue("viewstate", 0);
                mnode.setStringValue("fullname", fromposter.getFirstName() + " " + fromposter.getLastName());
                mnode.commit();

                RelationManager rm = ForumManager.getCloud().getRelationManager("forummessagebox", "forumprivatemessage", "related");
                if (rm != null) {
                    Node rel = rm.createRelation(mailbox.getNode(), mnode);
                    rel.commit();
                } else {
                    log.error("Forum can't load relation nodemanager forummessagebox/forumprivatemessage/related");
                }
		mailbox.signalMailboxChange();
            } else {
                log.error("Forum can't load forumprivatemessage nodemanager");
            }
        }
        return -1;
    }

    /**
     * create a new folder (messagebox) for the given poster
     *
     * @param posterid  MMBase objectnumber of the poster
     * @param newfolder name of the new folder
     * @return always -1 (ToDo?)
     */
    public int newFolder(int posterid, String newfolder) {
        Poster poster = getPoster(posterid);
        if (poster != null) {
            Mailbox mailbox = poster.getMailbox(newfolder);
            if (mailbox == null) {
                mailbox = poster.addMailbox(newfolder, "mailbox " + newfolder + " for user " + poster.getNick(), 1, 25, -1, 1, 1);
            }
        }
        return -1;
    }

    /**
     * called to maintain the memorycaches
     */
    public void maintainMemoryCaches() {
        for (PostArea a : postareas.values()) {
            // for now all postareas nodes are loaded so
            // we just call them all for a maintain
            a.maintainMemoryCaches();
        }
    }

    /**
     * provide the number of the last poster in this forum
     */
    public int getLastPostNumber() {
        return lastpostnumber;
    }

    public PostAreaConfig getPostAreaConfig(String name) {
        if (config != null) {
            return config.getPostAreaConfig(name);
        } else {
            return null;
        }

    }


    public String getAccountCreationType() {
        if (config != null) {
            String tmp = config.getAccountCreationType();
            if (tmp != null) {
                return tmp;
            }
        }
        return ForumManager.getAccountCreationType();
    }

    public String getAccountRemovalType() {
        if (config != null) {
            String tmp = config.getAccountRemovalType();
            if (tmp != null) {
                return tmp;
            }
        }
        return ForumManager.getAccountRemovalType();
    }

    public String getLoginModeType() {
        if (config != null) {
            String tmp = config.getLoginModeType();
            if (tmp != null) {
                return tmp;
            }
        }
        return ForumManager.getLoginModeType();
    }

    public String getLoginSystemType() {
        if (config != null) {
            String tmp = config.getLoginSystemType();
            if (tmp != null) {
                return tmp;
            }
        }
        return ForumManager.getLoginSystemType();
    }

    public String getLogoutModeType() {
        if (config != null) {
            String tmp = config.getLogoutModeType();
            if (tmp != null) {
                return tmp;
            }
        }
        return ForumManager.getLogoutModeType();
    }

    public void setLogoutModeType(String type) {
        if (checkConfig()) {
            config.setLogoutModeType(type);
        }
    }

    public void setLoginSystemType(String system) {
        if (checkConfig()) {
            config.setLoginSystemType(system);
        }
    }

    public void addProfileDef(ProfileEntryDef cm) {
        if (checkConfig()) {
            config.addProfileDef(cm);
        }
    }

    public void setLoginModeType(String type) {
        if (checkConfig()) {
            config.setLoginModeType(type);
        }
    }

    public String getGuestReadModeType() {
        if (config != null) {
            String tmp = config.getGuestReadModeType();
            if (tmp != null) {
                return tmp;
            }
        }
        return ForumManager.getGuestReadModeType();
    }

    public String getThreadStartLevel() {
        if (config != null) {
            String tmp = config.getThreadStartLevel();
            if (tmp != null) {
                return tmp;
            }
        }
        return ForumManager.getThreadStartLevel();
    }

    public void setAvatarsUploadEnabled(String mode) {
        if (checkConfig()) {
            config.setAvatarsUploadEnabled(mode);
        }
    }

    public String getAlias() {
        if (config != null) {
            return config.getAlias();
        }
        return null;
    }

    public void setAlias(String alias) {
        if (checkConfig()) {
            config.setAlias(alias);
        }
    }

    public void setAvatarsGalleryEnabled(String mode) {
        if (checkConfig()) {
            config.setAvatarsGalleryEnabled(mode);
        }
    }

    public void setGuestReadModeType(String type) {
        if (checkConfig()) {
            config.setGuestReadModeType(type);
        }
    }

    public String getGuestWriteModeType() {
        if (config != null) {
            String tmp = config.getGuestWriteModeType();
            if (tmp != null) {
                return tmp;
            }
        }
        return ForumManager.getGuestWriteModeType();
    }

    public void setGuestWriteModeType(String type) {
        if (checkConfig()) {
            config.setGuestWriteModeType(type);
        }
    }

    public void setNavigationMethod(String navigationmethod) {
        if (checkConfig()) {
            config.setNavigationMethod(navigationmethod);
            if (getNavigationMethod().equals("tree"))
                syncTreeAreas();
        }
    }

    public String getAvatarsDisabled() {
        if (getAvatarsUploadEnabled().equals("true") || getAvatarsGalleryEnabled().equals("true")) {
            log.debug("avatars are not disabled");
            return "false";
        }
        log.debug("avatars are disabled");
        return "true";
    }

    public String getAvatarsUploadEnabled() {
        if (config != null) {
            String tmp = config.getAvatarsUploadEnabled();
            if (tmp != null) {
                log.debug("config.getAvatarsUploadEnabled() on " + getId() + ": " + tmp);
                return tmp;
            }
        }
        return ForumManager.getAvatarsUploadEnabled();
    }

    public String getAvatarsGalleryEnabled() {
        if (config != null) {
            String tmp = config.getAvatarsGalleryEnabled();
            if (tmp != null) {
                log.debug("config.getAvatarsGalleryEnabled() on " + getId() + ": " + tmp);
                return tmp;

            }
        }
        return ForumManager.getAvatarsGalleryEnabled();
    }

    public String getHeaderPath() {
        if (config != null) {
            String tmp = config.getHeaderPath();
            if (tmp != null) {
                log.debug("config.getHeaderPath() on " + getId() + ": " + tmp);
                return tmp;
            }
        }
        return ForumManager.getHeaderPath();
    }

    public String getFooterPath() {
        if (config != null) {
            String tmp = config.getFooterPath();
            if (tmp != null) {
                log.debug("config.getFooterPath() on " + getId() + ": " + tmp);
                return tmp;
            }
        }
        return ForumManager.getFooterPath();
    }

    public String getFromEmailAddress() {
        if (config != null) {
            String tmp = config.getFromEmailAddress();
            if (tmp != null && !tmp.equals("")) {
                return tmp;
            }
        }
        return ForumManager.getFromEmailAddress();
    }

    public String getXSLTPostingsOdd() {
        if (config != null) {
            String tmp = config.getXSLTPostingsOdd();
            if (tmp != null) {
                return tmp;
            }
        }
        return ForumManager.getXSLTPostingsOdd();
    }

    public String getNavigationMethod() {
        if (config != null) {
            String tmp = config.getNavigationMethod();
            if (tmp != null) {
                return tmp;
            }
        }
        return ForumManager.getNavigationMethod();
    }

    public String getXSLTPostingsEven() {
        if (config != null) {
            String tmp = config.getXSLTPostingsEven();
            if (tmp != null) {
                return tmp;
            }
        }
        return ForumManager.getXSLTPostingsEven();
    }

    public String getContactInfoEnabled() {
        if (config != null) {
            String tmp = config.getContactInfoEnabled();
            if (tmp != null) {
                log.debug("config.getContactInfoEnabled() on " + getId() + ": " + tmp);
                return tmp;

            }
        }
        return ForumManager.getContactInfoEnabled();
    }

    public String getSmileysEnabled() {
        if (config != null) {
            String tmp = config.getSmileysEnabled();
            if (tmp != null) {
                log.debug("config.getSmileysEnabled() on " + getId() + ": " + tmp);
                return tmp;

            }
        }
        return ForumManager.getSmileysEnabled();
    }

    public String getPrivateMessagesEnabled() {
        if (config != null) {
            String tmp = config.getPrivateMessagesEnabled();
            if (tmp != null) {
                log.debug("config.getPrivateMessagesEnabled() on " + getId() + ": " + tmp);
                return tmp;

            }
        }
        return ForumManager.getPrivateMessagesEnabled();
    }

    public int getPostingsPerPage() {
        if (config != null) {
            int tmpsize = config.getPostingsPerPage();
            if (tmpsize > -1)
                return tmpsize;
        }
        return ForumManager.getPostingsPerPage();
    }

    public void setPostingsPerPage(int maxcount) {
        if (checkConfig()) {
            config.setPostingsPerPage(maxcount);
        }
    }

    public void setPostingsOverflowPostArea(int maxcount) {
        if (checkConfig()) {
            config.setPostingsOverflowPostArea(maxcount);
        }
    }

    public void setPostingsOverflowThreadPage(int maxcount) {
        if (checkConfig()) {
            config.setPostingsOverflowThreadPage(maxcount);
        }
    }

    public void setReplyOnEachPage(boolean value) {
        if (checkConfig()) {
            config.setReplyOnEachPage(value);
        }
    }

    public void setSpeedPostTime(int delay) {
        if (checkConfig()) {
            config.setSpeedPostTime(delay);
        }
    }

    private boolean checkConfig() {
        if (config == null) {
            config = new ForumConfig(getName());
        }
        return true;
    }

    public Iterator getProfileDefs() {
        if (config != null) {
            return config.getProfileDefs();
        }
        return null;
    }

    public ProfileEntryDef getProfileDef(String name) {
        if (config != null) {
            return config.getProfileDef(name);
        }
        return null;
    }

    public boolean hasNick() {
        if (config != null) {
            if (getProfileDef("nick") != null) {
                return true;
            }
        }
        return false;
    }

    public int getPostThreadLoadedCount() {
        int count = 0;
        for (PostArea pa : postareas.values()) {
            count += pa.getPostThreadLoadedCount();
        }
        return count;
    }

    public int getPostingsLoadedCount() {
        int count = 0;
        for (PostArea pa : postareas.values()) {
            count += pa.getPostingsLoadedCount();
        }
        return count;
    }

    public int getMemorySize() {
        if (postareas == null) {
            return 0;
        } else {
            int size = 0;
            for (PostArea pa : postareas.values()) {
                size += pa.getMemorySize();
            }
            return size;
        }
    }

    public String filterContent(String body) {
        if (filterwords != null) {
            return ForumManager.filterContent(filterwords, body);
        } else {
            return ForumManager.filterContent(body);
        }
    }

    public Map<String, String> getFilterWords() {
        if (filterwords != null) {
            return filterwords;
        } else {
            return ForumManager.getFilterWords();
        }
    }

    public void addWordFilter(String name, String value) {
        if (filterwords != null) {
            filterwords.put(name, value);
        } else {
            ForumManager.addWordFilter(name, value);
        }
    }

    public void removeWordFilter(String name) {
        if (filterwords != null) {
            filterwords.remove(name);
        } else {
            ForumManager.removeWordFilter(name);
        }
    }

    public String filterContent(Map<String, String> filterwords, String body) {
        return ForumManager.filterContent(filterwords, body);
    }

    public boolean getCloneMaster() {
        if (config != null) {
            return config.getCloneMaster();
        }
        return false;
    }

    public String getGuiEdit(String key) {
        if (config != null) {
            return config.getGuiEdit(key);
        }
        return "true";
    }

    public int getSpeedPostTime() {
        if (config != null) {
            int tmp = config.getSpeedPostTime();
            if (tmp != -1) {
                return tmp;
            }
        }
        return ForumManager.getSpeedPostTime();
    }

    public int getPostingsOverflowPostArea() {
        if (config != null) {
            int tmp = config.getPostingsOverflowPostArea();
            if (tmp != -1) {
                return tmp;
            }
        }
        return ForumManager.getPostingsOverflowPostArea();
    }

    public int getPostingsOverflowThreadPage() {
        if (config != null) {
            int tmp = config.getPostingsOverflowThreadPage();
            if (tmp != -1) {
                return tmp;
            }
        }
        return ForumManager.getPostingsOverflowThreadPage();
    }

    public ThreadObserver getThreadObserver(int id) {
        Object o = threadobservers.get(new Integer(id));
        if (o != null)
            return (ThreadObserver) o;
        return null;
    }

    public boolean setEmailOnChange(int id, Poster ap, boolean state) {
        Object o = threadobservers.get(Integer.valueOf(id));
        if (o != null) {
            return ((ThreadObserver) o).setEmailOnChange(ap, state);
        } else {
            ThreadObserver to = new ThreadObserver(this, -1, id, "", "", "");
            // to.setThreadId(id);
            threadobservers.put(Integer.valueOf(id), to);
            return to.setEmailOnChange(ap, state);
        }
    }

    public boolean setBookmarkedChange(int id, Poster ap, boolean state) {
        Object o = threadobservers.get(Integer.valueOf(id));
        if (o != null) {
            ((ThreadObserver) o).setBookmarkedChange(ap, state);
            if (state) {
                ap.addBookmarkedThread(id);
            } else {
                ap.removeBookmarkedThread(id);
            }
            return true;
        } else {
            ThreadObserver to = new ThreadObserver(this, -1, id, "", "", "");
            // to.setThreadId(id);
            threadobservers.put(new Integer(id), to);
            if (state) {
                ap.addBookmarkedThread(id);
            } else {
                ap.removeBookmarkedThread(id);
            }
            to.setBookmarkedChange(ap, state);
            return true;
        }
    }

    public String getEmailtext(String role) {
        return ForumManager.getEmailtext(role);
    }

    public String getExternalRootUrl() {
        return ForumManager.getExternalRootUrl();
    }

    public boolean getReplyOnEachPage() {
        if (config != null) {
            return config.getReplyOnEachPage();
        }
        return ForumManager.getReplyOnEachPage();
    }

    private void readFieldaliases() {
        if (config != null) {
            Iterator i = config.getFieldaliases();
            while (i.hasNext()) {
                FieldAlias fa = (FieldAlias) i.next();
                fa.init(this);
            }
        }
    }

    public ForumConfig getConfig() {
        return config;
    }


}
