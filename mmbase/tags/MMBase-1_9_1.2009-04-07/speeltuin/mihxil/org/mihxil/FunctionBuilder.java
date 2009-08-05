package org.mihxil;

import org.mmbase.util.logging.*;
import org.mmbase.module.core.*;
import java.util.*;

class FunctionBuilder extends TestBuilder {
    private static Logger log = Logging.getLoggerInstance(FunctionBuilder.class.getName());

    protected Object executeFunction(MMObjectNode node, String function, List args) {
        if (log.isDebugEnabled()) { 
            log.debug("executeFunction  " + function + "(" + args + ") on " + node);
        }
        if (function.equals("info")) {
            List empty = new Vector();
            java.util.Map info = (java.util.Map) super.executeFunction(node, function, empty);
            info.put("test", "() ");
            info.put("test1", "(<>) ");
            info.put("test2", "(<,>) ");
            if (args == null || args.size() == 0) {
                return info;
            } else {
                return info.get(args.get(0));
            }            
        } else if (function.equals("test")) {
            return "TeSt";
        } else if (function.equals("test1")) {
            return "TeSt-" + args.get(0);
        } else if (function.equals("test2")) {
            return "TeSt-" + args.get(0) + "-" + args.get(1);
        }
        log.debug("Function not matched in mediafragments");
        return super.executeFunction(node, function, args);
    }

    
    public static void main (String[] args) {
        doTest(new FunctionBuilder());
    }

}

