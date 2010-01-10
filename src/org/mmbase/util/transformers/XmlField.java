package org.mmbase.util.transformers;

import java.io.*;
import java.util.HashMap;
import java.util.Map;

import javax.xml.transform.stream.StreamSource;
import javax.xml.transform.stream.StreamResult;

import java.util.regex.*;

import org.mmbase.util.StringObject;
import org.mmbase.util.ResourceLoader;
import org.mmbase.util.XSLTransformer;

import org.mmbase.util.logging.Logger;
import org.mmbase.util.logging.Logging;

/**
 * XMLFields in MMBase. This class can encode such a field to several other formats.
 *
 * @author Michiel Meeuwissen
 * @version $Id$
 * @todo   THIS CLASS NEEDS A CONCEPT! It gets a bit messy.
 */

public class XmlField extends ConfigurableStringTransformer implements CharTransformer {

    private static final Logger log = Logging.getLoggerInstance(XmlField.class);

    // can be decoded:
    public final static int POORBODY = 5;
    public final static int RICHBODY = 6;

    // cannot yet be encoded even..
    public final static int HTML_INLINE = 7;
    public final static int HTML_BLOCK = 8;
    public final static int HTML_BLOCK_BR  = 9;
    public final static int HTML_BLOCK_NOSURROUNDINGP = 10;
    public final static int HTML_BLOCK_BR_NOSURROUNDINGP = 11;
    public final static int HTML_BLOCK_LIST = 12;
    public final static int HTML_BLOCK_LIST_BR = 13;
    public final static int HTML_BLOCK_LIST_NOSURROUNDINGP = 14;
    public final static int HTML_BLOCK_LIST_BR_NOSURROUNDINGP = 15;

    // cannot be decoded:
    public final static int ASCII = 51;
    public final static int XHTML = 52;

    private final static String CODING = "UTF-8"; // This class only support UTF-8 now.



    private static boolean isListChar(char c) {
        return c == '-' || c == '*';
    }
    private static String listTag(char c) {
        return c == '-' ? "ul" : "ol";
    }

    /**
     * Takes a string object, finds list structures and changes those to XML
     */
    static void handleList(StringObject obj) {
        String result = ListParser.transform(obj.toString());
        obj.setLength(0);
        obj.append(result);
    }

    /**
     * This is the original implementation of {@link #handleList}, but withouth suppport lists in
     * lists (MMB-1658). Code pretty much incomprehensible as it is, so didn't add support for this
     * here, but redid it in ListParser.jj.
     */
    private static void handleListLegacy(StringObject obj) {
        // handle lists
        // make <ul> possible (not yet nested), with -'s on the first char of line.
        int inList = 0; //
        int pos = 0;
        if (obj.length() < 3) {
            return;
        }
        char listChar = '-';
        if (isListChar(obj.charAt(0)) && obj.charAt(1) == ' ' && !isListChar(obj.charAt(2))) { // hoo, we even _start_ with a list;
            listChar = obj.charAt(0);
            obj.insert(0, "\n"); // in the loop \n- is deleted, so it must be there.
        } else {
            while (true) {
                int pos1 = obj.indexOf("\n- ", pos); // search the first
                int pos2 = obj.indexOf("\n* ", pos); // search the first

                pos = (pos1 > 0 && pos1 < pos2) || pos2 < 0 ? pos1 : pos2;
                if (pos == -1 || obj.length() <= pos + 3) break;
                if (! isListChar(obj.charAt(pos + 3))) {
                    listChar = obj.charAt(pos + 1);
                    break;
                }
                pos += 3;
            }
        }

        listwhile : while (pos != -1) {
            if (inList == 0) { // not yet in list
                inList++; // now we are
                obj.delete(pos, 2); // delete \n-
                // remove spaces..
                while (pos < obj.length() && obj.charAt(pos) == ' ') {
                    obj.delete(pos, 1);
                }
                if (pos > 0) {
                    // make sure lists start on a new line, which is essnetial for the correct
                    // working of 'place lists inside/outside p'.
                    obj.insert(pos, "\n");
                    pos += 1;
                }
                obj.insert(pos, "<" + listTag(listChar) + "><li>"); // insert 9 chars.
                pos += 8;

            } else { // already in list
                if (! (obj.length() > pos + 2 && obj.charAt(pos + 1) == listChar && obj.charAt(pos + 2) == ' ')) { // end of list
                    obj.delete(pos, 1); // delete \n
                    obj.insert(pos, "</li></" + listTag(listChar) + ">\n");
                    pos += 11;
                    inList--;
                } else { // not yet end
                    obj.delete(pos, 2); // delete \n-
                    // remove spaces..
                    while (pos < obj.length() && obj.charAt(pos) == ' ') {
                        obj.delete(pos, 1);
                    }
                    obj.insert(pos, "</li><li>");
                    pos += 9;
                }
            }
            if (inList > 0) { // search for new line
                pos = obj.indexOf("\n", pos);
                if (pos == -1)
                    break; // no new line found? End of list, of text.
                if (pos + 1 == obj.length()) {
                    obj.delete(pos, 1);
                    break; // if end of text, simply remove the newline.
                }
                while (obj.charAt(pos + 1) == ' ') {
                    // if next line starts with space, this new line does not count. This makes it possible to have some formatting in a <li>
                    pos = obj.indexOf("\n", pos + 1);
                    if (pos + 1 == obj.length()) {
                        obj.delete(pos, 1);
                        break listwhile; // nothing to do...
                    }
                }
            } else { // search for next item
                while (true) {
                    int pos1 = obj.indexOf("\n- ", pos);
                    int pos2 = obj.indexOf("\n* ", pos);
                    pos = (pos1 > 0 && pos1 < pos2) || pos2 < 0 ? pos1 : pos2;
                    if (pos == -1 || obj.length() <= pos + 3) break;
                    if (! isListChar(obj.charAt(pos + 3))) {
                        listChar = obj.charAt(pos + 1);
                        break; // should not start with two -'s, because this is some seperation line
                    }
                    pos += 3;
                }
            }
        }
        // make sure that the list is closed:
        while (inList > 0) { // lists in lists not already supported, but if we will...
            obj.insert(obj.length(), "</li></" + listTag(listChar) + ">");
            inList--; // always finish with a new line, it might be needed for the finding of paragraphs.
        }

    }

