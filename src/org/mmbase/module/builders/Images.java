/*

This software is OSI Certified Open Source Software.
OSI Certified is a certification mark of the Open Source Initiative.

The license (Mozilla version 1.0) can be read at the MMBase site.
See http://www.MMBase.org/license

*/
package org.mmbase.module.builders;

import java.util.*;

import org.mmbase.core.event.NodeEvent;
import org.mmbase.module.core.*;
import org.mmbase.util.*;
import org.mmbase.util.images.*;
import org.mmbase.util.functions.*;
import org.mmbase.util.logging.*;
import org.mmbase.util.xml.UtilReader;

import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.ServletContext;

/**
 * If this class is used as the class for your builder, then an 'handle' byte field is assumed to
 * contain an image. You builder will work together with 'icaches', and with imagemagick (or jai).
 *
 * This means that is has the following properties: ImageConvertClass,
 * ImageConvert.ConverterCommand, ImageConvert.ConverterRoot and MaxConcurrentRequests
 *
 * @author Daniel Ockeloen
 * @author Rico Jansen
 * @author Michiel Meeuwissen
 * @version $Id$
 */
public class Images extends AbstractImages {

    private static final Logger log = Logging.getLoggerInstance(Images.class);

    // This cache connects templates (or ckeys, if that occurs), with node numbers,
    // to avoid querying icaches.
    private CKeyCache templateCacheNumberCache = new CKeyCache(500) {
        public String getName()        { return "CkeyNumberCache"; }
        public String getDescription() { return "Connection between image conversion templates and icache node numbers"; }
        };

    public final static Parameter[] CACHE_PARAMETERS = {
        new Parameter("template",  String.class)
    };


    public final static Parameter[] CACHEDNODE_PARAMETERS = CACHE_PARAMETERS;
    public final static Parameter[] HEIGHT_PARAMETERS = CACHE_PARAMETERS;
    public final static Parameter[] WIDTH_PARAMETERS = CACHE_PARAMETERS;
    public final static Parameter[] DIMENSION_PARAMETERS = CACHE_PARAMETERS;


    public final static Parameter[] GUI_PARAMETERS = {
        new Parameter.Wrapper(MMObjectBuilder.GUI_PARAMETERS),
        new Parameter("template", String.class)
    };


    public Images() {
        templateCacheNumberCache.putCache();
    }

    /**
     * Supposed image type if not could be determined (configurable)
     */
    protected String defaultImageType = "jpg";

    private int maxArea = Integer.MAX_VALUE;

    /**
     * Read configurations (imageConvertClass, maxConcurrentRequest),
     * checks for 'icaches', inits the request-processor-pool.
     */
    public boolean init() {
        if (oType != -1) return true; // inited already

        if (!super.init()) return false;

        String tmp = getInitParameter("DefaultImageType");

        if (tmp != null) {
            defaultImageType = tmp;
        }

        String ma = getInitParameter("MaxArea");
        if (ma != null && !"".equals(ma)) {
            maxArea = Integer.parseInt(ma);
        }

        ImageCaches imageCaches = (ImageCaches) mmb.getMMObject("icaches");
        if(imageCaches == null) {
            log.warn("Builder with name 'icaches' wasn't loaded. Cannot do image-conversions.");
        }

        Map map = getInitParameters("mmbase/imaging");
        map.put("configfile", getConfigResource());

        Factory.init(map, imageCaches);

        return true;
    }

