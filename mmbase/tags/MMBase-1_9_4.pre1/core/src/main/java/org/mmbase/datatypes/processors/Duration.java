/*

This software is OSI Certified Open Source Software.
OSI Certified is a certification mark of the Open Source Initiative.

The license (Mozilla version 1.0) can be read at the MMBase site.
See http://www.MMBase.org/license

*/
package org.mmbase.datatypes.processors;

import java.util.regex.Pattern;

import org.mmbase.datatypes.*;
import org.mmbase.bridge.*;
import org.mmbase.util.Casting;
import org.mmbase.util.logging.Logger;
import org.mmbase.util.logging.Logging;

/**
 * Formats a long with hour:minutes:seconds. Ready for setting and getting, and also casting.
 *
 * @author Michiel Meeuwissen
 * @version $Id$
 * @since MMBase-1.9
 */

public class Duration {
    private static final Logger log = Logging.getLoggerInstance(Duration.class);
    // dd:HH:mm:ss.ss
    public static final Pattern DURATION_PATTERN = Pattern.compile("\\A(?:(?:(?:[0-9]+:)?[0-9]+:)?[0-9]+:)?[0-9]+(?:\\.[0-9]+)?\\z");


    public static class DataType extends LongDataType {
        private static final long serialVersionUID = 1L;

        public DataType(String name) {
            super(name, false);
        }

        @Override
        protected Number castString(Object preCast, Cloud cloud) throws CastException {
            if (preCast == null || "".equals(preCast)) return null;
            if (preCast instanceof String) {
                String s = (String) preCast;
                if (! DURATION_PATTERN.matcher(s).matches()) {
                    if (! StringDataType.DOUBLE_PATTERN.matcher(s).matches()) {
                        throw new CastException("Not a duration: " + s);
                    }
                }
            }
            Double l =  SetString.getDouble(Casting.toString(preCast)); // this makes it e.g. possible to report that 1e50 is too big for a
            return l;
        }
        @Override
        protected Object preCast(Object value, Cloud cloud, Node node, Field field) {
            if (value == null) return null;
            try {
                Object preCast =  castString(value, cloud);
                return preCast;
            } catch (CastException ce) {
                log.debug(ce);
                return -1;
            }
        }

    }

    public static class GetString implements  Processor {
        private static final long serialVersionUID = 1L;
        public static String getString(long time) {
            StringBuilder buf = new StringBuilder();
            time /= 10; // in centis
            long centis = -1;
            long s     = 0;
            long min   = 0;
            long h     = 0;
            long d     = 0;

            if (time != 0) {
                centis = time % 100;
                time /= 100;  // in s
                if (time != 0) {
                    s = time % 60;
                    time /= 60;  // in min
                    if (time != 0) {
                        min = time % 60;
                        time /= 60; // in hour
                        if (time != 0) {
                            h = time % 24;
                            d = time / 24;   // in day
                        }
                    }
                }
            }
            boolean append = false;

            if (d > 0) append = true;
            if (append) buf.append(d).append(':');
            append = true;
            if (h > 0) append = true;
            if (append) buf.append(h).append(':');
            if (min > 0) append = true;
            if (append) buf.append(min).append(':');
            if (s < 10 && s >= 0) buf.append('0');
            buf.append(s);
            if (centis > 0) {
                buf.append('.');
                if (centis < 10) buf.append('0');
                buf.append(centis);
            }
            return buf.toString();
        }

        public Object process(Node node, Field field, Object value) {
            long time = Casting.toLong(value) * 1000;
            return getString(time);
        }

        @Override
        public String toString() {
            return "getDuration";
        }
    }

    public static class SetString implements Processor {
        private static final long serialVersionUID = 1L;
        public static long getLong(String s) {
            return (long) getDouble(s).doubleValue();
        }
        public static Double getDouble(String s) {
            if (s == null || "".equals(s)) return null;
            if (StringDataType.DOUBLE_PATTERN.matcher(s).matches()) {
                log.debug("A valid double");
                return Casting.toDouble(s);
            }
            String[] fields = s.split(":");
            long factor = 1; // seconds
            int index = fields.length - 1;
            double l = Double.parseDouble(fields[index])  * factor;
            if (index == 0) return l;
            factor *= 60;  // minutes
            index--;
            l += Long.parseLong(fields[index]) * factor;
            if (index == 0) return l;
            factor *= 60;// hours
            index--;
            l += Long.parseLong(fields[index]) * factor;
            if (index == 0) return l;
            factor *= 24;// days
            index--;
            return l + 0.5; // +0.5 makes rounding correct
        }
        public Object process(Node node, Field field, Object value) {
            if (value instanceof Long) {
                return value;
            } else {
                String s = Casting.toString(value);
                if ("".equals(s)) return null;
                return getLong(s);
            }

        }

        @Override
        public String toString() {
            return "set_duration";
        }
    }

    // test the damn regexp
    public static void main(String argv[]) {
        System.out.println(DURATION_PATTERN.matcher(argv[0]).matches());
        long l = SetString.getLong(argv[0]) * 1000;
        System.out.println("" + l);
        System.out.println(GetString.getString(l));
    }

}
