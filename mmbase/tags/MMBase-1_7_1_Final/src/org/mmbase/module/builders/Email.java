/*

This software is OSI Certified Open Source Software.
OSI Certified is a certification mark of the Open Source Initiative.

The license (Mozilla version 1.0) can be read at the MMBase site.
See http://www.MMBase.org/license

*/
package org.mmbase.module.builders;

import java.util.*;

import org.mmbase.module.core.*;
import org.mmbase.module.*;
import org.mmbase.module.gui.html.scanparser;

import org.mmbase.util.*;
import org.mmbase.util.logging.Logger;
import org.mmbase.util.logging.Logging;

/**
 * Email builder, keeps and manages a queue of emails
 * that need to be send by the SendMail module.
 * It allows for emailing on time, repeat mail, stats
 * and using urls as input for subject and body.
 * @author Daniel Ockeloen
 * @version $Id: Email.java,v 1.25 2004-02-09 13:50:39 pierre Exp $
 */
public class Email extends MMObjectBuilder {

    // logger
    private static Logger log = Logging.getLoggerInstance(Email.class.getName());

    // sendprobe, maintains the queue using a wait/notify setup.
    private EmailSendProbe sendprobe;

    // The state a email can be in
    /**
     * Indicates the state of the email is unknown.
     */
    public final static int STATE_UNKNOWN=-1;
    /**
     * Indicates the email is waiting to be queued.
     */
    public final static int STATE_WAITING=0;
    /**
     * Indicates the email has been delivered.
     */
    public final static int STATE_DELIVERED=1;
    /**
     * Indicates the email has not been delivered due to an failure.
     */
    public final static int STATE_FAILED=2;
    /**
     * Indicates the email has been mark as spam.
     */
    public final static int STATE_SPAMGARDE=3;
    /**
     * Indicates the email has been queued and is waiting to be delivered.
     */
    public final static int STATE_QUEUED=4;


    // The type of supported emails
    /**
     * Email will be sent and removed after sending.
     */
    public final static int TYPE_ONESHOT=1;
    /**
     * Email will be sent and scheduled after sending for a next time
     */
    public final static int TYPE_REPEATMAIL=2;
    /**
     * Email will be sent and will not be removed.
     */
    public final static int TYPE_ONESHOTKEEP=3;

    // if oneshot types of mails are used we check for dups
    private Vector dubcheck=new Vector(100);

    // number of emails send sofar since startup
    private int numberofmailsend=0;

    // ref to sessions module needed to parse pages
    private static sessionsInterface sessions=null;


    /**
     * init
     */
    public boolean init() {
        super.init ();
        // start the EmailSendProbe
        sendprobe=new EmailSendProbe(this);
        // obtain the session module
        sessions=(sessionsInterface)Module.getModule("SESSION");
        return true;
    }


    /**
     * insert new email object
     */
     public int insert(String owner, MMObjectNode node) {
        // extra check to make sure we insert with a
        // a time, if -1 it will change it to the
        // current time.
        int i=node.getIntValue("mailtime");
        if (i==-1) {
            // set value to current time+2
            // the +2 is give the queue some time to react
            node.setValue("mailtime",((int)((System.currentTimeMillis()/1000))+2));
        }

        // insert it into the database
        int number=super.insert(owner,node);

        // get the mailstatus to see if we need to queue it
        int mailstatus=node.getIntValue("mailstatus");

        // is it waiting ?
        if (mailstatus==STATE_UNKNOWN || mailstatus==STATE_WAITING) {
            // check if we need to queue this mail, it does
            // this by checking if it needs to be put into
            // STATE_QUEUED mode
            int ttime=(int)((System.currentTimeMillis()/1000));
            ttime=node.getIntValue("mailtime")-ttime;

            // ttime hold the time in seconds until we want
            // to be mailed
            if (ttime<(sendprobe.maxtasktime)) {
                // the time is smaller than the queue
                // time so try to queue it
                sendprobe.putTask(node);
            }
        }
        return number;
    }

    /**
     * check the message object, now if state is 1 (oneshot)
     * it mails and removed itself from the cloud when done
     */
    public void checkOneShotMail(MMObjectNode node) {
        // just to make sure lets recheck state
        int mailstatus=node.getIntValue("mailstatus");
        if (mailstatus==STATE_QUEUED) {
            // so lets send this node as email
            sendMailNode(node);

            // sincer its oneshot remove it from
            // mmbase
            removeNode(node);
        }
    }


