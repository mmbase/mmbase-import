/*
 * xmlbs
 *
 * Copyright (C) 2002  R.W. van 't Veer
 *
 * This program is free software; you can redistribute it and/or
 * modify it under the terms of the GNU General Public License as
 * published by the Free Software Foundation; either version 2 of the
 * License, or (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful, but
 * WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU
 * General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program; if not, write to the Free Software
 * Foundation, Inc., 59 Temple Place - Suite 330, Boston,
 * MA 02111-1307, USA.
 */

package xmlbs;

import java.io.*;
import java.util.*;

/**
 * XML body shop tries to correct broken XML files.  XMLBS is
 * able to fix to following problems:
 * <UL>
 *   <LI>unquoted tag attributes</LI>
 *   <LI>stray illegal characters like &lt; and &gt;</LI>
 *   <LI>close unclosed tags trying to obey structure rules</LI>
 *   <LI>fix tag overlap like <TT>&lt;i&gt; foo &lt;b&gt; bar &lt;/i&gt; boo &lt;/b&gt;</TT></LI>
 * </UL>
 *
 * @author R.W. van 't Veer
 * @version $Revision: 1.1 $
 */
public class XMLBS {

    /** input */
    private InputStream in = null;
    /** input */
    private String inStr = null;
    /** document structure */
    private DocumentStructure ds = null;
    /** annotate flag */
    private boolean annotate = false;
    /** Charset encoding of InputStream */
    private String encoding = null;

    /** token list */
    private List tokens = null;

    /** marker used for annotation */
    private static final String WARNING_MARKER = "XMLBS!";

    /** processed flag, set to true when processing finished */
    private boolean processed = false;

    /**
     * Construct a body shop instances for stream with struction
     * descriptor.
     * @param in input stream
     * @param ds document structure descriptor
     * @throws IOException when reading from stream failed
     */
    public XMLBS(InputStream in, DocumentStructure ds) throws IOException {
        this(in, ds, null);
    }

    /**
     * Construct a body shop instances for stream with struction
     * descriptor.
     * @param in input stream
     * @param ds document structure descriptor
     * @param encoding Charset encoding
     * @throws IOException when reading from stream failed
     */
    public XMLBS(InputStream in, DocumentStructure ds, String encoding) throws IOException {
        this.in = in;
        this.ds = ds;
        this.encoding = encoding;
    }

    /**
     * Construct a body shop instances for stream with struction
     * descriptor.
     * @param in input stream
     * @param ds document structure descriptor
     * @throws IOException when reading from stream failed
     */
    public XMLBS(String in, DocumentStructure ds) throws IOException {
        this.inStr = in;
        this.ds = ds;
    }

    /**
     * Read and restructure data.
     * @throws IOException when reading from stream failed
     */
    public void process() throws IOException {
        // read tokens from stream
        tokenize();

        // remove unknown tags and unknown tag attributes
        cleanupTags();

        // reconstruct hierarchy
        hierarchy();

        // merge adjoined text tokens
        mergeAdjoinedText();

        // cleanup empty tags
        cleanEmptyTags();

        // remove unknown entities
        // TODO

        processed = true;
    }

    /**
     * Write result data to stream.
     * @param out output stream
     * @throws IOException when writing to stream fails
     * @throws IllegalStateException when data not yet
     * <a href="#process()">processed</a>.
     */
    public void write(OutputStream out)
        throws IOException, IllegalStateException {
        if (!processed) {
            throw new IllegalStateException();
        }

        for (Iterator it = tokens.iterator(); it.hasNext();) {
            Token tok = (Token) it.next();
            out.write(tok.toString().getBytes());
        }
        out.flush();
    }

    /**
     * @return true when annotation is configurated for this
     * processor
     */
    public boolean getAnnotate() {
        return annotate;
    }

    /**
     * @param flag turn annotation on (<TT>true</TT>) or off
     */
    public void setAnnotate(boolean flag) {
        annotate = flag;
    }

    /**
     * @return String
     */
    public String getEncoding() {
        return encoding;
    }

