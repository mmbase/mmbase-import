/*

This software is OSI Certified Open Source Software.
OSI Certified is a certification mark of the Open Source Initiative.

The license (Mozilla version 1.0) can be read at the MMBase site.
See http://www.MMBase.org/license

*/

package org.mmbase.bridge.jsp.taglib.typehandler;

import javax.servlet.jsp.JspTagException;
import org.mmbase.bridge.Field;
import org.mmbase.bridge.Node;
import org.mmbase.bridge.jsp.taglib.FieldInfoTag;
import java.util.Calendar;
import java.util.Date;


/**
 * The 'date' type does not exist yet, this class is used in IntegerHandler and LongHandler now.
 * @author Michiel Meeuwissen
 * @author Vincent vd Locht
 * @since  MMBase-1.6
 * @version $Id: DateHandler.java,v 1.3 2003-03-20 10:56:14 pierre Exp $
 */
public class DateHandler extends AbstractTypeHandler {

    private static int DATE_FACTOR      = 1000; // MMBase stores dates in seconds not in milliseconds
    private static boolean EXIST_YEAR_0 = false;

    /**
     * Constructor for LongHandler.
     * @param context
     */
    public DateHandler(FieldInfoTag context) {
        super(context);
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

        String options = context.getOptions();
        if (options == null || options.indexOf("date") > -1) {
            buffer.append("<select name=\"" + prefix(field.getName() + "_day") + "\">\n");
            for (int i = 1; i <= 31; i++) {
                if (cal.get(Calendar.DAY_OF_MONTH) == i) {
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
            buffer.append("<select name=\"");
            buffer.append(prefix(field.getName() + "_month"));
            buffer.append("\">\n");
            for (int i = 1; i <= 12; i++) {
                if (cal.get(Calendar.MONTH) == (i - 1)) {
                    buffer.append("  <option selected=\"selected\">" + i + "</option>\n");
                } else {
                    buffer.append("  <option>" + i + "</option>\n");
                }
            }
            buffer.append("</select>-");
            buffer.append("<input type =\"text\" size=\"5\" name=\"");
            buffer.append(prefix(field.getName() + "_year"));
            buffer.append("\" ");
            buffer.append("value=\"");
            yearFieldValue(cal, buffer);
            buffer.append("\" />");
        } else {
            buffer.append("<input type=\"hidden\" name=\"" + prefix(field.getName() + "_day") + "\" value=\"" + cal.get(Calendar.DAY_OF_MONTH) + "\" />");
            buffer.append("<input type=\"hidden\" name=\"" + prefix(field.getName() + "_month") + "\" value=\"" + cal.get(Calendar.MONTH) + "\" />");
            buffer.append("<input type=\"hidden\" name=\"" + prefix(field.getName() + "_year") + "\" value=\"");
           yearFieldValue(cal, buffer);
            buffer.append("\" />");
        }
        if (options == null || options.indexOf("time") > -1) {
            buffer.append("&nbsp;&nbsp;<select name=\"" + prefix(field.getName() + "_hour") + "\">\n");
            for (int i = 0; i <= 23; i++) {
                if (cal.get(Calendar.HOUR_OF_DAY) == i) {
                    buffer.append("  <option selected=\"selected\">");
                } else {
                    buffer.append("  <option>");
                }
                if (i<10) buffer.append("0");
                buffer.append(i + "</option>\n");
            }
            buffer.append("</select> h :");

            buffer.append("<select name=\"" + prefix(field.getName() + "_minute") + "\">\n");
            for (int i = 0; i <= 59; i++) {
                if (cal.get(Calendar.MINUTE) == i) {
                    buffer.append("  <option selected=\"selected\">");
                } else {
                    buffer.append("  <option>");
                }
                if (i< 10) buffer.append("0");
                buffer.append(i + "</option>\n");
            }
            buffer.append("</select> m :");
            buffer.append("<select name=\"" + prefix(field.getName() + "_second") + "\">\n");
            for (int i = 0; i <= 59; i++) {
                if (cal.get(Calendar.SECOND) == i) {
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
    public String useHtmlInput(Node node, Field field) throws JspTagException {

        String fieldName = field.getName();
        Calendar cal = Calendar.getInstance();
        try {
            Integer day    = new Integer(context.getContextTag().findAndRegisterString(prefix(fieldName + "_day")));
            Integer month  = new Integer(context.getContextTag().findAndRegisterString(prefix(fieldName + "_month")));
            Integer year   = new Integer(context.getContextTag().findAndRegisterString(prefix(fieldName + "_year")));
            Integer hour   = new Integer(context.getContextTag().findAndRegisterString(prefix(fieldName + "_hour")));
            Integer minute = new Integer(context.getContextTag().findAndRegisterString(prefix(fieldName + "_minute")));
            Integer second = new Integer(context.getContextTag().findAndRegisterString(prefix(fieldName + "_second")));
            cal.set(checkYear(year, fieldName), month.intValue() - 1, day.intValue(), hour.intValue(), minute.intValue(), second.intValue());
            node.setLongValue(fieldName, cal.getTime().getTime() / DATE_FACTOR);
        } catch (java.lang.NumberFormatException e) {
            throw new JspTagException("Not a valid number (" + e.toString() + ") in field " + fieldName);
        }
        return "";
    }


    /**
     * @see TypeHandler#whereHtmlInput(Field)
     */
    public String whereHtmlInput(Field field) throws JspTagException {

        StringBuffer buffer = new StringBuffer();
        String guitype = field.getGUIType();
        String fieldName = field.getName();
        Calendar cal = Calendar.getInstance();
        String input_day    = context.getContextTag().findAndRegisterString(prefix(fieldName + "_day"));
        String input_month  = context.getContextTag().findAndRegisterString(prefix(fieldName + "_month"));
        String input_year   = context.getContextTag().findAndRegisterString(prefix(fieldName + "_year"));
        String input_hour   = context.getContextTag().findAndRegisterString(prefix(fieldName + "_hour"));
        String input_minute = context.getContextTag().findAndRegisterString(prefix(fieldName + "_minute"));
        String input_second = context.getContextTag().findAndRegisterString(prefix(fieldName + "_second"));
        if (input_day==null || input_hour==null) {
            return null;
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
        // check if changed:
        if (! context.getContextTag().findAndRegisterString(prefix(fieldName)).equals("" + cal.getTime().getTime() / DATE_FACTOR)) {
            buffer.append("(" + fieldName + ">" + (cal.getTime().getTime() / DATE_FACTOR) + ")");
        } else {
            return null;
        }


        return buffer.toString();
    }

}
