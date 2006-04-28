/*

This software is OSI Certified Open Source Software.
OSI Certified is a certification mark of the Open Source Initiative.

The license (Mozilla version 1.0) can be read at the MMBase site.
See http://www.MMBase.org/license

*/
package org.mmbase.util;

import java.io.File;
import java.io.Writer;
import java.io.StringWriter;
import java.io.OutputStreamWriter;
import java.io.FileOutputStream;

import java.util.*;

import javax.xml.transform.*;
import javax.xml.parsers.*;
import javax.xml.transform.stream.StreamResult;
import javax.xml.transform.stream.StreamSource;
import javax.xml.transform.dom.DOMSource;


import org.mmbase.cache.xslt.*;

import org.mmbase.util.xml.URIResolver;
import org.mmbase.util.logging.Logger;
import org.mmbase.util.logging.Logging;

/**
 * Make XSL Transformations
 *
 * @author Case Roole, cjr@dds.nl
 * @author Michiel Meeuwissen
 * @version $Id: XSLTransformer.java,v 1.19 2003-07-18 14:56:53 michiel Exp $
 *
 */
public class XSLTransformer {
    // logger
    private static Logger log = Logging.getLoggerInstance(XSLTransformer.class.getName());
    /**
     * Empty constructor
     */
    public XSLTransformer() {}

    /**
     * Transform an XML document using a certain XSL document.
     *
     * @param xmlPath Path to XML file
     * @param xslPath Path to XSL file
     * @return String with converted XML document
     */
    public String transform(String xmlPath, String xslPath) {
	return transform(xmlPath,xslPath,false);
    }

    /**
     * Transform an XML document using a certain XSL document, on
     * MMBase specic way (error handling, entitity resolving, uri
     * resolving, logging), and write it to string, which optionally can be
     * 'cut'.
     *
     * @param xmlPath Path to XML file
     * @param xslPath Path to XSL file
     * @param cutXML if <code>true</code>, cuts the &lt;?xml&gt; line that normally starts an
     *               xml document
     * @return String with converted XML document
     *
     *
     */
    public String transform(String xmlPath, String xslPath, boolean cutXML) {
        try {
            StringWriter res = new StringWriter();
            transform(new File(xmlPath), new File(xslPath), new StreamResult(res), null, true);
	    String s = res.toString();
	    int n = s.indexOf("\n");
	    if (cutXML && s.length() > n) {
		s = s.substring(n + 1);
	    }
	    return s;
        } catch (Exception e) {
            log.error(e.getMessage());
	    log.error(Logging.stackTrace(e));
            return "Error during XSLT tranformation: "+e.getMessage();
        }
    }

    /**
     * This is the base function which calls the actual XSL
     * transformations. Performs XSL transformation on MMBase specific
     * way (using MMBase cache, and URIResolver).
     *
     * @since MMBase-1.6
     **/

    public void transform(Source xml, File xslFile, Result result, Map params) throws TransformerException, ParserConfigurationException, java.io.IOException, org.xml.sax.SAXException {
        transform(xml, xslFile, result, params, true);
    }
    public void transform(Source xml, File xslFile, Result result, Map params, boolean considerDir) throws TransformerException, ParserConfigurationException, java.io.IOException, org.xml.sax.SAXException {

        if (log.isDebugEnabled()) {
            Runtime rt = Runtime.getRuntime();
            rt.gc();
            log.debug("total memory      : " + rt.totalMemory() / (1024 * 1024) + " Mbyte   free memory       : " + rt.freeMemory() / (1024 * 1024) + " Mbyte");
        }

        TemplateCache cache= TemplateCache.getCache();
        Source xsl = new StreamSource(xslFile);
        URIResolver uri;
        if (considerDir) {
            uri = new URIResolver(xslFile.getParentFile());
        } else {
            uri = new URIResolver();
        }
        Templates cachedXslt = cache.getTemplates(xsl, uri);
        if (log.isDebugEnabled()) {
            // log.debug("Size of cached XSLT " + SizeOf.getByteSize(cachedXslt) + " bytes");
            log.debug("Size of URIResolver " + SizeOf.getByteSize(uri) + " bytes");
            log.debug("template cache size " + cache.size() + " entries");
        }
        if (cachedXslt == null) {
            cachedXslt = FactoryCache.getCache().getFactory(uri).newTemplates(xsl);
            cache.put(xsl, cachedXslt, uri);
        } else {
            if (log.isDebugEnabled()) log.debug("Used xslt from cache with " + xsl.getSystemId());
        }
        Transformer transformer = cachedXslt.newTransformer();


        if (log.isDebugEnabled()) {
            log.debug("Size of transformer " + SizeOf.getByteSize(transformer) + " bytes");
        }
        if (params != null) {
            Iterator i = params.entrySet().iterator();
            while (i.hasNext()){
                Map.Entry entry = (Map.Entry) i.next();
                if (log.isDebugEnabled()) log.debug("setting param " + entry.getKey() + " to " + entry.getValue());
                transformer.setParameter((String) entry.getKey(), entry.getValue());
            }
        }
        transformer.transform(xml, result);
    }

    /**
     * Perfoms XSL Transformation on XML-file which is parsed MMBase
     * specificly (useing MMBasse EntityResolver and Errorhandler).
     *
     * @since MMBase-1.6
     */
    public void transform(File xmlFile, File xslFile, Result result, Map params, boolean considerDir) throws TransformerException, ParserConfigurationException, java.io.IOException, org.xml.sax.SAXException {
        // create the input xml.
        DocumentBuilderFactory dfactory = DocumentBuilderFactory.newInstance();

        // turn validating on
        XMLEntityResolver resolver = new XMLEntityResolver(true);
        dfactory.setNamespaceAware(true);
        DocumentBuilder db = dfactory.newDocumentBuilder();

        XMLErrorHandler handler = new XMLErrorHandler();
        db.setErrorHandler(handler);
        db.setEntityResolver(resolver);
        org.w3c.dom.Document xmlDoc = db.parse(xmlFile);

        transform(new DOMSource(xmlDoc), xslFile, result, params, considerDir);
    }


