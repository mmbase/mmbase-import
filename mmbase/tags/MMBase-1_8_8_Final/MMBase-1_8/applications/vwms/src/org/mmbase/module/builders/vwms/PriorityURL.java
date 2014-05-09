/*

This software is OSI Certified Open Source Software.
OSI Certified is a certification mark of the Open Source Initiative.

The license (Mozilla version 1.0) can be read at the MMBase site.
See http://www.MMBase.org/license

*/
package org.mmbase.module.builders.vwms;

import java.lang.String;
import org.mmbase.util.Sortable;

/**
 * Class PriorityURL
 *
 * @javadoc
 * @deprecated not used anywhere
 * @author vpro
 * @version $Id: PriorityURL.java,v 1.4 2004-10-08 10:48:07 pierre Exp $
 */
public class PriorityURL implements Sortable {
    public static int MIN_PRIORITY=0;
    public static int LOW_PRIORITY=20;
    public static int DEF_PRIORITY=80;
    public static int MEDIUM_PRIORITY=100;
    public static int HIGH_PRIORITY=150;
    public static int MAX_PRIORITY=160;

    private String url;
    private int priority;

    public PriorityURL(String url) {
        this(url,DEF_PRIORITY);
    }

    public PriorityURL(String url,int priority) {
        this.url=url;
        this.priority=priority;
    }

    public String getURL() {
        return url;
    }

    public int getPriority() {
        return priority;
    }

    public void setPriority(int priority) {
        this.priority=priority;
    }

    public void increasePriority() {
        priority++;
    }

    public void decreasePriority() {
        priority--;
    }

    public int hashCode() {
        return url.hashCode();
    }

    public int compare(Sortable otherone) {
        return ((PriorityURL)otherone).getPriority()-getPriority();
    }

    public String toString() {
        return priority+":"+url;
    }
}
