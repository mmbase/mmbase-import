<%
  // create hsetAssignedVals (used in metadef_checkrelations.jsp)
  // create hsetVocabularis (used in metavocabulary_field.jsp)
  // List assigned metadefinitions 
  Enumeration enumAssignedParamNames = request.getParameterNames();
  while(enumAssignedParamNames.hasMoreElements())
  {
      String sParameter = (String) enumAssignedParamNames.nextElement();

      String[] arrstrParameters = request.getParameterValues(sParameter);

      if(sParameter.charAt(0) == 'm')
      {
            String sMetadataDefinitionID = sParameter.substring(1);

            for (int i=0 ; i < arrstrParameters.length ; i++)
            {

             if(arrstrParameters[i]!= null &&
                !arrstrParameters[i].equals(EMPTY_VALUE) &&
                !arrstrParameters[i].equals(""))
             {
                 // Put metadefinition in the list

                 hsetAssignedVals.add(sMetadataDefinitionID);
                 // Let's check this metadefinition metavocabularies

              %>

                <mm:list nodes="<%=sMetadataDefinitionID %>"  path="metadefinition,related,metavocabulary"
                         searchdir="destination" fields="metavocabulary.number,metavocabulary.value" >
                   <mm:field name="metavocabulary.value" jspvar="sVocValue" vartype="String">
                      <mm:field name="metavocabulary.number" jspvar="sVocNumber" vartype="String">

                        <%
                          if(sVocValue.equals(arrstrParameters[i]))
                          {

                             hsetVocabularis.add(sVocNumber);

                          }
                       %>



                  <mm:node number="<%=sVocNumber%>">
                   <mm:relatednodes type="metavocabulary" role="related" searchdir="destination">
                     <mm:field name="value" jspvar="sVocValue2" vartype="String">
                      <mm:field name="number" jspvar="sVocNumber2" vartype="String">

                        <%
                          if(sVocValue2.equals(arrstrParameters[i]))
                          {

                             hsetVocabularis.add(sVocNumber2);

                          }
                       %>
                      </mm:field>
                     </mm:field>
                   </mm:relatednodes>
                  </mm:node>


                 </mm:field>
               </mm:field>
             </mm:list>

               <%
               } // end of if(arrstrParameters[i]!= null

          }  // end of for

      } // end of if(sParameter.charAt(0) == 'm')

} // end of while

%>
