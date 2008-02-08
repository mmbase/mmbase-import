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
 * @version $Id: WmBbURLComposer.java,v 1.6 2003-07-15 12:43:45 vpro Exp $
 * @since MMBase-1.7
 */
public class WmBbURLComposer extends WmSbURLComposer {
      
    protected String getBandPrefix() {
        return "bb.";
    }
    
    protected String getBand() {
        return "breedband";
    }
}