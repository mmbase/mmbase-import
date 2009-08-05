package org.mmbase.mmsite;

import java.util.*;
import javax.servlet.http.HttpServletRequest;
import org.mmbase.util.transformers.CharTransformer;
import org.mmbase.util.transformers.Identifier;
import org.mmbase.util.xml.UtilReader;
import org.mmbase.bridge.*;
import org.mmbase.bridge.util.Queries;
import org.mmbase.storage.search.*;
import org.mmbase.framework.*;
import org.mmbase.framework.basic.DirectoryUrlConverter;
import org.mmbase.framework.basic.BasicFramework;
import org.mmbase.framework.basic.Url;
import org.mmbase.framework.basic.BasicUrl;
import org.mmbase.util.functions.*;
import org.mmbase.util.logging.*;

/**
 * The UrlConverter that can filter and create urls for pages in the site application.
 * It can be used as a '/' (root) UrlConverter. Use 'excludedPaths' to list directories to
 * exclude that might get mixed up and are not mentioned in web.xml.
 * It presumes (pages) nodes with the fields 'path' and 'template'.
 *
 * @author Andre van Toly
 * @version $Id: SiteUrlConverter.java,v 1.1 2009-02-11 21:06:13 andre Exp $
 * @since MMBase-1.9
 */
public class SiteUrlConverter extends DirectoryUrlConverter {
    private static final Logger log = Logging.getLoggerInstance(SiteUrlConverter.class);

    /* paths that should always be excluded (since this uc listens to all on '/') */
 	protected static ArrayList<String> excludedPaths = new ArrayList(Arrays.asList(
 	    "mmbase"
 	));
    protected static String homepage = "/index.jsp";
    protected static String extension = "";
    protected static boolean useExtension = false;

    public SiteUrlConverter(BasicFramework fw) {
        super(fw);
        setDirectory("/");
        addComponent(ComponentRepository.getInstance().getComponent("site"));
    }

    public void setUseExtension(boolean t) {
        useExtension = t;
    }
    
    public void setExtension(String e) {
        extension = e;
    }

    public void setHomepage(String s) {
        homepage = s;
    }

    @Override public int getDefaultWeight() {
        int q = super.getDefaultWeight();
        return Math.max(q, q + 2000);
    }

    /**
     * Generates a nice url linking to a template for a 'pages' node. 
     */
    @Override protected void getNiceDirectoryUrl(StringBuilder b, Block block, Parameters parameters, Parameters frameworkParameters,  boolean action) throws FrameworkException {
        if (log.isDebugEnabled()) {
            if (log.isDebugEnabled()) log.debug("" + parameters + frameworkParameters);
            if (log.isDebugEnabled()) log.debug("Found 'page' block: " + block);
        }
        
        if (block.getName().equals("page")) {
            Node n = parameters.get(Framework.N);
            parameters.set(Framework.N, null);
            
			String path = n.getStringValue("path");
			if (path.startsWith("/")) path = path.substring(1, path.length());
			if (path.endsWith("/")) path = path.substring(0, path.length() - 1);
			b.append(path);
			
			if (log.isDebugEnabled()) log.debug("b: " + b.toString());
        }
    }


    /**
     * Translates the result of {@link #getNiceUrl} back to an actual JSP which can render the block
     */
    @Override public Url getFilteredInternalDirectoryUrl(List<String> pa, Map<String, ?> params, Parameters frameworkParameters) throws FrameworkException {
		StringBuilder result = new StringBuilder();
		Cloud cloud = ContextProvider.getDefaultCloudContext().getCloud("mmbase");
		if (log.isDebugEnabled()) log.debug("path pieces: " + pa + ", path size: " + pa.size()); 
		
		StringBuilder sb = new StringBuilder();
		for (String piece: pa) {
			sb.append("/").append(piece);
			//if (log.isDebugEnabled()) log.debug("piece: " + piece);
		}
		String path = sb.toString();
		//if (log.isDebugEnabled()) log.debug("path: " + path);
        
        if (pa.size() == 0) {
            result.append(homepage);
			if (log.isDebugEnabled()) log.debug("Returning: " + result.toString());
            return new BasicUrl(this, result.toString());
            
		} else if (excludedPaths.contains(pa.get(0))) {
			if (log.isDebugEnabled()) log.debug("Returning null, path in excludepaths: " + path);
		    return Url.NOT;
		    
        } else {
			Node page = UrlUtils.getPagebyPath(cloud, path);
            if (page != null) {
				String template = page.getNodeValue("template").getStringValue("url");
				if (!template.startsWith("/")) result.append("/");
				result.append(template).append("?n=" + page.getNumber());
				
            } else {
				return Url.NOT;
			}
			
			if (log.isDebugEnabled()) log.debug("Returning: " + result.toString());
            return new BasicUrl(this, result.toString());
        }
        
    }

	

}
