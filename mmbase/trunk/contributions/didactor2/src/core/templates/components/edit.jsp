<%@taglib uri="http://www.mmbase.org/mmbase-taglib-1.1" prefix="mm" %>
<%@taglib uri="http://www.didactor.nl/ditaglib_1.0" prefix="di" %>
<%@page import="nl.didactor.component.Component,java.util.*" %>
<mm:content postprocessor="reducespace" expires="0">
<mm:cloud method="delegate" jspvar="cloud">
<mm:import externid="component" />
<%@include file="/shared/setImports.jsp" %>
<html>
  <head>
    <link rel="stylesheet" type="text/css" href="<mm:treefile page="/css/base.css" objectlist="$includePath" referids="$referids" />" />
  </head>
  <body>
    <div style="margin-left: 10px">
      <di:hasrole role="systemadministrator">
        <mm:node number="$component">
          <h1><mm:field name="name" /></h1>
          <mm:field name="classname">
            <mm:isnotempty>
              Implementing class: <mm:write /> <br />
              <hr />
            </mm:isnotempty>
          </mm:field>
          <mm:import jspvar="cname"><mm:field name="name" /></mm:import>
          <%
            Component comp = Component.getComponent(cname);
            if (comp != null) {
              Vector scopes = comp.getScopes();
            %>
            <% if (comp.getTemplateBar() == null || "".equals(comp.getTemplateBar())) { %>
              This component is not shown in the navigation bars.
            <% } else { %>
              Cockpit bar: <%=comp.getTemplateBar()%> <br>
              Position in cockpit bar: <%=comp.getBarPosition()%> <br>
            <% } %>
            <hr />
            <h2>Activated providers, educations, classes</h2>
            <% if (scopes.contains("providers")) { %>
              <form method="post">
              <input type="hidden" name="process" value="true" />
              <mm:import externid="process" />
              <mm:listnodes type="providers">
                <mm:first><ul></mm:first>
                <li>
                  <mm:field id="n_provider" name="number" write="false" />
                  <mm:import id="checked" reset="true"></mm:import>
                  <mm:import externid="chk_${n_provider}" id="posted" reset="true"/>
                  <%-- Process any changes in checkboxes --%>
                  <mm:listcontainer path="providers,settingrel,components">
                    <mm:constraint field="providers.number" value="$n_provider" />
                    <mm:constraint field="components.number" value="$component" />
                    <mm:compare referid="process" value="true">
                      <mm:compare referid="posted" value="on">
                        <mm:size> 
                          <mm:islessthan value="1">
                            <mm:node id="node_provider" number="$n_provider" />
                            <mm:node id="node_component" number="$component" />
                            <mm:createrelation role="settingrel" source="node_provider" destination="node_component" />
                            <mm:remove referid="node_provider" />
                            <mm:remove referid="node_component" />
                          </mm:islessthan>
                        </mm:size>
                      </mm:compare>
                      <mm:compare referid="posted" value="on" inverse="true">
                        <mm:size> 
                          <mm:isgreaterthan value="0">
                            <mm:list>
                              <mm:deletenode element="settingrel" />
                            </mm:list>
                          </mm:isgreaterthan>
                        </mm:size>
                      </mm:compare>
                    </mm:compare>

                    <mm:size>
                      <mm:isgreaterthan value="0">
                        <mm:import id="checked" reset="true">checked="true"</mm:import>
                      </mm:isgreaterthan>
                    </mm:size>
                  </mm:listcontainer>

                  <input type="checkbox" <mm:write referid="checked" escape="none" /> name="chk_<mm:field name="number" />" />
                  <mm:field name="name" />
                  <% if (scopes.contains("educations")) { %>
                    <mm:relatednodes type="educations">
                      <mm:first><ul></mm:first>
                      <li>
                        <mm:field id="n_education" name="number" write="false" />
                        <mm:import id="checked" reset="true"></mm:import>
                        <mm:import externid="chk_${n_education}" id="posted" reset="true"/>
                        <mm:listcontainer path="educations,settingrel,components">
                          <mm:constraint field="educations.number" value="$n_education" />
                          <mm:constraint field="components.number" value="$component" />
                          <mm:compare referid="process" value="true">
                            <mm:compare referid="posted" value="on">
                              <mm:size> 
                                <mm:islessthan value="1">
                                  <mm:node id="node_education" number="$n_education" />
                                  <mm:node id="node_component" number="$component" />
                                  <mm:createrelation role="settingrel" source="node_education" destination="node_component" />
                                  <mm:remove referid="node_education" />
                                  <mm:remove referid="node_component" />
                                </mm:islessthan>
                              </mm:size>
                            </mm:compare>
                            <mm:compare referid="posted" value="on" inverse="true">
                              <mm:size> 
                                <mm:isgreaterthan value="0">
                                  <mm:list>
                                    <mm:deletenode element="settingrel" />
                                  </mm:list>
                                </mm:isgreaterthan>
                              </mm:size>
                            </mm:compare>
                          </mm:compare>
                          <mm:size>
                            <mm:isgreaterthan value="0">
                              <mm:import id="checked" reset="true">checked="true"</mm:import>
                            </mm:isgreaterthan>
                          </mm:size>
                        </mm:listcontainer>

                        <input type="checkbox" <mm:write referid="checked" escape="none" /> name="chk_<mm:field name="number" />" />
                        <mm:field name="name" />
                        <% if (scopes.contains("classes")) { %>
                          <mm:relatednodes type="classes">
                            <mm:first><ul></mm:first>
                            <li>
                              <mm:field id="n_class" name="number" write="false" />
                              <mm:import id="checked" reset="true"></mm:import>
                              <mm:import externid="chk_${n_class}" id="posted" reset="true"/>
                              <mm:listcontainer path="classes,settingrel,components">
                                <mm:constraint field="classes.number" value="$n_class" />
                                <mm:constraint field="components.number" value="$component" />
                                <mm:compare referid="process" value="true">
                                  <mm:compare referid="posted" value="on">
                                    <mm:size> 
                                      <mm:islessthan value="1">
                                        <mm:node id="node_class" number="$n_class" />
                                        <mm:node id="node_component" number="$component" />
                                        <mm:createrelation role="settingrel" source="node_class" destination="node_component" />
                                        <mm:remove referid="node_class" />
                                        <mm:remove referid="node_component" />
                                      </mm:islessthan>
                                    </mm:size>
                                  </mm:compare>
                                  <mm:compare referid="posted" value="on" inverse="true">
                                    <mm:size> 
                                      <mm:isgreaterthan value="0">
                                        <mm:list>
                                          <mm:deletenode element="settingrel" />
                                        </mm:list>
                                      </mm:isgreaterthan>
                                    </mm:size>
                                  </mm:compare>
                                </mm:compare>
                                <mm:size>
                                  <mm:isgreaterthan value="0">
                                    <mm:import id="checked" reset="true">checked="true"</mm:import>
                                  </mm:isgreaterthan>
                                </mm:size>
                              </mm:listcontainer>

                              <input type="checkbox" <mm:write referid="checked" escape="none" /> name="chk_<mm:field name="number" />" />
                              <mm:field name="name" />
                            </li>
                            <mm:last></ul></mm:last>
                          </mm:relatednodes>
                        <% } %>
                        <mm:last></ul></mm:last>
                      </li>
                    </mm:relatednodes>
                  <% } %>
                </li>
                <mm:last></ul></mm:last>
              </mm:listnodes>
              <input type="submit" value="save">
              </form>
            <% } %>
            <hr />
            <h2> Settings </h2>
            <%
              Iterator i = comp.getSettings().values().iterator();
              if (!i.hasNext()) {
                out.print("This component does not have any settings");
              }
            %>
            <ul>
            <%
              while (i.hasNext()) {
                Component.Setting setting = (Component.Setting)i.next();
                if (setting.getScope().contains("component")) {
                  %>
                  <li> <a href="editsetting.jsp?component=<mm:write referid="component" />&setting=<%=setting.getName()%>">
                    <%=setting.getName()%>
                  </a> </li>
                  <%
                }
              }
            %>
            </ul>
          <% } %>
        </mm:node>
      </di:hasrole>
    </div>
  </body>
</html>
</mm:cloud>
</mm:content>
