/*

This software is OSI Certified Open Source Software.
OSI Certified is a certification mark of the Open Source Initiative.

The license (Mozilla version 1.0) can be read at the MMBase site.
See http://www.MMBase.org/license

*/
package org.mmbase.applications.email;

import java.util.*;
import org.mmbase.util.*;

/**
 * @javadoc
 */

public interface SendMailInterface {
    public boolean sendMail(String from, String to, String data);
    public boolean sendMail(String from, String to, String data, Map headers);
    public boolean sendMail(Mail mail);
    /**
     * checks the e-mail address
     */
    public String verify(String name);
    public List   expand(String name);
}
