/*

This software is OSI Certified Open Source Software.
OSI Certified is a certification mark of the Open Source Initiative.

The license (Mozilla version 1.0) can be read at the MMBase site.
See http://www.MMBase.org/license

*/
package org.mmbase.module;

import java.util.Map;
import javax.mail.internet.*;
import org.mmbase.util.Mail;

/**
 * This interface has only one implementation.
 */

public interface SendMailInterface {

    /**
     * @javadoc
     */
    public boolean sendMail(String from, String to, String data);
    /**
     * @javadoc
     */
    public boolean sendMail(String from, String to, String data, Map<String, String> headers);
    /**
     * @javadoc
     */
    public boolean sendMail(Mail mail);


    /**
     * Sends a 'multipart' mail.
     *
     */
    public boolean sendMultiPartMail(String from, String to, Map<String, String> headers, MimeMultipart mmpart);


}
