<%@taglib uri="/WEB-INF/tld/struts-bean.tld" prefix="bean" %>
<%@page language="java" contentType="text/html; charset=utf-8" %>
<%@taglib uri="http://www.mmbase.org/mmbase-taglib-1.0" prefix="mm" %>
<%@taglib uri="http://www.opensymphony.com/oscache" prefix="cache" %>
<%@page import="java.util.*,java.text.*,java.io.*,org.mmbase.bridge.*,org.mmbase.util.logging.Logger,nl.leocms.util.*,nl.leocms.util.tools.HtmlCleaner" %>
<mm:import id="paginaID" externid="p" jspvar="paginaID" vartype="String">-1</mm:import>
<mm:import externid="language" jspvar="language" vartype="String"><%= 
  (request.getHeader("Accept-Language").toUpperCase().indexOf("NL")>-1 ? "nl" : "eng") %></mm:import>
<mm:import externid="material" jspvar="materialTypeID" vartype="String">-1</mm:import>
<mm:import externid="orgtype" jspvar="organisationTypeID" vartype="String">-1</mm:import>
<mm:import externid="locatie" jspvar="locatieID" vartype="String">-1</mm:import>
<mm:import externid="projtype" jspvar="projectTypeID" vartype="String">0</mm:import>
<mm:import externid="dur" jspvar="durationType" vartype="String">-1</mm:import>
<mm:import externid="a" jspvar="artikelID" vartype="String">-1</mm:import>
<%

String emailId = request.getParameter("e");
String nameId = request.getParameter("n");
String textId = request.getParameter("d");

boolean isIE = (request.getHeader("User-Agent").toUpperCase().indexOf("MSIE")>-1);

String thisLanguage = "?language=nl";
String otherLanguage = "?language=eng";
String otherLanguageName = "en";
if("eng".equals(language)) {
  thisLanguage = "?language=eng";
  otherLanguage = "?language=nl";
  otherLanguageName = "nl";
}
String queryString = request.getQueryString();
if(queryString!=null&&!queryString.equals("")) {
  int ls = queryString.indexOf("language=");
  if(ls!=-1) {
    int le = queryString.indexOf("&",ls);
    if(le!=-1){
      queryString = queryString.substring(0,ls) + queryString.substring(le+1);
    } else {
      queryString = queryString.substring(0,ls);
    }
  }
}

%>
<mm:cloud>
<html>
  <head>
    <base href="<%= javax.servlet.http.HttpUtils.getRequestURL(request) %>" />
    <title>
      <mm:node number="$paginaID" notfound="skipbody">
         <mm:relatednodes type="rubriek" path="posrel,rubriek"><mm:field name="naam" /></mm:relatednodes> - <mm:field name="titel" />
      </mm:node>
    </title>
    <link rel="stylesheet" type="text/css" href="css/website.css">
    <style>
    td {
       width: 30px;
       height: 30px;
       font-size: 140%;
       text-align: center;
    }
    </style>
    <meta http-equiv="imagetoolbar" content="no" />
    <mm:node number="home" jspvar="dummy">
      <meta name="description" content="<%= LocaleUtil.getField(dummy,"omschrijving",language, "") %>" />
      <meta name="keywords" content="<%= LocaleUtil.getField(dummy,"kortetitel",language, "") %>" />
    </mm:node>
    <script language="javascript" src="scripts/launchcenter.js"></script>
    <script src="http://www.google-analytics.com/urchin.js" type="text/javascript">
    </script>
    <script type="text/javascript">
      _uacct = "UA-495341-3";
      urchinTracker();
    </script>
  </head>
<body>
</mm:cloud>
