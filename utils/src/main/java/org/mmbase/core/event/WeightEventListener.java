/*
 * This software is OSI Certified Open Source Software.
 * OSI Certified is a certification mark of the Open Source Initiative. The
 * license (Mozilla version 1.0) can be read at the MMBase site. See
 * http://www.MMBase.org/license
 */
package org.mmbase.core.event;

/**
 *
 * @author Michiel Meeuwissen
 * @since MMBase-1.9.6
 * @version $Id$
 */
public interface WeightEventListener extends EventListener {
    public int getWeight();

}
