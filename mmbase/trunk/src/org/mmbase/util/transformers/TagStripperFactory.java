package org.mmbase.util.transformers;

import java.util.*;
import java.util.regex.*;
import javax.swing.text.*;
import javax.swing.text.html.*;
import java.io.*;
import org.mmbase.util.functions.*;


import org.mmbase.util.logging.Logger;
import org.mmbase.util.logging.Logging;


/**
 * Can be used to strip tags and attributes from HTML. Also, if markup remains, it can be made
 * 'locally' well formed XML (the 'escapeamps' parameter suffices then), by which I mean that if you
 * put it in a div, that div is then well formed.
 *
 * http://javafaq.nu/java-example-code-618.html
 * @author Michiel Meeuwissen
 * @version $Id$
 * @since MMBase-1.8.4
 */
public class TagStripperFactory implements ParameterizedTransformerFactory<CharTransformer>  {

    private static final Logger log = Logging.getLoggerInstance(TagStripperFactory.class);


    private static final String NL_TOKEN = "XXXX_NL_XXXX";

    public static final Parameter<String> TAGS         =
        new Parameter<String>("tags", String.class, "");  // allowed tags, default no tags are permitted.

    public static final Parameter<Boolean> ADD_BRS     =
        new Parameter<Boolean>("addbrs", Boolean.class, Boolean.FALSE);

    public static final Parameter<Boolean> ESCAPE_AMPS =
        new Parameter<Boolean>("escapeamps", Boolean.class, Boolean.FALSE);

    protected static final Parameter[] PARAMS = new Parameter[] { TAGS, ADD_BRS, ESCAPE_AMPS };

    public Parameters createParameters() {
        return new Parameters(PARAMS);
    }



    /**
     * Creates a parameterized transformer.
     */
    public CharTransformer createTransformer(final Parameters parameters) {

        parameters.checkRequiredParameters();
        if (log.isDebugEnabled()) {
            log.debug("Creating transformer, with " + parameters);
        }

        final List<Tag> tagList;
        String tags = parameters.getString(TAGS).toUpperCase();
        if (tags.equals("XSS")) {
            tagList = XSS;
        } else if (tags.equals("")) {
            tagList = NONE;
        } else if (tags.equals("NONE")) {
            tagList = NONE;
        } else {
            throw new RuntimeException("Unknonw value for 'tags' parameter '" + tags + "'. Known are 'XSS': strip only cross-site scripting, and '': strip all tags.");
        }

        final HTMLEditorKit.Parser parser = new ParserGetter().getParser();
        ReaderTransformer trans = new ReaderTransformer() {
                public Writer transform(Reader r, final Writer w) {
                    final TagStripper callback = new TagStripper(w, tagList);
                    callback.addBrs     = parameters.get(ADD_BRS);
                    callback.escapeAmps = parameters.get(ESCAPE_AMPS);
                    if (callback.addBrs) {
                        // before going into the parser, make existing newlines recognizable, by replacing them by a token
                        r = new TransformingReader(r, new ChunkedTransformer(ChunkedTransformer.XMLTEXT) {
                                protected boolean replace(String string, Writer w, Status status) throws IOException {
                                    w.write(string.replaceAll("\n", NL_TOKEN));
                                    return false;
                                }

                                protected String base() { return "nl"; }

                            });
                    }
                    try {
                        parser.parse(r, callback, true);
                    } catch (Exception e) {
                        log.warn(e.getMessage(), e);
                    }

                    return w;
                }
                public String toString() {
                    return tagList + " " + (parameters.get(ADD_BRS) ? "(adding brs)" : "");
                }
            };
        if (log.isDebugEnabled()) {
            log.debug("Created " + trans);
        }
        return trans;
    }


    /**
     * Enumeration for types of allowances
     */
    private enum Allows {
        YES,
        NO,
        DONTKNOW
    }

    /**
     *
     */
    private static abstract class Allowance {
        abstract Allows allows(String p);
    }

    private static final Allowance ALLOW_ALL    = new Allowance() {
            Allows allows(String p) { return Allows.YES; }
            public String toString() { return "ALL"; }
        };
    private static final Allowance DISALLOW_ALL = new Allowance() {
            Allows allows(String p) { return Allows.NO; }
            public String toString() { return "NONE"; }
        };

    private static class PatternAllowance extends Allowance {
        private final Pattern pattern;
        PatternAllowance(Pattern p) {
            pattern = p;
        }
        PatternAllowance(String s) {
            pattern = Pattern.compile(s);
        }
        Allows allows (String p) {
            return pattern.matcher(p).matches() ?  Allows.YES : Allows.DONTKNOW;
        }
        public String toString() {
            return pattern.toString();
        }
    }
    private static class PatternDisallowance extends Allowance {
        private final Pattern pattern;
        PatternDisallowance(Pattern p) {
            pattern = p;
        }

