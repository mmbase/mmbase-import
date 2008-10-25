/*

This software is OSI Certified Open Source Software.
OSI Certified is a certification mark of the Open Source Initiative.

The license (Mozilla version 1.0) can be read at the MMBase site.
See http://www.MMBase.org/license

*/
package org.mmbase.framework.basic;
import java.util.*;

import org.mmbase.framework.*;
import org.mmbase.util.functions.*;

/**

 *
 * @author Michiel Meeuwissen
 * @version $Id: Url.java,v 1.2 2008-10-25 08:58:47 michiel Exp $
 * @since MMBase-1.9
 * @todo EXPERIMENTAL
 */

public class Url {
    private final String url;
    private int quality = 0;
    public Url(String url) {
        this.url = url;
    }
    public Url(String url, int q) {
        this.url = url;
        this.quality = q;
    }
    public String getUrl() {
        return url;
    }
    public int getQuality() {
        return quality;
    }

    public static final Url NOT = new Url(null, Integer.MIN_VALUE);


    public String toString() {
        return url == null ? "NULL" : url;
    }
}
