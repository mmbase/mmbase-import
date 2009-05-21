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

import org.mmbase.bridge.Node;
import org.mmbase.util.Casting;
import javax.servlet.jsp.JspTagException;

import org.mmbase.util.logging.Logger;
import org.mmbase.util.logging.Logging;

import java.util.*;
import java.util.regex.Pattern;
import java.math.BigDecimal;

/**
 * A very simple tag to check if the value of a certain context
 * variable equals a certain String value.
 *
 * @author Michiel Meeuwissen
 * @version $Id$
 */

public class CompareTag extends PresentTag implements Condition, WriterReferrer {

    protected static final Logger log = Logging.getLoggerInstance(CompareTag.class);

    private Attribute value    = Attribute.NULL;
    private Attribute valueSet = Attribute.NULL;
    private Attribute regexp   = Attribute.NULL;
    public void setValue(String v) throws JspTagException {
        value =  getAttribute(v);
    }

    public void setValueset(String vs) throws JspTagException {
        valueSet =  getAttribute(vs);
    }
    public void setRegexp(String r) throws JspTagException {
        regexp =  getAttribute(r);
    }

    private Attribute referid2 = Attribute.NULL;
    public void setReferid2(String r) throws JspTagException {
        referid2 = getAttribute(r);
    }

    protected boolean doCompare(Comparable<Comparable> v1, Comparable v2) {
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
        Object o =  getObject(referid2.getString(this));
        if (o instanceof Node) {
            return "" + ((Node)o).getNumber();
        } else {
            return o;
        }

    }


    public int doStartTag() throws JspTagException {
        Object compare1;

        // find compare1
        if (getReferid() == null) {
            compare1 =  findWriter().getWriterValue();
            if (compare1 == null){
                compare1 = "";
            }
        } else {
            compare1 = getObject(getReferid());
        }
        if (compare1 instanceof Boolean) {
            compare1 = Casting.toInteger(compare1);
        } else if (compare1 instanceof Date) {
            compare1 = Casting.toInteger(compare1);
        } else if (compare1 instanceof List) {
            compare1 = Casting.toString(compare1);
        } else if (compare1 instanceof Node) {
            compare1 = Casting.toString(compare1);
        } else if (compare1 instanceof byte[]) {
            compare1 = Casting.toString(compare1);
        } else if (compare1 instanceof org.apache.commons.fileupload.FileItem) {
            compare1 = Casting.toString(compare1);
        }

        if (! (compare1 instanceof Comparable)) {
            throw new JspTagException("Cannot compare variable '" + getReferid() + "' of type " + compare1.getClass().getName());
        }

        boolean result = false;
        if (regexp != Attribute.NULL) {
            if (value != Attribute.NULL || valueSet != Attribute.NULL) {
                throw new JspTagException("Cannot use 'regexp' attribute in combination with 'value' or 'valueSet' attributes");
            }
            Pattern pattern = Pattern.compile (regexp.getString(this));
            result = pattern.matcher("" + compare1).matches();
        } else {
            // find compare2-set.
            Set<Object> compareToSet = new HashSet<Object>();
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

            Iterator<Object> i = compareToSet.iterator();


            if (compare1 instanceof Number) {
                compare1 = new BigDecimal(compare1.toString());
                while (i.hasNext()) {
                    Object compare2 = i.next();
                    if (compare2 instanceof Date) {
                        compare2 = Casting.toInteger(compare2);
                    }

                    if (compare2 instanceof String) {
                        if ("".equals(compare2)) { // do something reasonable in IsEmpty
                            compare2 = new BigDecimal("0");
                        }else if("true".equals(((String)compare2).toLowerCase()) || "false".equals((((String)compare2).toLowerCase()))) {
                            //if compare1 was a boolean it will be a number by now, and we will have to change compare2 to a number too.
                            compare2 = new BigDecimal(Casting.toInteger(new Boolean((String)compare2)));
                        } else {
                            compare2 = new BigDecimal((String)compare2);
                        }
                    } else if (compare2 instanceof Number) {
                        compare2 = new BigDecimal(compare2.toString());
                    } else if (compare2 instanceof Node) {
                        compare2 = new BigDecimal(((Node)compare2).getNumber());
                    }

                    if (doCompare((Comparable<Comparable>)compare1, (Comparable)compare2)) {
                        result = true;
                        break;

                    }

                }
            } else {
                while (i.hasNext()) {
                    Object compare2 = i.next();
                    if (compare2 instanceof Date || compare2 instanceof Boolean) {
                        compare2 = Casting.toInteger(compare2);
                    }
                    if ("true".equals(compare2)) {
                        compare2 = 1;
                    } else if ("false".equals(compare2)) {
                        compare2 = 0;
                    }
                    if (compare2 instanceof Number) {
                        compare2 = new BigDecimal(compare2.toString());
                        Number compare1n;
                        if ("".equals(compare1)) { // do something reasonable in IsEmpty
                            compare1n = new BigDecimal("0");
                        } else {
                            if ("true".equals(compare1)) {
                                compare1n = new BigDecimal(1);
                            } else if ("false".equals(compare1)) {
                                compare1n = new BigDecimal(0);
                            } else {
                                compare1n = new BigDecimal((String)compare1);
                            }
                        }
                        if (doCompare((Comparable<Comparable>)compare1n, (Comparable)compare2)) {
                            result = true;
                            break;
                        }
                    } else { // both compare1 and compare2 are not Number, simply compare then
                        if (! (compare2 instanceof Comparable)) compare2 = Casting.toString(compare2);
                        if (doCompare((Comparable<Comparable>)compare1, (Comparable)compare2)) {
                            result = true;
                            break;
                        }
                    }
                }
            }
        }
        if (result != getInverse() ) {
            return EVAL_BODY;
        } else {
            return SKIP_BODY;
        }
    }
}
