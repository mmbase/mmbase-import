<%-- alterheader.jsp - an alternative header for mmbase.org 
--%><%-- Too little comments in this file to grasp the general idea by non-initiated 
--%><!DOCTYPE html PUBLIC "-//W3C//DTD XHTML 1.0 Transitional//EN"
        "http://www.w3.org/TR/xhtml1/DTD/xhtml1-transitional.dtd">
<html>
<head>
  <link rel="stylesheet" type="text/css" href="<mm:url page="/css/mmmbase.css" />" />
  <link rel="stylesheet" type="text/css" href="<mm:url page="/css/navi.css" />" />
  <mm:node number="$portal"><mm:related path="posrel,templates">
	   <mm:field id="url" name="templates.url" write="false"/> 
	   <link rel="stylesheet" type="text/css" href="<mm:url page="$url"/>" />
  </mm:related></mm:node>
  <link rel="shortcut icon" href="/media/favicon.ico" /> 
  <title>MMBase: <mm:node number="$portal" notfound="skipbody"><mm:field name="name" /></mm:node> - <mm:node number="$page" notfound="skipbody"><mm:field name="title" /></mm:node></title>
  <script type="text/javascript" language="javascript" src="<mm:url page="/scripts/mmbase.js" />"></script>
  <script type="text/javascript" language="javascript" src="<mm:url page="/scripts/navi.js" />"></script>
<style type="text/css" title="text/css" media="screen">
/* <![CDATA[ */
.balkdark
{
	color: #E6E9DD;
	font-family: "Lucida Grande", Arial, sans-serif;
	background: #033;
	padding: 4pt;
}
.balklite {
	color: #033;
	font-family: "Lucida Grande", Arial, sans-serif;
	background: #CCEB6F;
	padding: 4pt;
}
.line
{
	border-color: #CCEB6F;
	border-width: 0 0 1px 0;
	border-style: solid;
	padding: 4pt 4pt 1pt 4pt;
}
/* ]]> */
</style>
</head>
<body>
<%@ include file="nav.jsp" %>
<%-- tables in tables in tables!! Why?! --%>
<table border="0" cellspacing="0" cellpadding="0" class="content" style="width: 100%">
<tr>
  <td><table cellpadding="0" cellspacing="0" border="0" id="hiero" width="100%">
		<tr>
		 <mm:node number="$portal" notfound="skipbody">
		  <mm:relatednodes role="posrel" type="images" max="3" orderby="posrel.pos">
		  <mm:index>
			 <td width="33%" <mm:compare value="2" inverse="true"> background="<mm:image/>"</mm:compare> >
			 <mm:compare value="2">
			 <a href="index.jsp"><img src="<mm:image/>" alt="MMBase" border="0" /></a>
			 </mm:compare>
		 <mm:compare value="2" inverse="true">&nbsp;</mm:compare></td>
		  </mm:index>
	</mm:relatednodes>
   </mm:node>
  </tr></table></td>
</tr>
<tr>
	<td><table border="0" width="100%" cellspacing="0" cellpadding="0" class="breadcrumbar">
	  <tr>
	    <td width="100%"><span class="breadcrum"><%@ include file="/includes/breadcrums.jsp" %></span></td>

<!-- tab menu -->
    <mm:node number="home">
	 <mm:field name="number">
	  <mm:compare referid2="portal">
	  <!-- selected -->
	  <td style="background-color: rgb(255, 255, 255);">[&nbsp;&nbsp;<mm:field name="name"/>&nbsp;&nbsp;]&nbsp;</td>
	  </mm:compare>
	  <mm:compare referid2="portal" inverse="true">
	  <td><a href="<mm:url page="/"><mm:param name="portal"><mm:field name="number"/></mm:param></mm:url>">[&nbsp;&nbsp;<mm:field name="name"/>&nbsp;&nbsp;]&nbsp;</a></td>
	  </mm:compare>
	  </mm:field>
	</mm:node>

	<mm:list nodes="home" path="portals1,posrel,portals2" searchdir="destination" orderby="posrel.pos" directions="UP">
	  <mm:field name="portals2.number">
	  <mm:compare referid2="portal">
	  <td style="background-color: rgb(255, 255, 255);">[&nbsp;&nbsp;<mm:field name="portals2.name"/>&nbsp;&nbsp;]&nbsp;</td>
	  </mm:compare>
	  <mm:compare referid2="portal" inverse="true">
	  <td><a href="<mm:url page="/"><mm:param name="portal"><mm:field name="portals2.number"/></mm:param></mm:url>">[&nbsp;&nbsp;<mm:field name="portals2.name"/>&nbsp;&nbsp;]&nbsp;</a></td>
	  </mm:compare>
	  </mm:field>
    </mm:list>
<!-- end tab menu -->

		<td align="right">&nbsp;&nbsp;&nbsp;&nbsp;</td>
	  </tr>
	</table></td>
</tr>
</table>
<!-- END FILE: /includes/alterheader.jsp -->
