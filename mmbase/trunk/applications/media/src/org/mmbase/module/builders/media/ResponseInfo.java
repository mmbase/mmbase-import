/*
  
This software is OSI Certified Open Source Software.
OSI Certified is a certification mark of the Open Source Initiative.
  
The license (Mozilla version 1.0) can be read at the MMBase site.
See http://www.MMBase.org/license
  
*/

package org.mmbase.module.builders.media;

import org.mmbase.module.core.MMObjectNode;
import java.net.URL;
import java.util.Map;

/**
 * ResponseInfo is a wrapper/container class  around an URL.  It contains besides the
 * URL some extra meta information about it, like the original source
 * boject of the resource it represents and if it is currently
 * available or not.  An URL can be unavailable because of two
 * reasons: Because the provider is offline, or because the fragment
 * where it belongs to is not valid (e.g. because of publishtimes)
 *
 * It is used by the Media builders to pass around information (mainly
 * as entry in Lists)
 *
 * @author Michiel Meeuwissen
 * @version $Id: ResponseInfo.java,v 1.2 2003-01-08 22:23:07 michiel Exp $
 * @todo    Move to org.mmbase.util.media, I think
 */

public class ResponseInfo  {
    private URL          url;
    private MMObjectNode source;
    private boolean      available;
    private Map          info;
    ResponseInfo(URL u, MMObjectNode s,  boolean a, Map i) {
        url = u; source = s; info = i; available = a;
    }
    ResponseInfo(URL u, MMObjectNode s) {
        this(u, s, true, null);
    }
    public URL          getURL()      { return url;  }
    public MMObjectNode getSource()   { return source;  }
    public boolean      isAvailable() { return available; }
    public Map          getInfo()     { return info; }
    
    public String toString() {
        if (available) {
            return url.toString();
        } else {
            return "{" + url.toString() + "}";
        }
    }
}
