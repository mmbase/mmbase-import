/*

This software is OSI Certified Open Source Software.
OSI Certified is a certification mark of the Open Source Initiative.

The license (Mozilla version 1.0) can be read at the MMBase site.
See http://www.MMBase.org/license

*/
package org.mmbase.module;

import java.util.*;
import org.mmbase.util.*;

/**
 * @application Mail
 * @javadoc
 */

public interface SendMailInterface {
    /**
     * @javadoc
     */
    public boolean sendMail(String from, String to, String data);
    /**
     * @javadoc
     */
    public boolean sendMail(String from, String to, String data, Map headers);
    /**
     * @javadoc
     */
    public boolean sendMail(Mail mail);
    /**
     * checks the e-mail address
     */
    public String verify(String name);
    /**
     * @javadoc
     */
    public List   expand(String name);

}
