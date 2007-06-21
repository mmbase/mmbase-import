/*

This software is OSI Certified Open Source Software.
OSI Certified is a certification mark of the Open Source Initiative.

The license (Mozilla version 1.0) can be read at the MMBase site.
See http://www.MMBase.org/license

*/

package org.mmbase.util.jumpers.strategies;

import java.util.List;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.StringTokenizer;

import org.mmbase.util.logging.Logger;
import org.mmbase.util.logging.Logging;

import org.mmbase.module.core.MMBase;
import org.mmbase.module.core.MMObjectNode;
import org.mmbase.module.builders.Jumpers;

/**
* This is the factory-class for jumper-strategies.
*
* Add a strategy by adding the following lines in jumpers.xml:
*   <property name="calculator.default.classname">org.mmbase.util.jumpers.JumperCalculator</property>
*   <property name="calculator.default.strategies">org.mmbase.util.jumpers.strategies.ScanStrategy, org.mmbase.util.jumpers.strategies.UrlStrategy</property>
*
* The order of the strategies is significant!
*
* This factory gets a node and will query all available strategies untill a strategy.contains(node) 
* returns true and after which it will return stategy.calculate(node).
*
* @author Marcel Maatkamp, VPRO Digitaal
* @version $Id: JumperStrategyFactory.java,v 1.2 2007-06-21 16:04:56 nklasens Exp $
*/


public class JumperStrategyFactory {

    private static final Logger log = Logging.getLoggerInstance(JumperStrategyFactory.class);

    private static Jumpers jumpers;
    private static List<JumperStrategy> strategies = new ArrayList<JumperStrategy>();

    /**
    * Add available strategies to the pool of strategies.
    *
    * If you enable a strategy in jumpers.xml, this class will instantiate the strategy
    * and add it to the pool of available strategies. When a jumper requests an url, it
    * will walk through all available strategies untill it finds a strategy willing to 
    * solve the url.
    * 
    * The order of the strategies is significant!
    *
    * @see org.mmbase.util.jumpers.strategies.JumperStrategy#contains(MMObjectNode)
    * @see org.mmbase.util.jumpers.strategies.JumperStrategy#handle(MMObjectNode)
    * @param strategynames comma-seperated list of fully-qualified strategynames
    */

    public JumperStrategyFactory(String strategynames) { 
        StringTokenizer tok = new StringTokenizer(strategynames,",");
        while(tok.hasMoreTokens()) {
            String strategy_classname = tok.nextToken().trim();
            try { 
                JumperStrategy strategy = (JumperStrategy)Class.forName(strategy_classname).newInstance();
                JumperStrategyFactory.strategies.add(strategy);
                log.info("using strategy["+strategy_classname+"]");
            } catch(java.lang.ClassNotFoundException e) {
                log.fatal("strategy name("+strategy_classname+"): Exception: "+e);
            } catch(java.lang.InstantiationException e) {
                log.fatal("strategy with name("+strategy_classname+"): Exception: "+e);
            } catch(java.lang.IllegalAccessException e) {
                log.fatal("strategy with name("+strategy_classname+"): Exception: "+e);
            }
        }
    }

    /**
    * calculates an url for this node.
    *
    * It will query all available strategies and return an url if
    * a strategy.contains() returns true and strategy.calculate() returns an url
    *
    * The factory will walk through its strategies like:
    *
    *   for all enables strategies AND url not found {
    *       if(strategy[x].contains(node))
    *           return strategy[x].calculate(node)
    *   }
    *
    * @see org.mmbase.util.jumpers.strategies.JumperStrategy#contains(MMObjectNode)
    * @see org.mmbase.util.jumpers.strategies.JumperStrategy#handle(MMObjectNode)
    * @param node node for which an url has to be calculated for
    * @return the url for this node
    */
    public static String handle(MMObjectNode node) { 
        if(jumpers == null) {
            jumpers = (Jumpers)MMBase.getMMBase().getMMObject("jumpers");
        }

        long starttime = System.currentTimeMillis();  
        String url = null;

        Iterator<JumperStrategy> i = strategies.iterator();
        while(i.hasNext()) { 
            JumperStrategy strategy = i.next();
            if(strategy.contains(node)) { 
                if (log.isDebugEnabled()) {
                    log.debug(node.getBuilder().getTableName() + "(" + node.getNumber() + "): " + strategy.getClass().getName() + " going to handle..");
                }
                String result = strategy.calculate(node);
                if (log.isServiceEnabled()) {
                    long stoptime = System.currentTimeMillis();  
                    log.service(node.getBuilder().getTableName() + "(" + node.getNumber() + "): " + strategy.getClass().getName() + "(" + result + ") calculated in [" + (stoptime - starttime) + "] millisecond(s)");
                }
                return result;
            }
        }
       
        if(url == null) {
            log.debug("cannot find strategy for " + node.getBuilder().getTableName() + "(" + node.getNumber() + ")");
        }

        return url;
    }

    /**
    * Perform test and output results into logfile.
    *
    * Tests are defined by adding them with 'add_test(number, "expected url");'
    */

    public boolean test() { 
        boolean result = false;
        Iterator<JumperStrategy> i = strategies.iterator();
        while(i.hasNext()) { 
            JumperStrategy strategy = i.next();
            result = strategy.test();
            if(!result)
                return result;
        }
        return result;
    }
}
