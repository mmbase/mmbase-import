<%@taglib uri="http://www.mmbase.org/mmbase-taglib-1.1" prefix="mm" %>
<%@taglib uri="http://www.didactor.nl/ditaglib_1.0" prefix="di" %>
<%-- expires is 0; show always new content --%>
<mm:content postprocessor="reducespace" expires="0">
<mm:cloud loginpage="/login.jsp" jspvar="cloud">
<%@include file="/shared/setImports.jsp" %>
<mm:node referid="user">
  <mm:relatedcontainer path="settingrel,components">
    <mm:constraint field="components.name" value="email" />
    <mm:size write="false" id="nr_settings" />
    <mm:compare referid="nr_settings" value="0">
      <mm:node number="component.email" id="n_email" />
      <mm:createrelation role="settingrel" source="user" destination="n_email" />
    </mm:compare>
    <mm:related>
      <mm:node element="settingrel" id="n_srel">
        <mm:relatednodescontainer type="settings" role="related">
          <mm:constraint field="name" value="mayforward"/>
          <mm:size write="false" id="nr_mf" />
          <mm:compare referid="nr_mf" value="0">
            <mm:createnode type="settings" id="n_setting">
              <mm:setfield name="name">mayforward</mm:setfield>
            </mm:createnode>
            <mm:createrelation source="n_srel" destination="n_setting" role="related" />
         </mm:compare>
         <mm:relatednodes>
           <mm:import externid="mayforward"/>
           <%-- Oneliner: no problems with spaces in the mayforward value --%>
           <mm:setfield name="value"><mm:compare referid="mayforward" value="on">1</mm:compare><mm:compare referid="mayforward" value="on" inverse="true">0</mm:compare></mm:setfield>
         </mm:relatednodes>
       </mm:relatednodescontainer>
     </mm:node>
   </mm:related>
 </mm:relatedcontainer>
</mm:node>
</mm:cloud>
</mm:content>
