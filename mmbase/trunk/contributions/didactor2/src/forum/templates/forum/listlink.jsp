<%--
This include is used for links which consist out of a (small) icon and a text
(e.g.: as used for the list of folders in my documents)

Parameters:
'name' Text displayed next to the icon
'icon' The name of the icon, ../gfx/icons/${icon}.gif is used, make sure it exists!
'link' The onclick link of this image.

--%>
<%@taglib uri="http://www.mmbase.org/mmbase-taglib-2.0" prefix="mm" %>
<%@taglib uri="http://www.didactor.nl/ditaglib_1.0" prefix="di" %>
<mm:cloud jspvar="cloud" name="mmbase" loginpage="/login.jsp">
  <%@ include file="/shared/setImports.jsp"%>

  <mm:import id="name" externid="name"/>
  <mm:import id="icon" externid="icon"/>

  <mm:import id="alt"><mm:write referid="icon"/></mm:import>
  <mm:compare referid="name" value="" inverse="true">
    <mm:remove referid="alt"/>
    <mm:import id="alt"></mm:import>
  </mm:compare>

  <mm:compare referid="icon" value="">
    <mm:remove referid="icon"/>
    <mm:import id="icon"><mm:write referid="name"/></mm:import>
  </mm:compare>

  <mm:import id="link" externid="link"/>

  <mm:compare referid="name" value="" inverse="true">
    <table cellspacing=0>
      <tr>
        <td valign="top">
          <a href="<mm:write referid="link"/>" class="forumlist">
            <img src="<mm:treefile write="true" page="/forum/gfx/${icon}.gif" objectlist="$includePath"/>" title="<mm:write referid="alt"/>" alt="<mm:write referid="alt"/>" width="18" height="17" border="0"></a>
        </td>
        <td>&nbsp;</td>
        <td valign=center>
          <a href="<mm:write referid="link"/>" class="forumlist">
            <mm:write referid="name"/>
          </a>
        </td>
      </tr>
    </table>
  </mm:compare>
</mm:cloud>