    /**
     * Can be used to transform a directory of XML-files. Of course the result must be written to files too.
     *
     * The transformations will be called with a paramter "root" which
     * points back to the root directory relatively. You need this
     * when all your transformations results (probably html's) need to
     * refer to the same file which is relative to the root of the transformation.
     *
     * @since MMBase-1.6
     */

    public void transform(File xmlDir, File xslFile, File resultDir, boolean recurse, Map params, boolean considerDir) throws TransformerException, ParserConfigurationException, java.io.IOException, org.xml.sax.SAXException {
        if (! xmlDir.isDirectory()) {
            throw  new TransformerException("" + xmlDir + " is not a directory");
        }
        if (! resultDir.exists()) {
            resultDir.mkdir();
        }
        if (! resultDir.isDirectory()) {
            throw  new TransformerException("" + resultDir + " is not a directory");
        }
        if (params == null) params = new HashMap();

        List exclude = (List) params.get("exclude");

        File[] files = xmlDir.listFiles();
        for (int i = 0; i < files.length; i++) {
            if (exclude.contains(files[i].getName())) continue;

            if (recurse && files[i].isDirectory()) {
                if ("CVS".equals(files[i].getName())) continue;
                File resultSubDir = new File(resultDir, files[i].getName());
                Map myParams;
                if (params == null) {
                    myParams = new HashMap();
                } else {
                    myParams = new HashMap(params);
                }

                if (myParams.get("root") == null) {
                    myParams.put("root", "../");
                } else {
                    if ("./".equals(myParams.get("root"))) {
                        myParams.put("root", "../");
                    } else {
                        myParams.put("root", myParams.get("root") + "../");
                    }
                }
                log.info("Transforming directory " + files[i] + " (root is " + myParams.get("root") + ")");
                transform(files[i], xslFile, resultSubDir, recurse, myParams, considerDir);
            } else {
                if (! files[i].getName().endsWith(".xml")) continue;
                String fileName = files[i].getName();
                fileName = fileName.substring(0, fileName.length() - 4);
                params.put("filename", fileName);
                String extension = (String) params.get("extension");
                if (extension == null) extension = "html";
                File resultFile = new File(resultDir, fileName  + "." + extension);
                if (resultFile.lastModified() > files[i].lastModified()) {
                    log.info("Not transforming " + files[i] + " because " + resultFile + " is up to date");
                } else {
                    log.info("Transforming file " + files[i] + " to " + resultFile);
                    try {
                        Result res;
                        if ("true".equals(params.get("dontopenfile"))) {
                            res = new StreamResult(System.out);
                        } else {
                            res = new StreamResult(resultFile);
                        }
                        transform(files[i], xslFile, res, params, considerDir);
                    } catch (Exception e) {
                        log.error(e.toString());
                        log.error(Logging.stackTrace(e));
                    }
                }
            }
        }
    }

    /**
     * Invocation of the class from the commandline for testing/building
     */
    public static void main(String[] argv) {
        XSLTransformer t = new XSLTransformer();
        // log.setLevel(org.mmbase.util.logging.Level.DEBUG);
        if (argv.length < 2) {
            log.info("Use with two arguments: xslt-file xml-inputfile [xml-outputfile]");
            log.info("Use with tree arguments: xslt-file xml-inputdir xml-outputdir [key=value options]");
            log.info("special options can be:");
            log.info("   usecache=true:     Use the Template cache or not (to speed up)");
            log.info("   exclude=<filename>:  File/directory name to exclude (can be used multiple times");
            log.info("   extension=<file extensions>:  File extensions to use in transformation results (defaults to html)");
            log.info("Other options are passed to XSL-stylesheet as parameters.");

        } else {
            HashMap params=null;
            if (argv.length > 3) {
                params= new HashMap();
                for (int i = 3; i<argv.length; i++) {
                    String key = argv[i];
                    String value = "";
                    int p = key.indexOf("=");
                    if (p > 0) {
                        if (p<key.length()-1) value = key.substring(p+1);
                        key = key.substring(0, p);
                    }
                    if (key.equals("usecache")) {
                        TemplateCache.getCache().setActive(value.equals("true"));
                    } else if (key.equals("exclude")) {
                        if (params.get("exclude") == null) {
                            params.put("exclude", new ArrayList());
                        }
                        List excludes = (List) params.get("exclude");
                        excludes.add(value);
                    } else {
                        params.put(key, value);
                    }
                }
            }

            File in = new File(argv[1]);
            if (in.isDirectory()) {
                log.info("Transforming directory " + in);
                long start = System.currentTimeMillis();
                try {
                    t.transform(in, new File(argv[0]), new File(argv[2]), true, params, true);
                } catch (Exception e) {
                    log.error("Error: " + e.toString());
                }
                log.info("Transforming took " + (System.currentTimeMillis() - start) / 1000.0 + " seconds");
            } else {
                log.info("Transforming file " + argv[1]);
                if (argv.length > 2) {
                    try {
                        FileOutputStream stream = new FileOutputStream(argv[2]);
                        Writer f = new OutputStreamWriter(stream,"utf-8");
                        t.transform(new File(argv[1]), new File(argv[0]), new StreamResult(f), params, true);
                        f.close();
                    } catch (Exception e) {
                        log.error("Error: " + e.toString());
                    }
                } else {
                    String s=t.transform(argv[1], argv[0], false);
                    log.info(s);
                }
            }
        }
    }
}
