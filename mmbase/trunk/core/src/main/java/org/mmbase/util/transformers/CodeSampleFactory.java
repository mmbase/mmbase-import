/*

This software is OSI Certified Open Source Software.
OSI Certified is a certification mark of the Open Source Initiative.

The license (Mozilla version 1.0) can be read at the MMBase site.
See http://www.MMBase.org/license

*/
package org.mmbase.util.transformers;

import java.util.regex.Pattern;
import java.util.regex.Matcher;

import org.mmbase.util.functions.Parameter;
import org.mmbase.util.functions.Parameters;
import org.mmbase.util.logging.Logger;
import org.mmbase.util.logging.Logging;

/**
 * This TransformerFactory enables the use of two different escapers in one piece
 * of text. I started it to simplify the inclusion of a code snippet in html.
 * You can specify the tags between which you wish to escape your text, the
 * escaper to use and you can set an escaper for the rest of the text.<br />
 * The tags default to &lt;pre&gt; and &lt;/pre&gt;, the first escaper defaults to
 * 'text/html' (which escapes &amp;, &lt;, &gt;, &quot; and leaves the linebreakes
 * untouched). The last escaper does by default nothing. But of course you can set
 * your own with the parameters 'starttag', 'closetag', 'escapecode' and 'escaperest'.
 *
 * @author Andr&eacute; van Toly
 * @since MMBase 1.8.0
 * @version $Id$
 */

public class CodeSampleFactory implements ParameterizedTransformerFactory<CharTransformer> {

    private final static Logger log = Logging.getLoggerInstance(CodeSampleFactory.class);

    private final static Parameter[] PARAMS = new Parameter[] {
        new Parameter<String>("starttag", String.class, "<pre>"),
        new Parameter<String>("closetag", String.class, ""),
        new Parameter<String>("escapecode", String.class, "text/html"),   // like attr. escaper of mm:content
        new Parameter<String>("escaperest", String.class, ""),     // do nothing by default

    };

    public Parameters createParameters() {
        return new Parameters(PARAMS);
    }

    public CharTransformer createTransformer(Parameters parameters) {
        parameters.checkRequiredParameters();

        return new CodeSample( (String) parameters.get("starttag"),
                               (String) parameters.get("closetag"),
                               (String) parameters.get("escapecode"),
                               (String) parameters.get("escaperest") );
    }

    protected class CodeSample extends StringTransformer {
        private String starttag = "<pre>";      // default?
        private String closetag = "";
        private String escapecode = "text/html";
        private String escaperest = "";         // do nothing

        /**
         * Constructor
         *
         */
        public CodeSample(String st, String ct, String ec, String er) {
            starttag = st;
            closetag = ct;
            escapecode = ec;
            escaperest = er;
        }

        /**
         * Default transform method with no (other) parameters then the string
         * to transform while using &lt;pre&gt; as the tag between which to escape
         * the text while using the default escaper 'text/html'.
         *
         * @param str The original string
         * @return The transformed string
         */
        public String transform(String str) {
            return transform(str, starttag, closetag, escapecode, escaperest);
        }

        /**
         * Transforms the characters of a code example between two tags,
         * &lt;pre&gt;-tags and &lt;/pre&gt; f.e., within a string and can use a
         * different escaper for the rest of the text.
         *
         * @param str           The original string
         * @param starttag      The opentag of the pair of tags between which code needs to be escaped
         * @param closetag      The closetag of the pair of tags between which code needs to be escaped
         * @param escapecode    The escaper to use on the piece of text between the tags
         * @param escaperest    The escaper to use on the rest of the text
         * @return              The transformed string
         */
        public String transform(String str, String starttag, String closetag, String escapecode, String escaperest) {
            StringBuilder result = new StringBuilder();

            String stag = starttag;
            String ctag = closetag;
            if (ctag.equals("")) {  // create closetag based on starttag, only usefull on an html tag
                ctag = "</" + starttag.substring(1, starttag.length());
            }

            Pattern sp = Pattern.compile("\\Q" + stag + "\\E", Pattern.DOTALL);
            Pattern cp = Pattern.compile("\\Q" + ctag + "\\E", Pattern.DOTALL);
            Matcher stm = sp.matcher(str);  // starttag Matcher
            Matcher ctm = cp.matcher(str);  // closetag Matcher

            int s = 0;  // startposition 'rest' of the text
            while (stm.find() && ctm.find()) {
                // stm.start(0) = position of starttag
                // stm.end(0) = where starttag ends
                // ctm.start(0) = position of closetag
                // etc.
                String normalStr = "";  // for the 'normal' text (not between the tags)
                normalStr = str.substring(s, stm.start(0) );
                if (log.isDebugEnabled()) log.debug("Found rest str: " + normalStr);
                normalStr = transformPart(normalStr, escaperest);
                result.append(normalStr).append(stag);  // the transformed str and the tag

                s = ctm.end(0);     // here starts the rest of the text to be worked on

                String codeStr = str.substring(stm.end(0), ctm.start(0));     // the 'code'
                if (log.isDebugEnabled()) log.debug("Found code str: " + codeStr);
                codeStr = transformPart(codeStr, escapecode);

                result.append(codeStr).append(ctag);
            }

            // use escaperest upond the remaining piece of text and append it
            //   plus we always use escaperest, even when there is not match
            String rest = str.substring(s, str.length());
            result.append( transformPart(rest, escaperest) );
            if (s == 0) {
                if (log.isDebugEnabled()) log.debug("No match with the tags '" + stag + "' and '" + ctag + "'");
            }
            return result.toString();
        }


       /**
         * Transforms parts of the string. Calls the transform methods in
         * {@link org.mmbase.util.transformers.XmlField} and {@link org.mmbase.util.transformers.Xml}.
         * This method needs to be rewritten to support all escapers/transformers. It only
         * supports p, pp, p-ommit-surrounding, pp-ommit-surrounding, inline, text/html and text/xml.
         *
         * @param str       String to transform
         * @param escaper   The transformer or escaper to use (see escaper attr of &lt;mm:content /&gt;)
         * @return          The transformed string
         */
        public String transformPart(String str, String escaper) {
            if (escaper.equals("p")) {
                str = XmlField.richToHTMLBlock(str, true, true);
            } else if (escaper.equals("pp")) {
                str = XmlField.richToHTMLBlock(str);
            } else if (escaper.equals("p-ommit-surrounding")) {
                str = XmlField.richToHTMLBlock(str, true, false);
            } else if (escaper.equals("pp-ommit-surrounding")) {
                str = XmlField.richToHTMLBlock(str, false, false);
            } else if (escaper.equals("inline")) {
                str = XmlField.poorToHTMLInline(str);
            } else if (escaper.equals("text/html") || escaper.equals("text/xml")) {
                str = Xml.XMLEscape(str);
            } else {    // at least return something
                //throw new UnsupportedOperationException("Cannot transform");
            }
            if (log.isDebugEnabled()) log.debug("Returning: " + str);
            return str;
        }

    }
}
