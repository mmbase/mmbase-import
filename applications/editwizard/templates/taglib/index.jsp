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
    @version  $Id: index.jsp,v 1.2 2002-05-07 13:35:57 michiel Exp $
 
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
  <mm:import id="jsps">../jsp/</mm:import>
	<h1>Editwizard Examples</h1>

  <!-- check if the MyNews application was installed -->
  <mm:cloud>
  <mm:listnodes type="versions" constraints="[type] LIKE '%application%' AND [name] LIKE '%MyNews%'">
      <mm:first><mm:import id="mynews_installed">true</mm:import></mm:first>
  </mm:listnodes>
  </mm:cloud>
	<br />	

  <!-- Yes, installed, show the editwizard entry page -->
  <mm:present referid="mynews_installed">
	<a href="<mm:url referids="referrer" page="${jsps}list.jsp">           
           <mm:param name="title">People</mm:param>
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
  <br />   
   <a href="<mm:url referids="referrer" page="${jsps}list.jsp">
           <mm:param name="title">Images</mm:param>
           <mm:param name="wizard">samples/imageupload</mm:param>
           <mm:param name="nodepath">images</mm:param>
           <mm:param name="fields">title</mm:param>
           <mm:param name="orderby">title</mm:param>
           </mm:url>" 
           onClick="return openListImages(this);">    
           Images</a> (search:  <input type="text" name="imagedesc" value="" style="width:200px;text-align:left;" />) <br />
    <a href="<mm:url referids="referrer" page="${jsps}list.jsp">
		       <mm:param name="title">News</mm:param>
        	 <mm:param name="wizard">samples/news</mm:param>
           <mm:param name="nodepath">news</mm:param>
           <mm:param name="fields">number,title</mm:param>
           <mm:param name="orderby">number</mm:param>
           <mm:param name="directions">down</mm:param>
           </mm:url>">News</a><br />  
   </mm:present>

   <!-- MyNews applications was not installed, perhaps builders are missing and so on. Give warning. -->
   <mm:notpresent referid="mynews_installed">
   <h1>The 'MyNews' application was not deployed. Please deploy it before using this example.</h1>
   </mm:notpresent>

<hr />

<a href="<mm:url page="../index.html" />">back</a>


</form>
</body>
</html>
