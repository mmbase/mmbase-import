/*

This software is OSI Certified Open Source Software.
OSI Certified is a certification mark of the Open Source Initiative.

The license (Mozilla version 1.0) can be read at the MMBase site.
See http://www.MMBase.org/license

*/
package org.mmbase.config;

import java.util.*;

import javax.xml.parsers.DocumentBuilder;

import org.mmbase.util.*;
import org.mmbase.util.logging.*;
import org.mmbase.util.xml.DocumentReader;
import org.w3c.dom.Document;

/**
 * Class XMLParseResult
 * 
 * @javadoc
 * @duplicate Same code appears in module/Config
 */

class XMLParseResult {

    private static Logger log = Logging.getLoggerInstance(XMLParseResult.class);

    List warningList, errorList, fatalList, resultList;
    boolean hasDTD;
    String dtdpath;

    public XMLParseResult(String path) {
        hasDTD = false;
        dtdpath = null;
        try {

            XMLCheckErrorHandler errorHandler = new XMLCheckErrorHandler();

            DocumentBuilder parser = DocumentReader.getDocumentBuilder(true, errorHandler, null);

            Document document = parser.parse(path);

            warningList = errorHandler.getWarningList();
            errorList = errorHandler.getErrorList();
            fatalList = errorHandler.getFatalList();

            resultList = errorHandler.getResultList();

        } catch (Exception e) {
            warningList = new Vector();
            errorList = new Vector();

            ErrorStruct err = new ErrorStruct("fatal error", 0, 0, e.getMessage());

            fatalList = new Vector();
            fatalList.add(err);
            resultList = new Vector();
            resultList.add(err);

            if (log.isDebugEnabled()) {
                log.debug("ParseResult error: " + e.getMessage());
            }
        }
    }

    public List getResultList() {
        return resultList;
    }

    public List getWarningList() {
        return warningList;
    }

    public List getErrorList() {
        return errorList;
    }

    public List getFatalList() {
        return fatalList;
    }

    public boolean hasDTD() {
        return hasDTD;
    }

    public String getDTDPath() {
        return dtdpath;
    }

}
