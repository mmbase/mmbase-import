/*

This software is OSI Certified Open Source Software.
OSI Certified is a certification mark of the Open Source Initiative.

The license (Mozilla version 1.0) can be read at the MMBase site.
See http://www.MMBase.org/license

*/

package org.mmbase.bridge.implementation;

import java.util.Collection;
import java.util.NoSuchElementException;
import org.mmbase.bridge.*;
import org.mmbase.module.core.*;
import org.mmbase.util.logging.*;

/**
 * A list of Modules
 *
 * @author Pierre van Rooden
 * @version $Id: BasicModuleList.java,v 1.8 2003-03-04 09:19:06 pierre Exp $
 */
public class BasicModuleList extends BasicList implements ModuleList {
    private static Logger log = Logging.getLoggerInstance(BasicModuleList.class.getName());

    BasicModuleList() {
        super();
    }

    /**
     * ...
     */
    BasicModuleList(Collection c) {
        super(c);
    }

    protected Object validate(Object o) throws ClassCastException {
        return (Module)o;
    }

    /**
     *
     */
    public Module getModule(int index) {
        return (Module)get(index);
    }

    /**
     *
     */
    public ModuleIterator moduleIterator() {
        return new BasicModuleIterator(this);
    };

    public class BasicModuleIterator extends BasicIterator implements ModuleIterator {

        BasicModuleIterator(BasicList list) {
            super(list);
        }

        public Module nextModule() {
            return (Module)next();
        }

        public Module previousModule() {
            return (Module)previous();
        }

    }

}
