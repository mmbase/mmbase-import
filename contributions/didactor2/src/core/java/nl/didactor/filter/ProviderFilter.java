/*

This software is OSI Certified Open Source Software.
OSI Certified is a certification mark of the Open Source Initiative.

The license (Mozilla version 1.0) can be read at the MMBase site.
See http://www.MMBase.org/license

*/
package nl.didactor.filter;

import javax.servlet.*;
import javax.servlet.http.*;
import java.util.*;

import org.mmbase.bridge.*;
import org.mmbase.util.*;
import org.mmbase.module.core.MMBase;
import org.mmbase.module.core.MMBaseContext;
import org.mmbase.servlet.*;

import org.mmbase.util.logging.*;

/**
 * Redirects request based on information supplied by the jumpers builder.
 *

 * @author Michiel Meeuwissen
 * @version $Id: ProviderFilter.java,v 1.5 2007-06-05 08:56:22 michiel Exp $
 */
public class ProviderFilter implements Filter, MMBaseStarter {
    private static final Logger log = Logging.getLoggerInstance(ProviderFilter.class);
    private static MMBase mmbase;

    private static Map<String, Map<String, Object>> providerCache = new HashMap<String, Map<String, Object>>();

    public static void clearCache() {
        providerCache.clear();
    }

    /**
     * Initializes the filter
     */
    public void init(javax.servlet.FilterConfig filterConfig) throws ServletException {
        log.info("Starting ProviderFilter with " + filterConfig);
        MMBaseContext.init(filterConfig.getServletContext());
        MMBaseContext.initHtmlRoot();
        // stuff that can take indefinite amount of time (database down and so on) is done in separate thread
        Thread initThread = new MMBaseStartThread(this);
        initThread.start();
    }
    public void setInitException(ServletException se) {
        // never mind, simply, ignore
    }


    public MMBase getMMBase() {
        return mmbase;
    }

    public void setMMBase(MMBase mmb) {
        mmbase = mmb;
    }


    protected Node selectByRelatedUrl(NodeList nodes, String url) {
        log.debug("Select  for " + url);
        NodeIterator ni = nodes.nodeIterator();
        while (ni.hasNext()) {
            Node suggestion = ni.nextNode();
            NodeList urls = suggestion.getRelatedNodes("urls");
            NodeIterator ui = urls.nodeIterator();
            while (ui.hasNext()) {
                Node urlNode = ui.nextNode();
                String u = urlNode.getStringValue("url");
                if (u.equals(url)) {
                    log.debug("found "  + suggestion.getNumber());
                    return suggestion;
                }
            }
        }
        return null;
    }

    protected Locale findLocale(Node provider, Node education) {
        Locale locale;
        {
            String ls = provider.getStringValue("locale");
            if (ls == null || "".equals(ls)) {
                locale = provider.getCloud().getCloudContext().getDefaultLocale();
            } else {
                locale = LocalizedString.getLocale(ls);
            }
        }

        String variant = provider.getStringValue("path");
        if (variant != null && (! "".equals(variant)) && education != null) {
            String educationPath = education.getStringValue("path");
            if (educationPath != null && (! "".equals(educationPath))) {
                variant += "_" + educationPath;
            }
        }
        if (variant != null && ! "".equals(variant)) {
            return new Locale(locale.getLanguage(), locale.getCountry(), variant);
        } else {
            return locale;
        }

    }


