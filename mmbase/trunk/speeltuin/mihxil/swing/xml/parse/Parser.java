/*
 *  package org.mmbase.util.swing.xml.parse;
 *
 *
 *  This program is free software; you can redistribute it and/or modify
 *  it under the terms of the GNU General Public License as published by
 *  the Free Software Foundation; either version 2 of the License, or
 *  (at your option) any later version.
 *
 *  This program is distributed in the hope that it will be useful,
 *  but WITHOUT ANY WARRANTY; without even the implied warranty of
 *  MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 *  GNU General Public License for more details.
 *
 *  You should have received a copy of the GNU General Public License
 *  along with this program; if not, write to the Free Software
 *  Foundation, Inc., 59 Temple Place, Suite 330, Boston, MA  02111-1307  USA
 */

package org.mmbase.util.swing.xml.parse;
import java.io.Reader;
import java.io.IOException;
import java.util.Hashtable;



/** 
 * This class was based on the class from tinyXML. It is very much simplified.
 *
 *  After parsing has been completed, the same instance of this class
 *  may be reused to parse another document.
 *
 *  @author Tom Gibara
 *  @author Michiel Meeuwissen
 *  @since MMBase-1.6
 */


public class Parser {

    private static final ParseException EOS = new ParseException("unexpected end of document");

    private Reader    reader;
    private char      cn;
    private Responder xr;


    /** Called to parse an XML document.
     *  Obtains document from supplied xmlResponder
     *  to which document information is also sent.
     *
     *  @param xmlResponder callback interface
     *
     */

    public void parseXML(Responder xmlResponder) throws ParseException {
	xr     = xmlResponder;
	reader = xr.getDocumentReader();
	readDocument();
        reader = null;
	xr     = null;
    }




    //returns true if character is white space
    //as defined by XML spec
    private static boolean isWhite(char c) {
	return (c==32) || (c==9) || (c==13) || (c==10);
    }


    //returns true if this character  may legally
    //appear as the first letter of a name
    //as defined in the spec
    private static boolean isFirstNameChar(char c) {
	return (Character.isLetter(c) || c==':' || c=='_');
    }


    //returns true if this character is a ' or "
    private static boolean isQuote(char c) {
	return (c=='\'' || c=='"');
    }

    //reads a single character from the underlying reader
    //throws an EOS exception if it was there are no more
    private void read() throws ParseException {
	try { cn = (char)reader.read(); }
	catch (IOException e) { throw new ParseException(e.getMessage()); }
	if (cn==(char)-1) { throw EOS; }
    }


    //used to eat up unwanted whitepace
    //terminates at first non-white character
    private void readWhite() throws ParseException {
	while (isWhite(cn)) read();
    }


    //reads a single character reference
    //expects # to have already been read
    //terminates when ; is read
    //this could be made more efficient by replacing startsWith by a stribgbuffer function
    private char readCharacterRef() throws ParseException {
	StringBuffer sb = new StringBuffer();
	read();
	while (cn!=';') {
	    sb.append(cn);
	    read();
	}
	String ref = sb.toString();
	int radix = 10;
	if (ref.startsWith("x")) {
	    ref = ref.substring(1);
	    radix = 16;
	}
	try { return (char)Integer.parseInt(ref,radix); }
	catch (NumberFormatException e) { throw new ParseException("unrecognized character reference"); }
    }



    //reads a quoted string value
    //expands references
    //expects first quote to have been read
    //eats trailing whitespace
    private String readAttrValue() throws ParseException {
	StringBuffer sb = new StringBuffer();
	if (!isQuote(cn))
	    throw new ParseException("unquoted attribute value");
	char term = cn;
	read();
	while (cn!=term) {
	    if (cn=='<') throw new ParseException("unescaped < in attribute value");
	    else { sb.append(cn); }
	    read();
	}
	read();
	readWhite();
	return sb.toString();
    }


    //reads a tag name
    //expects the first letter to have been read
    //eats trailing whitespace
    private String readName() throws ParseException {
	if (!isFirstNameChar(cn))
	    throw new ParseException("name in tag started without letter, : or _");
	StringBuffer sb = new StringBuffer();
	do {
	    sb.append(cn);
	    read();
	}
	while (Character.isLetterOrDigit(cn) || cn=='.' || cn=='-' || cn=='_' || cn==':');
	readWhite();
	return sb.toString();
    }


    //reads tag attributes
    //eats trailing whitespace
    //expects first letter to have been read
    //whether or not attributes exist
    private Hashtable readAttributes() throws ParseException {
	Hashtable ht = null;
	String a;
	String v;
	while (isFirstNameChar(cn)) {
	    a = readName();
	    if (cn!='=') throw new ParseException("expected = after attribute name");
	    read();
	    readWhite();
	    v = readAttrValue();
	    if (ht==null) ht = new Hashtable();
	    ht.put(a,v);
	}
	return ht;
    }



