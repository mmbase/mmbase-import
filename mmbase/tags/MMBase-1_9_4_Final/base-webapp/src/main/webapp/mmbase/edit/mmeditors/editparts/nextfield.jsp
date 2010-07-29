    <frameset cols="220,*" framespacing="0">
      <frame src="<mm:url page="../editnode.jsp" />" name="selectarea" marginheight="0" marginwidth="0" />
<mm:write referid="field" vartype="String" jspvar="field" >
<%
   NodeManager manager = cloud.getNodeManager(managerName);
   FieldList fields=manager.getFields(NodeManager.ORDER_EDIT);
   Field newfield=null;
   for (Iterator i=fields.iterator(); i.hasNext(); ) {
     Field f=(Field)i.next();
     if (f.getName().equals(field)) {
        if (i.hasNext()) {
          newfield=(Field)i.next();
        }
     }
   }
   String basepath=request.getContextPath()+request.getServletPath();
   basepath=basepath.substring(0,basepath.lastIndexOf("/"));
   if (newfield!=null) {
     String editpath = newfield.getGUIType()+".jsp";
     if (!new java.io.File(pageContext.getServletContext().getRealPath(basepath+"/"+editpath)).exists()) {
       editpath = "autoedit.jsp";
     } %>
      <frame src="<mm:url page="<%=editpath%>" ><mm:param name="field"><%=newfield.getName()%></mm:param></mm:url>" name="workarea" scrolling="auto" marginheight="0" marginwidth="0" />
<% } else { %>
      <frame src="<mm:url page="../work.jsp" />" name="workarea" scrolling="auto" marginheight="0" marginwidth="0" />
<% } %>
</mm:write>
    </frameset>


