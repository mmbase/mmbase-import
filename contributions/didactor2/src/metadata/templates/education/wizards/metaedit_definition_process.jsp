<mm:node number="<%= sMetadataDefinitionID%>">
   <mm:field name="type" jspvar="sType" vartype="String" write="false">
      <%
         sMetadataDefinitionType = sType;
      %>
   </mm:field>

<%

if(sMetadataDefinitionType.equals("1"))
{ //Vocabulary

%>
    <mm:remove referid="vocabulary_id" />
    <mm:remove referid="metadata_id" />
    <mm:relatednodes type="metavocabulary" role="related">
      <mm:node id="vocabulary_id">
        <mm:field name="value" jspvar="vocValue" vartype="String">
          <mm:field name="number" jspvar="vocNumber" vartype="String">
           <mm:remove referid="metadata_id" />
           <mm:node number="<%= sMetadataID %>" id="metadata_id" >
           <%
              bIsRelated = false;
             %>
             <mm:relatednodes type="metavocabulary" role="posrel">
               <mm:field name="number" jspvar="vocNumber2" vartype="String">
                <%

                 if(vocNumber2.equals(vocNumber))
                 {
                    bIsRelated = true;
                 }

               %>
              </mm:field>
            </mm:relatednodes>

             <%
             if(!bIsRelated)
             {

               String[] arrstrParameters = request.getParameterValues(sParameter);
               if ((arrstrParameters.length > 1) || (!arrstrParameters[0].equals(EMPTY_VALUE)))
               {
                 for(int f = 0; f < arrstrParameters.length; f++)
                 {
                   if(vocValue.equals(arrstrParameters[f]))
                   {

                    %>
                    <mm:createrelation source="metadata_id" destination="vocabulary_id" role="posrel" />
                    <%
                   } // end of if(vocValue.equals(arrstrParameters[f]))

                 } // end of for(int f = 0; f < arrstrParameters.length; f++)

               } // end of if ((arrstrParameters.length > 1) || (!arrstrParameters[0].equals(EMPTY_VALUE)))

             } // end of if(!bIsRelated)
           %>

           </mm:node>
         </mm:field>
       </mm:field>
     </mm:node>
  </mm:relatednodes>

<%
}
if(sMetadataDefinitionType.equals("2"))
{//Date
   %>
      <mm:node number="<%= sMetadataID %>">
         <mm:relatednodes type="metadate">
            <mm:deletenode deleterelations="true"/>
         </mm:relatednodes>
      </mm:node>
   <%
   String[] arrstrParameters = request.getParameterValues(sParameter);
   try
   {
      String sDate = arrstrParameters[0] + "-" + arrstrParameters[1] + "-" + arrstrParameters[2] + "|" + arrstrParameters[3] + ":" + arrstrParameters[4];
      SimpleDateFormat df  = new SimpleDateFormat("dd-MM-yyyy|hh:mm");
      Date date = df.parse(sDate);
      %>
         <mm:remove referid="date_id" />
         <mm:remove referid="metadata_id" />
         <mm:createnode type="metadate" id="date_id">
            <mm:setfield name="value"><%= date.getTime() / 1000 %></mm:setfield>
         </mm:createnode>
         <mm:node number="<%= sMetadataID %>" id="metadata_id">
            <mm:createrelation source="metadata_id" destination="date_id" role="posrel" />
         </mm:node>
      <%
   }
   catch(Exception e)
   {
      %>
         <mm:node number="<%= sMetadataID %>">
            <mm:relatednodes type="metadate">
               <mm:deletenode deleterelations="true"/>
            </mm:relatednodes>
         </mm:node>
      <%
   }
}

