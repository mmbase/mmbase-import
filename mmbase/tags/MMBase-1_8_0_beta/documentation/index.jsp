<%@page language="java" contentType="text/html;charset=utf-8" session="false"
%><%@page import="java.io.*" %><%
      String path = request.getServletPath();
      int pos = path.lastIndexOf("/");
      if (pos == -1) {
        path="/";
      } else {
        path = path.substring(0,pos+1);
      }
      org.mmbase.util.DocumentationIndex documentationIndex = new org.mmbase.util.DocumentationIndex(getServletContext(), path);
      documentationIndex.createIndexPage(out);
%>
