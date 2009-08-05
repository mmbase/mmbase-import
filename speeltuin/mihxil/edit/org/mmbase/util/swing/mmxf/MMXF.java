package org.mmbase.util.swing.mmxf;
import java.io.*;
import java.util.HashMap;
import java.util.Map;
import javax.swing.text.AttributeSet;
import javax.swing.text.StyleContext;

import org.mmbase.util.swing.xml.TagType;
import org.mmbase.util.swing.xml.parse.ParseException;

/**
 * Describes the structure elements (tags) of an MMXF document.
 *
 */
public class MMXF {
    
    // --- Tag Names -----------------------------------
    //                                                name       style
    public static final TagType A       = new TagType("a",       "default");
    public static final TagType EM      = new TagType("em",      "emphasize");
    public static final TagType P       = new TagType("p",       "default");
    public static final TagType LI      = new TagType("li",      "default");
    public static final TagType SECTION = new TagType("section", "section");
    public static final TagType NONE    = new TagType("",        "");    
    public static final TagType UL      = new TagType("ul",      "default");
    public static final TagType MMXF    = new TagType("mmxf",    "root");
    
    
    
    static final TagType allTagTypes[]  = {
        A, EM, P, SECTION, LI, UL, MMXF, NONE
    };


    private static final Map tagMap = new HashMap();
    static {
	for (int i = 0; i < allTagTypes.length; i++ ) {
	    tagMap.put(allTagTypes[i].toString(), allTagTypes[i]);
	    StyleContext.registerStaticAttributeKey(allTagTypes[i]);
	}
    }

    /**
     * This is the set of actual tags that
     * are known about the the default reader.
     * This set does not include tags that are
     * manufactured by the reader.
     */
    public static TagType[] getAllTagTypes() {
	TagType[] tags = new TagType[allTagTypes.length];
	System.arraycopy(allTagTypes, 0, tags, 0, allTagTypes.length);
	return tags;
    }

    /**
     * Fetch a tag constant for a well-known tag name (i.e. one of
     * the tags in the set <code>allTagTypes</code>).  If the given
     * name does not represent one of the well-known tags, then
     * null will be returned.
     */
    public static TagType getTagType(String tagName) throws ParseException {
	Object t =  tagMap.get(tagName);
        if (t == null) throw new ParseException("Unknown tagtype " + tagName);
        return (TagType) t;
    }


}
