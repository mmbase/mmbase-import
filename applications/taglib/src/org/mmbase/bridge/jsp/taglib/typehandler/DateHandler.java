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
import org.mmbase.bridge.jsp.taglib.util.ContextContainer;
import java.util.Calendar;
import java.util.Date;


import org.mmbase.storage.search.*;

import org.mmbase.util.logging.Logging;
import org.mmbase.util.logging.Logger;

/**
 * The 'date' type does not exist yet, this class is used in IntegerHandler and LongHandler now.
 * @author Michiel Meeuwissen
 * @author Vincent vd Locht
 * @since  MMBase-1.6
 * @version $Id: DateHandler.java,v 1.13 2003-12-18 09:03:49 michiel Exp $
 */
public class DateHandler extends AbstractTypeHandler {

    private static final Logger log = Logging.getLoggerInstance(DateHandler.class);

    private static int DATE_FACTOR      = 1000; // MMBase stores dates in seconds not in milliseconds
    private static boolean EXIST_YEAR_0 = false;

    /**
     * Constructor for LongHandler.
     * @param context
     */
    public DateHandler(FieldInfoTag tag) {
        super(tag);
    }

    private void yearFieldValue(Calendar cal, StringBuffer buffer) {
        if (EXIST_YEAR_0) {
            // the year '0' does not really exist in gregorian, So 4 BC == -3, 1 BC == 0
            if(cal.get(Calendar.ERA) == java.util.GregorianCalendar.BC) {
                buffer.append("-");
                buffer.append(cal.get(Calendar.YEAR) - 1);
            } else {
                buffer.append(cal.get(Calendar.YEAR));
            }
        } else {
            // perhaps this is simpler..
            if(cal.get(Calendar.ERA) == java.util.GregorianCalendar.BC) {
                buffer.append("-");
            }
            buffer.append(cal.get(Calendar.YEAR));
        }
    }

