/*

This software is OSI Certified Open Source Software.
OSI Certified is a certification mark of the Open Source Initiative.

The license (Mozilla version 1.0) can be read at the MMBase site.
See http://www.MMBase.org/license

*/
package org.mmbase.util.functions;
import java.lang.annotation.*;

/**
 * The name of a parameter as a annotation interface. Is used in {@link MethodFunction}. If your
 * method has with this annotated parameters, the name will be used for the parameter.
 *
 * @author Michiel Meeuwissen
 * @version $Id$
 * @since MMBase-1.9
 */
@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.PARAMETER)
public @interface Name {
    String value();

}
