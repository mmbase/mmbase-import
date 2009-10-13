/*

 This software is OSI Certified Open Source Software.
 OSI Certified is a certification mark of the Open Source Initiative.

 The license (Mozilla version 1.0) can be read at the MMBase site.
 See http://www.MMBase.org/license

 */

package org.mmbase.applications.mmbob;

import java.lang.*;
import java.net.*;
import java.util.*;
import java.io.*;

import org.mmbase.util.*;
import org.mmbase.bridge.*;
import org.mmbase.module.core.*;
import org.mmbase.module.corebuilders.*;
import org.mmbase.storage.search.*;
import org.mmbase.storage.search.implementation.*;

import org.mmbase.util.logging.Logging;
import org.mmbase.util.logging.Logger;

import org.w3c.dom.*;
import org.xml.sax.*;

/**
 * @author Daniel Ockeloen
 * 
 */
public class ProfileEntryDef {

    // logger
    static private Logger log = Logging.getLoggerInstance(ProfileEntryDef.class);

    private String name;
    private String guiname;
    private int guipos;
    private int size;
    private boolean edit;
    private String external;
    private String externalname;
    private String type;
    /**
     * TODO: implement!
     */
    private boolean changeable;
    private boolean required;

    public ProfileEntryDef() {}

    public void setEdit(boolean edit) {
        this.edit = edit;
    }

    public boolean getEdit() {
        return edit;
    }

    public void setGuiPos(int guipos) {
        this.guipos = guipos;
    }

    public int getGuiPos() {
        return guipos;
    }

    public void setSize(int size) {
        this.size = size;
    }

    public int getSize() {
        return size;
    }

    public void setGuiName(String guiname) {
        this.guiname = guiname;
    }

    public String getGuiName() {
        return guiname;
    }

    /**
     * the name of the profile field
     * @param name
     */
    public void setName(String name) {
        this.name = name;
    }

    /**
     * the name of the profile field
     * @return
     */
    public String getName() {
        return name;
    }

    /**
     * a fully qualified name of a class that implements ExternalProfileInterface
     * @param external
     */
    public void setExternal(String external) {
        log.info("SET EXTERNQAL=" + external + " PD=" + this);
        this.external = external;
    }

    /**
     * 
     * @return a fully qualified name of a class that implements ExternalProfileInterface or null
     */
    public String getExternal() {
        if("".equals(external))return null;
        return external;
    }

    public String getExternalString() {
        if (external == null) return "";
        return external;
    }

    public void setExternalName(String externalname) {
        this.externalname = externalname;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    /**
     * the identifier of the field in the external profile
     * for entree it is something like [dienstid]:[type]:[fieldid] example: 187388721:GS:5
     * @return
     */
    public String getExternalName() {
        return externalname;
    }

    public boolean isChangeable() {
        return changeable;
    }

    public void setChangeable(boolean changeable) {
        this.changeable = changeable;
    }

    public boolean isRequired() {
        return required;
    }

    public void setRequired(boolean required) {
        this.required = required;
    }

    /**
     * the identifier of the field in the external profile
     * @return
     */
    public String getExternalNameString() {
        if (externalname == null) return "";
        return externalname;
    }

}
