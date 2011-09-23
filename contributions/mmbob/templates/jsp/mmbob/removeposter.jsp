<%@ include file="jspbase.jsp" %>
<mm:cloud>
<mm:content type="text/html" encoding="UTF-8" escaper="entities" expires="0">
<mm:import externid="forumid" />
<%@ include file="thememanager/loadvars.jsp" %>

<mm:import externid="postareaid" />

<!-- login part -->
  <%@ include file="getposterid.jsp" %>
<!-- end login part -->

<mm:locale language="$lang">
<%@ include file="loadtranslations.jsp" %>

<!-- action check -->
<mm:import externid="action" />
<mm:present referid="action">
 <mm:include page="actions.jsp" />
</mm:present>
<!-- end action check -->

<html>
<head>
   <link rel="stylesheet" type="text/css" href="<mm:write referid="style_default" />" />
   <title>MMBob</title>
</head>
<body>

<div class="header">
    <mm:import id="headerpath" jspvar="headerpath"><mm:function set="mmbob" name="getForumHeaderPath" referids="forumid"/></mm:import>
    <jsp:include page="<%=headerpath%>"/>
</div>

<div class="bodypart">
<mm:import externid="removeposterid"/>
<table cellpadding="0" cellspacing="0" class="list" style="margin-top : 50px;" width="75%">
  <tr>
    <th colspan="3" align="center" >
      <mm:write referid="mlg.Delete" /> <mm:write referid="mlg.member"/>
    </th>
  </tr>

  <form action="<mm:url page="index.jsp">
                    <mm:param name="forumid" value="$forumid" />
                                        <mm:param name="removeposterid" value="$removeposterid"/>
                    <mm:param name="admincheck" value="true" />
                </mm:url>" method="post">
  <tr>
    <td colspan="2" align="center">
      <mm:write referid="mlg.Delete" /> <mm:write referid="mlg.member"/> : <mm:node number="$removeposterid"><mm:field name="account" /> (<mm:field name="firstname" /> <mm:field name="lastname" />)</mm:node>
    </td>
  </tr>

  <tr>
    <td colspan="2" align="center"><mm:write referid="mlg.Are_you_sure" />?</td>
  </tr>

  <input type="hidden" name="admincheck" value="true">
  <input type="hidden" name="action" value="removeposter">


  <tr>
    <td align="center" ><input type="submit" value="<mm:write referid="mlg.Delete" />"> </td>
    <td>
      </form>
      <form action="<mm:url page="index.jsp">
    <mm:param name="forumid" value="$forumid" />
    </mm:url>"
    method="post">
      <p />
      <center>
      <input type="submit" value="<mm:write referid="mlg.Cancel" />">
      </center>
      </form>
    </td>
  </tr>
</table>
</div>

<div class="footer">
    <mm:import id="footerpath" jspvar="footerpath"><mm:function set="mmbob" name="getForumFooterPath" referids="forumid"/></mm:import>
    <jsp:include page="<%=footerpath%>"/>
</div>

</body>
</html>

</mm:locale>
</mm:content>
</mm:cloud>

