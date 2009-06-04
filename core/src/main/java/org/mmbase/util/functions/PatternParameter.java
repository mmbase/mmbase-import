/*

This software is OSI Certified Open Source Software.
OSI Certified is a certification mark of the Open Source Initiative.

The license (Mozilla version 1.0) can be read at the MMBase site.
See http://www.MMBase.org/license

*/

package org.mmbase.util.functions;

import org.mmbase.datatypes.*;
import org.mmbase.util.*;
import org.mmbase.util.logging.*;
import java.util.*;
import java.util.regex.Pattern;


/**
 * Especially the blocks of the framework may want to allow for all parameters according to a
 * certain pattern.
 *
 * @author Michiel Meeuwissen
 * @since  MMBase-1.9
 * @version $Id$
 */

public class PatternParameter<C> extends Parameter<C> {
    private static final Logger log = Logging.getLoggerInstance(PatternParameter.class);

    private static final long serialVersionUID = 1L;
    private final Pattern pattern;

    public PatternParameter(Pattern p, DataType<C> dataType) {
        super(p.pattern(), dataType, true);
        pattern = p;
    }
    public PatternParameter(Pattern p, DataType<C> dataType, boolean copy) { 
        super(p.pattern(), dataType, copy);
        pattern = p;
    }

    public PatternParameter(Pattern p, Class<C> type) { 
        super(p.pattern(), type);
        pattern = p;
    }

    public PatternParameter(Pattern p, Class<C> type, C defaultValue) {
        super(p.pattern(), type, defaultValue);
        pattern = p;
    }
    public PatternParameter(Pattern p, C defaultValue) {
        super(p.pattern(), defaultValue);
        pattern = p;
    }


    public Pattern getPattern() {
        return pattern;
    }
    @Override
    public boolean matches(String key) {
        return pattern.matcher(key).matches();
    }


}
