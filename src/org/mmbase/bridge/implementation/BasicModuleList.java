/*

This software is OSI Certified Open Source Software.
OSI Certified is a certification mark of the Open Source Initiative.

The license (Mozilla version 1.0) can be read at the MMBase site.
See http://www.MMBase.org/license

*/

package org.mmbase.bridge.implementation;

import java.util.Collection;
import org.mmbase.bridge.*;

/**
 * A list of Modules
 *
 * @author Pierre van Rooden
 * @version $Id$
 */
public class BasicModuleList extends BasicList<Module> implements ModuleList {

    BasicModuleList() {
        super();
    }

    BasicModuleList(Collection<? extends Module> c) {
        super(c);
    }

    public Module getModule(int index) {
        return get(index);
    }

    public ModuleIterator moduleIterator() {
        return new BasicModuleIterator();
    };

    protected class BasicModuleIterator extends BasicIterator implements ModuleIterator {

        public Module nextModule() {
            return next();
        }

        public Module previousModule() {
            return previous();
        }

    }

}
