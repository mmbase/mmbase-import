package org.mmbase.bridge.jsp.taglib;

import javax.servlet.jsp.*;
import javax.servlet.jsp.tagext.*;


/**
* conditional sub tag for list. content enclosed in the head tag
* will only be displayed if the current list item is the first item
* in the list therefore the head tag uses MMList.isFirst();
* @author Kees Jongenburger
**/
public class MMListHead extends BodyTagSupport{
	
	public int doStartTag() throws JspException{
		try {
			MMList mmList = (MMList)this.findAncestorWithClass((Tag)this,Class.forName("org.mmbase.bridge.jsp.taglib.MMList"));
			
			if (mmList == null)
				return SKIP_BODY;
			if (mmList.isFirst())
				return EVAL_BODY_TAG;
		} catch (Exception e){
			return SKIP_BODY;
		}
		return SKIP_BODY;
	}
	
	public int doEndTag() throws JspException{
		try{
			if(bodyContent != null)
				bodyContent.writeOut(bodyContent.getEnclosingWriter());
		} catch(java.io.IOException e){
			throw new JspException("IO Error: " + e.getMessage());
		}
		return EVAL_PAGE;
	}
}
