<%@ page isErrorPage="true" %><%@ taglib uri="http://www.mmbase.org/mmbase-taglib-1.0" prefix="mm"
%><%@ page import="org.mmbase.applications.editwizard.*"
%><%@ page import="org.w3c.dom.Document"
%><%
     try { 
        if(exception == null) exception = new javax.servlet.jsp.JspException("dummy-exception, to test the errorpage-page");
        
        // place all objects
        String s = "<error />";
        Document doc = Utils.parseXML(s);
        
        java.util.Map params = new java.util.HashMap();
        String sessionKey = request.getParameter("sessionkey");
        if (sessionKey == null) sessionKey = "editwizard";
        Config ewConfig = (Config)session.getAttribute(sessionKey);
        if (ewConfig!=null) {
            params.put("backpage", ewConfig.backPage);
        } else {
            ewConfig = new Config();
            Config.Configurator configurator = new Config.Configurator(pageContext, ewConfig);
        }
        
        java.io.File template = ewConfig.uriResolver.resolveToFile("xsl/exception.xsl");
    
        String message = exception.getMessage();
        if (message == null) {
            message = exception.toString();
        }
    
        org.w3c.dom.Node docel = doc.getDocumentElement();
    
        org.w3c.dom.Node excnode = docel.getOwnerDocument().createElement("exception");
    
        java.util.StringTokenizer lines = new java.util.StringTokenizer(message,"\n\r");
        // only show 1 line, otherwise we still could get very difficult and complicated messages
        if(lines.hasMoreElements()) {
            Utils.storeText(excnode,org.mmbase.util.Encode.encode("ESCAPE_HTML", lines.nextToken()));
        }
        docel.appendChild(excnode);
    
        org.w3c.dom.Node sttnode = docel.getOwnerDocument().createElement("stacktrace");
        Utils.storeText(sttnode,org.mmbase.util.logging.Logging.stackTrace(exception));
        docel.appendChild(sttnode);
    
        Utils.transformNode(doc, template, ewConfig.uriResolver, out, params);
   } catch (Exception e) {
        out.println("The following error occurred: "+exception);  
   }
%>