    /**
     * @see TypeHandler#htmlInput(Node, Field, boolean)
     */
    public String htmlInput(Node node, Field field, boolean search) throws JspTagException {
        // todo implement search
        StringBuffer buffer = new StringBuffer();
        Calendar cal = Calendar.getInstance();
        if (node !=null) {
            if (node.getLongValue(field.getName()) != -1) {
                cal.setTime( new Date((long) node.getLongValue( field.getName() ) * DATE_FACTOR));
            }
        }
        buffer.append("<input type=\"hidden\" name=\"");
        buffer.append(prefix(field.getName()));
        buffer.append("\" value=\"");
        buffer.append(cal.getTime().getTime()/DATE_FACTOR);
        buffer.append("\" />");
        // give also present value, this makes it possible to see if user changed this field.

        ContextContainer container = tag.getContextProvider().getContextContainer();
        if (search) {
            String name = prefix(field.getName() + "_search");
            String searchi =  (String) container.find(tag.getPageContext(), name);
            if (searchi == null) searchi = "no";
            buffer.append("<select name=\"" + name + "\">\n");
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



        String options = tag.getOptions();
        if (options == null || options.indexOf("date") > -1) {
            String dayName = prefix(field.getName() + "_day");
            String searchDay =  (String) container.find(tag.getPageContext(), dayName);
            int checkDay;
            if (searchDay == null) {
                checkDay = cal.get(Calendar.DAY_OF_MONTH);
            } else {
                checkDay = Integer.parseInt(searchDay);
            }
            buffer.append("<select name=\"" + dayName + "\">\n");
            for (int i = 1; i <= 31; i++) {
                if (checkDay == i) {
                    buffer.append("  <option selected=\"selected\">");
                    buffer.append(i);
                    buffer.append("</option>\n");
                } else {
                    buffer.append("  <option>");
                    buffer.append(i);
                    buffer.append("</option>\n");
                }
            }
            buffer.append("</select>-");
            String monthName = prefix(field.getName() + "_month");
            String searchMonth =  (String) container.find(tag.getPageContext(), monthName);
            int checkMonth;
            if (searchMonth == null) {
                checkMonth = cal.get(Calendar.MONTH) + 1;
            } else {
                checkMonth = Integer.parseInt(searchMonth);
            }
            buffer.append("<select name=\"");
            buffer.append(monthName);
            buffer.append("\">\n");
            for (int i = 1; i <= 12; i++) {
                if (checkMonth == i) {
                    buffer.append("  <option selected=\"selected\">" + i + "</option>\n");
                } else {
                    buffer.append("  <option>" + i + "</option>\n");
                }
            }
            buffer.append("</select>-");
            buffer.append("<input type =\"text\" size=\"5\" name=\"");
            String yearName = prefix(field.getName() + "_year");
            String searchYear =  (String) container.find(tag.getPageContext(), yearName);
            buffer.append(yearName);
            buffer.append("\" ");
            buffer.append("value=\"");
            if (searchYear == null) {
                yearFieldValue(cal, buffer);
            } else {
                buffer.append(searchYear);
            }
            buffer.append("\" />");
        } else {
            buffer.append("<input type=\"hidden\" name=\"" + prefix(field.getName() + "_day") + "\" value=\"" + cal.get(Calendar.DAY_OF_MONTH) + "\" />");
            buffer.append("<input type=\"hidden\" name=\"" + prefix(field.getName() + "_month") + "\" value=\"" + cal.get(Calendar.MONTH) + "\" />");
            buffer.append("<input type=\"hidden\" name=\"" + prefix(field.getName() + "_year") + "\" value=\"");
            yearFieldValue(cal, buffer);
            buffer.append("\" />");
        }
        if (options == null || options.indexOf("time") > -1) {
            String hourName = prefix(field.getName() + "_hour");
            String searchHour =  (String) container.find(tag.getPageContext(), hourName);
            int checkHour;
            if (searchHour == null) {
                checkHour = cal.get(Calendar.HOUR_OF_DAY);
            } else {
                checkHour = Integer.parseInt(searchHour);
            }
            buffer.append("&nbsp;&nbsp;<select name=\"" + hourName + "\">\n");
            for (int i = 0; i <= 23; i++) {
                if (checkHour == i) {
                    buffer.append("  <option selected=\"selected\">");
                } else {
                    buffer.append("  <option>");
                }
                if (i <10) buffer.append("0");
                buffer.append(i + "</option>\n");
            }
            buffer.append("</select> h :");

            String minuteName = prefix(field.getName() + "_minute");
            String searchMinute =  (String) container.find(tag.getPageContext(), minuteName);
            int checkMinute;
            if (searchMinute == null) {
                checkMinute = cal.get(Calendar.MINUTE);
            } else {
                checkMinute = Integer.parseInt(searchMinute);
            }

            buffer.append("<select name=\"" + minuteName + "\">\n");
            for (int i = 0; i <= 59; i++) {
                if (checkMinute == i) {
                    buffer.append("  <option selected=\"selected\">");
                } else {
                    buffer.append("  <option>");
                }
                if (i< 10) buffer.append("0");
                buffer.append(i + "</option>\n");
            }
            buffer.append("</select> m :");

            String secondName = prefix(field.getName() + "_second");
            String searchSecond =  (String) container.find(tag.getPageContext(), secondName);
            int checkSecond;
            if (searchSecond == null) {
                checkSecond = cal.get(Calendar.SECOND);
            } else {
                checkSecond = Integer.parseInt(searchSecond);
            }

            buffer.append("<select name=\"" + secondName + "\">\n");
            for (int i = 0; i <= 59; i++) {
                if (checkSecond == i) {
                    buffer.append("  <option selected=\"selected\">");
                } else {
                    buffer.append("  <option>");
                }
                if (i< 10) buffer.append("0");
                buffer.append(i + "</option>\n");
            }
            buffer.append("</select> s");
        } else {
            buffer.append("<input type=\"hidden\" name=\"" + prefix(field.getName() + "_hour") + "\" value=\"0\" />");
            buffer.append("<input type=\"hidden\" name=\"" + prefix(field.getName() + "_minute") + "\" value=\"0\" />");
            buffer.append("<input type=\"hidden\" name=\"" + prefix(field.getName() + "_second") + "\" value=\"0\" />");
        }

        return buffer.toString();
    }

    /**
     * Just to override. For example if the date is stored in an int and not in a long, then the year can be checked.
     *
     * @return int
     */

    protected int checkYear(Integer year, String fieldName) throws JspTagException {
        int y = year.intValue();
        if (! EXIST_YEAR_0) {
            if (y == 0) throw new JspTagException("The year '0' does not exist and cannot be used for field '" + fieldName + "'");
            if (y < 0) y++; // This makes that year BC 1  =  -1 in stead of BC 1 = 0 (which seems to be the java way)
        }
        return y;
    }

    /**
     * @see TypeHandler#useHtmlInput(Node, Field)
     */
    public boolean useHtmlInput(Node node, Field field) throws JspTagException {

        String fieldName = field.getName();
        Calendar cal = Calendar.getInstance();
        try {
            Integer day    = new Integer( (String) tag.getContextProvider().getContextContainer().find(tag.getPageContext(), prefix(fieldName + "_day")));
            Integer month  = new Integer( (String) tag.getContextProvider().getContextContainer().find(tag.getPageContext(), prefix(fieldName + "_month")));
            Integer year   = new Integer( (String) tag.getContextProvider().getContextContainer().find(tag.getPageContext(), prefix(fieldName + "_year")));
            Integer hour   = new Integer( (String) tag.getContextProvider().getContextContainer().find(tag.getPageContext(), prefix(fieldName + "_hour")));
            Integer minute = new Integer( (String) tag.getContextProvider().getContextContainer().find(tag.getPageContext(), prefix(fieldName + "_minute")));
            Integer second = new Integer( (String) tag.getContextProvider().getContextContainer().find(tag.getPageContext(), prefix(fieldName + "_second")));
            cal.set(checkYear(year, fieldName), month.intValue() - 1, day.intValue(), hour.intValue(), minute.intValue(), second.intValue());
            long oldValue = node.getLongValue(field.getName());
            long newValue = cal.getTime().getTime() / DATE_FACTOR;
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
        String fieldName = field.getName();
        Calendar cal = Calendar.getInstance();
        String input_day    =  (String) tag.getContextProvider().getContextContainer().find(tag.getPageContext(), prefix(fieldName + "_day"));
        String input_month  =  (String) tag.getContextProvider().getContextContainer().find(tag.getPageContext(), prefix(fieldName + "_month"));
        String input_year   =  (String) tag.getContextProvider().getContextContainer().find(tag.getPageContext(), prefix(fieldName + "_year"));
        String input_hour   =  (String) tag.getContextProvider().getContextContainer().find(tag.getPageContext(), prefix(fieldName + "_hour"));
        String input_minute =  (String) tag.getContextProvider().getContextContainer().find(tag.getPageContext(), prefix(fieldName + "_minute"));
        String input_second =  (String) tag.getContextProvider().getContextContainer().find(tag.getPageContext(), prefix(fieldName + "_second"));
        if (input_day == null || input_hour == null) {
            return -1;
        }
        try {
            Integer day    = new Integer(input_day);
            Integer month  = new Integer(input_month);
            Integer year   = new Integer(input_year);
            Integer hour   = new Integer(input_hour);
            Integer minute = new Integer(input_minute);
            Integer second = new Integer(input_second);
            cal.set(checkYear(year, fieldName), month.intValue() - 1, day.intValue(), hour.intValue(), minute.intValue(), second.intValue());
        } catch (java.lang.NumberFormatException e) {
            throw new JspTagException("Not a valid number (" + e.toString() + ")");
        }
        return cal.getTime().getTime() / DATE_FACTOR;       
    }

    /**
     * @see TypeHandler#whereHtmlInput(Field)
     */
    public String whereHtmlInput(Field field) throws JspTagException {            
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
            return "( [" + fieldName + "] = " + time + ")";
        } else {
            log.warn("Found unknown operator value '" + operator + "'");
            return null;
        }
    }

    public Constraint whereHtmlInput(Field field, Query query) throws JspTagException {
        String fieldName = field.getName();
        String operator = (String) tag.getContextProvider().getContextContainer().find(tag.getPageContext(), prefix(fieldName + "_search"));
        if (operator == null || operator.equals("no")) {
            return null;
        }

        Long time = new Long(getSpecifiedValue(field));

        Constraint con;
        if (operator.equals("greater")) {
            con = Queries.createConstraint(query, fieldName, FieldCompareConstraint.GREATER, time);
        } else if (operator.equals("less")) {
            con = Queries.createConstraint(query, fieldName, FieldCompareConstraint.LESS, time);
        } else if (operator.equals("equal")) {
            con = Queries.createConstraint(query, fieldName, FieldCompareConstraint.EQUAL, time);
        } else {
            log.warn("Found unknown operator value '" + operator + "'");
            return null;
        }
        return Queries.addConstraint(query, con);

    }


}
