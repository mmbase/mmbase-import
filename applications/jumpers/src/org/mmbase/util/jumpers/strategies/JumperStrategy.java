/*

This software is OSI Certified Open Source Software.
OSI Certified is a certification mark of the Open Source Initiative.

The license (Mozilla version 1.0) can be read at the MMBase site.
See http://www.MMBase.org/license

*/

package org.mmbase.util.jumpers.strategies;

import java.util.Map;
import java.util.HashMap;
import java.util.Iterator;
import java.math.BigDecimal;

import org.mmbase.util.logging.Logger;
import org.mmbase.util.logging.Logging;

import org.mmbase.module.core.MMBase;
import org.mmbase.module.core.MMObjectNode;
import org.mmbase.module.builders.Jumpers;

import org.mmbase.bridge.*;

/**
* This is the baseclass for strategies.
*
* A strategy has to extend this class and provide its own methods for:
*   - contains(MMObjectNode)
*   - calculate(MMObjectNode)
*
* The contains(node) checks whether the strategy can/will handle this node.
* For example, it can check whether the node has a certain type of relation,
* is of a special type or is just of a type this strategy can handle.
*
* The calculate(node) will then try to calculate an url.
*
* @see #contains(MMObjectNode)
* @see #calculate(MMObjectNode)
*
* @author Marcel Maatkamp, VPRO Digitaal
* @version $Id: JumperStrategy.java,v 1.2 2007-06-21 16:04:56 nklasens Exp $
*/
public abstract class JumperStrategy { 

    private static final Logger log = Logging.getLoggerInstance(JumperStrategy.class);

    protected static MMBase     mmbase  = null; 
    protected static Jumpers    jumpers = null;

    protected Map<String, String> testset = new HashMap<String, String>();
    protected static Cloud cloud;

    static { 
        mmbase = MMBase.getMMBase();
        if(mmbase == null) {
            throw new AssertionError("mmbase could not be found!");
        }

        jumpers = (Jumpers)mmbase.getMMObject("jumpers");
        if(jumpers == null) {
            throw new AssertionError("builder jumpers could not be found!");
        }
    }

    /**
    * signals whether this strategy can calculate an url for this node.
    *
    * It will query the database and return true if this strategy can procude an url
    * The factory will walk through its strategies like:
    *
    *   for all enables strategies AND url not found {
    *       if(strategy[x].contains(node))
    *           return strategy[x].calculate(node)
    *   }
    *
    * @param node node for which an url has to be calculated for
    * @return url for this node
    */
    public abstract boolean contains(MMObjectNode node);

    /**
    * calculates an url for this node.
    *
    * call this method only when the contains(node) returns true.
    *
    * @see #contains(MMObjectNode)
    * @param node node for which an url has to be calculated for
    * @return the url for this node
    */
    public abstract String  calculate(MMObjectNode node);

    public void add_test(int key, String url) { 
        add_test("" + key, url);
    }

    public void add_test(String key, String url) { 
        testset.put(key, url);
    }

    public boolean test() { 
        boolean result = true;

        long starttime = System.currentTimeMillis();
        if(testset.size() > 0) { 
            log.info("strategy(" + getClass().getName() + "): test started..");
            
            Iterator<String> i = testset.keySet().iterator();
            while(i.hasNext() && result) { 
                long _starttime = System.currentTimeMillis();

                String key = i.next();
                String url = testset.get(key);
                MMObjectNode node = null;

                if(url != null && !url.equals("")) { 
                    node = jumpers.getNode(key);
                    if(("" + node.getNumber()).equals(key)) { 
                        if(contains(node)) { 
                            String calculated_url = calculate(node);
                            if(calculated_url != null && !calculated_url.equals("")) { 
                                if(!url.equals(calculated_url)) { 
                                    log.fatal("strategy(" + getClass().getName() + "): node(" + node.getNumber() + "): calculated(" + calculated_url + ") does not match expected url(" + url + ")");
                                    result = false;
                                } else {
                                    result = true;
                                }
                            } else { 
                                log.fatal("strategy(" + getClass().getName() + "): key(" + key + "): calculated_url(" + calculated_url + ") is not valid!");
                                result = false;
                            }
                        } else { 
                            log.fatal("strategy(" + getClass().getName() + "): key(" + key + "): not handled by this strategy!");
                            result = false;
                        }
                    } else { 
                        log.fatal("strategy(" + getClass().getName() + "): key(" + key + "): not a valid node(" + node + ")");
                        result = false;
                    }
                } else  { 
                    log.fatal("strategy(" + getClass().getName() + "): key(" + key + "): test_url(" + url + ") is not valid!");
                    result = false;
                }

                long _stoptime = System.currentTimeMillis();
                double _time = (_stoptime - _starttime)/1000.00;
                log.info("strategy(" + getClass().getName() + "): " + node.getBuilder().getTableName() + "(" + key + "): url(" + url + ") in " + _time + " sec.");
            }

        } else { 
            // not having a testset is not a failure
            log.info("strategy("+getClass().getName()+"): no tests defined for this strategy!");
            result = true;
        }

        long stoptime = System.currentTimeMillis();
        if(testset.size() > 0 && result) { 
            double time = (stoptime - starttime)/1000.00;
            BigDecimal bd = new BigDecimal(testset.size()/time);
            bd = bd.setScale(3,BigDecimal.ROUND_UP);
            double urls_sec = bd.doubleValue();

            log.info("strategy(" + getClass().getName() + "): all tests are ok, calculated " + testset.size() + " urls in " + time + " sec. (" + urls_sec + " urls/sec)");
        } else if(!result) { 
            log.warn("strategy(" + getClass().getName() + "): tests failed!");
        }

        return result;
    }
}
