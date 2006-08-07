/*

This software is OSI Certified Open Source Software.
OSI Certified is a certification mark of the Open Source Initiative.

The license (Mozilla version 1.0) can be read at the MMBase site.
See http://www.MMBase.org/license

*/
package com.finalist.cmsc.basicmodel.taglib;

import java.util.Iterator;

import javax.servlet.jsp.JspException;
import javax.servlet.jsp.JspTagException;

import net.sf.mmapps.commons.util.StringUtil;

import org.mmbase.bridge.*;
import org.mmbase.bridge.jsp.taglib.NodeReferrerTag;
import org.mmbase.bridge.jsp.taglib.util.Attribute;
import org.mmbase.storage.search.*;
import org.mmbase.util.functions.Function;
import org.mmbase.util.functions.Parameters;
import org.mmbase.util.images.Dimension;


/**
 * Tag that creates a list of related image elements.
 */

@SuppressWarnings("serial")
public class LinkedImagesTag  extends NodeReferrerTag {

    private static final String IMAGEREL = "imagerel";
    
    private Attribute position = Attribute.NULL;
    
    /** Holds value of property style. */
    private Attribute style = Attribute.NULL;
    
    /** Holds value of property clazz. */
    private Attribute styleClass = Attribute.NULL;
    
    /** Holds value of property align. */
    private Attribute align = Attribute.NULL;
    
    /** Holds value of property border. */
    private Attribute border = Attribute.NULL;
    
    /** Holds value of property hspace. */
    private Attribute hspace = Attribute.NULL;
    
    /** Holds value of property vspace. */
    private Attribute vspace = Attribute.NULL;

    public void setPosition(String position) throws JspTagException {
        this.position = getAttribute(position);
    }
    
    public void setStyle(String style) throws JspTagException {
        this.style = getAttribute(style);
    }
    
    public void setStyleClass(String styleClass) throws JspTagException {
        this.styleClass = getAttribute(styleClass);
    }

    public void setAlign(String align) throws JspTagException {
        this.align = getAttribute(align);
    }

    public void setBorder(String border) throws JspTagException {
        this.border = getAttribute(border);
    }
    
    public void setHspace(String hspace) throws JspTagException {
        this.hspace = getAttribute(hspace);
    }
    
    public void setVspace(String vspace) throws JspTagException {
        this.vspace = getAttribute(vspace);
    }
    
    /**
     * This tag returns links to content.
     *
     * @throws javax.servlet.jsp.JspException When something goes wrong.
     * @return SKIP_BODY
     */
    public int doStartTag() throws JspException {
       try {
           Node parentNode = getNode();
           helper.useEscaper(false);
           
           RelationManager imagerelManager = getCloudVar().getRelationManager(IMAGEREL);
           NodeQuery query = imagerelManager.createQuery();
           StepField sf = query.getStepField(imagerelManager.getField("order"));
           query.addSortOrder(sf, SortOrder.ORDER_ASCENDING);

           Field sourceField = imagerelManager.getField("snumber");
           FieldValueConstraint soourceConstraint = query.createConstraint((query.getStepField(sourceField)),
                   FieldCompareConstraint.EQUAL, parentNode.getValue("number"));
           query.setConstraint(soourceConstraint);

           String pos = position.getString(this);
           if (!StringUtil.isEmpty(pos)) {
               Field posField = imagerelManager.getField("pos");
               FieldValueConstraint posConstraint = query.createConstraint((query.getStepField(posField)),
                       FieldCompareConstraint.EQUAL, pos);
               Constraint constraint = query.getConstraint();
               if (constraint != null) {
                   Constraint compConstraint = query.createConstraint(constraint, CompositeConstraint.LOGICAL_AND, posConstraint);
                   query.setConstraint(compConstraint);
               }
               else {
                   query.setConstraint(posConstraint);
               }
           }
           NodeList list = imagerelManager.getList(query);

           String outputValue = "";
           
           for (Iterator iter = list.iterator(); iter.hasNext();) {
                Node imagerelNode = (Node) iter.next();
                if (imagerelNode.isRelation()) {
                    Relation imagerel = imagerelNode.toRelation();
                    Node image = imagerel.getDestination();
                    
                    int width = imagerel.getIntValue("width");
                    int height = imagerel.getIntValue("height");
                    String crop = imagerel.getStringValue("crop");
                    String legendType = imagerel.getStringValue("legend");
                    boolean popup = imagerel.getBooleanValue("popup");

                    ImageTag imgTag = new ImageTag();
                    imgTag.setPageContext(pageContext);
                    // Issue NIJ-149: legendType was not set
                    imgTag.setLegendtype(legendType);
                    
                    imgTag.setExternalAttributes(getOtherAttributes());
                    String templateStr = imgTag.getTemplate(image, null, width, height, crop);
                    Dimension dim = imgTag.getDimension(image, templateStr);
                    String servletArgument = imgTag.getServletArgument(image, templateStr);
                    String servletPath = getServletPath(imgTag, image, servletArgument);
                    outputValue += imgTag.getOutputValue(image, servletPath, dim, legendType, popup);
                }
            }
           
           if (outputValue != null) {
               helper.setValue(outputValue);
           }
       }
       catch (BridgeException e) {
           throw new JspException("An exception occurred when retrieving content", e);
        }
        return SKIP_BODY;
    }
    
    public String getServletPath(ImageTag imgTag, Node node, String servletArgument) throws JspTagException {
        Function servletPathFunction = imgTag.getServletFunction(node);        
        Parameters args = imgTag.getServletArguments(servletArgument, servletPathFunction);
        fillStandardParameters(args);
        return servletPathFunction.getFunctionValue( args).toString();
    }
    
    private String getOtherAttributes() throws JspTagException {
        StringBuffer attributes = new StringBuffer();
        attributes.append((styleClass != Attribute.NULL) ? (" class=\"" + styleClass.getString(this) + "\"") : "");
        attributes.append((style != Attribute.NULL) ? (" style=\"" + style.getString(this) + "\"") : "");
        attributes.append((align != Attribute.NULL) ? (" align=\"" + align.getString(this) + "\"") : "");
        attributes.append((border != Attribute.NULL) ? (" border=\"" + border.getString(this) + "\"") : " border=\"0\"");
        attributes.append((hspace != Attribute.NULL) ? (" hspace=\"" + hspace.getString(this) + "\"") : "");
        attributes.append((vspace != Attribute.NULL) ? (" vspace=\"" + vspace.getString(this) + "\"") : "");
        return attributes.toString();
    }

    public int doEndTag() throws JspTagException {
        helper.doEndTag();
        return super.doEndTag();
    }

}
