<%@ page language="java" contentType="text/html; charset=utf-8" %>
<%@ taglib uri="http://www.mmbase.org/mmbase-taglib-1.0" prefix="mm" %>
<%@ page import="org.mmbase.bridge.*,java.util.*" 
%><% response.setContentType("text/html; charset=utf-8"); 
%><mm:import id="rank"><%= org.mmbase.util.xml.UtilReader.get("editors.xml").getProperties().getProperty("rank", "basic user")%></mm:import>
