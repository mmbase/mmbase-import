/*

This software is OSI Certified Open Source Software.
OSI Certified is a certification mark of the Open Source Initiative.

The license (Mozilla version 1.0) can be read at the MMBase site.
See http://www.MMBase.org/license

*/
package org.mmbase.module;

import java.util.*;

import org.mmbase.util.*;
import org.mmbase.util.logging.*;

/**
 * @application SCAN
 * @javadoc
 * @rename Calc.java
 * @author Daniel Ockeloen
 * @version $Id: CALC.java,v 1.10.2.1 2007-07-24 20:55:37 michiel Exp $
 */
public class CALC extends ProcessorModule {
    // logging
    private static Logger log = Logging.getLoggerInstance(CALC.class.getName());

    /**
     * Generate a list of values from a command to the processor
     * @javadoc
     * @deprecated-now doesn't add any functionality
     */
     public Vector  getList(PageInfo sp,StringTagger tagger, String value) {
        String line = Strip.DoubleQuote(value,Strip.BOTH);
        StringTokenizer tok = new StringTokenizer(line,"-\n\r");
        if (tok.hasMoreTokens()) {
            String cmd=tok.nextToken();
        }
        return null;
    }

    /**
     * Execute the commands provided in the form values
     * @javadoc
     * @deprecated-now doesn't add any functionality
     */
    public boolean process(PageInfo sp, Hashtable cmds,Hashtable vars) {
        log.debug("CMDS="+cmds);
        log.debug("VARS="+vars);
        return false;
    }

    /**
     * Handle a $MOD command
     * @javadoc
     */
    public String replace(PageInfo sp, String cmds) {
        StringTokenizer tok = new StringTokenizer(cmds,"-\n\r");
        if (tok.hasMoreTokens()) {
            String cmd=tok.nextToken();
            while (tok.hasMoreTokens()) {
                cmd+="-"+tok.nextToken();
            }
            return doCalc(cmd);
        }
        log.warn("Calc replace was involed with no decent command");
        return "No command defined";
    }

    /**
     * @javadoc
     */
    String doCalc(String cmd) {
        log.debug("Calc module calculates "+cmd);
        ExprCalc cl=new ExprCalc(cmd);
        // marcel: annoying, upgraded this log to debug
        log.debug("Calc converts number Natural number");
        return ""+(int)(cl.getResult()+0.5);
    }

    /**
     * @javadoc
     */
    public String getModuleInfo() {
        return "Support routines simple calc, Daniel Ockeloen";
    }
}
