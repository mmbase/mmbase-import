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
import org.mmbase.applications.media.builders.MediaProviders;
import java.util.*;
import java.net.*;


/**
 * Provides the functionality to create URL's (or URI's) for a certain
 * fragment, source, provider combination.
 *
 * @author Michiel Meeuwissen
 * @version $Id: MediaURLComposer.java,v 1.1 2003-02-03 17:50:33 michiel Exp $

 * @since MMBase-1.7
 */
public class MediaURLComposer extends FragmentURLComposer {
    private static Logger log = Logging.getLoggerInstance(MediaURLComposer.class.getName());

    protected MMObjectNode    composer;
    protected MMObjectNode    provider;
    
    public MediaURLComposer(MMObjectNode composer, MMObjectNode provider, MMObjectNode source, MMObjectNode fragment, Map info) {
        super(source, fragment, info);
        this.composer = composer; 
        this.provider = provider;
    }
    public String       getURL() {
        StringBuffer args = new StringBuffer(composer.getStringValue("protocol") + "://" + provider.getStringValue("host") + composer.getStringValue("rootpath") + source.getStringValue("url"));
        if (getFormat() == Format.RM) {
            getRMArgs(args);
        }
        return args.toString();
    }
    public boolean      isAvailable() { 
        boolean res = super.isAvailable();
        boolean providerAvailable = (provider.getIntValue("state") == MediaProviders.STATE_ON); // todo: use symbolic constant
        return res && providerAvailable;
    }
  
}
