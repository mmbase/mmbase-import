package nl.didactor.education.servlet;
import nl.didactor.education.PDFConverter;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.ServletException;
import java.io.OutputStream;
import java.net.URL;

public class PDFServlet extends HttpServlet {
    
    public void doGet (HttpServletRequest req, HttpServletResponse resp) 
        throws ServletException, java.io.IOException
    {
        resp.setContentType("application/pdf");      
        int number = Integer.parseInt(req.getParameter("number"));
        URL url = new URL("http://"+java.net.InetAddress.getLocalHost().getHostAddress()+":"+req.getServerPort()+req.getContextPath()+"/education/pdfhtml.jsp?number="+number);
        PDFConverter.pageAsPDF(url, resp.getOutputStream());
   }
}
