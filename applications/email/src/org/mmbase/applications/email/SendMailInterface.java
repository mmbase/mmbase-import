/*

This software is OSI Certified Open Source Software.
OSI Certified is a certification mark of the Open Source Initiative.

The license (Mozilla version 1.0) can be read at the MMBase site.
See http://www.MMBase.org/license

*/
package org.mmbase.applications.email;

import org.mmbase.util.Mail;
import java.util.Map;
import javax.mail.internet.MimeMultipart;

/**
 * This extension of SendMailInterface also support multipart-mails.
 */

public interface SendMailInterface extends org.mmbase.module.SendMailInterface {

    /**
     * Sends a 'multipart' mail.
     */
    public boolean sendMultiPartMail(String from, String to, Map headers, MimeMultipart mmpart);


}
