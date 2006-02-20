<%  // Include sub-metavocabularies (metavocabulary-related-metavocabulary) %> 
<mm:relatednodes type="metavocabulary" role="related" searchdir="destination">
   <mm:first>
      <a href="javascript:toggleDiv('m_<%= sMetaDefinitionID %>_<%= sNumber %>');"><img src="gfx/metaedit_plus.gif" border="0" align="middle"/></a>
      <div id="m_<%= sMetaDefinitionID %>_<%= sNumber %>" style="padding-left:10px;display:<%= (metavocabularyIsShown ? "block" : "none" ) %>">   
   </mm:first>
   <mm:field name="number" jspvar="sRelVocNumber" vartype="String" write="false">
   <%
   // Todo: check whether this metavocabulary is not shown on this page already
   %>
   <input type="checkbox" name="<%=sPrefix%><%= sMetaDefinitionID %>" value="<mm:field name="value" />"
      <%
      if(hsetVocabularis.contains(sRelVocNumber)) {
         %>
         checked="checked"
         <%
      }
      %>
      /><mm:field name="value" />
      <br/>
   </mm:field>
   <mm:last>
      </div>
   </mm:last>
</mm:relatednodes>
