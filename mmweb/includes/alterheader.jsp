<%-- alterheader.jsp - an alternative header for mmbase.org 
--%><html xmlns="http://www.w3.org/1999/xhtml">
<head>
  <title>MMBase<mm:node number="$portal" notfound="skip">: <mm:field name="name" /></mm:node> - <mm:node number="$page" notfound="skip"><mm:field name="title" /></mm:node></title>
<link rel="stylesheet" type="text/css" href="<mm:url page="/css/mmmbase.css" />" />
<link rel="stylesheet" type="text/css" href="<mm:url page="/css/navi.css" />" />
<link rel="stylesheet" type="text/css" href="<mm:url page="/css/mmbase-head.css" />" />
<mm:node number="$portal"><mm:related path="posrel,templates">
  <mm:field id="url" name="templates.url" write="false"/> 
  <link rel="stylesheet" type="text/css" href="<mm:url page="$url" />" />
</mm:related></mm:node>
<link rel="shortcut icon" href="/media/favicon.ico" /> 
      <link rel="alternate" 
          type="application/rss+xml" 
          title="RSS" 
          href="<mm:url page="/rss" />" 
	  />


<script type="text/javascript" language="javascript" src="<mm:url page="/scripts/mmbase.js" />"><!-- MSIE needs this --></script>
<script type="text/javascript" language="javascript" src="<mm:url page="/scripts/navi.js" />"><!-- MSIE needs this --></script>
</head>
<body>
<%@ include file="nav.jsp" %>
<!-- head -->
<table border="0" cellspacing="0" cellpadding="0" width="100%">
<mm:node number="$portal"><mm:relatednodes role="posrel" type="images" 
  max="1" constraints="posrel.pos=11">
  <tr>
    <td class="tdheader"><a href="<mm:url page="/index.jsp" />"><img src="<mm:image />" alt="MMBase" width="826" height="55" /></a></td>
  </tr>
</mm:relatednodes></mm:node>
</table>
<div id="top">
<span class="breadcrum"><%
Nav bc = currentNav;
StringBuffer sb = new StringBuffer();
while( bc != null && bc.hasParent() ) {
  sb.insert(0,"<a href=\""+ bc.baseURL +"&amp;page="+ bc.id +"\">"+ bc.name+"</a>");
  bc  = bc.getParent();
  if (bc != null && bc.hasParent() ) {
	sb.insert(0," / ");
  }
} %><%= sb.toString() %></span>
<%-- mm:node number="$portal"><mm:field name="name" id="portalname" write="false" />
<ul id="portalnav">
  <li id="butmmbase"><a <mm:compare referid="portalname" value="MMBase">class="selected" </mm:compare>href="<mm:url page="/index.jsp"><mm:param name="portal" value="portal_mmbase" /></mm:url>">MMBase</a></li>
  <li id="butdev"><a <mm:compare referid="portalname" value="Developers">class="selected" </mm:compare>href="<mm:url page="/index.jsp"><mm:param name="portal" value="portal_developers" /></mm:url>">Developers</a></li>
  <li id="butfound"><a <mm:compare referid="portalname" value="Foundation">class="selected" </mm:compare>href="<mm:url page="/index.jsp"><mm:param name="portal" value="portal_foundation" /></mm:url>">Foundation</a></li>
</ul>
</mm:node --%>
</div>
<!-- /head -->