    /**
     * check the message object, now if state is 1 (oneshot)
     */
    public void checkOneShotKeepMail(MMObjectNode node) {
        // just to make sure lets recheck state
        int mailstatus=node.getIntValue("mailstatus");
        if (mailstatus==STATE_UNKNOWN || mailstatus==STATE_WAITING) {
            // so lets send this node as email
            sendMailNode(node);
        }
    }

    /**
     * check the message object, now if state is 1 (oneshot)
     * it mails and removed itself from the cloud when done
     */
    public void checkRepeatMail(MMObjectNode node) {
        // just to make sure lets recheck state
        int mailstatus=node.getIntValue("mailstatus");
        if (mailstatus==STATE_QUEUED) {
            // so lets send this node as email
            mailstatus=sendMailNode(node);

            // if something goes wrong put the
            // email in failed state
            if (mailstatus==STATE_FAILED) return;

            // get the mailtime, so we can calc the new
            // mailtime.
            int mailtime=node.getIntValue("mailtime");

            // what is the repeat time of the message
            int repeattime=node.getIntValue("repeattime");

            // calc the new proposed time
            int proposedtime=mailtime+repeattime;

            // get the current time to make sure we are not
            // allready passed the new proposed time
            int nowtime=(int)(System.currentTimeMillis()/1000);

            // if we allready passed the time or repeattime
            // is less then 60 seconds we ignore as a spam
            // guard
            //if (repeattime>59 && nowtime<proposedtime) {
            if (repeattime>59) {
                // set the new proposed time
                node.setValue("mailtime",proposedtime);
                // signal that we are ready again
                node.setValue("mailstatus",STATE_WAITING);
                // commit to the cloud
                node.commit();

                // well try putting it in the queue
                int ttime=(int)((System.currentTimeMillis()/1000));
                int ntime=node.getIntValue("mailtime");
                if (ntime<(ttime+sendprobe.maxtasktime)) {
                    sendprobe.putTask(node);
                }
            } else {
                // spam guard triggered
                node.setValue("mailstatus",STATE_SPAMGARDE);
                // commit to the cloud
                node.commit();
            }
        }
    }


    /**
     * @javadoc
     */
    public int sendMailNode(MMObjectNode node) {
        SendMailInterface sendmail=(SendMailInterface)Module.getModule("sendmail");
        if (sendmail==null) {
            log.error("sendmail module not active, cannot send email");
            return -1; // STATE_FAILED ?
        }
        // perform the dup check
        String ckey=""+node.getIntValue("number");

        // don't perform a dup check
        if (node.getIntValue("mailtype")!=TYPE_REPEATMAIL) {
            if (dubcheck.contains(ckey)) {
                log.debug("Email -> dup killed id="+ckey);
                return -1; // STATE_FAILED ?
             } else {
                // put the id in the dup checker
                dubcheck.addElement(ckey);
                if (dubcheck.size()>100) {
                    dubcheck.removeElementAt(0);
                }
            }
        }

        // get subject of the mail (including url based)
        String subject=getSubject(node);

        // get To of the mail
        String to=getTo(node);

        // get From of the mail
        String from=node.getStringValue("from");

        // get ReplyTo of the mail
        String replyto=node.getStringValue("replyto");

        // get Body of the mail (including url based)
        String body=getBody(node);

        // now if a url is defined it overrides the body
        // it will then send the webpage defined by the
        // url as the body !! (only  local url's are
        // now supported that use html or shtml
        // ** this is a second way todo it (see getBody)
        // ** im unclear why i did this but we need to
        // ** keep it for legacy reasons now
        String url=node.getStringValue("bodyurl");
        if (url!=null && !url.equals("")) {
            // get the page
            String tmpbody=getPage(url);
            if (tmpbody!=null) body=tmpbody;
        }

        // check if we have multiple to's
        StringTokenizer tok = new StringTokenizer(to,",\n\r");
        while (tok.hasMoreTokens()) {
            // get the next todo
            String to_one=tok.nextToken();

            // create new (sendmail) object
            Mail mail=new Mail(to_one,from);

            // set the subject
            mail.setSubject(subject);

            // set default date
            mail.setDate();

            // set the reply header if defined
            if (replyto!=null && !replyto.equals("")) mail.setReplyTo(replyto);

            // fill the body
            mail.setText(body);

            // little trick if it seems valid html page set
            // the headers for html mail
            if (body.indexOf("<HTML>")!=-1 && body.indexOf("</HTML>")!=-1) {
                mail.setHeader("Mime-Version","1.0");
                mail.setHeader("Content-Type","text/html; charset=\"ISO-8859-1\"");
            }

            // is the don't mail tag set ? this allows
            // a generated body to signal it doesn't
            // want to be mailed since for some reason
            // invalid (for example there is no news for
            // you
            if (body.indexOf("<DONTMAIL>")==-1) {
                // if the subject contains 'fakemail'
                // perform all actions butt don't really
                // mail. This is done for testing
                if (subject!=null && subject.indexOf("fakemail")!=-1) {
                    // add one to the sendmail counter
                    numberofmailsend++;
                    System.out.println("FAKE SEND");
                    node.setValue("mailstatus",STATE_DELIVERED);
                } else {
                if (to==null || ((SendMailInterface)Module.getModule("sendmail")).sendMail(mail)==false) {
                    log.debug("Email -> mail failed");
                    node.setValue("mailstatus",STATE_FAILED);
                    // add one to the sendmail counter
                    numberofmailsend++;
                } else {
                    // add one to the sendmail counter
                    numberofmailsend++;
                    log.debug("Email -> mail send");
                    node.setValue("mailstatus",STATE_DELIVERED);
                }
                }
            } else {
                log.debug("Don't mail tag found");
            }
        }
        // set the new mailedtime, that can be used by admins
        // to see when it was mailed vs the requested mail
        // time
        node.setValue("mailedtime",(int)(System.currentTimeMillis()/1000));

        // commit the changes to the cloud
        node.commit();

        return node.getIntValue("mailstatus");
    }


