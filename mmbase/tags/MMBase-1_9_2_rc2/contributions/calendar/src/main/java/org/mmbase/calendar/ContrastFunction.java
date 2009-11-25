/*

This software is OSI Certified Open Source Software.
OSI Certified is a certification mark of the Open Source Initiative.

The license (Mozilla version 1.0) can be read at the MMBase site.
See http://www.MMBase.org/license

*/
package org.mmbase.calendar;

import org.mmbase.util.functions.*;

import java.util.regex.*;
import org.mmbase.bridge.*;
import org.mmbase.util.logging.Logger;
import org.mmbase.util.logging.Logging;

/**
 * Calculates a contrasting color for a given color. Used by calendar items types.
 *
 * @author Michiel Meeuwissen
 * @version $Id$
 * @since MMBase-1.9
 */
public class ContrastFunction extends org.mmbase.util.functions.NodeFunction {

    private static final Logger log = Logging.getLoggerInstance(ContrastFunction.class);
    public static final Pattern CSS1_BRIGHT_COLORS =  Pattern.compile("aqua|lime|silver|white|yellow");
    public static final Pattern CSS1_DARK_COLORS =  Pattern.compile("black|blue|fuchsia|gray|green|maroon|navy|olive|purple|red|teal");

    public static final Pattern HEX3_COLORS = Pattern.compile("#([0-9,a-f])([0-9,a-f])([0-9,a-f])");
    public static final Pattern HEX6_COLORS = Pattern.compile("#([0-9,a-f][0-9,a-f])([0-9,a-f][0-9,a-f])([0-9,a-f][0-9,a-f])");

    public ContrastFunction() {
        super("contrast", Parameter.EMPTY, ReturnType.STRING);
    }

    public static String getContrast(String color) {
        Matcher matcher = CSS1_BRIGHT_COLORS.matcher(color);
        if (matcher.matches()) {
            return "black";
        }
        matcher = CSS1_DARK_COLORS.matcher(color);
        if (matcher.matches()) {
            return "white";
        }
        // taking upper or lower bound for each of the three components.
        // perhaps it's nicer to simply always choose either black or white.

        matcher = HEX3_COLORS.matcher(color);
        if (matcher.matches()) {
            char r = matcher.group(1).charAt(0);
            char g = matcher.group(2).charAt(0);
            char b = matcher.group(2).charAt(0);
            r = r > '7' ? '0' : 'f';
            g = g > '7' ? '0' : 'f';
            b = b > '7' ? '0' : 'f';
            return "#" + r + g + b;
        }
        matcher = HEX6_COLORS.matcher(color);
        if (matcher.matches()) {
            int r = Integer.valueOf(matcher.group(1), 16);
            int g = Integer.valueOf(matcher.group(2), 16);
            int b = Integer.valueOf(matcher.group(2), 16);
            r = r > 128 ? 0 : 255;
            g = g > 128 ? 0 : 255;
            b = b > 128 ? 0 : 255;
            return "#" +
                (r < 16 ? "0" : "") + Integer.toHexString(r) +
                (g < 16 ? "0" : "") + Integer.toHexString(g) +
                (b < 16 ? "0" : "") + Integer.toHexString(b);

        }
        return "black";
    }


    protected String getFunctionValue(Node node, Parameters parameters) {
        return getContrast(node.getStringValue("color"));
    }

    public static void main(String[] argv) {
        System.out.println(getContrast(argv[0]));
    }

}
