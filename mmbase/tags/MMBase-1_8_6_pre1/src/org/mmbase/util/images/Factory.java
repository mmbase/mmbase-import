/*

This software is OSI Certified Open Source Software.
OSI Certified is a certification mark of the Open Source Initiative.

The license (Mozilla version 1.0) can be read at the MMBase site.
See http://www.MMBase.org/license

*/
package org.mmbase.util.images;

import java.util.*;
import org.mmbase.util.Queue;

import org.mmbase.module.core.MMObjectNode;

import org.mmbase.util.logging.Logging;
import org.mmbase.util.logging.Logger;

/**
 * Loads the ImageConverters and ImageInformers.
 *
 * @author Michiel Meeuwissen
 * @since MMBase-1.8
 */


public class Factory {
    private static final Logger log = Logging.getLoggerInstance(Factory.class);

    private static ImageInformer imageInformer;
    private static ImageConverter imageConverter;

    protected static Map params = new HashMap();

    /**
     * The ImageConvertInterface implementation to be used (defaults to ConvertImageMagic)
     */
    protected static final Class DEFAULT_IMAGECONVERTER = ImageMagickImageConverter.class;
    protected static final Class DEFAULT_IMAGEINFORMER  = DummyImageInformer.class;

    protected static int maxConcurrentRequests = 2;


    protected static final int maxRequests = 32;
    protected static Queue imageRequestQueue     = new Queue(maxRequests);
    protected static Map imageRequestTable       = new Hashtable(maxRequests);
    protected static ImageConversionRequestProcessor ireqprocessors[];

    /**
     * The default image format.
     */
    protected static String defaultImageFormat = "jpeg";


    public static void init(Map properties, org.mmbase.module.core.MMObjectBuilder imageCaches) {
        params.putAll(properties);

        String tmp = (String) properties.get("MaxConcurrentRequests");
        if (tmp != null) {
            try {
                maxConcurrentRequests = Integer.parseInt(tmp);
            } catch (NumberFormatException e) {
                //
            }
        }

        tmp = (String) params.get("ImageConvert.DefaultImageFormat");
        if (tmp != null && ! tmp.equals("")) {
            defaultImageFormat = tmp;
        }


        imageConverter = loadImageConverter();
        imageInformer = loadImageInformer();
        log.info("Got " + imageInformer);

        imageConverter.init(params);
        imageInformer.init(params);


        // Startup parrallel converters
        ireqprocessors = new ImageConversionRequestProcessor[maxConcurrentRequests];
        log.info("Starting " + maxConcurrentRequests + " Converters for " + imageConverter);
        for (int i = 0; i < maxConcurrentRequests; i++) {
            ireqprocessors[i] = new ImageConversionRequestProcessor(imageCaches, imageConverter, imageInformer, imageRequestQueue, imageRequestTable);
        }
    }


    public static String getDefaultImageFormat() {
        return defaultImageFormat;
    }


    private static ImageConverter loadImageConverter() {

        String className  = DEFAULT_IMAGECONVERTER.getName();
        String tmp = (String) params.get("ImageConvertClass");
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
            Class cl = Class.forName(className);
            ici = (ImageConverter) cl.newInstance();
            log.service("loaded '" + className+"' for image Factory");
        } catch (ClassNotFoundException e) {
            log.error("is classname in " + params.get("configfile") + " correct? ('not found class " + className + "')");
            log.error(Logging.stackTrace(e));
        } catch (InstantiationException e) {
            log.error("something went wrong ('could not instantiate class " + className + "')");
            log.error(Logging.stackTrace(e));
        } catch (java.lang.IllegalAccessException e) {
            log.error("something went wrong ('illegal access class " + className + "')");
            log.error(Logging.stackTrace(e));
        } catch (NoClassDefFoundError e) {
            log.error("are all lib's available? ('missing class used by class" + className + "')");
            log.error(Logging.stackTrace(e));
        }
        return ici;
    }

    private static ImageInformer loadImageInformer() {
        String className  = DEFAULT_IMAGEINFORMER.getName();
        String tmp = (String) params.get("ImageInformerClass");
        if (tmp != null) className = tmp;

        ImageInformer ii  = null;

        try {
            Class cl = Class.forName(className);
            ii = (ImageInformer) cl.newInstance();
            log.service("loaded '" + className + "' for image informer Factory");
        } catch (ClassNotFoundException e) {
            log.error("is classname in " + params.get("configfile") + " correct? ('not found class " + className + "')");
            log.error(Logging.stackTrace(e));
        } catch (InstantiationException e) {
            log.error("something went wrong ('could not instantiate class " + className + "')");
            log.error(Logging.stackTrace(e));
        } catch (java.lang.IllegalAccessException e) {
            log.error("something went wrong ('illegal access class " + className + "')");
            log.error(Logging.stackTrace(e));
        } catch (NoClassDefFoundError e) {
            log.error("are all lib's available? ('missing class used by class" + className + "')");
            log.error(Logging.stackTrace(e));
        }
        if (ii == null) ii = new DummyImageInformer();
        return ii;
    }

    public static ImageInformer getImageInformer() {
        return imageInformer;
    }

    public static ImageConverter getImageConverter() {
        return imageConverter;
    }

    /**
     * Triggers a image-conversion.
     */
    public static ImageConversionRequest getImageConversionRequest(List pars, byte[] in, String format,  MMObjectNode icacheNode) {
        ImageConversionRequest req;
        String ckey = icacheNode.getStringValue(Imaging.FIELD_CKEY);
        // convert the image, this will be done in an special thread,...
        synchronized(imageRequestTable) {
            req = (ImageConversionRequest) imageRequestTable.get(ckey);
            if (req != null) {
                log.info("A conversion is already in progress (" + ckey + ")...  (requests=" + ( req.count() + 1) + ")");
            } else {
                req = new ImageConversionRequest(pars, in, format, icacheNode);
                imageRequestTable.put(ckey, req);
                imageRequestQueue.append(req);
            }
        }
        return req;
    }

    public static Imaging.CKey getCKey(int nodeNumber, String template) {
        return new Imaging.CKey(nodeNumber, template);
    }

}
