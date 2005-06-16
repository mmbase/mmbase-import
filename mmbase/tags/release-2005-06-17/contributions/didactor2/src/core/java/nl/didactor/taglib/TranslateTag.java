package nl.didactor.taglib;

import java.io.IOException;
import java.util.*;
import java.text.*;
import javax.servlet.jsp.tagext.*;
import javax.servlet.jsp.*;
import javax.servlet.Servlet;

/**
 * Translate tag: it will figure out a translation for a given
 * abstract path.
 * @author Johannes Verelst &lt;johannes.verelst@eo.nl&gt;
 */
public class TranslateTag extends BodyTagSupport { 
    private String translatePath;
    private String translateDebug;

    // These parameters are set with the different setXyz() methods
    // they may not be manipulated by this class, because that will
    // mess up in case we have tagpooling enabled.
    private String path;
    private String debug;
    private String id;

    public void setId(String id) {
        this.id = id;
    }

    public void setPath(String path) {
        this.path = path;
    }

    public void setDebug(String value) {
        this.debug = value;
    }

    public int doStartTag() throws JspTagException 
    {
        String translationpath = ((Servlet)pageContext.getPage()).getServletConfig().getServletContext().getRealPath("/WEB-INF/config/translations");
        TranslateTable.init(translationpath);
        if (path == null) 
        {
        	path = "";
            translatePath = (String)pageContext.getAttribute("path");
            if (translatePath == null) 
            {
                translatePath = "";
            }
        } 
        else 
        {
            pageContext.setAttribute("path", path);
            translatePath = path;

            if ("true".equals(debug)) 
            {
                pageContext.setAttribute("translateDebug", "true");
            }
        }
        translateDebug = debug;

        if (translateDebug == null) 
        {
            translateDebug = (String)pageContext.getAttribute("translateDebug");
        }
        
        return EVAL_BODY_BUFFERED;
    }

    public int doAfterBody() {
        String body = getBodyContent().getString();
        TranslateTable tt = new TranslateTable(path);
        String translation = "";

        if (id != null)
            translation = tt.translate(id);
        else
            translation = tt.translate(body);

        if (translation == null)
            translation = body;

        if ("true".equals(translateDebug)) {
            translation = "<FONT COLOR=\"#FF0000\">" + translation + "</FONT>";
        }

        try {
            getPreviousOut().print(translation);
        } catch (java.io.IOException e) {
        }

        return SKIP_BODY;
    }
}
