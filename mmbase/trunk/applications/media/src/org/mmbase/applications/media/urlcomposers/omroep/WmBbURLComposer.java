/*
  
This software is OSI Certified Open Source Software.
OSI Certified is a certification mark of the Open Source Initiative.
  
The license (Mozilla version 1.0) can be read at the MMBase site.
See http://www.MMBase.org/license
  
*/

package org.mmbase.applications.media.urlcomposers.omroep;

import org.mmbase.module.core.MMObjectNode;
import java.util.*;

/**
 *
 * @author Michiel Meeuwissen
 * @version $Id: WmBbURLComposer.java,v 1.4 2003-07-11 13:57:02 vpro Exp $
 * @since MMBase-1.7
 */
public class WmBbURLComposer extends WmSbURLComposer {
    public WmBbURLComposer(MMObjectNode provider, MMObjectNode source, MMObjectNode fragment, Map info, List cacheExpireObjects) {
        super(provider, source, fragment, info, cacheExpireObjects);
    }
    protected String getBandPrefix() {
        return "bb.";
    }
    protected String getBand() {
        return "breedband";
    }
}


