/*
 
This software is OSI Certified Open Source Software.
OSI Certified is a certification mark of the Open Source Initiative.
 
The license (Mozilla version 1.0) can be read at the MMBase site.
See http://www.MMBase.org/license
 
*/
/*
 
  $Id: Attachments.java,v 1.2 2000-08-06 00:31:05 case Exp $
 
  $Log: not supported by cvs2svn $
 
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
 * @version $Id: Attachments.java,v 1.2 2000-08-06 00:31:05 case Exp $
 *
 */
public class Attachments extends MMObjectBuilder {
    private String classname = getClass().getName();
    private boolean debug = false;

    protected String defaultMimeType = "application/x-binary";

    public boolean process(scanpage sp, StringTokenizer command, Hashtable cmds, Hashtable vars) {
        debug("CMDS="+cmds);
        debug("VARS="+vars);
        EditState ed = (EditState)vars.get("EDITSTATE");
        System.out.println("Attachments::process() called");
        String action = command.nextToken();
        if (action.equals("SETFIELD")) {
            String fieldname = command.nextToken();
            System.out.println("fieldname = "+fieldname);
            setEditFileField(ed, fieldname, cmds, sp);
        }
        return false;
    }

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
    /*
      XXX This method should go, now that processing is taking place immediately
    public int preCommit(EditState ed, MMObjectNode node) {
        String mimeType;
        byte[] handle = node.getByteValue("handle");
        if (handle != null) {
            int size = handle.length;
            mimeType = defaultMimeType; // Dunno how to dynamically determine this :-(
            node.setValue("size",size);
            node.setValue("mimetype",mimeType);
            if (debug) debug("handle size set to "+size);
            if (debug) debug("handle mimetype set to "+mimeType);
        }
        return(-1);
}
    */

    /**
     * cjr: copied from FieldEditor, commented out the FieldDefs def = .. line
     */
    /* XXX doesn't work XXX
    protected boolean setEditDISKField(EditState ed, String fieldname,Hashtable cmds,scanpage sp) {
    MMObjectBuilder obj=ed.getBuilder();
    //FieldDefs def=obj.getField(fieldname); // Doesn't seem to be called ?!
    try {
     MMObjectNode node=ed.getEditNode();
     if (node!=null) {
    //String filename=(String)cmds.get("EDIT-BUILDER-SETFIELDFILE_DISK-"+fieldname);
    String filename = sp.poster.getPostParameterFile(fieldname);
    System.out.println("filename = "+filename);
    //byte[] bytes=getFile(filename);
    byte[] bytes = getFile("/tmp/net7681");
    if (bytes==null) {
      System.out.println("FieldEditor-> Empty file !!");
} else {
      node.setValue(fieldname,bytes);
      node.setValue("mimetype","foo/bar");
      node.setValue("size",5000);
}
     }
} catch (Exception e) {
     e.printStackTrace();
}
    return(true);
}
    */

    protected boolean setEditFileField(EditState ed, String fieldname,Hashtable cmds,scanpage sp) {
        MMObjectBuilder obj=ed.getBuilder();
        //FieldDefs def=obj.getField(fieldname);
        try {
            MMObjectNode node=ed.getEditNode();
            if (node!=null) {
                byte[] bytes=sp.poster.getPostParameterBytes("file");
                node.setValue(fieldname,bytes);

                if (bytes != null && bytes.length > 0) {
                    MagicFile magic = new MagicFile();
                    String mimetype = magic.test(bytes);
                    node.setValue("mimetype",mimetype);
                    node.setValue("size",bytes.length);
                } else {
                    if (debug) {
                        debug("Damn. Got zero bytes");
                    }
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return(true);
    }

    /**
     * cjr: copied from FieldEditor
     * getFile: This method creates a byte array using the specified filepath argument.
     */
    public byte[] getFile(String filepath) {
        try {
            File file = new File(filepath);
            FileInputStream fis = new FileInputStream(filepath);
            byte[] ba = new byte[(int)file.length()];	//Create a bytearray with a length the size of the filelength.
            fis.read(ba);	//Read up to ba.length bytes of data from this inputstream into the bytearray ba.
            fis.close();
            return(ba);
        } catch (IOException ioe) {
            return(null);
        }
    }
}




