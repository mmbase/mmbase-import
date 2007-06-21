/*

This software is OSI Certified Open Source Software.
OSI Certified is a certification mark of the Open Source Initiative.

The license (Mozilla version 1.0) can be read at the MMBase site.
See http://www.MMBase.org/license

*/

package org.mmbase.util.jumpers;

import org.mmbase.module.core.MMObjectNode;
import org.mmbase.util.logging.Logger;
import org.mmbase.util.logging.Logging;

import org.mmbase.util.jumpers.strategies.JumperStrategyFactory;

/**
* This is the factory-class for jumper-strategies.
*
* Add a strategy by adding the following lines in jumpers.xml:
*   <property name="calculator.default.classname">org.mmbase.util.jumpers.JumperCalculator</property>
*   <property name="calculator.default.strategies">org.mmbase.util.jumpers.strategies.ScanStrategy,org.mmbase.util.jumpers.strategies.UrlStrategy</property>
*
* The order of the strategies is significant!
*
* This factory gets a node and will query all available strategies untill a strategy.contains(node)
* returns true and after which it will return stategy.calculate(node).
*
* @author Marcel Maatkamp, VPRO Digitaal
* @version $Id: JumperCalculator.java,v 1.2 2007-06-21 16:04:56 nklasens Exp $
*/

public class JumperCalculator { 

    private static final Logger log = Logging.getLoggerInstance(JumperCalculator.class);

    protected static JumperStrategyFactory factory = null;

    /** 
    * Empty constructor.
    * Leave this in, so that other calculators can subclass
    */
    public JumperCalculator() {}

    /**
    * List of strategies to be used by the calculators.
    *
    * Add a strategy by adding the following lines in jumpers.xml:
    *   <property name="calculator.default.classname">org.mmbase.util.jumpers.JumperCalculator</property>
    *   <property name="calculator.default.strategies">org.mmbase.util.jumpers.strategies.ScanStrategy,org.mmbase.util.jumpers.strategies.UrlStrategy</property>
    *
    * @param strategies a comma-seperated list of fully qualified classnames of strategies
    */
    public JumperCalculator(String strategies) { 
        factory = new JumperStrategyFactory(strategies);
    }

    /**
    * Calculate an url for this node.
    *
    * @param node for which an url has to be calculated
    * @return url or null if it is not found
    */
    public String calculate(MMObjectNode node) { 
        if(node == null) {
            throw new IllegalArgumentException("node("+node+") is null!");
        }

        String result = JumperStrategyFactory.handle(node);
        if(result != null) {
            log.debug("found " + node.getBuilder().getTableName() + "(" + node.getNumber() + "): url(" + result + ")");
        }

        return result;
    }

    // -----------------------------------------

    /**
    * test-method; when called will execute all tests for all enabled strategies
    */
    public boolean test() { 
        boolean result = false;

        long starttime = System.currentTimeMillis();
        result = factory.test();
        long stoptime = System.currentTimeMillis();
        if(result) {
            log.info("tests OK in "+((stoptime - starttime)/1000.00)+" sec.");
        } else {
            log.info("tests FAILED in "+((stoptime - starttime)/1000.00)+" sec.");
        }

        return result;
    }
}
