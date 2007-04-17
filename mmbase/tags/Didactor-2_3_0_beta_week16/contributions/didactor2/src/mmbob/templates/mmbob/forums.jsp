<%-- !DOCTYPE html PUBLIC "-//W3C//DTD XHTML 1.0 Transitional//EN" "http://www.w3.org/TR/xhtml/DTD/transitional.dtd" --%>
<%@ page contentType="text/html; charset=utf-8" language="java" %>
<%@taglib uri="http://www.didactor.nl/ditaglib_1.0" prefix="di" %>
<%@taglib uri="http://www.mmbase.org/mmbase-taglib-1.0" prefix="mm" %>
<mm:cloud method="delegate" jspvar="cloud">
<%@include file="/shared/setImports.jsp" %>
<%@ include file="thememanager/loadvars.jsp" %>
<%@ include file="settings.jsp" %>
<HTML>
<HEAD>
   <link rel="stylesheet" type="text/css" href="<mm:write referid="style_default" />" />
   <link rel="stylesheet" type="text/css" href="<mm:treefile page="/css/base.css" objectlist="$includePath" referids="$referids" />" />
   <title>MMBob</title>
</head>

<!-- action check -->
<mm:import externid="action" />
<mm:present referid="action">
 <mm:include page="actions.jsp" />
</mm:present>
<!-- end action check -->

<center>

<table cellpadding="0" cellspacing="0" class="list" style="margin-top : 10px;" width="95%">
   <tr><th><di:translate key="mmbob.forumname" /></th><th><di:translate key="mmbob.numberofmessages" /></th><th><di:translate key="mmbob.numberofviews" /></th><th><di:translate key="mmbob.numberofmembers" /></th></tr>
  <mm:nodelistfunction set="mmbob" name="getForums">
            <tr>
              <mm:link page="start.jsp">
                <mm:param name="forumid"><mm:field name="id" /></mm:param>
                <td><a href="${_}"><mm:field name="name" /></a></td>
              </mm:link>
            <td><mm:field name="postcount" /></td>
            <td><mm:field name="viewcount" /></td>
            <td><mm:field name="posterstotal" /></td>
            </tr>
  </mm:nodelistfunction>
</table>
    <table cellpadding="0" cellspacing="0" class="list" style="margin-top : 10px;" width="95%">
    <tr><th align="left"><di:translate key="mmbob.adminfunctions" /></th></tr>
    <td>
    <p />
    <a href="<mm:url page="newforum.jsp"></mm:url>"><di:translate key="mmbob.addforum" /></a><br />
    <a href="<mm:url page="removeforum.jsp"></mm:url>"><di:translate key="mmbob.removeforum" /></a><br />
    <p />
    </td>
    </tr>
    </table>
</center>
</html>
</mm:cloud>
