/*
 *  org.mmbase.util.swing.xml.parse
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
import java.util.Hashtable;
import java.util.Stack;

/** 
 *
 * Based on tinyXML class. Very much simplified. It cannot determin
 * the encoding by itself, but simply accepts a Reader (in stead of an
 * InputStream). This is much more usefull if the XML doesn't come
 * from a file, e.g. when the XML is edited, in an EditorKit.
 *
 * It provided also a stack and a reader, which you highly problably need in your implementation.
 *
 *
 *  @author Tom Gibara
 *  @author Michiel Meeuwissen
 *  @since MMBase-1.6
 *  @see Parser 
 */


abstract public class Responder {
    protected Stack  stack = new Stack();
    protected Reader reader;

    /** This method is called to indicate that the document stream has been opened successfully.
     */

    public void recordDocStart() {
    }


    /** This method is called to indicate that the document stream has been successfully closed.
     */

    public void recordDocEnd() {
    }

    /** This method is called to indicate the start of an element (tag).
     *
     * @param name the name of the element (tag name)
     * @param attr a hashtable containing the explicitly supplied attributes (as strings)
     *
     */

    public void recordElementStart(String name, Hashtable attr) throws ParseException {

    }


    /** This method is called to indicate the closure of an element
     *  (tag).
     *  
     *  @param name the name of the element (tag) being closed
     * */

    public void recordElementEnd(String name) throws ParseException {
    }



    /** This method is called to return character data to the
     *  application.  As per the XML specification, newlines on all
     *  platforms are converted to single 0x0A's.  Contiguous
     *  character data may be returned with successive calls to this
     *  method.
     *
     *  @param charData character data from the document.
     *
     */

    public void recordCharData(String charData) throws ParseException {
    }


    /** This method is called to return comments to the application.
     *  Most applications will have no use for this information.
     *
     * param comment the contents of the comment tag
     *
     */

    public void recordComment(String comment) {
        System.out.println("*Comment: "+comment);
    }





    /** This method is always called exactly once, before all other
     *  callbacks.  It is used to retrieve an inputstream from which
     *  the XML document will be parsed.  This method may
     *  <strong>not</strong> return null.
     *
     *  @return a Reader which supplies the document text.
     * */

    public Reader getDocumentReader() throws ParseException {
        return reader;
    }



}



