/*

This software is OSI Certified Open Source Software.
OSI Certified is a certification mark of the Open Source Initiative.

The license (Mozilla version 1.0) can be read at the MMBase site.
See http://www.MMBase.org/license

*/

package org.mmbase.bridge.jsp.taglib.typehandler;

import java.util.*;

import java.text.SimpleDateFormat;
import javax.servlet.jsp.JspTagException;

import org.mmbase.bridge.*;
import org.mmbase.datatypes.*;
import org.mmbase.bridge.util.Queries;
import org.mmbase.bridge.jsp.taglib.*;
import org.mmbase.bridge.jsp.taglib.util.ContextContainer;
import org.mmbase.storage.search.*;
import org.mmbase.util.Casting;

import org.mmbase.util.logging.Logging;
import org.mmbase.util.logging.Logger;

/**
 * @author Michiel Meeuwissen
 * @author Vincent vd Locht
 * @since  MMBase-1.6
 * @version $Id$
 */
public class DateHandler extends AbstractTypeHandler {

    private static final Logger log = Logging.getLoggerInstance(DateHandler.class);

    private static boolean EXIST_YEAR_0 = false;
    private static final SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss.SSS");

    /**
     * @param tag
     */
    public DateHandler(FieldInfoTag tag) {
        super(tag);
    }


    private Calendar getInstance() throws JspTagException {
        return Calendar.getInstance(tag.getTimeZone());
    }

    protected DateTimePattern getPattern(DataType dt) throws JspTagException {
        DateTimePattern dateTimePattern;
        if (! (dt instanceof DateTimeDataType)) {
            // backwards compatibility
            String options = tag.getOptions();
            boolean doDate = true;
            boolean doTime = true;
            if (options != null) {
                boolean time = false;
                if (options.indexOf("time") > -1) {
                    doTime = true;
                    time = true;
                    doDate = false;

                }
                if (options.indexOf("date") > -1) {
                    doDate = true;
                    doTime = time;
                }
            }
            StringBuilder buf = new StringBuilder();
            if (doDate) {
                buf.append("yyyy-MM-dd");
            }
            if (doTime) {
                if (buf.length() > 0) buf.append(" ");
                buf.append("HH'h':mm'm':ss's'");
            }
            dateTimePattern = new DateTimePattern(buf.toString());
        } else {
            dateTimePattern = ((DateTimeDataType) dt).getPattern();
        }
        return dateTimePattern;


    }

    /**
     * Returns the field value as specified by the client's post.
     */
    @Override protected Object getFieldValue(Node node, Field field) throws JspTagException {
        Calendar cal = getSpecifiedValue(field, getInstance());
        return cal == null ? null : cal.getTime();
    }

