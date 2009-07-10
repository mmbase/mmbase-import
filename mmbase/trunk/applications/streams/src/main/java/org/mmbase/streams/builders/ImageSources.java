 /*

 This software is OSI Certified Open Source Software.
 OSI Certified is a certification mark of the Open Source Initiative.

 The license (Mozilla version 1.0) can be read at the MMBase site.
 See http://www.MMBase.org/license
  */

package org.mmbase.streams.builders;
import  org.mmbase.applications.media.builders.*;
import  org.mmbase.module.builders.*;
import  org.mmbase.module.core.*;
import  org.mmbase.util.images.*;
import org.mmbase.servlet.FileServlet;

import java.util.*;
import java.io.*;
import org.mmbase.util.logging.*;


/**
 *
 * @author Michiel Meeuwissen
 * @version $Id: MediaSources.java 36531 2009-07-03 07:03:20Z michiel $
 * @since MMBase-1.9.2
 */
public class ImageSources extends MediaSources implements ImagesInterface {
    private static final Logger log = Logging.getLoggerInstance(ImageSources.class);


    public ImageSources() {
    }

    public Dimension getDimension(MMObjectNode node) {
        return new Dimension(node.getIntValue("width"), node.getIntValue("height"));
    }


    public String getImageFormat(MMObjectNode node) {
        return "png";
    }

    public java.io.InputStream getBinary(MMObjectNode node) {
        try {
            File file = new File(FileServlet.getDirectory(), "" + node.getValue("url"));
            return new FileInputStream(file);
        } catch (FileNotFoundException fne) {
            return null;
        }

    }


}
