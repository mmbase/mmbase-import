<%@page import="com.finalist.tree.*,nl.leocms.rubrieken.*,nl.leocms.authorization.forms.*,org.mmbase.security.*,java.util.*" %>
<%@include file="/taglibs.jsp" %>
<html>
<head>
<link href="<mm:url page="<%= editwizard_location %>"/>/style/color/wizard.css" type="text/css" rel="stylesheet"/>
<link href="<mm:url page="<%= editwizard_location %>"/>/style/layout/wizard.css" type="text/css" rel="stylesheet"/>
<title>Users</title>
</head>
<body style="overflow: auto">
<mm:cloud jspvar="cloud" rank='administrator'>
<script>
    function showTab(tab, hide) {
        document.getElementById(tab).style.display = 'inline';
        document.getElementById(hide).style.display = 'none';
    }
</script>
<style>
    input.select { font-height: 4px;}
</style>
<html:form action="/editors/usermanagement/UserAction">
<bean:define id="userForm" name="UserForm" type="nl.leocms.authorization.forms.UserForm" />
<html:hidden property="nodeNumber"/>
<div id="user">
<table class="stepscontent" style="width:200"><tr>
<td class="stepcurrent">Gegevens</td><td class="stepother"><a href="javascript:showTab('rollen','user')">Rollen</a></td>
</tr>
</table>
<table class="formcontent">
    <tr><td class="fieldname" width='180'>Account</td><td>
    <logic:equal name="UserForm" property="nodeNumber" value="-1">
        <html:text property="username" size='15' maxlength='15'/> <span class="notvalid"><html:errors bundle="LEOCMS" property="username"/>
    </logic:equal>
    <logic:notEqual name="UserForm" property="nodeNumber" value="-1">
        <bean:write name="UserForm" property="username"/>
    </logic:notEqual>
    </td></tr>
    <tr><td class="fieldname">Voornaam </td><td><html:text property="voornaam" size='30'/></td></tr>
    <tr><td class="fieldname">Tussenvoegsels </td><td><html:text property="tussen" size='15'/></td></tr>
    <tr><td class="fieldname">Achternaam </td><td><html:text property="achternaam" size='30'/></td></tr>
    <tr><td class="fieldname">Email </td><td><html:text property="email" size='30'/> <span class="notvalid"><html:errors bundle="LEOCMS" property="email"/></span></td></tr>
    <tr><td class="fieldname">Afdeling </td><td><html:text property="afdeling" size='30'/></td></tr>
    <tr><td class="fieldname">Notitie </td><td><html:textarea property="notitie" cols='60' rows='5'/></td></tr>
   <%
      if (userForm.getUsername() == null || !userForm.getUsername().equals("admin")
         || (userForm.getUsername().equals(cloud.getUser().getIdentifier()))) {
   %>
    <tr><td class="fieldname">Wachtwoord </td><td><html:password property="password" size='15' maxlength='15'/><span class="notvalid"><html:errors bundle="LEOCMS" property="password"/></td></tr>
    <tr><td class="fieldname" nowrap>Bevestig wachtwoord </td><td><html:password property="password2" size='15' maxlength='15'/><span class="notvalid"><html:errors bundle="LEOCMS" property="password2"/></td></tr>
   <%
      }
   %>
    <tr><td class="fieldname" nowrap>Email signalering</td><td class="field"><html:checkbox property="emailSignalering" style="width: auto;"/></td></tr>
    <tr><td class="fieldname">Rank</td><td>
    <logic:equal name="UserForm" property="username" value="admin">
        administrator
    </logic:equal>
    <logic:notEqual property="username" name="UserForm" value="admin">
        <html:select property="rank" size="1">
           <html:optionsCollection label="description" value="value" property="ranks" name="UserForm"/>
        </html:select>
    </logic:notEqual>
    </td></tr>
</table>
</div>
<div id="rollen" style="display: none">
<table class="stepscontent" style="width:200"><tr>
<td class="stepother"><a href="javascript:showTab('user','rollen')">Gegevens</a></td><td class="stepcurrent">Rollen</td>
</tr></table><br>
<p><b>Als er bij een rubriek geen rol is geselecteerd, gelden de rechten van de bovenliggende rubriek.</b></p>
<p style="padding: 0px; margin: 0px; bottom: 0px;">
<%
    HTMLTree t=new HTMLTree(new RubriekTreeModel(cloud));
    UserForm f=(UserForm)request.getAttribute("UserForm");
    t.setCellRenderer(new FormRenderer( f.getRollen() ) );
    t.setExpandAll(true);
    t.setImgBaseUrl("../img/");
    t.render( out );
%>
</p>
</div>
<br>
<table><tr><td><html:submit value='Opslaan' style="width:90"/></td><td><html:cancel value='Annuleren' style="width:90"/></td></tr>
</table>
</html:form>
</mm:cloud>
</body>
