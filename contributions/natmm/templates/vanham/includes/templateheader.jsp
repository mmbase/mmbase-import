<%@page language="java" contentType="text/html; charset=utf-8"
%><%@taglib uri="http://www.mmbase.org/mmbase-taglib-1.0" prefix="mm" 
%><%@taglib uri="http://www.opensymphony.com/oscache" prefix="cache" 
%><%@page import="java.util.*,java.text.*,java.io.*,org.mmbase.bridge.*,org.mmbase.util.logging.Logger,nl.leocms.util.*,nl.leocms.util.tools.HtmlCleaner" %>
<mm:import id="paginaID" externid="p" jspvar="paginaID" vartype="String">-1</mm:import>
<mm:import externid="language" jspvar="language" vartype="String">nl</mm:import>
<%
String imageId = request.getParameter("i"); 
String offsetId = request.getParameter("offset"); if(offsetId==null){ offsetId=""; }
%>
