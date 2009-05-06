/*

This software is OSI Certified Open Source Software.
OSI Certified is a certification mark of the Open Source Initiative.

The license (Mozilla version 1.0) can be read at the MMBase site.
See http://www.MMBase.org/license

*/

package org.mmbase.applications.media.urlcomposers.omroep;
import org.mmbase.applications.media.Format;
import org.mmbase.applications.media.urlcomposers.RamURLComposer;
import org.mmbase.applications.media.urlcomposers.RealURLComposer;



/**
 * An example. Produces an URL to the omroep cgi-scripts (for real and wm)
 *
 * @author Michiel Meeuwissen
 * @version $Id: CgiURLComposer.java,v 1.11 2009-04-16 10:28:07 michiel Exp $
 * @since MMBase-1.7
 */
public class CgiURLComposer extends RamURLComposer {

    /**
     * These scripts actually wrap the source in a ram or wmp
     */
    @Override
    public Format getFormat() {
        Format format = super.getFormat();
        if (format.isReal())  return Format.RAM;
        if (format.isWindowsMedia()) return Format.WMP;
        return format;
    }

    /**
     * Host must be cgi.omroep.nl script.
     */
    @Override
    public boolean canCompose() {
        return provider.getStringValue("host").equals("cgi.omroep.nl")  &&
            (provider.getStringValue("rootpath").charAt(0) != '%');
    }

    @Override
    protected StringBuffer getURLBuffer() {
        String rootpath = provider.getStringValue("rootpath");
        StringBuffer buff = new StringBuffer(provider.getStringValue("protocol") + "://cgi.omroep.nl" + rootpath);
        buff.append(source.getStringValue("url"));
        RealURLComposer.getRMArgs(buff, fragment, info); // append time, title
        if (fragment == null) {
            // cgi script does not work without title (for wm)?
            buff.append('?').append("title=geen_beschrijving");
        }
        return buff;
    }

}


