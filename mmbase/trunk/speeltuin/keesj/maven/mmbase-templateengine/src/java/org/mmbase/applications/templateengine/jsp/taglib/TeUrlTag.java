package org.mmbase.applications.templateengine.jsp.taglib;

import javax.servlet.jsp.*;

import org.mmbase.bridge.jsp.taglib.*;
import org.mmbase.util.*;
import org.mmbase.util.logging.*;
import org.mmbase.applications.templateengine.*;
import java.util.*;
public class TeUrlTag extends ContextReferrerTag {
    private static Logger log = Logging.getLoggerInstance(TeUrlTag.class);
    private String name = "";

    private List referids = null;
    public TeUrlTag() {
        super();
    }

    public void setName(String name) {
        this.name = name;
    }

    public void setReferids(String id) {
        this.referids = StringSplitter.split(id);
    }

    public int doEndTag() throws javax.servlet.jsp.JspTagException {
        try {
            NodeProvider np = (NodeProvider) findAncestorWithClass(this, NodeProvider.class);

            WhiteBoard wb = (WhiteBoard) pageContext.getRequest().getAttribute("wb");

            if (np == null) {
                pageContext.getOut().write(" url tag not in node provider" + name + "!");
                return EVAL_PAGE;
            }
            if (wb == null) {
                pageContext.getOut().write("no whiteboard in context!");
                return EVAL_PAGE;
            }

            String type = np.getNodeVar().getNodeManager().getName();
            Facade facade = wb.getFacade();
            
            List list = new Vector();

			if (name != null && name.trim().length() >0 ){
				list.add(name);
			}
            list.add(np.getNodeVar());
            if (referids != null) {
                Iterator i = referids.iterator();
                while (i.hasNext()) {
                    String key = (String) i.next();
                    Object val = getObject(key);
                    if (val != null) {
                        //log.debug("referid (key, value,type)=(" + key + "," + val + "," + val.getClass().getName() + ")");
                        list.add(val);
                    }
                }
            }

            String content = facade.getNavigationControl().resoveURL(wb.getCurrentNavigation(), list);
            // FIXME: yup
            //String url = facade.getNavigationControl().findUrlString(wb.getCurrentNavigation(),type);
            pageContext.getOut().write(content);
            referids = null;
        } catch (java.io.IOException e) {
            throw new JspTagException("IO Error: " + e.getMessage());
        }
        return EVAL_PAGE;
    }
}
