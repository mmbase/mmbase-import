 /*

 This software is OSI Certified Open Source Software.
 OSI Certified is a certification mark of the Open Source Initiative.

 The license (Mozilla version 1.0) can be read at the MMBase site.
 See http://www.MMBase.org/license
  */

package org.mmbase.streams.builders;
import org.mmbase.applications.media.builders.*;
import org.mmbase.module.builders.*;
import org.mmbase.module.core.*;
import org.mmbase.util.images.*;
import org.mmbase.servlet.FileServlet;
import org.mmbase.util.functions.*;
import org.mmbase.bridge.*;
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

    public StringBuilder getFileName(MMObjectNode node, StringBuilder buf) {
        buf.append(node.getValue("url"));
        buf.append(".png");
        return buf;
    }

    public java.io.InputStream getBinary(MMObjectNode node) {
        try {
            File file = new File(FileServlet.getDirectory(), "" + node.getValue("url"));
            return new FileInputStream(file);
        } catch (FileNotFoundException fne) {
            return null;
        }
    }

    /**
     * Returns a icache node for given image node and conversion template. If such a node does not exist, it is created.
     *
     */
    public MMObjectNode getCachedNode(MMObjectNode node, String template) {
        ImageCaches imageCaches = (ImageCaches) mmb.getMMObject("icaches");
        if(imageCaches == null) {
            throw new UnsupportedOperationException("The 'icaches' builder is not availabe");
        }

        MMObjectNode icacheNode = imageCaches.getCachedNode(node.getNumber(), template);

        if (icacheNode == null) {
            icacheNode = imageCaches.getNewNode("imagesmodule");
            String ckey = Factory.getCKey(node.getNumber(), template).toString();
            icacheNode.setValue(Imaging.FIELD_CKEY, ckey);
            icacheNode.setValue(ImageCaches.FIELD_ID, node.getNumber());
            Dimension dimension          = getDimension(node);
            Dimension predictedDimension = Imaging.predictDimension(dimension, Imaging.parseTemplate(template));
            if (log.isDebugEnabled()) {
                log.debug("" + dimension + " " + ckey + " --> " + predictedDimension);
            }

            icacheNode.setValue(ImageCaches.FIELD_HEIGHT, predictedDimension.getHeight());
            icacheNode.setValue(ImageCaches.FIELD_WIDTH,  predictedDimension.getWidth());
            int icacheNumber = icacheNode.insert("imagesmodule");
            if (log.isDebugEnabled()) {
                log.debug("Inserted " + icacheNode);
            }
            if (icacheNumber < 0) {
                throw new RuntimeException("Can't insert cache entry id=" + node.getNumber() + " key=" + template);
            }
        }
        return icacheNode;
    }

    protected Dimension getDimension(MMObjectNode node, String template) {
        if (template == null) {
            return getDimension(node);
        } else {
            MMObjectNode icache = getCachedNode(node, template);
            return new Dimension(icache.getIntValue("width"), icache.getIntValue("height"));
        }
    }


    private static Parameter<String> TEMPLATE = new Parameter<String>("template", String.class);
    private static Parameter<?>[] PARAMS = new Parameter<?>[] { TEMPLATE };
    {
        addFunction(new NodeFunction<Integer>("cachednode", PARAMS , ReturnType.INTEGER) {
                public Integer getFunctionValue(Node node, Parameters parameters) {
                    return ImageSources.this.getCachedNode(ImageSources.this.getNode(node.getNumber()), parameters.get(TEMPLATE)).getNumber();
                }
            });
    }

    private static ReturnType<Dimension> DIM_RT = new ReturnType<Dimension>(Dimension.class, "dimension");

    {
        addFunction(new NodeFunction<Dimension>("dimension", PARAMS , DIM_RT) {
                public Dimension getFunctionValue(Node node, Parameters parameters) {
                    return ImageSources.this.getDimension(ImageSources.this.getNode(node.getNumber()), parameters.get(TEMPLATE));
                }
            });
    }


}