    /**
     * If you want to add a _ in your text, that should be possible too...
     * Should be done last, because no tags can appear in <em>

     * @param ch This is '_' or e.g. '*'
     * @param tag The tag to produce, e.g. "em" or "strong"
     */
    // test cases:
    // I cite _m_pos_! -> <mmxf><p>I cite <em>m_pos</em>!</p></mmxf>

    static void handleEmph(StringObject obj, char ch, String tag) {

        obj.replace("" + ch + ch, "&#95;"); // makes it possible to escape underscores (or what you choose)

        // Emphasizing. This is perhaps also asking for trouble, because
        // people will try to use it like <font> or other evil
        // things. But basicly emphasizion is content, isn't it?

        String sch = "" + ch;

        int posEmphOpen = obj.indexOf(sch, 0);
        int posTagOpen = obj.indexOf("<", 0); // must be closed before next tag opens.


        OUTER:
        while (posEmphOpen != -1) {

            if (posTagOpen > 0 &&
                posTagOpen < posEmphOpen) { // ensure that we are not inside existing tags
                int posTagClose = obj.indexOf(">", posTagOpen);
                if (posTagClose == -1) break;
                posEmphOpen = obj.indexOf(sch, posTagClose);
                posTagOpen  = obj.indexOf("<", posTagClose);
                continue;
            }

            if (posEmphOpen + 1 >= obj.length()) break; // no use, nothing can follow

            if ((posEmphOpen > 0 && Character.isLetterOrDigit(obj.charAt(posEmphOpen - 1))) ||
                (! Character.isLetterOrDigit(obj.charAt(posEmphOpen + 1)))) {
                // _ is inside a word, ignore that.
                // or not starting a word
                posEmphOpen = obj.indexOf(sch, posEmphOpen + 1);
                continue;
            }

            // now find closing _.
            int posEmphClose = obj.indexOf(sch, posEmphOpen + 1);
            if (posEmphClose == -1) break;
            while((posEmphClose + 1) < obj.length() &&
                  (Character.isLetterOrDigit(obj.charAt(posEmphClose + 1)))
                  ) {
                posEmphClose = obj.indexOf(sch, posEmphClose + 1);
                if (posEmphClose == -1) break OUTER;
            }

            if (posTagOpen > 0
                && posEmphClose > posTagOpen) {
                posEmphOpen = obj.indexOf(sch, posTagOpen); // a tag opened before emphasis close, ignore then too, and re-search
                continue;
            }

            // realy do replacing now
            obj.delete(posEmphClose, 1);
            obj.insert(posEmphClose,"</" + tag + ">");
            obj.delete(posEmphOpen, 1);
            obj.insert(posEmphOpen, "<" + tag + ">");
            posEmphClose += 7;

            posEmphOpen = obj.indexOf(sch, posEmphClose);
            posTagOpen  = obj.indexOf("<", posEmphClose);

        }

        obj.replace("&#95;", sch);
    }


