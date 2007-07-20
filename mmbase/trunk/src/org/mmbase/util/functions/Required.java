/*

This software is OSI Certified Open Source Software.
OSI Certified is a certification mark of the Open Source Initiative.

The license (Mozilla version 1.0) can be read at the MMBase site.
See http://www.MMBase.org/license

*/
package org.mmbase.util.functions;
import java.lang.annotation.*;

/**
 * 
 *
 * @author Michiel Meeuwissen
 * @version $Id: Required.java,v 1.1 2007-07-20 13:21:43 michiel Exp $
 * @since MMBase-1.9
 */
@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.METHOD)
public @interface Required {
    boolean value();

}
