<%-- !DOCTYPE html PUBLIC "-//W3C//DTD XHTML 1.0 Transitional//EN" "http://www.w3.org/TR/xhtml/DTD/transitional.dtd" --%>
<%@ page contentType="text/html; charset=utf-8" language="java" 
%><%@taglib uri="http://www.didactor.nl/ditaglib_1.0" prefix="di" 
%><%@taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c"
%><%@taglib uri="http://www.mmbase.org/mmbase-taglib-2.0" prefix="mm" 
%>
<mm:cloud method="delegate" jspvar="cloud">
<%@include file="/shared/setImports.jsp" %>
<%@ include file="thememanager/loadvars.jsp" %>
<%@ include file="settings.jsp" %>
<html>
<head>
  <link rel="stylesheet" type="text/css" href="<mm:write referid="style_default" />" />
  <link rel="stylesheet" type="text/css" href="<mm:treefile page="/css/base.css" objectlist="$includePath" referids="$referids" />" />
  <title>MMBob</title>
</head>
<mm:import externid="adminmode">false</mm:import>
<mm:import externid="forumid" />
<mm:import externid="pathtype">onlineposters</mm:import>
<mm:import externid="posterid" id="profileid" />

<!-- login part -->
<%@ include file="getposterid.jsp" %>
<!-- end login part -->


<!-- action check -->
<mm:import externid="action" />
<mm:present referid="action">
 <mm:include page="actions.jsp" />
</mm:present>
<!-- end action check -->

<center>
  <mm:include page="path.jsp?type=$pathtype" />
  <table cellpadding="0" cellspacing="0" class="list" style="margin-top : 20px;" width="90%">
    <tr>
      <th><di:translate key="mmbob.account" /></th>
      <di:ifsetting component="mmbob" setting="showlocation">
        <th><di:translate key="mmbob.location" /></th>
      </di:ifsetting>
      <th><di:translate key="mmbob.lastseen" /></th>
    </tr>
    <mm:nodelistfunction set="mmbob" name="getPostersOnline" referids="forumid">
      <mm:import id="dummyposterid" reset="true"><mm:field name="id" /></mm:import>
      <mm:list nodes="$dummyposterid" path="posters,people">
        <tr>
          <td><a href="<mm:treefile page="/portfolio/index.jsp" objectlist="$includePath" referids="$referids">
                         <mm:param name="contact"><mm:field name="people.number"/></mm:param>
                       </mm:treefile>" target="_top">
                       <di:person element="posters" /> (<mm:field name="posters.account" />)</a>
          </td>
          <di:ifsetting component="mmbob" setting="showlocation">
            <td><mm:field name="posters.location" /></td>
          </di:ifsetting>
          <td><mm:field name="posters.lastseen"><mm:time format="${timeFormat}" /></mm:field></td>
        </tr>
      </mm:list>
    </mm:nodelistfunction>
  </table>
</center>
</html>
</mm:cloud>
