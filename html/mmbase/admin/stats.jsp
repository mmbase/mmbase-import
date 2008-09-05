<%@page session="false" import="org.mmbase.module.core.MMBase,org.mmbase.cache.Cache,org.mmbase.cache.CacheManager,org.mmbase.framework.FrameworkFilter"
%><%@ taglib uri="http://www.mmbase.org/mmbase-taglib-1.0" prefix="mm"
%><mm:content type="text/plain" postprocessor="reducespace" expires="10">

<%--
  This 'stats' page can be used by  MRTG.

  For example configuration see speeltuin/keesj/mrtgstats/mmbase-mrtg.conf.

  @since MMBase-1.7

 --%>

<mm:import externid="action">memory</mm:import>

<mm:write referid="action">
  <mm:compare value="memory">
     <% Runtime runtime = Runtime.getRuntime(); %>
     <mm:import externid="gc" />
		<mm:present referid="gc">
      <mm:cloud rank="administrator">
        <%  runtime.gc();%>
      </mm:cloud>
    </mm:present>
		  <%
        long freeMemory = runtime.freeMemory();
        long maxMemory  = runtime.totalMemory();
     %>
<%=maxMemory - freeMemory%><%--avaiable in jvm --%>
<%=maxMemory%><%-- free --%>
  </mm:compare>
  <mm:compare value="cache">
    <mm:import externid="cachetype" jspvar="type" vartype="string">Nodes</mm:import>
    <% Cache cache = CacheManager.getCache(type);
       if (cache != null) {
    %>
<%= cache.getHits() %>
<%= cache.getHits() + cache.getMisses() %>
    <% }  else { %>
0
0
    <% } %>
  </mm:compare>
  <mm:compare value="cachefill">
    <mm:import externid="cachetype" jspvar="type" vartype="string">Nodes</mm:import>
    <% Cache cache = CacheManager.getCache(type);
       if (cache != null) {
    %>
<%= cache.getSize() %>
<%= cache.getMaxSize() %>
    <% } else { %>
0
0
    <% } %>
  </mm:compare>
  <mm:compare value="requests">
    <mm:import externid="type" jspvar="type" vartype="string" />
    <%
    long chained = FrameworkFilter.getChainedRequests();
    long included = FrameworkFilter.getIncludedRequests();
    long forwarded = FrameworkFilter.getForwardedRequests();
    long errors = FrameworkFilter.getErrorRequests();
    long total = chained + included + forwarded + errors;
    long value;
    if (type == null || type.equals("completed")) {
      value = chained + included + forwarded;
    } else if (type.equals("chained")) {
       value = chained;
    } else if (type.equals("forwarded")) {
       value = forwarded;
    } else if (type.equals("included")) {
       value = included;
    } else if (type.equals("filtered")) {
       value = forwarded + included;
    } else if (type.equals("errors")) {
       value = errors;
    } else {
      throw new IllegalArgumentException("No requests type " + type);
    }
    %>
<%=value%>
<%=total%>
  </mm:compare>
  <mm:compare value="mrtgconfig">
<mm:import id="this"><%=request.getRequestURL()%></mm:import>
<mm:import id="thisserver"><%= request.getServerName() %><%=request.getContextPath().replaceAll("/", "_") %></mm:import>
#
# Copy/paste the following in your mrtg.cfg. Generated by <mm:write referid="this" />?action=mrtgconfig
#

#
# Memory useage for <mm:write referid="thisserver" />
#
Target[<mm:write referid="thisserver" />_memory]: `/usr/bin/wget -q -O- "<mm:write referid="this" />?action=memory"`
Title[<mm:write referid="thisserver" />_memory]: <mm:write referid="thisserver" /> memory usage
MaxBytes[<mm:write referid="thisserver" />_memory]: 50000000000
Options[<mm:write referid="thisserver" />_memory]:  integer, gauge, nopercent
kilo[<mm:write referid="thisserver" />_memory]: 1024
Ylegend[<mm:write referid="thisserver" />_memory]: memory usage
LegendO[<mm:write referid="thisserver" />_memory]: total :
LegendI[<mm:write referid="thisserver" />_memory]: used :
ShortLegend[<mm:write referid="thisserver" />_memory]: Bytes
PageTop[<mm:write referid="thisserver" />_memory]: <h1><mm:write referid="thisserver" /> memory information</h1>


