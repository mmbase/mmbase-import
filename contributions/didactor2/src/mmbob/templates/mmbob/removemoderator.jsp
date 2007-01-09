<%-- !DOCTYPE html PUBLIC "-//W3C//DTD XHTML 1.0 Transitional//EN" "http://www.w3.org/TR/xhtml/DTD/transitional.dtd" --%>
<%@ page contentType="text/html; charset=utf-8" language="java" %>
<%@taglib uri="http://www.didactor.nl/ditaglib_1.0" prefix="di" %>
<%@taglib uri="http://www.mmbase.org/mmbase-taglib-1.0" prefix="mm" %>
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
<mm:import externid="forumid" />
<mm:import externid="postareaid" />

<!-- action check -->
<mm:import externid="action" />
<mm:present referid="action">
 <mm:include page="actions.jsp" />
</mm:present>
<!-- end action check -->


<center>
<table cellpadding="0" cellspacing="0" class="list" style="margin-top : 50px;" width="75%">
  <tr><th colspan="3" align="left" ><di:translate key="mmbob.removemoderatorfor" /> : <mm:node number="$postareaid"><mm:field name="name" /></mm:node>
  
  </th></tr>

  <form action="<mm:url page="postarea.jsp">
                    <mm:param name="forumid" value="$forumid" />
                    <mm:param name="postareaid" value="$postareaid" />
                    <mm:param name="admincheck" value="true" />
                </mm:url>" method="post">
    <tr><th align="left"><di:translate key="mmbob.presentmoderators" /></th><td colspan="2" align="left">
          <mm:nodelistfunction set="mmbob" name="getModerators" referids="forumid,postareaid">
            <mm:field name="account" /> (<di:person />)<br />
          </mm:nodelistfunction>
    <p />
    </td></tr>
    <tr><th align="left"><di:translate key="mmbob.possiblemoderators" /></th><td colspan="2">
          <select name="remmoderator">
          <mm:nodelistfunction set="mmbob" name="getModerators" referids="forumid,postareaid">
                <option value="<mm:field name="id" />"><mm:field name="account" /> (<di:person />)<br />
          </mm:nodelistfunction>
        </select>
    </td></tr>
    <input type="hidden" name="admincheck" value="true">
    <input type="hidden" name="action" value="removemoderator">
    <tr><th>&nbsp;</th><td align="middle" >
    <input type="submit" value="<di:translate key="mmbob.delete" />">
    </form>
    </td>
    <td>
    <form action="<mm:url page="postarea.jsp">
    <mm:param name="forumid" value="$forumid" />
    <mm:param name="postareaid" value="$postareaid" />
    </mm:url>"
    method="post">
    <p />
    <center>
    <input type="submit" value="<di:translate key="mmbob.cancel" />">
    </form>
    </td>
    </tr>

</table>
</html>
</mm:cloud>

