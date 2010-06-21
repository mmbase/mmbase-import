/*

This software is OSI Certified Open Source Software.
OSI Certified is a certification mark of the Open Source Initiative.

The license (Mozilla version 1.0) can be read at the MMBase site.
See http://www.MMBase.org/license

*/
package org.mmbase.calendar;

import java.util.*;
import org.mmbase.bridge.*;
import org.mmbase.bridge.util.*;
import org.mmbase.storage.search.*;
import org.mmbase.storage.search.implementation.*;
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
    private NodeQuery abstractQuery;
    private boolean desiredValue;
    private Date startForm;
    private Date stopForm;
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
        abstractQuery = (NodeQuery) query.clone();
        Queries.removeConstraint(abstractQuery, getDate(abstractQuery, abstractQuery.getConstraint(), "start"));
        Queries.removeConstraint(abstractQuery, getDate(abstractQuery, abstractQuery.getConstraint(), "stop"));
        abstractQuery.setMaxNumber(Integer.MAX_VALUE);
    }

    public void setStartForm(Date start) {
        this.startForm = start;
    }


    public void setStopForm(Date stop) {
        this.stopForm = stop;
    }

    @Required
    public void setStart(Date start) {
        this.start = start;
    }

    @Required
    public void setStop(Date stop) {
        this.stop = stop;
    }

    protected static BasicFieldValueConstraint getDate(NodeQuery q, Constraint constraint, String field) {
        if (constraint instanceof BasicFieldValueConstraint) {
            BasicFieldValueConstraint fvc = (BasicFieldValueConstraint) constraint;
            if (fvc.getField().getStep().equals(q.getNodeStep()) && fvc.getField().getFieldName().equals(field)) {
                return fvc;
            } else {
                return null;
            }
        } else if (constraint instanceof CompositeConstraint) {
            CompositeConstraint composite = (CompositeConstraint) constraint;
            for (Constraint cons : composite.getChilds()) {
                BasicFieldValueConstraint res = getDate(q, cons, field);
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
        if (startForm == null) {
            // TODO should match one item before given item
            LOG.debug("No start given");
            startForm = start;
        }
        if (stopForm == null) {
            LOG.debug("No stop given");
            // TODO should match one item after given item
            stopForm = stop;
        }
        result.addAll(abstractQuery.getNodeManager().getList(abstractQuery));
        return result;
    }

    protected Node createNode() {
        Cloud cloud = query.getCloud();
        Node newNode = cloud.getNodeManager("calendar_items").createNode();
        // TODO this supposes that the node belongs to the last step always
        Queries.applyConstraints(abstractQuery, abstractQuery.getSteps().get(query.getSteps().size() - 1), newNode);
        newNode.setStringValue("title", title);
        return newNode;
    }

    protected int addAndDelete(List<Node> periods) {

        if (desiredValue) {
            LOG.debug("including " + "  " + start + " " + stop);
        } else {
            LOG.debug("execluding " + "  " + start + " " + stop);
        }
        for (int i = 0 ; i < periods.size(); i++) {
            Node period = periods.get(i);
            if (desiredValue) {
                LOG.debug("include period " + period.getDateValue("start") + "-" + period.getDateValue("stop") + "  " + start + " " + stop);
                if (period.getDateValue("start").getTime() <= start.getTime()) {
                    if (period.getDateValue("stop").getTime() >= stop.getTime()) {
                        LOG.debug("already covered by a (longer) period");
                        return 0;
                    } else if (period.getDateValue("stop").getTime() >= start.getTime()) {
                        LOG.debug("partially covered by a period that can be extended");
                        period.setDateValue("stop", stop);
                        period.commit();
                        return 1;
                    } else {
                        LOG.debug("period does not border or overlap");
                        continue;
                    }
                } else {
                    if (period.getDateValue("start").getTime() <= stop.getTime()) {
                        if (period.getDateValue("stop").getTime() <= stop.getTime()) {
                            LOG.debug("period is completely included by desired period, extend on both sides.");
                            period.setDateValue("stop", stop);
                            period.setDateValue("start", start);
                            period.commit();
                        } else {
                            LOG.debug("desired period is at the beginning of an existing item, extend at beginning");
                            period.setDateValue("start", start);
                            period.commit();
                        }
                        return 1;
                    }
                }
            } else {
                LOG.debug("exclude period " + period.getDateValue("start") + "-" + period.getDateValue("stop") + "  " + start + " " + stop);
                if (period.getDateValue("start").getTime() <= start.getTime()) {
                    if (period.getDateValue("stop").getTime() > start.getTime()) {
                        if (period.getDateValue("stop").getTime() <= stop.getTime()) {
                            LOG.debug("covered at the end of (longer) period");
                            if (period.getDateValue("start").getTime() == start.getTime()) {
                                periods.remove(i);
                                period.delete(true);
                                return 1;
                            } else {
                                period.setDateValue("stop", start);
                                period.commit();
                                return 1;
                            }
                        } else if (period.getDateValue("start").getTime() == start.getTime()) {
                            LOG.debug("covered at the begin of a longer period");
                            period.setDateValue("start", stop);
                            period.commit();
                            return 1;
                        } else {
                            LOG.debug("covered by a longer period");
                            Date originalStop = period.getDateValue("stop");
                            period.setDateValue("stop", start);
                            period.commit();
                            Node newNode = createNode();
                            newNode.setDateValue("start", stop);
                            newNode.setDateValue("stop", originalStop);
                            newNode.commit();
                            if (query.getSteps().size() > 1) {
                                Queries.addToResult(query, newNode);
                            }
                            periods.add(newNode);
                            return 2;
                        }
                    }
                } else {
                }
            }

        }

        if (desiredValue) {
            LOG.debug("no matching period found, so creating one");
            Node newNode = createNode();
            newNode.setDateValue("start", start);
            newNode.setDateValue("stop", stop);
            newNode.commit();
            if (query.getSteps().size() > 1) {
                Queries.addToResult(query, newNode);
            }
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
        LOG.service("Found " + startForm + "-" + stopForm + ": " + periods);
        changes += fix(periods);
        return changes;
    }


}