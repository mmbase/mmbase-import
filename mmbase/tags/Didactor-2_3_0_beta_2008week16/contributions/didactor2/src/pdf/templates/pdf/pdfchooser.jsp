<%@taglib uri="http://www.mmbase.org/mmbase-taglib-2.0" prefix="mm"%>
<%@taglib uri="http://www.didactor.nl/ditaglib_1.0" prefix="di" %>
<%@page import="java.net.URL, nl.didactor.pdf.PDFConverter, java.io.ByteArrayOutputStream"%>


<mm:content postprocessor="reducespace">
<mm:cloud method="delegate" jspvar="cloud">
   <%@include file="/shared/setImports.jsp"%>
   <mm:import externid="number" required="true" jspvar="number"/>
   <mm:import externid="action"/>
   <mm:present referid="action">
   <mm:compare referid="action" value="mail">


    <mm:list nodes="$user" path="people,mailboxes" fields="mailboxes.number" constraints="mailboxes.type=11">
        <mm:field name="mailboxes.number" id="mailboxNumber" write="false"/>
        <mm:node referid="mailboxNumber" id="mailboxNode"/>
    </mm:list>
    <mm:notpresent referid="mailboxNode">
        <mm:list nodes="$user" path="people,mailboxes" fields="mailboxes.number" constraints="mailboxes.type=1">
            <mm:field name="mailboxes.number" id="mailboxNumber" write="false"/>
            <mm:node referid="mailboxNumber" id="mailboxNode"/>
        </mm:list>
    </mm:notpresent>
    <mm:notpresent referid="mailboxNode">
        Deze gebruiker heeft geen sent mailbox!
    </mm:notpresent>

    <mm:import jspvar="providerNumber" reset="true"><mm:write referid="provider"/></mm:import>
<%
    String baseUrl = getServletContext().getInitParameter("internalUrl");

    if (baseUrl == null) {
        throw new ServletException("Please set 'internalUrl' in the web.xml!");
    }

    URL url = new URL(baseUrl+"/pdf/pdfhtml.jsp?number="+number+"&provider="+providerNumber);
    URL imageUrl = new URL(baseUrl+"/pdf/pdfheaderimage.jsp?provider="+providerNumber);
    ByteArrayOutputStream outStream = new ByteArrayOutputStream();
    PDFConverter.pageAsPDF(url, outStream,imageUrl);
%>
    <mm:node number="$number">
        <mm:import id="attachmentname"><mm:field name="title"/><mm:field name="name"/></mm:import>
    </mm:node>

    <mm:createnode type="attachments" id="attachment" jspvar="attachment">
        <mm:setfield name="title"><mm:write referid="attachmentname"/></mm:setfield>
        <mm:setfield name="mimetype">application/pdf</mm:setfield>
        <mm:setfield name="filename"><mm:write referid="attachmentname"/>.pdf</mm:setfield>
        <% attachment.setByteValue("handle",outStream.toByteArray()); %>
        <mm:setfield name="date"><%=System.currentTimeMillis()/1000%></mm:setfield>
    </mm:createnode>

    <mm:createnode type="emails" id="emailNode" jspvar="mailNode">
         <mm:setfield name="type">0</mm:setfield>
    </mm:createnode>


    <mm:createrelation role="related" source="mailboxNode" destination="emailNode"/>
    <mm:createrelation role="related" source="attachment" destination="emailNode"/>

    <mm:treeinclude page="/email/write/write.jsp" objectlist="$includePath" referids="$referids">
        <mm:param name="id"><mm:write referid="emailNode"/></mm:param>
    </mm:treeinclude>
  </mm:compare>
</mm:present>

 <mm:notpresent referid="action">
<html>
<head>
<title>PDF output</title>
</head>
<body>
     <a href="<%= request.getContextPath() %>/pdf.db?number=<mm:write referid="number"/>&provider=<mm:write referid="provider"/>"><img src="printPDF.gif" title="Bekijk als PDF" alt="Bekijk als PDF" border="0"></a> <a href="pdfchooser.jsp?action=mail&number=<mm:write referid="number"/>" target="_top"><img src="mailPDF.gif" title="Mail als PDF" alt="Mail als PDF" border="0"/></a>
</body>
</html>

</mm:notpresent>
</mm:cloud>
</mm:content>