    /**
     * get the To header if its not set directly
     * try to obtain it from related objects.
     */
    String getTo(MMObjectNode node) {
        String to=node.getStringValue("to");
        if (to==null || to.equals("")) {
            to=getPeoplesEmail(node);
            String tmp=getUsersEmail(node);
            if (tmp!=null) {
                if (to==null || to.equals("")) {
                    to=tmp;
                } else {
                    to+=","+tmp;
                }
            }
        }
        return to;
    }

    /**
     * get the email addresses of related people
     */
    String getPeoplesEmail(MMObjectNode node) {
        // try and find related people
        String to=null;
        Vector rels=node.getRelatedNodes("people");
        if (rels!=null) {
            Enumeration enumeration=rels.elements();
                while (enumeration.hasMoreElements()) {
                MMObjectNode pnode=(MMObjectNode)enumeration.nextElement();
                String email=pnode.getStringValue("email");
                if (to==null || to.equals("")) {
                    to=email;
                } else {
                    to+=","+email;
                }
            }
        }
        return to;
    }

    /**
     * get the email addresses of related people
     */
    String getUsersEmail(MMObjectNode node) {
        // try and find related users
        String to=null;
        Vector rels=node.getRelatedNodes("users");
        if (rels!=null) {
            Enumeration enumeration=rels.elements();
                while (enumeration.hasMoreElements()) {
                MMObjectNode pnode=(MMObjectNode)enumeration.nextElement();
                String email=pnode.getStringValue("email");
                if (to==null || to.equals("")) {
                    to=email;
                } else {
                    to+=","+email;
                }
            }
        }
        return to;
    }

    /**
     * get the subject, obtain it by url/getPage if needed
     */
    String getSubject(MMObjectNode node) {
        String subject=node.getStringValue("subject");
        if (subject!=null) {
            if (subject.startsWith("/")) {
                // we need a url=".." in tcp !
                String pagesubject=getPage(subject);
                if (pagesubject!=null) {
                    return pagesubject;
                } else {
                    return "subject page 404 : "+subject;
                }
            } else {
                return subject;
            }
        } else {
            return ""; // should we make the subject empty on defailt ?
        }
    }

    /**
     * get the body, obtain it by url/getPage if needed
     */
    String getBody(MMObjectNode node) {
        String body=node.getStringValue("body");
        if (body!=null) {
            if (body.startsWith("/")) {
                // we need a url=".." in tcp !
                String pagebody=getPage(body);
                if (pagebody!=null) {
                    return pagebody;
                } else {
                    return "body page 404 : "+body;
                }
            } else {
                return body;
            }
        } else {
            return ""; // should we make the body empty on defailt ?
        }
    }

