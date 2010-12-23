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

package org.mmbase.streams.media.codec.video.theora;

import java.util.*;
import java.io.*;
import java.net.*;
import org.mmbase.util.logging.*;
import javax.media.*;
import javax.media.format.*;
import javax.media.protocol.*;

import net.sf.fmj.media.RegistryDefaults;
/**
 * A wrapper around the 'ffmpeg2theora' command line utility.

 * @TODO This JMF stuff is pretty hard to get going. It's not yet clear to me how exactly I should
 * register it, and how then I can use it. For the moment I gave up.
 *
 * @author Michiel Meeuwissen
 * @version $Id$
 */

public class FFMpeg2Theora extends com.sun.media.BasicCodec implements javax.media.Codec {


    protected static final Format[] OUT = new Format[] {
        new VideoFormat("theora", null, -1, Format.byteArray, -1.0f),
    };

    static {
        RegistryDefaults.registerAll(RegistryDefaults.FMJ | RegistryDefaults.FMJ_NATIVE);
        PlugInManager.addPlugIn(FFMpeg2Theora.class.getName(), new Format[0], OUT, PlugInManager.CODEC);

        final List v = PackageManager.getProtocolPrefixList();
        if (! v.contains("org.mmbase.streams")) v.add("org.mmbase.streams");

        for (Object plugin :  PlugInManager.getPlugInList(null, null, PlugInManager.CODEC)) {
            String clazz = (String) plugin;
            try {
                Codec codec = (Codec) Class.forName(clazz).newInstance();
                System.out.println("codec: " + codec + " " + codec.getName() + " " + Arrays.asList(codec.getSupportedOutputFormats(null)));
            } catch (Exception e) {
                System.err.println(e.getMessage());
            }
        }

    }
    private static final Logger log = Logging.getLoggerInstance(FFMpeg2Theora.class);

    public String getName() {
        return "ffmpeg2theora";
    }


    public Format[]  getSupportedOutputFormats(Format input) {
        return  OUT;
    }


    public int process(Buffer in, Buffer out)  {
        Object inData = in.getData();
        Object outData = out.getData();
        // call ffmpeg2theora command line?
        return BUFFER_PROCESSED_OK;
    }


    public static void main(String[] argv) throws Exception {
        final File inFile  = new File(argv[0]);
        final File outFile = new File(argv[1]);
        final URL inURL = inFile.toURI().toURL();
        final URL outURL = outFile.toURI().toURL();
        System.out.println("IN: " + inURL);
        System.out.println("OUT: " + outURL);
        final ContentDescriptor outputContentDescriptor = new FileTypeDescriptor(ContentDescriptor.mimeTypeToPackageName("video/mpeg"));
        System.out.println("CD: " + outputContentDescriptor);
        final ProcessorModel processorModel = new ProcessorModel(new MediaLocator(inURL), OUT, outputContentDescriptor);
        System.out.println("PM: " + processorModel);
        final Processor processor = Manager.createRealizedProcessor(processorModel);
        System.out.println("P: " + processor);
        final DataSource ds = processor.getDataOutput();
        final MediaLocator m = new MediaLocator(outURL);
        final DataSink destDataSink  = Manager.createDataSink(ds, m);
        destDataSink.open();
        destDataSink.start();

    }
}
