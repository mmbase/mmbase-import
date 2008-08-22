/**
 *  RELOAD TOOLS
 *
 *  Copyright (c) 2003 Oleg Liber, Bill Olivier, Phillip Beauvoir
 *
 *  Permission is hereby granted, free of charge, to any person obtaining a copy
 *  of this software and associated documentation files (the "Software"), to deal
 *  in the Software without restriction, including without limitation the rights
 *  to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 *  copies of the Software, and to permit persons to whom the Software is
 *  furnished to do so, subject to the following conditions:
 *
 *  The above copyright notice and this permission notice shall be included in
 *  all copies or substantial portions of the Software.
 *
 *  THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 *  IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 *  FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 *  AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 *  LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 *  OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE SOFTWARE.
 *
 *  Project Management Contact:
 *
 *  Oleg Liber
 *  Bolton Institute of Higher Education
 *  Deane Road
 *  Bolton BL3 5AB
 *  UK
 *
 *  e-mail:   o.liber@bolton.ac.uk
 *
 *
 *  Technical Contact:
 *
 *  Phillip Beauvoir
 *  e-mail:   p.beauvoir@bolton.ac.uk
 *
 *  Web:      http://www.reload.ac.uk
 *
 */

package uk.ac.reload.moonunit.metadata;

import uk.ac.reload.jdom.XMLDocument;

/**
 * Core IMS Metadata Methods and Functionality (none at the moment)
 *
 * @author Phillip Beauvoir
 * @version $Id: MD_Core.java,v 1.2 2005-08-29 00:06:51 azemskov Exp $
 */
public class MD_Core {

    public static final String ROOT_NAME = "lom";

    public static final String OLD_ROOT_NAME = "record";

    // Elements
    public static final String ANNOTATION = "annotation";
    public static final String CLASSIFICATION = "classification";
    public static final String DESCRIPTION = "description";
    public static final String EDUCATIONAL = "educational";
    public static final String GENERAL = "general";
    public static final String KEYWORD = "keyword";
    public static final String LANGSTRING = "langstring";
    public static final String LANG = "lang";
    public static final String LIFECYCLE = "lifecycle";
    public static final String METAMETADATA = "metametadata";
    public static final String RELATION = "relation";
    public static final String RIGHTS = "rights";
    public static final String TECHNICAL = "technical";
    public static final String TITLE = "title";

    public static final String SOURCE = "source";
    public static final String VALUE = "value";
    public static final String PURPOSE = "purpose";
    public static final String TAXON_PATH = "taxonpath";
    public static final String TAXON = "taxon";

    /**
     * The JDOM Document that forms the Metadata document that we shall be working on
     */
    private XMLDocument _doc;

    /**
     * Constructor
     */
    public MD_Core(XMLDocument doc) {
        _doc = doc;
    }

}