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
 * XML body shop tries to correct broken XML files. XMLBS is able to fix to
 * following problems:
 * <UL>
 * <LI>unquoted tag attributes</LI>
 * <LI>stray illegal characters like &lt; and &gt;</LI>
 * <LI>close unclosed tags trying to obey structure rules</LI>
 * <LI>fix tag overlap like
 * <TT>&lt;i&gt; foo &lt;b&gt; bar &lt;/i&gt; boo &lt;/b&gt;</TT></LI>
 * </UL>
 * 
 * @author R.W. van 't Veer
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

   /** annotate flag */
   private boolean removeEmptyTags = false;

   /** token list */
   private List<Token> tokens = null;

   /** marker used for annotation */
   private static final String WARNING_MARKER = "XMLBS!";

   /** processed flag, set to true when processing finished */
   private boolean processed = false;


   /**
    * Construct a body shop instances for stream with struction descriptor.
    * 
    * @param in
    *           input stream
    * @param ds
    *           document structure descriptor
    */
   public XMLBS(InputStream in, DocumentStructure ds) {
      this(in, ds, null);
   }


   /**
    * Construct a body shop instances for stream with struction descriptor.
    * 
    * @param in
    *           input stream
    * @param ds
    *           document structure descriptor
    * @param encoding
    *           Charset encoding
    */
   public XMLBS(InputStream in, DocumentStructure ds, String encoding) {
      this.in = in;
      this.ds = ds;
      this.encoding = encoding;
   }


   /**
    * Construct a body shop instances for stream with struction descriptor.
    * 
    * @param in
    *           input stream
    * @param ds
    *           document structure descriptor
    */
   public XMLBS(String in, DocumentStructure ds) {
      this.inStr = in;
      this.ds = ds;
   }


   /**
    * Read and restructure data.
    * 
    * @throws IOException
    *            when reading from stream failed
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
    * 
    * @param out
    *           output stream
    * @throws IOException
    *            when writing to stream fails
    * @throws IllegalStateException
    *            when data not yet <a href="#process()">processed</a>.
    */
   public void write(OutputStream out) throws IOException, IllegalStateException {
      if (!processed) {
         throw new IllegalStateException();
      }

      for (Token tok : tokens) {
         out.write(tok.toString().getBytes());
      }
      out.flush();
   }


   /**
    * @return true when annotation is configurated for this processor
    */
   public boolean getAnnotate() {
      return annotate;
   }


   /**
    * @param flag
    *           turn annotation on (<TT>true</TT>) or off
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
    * 
    * @param encoding
    *           The encoding to set
    */
   public void setEncoding(String encoding) {
      this.encoding = encoding;
   }


   public void setRemoveEmptyTags(boolean removeEmptyTags) {
      this.removeEmptyTags = removeEmptyTags;
   }


   // private stuff
   /**
    * Tokenize input stream.
    * 
    * @throws IOException
    *            when reading from stream failed
    */
   private void tokenize() throws IOException {
      Tokenizer tok = null;

      if (in != null) {
         Reader reader = null;
         if (encoding != null) {
            reader = new BufferedReader(new InputStreamReader(in, encoding));
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
      for (ListIterator<Token> it = tokens.listIterator(); it.hasNext();) {
         Token tok = it.next();
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
         Token tok = tokens.get(i);
         TagToken top = trail.getTop();

         if (tok instanceof TextToken) {
            TextToken txt = (TextToken) tok;
            if (txt.isWhiteSpace()) {
               tokens.remove(i--);
            }
            else {
               if (!ds.canContain(top, txt)) {
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
                     } while (!ds.canContain(top, txt) && trail.getDepth() > 0);
                  }
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
                           tokens.add(i++, comment("close first", top));
                        }
                        tokens.add(i++, top.closeTag());
                        trail.pop();
                        top = trail.getTop();
                     } while (!ds.canContain(top, tag) && trail.getDepth() > 0);

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
                     } while (!tag.isSameTag(top) && trail.getDepth() > 0);

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
      for (Iterator<Token> it = tokens.iterator(); it.hasNext();) {
         Token tok = it.next();
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
         Token tok = tokens.get(i);
         if (tok instanceof TagToken) {
            TagToken tag = (TagToken) tok;
            if (removeEmptyTags && tag.isEmptyTag() && ds.canContainText(tag) && tag.getAttributes().isEmpty()) {
               boolean removeTag = false;
               Token prevtok = tokens.get(i - 1);
               if (prevtok instanceof TagToken) {
                  TagToken prevtag = (TagToken) prevtok;
                  if (prevtag.isCloseTag() || prevtag.isEmptyTag()
                        || (prevtag.isOpenTag() && ds.canContainText(prevtag))) {
                     removeTag = true;
                  }
               }
               if (prevtok instanceof TextToken) {
                  removeTag = true;
               }
               if (removeTag) {
                  tokens.remove(i);
               }
            }
            else {
               if (tag.isOpenTag()) {
                  last = tag;
                  lastPos = i;
               }
               else {
                  if (tag.isCloseTag() && last != null && tag.isSameTag(last)) {
                     // see if what's between last and this is whitespace
                     boolean allWhite = true;
                     List<Token> l = tokens.subList(lastPos + 1, i);
                     for (Token t : l) {
                        if (t instanceof CommentToken) {
                           continue;
                        }
                        if (t instanceof TextToken && ((TextToken) t).isWhiteSpace()) {
                           continue;
                        }
                        allWhite = false;
                        break;
                     }
                     if (allWhite) {
                        // remove close tag
                        tokens.remove(i);
                        if (removeEmptyTags && ds.canContainText(last) && last.getAttributes().isEmpty()) {
                           boolean removeTag = false;
                           Token prevtok = tokens.get(lastPos - 1);
                           if (prevtok instanceof TagToken) {
                              TagToken prevtag = (TagToken) prevtok;
                              if (prevtag.isCloseTag() || prevtag.isEmptyTag()
                                    || (prevtag.isOpenTag() && ds.canContainText(prevtag))) {
                                 removeTag = true;
                              }
                           }
                           if (prevtok instanceof TextToken) {
                              removeTag = true;
                           }
                           if (removeTag) {
                              tokens.remove(lastPos);
                              i = lastPos - 1;
                           }
                           else {
                              // replace open by empty
                              tokens.set(lastPos, last.emptyTag());
                              // move current position
                              i = lastPos;
                           }
                        }
                        else {
                           // replace open by empty
                           tokens.set(lastPos, last.emptyTag());
                           // move current position
                           i = lastPos;
                        }
                        // forget open tag
                        lastPos = -1;
                        last = null;
                     }
                  }
               }
            }
         }
      }
   }


   /**
    * Create comment token for annotation.
    * 
    * @param msg
    *           message
    * @param tok
    *           token to include
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
      private List<TagToken> trail = new ArrayList<TagToken>();
      /** document structure */
      private DocumentStructure structure = null;


      /**
       * @param structure
       *           document structure
       */
      public CrumbTrail(DocumentStructure structure) {
         this.structure = structure;
      }


      /**
       * @return current parent tag
       */
      public TagToken getTop() {
         return (trail.size() == 0 ? null : trail.get(0));
      }


      /**
       * @param tag
       *           parent of next generation
       */
      public void push(TagToken tag) {
         trail.add(0, tag);
      }


      /**
       * Drop generation.
       * 
       * @return last generation
       */
      public TagToken pop() {
         return (trail.size() == 0 ? null : trail.remove(0));
      }


      /**
       * @return number of generations
       */
      public int getDepth() {
         return trail.size();
      }


      /**
       * @param tag
       *           close tag
       * @return true if any parent open tag of given close tag
       */
      public boolean hasOpenFor(TagToken tag) {
         for (TagToken t : trail) {
            if (t.isSameTag(tag)) {
               return true;
            }
         }
         return false;
      }


      /**
       * @param tok
       *           token to find container for
       * @return true if any parent can contain given token
       */
      public boolean hasContainerFor(Token tok) {
         for (TagToken t : trail) {
            if (structure.canContain(t, tok)) {
               return true;
            }
         }
         return false;
      }
   }

}
