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
 * @version $Id: FragmentURLComposer.java,v 1.5 2003-02-05 11:50:30 michiel Exp $
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

    public boolean equals(Object o) {
        if (o.getClass().equals(getClass())) {
            FragmentURLComposer r = (FragmentURLComposer) o;
            return 
                (fragment == null ? r.fragment == null : fragment.getNumber() == r.fragment.getNumber()) &&
                (source == null ? r.source == null : source.getNumber() == r.source.getNumber()) &&
                (provider == null ? r.provider == null : provider.getNumber() == r.provider.getNumber()) &&
                (info == null ? r.info == null : info.equals(r.info));
        }
        return false;
    }

}