    /**
     * Sets the Charset encoding.
     * @param encoding The encoding to set
     */
    public void setEncoding(String encoding) {
        this.encoding = encoding;
    }



    // private stuff
    /**
     * Tokenize input stream.
     * @throws IOException when reading from stream failed
     */
    private void tokenize() throws IOException {
        Tokenizer tok = null;        
        
        if (in != null) {
            Reader reader = null;
            if (encoding != null) {
                reader =
                    new BufferedReader(new InputStreamReader(in, encoding));
            }
            else {
                reader = new BufferedReader(new InputStreamReader(in));
            }

            tok = new Tokenizer(reader, ds);
        }
        else {
            tok = new Tokenizer(inStr, ds);
        }
        tokens = tok.readAllTokens();
    }

    /**
     * Remove unknown tags and unknown tag attributes.
     */
    private void cleanupTags() {
        for (ListIterator it = tokens.listIterator(); it.hasNext();) {
            Token tok = (Token) it.next();
            if (tok instanceof TagToken) {
                TagToken tag = (TagToken) tok;
                if (!ds.isKnownTag(tag)) {
                    if (annotate) {
                        it.set(comment("unknow tag", tag));
                    }
                    else {
                        it.remove();
                    }
                }
                else {
                    ds.retainKnownAttributes(tag);
                }
            }
        }
    }

    /**
     * Verify and restructure tag hierarchy.
     */
    private void hierarchy() {
        CrumbTrail trail = new CrumbTrail(ds);
        for (int i = 0; i < tokens.size(); i++) {
            Token tok = (Token) tokens.get(i);
            TagToken top = trail.getTop();

            if (tok instanceof TextToken) {
                TextToken txt = (TextToken) tok;
                if (!txt.isWhiteSpace() && !ds.canContain(top, txt)) {
                    // handle stray text
                    if (!trail.hasContainerFor(txt)) {
                        // misplaced text
                        if (annotate) {
                            tokens.set(i, comment("misplaced text", txt));
                        }
                        else {
                            tokens.remove(i--);
                        }
                    }
                    else {
                        // add close tags till top will have us
                        do {
                            if (annotate) {
                                tokens.add(i++, comment("close first", top));
                            }
                            tokens.add(i++, top.closeTag());
                            trail.pop();
                            top = trail.getTop();
                        }
                        while (!ds.canContain(top, txt)
                            && trail.getDepth() > 0);
                    }
                }
            }
            else if (tok instanceof TagToken) {
                TagToken tag = (TagToken) tok;
                if (tag.isOpenTag()) {
                    if (!ds.canContain(top, tag)) {
                        if (!trail.hasContainerFor(tag)) {
                            // misplaced tag
                            if (annotate) {
                                tokens.set(i, comment("misplaced tag", tag));
                            }
                            else {
                                tokens.remove(i--);
                            }
                        }
                        else {
                            // add close tags till top will have us
                            do {
                                if (annotate) {
                                    tokens.add(
                                        i++,
                                        comment("close first", top));
                                }
                                tokens.add(i++, top.closeTag());
                                trail.pop();
                                top = trail.getTop();
                            }
                            while (!ds.canContain(top, tag)
                                && trail.getDepth() > 0);

                            // new top
                            trail.push(tag);
                        }
                    }
                    else {
                        // new top
                        trail.push(tag);
                    }
                }
                else if (tag.isCloseTag()) {
                    if (!trail.hasOpenFor(tag)) {
                        // remove stray close tag in root
                        if (annotate) {
                            tokens.set(i, comment("remove close", tag));
                        }
                        else {
                            tokens.remove(i--);
                        }
                    }
                    else if (!tag.isSameTag(top)) {
                        if (trail.getDepth() > 0) {
                            // add close tags till top same tag
                            do {
                                if (annotate) {
                                    tokens.add(i++, comment("close also", top));
                                }
                                tokens.add(i++, top.closeTag());
                                trail.pop();
                                top = trail.getTop();
                            }
                            while (!tag.isSameTag(top) && trail.getDepth() > 0);

                            // keep close tag and remove top
                            trail.pop();
                        }
                        else {
                            // stray close
                            if (annotate) {
                                tokens.set(i, comment("stray close", tag));
                            }
                            else {
                                tokens.remove(i--);
                            }
                        }
                    }
                    else {
                        // keep close tag and remove top
                        trail.pop();
                    }
                }
            }
        }

        // close tags left on trail
        for (TagToken tag = trail.pop(); tag != null; tag = trail.pop()) {
            tokens.add(tag.closeTag());
        }
    }

