<%@page import = "java.util.ArrayList" %>

<%
   {
      boolean bValid = true;
      int listSize = 0;
      String sCurrentNode = null;
      ArrayList arliRelated = new ArrayList();

%>

<mm:field name="number" jspvar="sID" vartype="String" write="false">
   <%
      sCurrentNode = sID;
   %>
</mm:field>
<mm:listnodes type="metastandard" orderby="name">
   <mm:relatednodes type="metadefinition">
      <mm:remove referid="defnumber"/>
      <mm:field name="number" id="defnumber">
      <mm:field name="type" jspvar="sType" vartype="String">
         <mm:field name="required" jspvar="sReq" vartype="String">
            <mm:field name="minvalues" jspvar="sMin" vartype="String">
               <mm:field name="maxvalues" jspvar="sMax" vartype="String">
                  <%
                     if(bValid)
                     {//If metadate has valid yet

                        if(sType.equals("1"))
                        {//Duration
                           if(sReq.equals("1"))
                           {//Required
                              bValid = false;
                              int iCounter = 0;
                              int iMax = 0;
                              int iMin = 0;
                              boolean bMin = true;
                              boolean bMax = true;
                              %>

                                 <mm:list nodes="<%= sCurrentNode %>" path="learnobjects,metadata,metadefinition" constraints="metadefinition.number=$defnumber">
                                 <mm:field name="metadata.number" id="datanum">
                                    <mm:node referid="datanum">
                                          <mm:relatednodes type="metavocabulary">
                                             <%
                                               iCounter++;
                                             %>
                                          </mm:relatednodes>
                                    </mm:node>
                                 </mm:field>
                                 <mm:remove referid="datanum"/>
                                 </mm:list>
                              <%

                              try
                              {//If there is no min or max value, we will skip this value
                                 iMin =  (new Integer(sMin)).intValue();
                              }
                              catch(Exception e)
                              {
                                 bMin = false;
                              }
                              try
                              {
                                 iMax =  (new Integer(sMax)).intValue();
                              }
                              catch(Exception e)
                              {
                                 bMax = false;
                              }

                              //System.out.println(iMin + " " + iMax);
                              //System.out.println(bMin + " " + bMax);

                              if (((iCounter >= iMin) || (!bMin)) && ((iCounter <= iMax) || (!bMax)))
                              {
                                 bValid = true;
                              }
                           }
                        }
                        if(sType.equals("2"))
                        {//Date
                           if(sReq.equals("1"))
                           {//Required
                              bValid = false;
                              %>
                                <mm:list nodes="<%= sCurrentNode %>" path="learnobjects,metadata,metadefinition" constraints="metadefinition.number=$defnumber">
                                 <mm:field name="metadata.number" id="datanum">
                                    <mm:node referid="datanum">
                                       <mm:relatednodes type="metadate">
                                           <%
                                              bValid = true;
                                           %>
                                        </mm:relatednodes>
                                    </mm:node>
                                 </mm:field>
                                 <mm:remove referid="datanum"/>
                                 </mm:list>
                              <%
                           }
                        }
                        if(sType.equals("3"))
                        {//Lang strings
                           if(sReq.equals("1"))
                           {//Required
                              bValid = false;
                              %>
                                 <mm:relatednodes type="metadata">
                                    <mm:field name="number" jspvar="sID" vartype="String">
                                       <%
                                          if (arliRelated.contains(sID))
                                          {
                                             %>
                                                <mm:relatednodes type="metalangstring">
                                                   <%
                                                      bValid = true;
                                                   %>
                                                </mm:relatednodes>
                                             <%
                                          }
                                       %>
                                    </mm:field>
                                 </mm:relatednodes>
                              <%
                           }
                        }
                        if(sType.equals("4"))
                        {//Duration
                           if(sReq.equals("1"))
                           {//Required
                              bValid = false;
                              int iCounter = 0;
                              %>
                                 <mm:list nodes="<%= sCurrentNode %>" path="learnobjects,metadata,metadefinition" constraints="metadefinition.number=$defnumber">
                                 <mm:field name="metadata.number" id="datanum">
                                    <mm:node referid="datanum">
                                               <mm:relatednodes type="metadate">
                                                   <%
                                                      iCounter++;
                                                   %>
                                                </mm:relatednodes>
                                    </mm:node>
                                    </mm:field>
                                    <mm:remove referid="datanum"/>
                                 </mm:list>
                              <%
                              if (iCounter > 1)
                              {
                                 bValid = true;
                              }
                           }
                        }
                     }
                  %>
               </mm:field>
            </mm:field>
         </mm:field>
      </mm:field>
      </mm:field>
   </mm:relatednodes>
</mm:listnodes>


<%


      if(bValid)
      {
         imageName = "gfx/metavalid.gif";
         sAltText = "Metadata is correct, bewerk metadata voor dit object";
      }
      else
      {
         imageName = "gfx/metaerror.gif";
         sAltText = "Metadata is niet correct, bewerk metadata voor dit object";
      }

   }


%>