    /**
     * Makes sure that lines indicating headers (starting with $), are followed by at least 2
     * newlines, if followed by some list.
     * @since MMBase-1.8.6
     */
    static void preHandleHeaders(StringObject obj) {

        int pos = (obj.length() > 0 && (obj.charAt(0) == '$')) ? 0 : obj.indexOf("\n$");
        while (pos >= 0) {
            // search newline
            pos++;
            int nextLine = obj.indexOf("\n", pos);
            char firstChar = obj.charAt(nextLine + 1);
            if (isListChar(firstChar)) {
                obj.insert(nextLine, "\n");
                pos++;
            }
            pos = obj.indexOf("\n$", pos);
        }
    }
    /**
     * Some paragraphs are are really \sections. So this handler can
     * be done after handleParagraphs. It will search the paragraphs
     * which are really headers, and changes them. A header, in our
     * 'rich' text format, is a paragraph starting with one or more $.
     * If there are more then one, the resulting <section> tags are
     * going to be nested.
     *
     */
    static void handleHeaders(StringObject obj) {
        // handle headers
        int requested_level;
        char ch;
        int level = 0; // start without being in section.
        int pos = obj.indexOf("<p>$", 0);
        OUTER:
        while (pos != -1) {
            obj.delete(pos, 4); // remove <p>$

            requested_level = 1;
            // find requested level:
            while (true) {
                ch = obj.charAt(pos);
                if (ch == '$') {
                    requested_level++;
                    obj.delete(pos, 1);
                } else {
                    if (ch == ' ') {
                        obj.delete(pos, 1);
                    }
                    break;
                }
            }
            StringBuffer add = new StringBuffer();
            for (; requested_level <= level; level--) {
                // same or higher level section
                add.append("</section>");
            }
            level++;
            for (; requested_level > level; level++) {
                add.append("<section>");
            }
            add.append("<section><h>");

            obj.insert(pos, add.toString());
            pos += add.length();

            // search end title of  header;

            while (true) { // oh yes, and don't allow _ in title.
                int pos1 = obj.indexOf("_", pos);
                int posP  = obj.indexOf("</p>", pos);
                int posNl = obj.indexOf("\n", pos);
                int delete;
                int  pos2;
                if ((posP > 0 && posP < posNl) || posNl == -1) {
                    pos2 =  posP;
                    delete = 4;
                } else {
                    pos2 = posNl;
                    delete = 1;
                }
                if (pos1 < pos2 && pos1 > 0) {
                    obj.delete(pos1, 1);
                } else {
                    pos = pos2;
                    if (pos == -1) {
                        break OUTER; // not found, could not happen.
                    }
                    obj.delete(pos, delete);
                    obj.insert(pos, "</h>");
                    pos += 4;
                    if (delete == 1) {
                        obj.insert(pos, "<p>");
                        pos += 3;
                    }
                    break;
                }
            }
            pos = obj.indexOf("<p>$", pos); // search the next one.
        }
        // ready, close all sections still open.
        for (; level > 0; level--) {
            obj.insert(obj.length(), "</section>");
        }

    }

    // check if on that position the string object contains a <ul> or <ol>
    static private boolean containsListTag(StringObject obj, int pos) {
        return obj.length() > pos + 4 &&
               obj.charAt(pos) == '<' &&
               (obj.charAt(pos+1) == 'u' || obj.charAt(pos+1) == 'o') &&
               obj.charAt(pos+2) == 'l' &&
               obj.charAt(pos+3) == '>';
    }

    /**
     * Make <p> </p> tags.
     * @param leaveExtraNewLines (defaults to false) if false, 2 or more newlines starts a new p. If true, every 2 newlines starts new p, and every extra new line simply stays (inside the p).
     * @param surroundingP (defaults to true) wether the surrounding &lt;p&gt; should be included too.
     */
    static void handleParagraphs(StringObject obj, boolean leaveExtraNewLines, boolean surroundingP) {
        handleParagraphs(obj, leaveExtraNewLines, surroundingP, false);
    }

