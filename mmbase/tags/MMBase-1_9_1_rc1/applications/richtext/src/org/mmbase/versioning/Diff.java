package org.mmbase.versioning;

import org.incava.util.diff.Difference;
import java.util.*;


/**
 * Utility to present diffs.
 *
 * @author Michiel Meeuwissen
 * @version $Id: Diff.java,v 1.4 2008-06-30 11:18:03 michiel Exp $
 * @since
 */

public class Diff extends org.incava.util.diff.Diff {

    private static final long serialVersionUID = 1L;

    public Diff(Object[] a, Object[] b, Comparator comp) {
        super(a, b, comp);
    }

    public Diff(Object[] a, Object[] b) {
        super(a, b);
    }

    public Diff(Collection a, Collection b, Comparator comp) {
        super(a, b, comp);
    }

    public Diff(Collection a, Collection b) {
        super(a, b);
    }


    protected void appendToHtml(StringBuilder buf, Object[] a, int i, String claz) {
        buf.append("<td class='" + claz + "'>");
        if (i >= 0 && i < a.length) {
            buf.append("" + a[i]);
        }
        buf.append("</td>");
    }
    protected void appendToHtml(StringBuilder buf, int startA, int endA, int startB, int endB, Difference d) {
        for (int i = 0; i <= Math.max(endA - startA, endB - startB); i++) {
            buf.append("<tr class='" +
                       (d != null ?
                        (endB >= 0 ? "add " : "") +
                        (endA >= 0 ? "delete " : "")
                        :
                        "") +
                       "'>");
            buf.append("<td class='difference'>" + (i == 0 ? "" + d + ": " : "") + i +"</td>");
            appendToHtml(buf, a, endA != Difference.NONE ? i + startA : -1, "old");
            appendToHtml(buf, b, endB != Difference.NONE ? i + startB : -1, "new");
            buf.append("</tr>");
        }
    }

    public String toHtml() {
        StringBuilder buf = new StringBuilder();
        int startA = -1;
        int startB = -1;
        buf.append("<tr class='difference'><td colspan='100'>" + diff() + "</td></tr>");
        for (Difference d : diff()) {
            int delEnd = d.getDeletedEnd();
            int delStart= d.getDeletedStart();
            int addEnd = d.getAddedEnd();
            int addStart = d.getAddedStart();
            if (startA == -1 && startB == -1) {
                appendToHtml(buf, 0, delStart - 1, 0, addStart - 1, null);
                startB = addStart - 1;
                startA = delStart - 1;
            }
            if (delEnd != Difference.NONE) {
                startA = delEnd;
            }
            if (addEnd != Difference.NONE) {
                 startB = addEnd;
            }

            appendToHtml(buf, delStart, delEnd, addStart, addEnd, d);

        }
        appendToHtml(buf, startA + 1, a.length, startB + 1, b.length, null);
        return buf.toString();

    }

    public String toUnixDiff() {
        StringBuilder buf = new StringBuilder();
        for (Difference difference : diff()) {
            toUnixDiff(buf, difference, a, b);
        }
        return buf.toString();
    }


    public static void toUnixDiff(StringBuilder buf, Difference difference, Object[] aLines, Object[] bLines) {

        append(buf, difference.getDeletedStart(), difference.getDeletedEnd());
        buf.append(difference.getDeletedEnd() != Difference.NONE &&
                   difference.getAddedEnd() != Difference.NONE ? "c" : (difference.getDeletedEnd() == Difference.NONE ? "a" : "d"));
        append(buf, difference.getAddedStart(), difference.getAddedEnd());

        buf.append("\n");

        if (difference.getDeletedEnd() != Difference.NONE) {
            appendLines(difference, buf, difference.getDeletedStart(), difference.getDeletedEnd(), "<", aLines);
            if (difference.getAddedEnd() != Difference.NONE) {
                buf.append("---\n");
            }
        }
        if (difference.getAddedEnd() != Difference.NONE) {
            appendLines(difference, buf, difference.getAddedStart(), difference.getAddedEnd(), ">", bLines);
        }

    }

    protected static void append(StringBuilder buf, int start, int end) {
        // match the line numbering from diff(1):
        buf.append(end == Difference.NONE ? start : (1 + start));

        if (end != Difference.NONE && start != end) {
            buf.append(",").append(1 + end);
        }
    }

    protected static void appendLines(Difference difference, StringBuilder buf, int start, int end, String ind, Object[] lines) {
        for (int lnum = start; lnum <= end; ++lnum) {
            buf.append("" + ind + " " + lines[lnum] + "\n");
        }
    }

}