        PatternDisallowance(String s) {
            pattern = Pattern.compile(s);
        }
        Allows allows (String p) {
            return pattern.matcher(p).matches() ? Allows.NO : Allows.DONTKNOW;
        }
        public String toString() {
            return "!" + pattern.toString();
        }
    }
    private static class ChainedAllowance extends Allowance {
        private final List<Allowance> allowances = new ArrayList<Allowance>();
        void add(Allowance... alls) {
            for (Allowance a : alls) {
                allowances.add(a);
            }
        }
        Allows allows(String p) {
            for (Allowance a : allowances) {
                Allows allows = a.allows(p);
                if (allows != Allows.DONTKNOW) return allows;
            }
            return Allows.DONTKNOW;
        }
        public String toString() {
            return allowances.toString();
        }
    }


    private static class Attr {
        final Allowance key;
        final Allowance value;
        public Attr(Allowance k, Allowance v) {
            key = k; value = v;
        }
        public Attr(Allowance k) {
            key = k; value = ALLOW_ALL;
        }
        public Allows allows(String k, String v) {
            Allows ka = key.allows(k);
            if (ka == Allows.NO) return Allows.NO;
            Allows va = value == null ? Allows.YES : value.allows(v);
            if (va == Allows.NO) return Allows.NO;
            if (ka == Allows.YES && va == Allows.YES) return Allows.YES;
            return Allows.DONTKNOW;
        }
        public String toString() {
            return key.toString() + "=" + value;
        }
    }



    private static class Tag extends ChainedAllowance {
        private final List<Attr> attributes = new ArrayList<Attr>();
        public Tag(Allowance... wrapped) {
            super();
            add(wrapped);
        }
        public List<Attr> getAttributes() {
            return attributes;
        }
        public  boolean allowsAttribute(String k, String v) {
            ////System.out.println("Checking " + k + "=" + v + " for " + this);
            for (Attr attr : attributes) {
                switch (attr.allows(k, v)) {
                case YES: return true;
                case NO: return false;
                }
            }
            return true;
        }
        public String toString() {
            return super.toString() + "(" + attributes + ")";
        }
    }


    protected static class TagStripper extends HTMLEditorKit.ParserCallback {
        private final Writer out;
        private final List<Tag> tags;
        boolean addImplied = false;
        boolean addBrs     = false;
        boolean escapeAmps = false;
        List<HTML.Tag> impliedTags = new ArrayList<HTML.Tag>();
        List<HTML.Tag> stack       = new ArrayList<HTML.Tag>();

        public TagStripper(Writer out, List<Tag> t) {
            this.out = out;
            tags = t;
        }

        public String toString() {
            return "" + tags + (addBrs ? "(replacing newlines)" : "");
        }
        protected Tag allowed(String tagName) {
            //System.out.print("Checking wheter 'tagName' allowed");
            for (Tag tag : tags) {
                //System.out.println("using " + tag);
                Allows a = tag.allows(tagName);
                switch (a) {
                case YES: return tag;
                case NO: return null;
                }
            }
            return null;
        }

        public void handleText(char[] text, int position) {
            try {
                //System.out.println("Handling " + new String(text) + " for " + position);
                if (addBrs) {
                    String t = new String(text);
                    if (text[0] == '>') { // odd, otherwise <br /> ends up as <br />>
                        t = t.substring(1);
                    }
                    if (log.isTraceEnabled()) {
                        log.trace("handling " + t);
                    }

                    if (stack.get(0).isPreformatted()) {
                        t = t.replaceAll(NL_TOKEN, "\n");
                    } else {
                        t = t.replaceAll(NL_TOKEN, "<br class='auto' />");
                    }
                    if (escapeAmps) {
                        // see comment in handleAttributes
                        t = t.replaceAll("&", "&amp;");
                    }
                    out.write(t);

                } else {
                    if (escapeAmps) {
                        String t;
                        if (text[0] == '>') { // odd, otherwise <br /> ends up as <br />>
                            t = new String(text).substring(1);
                        } else {
                            t = new String(text);
                        }
                        // see comment in handleAttributes
                        t = t.replaceAll("&", "&amp;");
                        out.write(t);
                    } else {
                        // no need to wrap in string first.
                        if (text[0] == '>') {
                            out.write(text, 1, text.length - 1);
                        } else {
                            out.write(text);
                        }
                    }

                }
            } catch (IOException e) {
                log.warn(e);
            }
        }

        protected Tag getTag(HTML.Tag tag, MutableAttributeSet attributes) {
            //System.out.println("handling tag " + tag);
            boolean implied = attributes.containsAttribute(IMPLIED, Boolean.TRUE);
            Tag t;
            if (! addImplied && implied) {
                t = null;
                impliedTags.add(tag);
            } else {
                t = allowed(tag.toString());
            }
            return t;

        }
        protected void handleAttributes(Tag t, MutableAttributeSet attributes) throws IOException {
            //System.out.println("handling attributes");
            Enumeration<?> en = attributes.getAttributeNames();
            while (en.hasMoreElements()) {
                Object attName =  en.nextElement();
                if (addBrs && attName.equals("nl_")) continue;
                AttributeSet set = attributes;
                Object value = attributes.getAttribute(attName);
                while (value == null && set.getResolveParent() != null) {
                    set = set.getResolveParent();
                    value = set.getAttribute(attName);
                }
                if (t.allowsAttribute("" + attName, "" + value)) {
                    out.write(' ');
                    out.write("" + attName);
                    out.write('=');
                    out.write('"');
                    String s = "" + value;
                    if (escapeAmps) {
                        // HTMLEditorKit translates all Iso1 entities to unicode.
                        // Escape remaining amps, to produce valid Xml.
                        s = s.replaceAll("&", "&amp;");
                    }
                    s = s.replaceAll("\"", "&quot;");
                    out.write(s);
                    out.write('"');
                }
            }
        }

