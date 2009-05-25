/*
 
  This software is OSI Certified Open Source Software.
  OSI Certified is a certification mark of the Open Source Initiative.
 
  The license (Mozilla version 1.0) can be read at the MMBase site.
  See http://www.MMBase.org/license
 
*/

package org.mmbase.applications.mmbob;

import org.w3c.dom.*;

import java.io.*;
import java.util.*;

import org.mmbase.util.*;
import org.mmbase.util.logging.*;
import org.mmbase.util.xml.*;
import org.mmbase.module.core.*;
import org.mmbase.bridge.*;
import org.mmbase.storage.*;
import org.mmbase.storage.search.*;


/**
 * forumManager
 * ToDo: Write docs!
 *
 * @author Daniel Ockeloen (MMBased)
 * @version $Id: PostAreaConfig.java,v 1.9 2007-01-16 18:01:57 michiel Exp $
 */
public class PostAreaConfig {
    private static final Logger log = Logging.getLoggerInstance(PostAreaConfig.class);
    private String defaultaccount, defaultpassword;
    private String id = "unkown";
    private int pos = 0;
    private String guestreadmodetype, guestwritemodetype, threadstartlevel;

    public PostAreaConfig(DocumentReader reader,Element n) {
	decodeConfig(reader, n);
    }

    public PostAreaConfig(String id) {
	defaultaccount = "admin";
	defaultpassword = "admin2k";
	this.id = id;
    }

    private boolean decodeConfig(DocumentReader reader,Element n) {
        NamedNodeMap nm = n.getAttributes();
        if (nm != null) {
            String account = "admin";
            String password = "admin2k";
            // decode name
            org.w3c.dom.Node n3 = nm.getNamedItem("id");
            if (n3 != null) {
                id = n3.getNodeValue();
            }
            // decode pos
            n3 = nm.getNamedItem("pos");
            if (n3 != null) {
                try {
                    pos = Integer.parseInt(n3.getNodeValue());
                } catch (Exception e) {
                }
            }
            // decode account
            n3 = nm.getNamedItem("account");
            if (n3 != null) {
                account = n3.getNodeValue();
            }
            // decode password
            n3 = nm.getNamedItem("password");
            if (n3 != null) {
                password = n3.getNodeValue();
            }
            //log.info("ID="+id+" account="+account+" password="+password);
            if (id.equals("default")) {
                defaultaccount = account;
                defaultpassword = password;
            }

            for (Element n2 : ForumsConfig.list(reader.getChildElements(n, "generatedata"))) {
                nm = n2.getAttributes();
                if (nm != null) {
                    String role = null;
                    String dfile = null;
                    String tokenizer = null;
                    n3 = nm.getNamedItem("role");
                    if (n3 != null) {
                        role = n3.getNodeValue();
                    }
                    n3 = nm.getNamedItem("file");
                    if (n3 != null) {
                        dfile = n3.getNodeValue();
                    }
                    n3 = nm.getNamedItem("tokenizer");
                    if (n3 != null) {
                        tokenizer = n3.getNodeValue();
                    }
                    org.mmbase.applications.mmbob.generate.Handler.setGenerateFile(role, dfile, tokenizer);
                }
            }


            guestreadmodetype = getAttributeValue(reader, n, "guestreadmode", "type");
            guestwritemodetype = getAttributeValue(reader, n,"guestwritemode", "type");
            threadstartlevel = getAttributeValue(reader, n, "threadstart", "level");
        }
        return true;
    }
  
    public String getId() {
	return id;
    }


    private String getAttributeValue(DocumentReader reader,Element n,String itemname,String attribute) {
        for (Element n2 : ForumsConfig.list(reader.getChildElements(n, itemname))) {
            NamedNodeMap nm = n2.getAttributes();
            if (nm != null) {
                org.w3c.dom.Node n3 = nm.getNamedItem(attribute);
                if (n3 != null) {
                    return n3.getNodeValue();
                }
            }
        }
        return null;
    }

    public String getGuestReadModeType() {
        return guestreadmodetype;
    }

    public String getGuestWriteModeType() {
        return guestwritemodetype;
    }

    public String getThreadStartLevel() {
	// this should be fixed by asking the parent
	if (threadstartlevel==null || threadstartlevel.equals("default")) return "";
        return threadstartlevel;
    }

    public void setGuestReadModeType(String guestreadmodetype) {
        this.guestreadmodetype = guestreadmodetype;
    }

    public void setGuestWriteModeType(String guestwritemodetype) {
        this.guestwritemodetype = guestwritemodetype;
    }

    public void setThreadStartLevel(String threadstartlevel) {
        this.threadstartlevel = threadstartlevel;
    }

    public int getPos() {
	return pos;
    }

    public void setPos(int pos) {
	this.pos = pos;	
   }

}
