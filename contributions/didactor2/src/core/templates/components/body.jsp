
<mm:import id="linkedlist" jspvar="linkedlist" vartype="List"/>

<mm:listnodes type="components">
   <mm:remove referid="objectnumber"/>
   <mm:import id="objectnumber" jspvar="objectnumber"><mm:field name="number"/></mm:import>
   <%
      linkedlist.add( objectnumber );
   %>
</mm:listnodes>

  <div class="contentSubHeader">
     <a href="<mm:treefile page="/components/editcomponent.jsp" objectlist="$includePath" referids="$referids">
                 <mm:param name="component">-1</mm:param>
                 <mm:param name="callerpage"><%= sReturnURL %></mm:param>
                 <mm:param name="components_show_cockpit"><mm:write referid="components_show_cockpit"/></mm:param>
             </mm:treefile>"><fmt:message key="NEWCOMPONENT" /></a>
  </div>


  <div class="contentBody">
      <mm:listnodescontainer type="components">
         <mm:constraint field="number" referid="linkedlist" operator="IN"/>
         <di:table maxitems="10">
            <di:row>
               <di:headercell>Level</di:headercell>
               <di:headercell sortfield="name" default="true"><fmt:message key="COMPONENT" /></di:headercell>
               <di:headercell sortfield="classname"><fmt:message key="CLASSNAME" /></di:headercell>
            </di:row>

            <mm:listnodes>
               <di:row>
                  <mm:remove referid="link"/>
                  <mm:import id="link"><a href="<mm:treefile page="/components/editcomponent.jsp" objectlist="$includePath" referids="$referids">
                                                   <mm:param name="component"><mm:field name="number"/></mm:param>
                                                   <mm:param name="callerpage"><%= sReturnURL %></mm:param>
                                                   <mm:param name="components_show_cockpit"><mm:write referid="components_show_cockpit"/></mm:param>
                                                </mm:treefile>">

                  </mm:import>

                  <di:cell>
                     <mm:relatednodes type="providers" max="1">
                        <mm:write referid="link" escape="none"/><mm:nodeinfo type="type"/></a>
                     </mm:relatednodes>

                     <mm:relatednodes type="educations" max="1">
                        <mm:write referid="link" escape="none"/><mm:nodeinfo type="type"/></a>

                     </mm:relatednodes>
                  </di:cell>

                  <di:cell><mm:write referid="link" escape="none"/><mm:field name="name"/></a></di:cell>
                  <di:cell><mm:write referid="link" escape="none"/><mm:field name="classname"/></a></di:cell>
               </di:row>
            </mm:listnodes>
         </di:table>
      </mm:listnodescontainer>
   </div>
