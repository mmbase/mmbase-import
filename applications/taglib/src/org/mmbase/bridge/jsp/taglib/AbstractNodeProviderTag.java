/*

This software is OSI Certified Open Source Software.
OSI Certified is a certification mark of the Open Source Initiative.

The license (Mozilla version 1.0) can be read at the MMBase site.
See http://www.MMBase.org/license

*/
package org.mmbase.bridge.jsp.taglib;

import javax.servlet.jsp.JspException;

import java.util.Vector; 
import java.util.Enumeration;
import java.util.StringTokenizer;

import org.mmbase.bridge.Node;

import org.mmbase.util.logging.Logger;
import org.mmbase.util.logging.Logging;


/**
* A base class for tags which provide a node (And some fields).
*
* @author Michiel Meeuwissen
* @author Kees Jongenburger
**/
abstract public class AbstractNodeProviderTag extends CloudReferrerTag implements NodeProvider{
    
    private static Logger log = Logging.getLoggerInstance(AbstractNodeProviderTag.class.getName());
    
    private   Node   node;        
    protected String fields = "";
        
    /**
    * For use by children, they can find the currend 'node' belonging
    * to this tag.
    */
    
    public Node getNodeVar() {
        return node;
    }
    
    protected void setNodeVar(Node node) {
        this.node = node;
    }
    
    /**
    * @param fields a comma separated list of fields of a node
    **/
    public void setFields(String fields){
        this.fields = fields;
    }
    
    
    abstract public void doInitBody() throws JspException;
    
    protected void fillVars(){    
        Enumeration returnFieldEnum = stringSplitter(fields,",").elements();
        int j=1;
        while (returnFieldEnum.hasMoreElements()){
            String field = (String)returnFieldEnum.nextElement();
            // michiel: should be deprecated?
            if (log.isDebugEnabled()) {
                log.trace("will set " + getSimpleReturnValueName(field) + " to " + node.getValue(field));
            }
            pageContext.setAttribute(getSimpleReturnValueName(field) , "" + node.getValue(field));
            //pageContext.setAttribute(getPrefix() + "item"+(j++) ,
            //                         "" + node.getValue(field));
        }
        String id = getId();
        if (id != null && id != "") {
            pageContext.setAttribute(id, node);
        }
    }
    
    
    /**
    * simple util method to split comma separated values
    * to a vector
    * @param string the string to split
    * @param delimiter
    * @return a Vector containing the elements, the elements are also trimed
    **/
    static Vector stringSplitter(String string,String delimiter){
        Vector retval = new Vector();
        StringTokenizer st = new StringTokenizer(string, delimiter);
        while(st.hasMoreTokens()){
            retval.addElement(st.nextToken().trim());
        }
        return retval;
    }
    
    static Vector stringSplitter(String string) {
        return stringSplitter(string, ",");
    }
    
    private String getSimpleReturnValueName(String fieldName){
        return getSimpleReturnValueName(getId(), fieldName);
    }
    /**
     * Generates the variable-name for a field.
     *
     * @param id The id of the node.
     * @param fieldName The name of the field.
     */
    static String getSimpleReturnValueName(String id, String fieldName){
        String field = fieldName.replace('.','_');
        if (id != null && ! "".equals(id)) {
            field = id + "_" + field;
        }
        return field;
    }
}
