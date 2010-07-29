/*

This software is OSI Certified Open Source Software.
OSI Certified is a certification mark of the Open Source Initiative.

The license (Mozilla version 1.0) can be read at the MMBase site.
See http://www.MMBase.org/license

*/
package org.mmbase.util.functions;
import java.lang.annotation.*;

/**
 * This annotation can be used on a Bean which is to be wrapped in a {@link BeanFunction} to
 * explicitely define which setters must be interpreted as function parameters. Sometimes a class
 * has more general use, and not all setter methods must be exposed as function parameters.
 *
 * You can explicitely state all parameter names, or say that only the with {@link Type} annotated setters must be considered (or both).
 *
 *
 * @author Michiel Meeuwissen
 * @version $Id: Required.java 34900 2009-05-01 16:29:42Z michiel $
 * @since MMBase-1.9.2
 */
@Retention(RetentionPolicy.RUNTIME)
@Target({ElementType.TYPE})
public @interface FunctionParameters {


    String[] value() default {""};

    /**
     * If set to true, only setters annotated with {@link Type} are considered function parameters
     */
    boolean annotated() default false;

}
