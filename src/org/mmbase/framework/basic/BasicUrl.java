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

 * @author Michiel Meeuwissen
 * @version $Id$
 * @since MMBase-1.9
 */

public  class BasicUrl extends Url {
    private final String url;
    public BasicUrl(UrlConverter uc, String url) {
        super(uc);
        this.url = url;
    }
    public BasicUrl(UrlConverter uc, String url, int quality) {
        super(uc, quality);
        this.url = url;
    }
    public BasicUrl(Url u, int quality) {
        super(u.getUrlConverter(), quality);
        this.url = u.getUrl();
    }
    public String getUrl() {
        return url;
    }


}
