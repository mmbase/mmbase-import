/*

This software is OSI Certified Open Source Software.
OSI Certified is a certification mark of the Open Source Initiative.

The license (Mozilla version 1.0) can be read at the MMBase site.
See http://www.MMBase.org/license

*/
package org.mmbase.bridge.jsp.taglib;

/**
 * Basic interface that parent should implement if they provide Lists.
 * For example the several NodeListTag's  provide a List.
 *
 * @author Michiel Meeuwissen 
 * @version $Id: ListProvider.java,v 1.4 2003-06-06 10:03:08 pierre Exp $ 
 */
public interface ListProvider extends ContextProvider {
    /**
     * @return the size of the list
     *
     */
    public int size();
    
    /**
     * @return the index of the current item in a list
     *
     */
    public int getIndex();

    /**
     * @return the current item in a list
     */
    
    public Object getCurrent();

    
    /**
     * @return a boolean indicating wether the field on which was
     * sorted is changed.
     *
     */
    public boolean isChanged();
}
