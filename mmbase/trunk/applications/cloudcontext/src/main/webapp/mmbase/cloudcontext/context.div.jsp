<%org.mmbase.security.implementation.cloudcontext.Caches.waitForCacheInvalidation(); %>
<mm:context>
<div class="group">
<div class="environment">
 <p class="current">
    <%=getPrompt(m,"current_context")%>: <mm:nodeinfo type="gui" />
 </p>

<div class="rights">
   <form method="post" action="<mm:url referids="parameters,$parameters"><mm:param name="url">commit_context.jsp</mm:param></mm:url>">
   <input type="hidden" name="context" value="<mm:field name="number" />" />
   <table>
     <mm:fieldlist type="edit" fields="owner">
      <mm:maywrite>
       <tr><td><mm:fieldinfo type="guiname" /></td><td><mm:fieldinfo type="input" /></td></tr>
      </mm:maywrite>
      <mm:maywrite inverse="true">
       <tr><td><mm:fieldinfo type="guiname" /></td><td><mm:fieldinfo type="value" /></td></tr>
      </mm:maywrite>
    </mm:fieldlist>
   </table>
    <mm:import id="operations" vartype="list"><mm:write referid="visibleoperations" /></mm:import>
   <table class="rights">
    <tr><th><%=getPrompt(m, "groups")%></th><mm:stringlist referid="operations"><th><mm:write /></th></mm:stringlist></tr>
    <mm:functioncontainer>
    <mm:listnodes id="thisgroup" type="mmbasegroups" orderby="name">
      <mm:param name="grouporuser"><mm:field name="number" /></mm:param>
      <tr><td class="text"><a href="<mm:url referids="parameters,$parameters,thisgroup@group"><mm:param name="url">index_groups.jsp</mm:param></mm:url>"><mm:nodeinfo type="gui" /><mm:field name="description"><mm:isnotempty>(<mm:write />)</mm:isnotempty></mm:field></a></td>

        <mm:stringlist referid="operations">
        <mm:param name="operation"><mm:write /></mm:param>
        <td <mm:booleanfunction node="currentcontext" name="parentsallow">
               class="parent"
           </mm:booleanfunction>
          >
          <mm:booleanfunction  node="currentcontext" name="maygrant">
          <input type="checkbox" name="<mm:write />:<mm:field name="number" />"
             <mm:booleanfunction node="currentcontext" name="allows">
               checked="checked"
             </mm:booleanfunction>
          />
          </mm:booleanfunction>
          <mm:booleanfunction node="currentcontext" name="maygrant" inverse="true">
            <mm:booleanfunction node="currentcontext" name="allows">
            X
             </mm:booleanfunction>
          </mm:booleanfunction>
        </td>
        </mm:stringlist>
      </tr>
    </mm:listnodes>
    </mm:functioncontainer>
    <tr><td class="text"><input type="submit"  name="submit" value="<%=getPrompt(m,"submit")%>" /></td></tr>
    <tr><td class="text"><a href="<mm:url referids="parameters,$parameters"><mm:param name="url">index_contexts.jsp</mm:param></mm:url>"><%=getPrompt(m, "back")%></a></td></tr>
   </table>
   </form>
</div>


</div>
</mm:context>
