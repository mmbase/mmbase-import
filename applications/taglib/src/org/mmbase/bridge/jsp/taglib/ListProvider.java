/*

This software is OSI Certified Open Source Software.
OSI Certified is a certification mark of the Open Source Initiative.

The license (Mozilla version 1.0) can be read at the MMBase site.
See http://www.MMBase.org/license

*/
package org.mmbase.bridge.jsp.taglib;
import javax.servlet.jsp.JspTagException;
import javax.servlet.jsp.jstl.core.*;
import org.mmbase.util.logging.*;

/**
 * Basic interface that parent should implement if they provide Lists.
 * For example the several NodeListTag's  provide a List.
 *
 * @author Michiel Meeuwissen
 * @version $Id: ListProvider.java,v 1.13 2005-12-15 21:47:27 michiel Exp $
 */
public interface ListProvider extends ContextProvider, LoopTag {
    /**
     * @return the size of the list
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


    /**
     * @since MMBase-1.8
     */
    public class ListProviderLoopTagStatus implements LoopTagStatus {
        private static final Logger log = Logging.getLoggerInstance(ListProviderLoopTagStatus.class);

        private final ListProvider prov;
        public ListProviderLoopTagStatus(ListProvider l) {
            prov = l;
        }
        public Object getCurrent() {
            return prov.getCurrent();
        }
        public int getIndex() {
            return prov.getIndex();// - prov.getIndexOffset();
        }

        public int getCount() {
            return prov.size();
        }

        public boolean isFirst() {
            return getIndex() == 0;
        }
        public boolean isLast() {
            return getCount() == getIndex() + 1;
        }
        public Integer getBegin() {
            return null;
        }
        public Integer getEnd() {
            return null;
        }
        public Integer getStep() {
            return null;
        }
    }


}