    /**
     * Make &lt;p> &lt;/p> tags.
     * Note that if placeListsInsideP is <code>false</code>, the code generated with lists becomes akin to:
     * &lt;p&gt;...&lt;/p&gt;&lt;ul&gt;...&lt;/ul&gt;&lt;p&gt;...&lt;/p&gt;
     *
     * If placeListsInsideP is <code>true</code>, the code becomes:
     * &lt;p&gt;...&lt;ul&gt;...&lt;/ul&gt;...&lt;/p&gt;
     *
     * If there is no content in front of the first list, or after the last list, those paragraphs are empty and may not be
     * added.
     *
     * @param leaveExtraNewLines (defaults to false) if false, 2 or more newlines starts a new p. If true, every 2 newlines starts new p, and every extra new line simply stays (inside the p).
     * @param surroundingP (defaults to true) wether the surrounding &lt;p&gt; should be included too.
     * @param placeListsInsideP (defaults to false) wether a list should be placed inside a &lt;p&gt; (as allowed by xhtml2).
     */
    static void handleParagraphs(StringObject obj, boolean leaveExtraNewLines, boolean surroundingP, boolean placeListsInsideP) {

        log.debug(placeListsInsideP ? "placings lists INSIDE" : "placings lists OUTSIDE");
        // handle paragraphs:
        boolean inParagraph = true;
        int pos = 0;
        // we should actually test if the first bit is a list, and if so, skip it
        if (surroundingP) {
            if (!placeListsInsideP && containsListTag(obj, pos)) {
                //note: this does not take into account nested lists
                int posEnd = obj.indexOf("</" + obj.charAt(pos + 1)+ "l>", pos + 1);
                // only continue this if this is a balanced list
                if (posEnd != -1) {
                    pos = posEnd +5;
                    if (obj.length() > pos && obj.charAt(pos) == '\n') {
                        obj.delete(pos, 1);
                    }
                    if (pos >= obj.length()) {
                        return;
                    }
                }
            }
            obj.insert(pos, "<p>");
            pos += 3;
        } else {
            // if the code starts with a list, and it should be placed outside a paragraph,
            // add a \n to make sure that the list is parsed
            if (!placeListsInsideP && containsListTag(obj,pos)) {
                obj.insert(pos, "\n\n");
            }
        }
        boolean start = true;
        while (pos < obj.length()) {
            // one or more empty lines.
            if (start) {
                start = false;
                pos = obj.indexOf("\n", pos);
            } else {
                pos = obj.indexOf("\n", pos + 1);
            }
            if (pos == -1) break;

            int skip = 1;
            int l = obj.length();
            while(pos + skip < l && Character.isWhitespace(obj.charAt(pos + skip))) {
                if (obj.charAt(pos + skip ) == '\n') {
                    break;
                }
                skip++;
            }
            if (pos + skip >= l) break;
            // we need at least 2 lines for a paragraph.
            // however, if we instead have a list now, and we are not placeListsInsideP,
            // we should still terminate the paragraph, as the ul then falls outside
            // the paragraph.
            if (obj.charAt(pos + skip) != '\n') {
                if (!containsListTag(obj, pos + skip)) {
                    continue;
                }
                obj.delete(pos, skip);
                if (placeListsInsideP) {
                    int posEnd = obj.indexOf("</" + obj.charAt(pos + 1)+ "l>", pos + 1);
                    if (posEnd != -1) {
                        pos = posEnd + 5;
                        if (obj.length() > pos && obj.charAt(pos) == '\n' &&
                            (obj.length() == pos + 1 || obj.charAt(pos + 1) != '\n')) {
                            obj.delete(pos, 1);
                            continue;
                        } else {
                            if (obj.length() > pos + 2) {
                                obj.delete(pos, 2);
                            } else {
                                if (obj.length() > pos + 1) {
                                    obj.delete(pos, 1);
                                }
                                continue;
                            }
                        }
                    }

                }
            } else {
                // delete the 2 new lines of the p.
                obj.delete(pos, skip + 1);
            }

            if (leaveExtraNewLines) {
                while (obj.length() > pos && Character.isWhitespace(obj.charAt(pos))) {
                    pos++;
                }
            } else {
                while (obj.length() > pos && Character.isWhitespace(obj.charAt(pos))) {
                    obj.delete(pos, 1); // delete the extra new lines too
                }
            }
            if (inParagraph) { // close the previous paragraph.
                obj.insert(pos, "</p>");
                inParagraph = false;
                pos += 4;
            }
            // initialize skip for leading whitespace
            skip = 0;
            // if the next code happens to be a list tag (ul/ol), we can do two things:
            // - place the list outside the paragraph (if we are not placeListsInsideP).
            //   In that case, we should not start a new
            //   paragraph until after the list. Moreover, if we are then at the end of the
            //   text we should not include a paragraph at all unless it is enforced.
            // - include de ul in the paragraph. In that case, we simply continue as normal
            if (!placeListsInsideP && obj.length() > pos && containsListTag(obj,pos)) {
                int posEnd = obj.indexOf("</" + obj.charAt(pos + 1)+ "l>", pos + 1);
                // only continue this if this is a balanced list
                if (posEnd != -1) {
                    pos = posEnd + 5;
                    // skip all whitespace after a list.
                    int newlines = 0;
                    while (obj.length() > (pos + skip) && Character.isWhitespace(obj.charAt(pos + skip))) {
                        if (obj.charAt(pos + skip ) == '\n') {
                            newlines++;
                        }
                        if (newlines > 1 && leaveExtraNewLines) {
                            skip++; // count whitespace after the second newline,
                                    // to include in the next paragraph
                        } else {
                            obj.delete(pos, 1); // delete whitespace
                        }
                    }
                    // if no text follows, and we don't need an extra paragraphs, skip
                    // note that we always add a <p> if we have the 'ommitsurrounding' option
                    // - because the option expects this.
                    if (surroundingP && pos == obj.length()) {
                        break;
                    }
                }
            }
            // next paragraph.
            obj.insert(pos, "<p>");
            pos += skip + 3;
            inParagraph = true;
        }
        if (inParagraph) { // in current impl. this is always true

            // read whole text, but stil in paragraph
            // if text ends with newline, take it away, because it then means </p> rather then <br />
            if (obj.length() > 0) {
                if (obj.charAt(obj.length() - 1) == '\n') {
                    obj.delete(obj.length() - 1, 1);
                }
            }
            if (surroundingP) {
                obj.insert(obj.length(), "</p>");
            }
        }
    }

