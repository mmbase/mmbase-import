<%@page language="java" contentType="text/html;charset=UTF-8" 
%><%@ taglib uri="http://www.mmbase.org/mmbase-taglib-1.0" prefix="mm" 
%><mm:import externid="language">nl</mm:import><mm:locale language="$language"><mm:cloud jspvar="cloud" loginpage="login.jsp"><!DOCTYPE HTML PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN">
<html>
<% java.util.ResourceBundle m = null; // short var-name because we'll need it all over the place
   java.util.Locale locale = null; %>
<mm:write referid="language" jspvar="lang" vartype="string">
<%
  locale  =  new java.util.Locale(lang, "");
  m = java.util.ResourceBundle.getBundle("org.mmbase.util.media.resources.mediaedit", locale);
%>
</mm:write>

<head>
   <title><mm:write id="title" value="<%=m.getString("title")%>" /></title>
   <!--

    @since    MMBase-1.6
    @author   Michiel Meeuwissen
    @version  $Id: edit.jsp,v 1.1 2003-01-03 21:47:49 michiel Exp $
 
    -->
   <link href="style/streammanager.css" type="text/css" rel="stylesheet" />
<script src="<mm:url page="style/streammanager.js.jsp?dir=&amp;fragment=&amp;language=$language" />" language="javascript"><!--help IE--></script>
<head>
<mm:import externid="origin">media.myfragments</mm:import>
<mm:import id="startnodes"><mm:write referid="origin" /></mm:import>

