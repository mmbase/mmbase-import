/*
 
This software is OSI Certified Open Source Software.
OSI Certified is a certification mark of the Open Source Initiative.
 
The license (Mozilla version 1.0) can be read at the MMBase site.
See http://www.MMBase.org/license
 
*/
/*
 
  $Id
 
  $Log
 
*/
package org.mmbase.module.builders;

import java.util.*;
import java.io.*;
import java.sql.*;

import org.mmbase.module.builders.*;
import org.mmbase.module.database.*;
import org.mmbase.module.core.*;
import org.mmbase.module.gui.html.EditState;
import org.mmbase.util.*;


/**
 * @author cjr@dds.nl
 *
 * @version $Id
 *
 */
public class Attachments extends MMObjectBuilder {
    private String classname = getClass().getName();
    private boolean debug = true;

    protected String defaultMimeType = "application/x-binary";

    public String getGUIIndicator(String field,MMObjectNode node) {
        if (field.equals("handle")) {
            int num=node.getIntValue("number");
            int size = node.getIntValue("size");
            String mimeType = node.getStringValue("mimetype");
            if (size == -1) {
                return "";
            } else {
                String s;
                if (mimeType != null) {
                    return mimeType+", "+size+" bytes";
                } else {
                    return size+" bytes";
                }
            }
        }
        return (null);
    }

    /**
     * Eh, this function seems to be called when committing a not ("save changes")
     * I'd rather put the functionality defined here into a method that is called 
     * when processing a form, but hey, I haven't found how to do that.
     *
     * So anytime the stuff is committed, this piece of code tries to set the size
     * of the uploaded file and, eventually, the mimetype.
     * 
     */
    public int preCommit(EditState ed, MMObjectNode node) {
        String mimeType;
        byte[] handle = node.getByteValue("handle");
        if (handle != null) {
            int size = handle.length;
            mimeType = defaultMimeType; // Dunno how to dynamically determine this :-(
            node.setValue("size",size);
            node.setValue("mimetype",mimeType);
            debug("handle size set to "+size);
            debug("handle mimetype set to "+mimeType);
        }
        return(-1);
    }
}



