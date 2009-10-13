<!DOCTYPE HTML PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN">
<%@page language="java" contentType="text/html;charset=UTF-8"
%><%@ taglib uri="http://www.mmbase.org/mmbase-taglib-1.0" prefix="mm"
%><?xml version="1.0" encoding="UTF-8"?>
<html>
<head>
   <title>EditWizard Examples</title>
   <!--
    More complicated example.

    @since    MMBase-1.6
    @author   Michiel Meeuwissen
    @version  $Id: index.jsp,v 1.2 2006-02-13 16:17:20 pierre Exp $
    -->

   <link rel="stylesheet" type="text/css" href="../style.css" />
</head>
<body>
   <!-- We are going to set the referrer explicitely, because we don't wont to depend on the 'Referer' header (which is not mandatory) -->
  <mm:import id="referrer"><%=new java.io.File(request.getServletPath())%></mm:import>
  <mm:import id="jsps">/mmbase/edit/wizard/jsp</mm:import>
  <mm:import id="pagelength">10</mm:import>
  <table class="body">
    <tr><td class="left" /><td>
      <table class="body" cellspacing="0" cellpadding="4" width="100%">
        <tr>
          <td class="mysteps_top" valign="top" width="575">
          <span class="title"><nobr><span class="titleprompt">Editwizard Examples</span></nobr></span></td>
          <td class="gutter" width="200"><br /></td>
        </tr>
  </table>
  </td></tr><tr><td class="left" />
  <td class="listcanvas">
   <table>
  <p>
   This example overrides some XSL's of the editwizard by placing
   variants in xsl/ relative to this file.
  </p>
  <p>
     <a target="_new" href="<mm:url page="../citexml.jsp"><mm:param name="page">finalist/xsl/base.xsl</mm:param></mm:url>">view xsl/base.xsl</a><br />
     <a target="_new" href="<mm:url page="../citexml.jsp"><mm:param name="page">finalist/xsl/list.xsl</mm:param></mm:url>">view xsl/list.xsl</a><br />
     <a target="_new" href="<mm:url page="../citexml.jsp"><mm:param name="page">finalist/xsl/wizard.xsl</mm:param></mm:url>">view xsl/wizard.xsl</a><br />
  </p>
  <table class="listcanvas">
   <tr><td>
        <a href="<mm:url referids="referrer,pagelength" page="$jsps/list.jsp">
           <mm:param name="wizard">../advanced/tasks/people</mm:param>
           <mm:param name="nodepath">people</mm:param>
           <mm:param name="fields">number,firstname,lastname</mm:param>
           <mm:param name="orderby">number</mm:param>
           <mm:param name="directions">down</mm:param>
           </mm:url>">Person test</a>
  <!-- show how to jump to wizard.jsp directly -->
  (<a href="<mm:url referids="referrer,pagelength" page="$jsps/wizard.jsp">
            <mm:param name="wizard">../advanced/tasks/people</mm:param>
            <mm:param name="objectnumber">new</mm:param>
            </mm:url>">Create</a>)
  </td><td>

<tr><td>
   <a href="<mm:url referids="referrer,pagelength" page="$jsps/list.jsp">
           <mm:param name="wizard">../advanced/tasks/attachments</mm:param>
           <mm:param name="nodepath">attachments</mm:param>
           <mm:param name="fields">title</mm:param>
           <mm:param name="orderby">title</mm:param>
           </mm:url>" >
           Attachments</a>
   </td><td>
 Use the editwizards to upload and download attachments e.g. PDF files.
    </td>
  <td><a target="_new" href="<mm:url page="../citexml.jsp"><mm:param name="page">advanced/tasks/attachments.xml</mm:param></mm:url>">view XML</a></td>
 </tr>
    <tr><td>
    <a href="<mm:url referids="referrer,pagelength" page="$jsps/list.jsp">
             <mm:param name="wizard">../advanced/tasks/news</mm:param>
           <mm:param name="nodepath">news</mm:param>
           <mm:param name="fields">number,title,date</mm:param>
           <mm:param name="orderby">number</mm:param>
           <mm:param name="directions">down</mm:param>
           </mm:url>">News</a>
     </td><td>
       <ul>
         <li> How to use editwizards 'libs'. These are pieces
      of XML stored in the editwizard data directory which you can
      include in you own wizards</li>
       <li>'subwizards'</li>
       <li>fieldset</li>
        </ul>

     </td>
  <td><a target="_new" href="<mm:url page="../citexml.jsp"><mm:param name="page">advanced/tasks/news.xml</mm:param></mm:url>">view XML</a></td>
   </tr>
    <tr><td>
    <a href="<mm:url referids="referrer,pagelength" page="$jsps/list.jsp">
               <mm:param name="title">MyNews Magazine news</mm:param>
               <mm:param name="origin">default.mags</mm:param>
               <mm:param name="startnodes">default.mags</mm:param>
             <mm:param name="wizard">../advanced/tasks/news_origin</mm:param>
           <mm:param name="nodepath">mags,news</mm:param>
           <mm:param name="fields">news.number,news.title,news.date</mm:param>
           <mm:param name="orderby">news.number</mm:param>
           <mm:param name="directions">down</mm:param>
           <mm:param name="searchdirs">destination</mm:param>
           </mm:url>">News</a>
     </td><td>
        Only list news of default magazine (MyNews magazine).
     </td></tr>
    <tr><td>
    <a href="<mm:url referids="referrer,pagelength" page="$jsps/list.jsp">
             <mm:param name="wizard">../advanced/tasks/mags</mm:param>
           <mm:param name="nodepath">mags</mm:param>
           <mm:param name="fields">number,title</mm:param>
           <mm:param name="orderby">number</mm:param>
           <mm:param name="directions">down</mm:param>
           </mm:url>">Magazines</a>
     </td><td>
       Demonstrated is how to use 'posrel', and how to create 'optionlists'.
     </td>
  <td><a target="_new" href="<mm:url page="../citexml.jsp"><mm:param name="page">advanced/tasks/mags.xml</mm:param></mm:url>">view XML</a></td>
      </tr>

    <tr><td>
    <a href="<mm:url referids="referrer,pagelength" page="$jsps/list.jsp">
             <mm:param name="wizard">../advanced/tasks/people</mm:param>
           <mm:param name="nodepath">news,people</mm:param>
           <mm:param name="fields">people.number,news.title,people.firstname,people.lastname</mm:param>
           <mm:param name="orderby">people.lastname</mm:param>
           <mm:param name="searchfields">people.lastname</mm:param>
           </mm:url>">Authors</a>
     </td><td>
       Demo of multilevel search
     </td>
  <td>
   </td>
      </tr>
     </table>

<hr />
   <a href="<mm:url page="../../taglib/showanypage.jsp"><mm:param name="page"><%=request.getServletPath()%></mm:param></mm:url>">Source of this page</a><br />
<a href="<mm:url page="../" />">back</a>
  </td></tr>
  </table>
</body>
</html>
