<%@taglib uri="http://www.opensymphony.com/oscache" prefix="cache" 
%><% String componentTitle = "header";%>
<%@include file="cachesettings.jsp" %>
<cache:cache key="<%= cacheKey %>" time="<%= expireTime %>" scope="application">
<!-- <mm:time time="now" format=":LONG.LONG" /> -->
<html>
  <head>
     <link rel="stylesheet" type="text/css" href="<mm:url page="/css/mmbase-dev.css" />" />
     <link rel="stylesheet" type="text/css" href="<mm:url page="/css/mmbase-devnews.css" />" />
     <link rel="shortcut icon" href="/media/favicon.ico" /> 
     <title>
        <mm:node number="$portal" notfound="skipbody">
	  <mm:field name="name" />
        </mm:node> - <mm:node number="$page" notfound="skipbody"><mm:field name="title" /></mm:node>
      </title>
     <script type="text/javascript" language="javascript" src="/scripts/launchcenter.js"><!-- help IE --></script> 
     <meta http-equiv="imagetoolbar" content="no" />
   </head>
<body>
<mm:log>head</mm:log>
<%@ include file="nav.jsp" %>
	<mm:log>head2</mm:log>
<table border="0" cellspacing="0" cellpadding="0" class="content">
<tr><td>
<table border="0" cellspacing="0" cellpadding="0" class="layout">
<tr>
 <td><table cellpadding="0" cellspacing="0" border="0" id="hiero" width="100%">
   <tr><mm:node number="$portal" notfound="skipbody">
	<mm:log>1</mm:log>
     <mm:related path="posrel,images" fields="posrel.pos" max="3" orderby="posrel.pos">
	<mm:context>
           <mm:field name="posrel.pos" id="pos">
              <mm:node element="images">
		<td width="33%" <mm:compare referid="pos" value="2" inverse="true"> background="<mm:image/>"
                                </mm:compare> >
                <mm:compare referid="pos" value="2">
	              <a href="index.jsp"><img src="<mm:image/>" alt="MMBase" border="0" /></a>
                </mm:compare>
	        <mm:compare referid="pos" value="2" inverse="true">&nbsp;</mm:compare>
                </td>
	      </mm:node>
            </mm:field>
         </mm:context>
    </mm:related>
	<mm:log>2</mm:log>
     </mm:node>
    </tr></table>
  </td>
</tr>
<tr>
	<td><table border="0" width="100%" cellspacing="0" cellpadding="0" class="breadcrumbar">
	  <tr>
	    <td><span class="breadcrum"><%@ include file="/includes/breadcrums.jsp" %></span></td>
	  </tr>
	</table></td>
</tr>
<tr>
	<td class="white"><img src="/media/spacer.gif" alt="" width="626" height="1" /></td>
</tr>
<tr>
	<td><table border="0" cellspacing="0" cellpadding="0" class="layout">
		<tr>
			<td class="black"><img src="/media/spacer.gif" alt="" width="147" height="1" /></td>
			<td class="white"><img src="/media/spacer.gif" alt="" width="475" height="1" /></td>
			<td class="black"><img src="/media/spacer.gif" alt="" width="204" height="1" /></td>
		</tr>
		<tr>
			<td class="navbar"><img src="/media/spacer.gif" alt="" width="152" height="456" /></td>
<mm:log>nehead</mm:log>
</cache:cache>

