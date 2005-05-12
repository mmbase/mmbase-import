/*

This software is OSI Certified Open Source Software.
OSI Certified is a certification mark of the Open Source Initiative.

The license (Mozilla version 1.0) can be read at the MMBase site.
See http://www.MMBase.org/license

*/
package org.mmbase.util;
import edu.emory.mathcs.backport.java.util.concurrent.*;
import java.io.*;
/**
 * Generic MMBase Thread Pools
 *
 * @since MMBase 1.8
 * @author Michiel Meewissen
 * @version $Id: ThreadPools.java,v 1.1 2005-05-12 15:37:44 michiel Exp $
 */
public abstract class ThreadPools {
    
    /**
     * Generic Thread Pools which can be used by 'filters'.
     */
    public static final Executor filterExecutor = Executors.newCachedThreadPool();

}
