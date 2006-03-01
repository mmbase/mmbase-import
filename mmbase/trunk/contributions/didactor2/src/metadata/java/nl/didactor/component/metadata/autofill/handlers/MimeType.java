package nl.didactor.component.metadata.autofill.handlers;

import org.mmbase.bridge.*;

import nl.didactor.component.metadata.autofill.Handler;

public class MimeType extends Handler{

    protected void handlerAddMetaData(Node nodeLangString, Node nodeObject){
        nodeLangString.setValue("value", nodeObject.getFunctionValue("mimetype", null).toString());
        nodeLangString.commit();
    }


    protected boolean handlerCheckMetaData(Node nodeLangString, Node nodeObject){
        String sMimeType = nodeObject.getFunctionValue("mimetype", null).toString();

        if (sMimeType.equals(nodeLangString.getStringValue("value"))) {
            return true;
        }
        else {
            return false;
        }
    }
}
