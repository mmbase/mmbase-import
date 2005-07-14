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
public class RowTag extends CloudReferrerTag { 
    /**
     * Execute the body of the tag if the current user has the given role.
     */
    public int doStartTag() throws JspTagException {
        return EVAL_BODY;
    }

    // Code copied from MMBase 'CloudTag' code. Don't know why this is the case
    // but apparently there is no body written if this code is not included here
    // if EVAL_BODY == EVAL_BODY_BUFFERED
    public int doAfterBody() throws JspTagException {
        try {
            if (bodyContent != null) {
                TableTag tt = (TableTag)findParentTag(TableTag.class, null, true);
                getPreviousOut().print(tt.getLabel("row.start"));
                bodyContent.writeOut(bodyContent.getEnclosingWriter());
                getPreviousOut().print(tt.getLabel("row.end"));
            }    
        } catch (IOException ioe) {
           throw new TaglibException(ioe);
        }
        return SKIP_BODY;
    }
}
