package nl.didactor.component.metadata.autofill.handlers;

import org.mmbase.bridge.*;

import nl.didactor.component.metadata.autofill.Handler;

public class Extension extends Handler{

    protected void handlerAddMetaData(Node nodeLangString, Node nodeObject){
        nodeLangString.setValue("value", nodeObject.getFunctionValue("format", null).toString());
        nodeLangString.commit();
    }


    protected boolean handlerCheckMetaData(Node nodeLangString, Node nodeObject){
        String sExtension = nodeObject.getFunctionValue("format", null).toString();
        System.out.println("---" + sExtension);
        if (sExtension.equals(nodeLangString.getStringValue("value"))) {
            return true;
        }
        else {
            return false;
        }
    }
}
