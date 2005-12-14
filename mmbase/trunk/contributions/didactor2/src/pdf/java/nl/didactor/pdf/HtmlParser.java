package nl.didactor.pdf;



import java.io.IOException;

import java.io.InputStream;

import java.io.Reader;



import org.xml.sax.InputSource;

import org.xml.sax.SAXException;



import com.lowagie.text.ExceptionConverter;

import com.lowagie.text.DocListener;

import com.lowagie.text.xml.XmlParser;

import com.lowagie.text.markup.MarkupTags;



import com.lowagie.text.html.*;

import com.lowagie.text.ElementTags;





/**

 * This class can be used to parse some HTML files.

 *

 * maps &lt;hr&gt; to new page

 */



public class HtmlParser extends com.lowagie.text.html.HtmlParser {



    public HtmlParser() {

        super();

//        System.err.println("nl.didactor.pdf.HtmlParser created");

    }



// maps hr -> newpage

    public SAXmyHtmlHandler getHtmlHandler( DocListener document ) {

        HtmlTagMap map = new HtmlTagMap();



        HtmlPeer peer = new HtmlPeer(ElementTags.HORIZONTALRULE, HtmlTags.HORIZONTALRULE);

        map.remove(peer.getAlias());





        peer = new HtmlPeer(ElementTags.NEWPAGE, HtmlTags.HORIZONTALRULE);



        map.put(peer.getAlias(), peer);



//        peer = new HtmlPeer(ElementTags.PHRASE, HtmlTags.U);

//        peer.addValue(ElementTags.STYLE, MarkupTags.CSS_UNDERLINE);



//        map.remove(peer.getAlias());



//        peer = new HtmlPeer(ElementTags.PHRASE, HtmlTags.U);

//        peer.addValue(ElementTags.STYLE, MarkupTags.CSS_ITALIC);

//        map.put(peer.getAlias(), peer);





        return new SAXmyHtmlHandler(document,map);

    }



/**

 * Parses a given file.

 * @param document the document the parser will write to

 * @param is the InputSource with the content

 */



    public void go(DocListener document, InputSource is) {

        try {

            parser.parse(is, getHtmlHandler(document));

        }

        catch(SAXException se) {

            throw new ExceptionConverter(se);

        }

        catch(IOException ioe) {

            throw new ExceptionConverter(ioe);

        }

    }





/**

 * Parses a given file.

 * @param document the document the parser will write to

 * @param file the file with the content

 */



    public void go(DocListener document, String file) {

        try {

            parser.parse(file, getHtmlHandler(document));

        }

        catch(SAXException se) {

            throw new ExceptionConverter(se);

        }

        catch(IOException ioe) {

            throw new ExceptionConverter(ioe);

        }

    }





/**

 * Parses a given file.

 * @param document the document the parser will write to

 * @param is the InputStream with the content

 */



    public void go(DocListener document, InputStream is) {

        try {

//            System.err.println("Going to parse...");

            parser.parse(new InputSource(is), getHtmlHandler(document));

        }

        catch(SAXException se) {

            throw new ExceptionConverter(se);

        }

        catch(IOException ioe) {

            throw new ExceptionConverter(ioe);

        }

    }





/**

 * Parses a given file.

 * @param document the document the parser will write to

 * @param is the Reader with the content

 */



    public void go(DocListener document, Reader is) {

        try {

            parser.parse(new InputSource(is), getHtmlHandler(document));

        }

        catch(SAXException se) {

            throw new ExceptionConverter(se);

        }

        catch(IOException ioe) {

            throw new ExceptionConverter(ioe);

        }

    }

}

