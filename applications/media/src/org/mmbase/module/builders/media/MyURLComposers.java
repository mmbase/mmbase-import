/*
  
This software is OSI Certified Open Source Software.
OSI Certified is a certification mark of the Open Source Initiative.
  
The license (Mozilla version 1.0) can be read at the MMBase site.
See http://www.MMBase.org/license
  
*/

package org.mmbase.module.builders.media;

import org.mmbase.module.core.*;
import org.mmbase.util.logging.*;
import java.util.*;
import java.net.*;
import java.text.*;


/**
 * An example. URL's from these kind of URLComposers can contain 'start' and 'end' arguments.
 *
 * @author Michiel Meeuwissen
 * @version $Id: MyURLComposers.java,v 1.4 2003-01-14 20:36:20 michiel Exp $
 * @since MMBase-1.7
 */
public class MyURLComposers extends MediaURLComposers {
    
    private static Logger log = Logging.getLoggerInstance(MyURLComposers.class.getName());

    /** 
     * Adds 'start' and 'end' parameters using the fragment.
     */
    protected class MyResponseInfo extends MediaURLComposers.MediaResponseInfo {

        MyResponseInfo(MMObjectNode composer, MMObjectNode provider, MMObjectNode source, MMObjectNode fragment, Map info) {
            super(composer, provider, source, fragment, info);
        }
        public Format getFormat() {
            Format format = super.getFormat();
            if (composer.getStringValue("rootpath").startsWith("/cgi-bin")) {
                if (format == Format.RM)  return Format.RAM;
                if (format == Format.ASF) return Format.WMP;                
            }
            
            return format;
        }

        public String getURL() {
            String url      = source.getStringValue("url");
            String rootpath = composer.getStringValue("rootpath");
            String host = provider.getStringValue("host");
            if (rootpath.startsWith("/cgi-bin")) {
                host = "cgi.omroep.nl";
            }
                       
            StringBuffer args = new StringBuffer(source.getStringValue("url"));
            
            
            if (rootpath.startsWith("%")) {
                int lastSlash = url.lastIndexOf('/');
                String existingPrefix = url.substring(lastSlash + 1, lastSlash + 4);
                if (existingPrefix.equals("sb.") || existingPrefix.equals("bb.")) { // remove existing prefix.
                    args.delete(lastSlash + 1, lastSlash + 4);
                }
                String insert = rootpath.substring(1);
                args.insert(lastSlash + 1, insert + ".");
            } else {
                args.insert(0, rootpath);
            }
            getArgs(args);            
            return composer.getStringValue("protocol") + "://" + host + args.toString();
        }
    }
    
    
    protected ResponseInfo createResponseInfo(MMObjectNode composer, MMObjectNode provider, MMObjectNode source, MMObjectNode fragment, Map info) {
        return new MyResponseInfo(composer, provider, source, fragment, info);           
    };

}

