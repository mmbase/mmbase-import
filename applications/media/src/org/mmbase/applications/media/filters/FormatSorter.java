 /*
 
This software is OSI Certified Open Source Software.
OSI Certified is a certification mark of the Open Source Initiative.
 
The license (Mozilla version 1.0) can be read at the MMBase site.
See http://www.MMBase.org/license
 
 */

package org.mmbase.applications.media.filters;

import org.mmbase.applications.media.urlcomposers.URLComposer;
import org.mmbase.applications.media.builders.MediaSources;
import org.mmbase.applications.media.Format;
import java.util.*;
import org.mmbase.util.XMLBasicReader;
import org.w3c.dom.Element;
import org.mmbase.util.logging.*;

/**
 * This is a Sorter wich knows about a list of source
 * formats. This internal List can be filled by its constructors in
 * several ways. Sometimes e.g. the list would have only one entry and
 * therefore there are constructors with just one Format (or String)
 * argument.
 *
 *
 * @author  Michiel Meeuwissen
 * @version $Id: FormatSorter.java,v 1.2 2003-02-05 16:39:40 michiel Exp $
 */
public class FormatSorter extends  PreferenceSorter {
    private static Logger log = Logging.getLoggerInstance(FormatSorter.class.getName());

    protected List preferredFormats;

    public FormatSorter() {
        preferredFormats= new ArrayList();
    }
    
    public  FormatSorter(Format f) {
        this();
        preferredFormats.addAll(f.getSimilar());
    }
    public  FormatSorter(String f) {
        this(Format.get(f));
    }
    public  FormatSorter(List s) {
        preferredFormats = s;
    }
    
    protected int getPreference(URLComposer ri) {
        Format format = ri.getFormat();
        int index =  preferredFormats.indexOf(format);
        if (index == -1) { 
            if (log.isDebugEnabled()) log.debug("Not found format: '" + format + "' in" + preferredFormats);
            index = preferredFormats.size() + 1;
        }
        index = -index;   // low index =  high preference
        if (log.isDebugEnabled()) log.debug("preference of format '" + format + "': " + index);
        return index; 
    }

}

