/*
  
This software is OSI Certified Open Source Software.
OSI Certified is a certification mark of the Open Source Initiative.
  
The license (Mozilla version 1.0) can be read at the MMBase site.
See http://www.MMBase.org/license
  
*/

package org.mmbase.applications.media.urlcomposers;

import org.mmbase.module.core.MMObjectNode;
import java.util.Map;
/**
 *
 * @author Michiel Meeuwissen
 * @version $Id: OmroepBbURLComposer.java,v 1.1 2003-02-11 23:16:11 michiel Exp $
 * @since MMBase-1.7
 */
public class OmroepBbURLComposer extends OmroepSbURLComposer {
    public OmroepBbURLComposer(MMObjectNode provider, MMObjectNode source, MMObjectNode fragment, Map info) {
        super(provider, source, fragment, info);
    }
    protected String getBandPrefix() {
        return "bb.";
    }
}


