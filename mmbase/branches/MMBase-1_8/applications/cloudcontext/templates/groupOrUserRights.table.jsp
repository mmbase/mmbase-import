<mm:import id="operations" vartype="list"><mm:write referid="visibleoperations" /></mm:import>
<table class="rights">
  <tr><th>Contexts</th><mm:stringlist referid="operations"><th><mm:write /></th></mm:stringlist></tr>
  <mm:functioncontainer>
    <mm:param name="grouporuser"><mm:field name="number" /></mm:param>
    <mm:listnodes id="thiscontext" type="mmbasecontexts" orderby="name">
      <tr>
        <td class="text"><a href="<mm:url referids="parameters,$parameters,thiscontext@context"><mm:param name="url">index_contexts.jsp</mm:param></mm:url>"><mm:nodeinfo type="gui" /><mm:field name="description"><mm:isnotempty>(<mm:write />)</mm:isnotempty></mm:field></a></td>
        <mm:stringlist referid="operations">
          <mm:param name="operation"><mm:write /></mm:param>
          <td
           <mm:booleanfunction name="parentsallow">
             class="parent"
           </mm:booleanfunction>
           >
            <mm:booleanfunction name="maygrant">
              <input type="checkbox" name="<mm:write />:<mm:field name="number" />"
              <mm:booleanfunction name="allows">
                checked="checked"
              </mm:booleanfunction>
              />
            </mm:booleanfunction>
            <mm:booleanfunction name="maygrant" inverse="true">
              <mm:booleanfunction name="allows">
                X
              </mm:booleanfunction>
            </mm:booleanfunction>
          </td>
        </mm:stringlist>
      </tr>
    </mm:listnodes>
  </mm:functioncontainer>
  <tr><td><input type="submit"  name="submit" value="<%=getPrompt(m, "submit")%>" /></td></tr>
  <tr><td class="text"><a href="<mm:url referids="back@url,parameters,$parameters" />"><%=getPrompt(m,"back")%></a></td></tr>
</table>