    /**
     * Wikipedia syntax for tables. (simplified)
     * <pre>
     * {|
     * | a || b || c
     * |-
     * | d || e || f
     * |}
     * </pre>
     * or e.g.
     * <pre>
     * {|-
     * |+ caption
     * ! A !! B !! C
     * |-
     * | d
     * | e
     * | f
     * |}
     * </pre>
     *@since MMBase 1.8
     */
    static void handleTables(StringObject obj) {
        int tables = 0;
        int pos = 0;
        while (pos != -1) {
            // always at beginning of line when here.
            int l = obj.length();
            if (pos + 2 < l && ( obj.charAt(pos) == '{' && obj.charAt(pos + 1) == '|')) {
                int skip = 2;
                // allow for starting with {|- as well
                if (pos + skip < l && obj.charAt(pos + skip) == '-') skip++;
                // allow some trailing whitespace
                while(pos + skip < l && Character.isWhitespace(obj.charAt(pos + skip))) {
                    if (obj.charAt(pos + skip ) == '\n') {
                        break;
                    }
                    skip++;
                }
                if (pos + skip >= l) break;
                if (obj.charAt(pos + skip) != '\n') {
                    pos = obj.indexOf("\n", pos + skip);
                    continue;
                }
                skip ++;
                log.debug("ok, this is a table!");
                // don't use l onwards, length of obj will change

                if (pos > 0 && obj.charAt(pos - 1) == '\n') {
                    obj.delete(pos - 1, 1);
                    pos --;
                }
                if (pos > 0 && obj.charAt(pos - 1) == '\n') {
                    obj.delete(pos - 1, 1);
                    pos --;
                }
                tables ++;
                obj.delete(pos, skip);
                obj.insert(pos, "</p><table>");
                pos += 11;
                if (obj.charAt(pos) == '|' && obj.charAt(pos + 1) == '+') {
                    obj.delete(pos, 2);
                    obj.insert(pos, "<caption>");
                    pos += 9;
                    pos = obj.indexOf("\n", pos);
                    obj.delete(pos, 1);
                    obj.insert(pos, "</caption>");
                    pos += 10;
                }
                obj.insert(pos, "<tr>");
                pos += 4;
            }
            if (pos >= obj.length()) break;
            // always in tr here.
            if (tables > 0) {
                if (obj.charAt(pos) == '|') {
                    obj.delete(pos, 1);

                    if (pos + 2 < obj.length() && (obj.charAt(pos) == '-' && obj.charAt(pos + 1) == '\n')) {
                        obj.delete(pos, 2);
                        obj.insert(pos, "</tr><tr>");
                        pos += 9;
                    } else if (pos + 1 < obj.length() && (obj.charAt(pos) == '}' && (pos + 2 == obj.length() || obj.charAt(pos + 1) == '\n'))) {
                        obj.delete(pos, 2);
                        obj.insert(pos, "</tr></table>");
                        tables--;
                        pos += 13;
                        if (tables == 0) {
                            obj.insert(pos, "<p>");
                            pos +=3;
                        }
                        while (pos < obj.length() && obj.charAt(pos) == '\n') obj.delete(pos, 1);
                    } else if (pos + 3 < obj.length() && (obj.charAt(pos) == '\n' && obj.charAt(pos + 1) == '{' && obj.charAt(pos + 2) == '|')) {
                        obj.delete(pos, 3);
                        obj.insert(pos, "<td><table><tr>");
                        pos += 15;
                        tables++;
                    } else {
                        obj.insert(pos, "<td>");
                        pos += 4;
                        int nl = obj.indexOf("\n", pos);
                        int pipe = obj.indexOf("||", pos);
                        int end = pipe == -1 || nl < pipe ? nl : pipe;
                        if (end == -1) end += obj.length();
                        pos = end;
                        obj.delete(pos, 1);
                        obj.insert(pos, "</td>");
                        pos += 5;
                    }
                    continue;
                } else if (obj.charAt(pos) == '!') {
                    obj.delete(pos, 1);
                    obj.insert(pos, "<th>");
                    pos += 4;
                    int nl = obj.indexOf("\n", pos);
                    int pipe = obj.indexOf("!!", pos);
                    int end = pipe == -1 || nl < pipe ? nl : pipe;
                    if (end == -1) end += obj.length();
                    pos = end;
                    obj.delete(pos, 1);
                    obj.insert(pos, "</th>");
                    pos += 5;
                    continue;
                } else {
                    pos = obj.indexOf("\n", pos) + 1;
                    if (pos >= obj.length()) break;
                    // oddd. what to do know?
                }
            } else { // not in table, ignore find next new line
                pos = obj.indexOf("\n", pos) + 1;
                if (pos == 0) break;
                if (pos >= obj.length()) break;
            }
        }
        while (tables > 0) {
            obj.insert(pos, "</tr></table>");
            pos+= 13;
            tables--;
            if (tables == 0) {
                obj.insert(pos, "<p>");
                pos += 3;
                while (pos < obj.length() && obj.charAt(pos) == '\n') obj.delete(pos, 1);
            }
        }

    }
    /**
     * Removes all new lines and space which are too much.
     */
    static void cleanupText(StringObject obj) {
        // remaining new lines have no meaning.
        obj.replace(">\n", ">"); // don't replace by space if it is just after a tag, it could have a meaning then.
        obj.replace("\n", " "); // replace by space, because people could use it as word boundary.
        // remaining double spaces have no meaning as well:
        int pos = obj.indexOf(" ", 0);
        while (pos != -1) {
            pos++;
            while (obj.length() > pos && obj.charAt(pos) == ' ') {
                obj.delete(pos, 1);
            }
            pos = obj.indexOf(" ", pos);
        }
        // we used \r for non significant newlines:
        obj.replace("\r", "");

    }

