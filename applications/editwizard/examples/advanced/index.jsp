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
    @version  $Id: index.jsp,v 1.10 2002-05-27 22:02:04 michiel Exp $
 
    Showing: 
          - use of taglib in this entrance page
          - xml-definitions in subdir of entrance page
          - javascript for search action
          - jump to create directly
    -->

   <link rel="stylesheet" type="text/css" href="../style.css" />	
    <script language="javascript"><!--
	  function openListImages(el) {
	  var href = el.getAttribute("href");
	  var zoek = document.forms[0].elements["imagedesc"].value.toUpperCase();
	  if (zoek != '') {
	  href += "&constraints=UCASE%28description%29%20LIKE%20%27%25" + zoek + "%25%27%20or%20UCASE%28title%29%20LIKE%20%27%25" + zoek + "%25%27";
	  }
	  document.location = href;
	  return false;
}
--></script>        
</head>
<body>
<form>
   <!-- We are going to set the referrer explicitely, because we don't wont to depend on the 'Referer' header (which is not mandatory) -->     
  <mm:import id="referrer"><%=new java.io.File(request.getServletPath())%></mm:import>
  <mm:import id="jsps">/mmapps/editwizard/jsp/</mm:import>
	<h1>Editwizard Examples</h1>
  <p>
   This example overrides the 'list.xsl' of the editwizard bij placing
   a variant in xsl/list.xsl relative to this file. It also uses it's
   private XML editwizard definitions, which are also placed
   relativily to this file in the 'samples' directory.
  </p>
  <p>
  <td><a target="_new" href="<mm:url page="../citexml.jsp"><mm:param name="page"><%=new java.io.File(request.getServletPath()).getParent() + "/xsl/list.xsl"%></mm:param></mm:url>">view xsl/list.xsl</a></td>
  </p>
  <!-- check if the MyNews application was installed -->
  <mm:cloud>
  <mm:listnodes type="versions" constraints="[type] LIKE '%application%' AND [name] LIKE '%MyNews%'">
      <mm:first><mm:import id="mynews_installed">true</mm:import></mm:first>
  </mm:listnodes>
  </mm:cloud>
	<br />	
  <!-- Yes, installed, show the editwizard entry page -->
  <mm:present referid="mynews_installed">

  <table>    
   <tr><td>          
	<a href="<mm:url referids="referrer" page="${jsps}list.jsp">           
           <mm:param name="wizard">samples/people</mm:param>
           <mm:param name="nodepath">people</mm:param>
           <mm:param name="fields">number,firstname,lastname</mm:param>
           <mm:param name="orderby">number</mm:param>
           <mm:param name="directions">down</mm:param>
           </mm:url>">Person test</a>
  <!-- show how to jump to wizard.jsp directly -->
  (<a href="<mm:url referids="referrer" page="${jsps}wizard.jsp">
            <mm:param name="wizard">samples/people</mm:param>
            <mm:param name="objectnumber">new</mm:param>
            </mm:url>">Create</a>)
  </td><td>
     This is a '2 step' example. You can create/change the date for a
     person and relate a picture in the first step. In the second
   	 step then, you can relate articles to the person. We also
 	   demonstrate here how you can jump directly to the wizard to create a
	   new person (without having to go to the list first).
  </td>
  <td><a target="_new" href="<mm:url page="../citexml.jsp"><mm:param name="page"><%=new java.io.File(request.getServletPath()).getParent() + "/samples/people.xml"%></mm:param></mm:url>">view XML</a></td>
  </tr>
  <tr><td>
   <a href="<mm:url referids="referrer" page="${jsps}list.jsp">
           <mm:param name="wizard">samples/imageupload</mm:param>
           <mm:param name="nodepath">images</mm:param>
           <mm:param name="fields">title,owner</mm:param>
           <mm:param name="orderby">title</mm:param>
           </mm:url>" 
           onClick="return openListImages(this);">    
           Images</a> (search:  <input type="text" name="imagedesc"	value="" style="width:200px;text-align:left;" />)
   </td><td>
    A very simple image uploader. We show here how you could add
   search criteria. We also see that the delete prompt is overridden.
    </td>
  <td><a target="_new" href="<mm:url page="../citexml.jsp"><mm:param name="page"><%=new java.io.File(request.getServletPath()).getParent() + "/samples/imageupload.xml"%></mm:param></mm:url>">view XML</a></td>
 <tr>
    <tr><td>
    <a href="<mm:url referids="referrer" page="${jsps}list.jsp">
        	 <mm:param name="wizard">samples/news</mm:param>
           <mm:param name="nodepath">news</mm:param>
           <mm:param name="fields">number,title</mm:param>
           <mm:param name="orderby">number</mm:param>
           <mm:param name="directions">down</mm:param>
           </mm:url>">News</a>
     </td><td>       
      Demonstrated is how to use editwizards 'libs'. These are pieces
      of XML stored in the editwizard data directory which you can
      include in you own wizards. You'll find an example for
      'subwizards' here as well.
     </td>
  <td><a target="_new" href="<mm:url page="../citexml.jsp"><mm:param name="page"><%=new java.io.File(request.getServletPath()).getParent() + "/samples/news.xml"%></mm:param></mm:url>">view XML</a></td>
   </tr>
    <tr><td>
    <a href="<mm:url referids="referrer" page="${jsps}list.jsp">
		       <mm:param name="title">MyNews Magazine news</mm:param>
		       <mm:param name="startnodes">default.mags</mm:param>
        	 <mm:param name="wizard">samples/news</mm:param>
           <mm:param name="nodepath">mags,news</mm:param>
           <mm:param name="fields">news.number,news.title</mm:param>
           <mm:param name="orderby">news.number</mm:param>
           <mm:param name="directions">down</mm:param>
           </mm:url>">News</a>
     </td><td>       
        Only list news of default magazine (MyNews magazine).
     </td></tr>
    <tr><td>
    <a href="<mm:url referids="referrer" page="${jsps}list.jsp">
        	 <mm:param name="wizard">samples/mags</mm:param>
           <mm:param name="nodepath">mags</mm:param>
           <mm:param name="fields">number,title</mm:param>
           <mm:param name="orderby">number</mm:param>
           <mm:param name="directions">down</mm:param>
           </mm:url>">Magazines</a>
     </td><td>       
       Demonstrated is how to use 'posrel', and how to create 'optionlists'.
     </td>
  <td><a target="_new" href="<mm:url page="../citexml.jsp"><mm:param name="page"><%=new java.io.File(request.getServletPath()).getParent() + "/samples/mags.xml"%></mm:param></mm:url>">view XML</a></td>
      </tr>
     </table>
     
   </mm:present>

   <!-- MyNews applications was not installed, perhaps builders are missing and so on. Give warning. -->
   <mm:notpresent referid="mynews_installed">
   <h1>The 'MyNews' application was not deployed. Please deploy it before using this example.</h1>
   </mm:notpresent>

<hr />
   <a href="<mm:url page="../../taglib/showanypage.jsp"><mm:param name="page"><%=request.getServletPath()%></mm:param></mm:url>">Source of this page</a><br />
<a href="<mm:url page="../index.html" />">back</a>


</form>
</body>
</html>
