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
 * @version $Id: BasicModuleList.java,v 1.5 2002-01-31 10:05:11 pierre Exp $
 */
public class BasicModuleList extends BasicList implements ModuleList {
    private static Logger log = Logging.getLoggerInstance(BasicModuleList.class.getName());

    private CloudContext cloudcontext;

    /**
    * ...
    */
    BasicModuleList(Collection c, CloudContext cloudcontext) {
        super(c);
        this.cloudcontext=cloudcontext;
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

        public void set(Object o) {
            if (! (o instanceof Module)) {
                String message;
                message = "Object must be of type Module.";
                log.error(message);
                throw new BridgeException(message);
            }
            list.set(index, o);
        }
        public void add(Object o) {
            if (! (o instanceof Module)) {
                String message;
                message = "Object must be of type Module.";
                log.error(message);
                throw new BridgeException(message);
            }
            list.add(index, o);
        }

        public void set(Module m) {
            list.set(index, m);
        }

        public void add(Module m) {
            list.add(index, m);
        }


        public Module nextModule() {
            return (Module)next();
        }

    }

}
