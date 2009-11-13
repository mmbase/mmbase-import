/*

This software is OSI Certified Open Source Software.
OSI Certified is a certification mark of the Open Source Initiative.

The license (Mozilla version 1.0) can be read at the MMBase site.
See http://www.MMBase.org/license

*/
package org.mmbase.datatypes.processors;

import org.mmbase.bridge.*;
import org.mmbase.util.logging.*;

/**
 * If this commit-processor is configured on a field, then on commit of the node, the value of a
 * certain function (on the same node), is set into the field, if the field is empty.
 *
 * It also implements simply copying the value of another field in such cases (using the
 * 'fieldName') parameter.
 *
 * @author Michiel Meeuwissen
 * @version $Id$
 * @since MMBase-1.8.5
 */

public class FunctionValueIfEmptyCommitProcessor implements CommitProcessor {

    private static final Logger log = Logging.getLoggerInstance(FunctionValueIfEmptyCommitProcessor.class);

    private static final long serialVersionUID = 1L;
    private String functionName;
    private String fieldName;
    public void setFunctionName(String fn) {
        functionName = fn;
    }
    public void setFieldName(String fn) {
        fieldName = fn;
    }

    public void commit(Node node, Field field) {
        if (node.getValue(field.getName()) == null || "".equals(node.getStringValue(field.getName()))) {
            if (fieldName != null) {
                node.setValueWithoutProcess(field.getName(), node.getValue(fieldName));
            } else {
                node.setValue(field.getName(), node.getFunctionValue(functionName, null).get());
            }
        }
    }

}


