<%@page language="java" contentType="text/html;charset=UTF-8"%>
<%@taglib uri="http://www.mmbase.org/mmbase-taglib-1.0" prefix="mm"%>
<mm:cloud method="http" rank="basic user" jspvar="cloud">

<%
   String contentNodeNumber = request.getParameter("contentnodenumber");
   if ((contentNodeNumber != null) && (!contentNodeNumber.equals(""))) {
      session.setAttribute("contentmodus.contentnodenumber", contentNodeNumber);
   }
   else {
      session.removeAttribute("contentmodus.contentnodenumber");
   }
   String paginaNumber = request.getParameter("page");
   String workpaneSource = "../empty.html";
/* hh   if ((paginaNumber != null) && (!paginaNumber.equals(""))) {
      nl.leocms.util.PaginaHelper pHelper = new nl.leocms.util.PaginaHelper(cloud);
      workpaneSource = pHelper.createPaginaUrl(paginaNumber, request.getContextPath());      
   }
*/
%>
<html>
   <head>
   <title>Pagina Management</title>
   </head>
    <frameset cols="300,*" framespacing="2" frameborder="1">
    <frame src="pagina_all.jsp" name="tree" frameborder="1" scrolling="auto">
    <frame src="<%=workpaneSource%>" name="workpane" frameborder="1" scrolling="auto">
    </frameset>
</html>
</mm:cloud>