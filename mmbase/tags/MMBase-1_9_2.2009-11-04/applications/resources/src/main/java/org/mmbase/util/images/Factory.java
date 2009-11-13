/*

This software is OSI Certified Open Source Software.
OSI Certified is a certification mark of the Open Source Initiative.

The license (Mozilla version 1.0) can be read at the MMBase site.
See http://www.MMBase.org/license

*/
package org.mmbase.util.images;

import java.util.*;
import java.util.concurrent.*;
import java.io.*;

import org.mmbase.module.core.MMObjectNode;

import org.mmbase.util.logging.Logging;
import org.mmbase.util.logging.Logger;

/**
 * Loads the ImageConverters and ImageInformers.
 *
 * @author Michiel Meeuwissen
 * @since MMBase-1.8
 * @version $Id$
 */

public class Factory {
    private static final Logger log = Logging.getLoggerInstance(Factory.class);

    private static ImageInformer imageInformer;
    protected static final Map<String, String> params = new HashMap<String, String>();

    /**
     * The ImageConvertInterface implementation to be used (defaults to ConvertImageMagic)
     */
    protected static final Class<?> DEFAULT_IMAGECONVERTER = ImageMagickImageConverter.class;
    protected static final Class<?> DEFAULT_IMAGEINFORMER = DummyImageInformer.class;

    protected static int maxConcurrentRequests = 2;

    protected static final int maxRequests = 32;
    private static final BlockingQueue<ImageConversionRequest> imageRequestQueue = new ArrayBlockingQueue<ImageConversionRequest>(maxRequests);
    private static final Map<ImageConversionReceiver, ImageConversionRequest> imageRequestTable
        = new ConcurrentHashMap<ImageConversionReceiver, ImageConversionRequest>(maxRequests);
    private static ImageConversionRequestProcessor ireqprocessors[];

    /**
     * The default image format.
     */
    protected static String defaultImageFormat = "jpeg";

    public static void init(Map<String, String> properties) {
        if (isInited()) {
            log.warn("Initing while not shut down!", new Exception());
            shutdown();
        }
        params.clear();
        params.putAll(properties);

        String tmp = properties.get("MaxConcurrentRequests");
        if (tmp != null) {
            try {
                maxConcurrentRequests = Integer.parseInt(tmp);
            } catch (NumberFormatException e) {
                //
            }
        }

        tmp = params.get("ImageConvert.DefaultImageFormat");
        if (tmp != null && ! tmp.equals("")) {
            defaultImageFormat = tmp;
        }

        ImageConverter imageConverter = loadImageConverter();
        imageInformer = loadImageInformer();
        log.debug("Got " + imageInformer);

        imageConverter.init(params);
        imageInformer.init(params);

        // Startup parrallel converters
        ireqprocessors = new ImageConversionRequestProcessor[maxConcurrentRequests];
        log.service("Starting " + maxConcurrentRequests + " Converters for " + imageConverter);
        for (int i = 0; i < maxConcurrentRequests; i++) {
            ireqprocessors[i] = new ImageConversionRequestProcessor(imageConverter, imageRequestQueue, imageRequestTable);
        }
    }


    /**
     * @since MMBase-1.9.1
     */
    public static boolean isInited() {
        return ireqprocessors != null;
    }

    /**
     * @since MMBase-1.9
     */
    public static void shutdown() {
        for (ImageConversionRequestProcessor icrp : ireqprocessors) {
            log.service("Shutting down " + icrp);
            icrp.shutdown();
        }
        ireqprocessors = null;
    }

    /**
     * @since MMBase-1.9
     */
    public static Map<String, String> getParameters() {
        return Collections.unmodifiableMap(params);
    }

    public static String getDefaultImageFormat() {
        return defaultImageFormat;
    }

    private static ImageConverter loadImageConverter() {

        String className  = DEFAULT_IMAGECONVERTER.getName();
        String tmp = params.get("ImageConvertClass");
        if (tmp != null) className = tmp;

        // backwards compatibility
        if (className.equals("org.mmbase.module.builders.ConvertImageMagick")) {
            className = "org.mmbase.util.images.ImageMagickImageConverter";
        }
        if (className.equals("org.mmbase.module.builders.ConvertJAI")) {
            className = "org.mmbase.util.images.JAIImageConverter";
        }
        if (className.equals("org.mmbase.module.builders.ConvertDummy")) {
            className = "org.mmbase.util.images.DummyImageConverter";
        }
        ImageConverter ici = null;

        try {
            Class<?> cl = Class.forName(className);
            ici = (ImageConverter) cl.newInstance();
            log.service("loaded '" + className+"' " + ici + " for image Factory");
        } catch (ClassNotFoundException e) {
            log.error("is classname in " + params.get("configfile") + " correct? ('not found class " + className + "')", e);
        } catch (InstantiationException e) {
            log.error("something went wrong ('could not instantiate class " + className + "')", e);
        } catch (java.lang.IllegalAccessException e) {
            log.error("something went wrong ('illegal access class " + className + "')", e);
        } catch (NoClassDefFoundError e) {
            log.error("are all lib's available? ('missing class used by class" + className + "')", e);
        }
        return ici;
    }

    private static ImageInformer loadImageInformer() {
        String className  = DEFAULT_IMAGEINFORMER.getName();
        String tmp = params.get("ImageInformerClass");
        if (tmp != null) className = tmp;

        ImageInformer ii  = null;

        try {
            Class<?> cl = Class.forName(className);
            ii = (ImageInformer) cl.newInstance();
            log.service("loaded '" + className+"' for image Factory");
        } catch (ClassNotFoundException e) {
            log.error("is classname in " + params.get("configfile") + " correct? ('not found class " + className + "')", e);
        } catch (InstantiationException e) {
            log.error("something went wrong ('could not instantiate class " + className + "')", e);
        } catch (java.lang.IllegalAccessException e) {
            log.error("something went wrong ('illegal access class " + className + "')", e);
        } catch (NoClassDefFoundError e) {
            log.error("are all lib's available? ('missing class used by class" + className + "')", e);
        }
        if (ii == null) ii = new DummyImageInformer();
        return ii;
    }

    public static ImageInformer getImageInformer() {
        return imageInformer;
    }

    /**
     * Triggers a image-conversion.
     * @since MMBase-1.9
     */
    public static ImageConversionRequest getImageConversionRequest(InputStream in, String format, ImageConversionReceiver receiver, List<String> pars) {
        ImageConversionRequest req;
        // convert the image, this will be done in an special thread,...
        synchronized(imageRequestTable) {
            req = imageRequestTable.get(receiver);
            if (req != null) {
                log.info("A conversion is already in progress (" + req + ")");
            } else {
                req = new ImageConversionRequest(in, format, receiver, pars);
                if (imageRequestQueue.offer(req)) {
                    imageRequestTable.put(req.getReceiver(), req);
                } else {
                    log.error("No more space in queue to execute image-conversion " + req);
                }
            }
        }
        return req;
    }
    /**
     * @since MMBase-1.9
     */
    public static ImageConversionRequest getImageConversionRequest(InputStream in, String format, ImageConversionReceiver receiver, String... pars) {
        return getImageConversionRequest(in, format, receiver, Arrays.asList(pars));
    }


    public static ImageConversionRequest getImageConversionRequest(List<String> pars, byte[] in, String format,  MMObjectNode icacheNode) {
        return getImageConversionRequest(new ByteArrayInputStream(in), format, new NodeReceiver(icacheNode), pars);
    }

    public static Imaging.CKey getCKey(int nodeNumber, String template) {
        return new Imaging.CKey(nodeNumber, template);
    }


}
