<%

if(sMaxValues.equals("1")){
   String sSelected = "";
   %>
   <mm:relatednodes type="metadata">
      <mm:field name="number" jspvar="sID" vartype="String">
         <%
         if(hsetRelatedNodes.contains(sID)){
            %>
            <mm:relatednodes type="metavocabulary" searchdir="destination" role="posrel">
               <mm:field name="value" jspvar="sValue" vartype="String" write="false">
                  <%
                  sSelected = sValue;
                  %>
               </mm:field>
            </mm:relatednodes>
            <%
            }
            %>
      </mm:field>
   </mm:relatednodes>
   <select name="<%=sPrefix%><mm:field name="number"/>">
      <option><%= EMPTY_VALUE %></option>
      <mm:relatednodes type="metavocabulary" orderby="value" searchdir="destination">
         <mm:field name="number" jspvar="sNumber" vartype="String" write="false">
            <%
            // Let's test whether this metavocabulary is not allowed by another metavocabulary (metavocabulary-posrel-metavocabulary)
            boolean bVocabularyAllowed = true;
            %>
            <mm:related  path="posrel,metavocabulary2" constraints="<%= "posrel.pos='" + CONSTRAINT_FORBIDDEN + "'" %>" searchdir="source" fields="metavocabulary2.number">
               <mm:field name="metavocabulary2.number" jspvar="sCurrentNum2" vartype="String" write="false">
                  <%
                  bVocabularyAllowed = !hsetVocabularis.contains(sCurrentNum2);
                  %>
               </mm:field>
            </mm:related>
            <%
            if(bVocabularyAllowed) {
               boolean metavocabularyIsShown = false;
               %>
               <mm:field name="value" />
               <option name="<%=sPrefix%><%= sMetaDefinitionID %>" value="<mm:field name="value" />"
               <%
               if(hsetVocabularis.contains(sNumber)){
                  %> selected="selected" <%
                  metavocabularyIsShown = true;
               }
               %>
               ><mm:field name="value" /></option>
               <%@include file="me_form_subvocabulary.jsp" %>
            <%
            } // end of if(bVocabularyAllowed)
            %>
         </mm:field>
      </mm:relatednodes>
   </select>
<%
}
else{ // sMaxValues !=1
   HashSet hsetSelected = new HashSet();
   %>
   <mm:relatednodes type="metadata">
      <mm:field name="number" jspvar="sID" vartype="String">
         <%
         if(hsetRelatedNodes.contains(sID)){
            %>
            <mm:relatednodes type="metavocabulary" searchdir="destination">
               <mm:field name="value" jspvar="sValue" vartype="String" write="false">
                  <mm:field name="number" jspvar="sNumber" vartype="String" write="false">
                     <%
                     hsetSelected.add(sValue);
                     hsetVocabularis.add(sNumber);
                     %>
                  </mm:field>
               </mm:field>
            </mm:relatednodes>
         <%
         }
         %>
      </mm:field>
   </mm:relatednodes>

   <mm:relatednodes type="metavocabulary" searchdir="destination" role="related">
      <mm:field name="number" jspvar="sNumber" vartype="String" write="false">
         <%
         // Let's test whether this metavocabulary is not allowed by another metavocabulary (metavocabulary-posrel-metavocabulary)
         boolean bVocabularyAllowed = true;
         %>
         <mm:related path="posrel,metavocabulary2" constraints="<%= "posrel.pos='" + CONSTRAINT_FORBIDDEN + "'" %>" searchdir="source" fields="metavocabulary2.number">
            <mm:field name="metavocabulary2.number" jspvar="sCurrentNum2" vartype="String" write="false">
               <%
               bVocabularyAllowed = ! hsetVocabularis.contains(sCurrentNum2);
               %>
            </mm:field>
         </mm:related>
         <%
         if(bVocabularyAllowed){
            boolean metavocabularyIsShown = false;
            %>
            <InputField type="checkbox" name="<%=sPrefix%><%= sMetaDefinitionID %>" value="<mm:field name="value" />"
               <%
               if(hsetVocabularis.contains(sNumber)){
                 %>
                 checked="checked"
                 <%
                 metavocabularyIsShown = true;
               }
               %>
            />
            <mm:field name="value" />
            <%@include file="me_form_subvocabulary.jsp" %>
            <br/>
         <%
         } // end of if(bVocabularyAllowed)
         %>
      </mm:field>
   </mm:relatednodes>
<%
}
%>
