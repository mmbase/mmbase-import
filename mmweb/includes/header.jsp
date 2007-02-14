<%-- Too little comments in this file to grasp the general idea by non-initiated --%>

<% String componentTitle = "header";%>
<%@include file="cachesettings.jsp" %>
<% String userFullName = (String) session.getAttribute("user_node_name"); %>
<%-- cache:cache key="<%= cacheKey %>" time="<%= expireTime %>" scope="application" --%>
<!-- <mm:time time="now" format=":LONG.LONG" /> user(<%= userFullName %>) -->
<html>
  <head>
     <link rel="stylesheet" type="text/css" href="<mm:url page="/css/mmmbase.css" />" />
     <link rel="stylesheet" type="text/css" href="<mm:url page="/css/navi.css" />" />
     <%--
	Add stylesheets that are linked to the portal node
     --%>
     <mm:node number="$portal"><mm:related path="posrel,templates">
         <mm:field id="url" name="templates.url" write="false"/> 
             <link rel="stylesheet" type="text/css" href="<mm:url page="$url"/>" />
         </mm:related>
     </mm:node>
     <%--
	Add stylesheets that are set into the pageContext by the surrounding page
     --%>
<%
     if(pageContext.findAttribute("cssScripts") != null){
	Iterator cssScriptsIterator = ((List) pageContext.findAttribute("cssScripts")).iterator();
	while(cssScriptsIterator.hasNext()){
%>
             <link rel="stylesheet" type="text/css" href="<%=(String)cssScriptsIterator.next()%>" />
<%
        }
     }
%>
     <link rel="shortcut icon" href="/media/favicon.ico" /> 
     <title>
        <mm:node number="$portal" notfound="skipbody">
	  <mm:field name="name" />
        </mm:node> - <mm:node number="$page" notfound="skipbody"><mm:field name="title" /></mm:node><% if(userFullName != null) { %> - Welcome <%= userFullName %> <% } %>
      </title><%--
			
			I made this hack to be able to add javascript as well. Must be changed
			Ernst Bunders
			
     --%><script type="text/javascript" language="javascript" src="<mm:url page="/scripts/mmbase.js" />"><!-- help IE --></script> 
     <script type="text/javascript" language="javascript" src="<mm:url page="/scripts/navi.js" />"><!-- help IE --></script>
<%
		if(pageContext.findAttribute("jScripts")!=null){
			Iterator jScripts=((List)pageContext.findAttribute("jScripts")).iterator();
				while(jScripts.hasNext()){
				  out.println("<script type=\"text/javascript\" language=\"javascript\" src=\""+(String)jScripts.next()+"\" ></script>");
				}
			}
			
%>
		 <meta http-equiv="imagetoolbar" content="no" />
    <%-- assuming this page is only included from index.jsp, and this site only deployed on www.mmbase.org --%>
     <%-- base href="http://www.mmbase.org/index.jsp" --%>
    </head>
<%
	 String onLoadEvents="";
		 if(pageContext.findAttribute("bLoadEvs")!=null){
			 Iterator bLoadEvs=((List)pageContext.findAttribute("bLoadEvs")).iterator();		 
			 StringBuffer allEvents=new StringBuffer();

			 while(bLoadEvs.hasNext()){
			   allEvents.append((String)bLoadEvs.next());
				 allEvents.append(" ; ");
			 }
			 while(allEvents.indexOf("\"")>-1){
         allEvents.setCharAt(allEvents.indexOf("\""),'\'');			 																 
			 }
			 onLoadEvents=allEvents.toString();
		 }
%>
<body <%=(!"".equals(onLoadEvents)?"onLoad=\""+onLoadEvents+"\"":"")%> >
<%@ include file="nav.jsp" %>
<%-- tables in tables in tables!! Why?! --%>
<table border="0" cellspacing="0" cellpadding="0" class="content" style="width: 100%">
  <tr>
   <td>
    <table border="0" cellspacing="0" cellpadding="0" class="layout">
    <tr>
     <td>
       <table cellpadding="0" cellspacing="0" border="0" id="hiero" width="100%">
         <tr>
          <mm:node number="$portal" notfound="skipbody">
           <mm:relatednodes role="posrel" type="images" max="3" orderby="posrel.pos"
           	constraints="posrel.pos < 10"> <%-- reserving >= 10 for new lay-out --%>
           <mm:index>
              <td width="33%" <mm:compare value="2" inverse="true"> background="<mm:image/>"</mm:compare> >
              <mm:compare value="2">
	          <a href="index.jsp"><img src="<mm:image/>" alt="MMBase" border="0" /></a>
              </mm:compare>
	      <mm:compare value="2" inverse="true">&nbsp;</mm:compare></td>
           </mm:index>
     </mm:relatednodes>
    </mm:node>
    </tr></table>
  </td>
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

<%  String rightContent = "";
    if(userFullName != null) {
      rightContent = "<a style=\"color: black;\" href=\""+request.getContextPath()+response.encodeURL("/login/mmaccount.jsp")+"\">Welcome&nbsp;" + org.apache.commons.lang.StringUtils.replace(userFullName, " ", "&nbsp;") + "</a>&nbsp;";
    } else {
      String orgLocation = request.getContextPath() + "/index.jsp" + ((request.getQueryString() == null) ? "" : ("?" + request.getQueryString()));
      orgLocation = java.net.URLEncoder.encode(orgLocation);
String myUrl = request.getContextPath() + "/login/mmlogin.jsp?orgLocation="+orgLocation;
      String encUrl = response.encodeURL(myUrl);
      rightContent = "<a style=\"color: black;\" href=\"" +encUrl+ "\">Login</a>";
    }
%>
<td align="right">&nbsp;&nbsp;<%=rightContent%>&nbsp;&nbsp;</td>
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
			<td class="navbar"><img src="/media/spacer.gif" alt="" width="152" height="380" /></td>
<mm:log>nehead</mm:log>
<%-- /cache:cache --%>

<!-- END FILE: /includes/header.jsp -->
