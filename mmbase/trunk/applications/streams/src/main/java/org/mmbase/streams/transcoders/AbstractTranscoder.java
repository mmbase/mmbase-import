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

package org.mmbase.streams.transcoders;

import org.mmbase.applications.media.Format;
import org.mmbase.applications.media.Codec;
import java.net.*;
import java.lang.reflect.*;
import java.io.*;
import java.util.*;
import org.mmbase.bridge.Node;
import org.mmbase.util.externalprocess.*;
import org.mmbase.util.WriterOutputStream;
import org.mmbase.util.MimeType;

import org.mmbase.util.logging.*;


/**
 * Base transcoder for others.
 *
 * @author Michiel Meeuwissen
 * @version $Id$
 */
public abstract class AbstractTranscoder implements Transcoder {

    public static String PACKAGE = "org.mmbase.streams.transcoders.";

    public static final Logger LOG = Logging.getLoggerInstance(AbstractTranscoder.class);

    public static Transcoder getInstance(String key) throws ClassNotFoundException, InstantiationException, IllegalAccessException, NoSuchMethodException, InvocationTargetException  {
        String[] split = key.split(" ", 2);
        Transcoder trans;
        { // parse split[0] (a class name) to instantiate object
            String[] idWithClass = split[0].split(":", 2);
            if (idWithClass.length == 1) {
                idWithClass = new String[] { "", split[0]};
            }
            Class clazz;
            try {
                clazz  = Class.forName(idWithClass[1]);
            } catch (ClassNotFoundException cnfe) {
                clazz  = Class.forName(PACKAGE + idWithClass[1]);
            }
            
            if (!"".equals(idWithClass[0])) {
            Constructor constructor = clazz.getConstructor(String.class);
            trans = (Transcoder) constructor.newInstance(idWithClass[0]);
            } else {
                trans = (Transcoder) clazz.newInstance();
            }
        }
        { // parse split[1] to set properties on it.
            String[] props = split[1].split(", ");
            for (String prop : props) {
                String[] entry = prop.split("=", 2);
                String k = entry[0];
                String value = entry[1];
                org.mmbase.util.xml.Instantiator.setProperty(k, trans.getClass(), trans, value);
            }
        }
        return trans;
    }

    public void init(Node dest) {
        Format f = this.getFormat();
        dest.setIntValue("format", f.toInt());
        Codec c = this.getCodec();
        if (c == null || c == Codec.UNKNOWN) {
            dest.setValue("codec", null);
        } else {
            dest.setIntValue("codec", c.toInt());
        }
    }

    protected AbstractTranscoder() {
    }

    protected boolean clone = false;

    protected URI in;
    protected URI out;

    protected Format format;

    protected Codec  codec = Codec.UNKNOWN;

    protected MimeType  mimeType = MimeType.ANY;

    public void setFormat(String f) {
        format = Format.valueOf(f.toUpperCase());
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

    public MimeType getMimeType() {
        return mimeType;
    }
    public void setMimeType(String m) {
        mimeType = new MimeType(m);
    }

    /**
     * Default and generic implementation of the key. It can be parsed back to the same transcoder
     * instance with {@link #getInstance}. This makes for a key which is, like the icaches 'ckey'
     * key unique and parseable.
     *
     * The implementation depends on {@link Settings} annotations to be set on the classes.
     */
    public String getKey() {
        StringBuilder buf = new StringBuilder();
        {
            String cn = getClass().getName();
            if (cn.startsWith(PACKAGE)) {
                cn = cn.substring(PACKAGE.length());
            }
            buf.append(cn);
        }
        buf.append(" ");
        boolean appendedSetting = false;
        Settings settings = getClass().getAnnotation(Settings.class);
        if (settings == null) {
            throw new RuntimeException("Class " + getClass() + " is not annotated with " + Settings.class);
        }
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
                    LOG.error("No such method " + methodName + " or field " + setting + " on " + getClass());
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

    public void transcode(final URI in, final URI out, final Logger log) throws Exception {
        if (in == null) throw new IllegalArgumentException(toString());
        if (out == null) throw new IllegalArgumentException(toString());
        this.in = in;
        this.out = out;
        if (! clone) {
            throw new IllegalStateException("Clone this transcoder before useage!");
        }
        log.info("Transcoding " + in + " -> " + out + " (logging to " + log + " )");
        transcode(log);
    }

    protected abstract void transcode(final Logger log) throws Exception;


    @Override
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