    /**
     * The executeFunction of this builder adds the 'cache' function.
     * The cache function accepts a conversion template as argument and returns the cached image
     * node number. Using this you order to pre-cache an image.
     *
     * @since MMBase-1.6
     */
    protected Object executeFunction(MMObjectNode node, String function, List args) {
        if (log.isDebugEnabled()) {
            log.debug("executeFunction " + function + "(" + args + ") of images builder on node " + node.getNumber());
        }
        if ("info".equals(function)) {
            List empty = new ArrayList();
            Map info = (Map) super.executeFunction(node, function, empty);
            info.put("cache", "" + CACHE_PARAMETERS + " The node number of the cached converted image (icaches node)");
            if (args == null || args.size() == 0) {
                return info;
            } else {
                return info.get(args.get(0));
            }
        } else if ("cache".equals(function)) {
            if (args == null || args.size() < 1) {
                throw new RuntimeException("Images cache functions needs 1 argument (now: " + args + ")");
            }
            return new Integer(getCachedNode(node, (String) args.get(0)).getNumber());
        } else if ("cachednode".equals(function)) {
            try {
                return getCachedNode(node, (String) args.get(0));
            } catch (Exception e) {
                log.error(e.getMessage());
                log.error(Logging.stackTrace(e));
                return null;
            }
        } else if ("height".equals(function)) {
            if (args.size() == 0) {
                return new Integer(getDimension(node).getHeight());
            } else {
                return new Integer(getDimension(node, (String) args.get(0)).getHeight());
            }
        } else if ("width".equals(function)) {
            if (args.size() == 0) {
                return new Integer(getDimension(node).getWidth());
            } else {
                return new Integer(getDimension(node, (String) args.get(0)).getWidth());
            }
        } else if ("dimension".equals(function)) {
            if (args.size() == 0) {
                return getDimension(node);
            } else {
                return getDimension(node, (String) args.get(0));
            }
        } else {
            return super.executeFunction(node, function, args);
        }
    }
    /**
     * @since MMBase-1.7.4
     */
    protected Dimension getDimension(MMObjectNode node, String template) {
        if (template == null || template.equals("")) { // no template given, return dimension of node itself.
            return getDimension(node);
        }
        ImageCaches imageCaches = (ImageCaches) mmb.getMMObject("icaches");
        if(imageCaches == null) {
            throw new UnsupportedOperationException("The 'icaches' builder is not availabe");
        }
        MMObjectNode icacheNode = imageCaches.getCachedNode(node.getNumber(), template);
        if (icacheNode != null) {
            return imageCaches.getDimension(icacheNode);
        } else {
            // no icache available? Only return prediction.
            return  Imaging.predictDimension(getDimension(node), Imaging.parseTemplate(template));
        }
    }


    /**
     * Returns a icache node for given image node and conversion template. If such a node does not exist, it is created.
     *
     * @since MMBase-1.8
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
            if (imageCaches.storesDimension() || imageCaches.storesFileSize()) {
                Dimension dimension          = getDimension(node);
                Dimension predictedDimension = Imaging.predictDimension(dimension, Imaging.parseTemplate(template));
                if (predictedDimension.getArea() > maxArea) {
                    throw new IllegalArgumentException("The conversion '" + template + "' leads to an image which is too big (" + predictedDimension + ")");
                }
                if (log.isDebugEnabled()) {
                    log.debug("" + dimension + " " + ckey + " --> " + predictedDimension);
                }
                if (imageCaches.storesDimension()) {
                    icacheNode.setValue(FIELD_HEIGHT, predictedDimension.getHeight());
                    icacheNode.setValue(FIELD_WIDTH,  predictedDimension.getWidth());
                }
                if (imageCaches.storesFileSize()) {
                    icacheNode.setValue(FIELD_FILESIZE, Imaging.predictFileSize(dimension, getFileSize(node), predictedDimension));
                }
            }
            int icacheNumber = icacheNode.insert("imagesmodule");
            log.debug("Inserted " + icacheNode);
            if (icacheNumber < 0) {
                throw new RuntimeException("Can't insert cache entry id=" + node.getNumber() + " key=" + template);
            }
        }
        return icacheNode;
    }


    private static final org.mmbase.util.transformers.UrlEscaper URLESCAPER= new org.mmbase.util.transformers.UrlEscaper();
    /**
     * The GUI-indicator of an image-node also needs a res/req object.
     * @since MMBase-1.6
     */
    protected String getGUIIndicatorWithAlt(MMObjectNode node, String alt, Parameters args) {
        int num = node.getNumber();
        if (num < 0) {   // image servlet cannot handle uncommited images..
            return "...";
        }

        String ses = getSession(args, num);
        StringBuffer servlet = new StringBuffer();
        HttpServletRequest req = (HttpServletRequest) args.get(Parameter.REQUEST);
        boolean urlConvert = false;
        if (req != null) {
            ServletContext sx = MMBaseContext.getServletContext();
            if (sx != null && "true".equals(sx.getInitParameter("mmbase.taglib.url.makerelative"))) {
                servlet.append(getServletPath(UriParser.makeRelative(new java.io.File(req.getServletPath()).getParent(), "/")));
            } else {
                servlet.append(getServletPath());
            }
            if (sx != null) {
                urlConvert = "true".equals(sx.getInitParameter("mmbase.taglib.image.urlconvert"));
            }
        } else {
            servlet.append(getServletPath());
        }
        servlet.append(usesBridgeServlet && ses != null ? "session=" + ses + "+" : "");
        String template = (String) args.get("template");
        if (template == null) template = ImageCaches.GUI_IMAGETEMPLATE;

        String imageThumb;
        MMObjectNode icache;
        if (urlConvert) {
            icache = null;
            imageThumb = servlet.toString() + node.getNumber() + "+" + URLESCAPER.transform(template);
        } else {
            icache = getCachedNode(node, template);
            if (icache == null) {
                throw new RuntimeException("No icache found!");
            }
            imageThumb = servlet.toString() + (icache != null ? "" + icache.getNumber() : "");
        }

        servlet.append(node.getNumber());
        String image;
        HttpServletResponse res = (HttpServletResponse) args.get(Parameter.RESPONSE);
        if (res != null) {
            imageThumb = res.encodeURL(imageThumb);
            image      = res.encodeURL(servlet.toString());
        } else {
            image = servlet.toString();
        }
        String heightAndWidth;
        ImageCaches imageCaches = (ImageCaches) mmb.getMMObject("icaches");
        if (imageCaches != null && imageCaches.storesDimension()) {
            Dimension dim;
            if (icache != null) {
                dim = imageCaches.getDimension(icache);
            } else {
                List ar = new ArrayList();
                if (template != null) {
                    ar.add(template);
                }
                dim = (Dimension) node.getFunctionValue("dimension", ar);
            }
            StringBuffer buf = new StringBuffer();
            if(dim.getHeight() > 0) {
                buf.append("height=\"").append(dim.getHeight()).append("\" ");
            } else {
                log.warn("Found non-positive height.");
            }
            if (dim.getWidth() > 0) {
                buf.append("width=\"").append(dim.getWidth()).append("\" ");
            } else {
                log.warn("Found non-positive width.");
            }
            heightAndWidth = buf.toString();
        } else {
            heightAndWidth = "";
        }

        String title;
        String field = (String) args.get("field");
        if (field == null || field.equals("")) {
            // gui for the node itself.
            title = "";
        } else {
            if (storesDimension()) {
                title = " title=\"" + getMimeType(node) + " " + getDimension(node) + "\"";
            } else {
                title = " title=\"" + getMimeType(node) + "\"";
            }
        }

        return
            "<a href=\"" + image + "\" class=\"mm_gui\" onclick=\"window.open(this.href); return false;\"><img src=\"" + imageThumb + "\" " +
            heightAndWidth +
            "border=\"0\" alt=\"" +
            org.mmbase.util.transformers.Xml.XMLAttributeEscape(alt, '\"') +
            "\"" + title + " /></a>";
    }

