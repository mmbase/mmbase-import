/*

This software is OSI Certified Open Source Software.
OSI Certified is a certification mark of the Open Source Initiative.

The license (Mozilla version 1.0) can be read at the MMBase site.
See http://www.MMBase.org/license

*/
package org.mmbase.util.functions;
import java.lang.annotation.*;

/**
 * This annotation can be used on methods, to attribute a {@link org.mmbase.datatypes.Datatype}, e.g.  to the parameter of
 * the corresponding {@link BeanFunction}.
 *
 * @author Michiel Meeuwissen
 * @version $Id: Required.java 34900 2009-05-01 16:29:42Z michiel $
 * @since MMBase-1.9.2
 */
@Retention(RetentionPolicy.RUNTIME)
@Target({ElementType.METHOD, ElementType.PARAMETER})
public @interface Type {


    /**
     * An identifier, or an <datatype> xml-element
     */
    String value();


}
