/*

This software is OSI Certified Open Source Software.
OSI Certified is a certification mark of the Open Source Initiative.

The license (Mozilla version 1.0) can be read at the MMBase site.
See http://www.MMBase.org/license

*/
package org.mmbase.framework.basic;

import java.util.*;

import javax.servlet.http.HttpServletRequest;

import org.mmbase.framework.*;
import org.mmbase.util.functions.Parameters;
import org.mmbase.util.functions.Parameter;

import org.mmbase.util.logging.Logger;
import org.mmbase.util.logging.Logging;

/**
 * Keeps track of several UrlConverters and chains them one after another.
 * If the outcome of an UrlConverter is not <code>null</code> its result is returned. The
 * question remains whether we want UrlConverters to be realy chained so that the
 * outcome of a converter can be added to the outcome of its preceder.
 *
 * @author Andr&eacute; van Toly
 * @version $Id: ChainedUrlConverter.java,v 1.13 2008-10-25 09:34:29 michiel Exp $
 * @since MMBase-1.9
 */
public class ChainedUrlConverter implements UrlConverter {

    private static final Logger log = Logging.getLoggerInstance(ChainedUrlConverter.class);


    public static Parameter<Class> URLCONVERTER_PARAM = new Parameter<Class>("urlconverter", Class.class);

    public static String URLCONVERTER = "org.mmbase.urlconverter";
    /**
     * List containing the UrlConverters found in the framework configuration.
     */
    private final List<UrlConverter> uclist = new ArrayList<UrlConverter>();
    private final List<Parameter>   parameterDefinition = new ArrayList<Parameter>();
    {
        parameterDefinition.add(URLCONVERTER_PARAM);
    }

    /**
     * Adds the UrlConverters to the list.
     */
    public void add(UrlConverter u) {
        uclist.add(u);
        for (Parameter p : u.getParameterDefinition()) {
            if (! parameterDefinition.contains(p)) {
                parameterDefinition.add(p);
            }
        }
    }
    public boolean contains(UrlConverter u) {
        return uclist.contains(u);
    }

    public Parameter[] getParameterDefinition() {
        return parameterDefinition.toArray(Parameter.EMPTY);
    }



//     public static class Link {
//         public final static Link NULL = new Link(null, null);
//         public final Block block;
//         public final UrlConverter converter;
//         public Link(UrlConverter converter, Block b) {
//             this.block = b;
//             this.converter = converter;
//         }
//         public String getUrl() {
//         }
//     }

//     public Link chain(String path, Parameters frameworkParameters) throws FrameworkException {
//         for (UrlConverter uc : uclist) {
//             Block b = uc.getBlock(path, frameworkParameters);
//             if (b != null) {
//                 return new Link(uc, b);
//             }
//         }
//         return Link.NULL;
//     }

    public boolean isFilteredMode(Parameters frameworkParameters) throws FrameworkException {
        for (UrlConverter uc : uclist) {
            if (uc.isFilteredMode(frameworkParameters)) return true;
        }
        return false;
    }

    protected Url getProposal(Url u, Parameters frameworkParameters) {
        HttpServletRequest request = BasicUrlConverter.getUserRequest(frameworkParameters.get(Parameter.REQUEST));
        UrlConverter current  = (UrlConverter) request.getAttribute(URLCONVERTER);
        Class preferred       = frameworkParameters.get(URLCONVERTER_PARAM);
        Url b = u;
        if (preferred != null && ! preferred.isInstance(u.getUrlConverter())) {
            b = new Url(b, b.getQuality() - 10000);
        }
        if (current != null && u.getUrlConverter() != current) {
            b = new Url(b, b.getQuality() - 10000);
        }
        return b;
    }

    /**
     * The URL to be printed in a page
     */
    public Url getUrl(String path,
                         Map<String, Object> params,
                         Parameters frameworkParameters, boolean escapeAmps) throws FrameworkException {
        Url result = Url.NOT;
        for (UrlConverter uc : uclist) {
            Url proposal = getProposal(uc.getUrl(path, params, frameworkParameters, escapeAmps), frameworkParameters);
            if (proposal.getQuality() > result.getQuality()) {
                result = proposal;
            }

        }
        return result;
    }

    public Url getProcessUrl(String path,
                                Map<String, Object> params,
                                Parameters frameworkParameters, boolean escapeAmps) throws FrameworkException {
        Url result = Url.NOT;
        for (UrlConverter uc : uclist) {
            Url proposal = getProposal(uc.getProcessUrl(path, params, frameworkParameters, escapeAmps), frameworkParameters);
            if (proposal.getQuality() > result.getQuality()) {
                result = proposal;
            }
        }
        return result;
    }


    /**
     * The 'technical' url
     */
    public Url getInternalUrl(String path,
                              Map<String, Object> params,
                              Parameters frameworkParameters) throws FrameworkException {
        Url result = Url.NOT;
        for (UrlConverter uc : uclist) {
            Url proposal = getProposal(uc.getInternalUrl(path, params, frameworkParameters), frameworkParameters);
            if (proposal.getQuality() > result.getQuality()) {
                result = proposal;
            }

        }
        return result;
    }

    public String toString() {
        return "ChainedUrlConverter" + uclist;
    }

}