    /**
     * Only escape, clean up.
     * @since MMBase-1.7
     */
    protected static void handleFormat(StringObject obj, boolean format) {
        if (format) {
            obj.replace("\r", "\n");
        } else {
            cleanupText(obj);
        }

    }
    protected static String prepareDataString(String data) {
        return Xml.XMLEscape(data).replaceAll("\r", ""); // drop returns (\r), we work with newlines, \r will be used as a help.
    }
    protected static StringObject prepareData(String data) {
        return new StringObject(prepareDataString(data));
    }

    protected static void handleRich(StringObject obj, boolean sections, boolean leaveExtraNewLines, boolean surroundingP) {
        handleRich(obj, sections, leaveExtraNewLines, surroundingP, false);
    }

    protected static void handleRich(StringObject obj, boolean sections, boolean leaveExtraNewLines, boolean surroundingP, boolean placeListsInsideP) {
        // the order _is_ important!
        if (sections) {
            preHandleHeaders(obj);
        }
        handleList(obj);
        handleTables(obj);
        handleParagraphs(obj, leaveExtraNewLines, surroundingP, placeListsInsideP);
        if (sections) {
            handleHeaders(obj);
        }
        handleEmph(obj, '_', "em");
        handleEmph(obj, '*', "strong");
    }

    protected static void handleNewlines(StringObject obj) {
        obj.replace("</ul>\n", "</ul>"); // otherwise we will wind up with the silly "</ul><br /> the \n was necessary for </ul></p>
        obj.replace("</ol>\n", "</ol>");
        obj.replace("\n", "<br />");  // handle new remaining newlines.
    }



    /**
     * Defines a kind of 'rich' text format. This is a way to easily
     * type structured text in XML.  The XML tags which can be
     * produced by this are all HTML as well.
     *
     * This is a generalisation of the MMBase html() functions which
     * does similar duties, but hopefully this one is better, and more
     * powerfull too.
     *
     * The following things are recognized:
     * <ul>
     *  <li> Firstly, XMLEscape is called.</li>
     *  <li> A line starting with an asterix (*) will start an unnumberd
     *       list. The first new line not starting with a space or an other
     *       asterix will end the list </li>
     *  <li> Underscores are translated to the emphasize HTML-tag</li>
     *  <li> You can create a header tag by by starting a line with a dollar signs</li>
     *  <li> A paragraph can be begun (and ended) with an empty line.</li>
     * </ul>
     *
     * Test with commandline: java org.mmbase.util.Encode RICH_TEXT (reads from STDIN)
     *
     * @param data text to convert
     * @param format if the resulting XML must be nicely formatted (default: false)
     * @return the converted text
     */

    public static String richToXML(String data, boolean format, boolean placeListsInsideP) {
        StringObject obj = prepareData(data);
        handleRich(obj, true, true, true, placeListsInsideP);
        handleNewlines(obj);
        handleFormat(obj, format);
        return obj.toString();
    }

    public static String richToXML(String data, boolean format) {
        return richToXML(data, format, false);

    }
    public static String richToXML(String data) {
        return richToXML(data, false);
    }
    /**
     * As richToXML but a little less rich. Which means that only one new line is non significant.
     * @see #richToXML
     */
    public static String poorToXML(String data, boolean format, boolean placeListsInsideP) {
        StringObject obj = prepareData(data);
        handleRich(obj, true, false, true, placeListsInsideP);
        handleFormat(obj, format);
        return obj.toString();
    }

    public static String poorToXML(String data, boolean format) {
        return poorToXML(data, format, false);
    }

    public static String poorToXML(String data) {
        return poorToXML(data, false);
    }

    /**
     * So poor, that it actually generates pieces of XHTML 1.1 blocks (so, no use of sections).
     *
     * @see #richToXML
     * @since MMBase-1.7
     */
    public static String richToHTMLBlock(String data, boolean multipibleBrs, boolean surroundingP, boolean placeListsInsideP) {
        StringObject obj = prepareData(data);
        handleRich(obj, false, multipibleBrs, surroundingP, placeListsInsideP);   // no <section> tags, leave newlines if multipble br's requested
        handleNewlines(obj);
        handleFormat(obj, false);
        return obj.toString();
    }


    public static String richToHTMLBlock(String data) {
        return richToHTMLBlock(data, false, true, true);
    }

    public static String richToHTMLBlock(String data, boolean multipibleBrs, boolean surroundingP) {
        return richToHTMLBlock(data, multipibleBrs, surroundingP, true);
    }

    /**
     * So poor, that it actually generates pieces of XHTML 1.1 inlines (so, no use of section, br, p).
     *
     * @since MMBase-1.7
     */
    public static String poorToHTMLInline(String data) {
        StringObject obj = prepareData(data);
        // don't add newlines.
        handleFormat(obj, false);
        handleEmph(obj, '_', "em");
        handleEmph(obj, '*', "strong");
        return obj.toString();
    }


    /**
     * Base function for XSL conversions.
     */

    protected static String XSLTransform(String xslFile, String data) {
        try {
            java.net.URL u = ResourceLoader.getConfigurationRoot().getResource("xslt/" + xslFile);
            java.io.StringWriter res = new java.io.StringWriter();
            XSLTransformer.transform(new StreamSource(new StringReader(data)), u, new StreamResult(res), null);
            return res.toString();
        } catch (javax.xml.transform.TransformerException te) {
            return te.getMessage();
        }
    }

