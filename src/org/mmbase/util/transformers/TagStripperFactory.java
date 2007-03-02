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
 * XML tag stripper. This utility class can be used to strip unwanted tags from
 * a String containing XML. The tags allowed in the output are declared to the
 * HTMLTagStripper by calling addTag. Per tag, it is possible to declare which
 * attributes are allowed for the tag. It is also possible to simply accept
 * all attributes for a tag.

 * Based on code (com.quantiq.q.util.html.HTMLTagStripper) of Doug Tedd.
 *
 * Tag names and attribute names are checked on a case insensitive basis.
 *
 * http://javafaq.nu/java-example-code-618.html
 * @author Michiel Meeuwissen
 * @version $Id: TagStripperFactory.java,v 1.1 2007-03-02 17:31:53 michiel Exp $
 */
public class TagStripperFactory implements ParameterizedTransformerFactory  {

    private static final Logger log = Logging.getLoggerInstance(TagStripperFactory.class);

    protected static final Parameter[] PARAMS = new Parameter[] {
        new Parameter<String>("tags", String.class, "") // allowed tags, default no tags are permitted.
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

        ParserGetter kit = new ParserGetter();
        final HTMLEditorKit.Parser parser = kit.getParser();
        return new ReaderTransformer() {
            public Writer transform(Reader r, Writer w) {
                final HTMLEditorKit.ParserCallback callback = new TagStripper(w);
                try {
                    parser.parse(r, callback, true);
                } catch (Exception e) {
                    log.warn(e);
                }
                return w;
            }
        };
    }

    private static final class Allows {
        private Allows() {
        }
    }
    static final Allows YES = new Allows();
    static final Allows NO  = new Allows();
    static final Allows DONTKNOW  = new Allows();
    
    private static abstract class Allowance {
        Allows allows(String p) {
            return DONTKNOW;
        }
    }

    private static final Allowance ALLOW_ALL = new Allowance() { Allows allows(String p) { return YES; }};
    private static final Allowance DISALLOW_ALL = new Allowance() { Allows allows(String p) { return NO; }};

    private static class PatternAllowance extends Allowance {
        private final Pattern pattern;
        PatternAllowance(Pattern p) {
            pattern = p;
        }
        Allows allows (String p) {
            if (pattern.matcher(p).matches()) return YES;
            return DONTKNOW;
        }
    }
    private static class PatternDisAllowance extends Allowance {
        private final Pattern pattern;
        PatternDisAllowance(Pattern p) {
            pattern = p;
        }
        Allows allows (String p) {
            if (pattern.matcher(p).matches()) return NO;
            return DONTKNOW;
        }
    }
    private static class ChainedAllowance extends Allowance {
        private final List allowances = new ArrayList();
        
        void add(Allowance a) {
            allowances.add(a);
        }
        Allows allows(String p) {
            Iterator i = allowances.iterator();
            while (i.hasNext()) {
                Allowance a = (Allowance) i.next();
                Allows allows = a.allows(p);
                if (allows != DONTKNOW) return allows;
            }
            return DONTKNOW;
        }
    }

    
    static class AllowedAttribute {
        final Pattern key;
        final Pattern value;
        AllowedAttribute(Pattern k, Pattern v) {
            key = k; value = v;
        }
        public boolean allows(String k, String v) {
            boolean keyAllowed = key.matcher(k).matches();
            if (! keyAllowed) return false;
            if (value == null) return true;
            return value.matcher(v).matches();
        }
        
    }
    public static class DisallowedAttribute extends AllowedAttribute {
        DisallowedAttribute(Pattern k, Pattern v) {
            super(k, v);
        }
        public boolean allows(String k, String v) {
            return ! super.allows(k, v);
        }
    }
    
    public static class AssociatedAllowance extends Allowance {
        final Allowance wrapped;
        final Allowance associate;
        public AssociatedAllowance(Allowance wrapped, Allowance associate) {
            this.wrapped = wrapped; this.associate = associate;
        }
        Allowance getAssociate() {
            return associate;
        }
        Allows allows(String p) {
            return wrapped.allows(p);
        }
    }

    public static class Attr extends AssociatedAllowance  {
        public Attr(Allowance wrapped) {
            super(wrapped, ALLOW_ALL);
        }
        public Attr(Allowance wrapped, Allowance values) {
            super(wrapped, values);
        }
    }


    public static class Tag extends AssociatedAllowance {
        public Tag(Allowance wrapped) {
            super(wrapped, new Attr(ALLOW_ALL));
        }
        public Tag(Allowance wrapped, Attr attributes) {
            super(wrapped, attributes);
        }
        public Attr getAttr() {
            return (Attr) getAssociate();
        }
    }

    

    public static class TagStripper extends HTMLEditorKit.ParserCallback {
        private final Writer out;
        private final List allowedTags = new ArrayList();
        boolean addImplied = false;
        List impliedTags = new ArrayList();
        //private final List disallowedTags = new ArrayList();

        public TagStripper(Writer out) {
            this.out = out;
            allowedTags.add(new Tag(new PatternDisAllowance(Pattern.compile("font"))));
            allowedTags.add(new Tag(new PatternAllowance(Pattern.compile(".*"))));
        }
        public Tag allowed(String tagName) {
            Iterator i = allowedTags.iterator();
            while (i.hasNext()) {
                Tag tag = (Tag) i.next();
                Allows a = tag.allows(tagName);
                if (a == YES) {
                    return tag;
                } else if (a == NO) {
                    return null;
                }
            }
            return null;
        }

        public void handleText(char[] text, int position) {
            try {
                out.write(text);
                out.write("\n");
                out.flush();
            }
            catch (IOException e) {
                log.warn(e);
            }
        }
        
        
        public void handleStartTag(HTML.Tag tag, MutableAttributeSet attributes,
                                   int position) {
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
                        Attr atr = t.getAttr();
                        if (atr.allows("" + attName) != NO) {
                            Object value = attributes.getAttribute(attName);
                            AttributeSet set = attributes;
                            while (value == null && set.getResolveParent() != null) {
                                set = set.getResolveParent();
                                value = set.getAttribute(attName);
                            }
                            if (value != null && atr.getAssociate().allows("" + value) != NO) {
                                if (! (value instanceof String)) {
                                    log.debug("CLASSSS " + value.getClass());
                                }
                                out.write(' ');
                                out.write("" + attName);
                                out.write('=');
                                out.write('"');
                                out.write("" + value);
                                out.write('"');
                            }
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
        public void handleSimpleTag(HTML.Tag tag, MutableAttributeSet attributes,
                                    int position) {
            
            System.out.println("SIMPLE TAG " + tag);
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

    public static class ParserGetter extends HTMLEditorKit {
        // purely to make this method public
        public HTMLEditorKit.Parser getParser(){
            return super.getParser();
        }  
    }


    public static void main(String[] args) {
        ParameterizedTransformerFactory factory = new TagStripperFactory();
        Parameters params = factory.createParameters();
        CharTransformer transformer = (CharTransformer) factory.createTransformer(params);

//        String source = "<p style=\"nanana\">allow this <b>but not this</b></p>";
//        String source = "<p style=nanana/>";
//        String source = "<p style=\"nanana\">text</p>";
        String source = "< P sTyle=\"nanana\">\n<br><table WIDTH=\"45\" height=99 border='1' fONt=bold styLe=\"n\\\"one\">\n</table></p>";
        System.out.println("Source      = "+source);
        String dest = transformer.transform(source);
        System.out.println("Destination = "+dest);
    }

}