    //reads XML comments or (with -)
    //reads CDATA sections  (with ])
    //expects first letter of content read
    private void readCommentOrCDATA(char term) throws ParseException {
	int l = 0;
	StringBuffer sb = new StringBuffer();
	while (cn!='>' || l<2 || sb.charAt(l-2)!=term || sb.charAt(l-1)!=term) {
	    if (term=='-' && l>=2 && sb.charAt(l-2)=='-' && sb.charAt(l-1)=='-')
		throw new ParseException("-- found in comment");
	    sb.append(cn);
	    l++;
	    read();
	}
	sb.setLength(l-2);
	String s = sb.toString();
	if (term=='-') { xr.recordComment(s); }
	else { xr.recordCharData(s); }
    }




    //used to read element content dec. and enumerated type dec.
    //boolean parameter indicates whether the expression contains regexp stuff
    //eats trailing whitespace
    private String readParens(boolean regexp) throws ParseException {
	if (cn!='(') throw new ParseException("( expected");
	int bc=0;
	StringBuffer sb = new StringBuffer();
	do {
	    if (cn=='(') bc++;
	    if (cn==')') bc--;
	    sb.append(cn);
	    read();
	} while (bc!=0);
	if (regexp) {
	    if (cn=='?' || cn=='+' || cn=='*') {
		sb.append(cn);
		read();
	    }
	}
	readWhite();
	return sb.toString();
    }

    private void readNotationTag() throws ParseException {
	String name = readName();
	if (cn!='>') throw new ParseException("expected tag close");
	//System.out.println("** GOT NOTATION NAME: "+name+" PID: "+ids[0]+" SID: "+ids[1]+" **");
    }




    //expects first to have been read
    private String readChars(int count) throws ParseException {
	StringBuffer sb = new StringBuffer();
	while (sb.length()<count) {
	    sb.append(cn);
	    read();
	}
	return sb.toString();
    }
    

    //reads <! > tags
    //expects ! to have been read
    private void readBangTag(boolean pro) throws ParseException {
	String name;
	read();
	if (cn=='-') name = readChars(2);
	else if (cn=='[') name = readChars(7);
	else name = readChars(7);

	if (name.equals("--")) {
	    //comments okay anywhere
	    readCommentOrCDATA('-');
	}
	else if (name.equals("[CDATA[")) {
	    //no CDATA's in the prolog please
	    if (pro) throw new ParseException("CDATA found in prolog");
	    else readCommentOrCDATA(']');
	}
	else throw new ParseException("unrecognized <! > tag");
    }



    //reads > or />
    //returns true if the later was read
    private boolean readTagClose() throws ParseException {
	if (cn!='/' && cn!='>') throw new ParseException("expected tag close");
	boolean f = false;
	while (cn!='>') {
	    f = (cn=='/');
	    read();
	}
	return f;
    }


    //reads < > tags
    //expects first letter of name to have been read
    //returns name if it's closed
    private String readTag() throws ParseException {
	String name = readName();
	Hashtable attr = (readAttributes());
	xr.recordElementStart(name,attr);
	return (readTagClose()) ? name : null;
    }


    //reads </ > tags
    private void readClosingTag() throws ParseException {
	read();
	String closeName = readName();
	readWhite();
	if (readTagClose())
	    throw new ParseException("close tag ended with />");

	xr.recordElementEnd(closeName);
    }


    //expects first character of contents to be read
    //does not read beyond the tag-close
    private void readCharData() throws ParseException {
	StringBuffer sb = new StringBuffer();
	while (cn!='<') {	
	    sb.append(cn);
	    read();
	}
	xr.recordCharData(sb.toString());
    }



    private void readDocument() throws ParseException {

	boolean inProlog = true;
	boolean inEpilog = false;
	boolean isEmpty = true;
	int depth = 0;

	xr.recordDocStart();
	read();
	readWhite();
	while(true) {
	    //read in a node
	    if (cn=='<') {
		read();
		switch (cn) {
		case '!' :
		    readBangTag(inProlog);
		    break;

		case '/' :
		    readClosingTag();
		    depth--;
		    break;

		default  :
		    String closeName = readTag();
		    if  (closeName==null) depth++;
		    else xr.recordElementEnd(closeName);
		    if (inEpilog)
			throw new ParseException("element found outside root element");
		    inProlog = false;
		    break;
		}

		if (!inEpilog && !inProlog && depth==0) inEpilog = true;
		try {
		    read();
		    readWhite();
		}
		catch (ParseException e) { if (e==EOS) break; else throw e; }
	    }
	    else {
		readCharData();
		if (inProlog || inEpilog)
		    throw new ParseException("character data outside root element");
	    }


	}
	if (!inEpilog) throw new ParseException("no root element in document");
	xr.recordDocEnd();
    }



}
