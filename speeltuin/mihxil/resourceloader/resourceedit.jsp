<!DOCTYPE html PUBLIC "-//W3C//DTD XHTML 1.0 Strict//EN" "DTD/xhtml1-strict.dtd">
<%@page language="java" contentType="text/html;charset=utf-8" session="true" import="org.mmbase.util.*,org.mmbase.util.transformers.*,java.io.*,java.net.*,org.w3c.dom.*,java.util.*,javax.servlet.jsp.PageContext"
%><%@ taglib uri="http://www.mmbase.org/mmbase-taglib-1.0" prefix="mm" 
%><mm:content type="text/html" expires="0">
<html>
  <head>
    <title><mm:write id="title" value="MMBase Resource Editor" /></title>
    <link rel="stylesheet" href="<mm:url page="/mmbase/style/css/mmbase.css" />" type="text/css" />
    <link rel="icon" href="<mm:url page="/mmbase/style/images/favicon.ico" />" type="image/x-icon" />
    <link rel="shortcut icon" href="<mm:url page="/mmbase/style/images/favicon.ico" />" type="image/x-icon" />    
  </head>
<body>
  <%! Xml  xmlEscaper     = new Xml(Xml.ESCAPE);  %>
  <%
  ResourceLoader resourceLoader = (ResourceLoader) session.getAttribute("resourceedit_document");
  if (resourceLoader == null) resourceLoader = ResourceLoader.getRoot();   
%>
  <form method="post" name="resource" action="<mm:url />">
<table>
  <mm:cloud>
  <mm:import externid="dirs" />
  <mm:write referid="dirs" jspvar="dir" vartype="String">
    <mm:isnotempty>
    <% resourceLoader = new ResourceLoader(resourceLoader, dir); 
       session.setAttribute("resourceedit_document", resourceLoader);
    %>    
    </mm:isnotempty>
  </mm:write>
  <mm:import externid="resource" vartype="string" jspvar="resource" />
  <mm:import externid="recursive" />
  <% boolean recursive = false; %>
  <mm:present referid="recursive"><% recursive = true; %></mm:present>
  <mm:import externid="keepsearch"><%=ResourceLoader.XML_PATTERN.pattern()%></mm:import>
  <mm:import externid="search" vartype="string" jspvar="search" ><mm:write referid="keepsearch" escape="none" /></mm:import>
  <tr>
    <th class="main" colspan="2">
      <mm:present referid="resource">
        <a href="<mm:url referids="search,recursive?" />">&lt;-- Back</a> | 
      </mm:present>
      <mm:write referid="title" /> 
    </th> 
    <th class="main" colspan="1">
      Root: <%= resourceLoader.toInternalForm("") %>
      <mm:notpresent referid="resource">
      <select name="dirs" onChange="document.forms[0].search.value = document.forms[0].examples.value; document.forms[0].submit();">
        <option value=""></option>
        <% if (! resourceLoader.equals(ResourceLoader.getRoot())) {
         %>
           <option value="..">..</option>
        <%
           }
           Iterator diri = resourceLoader.getResourceContexts(null, false).iterator(); 
           while (diri.hasNext()) {
           String dir = (String) diri.next();
        %>
           <option value="<%=dir%>"><%=dir%></option>
        <%
           }
        %>
      </select>      
      recursive: <input type="checkbox" name="recursive" <mm:present referid="recursive">checked="checked"</mm:present> onChange="document.forms[0].search.value = document.forms[0].examples.value; document.forms[0].submit();" />
      </mm:notpresent>
    </th>
  </tr> 
  <input type="hidden" name="keepsearch" value="<mm:write referid="search" />" />
  <mm:notpresent referid="resource">
    <tr>
      <td>Search (regular expression):</td>
      <td colspan="2">
        <input type="text" name="search" value="<mm:write referid="search" />" />
        <select name="examples" onChange="document.forms[0].search.value = document.forms[0].examples.value; document.forms[0].submit();">         
           <option value=".*" <mm:isempty referid="search"> selected="selected" </mm:isempty> >ALL</option>
           <mm:write value=".*\.xml$$">
              <option value="<mm:write />" <mm:compare referid2="search"> selected="selected" </mm:compare> >xml's</option>
           </mm:write>
           <mm:write value=".*\.dtd$$">
             <option value="<mm:write />" <mm:compare referid2="search"> selected="selected" </mm:compare> >dtd's</option>
           </mm:write>
          <mm:write value=".*\.xsl[t]?$$">
             <option value="<mm:write />" <mm:compare referid2="search"> selected="selected" </mm:compare> >xslt's</option>
           </mm:write>
          <mm:write value=".*\.properties$$">
             <option value="<mm:write />" <mm:compare referid2="search"> selected="selected" </mm:compare> >properties</option>
           </mm:write>
        </select>
        <input type="submit" name="s" value="search" />
      </td>
    </tr>
    <tr><th>&nbsp;</th><th>Resource-name</th><th>External URL</th></tr>
    <tr><td><a href="<mm:url referids="search"><mm:param name="resource" value="" /></mm:url>">new</a></td><td colspan="2"></td></tr>
    <%
    Iterator i = resourceLoader.getResourcePaths(search == null || search.equals("") ? null : java.util.regex.Pattern.compile(search), recursive).iterator();

    while (i.hasNext()) {
       String res = (String) i.next();
       URL url = resourceLoader.findResource(res);
       URLConnection con = url.openConnection();
       boolean editable = con.getDoOutput();
%>
<tr><td><a href="<mm:url referids="search,recursive?"><mm:param name="resource" value="<%=res%>" /></mm:url>"><%= editable ? "Edit" : "View"%></a></td>
<%
      out.println("<td>"  + res + "</td><td>" + url  + "</td></tr>");
}
%>
</mm:notpresent>
   <mm:present referid="resource">
   <%  URL url = resourceLoader.findResource(resource); 
       URLConnection con = url.openConnection();
   %>
  <tr>
    <td colspan="3">
      <mm:import externid="save" />
      <mm:import externid="wasxml">TEXT</mm:import>
      <mm:import externid="xml"><mm:write referid="wasxml" /></mm:import>
     
        Resource: 
        <input type="text" name="resource" style="width: 200px;" value="<%=resource%>" />
        <input type="submit" name="load" value="load" />
         <% if (con.getDoOutput()) { %>
            <input type="submit" name="save" value="save" />
         <% } else { %>
             READONLY
         <% } %>
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
        <% resourceLoader.storeDocument(resource, doc); %>
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
         <% { Writer writer = resourceLoader.getWriter(resource); writer.write(text); writer.close(); } %>
       </mm:present>
       <input type="hidden" name="wasxml" value="<mm:write referid="xml" />" />
       <input type="submit" name="xml" value="XML" />
       <textarea name="text" style="width: 100%" rows="30"><%
       {
       Reader r = resourceLoader.getReader(resource);
       if (r != null) {
         BufferedReader reader = new BufferedReader(new TransformingReader(r, xmlEscaper));
         while(true) {
            String line = reader.readLine();
            if (line == null) break;
            out.println(line);
         }
       } else {
          out.println("new resource");
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