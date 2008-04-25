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
import org.mmbase.cache.xslt.*;
import org.mmbase.bridge.*;
import org.mmbase.module.core.*;
import org.mmbase.applications.mmbob.util.transformers.Smilies;
import org.mmbase.applications.mmbob.util.transformers.BBCode;
import org.mmbase.applications.thememanager.ThemeManager;
import org.mmbase.applications.mmbob.util.transformers.PostingBody;

import org.mmbase.util.logging.Logging;
import org.mmbase.util.logging.Logger;
import org.mmbase.util.SizeOf;

import javax.xml.transform.*;


/**
 * @author Daniel Ockeloen
 * @version $Id: Posting.java,v 1.29 2007-01-16 18:53:20 michiel Exp $
 */
public class Posting {


    /** The smilies transformer */
    private static Smilies smilies = new Smilies ();

    static private final Logger log = Logging.getLoggerInstance(Posting.class);

    private int id;
    private int createtime;
    private int edittime;
    private int threadpos;
    private PostThread parent;
    private String subject;
    private String c_body; 
    private String body; 
    private String c_poster; 

    private PostingBody postingBody = new PostingBody();

    /**
     * Construct the posting
     *
     * @param node   postingnode
     * @param parent postthread
     */
    public Posting(Node node, PostThread parent,boolean prefixwanted) {
        String prefix="";
        if (prefixwanted) prefix = "postings.";

        id = node.getIntValue(prefix+"number");
        c_body = node.getStringValue(prefix+"c_body");
        if (c_body.equals("")) {
            body = node.getStringValue(prefix+"body");
        } else {
            body = "";
        }
        c_poster = node.getStringValue(prefix + "c_poster");
        subject = node.getStringValue(prefix + "subject");
        createtime = node.getIntValue(prefix + "createtime");
        edittime = node.getIntValue(prefix + "edittime");
        this.parent = parent;
    }

    public int getMemorySize() {
        int size = c_body.length();
        size +=body.length();
        size +=c_poster.length();
        size +=subject.length();
        size += 16; // int values
        return size;
    }

    /**
     * Set the id of this postingnode
     *
     * @param id posting id
     */
    public void setId(int id) {
        this.id = id;
    }

    public void setThreadPos(int threadpos) {
        this.threadpos = threadpos;
    }

    /**
     * set the subject of the posting
     *
     * @param subject
     */
    public void setSubject(String subject) {
        this.subject = subject;
    }

    /**
     * set the body of the posting
     *
     * @param body
     * @param imagecontext The context where to find the images (eg smilies)
     */
    public void setBody(String body,String imagecontext,boolean parsed) {
        Node node = ForumManager.getCloud().getNode(id);
        if (parsed) {
            node.setStringValue("body", body);
        } else {
            node.setStringValue("body", "<posting>" + postingBody.transform(body) + "</posting>");
        }
        c_body = translateBody(node.getStringValue("body"),imagecontext);
        body="";
        node.setValue("c_body",c_body);
        node.commit();
    }

    /**
     * set the date/time of the last time this posting was editted
     *
     * @param time Date/time (Epoch)
     */
    public void setEditTime(int time) {
        edittime =  time;
    }


    /**
     * set the date/time of the last time this posting was posted
     *
     * @param time Date/time (Epoch)
     */
    public void setPostTime(int time) {
        createtime =  time;
    }

    /**
     * get the date/time of the last time this posting was editted
     *
     * @return Date/time (Epoch)
     */
    public int getEditTime() {
        return edittime;
    }

    /**
     * get the id of this posting
     *
     */
    public int getId() {
        return id;
    }

    public int getThreadPos() {
        return threadpos;
    }

    /**
     * set the node of this posting
     *
     * @param node posting
     */
    public void setNode(Node node) {
        id = node.getNumber();
    }

    /**
     * get the subject of this posting
     *
     * @return subject of this posting
     */
    public String getSubject() {
        return subject;
    }

    public String getDirectBody() {
        // do i really need to get the body again ?
        Node node = ForumManager.getCloud().getNode(id);
        return node.getStringValue("body");
    }

    /**
     * get the body of this posting
     *
     * @param imagecontext The context where to find the images (eg smilies)
     * @return body of this posting
     */
    public String getBody(String imagecontext) {
        if (c_body.equals("")) {
            long start = System.currentTimeMillis();
            Node node = ForumManager.getCloud().getNode(id);
            c_body = translateBody(body,imagecontext);
            body="";
            node.setValue("c_body",c_body);
            node.commit();
            long end = System.currentTimeMillis();
            return c_body;
        }
        return c_body;
    }

