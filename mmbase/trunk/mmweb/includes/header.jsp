<% String componentTitle = "header";%>
<%@include file="cachesettings.jsp" %>
<% String userFullName = (String) session.getAttribute("user_node_name"); %>
<cache:cache key="<%= cacheKey %>" time="<%= expireTime %>" scope="application">
<!-- <mm:time time="now" format=":LONG.LONG" /> user(<%= userFullName %>) -->
<html>
  <head>
     <link rel="stylesheet" type="text/css" href="<mm:url page="/css/mmbase-devnews.css" />" />
<mm:node number="$portal"><mm:related path="posrel,templates">
     <link rel="stylesheet" type="text/css" href="<mm:field name="templates.url"/>" />
</mm:related></mm:node>
     <link rel="shortcut icon" href="/media/favicon.ico" /> 
     <title>
        <mm:node number="$portal" notfound="skipbody">
	  <mm:field name="name" />
        </mm:node> - <mm:node number="$page" notfound="skipbody"><mm:field name="title" /></mm:node><% if(userFullName != null) { %> - Welcome <%= userFullName %> <% } %>
      </title>
     <script type="text/javascript" language="javascript" src="<mm:url page="/scripts/launchcenter.js" />"><!-- help IE --></script> 
     <meta http-equiv="imagetoolbar" content="no" />
    <%-- assuming this page is only included from index.jsp, and this site only deployed on www.mmbase.org --%>
     <%-- base href="http://www.mmbase.org/index.jsp" --%>
   </head>
<body>
<%@ include file="nav.jsp" %>
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
	    <td width="100%"><span class="breadcrum"><%@ include file="/includes/breadcrums.jsp" %></span></td>
<%  String rightContent = "";
    if(userFullName != null) {
      rightContent = "<a style=\"color: black;\" href=\""+request.getContextPath()+response.encodeURL("/login/mmaccount.jsp")+"\">Welcome&nbsp;" + org.apache.commons.lang.StringUtils.replace(userFullName, " ", "&nbsp;") + "</a>&nbsp;";
    } else {
      String orgLocation = request.getContextPath() + "/index.jsp" + ((request.getQueryString() == null) ? "" : ("?" + request.getQueryString()));
      orgLocation = java.net.URLEncoder.encode(orgLocation);
String myUrl = request.getContextPath() + "/login/mmlogin.jsp?orgLocation="+orgLocation;
      String encUrl = response.encodeURL(myUrl);
      rightContent = "<a style=\"color: black;\" href=\"" +encUrl+ "\">login</a>";
    }
%>
<td align="right"><%=rightContent%></td>
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

<!-- END FILE: /includes/header.jsp -->
