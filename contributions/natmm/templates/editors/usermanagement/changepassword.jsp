<%@page import="nl.leocms.util.*,nl.leocms.authorization.*" %>
<%@include file="/taglibs.jsp" %>
<html>
<head>
<link href="<mm:url page="<%= editwizard_location %>"/>/style/color/wizard.css" type="text/css" rel="stylesheet"/>
<link href="<mm:url page="<%= editwizard_location %>"/>/style/layout/wizard.css" type="text/css" rel="stylesheet"/>
<title>Wijzig wachtwoord</title>
</head>
<body>
<mm:cloud jspvar="cloud" rank='basic user'>

<%
   String username = cloud.getUser().getIdentifier();
   AuthorizationHelper authorizationHelper = new AuthorizationHelper(cloud);
   String succeeded = request.getParameter("succeeded");
%>
<h2>Wijzig wachtwoord</h2>

<%
   if ((succeeded != null) && (succeeded.equals("true"))) {
%>
      <font color="#FF0000"><b>Wachtwoord is succesvol gewijzigd.</b></font>
<%
   }
%>

    
<html:form action="/editors/usermanagement/ChangePasswordAction">
   <input type="hidden" name="nodenumber" value="<%= authorizationHelper.getUserNode(username).getNumber() %>">
   <table class="formcontent" style="width:auto;">
      <tr>
         <td class="fieldname" nowrap>Huidig wachtwoord</td>
          <td class="fieldname">
             <html:password property="password" size='15' maxlength='15'/></font>
             <span class="notvalid"><html:errors bundle="LEOCMS" property="password"/></span>
          </td>
       </tr>
       <tr>
         <td class="fieldname" nowrap>Nieuw wachtwoord</td>
          <td class="fieldname">
             <html:password property="newpassword" size='15' maxlength='15'/></font>
             <span class="notvalid"><html:errors bundle="LEOCMS" property="newpassword"/></span>
          </td>
       </tr>
      <tr>
       <td class="fieldname" nowrap>Bevestig nieuw wachtwoord</td>
          <td class="fieldname">
             <html:password property="confirmnewpassword" size='15' maxlength='15'/>
             <span class="notvalid"><html:errors bundle="LEOCMS" property="confirmnewpassword"/></span>
         </td>
       </tr>
       <tr>
          <td>&nbsp;</td>
          <td><html:submit value='Bewaar' style="width:90"/></td>
       </tr>
    </table>
</html:form>
</mm:cloud>
</body>

</html>