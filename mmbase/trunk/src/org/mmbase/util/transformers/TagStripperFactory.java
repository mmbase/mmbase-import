package org.mmbase.util.transformers;

import java.util.*;
import java.util.regex.*;
import javax.swing.text.*;
import javax.swing.text.html.*;
import javax.swing.text.html.parser.*;
import java.io.*;
import java.net.*;
import org.mmbase.util.functions.*;


import org.mmbase.util.logging.Logger;
import org.mmbase.util.logging.Logging;


/**
 *
 * http://javafaq.nu/java-example-code-618.html
 * @author Michiel Meeuwissen
 * @version $Id: TagStripperFactory.java,v 1.3 2007-03-08 14:05:15 michiel Exp $
 */
public class TagStripperFactory implements ParameterizedTransformerFactory  {

    private static final Logger log = Logging.getLoggerInstance(TagStripperFactory.class);

    protected static final Parameter[] PARAMS = new Parameter[] {
        new Parameter<String>("tags", String.class, "NONE"),  // allowed tags, default no tags are permitted.
        new Parameter<Boolean>("addbrs", Boolean.class, Boolean.FALSE)
    };

    public Parameters createParameters() {
        return new Parameters(PARAMS);
    }


    /**
     * Creates a parameterized transformer.
     */
    public Transformer createTransformer(final Parameters parameters) {

        parameters.checkRequiredParameters();
        if (log.isDebugEnabled()) {
            log.debug("Creating transformer, with " + parameters);
        }
        
        final List<Tag> tagList;
        String tags = parameters.getString("tags");
        if (tags.equals("XSS")) {
            tagList = XSS;
        } else {
            tagList = NONE;
        }
        final Boolean addbrs = (Boolean) parameters.get("addbrs");

        ParserGetter kit = new ParserGetter();
        final HTMLEditorKit.Parser parser = kit.getParser();
        ReaderTransformer trans = new ReaderTransformer() {
                public Writer transform(Reader r, Writer w) {
                    final TagStripper callback = new TagStripper(w, tagList);
                    callback.addBrs = addbrs;
                    try {
                        parser.parse(r, callback, true);
                    } catch (Exception e) {
                        log.warn(e);
                    }
                    return w;
                }
                public String toString() {
                    return tagList + " " + (addbrs ? "(adding brs)" : "");
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
        Allows allows(String p) {
            return Allows.DONTKNOW;
        }
    }

    private static final Allowance ALLOW_ALL    = new Allowance() { Allows allows(String p) { return Allows.YES; } public String toString() { return "ALL"; }};
    private static final Allowance DISALLOW_ALL = new Allowance() { Allows allows(String p) { return Allows.NO; } public String toString() { return "NONE"; }};

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
            //System.out.println("Checking " + k + "=" + v + " for " + this);
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
        List<HTML.Tag> impliedTags = new ArrayList<HTML.Tag>();
        public TagStripper(Writer out, List<Tag> t) {
            this.out = out;
            tags = t;
        }

        public String toString() {
            return "" + tags + (addBrs ? "(replacing newlines)" : "");
        }
        public Tag allowed(String tagName) {
            for (Tag tag : tags) {
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
                if (addBrs) {
                    String t = new String(text);
                    log.trace("handling " + t);
                    out.write(t.replaceAll("\n", "<br />"));
                } else {
                    out.write(text);
                    out.write("\n");
                }
                out.flush();
            }
            catch (IOException e) {
                log.warn(e);
            }
        }
        
        
        public void handleStartTag(HTML.Tag tag, MutableAttributeSet attributes, int position) {
            try {
                String tagName = tag.toString();
                Tag t;
                boolean implied = attributes.containsAttribute(IMPLIED, Boolean.TRUE);
                if (! addImplied && implied) {
                    t = null;
                    impliedTags.add(tag);
                } else {
                    t = allowed(tagName);
                }
                if (t != null) {
                    out.write('<');
                    out.write(tag.toString());
                    Enumeration en = attributes.getAttributeNames();
                    while (en.hasMoreElements()) {
                        Object attName =  en.nextElement();
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
                            out.write(("" + value).replaceAll("\"", "&quot;"));
                            out.write('"');
                        }
                    }
                    out.write('>');
                } else {
                    out.write(' ');
                }
            }
            catch (IOException e) {
                log.warn(e);
            }
        }
        
        public void handleEmptyTag(HTML.Tag tag) {
            System.out.println("EMPTY " + tag);
        }
        public void handleEndTag(HTML.Tag tag, int position) {
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
            }
            catch (IOException e) {
                log.warn(e);
            }
        }
        public void handleSimpleTag(HTML.Tag tag, MutableAttributeSet attributes, int position) {
            
            //System.out.println("SIMPLE TAG " + tag);
            try {
                String tagName = tag.toString();
                if (allowed(tagName) != null) {
                    out.write('<');
                    out.write(tagName);
                    out.write(" />");
                } else {
                    out.write(' ');
                }
            }
            catch (IOException e) {
                log.warn(e);
            }
            
        }
        
        public void flush() {
            try {
                
                out.flush();
                out.close();
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

    protected static final Attr EVENTS = new Attr(new PatternDisallowance("(?i)onclick|ondblclick|onmousedown|onmousemove|onmouseout|onmouseover|onmouseup|onload|onunload|onchange|onsubmit|onreset|onselect|onblur|onfocus|onkeydown|onkeyup|onkeypress"));

    // only strip cross-site-scripting
    public final static List<Tag> XSS = new ArrayList<Tag>();
    static {
        {
            Tag a = new Tag(new PatternAllowance("(?i)a"));
            a.getAttributes().add(new Attr(new PatternAllowance("(?i)href"), new PatternDisallowance("(?i)javascript:.*")));
            a.getAttributes().add(EVENTS);
            XSS.add(a);
        }            
        XSS.add(new Tag(new PatternDisallowance("(?i)script|embed|object|frameset"))); 

        {
            Tag all = new Tag(ALLOW_ALL);
            all.getAttributes().add(EVENTS);
            XSS.add(all);
        }
    }

    // strip all tags
    public final  static List<Tag> NONE = new ArrayList<Tag>();
    static {
        NONE.add(new Tag(DISALLOW_ALL));
    }



    public static void main(String[] args) {
        ParameterizedTransformerFactory factory = new TagStripperFactory();
        Parameters params = factory.createParameters();
        params.set("tags", "NONE");
        CharTransformer transformer = (CharTransformer) factory.createTransformer(params);

//        String source = "<p style=\"nanana\">allow this <b>but not this</b></p>";
//        String source = "<p style=nanana/>";
//        String source = "<p style=\"nanana\">text</p>";
        String source = "<P sTyle=\"nanana\">hoi hoi\n<br><table WIDTH=\"45\" height=99 border='1\"' fONt=bold styLe=\"n\\\"one\">\nbla bla bla</table></p>";
        System.out.println("Source      = "+source);
        String dest = transformer.transform(source);
        System.out.println("Destination = "+dest);
    }

}
