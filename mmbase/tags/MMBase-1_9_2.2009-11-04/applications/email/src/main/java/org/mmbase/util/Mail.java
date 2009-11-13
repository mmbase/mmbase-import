/*

This software is OSI Certified Open Source Software.
OSI Certified is a certification mark of the Open Source Initiative.

The license (Mozilla version 1.0) can be read at the MMBase site.
See http://www.MMBase.org/license

*/
package org.mmbase.util;

import java.text.SimpleDateFormat;
import java.util.*;

/**
 * This mail-object gives persons the functionality to create mail
 * and send it with the SendMail-module.
 *
 * @application Mail
 * @author Rob Vermeulen
 * @version $Id$
 */
public class Mail {

    private static SimpleDateFormat formatter = new SimpleDateFormat("EEE, dd MMM yyyy HH:mm:ss 'GMT'", Locale.US);

    static {
         formatter.setTimeZone(TimeZone.getTimeZone("GMT"));
    }

    /**
     * All the mail headers defined for this mail object.
     */
    public Hashtable<String, String> headers = new Hashtable<String, String>();

    /**
     * The recipient of the mail
     */
    public String to = "";
    /**
     * The sender of the mail
     */
    public String from = "";
    /**
     * The message body text
     */
    public String text = "";

    /**
     * Create a Mail object.
     * The parameters define recipient and sender, but this does not create any mail headers.
     * @param to the recipient of teh mail
     * @param from teh sender of the mail
     */
    public Mail(String to, String from) {
        this.to = to;
        this.from = from;
        headers.put("To", to);
        headers.put("From", from);
    }

    /**
     * Set the mail message text.
     */
    public void setText(String text) {
        this.text=text;
    }

    /**
     * Sets the subject of the mail
     */
    public void setSubject(String subject) {
        headers.put("Subject",subject);
    }

    /**
     * Sets the time of the mail
     */
    public void setDate() {
        Date d = new Date();
        headers.put("Date",formatter.format(d));
    }

    /**
     * Sets given time to the mail
     */
    public void setDate(String date) {
        headers.put("Date",date);
    }

    /**
     * Tells the mail from who the mail is coming.
     * Does not alter the {@link #from} field.
     */
    public void setFrom(String from) {
        headers.put("From",from);
    }

    /**
     * Tells the mail for who the mail is.
     * Does not alter the {@link #to} field.
     */
    public void setTo(String to) {
        headers.put("To",to);
    }

    /**
     * Sends the message to all persons mentioned in the CC list.
     * Recipients of the message can see the names of the other recipients.
     */
    public void setCc(String cc) {
        headers.put("CC",cc);
    }

    /**
     * Sends the message to all persons mentioned in the BCC list.
     * Recipients of the message cannot see the names of the other recipients.
     */
    public void setBcc(String bcc) {
        headers.put("BCC",bcc);
    }

    /**
     * Adds a comment to the mail.
     */
    public void setComment(String comment) {
        headers.put("Comment",comment);
    }

    /**
     * Sets the Reply-to address
     */
    public void setReplyTo(String reply) {
        headers.put("Reply-to",reply);
    }

    /**
     * Sets a mail header to a fixed value
     * @return the old value of the header (<Code>null</code> if not earlier defined)
     */
    public String setHeader(String header,String value) {
        return headers.put(header,value);
    }

    /**
     * Retrieves the value of a mail header.
     * @return the value of the header (<Code>null</code> if not defined)
     */
    public String getHeader(String header) {
        return headers.get(header);
    }

    /**
     * Returns a description of the mail object.
     * Includes headers and message text.
     * @return the mail description
     */
    public String toString() {
        return "Mail -> Headers : "+headers+"\nText :\n"+text+"\n-";
    }
}

