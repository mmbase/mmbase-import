/*

This software is OSI Certified Open Source Software.
OSI Certified is a certification mark of the Open Source Initiative.

The license (Mozilla version 1.0) can be read at the MMBase site.
See http://www.MMBase.org/license

*/
package org.mmbase.security;
import java.util.*;

/**
 * @author Michiel Meeuwissen
 * @version $Id: ActionRepository.java,v 1.3 2007-07-25 07:32:01 michiel Exp $
 * @since MMBase-1.9
 */
public abstract class ActionRepository extends Configurable {
    
    public abstract void add(Action a);

    public abstract Action get(String name);

    public abstract Collection<Action> get();
}
