<!DOCTYPE html PUBLIC "-//W3C//DTD XHTML 1.0 Strict//EN" "DTD/xhtml1-strict.dtd">
<%@page language="java" contentType="text/html;charset=utf-8" session="true" import="org.mmbase.util.*,java.io.*,java.net.*,org.w3c.dom.*,java.util.*"
%><%@ taglib uri="http://www.mmbase.org/mmbase-taglib-1.0" prefix="mm" 
%><mm:content type="text/html">
<html>
  <head>
    <title><mm:write id="title" value="MMBase Resource Editor" /></title>
    <link rel="stylesheet" href="mmbase/style/css/mmbase.css" type="text/css" />
    <link rel="icon" href="<mm:url page="/mmbase/style/images/favicon.ico" />" type="image/x-icon" />
    <link rel="shortcut icon" href="<mm:url page="/mmbase/style/images/favicon.ico" />" type="image/x-icon" />

  </head>
<body >
<%!  ResourceLoader resourceLoader = ResourceLoader.getRoot(); %>
<form method="post">
<table>
  <mm:cloud>
  <tr>
    <th class="main" colspan="2"><mm:write referid="title" /></th> 
    <th class="main" colspan="1">Root: <%= resourceLoader.findResource("") %></th>
  </tr>
  <mm:import externid="resource" vartype="string" jspvar="resource" />
  <mm:import externid="keepsearch">.*\.xml$</mm:import>
  <mm:import externid="search" vartype="string" jspvar="search" ><mm:write referid="keepsearch" escape="none" /></mm:import>
  <input type="hidden" name="keepsearch" value="<mm:write referid="search" />" />
  <mm:notpresent referid="resource">
    <tr><td>Search (regular expression):</td><td><input type="text" name="search" value="<mm:write referid="search" />" /><input type="submit" name="s" value="search" /></td><td /></tr>
    <tr><th>&nbsp;</th><th>Resource-name</th><th>External URL</th></tr>
    <%
    Iterator i = resourceLoader.getResourcePaths(search == null || search.equals("") ? null : java.util.regex.Pattern.compile(search), true).iterator();

    while (i.hasNext()) {
      String res = (String) i.next();
%>
<tr><td><a href="<mm:url referids="search"><mm:param name="resource" value="<%=res%>" /></mm:url>">Edit</a></td>
<%
      out.println("<td>"  + res + "</td><td>" + resourceLoader.findResource(res) + "</td></tr>");
}
%>
</mm:notpresent>
   <mm:present referid="resource">
  <tr>
    <td colspan="3">
      <mm:import externid="save" />
      <mm:import externid="wasxml">TEXT</mm:import>
      <mm:import externid="xml"><mm:write referid="wasxml" /></mm:import>
     
        Resource: 
        <input type="text" name="resource" style="width: 200px;" value="<%=resource%>" />
        <input type="submit" name="load" value="load" />
        <input type="submit" name="save" value="save" />
	<a href="<mm:url referids="search" />">Back</a>
        <mm:compare referid="xml" value="XML">          
          <input type="hidden" name="wasxml" value="<mm:write referid="xml" />" />
          <input type="submit" name="xml" value="TEXT" />
<%
          {
            Document doc;
            if (resource.equals(session.getAttribute("resourceedit_document_resource"))) {
                  doc = (Document) session.getAttribute("resourceedit_document");
            } else {
                  doc = resourceLoader.getDocument(resource);

                  session.setAttribute("resourceedit_document", doc);
                  session.setAttribute("resourceedit_document_resource", resource);
            }
            if (doc == null) {
              out.println("<br />Resource does not exist");
            } else {
%>
      <mm:present referid="save">
        <mm:import externid="text" jspvar="text" vartype="string" />
        <%
         {
             resourceLoader.storeDocument(doc, resource);
}
%>
      </mm:present>
<%
            NodeList list =  doc.getChildNodes();
            out.println("<br />");
            for (int i = 0 ; i < list.getLength(); i++) {
               Node node = list.item(i);
               out.println("" + node.getNodeName() + "<textarea name='bla'>" + node.getNodeValue() + "</textarea><br />"); 
            } 
            }
          }
%>
        </mm:compare>
        <mm:compare referid="xml" value="XML" inverse="true">
      <mm:present referid="save">
        <mm:import externid="text" jspvar="text" vartype="string" />
        <%
         {
         URL url = resourceLoader.findResource(resource);
         OutputStream stream = resourceLoader.createResourceAsStream(resource);
         PrintWriter writer = new PrintWriter(stream);
         writer.write(text);
         writer.close();
}
%>
      </mm:present>
          <input type="hidden" name="wasxml" value="<mm:write referid="xml" />" />
          <input type="submit" name="xml" value="XML" />
          <textarea name="text" style="width: 100%" rows="30"><%
          {
          URL url = resourceLoader.findResource(resource);
          if (url != null) {
             InputStream stream = url.openStream();
             if (stream != null) {
             BufferedReader reader = new BufferedReader(new InputStreamReader(stream));
              while(true) {
                String line = reader.readLine();
                if (line == null) break;
                out.println(line);
            }
          } else {
                out.println("new resource");
          }
             
          }
}
%></textarea>
        </mm:compare>
    </td>
  </tr>
  </mm:present>

</mm:cloud>
</table>
</form>
</body>
</html>
</mm:content>