/*
  
This software is OSI Certified Open Source Software.
OSI Certified is a certification mark of the Open Source Initiative.
  
The license (Mozilla version 1.0) can be read at the MMBase site.
See http://www.MMBase.org/license
  
*/

package org.mmbase.applications.media.urlcomposers;
import org.mmbase.applications.media.builders.MediaProviders;
import org.mmbase.applications.media.builders.MediaSources;
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
 * @version $Id: URLComposer.java,v 1.4 2003-02-04 17:43:33 michiel Exp $
 */

public class URLComposer  {
    protected MMObjectNode  source;
    protected MMObjectNode  provider;
    protected Map           info;

    public URLComposer(MMObjectNode provider, MMObjectNode source, MMObjectNode fragment, Map info) {
        this(provider, source, info); // no frament necessary on default
    }

    protected URLComposer(MMObjectNode provider, MMObjectNode source, Map info) { 
        if (source   == null) throw new RuntimeException("Source may not be null in a URLComposer object");
        if (provider == null) throw new RuntimeException("Source may not be null in a URLComposer object");
        this.provider = provider;
        this.source   = source;
        this.info     = info;
        if (this.info == null) info = new java.util.Hashtable();
    }


    public MMObjectNode getSource()   { return source;  }
    public Map          getInfo()     { return info; }
    public Format       getFormat()   { return Format.get(source.getIntValue("format")); } 

    /**
     * Extension will normally create URL's differently. They override this function.
     */

    protected StringBuffer getURLBuffer() {
        StringBuffer buff = new StringBuffer(provider.getStringValue("protocol") + "://" + provider.getStringValue("host") + provider.getStringValue("rootpath") + source.getStringValue("url"));
        return buff;            
    }
    /**
     * Returns the URL as a String. To encourage efficient coding,
     * this method is final. Override getURLBuffer instead.
     */

    public final String  getURL() {
        return getURLBuffer().toString();
    }

    public boolean      isAvailable() { 
        boolean sourceAvailable    = (source != null && source.getIntValue("state") == MediaSources.STATE_DONE);
        boolean providerAvailable  = (provider.getIntValue("state") == MediaProviders.STATE_ON);
        return providerAvailable && sourceAvailable;
    }
    
    public String toString() {
        // for verboseness:
        String className = getClass().getName().substring(getClass().getName().lastIndexOf(".") + 1);
        if (isAvailable()) {
            return className + "/" + getFormat() + ": " + getURL();
        } else {
            return "{" + className + "/" +  getFormat() + ": " + getURL() + "}";
        }
    }
}