<%
for (String t : new String[] {"chained", "filtered", "errors" }) {
  String id = "requests_" + t;
%>
#
# <mm:write referid="thisserver" /> REQUESTS <%=t%>
#
Target[<mm:write referid="thisserver" />_<%=id%>]: `/usr/bin/wget -q -O- "<mm:write referid="this" />?action=requests&type=<%=t%>"`
Title[<mm:write referid="thisserver" />_<%=id%>]: <mm:write referid="thisserver" /> <%=t%> requests
MaxBytes[<mm:write referid="thisserver" />_<%=id%>]: 100000000
Options[<mm:write referid="thisserver" />_<%=id%>]:  integer, nopercent
kilo[<mm:write referid="thisserver" />_<%=id%>]: 1000
Ylegend[<mm:write referid="thisserver" />_<%=id%>]: requests / s
LegendO[<mm:write referid="thisserver" />_<%=id%>]: <%=t%> requests
LegendI[<mm:write referid="thisserver" />_<%=id%>]: total requests :
ShortLegend[<mm:write referid="thisserver" />_<%=id%>]: requests / s
PageTop[<mm:write referid="thisserver" />_<%=id%>]: <h1><mm:write referid="thisserver" /> <%=t%> requests information</h1>

<%
}


org.mmbase.util.transformers.CharTransformer identifier = new org.mmbase.util.transformers.Identifier();
java.util.Iterator i  = CacheManager.getCaches().iterator();
while (i.hasNext()) {
   String cacheName = (String) i.next();
   Cache  cache     = CacheManager.getCache(cacheName);
   String id = identifier.transform(cache.getName());
     %>
#
# <mm:write referid="thisserver" /> <%=cache.getName() %>: <%=cache.getDescription() %>
#
Target[<mm:write referid="thisserver" />_<%=id%>]: `/usr/bin/wget -q -O- "<mm:write referid="this" />?action=cache&cachetype=<%=java.net.URLEncoder.encode(cache.getName(), "UTF-8")%>"`
Title[<mm:write referid="thisserver" />_<%=id%>]: <mm:write referid="thisserver" /> <%=cache.getName()%>
MaxBytes[<mm:write referid="thisserver" />_<%=id%>]: 100000000
Options[<mm:write referid="thisserver" />_<%=id%>]:  integer, nopercent
kilo[<mm:write referid="thisserver" />_<%=id%>]: 1000
Ylegend[<mm:write referid="thisserver" />_<%=id%>]: requests / s
LegendO[<mm:write referid="thisserver" />_<%=id%>]: hits :
LegendI[<mm:write referid="thisserver" />_<%=id%>]: requests :
ShortLegend[<mm:write referid="thisserver" />_<%=id%>]: requests / s
PageTop[<mm:write referid="thisserver" />_<%=id%>]: <h1><mm:write referid="thisserver" /> <%=cache.getName()%> information</h1>

<% id = id + "_fill"; %>
#
# <mm:write referid="thisserver" /> <%=cache.getName() %>: <%=cache.getDescription() %>
#
Target[<mm:write referid="thisserver" />_<%=id%>]: `/usr/bin/wget -q -O- "<mm:write referid="this" />?action=cachefill&cachetype=<%=java.net.URLEncoder.encode(cache.getName(), "UTF-8")%>"`
Title[<mm:write referid="thisserver" />_<%=id%>]: <mm:write referid="thisserver" /> <%=cache.getName()%>
MaxBytes[<mm:write referid="thisserver" />_<%=id%>]: 100000000
Options[<mm:write referid="thisserver" />_<%=id%>]:  integer, gauge, nopercent
kilo[<mm:write referid="thisserver" />_<%=id%>]: 1000
Ylegend[<mm:write referid="thisserver" />_<%=id%>]: cache useage
LegendO[<mm:write referid="thisserver" />_<%=id%>]: max size :
LegendI[<mm:write referid="thisserver" />_<%=id%>]: size :
ShortLegend[<mm:write referid="thisserver" />_<%=id%>]: # of entries
PageTop[<mm:write referid="thisserver" />_<%=id%>]: <h1><mm:write referid="thisserver" /> <%=cache.getName()%> information</h1>

 <% } %>

  </mm:compare>
   <mm:compare value="mrtgconfig" inverse="true">
<%
  //now add the uptime ans machine name (required by mrtg)
  int timeDiff =  (int)((System.currentTimeMillis()/1000) - MMBase.getMMBase().startTime);
  int days = timeDiff / (60 * 60 * 24);
%>
days <%= days %> <mm:time time='<%="" + timeDiff%>' format="HH:mm:ss" timezone="GMT" /> (hours:minutes:seconds)

<%= MMBase.getMMBase().getHost() %>
</mm:compare>
</mm:write>
</mm:content>
