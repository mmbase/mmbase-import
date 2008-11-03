/*

This software is OSI Certified Open Source Software.
OSI Certified is a certification mark of the Open Source Initiative.

The license (Mozilla version 1.0) can be read at the MMBase site.
See http://www.MMBase.org/license

*/
package org.mmbase.module.lucene;

import org.mmbase.module.Module;
import org.mmbase.applications.crontab.*;
import org.mmbase.util.functions.Parameters;

/**
 * Can be scheduled in MMBase crontab.

 * @author Michiel Meeuwissen
 * @version $Id: FullIndex.java,v 1.4 2008-07-30 06:05:32 michiel Exp $
 **/
public class FullIndex extends  AbstractCronJob {

    public void run() {
        Lucene lucene = (Lucene) Module.getModule("lucene");
        if (lucene != null) {
            Parameters params = lucene.fullIndexFunction.createParameters();
            String index = cronEntry.getConfiguration();
            params.set("index", index);
            lucene.fullIndexFunction.getFunctionValue(params);
        } else {
            throw new RuntimeException("Lucene module not found");
        }
    }
}
