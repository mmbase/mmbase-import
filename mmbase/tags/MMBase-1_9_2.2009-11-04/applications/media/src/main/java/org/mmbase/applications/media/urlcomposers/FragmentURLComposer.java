/*

This software is OSI Certified Open Source Software.
OSI Certified is a certification mark of the Open Source Initiative.

The license (Mozilla version 1.0) can be read at the MMBase site.
See http://www.MMBase.org/license

 */

package org.mmbase.applications.media.urlcomposers;
import org.mmbase.applications.media.builders.MediaFragments;
import org.mmbase.module.core.MMObjectNode;
import org.mmbase.util.HashCodeUtil;

import java.util.*;

/**
 * A Fragment URLComposer is an URLComposer which can also use
 * information about the Fragment in the URL. Generally this means
 * that it can represent a fragments 'completely' so, with
 * information about start and stop times.
 *
 *
 * @author Michiel Meeuwissen
 * @author Rob Vermeulen (VPRO)
 * @version $Id$
 */
public class FragmentURLComposer extends URLComposer  {
    protected MMObjectNode fragment;


    @Override
    public void init(MMObjectNode provider, MMObjectNode source, MMObjectNode fragment, Map<String, Object> info, Set<MMObjectNode> cacheExpireObjects) {
        super.init(provider, source, fragment, info, cacheExpireObjects);

        if (cacheExpireObjects != null) {
            cacheExpireObjects.add(fragment);
        }

        this.fragment = fragment;
    }

    public MMObjectNode getFragment()   {
        return fragment;
    }

    @Override
    public boolean      isAvailable() {
        Boolean fragmentAvailable;
        if (fragment != null) {
            fragmentAvailable = (Boolean) fragment.getFunctionValue(MediaFragments.FUNCTION_AVAILABLE, null);
        } else {
            fragmentAvailable = Boolean.TRUE;
        }
        return fragmentAvailable.booleanValue() &&  super.isAvailable();
    }

    /**
     * @see java.lang.Object#equals(java.lang.Object)
     */
    @Override
    public boolean equals(Object o) {
        if (super.equals(o)) {
            FragmentURLComposer r = (FragmentURLComposer) o;
            return (fragment == null ? r.fragment == null : fragment.getNumber() == r.fragment.getNumber());
        }
        return false;
    }


    /**
     * @see java.lang.Object#hashCode()
     */
    @Override
    public int hashCode() {
        return HashCodeUtil.hashCode(super.hashCode(), fragment);
    }
}
