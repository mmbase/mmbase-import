<DOCTYPE html PUBLIC "-//W3C//DTD XHTML 1.0 Strict//EN" "DTD/xhtml1-strict.dtd">
<%@page language="java" contentType="text/html;charset=UTF-8" 
%><%@ taglib uri="http://www.mmbase.org/mmbase-taglib-1.0" prefix="mm" 
%><%@include file="config/read.jsp" %><mm:content language="$config.lang" type="text/html" expires="0">
<mm:cloud jspvar="cloud" method="asis">
<html>
<mm:write id="language" referid="config.lang" write="false" />
<head>
  <title><mm:write id="title" value='<%=m.getString("title")%>' /></title>      
  <link href="style/streammanager.css" type="text/css" rel="stylesheet" />
  <script src="<mm:url page="style/streammanager.js.jsp?dir=&amp;fragment=" />" language="javascript"><!--help IE--></script>
</head>
<mm:import externid="origin">media.myfragments</mm:import>
<mm:import externid="superorigin" required="true" />
<mm:import id="startnodes"><mm:write referid="origin" /></mm:import>

<body  onload="init('poolselector');">
  <!-- We are going to set the referrer explicitely, because we don't wont to depend on the 'Referer' header (which is not mandatory) -->     
  <mm:import id="referrer"><%=new java.io.File(request.getServletPath())%>?origin=<mm:write referid="origin" />&superorigin=<mm:write referid="superorigin" /></mm:import>
  <mm:import id="jsps"><mm:write referid="config.editwizards" />jsp/</mm:import>
  
  <mm:import id="videofragmentfieldoptions">
     <option value="videofragments.title,videofragments.subtitle,videofragments.intro,videofragments.body,videofragments2.title">*</option>
     <option value="videofragments.title"><mm:fieldlist nodetype="videofragments" fields="title"><mm:fieldinfo type="guiname" /></mm:fieldlist></option>
     <option value="videofragments.subtitle"><mm:fieldlist nodetype="videofragments" fields="subtitle"><mm:fieldinfo type="guiname" /></mm:fieldlist></option>
     <option value="videofragments.intro"><mm:fieldlist nodetype="videofragments" fields="intro"><mm:fieldinfo type="guiname" /></mm:fieldlist></option>
     <option value="videofragments.body"><mm:fieldlist nodetype="videofragments" fields="body"><mm:fieldinfo type="guiname" /></mm:fieldlist></option>
  </mm:import>

  <mm:import id="audiofragmentfieldoptions">
    <option value="audiofragments.title,audiofragments.subtitle,audiofragments.intro,audiofragments.body">*</option>
     <option value="audiofragments.title"><mm:fieldlist nodetype="audiofragments" fields="title"><mm:fieldinfo type="guiname" /></mm:fieldlist></option>
     <option value="audiofragments.subtitle"><mm:fieldlist nodetype="audiofragments" fields="subtitle"><mm:fieldinfo type="guiname" /></mm:fieldlist></option>
     <option value="audiofragments.intro"><mm:fieldlist nodetype="audiofragments" fields="intro"><mm:fieldinfo type="guiname" /></mm:fieldlist></option>
     <option value="audiofragments.body"><mm:fieldlist nodetype="audiofragments" fields="body"><mm:fieldinfo type="guiname" /></mm:fieldlist></option>
  </mm:import>

  <mm:import id="videosourcefieldoptions">
     <option value="videofragments.title,videofragments.subtitle,videofragments.intro,videofragments.body">*</option>
     <option value="videofragments.title"><mm:fieldlist nodetype="videofragments" fields="title"><mm:fieldinfo type="guiname" /></mm:fieldlist></option>
     <option value="videofragments.subtitle"><mm:fieldlist nodetype="videofragments" fields="subtitle"><mm:fieldinfo type="guiname" /></mm:fieldlist></option>
     <option value="videofragments.intro"><mm:fieldlist nodetype="videofragments" fields="intro"><mm:fieldinfo type="guiname" /></mm:fieldlist></option>
     <option value="videofragments.body"><mm:fieldlist nodetype="videofragments" fields="body"><mm:fieldinfo type="guiname" /></mm:fieldlist></option>
  </mm:import>

  <mm:import id="audiosourcefieldoptions">
    <option value="audiofragments.title,audiofragments.subtitle,audiofragments.intro,audiofragments.body">*</option>
     <option value="audiofragments.title"><mm:fieldlist nodetype="audiofragments" fields="title"><mm:fieldinfo type="guiname" /></mm:fieldlist></option>
     <option value="audiofragments.subtitle"><mm:fieldlist nodetype="audiofragments" fields="subtitle"><mm:fieldinfo type="guiname" /></mm:fieldlist></option>
     <option value="audiofragments.intro"><mm:fieldlist nodetype="audiofragments" fields="intro"><mm:fieldinfo type="guiname" /></mm:fieldlist></option>
     <option value="audiofragments.body"><mm:fieldlist nodetype="audiofragments" fields="body"><mm:fieldinfo type="guiname" /></mm:fieldlist></option>
  </mm:import>


