<!DOCTYPE HTML PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN">
<?xml version="1.0" encoding="UTF-8"?>
<%@page language="java" contentType="text/html;charset=UTF-8"%>
<%@taglib uri="http://www.mmbase.org/mmbase-taglib-1.0" prefix="mm"%>

<html>
<head>
   <title>EditWizards finalist</title>
</head>
<body>

<h1>Editwizards</h1>

<!-- We are going to set the referrer explicitely, because we don't
want to depend on the 'Referer' header (which is not mandatory) -->
<mm:import id="referrer"><%=new  java.io.File(request.getServletPath())%></mm:import>
<mm:import id="jsps">/templates/jsp/</mm:import>

<mm:cloud method="http">

<table>
<mm:listnodes type="editwizards">
  <tr><td>
    <a href="<mm:url referids="referrer" page="${jsps}list.jsp">
       <mm:param name="wizard"><mm:field name="wizard"/></mm:param>
<%-- mm:param name="objectnumber"><mm:field name="objectnumber"/></mm:param --%>
       <mm:param name="nodepath"><mm:field name="nodepath"/></mm:param>
       <mm:param name="fields"><mm:field name="fields"/></mm:param>
       <mm:param name="constraints"><mm:field name="constraints"/></mm:param>
       <mm:param name="age"><mm:field name="age"/></mm:param>
       <mm:param name="distinct"><mm:field name="distinct"/></mm:param>
       <mm:param name="searchdir"><mm:field name="searchdir"/></mm:param>
       <mm:param name="orderby"><mm:field name="orderby"/></mm:param>
       <mm:param name="directions"><mm:field name="directions"/></mm:param>
       <mm:param name="pagelength"><mm:field name="pagelength"/></mm:param>
       <mm:param name="maxpagecount"><mm:field name="maxpagecount"/></mm:param>
       <mm:param name="maxupload"><mm:field name="maxupload"/></mm:param>
       <mm:param name="searchfields"><mm:field name="searchfields"/></mm:param>
       <mm:param name="searchtype"><mm:field name="searchtype"/></mm:param>       
       <mm:param name="searchvalue"><mm:field name="searchvalue"/></mm:param>
       <mm:param name="search"><mm:field name="search"/></mm:param>
       <mm:param name="origin"><mm:field name="origin"/></mm:param>
       <mm:param name="title"><mm:field name="title"/></mm:param>       
       <mm:param name="debug">true</mm:param>
       </mm:url>" target="wizard">
       
       <mm:field name="name"/>
    </a>
    </td><td>
    <mm:field name="description"/>
  </td></tr>
</mm:listnodes>
</table>

</mm:cloud>

</body>
</html>