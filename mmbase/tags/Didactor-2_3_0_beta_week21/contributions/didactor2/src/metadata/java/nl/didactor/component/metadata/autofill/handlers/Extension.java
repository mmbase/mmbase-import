package nl.didactor.component.metadata.autofill.handlers;

import javax.servlet.ServletContext;

import org.mmbase.bridge.*;
import org.mmbase.util.logging.Logging;
import org.mmbase.util.logging.Logger;

import nl.didactor.component.metadata.autofill.Handler;



public class Extension extends Handler{

    private static Logger log = Logging.getLoggerInstance(Extension.class);


    public Extension(ServletContext servletContext){
        super(servletContext);
    }


    protected void handlerAddMetaData(Node nodeLangString, Node nodeObject){
        log.info("handlerAddMetaData()");
        nodeLangString.setValue("value", nodeObject.getFunctionValue("format", null).toString());
        nodeLangString.commit();
    }


    protected boolean handlerCheckMetaData(Node nodeLangString, Node nodeObject){
        String sExtension = nodeObject.getFunctionValue("format", null).toString();

        if (sExtension.equals(nodeLangString.getStringValue("value"))) {
            return true;
        }
        else {
            return false;
        }
    }
}
