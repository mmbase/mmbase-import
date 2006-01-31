<%@taglib uri="http://www.mmbase.org/mmbase-taglib-1.1" prefix="mm" %>
<%@taglib uri="http://www.didactor.nl/ditaglib_1.0" prefix="di" %>
<%@page import="nl.didactor.component.Component,java.util.Vector"%>
<%-- expires is 0; show always new content --%>
<mm:content postprocessor="reducespace" expires="0">
<mm:cloud loginpage="/login.jsp" jspvar="cloud">
<%@include file="/shared/setImports.jsp" %>
<mm:node referid="user">
  <%
    Component[] comps = Component.getComponents();
    for (int i=0; i<comps.length; i++) {
      System.out.println("0");
      Vector settings = comps[i].getSettings("people");
      for (int j=0; j<settings.size(); j++) {
        Component.Setting setting = (Component.Setting)settings.get(j);
        if (setting.getType() == Component.Setting.TYPE_BOOLEAN) {
          String paramName = comps[i].getName() + "-" + setting.getName();
          String val1 = (String)request.getParameter(paramName);
          System.out.println("[" + paramName + "] = [" + val1 + "]");
          %>
          <mm:import externid="<%=paramName%>" jspvar="val" id="val"/>
          <mm:import jspvar="usernumber" vartype="Integer"><mm:write referid="user" /></mm:import>
          <%
          System.out.println("[" + paramName + "] = [" + val + "]");
          if ("on".equals(val)) {
            comps[i].setObjectSetting(setting.getName(), usernumber.intValue(), cloud, "1");
          } else {
            comps[i].setObjectSetting(setting.getName(), usernumber.intValue(), cloud, "0");
          }
        }
      }
    }
  %>
</mm:node>
</mm:cloud>
</mm:content>
