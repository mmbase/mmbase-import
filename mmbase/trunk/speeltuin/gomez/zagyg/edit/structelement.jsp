<mm:import externid="submit" />
<mm:import externid="struct_owner" />
<mm:nodeinfo id="actualtype" type="nodemanager" write="false" />
<span class="editintro">
  <mm:field name="gui()" />
</span>
<span class="editlinks">
<%--  <%@include file="util/findtemplate.jsp" %>
  <mm:node number="$template">
    <mm:field name="url" ><mm:isnotempty>(<mm:write />)</mm:isnotempty></mm:field>
  </mm:node>
--%>
  <mm:isnotempty referid="submit">
    <mm:context>
      <mm:maychangecontext>
        <mm:setcontext><mm:write referid="struct_owner" /></mm:setcontext>
      </mm:maychangecontext>
      <mm:field id="owner" name="owner" write="false" />
      <%-- also give away all relations --%>
      <mm:listrelations>
        <mm:maychangecontext>
          <mm:setcontext><mm:write referid="struct_owner" /></mm:setcontext>
        </mm:maychangecontext>
      </mm:listrelations>
    </mm:context>
  </mm:isnotempty>

  <span class="contextedit">
    <mm:maychangecontext>
      <mm:fieldlist id="struct" fields="owner">
        <mm:fieldinfo type="input" />
      </mm:fieldlist>
    </mm:maychangecontext>
  </span>
</span>
