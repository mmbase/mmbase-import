<%@ taglib uri="http://www.mmbase.org/mmbase-taglib-1.0" prefix="mm" %>
<%@page import="org.mmbase.bridge.*" %>
<mm:cloud name="mmbase" method="http" rank="administrator" jspvar="cloud">
<!DOCTYPE html PUBLIC "-//W3C//DTD XHTML 1.0 Transitional//EN" "http://www.w3.org/TR/xhtml/DTD/transitional.dtd">
<html xmlns="http://www.w3.org/TR/xhtml">
<head>
<title>Cache Monitor</title>
<meta http-equiv="pragma" value="no-cache" />
<meta http-equiv="expires" value="0" />
<link rel="stylesheet" type="text/css" href="<mm:url page="/mmbase/style/css/mmbase.css" />" />
</head>
<body class="basic" >
<!-- <%= cloud.getUser().getIdentifier()%>/<%=  cloud.getUser().getRank()%> -->
<table summary="email test" width="93%" cellspacing="1" cellpadding="3" border="0">

<mm:import externid="active" from="parameters" />

<mm:present referid="active">
  <mm:import externid="cache" from="parameters" required="true" />
  <mm:write referid="active" jspvar="active" vartype="String">
  <mm:write referid="cache" jspvar="cache" vartype="String">
  <%
    // have to test if this works...
    org.mmbase.cache.Cache.getCache(cache).setActive(active.equals("on") ? true : false);
  %>
  </mm:write></mm:write>
</mm:present>

<tr align="left">
  <th class="header" colspan="2">Cache Monitor - v1.0</th>
</tr>
<tr>
  <td class="multidata" colspan="2"><p>This tools hows the performance of the various MMBase caches.</p></td>
</tr>


<%
   java.util.Iterator i = org.mmbase.cache.Cache.getCaches().iterator();
   while (i.hasNext()) {
      org.mmbase.cache.Cache cache = org.mmbase.cache.Cache.getCache((String) i.next());
%>

<tr><td>&nbsp;</td></tr>
<tr align="left">
  <th class="header" colspan="2"><%= cache.getDescription() %> Cache Status</th>
</tr>
<tr>
  <td class="data" colspan="2">
  <% if(cache.isActive()) { %>
    <a href="<mm:url>
        <mm:param name="cache"><%=cache.getName()%></mm:param>
        <mm:param name="active">off</mm:param>
      </mm:url>" >On</a>
        <% } else { %>
    <a href="<mm:url>
        <mm:param name="cache"><%=cache.getName()%></mm:param>
        <mm:param name="active">on</mm:param>
      </mm:url>" >Off</a>
  <% } %>
  </td>
</tr>

<tr><td>&nbsp;</td></tr>
<tr align="left">
  <th class="header"><%= cache.getDescription() %> Cache Property</th>
  <th class="header">Value</th>
</tr>
<tr>
  <td class="data">Requests</td>
  <td class="data"><%= cache.getHits() + cache.getMisses() %></td>
</tr>
<tr>
  <td class="data">Hits</td>
  <td class="data"><%= cache.getHits() %></td>
</tr>
<tr>
  <td class="data">Misses</td>
  <td class="data"><%= cache.getMisses() %></td>
</tr>
<tr>
  <td class="data">Size</td>
  <td class="data"><%= cache.getSize() %></td>
</tr>
<tr>
  <td class="data">Performance</td>
  <td class="data"><%= cache.getRatio() * 100 %> %</td>
</tr>
<tr>
  <td class="data">Show first 500 entry's of the cache</td>
  <td class="navigate">
    <a href="<mm:url page="cache/showcache.jsp"><mm:param name="cache"><%= cache.getName() %></mm:param></mm:url>" ><img src="<mm:url page="/mmbase/style/images/next.gif" />" alt="next" border="0" align="right"></a>
  </td>
</tr>

<% }

 Module mmAdmin=ContextProvider.getDefaultCloudContext().getModule("mmadmin");

%>


<tr><td>&nbsp;</td></tr>
<tr align="left">
  <th class="header">Relation Cache Property</th>
  <th class="header">Value</th>
</tr>
<tr>
  <td class="data">Requests</td>
  <td class="data"><%=mmAdmin.getInfo("RELATIONCACHEREQUESTS",request,response)%></td>
</tr>
<tr>
  <td class="data">Hits</td>
  <td class="data"><%=mmAdmin.getInfo("RELATIONCACHEHITS",request,response)%></td>
</tr>
<tr>
  <td class="data">Misses</td>
  <td class="data"><%=mmAdmin.getInfo("RELATIONCACHEMISSES",request,response)%></td>
</tr>
<tr>
  <td class="data">Performance</td>
  <td class="data"><%=mmAdmin.getInfo("RELATIONCACHEPERFORMANCE",request,response)%></td>
</tr>

<tr><td>&nbsp;</td></tr>
<tr align="left">
  <th class="header">Temporary Node Cache Property</th>
  <th class="header">Value</th>
</tr>
<tr>
  <td class="data">Size</td>
  <td class="data"><%=mmAdmin.getInfo("TEMPORARYNODECACHESIZE",request,response)%></td>
</tr>

<tr><td>&nbsp;</td></tr>

<tr>
<td class="navigate"><a href="<mm:url page="../default.jsp" />" target="_top"><img src="<mm:url page="/mmbase/style/images/back.gif" />" alt="back" border="0" align="left" /></td>
<td class="data">Return to home page</td>
</tr>
</table>

</body></html>
</mm:cloud>
