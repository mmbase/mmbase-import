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

    public static final Parameter<Boolean> ADD_NEWLINES     =
        new Parameter<Boolean>("addnewlines", Boolean.class, Boolean.FALSE);


    protected static final Parameter[] PARAMS = new Parameter[] { TAGS, ADD_BRS, ESCAPE_AMPS, ADD_NEWLINES };

    @Override
    public Parameters createParameters() {
        return new Parameters(PARAMS);
    }



    /**
     * Creates a parameterized transformer.
     */
    @Override
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
            throw new RuntimeException("Unknown value for 'tags' parameter '" + tags + "'. Known are 'XSS': strip only cross-site scripting, and '': strip all tags.");
        }

        final HTMLEditorKit.Parser parser = new ParserGetter().getParser();
        ReaderTransformer trans = new ReaderTransformer() {
            @Override
                public Writer transform(Reader r, final Writer w) {
                    final TagStripper callback = new TagStripper(w, tagList);
                    callback.addBrs     = parameters.get(ADD_BRS);
                    callback.escapeAmps = parameters.get(ESCAPE_AMPS);
                    callback.addNewlines = parameters.get(ADD_NEWLINES);
                    if (callback.addBrs) {
                        // before going into the parser, make existing newlines recognizable, by replacing them by a token
                        r = new TransformingReader(r, new ChunkedTransformer(ChunkedTransformer.XMLTEXT) {
                                @Override
                                protected boolean replace(String string, Writer w, Status status) throws IOException {
                                    w.write(string.replaceAll("\n", NL_TOKEN));
                                    return false;
                                }

                                @Override
                                protected String base() {
                                    return "nl";
                                }

                            });
                    }
                    try {
                        parser.parse(r, callback, true);
                    } catch (Exception e) {
                        log.warn(e.getMessage(), e);
                    }

                    return w;
                }
            @Override
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
        @Override
        Allows allows(String p) {
            return Allows.YES;
        }
        @Override
        public String toString() {
            return "ALL";
        }
    };
    private static final Allowance DISALLOW_ALL = new Allowance() {
        @Override
        Allows allows(String p) {
            return Allows.NO;
        }
        @Override
        public String toString() {
            return "NONE";
        }
        };

    private static class PatternAllowance extends Allowance {
        private final Pattern pattern;
        PatternAllowance(Pattern p) {
            pattern = p;
        }
        PatternAllowance(String s) {
            pattern = Pattern.compile(s);
        }
        @Override
        Allows allows (String p) {
            return pattern.matcher(p).matches() ?  Allows.YES : Allows.DONTKNOW;
        }
        @Override
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
        @Override
        Allows allows (String p) {
            return pattern.matcher(p).matches() ? Allows.NO : Allows.DONTKNOW;
        }
        @Override
        public String toString() {
            return "!" + pattern.toString();
        }
    }
    private static class ChainedAllowance extends Allowance {
        private final List<Allowance> allowances = new ArrayList<Allowance>();
        void add(Allowance... alls) {
            allowances.addAll(Arrays.asList(alls));
        }
        @Override
        Allows allows(String p) {
            for (Allowance a : allowances) {
                Allows allows = a.allows(p);
                if (allows != Allows.DONTKNOW) return allows;
            }
            return Allows.DONTKNOW;
        }
        @Override
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
        @Override
        public String toString() {
            return key.toString() + "=" + value;
        }
    }



    private static class Tag extends ChainedAllowance {
        private final List<Attr> attributes = new ArrayList<Attr>();
        private boolean removeBody = false;

        public Tag(Allowance... wrapped) {
            super();
            add(wrapped);
        }
        public List<Attr> getAttributes() {
            return attributes;
        }

        public Tag setRemoveBody(boolean b) {
            removeBody = b;
            return this;
        }
        public boolean removeBody() {
            return removeBody;
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
        @Override
        public String toString() {
            return super.toString() + "(" + attributes + ")";
        }
    }

    static class TagCheck {
        final boolean allowed;
        final Tag tag;
        TagCheck(boolean a, Tag t) {
            allowed = a;
            tag = t;
        }


    }
    static enum State {
        DEFAULT,
        SCRIPT,
        ERROR;
    }

    protected static class TagStripper extends HTMLEditorKit.ParserCallback {
        private final Writer out;
        private final List<Tag> tags;
        boolean addImplied = false;
        boolean addBrs     = false;
        boolean escapeAmps = false;
        boolean addNewlines = false;
        List<HTML.Tag> impliedTags = new ArrayList<HTML.Tag>();
        List<HTML.Tag> stack       = new ArrayList<HTML.Tag>();
        int removeBody = 0;
        State state = State.DEFAULT;
        StringBuilder spaceBuffer = new StringBuilder();
        int wrote = 0;

        TagStripper(Writer out, List<Tag> t) {
            this.out = out;
            tags = t;
        }

        @Override
        public String toString() {
            return "" + tags + (addBrs ? "(replacing newlines)" : "");
        }


        TagCheck allowed(String tagName) {
            //System.out.print("Checking wheter 'tagName' allowed");
            for (Tag tag : tags) {
                //System.out.println("using " + tag);
                Allows a = tag.allows(tagName);
                switch (a) {
                case YES: {
                    return new TagCheck(true, tag);
                }
                case NO: {
                    return new TagCheck(false, tag);
                }
                }
            }
            return new TagCheck(false, null);
        }

        @Override
        public void handleText(char[] text, int position) {
            try {
                //System.out.println("Handling " + new String(text) + " for " + position + " " + stack);
                if (removeBody != 0) {
                    return;
                }
                if (state == State.SCRIPT) {
                    // sigh, the parser is pretty incomprehenisible
                    // It give a very odd handleText event after a script tag.
                    state = State.DEFAULT;
                    return;
                }
                space();
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
                    out.write(t); wrote+= t.length();

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
                        out.write(t); wrote += t.length();
                    } else {
                        // no need to wrap in string first.
                        if (text[0] == '>') {
                            out.write(text, 1, text.length - 1);
                            wrote += text.length - 2;
                        } else {
                            out.write(text);
                            wrote += text.length;
                        }
                    }

                }
            } catch (IOException e) {
                log.warn(e);
            }
        }

        TagCheck getTag(HTML.Tag tag, MutableAttributeSet attributes) {
            //System.out.println("getting tag " + tag);
            boolean implied = attributes.containsAttribute(IMPLIED, Boolean.TRUE);
            TagCheck t;
            if (! addImplied && implied) {
                t = new TagCheck(false, null);
                impliedTags.add(tag);
            } else {
                t = allowed(tag.toString());
            }
            return t;

        }
        void handleAttributes(Tag t, MutableAttributeSet attributes) throws IOException {
            //System.out.println("handling attributes for " + t + " " + attributes);
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
                String att = "" + attName;
                String val = "" + value;
                if (t.allowsAttribute(att, val)) {
                    out.write(' ');
                    out.write(att);
                    out.write('=');
                    out.write('"');
                    wrote +=3;
                    wrote += att.length();
                    String s = val;
                    if (escapeAmps) {
                        // HTMLEditorKit translates all Iso1 entities to unicode.
                        // Escape remaining amps, to produce valid Xml.
                        s = s.replaceAll("&", "&amp;");
                    }
                    s = s.replaceAll("\"", "&quot;");
                    out.write(s);
                    wrote += s.length();
                    out.write('"'); wrote++;
                }
            }
        }
        protected void space() throws IOException {
            out.write(spaceBuffer.toString());
            wrote += spaceBuffer.length();
            spaceBuffer.setLength(0);
        }

        @Override
        public void handleStartTag(HTML.Tag tag, MutableAttributeSet attributes, int position) {
            // System.out.println("Start tag " + tag + " for " + position);
            try {
                stack.add(0, tag);
                TagCheck t = getTag(tag, attributes);
                if (tag == HTML.Tag.SCRIPT) {
                    state = State.SCRIPT;
                }
                if (t.tag != null && t.tag.removeBody()) removeBody++;
                if (removeBody == 0) {
                    if (t.allowed) {
                        space();
                        out.write('<'); wrote++;
                        String ts = tag.toString();
                        out.write(ts);
                        wrote += ts.length();
                        handleAttributes(t.tag, attributes);
                        out.write('>'); wrote++;
                    } else {
                        if (tag == HTML.Tag.P && addNewlines) {
                        } else {
                            if (wrote > 0) {
                                spaceBuffer.append(' ');
                            }
                        }
                    }
                }


            } catch (IOException e) {
                log.warn(e);
            }
        }

        @Override
        public void handleEndTag(HTML.Tag tag, int position) {
            //System.out.println("End tag " + tag + " at " + position);
            stack.remove(0);
            try {
                String tagName = tag.toString();
                TagCheck t;
                boolean implied = impliedTags.contains(tag);
                if (! addImplied && implied) {
                    t = new TagCheck(false, null);
                } else {
                    t = allowed(tagName);
                }

                if (removeBody == 0) {
                    if (t.allowed) {
                        out.write("</"); wrote +=2;
                        out.write(tagName); wrote += tagName.length();
                        out.write('>'); wrote++;
                    } else {
                        if (tag == HTML.Tag.P && addNewlines) {
                            spaceBuffer.append("\n\n");
                        }
                    }
                }
                if (t.tag != null && t.tag.removeBody()) removeBody--;
            } catch (IOException e) {
                log.warn(e);
            }
        }
        @Override
        public void handleSimpleTag(HTML.Tag tag, MutableAttributeSet attributes, int position) {
            //stack.remove(0);
            //System.out.println("SIMPLE TAG " + tag);
            try {
                String tagName = tag.toString();
                TagCheck t = getTag(tag, attributes);
                if (removeBody == 0) {
                    if (t.allowed) {
                        out.write('<'); wrote++;
                        out.write(tagName); wrote += tagName.length();
                        handleAttributes(t.tag, attributes);
                        out.write(" />"); wrote +=3;
                    } else {
                        if (tag == HTML.Tag.BR && addNewlines) {
                            spaceBuffer.append('\n');
                        } else {
                            if (tag.breaksFlow()) {
                                spaceBuffer.append(' ');
                            }
                        }
                    }
                }
            } catch (IOException e) {
                log.warn(e);
            }

        }
        @Override
        public void handleError(String mes, int position) {
            //System.out.println("Error " + mes + " at " + position);
            log.debug(mes + " at " + position);
            state = State.ERROR;
        }

        @Override
        public void handleComment(char[] data, int pos) {
            //System.out.println("Comment at " + pos + " for " + new String(data));
            try {
                if (removeBody == 0) {
                    out.write("<!-- " + new String(data) + " -->");
                }
            } catch (IOException e) {
                log.warn(e);
            }
	}


        @Override
	public void handleEndOfLineString(String eol) {
            //System.out.println("EOL " + eol);
	}
        @Override
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
        @Override
        public HTMLEditorKit.Parser getParser(){
            return super.getParser();
        }
    }

    // event attribute can contain javascript, so those are forbidden when doing XSS-stripping.
    static final Attr EVENTS = new Attr(new PatternDisallowance("(?i)onclick|ondblclick|onmousedown|onmousemove|onmouseout|onmouseover|onmouseup|onload|onunload|onchange|onsubmit|onreset|onselect|onblur|onfocus|onkeydown|onkeyup|onkeypress"));

    // only strip cross-site-scripting
    final static List<Tag> XSS = new ArrayList<Tag>();
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
        XSS.add(new Tag(new PatternDisallowance("(?i)script|embed|object|frameset|iframe")).setRemoveBody(true));

        {
            // all other tags are permitted
            Tag all = new Tag(ALLOW_ALL);
            // but not those event attributes
            all.getAttributes().add(EVENTS);
            XSS.add(all);
        }
    }

    // strip all tags
    final  static List<Tag> NONE = new ArrayList<Tag>();
    static {
        NONE.add(new Tag(DISALLOW_ALL));
    }



}
