<%@page import="nl.leocms.authorization.*" %><%
AuthorizationHelper authHelper = new AuthorizationHelper(cloud);
UserRole userRole = authHelper.getRoleForUserWithPagina(authHelper.getUserNode(cloud.getUser().getIdentifier()), paginaID);    
if (userRole.getRol() >= Roles.SCHRIJVER) {
  %><mm:import id="isowner" /><%
} %>