    protected static void validate(String incoming) throws FormatException {
        try {
            if (log.isDebugEnabled()) {
                log.debug("Validating " + incoming);
            }
            javax.xml.parsers.DocumentBuilderFactory dfactory = javax.xml.parsers.DocumentBuilderFactory.newInstance();

            // turn validating on..
            dfactory.setValidating(true);
            dfactory.setNamespaceAware(true);
            javax.xml.parsers.DocumentBuilder documentBuilder = dfactory.newDocumentBuilder();

            // in order to find the dtd.....
            org.mmbase.util.XMLEntityResolver resolver = new org.mmbase.util.XMLEntityResolver();
            documentBuilder.setEntityResolver(resolver);

            // in order to log our xml-errors
            StringBuffer errorBuff = new StringBuffer();
            ErrorHandler errorHandler = new ErrorHandler(errorBuff);
            documentBuilder.setErrorHandler(errorHandler);
            // documentBuilder.init();
            java.io.InputStream input = new java.io.ByteArrayInputStream(incoming.getBytes(CODING));
            documentBuilder.parse(input);

            if (!resolver.hasDTD()) {
                throw new FormatException("no doc-type specified for the xml");
            }
            if (errorHandler.errorOrWarning) {
                throw new FormatException("error in xml: \n" + errorBuff.toString());
            }
        } catch (javax.xml.parsers.ParserConfigurationException pce) {
            throw new FormatException("[sax parser] not well formed xml: " + pce.toString());
        } catch (org.xml.sax.SAXException se) {
            log.debug("", se);
            //throw new FormatException("[sax] not well formed xml: "+se.toString() + "("+se.getMessage()+")");
        } catch (java.io.IOException ioe) {
            throw new FormatException("[io] not well formed xml: " + ioe.toString());
        }
    }

    protected static class FormatException extends java.lang.Exception {
        FormatException(String msg) {
            super(msg);
        }
    }

    // Catch any errors or warnings,....
    static class ErrorHandler implements org.xml.sax.ErrorHandler {
        boolean errorOrWarning;
        StringBuffer errorBuff;

        ErrorHandler(StringBuffer errorBuff) {
            super();
            this.errorBuff = errorBuff;
            errorOrWarning = false;
        }

        // all methods from org.xml.sax.ErrorHandler
        // from org.xml.sax.ErrorHandler
        public void fatalError(org.xml.sax.SAXParseException exc) {
            errorBuff.append("FATAL[" + getLocationString(exc) + "]:" + exc.getMessage() + "\n");
            errorOrWarning = true;
        }

        // from org.xml.sax.ErrorHandler
        public void error(org.xml.sax.SAXParseException exc) {
            errorBuff.append("Error[" + getLocationString(exc) + "]: " + exc.getMessage() + "\n");
            errorOrWarning = true;
        }

        // from org.xml.sax.ErrorHandler
        public void warning(org.xml.sax.SAXParseException exc) {
            errorBuff.append("Warning[" + getLocationString(exc) + "]:" + exc.getMessage() + "\n");
            errorOrWarning = true;
        }

        // helper methods
        /**
         * Returns a string of the location.
         */
        private String getLocationString(org.xml.sax.SAXParseException ex) {
            StringBuffer str = new StringBuffer();
            String systemId = ex.getSystemId();
            if (systemId != null) {
                int index = systemId.lastIndexOf('/');
                if (index != -1) {
                    systemId = systemId.substring(index + 1);
                }
                str.append(systemId);
            }
            str.append(" line:");
            str.append(ex.getLineNumber());
            str.append(" column:");
            str.append(ex.getColumnNumber());
            return str.toString();
        }
    }

    public XmlField() {
        super();
    }
    public XmlField(int to) {
        super(to);
    }

