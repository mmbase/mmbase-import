package org.mihxil;

import org.mmbase.module.core.*;
import org.mmbase.util.logging.*;
import java.util.*;

class TestBuilder extends MMObjectBuilder {
    private static Logger log = Logging.getLoggerInstance(TestBuilder.class.getName());
    private static final int times = 200000;
    

    final static private void doOneTest(MMObjectBuilder builder, MMObjectNode node, String arg) {
        long start,end;
        log.info("----" + arg);
        log.info(node.getValue(arg));
        start = System.currentTimeMillis();
        for (int i = 0; i < times; i++) {
            node.getValue(arg);
        }
        end = System.currentTimeMillis();
        log.info("Time for  " + arg + " : "  + (end - start) + " ms");
        
    }


    final static private void doOneTest(MMObjectBuilder builder, MMObjectNode node, String func, List args) {
        long start,end;        
        log.info("----" + func);
        log.info(node.getFunctionValue(func, args));
        start = System.currentTimeMillis();
        for (int i = 0; i < times; i++) {
            // to make an honest comparison, we need also take into account the construction of the list.
            List a;
            if (args != null) {
                a = new ArrayList();
                a.addAll(args);
            } else {
                a = null;
            }
            node.getFunctionValue(func, a);
        }
        end = System.currentTimeMillis();
        log.info("Time for  " + func + " : "  + (end - start) + " ms");
        
    }



    final static protected void doTest(MMObjectBuilder builder) {
        //log.setLevel(org.mmbase.util.logging.Level.DEBUG);
        MMObjectNode node = new MMObjectNode(builder);

        log.info("Testing class " + builder.getClass().getName());
        log.info("============= (calling with string)");
        doOneTest(builder, node, "test()");
        doOneTest(builder, node, "test1(one)");
        doOneTest(builder, node, "test2(one,two)");

        log.info("============== (calling with list)");
        doOneTest(builder, node, "test",  null);
        List args = new ArrayList();
        doOneTest(builder, node, "test", args); 
        args.add("one");
        doOneTest(builder, node, "test1", args); 
        args.add("two");
        doOneTest(builder, node, "test2", args);
    }

}
