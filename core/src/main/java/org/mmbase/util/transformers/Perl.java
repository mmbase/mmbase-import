/*

This software is OSI Certified Open Source Software.
OSI Certified is a certification mark of the Open Source Initiative.

The license (Mozilla version 1.0) can be read at the MMBase site.
See http://www.MMBase.org/license

*/
package org.mmbase.util.transformers;

/**
 * And yet another rather idiotic type of Transformer. Interpret the String as a perl program and
 * transform it to its result. The perl interpretetor needs to be in the PATH.
 *
 * @author Michiel Meeuwissen 
 * @since MMBase-1.7
 * @version $Id$
 */

public class Perl extends AbstractCommandStringTransformer implements CharTransformer {
    protected String[] getCommand() {
        return new String[] {"perl"};
    }
}
