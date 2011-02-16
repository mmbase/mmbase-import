/*

This software is OSI Certified Open Source Software.
OSI Certified is a certification mark of the Open Source Initiative.

The license (Mozilla version 1.0) can be read at the MMBase site.
See http://www.MMBase.org/license

*/
package org.mmbase.bridge.jsp.taglib;

import javax.servlet.jsp.jstl.core.*;
import javax.servlet.jsp.JspTagException;

/**
 * Basic interface that parent should implement if they provide Lists.
 * For example the several NodeListTag's  provide a List.
 *
 * @author Michiel Meeuwissen
 * @version $Id$
 */
public interface ListProvider extends ContextProvider, LoopTag {
    /**
     * @return the size of the list
     */
    int size();

    /**
     * @return the index of the current item in a list
     *
     */
    int getIndex();


    /**
     * @return The offset of the index (normally this will return 1)
     * @since MMBase-1.7
     */
    int getIndexOffset();

    /**
     * @return the current item in a list
     */

    @Override
    Object getCurrent();


    /**
     * @return a boolean indicating whether the field on which was
     * sorted is changed.
     *
     */
    boolean isChanged();

    /**
     * Removes the current item from the list.
     * @since MMBase-1.7
     */
    void remove();

    /**
     * @since MMBase-1.9
     */
    void setAdd(String c) throws JspTagException;
    /**
     * @since MMBase-1.9
     */
    void setRetain(String c) throws JspTagException;
    /**
     * @since MMBase-1.9
     */
    void setRemove(String c) throws JspTagException;


    /**
     * @since MMBase-1.8
     */
    public class ListProviderLoopTagStatus implements LoopTagStatus {

        private final ListProvider prov;
        public ListProviderLoopTagStatus(ListProvider l) {
            prov = l;
        }
        @Override
        public Object getCurrent() {
            return prov.getCurrent();
        }
        @Override
        public int getIndex() {
            return prov.getIndex() +  prov.getIndexOffset();
        }

        @Override
        public int getCount() {
            return prov.size();
        }
        @Override
        public boolean isFirst() {
            return prov.getIndex() == 0;
        }
        @Override
        public boolean isLast() {
            return getCount() == prov.getIndex() + 1;
        }
        @Override
        public Integer getBegin() {
            return prov.getIndexOffset();
        }
        @Override
        public Integer getEnd() {
            return prov.size() + prov.getIndexOffset() - 1;
        }
        @Override
        public Integer getStep() {
            return 1;
        }
    }


}
