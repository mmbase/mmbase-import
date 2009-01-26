<%@include file="/taglibs.jsp" %>
<%@include file="../../includes/image_vars.jsp" %>
<%@include file="../../includes/time.jsp" %>
<mm:cloud jspvar="cloud">
<%
   String rubriekID = request.getParameter("r");
   String styleSheet = request.getParameter("rs");
   String paginaID = request.getParameter("s");
   String submitButton = "Login";
   boolean isIE = (request.getHeader("User-Agent").toUpperCase().indexOf("MSIE")>-1);
   
%>
<%@include file="/editors/mailer/util/memberid_get.jsp" %>
 <div class="headerBar" style="width:100%;">MIJN <img src="includes/portal/logo.gif" border="0" style="vertical-align:middle;"/></div>
 <% 
 if(memberid==null || cloud.getNode(memberid).getStringValue("email").equals("") ) { 
   %>
   <form name="emailform" method="post" target="" action="/editors/mailer/mail/index.jsp">
   <table cellspacing="0" cellpadding="0" border="0" style="width:165px;">
      <tr><td colspan="2" style="height:2px;"></td></tr>
      <tr>
        <td class="maincolor" style="width:70px;padding:1px;line-height:0.85em;"><nobr>&nbsp;emailadres&nbsp;</nobr></td>
        <td class="maincolor" style="width:95px;padding:0px;padding-right:1px;vertical-align:top;<% if(!isIE) { %>padding-top:1px;<% } %>">
           <input type="text" name="username" value="" style="width:100%;border:0;">
        </td>
      </tr>
      <tr><td colspan="2" style="height:2px;"></td></tr>
      <tr>
        <td class="maincolor" style="width:70px;padding:1px;line-height:0.85em;"><nobr>&nbsp;password&nbsp;</nobr></td>
        <td class="maincolor" style="width:95px;padding:0px;padding-right:1px;vertical-align:top;<% if(!isIE) { %>padding-top:1px;<% } %>">
           <input type="password" name="password" value=""  style="width:100%;border:0;">
        </td>
      </tr>
      <tr><td colspan="2" style="height:2px;"></td></tr>
      <tr>
        <td colspan="2" align="right">
            <input type="submit" value="<%= submitButton %>" class="submit_image" style="width:165;font-weight:normal;" />
        </td>
     </tr>
      <tr><td colspan="2" style="height:2px;"></td></tr>
   </table>
   </form>
   <%
 } %>
</mm:cloud>