/*
 * Util.java
 *
 * Created on July 16, 2001, 10:47 AM
 */

package nanoxml;

import java.util.Iterator;
import java.util.Vector;
/**
 *
 * @author  keesj
 * @version 
 */
public abstract class Util {
    
    public static XMLElement findChildWithAttribute(XMLElement xmle , String attributeName , String attributeValue){
        if (attributeValue.equals(xmle.getProperty(attributeName))){
            return xmle;
        } else {
            Iterator childs = xmle.getChildren().iterator();
            while(childs.hasNext()){
                XMLElement childElement = (XMLElement)childs.next();
                XMLElement retVal = findChildWithAttribute(childElement,attributeName,attributeValue);
                if (retVal != null){
                    return retVal;
                }
            }
        }
        return null;
    }

    /**
     * This method takes an extra parameter called path which will be filled with
     * all the XMLElement's that are used to travel from the parent XMLElement
     * to the XMLElement that is searched for. The first element in the path
     * will be the XMLElement that is searched for. The last element in the
     * path will be parameter xmle.
     *
     */
    public static XMLElement findChildWithAttribute(XMLElement xmle,
            String attributeName, String attributeValue, Vector path){
        if (attributeValue.equals(xmle.getProperty(attributeName))){
            path.add(xmle);
            return xmle;
        } else {
            Iterator childs = xmle.getChildren().iterator();
            while(childs.hasNext()){
                XMLElement childElement = (XMLElement)childs.next();
                XMLElement retVal = findChildWithAttribute(childElement,attributeName,attributeValue,path);
                if (retVal != null){
                    path.add(xmle);
                    return retVal;
                }
            }
        }
        return null;
    }

}
