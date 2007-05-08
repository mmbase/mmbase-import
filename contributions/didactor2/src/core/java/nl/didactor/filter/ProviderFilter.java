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
import org.mmbase.module.core.MMBase;
import org.mmbase.module.core.MMBaseContext;
import org.mmbase.servlet.*;

import org.mmbase.util.logging.*;

/**
 * Redirects request based on information supplied by the jumpers builder.
 *

 * @author Michiel Meeuwissen
 * @version $Id: ProviderFilter.java,v 1.1 2007-05-08 12:30:41 michiel Exp $
 */
public class ProviderFilter implements Filter, MMBaseStarter {
    private static final Logger log = Logging.getLoggerInstance(ProviderFilter.class);
    private static MMBase mmbase;

    private static Map<String, Map<String, Object>> providerCache = new HashMap<String, Map<String, Object>>();


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


    protected Node findProvider(NodeList providers, String providerUrl) {
        NodeIterator ni = providers.nodeIterator();
        while (ni.hasNext()) {
            Node suggestion = ni.nextNode();
            NodeList urls = suggestion.getRelatedNodes("urls");
            NodeIterator ui = urls.nodeIterator();
            while (ui.hasNext()) {
                Node url = ui.nextNode();
                String u = url.getStringValue("url");
                if (u.equals(providerUrl)) {
                    return suggestion;
                }
            }
        }
        return null;
    }
    protected class Provider {
        public Node node;
        Provider(Node p) { node = p; }
    }
    protected Node findEducation(Provider provider, String providerUrl) {
        if (provider.node != null) { 
            //
        } else {
            /*
            NodeIterator ni = providers.nodeIterator();
            while (ni.hasNext()) {
                Node suggestion = ni.nextNode();
                NodeList educations = suggestion.getRelatedNodes("educations");
                NodeIterator ei = educationsnodeIterator();
                while (ei.hasNext()) {
                    Node education = ei.nextNode();
                    NodeList urls = education.getRelatedNodes("urs", "related", "both");
                    NodeIterator ui = urls.nodeIterator();
                    while (ui.hasNext()) {
                        Node url = ui.nextNode();
                        String u = url.getStringValue("url");
                        if (u.equals(providerUrl)) {
                            return suggestion;
                        }
                    }
                }
            }
            */
        }
        return null;
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
            // if mmbase not yet running. Things not using mmbase can work, otherwise this may give 503.
            filterChain.doFilter(request, response);
            return;
        }
        HttpServletRequest req = (HttpServletRequest) request;
        String serverName = request.getServerName();
        String contextPath = req.getContextPath();

        String educationParameter = req.getParameter("education");
        String providerParameter  = req.getParameter("provider");
        
        String key = serverName + contextPath + ':' + educationParameter + ':' + providerParameter;
        Map<String, Object> attributes = providerCache.get(key);
        if (attributes == null) {
            attributes = new HashMap<String, Object>();


            Cloud cloud = ContextProvider.getDefaultCloudContext().getCloud("mmbase");

            {
                Node provider = null;            
                
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
                        provider = findProvider(providers, "http://" + serverName + contextPath);
                        if (provider == null) {
                            provider = findProvider(providers, "http://" + serverName);
                        }
                        if (provider == null) {
                            //serverName cannot alone consitute a valid URL, but this was the only
                            //implemention in previous versions of Didactor.
                            provider = findProvider(providers, serverName);
                        }
                        // no matching URL object directly related to provider.
                        // Try via education object too.
                    }
                    
                    if (provider != null) {
                        attributes.put("provider", provider.getNumber());
                    } else {
                        log.warn("No provider found for " + key);
                        attributes.put("provider", null);
                    }
                }
            }

            {
                Node education = null;
            }

            providerCache.put(key, attributes);
        }

        // copy all attributes to the request.
        for (Map.Entry<String, Object> entry : attributes.entrySet()) {
            request.setAttribute(entry.getKey(), entry.getValue());
        }
        filterChain.doFilter(request, response);
    }



    public void destroy() {
    }


}
