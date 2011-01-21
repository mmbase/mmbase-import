/*
 * This software is OSI Certified Open Source Software.
 * OSI Certified is a certification mark of the Open Source Initiative. The
 * license (Mozilla version 1.0) can be read at the MMBase site. See
 * http://www.MMBase.org/license
 */
package org.mmbase.core.event;

import org.mmbase.security.*;

/**
 * @since MMBase-2.0
 */
public class SecurityLoaded extends SystemEvent.Collectable {
    private final MMBaseCop cop;
    public SecurityLoaded(MMBaseCop c) {
        cop = c;
    }
    public MMBaseCop getCop() {
        return cop;
    }
}
