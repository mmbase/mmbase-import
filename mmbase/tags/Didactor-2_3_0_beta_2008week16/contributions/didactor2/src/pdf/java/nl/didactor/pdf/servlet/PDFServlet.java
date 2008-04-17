package nl.didactor.pdf.servlet;
import nl.didactor.pdf.PDFConverter;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.ServletException;
import java.io.OutputStream;
import java.net.URL;

import org.mmbase.util.logging.Logger;
import org.mmbase.util.logging.Logging;

public class PDFServlet extends HttpServlet {

    private static Logger log = Logging.getLoggerInstance(PDFServlet.class);
    
    public void doGet (HttpServletRequest req, HttpServletResponse resp) 
        throws ServletException, java.io.IOException
    {
        resp.setContentType("application/pdf");
        String baseUrl = getServletContext().getInitParameter("internalUrl");
        if (baseUrl == null) {
            throw new ServletException("Please set 'internalUrl' in the web.xml!");
        }
        int number = Integer.parseInt(req.getParameter("number"));
        int provider = Integer.parseInt(req.getParameter("provider"));
        String debug = req.getParameter("debug");
        URL url = debug != null ? new URL(debug) : new URL(baseUrl+"/pdf/pdfhtml.jsp?number="+number+"&provider="+provider);
        URL headerImage =  new URL(baseUrl+"/pdf/pdfheaderimage.jsp?provider="+provider);
        PDFConverter.pageAsPDF(url, resp.getOutputStream(),headerImage);
   }
}

