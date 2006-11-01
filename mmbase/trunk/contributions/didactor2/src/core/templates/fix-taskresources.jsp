<%@taglib uri="http://www.mmbase.org/mmbase-taglib-2.0" prefix="mm" %>
<%@taglib uri="http://www.didactor.nl/ditaglib_1.0" prefix="di" %>
<html>
<mm:content postprocessor="reducespace" expires="0">
<mm:cloud method="delegate" jspvar="cloud">
<% if (!cloud.hasNodeManager("resources")) { 
%>
      You don't have builder 'resources'.<br/> 
<%
   } else {
%>
      Fixing bug with "resources" builder...<br/>
      <mm:listnodes type="resources" id="resource">
         <mm:field name="name" id="resourcename" write="false"/>
         <mm:field name="description" id="resourcedesc" write="false"/>
         <mm:createnode type="taskresources" id="taskresource">
            <mm:setfield name="name"><mm:write referid="resourcename"/></mm:setfield>
            <mm:setfield name="description"><mm:write referid="resourcedesc"/></mm:setfield>
         </mm:createnode>
         <mm:related path="posrel,coretasks">
            <mm:field name="coretasks.number" id="coretaskID" write="false"/>
            <mm:field name="posrel.pos" id="posrel_pos" write="false"/>
            <mm:createrelation role="posrel" source="coretaskID" destination="taskresource">
               <mm:setfield name="pos"><mm:write referid="posrel_pos"/></mm:setfield>
            </mm:createrelation>
         </mm:related>
         <mm:deletenode referid="resource" deleterelations="true"/>
      </mm:listnodes>
      <mm:list path="typerel" constraints="typerel.dnumber='resources'">
         <mm:node element="typerel">
            <mm:deletenode deleterelations="true"/>
         </mm:node>
      </mm:list>
      <mm:list path="versions" constraints="versions.name='resources' and versions.type='builder'">
         <mm:node element="versions">
            <mm:deletenode deleterelations="true"/>
         </mm:node>
      </mm:list>

      Done!
<% } %>

</mm:cloud>
</mm:content>
</html>
