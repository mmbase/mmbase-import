package nl.didactor.taglib.table;

import java.io.IOException;
import java.util.*;
import java.text.*;
import javax.servlet.jsp.tagext.*;
import javax.servlet.jsp.*;
import javax.servlet.Servlet;
import org.mmbase.bridge.jsp.taglib.*;
import org.mmbase.module.core.*;

/**
 * @author Johannes Verelst &lt;johannes.verelst@eo.nl&gt;
 */
public class CellTag extends CloudReferrerTag { 
    /**
     * Execute the body of the tag if the current user has the given role.
     */
    public int doStartTag() throws JspTagException {
        try {
            TableTag tt = (TableTag)findParentTag(TableTag.class, null, true);
            pageContext.getOut().print(tt.getLabel("cell.start"));
        } catch (IOException ioe) {
           throw new TaglibException(ioe);
        }
        return EVAL_BODY;
    }

    public int doAfterBody() throws JspTagException {
        if (EVAL_BODY == EVAL_BODY_BUFFERED) {
            try {
                if (bodyContent != null) {
                    bodyContent.writeOut(bodyContent.getEnclosingWriter());
                }
            } catch (IOException ioe) {
                throw new TaglibException(ioe);
            }
        }
        try {
            TableTag tt = (TableTag)findParentTag(TableTag.class, null, true);
            pageContext.getOut().print(tt.getLabel("cell.end"));
        } catch (IOException ioe) {
           throw new TaglibException(ioe);
        }
        return EVAL_PAGE;
    }
}