    /**
     * get the html body of this posting
     *
     * meaning this will be decoded with BBCodes, Urls, Security checks..
     * needs a better caching system that doesn't take memory again, probably
     * some flipper system that either the decoded or encoded is caches depending
     * on use.
     *
     * @param imagecontext The context where to find the images (eg smilies)
     * @return body of this posting
     */
    public String getBodyHtml(String imagecontext) {
        String body = getBody(imagecontext);
        body = BBCode.decode(body);
        return body;
    }

    public boolean inBody(String searchkey) {
        if (c_body.equals("")) {
            if (body.toLowerCase().indexOf(searchkey)!=-1) return true;
        } else {
            if (c_body.toLowerCase().indexOf(searchkey)!=-1) return true;
        }
        return false;
    }


    public boolean inSubject(String searchkey) {
        if (subject.toLowerCase().indexOf(searchkey)!=-1) return true;
        return false;
    }

    /**
     * get the accountname/nick of the poster of this posting
     *
     * @return accountname/nick of the poster
     */
    public String getPoster() {
        return c_poster;
    }

    /**
     * get the date/time (epoch) when this posting was posted
     *
     * @return date/time (epoch)
     */
    public int getPostTime() {
        return createtime;
    }

    /**
     * Delete a posting and signal the parent postthread that the posting must be removed
     *
     * @return allways <code>true</code>
     */
    public boolean remove() {
        Node node = ForumManager.getCloud().getNode(id);
        log.debug("going to remove posting: " + node.getNumber());
        removeForeignKeys(ForumManager.getCloud().getNodeManager("postareas"),  "lastpostnumber");
        removeForeignKeys(ForumManager.getCloud().getNodeManager("postthreads"),"lastpostnumber");
        removeForeignKeys(ForumManager.getCloud().getNodeManager("forums"),     "lastpostnumber");

        //make shure this node is not in a forum syncer
        ForumManager.nodeDeleted(node);
        node.delete(true);
        parent.childRemoved(this);
        return true;
    }

    private void removeForeignKeys(NodeManager nodeManager, String fieldName) {
        //check if nodenumber is somewhere referenced as a foreignkey
        Node node = ForumManager.getCloud().getNode(id);
        log.debug("Deleting  from " + nodeManager + " where " + fieldName + " = " + node.getNumber());
        NodeList nodeList = nodeManager.getList(fieldName +"="+node.getNumber(),null,null);
        if (log.isDebugEnabled()) {
            log.debug("found: " + nodeList);
        }
        NodeIterator it = nodeList.nodeIterator();
        Node tempNode;
        while (it.hasNext()) {
            tempNode = (Node)it.next();
            tempNode.setValue(fieldName, null);
            if (log.isDebugEnabled()) {
                log.debug("just set the value of field " + fieldName + " to null. but it reads: " + tempNode.getStringValue(fieldName));
                log.debug("cloud id: " + ForumManager.getCloud().hashCode());
            }
            tempNode.commit();
        }
    }


    /**
     * save the node to the cloud
     *
     * @return allways <code>true</code>
     */
    public boolean save() {
        Node node = ForumManager.getCloud().getNode(id);
        node.setStringValue("c_body",c_body);
        node.setStringValue("c_poster",c_poster);
        node.setStringValue("subject",subject);
        node.setIntValue("createtime",createtime);
        node.setIntValue("edittime",edittime);
        node.commit();
        return true;
    }

    private String translateBody(String body, String imagecontext) {
        log.debug("going to translate the BODY");
        String xsl = "mmbob/";
        if (threadpos%2 == 0) {
            xsl += parent.getParent().getParent().getXSLTPostingsEven();
        } else {
            xsl += parent.getParent().getParent().getXSLTPostingsOdd();
        }
        try {
            Reader rd = ResourceLoader.getConfigurationRoot().getReader(xsl);
            TransformerFactory tFactory = TransformerFactory.newInstance();
            Transformer transformer = tFactory.newTransformer(new javax.xml.transform.stream.StreamSource(rd));
            StringWriter result = new StringWriter();
            transformer.transform(new javax.xml.transform.stream.StreamSource(new StringReader(body)),new javax.xml.transform.stream.StreamResult(result));
            body = result.toString();
        } catch (Exception e) { 
            e.printStackTrace( );
        }

        int forumid = parent.getParent().getParent().getId();
        String themeid = ThemeManager.getAssign("MMBob."+forumid);

        if (themeid == null) {
            themeid = ThemeManager.getAssign("MMBob");
        } 

        //String imagecontext = "/mmbase/thememanager/images";
        if ("true".equals(parent.getParent().getParent().getSmileysEnabled())) {
            body = smilies.transform(body, themeid, imagecontext);
        }
        return body;
    }

    public PostThread getParent() {
        return parent;
    }
}
