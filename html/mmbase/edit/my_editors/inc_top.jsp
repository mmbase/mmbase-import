<%@ page language="java" contentType="text/html; charset=utf-8" import="org.mmbase.bridge.*,java.util.*" %>
<%@ taglib uri="http://www.mmbase.org/mmbase-taglib-1.0" prefix="mm" 
%><mm:import id="rank"><%= org.mmbase.util.xml.UtilReader.get("editors.xml").getProperties().getProperty("rank", "basic user")%></mm:import>