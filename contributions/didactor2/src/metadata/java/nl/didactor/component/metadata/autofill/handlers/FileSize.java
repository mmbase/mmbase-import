package nl.didactor.component.metadata.autofill.handlers;

import java.util.Date;
import java.util.Iterator;

import org.mmbase.bridge.*;

import nl.didactor.component.metadata.autofill.HandlerInterface;
import nl.didactor.metadata.util.MetaHelper;
import nl.didactor.metadata.util.MetaLangStringHelper;


public class FileSize implements HandlerInterface{

    /**
     * Sets the correct values. It overwrites old values if there any.
     * @param nodeMetaDefinition Node
     * @param nodeObject Node
     */
    public void addMetaData(Node nodeMetaDefinition, Node nodeObject){
        NodeList nlMetaDataNodes = nodeObject.getCloud().getList("" + nodeMetaDefinition.getNumber(),
           "metadefinition,metadata,object",
           "metadata.number",
           "object.number='" + nodeObject.getNumber() + "'",
           null,null,null,false);

        Node nodeMetaData = null;
        try {
            nodeMetaData = nodeMetaDefinition.getCloud().getNode(nlMetaDataNodes.getNode(0).getStringValue("metadata.number"));
        }
        catch (Exception e){
            //There is no metadata node yet, let's create it
            MetaHelper metaHelper = new MetaHelper();
            nodeMetaData = metaHelper.createMetaDataNode(nodeObject.getCloud(), nodeObject, nodeMetaDefinition);
        }

        int iFileSize = nodeObject.getByteValue("handle").length;
        Node nodeLangString = MetaLangStringHelper.doOneLangString(nodeMetaData);
        nodeLangString.setStringValue("value", "" + iFileSize);
        nodeLangString.commit();
    }




    /**
     * Checks the correct metadata value
     * @param nodeMetaDefinition Node
     * @param nodeObject Node
     * @return boolean
     */
    public boolean checkMetaData(Node nodeMetaDefinition, Node nodeObject){
        NodeList nlMetaDataNodes = nodeObject.getCloud().getList("" + nodeMetaDefinition.getNumber(),
           "metadefinition,metadata,metalangstring,metadata,object",
           "metalangstring.number",
           "object.number='" + nodeObject.getNumber() + "'",
           null,null,null,false);


       try{
           int iFileSize = nodeObject.getByteValue("handle").length;

           Node nodeLangString = nodeMetaDefinition.getCloud().getNode(nlMetaDataNodes.getNode(0).getStringValue("metalangstring.number"));
           if(("" + iFileSize).equals(nodeLangString.getValue("value"))){
               return true;
           }
           else {
               return false;
           }
       }
       catch (Exception e){
           return false;
       }
    }
}
