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
import com.lowagie.text.Chunk;
import com.lowagie.text.Image;
import com.lowagie.text.html.HtmlParser;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.URL;

public class PDFConverter {
    
    public static void pageAsPDF (URL url, OutputStream out, URL headerImageURL) 
        throws ServletException, java.io.IOException
    {
       try {
            Document pdf = new Document();
            pdf.setMargins(50f,50f,110f,50f);
            PdfWriter writer = PdfWriter.getInstance(pdf, out);

            pdf.setFooter(footer());

            if ( headerImageURL != null ) {
                pdf.setHeader(header(headerImageURL));
            }

            HtmlParser parser = new HtmlParser();
            parser.parse(pdf,url.openStream());
        
            pdf.close();        
        }
        catch ( DocumentException e ) {
            throw new ServletException(e);
        }
    }

    public static HeaderFooter header(URL headerImage)
        throws com.lowagie.text.BadElementException,
               java.net.MalformedURLException,
               java.io.IOException {
        HeaderFooter header = 
            new HeaderFooter( 
                new Phrase(
                    new Chunk( 
                        Image.getInstance( headerImage ),
                        310.0f,
                        -34.0f
                    )
                ),
                false
            );
        header.setBorder(Rectangle.NO_BORDER);
        return header; 
    }
 
    public static HeaderFooter footer() {
        HeaderFooter footer = new HeaderFooter( new Phrase("Pagina "), true);
        footer.setBorder(Rectangle.NO_BORDER); 
        return footer;
    }
   
}



