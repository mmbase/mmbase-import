<%@page language="java" contentType="text/html;charset=UTF-8" 
%><%@ taglib uri="http://www.mmbase.org/mmbase-taglib-1.0" prefix="mm" 
%><mm:import externid="language">nl</mm:import><mm:locale language="$language"><mm:cloud loginpage="login.jsp"><!DOCTYPE HTML PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN">
<?xml version="1.0" encoding="UTF-8"?>
<html>
<% java.util.ResourceBundle m = null; // short var-name because we'll need it all over the place
   java.util.Locale locale = null; %>
<mm:write referid="language" jspvar="lang" vartype="string">
<%
  locale  =  new java.util.Locale(lang, "");
  m = java.util.ResourceBundle.getBundle("org.mmbase.util.media.resources.mediaedit", locale);
%>
</mm:write>

<head>
   <title><mm:write id="title" value="<%=m.getString("title")%>" /></title>
   <!--

    @since    MMBase-1.6
    @author   Michiel Meeuwissen
    @version  $Id: entrancepage.jsp,v 1.2 2002-11-14 16:52:08 michiel Exp $
 
    -->
   <link href="style/streammanager.css" type="text/css" rel="stylesheet" />
</head>
<body>
   <!-- We are going to set the referrer explicitely, because we don't wont to depend on the 'Referer' header (which is not mandatory) -->     
  <mm:import id="referrer"><%=new java.io.File(request.getServletPath())%></mm:import>
  <mm:import id="jsps">/mmapps/editwizard/jsp/</mm:import>
	<h1><mm:write referid="title" /></h1>
  <p>
    needed: mediasource, mediafragments. 'posrel' relation between
    them. 'parent/child' between mediafragments.
  </p>
  <p>
   Fragment editor:
  </p>
  <ul>
   <li><a href="javascript:document.forms['fragments'].submit();"><mm:nodeinfo nodetype="mediafragments" type="guitype" /></a><form id="fragments" action="<mm:url referids="referrer,language" page="${jsps}list.jsp" />" method="post">
           <select name="searchfields">
               <option value="mediafragments.title"><mm:fieldlist nodetype="mediafragments" fields="title"><mm:fieldinfo type="guiname" /></mm:fieldlist></option>
           </select>
           <input type="text" name="searchvalue" />
           <!-- input type="submit" value="OK" /-->
           <input type="hidden" name="wizard" value="tasks/fragments" />
           <input type="hidden" name="nodepath" value="pools,mediafragments" />
           <input type="hidden" name="fields" value="mediafragments.number,mediafragments.title" />
           <input type="hidden" name="orderby" value="mediafragments.title" />
           <input type="hidden" name="startnodes" value="media.myfragments" />
           <input type="hidden" name="origin" value="media.myfragments" />
           <input type="hidden" name="directions" value="down" />
        </form>
     </li>
	<li><a href="<mm:url referids="language,referrer" page="${jsps}list.jsp">           
           <mm:param name="wizard">tasks/itemize</mm:param>
          <mm:param name="nodepath">mediafragments</mm:param>
           <mm:param name="fields">title</mm:param>
           <mm:param name="orderby">number</mm:param>
           <mm:param name="directions">down</mm:param>
           </mm:url>">Itemize <mm:nodeinfo nodetype="mediafragments" type="guitype" /></a></li>
	<li><a href="<mm:url referids="language,referrer" page="${jsps}list.jsp">           
           <mm:param name="wizard">tasks/sources</mm:param>
          <mm:param name="nodepath">mediasources</mm:param>
           <mm:param name="fields">url,format</mm:param>
           <mm:param name="orderby">number</mm:param>
           <mm:param name="directions">down</mm:param>
           </mm:url>"><mm:nodeinfo nodetype="mediasources" type="guitype" /></a></li>
            </ul>
  <hr />
  <img src="images/logo_po_hor.gif" />
  <hr />

  <mm:context>
  <mm:import id="langs" vartype="list">en,nl</mm:import>
  <mm:aliaslist id="language" referid="langs">
     <a href="<mm:url referids="language" />" ><mm:locale language="$_" jspvar="loc"><%= loc.getDisplayLanguage(loc)%></mm:locale></a><br />
  </mm:aliaslist>
  </mm:context>

</body>
</html>
</mm:cloud>
</mm:locale>