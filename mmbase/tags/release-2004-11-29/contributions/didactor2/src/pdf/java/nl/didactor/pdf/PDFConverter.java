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
import com.lowagie.text.html.HtmlParser;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.URL;

public class PDFConverter {
    
    public static void pageAsPDF (URL url, OutputStream out) 
        throws ServletException, java.io.IOException
    {
       try {
            Document pdf = new Document();
            PdfWriter writer = PdfWriter.getInstance(pdf, out);


            HeaderFooter footer = new HeaderFooter( new Phrase("Pagina "), true);
            footer.setBorder(Rectangle.NO_BORDER);
            pdf.setFooter(footer);

            HtmlParser parser = new HtmlParser();
            parser.parse(pdf,url.openStream());
            
            pdf.close();        
        }
        catch ( DocumentException e ) {
            throw new ServletException(e);
        }
    }
}