if(sMetadataDefinitionType.equals("3"))
{//Strings with langs code
   boolean bNoData = true;
   %>
      <mm:node number="<%= sMetadataID %>">
         <mm:relatednodes type="metalangstring">
            <mm:deletenode deleterelations="true"/>
            <%
               bNoData = false;
            %>
         </mm:relatednodes>
      </mm:node>
   <%
   String[] arrstrParameters = request.getParameterValues(sParameter);
   for(int f = 0; f < arrstrParameters.length ; f += 2)
   {// in cycle we are getting all values from request
      if(sRequest_Submitted.equals("remove"))
      {// if we have got "remove" command, we should skip the
         String[] sTarget = request.getParameter("add").split("\\,");
         if (sMetadataDefinitionID.equals(sTarget[0]))
         {
            if (sTarget[1].equals("" + f/2)) continue;
         }
      }

      String sLang = arrstrParameters[f];
      String sCode = arrstrParameters[f + 1];
      if ((sCode.equals("")) && (arrstrParameters.length == 2) && (bNoData))
      {// if we have got only one parameter and it is empty, and there are no existing nodes in db then we shouldn't store this lang string
         break;
      }

      %>
         <mm:remove referid="lang_id" />
         <mm:remove referid="metadata_id" />
         <mm:createnode type="metalangstring" id="lang_id">
            <mm:setfield name="language"><%= sLang %></mm:setfield>
            <mm:setfield name="value"><%= sCode %></mm:setfield>
         </mm:createnode>
         <mm:node number="<%= sMetadataID %>" id="metadata_id">
            <mm:createrelation source="metadata_id" destination="lang_id" role="posrel">
               <mm:setfield name="pos"><%= f + 1 %></mm:setfield>
            </mm:createrelation>
         </mm:node>
      <%
   }
}
if(sMetadataDefinitionType.equals("4"))
{//Duration
   boolean bNotEmpty = false;
   %>
      <mm:node number="<%= sMetadataID %>">
         <mm:relatednodes type="metadate">
            <%
               bNotEmpty = true;
            %>
            <mm:deletenode deleterelations="true"/>
         </mm:relatednodes>
      </mm:node>
   <%
   String[] arrstrParameters = request.getParameterValues(sParameter);
   try
   {
      String sDateBegin = arrstrParameters[0] + "-" + arrstrParameters[1] + "-" + arrstrParameters[2] + "|" + arrstrParameters[3] + ":" + arrstrParameters[4];
      String sDateEnd   = arrstrParameters[5] + "-" + arrstrParameters[6] + "-" + arrstrParameters[7] + "|" + arrstrParameters[8] + ":" + arrstrParameters[9];
      SimpleDateFormat df  = new SimpleDateFormat("dd-MM-yyyy|hh:mm");

      Date dateBegin = df.parse(sDateBegin);
      Date dateEnd   = df.parse(sDateEnd);
      %>
         <mm:remove referid="date_id" />
         <mm:remove referid="metadata_id" />
         <mm:createnode type="metadate" id="date_id">
            <mm:setfield name="value"><%= dateBegin.getTime() / 1000 %></mm:setfield>
         </mm:createnode>
         <mm:node number="<%= sMetadataID %>" id="metadata_id">
            <mm:createrelation source="metadata_id" destination="date_id" role="posrel">
               <mm:setfield name="pos">1</mm:setfield>
            </mm:createrelation>
         </mm:node>

         <mm:remove referid="date_id" />
         <mm:remove referid="metadata_id" />
         <mm:createnode type="metadate" id="date_id">
            <mm:setfield name="value"><%= dateEnd.getTime() / 1000 %></mm:setfield>
         </mm:createnode>
         <mm:node number="<%= sMetadataID %>" id="metadata_id">
            <mm:createrelation source="metadata_id" destination="date_id" role="posrel">
               <mm:setfield name="pos">2</mm:setfield>
            </mm:createrelation>
         </mm:node>
      <%
   }
   catch(Exception e)
   {
      %>
         <mm:node number="<%= sMetadataID %>">
            <mm:relatednodes type="metadate">
               <mm:deletenode deleterelations="true"/>
            </mm:relatednodes>
         </mm:node>
      <%
   }  // end of try
}  // end of if(sMetadataDefinitionType.equals("4"))
%>
</mm:node>
                            