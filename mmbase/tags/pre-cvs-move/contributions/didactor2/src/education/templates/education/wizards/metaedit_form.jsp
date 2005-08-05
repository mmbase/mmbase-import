<%@taglib uri="http://java.sun.com/jsp/jstl/fmt" prefix="fmt" %>
<%@taglib uri="http://www.mmbase.org/mmbase-taglib-1.0" prefix="mm"%>
<%@taglib uri="http://www.didactor.nl/ditaglib_1.0" prefix="di" %>

<%@page import = "java.util.*" %>


<%
   String sNode = request.getParameter("node");

   String [] MONTHS = { "januari", "februari", "maart", "april", "mei", "juni", "juli", "augustus", "september", "oktober", "november", "december" };
   String [] LANGUAGES = {"nl", "en", "fr"};
   String EMPTY_VALUE = "...";
   String sNodeValue = "";
   String sNodeUrlString ="";


   HashSet hsetRelatedNodes = new HashSet();
   SortedSet hsetLangCodes = new TreeSet();
   HashSet hsetAssignedVals = new HashSet();
   HashSet hsetVocabularis = new HashSet();
   HashSet hsetClassesOfTypeUrl = new HashSet();
   HashSet hsetClassesOfTypeBin = new HashSet();

   hsetClassesOfTypeUrl.add("audiotapes");
   hsetClassesOfTypeUrl.add("videotapes");

   hsetClassesOfTypeBin.add("attachments");
   hsetClassesOfTypeBin.add("pdfs");
%>

<style type="text/css">
   .bottom_link{
      color:18248C;
      font-family:arial;
      font-size:20px;
      font-weight:normal;
      text-decoration:none;
   }

   body, .body{
      font-family:arial;
      font-size:13px;
   }
</style>

