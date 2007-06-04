package nl.didactor.component.metadata.autofill;

import org.mmbase.bridge.*;


import javax.servlet.ServletContext;

import nl.didactor.component.metadata.autofill.HandlerInterface;
import nl.didactor.metadata.util.MetaHelper;
import nl.didactor.metadata.util.MetaLangStringHelper;


public abstract class Handler implements HandlerInterface{

    private ServletContext servletContext;


    public Handler(ServletContext servletContext){
        this.servletContext = servletContext;
    }

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

        Node nodeResultLangString = MetaLangStringHelper.doOneLangString(nodeMetaData);

        this.handlerAddMetaData(nodeResultLangString, nodeObject);
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
           Node nodeLangString = nodeMetaDefinition.getCloud().getNode(nlMetaDataNodes.getNode(0).getStringValue("metalangstring.number"));
           return handlerCheckMetaData(nodeLangString, nodeObject);
       }
       catch (Exception e){
           return false;
       }
    }

    /**
     * Should be implmented by handler class
     * @param nodeLangString Node
     * @param nodeObject Node
     */
    protected abstract void handlerAddMetaData(Node nodeLangString, Node nodeObject);

    /**
     * Should be implmented by handler class
     * @param nodeLangString Node
     * @param nodeObject Node
     * @return boolean
     */
    protected abstract boolean handlerCheckMetaData(Node nodeLangString, Node nodeObject);
}
