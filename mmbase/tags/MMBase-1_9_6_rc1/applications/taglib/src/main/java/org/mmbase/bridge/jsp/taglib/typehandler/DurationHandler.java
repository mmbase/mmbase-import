/*

This software is OSI Certified Open Source Software.
OSI Certified is a certification mark of the Open Source Initiative.

The license (Mozilla version 1.0) can be read at the MMBase site.
See http://www.MMBase.org/license

*/

package org.mmbase.bridge.jsp.taglib.typehandler;

import javax.servlet.jsp.JspTagException;
import org.mmbase.bridge.*;
import org.mmbase.bridge.util.Queries;
import org.mmbase.bridge.jsp.taglib.FieldInfoTag;
import org.mmbase.bridge.jsp.taglib.ParamHandler;
import org.mmbase.bridge.jsp.taglib.util.ContextContainer;


import org.mmbase.storage.search.*;

import org.mmbase.util.logging.Logging;
import org.mmbase.util.logging.Logger;

/**
 * Handles the field-type LONG/relativetime
 *
 * @author Michiel Meeuwissen
 * @since  MMBase-1.7.2
 * @version $Id$
 */
public class DurationHandler extends AbstractTypeHandler {

    private static final Logger log = Logging.getLoggerInstance(DurationHandler.class);

    /**
     * @param tag
     */
    public DurationHandler(FieldInfoTag tag) {
        super(tag);
    }

    /**
     * @see TypeHandler#htmlInput(Node, Field, boolean)
     */
    @Override public String htmlInput(Node node, Field field, boolean search) throws JspTagException {
        long currentValue = -1;
        long currentHours = 0;
        long currentMinutes = 0;
        long currentSeconds = 0;
        long currentMilliSeconds = 0;
        if (node !=null) {
            currentValue = node.getLongValue(field.getName());
            long help = currentValue;
            if (help < 0) help = 0;
            currentMilliSeconds = help % 1000;
            help /= 1000;
            currentSeconds = help % 60;
            help /= 60;
            currentMinutes = help % 60;
            currentHours   = help / 60;
        }

        StringBuilder buffer = new StringBuilder();
        buffer.append("<input type=\"hidden\" class=\"" + getClasses(node, field) + "\" name=\"");
        buffer.append(prefix(field.getName()));
        buffer.append("\" value=\"");
        buffer.append(currentValue);
        buffer.append("\" />");
        // give also present value, this makes it possible to see if user changed this field.


        ContextContainer container = tag.getContextProvider().getContextContainer();

        if (search) { // operator drop-down
            String name = prefix(field.getName() + "_search");
            String fieldid = prefixID(field.getName() + "_search");
            String searchi =  (String) container.find(tag.getPageContext(), name);
            if (searchi == null) searchi = "no";
            buffer.append("<select name=\"").append(name).append("\" ");
            buffer.append("id=\"").append(fieldid).append("\">\n");
            buffer.append("  <option value=\"no\" ");
            if (searchi.equals("no")) buffer.append(" selected=\"selected\" ");
            buffer.append("> </option>");
            buffer.append("  <option value=\"less\" ");
            if (searchi.equals("less")) buffer.append(" selected=\"selected\" ");
            buffer.append(">&lt;</option>");
            buffer.append("  <option value=\"greater\" ");
            if (searchi.equals("greater")) buffer.append(" selected=\"selected\" ");
            buffer.append(">&gt;</option>");
            buffer.append("  <option value=\"equal\" ");
            if (searchi.equals("equal")) buffer.append(" selected=\"selected\" ");
            buffer.append(">=</option>");
            buffer.append("</select>");
        }




        String hoursName = prefix(field.getName() + "_hours");
        String hoursId = prefixID(field.getName() + "_hours");
        String searchHours =  (String) container.find(tag.getPageContext(), hoursName);
        buffer.append("<input size=\"5\" type=\"text\" name=\"").append(hoursName).append("\" id=\"").append(hoursId).append("\" ");
        buffer.append("value=\"").append( (searchHours == null ? "" + currentHours : searchHours) ).append("\" /> h :\n");

        String minutesName = prefix(field.getName() + "_minutes");
        String minutesId = prefixID(field.getName() + "_minutes");
        buffer.append("<select name=\"").append(minutesName).append("\" ");
        buffer.append("id=\"").append(minutesId).append("\">\n");
        for (int i = 0; i <= 59; i++) {
            if (currentMinutes == i) {
                buffer.append("  <option selected=\"selected\">" + i + "</option>\n");
            } else {
                buffer.append("  <option>" + i + "</option>\n");
            }
        }
        buffer.append("</select> min : ");

        String secondsName = prefix(field.getName() + "_seconds");
        String secondsId = prefixID(field.getName() + "_seconds");
        buffer.append("<select name=\"").append(secondsName).append("\" ");
        buffer.append("id=\"").append(secondsId).append("\">\n");
        for (int i = 0; i <= 59; i++) {
            if (currentSeconds == i) {
                buffer.append("  <option selected=\"selected\">" + i + "</option>\n");
            } else {
                buffer.append("  <option>" + i + "</option>\n");
            }
        }
        buffer.append("</select> s . ");

        String milliSecondsName = prefix(field.getName() + "_milliseconds");
        String milliSecondsId = prefixID(field.getName() + "_milliseconds");
        String searchMilliSeconds =  (String) container.find(tag.getPageContext(), milliSecondsName);
        buffer.append("<input size=\"5\" type=\"text\" name=\"").append(milliSecondsName).append("\" id=\"").append(milliSecondsId).append("\" ");
        buffer.append("value=\"").append( (searchMilliSeconds == null ? "" + currentMilliSeconds : searchMilliSeconds) ).append("\" /> ms\n");

        return buffer.toString();
    }