    /**
     * @see TypeHandler#htmlInput(Node, Field, boolean)
     */
    @Override public String htmlInput(Node node, Field field, boolean search) throws JspTagException {

        StringBuilder buffer = new StringBuilder();
        boolean required = field.getDataType().isRequired();

        Calendar cal = getCalendarValue(node, field);
        buffer.append("<span class=\"mm_datetime " + getClasses(node, field) + "\">");
        buffer.append("<input type=\"hidden\" name=\"");
        buffer.append(prefix(field.getName()));
        buffer.append("\" value=\"");
        if (cal != null) {
            buffer.append(cal.getTime().getTime() / 1000);
        }
        buffer.append("\" />");
        // give also present value, this makes it possible to see if user changed this field.

        ContextContainer container = tag.getContextProvider().getContextContainer();
        if (search) {
            String name = prefix(field.getName() + "_search");
            String fieldid = prefixID(field.getName() + "_search");
            String searchi =  (String) container.find(tag.getPageContext(), name);
            if (searchi == null) searchi = "no";
            buffer.append("<select name=\"").append(name).append("\" ");
            buffer.append("id=\"").append(fieldid).append("\" class=\"mm_search\">");
            buffer.append("<option value=\"no\" ");
            if (searchi.equals("no")) buffer.append(" selected=\"selected\" ");
            buffer.append(">&#xA0;</option>");
            buffer.append("<option value=\"less\" ");
            if (searchi.equals("less")) buffer.append(" selected=\"selected\" ");
            buffer.append(">&lt;</option>");
            buffer.append("<option value=\"greater\" ");
            if (searchi.equals("greater")) buffer.append(" selected=\"selected\" ");
            buffer.append(">&gt;</option>");
            buffer.append("<option value=\"equal\" ");
            if (searchi.equals("equal")) buffer.append(" selected=\"selected\" ");
            buffer.append(">=</option>");
            buffer.append("</select>");
        }
        DataType dt = field.getDataType();
        DateTimePattern dateTimePattern = getPattern(dt);
        Calendar minDate = getInstance();
        Calendar maxDate = getInstance();
        if (dt instanceof DateTimeDataType) {
            Date min = ((DateTimeDataType) dt).getMin();
            minDate.setTime(min);
            Date max = ((DateTimeDataType) dt).getMax();
            maxDate.setTime(max);
        } else {
            minDate.setTime(new Date(Long.MIN_VALUE));
            maxDate.setTime(new Date(Long.MAX_VALUE));
        }


        Locale locale = tag.getLocale();
        List<String> parsed = dateTimePattern.getList(locale);

        boolean first = true;
        for (String pattern : parsed) {
            if (pattern.length() < 1) continue;
            char firstChar = pattern.charAt(0);
            if (firstChar ==  '\'') {
                buffer.append(pattern.substring(1));
                continue;
            }

            DateTimePattern.Element element = DateTimePattern.getElement(firstChar, minDate, maxDate);
            if (element == null) {
                continue;
            }

            String name = prefix(field.getName() + "_" + element.getName());
            String fieldid = prefixID(field.getName() + "_" + element.getName());
            String searchValue =  (String) container.find(tag.getPageContext(), name);
            int check;
            if (searchValue == null || searchValue.length() == 0) {
                if (cal != null) {
                    check = cal.get(element.getField()) + element.getOffset();
                } else {
                    check  = -1;
                }
            } else {
                check = Integer.parseInt(searchValue);
            }

            if (element.getMax() - element.getMin() < 200) {
                buffer.append("<select class=\"mm_datetime_").append(element.getName()).append("\" ");
                buffer.append("name=\"").append(name).append("\" ");
                buffer.append("id=\"").append(fieldid).append("\" ");
                addExtraAttributes(buffer);
                buffer.append(">");
                String checkOption = "  <option selected=\"selected\" value=\"" + check + "\">" +
                    (check == -1 ? "--" : element.toString(check - element.getOffset(), locale, pattern.length())) +
                    "</option>";
                if (! required && first) {
                    if (check == -1) {
                        buffer.append(checkOption);
                    } else {
                        buffer.append("<option value=\"-1\">--</option>");
                    }
                }
                if (check > -1 && check < element.getMin()) {
                    buffer.append(checkOption);
                }
                for (int i = element.getMin(); i <= element.getMax(); i++) {
                    if (firstChar == 'y' && i == 0 && ! EXIST_YEAR_0) continue;
                    if (check == i) {
                        buffer.append(checkOption);
                    } else {
                        String val = element.toString(i - element.getOffset(), locale, pattern.length());
                        buffer.append("<option value=\"" + i + "\">" + val + "</option>");
                    }
                }
                if (check > element.getMax()) {
                    buffer.append(checkOption);
                }
                buffer.append("</select>");
            } else {
                buffer.append("<input class=\"mm_datetime_").append(element.getName()).append("\" type=\"text\" size=\"").append( (pattern.length() + 1) ).append("\" ");
                buffer.append("name=\"").append(name).append("\" ");
                buffer.append("id=\"").append(fieldid).append("\" ");
                addExtraAttributes(buffer);
                buffer.append("value=\"");
                if (searchValue == null) {
                    if (cal != null) {
                        buffer.append(cal.get(element.getField()));
                    } else {
                        buffer.append("");
                    }
                } else {
                    buffer.append(searchValue);
                }
                buffer.append("\" />");
            }
            first = false;

        }

        buffer.append("</span>");
        return buffer.toString();
    }

    /**
     * Just to override. For example if the date is stored in an int and not in a long, then the year can be checked.
     *
     * @return int
     */

    protected int checkYear(int y, String fieldName) throws JspTagException {
        if (! EXIST_YEAR_0) {
            if (y == 0) throw new JspTagException("The year '0' does not exist and cannot be used for field '" + fieldName + "'");
            if (y < 0) y++; // This makes that year BC 1  =  -1 in stead of BC 1 = 0 (which seems to be the java way)
        }
        return y;
    }

    /**
     * @see TypeHandler#useHtmlInput(Node, Field)
     */
    @Override public boolean useHtmlInput(Node node, Field field) throws JspTagException {
        final String fieldName = field.getName();
        final Calendar cal = getInstance();
        Object oldValue = node.getValue(fieldName);
        if (oldValue != null) {
            oldValue = node.getDateValue(fieldName);
            cal.setTime((Date) oldValue);
        } else {
            cal.clear();
        }

        final Calendar newCal = getSpecifiedValue(field, cal);
        final Date newValue = newCal == null ? null : newCal.getTime();

        log.debug("oldValue " + oldValue + " newValue " + newValue);
        if (oldValue == null) {
            if (newValue != null) {
                log.debug("Setting " + newValue);
                node.setDateValue(fieldName, newValue);
                return true;
            }
        } else if (!oldValue.equals(newValue)) {
            log.debug("Setting " + newValue);
            node.setDateValue(fieldName, newValue);
            return true;
        } else {
            log.debug("Not setting");
        }

        return false;
    }

