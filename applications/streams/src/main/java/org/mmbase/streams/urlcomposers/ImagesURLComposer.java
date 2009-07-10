/*

This software is OSI Certified Open Source Software.
OSI Certified is a certification mark of the Open Source Initiative.

The license (Mozilla version 1.0) can be read at the MMBase site.
See http://www.MMBase.org/license

*/

package org.mmbase.streams.urlcomposers;


import org.mmbase.module.core.*;
import org.mmbase.servlet.MMBaseServlet;
import org.mmbase.util.logging.*;
import org.mmbase.applications.media.Format;
import org.mmbase.applications.media.State;
import org.mmbase.applications.media.urlcomposers.*;
import org.mmbase.module.builders.*;
import org.mmbase.util.images.*;
import java.util.*;


/**
 *
 * @author Michiel Meeuwissen
 */
public class ImagesURLComposer extends FragmentURLComposer {
    private static final Logger log = Logging.getLoggerInstance(ImagesURLComposer.class);

    String template = "s(100)";
    public void setTemplate(String t) {
        template = t ;
    }

    String description = "image";
    public void setDescription(String d) {
        description = d;
    }


    public String getTemplate() {
        return template;
    }
    public String getDescription() {
        return description;
    }

    @Override
    public State getState() {
        return State.DONE;
    }

    @Override
    public Dimension getDimension() {
        ImageCaches imageCaches = (ImageCaches) MMBase.getMMBase().getBuilder("icaches");
        MMObjectNode icacheNode = imageCaches.getCachedNode(source.getNumber(), template);
        return new Dimension(icacheNode.getIntValue("width"), icacheNode.getIntValue("height"));
    }


    @Override
    protected StringBuilder getURLBuffer() {
        ImageCaches imageCaches = (ImageCaches) MMBase.getMMBase().getBuilder("icaches");
        if(imageCaches == null) {
            throw new UnsupportedOperationException("The 'icaches' builder is not availabe");
        }
        MMObjectNode icacheNode = imageCaches.getCachedNode(source.getNumber(), template);
        if (icacheNode == null) {
            icacheNode = imageCaches.getNewNode("default");
            String ckey = Factory.getCKey(source.getNumber(), template).toString();
            icacheNode.setValue("ckey", ckey);
            icacheNode.setValue("id", source);
            icacheNode.insert("imagesurlcomposer");
        }

        StringBuilder buf = new StringBuilder();
        buf.append(imageCaches.getFunctionValue("servletpath", null));
        buf.append(icacheNode.getNumber());
        buf.append('/');
        buf.append(source.getStringValue("url"));
        return buf;

    }

    @Override
    public boolean equals(Object o) {
        if (super.equals(o)) {
            ImagesURLComposer other = (ImagesURLComposer) o;
            return other.template.equals(template);
        }
        return false;
    }


    @Override
    public boolean canCompose() {
        return source.getBuilder().getTableName().equals("imagesources");
    }


    @Override
    public String toString() {
        return super.toString() + " " + template;
    }

}