    /**
     * @see TypeHandler#useHtmlInput(Node, Field)
     */
    @Override public boolean useHtmlInput(Node node, Field field) throws JspTagException {

        String fieldName = field.getName();
        try {
            long oldValue = node.getLongValue(fieldName);
            long newValue = getSpecifiedValue(field);
            if (oldValue != newValue) {
                node.setLongValue(fieldName, newValue);
                return true;
            }
        } catch (java.lang.NumberFormatException e) {
            throw new JspTagException("Not a valid number (" + e.toString() + ") in field " + fieldName);
        }
        return false;
    }


    protected long getSpecifiedValue(Field field) throws JspTagException {
        try {
            String fieldName = field.getName();
            Integer hours  = Integer.parseInt((String) tag.getContextProvider().getContextContainer().find(tag.getPageContext(), prefix(fieldName + "_hours")));
            Integer minutes  = Integer.parseInt( (String) tag.getContextProvider().getContextContainer().find(tag.getPageContext(), prefix(fieldName + "_minutes")));
            Integer seconds   = Integer.parseInt( (String) tag.getContextProvider().getContextContainer().find(tag.getPageContext(), prefix(fieldName + "_seconds")));
            Integer milliSeconds   = Integer.parseInt( (String) tag.getContextProvider().getContextContainer().find(tag.getPageContext(), prefix(fieldName + "_milliseconds")));
            return  (long) milliSeconds.intValue() + 1000 * (seconds.intValue()  + 60 * (minutes.intValue() + 60 * hours.intValue()));
        } catch (java.lang.NumberFormatException e) {
            throw new JspTagException("Not a valid number (" + e.toString() + ")");
        }
    }

    /**
     * @see TypeHandler#whereHtmlInput(Field)
     */
    @Override public String whereHtmlInput(Field field) throws JspTagException {
     String fieldName = field.getName();
        String operator = (String) tag.getContextProvider().getContextContainer().find(tag.getPageContext(), prefix(fieldName + "_search"));
        if (operator == null || operator.equals("no")) {
            return null;
        }

        long time = getSpecifiedValue(field);
        if (time == -1) return null;

        if (operator.equals("greater")) {
            return "( [" + fieldName + "] >" + time + ")";
        } else if (operator.equals("smaller")) {
            return "( [" + fieldName + "] <" + time + ")";
        } else if (operator.equals("equal")) {

            String options = tag.getOptions();
            if (options != null && options.indexOf("date") > -1) {
                return "( [" + fieldName + "] >= " + time + "  AND [" + fieldName + "] < " + (time + 24 * 60 * 60) + ")";
            } else {
                return "( [" + fieldName + "] = " + time + ")";
            }
        } else {
            log.warn("Found unknown operator value '" + operator + "'");
            return null;
        }
    }

    @Override public Constraint whereHtmlInput(Field field, Query query) throws JspTagException {
        String fieldName = field.getName();
        String operator = (String) tag.getContextProvider().getContextContainer().find(tag.getPageContext(), prefix(fieldName + "_search"));
        if (operator == null || operator.equals("no")) {
            return null;
        }

        Long time = getSpecifiedValue(field);

        if (query.getSteps().size() > 1) {
            fieldName = field.getNodeManager().getName()+"."+fieldName;
        }
        Constraint con;
        if (operator.equals("greater")) {
            con = Queries.createConstraint(query, fieldName, FieldCompareConstraint.GREATER, time);
        } else if (operator.equals("less")) {
            con = Queries.createConstraint(query, fieldName, FieldCompareConstraint.LESS, time);
        } else if (operator.equals("equal")) {
            String options = tag.getOptions();
            if (options != null && options.indexOf("date") > -1) {
                con = Queries.createConstraint(query, fieldName, Queries.OPERATOR_BETWEEN, time, time + 24 * 60 * 60, false);
            } else {
                con = Queries.createConstraint(query, fieldName, FieldCompareConstraint.EQUAL, time);
            }
        } else {
            log.warn("Found unknown operator value '" + operator + "'");
            return null;
        }
        Queries.addConstraint(query, con);
        return con;

    }

    @Override public void paramHtmlInput(ParamHandler handler, Field field) throws JspTagException  {
        String fieldName = field.getName();
        String operator = (String) tag.getContextProvider().getContextContainer().find(tag.getPageContext(), prefix(fieldName + "_search"));
        if (operator == null || operator.equals("no")) {
            return;
        }
        handler.addParameter(prefix(fieldName + "_search"), operator);

        String hours    =  (String) tag.getContextProvider().getContextContainer().find(tag.getPageContext(), prefix(fieldName + "_hours"));
        String minutes  =  (String) tag.getContextProvider().getContextContainer().find(tag.getPageContext(), prefix(fieldName + "_minutes"));
        String seconds   =  (String) tag.getContextProvider().getContextContainer().find(tag.getPageContext(), prefix(fieldName + "_seconded"));
        String milliseconds   =  (String) tag.getContextProvider().getContextContainer().find(tag.getPageContext(), prefix(fieldName + "_milliseconds"));
        handler.addParameter(prefix(fieldName + "_hours"), hours);
        handler.addParameter(prefix(fieldName + "_minutes"), minutes);
        handler.addParameter(prefix(fieldName + "_seconds"), seconds);
        handler.addParameter(prefix(fieldName + "_milliseconds"), milliseconds);
    }



}
