<!DOCTYPE html PUBLIC "-//W3C//DTD XHTML 1.0 Strict//EN" "DTD/xhtml1-strict.dtd">
<%@ taglib uri="http://www.mmbase.org/mmbase-taglib-1.0" prefix="mm" 
%><%@page import="org.mmbase.bridge.*,org.mmbase.cache.Cache" 
%><%@include file="../settings.jsp" 
%><mm:content expires="0">
<mm:cloud method="$method" authenticate="$authenticate" rank="administrator" jspvar="cloud">
<html xmlns="http://www.w3.org/TR/xhtml">
<head>
  <title>Cache Monitor</title>
  <link rel="stylesheet" type="text/css" href="<mm:url page="/mmbase/style/css/mmbase.css" />" />
</head>
<body class="basic" >
<!-- <%= cloud.getUser().getIdentifier()%>/<%=  cloud.getUser().getRank()%> -->
<table summary="email test" width="93%" cellspacing="1" cellpadding="3" border="0">

    <mm:import externid="active" from="parameters" />
    <mm:import externid="clear"  from="parameters" />

<mm:present referid="active">
  <mm:import externid="cache" from="parameters" required="true" />
  <mm:write referid="active" jspvar="active" vartype="String">
  <mm:write referid="cache" jspvar="cache" vartype="String">
  <% Cache.getCache(cache).setActive(active.equals("on") ? true : false); %>
  </mm:write></mm:write>
</mm:present>

<mm:present referid="clear">
  <mm:import externid="cache" from="parameters" required="true" />
  <mm:write referid="cache" jspvar="cache" vartype="String">
  <% Cache.getCache(cache).clear();   %>
  </mm:write>
</mm:present>

<tr align="left">
  <th class="header" colspan="6">Cache Monitor</th>
</tr>
<tr>
  <td class="multidata" colspan="6">
    <p>
      This tools hows the performance of the various MMBase caches. You can also (temporary) turn
      on/off the cache here. For a persistance change you should change caches.xml.
    </p>
  </td>
</tr>


<%
   java.util.Iterator i = Cache.getCaches().iterator();
   while (i.hasNext()) {
      Cache cache = Cache.getCache((String) i.next());
%>

<tr align="left">
  <th class="header" colspan="5"><%= cache.getDescription() %> Cache</th>
  <th class="header" colspan="1">
  <% if(cache.isActive()) { %>
    <a href="<mm:url>
        <mm:param name="cache"><%=cache.getName()%></mm:param>
        <mm:param name="active">off</mm:param>
      </mm:url>" >Turn off</a> | 
    <a href="<mm:url>
       <mm:param name="cache"><%=cache.getName()%></mm:param>
       <mm:param name="clear">clear</mm:param>
       </mm:url>">Clear</a>    
        <% } else { %>
    <a href="<mm:url>
        <mm:param name="cache"><%=cache.getName()%></mm:param>
        <mm:param name="active">on</mm:param>
      </mm:url>" >Trun on</a>
  <% } %>
  </th>
</tr>
<tr>
  <td class="data">Requests</td>
  <td class="data"><%= cache.getHits() + cache.getMisses() %></td>
  <td class="data">Hits</td>
  <td class="data"><%= cache.getHits() %></td>
  <td class="data">Misses</td>
  <td class="data"><%= cache.getMisses() %></td>
</tr>
<tr>
  <td class="data">Size</td>
  <td class="data"><%= cache.size() %> / <%= cache.maxSize() %></td>
  <td class="data">Performance</td>
  <td class="data"><%= cache.getRatio() * 100 %> %</td>
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

</body>
</html>
</mm:cloud>
</mm:content>