<body  onload="init('poolselector');">
   <!-- We are going to set the referrer explicitely, because we don't wont to depend on the 'Referer' header (which is not mandatory) -->     
  <mm:import id="referrer"><%=new java.io.File(request.getServletPath())%>?origin=<mm:write referid="origin" /></mm:import>
  <mm:import id="jsps">/mmapps/editwizard/jsp/</mm:import>

  <mm:import id="videofieldoptions">
     <option value="videofragments.title"><mm:fieldlist nodetype="videofragments" fields="title"><mm:fieldinfo type="guiname" /></mm:fieldlist></option>
     <option value="videofragments.subtitle"><mm:fieldlist nodetype="videofragments" fields="subtitle"><mm:fieldinfo type="guiname" /></mm:fieldlist></option>
     <option value="videofragments.intro"><mm:fieldlist nodetype="videofragments" fields="intro"><mm:fieldinfo type="guiname" /></mm:fieldlist></option>
     <option value="videofragments.body"><mm:fieldlist nodetype="videofragments" fields="body"><mm:fieldinfo type="guiname" /></mm:fieldlist></option>
     <option value="videofragments.title,videofragments.subtitle,videofragments.intro,videofragments.body">*</option>
  </mm:import>

  <mm:import id="audiofieldoptions">
     <option value="audiofragments.title"><mm:fieldlist nodetype="audiofragments" fields="title"><mm:fieldinfo type="guiname" /></mm:fieldlist></option>
     <option value="audiofragments.subtitle"><mm:fieldlist nodetype="audiofragments" fields="subtitle"><mm:fieldinfo type="guiname" /></mm:fieldlist></option>
     <option value="audiofragments.intro"><mm:fieldlist nodetype="audiofragments" fields="intro"><mm:fieldinfo type="guiname" /></mm:fieldlist></option>
     <option value="audiofragments.body"><mm:fieldlist nodetype="audiofragments" fields="body"><mm:fieldinfo type="guiname" /></mm:fieldlist></option>
     <option value="audiofragments.title,audiofragments.subtitle,audiofragments.intro,audiofragments.body">*</option>
  </mm:import>


  <mm:node number="$origin">
  <h1><mm:field name="name" /><mm:field name="description"><mm:isnotempty> - </mm:isnotempty><mm:write /></mm:field></h1>
  </mm:node>

  <table class="entrance">

  <tr><th class="kop" colspan="2">Video</th></tr>
  <tr>
   <td><%=m.getString("basevideo")%></td>
   <td><form style="display:inline; " id="basevideo" 
            action="<mm:url referids="referrer,language,origin,startnodes" page="${jsps}list.jsp" />" 
            method="post">
           <select name="searchfields">
               <mm:write referid="videofieldoptions" />
           </select>
           <input type="text" name="searchvalue" />
           <input type="hidden" name="wizard" value="tasks/videofragments" />
           <input type="hidden" name="nodepath" value="pools,videofragments" />
           <input type="hidden" name="fields" value="videofragments.number,videofragments.title" />
           <input type="hidden" name="orderby" value="videofragments.title" />
           <input type="hidden" name="directions" value="down" />
        </form><a href="javascript:document.forms['basevideo'].submit();"><img src="media/search.gif" border="0"/></a>
       <a href="<mm:url referids="referrer,language,origin" page="${jsps}wizard.jsp">
            <mm:param name="wizard">tasks/videofragments</mm:param>
            <mm:param name="objectnumber">new</mm:param>
            </mm:url>"><img src="media/new.gif" border="0" alt="<%=m.getString("newstream")%>" /></a></td></tr>


   <tr><td><%=m.getString("clippingvideo")%></td>
        <td><form style="display: inline; " id="clippingvideo" 
                  action="<mm:url referids="referrer,language,origin,startnodes" page="${jsps}list.jsp" />" 
                  method="post">
           <select name="searchfields">
               <mm:write referid="videofieldoptions" />
           </select>
           <input type="text" name="searchvalue" />
           <input type="hidden" name="wizard" value="tasks/videoitemize" />
           <input type="hidden" name="nodepath" value="pools,videofragments,parent,videofragments2" />
           <input type="hidden" name="fields" value="videofragments2.number,videofragments.number,videofragments.title,videofragments2.title" />
           <input type="hidden" name="orderby" value="videofragments.title,videofragments2.title" />
          <input type="hidden" name="directions" value="down" />
          <input type="hidden" name="distinct" value="true" />
        </form><a href="javascript:document.forms['clippingvideo'].submit();"><img src="media/search.gif" alt="<%=m.getString("newstream")%>" border="0" /></a><a href="<mm:url referids="referrer,origin,language" page="${jsps}wizard.jsp">
            <mm:param name="wizard">tasks/videoitemize</mm:param>
            <mm:param name="objectnumber">new</mm:param>
            </mm:url>"><img src="media/new.gif" border="0" alt="<%=m.getString("newitems")%>" /></a></td></tr>





  <tr><th class="kop" colspan="2">Audio</th></tr>

  <tr>
   <td><%=m.getString("baseaudio")%></td><td><form style="display:inline; " id="baseaudio" action="<mm:url referids="referrer,language" page="${jsps}list.jsp" />" method="post">
           <select name="searchfields">
              <mm:write referid="audiofieldoptions" />             
           </select>
           <input type="text" name="searchvalue" />
           <input type="hidden" name="wizard" value="tasks/audiofragments" />
           <input type="hidden" name="nodepath" value="pools,audiofragments" />
           <input type="hidden" name="fields" value="audiofragments.number,audiofragments.title" />
           <input type="hidden" name="orderby" value="audiofragments.title" />
           <input type="hidden" name="startnodes" value="media.myfragments" />
           <input type="hidden" name="origin" value="media.myfragments" />
           <input type="hidden" name="directions" value="down" />
        </form><a href="javascript:document.forms['baseaudio'].submit();"><img src="media/search.gif" border="0"/></a>
       <a href="<mm:url referids="referrer,language" page="${jsps}wizard.jsp">
            <mm:param name="wizard">tasks/audiofragments</mm:param>
            <mm:param name="objectnumber">new</mm:param>
            <mm:param name="origin">media.myfragments</mm:param>
            </mm:url>"><img src="media/new.gif" border="0" alt="<%=m.getString("newstream")%>" /></a></td></tr>







   <tr><td><%=m.getString("clippingaudio")%></td><td><form style="display: inline; " id="clippingaudio" action="<mm:url referids="referrer,language" page="${jsps}list.jsp" />" method="post">
           <select name="searchfields">
               <mm:write referid="audiofieldoptions" />
           </select>
           <input type="text" name="searchvalue" />
           <input type="hidden" name="wizard" value="tasks/audioitemize" />
           <input type="hidden" name="nodepath" value="pools,audiofragments,parent,audiofragments2" />
           <input type="hidden" name="fields" value="audiofragments2.number,audiofragments.number,audiofragments.title,audiofragments2.title" />
           <input type="hidden" name="orderby" value="audiofragments.title,audiofragments2.title" />
           <input type="hidden" name="startnodes" value="media.myfragments" />
           <input type="hidden" name="origin" value="media.myfragments" />
          <input type="hidden" name="directions" value="down" />
          <input type="hidden" name="distinct" value="true" />
        </form><a href="javascript:document.forms['clippingaudio'].submit();"><img src="media/search.gif" alt="<%=m.getString("newstream")%>" border="0" /></a>
               <a href="<mm:url referids="referrer,language,origin" page="${jsps}wizard.jsp">
            <mm:param name="wizard">tasks/audioitemize</mm:param>
            <mm:param name="objectnumber">new</mm:param>
            </mm:url>"><img src="media/new.gif" border="0" alt="<%=m.getString("newitems")%>" /></a></td></tr>

  </table>
  <hr />
  <p align="right">
  <mm:context>
  <mm:import id="langs" vartype="list">en,nl</mm:import>
  <mm:aliaslist id="language" referid="langs">
     <a href="<mm:url referids="language,origin" />" ><mm:locale language="$_" jspvar="loc"><%= loc.getDisplayLanguage(loc)%></mm:locale></a><br />
  </mm:aliaslist>
  </mm:context>
  </p>
  <p align="right"><mm:context>
   <mm:url id="referrer" write="false" referids="origin,language" page="entrancepage.jsp" />
   <a href="<mm:url referids="referrer" page="logout.jsp" />"><%=m.getString("logout")%></a>
   (<%=cloud.getUser().getIdentifier()%>)</mm:context></p>
  <p align="right">
    <a href="images/Media.jpg" target="new">Object model</a>
  </p>
  <p id="colofon">
    <img src="images/mmbase.png" />
  </p>
</body>
</html>
</mm:cloud>
</mm:locale>