package nl.didactor.pdf;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.ServletException;
import com.lowagie.text.HeaderFooter;
import com.lowagie.text.Phrase;
import com.lowagie.text.Document;
import com.lowagie.text.Rectangle;
import com.lowagie.text.DocumentException;

import com.lowagie.text.pdf.PdfWriter;
import com.lowagie.text.xml.XmlWriter;

import com.lowagie.text.Chunk;
import com.lowagie.text.Image;
import java.io.*;
import java.net.URL;
import org.w3c.tidy.Tidy;

import org.mmbase.util.logging.Logger;
import org.mmbase.util.logging.Logging;

public class PDFConverter {

    private static Logger log = Logging.getLoggerInstance(PDFConverter.class);

    public static void pageAsPDF (URL url, OutputStream out, URL headerImageURL) 
        throws ServletException, java.io.IOException
    {
        PipedOutputStream pipeout = null;
        PipedInputStream pipein = null;
        InputStream urlStream = null;
        Document pdf = null;
        try {
            pdf = new Document();
//            pdf.compress = false; // debug
            pdf.setMargins(50f,50f,120f,50f);
//            XmlWriter writer = XmlWriter.getInstance(pdf, out);
            PdfWriter writer = PdfWriter.getInstance(pdf, out);
            

            pdf.setFooter(footer());

            if ( headerImageURL != null && header(headerImageURL)!=null ) {
                pdf.setHeader(header(headerImageURL));
            }

            HtmlParser parser = new HtmlParser();
            Tidy tidy = new Tidy();
            tidy.setXHTML(true);
            tidy.setInputEncoding("UTF8");
            tidy.setOutputEncoding("UTF8");
            tidy.setIndentContent(false);
            tidy.setWraplen(0);
            tidy.setMakeClean(true);
            tidy.setMakeBare(true);
            tidy.setFixBackslash(true);
            tidy.setFixComments(true);
            tidy.setHideComments(true);
            tidy.setFixUri(true);
            tidy.setTrimEmptyElements(true);
            tidy.setDropEmptyParas(true);
            tidy.setDropProprietaryAttributes(true);
            tidy.setWord2000(true);
            tidy.setDocType("omit");
            tidy.setBreakBeforeBR(true);
            pipeout = new PipedOutputStream();
            pipein = new PipedInputStream(pipeout);
            Thread t = new PdfThread(pdf, parser, pipein);
            t.start();
            urlStream = url.openStream();
            tidy.parse(urlStream,pipeout);
//            tidy.parse(urlStream,out); // debug: output tidied html instead of pdf
            t.join();
        }
        catch ( Exception e ) {
            throw new ServletException(e);
        }
        finally {
            if (urlStream != null) {
                urlStream.close();
            }
            if (pipeout != null) {
                pipeout.close();
            }
            if  (pipein != null) {
                pipein.close();
            }
            if (pdf != null) {
                pdf.close();
            }
            if (out != null) {
                out.close();
            }
        }
    }

    public static HeaderFooter header(URL headerImage)
        throws com.lowagie.text.BadElementException,
               java.net.MalformedURLException,
               java.io.IOException {
        HeaderFooter header = null;
        try {
           header = 
               new HeaderFooter( 
                   new Phrase(
                       new Chunk( 
                           Image.getInstance( headerImage ),
                           310.0f,
                           -14.0f
            ///               -60.0f 
                       )
                   ),
                   false
               );
           header.setBorder(Rectangle.NO_BORDER);
        } catch (Exception e) { 
           log.error("Could not find header image for PDF from " + headerImage + ". Is the setting for internalUrl in web.xml correct?");
        }
        return header; 
    }
 
    public static HeaderFooter footer() {
        HeaderFooter footer = new HeaderFooter( new Phrase("Pagina "), true);
        footer.setBorder(Rectangle.NO_BORDER); 
        return footer;
    }

    private static class PdfThread extends Thread {
        private HtmlParser parser;
        private InputStream pipein;
        private Document pdf;

        PdfThread(Document pdf, HtmlParser parser, InputStream pipein) {
               this.parser=parser;
               this.pdf=pdf;
               this.pipein=pipein;
        }
                
        public void run() {
            parser.go(pdf,pipein);
        }
    }
    
}



