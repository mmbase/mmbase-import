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

import java.io.IOException;
import java.io.Reader;
import java.io.StringReader;

import java.util.*;

/**
 * The tokenizer class generates a list of XML Tokens from a stream of
 * characters.
 * 
 * @see xmlbs.Token
 * @author R.W. van ' t Veer
 * @version $Revision: 1.3 $
 */
public class Tokenizer {
   /** token we bumbed into before returning a text token */
   private Token holdBack = null;
   /** stream we are reading from */
   private Reader in = null;
   /** document structure */
   private DocumentStructure ds = null;

   /** buffer size for stream rewind */
   static final int BUFFER_SIZE = 64 * 1024;


   /**
    * Construct tokenizer reading from stream.
    * 
    * @param in
    *           input stream
    * @param ds
    *           document structure
    */
   public Tokenizer(Reader in, DocumentStructure ds) {
      this.in = in;
      this.ds = ds;
   }


   /**
    * Construct tokenizer reading from string.
    * 
    * @param data
    *           string to read
    * @param ds
    *           Document Structure
    */
   public Tokenizer(String data, DocumentStructure ds) {
      this.in = new StringReader(data);
      this.ds = ds;
   }


   /**
    * Read next token from stream.
    * 
    * @return next token
    * @throws IOException
    *            we reading fails
    */
   public Token readToken() throws IOException {
      if (holdBack != null) {
         Token tok = holdBack;
         holdBack = null;
         return tok;
      }

      StringBuffer sb = new StringBuffer();
      for (int c; (c = readChar()) != -1;) {
         if (c == '<') {
            in.mark(BUFFER_SIZE);
            c = readChar();
            Token tok = null;

            if (c == '/' || Character.isLetter((char) c)) { // tag?
               tok = readTagToken((char) c);
            }
            else if (c == '?') { // special?
               tok = readSpecialToken();
            }
            else if (c == '!') { // declaration?
               tok = readDeclToken();
            }

            // succeeded in reading a token?
            if (tok != null) {
               String t = sb.toString();
               if (t.length() > 0) {
                  // hold back token and return text token first
                  holdBack = tok;
                  return new TextToken(t, ds);
               }
               return tok;
            }

            // stray < character
            sb.append('<');
            in.reset();
         }
         else {
            sb.append((char) c);
         }
      }

      String t = sb.toString();
      return (t.length() > 0) ? new TextToken(t, ds) : null;
   }


   /**
    * @return char as an int
    * @throws IOException
    */
   private int readChar() throws IOException {
      return in.read();
   }


   /**
    * Read all tokens from stream.
    * 
    * @return list of read tokens
    * @throws IOException
    *            when reading from stream fails
    */
   public List<Token> readAllTokens() throws IOException {
      List<Token> l = new ArrayList<Token>();
      Token t;
      while ((t = readToken()) != null) {
         l.add(t);
      }
      return l;
   }


   /**
    * Read a tag tokens from stream.
    * 
    * @param firstChar
    *           already read character
    * @return a tag token or <TT>null</TT> when no tag token found
    * @throws IOException
    *            when reading from stream fails
    */
   private Token readTagToken(char firstChar) throws IOException {
      StringBuffer sb = new StringBuffer();
      sb.append(firstChar);
      for (int c; (c = readChar()) != -1;) {
         if (c == '<') {
            // oeps this can't be a tag..
            return null;
         }
         if (c == '>') {
            // tag body read
            break;
         }
         sb.append((char) c);
      }

      String raw = sb.toString();
      return raw.length() > 0 ? new TagToken(raw, ds) : null;
   }


   /**
    * Read a special tokens from stream.
    * 
    * @return a tag token or <TT>null</TT> when no special token found
    */
   private Token readSpecialToken() {
      return null;
   }


   /**
    * Read a declaration tokens from stream. Declaration tokens are all &lt;!
    * tokens like comments and cdata blocks.
    * 
    * @return a declaration token or <TT>null</TT> when no token found
    * @throws IOException
    *            when reading from stream fails
    */
   private Token readDeclToken() throws IOException {
      int c = readChar();

      if (c == '-') { // comment?
         c = readChar();
         if (c != '-') {
            return null;
         }

         // try to find end of comment marker "-->"
         StringBuffer sb = new StringBuffer();
         while ((c = readChar()) != -1) {
            sb.append((char) c);

            if (c == '>' && sb.toString().endsWith("-->")) {
               break;
            }
         }

         // unterminated comment != comment
         if (c == -1) {
            return null;
         }

         String t = sb.toString();
         return new CommentToken(t.substring(0, t.length() - 3));
      }
      else if (c == '[') { // cdata section?
         // match CDATA[
         StringBuffer sb = new StringBuffer();
         for (int i = "CDATA[".length(); i > 0 && (c = readChar()) != -1; i--) {
            sb.append((char) c);
            if (!"CDATA[".startsWith(sb.toString())) {
               return null;
            }
         }

         // read data until ]]>
         sb = new StringBuffer();
         while ((c = readChar()) != -1) {
            sb.append((char) c);

            if (c == '>' && sb.toString().endsWith("]]>")) {
               break;
            }
         }

         // unterminated cdata section != cdata section
         if (c == -1) {
            return null;
         }

         String t = sb.toString();
         return new CDATAToken(t.substring(0, t.length() - 3));
      }

      return null;
   }
}
