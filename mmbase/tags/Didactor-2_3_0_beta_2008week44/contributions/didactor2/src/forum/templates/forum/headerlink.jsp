<%--
This include should be used for links which consists out of a big icon:
(e.g.: as used for the icons above the agenda)

Parameters:
'text' The mouseover text for this image
'icon' The name of the icon, ../gfx/icons/${icon}.gif is used, make sure it exists!
'link' The onclick link of this image. 
--%>
<%@taglib uri="http://www.mmbase.org/mmbase-taglib-2.0" prefix="mm" %>
<%@taglib uri="http://www.didactor.nl/ditaglib_1.0" prefix="di" %>
<mm:cloud jspvar="cloud" name="mmbase" loginpage="/login.jsp">
  <%@ include file="/shared/setImports.jsp"%>

  <mm:import id="text" externid="text"/>
  <mm:import id="icon" externid="icon"/>
  <mm:import id="link" externid="link"/>
  <td>
    <a href="<mm:write referid="link"/>" class="didactor">
      <img src="<mm:treefile write="true" page="/forum/gfx/${icon}.gif" objectlist="$includePath"/>" width="50" height="28" title="<mm:write referid="text"/>" alt="<mm:write referid="text"/>" border="0"></a>
  </td>

</mm:cloud>
