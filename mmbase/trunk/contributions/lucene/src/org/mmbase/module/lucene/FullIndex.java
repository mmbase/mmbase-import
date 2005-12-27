/*

This software is OSI Certified Open Source Software.
OSI Certified is a certification mark of the Open Source Initiative.

The license (Mozilla version 1.0) can be read at the MMBase site.
See http://www.MMBase.org/license

*/
package org.mmbase.module.lucene;

import org.mmbase.module.Module;

/**
 * Can be scheduled in MMBase crontab.

 * @author Michiel Meeuwissen
 * @version $Id: FullIndex.java,v 1.1 2005-12-27 15:45:06 michiel Exp $
 **/
public class FullIndex implements Runnable {
    
    public void run() {
        Lucene lucene = (Lucene) Module.getModule("lucene");
        if (lucene != null) {
            lucene.fullIndexFunction.getFunctionValue(null);
        }
    }
}
