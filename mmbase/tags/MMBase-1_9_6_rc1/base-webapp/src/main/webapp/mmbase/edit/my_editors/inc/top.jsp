<%@ page language="java" contentType="text/html" session="false" import="org.mmbase.bridge.*"
%><%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" 
%><%@ taglib uri="http://java.sun.com/jsp/jstl/fmt" prefix="fmt" 
%><%@ taglib uri="http://www.mmbase.org/mmbase-taglib-2.0" prefix="mm" 
%><!DOCTYPE html PUBLIC "-//W3C//DTD XHTML 1.1//EN" "http://www.w3.org/TR/xhtml11/DTD/xhtml11.dtd">
<mm:import id="rank"><%= org.mmbase.util.xml.UtilReader.get("editors.xml").getProperties().getProperty("rank", "basic user")%></mm:import>
