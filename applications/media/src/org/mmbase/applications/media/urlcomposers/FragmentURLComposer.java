/*
  
This software is OSI Certified Open Source Software.
OSI Certified is a certification mark of the Open Source Initiative.
  
The license (Mozilla version 1.0) can be read at the MMBase site.
See http://www.MMBase.org/license
  
*/

package org.mmbase.applications.media.urlcomposers;
import org.mmbase.applications.media.builders.MediaFragments;
import org.mmbase.module.core.MMObjectNode;
import java.util.Map;
import java.util.Hashtable;

/**
 * A Fragment URLComposer is an URLComposer which can also use information about the Fragment in the URL.
 *
 *
 * @author Michiel Meeuwissen
 * @version $Id: FragmentURLComposer.java,v 1.3 2003-02-04 17:43:33 michiel Exp $
 * @todo    Move to org.mmbase.util.media, I think
 */

abstract public class FragmentURLComposer extends URLComposer  {
    protected MMObjectNode fragment;

    public FragmentURLComposer(MMObjectNode provider, MMObjectNode source, MMObjectNode fragment, Map info) {
        super(provider, source, info);
        this.fragment = fragment;
    }

    public boolean      isAvailable() { 
        Boolean fragmentAvailable;
        if (fragment != null) {
            fragmentAvailable = (Boolean) fragment.getFunctionValue(MediaFragments.FUNCTION_AVAILABLE, null);
        } else {
            fragmentAvailable = Boolean.TRUE;
        }
        return fragmentAvailable.booleanValue() &&  super.isAvailable();
    }

}
