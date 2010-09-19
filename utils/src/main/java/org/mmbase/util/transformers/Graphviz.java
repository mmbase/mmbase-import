/*

This software is OSI Certified Open Source Software.
OSI Certified is a certification mark of the Open Source Initiative.

The license (Mozilla version 1.0) can be read at the MMBase site.
See http://www.MMBase.org/license

*/
package org.mmbase.util.transformers;
/**
 * Converts the tools of <a href="http://www.graphviz.org">graphviz</a> into an MMBase 'chartransformer'. Meaning that it can convert
 * a script in the <a href="http://en.wikipedia.org/wiki/DOT_language">dot language</a> into SVG (or something else, using the 'type' parameter).
 *
 * We can use this to generated diagrams e.g. to represent the current MMBase object model.
 *
 * @author Michiel Meeuwissen
 * @since MMBase-1.9.2
 * @version $Id$
 */

public class Graphviz extends AbstractCommandStringTransformer implements CharTransformer {
    private static final long serialVersionUID = 0L;

    private String command = "dot";
    private String type    = "svg";

    public void setCommand(String d) {
        command = d;
    }

    public void setType(String t) {
        type = t;
    }

    @Override
    protected String[] getCommand() {
        return new String[] {command, "-T" + type };
    }
}
