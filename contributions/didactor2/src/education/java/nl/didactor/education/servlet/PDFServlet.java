package nl.didactor.education.servlet;
import org.mmbase.servlet.BridgeServlet;
import org.mmbase.bridge.*;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.ServletException;
import com.lowagie.text.*;
import com.lowagie.text.pdf.PdfWriter;
import com.lowagie.text.html.HtmlParser;
import java.io.InputStream;
import java.net.URL;

public class PDFServlet extends BridgeServlet {
    
    public void doGet (HttpServletRequest req, HttpServletResponse resp) 
        throws ServletException, java.io.IOException
    {
        Cloud cloud = getAnonymousCloud();
        
        int number = Integer.parseInt(req.getParameter("number"));
        URL url = new URL("http://"+java.net.InetAddress.getLocalHost().getHostAddress()+":"+req.getServerPort()+req.getContextPath()+"/education/pdfhtml.jsp?number="+number);
        try {
            resp.setContentType("application/pdf");
            Document pdf = new Document();
            PdfWriter writer = PdfWriter.getInstance(pdf, resp.getOutputStream());


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
