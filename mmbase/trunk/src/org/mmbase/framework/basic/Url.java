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
 * @version $Id: Url.java,v 1.1 2008-10-25 08:32:02 michiel Exp $
 * @since MMBase-1.9
 * @todo EXPERIMENTAL
 */

public class Url {
    private final String url;
    private int quality = 0;
    Url(String url) {
        this.url = url;
    }
    public String getUrl() {
        return url;
    }
    public int getQuality() {
        return quality;
    }
    public static final Url NOT = new Url(null);
    static {
        NOT.quality = Integer.MIN_VALUE;
    }
}
