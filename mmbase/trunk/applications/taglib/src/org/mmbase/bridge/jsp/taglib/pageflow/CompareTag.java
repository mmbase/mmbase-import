/*

This software is OSI Certified Open Source Software.
OSI Certified is a certification mark of the Open Source Initiative.

The license (Mozilla version 1.0) can be read at the MMBase site.
See http://www.MMBase.org/license

*/
package org.mmbase.bridge.jsp.taglib.pageflow;

import org.mmbase.bridge.jsp.taglib.util.Attribute;
import org.mmbase.bridge.jsp.taglib.Condition;
import org.mmbase.bridge.jsp.taglib.WriterReferrer;
import javax.servlet.jsp.JspTagException;

import org.mmbase.util.logging.Logger;
import org.mmbase.util.logging.Logging;

import java.util.*;

/**
 * A very simple tag to check if the value of a certain context
 * variable equals a certain String value.
 *
 * @author Michiel Meeuwissen
 * @version $Id: CompareTag.java,v 1.28 2003-12-24 11:21:33 nico Exp $
 */

public class CompareTag extends PresentTag implements Condition, WriterReferrer {

    private static final Logger log = Logging.getLoggerInstance(CompareTag.class);

    private Attribute value    = Attribute.NULL;
    private Attribute valueSet = Attribute.NULL;
    public void setValue(String v) throws JspTagException {
        value =  getAttribute(v);
    }

    public void setValueset(String vs) throws JspTagException {
        valueSet =  getAttribute(vs);
    }

    private Attribute referid2 = Attribute.NULL;
    public void setReferid2(String r) throws JspTagException {
        referid2 = getAttribute(r);
    }

    protected boolean doCompare(Comparable v1, Comparable v2) {
        if (log.isDebugEnabled()) {
            log.debug("comparing " + (v1 != null ? v1.getClass().getName() : "") + "'" + v1 + "' to " + (v2 != null ? v2.getClass().getName() : "")+ "'" + v2 + "'");
        }

        // TODO this is a bit oddly implemented, perhaps using org.mmbase.util.Casting, or 'equals' after all.
        try {
            return v1.compareTo(v2) == 0; // (cannot use 'equals' because BigDecimal then also compares scale, which doesn't interest us too much).
        } catch (Throwable e) {
            // for example if v1 is a Node and v2 is not, then it throws a ClassCastException, which we of course don't want here.
            return false;
        }
    }

    protected Object getCompare2() throws JspTagException {
        if (referid2 == Attribute.NULL) {
            throw new JspTagException("Attribute 'value' or 'referid2' must be indicated");
        }
        return getObject(referid2.getString(this));

    }


    public int doStartTag() throws JspTagException {
        Object compare1;
        if (getReferid() == null) {
            compare1 =  findWriter().getWriterValue();
            if (compare1 == null) compare1 = "";
        } else {
            compare1 = getObject(getReferid());
        }
        if (compare1 instanceof Boolean) {
            compare1 = compare1.toString();
        }

        if (! (compare1 instanceof Comparable)) {
            throw new JspTagException("Cannot compare variable of type " + compare1.getClass().getName());
        }

        Set compareToSet = new HashSet();
        if (value != Attribute.NULL) {
            if (valueSet != Attribute.NULL) {
                throw new JspTagException("Can specify both 'value' and 'valueset' attributes");
            }
            if (referid2 != Attribute.NULL) {
                throw new JspTagException("Cannot indicate 'referid2' and 'value' attributes both");
            }
            compareToSet.add(value.getValue(this));
        } else if (valueSet != Attribute.NULL) {
            if (referid2 != Attribute.NULL) {
                throw new JspTagException("Cannot indicate 'referid2' and 'valueSet' attributes both");
            }
            compareToSet.addAll(valueSet.getList(this));
        } else {
            compareToSet.add(getCompare2());
        }

        Iterator i = compareToSet.iterator();
        while (i.hasNext()) {
            Object compare2 = i.next();
            if ((compare1 instanceof Number) && (compare2 instanceof String)) {
                log.debug("found an instance of Number");
                compare1 = new java.math.BigDecimal(compare1.toString());
                if ("".equals(compare2)) { // do something reasonable in IsEmpty
                    compare2 = new java.math.BigDecimal(0);
                } else {
                    compare2 = new java.math.BigDecimal((String)compare2);
                }
            }
            
            if ((compare2 instanceof Number) && (compare1 instanceof String)) {
                log.debug("found an instance of Number");
                compare2 = new java.math.BigDecimal(compare2.toString());
                if ("".equals(compare1)) { // do something reasonable in IsEmpty
                    compare1=new java.math.BigDecimal(0);
                } else {
                    compare1 = new java.math.BigDecimal((String)compare1);
                }
            }
            
            // if using 'BigDecimal' then avoid classcastexceptions also if other type is some number.
            if (compare1 instanceof java.math.BigDecimal) {
                if (! (compare2 instanceof java.math.BigDecimal)) {
                    compare2 = new java.math.BigDecimal(compare2.toString());
                }
            }
            
            if (doCompare((Comparable)compare1, (Comparable)compare2) != getInverse() ) {
                return EVAL_BODY_BUFFERED;
            }
        }
        return SKIP_BODY;
    }
}