<mm:content postprocessor="reducespace">
   <mm:cloud>

     <%// List assigned metadefinitions %>
     <%
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

                           </mm:field>
                         </mm:field>
                      </mm:list>

                     <%
                     } // end of if(arrstrParameters[i]!= null

                    }  // end of for

            } // end of if(sParameter.charAt(0) == 'm')

           } // end of while

     %>

     <%// Get languages list %>
     <mm:listnodes type="metastandard" >
         <mm:relatednodes type="metadefinition" role="posrel">
            <mm:field name="handler" jspvar="sHandler" vartype="String" write="false">
               <mm:field name="number" jspvar="sID" vartype="String" write="false">
                  <%

                     if(sHandler.equals("taal"))
                     {
                        %>
                           <mm:node number="<%= sID %>">
                              <mm:relatednodes type="metavocabulary">
                                 <mm:field name="value" jspvar="sLang" vartype="String">
                                    <%
                                       hsetLangCodes.add(sLang);
                                    %>
                                 </mm:field>
                              </mm:relatednodes>
                           </mm:node>
                        <%
                     }
                  %>
               </mm:field>
            </mm:field>
         </mm:relatednodes>
      </mm:listnodes>

      <%// Get all related metadata to this node %>
      <mm:node number="<%= sNode %>">
         <mm:relatednodes type="metadata">
            <mm:field name="number" jspvar="sID" vartype="String">
               <%
                  hsetRelatedNodes.add(sID);
               %>
            </mm:field>
         </mm:relatednodes>

         <mm:nodeinfo type="type" jspvar="sNodeType" vartype="String" >

           <%
             if(hsetClassesOfTypeUrl.contains(sNodeType))
             {
           %>
           <mm:field name="url" jspvar="sObjectUrl" vartype="String">
           <% sNodeUrlString = sObjectUrl; %>
           </mm:field>
           <%

             }
             else if (hsetClassesOfTypeBin.contains(sNodeType))
             {
           %>
             <mm:field name="size" jspvar="sObjectSize" vartype="String">
               <% sNodeValue =  sObjectSize; %>
             </mm:field>
           <%
             }


           %>

         </mm:nodeinfo>

      </mm:node>

      <form name="meta_form">

         <div style="overflow-y:scroll; width:100%; height:90%; position:absolute">
            <input type="hidden" name="node" value="<%= sNode %>"/>
            <input type="hidden" name="number" value="<%= sNode %>"/>
            <input type="hidden" name="submitted" value="submit"/>
            <input type="hidden" name="add" value=""/>
            <input type="hidden" name="close" value="yes"/>
            <%
               if(request.getParameter("set_defaults") != null)
               {
                 //If we are setting default values, we should repeat "set_default" parameter
                  %>
                     <input type="hidden" name="set_defaults" value="true"/>
                  <%
               }
            %>

               <%@include file="metaedit_header.jsp" %>
               <font style="font-size:5px">&nbsp;<br/></font>

               <%
                  //if we are working with default values, we have to show only one metastandart
                  //So, we have to use constraints
                  String sMetastandartConstraints;
                  String sCheckrelations ;

                  if(request.getParameter("set_defaults") != null) sMetastandartConstraints = "number=" + sNode;
                     else sMetastandartConstraints = "";

               %>
               <mm:listnodes type="metastandard" orderby="name" constraints="<%= sMetastandartConstraints %>">
                  <mm:first inverse="true">
                     <br/><br/>
                  </mm:first>

                  <font style="font-family:arial; font-size:20px; font-weight:normal"><mm:field name="name"/></font>
                  <br/>
                  <mm:field name="description"/>
                  <hr style="width:99%; height:1px; color:#CCCCCC">

                     <mm:relatednodes type="metadefinition" orderby="name">
                         <%
                           sCheckrelations = "Ok";
                         %>

                        <mm:field name="number" jspvar="rNum" vartype="String">
                            <mm:list nodes="<%=rNum %>"  path="metadefinition,posrel,metadefinition2"
                                searchdir="source" fields="metadefinition.number,metadefinition.name,posrel.pos" >
                                <mm:field name="metadefinition2.number" jspvar="rMd" vartype="String">
                                    <mm:field name="posrel.pos" jspvar="rPos" vartype="String">
                                                  <%
                                          if(rPos.equals("3") && hsetAssignedVals.contains(rMd))
                                              {
                                                sCheckrelations = "Fail";
                                              }
                                          else
                                              {
                                                sCheckrelations = "Ok";
                                              }
                                             %>
                                      </mm:field> <%-- posrel.pos --%>
                                 </mm:field> <%-- metadefinition2.number --%>
                             </mm:list>

                          <!--
                            Let's check metavocabulary references ...
                           -->

                           <%
                              if("Ok".equals(sCheckrelations))
                                 {

                           %>

                           <mm:list nodes="<%=rNum %>"  path="metadefinition,posrel,metavocabulary" searchdir="source"
                                      fields="metavocabulary.number,metavocabulary.value,posrel.pos" >
                               <mm:field name="metavocabulary.number" jspvar="rMv" vartype="String">
                                   <mm:field name="posrel.pos" jspvar="vPos" vartype="String">

                                           <%
                                        if(vPos.equals("3") && hsetVocabularis.contains(rMv))
                                            {
                                                sCheckrelations = "Fail";
                                            }
                                        else
                                            {
                                               sCheckrelations = "Ok";
                                            }
                                           %>

                                  </mm:field>
                              </mm:field>
                          </mm:list>

                         <%
                                  } // end of if("Ok".equals(sCheckrelations))
                         %>

                      </mm:field>

                     <%
                     if("Ok".equals(sCheckrelations))
                      {
                     %>

                     <a name="m<mm:field name="number"/>">
                     <font style="font-family:arial; font-size:13px; font-weight:bold"><mm:field name="name"/></font>
                     <br/>
                     <mm:field name="description"/>
                     <br/>
                     <br/>

                     <%
                        String sDefType = "";
                        String sMinValues = "";
                        String sMaxValues = "";
                        String sMetaDefinitionID = "";
                        String sMetaHandler = "";
                     %>

                     <mm:field name="type" jspvar="sType" vartype="String" write="false">

                     <%
                        sDefType = sType;
                     %>

                     </mm:field>
                     <mm:field name="minvalues" jspvar="sValue" vartype="String" write="false">

                        <%
                           sMinValues = sValue;
                        %>

                     </mm:field>
                     <mm:field name="maxvalues" jspvar="sValue" vartype="String" write="false">

                        <%
                           sMaxValues = sValue;
                        %>

                     </mm:field>
                     <mm:field name="number" jspvar="sID" vartype="String" write="false">

                        <%
                           sMetaDefinitionID = sID;
                        %>

                     </mm:field>
                     <mm:field name="handler" jspvar="sHandle" vartype="String" write="false">
                        <%
                           sMetaHandler = sHandle;
                        %>
                     </mm:field>


                     <%
                        if(request.getParameter("set_defaults") != null)
                        {// We have to insert default values here, so we have to create metadata
                           %>
                              <jsp:include page="metaedit_metaget.jsp" flush="true">
                                 <jsp:param name="node" value="<%= sNode %>" />
                                 <jsp:param name="metadata_definition" value="<%= sMetaDefinitionID %>" />
                                 <jsp:param name="set_defaults" value="true" />
                              </jsp:include>
                           <%
                        }
                        else
                        {
                           %>
                              <jsp:include page="metaedit_metaget.jsp" flush="true">
                                 <jsp:param name="node" value="<%= sNode %>" />
                                 <jsp:param name="metadata_definition" value="<%= sMetaDefinitionID %>" />
                              </jsp:include>
                           <%
                        }
                        hsetRelatedNodes.add(session.getAttribute("metadata_id"));

                        if(sDefType.equals("1"))
                        {//vocabulary

                           String sCheckVocabularies = "Ok";

                           if(sMaxValues.equals("1"))
                           {
                              String sSelected = "";
                              %>
                                 <mm:relatednodes type="metadata">
                                    <mm:field name="number" jspvar="sID" vartype="String">
                                       <%

                                          if(hsetRelatedNodes.contains(sID))
                                          {

                                             %>

                                                <mm:relatednodes type="metavocabulary" searchdir="destination">
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
                                 <select name="m<mm:field name="number"/>">
                                     <option><%= EMPTY_VALUE %></option>
                                     <mm:relatednodes type="metavocabulary" orderby="value" searchdir="destination">
                                        <mm:field name="number" jspvar="sCurrentNum" vartype="String" write="false">

                                           <%-- Let's test all posrel related metavocabularies --%>
                                           <%  sCheckVocabularies ="Ok";   %>

                                           <mm:list nodes="<%=sCurrentNum %>"  path="metavocabulary,posrel,metavocabulary2"
                                                    searchdir="source" fields="metavocabulary2.number,metavocabulary2.value,posrel.pos">
                                              <mm:field name="metavocabulary2.number" jspvar="sCurrentNum2" vartype="String" write="false">
                                                  <mm:field name="metavocabulary2.value" jspvar="sCurrentVal2" vartype="String" write="false">
                                                      <mm:field name="posrel.pos" jspvar="sCurrentPos" vartype="String" write="false">

                                                           <%

                                                              if("3".equals(sCurrentPos) && hsetVocabularis.contains(sCurrentNum2))
                                                                  {
                                                                    sCheckVocabularies = "Fail";
                                                                  }
                                                               else
                                                                 {
                                                                    sCheckVocabularies = "Ok";
                                                                 }

                                                          %>
                                                      </mm:field>
                                                  </mm:field>
                                               </mm:field>
                                            </mm:list>
                                         </mm:field>

                                        <%
                                         if(sCheckVocabularies.equals("Ok"))
                                         {
                                                     // ---- print control -----
                                        %>

                                       <mm:field name="value" jspvar="sCurrent" vartype="String" write="false">
                                          <option name="m<%= sMetaDefinitionID %>" value="<%= sCurrent %>"
                                             <%
                                               if(sSelected.equals(sCurrent))
                                                 {
                                                    %> selected="selected" <%
                                                 }
                                            %>
                                          ><%= sCurrent %></option>
                                       </mm:field>

                                       <%
                                        } // end of if(sCheckVocabularies.equals("Ok"))
                                       %>


                                  </mm:relatednodes>
                                  </select>
                              <%
                              }
                              else
                              {
                                 HashSet hsetSelected = new HashSet();

                              %>
                              <mm:relatednodes type="metadata">
                                  <mm:field name="number" jspvar="sID" vartype="String">
                                     <%
                                        if(hsetRelatedNodes.contains(sID))
                                          {
                                             %>
                                                <mm:relatednodes type="metavocabulary" searchdir="destination">
                                                   <mm:field name="value" jspvar="sValue" vartype="String" write="false">
                                                      <%
                                                         hsetSelected.add(sValue);
                                                      %>
                                                   </mm:field>
                                                </mm:relatednodes>
                                             <%
                                          }
                                      %>
                                   </mm:field>
                               </mm:relatednodes>
                               <mm:relatednodes type="metavocabulary" searchdir="destination">
                                    <mm:field name="number" jspvar="sCurrentNumber" vartype="String" write="false">


                                      <%-- Let's test all posrel related metavocabularies --%>
                                      <%  sCheckVocabularies ="Ok";   %>

                                      <mm:list nodes="<%=sCurrentNumber %>"  path="metavocabulary,posrel,metavocabulary2"
                                           searchdir="source" fields="metavocabulary2.number,metavocabulary2.value,posrel.pos">

                                           <mm:field name="metavocabulary2.number" jspvar="sCurrentNum2" vartype="String" write="false">
                                                <mm:field name="metavocabulary2.value" jspvar="sCurrentVal2" vartype="String" write="false">
                                                    <mm:field name="posrel.pos" jspvar="sCurrentPos" vartype="String" write="false">

                                                       <%
                                                        if("3".equals(sCurrentPos) && hsetVocabularis.contains(sCurrentNum2))
                                                           {
                                                             sCheckVocabularies = "Fail";
                                                           }
                                                        else
                                                          {
                                                             sCheckVocabularies = "Ok";
                                                           }
                                                       %>

                                                     </mm:field>
                                                  </mm:field>
                                             </mm:field>
                                         </mm:list>
                                    </mm:field> <!-- Close metavocabulary number field tag -->

                                 <%
                                   if(sCheckVocabularies.equals("Ok"))
                                     {
                                     // ---- print control -----
                                     String sMetaVocabularyTest = "Ok";
                                %>

                                <mm:field name="value" jspvar="sCurrent" vartype="String" write="false">

                                    <input type="checkbox" name="m<%= sMetaDefinitionID %>" value="<%= sCurrent %>"
                                    <%
                                      if(hsetSelected.contains(sCurrent))
                                       {
                                      %>
                                      checked="checked"
                                      <%
                                       }
                                       else
                                       {
                                         sMetaVocabularyTest = "Fail";
                                       }
                                      %>
                                      /><%= sCurrent %>
                                      <br/>
                                 </mm:field>


                                 <mm:relatednodes type="metavocabulary" role="related" searchdir="destination">
                                   <%
                                   // Let's define if there is no this metavocabulary on the page we have to add it here


                                   %>
                                  <mm:field name="value" jspvar="sCurrent" vartype="String" write="false">
                                    <mm:field name="number" jspvar="sCurNumber" vartype="String">
                                      <mm:node number="<%= sMetaDefinitionID %>" >
                                        <mm:relatednodes type="metavocabulary" role="related" searchdir="destination">
                                           <mm:field name="number" jspvar="sMetaNumber" vartype="String">
                                             <%
                                             if(sCurNumber.equals(sMetaNumber))
                                             {
                                                sMetaVocabularyTest = "Fail";
                                             }
                                             %>
                                           </mm:field>
                                        </mm:relatednodes>
                                      </mm:node>
                                   </mm:field>

                                   <%
                                   if(sMetaVocabularyTest.equals("Ok"))
                                   {
                                   %>
                                   <input type="checkbox" name="m<%= sMetaDefinitionID %>" value="<%= sCurrent %>" /><%= sCurrent %><br/>

                                   <%
                                   } // end of if(sMetaVocabularyTest.equals("Ok"))
                                   %>
                                   </mm:field>

                                 </mm:relatednodes>

                                 <%
                                 } // end of if(sCheckVocabularies.equals("Ok"))
                                 %>

                              </mm:relatednodes>
                              <%
                           }
                        }
                        if(sDefType.equals("2"))
                        {//date
                           Date date = null;
                        %>
                           <mm:relatednodes type="metadata">
                               <mm:field name="number" jspvar="sID" vartype="String">
                                   <%
                                      if(hsetRelatedNodes.contains(sID))
                                       {
                                          %>
                                             <mm:relatednodes type="metadate" max="1">
                                                <mm:field name="value" jspvar="dateValue" vartype="Date" write="false">
                                                   <%
                                                      date = dateValue;
                                                   %>
                                                </mm:field>
                                             </mm:relatednodes>
                                          <%
                                       }
                                    %>
                                </mm:field>
                            </mm:relatednodes>

                            <table border="0" cellpadding="0" cellspacing="0" class="body">
                                 <tr>
                                    <td>Dag</td>
                                    <td>Maand</td>
                                    <td>Jaar</td>
                                    <td>&nbsp;</td>
                                    <td>Uur</td>
                                    <td>Minuut</td>
                                 </tr>
                                 <tr>
                                    <td><input type="text" name="m<%= sMetaDefinitionID %>" value="<% if (date != null) out.print(date.getDate()); %>" style="width:30px;"/></td>
                                    <td>
                                       <select name="m<%= sMetaDefinitionID %>">
                                           <option><%= EMPTY_VALUE %></option>
                                           <%
                                              for(int i = 0; i < 12; i++)
                                              {
                                                 %>
                                                    <option value="<%= i + 1 %>"
                                                    <%
                                                       if((date!= null) && (date.getMonth() == i))
                                                       {
                                                          %> selected="selected" <%
                                                       }
                                                    %>
                                                    ><%= MONTHS[i] %></option>
                                                 <%
                                              }
                                           %>
                                       </select>
                                    </td>
                                    <td><input type="text" name="m<%= sMetaDefinitionID %>" value="<% if (date != null) out.print(1900 + date.getYear()); %>" style="width:60px;"/></td>
                                    <td>&nbsp;om&nbsp;</td>
                                    <td>
                                       <select name="m<%= sMetaDefinitionID %>">
                                           <option><%= EMPTY_VALUE %></option>
                                           <%
                                              for(int i = 0; i < 24; i++)
                                              {
                                                 %>
                                                    <option value="<%= i %>"
                                                    <%
                                                       if((date!= null) && (date.getHours() == i))
                                                       {
                                                          %> selected="selected" <%
                                                       }
                                                    %>
                                                    >
                                                    <%
                                                       if(i < 10)
                                                       {
                                                          %>0<%
                                                       }
                                                    %><%= i %></option>
                                                 <%
                                              }
                                           %>
                                       </select>
                                    </td>
                                    <td>
                                       <select name="m<%= sMetaDefinitionID %>">
                                           <option><%= EMPTY_VALUE %></option>
                                           <%
                                              for(int i = 0; i < 60; i++)
                                              {
                                                 %>
                                                    <option value="<%= i %>"
                                                    <%
                                                       if((date!= null) && (date.getMinutes() == i))
                                                       {
                                                          %> selected="selected" <%
                                                       }
                                                    %>
                                                    >
                                                    <%
                                                       if(i < 10)
                                                       {
                                                          %>0<%
                                                       }
                                                    %><%= i %></option>
                                                 <%
                                              }
                                           %>
                                       </select>
                                    </td>
                                 </tr>
                              </table>
                           <%
                        }
                        if(sDefType.equals("3"))
                        {//lang
                           int iCounter = 0;
                           boolean bEmpty = true;
                           %>
                              <mm:relatednodes type="metadata">
                                 <mm:field name="number" jspvar="sID" vartype="String">
                                    <%
                                       if(hsetRelatedNodes.contains(sID))
                                       {
                                          %>
                                             <mm:related path="posrel,metalangstring" orderby="posrel.pos">
                                                <mm:node element="metalangstring">
                                                   <mm:field name="language" jspvar="sLanguage" vartype="String" write="false">
                                                      <mm:field name="value" jspvar="sValue" vartype="String" write="false">
                                                         <mm:field name="number" jspvar="sMetalangID" vartype="String" write="false">
                                                            <table border="0" cellpadding="0" cellspacing="0">
                                                               <tr>
                                                                  <td>
                                                                     <select name="m<%= sMetaDefinitionID %>">
                                                                        <option style="width:20px"><%= EMPTY_VALUE %></option>
                                                                           <%
                                                                              for(Iterator it = hsetLangCodes.iterator(); it.hasNext();)
                                                                              {
                                                                                 String sLangCode = (String) it.next();
                                                                                 %>
                                                                                    <option value="<%= sLangCode %>"
                                                                                       <%
                                                                                          if(sLanguage.equals(sLangCode))
                                                                                          {
                                                                                             %>selected="selected"<%
                                                                                          }
                                                                                       %>
                                                                                    ><%= sLangCode %></option>
                                                                                 <%
                                                                              }

                                                                              bEmpty = false;
                                                                           %>
                                                                     </select>
                                                                  </td>
                                                                  <td>
                                                                     <input name="m<%= sMetaDefinitionID %>" type="text" value="<%= sValue %>" style="width:150px"/>
                                                                  </td>
                                                                  <td>
                                                                     &nbsp;
                                                                  </td>
                                                                  <td>
                                                                     <input type="image" src="gfx/minus.gif" onClick="meta_form.action='#m<%= sMetaDefinitionID %>'; submitted.value='remove'; add.value='<%= sMetaDefinitionID %>,<%= iCounter %>'">
                                                                  </td>
                                                               </tr>
                                                            </table>
                                                         </mm:field>
                                                      </mm:field>
                                                   </mm:field>
                                                </mm:node>
                                                <%
                                                   iCounter++;
                                                %>
                                             </mm:related>
                                          <%
                                       }
                                    %>
                                 </mm:field>
                              </mm:relatednodes>
                              <%
                                 if(bEmpty)
                                 {
                                    %>
                                       <table border="0" cellpadding="0" cellspacing="0">
                                          <tr>
                                             <td>
                                                <select name="m<%= sMetaDefinitionID %>">
                                                   <option style="width:20px"><%= EMPTY_VALUE %></option>
                                                      <%
                                                         boolean bFirst = true;
                                                         for(Iterator it = hsetLangCodes.iterator(); it.hasNext();)
                                                         {
                                                            String sLangCode = (String) it.next();
                                                            %>
                                                               <option value="<%= sLangCode %>"
                                                                  <%
                                                                     if(bFirst)
                                                                     {
                                                                        %>selected="selected"<%
                                                                        bFirst = false;
                                                                     }
                                                                  %>
                                                               ><%= sLangCode %></option>
                                                            <%
                                                         }
                                                         iCounter++;
                                                         bEmpty = false;
                                                      %>
                                                </select>
                                             </td>
                                             <td>
                                                <input name="m<%= sMetaDefinitionID %>" type="text" value="" style="width:150px"/>
                                             </td>
                                          </tr>
                                       </table>

                                    <%
                                 }
                              %>
                              <input type="image" src="gfx/plus.gif" onClick="meta_form.action='#m<%= sMetaDefinitionID %>'; submitted.value='add'; add.value='<%= sMetaDefinitionID %>'"> voeg meer tekenreeksen toe
                           <%
                        }
                        if(sDefType.equals("4"))
                        {//Duration
                           Date[] date = new Date[2];
                           %>
                              <mm:relatednodes type="metadata">
                                 <mm:field name="number" jspvar="sID" vartype="String">
                                    <%
                                       if(hsetRelatedNodes.contains(sID))
                                       {
                                          %>
                                             <mm:related path="posrel,metadate" orderby="posrel.pos">
                                                <mm:node element="metadate">
                                                   <mm:first>
                                                      <mm:field name="value" jspvar="dateValue" vartype="Date" write="false">
                                                         <%
                                                            date[0] = dateValue;
                                                         %>
                                                      </mm:field>
                                                   </mm:first>
                                                   <mm:first inverse="true">
                                                      <mm:field name="value" jspvar="dateValue" vartype="Date" write="false">
                                                         <%
                                                            date[1] = dateValue;
                                                         %>
                                                      </mm:field>
                                                   </mm:first>
                                                </mm:node>
                                             </mm:related>
                                          <%
                                       }
                                    %>
                                 </mm:field>




                              </mm:relatednodes>
                           <%
                           for(int f = 0; f < 2; f++)
                           {
                              %>
                                 <table border="0" cellpadding="0" cellspacing="0" class="body">
                                    <tr>
                                       <td>Dag</td>
                                       <td>Maand</td>
                                       <td>Jaar</td>
                                       <td>&nbsp;</td>
                                       <td>Uur</td>
                                       <td>Minuut</td>
                                    </tr>
                                    <tr>
                                       <td><input type="text" name="m<%= sMetaDefinitionID %>" value="<% if (date[f] != null) out.print(date[f].getDate()); %>" style="width:30px;"/></td>
                                       <td>
                                          <select name="m<%= sMetaDefinitionID %>">
                                              <option><%= EMPTY_VALUE %></option>
                                              <%
                                                 for(int i = 0; i < 12; i++)
                                                 {
                                                    %>
                                                       <option value="<%= i + 1 %>"
                                                       <%
                                                          if((date[f]!= null) && (date[f].getMonth() == i))
                                                          {
                                                             %> selected="selected" <%
                                                          }
                                                       %>
                                                       ><%= MONTHS[i] %></option>
                                                    <%
                                                 }
                                              %>
                                          </select>
                                       </td>
                                       <td><input type="text" name="m<%= sMetaDefinitionID %>" value="<% if (date[f] != null) out.print(1900 + date[f].getYear()); %>" style="width:60px;"/></td>
                                       <td>&nbsp;om&nbsp;</td>
                                       <td>
                                          <select name="m<%= sMetaDefinitionID %>">
                                              <option><%= EMPTY_VALUE %></option>
                                              <%
                                                 for(int i = 0; i < 24; i++)
                                                 {
                                                    %>
                                                       <option value="<%= i %>"
                                                       <%
                                                          if((date[f]!= null) && (date[f].getHours() == i))
                                                          {
                                                             %> selected="selected" <%
                                                          }
                                                       %>
                                                       >
                                                       <%
                                                          if(i < 10)
                                                          {
                                                             %>0<%
                                                          }
                                                       %><%= i %></option>
                                                    <%
                                                 }
                                              %>
                                          </select>
                                       </td>
                                       <td>
                                          <select name="m<%= sMetaDefinitionID %>">
                                              <option><%= EMPTY_VALUE %></option>
                                              <%
                                                 for(int i = 0; i < 60; i++)
                                                 {
                                                    %>
                                                       <option value="<%= i %>"
                                                       <%
                                                          if((date[f]!= null) && (date[f].getMinutes() == i))
                                                          {
                                                      %> selected="selected" <%
                                                          }
                                                       %>
                                                       >
                                                       <%
                                                          if(i < 10)
                                                          {
                                                             %>0<%
                                                          }
                                                       %><%= i %></option>
                                                    <%
                                                 }
                                              %>
                                          </select>
                                       </td>
                                    </tr>
                                 </table>
                              <%
                           } // end of for

                        } // end of if

                       if(sDefType.equals("5"))
                        {
                           //autocomplete value

                           String sNValue = "";
                           String sStyle = "width:150px";


                            %>
                            <table border="0" cellpadding="0" cellspacing="0">
                               <tr>
                               <%
                                   sNodeUrlString = "file:///C:/Realtek.log";

                                   if(!sNodeValue.equals(""))
                                      {
                                        sNValue = sNodeValue;
                                      }
                                   else if (!sNodeUrlString.equals(""))
                                      {
                                        // Autocomplete this field
                                        Class cl;
                                        Object []prm = new Object[1];
                                        Class []clm  = new Class[1];


                                        try
                                           {
                                              clm[0] = Class.forName("java.lang.String");
                                              prm[0] = sNodeUrlString;


                                              cl = Class.forName("nl.didactor.component.education.utils.handlers."+ sMetaHandler);
                                              sNValue = cl.getMethod("getData", clm).invoke(cl, prm).toString();
                                              //System.out.println(cl.getMethod("getDataType", null).invoke(cl, null));
                                              sStyle = cl.getMethod("getStyle", null).invoke(cl, null).toString();
                                           }
                                        catch(Exception ex1)
                                           {
                                              System.out.println(ex1.toString());
                                              ex1.printStackTrace();
                                           }
                                       }
                                  %>
                                  <td>
                                     <input name="m<%= sMetaDefinitionID %>" type="text" value="<%=sNValue%>" readonly="readonly" style="<%=sStyle%>"/>
                                  </td>
                                </tr>
                              </table>

                           <%

                         } // end of if(sDefType.equals("5"))

                      %>

                     <mm:last inverse="true">
                        <hr style="width:99%; height:1px; color:#CCCCCC">
                     </mm:last>

                    <%
                     }
                    %>

                  </mm:relatednodes>

                  <mm:last inverse="true">
                     <hr style="width:99%; height:1px; color:#CCCCCC">
                  </mm:last>

               </mm:listnodes>

            <style type="text/css">
               .special_buttons
               {
                  background-color:transparent;
                  color: #18248C;
                  font-size: 20px;
                  font-weight: normal;
                  font-family:arial;
                  border: none;
                  vertical-align: middle;
               }
            </style>

         </div>

         <div align="center" style="position:absolute; bottom:0px">
            <hr style="width:100%; height:3px; color:#000000">
            <table border="0" cellpadding="0" cellspacing="0" width="0" style="height:0px">
               <tr>
                  <td align="right">
                     <%
                        if(session.getAttribute("show_metadata_in_list") == null)
                        {
                           %><input type="reset" name="command" value="Annuleren" class="special_buttons" style="width:90px" onClick="document.location.href='menu.jsp'"/><%
                        }
                        else
                        {
                           %><input type="reset" name="command" value="Annuleren" class="special_buttons" style="width:90px" onClick="document.location.href='<%= session.getAttribute("metalist_url") %>'"/><%
                        }
                     %>
                  </td>
                  <td>-</td>
                  <td><input type="submit" name="command" value="Opslaan en beëindigen" class="special_buttons" style="width:200px" /></td>
                  <td>-</td>
                  <td><input type="submit" name="command" value="Opslaan" class="special_buttons" style="width:80px" onClick="close.value='no'"/></td>
               </tr>
            </table>
         </div>
      </form>
   </mm:cloud>
</mm:content>

