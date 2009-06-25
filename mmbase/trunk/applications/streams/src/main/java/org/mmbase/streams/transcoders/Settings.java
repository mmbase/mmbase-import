/*

This software is OSI Certified Open Source Software.
OSI Certified is a certification mark of the Open Source Initiative.

The license (Mozilla version 1.0) can be read at the MMBase site.
See http://www.MMBase.org/license

*/
package org.mmbase.streams.transcoders;
import java.lang.annotation.*;

/**
 * This annotation can be used on methods, to make it
 *
 * @author Michiel Meeuwissen
 * @version $Id: Required.java 34900 2009-05-01 16:29:42Z michiel $
 */
@Retention(RetentionPolicy.RUNTIME)
@Target({ElementType.TYPE})
public @interface Settings {
    String[] value();

}