<mm:node number="$origin">
  <mm:field name="owner" id="context" write="false" />
  <h1><mm:field name="name" /><mm:field name="description"><mm:isnotempty> - </mm:isnotempty><mm:write /></mm:field></h1>

  <table class="entrance">

    <tr>
      <th class="kop" colspan="2">Clips &amp; bronnen</th>
    </tr>
    <tr>
      <td><%=m.getString("clippingvideo")%></td>
      <td>
        <form style="display: inline; " id="clippingvideo" 
              action="<mm:url referids="referrer,language,origin,startnodes,context,superorigin" page="${jsps}list.jsp" />" 
              method="post">
          <select name="searchfields">
            <mm:write referid="videofragmentfieldoptions" escape="none" />
          </select>
          <%-- IE sucks, it adds extra space if there are lots of hidden inputs , so therefore ugly hackery with /> on next line --%>
          <input type="text" name="searchvalue" 
          /><input type="hidden" name="wizard" value="tasks/clipping/videofragments" 
          /><input type="hidden" name="nodepath" value="pools,related,videofragments,posrel,videofragments2" 
          /><input type="hidden" name="fields" value="pools.name,videofragments2.title,videofragments.number,videofragments.title,videofragments.owner" 
          /><input type="hidden" name="orderby" value="videofragments.number" 
          /><input type="hidden" name="directions" value="down" 
          /><input type="hidden" name="distinct" value="false" 
          /><input type="hidden" name="searchdirs" value="destination,source" 
          /><input type="hidden" name="main" value="videofragments" 
          />
        </form>
        <a  title="Zoek in bestaande clips" href="javascript:document.forms['clippingvideo'].submit();"><img src="media/search.gif" alt="<%=m.getString("newstream")%>" border="0" /></a>
        <a  title="Maak een nieuwe clip" href="<mm:url referids="referrer,origin,language,context,superorigin" page="${jsps}wizard.jsp">
                    <mm:param name="wizard">tasks/clipping/videofragments</mm:param>
                    <mm:param name="objectnumber">new</mm:param>
                 </mm:url>"><img src="media/new.gif" border="0" alt="<%=m.getString("newitems")%>" />
        </a>

        <a  title="Maak een nieuwe clip, via stream server" href="<mm:url referids="referrer,origin,language,context,superorigin" page="search_import.jsp">
                                                                     <mm:param name="type" value="video" />
                                                                  </mm:url>">
                                                                  Stream server<img src="media/new.gif" border="0" title="<%=m.getString("newitems")%>" />
        </a>
      </td>
    </tr>    
    <tr>
      <td><%=m.getString("clippingaudio")%></td>
      <td>
        <form style="display: inline; " id="clippingaudio" action="<mm:url referids="referrer,language,startnodes,context,origin,superorigin" page="${jsps}list.jsp" />" method="post">
           <select name="searchfields">
             <mm:write referid="audiofragmentfieldoptions" escape="none" />
           </select>
           <input type="text" name="searchvalue" 
          /><input type="hidden" name="wizard" value="tasks/clipping/audiofragments" 
           /><input type="hidden" name="nodepath" value="pools,audiofragments" 
           /><input type="hidden" name="fields" value="pools.name,audiofragments.number,audiofragments.title,audiofragments.owner" 
           /><input type="hidden" name="orderby" value="audiofragments.number" 
           /><input type="hidden" name="directions" value="down" 
           /><input type="hidden" name="distinct" value="false" 
           /><input type="hidden" name="main" value="audiofragments" 
           />
        </form>
        <a href="javascript:document.forms['clippingaudio'].submit();"><img src="media/search.gif" alt="<%=m.getString("newstream")%>" border="0" /></a>
        <a href="<mm:url referids="referrer,language,origin,context,superorigin" page="${jsps}wizard.jsp">
                   <mm:param name="wizard">tasks/clipping/audiofragments</mm:param>
                   <mm:param name="objectnumber">new</mm:param>
                 </mm:url>"><img src="media/new.gif" border="0" alt="<%=m.getString("newitems")%>" />
        </a>
        <a  title="Maak een nieuwe clip, via stream server" href="<mm:url referids="referrer,origin,language,context,superorigin" page="search_import.jsp">
                                                                     <mm:param name="type" value="audio" />
                                                                  </mm:url>">
                                                                  Stream server<img src="media/new.gif" border="0" title="<%=m.getString("newitems")%>" />
        </a>
      </td>
    </tr>
    <tr>
      <td>&nbsp;</td>
      <td>
        <a  title="Maak een nieuwe clip, via stream server" href="<mm:url referids="referrer,origin,language,context,superorigin" page="search_import.jsp">
        <mm:param name="type" value="any" />
        </mm:url>">
        Stream server<img src="media/new.gif" border="0" title="<%=m.getString("newitems")%>" />
        </a>
      </td>
    </tr>
    <tr><th class="kop" colspan="2">Toevoegen en bewerken van bronnen</th></tr>
    <tr>
      <td><%=m.getString("basevideo")%></td>
      <td><form style="display:inline; " id="basevideo" 
            action="<mm:url referids="referrer,language,origin,startnodes,context" page="${jsps}list.jsp" />" 
            method="post">
           <select name="searchfields">
               <mm:write referid="videosourcefieldoptions" escape="none" />
           </select>
           <input type="text" name="searchvalue" 
           /><input type="hidden" name="wizard" value="tasks/base/videofragments" 
           /><input type="hidden" name="nodepath" value="pools,base,videofragments" 
           /><input type="hidden" name="fields" value="videofragments.number,videofragments.title" 
           /><input type="hidden" name="orderby" value="videofragments.number" 
           /><input type="hidden" name="directions" value="down" 
           /><input type="hidden" name="main" value="videofragments" 
           />
        </form><a href="javascript:document.forms['basevideo'].submit();"><img src="media/search.gif" border="0"/></a>
       <a href="<mm:url referids="referrer,language,origin,context" page="${jsps}wizard.jsp">
            <mm:param name="wizard">tasks/base/videofragments</mm:param>
            <mm:param name="objectnumber">new</mm:param>
            </mm:url>"><img src="media/new.gif" border="0" alt="<%=m.getString("newstream")%>" /></a></td></tr>

  <tr>
   <td><%=m.getString("baseaudio")%></td><td><form style="display:inline; " id="baseaudio" action="<mm:url referids="referrer,language,context,startnodes,origin" page="${jsps}list.jsp" />" method="post">
           <select name="searchfields">
              <mm:write referid="audiosourcefieldoptions" escape="none" />             
           </select>
           <input type="text" name="searchvalue" 
           /><input type="hidden" name="wizard" value="tasks/base/audiofragments" 
           /><input type="hidden" name="nodepath" value="pools,base,audiofragments" 
           /><input type="hidden" name="fields" value="audiofragments.number,audiofragments.title" 
           /><input type="hidden" name="orderby" value="audiofragments.number" 
           /><input type="hidden" name="startnodes" value="<mm:write referid="origin" />" 
           /><input type="hidden" name="directions" value="down" 
           /><input type="hidden" name="main" value="audiofragments" 
           />
        </form><a href="javascript:document.forms['baseaudio'].submit();"><img src="media/search.gif" border="0"/></a>
       <a href="<mm:url referids="referrer,language,context,origin" page="${jsps}wizard.jsp">
            <mm:param name="wizard">tasks/base/audiofragments</mm:param>
            <mm:param name="objectnumber">new</mm:param>
            </mm:url>"><img src="media/new.gif" border="0" alt="<%=m.getString("newstream")%>" /></a></td></tr>


  </table>
  <hr />
  
  <%--p align="right"><mm:context>
   <mm:url id="referrer" write="false" referids="origin" page="entrancepage.jsp" />
   <a href="<mm:url referids="referrer" page="logout.jsp" />"><%=m.getString("logout")%></a>
   (<%=cloud.getUser().getIdentifier()%>)</mm:context></p --%>
  <p align="right">
    <a href="images/Media.jpg" target="new">Object model</a>
  </p>
  <p id="colofon">
    <img src="images/mmbase.png" />
  </p>

</mm:node>
</body>
</html>
</mm:cloud>
</mm:content>