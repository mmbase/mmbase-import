package nl.didactor.report;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.ServletException;
import org.mmbase.bridge.*;
import com.lowagie.text.pdf.*;
import com.lowagie.text.*;

import java.io.OutputStream;


public class ReportServlet extends HttpServlet {
    
    public void doGet (HttpServletRequest req, HttpServletResponse resp) 
        throws ServletException, java.io.IOException
    {
        resp.setContentType("application/pdf");
        int cnumber = Integer.parseInt(req.getParameter("class"));
        int enumber = Integer.parseInt(req.getParameter("education"));
		try {
	        Cloud cloud = LocalContext.getCloudContext().getCloud("mmbase");
			Document document = new Document(PageSize.A4, 36, 36, 36, 36);
			PdfWriter writer = PdfWriter.getInstance(document,resp.getOutputStream());
			document.open();
	        ClassReport report = new ClassReport(cloud.getNode(cnumber),cloud.getNode(enumber));
			ReportDocumentBuilder builder = new ReportDocumentBuilder(document,report);
			builder.buildDocument();
			writer.close();
			document.close();
		}
		catch (Exception e) {
			throw new ServletException(e);
		}
   }
}

