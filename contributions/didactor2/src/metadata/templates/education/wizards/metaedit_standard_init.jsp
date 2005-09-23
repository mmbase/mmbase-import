<mm:listnodes type="metastandard" orderby="name">
<mm:field name="name" jspvar="sMetastandardName" vartype="String">

<%

 enumParamNames = request.getParameterNames();

      while(enumParamNames.hasMoreElements())
        {

          String sParameter = (String) enumParamNames.nextElement();


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

                     hsetAssignedMetadefinitions.add(sMetadataDefinitionID);

                     // Let's check this metadefinition metavocabularies
                    %>


                      <mm:list nodes="<%=sMetadataDefinitionID %>"  path="metadefinition,related,metavocabulary"
                               searchdir="destination" fields="metavocabulary.number,metavocabulary.value" >
                         <mm:field name="metavocabulary.value" jspvar="sVocValue" vartype="String">
                            <mm:field name="metavocabulary.number" jspvar="sVocNumber" vartype="String">

                              <%
                                  if(sVocValue.equals(arrstrParameters[i]))
                                  {
                                      hsetAssignedVocabularies.add(sVocNumber);

                                      %>
                                        <mm:list nodes="<%=sVocNumber%>" path="metavocabulary,related,metavocabulary2" fields="metavocabulary2.value" searchdir="destination">
                                          <mm:field name="metavocabulary2.value"  jspvar="sCurValue" vartype="String">
                                              <mm:field name="metavocabulary2.number" jspvar="sCurNumber" vartype="String">
                                               <%
                                                arliRelVocVals.add(sCurValue);     // collect values
                                                arliRelVocNumbers.add(sCurNumber); // collect numbers

                                              %>
                                              </mm:field>
                                          </mm:field>
                                        </mm:list>


                                      <%
                                  }
                              %>

                          </mm:field>
                       </mm:field>
                   </mm:list>

                 <%

                    if(arliRelVocVals.contains(arrstrParameters[i]))
                       hsetAssignedVocabularies.add(arliRelVocNumbers.get(arliRelVocVals.indexOf(arrstrParameters[i])));

                    arliRelVocVals.clear(); // clear collected vocabularies
                    arliRelVocVals.clear(); // clear collected vocabularies

                 } // end of if(arrstrParameters[i]!= null &&

               } // end of for (int i=0 ; i < arrstrParameters.length ; i++)

            } // end of if if(sParameter.charAt(0) == 'm')

      } // end of while

 %>

 <mm:relatednodes type="metadefinition" role="posrel">
    <mm:field name="required" jspvar="sRequired" vartype="String">
       <mm:field name="number" jspvar="sNumber"  vartype="String">
           <mm:field name="name" jspvar="sName"    vartype="String">
               <mm:field name="type" jspvar="sType"  vartype="String">

                  <%
                     if(sRequired.equals("1"))
                      {
                        hsetHaveToBeNotEmpty.add(sNumber);
                      }
                      %>

                       <!-- Now we have to check whether we fill this values or not according type2 relations  -->

                         <mm:list nodes="<%=sNumber %>"  path="metadefinition,posrel,metadefinition2"
                            searchdir="source" fields="metadefinition.number,metadefinition.name,posrel.pos" >

                            <mm:field name="metadefinition2.number" jspvar="rMd" vartype="String">
                                <mm:field name="posrel.pos" jspvar="rPos" vartype="String">

                                 <%
                                 if(rPos.equals("1") && hsetAssignedMetadefinitions.contains(rMd))
                                 {
                                    hsetHaveToBeNotEmpty.add(sNumber);

                                 } // end of if(rPos.equals("1")

                              %>

                            </mm:field> <%// posrel.pos %>
                        </mm:field> <%// metadefinition2.number %>
                     </mm:list> <!-- path="metadefinition,posrel,metadefinition2" -->

                      <mm:list nodes="<%=sNumber %>"  path="metadefinition,constraints,metavocabulary" searchdir="source"
                       fields="metavocabulary.number,metavocabulary.value,constraints.type,constraints.maxvalues,constraints.minvalues" >

                         <mm:field name="metavocabulary.number" jspvar="rMv"   vartype="String">
                              <mm:field name="metavocabulary.value"  jspvar="rMval" vartype="String">
                                  <mm:field name="constraints.type" jspvar="vType"      vartype="String">
                                     <mm:field name="constraints.maxvalues" jspvar="iMaxvalues"    vartype="Integer">
                                         <mm:field name="constraints.minvalues" jspvar="iMinvalues"    vartype="Integer">

                                          <%

                                            String sParam = "m"+sNumber;
                                            String[] arrstrParams = request.getParameterValues(sParam);

                                            if(arrstrParams == null)
                                             {
                                               arrstrParams = new String[0];
                                             }


                                           if(vType.equals("1") && hsetAssignedVocabularies.contains(rMv))
                                            {
                                                hsetHaveToBeNotEmpty.add(sNumber);
                                            }

                                           if(vType.equals("2") &&
                                                hsetAssignedVocabularies.contains(rMv) &&
                                                sType.equals("1") &&
                                                (arrstrParams.length < iMinvalues.intValue() ||
                                                arrstrParams.length > iMaxvalues.intValue() ))
                                               {
                                                 bConstraintOk = false;
                                                 arliConstraintErrors.add("Metavocabulary "+
                                                   rMval+
                                                   " requires more or less values from metadefinition "+
                                                   sName);
                                               }


                                           if(vType.equals("3") &&
                                               hsetAssignedVocabularies.contains(rMv) &&
                                               hsetAssignedMetadefinitions.contains(sNumber))
                                                {
                                                    bConstraintOk = false;
                                                    arliConstraintErrors.add("Metavocabulary "+rMval+" forbids metadefinition "+sName);
                                                }


                                         %>

                                  </mm:field>
                              </mm:field>
                          </mm:field>
                      </mm:field>
                   </mm:field>

               </mm:list> <%// path="metadefinition,constraints,metavocabulary"  %>

            </mm:field>
         </mm:field>
       </mm:field>
     </mm:field>
   </mm:relatednodes>
 </mm:field>     <%// matastandard name %>
</mm:listnodes> <%// metastandards    %>
