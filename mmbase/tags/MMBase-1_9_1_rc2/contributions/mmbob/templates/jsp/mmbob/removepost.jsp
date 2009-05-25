<%@ include file="jspbase.jsp" %>
<mm:cloud>
<mm:content type="text/html" encoding="UTF-8" escaper="entities" expires="0">
<mm:import externid="forumid" />
<%@ include file="thememanager/loadvars.jsp" %>

<mm:import externid="postareaid" />
<mm:import externid="postthreadid" />
<mm:import externid="postingid" />

<!-- action check -->
<mm:import externid="action" />
<mm:present referid="action">
 <mm:include page="actions.jsp" />
</mm:present>
<!-- end action check -->

<!-- login part -->
  <%@ include file="getposterid.jsp" %>
<!-- end login part -->

<mm:locale language="$lang">
<%@ include file="loadtranslations.jsp" %>

<mm:nodefunction set="mmbob" name="getPosting" referids="forumid,postareaid,postthreadid,postingid,posterid,imagecontext">
        <mm:field name="maychange" id="maychange" write="false" />
        <mm:field name="postcount" id="postcount" write="false" />
</mm:nodefunction>

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
<mm:compare referid="maychange" value="true">
<mm:node referid="postingid">

<table cellpadding="0" cellspacing="0" class="list" style="margin-top : 50px;" width="75%">
  <tr><th width="25%" align="left"><mm:write referid="mlg.Member" /></th><th align="left"><mm:write referid="mlg.Topic"/> : <mm:field name="subject" /></th></tr>
  <mm:import id="tdvar">listpaging</mm:import>
  <tr>
    <td class="<mm:write referid="tdvar" />" align="left">
      <mm:field name="createtime"><mm:time format="d MMMM, yyyy, HH:mm:ss" /></mm:field>
    </td>
    <td class="<mm:write referid="tdvar" />" align="right">
    </td>
  </tr>
 <mm:nodefunction set="mmbob" name="getPosting" referids="forumid,postareaid,postthreadid,postingid,posterid,imagecontext">
  <td class="<mm:write referid="tdvar" />" valign="top" align="left">
    <p>
      <b><mm:field name="poster" /></b>
    </p>
  </td>

  <td class="<mm:write referid="tdvar" />" valign="top" align="left">
    <mm:field name="edittime"><mm:compare value="-1" inverse="true">** <mm:write referid="mlg.last_time_edited"/> **<mm:field name="edittime"><mm:time format="d MMMM, yyyy, HH:mm:ss" /></mm:field></mm:compare><p /></mm:field>


  <mm:import id="tmpid" reset="true"><mm:field name="id"/></mm:import>
  <mm:compare referid="postingid" referid2="tmpid">
     <mm:field name="body" />
  </mm:compare>
</mm:nodefunction>

    <br /><br /><br /><br /><br />
  </td>
 </tr>
</table>
</mm:node>

<table cellpadding="0" cellspacing="0" class="list" style="margin-top : 5px;" width="75%">
  <tr><th colspan="3" align="center"><mm:write referid="mlg.Are_you_sure"/></th></tr>
  <tr><td>
  <mm:compare referid="postcount" value="1">
  <form action="<mm:url page="postarea.jsp">
                    <mm:param name="forumid" value="$forumid" />
                    <mm:param name="postareaid" value="$postareaid" />
                    <mm:param name="postthreadid" value="$postthreadid" />
                    <mm:param name="delpostingid" value="$postingid" />
                </mm:url>" method="post">
  </mm:compare>
  <mm:compare referid="postcount" value="1" inverse="true">
  <form action="<mm:url page="thread.jsp">
                    <mm:param name="forumid" value="$forumid" />
                    <mm:param name="postareaid" value="$postareaid" />
                    <mm:param name="postthreadid" value="$postthreadid" />
                    <mm:param name="delpostingid" value="$postingid" />
                </mm:url>" method="post">
  </mm:compare>

    <input type="hidden" name="moderatorcheck" value="true">
    <input type="hidden" name="action" value="removepost">
    <p />
    <center>
    <input type="submit" value="<mm:write referid="mlg.Yes_delete"/>">
        </center>
    </form>
    </td>
    <td>
    <form action="<mm:url page="thread.jsp">
    <mm:param name="forumid" value="$forumid" />
    <mm:param name="postareaid" value="$postareaid" />
    <mm:param name="postthreadid" value="$postthreadid" />
    </mm:url>"
    method="post">
    <p />
    <center>
    <input type="submit" value="<mm:write referid="mlg.Cancel"/>">
        </center>
    </form>
    </td>
    </tr>

</table>
</mm:compare>
<mm:compare referid="maychange" value="false">
    <table cellpadding="0" cellspacing="0" class="list" style="margin-top : 40px;" width="75%" align="center">
        <tr><th>MMBob system error</th></tr>
        <tr><td height="40"><b>ERROR: </b> action not allowed by this user </td></tr>
    </table>
</mm:compare>
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

