<%@include file="includes/top0.jsp" %>
<mm:cloud logon="admin" pwd="<%= (String) com.finalist.mmbase.util.CloudFactory.getAdminUserCredentials().get("password") %>" method="pagelogon" jspvar="cloud">
<%@include file="includes/top1_params.jsp" %>
<%@include file="includes/top2_cacheparams.jsp" %>
<%
if(NatMMConfig.hasClosedUserGroup) {
   %>
   <%@include file="/editors/mailer/util/memberid_get.jsp" %>
   <%
   if (memberid==null) {
      org.mmbase.bridge.Node thisMember = cloud.getNodeManager("deelnemers").createNode();
      thisMember.commit();
      memberid = thisMember.getStringValue("number");
      %>
      <%@include file="/editors/mailer/util/memberid_set.jsp" %>
      <%

   }
   %>
   <!-- member is: <%= memberid %> -->
   <%
}
%>
<!-- cache:cache groups="<%= paginaID %>" key="<%= cacheKey %>" time="<%= expireTime %>" scope="application" -->
<%@include file="includes/top3_nav.jsp" %>
<%@include file="includes/top4_head.jsp" %>
<%@include file="includes/top5_breadcrumbs_and_pano.jsp" %>
<!-- outer table -->
<table width="100%" border="0" cellspacing="0" cellpadding="0">
<tr>
  <td style="width:30%"></td>
  <td>
