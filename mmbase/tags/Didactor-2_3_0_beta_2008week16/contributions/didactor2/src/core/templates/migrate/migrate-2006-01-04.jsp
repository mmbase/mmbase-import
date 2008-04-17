<%@taglib uri="http://www.mmbase.org/mmbase-taglib-1.0" prefix="mm" %>
<%@taglib uri="http://www.didactor.nl/ditaglib_1.0" prefix="di" %>
<mm:content postprocessor="reducespace" expires="0">
<mm:cloud method="delegate" jspvar="cloud">
<%@include file="/shared/setImports.jsp" %>

<di:hasrole role="systemadministrator">

  Adding mailbox "Drafts"...
   <mm:list path="people,mailboxes" fields="people.number" distinct="true">
      <mm:node element="people" id="thisperson">
         <mm:import id="havedrafts" reset="true">false</mm:import>
         <mm:relatednodes type="mailboxes" constraints="type=11">
            <mm:import id="havedrafts" reset="true">true</mm:import>
         </mm:relatednodes>
         <mm:compare referid="havedrafts" value="false">
            <mm:createnode type="mailboxes" id="newdrafts">
               <mm:setfield name="name">Drafts</mm:setfield>
               <mm:setfield name="type">11</mm:setfield>
            </mm:createnode>
            <mm:createrelation role="related" source="thisperson" destination="newdrafts"/>
         </mm:compare>
      </mm:node>
   </mm:list>
   done. <br>

</di:hasrole>

</mm:cloud>
</mm:content>
