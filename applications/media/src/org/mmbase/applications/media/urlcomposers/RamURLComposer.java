/*
  
This software is OSI Certified Open Source Software.
OSI Certified is a certification mark of the Open Source Initiative.
  
The license (Mozilla version 1.0) can be read at the MMBase site.
See http://www.MMBase.org/license
  
*/

package org.mmbase.applications.media.urlcomposers;

import org.mmbase.module.core.*;
import org.mmbase.util.logging.*;
import org.mmbase.applications.media.Format;
import java.util.*;
import java.net.*;


/**
 * Provides the functionality to create URL's (or URI's) for a certain
 * fragment, source, provider combination.
 *
 * This servlet mapping is supposed:
  <pre>
 &lt;servlet-mapping&gt;
    &lt;servlet-name&gt;mediafragment&lt;/servlet-name&gt;
    &lt;url-pattern&gt;/mediafragment.*&lt;/url-pattern&gt;
  &lt;/servlet-mapping&gt;
</pre>
 *
 * @author Michiel Meeuwissen
 * @version $Id: RamURLComposer.java,v 1.7 2003-02-05 15:18:01 michiel Exp $
 * @since MMBase-1.7
 */
public class RamURLComposer extends FragmentURLComposer { // also for wmp/asx
    private static Logger log = Logging.getLoggerInstance(RamURLComposer.class.getName());
    
    private final static String SERVLET_MAPPING = "/mediafragment"; // todo make configurable/ read from web.xml 


    protected  Format          format;
    public RamURLComposer(MMObjectNode provider, MMObjectNode source, MMObjectNode fragment, Map info) {
        super(provider, source, fragment, info);
        this.format = Format.get(source.getIntValue("format"));
    }
    protected StringBuffer  getURLBuffer() {
        return new StringBuffer(SERVLET_MAPPING + "." + format + "?fragment=" + (fragment == null ? "" : "" + fragment.getNumber()) + "&format=" + format);

        // todo, perhaps simply the right source number should be passed
    }

    public Format  getFormat()   { 
        if (format == Format.RM)  return Format.RAM; 
        if (format == Format.ASF) return Format.WMP; 
        return format;
    } 

}