    /**
     * Filters the request: tries to find a jumper and redirects to this url when found, otherwise the 
     * request will be handled somewhere else in the filterchain.
     * @param servletRequest The ServletRequest.
     * @param servletResponse The ServletResponse.
     * @param filterChain The FilterChain.
     */
    public void doFilter(ServletRequest request, ServletResponse response, FilterChain filterChain) throws java.io.IOException, ServletException {
        if (mmbase == null) {
            // if mmbase not yet running. Things not using mmbase can work, otherwise this may give
            // 503.
            log.debug("NO MMBASE member");
            filterChain.doFilter(request, response);
            return;
        }
        HttpServletRequest req = (HttpServletRequest) request;
        String serverName = req.getServerName();
        String contextPath = req.getContextPath();

        String educationParameter = req.getParameter("education");
        String providerParameter  = req.getParameter("provider");
        
        String key = serverName + contextPath + ':' + educationParameter + ':' + providerParameter;
        Map<String, Object> attributes = providerCache.get(key);
        if (attributes == null) {

            String[] urls = {"http://" + serverName + contextPath, 
                             "http://" + serverName, 
                            //serverName cannot alone consitute a valid URL, but this was the only
                            //implemention in previous versions of Didactor.
                             serverName};

            attributes = new HashMap<String, Object>();

            Cloud cloud = ContextProvider.getDefaultCloudContext().getCloud("mmbase");            
            Node provider = null;            
            {
                if (cloud.hasNode(providerParameter)) {
                    // explicitely stated provider on the URL.
                    // Don't know if that should be maintained.
                    provider = cloud.getNode(providerParameter);
                } else {
                    NodeList providers = cloud.getNodeManager("providers").getList(null, null, null);
                    if (providers.size() == 0) {
                        // create one
                        log.info("No provider objects found, should at least be one");                    
                    } else if (providers.size() == 1) {
                        provider = providers.getNode(0);
                    } else {
                        // which are we going to use?
                        for (String u : urls) {
                            provider = selectByRelatedUrl(providers, u);
                            if (provider != null) break;
                        }

                        // no matching URL object directly related to provider.
                        // Try via education object too.
                    }                    
                }
            }
            Node education = null;                
            {
                NodeList educations = provider != null ? 
                    provider.getRelatedNodes("educations") :
                    cloud.getNodeManager("educations").getList(null, null, null);
                
                for (String u : urls) {
                    education = selectByRelatedUrl(educations, u);
                    if (education != null) break;
                }                    


                if (education == null && provider != null) {
                    NodeList eds = provider.getRelatedNodes("educations");
                    if (eds.size() > 0) {
                        education = eds.nodeIterator().nextNode();
                    }   
                }
                if (education != null) {
                    log.debug("Found education " + education.getNumber());
                    attributes.put("education", "" + education.getNumber()); 
                } else {
                    log.warn("No education found for " + key);
                    attributes.put("education", null);
                }
                // try determining provider if education found, but not yet an education
                if (provider == null && education != null) {
                    NodeList providers = education.getRelatedNodes("providers");
                    if (providers.size() > 0) {
                        provider = providers.nodeIterator().nextNode();
                    }
                }
            }

            Locale locale; 
            if (provider != null) {
                log.debug("Found provider " + provider.getNumber());
                attributes.put("provider", "" + provider.getNumber());                
                locale = findLocale(provider, education);
            } else {
                log.warn("No provider found for " + key);
                attributes.put("provider", null);
                locale = cloud.getLocale();
            }
            
            attributes.put("javax.servlet.jsp.jstl.fmt.locale.request", locale);
            attributes.put("language", locale.toString());
            attributes.put("locale", locale);

            if (provider == null) {
                HttpServletResponse res = (HttpServletResponse) response;
                request.setAttribute("org.mmbase.servlet.error.message", "No provider found for '" + key + "'");
                res.sendError(HttpServletResponse.SC_NOT_FOUND, "No provider found for '" + key + "'");
                return;
            }

            if (education != null) {
                attributes.put("includePath", provider.getNumber() + "," + education.getNumber());
            } else {
                attributes.put("includePath", "" + provider.getNumber());
            }
            attributes.put("referids", "class?,workgroup?");

            providerCache.put(key, attributes);

            if (log.isDebugEnabled()) {
                log.debug("Found attributes for " + key + " " + attributes);
            }
        } else {
            if (log.isTraceEnabled()) {
                log.trace("Found attributes for " + key + " " + attributes);
            }
        }

        // copy all attributes to the request.
        for (Map.Entry<String, Object> entry : attributes.entrySet()) {
            request.setAttribute(entry.getKey(), entry.getValue());
        }
        assert request.getAttribute("provider") != null : "attributes" + attributes; 

        filterChain.doFilter(request, response);
    }



    public void destroy() {
    }


}
