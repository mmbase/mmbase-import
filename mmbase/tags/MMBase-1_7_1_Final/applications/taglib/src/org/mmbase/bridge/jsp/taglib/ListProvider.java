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
 * @version $Id: ListProvider.java,v 1.8 2003-12-18 11:52:42 michiel Exp $ 
 */
public interface ListProvider extends ContextProvider, org.mmbase.bridge.jsp.taglib.containers.QueryContainerOrListProvider {
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
     * @return The offset of the index (normally this will return 1)
     * @since MMBase-1.7
     */
    public int getIndexOffset();

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

    /**
     * Removes the current item from the list.
     * @since MMBase-1.7
     */
    public void remove();

}
