 /*
 
This software is OSI Certified Open Source Software.
OSI Certified is a certification mark of the Open Source Initiative.
 
The license (Mozilla version 1.0) can be read at the MMBase site.
See http://www.MMBase.org/license
 
 */

package org.mmbase.util.media;

import org.mmbase.module.builders.media.ResponseInfo;
import org.mmbase.module.builders.media.MediaSources;
import org.mmbase.module.builders.media.Format;
import java.util.*;
import org.mmbase.util.XMLBasicReader;
import org.w3c.dom.Element;
import org.mmbase.util.logging.*;

/**
 * This can sort a list with the requested formats on top.
 * @author  Michiel Meeuwissen
 * @version $Id: FormatComparator.java,v 1.3 2003-01-08 22:20:25 michiel Exp $
 */
public class FormatComparator extends  PreferenceComparator {
    private static Logger log = Logging.getLoggerInstance(FormatComparator.class.getName());

    protected List preferredSources;

    public FormatComparator() {
        preferredSources= new ArrayList();
    }
    
    public  FormatComparator(Format f) {
        this();
        preferredSources.addAll(f.getSimilar());
    }
    public  FormatComparator(String f) {
        this(Format.get(f));
    }
    public  FormatComparator(List s) {
        preferredSources = s;
    }
    
    protected int getPreference(ResponseInfo ri) {
        Format format = (Format) ri.getSource().getFunctionValue(MediaSources.FUNCTION_FORMAT, null);
        int index =  preferredSources.indexOf(format);
        if (index == -1) { 
            log.debug("Not found format: '" + format + "' in" + preferredSources);
            index = preferredSources.size() + 1;
        }
        index = -index;   // low index =  high preference
        if (log.isDebugEnabled()) log.debug("preference of format '" + format + "': " + index);
        return index; 
    }

}

