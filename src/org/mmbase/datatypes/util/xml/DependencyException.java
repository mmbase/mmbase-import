/*

This software is OSI Certified Open Source Software.
OSI Certified is a certification mark of the Open Source Initiative.

The license (Mozilla version 1.0) can be read at the MMBase site.
See http://www.MMBase.org/license

*/
package org.mmbase.datatypes.util.xml;

import org.mmbase.datatypes.BasicDataType;
import org.mmbase.datatypes.DataTypeCollector;
import org.w3c.dom.Element;
import org.mmbase.util.logging.*;
/**
 *
 * @author Michiel Meeuwissen
 * @version $Id$
 * @since MMBase-1.8.6
 **/
public class DependencyException extends Exception {

    private static final Logger log = Logging.getLoggerInstance(DependencyException.class);

    private final Element element;
    private final DataTypeDefinition def;
    private final BasicDataType requestedBaseDataType;
    private DataTypeCollector collector = null;

    public DependencyException(Element el, BasicDataType rdt, DataTypeDefinition def) {
        super();
        element = el;
        this.def = def;
        requestedBaseDataType = rdt;
    }

    public void setCollector(DataTypeCollector col) {
        if (collector != null) throw new IllegalStateException();
        collector = col;
    }
    public String getMessage() {
        return "Attribute 'base' ('" + element.getAttribute("base") + "') of datatype '" + element.getAttribute("id") + "' is an unknown datatype (in " + element.getOwnerDocument() + ").";


    }

    public String getId() {
        return element.getOwnerDocument() + "@" +  element.getAttribute("id") + " (@base=" + element.getAttribute("base") + ")";
    }

    public boolean retry() {
        try {
            log.info("Retrying " + getId());
            def.configure(element, requestedBaseDataType);
            if (collector != null) {
                collector.addDataType(def.dataType);
            }
            return true;
        } catch (DependencyException de) {
            return false;
        }
    }

    public BasicDataType fallback() {
        log.warn(getMessage());
        element.setAttribute("base", "");
        try {
            return  DataTypeReader.readDataType(element, requestedBaseDataType, collector).dataType;
        } catch (DependencyException de2) {
            log.fatal(de2.getMessage());
            return null;
        }

    }



}
