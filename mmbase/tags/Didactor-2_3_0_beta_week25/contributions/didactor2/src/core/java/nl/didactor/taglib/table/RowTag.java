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
 * @javadoc
 * @author Johannes Verelst &lt;johannes.verelst@eo.nl&gt;
 */
public class RowTag extends CloudReferrerTag { 
    /**
     */
    public int doStartTag() throws JspTagException {
        try {
            TableTag tt = (TableTag)findParentTag(TableTag.class, null, true);
            pageContext.getOut().print(tt.getLabel("row.start"));
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
            pageContext.getOut().print(tt.getLabel("row.end"));
        } catch (IOException ioe) {
            throw new TaglibException(ioe);
        }
        return SKIP_BODY;
    }
}
