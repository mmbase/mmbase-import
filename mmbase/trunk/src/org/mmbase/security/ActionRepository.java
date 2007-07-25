/*

This software is OSI Certified Open Source Software.
OSI Certified is a certification mark of the Open Source Initiative.

The license (Mozilla version 1.0) can be read at the MMBase site.
See http://www.MMBase.org/license

*/
package org.mmbase.security;

/**
 * @author Michiel Meeuwissen
 * @version $Id: ActionRepository.java,v 1.2 2007-07-25 07:09:41 michiel Exp $
 * @since MMBase-1.9
 */
public abstract class ActionRepository extends Configurable {
    
    public abstract void add(Action a);

    public abstract Action get(String name);
}
