package nl.didactor.taglib;

import java.io.IOException;
import java.util.*;
import java.text.*;
import javax.servlet.jsp.tagext.Tag;
import javax.servlet.jsp.tagext.BodyTag;
import javax.servlet.jsp.tagext.BodyTagSupport;
import javax.servlet.jsp.JspException;
import javax.servlet.jsp.JspTagException;
import org.mmbase.bridge.*;
import org.mmbase.bridge.implementation.BasicNodeList;
import org.mmbase.bridge.jsp.taglib.*;

/**
 * This tag will return a shuffled list: useful for shuffling
 * possible answers to a question.
 * @author Johannes Verelst &lt;johannes.verelst@eo.nl&gt;
 */
public class ShuffledListTag extends AbstractNodeListTag {
    private static Random random = new Random();
        
    public int doStartTag() throws JspException {
        return doStartTagHelper();
    }
     
    protected int setReturnValues(NodeList nodes, boolean trim) throws JspTagException {
        BasicNodeList bnodes = (BasicNodeList)nodes;
        for (int i=0; i<bnodes.size(); i++) {
            int randomindex = random.nextInt(bnodes.size());
            Node o1 = bnodes.getNode(i);
            Node o2 = bnodes.getNode(randomindex);
            bnodes.set(i, o2);
            bnodes.set(randomindex, o1);
        }
            
        return super.setReturnValues(bnodes, trim);
    }
}
