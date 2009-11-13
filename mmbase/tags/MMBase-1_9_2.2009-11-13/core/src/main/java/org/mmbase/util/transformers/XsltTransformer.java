/*

This software is OSI Certified Open Source Software.
OSI Certified is a certification mark of the Open Source Initiative.

The license (Mozilla version 1.0) can be read at the MMBase site.
See http://www.MMBase.org/license

*/
package org.mmbase.util.transformers;

import org.mmbase.util.*;

import java.io.*;
import java.util.*;
import java.net.URL;
import javax.xml.transform.stream.StreamResult;
import javax.xml.transform.stream.StreamSource;
import javax.xml.transform.Source;
import javax.xml.transform.Result;

/**
 * A Transformer based on an XSLT.
 *
 * @author Michiel Meeuwissen
 * @since MMBase-1.9.2
 * @version $Id$
 */

public  class XsltTransformer extends InputStreamTransformer {


    protected URL xslt = ResourceLoader.getConfigurationRoot().getResource("xslt/copy.xslt");


    /**
     * Sets the XSLT (as a resource in the mmbase configuration root).
     */
    public void setXslt(String x) {
        xslt = ResourceLoader.getConfigurationRoot().getResource(x);
    }

    public  OutputStream transform(InputStream r, OutputStream o) {
        Source xml = new StreamSource(r);
        Result res = new StreamResult(o);
        Map<String, Object> params = new HashMap<String, Object>();
        try {
            XSLTransformer.transform(xml, xslt, res, params);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }

        return o;
    }


}