    /**
     * @return The given Calendar instance or <code>null</code>
     */
    protected Calendar getSpecifiedValue(final Field field, Calendar cal) throws JspTagException {
        final String fieldName = field.getName();
        final DataType<Object> dt = field.getDataType();
        final DateTimePattern dateTimePattern = getPattern(dt);
        final Calendar minDate = getInstance();
        minDate.setTime(DateTimeDataType.MIN_VALUE);
        final Calendar maxDate = getInstance();
        maxDate.setTime(DateTimeDataType.MAX_VALUE);


        if (cal != null) {
            cal.clear();
        }

        final Locale locale = tag.getLocale();
        final List<String> parsed = dateTimePattern.getList(locale);

        int maxField = Calendar.ERA;
        for(String pattern : parsed) {
            if (pattern.length() < 1) continue;
            char firstChar = pattern.charAt(0);
            if (firstChar ==  '\'') {
                continue;
            }
            DateTimePattern.Element element = DateTimePattern.getElement(firstChar, minDate, maxDate);
            if (element == null) {
                continue;
            }
            try {
                String string =  (String) tag.getContextProvider().getContextContainer().find(tag.getPageContext(), prefix(fieldName + "_" + element.getName()));
                if (string == null || "".equals(string)) {
                    cal = null;
                } else {
                    int value = Integer.parseInt(string);
                    if (value == element.getNullValue()) {
                        cal = null;
                    } else {
                        if (cal != null) {
                            int f = element.getField();
                            if (f > maxField) maxField = f;
                            cal.set(f, value - element.getOffset());
                        }
                    }
                }
            } catch (java.lang.NumberFormatException e) {
                throw new TaglibException("Not a valid number (" + e.toString() + ") in field " + fieldName, e);
            }
        }
        if (log.isDebugEnabled()) {
            log.debug("Using " + dt + " --> " + (cal == null ? "NULL" : "" + cal.getTime()));
        }

        return  cal;
    }
    @Override protected Object getFieldValue(Node node, Field field, boolean useDefault) throws JspTagException {
        Calendar cal =  getCalendarValue(node, field);
        return cal == null ? null : cal.getTime();
    }
    protected Calendar getCalendarValue(Node node, Field field) throws JspTagException {
        Calendar cal = Calendar.getInstance();
        cal.set(0, 0, 0, 0, 0, 0);
        cal.set(Calendar.MILLISECOND, 0);
        cal = getSpecifiedValue(field, cal);
        if (cal == null) {
            if (node != null) {
                if (node.isNull(field.getName())) {
                    cal = null;
                } else {
                    cal = getInstance();
                    cal.setTime(node.getDateValue(field.getName()));
                }
            } else {
                Object def = field.getDataType().getDefaultValue(tag.getLocale(), tag.getCloudVar(), field);
                if (def != null) {
                    cal = getInstance();
                    cal.setTime(Casting.toDate(def));
                } else {
                    if (! field.getDataType().isRequired()) {
                        cal = null;
                    }  else {
                        cal = getInstance();
                    }
                }
            }
        }
        return cal;
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

        Date timeValue = getSpecifiedValue(field, getInstance()).getTime();

        String time;
        if (field.getType() == Field.TYPE_DATETIME) {
            time = dateFormat.format(timeValue);
        } else {
            time = "" + Casting.toLong(timeValue);
        }

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

        Object time = getSpecifiedValue(field, getInstance()).getTime();
        if (field.getType() != Field.TYPE_DATETIME) {
            time = Casting.toLong(time);
        }

        // expand fieldname with nodemanager name if needed
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
                Object nextTime;
                if (field.getType() == Field.TYPE_DATETIME) {
                    nextTime = new Date(((Date)time).getTime() + 24 * 60 * 60 * 1000);
                } else {
                    nextTime = ((Long)time).longValue() + 24 * 60 * 60;
                }
                con = Queries.createConstraint(query, fieldName, Queries.OPERATOR_BETWEEN, time, nextTime, false);
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

        String options = tag.getOptions();
        if (options == null || options.indexOf("time") > -1) {
            String hour    = (String) tag.getContextProvider().getContextContainer().find(tag.getPageContext(), prefix(fieldName + "_hour"));
            String minute  = (String) tag.getContextProvider().getContextContainer().find(tag.getPageContext(), prefix(fieldName + "_minute"));
            String  second = (String) tag.getContextProvider().getContextContainer().find(tag.getPageContext(), prefix(fieldName + "_second"));
            handler.addParameter(prefix(fieldName + "_hour"), hour);
            handler.addParameter(prefix(fieldName + "_minute"), minute);
            handler.addParameter(prefix(fieldName + "_second"), second);
        }
        String day    =  (String) tag.getContextProvider().getContextContainer().find(tag.getPageContext(), prefix(fieldName + "_day"));
        String month  =  (String) tag.getContextProvider().getContextContainer().find(tag.getPageContext(), prefix(fieldName + "_month"));
        String year   =  (String) tag.getContextProvider().getContextContainer().find(tag.getPageContext(), prefix(fieldName + "_year"));
        handler.addParameter(prefix(fieldName + "_day"), day);
        handler.addParameter(prefix(fieldName + "_month"), month);
        handler.addParameter(prefix(fieldName + "_year"), year);
    }
}
