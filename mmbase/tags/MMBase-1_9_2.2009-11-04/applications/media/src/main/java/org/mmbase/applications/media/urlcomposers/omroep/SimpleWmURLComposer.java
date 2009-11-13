/*

This software is OSI Certified Open Source Software.
OSI Certified is a certification mark of the Open Source Initiative.

The license (Mozilla version 1.0) can be read at the MMBase site.
See http://www.MMBase.org/license

*/

package org.mmbase.applications.media.urlcomposers.omroep;
import org.mmbase.applications.media.urlcomposers.URLComposer;

/**
 *
 * @author Michiel Meeuwissen
 * @version $Id$
 * @since MMBase-1.7
 */
public class SimpleWmURLComposer extends URLComposer {

    @Override
    public boolean canCompose() {
        return provider.getStringValue("host").equals("cgi.omroep.nl") && provider.getStringValue("rootpath").charAt(0) != '%';
    }

    @Override
    protected StringBuilder getURLBuffer() {
        StringBuilder buff = new StringBuilder("mms://media.omroep.nl" + source.getStringValue("url"));
        return buff;
    }
}


