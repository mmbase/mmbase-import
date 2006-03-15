package nl.didactor.component.metadata.autofill.handlers;

import java.util.Date;
import java.util.Iterator;
import java.text.SimpleDateFormat;

import org.mmbase.bridge.*;

import nl.didactor.component.metadata.autofill.HandlerInterface;
import nl.didactor.metadata.util.MetaHelper;
import nl.didactor.metadata.util.MetaLangStringHelper;

public class CreationDate implements HandlerInterface{

    SimpleDateFormat df = new SimpleDateFormat("dd-MM-yyyy");


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
            nodeMetaData = MetaHelper.createMetaDataNode(nodeObject.getCloud(), nodeObject, nodeMetaDefinition);
        }

        long lNodeObjectAge = (new Date()).getTime() - (new Integer(nodeObject.getFunctionValue("age", null).toString())).longValue() * 86400000;


        Node nodeResultLangString = MetaLangStringHelper.doOneLangString(nodeMetaData);

        nodeResultLangString.setValue("value", df.format(new Date(lNodeObjectAge)));
        nodeResultLangString.commit();
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
           long lNodeObjectAge = (new Date()).getTime() / 86400000 - (new Integer(nodeObject.getFunctionValue("age", null).toString())).longValue();

           Node nodeLangString = nodeMetaDefinition.getCloud().getNode(nlMetaDataNodes.getNode(0).getStringValue("metalangstring.number"));
           String sDate = nodeLangString.getStringValue("value");

           Date date = df.parse(sDate);
           long lMetaDataAge = date.getTime() / 86400000;

           if (lMetaDataAge == lNodeObjectAge) {
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
