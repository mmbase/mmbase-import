/*
  
This software is OSI Certified Open Source Software.
OSI Certified is a certification mark of the Open Source Initiative.
  
The license (Mozilla version 1.0) can be read at the MMBase site.
See http://www.MMBase.org/license
  
*/

package org.mmbase.applications.media.urlcomposers.omroep;

import org.mmbase.module.core.MMObjectNode;
import java.util.Map;
import java.util.Locale;
/**
 *
 * @author Michiel Meeuwissen
 * @version $Id: RealBbURLComposer.java,v 1.2 2003-02-17 09:11:29 michiel Exp $
 * @since MMBase-1.7
 */
public class RealBbURLComposer extends RealSbURLComposer {
    public RealBbURLComposer(MMObjectNode provider, MMObjectNode source, MMObjectNode fragment, Map info) {
        super(provider, source, fragment, info);
    }
    protected String getBandPrefix() {
        return "bb.";
    }
    public String getGUIIndicator(Locale locale) {
        return super.getGUIIndicator(locale) + " (breedband)";
    }


}


