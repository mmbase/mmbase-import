/*

This software is OSI Certified Open Source Software.
OSI Certified is a certification mark of the Open Source Initiative.

The license (Mozilla version 1.0) can be read at the MMBase site.
See http://www.MMBase.org/license

*/
package org.mmbase.bridge.jsp.taglib;

import javax.servlet.jsp.*;
import javax.servlet.jsp.tagext.*;

import java.util.Stack;
import org.mmbase.bridge.*;

import org.mmbase.util.logging.Logger;
import org.mmbase.util.logging.Logging;

/**
* MMBase taglib for use in combination with MMBase
* BaseTag the base Class of moost MMBase jsp tags
* @author Kees Jongenburger
**/

public abstract class BaseTag extends TagExtraInfo implements Tag, TagIdentifier{

    private static Logger log = Logging.getLoggerInstance(BaseTag.class.getName());
    /**
    * static Cloud context 
    **/
    private static CloudContext cloudContext;
    public  static String DEFAULT_CLOUD_NAME = "mmbase";
    private Cloud  pageCloud; 
    
    
    
    public BaseTag() {
    }
    
    // michiel: all tags can have an attribute 'id'.
    private String id;
    public  void setId(String i){
        this.id = id;
    }   
    
    public String getId() {
        if (id == null) return "";
            return id;
    }
    
    /**
    * @return the default cloud context 
    **/
    public CloudContext getDefaultCloudContext(){
        if (cloudContext == null){
            cloudContext=LocalContext.getCloudContext();
        } 
        return cloudContext;
    }
    
    /**
    * @return the default cloud being the cloud with name equals to the DEFAULT_CLOUD_NAME
    * defined in this class
    **/
    public Cloud getDefaultCloud(){
        return getPageCloud();
    }
    
    /**
    * @return the page cloud being the cloud set with the <mm:cloud> tag
    **/
    public Cloud getPageCloud(){
        if (pageCloud == null){
            pageCloud = (Cloud)pageContext.getAttribute("cloud");
            // fallback if CLOUD tag was not used
            if (pageCloud==null) {
                pageCloud=getDefaultCloudContext().getCloud(DEFAULT_CLOUD_NAME);
                pageContext.setAttribute("cloud",pageCloud);
            }
        }
        return pageCloud;
    }
    
    /**
    * @return the page cloud being (used by the <mm:cloud> tag)
    **/
    protected void setPageCloud(Cloud cloud){
        pageCloud=cloud;
        pageContext.setAttribute("cloud",cloud);
    }
    
    
    /**
    * method inherited from TagExtraInfo used to define
    * what parameters should be returned from a tag
    * this method is not implemented since every tag should return
    * what it want, still it is there so that inheriting classes
    * get a change to implement it
    **/
    public  VariableInfo[] getVariableInfo(TagData data){
        System.err.println("BaseTag.getVariableInfo was called. this is can normaly not happen");
        System.err.println("the cause is that in the tld file you declared the class that overrides");
        System.err.println("this class to implement TagExtraInfo but did not create the function");
        return null;
    };
    
    
    /**
    * holder for the parent tag
    **/
    protected Tag parent;
    
    /**
    * holder for the PageContext
    **/
    protected PageContext pageContext;
    
    
    /**
    * holder for the content of the body
    **/
    protected BodyContent bodyOut;
    
    /**
    * holder for the DoStartTag return 
    * what to do after the endtag is called (default SKIP_BODY)
    * so the iner tags wil get evaludated
    **/
    int startTagReturnValue = Tag.SKIP_BODY;
    
    /**
    * holder for the doEndTag return 
    * what to do after the endtag is called (default EVAL_PAGE)
    **/
    int endTagReturnValue = Tag.EVAL_PAGE;
    
    
    
    /**
    * @return a value descbibing what to do after 
    * this method returns(SKIP_BODY|EVAL_BODY_INCLUDE|SKIP_PAGE|EVAL_PAGE) or
    * EVAL_BODY_TAG if the sub class implements BodyTag
    **/
    public int doEndTag() throws JspException{
        return endTagReturnValue;
    }
    
    /**
    * @return a value descbribing what to do after 
    * this method returns(SKIP_BODY|EVAL_BODY_INCLUDE|SKIP_PAGE|EVAL_PAGE) or
    * EVAL_BODY_TAG if the sub class implements BodyTag
    **/
    public int doStartTag() throws JspException{
        return startTagReturnValue;
    }
    
    
    public void setPageContext(PageContext pageContext){
        this.pageContext = pageContext;
    }
    
    public void release() {
        parent = null;
        pageContext = null;
    }
    
    public Tag getParent(){
        return parent;
    }
    
    public void setParent(Tag parent){
        this.parent = parent;
    }
    
    public void setBodyContent(BodyContent bodyOut) {
        this.bodyOut = bodyOut;
    }
    
    // Default implementations for BodyTag methods as well
    // just in case a tag decides to implement BodyTag.
    public void doInitBody() throws JspException {
    }
    
    public int doAfterBody() throws JspException {
        return SKIP_BODY;
    }
    
    
    //where does this belong
    /**
    * This method tries to find a parent object of type NodeProvider
    * @param id the id of the parent we are looking for , this id might be null or ""
    * in that case the first node provider found will be taken
    * @return the NodeProvider if found else null
    **/
    public static NodeProvider findNodeProvider(Tag tag,String id) {
        //this has become quite a long story
        //the basic idea was:
    /*
        if (id == null) id = "";
        do {
           tag = tag.getParent();
        } while(tag != null && (tag instanceof NodeProvider && id.equals(((NodeProvider)tag).getId())));
        return (NodeProvider)tag;
    */

        Tag retval = null;
        boolean found = false;
        while (!found){
            tag = tag.getParent();
            if (tag != null){
                if (tag instanceof NodeProvider){
                    if (id == null || id.equals("")){
                                                retval = tag;
                                                found = true;

                    } else {
                        if ( ((NodeProvider)tag).getId() != null){
                            if (((NodeProvider)tag).getId().equals(id)){
                                retval = tag;
                                found = true;
                            }
                        }
                    }
                } 
            }
        }
        if (tag == null)
            System.err.println("no NodeProvider found");
        return (NodeProvider)retval;
    }

}
