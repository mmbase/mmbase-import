<%@page language="java" contentType="text/html;charset=utf-8" session="false"
%><%@page import="java.io.*" %><%
      org.mmbase.util.DocumentationIndex documentationIndex = new org.mmbase.util.DocumentationIndex();
      String path = pageContext.getServletContext().getRealPath(request.getServletPath());
      File documentationDir =  new File(path).getParentFile();
      documentationIndex.createIndexPage(out, documentationDir);
%>
