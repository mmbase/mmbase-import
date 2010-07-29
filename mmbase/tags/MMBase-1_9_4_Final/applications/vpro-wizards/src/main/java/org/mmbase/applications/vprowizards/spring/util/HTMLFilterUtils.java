/*

This software is OSI Certified Open Source Software.
OSI Certified is a certification mark of the Open Source Initiative.

The license (Mozilla version 1.0) can be read at the MMBase site.
See http://www.MMBase.org/license

*/ 
package org.mmbase.applications.vprowizards.spring.util;

import java.io.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.apache.xerces.xni.XNIException;
import org.apache.xerces.xni.parser.*;
import org.cyberneko.html.HTMLConfiguration;
import org.cyberneko.html.filters.ElementRemover;
import org.mmbase.util.logging.Logger;
import org.mmbase.util.logging.Logging;

/**
 * HTMLFilter contains utility methods for filtering and html used
 * in input fields
 *
 * @author Peter Maas <peter.maas@finalist.com>
 */
public class HTMLFilterUtils {
    private static final Logger log = Logging.getLoggerInstance(HTMLFilterUtils.class);

    public static final String ENCODING = "UTF-8";
    private static Pattern htmlNewline = Pattern.compile("(<(br|/p){1}\\s*/?>)",Pattern.CASE_INSENSITIVE);

    public static ElementRemover remover = new ElementRemover();
    static {
        // set which elements to accept
        remover.acceptElement("p", null);
        remover.acceptElement("i", null);
        remover.acceptElement("b", null);
        remover.acceptElement("strong", null);
        remover.acceptElement("em", null);
        remover.acceptElement("br", null);
        remover.acceptElement("ul", null);
        remover.acceptElement("ol", null);
        remover.acceptElement("li", null);
        remover.acceptElement("div", null);
        //remover.acceptElement("span", new String[] { "style" });
        remover.acceptElement("a", new String[] { "href" });

        // embedded video's
        remover.acceptElement("object", new String[] { "width","height" });
        remover.acceptElement("param",  new String[] { "name","value" });
        remover.acceptElement("embed",  new String[] { "width", "height", "src", "type", "wmode", "style", "id", "flashvars", "quality", "bgcolor", "name", "align","allowscriptaccess", "type", "pluginspage", "allowFullScreen", "scale", "salign", "bgcolor", "resizeVideo", "FlashVars" });

        // completely remove script elements
        remover.removeElement("script"); // also removes content
    }

    public static boolean isHTML(String input){
        Pattern p = Pattern.compile("<br|<li|<span|<p|<b|<i|<strong|<em|&quot;|&eacute;|<div|<font|<object|<param|<embed");
        Matcher m = p.matcher(input);
        return m.find();
    }

    public static String filter(String input) throws XNIException, IOException{
        return HTMLFilterUtils.filter(input,remover);
    }

    public static String filter(String input, ElementRemover remover) throws XNIException, IOException{
        // create writer filter
        StringWriter sw = new StringWriter();
        org.cyberneko.html.filters.Writer writer =
            new org.cyberneko.html.filters.Writer(sw,ENCODING);

        // setup filter chain
        XMLDocumentFilter[] filters = {remover, new JavascriptUrlFilter(),  writer};

        // create HTML parser
        XMLParserConfiguration parser = new HTMLConfiguration();
        parser.setProperty("http://cyberneko.org/html/properties/filters", filters);
        parser.setProperty("http://cyberneko.org/html/properties/names/elems","lower");

        XMLInputSource source = new XMLInputSource(null,null,null,new StringReader(input),ENCODING);

        parser.parse(source);

        return sw.toString().replaceAll("&apos;", "&#39;");
    }

    public static String removeAllTags(String input) {
        if (input == null) {
            return "";
        }

            Matcher m = htmlNewline.matcher(input);
            StringBuffer sb = new StringBuffer();
            while (m.find()) {
                m.appendReplacement(sb, " " + m.group());
            }
            m.appendTail(sb);
            input = sb.toString();

        try {
            ElementRemover remover = new ElementRemover();
            return filter(input, remover).trim();
        }
        catch (Exception e) {
            log.error("something went wrong removing all tags: "+e.getMessage());
            throw new RuntimeException(e);
        }
    }
}
