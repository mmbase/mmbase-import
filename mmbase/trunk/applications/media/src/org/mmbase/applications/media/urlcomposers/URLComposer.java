/*
  
This software is OSI Certified Open Source Software.
OSI Certified is a certification mark of the Open Source Initiative.
  
The license (Mozilla version 1.0) can be read at the MMBase site.
See http://www.MMBase.org/license
  
*/

package org.mmbase.applications.media.urlcomposers;

import org.mmbase.module.core.MMObjectNode;
import org.mmbase.applications.media.Format;
import java.util.Map;

/**
 * URLComposer is a wrapper/container class  around an URL.  It contains besides the
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
 * @version $Id: URLComposer.java,v 1.1 2003-02-03 17:50:34 michiel Exp $
 */

abstract public class URLComposer  {
    protected MMObjectNode source;
    protected Map          info;
    abstract public String       getURL();
    abstract public boolean      isAvailable();
    public MMObjectNode getSource()   { return source;  }
    public Map          getInfo()     { return info; }
    public Format       getFormat()   { return Format.get(source.getIntValue("format")); } 
    
    public String toString() {
        if (isAvailable()) {
            return getFormat().toString() + ": " + getURL();
        } else {
            return "{" +  getFormat().toString() + ": " + getURL() + "}";
        }
    }
}
