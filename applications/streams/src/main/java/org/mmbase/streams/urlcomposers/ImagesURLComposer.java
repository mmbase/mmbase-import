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


package org.mmbase.streams.urlcomposers;

import org.mmbase.applications.media.Format;
import org.mmbase.util.MimeType;
import org.mmbase.applications.media.State;
import org.mmbase.applications.media.urlcomposers.FragmentURLComposer;
import org.mmbase.module.builders.ImageCaches;
import org.mmbase.module.builders.Images;
import org.mmbase.module.core.MMBase;
import org.mmbase.module.core.MMObjectNode;
import org.mmbase.streams.builders.ImageSources;
import org.mmbase.util.images.Dimension;
import org.mmbase.util.images.Factory;
import org.mmbase.util.logging.Logger;
import org.mmbase.util.logging.Logging;


/**
 * The FragmentURLComposer to make images urls available the same way as audio and video.
 *
 * @author Michiel Meeuwissen
 * @version $Id$
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
    public boolean isMain() {
        return false;
    }

    ImageSources getBuilder() {
        ImageSources builder = (ImageSources) MMBase.getMMBase().getBuilder("imagesources");
        return builder;
    }

    private String getImagetype() {
        ImageCaches imageCaches = (ImageCaches) MMBase.getMMBase().getBuilder("icaches");
        if (imageCaches == null) {
            throw new UnsupportedOperationException("The 'icaches' builder is not availabe");
        }
        MMObjectNode icacheNode = imageCaches.getCachedNode(source.getNumber(), template);
        if (icacheNode == null) {
            Images images = (Images) MMBase.getMMBase().getBuilder("images");
            icacheNode = images.getCachedNode(source, template);
        }
        return imageCaches.getImageFormat(icacheNode);
    }

    @Override
    public MimeType getMimeType() {
        return new MimeType("image", getImagetype());
    }

    @Override
    public Format getFormat() {
        return Format.get(getImagetype());
    }

    @Override
    public Dimension getDimension() {
        MMObjectNode icacheNode = getBuilder().getCachedNode(source, template);
        return new Dimension(icacheNode.getIntValue("width"), icacheNode.getIntValue("height"));
    }

    @Override
    public int getFilesize() {
        MMObjectNode icacheNode = getBuilder().getCachedNode(source, template);
        return icacheNode.getIntValue("filesize");
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
    @SuppressWarnings("EqualsWhichDoesntCheckParameterClass")
    public boolean equals(Object o) {
        if (super.equals(o)) {
            ImagesURLComposer other = (ImagesURLComposer) o;
            return other.template.equals(template);
        }
        return false;
    }

    @Override
    public int hashCode() {
        int hash = super.hashCode();
        hash = 97 * hash + (this.template != null ? this.template.hashCode() : 0);
        return hash;
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
