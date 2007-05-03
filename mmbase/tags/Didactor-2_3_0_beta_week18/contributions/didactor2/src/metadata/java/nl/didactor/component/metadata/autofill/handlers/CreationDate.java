package nl.didactor.component.metadata.autofill.handlers;

import java.util.Date;
import java.text.SimpleDateFormat;
import javax.servlet.ServletContext;


import org.mmbase.bridge.*;
import org.mmbase.util.logging.Logging;
import org.mmbase.util.logging.Logger;

import nl.didactor.component.metadata.autofill.Handler;
import nl.didactor.metadata.util.MetaHelper;
import nl.didactor.metadata.util.MetaLangStringHelper;
import java.text.*;



public class CreationDate extends Handler{

    private static Logger log = Logging.getLoggerInstance(CreationDate.class);

    SimpleDateFormat df = new SimpleDateFormat("dd-MM-yyyy");

    public CreationDate(ServletContext servletContext){
        super(servletContext);
    }

    /**
     * Sets the correct values. It overwrites old values if there any.
     * @param nodeMetaDefinition Node
     * @param nodeObject Node
     */
    protected void handlerAddMetaData(Node nodeLangString, Node nodeObject){
        log.info("CreationDate:addMetaData()");

        long lNodeObjectAge = (new Date()).getTime() - (new Integer(nodeObject.getFunctionValue("age", null).toString())).longValue() * 86400000;
        nodeLangString.setValue("value", df.format(new Date(lNodeObjectAge)));
        nodeLangString.commit();
    }



    /**
     * Checks the correct metadata value
     * @param nodeMetaDefinition Node
     * @param nodeObject Node
     * @return boolean
     */
    protected boolean handlerCheckMetaData(Node nodeLangString, Node nodeObject){
        log.info("CreationDate:checkMetaData()");
        long lNodeObjectAge = (new Date()).getTime() / 86400000 - (new Integer(nodeObject.getFunctionValue("age", null).toString())).longValue();
        String sDate = nodeLangString.getStringValue("value");
        try{
            Date date = df.parse(sDate);
            long lMetaDataAge = date.getTime() / 86400000;

            if(lMetaDataAge == lNodeObjectAge){
                return true;
            }
            else{
                return false;
            }
        }
        catch(ParseException e){
            return false;
        }
    }
}
