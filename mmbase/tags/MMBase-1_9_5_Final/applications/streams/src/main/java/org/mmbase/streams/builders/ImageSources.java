/*

This file is part of the MMBase Streams application,
which is part of MMBase - an open source content management system.
    Copyright (C) 2009 André van Toly, Michiel Meeuwissen

MMBase Streams is free software: you can redistribute it and/or modify
it under the terms of the GNU General Public License as published by
the Free Software Foundation, either version 3 of the License, or
(at your option) any later version.

MMBase Streams is distributed in the hope that it will be useful,
but WITHOUT ANY WARRANTY; without even the implied warranty of
MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
GNU General Public License for more details.

You should have received a copy of the GNU General Public License
along with MMBase. If not, see <http://www.gnu.org/licenses/>.

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
 * Builder for imagesources. Changes the behaviour of the default mediasources object to use 
 * 'icaches' in stead of 'streamsourcescaches'.
 *
 * @author Michiel Meeuwissen
 * @version $Id$
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
     * Returns a icaches node for given image node and conversion template. 
     * If no icaches node does not exist, it is created.
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

    private static ReturnType<MMObjectNode> MMOBJECTNODE_RT = new ReturnType<MMObjectNode>(MMObjectNode.class, "mmobjectnode");
    private static Parameter<String> TEMPLATE = new Parameter<String>("template", String.class);
    private static Parameter<?>[] PARAMS = new Parameter<?>[] { TEMPLATE };
    {
        addFunction(new NodeFunction<MMObjectNode>("cachednode", PARAMS , MMOBJECTNODE_RT) {
                public MMObjectNode getFunctionValue(Node node, Parameters parameters) {
                    return ImageSources.this.getCachedNode(ImageSources.this.getNode(node.getNumber()), parameters.get(TEMPLATE));
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
