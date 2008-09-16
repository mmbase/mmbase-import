<%@page import="nl.leocms.forms.MembershipForm" %>
<%@include file="/taglibs.jsp" %>
<mm:cloud jspvar="cloud">
<%
String localPath = request.getServletPath(); // localPath will always start with a forwardslash
localPath = localPath.substring(0,localPath.lastIndexOf("/"));
%>
<html:form action="<%= localPath + "/MembershipForm" %>" scope="session" name="MembershipForm" type="nl.leocms.forms.MembershipForm">
<bean:define id="nodeId" property="node" name="MembershipForm" scope="session" type="java.lang.String"/>
<table border="0" cellpadding="0" cellspacing="0" style="width:364px;vertical-align:top;padding:10px;padding-top:0px;">
   <tr>
      <td>
         <table class="dotline"><tr><td height="3"></td></tr></table>
         <mm:node number="<%= nodeId %>" jspvar="node">
			   <%= MembershipForm.getMessage(node,"html") %>
         </mm:node>
         <%--
			<bean:define id="payment_typeId" property="payment_type" name="MembershipForm" scope="session" type="java.lang.String"/>
			<% if (payment_typeId.equals("a")) {%>
			   <bean:message bundle="LEOCMS" key="membershipform.thank.you.a" />
			<% } else {
				String sPeriod = "";
			   if (payment_typeId.equals("m")) { sPeriod = "maand"; } else { sPeriod = "jaar"; } %>
					<bean:message bundle="LEOCMS" key="membershipform.thank.you.mj" arg0="<%= SubscribeAction.priceFormating(amountId.intValue()) %>" arg1="<%= sPeriod %>"/>
			<% } %>
         --%>
         <table class="dotline"><tr><td height="3"></td></tr></table>
		</td>
   </tr>
   <tr>
      <td align="right">
         <html:submit property="action" value="<%= MembershipForm.readyAction %>" styleClass="submit_image" style="width:150px;" />
      </td>
   </tr>
</table>   
</html:form>
</mm:cloud>