/* 

This software is OSI Certified Open Source Software.
OSI Certified is a certification mark of the Open Source Initiative.

The license (Mozilla version 1.0) can be read at the MMBase site.
See http://www.MMBase.org/license

*/
package org.mmbase.module;

import org.mmbase.module.CounterImplementationInterface;

import org.mmbase.module.ProcessorModule;
import org.mmbase.module.CounterInterface;
import org.mmbase.module.sessionInfo;
import org.mmbase.util.scanpage;

import org.mmbase.util.logging.Logger;
import org.mmbase.util.logging.Logging;

/**
 * Class Counter
 * 
 * @javadoc
 */

public class Counter extends  ProcessorModule implements CounterInterface {

    //  ---------------------------------------------------------------------------------- 
    private static Logger log = Logging.getLoggerInstance(Counter.class.getName()); 
    
    //  ---------------------------------------------------------------------------------- 
    public Counter() {
            
    }
    //  ---------------------------------------------------------------------------------- 

    public void init() {
    }

    //  ---------------------------------------------------------------------------------- 

    public String getTag( String part, sessionInfo session, scanpage sp ) {
        CounterImplementationInterface counter;
        String result = null;
        int i;
        String params="";

        if (part != null && !part.equals("")) {
            part = part.trim();
        }

        if (log.isDebugEnabled()) { 
            log.debug("getTag(" + part + ")");
        }

        // check what counter this tag tags to
        // -----------------------------------

        part = part.trim();

        i=part.indexOf(' ');
        if (i!=-1) {
            params=part.substring(i+1);
            part=part.substring(0,i);
        }
        if (log.isServiceEnabled()) {
            log.service("getTag() : module=" + part + " params=" + params);
        }
        counter = (CounterImplementationInterface) getModule(part);

        if( counter != null ) {
            result = counter.getTag( params, session, sp );
        } else {
            log.error("module "+part+" is not found and loaded!");
        }
        // debug("getTag(): result:"+result);
        return result;
    }
}
