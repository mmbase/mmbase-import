<% String componentTitle = "header";%>
<%!

    // MM: hard node numbers ?!?!
    // To create an URL you need a request and a response (jsessionid encoding, contextpath)
    // these functions can't be right!

    /* Transforms a portal node number into the base url for that portal
     * Notice that the old format (/index.jsp?portal=123) will still work
     * and is still used underwater.
     * @param portalNumber The number of the node of the portal.
     * @returns A base url for the specified portal.
     */
    String createUrlXXX(String portalNumber) {
        String pn = portalNumber.intern();
        if(pn == "199") return "/devportal/index.jsp";
        if(pn == "202") return "/mmbaseportal/index.jsp";
        if(pn == "205") return "/foundationportal/index.jsp";
        // this should never happen - parameters should not be appended.
        return "/index.jsp?portal=" + pn;
    }
                                                                                                                                  
    /**
     * Like the former, but will render the first parameter.
     * Further parameters can be appended by &paramName=paramValue
     */
    String createUrlXXX(String portalNumber, String encodedParamName1, String encodedParamValue1) {
        String pn = portalNumber.intern();
        String param = encodedParamName1 + "=" + encodedParamValue1;
        if(pn == "199") return "/devportal/index.jsp?" + param;
        if(pn == "202") return "/mmbaseportal/index.jsp?" + param;
        if(pn == "205") return "/foundationportal/index.jsp?" + param;
        // this should never happen - parameters may be appended.
        return "/index.jsp?portal=" + pn + "&" + param;
    }
%>
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
     <script type="text/javascript" language="javascript" src="/scripts/launchcenter.js"><!-- help IE --></script> 
     <meta http-equiv="imagetoolbar" content="no" />
<%-- assuming this page is only included from index.jsp, and this site only deployed on www.mmbase.org --%>
     <%-- base href="http://www.mmbase.org/index.jsp" --%>
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
	    <td width="100%"><span class="breadcrum"><%@ include file="/includes/breadcrums.jsp" %></span></td>
<%  String rightContent = "";
    if(userFullName != null) {
      rightContent = "<a style=\"color: black;\" href=\"/login/mmaccount.jsp\">Welcome&nbsp;" + org.apache.commons.lang.StringUtils.replace(userFullName, " ", "&nbsp;") + "</a>&nbsp;";
    } else {
      String orgLocation = "/index.jsp" + ((request.getQueryString() == null) ? "" : ("?" + request.getQueryString()));
      orgLocation = java.net.URLEncoder.encode(orgLocation);
      rightContent = "<a style=\"color: black;\" href=\"/login/mmlogin.jsp?orgLocation=" + orgLocation + "\">login</a>";
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