    /**
     * Merge adjoined text blocks.
     */
    private void mergeAdjoinedText() {
        Token last = null;
        for (Iterator it = tokens.iterator(); it.hasNext();) {
            Token tok = (Token) it.next();
            if (tok instanceof TextToken && last instanceof TextToken) {
                it.remove();
                TextToken txt = (TextToken) tok;
                TextToken ltxt = (TextToken) last;
                ltxt.setData(ltxt.getData() + " " + txt.getData());
            }
            else {
                last = tok;
            }
        }
    }

    /**
     * Merge adjoined text blocks.
     */
    private void cleanEmptyTags() {
        TagToken last = null;
        int lastPos = -1;
        for (int i = 0; i < tokens.size(); i++) {
            Token tok = (Token) tokens.get(i);
            if (tok instanceof TagToken) {
                TagToken tag = (TagToken) tok;
                if (tag.isOpenTag()) {
                    last = tag;
                    lastPos = i;
                }
                else if (
                    tag.isCloseTag() && last != null && tag.isSameTag(last)) {
                    // see if what's between last and this is whitespace
                    boolean allWhite = true;
                    List l = tokens.subList(lastPos + 1, i);
                    for (Iterator it = l.iterator(); it.hasNext();) {
                        Token t = (Token) it.next();
                        if (t instanceof CommentToken) {
                            continue;
                        }
                        if (t instanceof TextToken
                            && ((TextToken) t).isWhiteSpace()) {
                            continue;
                        }
                        allWhite = false;
                        break;
                    }
                    if (allWhite) {
                        // remove close tag
                        tokens.remove(i);
                        // replace open by empty
                        tokens.set(lastPos, last.emptyTag());
                        // move current position
                        i = lastPos;
                        // forget open tag
                        lastPos = -1;
                        last = null;
                    }
                }
            }
        }
    }

    /**
     * Create comment token for annotation.
     * @param msg message
     * @param tok token to include
     * @return comment token for annotation
     */
    private static CommentToken comment(String msg, Token tok) {
        return new CommentToken(WARNING_MARKER + "(" + msg + ")" + tok);
    }

    /**
     * Crumb trail into document holds parents, grantparent etc.
     */
    class CrumbTrail {
        /** actual trail */
        private List trail = new Vector();
        /** document structure */
        private DocumentStructure ds = null;

        /**
         * @param ds document structure
         */
        public CrumbTrail(DocumentStructure ds) {
            this.ds = ds;
        }

        /**
         * @return current parent tag
         */
        public TagToken getTop() {
            return (TagToken) (trail.size() == 0 ? null : trail.get(0));
        }

        /**
         * @param tag parent of next generation
         */
        public void push(TagToken tag) {
            trail.add(0, tag);
        }

        /**
         * Drop generation.
         * @return last generation
         */
        public TagToken pop() {
            return (TagToken) (trail.size() == 0 ? null : trail.remove(0));
        }

        /**
         * @return number of generations
         */
        public int getDepth() {
            return trail.size();
        }

        /**
         * @param tag close tag
         * @return true if any parent open tag of given close tag
         */
        public boolean hasOpenFor(TagToken tag) {
            for (Iterator it = trail.iterator(); it.hasNext();) {
                TagToken t = (TagToken) it.next();
                if (t.isSameTag(tag)) {
                    return true;
                }
            }
            return false;
        }

        /**
         * @param tok token to find container for
         * @return true if any parent can contain given token
         */
        public boolean hasContainerFor(Token tok) {
            for (Iterator it = trail.iterator(); it.hasNext();) {
                TagToken t = (TagToken) it.next();
                if (ds.canContain(t, tok)) {
                    return true;
                }
            }
            return false;
        }
    }
    
}
