<%@include file="/taglibs.jsp" %>
<%
String localPath = request.getServletPath(); // localPath will always start with a forwardslash
localPath = localPath.substring(0,localPath.lastIndexOf("/"));
%>
<mm:cloud jspvar="cloud">
<html:form action="<%= localPath +  "/SubscribeAction" %>" scope="session">
   <table border="0" cellpadding="0" cellspacing="0" style="width:280px;margin-right:94px;">
      <tr>
         <td>
            <table class="dotline"><tr><td height="3"></td></tr></table>
            <span class="colortitle">Bedankt voor uw aanmelding.</span><br/><br/>
            U ontvangt een email waarmee u uw aanmelding kunt bevestigen.<br/><br/>
         </td>
      </tr>
      <tr>
         <td align="right">
            <html:submit property="action" value="Naar agenda" styleClass="submit_image" style="width:150px;" />
         </td>
      </tr>
      <tr>
         <td>
            <br/><br/>
            <table class="dotline"><tr><td height="3"></td></tr></table>
         </td>
      </tr>
   </table>   
</html:form>
<br/>
<br/>
<br/>
</mm:cloud>
