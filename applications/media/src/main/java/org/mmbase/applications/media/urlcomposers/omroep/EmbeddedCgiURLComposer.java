/*
  
This software is OSI Certified Open Source Software.
OSI Certified is a certification mark of the Open Source Initiative.
  
The license (Mozilla version 1.0) can be read at the MMBase site.
See http://www.MMBase.org/license
  
*/

package org.mmbase.applications.media.urlcomposers.omroep;
import org.mmbase.applications.media.Format;
import org.mmbase.applications.media.urlcomposers.*;
import org.mmbase.module.core.*;

import java.util.*;
import java.net.*;
import java.text.*;


/**
 * An example. Produces an URL to the omroep cgi-scripts (for real and wm)
 *
 * @author Michiel Meeuwissen
 * @version $Id$
 * @since MMBase-1.9.1
 */
public class EmbeddedCgiURLComposer extends RealURLComposer {


    @Override
    public String getGUIIndicator(Map options) {
        return super.getGUIIndicator(options) + " (for embedding)";
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
    protected StringBuilder getURLBuffer() {
        String rootpath = provider.getStringValue("rootpath");
        StringBuilder buff = new StringBuilder(provider.getStringValue("protocol") + "://cgi.omroep.nl" + rootpath);
        buff.append(source.getStringValue("url"));
        RealURLComposer.getRMArgs(buff, fragment, info); // append time, title
        if (fragment == null) {
            // cgi script does not work without title (for wm)?
            buff.append('?').append("title=geen_beschrijving");
        }
        buff.append(";embed=1");
        return buff;            
    }

}


