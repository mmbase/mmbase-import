 /*

This software is OSI Certified Open Source Software.
OSI Certified is a certification mark of the Open Source Initiative.

The license (Mozilla version 1.0) can be read at the MMBase site.
See http://www.MMBase.org/license

  */

package org.mmbase.applications.media.filters;

import org.mmbase.applications.media.urlcomposers.URLComposer;
import java.util.regex.*;
import org.mmbase.util.logging.*;
import org.mmbase.util.xml.DocumentReader;
import org.w3c.dom.Element;

/**
 *
 *
 * @author Michiel Meeuwissen
 * @since MMBase-1.9.2
 * @version $Id$
 */
public class  FieldValueLabeler  extends Labeler  {
    private static final Logger log = Logging.getLoggerInstance(FieldValueLabeler.class);

    private String field = "label";
    private Pattern pattern = null;
    private String label = null;
    private String key = "label";

    public void setField(String f) {
        field = f;
    }
    public void setPattern(String p) {
        pattern = Pattern.compile(p);
    }

    public void setLabel(String l) {
        label = l;
    }

    public void setKey(String k) {
        key = k;
    }

    @Override
    public void configure(DocumentReader reader, Element element) {
        FilterUtils.propertiesConfigure(this, reader, element);
    }


    @Override
    protected void label(URLComposer uc) {
        if (pattern.matcher(uc.getSource().getStringValue(field)).matches()) {
            uc.getInfo().put(key, label);
        }
    }

}
