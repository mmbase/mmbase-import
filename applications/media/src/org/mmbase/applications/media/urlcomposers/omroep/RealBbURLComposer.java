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
 * @version $Id: RealBbURLComposer.java,v 1.5 2003-07-15 12:26:37 vpro Exp $
 * @since MMBase-1.7
 */
public class RealBbURLComposer extends RealSbURLComposer {
     
    protected String getBandPrefix() {
        return "bb.";
    }

    protected String getBand() {
        return "breedband";
    }
}


