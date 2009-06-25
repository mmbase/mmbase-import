/*

This software is OSI Certified Open Source Software.
OSI Certified is a certification mark of the Open Source Initiative.

The license (Mozilla version 1.0) can be read at the MMBase site.
See http://www.MMBase.org/license

*/
package org.mmbase.streams.transcoders;

import org.mmbase.applications.media.Format;
import org.mmbase.applications.media.Codec;
import java.net.*;
import java.lang.reflect.*;
import java.io.*;
import java.util.*;
import org.mmbase.util.externalprocess.*;
import org.mmbase.util.WriterOutputStream;

import org.mmbase.util.logging.*;


/**
 *
 * @author Michiel Meeuwissen
 * @version $Id$
 */
public abstract class AbstractTranscoder implements Transcoder {

    public static final Logger LOG = Logging.getLoggerInstance(AbstractTranscoder.class);

    public static Transcoder getInstance(String key) throws ClassNotFoundException, InstantiationException, IllegalAccessException  {
        String[] split = key.split(" ", 2);
        Transcoder trans = (Transcoder) Class.forName(split[0]).newInstance();
        String[] props = split[1].split(", ");
        for (String prop : props) {
            String[] entry = prop.split("=", 2);
            String k = entry[0];
            String value = entry[1];
            org.mmbase.util.xml.Instantiator.setProperty(k, trans.getClass(), trans, value);
        }
        return trans;

    }

    protected boolean clone = false;

    protected URI in;
    protected URI out;

    protected Format format;

    protected Codec  codec = Codec.UNKNOWN;

    public void setFormat(String f) {
        format = Format.valueOf(f);
    }

    public Format getFormat() {
        return format;
    }

    public void setCodec(String c) {
        codec = Codec.valueOf(c.toUpperCase());
    }

    public Codec getCodec() {
        return codec;
    }

    public  final String getKey() {
        StringBuilder buf = new StringBuilder(getClass().getName());
        buf.append(" ");
        boolean appendedSetting = false;
        Settings settings = getClass().getAnnotation(Settings.class);
        for (String setting : settings.value()) {
            Object value = null;

            String methodName = "get" + setting.substring(0, 1).toUpperCase() + setting.substring(1);
            try {
                Method m = getClass().getMethod(methodName);
                value = m.invoke(this);
            } catch (NoSuchMethodException nsme) {
                try {
                    Field f = getClass().getDeclaredField(setting);
                    value = f.get(this);
                } catch (NoSuchFieldException nsfe) {
                    LOG.error("No such method " + methodName + " or field " + setting + " on " + getClass());;
                } catch (IllegalAccessException iea) {
                    LOG.error(iea);
                }
            } catch (IllegalAccessException iea) {
                LOG.error(iea);
            } catch (InvocationTargetException ita) {
                LOG.error(ita);
            }
            if (value != null) {
                if (appendedSetting) {
                    buf.append(", ");
                }
                buf.append(setting).append("=");
                buf.append(value);
                appendedSetting = true;

            }
        }
        return buf.toString();
    }

    public final void transcode(final URI in, final URI out, final Logger log) throws Exception {
        if (in == null) throw new IllegalArgumentException();
        if (out == null) throw new IllegalArgumentException();
        this.in = in;
        this.out = out;
        if (! clone) {
            throw new IllegalStateException("Clone this transcoder before useage!");
        }
        log.info("Transcoding " + in + " -> " + out + " (logging to " + log + " )");
        transcode(log);
    }

    protected abstract void transcode(final Logger log) throws Exception;



    public String toString() {
        return getKey();
    }

    public URI getIn() {
        return in;
    }
    public URI getOut() {
        return out;
    }

    public AbstractTranscoder clone() {
        try {
            AbstractTranscoder c =  (AbstractTranscoder) super.clone();
            c.clone = true;
            return c;
        } catch (CloneNotSupportedException cnse) {
            throw new RuntimeException(cnse);
        }
    }



}
