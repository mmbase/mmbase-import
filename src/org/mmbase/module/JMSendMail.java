/*
 
This software is OSI Certified Open Source Software.
OSI Certified is a certification mark of the Open Source Initiative.
 
The license (Mozilla version 1.0) can be read at the MMBase site.
See http://www.MMBase.org/license
 
*/
package org.mmbase.module;

import java.io.*;
import java.net.*;
import java.util.*;

import org.mmbase.util.*;
import org.mmbase.util.logging.*;
import javax.mail.*;
import javax.mail.internet.*;


/**
 * Module providing mail functionality based on JavaMail.
 * Based on org.mmbase.module.SendMail by Rob Vermeulen, where
 * actually sending mail has been replaced by JavaMail functionality
 * while other methods have remained unchanged.
 *
 * @author Case Roole
 */
public class JMSendMail extends Module implements SendMailInterface {
    private static Logger log = Logging.getLoggerInstance(JMSendMail.class.getName());
    protected String from;
    protected String to;
    protected String replyto;
    protected String cc;
    protected String bcc;
    protected String subject;
    protected String message;

    private DataInputStream in = null;
    private DataOutputStream out = null;
    private Socket connect = null;

    private String smtphost = "";

    public void reload() {
        smtphost=getInitParameter("mailhost");
    }

    public void unload() { }


    public void onload() { }


    public void shutdown() { }


    public void init() {
        from = null;
        to = null;
        replyto = null;
        cc = null;
        bcc = null;
        message = "<no message specified>";
        subject = "<no subject specified>";
        smtphost=getInitParameter("mailhost");
        log.info("Module SendMail started (smtphost="+smtphost+")");
    }

    public void setFrom(String from) {
        this.from = from;
    }

    public void setTo(String to) {
        this.to = to;
    }

    public void setReplyTo(String replyto) {
        this.replyto = replyto;
    }

    public void setCc(String cc) {
        this.cc = cc;
    }

    public void setBcc(String bcc) {
        this.bcc = bcc;
    }

    public void setSubject(String subject) {
        this.subject = subject;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    /**
     * Connect to the smtphost
     * ..unchanged..
     */
    private boolean connect(String host, int port) {
        log.service("SendMail connected to host="+host+", port="+port+")");
        String result="";

        try {
            connect=new Socket(host,port);
        } catch (Exception e) {
            log.error("SendMail cannot connect to host="+host+", port="+port+")."+e);
            return false;
        }
        try {
            out=new DataOutputStream(connect.getOutputStream());
        } catch (IOException e) {
            log.error("Sendmail cannot get outputstream." +e);
            return false;
        }
        try {
            in=new DataInputStream(connect.getInputStream());
        } catch (IOException e) {
            log.error("SendMail cannot get inputstream."+e);
            return false;
        }
        try {
            result = in.readLine();
        } catch (Exception e) {
            log.error("SendMail cannot read response."+e);
            return false;
        }
        /** Is anwser 220 **/
        if(result.indexOf("220")!=0)  return false;
        return true;
    }


    /**
     	 * Send mail
     * ..unchanged..
     */
    public synchronized boolean sendMail(String from, String to, String data) {
        return sendMail(from,to,data,new Hashtable());
    }


    /**
     * Send mail with headers 
     */
    public boolean sendMail(String from, String to, String data, Hashtable headers) {
        setFrom(from);
        setTo(to);
        setMessage(data);

        // Fields supported by org.mmbase.module.SendMail that are ignored: Date, Comment, Reply-to
        String s = null;
        s = (String)headers.get("Subject");
        if (s!=null) {
            setSubject(s);
        }

        s = (String)headers.get("CC");
        if (s!=null) {
            setCc(s);
        }

        s = (String)headers.get("BCC");
        if (s!=null) {
            setBcc(s);
        }

        try {
            log.service("JMSendMail sending mail to "+to);
            send();
            log.service("JMSendMail done.");
            return true;
        } catch (Exception e) {
            log.error("JMSendMail failure: "+e.getMessage());
            return false;
        }
    }

    /**
    * Actually send the email.
    */
    protected void send() throws Exception {
        if (to == null) {
            throw new Exception("No 'to' address specified");
        } else if (from == null) {
            throw new Exception("No 'from' address specified");
        } else {
            // start a session
            try {
                Properties prop = System.getProperties();
                prop.put("mail.smtp.host",smtphost);
                Session session1 = Session.getInstance(prop,null);
                // construct a message
                MimeMessage msg = new MimeMessage(session1);
                msg.setFrom(new InternetAddress(from));
                msg.addRecipient(Message.RecipientType.TO,new InternetAddress(to));
                if (cc != null) {
                    msg.addRecipient(Message.RecipientType.CC,new InternetAddress(cc));
                }
                if (bcc != null) {
                    msg.addRecipient(Message.RecipientType.CC,new InternetAddress(bcc));
                }
                msg.setSubject(subject);
                msg.setText(message);
                // connect to the transport
                Transport trans = session1.getTransport("smtp");
                trans.connect(smtphost,null,null);
                // send the message
                trans.sendMessage(msg,msg.getAllRecipients());
                //smtphost
                trans.close();
            } catch (Exception e) {
                throw e;
            }
        }
    }

    /**
     * Send mail
     * ..unchanged..
     */
    public boolean sendMail(Mail mail) {
        return sendMail(mail.from, mail.to, mail.text, mail.headers);
    }

    /**
     * checks the e-mail address
     * ..unchanged..
     */
    public String verify(String name) {
        String anwser="";

        /** Connect to mail-host **/
        if (!connect(smtphost,25)) return "Error";

        try {
            out.writeBytes("VRFY "+name+"\r\n");
            out.flush();
            anwser = in.readLine();
            if(anwser.indexOf("250")!=0)  return "Error";

        } catch (Exception e) {
            log.error("Sendmail verify error on: "+name+". "+e);
            return "Error";
        }
        anwser=anwser.substring(4);
        return anwser;
    }

    /**
     * gives all the members of a mailinglist 
     * ..unchanged..
     */
    public Vector expand(String name) {
        String anwser="";
        Vector ret = new Vector();

        /** Connect to mail-host **/
        if (!connect(smtphost,25)) return ret;

        try {
            out.writeBytes("EXPN "+name+"\r\n");
            out.flush();
            while (true) {
                anwser = in.readLine();
                if(anwser.indexOf("250")==0) {
                    ret.addElement(anwser.substring(4));
                }
                if(anwser.indexOf("-")!=3) break;
            }
        } catch (Exception e) {
            log.error("Sendmail expand error on:"+name+". "+e);
            return new Vector();
        }
        return ret;
    }

    public String getModuleInfo() {
        return("Sends mail through JavaMail using a smtphost, Case Roole");
    }
}
