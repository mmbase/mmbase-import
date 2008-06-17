package org.incava.util.diff;


/**
 * Represents a difference, as used in <code>Diff</code>. A difference consists
 * of two pairs of starting and ending points, each pair representing either the
 * "from" or the "to" collection passed to <code>Diff</code>. If an ending point
 * is -1, then the difference was either a deletion or an addition. For example,
 * if <code>getDeletedEnd()</code> returns -1, then the difference represents an
 * addition.
 */
public class Difference
{
    public static final int NONE = -1;

    /**
     * The point at which the deletion starts.
     */
    private int delStart = NONE;

    /**
     * The point at which the deletion ends.
     */
    private int delEnd = NONE;

    /**
     * The point at which the addition starts.
     */
    private int addStart = NONE;

    /**
     * The point at which the addition ends.
     */
    private int addEnd = NONE;

    /**
     * Creates the difference for the given start and end points for the
     * deletion and addition.
     */
    public Difference(int delStart, int delEnd, int addStart, int addEnd)
    {
        this.delStart = delStart;
        this.delEnd   = delEnd;
        this.addStart = addStart;
        this.addEnd   = addEnd;
    }

    /**
     * The point at which the deletion starts, if any. A value equal to
     * <code>NONE</code> means this is an addition.
     */
    public int getDeletedStart()
    {
        return delStart;
    }

    /**
     * The point at which the deletion ends, if any. A value equal to
     * <code>NONE</code> means this is an addition.
     */
    public int getDeletedEnd()
    {
        return delEnd;
    }

    /**
     * The point at which the addition starts, if any. A value equal to
     * <code>NONE</code> means this must be an addition.
     */
    public int getAddedStart()
    {
        return addStart;
    }

    /**
     * The point at which the addition ends, if any. A value equal to
     * <code>NONE</code> means this must be an addition.
     */
    public int getAddedEnd()
    {
        return addEnd;
    }

    /**
     * Sets the point as deleted. The start and end points will be modified to
     * include the given line.
     */
    public void setDeleted(int line)
    {
        delStart = Math.min(line, delStart);
        delEnd   = Math.max(line, delEnd);
    }

    /**
     * Sets the point as added. The start and end points will be modified to
     * include the given line.
     */
    public void setAdded(int line)
    {
        addStart = Math.min(line, addStart);
        addEnd   = Math.max(line, addEnd);
    }

    /**
     * Compares this object to the other for equality. Both objects must be of
     * type Difference, with the same starting and ending points.
     */
    public boolean equals(Object obj)
    {
        if (obj instanceof Difference) {
            Difference other = (Difference)obj;

            return (delStart == other.delStart &&
                    delEnd   == other.delEnd &&
                    addStart == other.addStart &&
                    addEnd   == other.addEnd);
        }
        else {
            return false;
        }
    }

    /**
     * Returns a string representation of this difference.
     */
    public String toString()
    {
        StringBuilder buf = new StringBuilder();
        buf.append("del: [" + delStart + ", " + delEnd + "]");
        buf.append(" ");
        buf.append("add: [" + addStart + ", " + addEnd + "]");
        return buf.toString();
    }

    public String toUnixDiff(Object[] aLines, Object[] bLines) {
        StringBuilder buf = new StringBuilder();
        int        delStart = getDeletedStart();
        int        delEnd   = getDeletedEnd();
        int        addStart = getAddedStart();
        int        addEnd   = getAddedEnd();
        append(buf, delStart, delEnd);
        buf.append(delEnd != Difference.NONE && addEnd != Difference.NONE ? "c" : (delEnd == Difference.NONE ? "a" : "d"));
        append(buf, addStart, addEnd);

        buf.append("\n");

        if (delEnd != Difference.NONE) {
            appendLines(buf, delStart, delEnd, "<", aLines);
            if (addEnd != Difference.NONE) {
                buf.append("---\n");
            }
        }
        if (addEnd != Difference.NONE) {
            appendLines(buf, addStart, addEnd, ">", bLines);
        }
        return buf.toString();

    }
    protected void append(StringBuilder buf, int start, int end) {
        // match the line numbering from diff(1):
        buf.append(end == Difference.NONE ? start : (1 + start));

        if (end != Difference.NONE && start != end) {
            buf.append(",").append(1 + end);
        }
    }

    protected void appendLines(StringBuilder buf, int start, int end, String ind, Object[] lines) {

        for (int lnum = start; lnum <= end; ++lnum) {
            buf.append("" + ind + " " + lines[lnum] + "\n");
        }
    }

}
