<!DOCTYPE html PUBLIC "-//W3C//DTD XHTML 1.0 Strict//EN" "DTD/xhtml1-strict.dtd">
<%!
   String saveDevide(float f1, float f2){
      try{
         return "" + (f1 / f2);
      }catch(ArithmeticException e){
         return "";
      }
   }

	 String savePercentage(float f1, float f2){
      try{
         return "" + ((100f /f1) * f2);
      }catch(ArithmeticException e){
         return "";
      }
   }

   String saveName(String name){
      return name.replace(' ', '_');
   }
%>
<%@ taglib uri="http://www.mmbase.org/mmbase-taglib-1.0" prefix="mm"
%><%@page import="org.mmbase.bridge.*,org.mmbase.cache.*,java.util.*"
%><%@include file="../settings.jsp"
%><mm:content expires="0">
<mm:cloud method="$method" authenticate="$authenticate" rank="administrator" jspvar="cloud">
<mm:import externid="rs_show">-</mm:import>
<mm:import externid="rs_action">-</mm:import>
<mm:import externid="rs_name">-</mm:import>
<html xmlns="http://www.w3.org/TR/xhtml">
<head>
  <title>Cache Monitor</title>
  <link rel="stylesheet" type="text/css" href="<mm:url page="/mmbase/style/css/mmbase.css" />" />
  <style type="text/css">
      .label{
         background-color:    #cccccc;
         width:               50%;
         float:               left;
      }
      .data{
         float:               left;
      }
      .row{
         border-bottom:       1px solid black;
         overflow:            auto;
      }
      a:visited{
         color:               blue;
      }
      hr{
         color:               #333333;
      }
  </style>
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
      on/off the cache here. For a persistant change you should change caches.xml.
    </p>
  </td>
</tr>


<%
   List caches = new ArrayList();
   List queryCaches = new ArrayList();
       //first sort the caches


   for (Iterator i = Cache.getCaches().iterator(); i.hasNext(); ) {
      Cache cache = Cache.getCache((String) i.next());
      if(cache instanceof QueryResultCache){
         queryCaches.add(cache);
      }else{
         caches.add(cache);
      }
   }
   Collections.sort(queryCaches, new Comparator(){
        public int compare(Object o1, Object o2){
            Cache c1 = (Cache)o1;
            Cache c2 = (Cache)o2;
            return c1.getName().toLowerCase().compareTo(c2.getName().toLowerCase());
        }
    });
%>
   <tr><td colspan="6"><h3>Query Caches</h3></td></tr>
   <tr><td colspan="6"><p>Query caches are used to cache the result of different types of
   queries. These caches have a plugin like system of for (sets of) rules that will decide if
   a certain change in the cloud should invalidate a query from the cache. </p></td></tr>

