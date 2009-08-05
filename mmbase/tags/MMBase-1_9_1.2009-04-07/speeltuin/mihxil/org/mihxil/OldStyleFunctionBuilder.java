package org.mihxil;

import org.mmbase.module.core.*;
import org.mmbase.util.logging.*;
import java.util.*;

class OldStyleFunctionBuilder extends TestBuilder {

    private static Logger log = Logging.getLoggerInstance(OldStyleFunctionBuilder.class.getName());

    protected Object executeFunction(MMObjectNode node, String function, String arguments) {
        if (log.isDebugEnabled()) { 
            log.debug("executeFunction  " + function + "(" + arguments + ") on " + node);
        }
        if (function.equals("test")) {
            return "TeSt";
        } else if (function.equals("test1")) {
            return "TeSt-" + arguments;
        } else if (function.equals("test2")) {
            List args = getFunctionParameters(arguments);
            return "TeSt-" + args.get(0) + "-" + args.get(1);
        }
        log.debug("Function not matched in mediafragments");
        return super.executeFunction(node, function, arguments);
    }

    public static void main (String[] args) {
        doTest(new OldStyleFunctionBuilder());
    }
}
