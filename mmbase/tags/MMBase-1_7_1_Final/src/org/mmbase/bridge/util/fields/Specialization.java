/*

This software is OSI Certified Open Source Software.
OSI Certified is a certification mark of the Open Source Initiative.

The license (Mozilla version 1.0) can be read at the MMBase site.
See http://www.MMBase.org/license

*/
package org.mmbase.bridge.util.fields;

import org.mmbase.bridge.*;
import java.util.*;

/**
 * As yet unused, but something like this will be available when field-types project is finished.
 *
 * Don't use! This is class is only provided to give you the possibility to help us!
 *
 * @author Michiel Meeuwissen
 * @version $Id: Specialization.java,v 1.1 2003-12-09 22:36:37 michiel Exp $
 * @since MMBase-1.8
 */
interface Specialization {


    /**
     * A GuiHandler instance can be configured by one String, and can decide itself how to interpret it.
     * These strings are the bodies of the 'guitype' tags of builder xml's.
     */       
    void initialize(String config);



    /**
     * Determines the possible values of this field.
     * @return 
     A list of possible values, or 'null' if there are no limited number of values
     *         Possibly the List may contain only Maps, in which case this has to be considered 
     *         a 'combined' value (think 'dates').
     */
    PossibleValues getPossibleValues(Node node, Field field);

    /**
     * Check weither a value is valid for a certain field.
     */
    boolean checkValid(Node node, Field field, Object value);

        
    interface PossibleValues {
        boolean possible(Object o);
    }

    interface PossibleFloatValues extends PossibleValues {
        interface Range {
            float getStart();
            float getEnd();
            
        }
        // at least one Range
        Range[] getRanges();
    } 

    interface PossibleEnumValues extends PossibleValues {
        List getValues();
    } 

}