    /**
     * check the message object
     */
    public void checkMailNode(MMObjectNode node) {
        // get the mailtype and call method based on
        // it.
        int mailtype=node.getIntValue("mailtype");
        switch(mailtype) {
            case TYPE_ONESHOT :
                checkOneShotMail(node);
                break;
            case TYPE_ONESHOTKEEP :
                checkOneShotKeepMail(node);
                break;
            case TYPE_REPEATMAIL:
                checkRepeatMail(node);
                break;
            default:
                checkOneShotMail(node);
                break;
        }
    }

    /**
     * getPage, using the scanparser. (remember this is a hack to be
     * replaced by a new version soon).
     */
    public String getPage(String url) {
        // get the scanparser
        scanparser m=(scanparser)mmb.getBaseModule("SCANPARSER");
        if (m!=null) {
            // found module, create a empty
            // context since we don't have a user
            scanpage sp=new scanpage();

            // setup a session so we can use sessions

            String sname="emailuser";
            sp.sname=sname;
            sessionInfo session=sessions.getSession(sp,sname);
            sp.session=session;

            // get the page and return it
            return m.calcPage(url,sp,0);
        }
        return null;
    }

    /**
    * performTask, called by the email probe if a email object
    * needs to be handled (mostly because its mail time has passed).
    */
    public synchronized boolean performTask(MMObjectNode node) {
        checkMailNode(node);
        return true;
    }

    /**
     * some stat calls used by the email admin tool
     */
    public String replace(scanpage sp, StringTokenizer tok) {
        if (tok.hasMoreTokens()) {
            String cmd=tok.nextToken();
            if (cmd.equals("DBQUEUED")) return ""+getNumberOfQueued();
            else if (cmd.equals("MEMTASKS")) return ""+getNumberOfTasks();
            else if (cmd.equals("MAXMEMTASKS")) return ""+getMaxNumberOfTasks();
            else if (cmd.equals("DBQUEUEDTIME")) return ""+getMaxQueuedTime();
            else if (cmd.equals("DBQUEUEPROBETIME")) return ""+getQueueProbeTime();
            else if (cmd.equals("NUMBEROFMAILSEND")) return ""+numberofmailsend;
        }
        return null;
    }

    /**
     * some list commands for the email admin tool
     */
    public Vector getList(scanpage sp, StringTagger tagger, StringTokenizer tok) {
        if (tok.hasMoreTokens()) {
            String cmd=tok.nextToken();
            if (cmd.equals("MEMTASKS")) return getMemTasks();
        }
        return null;
    }

    /**
     * enumeration the tasks in memory for the admin tool
     */
    public Vector getMemTasks() {
        Vector results=new Vector();
        Enumeration enumeration=sendprobe.tasks.elements();
            while (enumeration.hasMoreElements()) {
            MMObjectNode node=(MMObjectNode)enumeration.nextElement();
            results.addElement(""+node.getIntValue("number"));
            int ntime=node.getIntValue("mailtime");
            int ttime=(int)((System.currentTimeMillis()/1000));
            results.addElement(""+(ntime-ttime));
            results.addElement(node.getStringValue("to"));
            results.addElement(node.getStringValue("from"));
            results.addElement(node.getStringValue("subject"));
        }
        return results;
    }

    /**
     * return the number of queued messages
     */
    public int getNumberOfQueued() {
        if (sendprobe!=null) {
            return sendprobe.dbqueued;
        } else {
            return -1;
        }
    }

    /**
     * return the max time of queued messages
     */
    public int getMaxQueuedTime() {
        if (sendprobe!=null) {
            return sendprobe.maxtasktime;
        } else {
            return -1;
        }
    }

    /**
     * return the time interval we check the
     * database for queued messages
     */
    public int getQueueProbeTime() {
        if (sendprobe!=null) {
            return sendprobe.queueprobetime;
        } else {
            return -1;
        }
    }

    /**
     * return the number of tasks
     */
    public int getNumberOfTasks() {
        if (sendprobe!=null) {
            return sendprobe.tasks.size();
        } else {
            return -1;
        }
    }

    /**
     * return the maximum number of tasks we
     * queue in memory
     */
    public int getMaxNumberOfTasks() {
        if (sendprobe!=null) {
            return sendprobe.internalqueuesize;
        } else {
            return -1;
        }
    }
}
