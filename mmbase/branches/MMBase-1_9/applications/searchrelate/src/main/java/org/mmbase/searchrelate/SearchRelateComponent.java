/*
 * This software is OSI Certified Open Source Software.
 * OSI Certified is a certification mark of the Open Source Initiative. The
 * license (Mozilla version 1.0) can be read at the MMBase site. See
 * http://www.MMBase.org/license
 */
package org.mmbase.searchrelate;
import org.mmbase.framework.*;
import org.mmbase.core.event.EventManager;

import org.mmbase.util.logging.Logger;
import org.mmbase.util.logging.Logging;

/**
 *
 * @author  Michiel Meeuwissen
 * @version $Id$
 */
public class SearchRelateComponent extends BasicComponent {
    private static final Logger LOG = Logging.getLoggerInstance(SearchRelateComponent.class);


    public SearchRelateComponent(String name) {
        super(name);
    }
    @Override
    public void init() {
        super.init();
        // Nothing to do after all
    }
}

