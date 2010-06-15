/*

This software is OSI Certified Open Source Software.
OSI Certified is a certification mark of the Open Source Initiative.

The license (Mozilla version 1.0) can be read at the MMBase site.
See http://www.MMBase.org/license

*/
package org.mmbase.calendar;

import java.util.*;
import org.mmbase.bridge.*;
import org.mmbase.storage.search.*;
import org.mmbase.util.functions.Required;
import org.mmbase.util.logging.Logger;
import org.mmbase.util.logging.Logging;

/**
 * The gui function of calendar items and calendar item types.
 *
 * @author Michiel Meeuwissen
 * @version $Id: GuiFunction.java 35892 2009-06-09 20:03:49Z michiel $
 * @since MMBase-2.0
 */
public class ItemsController {

    private static final Logger LOG = Logging.getLoggerInstance(ItemsController.class);

    private NodeQuery query;
    private boolean desiredValue;
    private Date start;
    private Date stop;
    private String title;



    @Required
    public void setValue(boolean value) {
        this.desiredValue = value;
    }


    public void setTitle(String t) {
        this.title = t;
    }



    @Required
    public void setQuery(NodeQuery query) {
        this.query = query;
    }

    public void setStart(Date start) {
        this.start = start;
    }


    public void setStop(Date stop) {
        this.stop = stop;
    }

    protected static FieldValueConstraint getDate(NodeQuery q, Constraint constraint, String field) {
        if (constraint instanceof FieldValueConstraint) {
            FieldValueConstraint fvc = (FieldValueConstraint) constraint;
            if (fvc.getField().getStep().equals(q.getNodeStep()) && fvc.getField().getFieldName().equals(field)) {
                return fvc;
            } else {
                return null;
            }
        } else if (constraint instanceof CompositeConstraint) {
            CompositeConstraint composite = (CompositeConstraint) constraint;
            for (Constraint cons : composite.getChilds()) {
                FieldValueConstraint res = getDate(q, cons, field);
                if (res != null) {
                    return res;
                }
            }
            return null;
        } else {
            return null;
        }

    }
    protected static Date getDate(NodeQuery q, String field) {
        Constraint constraint = q.getConstraint();
        if (constraint == null) return null;
        return (Date) getDate(q, constraint, field).getValue();


    }

    protected List<Node> getRelevantPeriods() {

        List<Node> result = new ArrayList<Node>();
        if (start == null) {
            start = getDate(query, "start");
        }
        if (stop == null) {
            stop = getDate(query, "stop");
        }
        NodeQuery clone = (NodeQuery) query.clone();
        return result;
    }

    protected int addAndDelete(List<Node> periods) {
        Date startDate = getDate(query, "start");
        Date endDate   = getDate(query, "stop");

        for (int i = 0 ; i < periods.size(); i++) {
            Node period = periods.get(i);
            if (desiredValue) {
                if (period.getDateValue("start").getTime() <= startDate.getTime()) {
                    if (period.getDateValue("stop").getTime() >= endDate.getTime()) {
                        // already covered by a (longer) period
                        return 0;
                    } else if (period.getDateValue("stop").getTime() >= startDate.getTime()) {
                        // partially covered by a period that can be extended
                        period.setDateValue("stop", endDate);
                        return 1;
                    } else {
                        // period does not border or overlap
                        continue;
                    }
                } else {
                    if (period.getDateValue("start").getTime() <= endDate.getTime()) {
                        if (period.getDateValue("stop").getTime() <= endDate.getTime()) {
                            // period is completely included by desired period, extend on both sides.
                            period.setDateValue("stop", endDate);
                            period.setDateValue("start", startDate);
                        } else {
                            // desired perios is at the beginning of an existing item, extend at beginning
                            period.setDateValue("start", startDate);
                        }
                        return 1;
                    }
                }
            } else {
                if (period.getDateValue("start").getTime() <= startDate.getTime()) {
                    if (period.getDateValue("stop").getTime() > startDate.getTime()) {
                        if (period.getDateValue("stop").getTime() <= endDate.getTime()) {
                            // covered at the end of (longer) period
                            if (period.getDateValue("start").getTime() == startDate.getTime()) {
                                periods.remove(i);
                                period.delete(true);
                                return 1;
                            } else {
                                period.setDateValue("stop", startDate);
                                return 1;
                            }
                        } else if (period.getDateValue("start").getTime() == startDate.getTime()) {
                            // convered at the begin of a longer period
                            period.setDateValue("start", endDate);
                            return 1;
                        }
                    }
                }
            }

        }

        if (desiredValue) {
            // no matching period found, so create one
            Cloud cloud = query.getCloud();
            Node newNode = cloud.getNodeManager("calendar_items").createNode();
            newNode.setStringValue("title", title);
            newNode.setDateValue("start", startDate);
            newNode.setDateValue("stop", endDate);
            newNode.commit();
            periods.add(newNode);
            return 1;
        } else {
            return 0;
        }
    }

    protected int merge(List<Node> periods) {
        int changes = 0;
        for (int i = 0 ; i < periods.size() - 1; i++) {
            Node period1 = periods.get(i);
            Node period2 = periods.get(i + 1);
            if (period1.getDateValue("stop").getTime() >= period2.getDateValue("start").getTime()) {
                period1.setDateValue("stop", period2.getDateValue("stop"));
                periods.remove(i + 1);
                period2.delete(true);
                changes++;
                i--; continue;
            }
        }
        return changes;
    }

    protected int fix(List<Node> periods) {
        int result = addAndDelete(periods);
        result += merge(periods);
        return result;
    }

    public int post() {
        int changes = 0;
        List<Node> periods = getRelevantPeriods();
        changes += fix(periods);
        return changes;
    }


}