        public void handleStartTag(HTML.Tag tag, MutableAttributeSet attributes, int position) {
            //System.out.println("Start tag " + tag);
            try {
                stack.add(0, tag);
                Tag t = getTag(tag, attributes);

                if (t != null) {
                    out.write('<');
                    out.write(tag.toString());
                    handleAttributes(t, attributes);
                    out.write('>');
                } else {
                    out.write(' ');
                }
            } catch (IOException e) {
                log.warn(e);
            }
        }

        public void handleEndTag(HTML.Tag tag, int position) {
            //System.out.println("End tag " + tag);
            stack.remove(0);
            try {
                String tagName = tag.toString();
                Tag t;
                boolean implied = impliedTags.contains(tag);
                if (! addImplied && implied) {
                    t = null;
                } else {
                    t = allowed(tagName);
                }
                if (t != null) {
                    out.write("</");
                    out.write(tagName);
                    out.write('>');
                } else {
                    out.write(' ');
                }
            } catch (IOException e) {
                log.warn(e);
            }
        }
        public void handleSimpleTag(HTML.Tag tag, MutableAttributeSet attributes, int position) {
            //stack.remove(0);
            ////System.out.println("SIMPLE TAG " + tag);
            try {
                String tagName = tag.toString();
                Tag t = getTag(tag, attributes);
                if (t != null) {
                    out.write('<');
                    out.write(tagName);
                    handleAttributes(t, attributes);
                    out.write(" />");
                } else {
                    out.write(' ');
                }
            } catch (IOException e) {
                log.warn(e);
            }

        }
        public void handleError(String mes, int position) {
            log.debug(mes + " at " + position);
        }

        public void handleComment(char[] data, int pos) {
            try {
                out.write("<!-- " + new String(data) + " -->");
            } catch (IOException e) {
                log.warn(e);
            }
	}



        public void flush() {
            try {
                out.flush();
            } catch (IOException e) {
                log.warn(e);
            }
        }
    }

    protected static class ParserGetter extends HTMLEditorKit {
        // purely to make this method public
        public HTMLEditorKit.Parser getParser(){
            return super.getParser();
        }
    }

    // event attribute can contain javascript, so those are forbidden when doing XSS-stripping.
    protected static final Attr EVENTS = new Attr(new PatternDisallowance("(?i)onclick|ondblclick|onmousedown|onmousemove|onmouseout|onmouseover|onmouseup|onload|onunload|onchange|onsubmit|onreset|onselect|onblur|onfocus|onkeydown|onkeyup|onkeypress"));

    // only strip cross-site-scripting
    public final static List<Tag> XSS = new ArrayList<Tag>();
    static {
        {
            // <a> tags are permitted.
            Tag a = new Tag(new PatternAllowance("(?i)a"));
            // also the href attribute is permitted on that tag, but not all values.
            a.getAttributes().add(new Attr(new PatternAllowance("(?i)href"), new PatternDisallowance("(?i)javascript:.*")));
            // these 'events' attributes are forbidden also on <a>
            a.getAttributes().add(EVENTS);
            XSS.add(a);
        }
        // tags that are forbidden all together, because they a scripting, or the contents cannot be checked
        XSS.add(new Tag(new PatternDisallowance("(?i)script|embed|object|frameset|iframe")));

        {
            // all other tags are permitted
            Tag all = new Tag(ALLOW_ALL);
            // but not those event attributes
            all.getAttributes().add(EVENTS);
            XSS.add(all);
        }
    }

    // strip all tags
    public final  static List<Tag> NONE = new ArrayList<Tag>();
    static {
        NONE.add(new Tag(DISALLOW_ALL));
    }



    public static void main(String[] args) throws IOException {
        TagStripperFactory factory = new TagStripperFactory();
        Parameters params = factory.createParameters();
        params.set(TAGS, "NONE");
        params.set(ADD_BRS, false);
        params.set(ESCAPE_AMPS, true);
        CharTransformer transformer = factory.createTransformer(params);

        //        String source = "<p style=\"nanana\">allow this <b>but not this</b></p>";
//        String source = "<p style=nanana/>";
//        String source = "<p style=\"nanana\">text</p>";
//        String source = "<P sTyle=\"nanana\">hoi hoi\n<br><table WIDTH=\"45\" height=99 border='1\"' fONt=bold styLe=\"n\\\"one\">\nbla bla bla</table></p>";
        ////System.out.println("Source      = "+source);
        Writer w = new OutputStreamWriter(System.out);
        transformer.transform(new InputStreamReader(System.in), w);
        w.flush();
        ////System.out.println("Destination = "+dest);

        org.mmbase.util.ThreadPools.filterExecutor.shutdown();


    }

}
