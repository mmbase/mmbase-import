/*

This software is OSI Certified Open Source Software.
OSI Certified is a certification mark of the Open Source Initiative.

The license (Mozilla version 1.0) can be read at the MMBase site.
See http://www.MMBase.org/license

*/
package org.mmbase.bridge.jsp.taglib;

import javax.servlet.jsp.JspTagException;

import java.util.Vector; 
import java.util.Enumeration;
import java.util.StringTokenizer;

import org.mmbase.bridge.Node;

import org.mmbase.util.logging.Logger;
import org.mmbase.util.logging.Logging;


/**
* A base class for tags which provide a node (And some fields). The
* general attributes for a NodeProvider are
* <ul>
* <li> id: The identifier. Used as a key for the Context. If this
* attribute is missing, the Node variable will not be imported in the Context. </li>
* <li> jspvar: An identifier for a jsp variable available in the
* body. If this attribute is missing, no jsp-variable will be
* created.</li>
* </ul>
*
* @author Michiel Meeuwissen
* @author Kees Jongenburger
**/
abstract public class AbstractNodeProviderTag extends CloudReferrerTag implements NodeProvider{
    
    private static Logger log = Logging.getLoggerInstance(AbstractNodeProviderTag.class.getName());
    
    private   Node   node;        
    protected String fields = "";
    private   String jspvar = null;
    

    // general attributes for NodeProviders
    // id from TagSupport

    public void setJspvar(String jv) {
        jspvar = jv;
        if ("".equals(jspvar)) jspvar = null;
    }

        
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
    
    
    abstract public void doInitBody() throws JspTagException;
    
    protected void fillVars() throws JspTagException {    
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
        if (jspvar != null) {
            pageContext.setAttribute(jspvar, node);
        }
        if (id != null && ! "".equals(id)) {
            findCloudProvider().registerNode(id, node);
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
        return getSimpleReturnValueName(jspvar, fieldName);
    }
    /**
     * Generates the variable-name for a field.
     *
     * @param prefix A prefix to use. Can be null.
     * @param fieldName The name of the field.
     */
    static String getSimpleReturnValueName(String prefix, String fieldName){
        String field = fieldName.replace('.','_');
        if (prefix != null && ! "".equals(prefix)) {
            field = prefix + "_" + field;
        }
        return field;
    }
}