<%
   for(Iterator i =  queryCaches.iterator(); i.hasNext(); ){
      QueryResultCache cache = (QueryResultCache) i.next();
%>
   <mm:import id="cacheName" reset="true"><%=saveName(cache.getName())%></mm:import>
   <tr><td colspan="6">  <a name="<mm:write referid="cacheName"/>"></td></tr>
   <%@include file="cache/cache_detail.jsp"%>


  <%-- Handle the possible action of globally switching strategies on or off --%>
  <mm:import id="globalStrategyEnabled" reset="true"><%= cache.getReleaseStrategy().isEnabled() ? "enabled" : "disabled"%></mm:import>
  <mm:present referid="rs_name">
    <mm:compare referid="rs_name" referid2="cacheName">
        <mm:present referid="rs_action">
            <mm:compare referid="rs_action" referid2="globalStrategyEnabled" inverse="true">
                <% cache.getReleaseStrategy().setEnabled( request.getParameter("rs_action").equals("enabled") ? true : false );%>
            </mm:compare>
        </mm:present>
    </mm:compare>
  </mm:present>

     <%-- determin the colors for the global strategy line--%>
    <mm:import id="globalStrategyEnabled" reset="true"><%= cache.getReleaseStrategy().isEnabled() ? "enabled" : "disabled"%></mm:import>
    <mm:compare referid="globalStrategyEnabled" value="enabled">
        <mm:import id="textStyle" reset="true">color: green;</mm:import>
        <mm:import id="linkStyle" reset="true">color: green;</mm:import>
    </mm:compare>
    <mm:compare referid="globalStrategyEnabled" value="enabled" inverse="true">
        <mm:import id="textStyle" reset="true">color: red;</mm:import>
        <mm:import id="linkStyle" reset="true">color: red;</mm:import>
    </mm:compare>

  <%-- Create the url to toggle global strategy active/inactive for this cache --%>
  <mm:import reset="true" id="url">
      <mm:url>
         <mm:param name="rs_name"><mm:write referid="cacheName"/></mm:param>
         <mm:compare referid="globalStrategyEnabled" value="disabled"> <mm:param name="rs_action">enabled</mm:param> </mm:compare>
         <mm:compare referid="globalStrategyEnabled" value="disabled" inverse="true"> <mm:param name="rs_action">disabled</mm:param> </mm:compare>
      </mm:url>#<mm:write referid="cacheName"/>
   </mm:import>

   <tr>
      <td  colspan="5" style="<mm:write referid="textStyle"/>">Events Analyzed : <%= cache.getReleaseStrategy().getTotalEvaluated()%>, Queries preserved : <%= cache.getReleaseStrategy().getTotalPreserved() %>, Queries flushed : <%= cache.getReleaseStrategy().getTotalEvaluated() - cache.getReleaseStrategy().getTotalPreserved()%></td>
      <td  ><a href="<mm:write referid="url" escape="none"/>"/><b><%= cache.getReleaseStrategy().isEnabled() ? "disable" : "enable"%></b></a> </td>
    </tr>

   <%-- create the toggle link for showing / hiding strategy details --%>
   <mm:import reset="true" id="url">
      <mm:url>
      <mm:compare referid="rs_show" referid2="cacheName" inverse="true"> <mm:param name="rs_show"><mm:write referid="cacheName"/></mm:param> </mm:compare>
      </mm:url>#<mm:write referid="cacheName"/>
   </mm:import>

   <tr>
      <td colspan="5">Switch cache release strategy statistics view</td>
      <td><a href="<mm:write referid="url"/>"><b>Toggle</b></a> </td>
   </tr>
<%--
   Release Strategy bit
--%>

   <mm:compare referid="rs_show" referid2="cacheName">

   <%-- show the statistics --%>
   <tr><td colspan="6">
   <table border="0" >
   <%
      ChainedReleaseStrategy base = cache.getReleaseStrategy();
      for(Iterator ii = base.iterator(); ii.hasNext(); ){
         ReleaseStrategy strategy = (ReleaseStrategy) ii.next();
   %>
      <mm:import id="strategyName" reset="true"><%=saveName(strategy.getName())%></mm:import>

      <%-- handel actions for this strategy --%>
      <mm:compare referid="rs_name" referid2="strategyName">
         <mm:compare referid="rs_action"  value="setActive">
            <% strategy.setEnabled(true); %>
         </mm:compare>
         <mm:compare referid="rs_action"  value="setInactive">
            <% strategy.setEnabled(false); %>
         </mm:compare>
      </mm:compare>


      <%-- create some action urls --%>
      <mm:remove referid="toggleActiveUrl"/>
      <mm:import id="toggleActiveUrl" reset="true">
         <mm:url id="toggleActiveUrl">
            <mm:param name="rs_show"><mm:write referid="cacheName"/></mm:param>
            <mm:param name="rs_action"><%=strategy.isEnabled() ? "setInactive" : "setActive"%></mm:param>
            <mm:param name="rs_name"><%=saveName(strategy.getName())%></mm:param>
         </mm:url>#<mm:write referid="cacheName"/>
      </mm:import>

      <%-- define the text style --%>
      <mm:import id="strategyEnabled" reset="true"><%= strategy.isEnabled() ? "enabled" : "disabled" %></mm:import>
      <mm:compare referid="strategyEnabled" value="enabled">
         <mm:import id="textStyle" reset="true">color: black;</mm:import>
         <mm:import id="linkStyle" reset="true">color: green;</mm:import>
      </mm:compare>
      <mm:compare referid="strategyEnabled" value="enabled" inverse="true">
         <mm:import id="textStyle" reset="true">color: #666666;</mm:import>
         <mm:import id="linkStyle" reset="true">color: red;</mm:import>
      </mm:compare>

      <%-- show the values --%>
            <tr >
               <td align="left" valign="top" style="width: 50%;">
                  <p><b><%=strategy.getName()%></b><p>
                  <p><%=strategy.getDescription()%></p>
               </td>
               <td style="padding: 0px;">
                     <div class="row">
                        <div class="label" style="<mm:write referid="textStyle"/>">enabled:</div>
                        <div class="data" ><a href="<mm:write escape="none" referid="toggleActiveUrl"/>">
                            <span style="<mm:write referid="linkStyle"/>"><%= strategy.isEnabled() ? "enabled" : "disabled"%>(press to toggle)</a> </span>
                        </div>
                     </div>
                     <div class="row">
                        <div class="label" style="<mm:write referid="textStyle"/>">total queries evaluated:</div>
                        <div class="data" style="<mm:write referid="textStyle"/>"><%=""+strategy.getTotalEvaluated()%></div>
                     </div>
                     <div class="row">
                        <div class="label" style="<mm:write referid="textStyle"/>">total queries preserved:</div>
                        <div class="data" style="<mm:write referid="textStyle"/>"><%=""+strategy.getTotalPreserved()%></div>
                     </div>
                     <div class="row">
                        <div class="label" style="<mm:write referid="textStyle"/>">total evaluation time (millis):</div>
                        <div class="data" style="<mm:write referid="textStyle"/>"><%=""+strategy.getTotalEvaluationTimeMillis()%></div>
                     </div>
                     <div class="row">
                        <div class="label" style="<mm:write referid="textStyle"/>">avarage evaluation time (millis):</div>
                        <div class="data" style="<mm:write referid="textStyle"/>"><%="" + saveDevide(strategy.getTotalEvaluationTimeMillis(), strategy.getTotalEvaluated())%></div>
                     </div>
                     <div class="row">
                        <div class="label" style="<mm:write referid="textStyle"/>">percentatge performance:</div>
                        <div class="data" style="<mm:write referid="textStyle"/>"><%="" + savePercentage(strategy.getTotalEvaluated(), strategy.getTotalPreserved())%> %</div>
                     </div>
               </td>
            </tr>
            <tr><td colspan="2"><hr/></td>  </tr>
   <%}%>
   </table>


      <div id="st_<%=cache.getName()%>" >
         <table>

         </table>
      </td>
   </td></tr>
   </mm:compare>
<%--
   End of release Strategy bit
--%>
<% } %>
<tr><td colspan="6"><h3>Other Caches </h3></td></tr>
<%
    //first sort the caches
    Collections.sort(caches, new Comparator(){
        public int compare(Object o1, Object o2){
            Cache c1 = (Cache)o1;
            Cache c2 = (Cache)o2;
            return c1.getName().toLowerCase().compareTo(c2.getName().toLowerCase());
        }
    });
    for( Iterator i = caches.iterator(); i.hasNext(); ){
    Cache cache = (Cache) i.next();
%>

   <%@include file="cache/cache_detail.jsp"%>
<%
   }

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