    // javadoc inherited
    protected String getSGUIIndicatorForNode(MMObjectNode node, Parameters args) {
        return getGUIIndicatorWithAlt(node, node.getStringValue("title"), args);
    }


    // javadoc inherited
    public String getDefaultImageType() {
        return defaultImageType;
    }

    // javadoc inherited
    public boolean commit(MMObjectNode node) {
        Collection changed = node.getChanged();
        // look if we need to invalidate the image cache...
        boolean imageCacheInvalid = changed.contains("handle");
        // do the commit
        if(super.commit(node)) {
            // when cache is invalid, invalidate
            if(imageCacheInvalid) {
                invalidateImageCache(node);
                templateCacheNumberCache.remove(node.getNumber());
            }
            return true;
        }
        return false;
    }

    /**
     * Override the MMObjectBuilder removeNode, to invalidate the Image Cache AFTER a deletion of the
     * image node.
     * Remove a node from the cloud.
     * @param node The node to remove.
     */
    public void removeNode(MMObjectNode node) {
        invalidateImageCache(node);
        templateCacheNumberCache.remove(node.getNumber());
        super.removeNode(node);
    }



    /* (non-Javadoc)
     * @see org.mmbase.module.core.MMObjectBuilder#notify(org.mmbase.core.event.NodeEvent)
     */
    public void notify(NodeEvent event) {
        if (log.isDebugEnabled()) {
            log.debug("Changed " + event.getMachine() + " " + event.getNodeNumber() +
                " " + event.getBuilderName() + " "+ NodeEvent.newTypeToOldType(event.getType()));
        }
        invalidateTemplateCacheNumberCache(event.getNodeNumber());
        super.notify(event);
    }


    /**
     * Invalidate the Image Cache, if there is one, for a specific ImageNode
     * @param node The image node, which is the original
     */
    private void invalidateImageCache(MMObjectNode node) {
        ImageCaches icache = (ImageCaches) mmb.getMMObject("icaches");
        if(icache != null) {
            // we have a icache that is active...
            icache.invalidate(node);
        }
    }

    /**
     * @javadoc
     */
    void invalidateTemplateCacheNumberCache(int number) {
        templateCacheNumberCache.remove(number);
    }

    /**
     * @since MMBase-1.7
     */
    void invalidateTemplateCacheNumberCache(String ckey) {
        templateCacheNumberCache.remove(ckey);
    }




}

