<%@page session="false" import="org.mmbase.module.core.MMBase,org.mmbase.cache.Cache"
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
		  <%runtime.gc();%>
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
    <% Cache cache = Cache.getCache(type); 
       if (cache != null) {
    %>    
<%= cache.getHits() %>
<%= cache.getHits() + cache.getMisses() %>
    <% } %>
  </mm:compare>
</mm:write>
<%        
  //now add the uptime ans machine name (required by mrtg)
  int timeDiff =  (int)((System.currentTimeMillis()/1000) - MMBase.getMMBase().startTime);
        
  int days = timeDiff / (60 * 60 * 24);
  int hours =(timeDiff / (60  * 60)) % 24;
  int minutes = (timeDiff / 60) % 60 ;
  int seconds = timeDiff % 60;
  out.println("" + days +" days " + hours +":" + (minutes < 10 ? "0" : "") + minutes +":" + (seconds  < 10 ? "0" : "") + seconds +" (hours:minutes:seconds)");

 %>
<%= MMBase.getMMBase().getHost() %>

</mm:content>
