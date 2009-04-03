<%@taglib uri="http://www.mmbase.org/mmbase-taglib-2.0" prefix="mm" %>
<%@taglib uri="http://www.didactor.nl/ditaglib_1.0" prefix="di" %>
<%@page import="nl.didactor.component.Component,java.util.List"%>
<%-- expires is 0; show always new content --%>
<mm:content postprocessor="reducespace" expires="0">
<mm:cloud method="delegate" jspvar="cloud">
<%@include file="/shared/setImports.jsp" %>
<mm:node referid="user">
  <%
    Component[] comps = Component.getComponents();
    for (int i=0; i<comps.length; i++) {
      List settings = comps[i].getSettings("people");
      for (int j=0; j<settings.size(); j++) {
        Component.Setting setting = (Component.Setting)settings.get(j);
        if (setting.getType() == Component.Setting.TYPE_BOOLEAN) {
          String paramName = comps[i].getName() + "-" + setting.getName();
          String val1 = (String)request.getParameter(paramName);
          %>
          <mm:import externid="<%=paramName%>" jspvar="val" id="val" reset="true"/>
          <mm:import jspvar="usernumber" vartype="Integer"><mm:write referid="user" /></mm:import>
          <%
          if ("on".equals(val)) {
            comps[i].setObjectSetting(setting.getName(), usernumber.intValue(), cloud, "true");
          } else {
            comps[i].setObjectSetting(setting.getName(), usernumber.intValue(), cloud, "false");
          }
        }
      }
    }
  %>
</mm:node>
</mm:cloud>
</mm:content>
