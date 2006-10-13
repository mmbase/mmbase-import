package nl.eo.patmos;

import org.mmbase.bridge.Cloud;
import org.mmbase.framework.*;
import javax.servlet.jsp.PageContext;
import java.util.*;

public class PatmosFramework implements Framework {
    public String getName() {
        return "PATMOS (EO)";
    }

    public String getComponentUrl(Component component, String block, String type) {
        return null;
    }

    public String getUrl(String page, String component, Cloud cloud, PageContext pageContext, List params) throws javax.servlet.jsp.JspTagException {
        if (component != null && !"".equals(component)) {
            return UrlResolver.findUrl(component + "/" + page, cloud, pageContext, params);
        } else {
            return page;
        }
    }

    public boolean makeRelativeUrl() {
        return false;
    }
}