    public Map transformers() {
        Map h = new HashMap();
        h.put("MMXF_ASCII", new Config(XmlField.class, ASCII, "Converts xml to ASCII (cannoted be reversed)"));
        h.put("MMXF_BODY_RICH", new Config(XmlField.class, RICHBODY, "XHTML 2 compliant XML."));
        h.put("MMXF_BODY_POOR", new Config(XmlField.class, POORBODY, "XHTML 2 compliant XML, but withough <br/> tags"));
        h.put("MMXF_HTML_INLINE", new Config(XmlField.class, HTML_INLINE, "Decodes only escaping and with <em>"));
        h.put("MMXF_HTML_BLOCK", new Config(XmlField.class,  HTML_BLOCK, "Decodes only escaping and with <em>, <p>, <br /> (only one) and <ul>"));
        h.put("MMXF_HTML_BLOCK_BR", new Config(XmlField.class,  HTML_BLOCK_BR, "Decodes only escaping and with <em>, <p>, <br /> (also multiples) and <ul>"));
        h.put("MMXF_HTML_BLOCK_NOSURROUNDINGP", new Config(XmlField.class,  HTML_BLOCK_NOSURROUNDINGP, "Decodes only escaping and with <em>, <p>, <br /> (only one) and <ul>"));
        h.put("MMXF_HTML_BLOCK_BR_NOSURROUNDINGP", new Config(XmlField.class,  HTML_BLOCK_BR_NOSURROUNDINGP, "Decodes only escaping and with <em>, <p>, <br /> (also multiples) and <ul>"));
        h.put("MMXF_HTML_BLOCK_LIST", new Config(XmlField.class,  HTML_BLOCK_LIST, "Decodes only escaping and with <em>, <p>, <br /> (only one) and <ul>, with <ul> inside the <p>"));
        h.put("MMXF_HTML_BLOCK_LIST_NOSURROUNDINGP", new Config(XmlField.class,  HTML_BLOCK_LIST_NOSURROUNDINGP, "Decodes only escaping and with <em>, <p>, <br /> (only one) and <ul>, with <ul> inside the <p>"));
        h.put("MMXF_HTML_BLOCK_LIST_BR", new Config(XmlField.class,  HTML_BLOCK_LIST_BR, "Decodes only escaping and with <em>, <p>, <br /> (also multiples) and <ul>, with <ul> inside the <p>"));
        h.put("MMXF_HTML_BLOCK_LIST_BR_NOSURROUNDINGP", new Config(XmlField.class,  HTML_BLOCK_LIST_BR_NOSURROUNDINGP, "Decodes only escaping and with <em>, <p>, <br /> (also multiples) and <ul>, with <ul> inside the <p>"));
        h.put("MMXF_XHTML", new Config(XmlField.class, XHTML, "Converts to piece of XHTML"));
        return h;
    }

    public String transform(String data) {
        switch (to) {
        case RICHBODY :
        case POORBODY :
            throw new UnsupportedOperationException();
            // XXXX
            // needing richtext xslt here.
            //return XSLTransform("mmxf2rich.xslt", XML_TAGSTART + data + XML_TAGEND);
        case ASCII :
            return XSLTransform("text.xslt", data);
        case HTML_BLOCK:
        case HTML_BLOCK_BR:
        case HTML_INLINE:
            throw new UnsupportedOperationException("Cannot transform");
        default :
            throw new UnknownCodingException(getClass(), to);
        }
    }

    public String transformBack(String r) {
        String result = null;
        switch (to) {
        case RICHBODY :
            result = richToXML(r);
            // rich will not be validated... Cannot be used yet!!
            break;
        case POORBODY :
            result = poorToXML(r);
            break;
        case HTML_BLOCK:
            result = richToHTMLBlock(r, false, true, true);
            break;
        case HTML_BLOCK_BR:
            result = richToHTMLBlock(r, true, true, true);
            break;
        case HTML_BLOCK_NOSURROUNDINGP:
            result = richToHTMLBlock(r, false, false, true);
            break;
        case HTML_BLOCK_BR_NOSURROUNDINGP:
            result = richToHTMLBlock(r, true, false, true);
            break;

        case HTML_BLOCK_LIST:
            result = richToHTMLBlock(r, false, true, false);
            break;
        case HTML_BLOCK_LIST_BR:
            result = richToHTMLBlock(r, true, true, false);
            break;
        case HTML_BLOCK_LIST_NOSURROUNDINGP:
            result = richToHTMLBlock(r, false, false, false);
            break;
        case HTML_BLOCK_LIST_BR_NOSURROUNDINGP:
            result = richToHTMLBlock(r, true, false, false);
            break;

        case HTML_INLINE:
            result = poorToHTMLInline(r);
            break;
        case ASCII :
            throw new UnsupportedOperationException("Cannot transform");
        default :
            throw new UnknownCodingException(getClass(), to);
        }
        return result;
    }

    public String getEncoding() {
        switch (to) {
        case RICHBODY :
            return "MMXF_BODY_RICH";
        case POORBODY :
            return "MMXF_BODY_POOR";
        case HTML_BLOCK :
            return "MMXF_HTML_BLOCK";
        case HTML_BLOCK_BR :
            return "MMXF_HTML_BLOCK_BR";
        case HTML_BLOCK_NOSURROUNDINGP :
            return "MMXF_HTML_BLOCK_NOSURROUNDINGP";
        case HTML_BLOCK_BR_NOSURROUNDINGP :
            return "MMXF_HTML_BLOCK_BR_NOSURROUNDINGP";
        case HTML_BLOCK_LIST :
            return "MMXF_HTML_BLOCK_LIST";
        case HTML_BLOCK_LIST_BR :
            return "MMXF_HTML_BLOCK_LIST_BR";
        case HTML_BLOCK_LIST_NOSURROUNDINGP :
            return "MMXF_HTML_BLOCK_LIST_NOSURROUNDINGP";
        case HTML_BLOCK_LIST_BR_NOSURROUNDINGP :
            return "MMXF_HTML_BLOCK_LIST_BR_NOSURROUNDINGP";
        case HTML_INLINE :
            return "MMXF_HTML_INLINE";
        case ASCII :
            return "MMXF_ASCII";
        default :
            throw new UnknownCodingException(getClass(), to);
        }
    }
}
