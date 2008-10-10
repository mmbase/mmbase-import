<%@taglib uri="http://www.mmbase.org/mmbase-taglib-2.0" prefix="mm" %>
<%@taglib uri="http://www.didactor.nl/ditaglib_1.0" prefix="di" %>
<%@page import="nl.didactor.component.Component,java.util.List" %>
<%-- expires is 0; show always new content --%>
<mm:content postprocessor="reducespace" expires="0">
<mm:cloud method="delegate" jspvar="cloud">
<%@include file="/shared/setImports.jsp" %>
<mm:node number="$user">
  <%
    Component[] comps = Component.getComponents();
    for (int i=0; i<comps.length; i++) {
      List settings = comps[i].getSettings("people");
      for (int j=0; j<settings.size(); j++) {
        Component.Setting setting = (Component.Setting)settings.get(j);
        if (setting.getType() == Component.Setting.TYPE_BOOLEAN) {
          %>
          <tr>
            <td/>
            <td>
              <input type="checkbox" name="<%=comps[i].getName()%>-<%=setting.getName()%>"
                <di:ifsetting component="<%=comps[i].getName()%>" setting="<%=setting.getName()%>">checked="checked"</di:ifsetting>
              />
              <di:translate key="<%=setting.getPrompt()%>" />
            </td>
          </tr>
          <%
        }
      }
    }
  %>
</mm:node>
</mm:cloud>
</mm:content>
