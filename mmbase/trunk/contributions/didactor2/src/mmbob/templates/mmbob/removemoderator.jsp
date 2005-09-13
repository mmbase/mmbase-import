<%-- !DOCTYPE html PUBLIC "-//W3C//DTD XHTML 1.0 Transitional//EN" "http://www.w3.org/TR/xhtml/DTD/transitional.dtd" --%>
<%@ page contentType="text/html; charset=utf-8" language="java" %>
<%@taglib uri="http://java.sun.com/jsp/jstl/fmt" prefix="fmt" %>
<%@taglib uri="http://www.mmbase.org/mmbase-taglib-1.0" prefix="mm" %>
<mm:cloud loginpage="/login.jsp" jspvar="cloud">
<%@include file="/shared/setImports.jsp" %>
   <%

      String bundleMMBob = null;

   %>

   <mm:write referid="lang_code" jspvar="sLangCode" vartype="String" write="false">

      <%

         bundleMMBob = "nl.didactor.component.mmbob.MMBobMessageBundle_" + sLangCode;

      %>

   </mm:write>

<fmt:bundle basename="<%= bundleMMBob %>">
<%@ include file="thememanager/loadvars.jsp" %>
<%@ include file="settings.jsp" %>
<html>
<head>
   <link rel="stylesheet" type="text/css" href="<mm:write referid="style_default" />" />
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
  <tr><th colspan="3" align="left" ><fmt:message key="RemoveModeratorFor"/> : <mm:node number="$postareaid"><mm:field name="name" /></mm:node>
  
  </th></tr>

  <form action="<mm:url page="postarea.jsp">
                    <mm:param name="forumid" value="$forumid" />
                    <mm:param name="postareaid" value="$postareaid" />
                    <mm:param name="admincheck" value="true" />
                </mm:url>" method="post">
    <tr><th align="left"><fmt:message key="PresentModerators"/></th><td colspan="2" align="left">
          <mm:nodelistfunction set="mmbob" name="getModerators" referids="forumid,postareaid">
            <mm:field name="account" /> (<mm:field name="firstname" /> <mm:field name="lastname" />)<br />
          </mm:nodelistfunction>
    <p />
    </td></tr>
    <tr><th align="left"><fmt:message key="PossibleModerators"/></th><td colspan="2">
          <select name="remmoderator">
          <mm:nodelistfunction set="mmbob" name="getModerators" referids="forumid,postareaid">
                <option value="<mm:field name="id" />"><mm:field name="account" /> (<mm:field name="firstname" /> <mm:field name="lastname" />)<br />
          </mm:nodelistfunction>
        </select>
    </td></tr>
    <input type="hidden" name="admincheck" value="true">
    <input type="hidden" name="action" value="removemoderator">
    <tr><th>&nbsp;</th><td align="middle" >
    <input type="submit" value="<fmt:message key="delete" />">
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
    <input type="submit" value="<fmt:message key="cancel" />">
    </form>
    </td>
    </tr>

</table>
</html>
</fmt:bundle>
</mm:cloud>

