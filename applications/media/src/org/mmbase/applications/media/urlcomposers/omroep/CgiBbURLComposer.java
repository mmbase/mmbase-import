/*

This software is OSI Certified Open Source Software.
OSI Certified is a certification mark of the Open Source Initiative.

The license (Mozilla version 1.0) can be read at the MMBase site.
See http://www.MMBase.org/license

*/

package org.mmbase.applications.media.urlcomposers.omroep;

/**
 *
 * @author Michiel Meeuwissen
 * @version $Id: CgiBbURLComposer.java,v 1.2 2009-04-16 10:28:07 michiel Exp $
 * @since MMBase-1.7
 */
public class CgiBbURLComposer extends CgiSbURLComposer {

    @Override
    protected String getBandPrefix() {
        return "bb.";
    }

    @Override
    protected String getBand() {
        return "breedband";
    }
}


