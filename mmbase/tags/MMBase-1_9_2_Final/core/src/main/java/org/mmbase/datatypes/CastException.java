/*

This software is OSI Certified Open Source Software.
OSI Certified is a certification mark of the Open Source Initiative.

The license (Mozilla version 1.0) can be read at the MMBase site.
See http://www.MMBase.org/license

*/
package org.mmbase.datatypes;

import java.util.*;
import org.mmbase.util.LocalizedString;

/**
 *
 * @author Michiel Meeuwissen
 * @version $Id$
 * @since MMBase-1.8
 */
public class CastException extends Exception {

    private static final long serialVersionUID = 0L;

    final Collection<LocalizedString> errors = new ArrayList<LocalizedString>();

    //javadoc is inherited
    public CastException() {
        super();
        errors.add(new LocalizedString(getMessage()));
    }

    //javadoc is inherited
    public CastException(String mes) {
        super(mes);
        errors.add(new LocalizedString(mes));
    }

    //javadoc is inherited
    public CastException(Throwable cause) {
        super(cause);
        errors.add(new LocalizedString(cause.getMessage()));
    }

    //javadoc is inherited
    public CastException(String mes, Throwable cause) {
        super(mes, cause);
        errors.add(new LocalizedString(mes));
    }

    /**
     * @since MMBase-1.9.2
     */
    public CastException(Collection<LocalizedString> mes) {
        super(mes.toString());
        errors.addAll(mes);
    }

    /**
     * @since MMBase-1.9.2
     */
    public CastException(String mes, Collection<LocalizedString> errors) {
        super(mes);
        this.errors.addAll(errors);
    }

    /**
     * @since MMBase-1.9.2
     */
    public CastException(Collection<LocalizedString> mes, Throwable cause) {
        super(mes.toString(), cause);
        this.errors.addAll(mes);
    }

    /**
     * @since MMBase-2.0
     */
    public Collection<LocalizedString> getErrors() {
        return errors;
    }

    @Override
    public String toString() {
        return getMessage() + (errors.size() >  0 ? errors : "");
    }



}
