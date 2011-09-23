/*

This software is OSI Certified Open Source Software.
OSI Certified is a certification mark of the Open Source Initiative.

The license (Mozilla version 1.0) can be read at the MMBase site.
See http://www.MMBase.org/license

 */

package org.mmbase.applications.media.urlcomposers;

import org.mmbase.util.MimeType;
import org.mmbase.util.logging.Logger;
import org.mmbase.util.logging.Logging;
import org.mmbase.applications.media.Format;
import org.mmbase.applications.media.Codec;
import org.mmbase.applications.media.State;
import org.mmbase.util.images.Dimension;

import java.util.*;

/**
 * Serializable wrapper around URLComposers
 * @author Michiel Meeuwissen
 * @version $Id: URLComposer.java 42457 2010-06-08 11:53:39Z andre $
 */
public class URLWrapper implements java.io.Serializable  {
    private static final long serialVersionUID =  0L;

    private static final Logger log = Logging.getLoggerInstance(URLWrapper.class);

    protected final int  source;
    protected final int  provider;
    protected final Map<String, Object>           info;
    protected final Format    format;
    protected final Codec    codec;
    protected final Codec    acodec;
    protected final int      bitrate;
    protected final MimeType    mimeType;
    protected final Dimension dimension;
    protected final int fileSize;
    protected final String url;
    protected final String fileName;
    protected final boolean available;
    protected final boolean main;
    protected final State state;

    public URLWrapper(URLComposer uc) {
        source = uc.getSource().getNumber();
        provider = uc.getProvider().getNumber();
        info = null; //uc.getInfo();
        format = uc.getFormat();
        codec = uc.getCodec();
        acodec = uc.getAcodec();
        bitrate = uc.getBitrate();
        mimeType = uc.getMimeType();
        dimension = uc.getDimension();
        fileSize = uc.getFilesize();
        url = uc.getURL();
        fileName = uc.getFilename();
        available = uc.isAvailable();
        main = uc.isMain();
        state = uc.getState();
    }


    public int getSource()   {
        return source;
    }
    public int getProvider() {
        return provider;
    }
    public Map<String, Object> getInfo()     {
        return info;
    }

    public Format getFormat() {
        return format;
    }

    public Codec getCodec() {
        return codec;
    }

    public Codec getAcodec() {
        return acodec;
    }

    public int getBitrate() {
        return bitrate;
    }

    public MimeType       getMimeType() {
        return mimeType;
    }


    public Dimension getDimension() {
        return dimension;
    }

    public int getFilesize() {
        return fileSize;
    }

    public final String getURL() {
        return url;
    }

    public final String getFilename() {
        return fileName;
    }

    /**
     * Wether the URL which will be produced by this composer is actually already useable.
     * This means that the provider must be 'on', and the source must be either an original ({@link
     * State#SOURCE}), or its a generated source and its generation is done.
     */
    public boolean isAvailable() {
        return available;
    }

    public boolean isMain() {
        return main;

    }

    public State getState() {
        return state;
    }

    @Override
    public String toString() {
        if (isAvailable()) {
            return getFormat() + ": " + getURL();
        } else {
            return "{" + getFormat() + ": " + getURL() + "}";
        }
    }


}
