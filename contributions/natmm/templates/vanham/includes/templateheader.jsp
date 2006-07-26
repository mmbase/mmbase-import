<%@taglib uri="/WEB-INF/tld/struts-bean.tld" prefix="bean" %>
<%@page language="java" contentType="text/html; charset=utf-8" %>
<%@taglib uri="http://www.mmbase.org/mmbase-taglib-1.0" prefix="mm" %>
<%@taglib uri="http://www.opensymphony.com/oscache" prefix="cache" %>
<%@page import="java.util.*,java.text.*,java.io.*,org.mmbase.bridge.*,org.mmbase.util.logging.Logger,nl.leocms.util.*,nl.leocms.util.tools.HtmlCleaner" %>
<mm:import id="paginaID" externid="p" jspvar="paginaID" vartype="String">-1</mm:import>
<mm:import externid="language" jspvar="language" vartype="String">nl</mm:import>
<mm:import externid="material" jspvar="materialTypeID" vartype="String">-1</mm:import>
<mm:import externid="orgtype" jspvar="organisationTypeID" vartype="String">-1</mm:import>
<mm:import externid="locatie" jspvar="locatieID" vartype="String">-1</mm:import>
<mm:import externid="projtype" jspvar="projectTypeID" vartype="String">-1</mm:import>
<mm:import externid="dur" jspvar="durationType" vartype="String">-1</mm:import>
<mm:import externid="a" jspvar="artikelID" vartype="String">-1</mm:import>
<mm:import externid="showdate" jspvar="showdateID">false</mm:import>
<mm:import externid="s" jspvar="showintroID">true</mm:import>
<%
String imageId = request.getParameter("i");
String offsetId = request.getParameter("offset"); if(offsetId==null){ offsetId=""; }
String emailId = request.getParameter("e");
String nameId = request.getParameter("n");
String textId = request.getParameter("d");
%>
<html>
  <head>
    <title>VAN HAM - Homepage</title>
    <link rel="stylesheet" type="text/css" href="css/website.css">
    <%--
    <style>
    td { 
       width: 60px;
       height: 60px;
       font-size: 200%;
       text-align: center;
    } 
    </style>
    --%>
    <style>
    td { 
       width: 30px;
       height: 30px;
       font-size: 140%;
       text-align: center;
    } 
    </style>
    <script language="javascript" src="scripts/launchcenter.js"></script>
    <script src="http://www.google-analytics.com/urchin.js" type="text/javascript">
    </script>
    <script type="text/javascript">
      _uacct = "UA-495341-3";
      urchinTracker();
    </script>
  </head>
<body>
