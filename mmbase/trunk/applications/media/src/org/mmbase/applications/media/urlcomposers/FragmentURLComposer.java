/*
 
This software is OSI Certified Open Source Software.
OSI Certified is a certification mark of the Open Source Initiative.
 
The license (Mozilla version 1.0) can be read at the MMBase site.
See http://www.MMBase.org/license
 
 */

package org.mmbase.applications.media.urlcomposers;
import org.mmbase.applications.media.builders.MediaFragments;
import org.mmbase.module.core.MMObjectNode;
import org.mmbase.util.logging.*;

import java.util.*;

/**
 * A Fragment URLComposer is an URLComposer which can also use
 * information about the Fragment in the URL. Generally this means
 * that is can represent a fragments 'completely' so, with
 * information about start and stop times.
 *
 *
 * @author Michiel Meeuwissen
 * @author Rob Vermeulen (VPRO)
 */
abstract public class FragmentURLComposer extends URLComposer  {
    protected MMObjectNode fragment;
    
    public void init(MMObjectNode provider, MMObjectNode source, MMObjectNode fragment, Map info, Set cacheExpireObjects) {
        super.init(provider, source, fragment, info, cacheExpireObjects);
        
        if (cacheExpireObjects != null) {
            cacheExpireObjects.add(fragment);
        }
        
        this.fragment = fragment;
    }
    
    public MMObjectNode getFragment()   { return fragment; }
    
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