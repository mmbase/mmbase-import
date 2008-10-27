/*

This software is OSI Certified Open Source Software.
OSI Certified is a certification mark of the Open Source Initiative.

The license (Mozilla version 1.0) can be read at the MMBase site.
See http://www.MMBase.org/license

*/
package org.mmbase.framework.basic;
import java.util.*;

import org.mmbase.framework.*;
import org.mmbase.util.functions.*;

/**
 * An Url object is the result of {@link UrlConverter}s. It basicly bundles a String ({@link
 * #getUrl}) with a 'quality' of this string.  
 *
 * @author Michiel Meeuwissen
 * @version $Id: Url.java,v 1.4 2008-10-27 17:16:34 michiel Exp $
 * @since MMBase-1.9
 * @todo EXPERIMENTAL
 */

public abstract class Url {
    private final UrlConverter urlConverter;
    private int quality = 0;
    public Url(UrlConverter uc) {
        this.url = url;
        this.urlConverter = uc;
    }
    public Url(UrlConverter uc, int q) {
        this.quality = q;
        this.urlConverter = uc;
    }
    /**
     * The actuall url as a String. 
     */
    public abstract String getUrl();

    public int getQuality() {
        return quality;
    }
    public UrlConverter getUrlConverter() {
        return urlConverter;
    }


    public String toString() {
        return url == null ? "NULL" : url;
    }
    public static final Url NOT = new Url(null, Integer.MIN_VALUE) {
            public String getUrl() { return null; }
        };

    public static Url createUrl(UrlConverter uc, final String url, int quality) {
        return new Url(uc, quality) {
            public String getUrl() { return url; }
        };
    }
}
