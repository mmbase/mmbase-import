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
    @version  $Id: entrancepage.jsp,v 1.3 2002-11-18 17:22:06 michiel Exp $
 
    -->
   <link href="style/streammanager.css" type="text/css" rel="stylesheet" />
</head>
<body>
   <!-- We are going to set the referrer explicitely, because we don't wont to depend on the 'Referer' header (which is not mandatory) -->     
  <mm:import id="referrer"><%=new java.io.File(request.getServletPath())%></mm:import>
  <mm:import id="jsps">/mmapps/editwizard/jsp/</mm:import>
  <p align="right">
    <img src="images/logo_po_hor.gif"  />
  </p>
  <hr />
	<h1><mm:write referid="title" /></h1>
  <p>
   Fragment editor:
  </p>
  <table class="entrance">
   <tr>
   <td><%=m.getString("basefragment")%></td><td><form style="display:inline; " id="basefragments" action="<mm:url referids="referrer,language" page="${jsps}list.jsp" />" method="post">
           <select name="searchfields">
               <option value="mediafragments.title"><mm:fieldlist nodetype="mediafragments" fields="title"><mm:fieldinfo type="guiname" /></mm:fieldlist></option>
           </select>
           <input type="text" name="searchvalue" />
           <input type="hidden" name="wizard" value="tasks/basefragments" />
           <input type="hidden" name="nodepath" value="pools,mediafragments" />
           <input type="hidden" name="fields" value="mediafragments.number,mediafragments.title" />
           <input type="hidden" name="orderby" value="mediafragments.title" />
           <input type="hidden" name="startnodes" value="media.myfragments" />
           <input type="hidden" name="origin" value="media.myfragments" />
           <input type="hidden" name="directions" value="down" />
        </form><a href="javascript:document.forms['basefragments'].submit();"><img src="media/search.gif" border="0"/></a>
       <a href="<mm:url referids="referrer,language" page="${jsps}wizard.jsp">
            <mm:param name="wizard">tasks/basefragments</mm:param>
            <mm:param name="objectnumber">new</mm:param>
            <mm:param name="origin">media.myfragments</mm:param>
            </mm:url>"><img src="media/new.gif" border="0" alt="<%=m.getString("newstream")%>" /></a></td></tr>
   <tr><td><%=m.getString("itemizations")%></td><td><form style="display: inline; " id="fragments" action="<mm:url referids="referrer,language" page="${jsps}list.jsp" />" method="post">
           <select name="searchfields">
               <option value="mediafragments.title,mediafragments2.title"><mm:fieldlist nodetype="mediafragments" fields="title"><mm:fieldinfo type="guiname" /></mm:fieldlist></option>
           </select>
           <input type="text" name="searchvalue" />
           <input type="hidden" name="wizard" value="tasks/itemize" />
           <input type="hidden" name="nodepath" value="pools,mediafragments,parent,mediafragments2" />
           <input type="hidden" name="fields" value="mediafragments2.number,mediafragments.number,mediafragments.title,mediafragments2.title" />
           <input type="hidden" name="orderby" value="mediafragments.title,mediafragments2.title" />
           <input type="hidden" name="startnodes" value="media.myfragments" />
           <input type="hidden" name="origin" value="media.myfragments" />
          <input type="hidden" name="directions" value="down" />
          <input type="hidden" name="distinct" value="true" />
        </form><a href="javascript:document.forms['fragments'].submit();"><img src="media/search.gif" alt="<%=m.getString("newstream")%>" border="0" /></a>
               <a href="<mm:url referids="referrer,language" page="${jsps}wizard.jsp">
            <mm:param name="wizard">tasks/itemize</mm:param>
            <mm:param name="objectnumber">new</mm:param>
            </mm:url>"><img src="media/new.gif" border="0" alt="<%=m.getString("newitems")%>" /></a></td></tr>
   </table>
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