<!-- outer table -->
<table width="790" border="0" cellspacing="0" cellpadding="0" align="center" valign="top" style="margin-left:6px;">
<tr>
  <td style="vertical-align:top;width:165px;padding:2px;">
   <jsp:include page="includes/portal/login.jsp">
      <jsp:param name="s" value="<%= paginaID %>" />
      <jsp:param name="r" value="<%= rubriekID %>" />
      <jsp:param name="rs" value="<%= styleSheet %>" />
      <jsp:param name="sr" value="0" />
    </jsp:include>
    <jsp:include page="includes/teaser.jsp">
      <jsp:param name="s" value="<%= paginaID %>" />
      <jsp:param name="r" value="<%= rubriekID %>" />
      <jsp:param name="rs" value="<%= styleSheet %>" />
      <jsp:param name="teasersbypool" value="true" />
      <jsp:param name="sr" value="0" />
    </jsp:include>
    <jsp:include page="includes/portal/navleft.jsp">
      <jsp:param name="s" value="<%= paginaID %>" />
      <jsp:param name="r" value="<%= rubriekID %>" />
      <jsp:param name="rs" value="<%= styleSheet %>" />
      <jsp:param name="sr" value="0" />
    </jsp:include>
    <% 
    if(isIE) { 
      %>
      <div style="padding-top:20px;text-align:center;">
        <table><tr><td style="text-align:center; color:#B5B5B5; font-weight:bold;" 
                       onClick="this.style.behavior='url(#default#homepage)'; this.setHomePage('<%= HttpUtils.getRequestURL(request) + "?" + request.getQueryString() %>');"
                       onmouseover="this.style.cursor='pointer'">
          Maak dit <img src="includes/portal/heart.gif" alt="" border="0" /><br/>
          mijn startpagina
          </td></tr></table>
      </div>
      <%
    } %>
  </td>
  <td style="vertical-align:top;width:400px;padding:2px;">
    <jsp:include page="includes/portal/middle_top.jsp">
      <jsp:param name="s" value="<%= paginaID %>" />
      <jsp:param name="r" value="<%= rubriekID %>" />
      <jsp:param name="rs" value="<%= styleSheet %>" />
    </jsp:include>
    <jsp:include page="includes/portal/channels.jsp">
      <jsp:param name="s" value="<%= paginaID %>" />
      <jsp:param name="r" value="<%= rubriekID %>" />
      <jsp:param name="rs" value="<%= styleSheet %>" />
    </jsp:include>
    <jsp:include page="includes/portal/dossiers.jsp">
      <jsp:param name="o" value="<%= paginaID %>"/>
    </jsp:include>
    <%@include file="includes/portal/polls.jsp" %>
    <%--
    <jsp:include page="includes/portal/nieuwsbrief.jsp">
      <jsp:param name="s" value="<%= paginaID %>" />
      <jsp:param name="r" value="<%= rubriekID %>" />
      <jsp:param name="rs" value="<%= styleSheet %>" />
    </jsp:include>
    --%>
    <br/>
    <jsp:include page="includes/teaser.jsp">
      <jsp:param name="s" value="<%= paginaID %>" />
      <jsp:param name="r" value="<%= rubriekID %>" />
      <jsp:param name="rs" value="<%= styleSheet %>" />
      <jsp:param name="sr" value="1" />
    </jsp:include>
  </td>
  <td style="vertical-align:top;width:214px;padding:2px;padding-top:1px;">
    <form name="theClock">
      <input type=text name="theTime" class="headerBar" style="width:212px;border:none;font:normal;font-size:90%;text-align:right;padding-right:3px;">
    </form>
    <%
    String embargoLinkConstraint = "(link.embargo < '" + (nowSec+quarterOfAnHour) + "') AND "
                                + "(link.use_verloopdatum='0' OR link.verloopdatum > '" + nowSec + "' )";
    %>
    <mm:import id="video_image_url">null</mm:import>
    <mm:list nodes="<%= paginaID %>" path="pagina,contentrel,link,posrel,images" orderby="posrel.pos" max="1">
       <mm:node element="images">
          <mm:import id="video_image_url" reset="true"><mm:image template="s(214!x177!)" /></mm:import>
       </mm:node>
    </mm:list>

    <mm:compare referid="video_image_url" value="null">
       <mm:list nodes="<%= paginaID %>" path="pagina,contentrel,link" fields="link.number" constraints="<%= embargoLinkConstraint %>">
         <iframe src="<mm:url page="includes/portal/video.jsp">
                        <mm:param name="link"><mm:field name="link.number" /></mm:param>
                     </mm:url>" style="padding:0px;width:214px;height:177px;" id="video<mm:field name="link.number" />" scrolling="no"></iframe>
       </mm:list>
    </mm:compare>

    <mm:compare referid="video_image_url" value="null" inverse="true">
       <mm:list nodes="<%= paginaID %>" path="pagina,contentrel,link" fields="link.number" constraints="<%= embargoLinkConstraint %>">
         <iframe src="<mm:url page="includes/portal/video_image.jsp">
                        <mm:param name="video_image_url"><mm:write referid="video_image_url"/></mm:param>
                        <mm:param name="link"><mm:field name="link.number" /></mm:param>
                     </mm:url>" style="padding:0px;width:214px;height:177px;" id="video<mm:field name="link.number" />" scrolling="no"></iframe>
       </mm:list>
    </mm:compare>
    <jsp:include page="includes/portal/linklijst.jsp">
      <jsp:param name="s" value="<%= paginaID %>" />
      <jsp:param name="r" value="<%= rubriekID %>" />
      <jsp:param name="rs" value="<%= styleSheet %>" />
    </jsp:include>
    <jsp:include page="includes/portal/fun.jsp">
      <jsp:param name="s" value="<%= paginaID %>" />
      <jsp:param name="r" value="<%= rubriekID %>" />
      <jsp:param name="rs" value="<%= styleSheet %>" />
    </jsp:include>
    <jsp:include page="includes/portal/weblogs.jsp">
      <jsp:param name="s" value="<%= paginaID %>" />
      <jsp:param name="r" value="<%= rubriekID %>" />
      <jsp:param name="rs" value="<%= styleSheet %>" />
    </jsp:include>
  </td>
</tr>
</table>

<!-- outer table -->
   </td>
   <td style="width:30%"></td>
 </tr>
</table>
<!-- outer table -->

<%@include file="includes/footer.jsp" %>
<!-- /cache:cache -->
</mm:cloud>
